package scheduler;
/**
 * Slot
 */
abstract public class Slot {


    protected String day;
    protected int time; //TODO: Make Double
    protected int max, min;
    protected int duration;

    public Slot(String input){
        String[] dayTimeMaxMin = input.split(",");
        day = dayTimeMaxMin[0];
        String[] hm = dayTimeMaxMin[1].split(":");
        time = Integer.parseInt(hm[0]) * 60;
        time += Integer.parseInt(hm[1]);
        max = Integer.parseInt(dayTimeMaxMin[2]);
        min = Integer.parseInt(dayTimeMaxMin[3]);
    }

    public boolean byDayTime(String day, String time) {
        String[] hm = time.split(":");
        int compareTime = Integer.parseInt(hm[0]) * 60;
        compareTime += Integer.parseInt(hm[1]);
        return byDayTime(day, compareTime);
    }

    public boolean byDayTime(String day, int time) {
        return this.day.equals(day) && this.time == time;
    }
    public String toString() {
        // TODO Auto-generated method stub
        return day + ", " + time / 60 + ":" + (time % 60 == 0 ? "00" : "30");
    }

    public int getMax() {
        return this.max;
    }

    public int getMin() {
        return this.min;
    }

    public int getTime() {
        return this.time;
    }

    public String getDay() {
        return this.day;
    }

    public int getDuration() {
        return duration;
    }
}