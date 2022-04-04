import java.io.FileWriter;
import java.util.*;
import com.microsoft.z3.*;

/**
 * The HelloWorld program implements an application that
 * simply displays "Hello World!" to the standard output.
 *
 * @author  Florian Pötz
 * @version 1.0
 */

public class Main {
    private static final String ABNORMAL = "ab_";
    private static final String BRANCH_COVERAGE = "bc";
    private static final String PATH_COVERAGE = "pc";
    private static final String TEMP = "temp_";
    private static final String CSV = ".csv";

    private static final HashMap<String, String> cfg = new HashMap<>();
    private static final Context ctx = new Context(cfg);

    public static void main(String[] args) {

        InputParser input_parser = new InputParser(args, ctx);

        String method = input_parser.getMethod();
        List<BoolExpr> constraints = input_parser.getConstraints();
        List<IntExpr> input_variables = input_parser.getInputVariables();
        boolean restrict_values = input_parser.getAllSolutions();
        int min = input_parser.getMinValue();
        int max = input_parser.getMaxValue();

        List<List<String>> test_cases = new ArrayList<>();

        if (method.equals(BRANCH_COVERAGE)) {
            BranchCoverage bc = new BranchCoverage(ctx, constraints, input_variables, restrict_values, min, max);
            test_cases = bc.computeTestCases();
        }
        else if (method.equals(PATH_COVERAGE)) {
            PathCoverage bc = new PathCoverage(ctx, constraints, input_variables, restrict_values, min, max);
            test_cases = bc.computeTestCases();
        }
        exportTestCases(test_cases, buildOutputPath(args[0]));
    }

    private static String buildOutputPath(String input_path) {
        String output_path = "output/";
        int dot_index = input_path.lastIndexOf(".");
        int slash_index = input_path.lastIndexOf("/");
        int backslash_index = input_path.lastIndexOf("\\");

        if (dot_index != -1 && slash_index != -1 && slash_index < dot_index)
            output_path += input_path.substring(slash_index + 1, dot_index) + CSV;
        else if (dot_index != -1 && backslash_index != -1 && backslash_index < dot_index)
            output_path += input_path.substring(backslash_index + 1, dot_index) + CSV;
        else if (dot_index != -1)
            output_path += input_path.substring(0, dot_index) + CSV;
        else {
            System.out.println("Error during creation of output path.");
            System.exit(-1);
        }
        return output_path;
    }

    private static void exportTestCases(List<List<String>> test_cases, String output_path) {
        for (List<String> tc_list : test_cases) {
            if (!tc_list.isEmpty()) {
                System.out.print(tc_list.get(0));
            }
            for (int i = 1; i < tc_list.size(); i++) {
                System.out.print(" " + tc_list.get(i));
            }
            System.out.println();
        }
        FileWriter file = null;
        String delimiter = " ";
        String separator = "\n";

        try {
            file = new FileWriter(output_path, false);
            for (int i = 0; i < test_cases.size(); i++) {
                for (int j = 0; j < test_cases.get(i).size(); j++) {
                    file.append(test_cases.get(i).get(j));
                    if (j != test_cases.get(i).size() - 1);
                    file.append(delimiter);
                }
                if (i != test_cases.size() - 1)
                    file.append(separator);
            }

            file.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private static BoolExpr findConditionDependency(BoolExpr b_expr) {
        if (b_expr.getNumArgs() > 1 && b_expr.getArgs()[0].toString().startsWith(TEMP))
            return (BoolExpr) b_expr.getArgs()[0];

        return null;
    }

    private static List<BoolExpr> getPathCoverageConstraints(ArrayList<BoolExpr> constraints) {
        List<BoolExpr> ret = new ArrayList<>();

        System.out.println(constraints.size());
        Solver solver = ctx.mkSolver();

        HashMap<BoolExpr, ArrayList<BoolExpr>> control_flow_paths = new HashMap<>();

        for (BoolExpr c : constraints) {
            if (c.getNumArgs() > 1 && c.getArgs()[0].toString().startsWith(ABNORMAL)) {
                BoolExpr lhs = (BoolExpr) c.getArgs()[0];
                BoolExpr rhs = (BoolExpr) c.getArgs()[1];
                if (rhs.getNumArgs() > 1 && rhs.getArgs()[0].toString().startsWith(TEMP)) {
                    ArrayList<BoolExpr> flow_path = control_flow_paths.get(lhs);
                    if (control_flow_paths.containsKey(lhs)) {
                        //control_flow_paths
                    }
                }
            }
        }

        // parse constraints
        for (BoolExpr b_expr : constraints) {
            System.out.println("expression:");
            System.out.println(b_expr);
            System.out.println("---");
            System.out.println(b_expr.getNumArgs());
            //b_expr = (BoolExpr) b_expr.getArgs()[b_expr.getNumArgs()-1];
            //System.out.println(b_expr);
            ret.add(b_expr);
            //System.out.println(constraints.get(0));
            //b_expr.getClass().toString();
            solver.add(b_expr);
            Expr tmp = b_expr.getArgs()[0];
            //solver.add(ctx.mkNot(tmp));
            solver.add(tmp);
            /*System.out.println("---------------------");
            System.out.println(b_expr);
            System.out.println("args:");
            Expr[] t = b_expr.getArgs();
            Z3_ast_kind v = b_expr.getASTKind();
            System.out.println(v.toString());
            for (int j = 0; j < t.length; j++) {
                System.out.println("num args: " + t[j].getNumArgs());
                if (t[j].isEq())
                    System.out.println("yes, equal");
                else
                    System.out.println("no, not equal");
                System.out.println(t[j]);
            }*/
        }


        if (Status.SATISFIABLE == solver.check()) {
            Model model = solver.getModel();
            System.out.println("model:");
            System.out.println(model);
        }

        //Solver solver2 = ctx.mkSolver();
        //solver.add()

        //System.out.println("formula " + f);

        return ret;
    }
}
