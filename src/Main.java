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
    private static final String BRANCH_COVERAGE = "bc";
    private static final String PATH_COVERAGE = "pc";
    private static final String CSV = ".csv";

    private static final HashMap<String, String> cfg = new HashMap<>();
    private static final Context ctx = new Context(cfg);

    public static void main(String[] args) {

        InputManager input_parser = new InputManager(args, ctx);

        String method = input_parser.getMethod();
        List<BoolExpr> constraints = input_parser.getConstraints();
        List<IntExpr> input_variables = input_parser.getInputVariables();
        boolean restrict_values = input_parser.getAllSolutions();
        int min = input_parser.getMinValue();
        int max = input_parser.getMaxValue();

        List<List<String>> test_cases = new ArrayList<>();

        if (method.equals(BRANCH_COVERAGE)) {
            BranchCoverage bc = new BranchCoverage(ctx, constraints, input_variables, restrict_values, min, max);
            bc.computeTestCases();
            test_cases = bc.getTestCases();
        }
        else if (method.equals(PATH_COVERAGE)) {
            PathCoverage pc = new PathCoverage(ctx, constraints, input_variables, restrict_values, min, max);
            pc.computeTestCases();
            test_cases = pc.getTestCases();
        }
        exportTestCases(test_cases, buildOutputPath(args[0], method));
    }

    private static String buildOutputPath(String input_path, String method) {
        String output_path = "output/";
        int dot_index = input_path.lastIndexOf(".");
        int slash_index = input_path.lastIndexOf("/");
        int backslash_index = input_path.lastIndexOf("\\");

        if (dot_index != -1 && slash_index != -1 && slash_index < dot_index)
            output_path += input_path.substring(slash_index + 1, dot_index) + "_" + method + CSV;
        else if (dot_index != -1 && backslash_index != -1 && backslash_index < dot_index)
            output_path += input_path.substring(backslash_index + 1, dot_index) + "_" + method + CSV;
        else if (dot_index != -1)
            output_path += input_path.substring(0, dot_index) + "_" + method + CSV;
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
                System.out.print(";" + tc_list.get(i));
            }
            System.out.println();
        }
        FileWriter file;
        String delimiter = ";";
        String separator = "\n";

        try {
            file = new FileWriter(output_path, false);
            for (int i = 0; i < test_cases.size(); i++) {
                for (int j = 0; j < test_cases.get(i).size(); j++) {
                    file.append(test_cases.get(i).get(j));
                    if (j != test_cases.get(i).size() - 1)
                        file.append(delimiter);
                }
                if (i != test_cases.size() - 1)
                    file.append(separator);
            }

            file.close();
            System.out.println("Successfully computed and exported " + (test_cases.size() - 1) + " test cases.");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
