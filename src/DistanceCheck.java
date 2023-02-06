import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

/*
 * * * * * * * * * DistanceCheck * * * * * * * * * * * *
 * - Does not return a value                           *
 * - continuously samples distance from objects        *
 * - distance field cannot be private, used by others  *
 *                                                     *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */

public class DistanceCheck extends Thread {
	private MovePilot pilot; // shared MovePilot
	private EV3UltrasonicSensor ultSense = new EV3UltrasonicSensor(SensorPort.S3);
	private SampleProvider sonicSample = ultSense.getDistanceMode(); 
	protected static float[] distance = new float[1];
	
	/* Constructor */
	DistanceCheck(MovePilot p) {
		this.pilot = p;
	}
	
	/* Getter */
	public static float getDistance() {
		return distance[0];
	}
	
	/* Required to extend Thread - essentially a second main to run things from separately */
	@Override
	public void run() {
		while (true) { // always running
			sonicSample.fetchSample(distance, 0); // check sample
			
			if (distance[0] <= MapFlowers.getFromWall()) { // stop if close to wall
				pilot.stop();
				Delay.msDelay(1000);
			}
		}
	}
}