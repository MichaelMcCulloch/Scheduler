package scheduler;
/**
 * LabSlot implements Slot
 */
public class LabSlot extends Slot {

    public LabSlot(String input){
        
        super(input);

        switch (day) {
            case "MO":
            case "TU":
                duration = 50;
                break;
            case "FR": 
                duration = 110;
                break;
            default:
                break;
            }
    }
    

}