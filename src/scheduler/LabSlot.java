package scheduler;
/**
 * LabSlot implements Slot
 */
public class LabSlot extends Slot {

    public LabSlot(String input){
        
        super(input);

        switch (day) {
            case Monday:
            case Tuesday:
                duration = 50;
                break;
            case Friday: 
                duration = 110;
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
			dayStr = "MW ";			
			break;
		case Tuesday:
			dayStr = "TR ";
			break;
		case Friday:
			dayStr = "F  ";
			break;
		default:
			dayStr = "N/A";
			break;
		}
    	// TODO Auto-generated method stub
    	return dayStr + ", " + time / 60 + ":" + (time % 60 == 0 ? "00" : "30");
    }

}