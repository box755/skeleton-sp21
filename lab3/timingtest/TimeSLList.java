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
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCOunts = new AList<>();

        SLList<Integer> lst;
        int N = 1000;
        while(N <= 128000){
            lst = new SLList<>();
            int ops = 10000;//getLast的測試中，ops是我們決定的（固定），用以測試不同大小list跑10000次要多久
            for(int i=0; i<N; i++){
                lst.addLast(i);
            }
            Stopwatch sw = new Stopwatch();
            for(int i=0; i<ops; i++){
                lst.getLast();
            }
            double timesInSeconds =  sw.elapsedTime();
            Ns.addLast(N);
            times.addLast(timesInSeconds);
            opCOunts.addLast(ops);
            N = N*2;
        }
        printTimingTable(Ns, times, opCOunts);
    }

}
