package scheduler;
/**
 * CourseSlot implements Slot
 */
public class CourseSlot implements Slot {
    private String day, hour;
    private int courseMax, courseMin;

    
    /**
     * CourseSlot Constructor
     * @param input  An input string for the creation of a course slot. The input string should have the format:
     * DD,HH:MM,CourseMax,CourseMin
     * 
     * Examples:
     * MO,8:00,2,1
     * TU,15:30,5,0
     * 
     */
    public CourseSlot(String input){

        String[] dayTimeMaxMin = input.split(",");
        day = dayTimeMaxMin[0];
        hour = dayTimeMaxMin[1];
        courseMax = Integer.parseInt(dayTimeMaxMin[2]);
        courseMin = Integer.parseInt(dayTimeMaxMin[3]);
    }

    /**
     * Compares if the course occurs a the time DD:HH
     * @param day Day (either MO or TU)
     * @param time Hour  
     */
    @Override
    public boolean byDayTime(String day, String time) {
        return (this.day.equals(day )&& this.hour.equals(time));
    }
    
    
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return "LEC@" + day + ":" + hour;
    }

    @Override
    public int getMax() { return this.courseMax; }
    
    @Override
    public int getMin() { return this.courseMin; }
}