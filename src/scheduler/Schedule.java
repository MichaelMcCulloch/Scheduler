package scheduler;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.lang.*;

/**
 * Schedule
 */
public class Schedule implements Comparable<Schedule> {

    private ArrayList<Pair<Course,Slot>> assignments;
    private ArrayList<Pair<Slot,Integer>> counters;
    private Schedule parent;
    private int depth, score, bound;
    
    protected ArrayList<Pair<Course,Slot>> getAssignments(Pair<Course,Slot> newAssignment){
        if (parent == null) return assignments;
        else {
            ArrayList<Pair<Course,Slot>> ll = (ArrayList<Pair<Course,Slot>>) parent.assignments.clone();
            ll.add(newAssignment);
            return ll;
        }
    }
    protected ArrayList<Pair<Slot,Integer>> getCounts(Pair<Course,Slot> newAssignment){
        if (parent == null) return counters;
        else {
            ArrayList<Pair<Slot,Integer>> ll = (ArrayList<Pair<Slot,Integer>>) parent.counters.clone();
            boolean found = false;
            for (int i = 0; i < ll.size(); i++){
                Pair<Slot,Integer> old = ll.get(i);
                if (old.fst() == newAssignment.snd()){
                    Integer c = old.snd();
                    ll.remove(i);
                    ll.add(new Pair<Slot,Integer>(old.fst(), c+1));
                    found = true;
                    break;
                }
            }
            if (!found) ll.add(new Pair<Slot,Integer>(newAssignment.snd(), 1));
            return ll;
        }
    }
    

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
        this.parent = null;
        this.depth = 0;
        //all zero
        Map<Slot,Integer> count = new HashMap<>();
        this.assignments = new ArrayList<>();
        for (Entry<Course,Slot> var : initialAssignments.entrySet()) {
            assignments.add(new Pair<Course,Slot>(var.getKey(), var.getValue()));
            Integer old = count.get(var.getValue());
            if (old == null) old = 0;
            count.put(var.getValue(), old + 1);
        }
        this.counters = new ArrayList<>();
        for (Entry<Slot,Integer> var : count.entrySet()) {
            this.counters.add(new Pair<Slot,Integer>(var.getKey(), var.getValue()));
        }
        count = null; //GC

