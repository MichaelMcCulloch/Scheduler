import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.*;
import java.util.function.Consumer;

/**
 * Responsible for the creation of threads. Since all the peices of the tree exist here in the queue, this might as well be the model
 */

public class Model {

    //This all needs to change. Maybe the parser should instantiate this? Maybe we don't use it at all

    private static volatile Model instance = null;
    private boolean isDataSet = false;
    
    private String name;
    private List<LabSlot> labSlots;
    private List<CourseSlot> courseSlots;
    private List<Course> allCourses;

    private Map<Course, Slot> unwanted;
    private List<Triple<Course, Slot, Integer>> preferences;
    private List<Pair<Course, Course>> together, incompatible;
    
    private volatile Schedule bestNode = null;
    private volatile Integer bound = null;

    private volatile Lock bestLock = new ReentrantLock(true);
    private volatile Lock boundLock = new ReentrantLock(true);

    public int  wMinFilled = 1, 
                wPref = 1, 
                wSecDiff = 1, 
                wPair = 1;
    protected Model(){
        //no direct instantiation
    }

    public static Model getInstance(){
        if (instance == null) {
        	instance = new Model();
        }
        return instance;
    }
    
    public class AlreadyInstantiated extends Error{
        public AlreadyInstantiated(){
            super();
        }
    }


    /**
     * Concurrency bug: Call this only from parser
     */
    public void setData(List<Course> allCourses, 
    					List<LabSlot> labSlots,
    					List<CourseSlot> courseSlots,
    					Map<Course,Slot> unwanted, 
    					List<Triple<Course,Slot,Integer>> preferences, 
    					List<Pair<Course, Course>> together,
    					List<Pair<Course, Course>> incompatible) throws AlreadyInstantiated {
        if (isDataSet) throw new AlreadyInstantiated();
        isDataSet = true;
        
        //set the courses
        this.allCourses = allCourses;
        this.labSlots = labSlots;
        this.courseSlots = courseSlots;
        this.unwanted = unwanted;
        this.preferences = preferences;
        this.together = together;
        this.incompatible = incompatible;

    }

    /**
     * Picks the best node based on its score only. not depths
     */
    
    public Consumer<Schedule> checkBest = new Consumer<Schedule>() {
        public void accept(Schedule sched){
            bestLock.lock();
            if (bestNode == null || sched.betterThan(bestNode)) {
                bestNode = sched;
            }
            bestLock.unlock();
        }
    };

    public Integer checkBound(int newBound){
        boundLock.lock();
        Integer better = null;
        if (bound == null || newBound < bound) {
            bound = newBound;
            better = null;
        } else {
            better = bound;
        }

        boundLock.unlock();
        return better;
    }

    public List<Course> getCourses(){
        return allCourses;
    }

    public List<LabSlot> getLabSlots(){
        return labSlots;
    }
    public List<CourseSlot> getCourseSlots(){
        return courseSlots;
    }
    
    public Schedule getBest() {
    	bestLock.lock();
    	Schedule b = bestNode;
    	bestLock.unlock();
    	return b;
    }
}