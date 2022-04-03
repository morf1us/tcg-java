import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import com.microsoft.z3.*;

/**
 * The HelloWorld program implements an application that
 * simply displays "Hello World!" to the standard output.
 *
 * @author  Florian Pötz
 * @version 1.0
 */

public class TCGenerator {
    private static final String ABNORMAL = "ab_";
    private static final String ASSERT  = "(assert";
    private static final String BRANCH_COVERAGE = "bc";
    private static final String PATH_COVERAGE = "pc";
    private static final String OBJECTIVE = "objective";
    private static final String TEMP = "temp_";

    private static final HashMap<String, String> cfg = new HashMap<>();
    private static final Context ctx = new Context(cfg);

    private static int MIN_VALUE = -10;
    private static int MAX_VALUE = 10;

    private static String declarations = "";
    private static final ArrayList<IntExpr> input_variables = new ArrayList<>();

    public static void main(String[] args) {

        if (!checkCommandLineArguments(args)) {
            System.out.println("""
                    Wrong usage!
                    Correct usage: FILEPATH METHOD MIN_VALUE MAX_VALUE.
                    METHOD: bc | pc
                    MIN_VALUE: smallest integer used for generating test cases.
                    MAX_VALUE: largest integer used for generating test cases.""");
            System.exit(0);
        }

        String filepath = args[0];
        String method = args[1];

        // prepare constraints
        ArrayList<BoolExpr> constraints = readSMTLIB2File(filepath);
        extractInputVariables(constraints);
        removeObjective(constraints);
        addValueRangeConstraints(constraints);

        if (method.equals(BRANCH_COVERAGE)) {
            List<List<Integer>> test_cases = getBranchCoverageTestCases(constraints);

        }
        else if (method.equals(PATH_COVERAGE)) {
            // todo: export smt-lib2 file with constraints
            List<BoolExpr> path_coverage = getPathCoverageConstraints(constraints);
            System.out.println("Hasn't been implemented yet.");
            System.exit(0);
        }
    }

    private static boolean checkCommandLineArguments(String[] args) {
        if ((args.length != 2 && args.length != 4) || (!Objects.equals(args[1], BRANCH_COVERAGE) && !Objects.equals(args[1], PATH_COVERAGE))) {
            return false;
        }
        if (args.length == 4) {
            MIN_VALUE = Integer.parseInt(args[2]);
            MAX_VALUE = Integer.parseInt(args[3]);
            return MIN_VALUE < MAX_VALUE;
        }
        return true;
    }

