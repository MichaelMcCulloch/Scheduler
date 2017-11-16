import java.util.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * Parser
 */
public class Parser {

    private String name;
    private List<String> courses, labsTutorials;
    private List<Slot> courseSlots, labTutSlots;
    private List<Pair<String, String>> incompatible, pair;
    private List<Pair<String, Slot>> unwanted, preferred, partAssignCourse, partAssignLT;
    

    private Schedule initialInstance;




    public Parser(File f) throws IOException{
        Scanner input = new Scanner(new FileReader(f));
        Queue<String> lines = new LinkedList<String>(Files.readAllLines(f.toPath(),Charset.defaultCharset())); //java weird about files
        for (String line : lines) {
            line = line.replaceAll(" ", "");
            line = line.replaceAll("\t", "");
        }

        input.close();
    }

    private String parseName(Queue<String> q) {
        while (!q.remove().equals("Name:\n"));
        return q.remove();
    }

    private List<Slot> courseSlots(Queue<String> q) {
        while (!q.remove().equals("Course slots:\n"));
        List<Slot> slots = new ArrayList<>();
        while (!q.peek().equals("\n")) {
            slots.add(new Slot(q.remove()));
        }

        return slots;
    }

    private List<Slot> labSlots(Queue<String> q) {
        while (!q.remove().equals("Lab slots:\n"));
        List<Slot> slots = new ArrayList<>();
        while (!q.peek().equals("\n")) {
            slots.add(new Slot(q.remove()));
        }

        return slots;
    }

    private List<String> courses(Queue<String> q) {
        while (!q.remove().equals("Courses:\n"));
        List<String> courses = new ArrayList<>();
        while (!q.peek().equals("\n")) {
            courses.add(q.remove());
        }

        
        return courses;
    }

    private List<String> labs(Queue<String> q) {
        while (!q.remove().equals("Labs:\n"));
        List<String> labs = new ArrayList<>();
        while (!q.peek().equals("\n")) {
            labs.add(q.remove());
        }

        return labs;
    }

    private List<Pair<String,String>> notCompatible(Queue<String> q) {
        while (!q.remove().equals("Not compatible:\n"));
        List<Pair<String,String>> nc = new ArrayList<>();
        while (!q.peek().equals("\n")) {
            String next = q.remove();
            String[] pair = next.split(",");
            nc.add(new Pair<String,String>(pair[0], pair[1]));
        }
        return nc;
    }

    private List<Pair<String,String>> pair(Queue<String> q) {
        while (!q.remove().equals("Pair:\n"));
        List<Pair<String,String>> nc = new ArrayList<>();
        while (!q.peek().equals("\n")) {
            String next = q.remove();
            String[] pair = next.split(",");
            nc.add(new Pair<String,String>(pair[0], pair[1]));
        }
        return nc;
    }

    private List<Pair<String,Slot>> unwanted(Queue<String> q) {
        while (!q.remove().equals("Unwanted:\n"));
        List<Pair<String,Slot>> nc = new ArrayList<>();
        while (!q.peek().equals("\n")) {
            String next = q.remove();
            String[] cdtTuple = next.split(",");

            Pair<Boolean, Integer> pair1 = findCourse(cdtTuple[0]);
            Pair<Boolean, Integer> pair2 = findSlot(cdtTuple[1], cdtTuple[2]);

        }
        return nc;
        
    }

    private List<Pair<String,Slot>> preferences(Queue<String> q) {
        while (!q.remove().equals("Preferences:\n"));
        
    }

    private List<Pair<String,Slot>> partAssign(Queue<String> q) {
        while (!q.remove().equals("Partial Assignment:\n"));
        
    }
    private Pair<Boolean,Integer> findCourse(String course){
        boolean inCourseList = true;
        int index = -1;
        for (int i = 0; i < courses.size(); i++ ){
            if (courses.get(i).equals(course)){
                return new Pair<Boolean,Integer>(true, i);
            }
        } for (int i = 0; i < labsTutorials.size(); i++ ){
            if (labsTutorials.get(i).equals(course)){
                return new Pair<Boolean,Integer>(false, i);
            }
        }
    }
    private Pair<Boolean, Integer> findSlot(String day, String time){
        boolean inCourseSlotList = true;
        int index = -1;

        for (int i = 0; i < courseSlots.size(); i++ ){
            if (courseSlots.get(i).equals(day, time)){
                return new Pair<Boolean,Integer>(true, i);
            }
        } for (int i = 0; i < labTutSlots.size(); i++ ){
            if (labTutSlots.get(i).equals(day, time)){
                return new Pair<Boolean,Integer>(false, i);
            }
        }
        return null;
    }
}