package scheduler;
import java.util.*;

/**
 * Lecture implements Course
 */
public class Lecture extends Course {
	private List<Lecture> siblings = new ArrayList<>();

    
    public Lecture(String id){
        name = id;
        //TODO: parse with regex
        courseNum = Integer.parseInt(id.substring(4, 7));
    	setDept(name.substring(0, 4));
    	setLecNum(Integer.parseInt(name.substring(10,12)));
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
    
    public boolean both500s(Lecture section) {
    	return (this.name.substring(0,5).equals("CPSC5") &&
		this.name.substring(0,5).equals(section.toString().substring(0,5)));

    }

}