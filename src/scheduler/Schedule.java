package scheduler;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.lang.*;

/**
 * Schedule
 */
public class Schedule implements Comparable<Schedule> {

    private Map<Course, Slot> assignments;
    private int depth, score, bound;
    private Map<Slot, Integer> counters;
    
    

    public class ConstraintsFailed extends Exception {
        public ConstraintsFailed() {
            super();
        }
        
        
    }

    /**
     * To initialize the root node
     */
    public Schedule(final Map<Course,Slot> initialAssignments){
        //no parent
        this.depth = 0;
        //all zero
        counters = new HashMap<>();
        for (Slot entry : Model.getInstance().getCourseSlots()) {
            counters.put(entry, 0);
        }
        for (Slot entry : Model.getInstance().getLabSlots()) {
            counters.put(entry, 0);
        }

        //count new Assignments
        this.assignments = initialAssignments;
        for (Entry<Course,Slot> assign : initialAssignments.entrySet()) {
            int count = counters.get(assign.getValue());
            counters.put(assign.getValue(), count + 1);
        }
        this.bound = evalPair() 	* Model.getInstance().getWeights(Model.Weight.Paired)
                   + evalSecDiff() 	* Model.getInstance().getWeights(Model.Weight.SectionDifference);
        this.score = bound 
                   + evalPref() 	* Model.getInstance().getWeights(Model.Weight.Preference)
        		   + evalMinFilled() * Model.getInstance().getWeights(Model.Weight.MinFilled);

    }

    public Schedule(Schedule parent, final Pair<Course,Slot> newAssignment) throws ConstraintsFailed{
        
        this.assignments = new HashMap<>();
        this.counters = new HashMap<>();
        this.depth = parent.depth + 1;
        
        //copy data from parent
        assignments.putAll(parent.assignments);
        counters.putAll(parent.counters);

        //add new assignment
        assignments.put(newAssignment.fst(), newAssignment.snd());
        //increment counter
        int countSlot = counters.get(newAssignment.snd());
        counters.put(newAssignment.snd(), countSlot + 1);
        //check constraints
        if (!constr(newAssignment)) throw new ConstraintsFailed();
        parent = null; //GC
        this.bound = evalPair()      * Model.getInstance().getWeights(Model.Weight.Paired)
                   + evalSecDiff()   * Model.getInstance().getWeights(Model.Weight.SectionDifference);
        this.score = bound 
        		   + evalPref()      * Model.getInstance().getWeights(Model.Weight.Preference)
                   + evalMinFilled() * Model.getInstance().getWeights(Model.Weight.MinFilled);

    }


    public Map<Course, Slot> getAssigned() {
        return assignments;
    }

    //Serves purpose of fLEAF;
    @Override
    public int compareTo(Schedule other) {
    	//if (Searcher.best == null) {
		    if (this.depth > other.depth)
		        return -1;
		    else if (this.depth < other.depth)
		        return 1;
			//}
        return (betterThan(other)) ? -1 : 1;
    }

    public boolean betterThan(Schedule other) {
        return (this.score < other.score);
    }

    public int getBound() {
        return this.bound;
    }

