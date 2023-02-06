import lejos.hardware.Button;
import lejos.hardware.Sound;

/*					EmergencyExit.java
 * 
 * Ends the program at any poiint when the grey interrupt button is pressed.
 */

public class EmergencyExit extends Thread {
	
	public void run() {
		while (true) {
			if (Button.ESCAPE.isDown()) {
				Sound.beepSequence();
				System.exit(0);
			}
		}
	}
}
