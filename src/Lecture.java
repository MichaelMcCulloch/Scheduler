import java.util.ArrayList;
import java.util.List;

/**
 * Lecture implements Course
 */
public class Lecture implements Course {
	private List<Lecture> siblings = new ArrayList<>();
	private String name;
    public Lecture(String id){
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
    
    public List<Lecture> getSiblings() {
    	return this.siblings;
    }
    
    public void addSibling(Lecture section) {
    	siblings.add(section);
    }
    
    public boolean isSibling(Lecture section) {
    	return (this.name.split("LEC")[0].equals(section.toString().split("LEC")[0]) ? true : false); //check if names are the same up to "LEC"
    }
}