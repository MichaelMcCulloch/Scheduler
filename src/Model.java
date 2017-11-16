import java.util.List;
import java.util.concurrent.locks.*;

/**
 * Responsible for the creation of threads. Since all the peices of the tree exist here in the queue, this might as well be the model
 */

public class Model {

    private static List<Slot> allSlots;
    private static List<Course> allCourses;

    
    private volatile static Node<Prob> bestNode;
    private static Lock bestLock = new ReentrantLock(true);
    public static volatile boolean shutdownSignal;
    private Node<Prob> root;

    public Model(Node<Prob> root, List<Slot> slots, List<Course> courses) {
        this.root = root;
        this.allCourses = courses;
        this.allSlots = slots;
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

    public static List<Course> getCourses(){
        return allCourses;
    }

    public static List<Slot> getSlots(){
        return allSlots;
    }
}