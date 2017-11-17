/**
 * LabSlot implements Slot
 */
public class LabSlot implements Slot {

    private String day, hour;
    private int labMax, labMin;

    public LabSlot(String input){
        String[] dayTimeMaxMin = input.split(",");
        day = dayTimeMaxMin[0];
        hour = dayTimeMaxMin[1];
        labMax = Integer.parseInt(dayTimeMaxMin[2]);
        labMin = Integer.parseInt(dayTimeMaxMin[3]);
    }
    
    @Override
    public boolean byDayTime(String day, String time) {
        return (this.day.equals(day )&& this.hour.equals(time));
    }

    @Override
    public boolean isLectSlot() {
        return false;
    }
}