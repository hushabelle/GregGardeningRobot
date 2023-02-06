import lejos.hardware.Battery;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

/*			LowBattery.java
*
* This behaviour detects when the robot is on a low charge level,
* displays on the LCD screen and shuts down.
* 
* @param n	a (shared) Navigator generated in Gregg.java
* 
*/

public class LowBattery implements Behavior {
	final float LOW_LEVEL = 3; // low-battery threshold
	private Navigator navigator;
	
	/* Constructor */
	LowBattery(Navigator n) {
		this.navigator = n;
	}
	
	public boolean takeControl() {
		return Battery.getVoltage()<LOW_LEVEL;
	}

	public void action() { // display message to LCD, exit program
		navigator.stop();
		
		LCD.clear();
		Sound.beepSequence();
		LCD.drawString("Battery", 4, 3);
	    LCD.drawString("Low", 5, 4);
	    Delay.msDelay(5000);
	    LCD.clear();
	    LCD.drawString("Goodbye...", 0, 2);
	    Delay.msDelay(1000);
	    System.exit(0);
	}

	public void suppress() {}
}