import com.microsoft.z3.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements the computation of test cases fulfilling the path coverage criterion, i.e. the computed
 * test cases will visit all possible paths in the program at least once.
 *
 */

public class PathCoverage extends AbstractCoverageProperty {
    public PathCoverage(Context ctx, List<BoolExpr> constraints, List<Expr<?>> input_variables) {
        super(ctx, constraints, input_variables);
    }

    public void computeTestCases() {
        // prepare constraints
        removeObjective();
        removeABs();
        if (restrict_values)
            addValueRangeConstraints();

        // prepare solver
        Solver solver = ctx.mkSolver();
        for (BoolExpr b : constraints) {
            solver.add(b);
        }

        while (solver.check() == Status.SATISFIABLE) {
            Model model = solver.getModel();

            HashMap<String, Expr<?>> sat_model = new HashMap<>();
            for (FuncDecl<?> cd : model.getConstDecls()) {
                sat_model.put(cd.getName().toString(), model.getConstInterp(cd));
            }
            List<BoolExpr> negated_assignments = new ArrayList<>();
            List<String> stored_model = new ArrayList<>();

            for (Map.Entry<String, Expr<?>> entry : sat_model.entrySet()) {
                if (entry.getKey().startsWith(TEMP)) {
                    BoolExpr condition = (BoolExpr) ctx.mkConst(entry.getKey(), ctx.mkBoolSort());
                    BoolExpr negated_assignment = ctx.mkNot(ctx.mkEq(condition, entry.getValue()));
                    negated_assignments.add(negated_assignment);
                }
            }
            for (Expr<?> iv : input_variables) {
                stored_model.add(sat_model.get(iv.toString()).toString());
            }
            // store temp assignments for testing
            /*for (Map.Entry<String, Expr<?>> entry : sat_model.entrySet()) {
                if (entry.getKey().startsWith(TEMP))
                    stored_model.add(entry.getKey() + ":" + entry.getValue());
            }*/

            BoolExpr prev_model = ctx.mkOr(negated_assignments.toArray(new BoolExpr[0]));
            solver.add(prev_model);

            test_cases.add(stored_model);
        }
    }
}
