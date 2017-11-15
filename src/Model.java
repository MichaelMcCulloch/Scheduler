import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Responsible for the creation of threads. Since all the peices of the tree exist here in the queue, this might as well be the model
 */

public class Model {

    public static Node<Prob> bestNode;

    public PriorityBlockingQueue<Node<Prob>> queue;

    public Model(Node<Prob> root) {
        queue = new PriorityBlockingQueue<>();
        queue.add(root);
    }

    public static void newBest(Node<Prob> candidate) {
        if (bestNode == null || candidate.compareTo(bestNode) > 0) {
            bestNode = candidate;
        }
    }

    public static void main(String[] args) {

        PriorityBlockingQueue<Node<Integer>> test = new PriorityBlockingQueue<>();
        test.add(new Node(null, 0));

        int poolSize = 1;
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);

        Searcher[] searchers = new Searcher[poolSize];
        for (int i = 0; i < poolSize; i++) {
            Searcher k = new Searcher(test);
            searchers[i] = k;
            pool.execute(k);
        }
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            //TODO: handle exception
        }

        for (int i = 0; i < poolSize; i++) {
            searchers[i].shutdown();
        }
        pool.shutdown();
        System.out.println(test.size());
    }

}