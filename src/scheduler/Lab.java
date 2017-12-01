package scheduler;
import java.util.ArrayList;
import java.util.List;

/**
 * Lab implements Course
 */
public class Lab implements Course {


    private List<Course> mutex = new ArrayList<>();

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
        	setLecNum(Integer.parseInt(name.substring(10,12)));
        }
        else {
        	lecNum = -1;
        }
    	setDept(name.substring(0, 4));
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