public class Test {
    public int test1(int a, int b) {
        int x = 0;
        while (a < 10) {
            x = x + 1;
            a = a + 1;
        }

        if (b > 1) {
            if (b > 2) {
                if (b > 3) {
                    x = x - 1;
                }
            }
        }

        return x;
    }
}
