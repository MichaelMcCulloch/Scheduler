import java.util.ArrayList
import java.util.List;

/**
 * Lab implements Course
 */
public class Lab implements Course {

    private List<Course> mutex = new ArrayList<>();
    private String name;
    public Lab(String id){
        name = id;
    }
    @Override
    public boolean byName(String name) {
        return this.name.equals(name);
    }
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return name;
    }
    @Override
    public List<Course> getMutex() {
        return this.mutex;
    }
    @Override
    public void addMutex(Course c) {
        mutex.add(c);
    }

}