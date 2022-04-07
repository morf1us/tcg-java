public class Test {
    public int test1(boolean a, boolean b, boolean c) {
        int x = 0;
        if (a == true) {
            if (b == true) {
                if (c == true) {
                    x = x - 1;
                }
                else {
                    x = x + 1;
                }
            }
            else {
                x = x - 1;
            }
        }
        else {
            x = x - 1;
        }
        
        return x;
    }
}
