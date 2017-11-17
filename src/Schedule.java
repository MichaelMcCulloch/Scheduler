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
                + evalSecDiff()      * Model.wSecDiff;
    }
    private int evalMinFilled(){
        return 0;
    }

    private int evalPref(){
        return 0;
    }

    private int evalPair(){
        return 0;
    }
    private int evalSecDiff(){
        return 0;
    }

    /**
     * The Div function, may be called by main
     * Accepting a function like a parameter
     * runs completion of caller (from main, checks global best; from searcher, checks local best)
     */
    public List<Schedule> div(Consumer<Schedule> completion) {
        List<Schedule> n = new ArrayList<>();

        //find a class not used in the current assignment
        Course assign = null;
        for (Course c : Model.getCourses()) {
            try {
                assignments.get(c);
                assign = c;
            } catch (Exception e) {
                break;
            }
        }
        //for each slot, make children in which this course is in that slot
        if (assign != null) {
            List<Slot> slots;
            if (assign instanceof Lecture){
                slots = new ArrayList<>(Model.getCourseSlots());
            } else {
                slots = new ArrayList<>(Model.getLabSlots());
            }
            for (Slot s : slots) {
                
            }
        } else {
            //check if solved
        }


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