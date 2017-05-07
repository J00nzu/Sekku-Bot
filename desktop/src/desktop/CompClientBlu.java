package desktop;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class CompClientBlu extends Thread {
	private static final String HOST = "10.0.1.1";
	private static final int PORT = 55555;
	
	Socket socket;
	DataOutputStream dos;
	
	private float turnFloat = 0;
	private int soundInt = 0; 
	// 0=noSound, 1=error, 2=autoOn, 3=targetFound, 4=targetLost
	
	public void run() {;
		try {
			socket = new Socket(HOST,PORT);
			dos = new DataOutputStream(socket.getOutputStream());
			while (true){
				dos.writeFloat(turnFloat);
				dos.writeInt(soundInt);
				dos.flush();
				System.out.println("Sent: "+turnFloat+" + "+soundInt);
				if (soundInt != 0){
					soundInt = 0;
				}
				Thread.sleep(100);
				if (turnFloat == 9001){
					break;
				}
			}
			dos.close();
			socket.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void changeTurnFloat(float flo){
		this.turnFloat = flo;
	}
	
	public void changeSoundInt(int ind){
		this.soundInt = ind;
	}

}
