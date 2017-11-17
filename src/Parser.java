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
        while (fileScanner.hasNextLine()) {
        	lines.add(fileScanner.nextLine().replaceAll("\\s+", ""));
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
        while (!q.remove().equals("Name:"));
        return q.remove();
    }

    private List<CourseSlot> parseCourseSlots(Queue<String> q) {
        while (!q.remove().equals("Courseslots:"));
        List<CourseSlot> cSlots = new ArrayList<>();
        while (!q.peek().equals("")) {
            cSlots.add(new CourseSlot(q.remove()));
        }
        return cSlots;
    }

    private List<LabSlot> parseLabSlots(Queue<String> q) {
        while (!q.remove().equals("Labslots:"));
        List<LabSlot> lSlots = new ArrayList<>();
        while (!q.peek().equals("")) {
            lSlots.add(new LabSlot(q.remove()));
        }
        return lSlots;
    }
    
    private List<Lecture> parseCourses(Queue<String> q) {
        while (!q.remove().equals("Courses:"));
        List<Lecture> courses = new ArrayList<>();
        while (!q.peek().equals("")) {
            courses.add(new Lecture(q.remove()));
        }
        return courses;
    }

    private List<Lab> parseLabs(Queue<String> q) {
        while (!q.remove().equals("Labs:"));
        List<Lab> labs = new ArrayList<>();
        while (!q.peek().equals("")) {
            labs.add(new Lab(q.remove()));
        }
        return labs;
    }

    private List<Pair<Course,Course>> parseIncompatible(Queue<String> q){
        while (!q.remove().equals("Notcompatible:"));
        List<Pair<Course,Course>> nc = new ArrayList<>();
        while (!q.peek().equals("")) {
            String next = q.remove();
            String[] p = next.split(",");
            Course a = findByName(p[0]);
            Course b = findByName(p[1]);
            nc.add(new Pair<Course,Course>(a, b));
        }
        return nc;
    }

    private List<Pair<Course,Course>> parseTogether(Queue<String> q) {
        while (!q.remove().equals("Pair:"));
        List<Pair<Course,Course>> pairs = new ArrayList<>();
        while (!q.peek().equals("")) {
            String next = q.remove();
            String[] p = next.split(",");
            Course a = findByName(p[0]);
            Course b = findByName(p[1]);
            pairs.add(new Pair<Course,Course>(a, b));
        }
        return pairs;
    }

    private Map<Course,Slot> parseUnwanted(Queue<String> q) {
        while (!q.remove().equals("Unwanted:"));
        Map<Course,Slot> nope = new HashMap<>();
        while (!q.peek().equals("")) {
            String next = q.remove();
            String[] cdtTuple = next.split(",");
            Course c = findByName(cdtTuple[0]);
            if (c == null) continue;
            Slot s = findByDayTime(c.isLecture(), cdtTuple[1], cdtTuple[2]);
            if (s == null) continue;
            nope.put(c, s);
        }
        return nope;
    }

    private Map<Course,Slot> parsePartAssign(Queue<String> q) {
        while (!q.remove().equals("Partialassignments:"));
        Map<Course,Slot> yes = new HashMap<>();
        while (!q.isEmpty() && !q.peek().equals("")) {
            String next = q.remove();
            String[] cdtTuple = next.split(",");
            Course c = findByName(cdtTuple[0]);
            if (c == null) continue;
            Slot s = findByDayTime(c.isLecture(), cdtTuple[1], cdtTuple[2]);
            if (s == null) continue;
            yes.put(c, s);
        }
        return yes;
    }

    private List<Triple<Course, Slot, Integer>> parsePreferences(Queue<String> q) {
        while (!q.remove().equals("Preferences:"));
        List<Triple<Course, Slot, Integer>> prefs = new ArrayList<>();
        while (!q.peek().equals("")) {
            String next = q.remove();
            String[] dtcpTuple = next.split(",");
            Course c = findByName(dtcpTuple[2]);
            if (c == null) continue;
            Slot s = findByDayTime(c.isLecture(), dtcpTuple[0], dtcpTuple[1]);
            if (s == null) continue;
            Integer value = Integer.parseInt(dtcpTuple[3]);
            prefs.add(new Triple<Course,Slot,Integer>(c, s, value));
        }
        return prefs;
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

    private Slot findByDayTime(boolean isLecture, String day, String hour){
        if (isLecture) {
            for (CourseSlot var : courseSlots) {
                if (var.byDayTime(day, hour)) return var;
            }
        } else {
            for (LabSlot var : labSlots) {
                if (var.byDayTime(day, hour)) return var;
            }
        }
        System.out.println("Warning: Slot" + day + " " + hour  + " not found");
        return null;
    }

    public Schedule getInitialInstance(){
        return initialInstance;
    }
}