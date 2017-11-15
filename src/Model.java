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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Responsible for the creation of threads. Since all the peices of the tree exist here in the queue, this might as well be the model
 */

public class Model {

    public static Node<Prob> bestNode;
    private static Lock bestLock = new ReentrantLock(true);

    public PriorityBlockingQueue<Node<Prob>> queue;

    public Model(Node<Prob> root) {
        queue = new PriorityBlockingQueue<>();
        queue.add(root);
    }

    public static void newBest(Node<Prob> candidate) {
        bestLock.lock();
        if (bestNode == null || candidate.compareTo(bestNode) > 0) {
            bestNode = candidate;
        }
        bestLock.unlock();
    }

    public static ArrayList<Node<Integer>> div(Node<Integer> instance) {

        ArrayList<Node<Integer>> n = new ArrayList<>();

        for (int j = 0; j < 50; j++) {
            n.add(new Node(instance, instance.getInstance() + j));
        }
        return n;
    }

    public static void main(String[] args) {

        Node<Integer> root = new Node<Integer>(null, 0);
        int poolSize = 8;
        ExecutorService pool = Executors.newFixedThreadPool(poolSize);

        Searcher[] searchers = new Searcher[poolSize];
        for (int i = 0; i < poolSize; i++) {
            ArrayList<Node<Integer>> next = new ArrayList<>();
            next.add(new Node<Integer>(root, i + 1));

            Searcher k = new Searcher(next);
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
    }

}