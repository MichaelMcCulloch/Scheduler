import java.util.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;

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
        	lines.add(fileScanner.nextLine().replaceAll("\\s+", "").toUpperCase(u);
        }

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

        initialInstance = new Schedule(null, partAssign);
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
            courses.add(new Lecture(q.remove()));
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
            nc.add(new Pair<Course,Course>(a, b));
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
            if (cs == null) continue;
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
        System.out.println("Warning: " + ((isLecture) ? "Course" : "Lab") + "Slot" + day + " " + hour  + " not found");
        return null;
    }

    public Schedule getInitialInstance(){
        return initialInstance;
    }
}