package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        SLList<Integer> list = new SLList<>();
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCounts = new AList<>();
        int ops = 0, trick = 0;
        for (int i = 0; i < 128000; i++){
            list.addLast(i);
            ops++;
            if (list.size() == Math.pow(2, trick) * 1000){
                Stopwatch stopwatch = new Stopwatch();
                for (int j = 0; j < ops; j++) {
                    list.getLast();
                }
                Ns.addLast(list.size());
                times.addLast(stopwatch.elapsedTime());
                opCounts.addLast(ops);
                trick++;
            }
        }
        printTimingTable(Ns, times, opCounts);
    }

}
