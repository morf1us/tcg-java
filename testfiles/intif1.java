public class Test {
    public int test1(int a, int b) {
        int x = 0;
        int y = 0;
        if (a > 1) {
            x = 2;
            if (b > 2) {
                y = x + 1;
            }
        }
        else {
            while (a < 0) {
                y = y + 1;
                a = a + 1;
            }
        }
        
        return x;
    }
}
