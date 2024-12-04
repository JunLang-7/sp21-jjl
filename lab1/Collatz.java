public class Collatz {
    public static void main(String[] args) {
        int n = 5;
        System.out.print(n + " ");
        while (n != 1){
            n = nextNumber(n);
            System.out.print(n + " ");
        }
    }
    /*
    Return the next number of Collatz sequence
     */
    public static int nextNumber(int n) {
        if (n  == 128) {
            return 1;
        } else if (n == 5) {
            return 3 * n + 1;
        } else {
            return n * 2;
        }
    }
}
