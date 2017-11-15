import java.util.*;
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

    private PriorityQueue<Node<Prob>> workQueue;
    private Node<Prob> best;

    public Searcher(List<Node<Prob>> instances) {
        workQueue = new PriorityQueue<>();
        this.workQueue.addAll(instances);
    }

    /**
     * Run the search control starting with the first node in the workQueue
     */
    @Override
    public void run() {

        while (!Model.shutdownSignal) {
            try {
                Node<Prob> next = workQueue.remove();
                List<Node<Prob>> children = div(next);

                // Filter out solved/unsolved nodes, they don't belong on the queue;
                List<Node<Prob>> unsolvedNodes = children.stream().filter(p -> !decideSolved(p))
                        .collect(Collectors.toList());

                workQueue.addAll(unsolvedNodes);
            } catch (Exception e) {
                //TODO: handle exception
            }
        }
        //For testing
        System.out.println("Shutting down: " + workQueue.size());
    }

    /**
     * The Div function, may be called by main
     */
    public static List<Node<Prob>> div(Node<Prob> instance) {

        List<Node<Prob>> n = new ArrayList<>();
        return n;
    }

    /**
     * If the instance is not solved/unsolvable, return false;
     * Otherwise, if it is Solved, check if it's the best;
     * If it is unsolvable, just return true;
     * if it is below the bound, discard it
     */
    private static boolean decideSolved(Node<Prob> instance) {
        return false;
    }

    private void checkBest(Node<Prob> instance) {
        if (best == null || instance.getInstance().compareTo(best.getInstance()) < 0) {
            best = instance;
            Model.checkBest(instance);
        }
    }

}