import java.util.List;
import java.util.concurrent.locks.*;
import java.util.function.Consumer;

/**
 * Responsible for the creation of threads. Since all the peices of the tree exist here in the queue, this might as well be the model
 */

public class Model {

    //This all needs to change. Maybe the parser should instantiate this? Maybe we don't use it at all

    private static List<LabSlot> labSlots;
    private static List<CourseSlot> courseSlots;
    private static List<Course> allCourses;
    private static List<Triple<Course, Slot, Integer>> preferences;
    private static List<Pair<Course, Course>> together;
    
    public static int wMinFilled, wPref, wSecDiff, wPair;
    public static int penCourseMin, penLabMin, penSecDiff, penPair;

    
    private volatile static Schedule bestNode;
    private static Lock bestLock = new ReentrantLock(true);

    public Model(List<Course> courses, List<Slot> slots) {
        setData(courses, slots);
    }

    private static void setData(List<Course> courses, List<Slot> slots){
        allCourses = courses;
        
        for (Slot s : slots) {
            if (s instanceof CourseSlot){
                courseSlots.add((CourseSlot) s);
            } else {
                labSlots.add((LabSlot) s);
            }
        }

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

    public static List<LabSlot> getLabSlots(){
        return labSlots;
    }
    
    public static List<Triple<Course, Slot, Integer>> getPreferences(){
    	return preferences;
    }
    
    public static List<Pair<Course, Course>> getTogether() {
    	return together;
    }
}