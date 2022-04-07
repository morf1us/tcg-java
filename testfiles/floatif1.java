public class Test {
    public int test1(float a, float b, float c) {
        int x = 0;
        if (a > 1.0) {
            if (b > 2.0) {
                if (c > 3.0) {
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
