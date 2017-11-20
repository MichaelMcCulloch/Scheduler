import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Schedule
 */
public class Schedule implements Comparable<Schedule> {

    private Map<Course,Slot> assignments;
    private Schedule parent;
    private int depth, score;
    private Map<Slot, Integer> counters;




    public Schedule(Schedule parent, Map<Course,Slot> assignments){

        //fresh copy of assignments
        this.assignments = new HashMap<>();
        for (Entry<Course,Slot> var : assignments.entrySet()) {
            this.assignments.put(var.getKey(), var.getValue());
        }
        this.parent = parent;
        this.depth = parent.depth;
        this.score = eval();
    }

    public Schedule(Schedule parent, Map<Course,Slot> assignments, Map<Slot, Integer> counters){
        this(parent, assignments);
        //fresh copy of Counters
        this.counters = new HashMap<>();
        for (Entry<Slot,Integer> var : counters.entrySet()) {
            this.counters.put(var.getKey(), var.getValue());
        }
        
    }

    public Map<Course, Slot> getAssigned(){
        return assignments;
    }

    //Serves purpose of fLEAF;
    @Override    
    public int compareTo(Schedule other) {
        if (this.depth > other.depth) return -1;
        else if (this.depth < other.depth) return 1;
        else return (betterThan(other)) ? -1 : 1;
    }

    public boolean betterThan(Schedule other){
        return (this.score > other.score);
    }

    private int eval(){
        return    evalMinFilled()   * Model.wMinFilled 
                + evalPair()        * Model.wPair
                + evalPref()        * Model.wPref
                + evalSecDiff()     * Model.wSecDiff;
    }
    
    private int evalMinFilled(){
    	int sum = 0;
    	for (Map.Entry<Slot, Integer> entry : counters.entrySet()) {
    		int delta = entry.getKey().getMin() - entry.getValue();
    		if (delta > 0) {
    			if (entry.getKey() instanceof CourseSlot) {
    				sum += delta * Model.penCourseMin;
    			} else {
    				sum += delta * Model.penLabMin;
    			}
    		} 
    	}
        return sum;
    }

    private int evalPref(){
        int sum = 0;
        for (Triple<Course, Slot, Integer> pref : Model.getPreferences()) {
        	if (!assignments.get(pref.fst()).equals(pref.snd())) {
        		sum += pref.trd();
        	}
        }
        return sum;
    }

    private int evalPair(){
    	int sum = 0;
    	for (Pair<Course, Course> pair : Model.getTogether()) {
    		if (!assignments.get(pair.fst()).equals(assignments.get(pair.snd()))) {
    			sum += Model.penPair;
    		}
    	}
        return sum;
    }
    
    private int evalSecDiff(){
        //Need a good way to compare the assignment of a course section to other sections of the same course. Going to think about this for a bit.
        return 0;
    }

    /**
     * The Div function, may be called by main
     * Accepting a function like a parameter
     * runs completion of caller (from main, checks global best; from searcher, checks local best)
     */
    public List<Schedule> div(Consumer<Schedule> completion) {
        List<Schedule> n = new ArrayList<>();



        /**
         * Filter out nodes which violate the hard constraints and which are solved, 
         * and check if they are the best in a calling thread
         */

        List<Schedule> unsolvedNodes = new ArrayList<>();
        
        for (Schedule schedule: n) {
            if (schedule.constr() && !schedule.solved()){
                unsolvedNodes.add(schedule);
                completion.accept(schedule);
            }
        }
        return unsolvedNodes;
    }

    /**
     * Decide if this problem instance meets the hard constraints
     */
    public boolean constr() {
        return constrMax()
                && constrIncompatible()
                && constrUnwanted();
    }

    public boolean constrMax(){
        return false;
    }

    public boolean constrIncompatible(){
        return false;
    }

    public boolean constrUnwanted(){
        return false;
    }

    /**
     * If the instance is not solved/unsolvable, return false;
     * Otherwise, if it is Solved, check if it's the best;
     * If it is unsolvable, just return true;
     * if it is below the bound, discard it
     */
    public boolean solved() {
        
        return false;
    }
    
}