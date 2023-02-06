import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.BaseRegulatedMotor;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

/*							Gregg.java
 * 
 * The central class for Gregg which contains the main method. This class
 * is responsible for intialising shared objects, creating and managing
 * behaviour objects and handles the Arbitrator
 * 
 */

public class Gregg {	
	/* Declare constants */
	final static float WHEEL_DIAMETER = 56; // (mm) 
	final static float AXLE_LENGTH = 120; // (mm) from wheel centre to wheel centre	
	
	public static void main(String[] args) throws NullPointerException {		
		/* Initialise Motors and Chassis */
		BaseRegulatedMotor mLeft = new EV3LargeRegulatedMotor(MotorPort.A);
		Wheel wLeft = WheeledChassis.modelWheel(mLeft, WHEEL_DIAMETER).offset(-AXLE_LENGTH / 2);
		BaseRegulatedMotor mRight = new EV3LargeRegulatedMotor(MotorPort.B);
		Wheel wRight = WheeledChassis.modelWheel(mRight, WHEEL_DIAMETER).offset(AXLE_LENGTH / 2);
		Chassis chassis = new WheeledChassis((new Wheel[] {wRight, wLeft}), WheeledChassis.TYPE_DIFFERENTIAL);
		
		/* Shared MovePilot, PoseProvider and Navigator */
		MovePilot pilot = new MovePilot(chassis);
		pilot.setLinearSpeed(150);
		PoseProvider poseP = new OdometryPoseProvider(pilot);
		Navigator navigator = new Navigator(pilot);
		
		/* GyroSensor for correction */
		EV3GyroSensor gyro = new EV3GyroSensor(SensorPort.S4);
		SampleProvider gyroSample = gyro.getAngleMode();
		
		/* Shared ColourSensor, SampleProvider */
		EV3ColorSensor cs = new EV3ColorSensor(SensorPort.S1);
		SampleProvider cSample = cs.getRedMode();
		
		/* Initialise Threads */
		Thread distCheck = new DistanceCheck(pilot);
		Thread colourCheck = new ColourCheck(pilot, cSample);
		EmergencyExit EmergencyExit = new EmergencyExit();

		/* Initialise Behaviours */
		MapEnvironment mapEnvironment = new MapEnvironment(pilot, gyro, gyroSample, DistanceCheck.distance);
		MapFlowers mapFlowers = new MapFlowers(pilot, poseP, mapEnvironment.getXY());
		Move moveGregg = new Move(pilot, poseP, mapFlowers.getPoseArray());
		LowBattery lowBattery = new LowBattery(navigator);
		PlaySound playSound = new PlaySound();
		
		/* Define behaviour list and priorities */
		Behavior[] behaviorList = {moveGregg, playSound, mapFlowers, mapEnvironment, lowBattery};
		
		/* Initialise Arbitrator */
		Arbitrator arbiter = new Arbitrator(behaviorList, false); // false ensures a continuous loop
		
		/* Welcome, Authors, Version */
		LCD.clear();
		LCD.drawString("Welcome!", 0, 1);
		LCD.drawString("Authors:", 0, 2);
		LCD.drawString("Annabelle Wong", 0, 3);
		LCD.drawString("Jake Thorpe", 0, 4);
		LCD.drawString("Version: 1.0", 0, 5);
		Button.waitForAnyPress();
		LCD.clear();
		
		/* Start Threads */
		distCheck.start();
		colourCheck.start();
		EmergencyExit.start();
		
		/* Start Arbitrator */
		arbiter.go();
		
		/* Close open resources */
		cs.close(); // is not reached while arbiter returnWhenInactive = false
	}
}
