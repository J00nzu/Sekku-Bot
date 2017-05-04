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
	
	public void run() {;
		try {
			socket = new Socket(HOST,PORT);
			dos = new DataOutputStream(socket.getOutputStream());
			while (true){
				dos.writeFloat(turnFloat);
				dos.flush();
				System.out.println("Sent: "+turnFloat);
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

}
