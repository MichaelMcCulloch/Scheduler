/**
 * CourseSlot implements Slot
 */
public class CourseSlot implements Slot {
    private String day, hour;
    private int courseMax, courseMin;

    public CourseSlot(String input){

        String[] dayTimeMaxMin = input.split(",");
        day = dayTimeMaxMin[0];
        hour = dayTimeMaxMin[1];
        courseMax = Integer.parseInt(dayTimeMaxMin[2]);
        courseMin = Integer.parseInt(dayTimeMaxMin[3]);
    }

    @Override
    public boolean byDayTime(String day, String time) {
        return (this.day.equals(day )&& this.hour.equals(time));
    }
    
    @Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return "LEC@" + day + ":" + hour;
    }

}