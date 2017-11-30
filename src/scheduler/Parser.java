package scheduler;
import java.util.*;
import java.util.Map.Entry;
import java.io.*;

/**
 * Parser
 */
public class Parser {

    private String name;
    private List<CourseSlot> courseSlots;
    private List<LabSlot> labSlots;
    private List<Lecture> courseList;
    private List<Lab> labList;
    private Map<Course, Slot> unwanted, partAssign;
    private List<Triple<Course, Slot, Integer>> preferences;
    private List<Pair<Course, Course>> together, incompatible;

    private Schedule initialInstance;

    public Parser(File f) throws FileNotFoundException {
    	Scanner fileScanner = new Scanner(f);
        List<String> lines = new ArrayList<>();
        // Accumulate and remove spaces, set to uppercase
        while (fileScanner.hasNextLine()) {
        	lines.add(fileScanner.nextLine().replaceAll("\\s+", "").toUpperCase());
        }
        fileScanner.close();
        Queue<String> pending = new LinkedList<String>(lines);
        this.name = parseName(pending);
        this.courseSlots = parseCourseSlots(pending);
        this.labSlots = parseLabSlots(pending);
        this.courseList = parseCourses(pending);
        this.labList = parseLabs(pending);
        this.incompatible = parseIncompatible(pending);
        this.unwanted = parseUnwanted(pending);
        this.preferences = parsePreferences(pending);
        this.together = parseTogether(pending);
        this.partAssign = parsePartAssign(pending);

        
        specialCases();
        
        List<Course> allCourses = new ArrayList<>();
        allCourses.addAll(courseList);
        allCourses.addAll(labList);


        //Pre-sort the course list by the number of times it occurs in the constraints
        List<Pair<Course, Integer>> counts = new ArrayList<>();
        for (Course c : allCourses) {
            int compatCount = countCompat(c);
            int unwantedCount = countUnwanted(c);
            int prefCount = countPreferences(c);
            int togetherCount = countTogether(c);
            int sum = compatCount + unwantedCount + prefCount + togetherCount;
            counts.add(new Pair<Course,Integer>(c, sum));
        }
        
        counts.sort(new Comparator<Pair<Course,Integer>>() {
            @Override
            public int compare(Pair<Course, Integer> o1, Pair<Course, Integer> o2) {
                return Integer.compare(o1.snd(), o2.snd());
            }
        });

        List<Course> newList = new ArrayList<>();
        for (Pair<Course, Integer> var : counts) {
            newList.add(var.fst());
        }

        Model m = Model.getInstance();
        m.setData(newList, labSlots, courseSlots, unwanted, preferences, together, incompatible);
        
        try {
            initialInstance = new Schedule(null, partAssign);
        } catch (Exception e) {
            System.exit(1);
        }
        
    }
    private int countCompat(Course c){
        int count=0;
        for (Pair<Course,Course> var : incompatible) {
            if (var.fst() == c) count += 1;
            if (var.snd() == c) count += 1;
        }
        return count;
    }
    private int countUnwanted(Course c){
        int count = 0;
        for (Entry<Course,Slot> var : unwanted.entrySet()) {
            if (var.getKey() == c) count += 1;
        }
        return count;
    }
    private int countPreferences(Course c){
        int count = 0;
        for (Triple<Course, Slot, Integer> var : preferences) {
            if (var.fst() == c) count += 1;
        }
        return count;
    }
    private int countTogether(Course c){
        int count=0;
        for (Pair<Course,Course> var : together) {
            if (var.fst() == c) count += 1;
            if (var.snd() == c) count += 1;
        }
        return count;
    }

    private String parseName(Queue<String> q) {
        while (!q.remove().equals("NAME:"));
        return q.remove();
    }
    
    private List<CourseSlot> parseCourseSlots(Queue<String> q) {
        while (!q.remove().equals("COURSESLOTS:"));
        List<CourseSlot> cSlots = new ArrayList<>();
        while (!q.peek().equals("")) {
            cSlots.add(new CourseSlot(q.remove()));
        }
        return cSlots;
    }

    private List<LabSlot> parseLabSlots(Queue<String> q) {
        while (!q.remove().equals("LABSLOTS:"));
        List<LabSlot> lSlots = new ArrayList<>();
        while (!q.peek().equals("")) {
            lSlots.add(new LabSlot(q.remove()));
        }
        return lSlots;
    }
    
    private List<Lecture> parseCourses(Queue<String> q) {
        while (!q.remove().equals("COURSES:"));
        List<Lecture> courses = new ArrayList<>();
        while (!q.peek().equals("")) {
        	Lecture next = new Lecture(q.remove());
        	for (Lecture alreadyParsed : courses) {		//for all courses already parsed
        		if (alreadyParsed.isSibling(next)) {	//if siblings
        			alreadyParsed.addSibling(next);		//add reciprocal references to sibling list
        			next.addSibling(alreadyParsed);
        		}
        		if (alreadyParsed.both500s(next)) {
        			alreadyParsed.addMutex(next);
        			next.addMutex(alreadyParsed);
        		}
        	}
            courses.add(next);
        }
        return courses;
    }

    private List<Lab> parseLabs(Queue<String> q) {
        while (!q.remove().equals("LABS:"));
        List<Lab> labs = new ArrayList<>();
        while (!q.peek().equals("")) {
            labs.add(new Lab(q.remove()));
        }
        return labs;
    }

    private List<Pair<Course,Course>> parseIncompatible(Queue<String> q){
        while (!q.remove().equals("NOTCOMPATIBLE:"));
        List<Pair<Course,Course>> nc = new ArrayList<>();
        while (!q.peek().equals("")) {
            String next = q.remove();
            String[] p = next.split(",");
            Course a = findByName(p[0]);
            Course b = findByName(p[1]);
            if (a == null || b == null) continue;
            //nc.add(new Pair<Course,Course>(a, b));
            a.addMutex(b);
            b.addMutex(a);
        }
        return nc;
    }

