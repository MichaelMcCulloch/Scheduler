package scheduler;
import java.util.ArrayList;
import java.util.List;

/**
 * Lab implements Course
 */
public class Lab implements Course {


    private List<Course> mutex = new ArrayList<>();
    private List<Pair<Slot,Integer>> preference = new ArrayList<>();

    private String name;
    private int courseNum;
    private int lecNum;
    private String dept;
    
    public Lab(String id){
        name = id;
    	//TODO: parse with regex.
        //Assume course id is 4 characters, course num is 3, no spaces
        courseNum = Integer.parseInt(id.substring(4, 7));
        if (name.length()>14) {
        	setDept(name.substring(0, 3));
        	setLecNum(Integer.parseInt(name.substring(10,11)));
        }
        else {
        	dept = null;
        	lecNum = -1;
        }
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
    
    @Override
    public int getSectNum() {
        return courseNum;
    }

	public void addPreference(Pair<Slot,Integer> pref) {
		int i=0;
		while (i<this.preference.size() && pref.snd()>this.preference.get(i).snd()) i++;
		this.preference.add(i,pref);
	}
	
	public List<Pair<Slot,Integer>> getPreference(){
		return this.preference;
	}

	public String getDept() {
		return dept;
	}
	public void setDept(String dept) {
		this.dept = dept;
	}
	public int getLecNum() {
		return lecNum;
	}
	public void setLecNum(int lecNum) {
		this.lecNum = lecNum;
	}    
	public int getCourseNum() {
		return courseNum;
	}
}
