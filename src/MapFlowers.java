import java.util.ArrayList;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Pose;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

/*
 *				MapFlowers.java
 *                                                                
 * The behaviour maps the mapped boundary in a spiral manner. It continuously 
 * senses for a coloured flower and marks down its pose.                                   
 *                                                                 
 * @param  pilot	a (shared) MovePilot generated in Gregg.java   
 * @param pp		a (shared) PoseProvider generated in Gregg.java
 * @param widthHeight	the boundary mapped in MapEnvironment.java
 * @return          	a list of poses where a flower is detected 
 *
 */

public class MapFlowers implements Behavior{
	private MovePilot pilot; 
	private PoseProvider poseProvider;
	private float[] xY; // x,y (boundary) values from MapEnvironment
	private int turn = 0; // track turn count
	private static float fromWall = 0.20f; // distance threshold from wall (m)
	private static boolean isFlowersMapped = false;
	private static ArrayList<Pose> poseArray = new ArrayList<Pose>(); // array to hold poses

	/* Constructor */
	MapFlowers(MovePilot p, PoseProvider pp, float[] widthHeight) {
		this.pilot = p;
		this.poseProvider = pp;
		this.xY = widthHeight;
	}
	
	/* Getters */
	public static boolean getIsFlowersMapped() {
		return isFlowersMapped;
	}
	
	public static float getFromWall() {
		return fromWall;
	}
	
	public ArrayList<Pose> getPoseArray() {
		return poseArray;
	}
	
	public Pose getHomePose() {
		return poseArray.get(0);
	}
	
	public boolean takeControl() {
		return (!isFlowersMapped);
	} 
	
	/* Take the current robot pose, output to screen */
	private void takePose() {
		
		LCD.drawString("Flower detected!", 0, 2);
		Sound.beepSequenceUp();
		Delay.msDelay(500);
		LCD.clear();
		LCD.drawString("Creating pose", 0, 2); // not a waypoint but a pose at this point
		Delay.msDelay(500);
		LCD.drawString("Waypoint added!", 0, 2);
		Delay.msDelay(500);
		LCD.clear();
		
		Pose newPose = poseProvider.getPose(); // create new pose	
		poseArray.add(newPose); // add pose to ArrayList
		LCD.drawString("" + newPose, 0, 2);
		Button.waitForAnyPress();
	}
	
	public void action() {
		while(!isFlowersMapped) {
			if (poseArray.isEmpty()) {
				poseProvider.setPose(new Pose(0, 0, 0));
				takePose(); // take starting pose
			}
			pilot.travel((xY[0] - fromWall) * 1000, false); // convert from m to mm, false so move completes before loop continues unless stopped by Thread

			if (!pilot.isMoving()) { // check DistanceCheck or ColourCheck stopped movement
				if (ColourCheck.getColour() > 0.5f) { // if ColourCheck
					ColourCheck.resetCL(); // reset cLevel, so we don't keep getting stopped
					takePose(); // mark position of the flower
					Delay.msDelay(1000);
				}
				if (DistanceCheck.getDistance() <= fromWall) { // if DistanceCheck
					Delay.msDelay(500);
					pilot.rotate(90, false);					
					turn++;
					
					if (turn % 3 == 0) { // creates square spiral pattern of movement for mapping area
						fromWall += 0.20;
					}
				}
			}
			// if fromWall >= X or Y then we must be at the centre point of that axis, ie, the middle
			if (fromWall >= xY[0] / 2) { // square, so fine to check just X or Y (for now)
				pilot.stop();
				isFlowersMapped = true; // cannot run again now
				fromWall = 0.15f; // reset fromWall back to 150mm from wall (otherwise he will stop from really far away from wall)
				return;
			}
		}
	}
	
	public void suppress() {
		pilot.stop();
		return;
	}
}
