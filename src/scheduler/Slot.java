package scheduler;
/**
 * Slot
 */
abstract public class Slot {

    public enum Day {
        Monday,
        Tuesday,
        Friday
    }

    protected Day day;
    protected int time; //TODO: Make Double
    protected int max, min;
    protected int duration;

    public Slot(String input){
        String[] dayTimeMaxMin = input.split(",");
        String dayStr = dayTimeMaxMin[0];
        this.day = strToDay(dayStr);
        String[] hm = dayTimeMaxMin[1].split(":");
        time = Integer.parseInt(hm[0]) * 60;
        time += Integer.parseInt(hm[1]);
        max = Integer.parseInt(dayTimeMaxMin[2]);
        min = Integer.parseInt(dayTimeMaxMin[3]);
    }

    public Day strToDay(String day) {
        switch (day) {
            case "MO" : return Day.Monday;
            case "TU" : return Day.Tuesday;
            case "FR" : return Day.Friday;
            default : return null;
        }
    }

    //For use by Parser only
    public boolean byDayTime(String dayStr, String time) {
        String[] hm = time.split(":");
        int compareTime = Integer.parseInt(hm[0]) * 60;
        compareTime += Integer.parseInt(hm[1]);
        return byDayTime(strToDay(dayStr), compareTime);
    }

    public boolean byDayTime(Day day, int time) {
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

    public Day getDay() {
        return this.day;
    }

    public int getDuration() {
        return duration;
    }
}