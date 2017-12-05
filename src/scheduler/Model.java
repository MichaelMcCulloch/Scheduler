package scheduler;
import java.util.*;
import java.util.function.Consumer;


public class Model {
	public static int numThreads = 2;//Runtime.getRuntime().availableProcessors();
	
    private static volatile Model instance = null;

    private List<LabSlot> labSlots;
    private List<CourseSlot> courseSlots;
    private List<Course> allCourses;

    private Map<Course, List<Slot>> unwanted;
    private List<Triple<Course, Slot, Integer>> preferences;
    private List<Pair<Course, Course>> together, incompatible;

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

    private Map<Weight, Integer> weights;
    private Map<Penalty, Integer> penalties;
                
    protected Model(){
        //no direct instantiation
    }

    public static Model getInstance(){
        if (instance == null) {
        	instance = new Model();
        }
        return instance;
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
                        Map<Weight, Integer> weights,
                        Map<Penalty, Integer> penalties) {

        
        //set the courses
        this.allCourses = allCourses;
        this.labSlots = labSlots;
        this.courseSlots = courseSlots;
        this.unwanted = unwanted;
        this.preferences = preferences;
        this.together = together;
        this.incompatible = incompatible;

        this.weights = weights;
        this.penalties = penalties;
    }

    public Consumer<Schedule> checkBest = new Consumer<Schedule>() {
        public void accept(Schedule sched){
            checkBest(sched);
        }
    };

    public List<Course> getCourses(){ return allCourses; }

    public List<LabSlot> getLabSlots(){ return labSlots; }
    public List<CourseSlot> getCourseSlots(){ return courseSlots; }
    public List<Triple<Course, Slot, Integer>> getPreferences(){ return preferences; }
    public List<Pair<Course, Course>> getTogether(){ return together; }
    public List<Pair<Course, Course>> getIncompatible(){ return incompatible; }
    public Map<Course,List<Slot>> getUnwanted(){ return unwanted; }


    public int getWeights(Weight w){
        return weights.get(w);
    }

    public int getPenalies(Penalty p){
        return penalties.get(p);
    }

    private void checkBest(Schedule sched){
        Schedule best = Searcher.best;
        if (best == null || sched.betterThan(best)) {
            best = sched;
            Searcher.bound = sched.getScore();
        }
    }
    public Schedule getBest() {
    	Schedule b = Searcher.best;
    	return b;
    }

    
}