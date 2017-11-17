/**
 * Lab implements Course
 */
public class Lab implements Course {

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
}