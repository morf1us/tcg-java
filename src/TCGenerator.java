import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import com.microsoft.z3.*;
import com.microsoft.z3.enumerations.Z3_ast_kind;

public class TCGenerator {
    public static void main(String[] args) {

        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        Context ctx = new Context(cfg);

        StringBuilder decls = new StringBuilder();
        List<BoolExpr> f = getFileContent(ctx, "testfiles/if1.smt2", decls);

        System.out.println(f.size());

        Solver solver = ctx.mkSolver();

        for (int i = 0; i < f.size(); i++) {
            solver.add(f.get(i));
            System.out.println("---------------------");
            System.out.println(f.get(i));
            System.out.println("args:");
            Expr[] t = f.get(i).getArgs();
            Z3_ast_kind v = f.get(i).getASTKind();
            System.out.println(v.toString());
            for (int j = 0; j < t.length; j++)
                System.out.println(t[j]);
        }

        if (Status.SATISFIABLE == solver.check()) {
            Model model = solver.getModel();
            System.out.println("model:");
            System.out.println(model);
        }

        Solver solver2 = ctx.mkSolver();
        //solver.add()

        //System.out.println("formula " + f);
    }

    private static List<BoolExpr> getFileContent(Context ctx, String path, StringBuilder decls) {
        List<BoolExpr> ret = new ArrayList<BoolExpr>();

        try {
            String file_content = new String(Files.readAllBytes(Paths.get(path)));
            decls.append(file_content.substring(0, file_content.indexOf("(assert")));

            try {
                //List<FuncDecl> decls = new ArrayList<FuncDecl>();
                //List<Symbol> names = new ArrayList<Symbol>();

                BoolExpr[] expressions = ctx.parseSMTLIB2String(new String(file_content), null, null, null, null);
                int len = expressions.length;
                if (len > 0 && expressions[len-1].getArgs()[0].toString().equals("objective")) {
                    expressions = Arrays.copyOf(expressions, len-1);
                }

                ret = Arrays.asList(expressions);

            } catch (Z3Exception e) {
                System.out.printf("Z3 error while parsing file: %s%n", e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }
}