        this.bound = evalPair() 	* Model.getInstance().getWeights(Model.Weight.Paired)
                   + evalSecDiff() 	* Model.getInstance().getWeights(Model.Weight.SectionDifference);
        this.score = bound 
                   + evalPref() 	* Model.getInstance().getWeights(Model.Weight.Preference)
        		   + evalMinFilled() * Model.getInstance().getWeights(Model.Weight.MinFilled);

    }

    public Schedule(Schedule parent, final Pair<Course,Slot> newAssignment) throws ConstraintsFailed{
        this.parent = parent;
        this.assignments = getAssignments(newAssignment);
        this.counters = getCounts(newAssignment);
        this.depth = parent.depth + 1;

        //increment counter
        
        //check constraints
        if (!constr(newAssignment)) throw new ConstraintsFailed();

        this.bound = evalPair()      * Model.getInstance().getWeights(Model.Weight.Paired)
                   + evalSecDiff()   * Model.getInstance().getWeights(Model.Weight.SectionDifference);
        this.score = bound 
        		   + evalPref()      * Model.getInstance().getWeights(Model.Weight.Preference)
                   + evalMinFilled() * Model.getInstance().getWeights(Model.Weight.MinFilled);

    }

    //Serves purpose of fLEAF;
    @Override
    public int compareTo(Schedule other) {
    	if (Searcher.best == null) {
		    if (this.depth > other.depth)
		        return -1;
		    else if (this.depth < other.depth)
		        return 1;
			}
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
        for (Pair<Slot,Integer> var : this.counters) {
            int delta = var.fst().getMin() - var.snd();
            if (delta > 0){
                if (var.fst() instanceof CourseSlot){
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
        Map<Course,Slot> temp = new HashMap<>(); //memoize
        for (Triple<Course, Slot, Integer> pref : Model.getInstance().getPreferences()) {
            Slot slot = temp.get(pref.fst()); //is it in the map?
            if (slot == null) { //find it
                for (Pair<Course,Slot> var : this.assignments) {
                    if (var.fst() == pref.fst()){
                        slot = var.snd();
                        temp.put(var.fst(), var.snd());
                        break;
                    }
                }
                continue;
            }
            if (slot != null && (!slot.equals(pref.snd()))) {
                sum += pref.trd();
            }
        }
        temp = null; //GC
        return sum;
    }

    private int evalPair() {
        int sum = 0;
        Map<Course,Slot> temp = new HashMap<>(); //to memoize with
        for (Pair<Course, Course> pair : Model.getInstance().getTogether()) {
            Slot slot1 = temp.get(pair.fst());
            if (slot1 == null) { //try to find it
                for(Pair<Course,Slot> var : this.assignments){
                    if (var.fst() == pair.fst()){
                        slot1 = var.snd();
                        temp.put(var.fst(), var.snd());
                        break;
                    }
                }
            }
            Slot slot2 = temp.get(pair.snd());
            if (slot2 == null) { //try to find it
                for(Pair<Course,Slot> var : this.assignments){
                    if (var.fst() == pair.snd()){
                        slot2 = var.snd();
                        temp.put(var.fst(), var.snd());
                        break;
                    }
                }
            }
            if (slot1 != null && slot2 != null && (!slot1.equals(slot2))) {
                sum += Model.getInstance().getPenalies(Model.Penalty.Pair);
            }
        }
        temp = null; //GC
        return sum;
    }

    private int evalSecDiff() {
        int sum = 0;
        Map<Course,Slot> temp = new HashMap<>(); //to memoize with
        for (Course entry : Model.getInstance().getCourses()) {
            if (entry instanceof Lecture) {
                for (Lecture sibling : ((Lecture) entry).getSiblings()) {
                    Slot slot1 = temp.get(sibling);
                    if (slot1 == null) { //try to find it
                        for(Pair<Course,Slot> var : this.assignments){
                            if (var.fst() == sibling){
                                slot1 = var.snd();
                                temp.put(var.fst(), var.snd());
                                break;
                            }
                        }
                    }
                    Slot slot2 = temp.get(entry);
                    if (slot2 == null) { //try to find it
                        for(Pair<Course,Slot> var : this.assignments){
                            if (var.fst() == entry){
                                slot2 = var.snd();
                                temp.put(var.fst(), var.snd());
                                break;
                            }
                        }
                    }
                    if (slot1 != null && slot2 != null && slot1.equals(slot2)) {
                        sum += Model.getInstance().getPenalies(Model.Penalty.SectionDifference);
                    }
                }
            }
        }
        temp = null; //GC
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
        	boolean found = false;
        	for (Pair<Course,Slot> v : assignments) {
				if (c == v.fst()) {
					found = true;
					break;
				}
			}
        	if (found) continue;
        	else {
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

        for (Pair<Slot,Integer> var : this.counters) {
            if (s == var.fst()) {
                if (var.snd() > s.getMax()) return false;
                else return true;
            }
        }
        return true;
    }

    /**
    * Checks the hard constraints of having different time slots for a course and its corresponding labs
    */
    public boolean constrCourseConflict(Pair<Course, Slot> newAssignment) {

        Course c = newAssignment.fst();
        Slot s = newAssignment.snd();
        List<Course> mutex = newAssignment.fst().getMutex();

        if (c instanceof Lecture) {
            for (Course conflict : c.getMutex()) {
                Slot conflictTimeSlot = null; //find the slot currently assigned
                for (Pair<Course,Slot> var : this.assignments) {
                    if (var.fst() == conflict) {
                        conflictTimeSlot = var.snd();
                        break;
                    }
                }
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
                Slot conflictTimeSlot = null; //find the slot currently assigned
                for (Pair<Course,Slot> var : this.assignments) {
                    if (var.fst() == conflict) {
                        conflictTimeSlot = var.snd();
                        break;
                    }
                }
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
        Map<Course,Slot> temp = new HashMap<>();
        for (Pair<Course,Slot> var : this.assignments) {
            temp.put(var.fst(), var.snd());
        }

        for (Course c : Model.getInstance().getCourses()) {
            if(temp.get(c) == null) return false;
        }
        temp = null;//gc
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
        for (Pair<Course, Slot> assign : this.assignments) {
            if (assign.fst().toString().length() > maxPadding)
                maxPadding = assign.fst().toString().length();
        }

        assignments.sort(new Comparator<Pair<Course, Slot>>() {
            @Override
            public int compare(Pair<Course, Slot> o1, Pair<Course, Slot> o2) {
                return o1.fst().toString().compareTo(o2.fst().toString());
            }
        });

        String out = "Eval " + score + "\n";
        for (Pair<Course, Slot> pair : assignments) {
            String courseName = pair.fst().toString();
            int paddingToAdd = maxPadding - courseName.length();
            for (int i = 0; i < paddingToAdd; i++)
                courseName += " ";
            out += courseName + "\t" + pair.snd() + "\n";
        }

        return out;
    }

}