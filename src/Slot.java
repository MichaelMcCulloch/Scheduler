/**
 * Slot
 */
public interface Slot {

    public boolean byDayTime(String day, String time);
    
    public int getMax();
    public int getMin();
}