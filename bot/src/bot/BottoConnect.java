package bot;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.lcd.LCD;

/**
 * A server-like thread. Waits for a connection to be made to PORT.
 * Receives data and transmits the data forward to motor controller and sound controller
 * Acts as a bridge from desktop to the brick.
 * @author Elda
 *
 */
public class BottoConnect extends Thread { //brick part
	private static final int PORT = 55555; // pre-determined listening port
	private static final int TIMEOUT = 2000; // time (in milliseconds) to terminate thread after not receiving an input
	
	// server connection parameters
	ServerSocket serverSocket; 
	Socket socket;
	DataInputStream stream;
	
	// saved threads
	BottoMotor motorcon;
	BottoSound soundcon;
	/**
	 * Receives initialized devices and stores them.
	 * @param motor Initialized motor controller Thread
	 * @param sound Initialized sound controlled Thread
	 */
	public BottoConnect(BottoMotor motor, BottoSound sound){
		this.motorcon = motor;
		this.soundcon = sound;
	}
	/**
	 * Start of this thread.
	 * Starts other threads, then waits for connection to be made.
	 * After getting connection, reads input stream and distributes data to controller threads.
	 */
	public void run(){
		// start connected threads
		motorcon.start();
		soundcon.start();
		try {
			// initialize listening
			serverSocket = new ServerSocket(PORT);
			socket = serverSocket.accept(); // unless connection is made, code stops here
			LCD.drawString("Connection made?", 0, 0);
			socket.setSoTimeout(TIMEOUT);
			stream = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
			LCD.drawString("Error",0,1);
		}
		// reads input stream and passes data forward accordingly
		while(true){
			try {
				float nextInt = stream.readFloat();
				int nextSound = stream.readInt();
				motorcon.changeFloat(nextInt);
				if (nextSound != 0){
					soundcon.changeIncCommand(nextSound);
				}
			} catch (IOException e) {
				// on IOException and TIMEOUT, closes other threads and leaves while-loop
				motorcon.endThread();
				soundcon.endThread();
				break;
			}
		}
		try {
			// closing sequence, closes all connections
            if (serverSocket != null) serverSocket.close();
            if (socket != null) socket.close();
            if (stream != null) stream.close();
        } catch (Exception e1) {
            //System.err.println("Exception closing window: " + e1);
        }
		LCD.drawString("Ended?", 0, 0);
	}
}
