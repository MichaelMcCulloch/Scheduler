import java.util.ArrayList;
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

    /**
     * No null nodes
     */
    private WorkQueue<Node<Prob>> workQueue;

    private Node<Prob> best;

    public Searcher(WorkQueue<Node<Prob>> workQueue) {
        this.workQueue = workQueue;
    }

    /**
     
     */
    @Override
    public void run() {

        while (true) {

            Node<Prob> next = workQueue.remove();

            ArrayList<Node<Prob>> children = div(next);

            /**
             * track best so far, put it on the shared 
             */
            if (best == null || candidate.compareTo(best) > 0)
                best = candidate;

            workQueue.add(children);
        }
    }

    private ArrayList<Node<Prob>> div(Node<Prob> instance) {

        return null;
    }

}