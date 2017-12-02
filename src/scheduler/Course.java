package scheduler;
import java.util.*;


abstract public class Course {

    private List<Course> mutex = new ArrayList<>();
    
    protected String name;
    protected int courseNum;
    protected int lecNum;
    protected String dept;

    public boolean byName(String name) {
        return this.name.equals(name);
    }

    public String toString() {
    	// TODO Auto-generated method stub
    	return name;
    }

    public List<Course> getMutex() {
        return this.mutex;
    }
    
    public void addMutex(Course c) {
        mutex.add(c);
    }

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