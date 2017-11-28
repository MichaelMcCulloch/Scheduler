/**
 * Slot
 */
public interface Slot {

    public boolean byDayTime(String day, String time);
    public boolean byDayTime(String day, int time);
    public String toString();
    public int getMax();
    public int getMin();
    public int getTime();
    public String getDay();
    public int getDuration();
}