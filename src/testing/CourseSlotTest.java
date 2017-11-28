package testing;

import java.util.ArrayList;

import org.junit.Test;
import junit.framework.*;


import scheduler.CourseSlot;


public class CourseSlotTest extends TestCase{
	
	//Every input from deptinst1 with spaces removed
	String[] testStrings = {"MO,8:00,2,1","MO,9:00,5,0","MO,10:00,5,0","MO,11:00,5,0","MO,12:00,5,0","MO,13:00,5,0","MO,14:00,5,0","MO,15:00,5,0","MO,16:00,5,0","MO,17:00,5,0","MO,18:00,1,0","MO,19:00,1,0","MO,20:00,1,0","TU,8:00,2,1","TU,9:30,5,0","TU,11:00,5,0","TU,12:30,5,0","TU,14:00,5,0","TU,15:30,5,0","TU,17:00,5,0","TU,18:30,1,0"};
	
	
	/**
	 * Test the creation of a course slot using each string from deptinst1's course slot list
	 * Spaces have been removed manually
	 */
	@Test
	public void testCreation() {
		for (int i=0; i<testStrings.length; i++) {
			try {
				CourseSlot A = new CourseSlot(testStrings[i]);
			}
			catch(Exception e) {
				fail("Could not parse string: "+testStrings[i]);
			}
		}	
	}
	
	
	/**
	 * Test every possible Day/Time combo
	 */
	@Test
	public void testByDayTime() {
		String[] day= {"MO","TU"};

		for (int i =0; i<day.length; i++) {
			for(int j=0; j<24; j++) {
				for(int k=0; k<60; k++) {
					
					String cslot=day[i]+","+String.format("%d:%02d",j,k)+",0,0";
					CourseSlot A = new CourseSlot(cslot);
					
					for(int l=0; l<day.length; l++) {
						for(int m=0; m<24; m++) {
							for(int n=0; n<60; n++) {
								
								String d =day[l];
								String t=String.format("%d:%02d",m,n);
								if (i==l && j==m && k==n) assertTrue(cslot+" should be at: "+d+":"+t+"!",A.byDayTime(d,t));
								else assertFalse(cslot+" should not be at: "+d+":"+t+"!",A.byDayTime(d,t));
							
							}
						}
					}
				}
			}
		}
	}
}
