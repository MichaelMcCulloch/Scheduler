package scheduler;

import java.util.*;
import java.util.function.Consumer;

/**
 * Searcher 
 * While goal state not met:
    *  Remove a Schedule S from the queue,
    *  ks = S.DIV
    *  put all ks into the work queue;
 */
public class Searcher implements Runnable {

    private PriorityQueue<Schedule> workQueue;
    private static volatile boolean shutdownSignal = false;
    public static volatile Schedule best; //complete schedules only
    public static volatile Integer bound;
    
    protected static volatile int[] finished = new int[Model.getInstance().numThreads];

    public Searcher(List<Schedule> instances) {
        workQueue = new PriorityQueue<>(instances);
    }

    public static void stop() {
        shutdownSignal = true;
    }

    /**
     * Run the search control starting with the first node in the workQueue
     */
    @Override
    public void run() {
    	finished[(int) (Thread.currentThread().getId() % Model.getInstance().numThreads)] = 1;
        while (!workQueue.isEmpty() && !shutdownSignal) {
            try {
                Schedule next = workQueue.remove();
                if (bound == null || next.getBound() <= bound) {
                    List<Schedule> children = next.div(checkBest, bound);
                    if (children.isEmpty()) continue;
                    workQueue.addAll(children);
                }
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
        //For testing
        System.out.println("Shutting down: " + workQueue.size());
        finished[(int) (Thread.currentThread().getId() % Model.getInstance().numThreads)] = 0;

    }

    //passing a function as a parameter
    Consumer<Schedule> checkBest = new Consumer<Schedule>() {
        public void accept(Schedule sched) {
            checkBest(sched);
        }
    };

    public void checkBest(Schedule sched){
        if (best == null || sched.betterThan(best)) {
            best = sched;
            bound = sched.getScore();
        }
    }
    
    public static boolean finished() {
    	for (int i : finished) {
			if (i == 1) return false;
		}
    	return true;
    }

}