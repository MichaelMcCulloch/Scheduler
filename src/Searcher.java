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

    public PriorityBlockingQueue<Node<Prob>> qRef;

    public Searcher() {

    }

    /**
     
     */
    @Override
    public void run() {

    }

}
