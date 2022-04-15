import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class providing necessary functionalities used by all coverage property child classes.
 *
 */

abstract class AbstractCoverageProperty {
    protected Context ctx;
    protected List<BoolExpr> constraints;
    protected List<Expr<?>> input_variables;
    protected List<List<String>> test_cases;

    protected static final String ABNORMAL = "ab_";
    protected static final String OBJECTIVE = "objective";
    protected static final String TEMP = "temp_";

    public AbstractCoverageProperty(Context ctx, List<BoolExpr> constraints, List<Expr<?>> input_variables) {
        this.ctx = ctx;
        this.constraints = new ArrayList<>(constraints);
        this.input_variables = new ArrayList<>(input_variables);
        this.test_cases = new ArrayList<>();

        prepareTestCases();
    }

    protected void prepareTestCases() {
        List<String> vars = new ArrayList<>();
        for (Expr<?> iv : this.input_variables) {
            String v = iv.toString();
            int i = v.lastIndexOf("_");
            vars.add(v.substring(0, i));
        }
        this.test_cases.add(vars);
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

    protected String generateGenericInputValue(Expr<?> iv) {
        if (iv.isBool()) {
            return "true";
        }
        if (iv.isInt()) {
            return "0";
        }
        return "";
    }

    public List<List<String>> getTestCases() { return this.test_cases; }

    abstract void computeTestCases();
}
