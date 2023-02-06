import lejos.robotics.SampleProvider;
import lejos.robotics.navigation.MovePilot;

public class ColourCheck extends Thread {
	private MovePilot pilot; // shared MovePilot
	private SampleProvider cSample; // shared SampleProvider (colour)
	private static float[] cLevel = new float[1];
	private float threshold = 0.5f;
	
	/* Constructor */
	ColourCheck(MovePilot p, SampleProvider sp) {
		this.pilot = p;
		this.cSample = sp;
	}
	
	/* Getter */
	public static float getColour() {
		return cLevel[0];
	}
	
	/* (Re)setter */
	public static void resetCL() {
		cLevel[0] = 0;
	}
	
	@Override
	public void run() {
		while (true) {
			cSample.fetchSample(cLevel, 0);
			if (cLevel[0] > threshold) { //means red(0.93-0.96)
				pilot.stop(); // Stop here, something detected
				try {
					Thread.sleep(5000); // sleep Thread for 5s so that it doesnt keep scanning the same red spot over and over again
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}