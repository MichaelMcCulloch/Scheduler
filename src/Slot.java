/**
 * Slot
 */
public class Slot {
	private String type;
	private String day;
	private String time;
	private int max;
	private int min;
	
	public Slot(String line, String type) {
		line = line.replaceAll(" +", "");		//Remove erroneous whitespace
		String[] parts = line.split(",");
		this.type = type;
		this.day = parts[0];
		this.time = parts[1];
		this.max = Integer.parseInt(parts[2]);
		this.min = Integer.parseInt(parts[3]);
	}
	
	public String toString() {
		return day + ", " + time + ", " + Integer.toString(max) + ", " + Integer.toString(min);
	}
	
    //TODO Getters
    public boolean equals(Slot other){
        return this.type.equals(other.type) && 
                this.day.equals(other.day) &&
                this.time.equals(other.time) &&
                this.max == other.max &&
                this.min == other.min;
    }
}