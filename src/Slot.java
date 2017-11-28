/**
 * Slot
 */
public interface Slot {

    public boolean byDayTime(String day, String time);
    public String toString();
    public int getMax();
    public int getMin();
    public String getHour();
    public String getDay();
}