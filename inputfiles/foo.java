public class Test {
    public void foo (int x, int state, int trans) {
        int y;
        int new_state;
        if (state == 0) {
            y = 3*x;
            if (trans == 1) {
                new_state = 1;
            }
        } else {
            y = -2 * x;
            if (trans == 1) {
                new_state = 1;
            }
        }
    }
}
