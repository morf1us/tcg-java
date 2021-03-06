import com.microsoft.z3.*;

import java.util.*;

/**
 * This class implements the computation of test cases fulfilling the branch coverage criterion, i.e. the computed
 * test cases will visit each possible branch in the program at least once.
 *
 */

public class BranchCoverage extends AbstractCoverageProperty {
    private final HashSet<String> relevant_branches;

    public BranchCoverage(Context ctx, List<BoolExpr> constraints, List<Expr<?>> input_variables) {
        super(ctx, constraints, input_variables);
        relevant_branches = new HashSet<>();
    }

    private void computeRelevantBranches() {
        HashSet<String> abnormals = new HashSet<>();
        for (BoolExpr c : this.constraints) {
            if (c.getNumArgs() > 1 && c.getArgs()[0].toString().startsWith(ABNORMAL)) {
                if (!abnormals.contains(c.getArgs()[0].toString())) {
                    BoolExpr rhs = (BoolExpr) c.getArgs()[1];
                    if (rhs.getNumArgs() > 1 && rhs.getArgs()[0].toString().startsWith(TEMP))
                        relevant_branches.add(rhs.getArgs()[0].toString());
                    abnormals.add(c.getArgs()[0].toString());
                }
            }
        }
    }

    public void computeTestCases() {
        // compute relevant temp variables.
        computeRelevantBranches();

        // prepare constraints
        removeObjective();
        removeABs();

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
                if (relevant_branches.contains(entry.getKey())) {
                    BoolExpr condition = (BoolExpr) ctx.mkConst(entry.getKey(), ctx.mkBoolSort());
                    BoolExpr negated_assignment = ctx.mkNot(ctx.mkEq(condition, entry.getValue()));
                    negated_assignments.add(negated_assignment);
                }
            }
            for (Expr<?> iv : input_variables) {
                if (sat_model.get(iv.toString()) != null) {
                    stored_model.add(sat_model.get(iv.toString()).toString());
                }
                else { // input variable doesn't have any influence on branches/paths, so generate generic value
                    stored_model.add(generateGenericInputValue(iv));
                }
            }

            BoolExpr prev_model = ctx.mkOr(negated_assignments.toArray(new BoolExpr[0]));
            solver.add(prev_model);

            test_cases.add(stored_model);
        }
    }
}
