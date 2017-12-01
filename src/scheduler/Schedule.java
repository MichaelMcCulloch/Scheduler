package scheduler;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.xml.validation.SchemaFactoryConfigurationError;

/**
 * Schedule
 */
public class Schedule implements Comparable<Schedule> {

    private Map<Course, Slot> assignments;
    private Schedule parent;
    private int depth, score, bound;
    private Map<Slot, Integer> counters;



    public class ConstraintsFailed extends Exception{
        public ConstraintsFailed(){
            super();
        }
    }

  

    public Schedule(Schedule parent, final Map<Course, Slot> assignments) throws ConstraintsFailed {
        this(parent, assignments, null);
        for (Slot entry : Model.getInstance().getCourseSlots()) {
        	counters.put(entry, 0);
        }
        for (Slot entry : Model.getInstance().getLabSlots()) {
        	counters.put(entry, 0);
        }
    }

    public Schedule(Schedule parent, final Map<Course, Slot> assignments, Pair<Course, Slot> newAssignment) throws ConstraintsFailed{
        //fresh copy of assignments
        
        this.assignments = new HashMap<>();
        this.counters = new HashMap<>();
        
        for (Slot entry : Model.getInstance().getCourseSlots()) {
        	counters.put(entry, 0);
        }
        for (Slot entry : Model.getInstance().getLabSlots()) {
        	counters.put(entry, 0);
        }
        for (Entry<Course, Slot> var : assignments.entrySet()) {
            Slot s = var.getValue();
            Course c = var.getKey();
            insertElem(c, s);
        }
        //add the new node
        if (newAssignment != null) {
            if (!constr(newAssignment)) throw new ConstraintsFailed();
            insertElem(newAssignment.fst(), newAssignment.snd());
        }
        this.parent = parent;
        this.depth = (parent == null) ? 0 : parent.depth + 1;
        this.bound = evalPair() * Model.getInstance().getWeights(Model.Weight.Paired)
                    + evalPref() * Model.getInstance().getWeights(Model.Weight.Preference)
                    + evalSecDiff() * Model.getInstance().getWeights(Model.Weight.SectionDifference);
        this.score = bound + evalMinFilled() * Model.getInstance().getWeights(Model.Weight.MinFilled);
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
        return (this.score < other.score);
    }

    public int getBound(){
        return this.bound;
    }
    
    /*
    private int eval() {
        return evalMinFilled() * Model.getInstance().getWeights(Model.Weight.MinFilled)
                + evalPair() * Model.getInstance().getWeights(Model.Weight.Paired)
                + evalPref() * Model.getInstance().getWeights(Model.Weight.Preference)
                + evalSecDiff() * Model.getInstance().getWeights(Model.Weight.SectionDifference);
    }
    */

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
                for (Lecture sibling : ((Lecture) entry).getSiblings()) {
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
    public List<Schedule> div(Consumer<Schedule> checkBest, Function<Integer, Boolean> boundCheck) {
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
                try{
                    Schedule next = new Schedule(this, assignments, new Pair<Course, Slot>(assign, s));
                    
                    boolean satisfactory = boundCheck.apply(next.getBound());
                    
                    if (next.solved()) checkBest.accept(next);
                    else if (satisfactory) n.add(next);
                } catch (Schedule.ConstraintsFailed e) {
                    //new assignment did not meet constraints.
                }
            }
        } else {
            //If you get here, you have just called div on a solved node. Why. 
            //System.out.println("Error, This node is already solved");
            //System.out.println(this);

        }

        /**
         * Filter out nodes which violate the hard constraints and which are solved, 
         * and check if they are the best in a calling thread
         * TODO: Want to be checking for constaints and solvedness in the insertElem()/the constructor
         */

