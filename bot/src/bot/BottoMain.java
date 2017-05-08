package bot;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;

// initializes all threads and all devices, and sets program in motion
// this brick side server-like programs must be started before the desktop program will work
public class BottoMain {
	
    public static void main(String[] args) {
    	// device initializations
    	RegulatedMotor motor = new EV3LargeRegulatedMotor(MotorPort.C);
    	EV3TouchSensor sens1 = new EV3TouchSensor(SensorPort.S1);
    	EV3TouchSensor sens2 = new EV3TouchSensor(SensorPort.S4);
    	
    	// thread initializations
    	BottoMotor cont = new BottoMotor(motor, sens1, sens2);
    	BottoSound soun = new BottoSound();
    	BottoConnect connection = new BottoConnect(cont, soun);
    	connection.start();
    }
}
