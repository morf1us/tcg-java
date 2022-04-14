import com.microsoft.z3.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * The InputManager class is responsible for parsing the input, extracting the input variables of the initial java
 * program, extracting the constraints computed by Modelinho and make them accessible for test case computation.
 *
 */

public class InputManager {
    private String file_path;
    private String coverage_property;
    private boolean write_to_file;

    private static final String BRANCH_COVERAGE = "bc";
    private static final String PATH_COVERAGE = "pc";
    private static final String EXPORT = "-export";
    private static final String ABNORMAL = "ab_";

    private final ArrayList<BoolExpr> constraints;
    private final ArrayList<Expr<?>> input_variables;


    public InputManager(String[] args, Context ctx) {
        if (!checkCommandLineArguments(args)) {
            System.out.println("""
                    Wrong usage!
                    Correct usage: FILEPATH METHOD EXPORT
                    FILEPATH: path to .smt2 input file
                    METHOD: bc | pc
                    EXPORT (optional): -export""");
            System.exit(0);
        }
        this.constraints = readSMTLIB2File(this.file_path, ctx);
        this.input_variables = extractInputVariables(this.constraints);
    }

    private boolean checkCommandLineArguments(String[] args) {
        if ((args.length != 2 && args.length != 3) || (!args[1].equals(BRANCH_COVERAGE) && !args[1].equals(PATH_COVERAGE))) {
            return false;
        }
        this.file_path = args[0];
        this.coverage_property = args[1];

        if (args.length == 3) {
            if (args[2].equals(EXPORT))
                this.write_to_file = true;
            else
                return false;
        }
        else {
            this.write_to_file = false;
        }

        return true;
    }

    private ArrayList<BoolExpr> readSMTLIB2File(String path, Context ctx) {
        ArrayList<BoolExpr> input_constraints = new ArrayList<>();

        try {
            String file_content = new String(Files.readAllBytes(Paths.get(path)));

            try {
                BoolExpr[] constraints_arr = ctx.parseSMTLIB2String(file_content, null, null, null, null);
                input_constraints = new ArrayList<>(Arrays.asList(constraints_arr));

            } catch (Z3Exception e) {
                System.out.printf("Z3 error while parsing file: %s%n", e);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return input_constraints;
    }

    private ArrayList<Expr<?>> extractInputVariables(ArrayList<BoolExpr> constraints) {
        ArrayList<Expr<?>> vars = new ArrayList<>();
        Iterator<BoolExpr> it = constraints.iterator();
        while (it.hasNext()) {
            BoolExpr b_expr = it.next();
            if (!(b_expr.isOr() && b_expr.getNumArgs() > 0 && b_expr.getArgs()[0].toString().startsWith(ABNORMAL))) {
                vars.add(b_expr.getArgs()[0]);
                it.remove();
            }
            else { break; }
        }

        if (vars.isEmpty()) {
            System.out.println("The input file doesn't contain any input variables - no test cases to create.");
            System.exit(0);
        }

        return vars;
    }
    
    public ArrayList<BoolExpr> getConstraints() {
        return this.constraints;
    }

    public ArrayList<Expr<?>> getInputVariables() {
        return this.input_variables;
    }
    public String getCoverageProperty() {
        return this.coverage_property;
    }

    public boolean getWriteToFile() {
        return this.write_to_file;
    }
}
