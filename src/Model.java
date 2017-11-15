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
    public static volatile boolean shutdownSignal;
    private Node<Prob> root;

    public Model(Node<Prob> root) {
        this.root = root;
    }

    public static void newBest(Node<Prob> candidate) {
        bestLock.lock();
        if (bestNode == null || candidate.compareTo(bestNode) > 0) {
            bestNode = candidate;
        }
        bestLock.unlock();
    }

    /**
     * Global Div function, may be called by multiple threads
     * Mark nodes solved or unsolved here
     * Mark best node here
     */
    public static ArrayList<Node<Prob>> div(Node<Prob> instance) {

        ArrayList<Node<Prob>> n = new ArrayList<>();

        return n;
    }
}