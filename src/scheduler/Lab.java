package scheduler;
import java.util.ArrayList;
import java.util.List;

/**
 * Lab implements Course
 */
public class Lab extends Course {

    
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
}