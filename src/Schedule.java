import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.text.StyledEditorKit.BoldAction;

/**
 * Schedule
 */
public class Schedule implements Comparable<Schedule> {

    private Map<Course,Slot> assignments;
    private Schedule parent;
    private int depth, score;
    private Map<Slot, Integer> counters;


    public Schedule(Schedule parent, final Map<Course,Slot> assignments){
        this(parent, assignments, null);
    }

    public Schedule(Schedule parent, final Map<Course,Slot> assignments, Pair<Course,Slot> newAssignment){
        //fresh copy of assignments
        this.assignments = new HashMap<>();
        this.counters = new HashMap<>();
        for (Entry<Course,Slot> var : assignments.entrySet()) {
            Slot s = var.getValue();
            Course c = var.getKey();
            insertElem(c, s);
        }
        //add the new node
        if (newAssignment != null) insertElem(newAssignment.fst(), newAssignment.snd());
        this.parent = parent;
        this.depth  = (parent == null) ? 0 : parent.depth;
        this.score  = eval();
    }

    private boolean insertElem(Course c, Slot s){

        Integer count = counters.get(s);
        //count occurances of an item.
        counters.put(s, (count == null) ? 1 : count + 1);
        this.assignments.put(c, s);
        return true; //unles a constraint is violated
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
        return    evalMinFilled()   * Model.getInstance().wMinFilled 
                + evalPair()        * Model.getInstance().wPair
                + evalPref()        * Model.getInstance().wPref
                + evalSecDiff()     * Model.getInstance().wSecDiff;
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
        for (Course c : Model.getInstance().getCourses()) {
            Slot s = assignments.get(c);
            if (s == null) {
            	assign = c;
            	break;
            }
        }
        //for each slot, make children in which this course is in that slot
        if (assign != null) {
            List<Slot> slots;
            if (assign instanceof Lecture){
                slots = new ArrayList<>(Model.getInstance().getCourseSlots());
            } else {
                slots = new ArrayList<>(Model.getInstance().getLabSlots());
            }
            for (Slot s : slots) {
                Schedule next = new Schedule(this, assignments, new Pair<Course,Slot>(assign, s));
                n.add(next);
            }
        } else {
            //If you get here, you have just called div on a solved node. Why. 
            System.out.println("Error, This node is already solved");
            
        }


        /**
         * Filter out nodes which violate the hard constraints and which are solved, 
         * and check if they are the best in a calling thread
         * TODO: Want to be checking for constaints and solvedness in the insertElem()/the constructor
         */

        List<Schedule> unsolvedNodes = new ArrayList<>();
        
        for (Schedule schedule: n) {

            boolean constr = schedule.constr();
            boolean solved = false;
            if (constr) {
                solved = schedule.solved();
            }

            if (constr && !solved){
                unsolvedNodes.add(schedule);
            } else if (solved) {
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
    
    public String toString() {
    	return assignments.toString();
    }
  
    
    /**
     * Print in row, in alphabetical order 
     * @return
     */
    public String prettyPrint() {
		return "";
	}
    
}