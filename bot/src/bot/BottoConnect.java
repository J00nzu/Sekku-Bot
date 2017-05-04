package bot;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.lcd.LCD;

public class BottoConnect extends Thread { //brick part
	private static final int PORT = 55555;
	private static final int TIMEOUT = 2000;
	
	ServerSocket serverSocket;
	Socket socket;
	DataInputStream stream;
	BottoMotor motorcon;
	
	public BottoConnect(BottoMotor motor){
		this.motorcon = motor;
	}
	
	public void run(){
		motorcon.start();
		try {
			serverSocket = new ServerSocket(PORT);
			socket = serverSocket.accept();
			LCD.drawString("Connection made?", 0, 0);
			socket.setSoTimeout(TIMEOUT);
			stream = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			LCD.drawString("Herb",0,1);
		}
		while(true){
			try {
				float nextInt = stream.readFloat();
				motorcon.changeFloat(nextInt);
			} catch (IOException e) {
				motorcon.endThread();
				break;
			}
		}
		try {
            if (serverSocket != null) serverSocket.close();
            if (socket != null) socket.close();
            if (stream != null) stream.close();
        } catch (Exception e1) {
            System.err.println("Exception closing window: " + e1);
        }
		LCD.drawString("Ended?", 0, 0);
	}
}
