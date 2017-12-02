package scheduler;
/**
 * CourseSlot implements Slot
 */
public class CourseSlot extends Slot {
    

    public CourseSlot(String input) {
        super(input);

        switch (day) {
        case "MO":
            duration = 50;
            break;
        case "TU":
            duration = 75;
            break;
        default:
            break;
        }
    }



   
}