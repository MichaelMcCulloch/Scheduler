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



   
}