    private static ArrayList<BoolExpr> readSMTLIB2File(String path) {
        ArrayList<BoolExpr> ret = new ArrayList<>();

        try {
            String file_content = new String(Files.readAllBytes(Paths.get(path)));
            declarations = file_content.substring(0, file_content.indexOf(ASSERT));

            try {
                BoolExpr[] expressions = ctx.parseSMTLIB2String(file_content, null, null, null, null);
                ret = new ArrayList<>(Arrays.asList(expressions));

            } catch (Z3Exception e) {
                System.out.printf("Z3 error while parsing file: %s%n", e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret;
    }

    private static void removeObjective(ArrayList<BoolExpr> constraints) {
        if (!constraints.isEmpty()) {
            BoolExpr last_c = constraints.get(constraints.size() - 1);
            if (last_c.getNumArgs() > 0 && last_c.getArgs()[0].toString().equals(OBJECTIVE)) {
                constraints.remove(constraints.size() - 1);
            }
        }
    }

    private static void removeABs(ArrayList<BoolExpr> constraints) {
        for (int i = 0; i < constraints.size(); i++) {
            BoolExpr c = constraints.get(i);
            if (c.getNumArgs() > 1 && c.getArgs()[0].toString().startsWith(ABNORMAL)) {
                constraints.set(i, (BoolExpr) c.getArgs()[1]);
            }
        }
    }

    private static void extractInputVariables(ArrayList<BoolExpr> constraints) {
        Iterator<BoolExpr> it = constraints.iterator();
        while (it.hasNext()) {
            BoolExpr b_expr = it.next();
            if (!(b_expr.isOr() && b_expr.getNumArgs() > 0 && b_expr.getArgs()[0].toString().startsWith(ABNORMAL))) {
                input_variables.add((IntExpr) b_expr.getArgs()[0]);
                it.remove();
            }
            else { break; }
        }

        if (input_variables.isEmpty()) {
            System.out.println("The input file doesn't contain any input variables - no test cases to create.");
            System.exit(0);
        }

        System.out.println("number of input variables:");
        System.out.println(input_variables.size());
        for (IntExpr input_var : input_variables) {
            System.out.println(input_var.toString());
        }
    }

    private static void addValueRangeConstraints(ArrayList<BoolExpr> constraints) {
        for (IntExpr e : input_variables) {
            BoolExpr ge = ctx.mkGe(e, ctx.mkInt(MIN_VALUE));
            BoolExpr le = ctx.mkLe(e, ctx.mkInt(MAX_VALUE));
            BoolExpr and = ctx.mkAnd(ge, le);
            constraints.add(and);
        }
    }

    private static BoolExpr findConditionDependency(BoolExpr b_expr) {
        if (b_expr.getNumArgs() > 1 && b_expr.getArgs()[0].toString().startsWith(TEMP))
            return (BoolExpr) b_expr.getArgs()[0];

        return null;
    }

    private static List<List<Integer>> getBranchCoverageTestCases(ArrayList<BoolExpr> constraints) {
        List<List<Integer>> ret = new ArrayList<>();
        HashMap<BoolExpr, List<BoolExpr>> control_flow_conditions = new HashMap<>();

        removeABs(constraints);

        // prepare solver
        Solver solver = ctx.mkSolver();
        for (BoolExpr b : constraints) {
            solver.add(b);
        }

        while (solver.check() == Status.SATISFIABLE) {
            Model model = solver.getModel();
            /*System.out.println(model.getNumFuncs());
            System.out.println(model.getNumConsts());
            System.out.println(model.getNumSorts());*/
            System.out.println("model:");
            System.out.println(model);

            HashMap<String, Expr> sat_model = new HashMap<String, Expr>();
            for (FuncDecl cd : model.getConstDecls()) {
                System.out.println(cd.toString());
                System.out.println(model.getConstInterp(cd));
                sat_model.put(cd.getName().toString(), model.getConstInterp(cd));
            }
            List<BoolExpr> assignments = new ArrayList<>();
            for (IntExpr iv : input_variables) {
                Expr assignment = sat_model.get(iv.toString());
                if (assignment != null) {
                    BoolExpr negated_assignment = ctx.mkNot(ctx.mkEq(iv, assignment));
                    assignments.add(negated_assignment);
                }
                else {
                    System.out.println("shouldn't reach here.");
                }
            }
            BoolExpr prev_model = ctx.mkOr(assignments.toArray(new BoolExpr[0]));
            solver.add(prev_model);
        }

        /*for (BoolExpr c : constraints) {
            System.out.println(c.toString());
            if (c.getNumArgs() > 1 && c.getArgs()[0].toString().startsWith(TEMP)) {
                BoolExpr lhs = (BoolExpr) c.getArgs()[0];
                BoolExpr rhs = (BoolExpr) c.getArgs()[1];
                System.out.println(rhs.toString());
                List<BoolExpr> condition = control_flow_conditions.get(lhs);
                if (condition == null && rhs.getNumArgs() > 1 && rhs.getArgs()[0].toString().startsWith(TEMP)) {
                    BoolExpr dependency = findConditionDependency((BoolExpr) rhs.getArgs()[1]);
                    if (dependency != null)
                        control_flow_conditions.get(dependency).add((BoolExpr) rhs.getArgs()[0]);
                }
                else {
                    //con
                }
            }
        }*/

        return ret;
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
