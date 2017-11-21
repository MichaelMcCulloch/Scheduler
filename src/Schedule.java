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
            if (!assignments.get(pref.fst()).equals(pref.snd())) {
                sum += pref.trd();
            }
        }
        return sum;
    }

    private int evalPair() {
        int sum = 0;
        for (Pair<Course, Course> pair : Model.getInstance().getTogether()) {
            if (!assignments.get(pair.fst()).equals(assignments.get(pair.snd()))) {
                sum += Model.getInstance().getPenalies(Model.Penalty.Pair);
            }
        }
        return sum;
    }

    private int evalSecDiff() {
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
     * Decide if this problem instance meets the hard constraints by checking the newly assigned slot
     */
    public boolean constr(Pair<Course,Slot> newAssignment) {    // QUESTION : Do we need partAssign in the constr() function or
        return constrMax(newAssignment)                         // are we going to put them in the hash table at the beginning??????????
                && constrCourseLabConflict(newAssignment)
                && constrIncompatible(newAssignment)
                && constrUnwanted(newAssignment)
                && constrEveningSlot(newAssignment)
                && constr500Level(newAssignment)
                && constrTue11(newAssignment)
                && constrSpecialCourses(newAssignment);
    }

    /**
    * Checks the hard constraints of courseMax and labMax
    */
    public boolean constrMax(Pair<Course,Slot> newAssignment){

        Slot s = newAssignment.snd();

        if (s instanceof CourseSlot) {
            int courseCounter = counters.get(s);
            if (courseCounter > s.courseMax) {
                return false;
            }
        } else if (s instanceof LabSlot) {
            int labCounter = counters.get(s);
            if (labCounter > s.labMax) {
                return false;
            }
        }
        return true;
    }

    /**
    * Checks the hard constraints of having different time slots for a course and its corresponding labs
    */
    public boolean constrCourseLabConflict(Pair<Course,Slot> newAssignment){

        Course c = newAssignment.fst();

        if (c instanceof Lecture) {
            String[] nameParts = c.name.split(" ");
            // Searching the hash table to find all Lab Slots that have the same number as nameParts[1]
            // If no Lab Slots is found --> return true
            // otherwise:
            // for (all found lab slots){
            //  if (labSlot == newAssignment.snd()){
            //      return false;
            //  } else if (labSlot is on Fri && newAssignment.snd() is on Mon && (labSlotHour + 1) == newAssignment.snd()){
            //      return false;
            //  } else if (labSlot is on Tue && newAssignment.snd() is on Tue && (labSlotHour - 0.5) == newAssignment.snd()){
            //      return false;
            //  } else if (labSlot is on Tue && newAssignment.snd() is on Tue && (labSlotHour + 0.5) == newAssignment.snd()){
            //      return false;
            //  }
            //}
        } else if (c instanceof Lab) {
            String[] labNameParts = c.name.split(" ");

            if (labNameParts.length == 6) {
                // Searching the hash table to find all Course Slots that have same number as labNameParts[1] && have same LEC as labNameParts[3]
                // If no Course Slots is found --> return true
                // otherwise:
                // for (all found course slots){
                //  if (courseSlot == newAssignment.snd()){
                //      return false;
                //  } else if (courseSlot is on Mon && newAssignment.snd() is on Fri && (courseSlotHour - 1) == newAssignment.snd()){
                //      return false;
                //  } esle if (courseSlot is on Tue && newAssignment.snd() is on Tue && (courseSlotHour + 1) == newAssignment.snd()){
                //      return false;
                //  } else if (courseSlot is on Tue && newAssignment.snd() is on Tue && (courseSlotHour + 0.5) == newAssignment.snd()){
                //      return false;
                //  } else if (courseSlot is on Tue && newAssignment.snd() is on Tue && (courseSlotHour - 0.5) == newAssignment.snd()){
                //      return false;
                //  }
                // }
            } else {
                // Searching the hash table to find all Course Slots that have same number as labNameParts[1]
                // If no Course Slots is found --> return true
                // otherwise:
                // for (all found course slots){
                //  if (courseSlot == newAssignment.snd()){
                //      return false;
                //  } else if (courseSlot is on Mon && newAssignment.snd() is on Fri && (courseSlotHour - 1) == newAssignment.snd()){
                //      return false;
                //  } esle if (courseSlot is on Tue && newAssignment.snd() is on Tue && (courseSlotHour + 1) == newAssignment.snd()){
                //      return false;
                //  } else if (courseSlot is on Tue && newAssignment.snd() is on Tue && (courseSlotHour + 0.5) == newAssignment.snd()){
                //      return false;
                //  } else if (courseSlot is on Tue && newAssignment.snd() is on Tue && (courseSlotHour - 0.5) == newAssignment.snd()){
                //      return false;
                //  }
                // }
            }
        }
    }

    /**
    * Checks the hard constraints for incompatible courses
    */
    public boolean constrIncompatible(Pair<Course,Slot> newAssignment){

        Course c = newAssignment.fst();

        for (Course incompatibleCourse : ((Model.getInstance().getIncompatible()).fst() || (Model.getInstance().getIncompatible()).snd())) {
            
            if (c == incompatibleCourse && exists(assignments.get(incompatibleCourse))) { //checking if c is in incompatible list and if the other
                                                                                          //course in incompatible pair has been assigned to a slot yet
                Slot s1 = assignments.get(c);
                Slot s2 = assignments.get(incompatibleCourse);

                if (s1 == s2) {
                    return false;
                    break;
                }
                return true;
            }
        }

        return true;
    }

    /**
    * Checks the hard constraints for unwanted time slots for specific courses
    */
    public boolean constrUnwanted(Pair<Course,Slot> newAssignment) {

        Course c = newAssignment.fst();

        for (Course unwantedCourse : (Model.getInstance().getUnwanted()).fst()) {
            if (c == unwantedCourse) {
                Slot newlyAssignedSlot = newAssignment.snd();
                Slot unwantedSlot = (Model.getInstance().getUnwanted()).snd();
                if (newlyAssignedSlot == unwantedSlot) {
                    return false;
                    break;
                }
            }
        }
        return true;
    }

    /**
    * Checks the hard constraints for evening time slots assigned to LEC 9 courses
    */
    public boolean constrEveningSlot(Pair<Course,Slot> newAssignment){

        Course c = newAssignment.fst();

        if (c instanceof Lecture) {
            String[] nameParts = c.name.split(" ");
            if (nameParts[3] == "9" || nameParts[3] == "09") {
                Slot s = newAssignment.snd();
                if (s.getHour() != "18:00" || s.getHour() != "19:00" || s.getHour() != "20:00" || s.getHour() != "18:30") {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
    * Checks the hard constraints for 500 level courses to be scheduled at different times
    */
    public boolean constr500Level(Pair<Course,Slot> newAssignment){

        Course c = newAssignment.fst();

        if (c instanceof Lecture) {
            String[] nameParts = c.name.split(" ");
            if (nameParts[1].charAt(0)  == "5") {
                Slot s = newAssignment.snd();
                // Searching in the hash table to fine the slots of all 500 courses that are assigned to a time slot
                // for (all found 500 courses)
                // their time slot != s
                // other wise return false
                
            } 
        } else {
            return true;
        }
    }

    /**
    * Checks the hard constraints for no courses to be scheduled at Tue 11-12:30
    */
    public boolean constrTue11(Pair<Course,Slot> newAssignment){

        Course c = newAssignment.fst();

        if (c instanceof Lecture && (newAssignment.snd()).byDayTime("Tue","11:00")) {
            return false;
        } else if (c instanceof Lab) {
            Slot labTime = newAssignment.snd();
            if (labTime.byDayTime("Tue","11:00") || labTime.byDayTime("Tue","12:00")) {
                return false;
            }
        } else {
            return true;
        }
    }

    /**
    * Checks the hard constraints for special courses that cannot be scheduled at the same time (CPSC 313 and CPSC 813)
    */
    public boolean constrSpecialCourses(Pair<Course,Slot> newAssignment){      // Maybe we should add these courses to the incompatible!!!
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