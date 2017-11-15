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

    boolean shutdown = false;

    private PriorityQueue<Node<Integer>> workQueue;
    private Node<Integer> best;

    public Searcher(ArrayList<Node<Integer>> instances) {
        workQueue = new PriorityQueue<>();
        this.workQueue.addAll(instances);
    }

    public void shutdown() {
        shutdown = true;
    }

    private ArrayList<Node<Integer>> layeredSearch(int depth, ArrayList<Node<Integer>> accumulator) {

        if (depth == 0)
            return accumulator;
        ArrayList<Node<Integer>> next = new ArrayList<>();

        for (Node<Integer> node : accumulator) {
            next.addAll(processNode(node));
        }

        return layeredSearch(depth - 1, next);

    }

    private ArrayList<Node<Integer>> processNode(Node<Integer> node) {

        /**
         * track best so far, put it on the shared 
         
        if (best == null || candidate.compareTo(best) > 0)
            best = candidate;
        */
        ArrayList<Node<Integer>> next = Model.div(node);
        return next;
    }

    /**
     
     */
    @Override
    public void run() {

        while (!shutdown) {
            
            try {
                Node<Integer> next = workQueue.remove();
                ArrayList<Node<Integer>> temp = new ArrayList<>();
                temp.add(next);
                
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
        System.out.println("Shutting down: " + workQueue.size());
    }

}