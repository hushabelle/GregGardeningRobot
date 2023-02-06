import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

/* 						MapEnvironment.java
 * 
 * This behaviour maps the boundaries of the square Gregg is       
 * placed in. It does this by iterating 3 times and averaging the     
 * measurements taken.                                                
 *                                                                    
 * @param	p		a (shared) MovePilot generated in Gregg.java          
 * @param	d 		the static float[] which contains the distance
 * 					of the robot from objects
 * 
 */

public class MapEnvironment implements Behavior {
	private boolean isBoundaryMapped = false;
	private MovePilot pilot;
	private EV3GyroSensor gyro;
	private SampleProvider sample;
	private float[] gyroSample = new float[1];
	private float[] distance;
	private float[] widthHeight = new float[2];

	/* Constructor */
	MapEnvironment(MovePilot p, EV3GyroSensor g, SampleProvider sp, float[] d) {
		this.pilot = p;
		this.gyro = g;
		this.sample = sp;
		this.distance = d;
	}
	
	/* Getters */
	public float[] getXY() {
		return widthHeight;
	}
	
	public boolean getIsBoundaryMapped() {
		return isBoundaryMapped;
	}
	
	public boolean takeControl() {
		return (!isBoundaryMapped);
	}
	
	private void mapBoundary() {				
		
		float[] avgMeasurements = new float[4]; // holds the X and Y axis measurements
		
		LCD.clear();
		LCD.drawString("Press to begin mapping...", 0, 2);
		Button.waitForAnyPress();
		
		for (int i = 0; i < 4; i++ ) { // 2 measurements per x,y axis
			gyro.reset();
			Delay.msDelay(1500);
			avgMeasurements[i] = distance[0];
			pilot.rotate(90, false);
			
			LCD.clear();
			sample.fetchSample(gyroSample, 0);
			gyroSample[0] = -gyroSample[0];
			LCD.drawString("" + gyroSample[0], 0, 2);
			Delay.msDelay(1000);
			// turn right
			if (gyroSample[0] < 90) {
				Delay.msDelay(1000);
				float newAngle = 90 - gyroSample[0];
				if (newAngle < 5) {
					newAngle += 5;
				}				
				LCD.clear();
				LCD.drawString("" + newAngle, 0, 2);
				Delay.msDelay(1000);
				pilot.rotate(newAngle, false);
			}
			// turn left
			if (gyroSample[0] > 90) {
				Delay.msDelay(1000);
				float newAngle = gyroSample[0] - 90;
				if (newAngle < 5) {
					newAngle += 5;
				}				
				LCD.clear();
				LCD.drawString("" + newAngle, 0, 2);
				Delay.msDelay(1000);
				pilot.rotate(-(gyroSample[0] - 90));
			}
		}
		float totalX = 0;
		float totalY = 0;
		
		for (int i = 0; i < 4; i++) {
			if (i % 2 == 0) {
				// even indices (incl. 0) are X values
				totalX = totalX + avgMeasurements[i];
			} else {
				// odd indices are Y values
				totalY = totalY + avgMeasurements[i];
			}
			LCD.clear();
			LCD.drawString("width: " + totalX, 0, 2);
			LCD.drawString("height: " + totalY, 0, 3);
		}
		float avgX = (totalX) + 0.11f; // 0.11f is the offset for Greggs body
		float avgY = (totalY) + 0.11f;
		
		// Display w,h of bounding rectangle
		LCD.clear();
		LCD.drawString("width: " + avgX, 0, 4);
		LCD.drawString("height: " + avgY, 0, 5);
		Button.waitForAnyPress();
		
		widthHeight[0] = avgX;
		widthHeight[1] = avgY;
		isBoundaryMapped = true;
	}
	
	public void action() {
		mapBoundary();
	}
	
	public void suppress() {
		pilot.stop();
		return;
	}
}
