/**
 * CourseSlot implements Slot
 */
public class CourseSlot implements Slot {
    private String day;
    private int time; //TODO: Make Double
    private int courseMax, courseMin;
    private int duration;

    public CourseSlot(String input) {

        String[] dayTimeMaxMin = input.split(",");
        day = dayTimeMaxMin[0];
        String[] hm = dayTimeMaxMin[1].split(":");
        time = Integer.parseInt(hm[0]) * 60;
        time += Integer.parseInt(hm[1]);
        courseMax = Integer.parseInt(dayTimeMaxMin[2]);
        courseMin = Integer.parseInt(dayTimeMaxMin[3]);

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
        return "LEC@" + day + ":" + time / 60 + ":" + time % 60;
    }

    @Override
    public int getMax() {
        return this.courseMax;
    }

    @Override
    public int getMin() {
        return this.courseMin;
    }

    @Override
    public int getTime() {
        return this.time;
    }

    @Override
    public String getDay() {
        return this.day;
    }

    @Override
    public int getDuration() {
        return duration;
    }
}