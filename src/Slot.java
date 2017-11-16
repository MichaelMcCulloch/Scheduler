/**
 * Slot
 */
public class Slot {


	private String day, time;

	public Slot(String data){

	}

	public boolean equals(String day, String time){
		return this.day.equals(day) && this.time.equals(time);
	}
}