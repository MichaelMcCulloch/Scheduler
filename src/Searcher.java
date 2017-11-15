import java.awt.AlphaComposite;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

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

    private PriorityQueue<Node<Integer>> workQueue;
    private Node<Integer> best;

    public Searcher(ArrayList<Node<Integer>> instances) {
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
                Node<Integer> next = workQueue.remove();

                ArrayList<Node<Integer>> children = Model.div(next);

                /**
                 * track best so far, put it on the shared 
                 
                if (best == null || candidate.compareTo(best) > 0)
                    best = candidate;
                */
                workQueue.addAll(children);
            } catch (Exception e) {
                //TODO: handle exception
            }

        }
        //For testing
        System.out.println("Shutting down: " + workQueue.size());
    }

}