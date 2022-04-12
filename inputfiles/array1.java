public class Test {
    public int test1(int prev_max, int prev_min) {
        int[] input = {1, 2, 3, 4, 5, 6};
        int length = 6;
        
        int i = 1;
        int result = 0;
        int min = prev_min;
        int max = prev_max;
        
        while (i < length) {
            if (input[i] < min) {
                min = input[i];
            }
            else if (input[i] > max) {
                max = input[i];
            }
            i = i + 1;
        }
        result = min * max;
        return result;
    }
}
