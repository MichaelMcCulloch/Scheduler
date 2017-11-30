package scheduler;
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

    public int getSectNum();
    
    public void addPreference(Pair<Slot,Integer> pref);
    
    public List<Pair<Slot,Integer>> getPreference();
}