        //List<Schedule> unsolvedNodes = new ArrayList<>();

        
        return n;
    }

    /**
     * Decide if this problem instance meets the hard constraints by checking the newly assigned slot
     */
    public boolean constr(Pair<Course, Slot> newAssignment) {
         		boolean a =constrMax(newAssignment);
         		boolean b =constrCourseConflict(newAssignment);
         		boolean c =constrUnwanted(newAssignment);
         		boolean d =constrEveningSlot(newAssignment);
         		boolean e =constrTue11(newAssignment);
         		return a && b && c && d && e;
    }

    //TODO: ADDING THE LIST OF ALL 500 COURSES TO THE MUTEX LIST FOR EACH 500 LEVEL COURSE.

    /**
    * Checks the hard constraints of courseMax and labMax
    */
    public boolean constrMax(Pair<Course, Slot> newAssignment) {

        Slot s = newAssignment.snd();
        
        if (s instanceof CourseSlot) {
            Integer courseCounter = counters.get(s);
            if (courseCounter!=null){
	            if (courseCounter > s.getMax()) {
	                return false;
	            }
            }
        } else if (s instanceof LabSlot) {
            Integer labCounter = counters.get(s);
            if (labCounter!=null) {
	            if (labCounter > s.getMax()) {
	                return false;
	            }
            }
        }
        return true;
    }

    /**
    * Checks the hard constraints of having different time slots for a course and its corresponding labs
    */
    public boolean constrCourseConflict(Pair<Course, Slot> newAssignment) {

        Course c = newAssignment.fst();

        if (c instanceof Lecture) {
            for (Course conflict : c.getMutex()) {
                Slot conflictTimeSlot = assignments.get(conflict);
                if (conflictTimeSlot != null) {
	                if (conflict instanceof Lecture) {
	                    if (newAssignment.snd() == conflictTimeSlot) {
	                        return false;
	                    }
	                }
	                if (conflict instanceof Lab && hasOverlap(newAssignment.snd(), conflictTimeSlot)) {
	                    return false;
	                }
                }
            }

        } else if (c instanceof Lab) {
            for (Course conflict : c.getMutex()) {
                Slot conflictTimeSlot = assignments.get(conflict);
                if (conflictTimeSlot != null) {
	                if (conflict instanceof Lab) {
	                    if (newAssignment.snd() == conflictTimeSlot) {
	                        return false;
	                    }
	                }
	                if (conflict instanceof Lecture && hasOverlap(newAssignment.snd(), conflictTimeSlot)) {
	                    return false;
	                }
	            }
            }
        }
        return true;
    }

    /**
    * Checks if a lecture time has an overlap with a lab time
    * Does not work for lab-lab conflicts
    */
    public boolean hasOverlap(Slot lectureTime, Slot labTime) {
        String lectureDay = lectureTime.getDay();
        int lecTime = lectureTime.getTime();
        String labDay = labTime.getDay();
        int laboratoryTime = labTime.getTime();

        int labHour = laboratoryTime / 60;
        int lectureHour = lecTime / 60;

        if (lecTime <= laboratoryTime){
            if ((lectureDay == "MO" ||lectureDay == "FR") && (labDay == "MO" || labDay == "FR")) {
                if (lecTime + lectureTime.getDuration() > laboratoryTime) return true;
            } else if (lectureDay == "TU" && labDay == lectureDay) {
                if (lecTime + lectureTime.getDuration() > laboratoryTime) return true;
            }
        }

        if (laboratoryTime <= lecTime){
            if ((lectureDay == "MO" ||lectureDay == "FR") && (labDay == "MO" || labDay == "FR")) {
                if (laboratoryTime + labTime.getDuration() > lecTime) return true;
            } else if (lectureDay == "TU" && labDay == lectureDay) {
                if (laboratoryTime + labTime.getDuration() > lecTime) return true;
            }
        }
        return false;
    }

    /**
    * Checks the hard constraints for unwanted time slots for specific courses
    */
    public boolean constrUnwanted(Pair<Course, Slot> newAssignment) {

        Course c = newAssignment.fst();
        Slot badSlot = Model.getInstance().getUnwanted().get(c);
        if (badSlot == null) return true;
        else if (badSlot == newAssignment.snd()) return false;
        else return true;
    }

    /**
    * Checks the hard constraints for evening time slots assigned to LEC 9 courses
    */
    public boolean constrEveningSlot(Pair<Course, Slot> newAssignment) {

        Course c = newAssignment.fst();

        if (c instanceof Lecture) {
            int sectionNum = c.getSectNum();
            if (sectionNum >= 900) {
                Slot s = newAssignment.snd();
                if (s.getTime() < 18 * 60) {
                    return false;
                }
            } 
        } 
        return true;
    }

    /**
    * Checks the hard constraints for no courses to be scheduled at Tue 11-12:30
    */
    public boolean constrTue11(Pair<Course, Slot> newAssignment) {

        Course c = newAssignment.fst();

        if (c instanceof Lecture && (newAssignment.snd()).byDayTime("Tue", "11:00")) {
            return false;
        } else if (c instanceof Lab) {
            Slot labTime = newAssignment.snd();
            if (labTime.byDayTime("Tue", "11:00") || labTime.byDayTime("Tue", "12:00")) {
                return false;
            }
        } 
        return true;
    }

    /**
     * If the instance is not solved/unsolvable, return false;
     * Otherwise, if it is Solved, check if it's the best;
     * If it is unsolvable, just return true;
     * if it is below the bound, discard it
     */
    public boolean solved() {
        for (Course c : Model.getInstance().getCourses()) {
        	if (assignments.get(c) == null) return false;
		}
        return true;
    }

    public String toString() {
        return assignments.toString();
    }

    /**
     * Print in row, in alphabetical order 
     * @return
     */
    public String prettyPrint() {
        
        int maxPadding = 0;
        List<Pair<Course, Slot>> list = new ArrayList<>();
        for (Entry<Course, Slot> assign : this.assignments.entrySet()) {
            list.add(new Pair<>(assign.getKey(), assign.getValue()));
            if (assign.getKey().toString().length() > maxPadding)
                maxPadding = assign.getKey().toString().length();
        }
        
        list.sort(new Comparator<Pair<Course,Slot>>() {
            @Override
            public int compare(Pair<Course, Slot> o1, Pair<Course, Slot> o2) {
                return o1.fst().toString().compareTo(o2.fst().toString());
            }
        });
        
        String out = "Eval " + score + "\n";
        for (Pair<Course, Slot> pair : list) {
            String courseName = pair.fst().toString();
            int paddingToAdd = maxPadding - courseName.length();
            for (int i = 0; i < paddingToAdd; i++) courseName += " ";
             out += courseName + "\t" + pair.snd() + "\n";
         }
        
        return out;
    }

}