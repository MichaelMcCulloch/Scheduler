import java.util.ArrayList;
import java.util.List;

/**
 * Course
 */
public interface Course {
    
    public boolean byName(String name);
    
    public String toString();
    
    public List<Course> getMutex();
    
    public void addMutex(Course c);
}