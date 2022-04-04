import com.microsoft.z3.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BranchCoverage extends AbstractCoverageProperty {
    public BranchCoverage(Context ctx, List<BoolExpr> constraints, List<IntExpr> input_variables, boolean all_solutions, int min, int max) {
        super(ctx, constraints, input_variables, all_solutions, min, max);
    }

    public List<List<String>> computeTestCases() {
        List<String> vars = new ArrayList<>();

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

            HashMap<String, Expr> sat_model = new HashMap<>();
            for (FuncDecl cd : model.getConstDecls()) {
                System.out.println(cd.toString());
                System.out.println(model.getConstInterp(cd));
                sat_model.put(cd.getName().toString(), model.getConstInterp(cd));
            }
            List<BoolExpr> negated_assignments = new ArrayList<>();
            List<String> stored_model = new ArrayList<>();

            for (Map.Entry<String, Expr> entry : sat_model.entrySet()) {
                if (entry.getKey().startsWith(TEMP)) {
                    BoolExpr condition = (BoolExpr) ctx.mkConst(entry.getKey(), ctx.mkBoolSort());
                    BoolExpr negated_assignment = ctx.mkNot(ctx.mkEq(condition, entry.getValue()));
                    negated_assignments.add(negated_assignment);
                }
            }
            for (IntExpr iv : input_variables) {
                stored_model.add(sat_model.get(iv.toString()).toString());
            }

            BoolExpr prev_model = ctx.mkOr(negated_assignments.toArray(new BoolExpr[0]));
            solver.add(prev_model);

            test_cases.add(stored_model);
        }

        return test_cases;
    }
}
