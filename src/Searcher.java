import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
    private Schedule best; //complete schedules only
    private Integer bound; //TODO: for incomplete schedules
    private Model model;
    private int count = 0;

    public Searcher(List<Schedule> instances) {
        workQueue = new PriorityQueue<>(instances);
        this.model = Model.getInstance();
    }

    public static void stop() {
        shutdownSignal = true;
    }

    /**
     * Run the search control starting with the first node in the workQueue
     */
    @Override
    public void run() {
    	
    	Schedule last = null;

        while (!workQueue.isEmpty() && !shutdownSignal) {
            try {
                Schedule next = workQueue.remove();
                List<Schedule> children = next.div(checkBest);
                if (children.isEmpty()) continue;
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
                model.checkBest.accept(sched);
            }
        }
    };

    Consumer<Integer> adjustBound = new Consumer<Integer>() {
        public void accept(Integer newBound) {
            if (bound == null || newBound < bound){
                bound = newBound;
                Integer maybeEvenBetter = model.checkBound(newBound);
                if (maybeEvenBetter != null) bound = maybeEvenBetter;
            }
        }
    };
}