package scheduler;
/**
 * LabSlot implements Slot
 */
public class LabSlot implements Slot {

    private String day;
    private int  time;
    private int labMax, labMin;
    private int duration;

    public LabSlot(String input){
        String[] dayTimeMaxMin = input.split(",");
        day = dayTimeMaxMin[0];
        String[] hm = dayTimeMaxMin[1].split(":");
        time = Integer.parseInt(hm[0]) * 60;
        time += Integer.parseInt(hm[1]);
        labMax = Integer.parseInt(dayTimeMaxMin[2]);
        labMin = Integer.parseInt(dayTimeMaxMin[3]);

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
    
    @Override
    public boolean byDayTime(String day, String time) {
        String[] hm = time.split(":");
        int compareTime = Integer.parseInt(hm[0]) * 60;
        compareTime += Integer.parseInt(hm[1]);
        return byDayTime(day, compareTime);
    }

    @Override
    public boolean byDayTime(String day, int time) {
        return this.day.equals(day) && this.time == time;
    }
    
    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return day + ", " + time / 60 + ":" + (time % 60 == 0 ? "00" : "30");
    }

    @Override
    public int getMax() { return this.labMax; }
    
    @Override
    public int getMin() { return this.labMin; }

    @Override
    public int getTime() { return this.time; }

    @Override
    public String getDay() { return this.day; }

    @Override
    public int getDuration() {
        return duration;
    }
}