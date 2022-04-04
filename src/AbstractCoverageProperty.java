import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.IntExpr;

import java.util.ArrayList;
import java.util.List;


abstract class AbstractCoverageProperty {
    protected Context ctx;
    protected List<BoolExpr> constraints;
    protected List<IntExpr> input_variables;
    protected List<List<String>> test_cases;
    protected boolean restrict_values;
    protected int min;
    protected int max;

    protected static final String ABNORMAL = "ab_";
    protected static final String OBJECTIVE = "objective";
    protected static final String TEMP = "temp_";

    public AbstractCoverageProperty(Context ctx, List<BoolExpr> constraints, List<IntExpr> input_variables, boolean restrict_values, int min, int max) {
        this.ctx = ctx;
        this.constraints = new ArrayList<>(constraints);
        this.input_variables = new ArrayList<>(input_variables);
        this.test_cases = new ArrayList<>();
        this.restrict_values = restrict_values;
        this.min = min;
        this.max = max;

        prepareTestCases();
    }

    protected void prepareTestCases() {
        List<String> vars = new ArrayList<>();
        for (IntExpr iv : this.input_variables) {
            String v = iv.toString();
            int i = v.lastIndexOf("_");
            vars.add(v.substring(0, i));
        }
        this.test_cases.add(vars);
    }

    protected void addValueRangeConstraints() {
        for (IntExpr e : this.input_variables) {
            BoolExpr ge = this.ctx.mkGe(e, ctx.mkInt(this.min));
            BoolExpr le = this.ctx.mkLe(e, ctx.mkInt(this.max));
            BoolExpr and = this.ctx.mkAnd(ge, le);
            this.constraints.add(and);
        }
    }

    protected void removeObjective() {
        if (!this.constraints.isEmpty()) {
            BoolExpr last_c = this.constraints.get(this.constraints.size() - 1);
            if (last_c.getNumArgs() > 0 && last_c.getArgs()[0].toString().equals(OBJECTIVE)) {
                this.constraints.remove(constraints.size() - 1);
            }
        }
    }

    protected void removeABs() {
        for (int i = 0; i < this.constraints.size(); i++) {
            BoolExpr c = this.constraints.get(i);
            if (c.getNumArgs() > 1 && c.getArgs()[0].toString().startsWith(ABNORMAL)) {
                this.constraints.set(i, (BoolExpr) c.getArgs()[1]);
            }
        }
    }

    public List<List<String>> getTestCases() { return this.test_cases; }
    abstract void computeTestCases();
}
