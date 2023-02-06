import java.util.ArrayList;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

/*							Move.java
 *
 * The move behaviour receives an array of Poses taken by the         
 * MapFlowers behaviour and creates waypoints based on these. The     
 * robot will then navigate to each of these waypoints.               
 *                                                                    
 * @param	pilot		a MovePilot object                            
 * @param	poseProv	a PoseProvider object                         
 * @param	poseArr		an ArrayList of poses taken by the MapFlowers 
 * 						behaviour                                     
 *                                                                    
 */

public class Move implements Behavior {
	private MovePilot pilot;
	private PoseProvider poseP;
	private ArrayList<Pose> poseArray;
	private boolean isVisitedWaypoints = false;
	
	/* EV3MediumMotor for the arm */
	private BaseRegulatedMotor arm = new EV3MediumRegulatedMotor(MotorPort.D);
	
	Move(MovePilot pilot, PoseProvider poseProv, ArrayList<Pose> poseArr) { 
		this.pilot = pilot;
		this.poseP = poseProv;
		this.poseArray = poseArr;
	}
	
	public boolean takeControl() {
		return true;
	}
	
	public void action() {		
		if (!isVisitedWaypoints) {
			Navigator navigator = new Navigator(pilot);
			poseP.setPose(poseArray.get(0));
			Button.waitForAnyPress(); // put Gregg back to home position
			LCD.clear();
			LCD.drawString("Finding path...", 0, 3);
			Delay.msDelay(500);
			
			for (int i = 1; i < poseArray.size(); i++) { // i = 1, so we ignore start pose
				// take the x position, y position, and heading from a Pose and create waypoints and navigate to these
				navigator.goTo(new Waypoint(poseArray.get(i).getX(), poseArray.get(i).getY(), poseArray.get(i).getHeading()));
				Delay.msDelay(7500);
				arm.setSpeed(90);
				arm.forward(); // turn arm to water flowers
				Delay.msDelay(1000);
				arm.stop();
				Delay.msDelay(1000);
			}
			navigator.goTo(new Waypoint(poseArray.get(0).getX(), poseArray.get(0).getY(), poseArray.get(0).getHeading())); // return home
			isVisitedWaypoints = true;
		}
	}
	
	public void suppress() {
		pilot.stop();
		return;
	}
}