    private List<Pair<Course,Course>> parseTogether(Queue<String> q) {
        while (!q.remove().equals("PAIR:"));
        List<Pair<Course,Course>> pairs = new ArrayList<>();
        while (!q.peek().equals("")) {
            String next = q.remove();
            String[] p = next.split(",");
            Course a = findByName(p[0]);
            Course b = findByName(p[1]);
            if (a == null || b == null) continue;
            pairs.add(new Pair<Course,Course>(a, b));
        }
        return pairs;
    }

    private Map<Course,Slot> parseUnwanted(Queue<String> q) {
        while (!q.remove().equals("UNWANTED:"));
        Map<Course,Slot> nope = new HashMap<>();
        while (!q.peek().equals("")) {
            String next = q.remove();
            String[] cdtTuple = next.split(",");
            Pair<Course,Slot> cs = getCourseSlotPair(cdtTuple[0],  cdtTuple[1], cdtTuple[2]);
            if (cs == null) continue;
            nope.put(cs.fst(), cs.snd());
        }
        return nope;
    }

    private Map<Course,Slot> parsePartAssign(Queue<String> q) {
        while (!q.remove().equals("PARTIALASSIGNMENTS:"));
        Map<Course,Slot> yes = new HashMap<>();
        while (!q.isEmpty() && !q.peek().equals("")) {
            String next = q.remove();
            String[] cdtTuple = next.split(",");
            Pair<Course,Slot> cs = getCourseSlotPair(cdtTuple[0],  cdtTuple[1], cdtTuple[2]);
            if (cs == null) { //Bad assignment
                System.exit(1);   
            }
            yes.put(cs.fst(), cs.snd());
        }
        return yes;
    }

    /**
     * Tuple is of form Course, Slot, Importance
     */
    private List<Triple<Course, Slot, Integer>> parsePreferences(Queue<String> q) {
        while (!q.remove().equals("PREFERENCES:"));
        List<Triple<Course, Slot, Integer>> prefs = new ArrayList<>();
        while (!q.peek().equals("")) {
            String next = q.remove();
            String[] dtcpTuple = next.split(",");
            Pair<Course,Slot> cs = getCourseSlotPair(dtcpTuple[2],  dtcpTuple[0], dtcpTuple[1]);
            if (cs == null) continue;
            Integer value = Integer.parseInt(dtcpTuple[3]);
            prefs.add(new Triple<Course,Slot,Integer>(cs.fst(), cs.snd(), value));
        }
        return prefs;
    }

    /*
     * If 313 or 413 exists, adds 813 and 913 to partAssign with the required time slot
     */
    private void specialCases() {
        Boolean found313 = false,
                found413 = false;
        Lab q813 = new Lab("CPSC813"), //quiz 813
            q913 = new Lab("CPSC913"); //quiz 913
        Slot s = findByDayTime(false, "TU", "18:00");
        for (Lecture var : courseList) {
            String id = var.toString().split("LEC")[0];
            if (id.equals("CPSC313")) {
                if (!found313){ //Add 813
                    found313 = true;
                    labList.add(q813);
                    if (s != null) {
                        partAssign.put(q813, s);
                    } else {
                        System.out.println("Error: No TU 18:00 slot exists for " + var.toString());
                        System.exit(0); //does this actually exit?
                    }                   
                }
                for (Course c : var.getMutex()) { //Add all var mutex to q913
                    q813.addMutex(c);
                    c.addMutex(q813);
                }
                var.addMutex(q813);
                q813.addMutex(var);
            } else if (id.equals("CPSC413")) {
                if (!found413){ //Add 913
                    found413 = true;
                    labList.add(q913);
                    if (s != null) {
                        partAssign.put(q913, s);
                    } else {
                        System.out.println("Error: No TU 18:00 slot exists for " + var.toString());
                        System.exit(0); //Does this actually exit?
                    }                   
                }
                for (Course c : var.getMutex()) { //Add all var mutex to q913
                    q913.addMutex(c);
                    c.addMutex(q913);
                }
                var.addMutex(q913);
                q913.addMutex(var);
            }           
        }
    }
    
    /**
     * Get a pair of course & slot. 
     */
    private Pair<Course,Slot> getCourseSlotPair(String courseID, String slotDay, String slotHour){
        Course c = findByName(courseID);
        if (c == null) return null;
        Slot s = findByDayTime(c instanceof Lecture, slotDay, slotHour);
        if (s == null) return null;
        return new Pair<Course,Slot>(c,s);
    }
    
    private Course findByName(String identifier){
        List<Course> allCourses = new ArrayList<>();
        allCourses.addAll(labList);
        allCourses.addAll(courseList);
        for (Course var : allCourses) {
            if (var.byName(identifier)) return var;
        }
        System.out.println("Warning: " + identifier + " not found in ");
        return null;
    }

    /**
     * Returns a lab slot if the course is a lab, or a lecture slot otherwise
     */
    private Slot findByDayTime(boolean isLecture, String day, String hour){
        List<Slot> searchThrough = new ArrayList<>((isLecture) ? courseSlots : labSlots);
        for (Slot var : searchThrough) {
            if (var.byDayTime(day, hour)) return var;
        }
        System.out.println("Warning: " + ((isLecture) ? "Course" : "Lab") + " Slot " + day + " " + hour  + " not found");
        return null;
    }

    public Schedule getInitialInstance(){
        return initialInstance;
    }
}