    public int getScore() {
        return this.score;
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
            if (slot != null && (!slot.equals(pref.snd()))) {
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
            if (slot1 != null && slot2 != null && (!slot1.equals(slot2))) {
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
    public List<Schedule> div(Consumer<Schedule> checkBest, Integer bound) {
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
                try {
                    Schedule next = new Schedule(this, new Pair<Course, Slot>(assign, s));
                    boolean satisfactory = (bound == null || this.bound < bound);
                    //System.out.println(String.format("bound: %d, test: %d", bound,this.bound));
                    if (next.solved())
                        checkBest.accept(next);
                    else if (satisfactory)
                        n.add(next);
                } catch (Schedule.ConstraintsFailed e) {
                }
            }
        }
        return n;
    }

    /**
     * Decide if this problem instance meets the hard constraints by checking the newly assigned slot
     */
    public boolean constr(Pair<Course, Slot> newAssignment) {
        boolean a = constrMax(newAssignment);
        boolean b = constrCourseConflict(newAssignment);
        boolean c = constrUnwanted(newAssignment);
        boolean d = constrEveningSlot(newAssignment);
        boolean e = constrTue11(newAssignment);
        return a && b && c && d && e;
    }

    //TODO: ADDING THE LIST OF ALL 500 COURSES TO THE MUTEX LIST FOR EACH 500 LEVEL COURSE.

    /**
    * Checks the hard constraints of courseMax and labMax
    */
    public boolean constrMax(Pair<Course, Slot> newAssignment) {

        Slot s = newAssignment.snd();

        if (s instanceof CourseSlot) {
            //System.out.println(s);
            //System.out.println(s.getMax());
            //System.out.println(counters.get(s));
            Integer courseCounter = counters.get(s);
            if (courseCounter != null) {
                if (courseCounter > s.getMax()) {
                    return false;
                }
            }
        } else if (s instanceof LabSlot) {
            Integer labCounter = counters.get(s);
            if (labCounter != null) {
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
                    if (conflict instanceof Lab) {
                        if (hasOverlap(newAssignment.snd(), conflictTimeSlot)) {
                            return false;
                        }
                    }
                }
            }

        } else if (c instanceof Lab) {
            for (Course conflict : c.getMutex()) {
                Slot conflictTimeSlot = assignments.get(conflict);
                if (conflictTimeSlot != null) {
                    if (conflict instanceof Lab) {
                        continue;
                    }
                    if (conflict instanceof Lecture) {
                        if (hasOverlap(newAssignment.snd(), conflictTimeSlot)) {
                            return false;
                        }
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
        Slot.Day lectureDay = lectureTime.getDay();
        int lecTime = lectureTime.getTime();
        Slot.Day labDay = labTime.getDay();
        int laboratoryTime = labTime.getTime();

        if (lecTime <= laboratoryTime) {
            if ((lectureDay == Slot.Day.Monday) && (labDay == Slot.Day.Monday || labDay == Slot.Day.Friday)) {
                if (lecTime + lectureTime.getDuration() > laboratoryTime)
                    return true;
            } else if (lectureDay == Slot.Day.Tuesday && labDay.equals(lectureDay)) {
                if (lecTime + lectureTime.getDuration() > laboratoryTime)
                    return true;
            }
        }

        if (laboratoryTime <= lecTime) {
            if (lectureDay == Slot.Day.Monday && (labDay == Slot.Day.Monday || labDay == Slot.Day.Friday)) {
                if (laboratoryTime + labTime.getDuration() > lecTime)
                    return true;
            } else if (lectureDay == Slot.Day.Tuesday && labDay.equals(lectureDay)) {
                if (laboratoryTime + labTime.getDuration() > lecTime)
                    return true;
            }
        }
        return false;
    }

    /**
    * Checks the hard constraints for unwanted time slots for specific courses
    */
    public boolean constrUnwanted(Pair<Course, Slot> newAssignment) {

        Course c = newAssignment.fst();
        List<Slot> badSlot = Model.getInstance().getUnwanted().get(c);
        if (badSlot == null)
            return true;
        for (Slot slot : badSlot) {
            if (slot == newAssignment.snd())
                return false;
        }
        return true;
    }

    /**
    * Checks the hard constraints for evening time slots assigned to LEC 9 courses
    */
    public boolean constrEveningSlot(Pair<Course, Slot> newAssignment) {

        Course c = newAssignment.fst();

        if (c instanceof Lecture) {
            int sectionNum = c.getLecNum();
            int firstDigit = Integer.parseInt(Integer.toString(sectionNum).substring(0, 1));
            if (firstDigit >= 9) {
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
        int eleven = 11 * 60, twelve = 12 * 60;
        if (c instanceof Lecture && (newAssignment.snd()).byDayTime(Slot.Day.Tuesday, eleven)) {
            return false;
        } else if (c instanceof Lab) {
            Slot labTime = newAssignment.snd();
            if (labTime.byDayTime(Slot.Day.Tuesday, eleven) || labTime.byDayTime(Slot.Day.Tuesday, twelve)) {
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
            if (assignments.get(c) == null)
                return false;
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

        list.sort(new Comparator<Pair<Course, Slot>>() {
            @Override
            public int compare(Pair<Course, Slot> o1, Pair<Course, Slot> o2) {
                return o1.fst().toString().compareTo(o2.fst().toString());
            }
        });

        String out = "Eval " + score + "\n";
        for (Pair<Course, Slot> pair : list) {
            String courseName = pair.fst().toString();
            int paddingToAdd = maxPadding - courseName.length();
            for (int i = 0; i < paddingToAdd; i++)
                courseName += " ";
            out += courseName + "\t" + pair.snd() + "\n";
        }

        return out;
    }

}