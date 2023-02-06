import java.io.File;
import lejos.hardware.BrickFinder;
import lejos.hardware.Sound;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.Delay;

/*
 *							PlaySound
 * This behaviour plays a sound and displays an emoticon on the LCD
 * display when its touch sensor is pressed.                                                *
 */

public class PlaySound implements Behavior{
	private float[] touchLevel = new float[1];
	EV3TouchSensor ts = new EV3TouchSensor(SensorPort.S2);
	SampleProvider touch = ts.getTouchMode();
	private GraphicsLCD g= BrickFinder.getDefault().getGraphicsLCD();
	static File soundfile = new File("sadasf.wav");

	
	public boolean takeControl() {
		touch.fetchSample(touchLevel, 0);
		if (touchLevel[0]==1) { //touch sensor is pressed
			return true;
		}
		return false;
	}

	
	public void action() {
		g.fillRect(20, 25, 40, 40); //x,y,width,height
		g.fillRect(120, 25, 40, 40);
		g.fillArc(25, 85, 120, 50, 0, 180);//x,y,width,height,startangle, 
		//Sound.beepSequence();
		Sound.playSample(soundfile,100);
		Delay.msDelay(5000);
	}

	
	public void suppress() {
	}
}