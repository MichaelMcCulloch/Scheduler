package scheduler;

import java.util.Scanner;

public class Reader implements Runnable{
	public boolean input = false;
	@Override
	public void run() {
		Scanner user = new Scanner(System.in);
		
		if (user.hasNextLine()) input = true;
		user.close();
	}
	
}
