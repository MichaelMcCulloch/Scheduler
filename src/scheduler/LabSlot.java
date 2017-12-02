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
    

}