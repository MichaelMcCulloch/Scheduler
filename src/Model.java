import java.util.concurrent.locks.*;

/**
 * Responsible for the creation of threads. Since all the peices of the tree exist here in the queue, this might as well be the model
 */

public class Model {

    private volatile static Node<Prob> bestNode;
    private static Lock bestLock = new ReentrantLock(true);
    public static volatile boolean shutdownSignal;
    private Node<Prob> root;

    public Model(Node<Prob> root) {
        this.root = root;
    }

    /**
     * Picks the best node based on its score only. not depths
     */
    public static void checkBest(Node<Prob> candidate) {
        bestLock.lock();
        if (bestNode == null || candidate.getInstance().compareTo(bestNode.getInstance()) < 0) {
            bestNode = candidate;
        }
        bestLock.unlock();
    }
}