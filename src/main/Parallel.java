package main;

import java.util.ArrayList;

/**
 * Created by Marius on 13/02/2017.
 */
public class Parallel {

    ArrayList<Thread> threads;

    public void forLoop(int range, ParallelRun runFunct){

        int threadCount= Runtime.getRuntime().availableProcessors();
        threads = new ArrayList<Thread>(threadCount-1);
        int rangePerThread = range/threadCount;
        for(int t=1; t<threadCount; t++){
            final int threadNumber = t;
            threads.add(new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=(threadNumber*rangePerThread); i<((threadNumber+1)*rangePerThread); i++)
                        runFunct.run(i);
                }
            }));
        }



    }

    public interface ParallelRun{

        void run(int i);

    }

}
