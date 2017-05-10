package bot;
import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.RegulatedMotor;

/**
 * A thread that controls camera motor turning.
 * Receives control instructions from outside and controls accordingly.
 * Reads touch sensors and prevents turning to that direction when limit has been reached
 * To end the thread, call endThread()
 * @author Elda
 *
 */
public class BottoMotor extends Thread {
	private static final int SPEED = 50;
	private float turnFloat = 0;
	private float savedTurnFloat = 0;
	private boolean endBool = false;
	
	// saved device parameters
	RegulatedMotor motor;
	EV3TouchSensor touchSen1;
	EV3TouchSensor touchSen2;
	
	float[] sample1;
	float[] sample2;
	/**
	 * Constructor for a thread that controls camera motor.
	 * Receives initialized devices and stores them, initializing samples in the process.
	 * During runtime, receives updates with changeFloat().
	 * @param mot Initialized motor.
	 * @param sense1 Initialized right side touch sensor.
	 * @param sense2 Initialized left side touch sensor.
	 */
	public BottoMotor(RegulatedMotor mot, EV3TouchSensor sense1, EV3TouchSensor sense2){ 
		this.motor = mot;
		this.touchSen1 = sense1; //port1
		this.touchSen2 = sense2; //port4
		sample1 = new float[touchSen1.sampleSize()];
		sample2 = new float[touchSen2.sampleSize()];
	}
	/**
	 * Start of the thread.
	 * Reads input data and touch sensors and proceeds to turn the motor accordingly.
	 */
	public void run(){
		while (!endBool){
			touchSen1.fetchSample(sample1, 0);
			touchSen2.fetchSample(sample2, 0);
			LCD.drawString(sample1[0]+" "+sample2[0], 0, 4);
			if (turnFloat != savedTurnFloat){
				savedTurnFloat = turnFloat;
				if((turnFloat > 0) && (sample1[0] == 0)){ // turning right
					motor.setSpeed((int) (turnFloat*(-1)*SPEED));
					motor.forward();
				} else if ((turnFloat < 0) && (sample2[0] == 0)){ // turning left
					motor.setSpeed((int) (turnFloat*SPEED));
					motor.backward();
				} else { //if turnFloat = 0
					motor.stop();
				}
			} else if (((turnFloat > 0) && (sample1[0] == 1)) || ((turnFloat < 0) && (sample2[0] == 1))){
				motor.stop();
			}
		}
		// closing sequence, closes connected devices
		motor.close();
		touchSen1.close();
		touchSen2.close();
	}
	
	/**
	 * Sets turnFloat (a parameter used to determine turn direction and speed) to given float.
	 * @param flo new turnFloat.
	 */
	public void changeFloat(float flo){
		this.turnFloat = flo;
	}
	
	/**
	 * Once run, softly ends the thread.
	 */
	public void endThread(){
		endBool = true;
	}
}
