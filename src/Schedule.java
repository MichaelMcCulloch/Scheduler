import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;


/**
 * Schedule
 */
public class Schedule implements Comparable<Schedule> {

    private Map<Course, Slot> assignments;
    private Schedule parent;
    private int depth, score;
    private Map<Slot, Integer> counters;

    public Schedule(Schedule parent, final Map<Course, Slot> assignments) {
        this(parent, assignments, null);
    }

    public Schedule(Schedule parent, final Map<Course, Slot> assignments, Pair<Course, Slot> newAssignment) {
        //fresh copy of assignments
        this.assignments = new HashMap<>();
        this.counters = new HashMap<>();
        for (Entry<Course, Slot> var : assignments.entrySet()) {
            Slot s = var.getValue();
            Course c = var.getKey();
            insertElem(c, s);
        }
        //add the new node
        if (newAssignment != null)
            insertElem(newAssignment.fst(), newAssignment.snd());
        this.parent = parent;
        this.depth = (parent == null) ? 0 : parent.depth + 1;
        this.score = eval();
    }

    private boolean insertElem(Course c, Slot s) {

        Integer count = counters.get(s);
        //count occurances of an item.
        counters.put(s, (count == null) ? 1 : count + 1);
        this.assignments.put(c, s);
        return true; //unles a constraint is violated
    }

    public Map<Course, Slot> getAssigned() {
        return assignments;
    }

    //Serves purpose of fLEAF;
    @Override
    public int compareTo(Schedule other) {
        if (this.depth > other.depth)
            return -1;
        else if (this.depth < other.depth)
            return 1;
        else
            return (betterThan(other)) ? -1 : 1;
    }

    public boolean betterThan(Schedule other) {
        return (this.score > other.score);
    }

    private int eval() {
        return  evalMinFilled() * Model.getInstance().getWeights(Model.Weight.MinFilled)
                + evalPair() * Model.getInstance().getWeights(Model.Weight.Paired)
                + evalPref() * Model.getInstance().getWeights(Model.Weight.Preference)
                + evalSecDiff() * Model.getInstance().getWeights(Model.Weight.SectionDifference);
    }

    private int evalMinFilled() {
        int sum = 0;
        for (Map.Entry<Slot, Integer> entry : counters.entrySet()) {
            int delta = entry.getKey().getMin() - entry.getValue();
            if (delta > 0) {
                if (entry.getKey() instanceof CourseSlot) {
                    sum += delta * Model.getInstance().getPenalies(Model.Penalty.CourseMin);
                } else {
                    sum += delta * Model.getInstance().getPenalies(Model.Penalty.LabMin);
                }
            }
        }
        return sum;
    }

    private int evalPref() {
        int sum = 0;
        for (Triple<Course, Slot, Integer> pref : Model.getInstance().getPreferences()) {
        	Slot slot = assignments.get(pref.fst());
            if (slot == null || (!slot.equals(pref.snd()))) {
                sum += pref.trd();
            }
        }
        return sum;
    }

    private int evalPair() {
        int sum = 0;
        for (Pair<Course, Course> pair : Model.getInstance().getTogether()) {
        	Slot slot1 = assignments.get(pair.fst());
        	Slot slot2 = assignments.get(pair.snd());
            if (slot1 == null || slot2 == null || (!slot1.equals(slot2))) {
                sum += Model.getInstance().getPenalies(Model.Penalty.Pair);
            }
        }
        return sum;
    }

    private int evalSecDiff() {
        int sum = 0;
        for (Course entry : Model.getInstance().getCourses()) {
        	if (entry instanceof Lecture) {
        		for (Lecture sibling : ((Lecture)entry).getSiblings()) {
        			Slot slot1 = assignments.get(sibling);
        			Slot slot2 = assignments.get(entry);
        			if (slot1 != null && slot2 != null && slot1.equals(slot2)) {
        				sum += Model.getInstance().getPenalies(Model.Penalty.SectionDifference);
        			}
        		}
        	}
        }
        return sum;
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
            if (assign instanceof Lecture) {
                slots = new ArrayList<>(Model.getInstance().getCourseSlots());
            } else {
                slots = new ArrayList<>(Model.getInstance().getLabSlots());
            }
            for (Slot s : slots) {
                Schedule next = new Schedule(this, assignments, new Pair<Course, Slot>(assign, s));
                n.add(next);
            }
        } else {
            //If you get here, you have just called div on a solved node. Why. 
            System.out.println("Error, This node is already solved");
            System.out.println(this);

        }

        /**
         * Filter out nodes which violate the hard constraints and which are solved, 
         * and check if they are the best in a calling thread
         * TODO: Want to be checking for constaints and solvedness in the insertElem()/the constructor
         */

        List<Schedule> unsolvedNodes = new ArrayList<>();

        for (Schedule schedule : n) {

            boolean constr = true; //schedule.constr();
            boolean solved = false;
            if (constr) {
                solved = false; //schedule.solved();
            }

            if (constr && !solved) {
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
        return constrMax() && constrIncompatible() && constrUnwanted();
    }

    public boolean constrMax() {
        return false;
    }

    public boolean constrIncompatible() {
        return false;
    }

    public boolean constrUnwanted() {
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