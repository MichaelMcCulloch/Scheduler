import java.util.List;
import java.util.concurrent.locks.*;
import java.util.function.Consumer;

/**
 * Responsible for the creation of threads. Since all the peices of the tree exist here in the queue, this might as well be the model
 */

public class Model {

    private static List<Slot> allSlots;
    private static List<Course> allCourses;

    
    private volatile static Schedule bestNode;
    private static Lock bestLock = new ReentrantLock(true);

    public Model(List<Slot> slots, List<Course> courses) {
        this.allCourses = courses;
        this.allSlots = slots;
    }


    /**
     * Picks the best node based on its score only. not depths
     */
    
    public static Consumer<Schedule> checkBest = new Consumer<Schedule>() {
        public void accept(Schedule sched){
            bestLock.lock();
            if (bestNode == null || sched.betterThan(bestNode)) {
                bestNode = sched;
            }
            bestLock.unlock();
        }
    };

    public static List<Course> getCourses(){
        return allCourses;
    }

    public static List<Slot> getSlots(){
        return allSlots;
    }
}