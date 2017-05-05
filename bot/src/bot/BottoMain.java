package bot;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.RegulatedMotor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;

public class BottoMain {
	
    public static void main(String[] args) {
    	RegulatedMotor motor = new EV3LargeRegulatedMotor(MotorPort.C);
    	EV3TouchSensor sens1 = new EV3TouchSensor(SensorPort.S1);
    	EV3TouchSensor sens2 = new EV3TouchSensor(SensorPort.S4);
    	BottoMotor cont = new BottoMotor(motor, sens1, sens2);
    	BottoConnect connection = new BottoConnect(cont);
    	connection.start();
    }
}
