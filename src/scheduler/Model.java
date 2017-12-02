package scheduler;
import java.util.*;
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

    private Map<Course, List<Slot>> unwanted;
    private List<Triple<Course, Slot, Integer>> preferences;
    private List<Pair<Course, Course>> together, incompatible;
    
    private volatile Schedule bestNode = null;
    private volatile Integer bound = null;

    private volatile Lock boundLock = new ReentrantLock(true);

    public enum Weight{
        MinFilled,
        Preference,
        SectionDifference,
        Paired
    }
    
    public enum Penalty {
        CourseMin,
        LabMin,
        SectionDifference,
        Pair
    }

    private int wMinFilled = 1, 
                wPref = 1, 
                wSecDiff = 1, 
                wPair = 1;

    private int penCourseMin = 1,
            penLabMin = 1, 
            penSecDiff = 1,
            penPair = 1;
                
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
    					Map<Course,List<Slot>> unwanted, 
    					List<Triple<Course,Slot,Integer>> preferences, 
    					List<Pair<Course, Course>> together,
                        List<Pair<Course, Course>> incompatible,
                        int[] weights,
                        int[] penalties) throws AlreadyInstantiated {
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
        this.wMinFilled = weights[0];
        this.wPref = weights[1];
        this.wSecDiff = weights[2];
        this.wPair = weights[3];
        penCourseMin = penalties[0];
        penLabMin = penalties[1];
        penSecDiff = penalties[2];
        penPair = penalties[3];
    }

    /**
     * Picks the best node based on its score only. not depths
     */
    
    public Consumer<Schedule> checkBest = new Consumer<Schedule>() {
        public void accept(Schedule sched){
            checkBest(sched);
        }
    };
    
    public Integer getBound() {
    	return bound;
    }
    
    public void setBound(int i) {
    	boundLock.lock();
    	bound = i;
    	boundLock.unlock();
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
    
    public List<Triple<Course, Slot, Integer>> getPreferences(){
        return preferences;
    }
    
    public List<Pair<Course, Course>> getTogether(){
        return together;
    }

    public List<Pair<Course, Course>> getIncompatible(){
        return incompatible;
    }

    public Map<Course,List<Slot>> getUnwanted(){
        return unwanted;
    }

    public int getWeights(Weight w){
        switch (w) {
            case MinFilled: return wMinFilled;
            case Preference: return wPref;
            case SectionDifference: return wSecDiff;
            case Paired: return wPair;
            default: return 0;
        }
    }

    public int getPenalies(Penalty p){
        switch (p) {
            case CourseMin: return penCourseMin;
            case LabMin: return penLabMin;
            case SectionDifference: return penSecDiff;
            case Pair: return penPair;
            default: return 0;
        }
    }

    private synchronized void checkBest(Schedule sched){
        if (bestNode == null || sched.betterThan(bestNode)) {
            bestNode = sched;
            bound = sched.getScore();
        }
    }
    public synchronized Schedule getBest() {
    	Schedule b = bestNode;
    	return b;
    }

    
}