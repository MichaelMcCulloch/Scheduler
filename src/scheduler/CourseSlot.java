package scheduler;
/**
 * CourseSlot implements Slot
 */
public class CourseSlot extends Slot {
    

    public CourseSlot(String input) {
        super(input);

        switch (day) {
        case Monday:
            duration = 50;
            break;
        case Tuesday:
            duration = 75;
            break;
        default:
            break;
        }
    }

    @Override
    public String toString() {
    	
    	String dayStr;
    	switch (this.day) {
		case Monday:
			dayStr = "MWF";			
			break;
		case Tuesday:
			dayStr = "TR ";
			break;
		default:
			dayStr = "N/A";
			break;
		}
    	// TODO Auto-generated method stub
    	return dayStr + ", " + time / 60 + ":" + (time % 60 == 0 ? "00" : "30");
    
    }

   
}