import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Z3Exception;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * The HelloWorld program implements an application that
 * simply displays "Hello World!" to the standard output.
 *
 * @author  Florian Pötz
 * @version 1.0
 */

public class InputParser {
    private String file_path;
    private String method;
    private boolean all_solutions;
    private int min_value;
    private int max_value;

    private static final String BRANCH_COVERAGE = "bc";
    private static final String PATH_COVERAGE = "pc";
    private static final String ABNORMAL = "ab_";
    private static final String ASSERT  = "(assert";

    private String declarations;
    private final ArrayList<BoolExpr> constraints;
    private final ArrayList<IntExpr> input_variables;


    public InputParser(String[] args, Context ctx) {
        if (!checkCommandLineArguments(args)) {
            System.out.println("""
                    Wrong usage!
                    Correct usage: FILEPATH METHOD MIN_VALUE MAX_VALUE
                    METHOD: bc | pc
                    MIN_VALUE (optional): smallest integer used for generating test cases.
                    MAX_VALUE (optional): largest integer used for generating test cases.""");
            System.exit(0);
        }
        this.constraints = readSMTLIB2File(this.file_path, ctx);
        this.input_variables = extractInputVariables(this.constraints);
    }

    private boolean checkCommandLineArguments(String[] args) {
        if ((args.length != 2 && args.length != 4) || (!Objects.equals(args[1], BRANCH_COVERAGE) && !Objects.equals(args[1], PATH_COVERAGE))) {
            return false;
        }
        this.file_path = args[0];
        this.method = args[1];

        if (args.length == 4) {
            this.min_value = Integer.parseInt(args[2]);
            this.max_value = Integer.parseInt(args[3]);
            this.all_solutions = true;
            return min_value <= max_value;
        }
        return true;
    }

    private ArrayList<BoolExpr> readSMTLIB2File(String path, Context ctx) {
        ArrayList<BoolExpr> input_constraints = new ArrayList<>();

        try {
            String file_content = new String(Files.readAllBytes(Paths.get(path)));
            this.declarations = file_content.substring(0, file_content.indexOf(ASSERT));

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

    private ArrayList<IntExpr> extractInputVariables(ArrayList<BoolExpr> constraints) {
        ArrayList<IntExpr> vars = new ArrayList<>();
        Iterator<BoolExpr> it = constraints.iterator();
        while (it.hasNext()) {
            BoolExpr b_expr = it.next();
            if (!(b_expr.isOr() && b_expr.getNumArgs() > 0 && b_expr.getArgs()[0].toString().startsWith(ABNORMAL))) {
                vars.add((IntExpr) b_expr.getArgs()[0]);
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

    public ArrayList<IntExpr> getInputVariables() {
        return this.input_variables;
    }

    public int getMinValue() {
        return this.min_value;
    }

    public int getMaxValue() {
        return this.max_value;
    }

    public boolean getAllSolutions() {
        return this.all_solutions;
    }

    public String getMethod() {
        return this.method;
    }

    public String getDeclarations() {
        return this.declarations;
    }
}
