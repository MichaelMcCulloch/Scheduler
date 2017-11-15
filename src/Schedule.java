import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Schedule
 */
public class Schedule implements Comparable<Schedule> {

    private List<Slot> assignments;
    private Schedule parent;
    private int depth, score;



    public Schedule(Schedule parent, List<Slot> assignments){
        this.assignments = assignments;
        this.parent = parent;
        this.depth = parent.depth;
        this.score = fLEAF();
    }

    public List<Slot> getAssigned(){
        return assignments;
    }

    @Override
    public int compareTo(Schedule other) {
        if (this.depth < other.depth) return -1;
        else if (this.depth > other.depth) return 1;
        else return (betterThan(other)) ? -1 : 1;
    }

    public boolean betterThan(Schedule other){
        return (this.score > other.score);
    }

    private int fLEAF(){
        return 0;
    }

    /**
     * The Div function, may be called by main
     * Accepting a function like a parameter
     * runs completion of caller (from main, checks global best; from searcher, checks local best)
     */
    public List<Schedule> div(Consumer<Schedule> completion) {
        List<Schedule> n = new ArrayList<>();

        int selected = 0;
        List<Slot> available = Model.getSlots();
        List<Slot> allocated = getAssigned();

        while (allocated.get(selected) != null) {
            selected++;
        } // find first available slot to fill.
          // Iterate through all available slots
        for (Slot t : available) {
            //Prepare a fresh copy
            List<Slot> newAssignment = new ArrayList<>(allocated.size());
            Collections.copy(newAssignment, allocated);

            //assign the course to the timeslot
            newAssignment.set(selected, t);
            Schedule next = new Schedule(this, newAssignment);
            n.add(next);
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
        return true;
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