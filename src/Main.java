import java.io.FileWriter;
import java.util.*;
import com.microsoft.z3.*;

/**
 * The Main class creates an InputManager objects and according to the user specification creates the respective
 * coverage property object, initiates the computation of test cases, prints them and finally exports them, if desired.
 *
 */

public class Main {
    private static final String BRANCH_COVERAGE = "-bc";
    private static final String PATH_COVERAGE = "-pc";

    public static void main(String[] args) {
        HashMap<String, String> cfg = new HashMap<>();
        Context ctx = new Context(cfg);

        InputManager input_manager = new InputManager(args, ctx);

        List<BoolExpr> constraints = input_manager.getConstraints();
        List<Expr<?>> input_variables = input_manager.getInputVariables();
        String coverage_property = input_manager.getCoverageProperty();
        boolean write_to_file = input_manager.getWriteToFile();

        List<List<String>> test_cases = new ArrayList<>();

        if (coverage_property.equals(BRANCH_COVERAGE)) {
            BranchCoverage bc = new BranchCoverage(ctx, constraints, input_variables);
            bc.computeTestCases();
            test_cases = bc.getTestCases();
        }
        else if (coverage_property.equals(PATH_COVERAGE)) {
            PathCoverage pc = new PathCoverage(ctx, constraints, input_variables);
            pc.computeTestCases();
            test_cases = pc.getTestCases();
        }
        exportTestCases(test_cases, buildOutputPath(args[0], coverage_property), write_to_file, coverage_property);
    }

    private static String buildOutputPath(String input_path, String method) {
        String CSV = ".csv";
        String output_path = "outputfiles";
        int dot_index = input_path.lastIndexOf(".");
        int slash_index = input_path.lastIndexOf("/");
        int backslash_index = input_path.lastIndexOf("\\");

        if (dot_index != -1 && slash_index != -1 && slash_index < dot_index)
            output_path += "/" + input_path.substring(slash_index + 1, dot_index) + "_" + method + CSV;
        else if (dot_index != -1 && backslash_index != -1 && backslash_index < dot_index)
            output_path += "\\" + input_path.substring(backslash_index + 1, dot_index) + "_" + method + CSV;
        else if (dot_index != -1)
            output_path += "/" + input_path.substring(0, dot_index) + "_" + method + CSV;
        else {
            System.out.println("Error during creation of output path.");
            System.exit(-1);
        }
        return output_path;
    }

    private static void exportTestCases(List<List<String>> test_cases, String output_path, boolean write_to_file, String coverage_property) {
        String c_prop = "";
        if (coverage_property.equals(BRANCH_COVERAGE))
            c_prop = "Branch Coverage";
        else if (coverage_property.equals(PATH_COVERAGE))
            c_prop = "Path Coverage";

        for (List<String> tc_list : test_cases) {
            if (!tc_list.isEmpty()) {
                System.out.print(tc_list.get(0));
            }
            for (int i = 1; i < tc_list.size(); i++) {
                System.out.print(";" + tc_list.get(i));
            }
            System.out.println();
        }

        if (write_to_file) {
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
                System.out.println("Successfully computed and exported " + (test_cases.size() - 1) + " test case(s) covering " + c_prop + ".");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("Successfully computed " + (test_cases.size() - 1) + " test case(s) covering " + c_prop + ".");
        }
    }
}
