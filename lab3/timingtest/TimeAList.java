package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        //初始化Ns, times, opCOunts來儲存每次實驗的資料
        AList<Integer> Ns = new AList<>();
        AList<Double> times = new AList<>();
        AList<Integer> opCOunts = new AList<>();
        //初始化lst作為實驗容器，每次迴圈會重新初始化
        AList<Integer> lst;
        int N = 1000;
        while(N <= 8000){
            lst = new AList<>();
            int ops = 0;
            Stopwatch sw = new Stopwatch();//使用大學給的類別來計算每次addLast完一定次數元素後，所花的時間
            for(int i=0; i<N; i++){
                lst.addLast(i);
                ops ++;
            }
            double timeInSeconds = sw.elapsedTime();//停止計時
            //把這輪實驗資料存到list中
            Ns.addLast(N);
            times.addLast(timeInSeconds);
            opCOunts.addLast(ops);
            N = N*2;
        }
        printTimingTable(Ns, times, opCOunts);//呼叫範本提供的印表函式，導入各個資料的list


    }
}
