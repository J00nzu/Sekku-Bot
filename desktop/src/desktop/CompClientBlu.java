package desktop;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

//transfers received data over bluetooth to set HOST
public class CompClientBlu extends Thread {
	private static final String HOST = "10.0.1.1"; // IP address of the brick
	private static final int PORT = 55555; // pre-determined port to use for this transaction
	
	Socket socket; // saved connection socket
	DataOutputStream dos; // saved connection data output stream
	
	private float turnFloat = 0; // received data regarding camera turning (-1:1)
	private int soundInt = 0;  // received data regarding sound file
	// 0=noSound, 1=error, 2=autoOn, 3=targetFound, 4=targetLost
	

	public void run() {
		while (dos == null){
			try {
				// estabilishes connection and data stream
				socket = new Socket(HOST,PORT);
				dos = new DataOutputStream(socket.getOutputStream());
				// sends known and received data at rate of 10/second
				while (true){
					dos.writeFloat(turnFloat);
					dos.writeInt(soundInt);
					dos.flush(); // actually sends the data packet
					// System.out.println("Sent: "+turnFloat+" + "+soundInt);
					if (soundInt != 0){
						soundInt = 0;
					}
					Thread.sleep(100);
					if (turnFloat == 9001){ // end this thread by giving it 9001 via method
						break;
					}
				}
				dos.close();
				socket.close();
			} catch (UnknownHostException e) {
				System.out.println("Connection not found, retrying...");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	// method to change what turnFloat value is transmitted
	// (set this to 9001 to end the thread)
	public void changeTurnFloat(float flo){
		this.turnFloat = flo;
	}
	
	// method to change what soundInt value is transmitted
	public void changeSoundInt(int ind){
		this.soundInt = ind;
	}
}
