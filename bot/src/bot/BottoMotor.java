package bot;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.RegulatedMotor;

public class BottoMotor extends Thread {
	private static final int SPEED = 50;
	private float turnFloat = 0;
	
	private boolean endBool = false;
	RegulatedMotor motor;
	EV3TouchSensor touchSen1;
	EV3TouchSensor touchSen2;
	
	float[] sample1;
	float[] sample2;
	
	public BottoMotor(RegulatedMotor mot, EV3TouchSensor sense1, EV3TouchSensor sense2){ 
		this.motor = mot;
		this.touchSen1 = sense1; //port1
		this.touchSen2 = sense2; //port4
		sample1 = new float[touchSen1.sampleSize()];
		sample2 = new float[touchSen2.sampleSize()];
	}
	
	public void run(){
		while (!endBool){
			if (turnFloat !=0){
				touchSen1.fetchSample(sample1, 0);
				touchSen2.fetchSample(sample2, 0);
				if((turnFloat < 0) && (sample1[0] == 0)){
					motor.setSpeed((int) (turnFloat*(-1)*SPEED));
					motor.backward();
				} else if ((turnFloat > 0) && (sample2[0] == 0)){
					motor.setSpeed((int) (turnFloat*SPEED));
					motor.forward();
				} else {
					motor.stop();
				}
			} else{
				motor.stop();
			}
		}
		motor.close();
		touchSen1.close();
		touchSen2.close();
	}
	
	public void changeFloat(float flo){
		this.turnFloat = flo;
	}
	
	public void endThread(){
		endBool = true;
	}
}
