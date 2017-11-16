import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Searcher
 * Idea is that many of these can run simultaneously, and share a work queue. 
 * While goal state not met:
    *  Remove a PROB K from the queue,
    *  ...some processing tbd
    *  Remove a DIV D from K.divisons queue
    *  ks = DIV(K)
    *  ...some processing tbd
    *  put all ks into the work queue;
 */
public class Searcher implements Runnable {

    private PriorityQueue<Schedule> workQueue;
    private static volatile boolean shutdownSignal = false;
    private Schedule best;

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

        while (!shutdownSignal) {
            try {
                Schedule next = workQueue.remove();
                List<Schedule> children = next.div(checkBest);
                workQueue.addAll(children);
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
        //For testing
        System.out.println("Shutting down: " + workQueue.size());
    }

    //passing a function as a parameter
    Consumer<Schedule> checkBest = new Consumer<Schedule>() {
        public void accept(Schedule sched) {
            if (best == null || sched.betterThan(best)) {
                best = sched;
                Model.checkBest.accept(sched);
            }
        }
    };

}