package desktop;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamEvent;
import com.github.sarxos.webcam.WebcamListener;

import boofcv.io.webcamcapture.UtilWebcamCapture;

/**
 * Provides access to the system's webcams
 * 
 * @author Joni
 *
 */
public class CompCameraProvider {

	private Webcam webcam;
	private String camName;
	private int sWidth, sHeight;
	private BufferedImage lastFrame;
	private static float FPS = 30;
	private static int FrameTime= (int) (1000/FPS);
	private long lastFrameTime = 0;

	private Semaphore frameLock = new Semaphore(1);
	private Object frameMonitor = new Object();

	/**
	 * --Example--
	 * 
	 * <pre>
	 * {
	 * 	&#064;code
	 * 	String camName = CompCameraProvider.getDefaultWebcam();
	 * 	CompCameraProvider camera = new CompCameraProvider(camName, 640, 480);
	 * 	camera.open();
	 * }
	 * </pre>
	 * 
	 * @author J00nzu
	 * @param name
	 *            name of the camera device
	 * @param sourceWidth
	 *            desired width in pixels
	 * @param sourceHeight
	 *            desired height in pixels
	 * 
	 */
	public CompCameraProvider(String deviceName, int sourceWidth,
			int sourceHeight) {
		this.camName = deviceName;
		this.sWidth = sourceWidth;
		this.sHeight = sourceHeight;
	}

	public void open() {
		if (webcam == null) {
			webcam = UtilWebcamCapture.openDevice(camName, sWidth, sHeight);

		}
	}

	public void close() {
		if (webcam != null) {
			webcam.close();
		}
	}

	/**
	 * Gets all the available webcams on this system
	 * @return returns a list of strings with the webcam names
	 */
	public static List<String> getAvailableWebcams() {
		List<Webcam> webcams = Webcam.getWebcams();

		ArrayList<String> strings = new ArrayList<String>();

		for (Webcam wc : webcams) {
			strings.add(wc.getName());
		}

		return strings;
	}

	public static String getDefaultWebcam() {
		String camN = "";
		Webcam cam = Webcam.getDefault();

		if (cam != null) {
			camN = cam.getName();
		}

		return camN;
	}

	public synchronized BufferedImage getFrame() {
		if (webcam != null) {
			if(frameLock.tryAcquire()){
				lastFrame = webcam.getImage();
				
				synchronized (frameMonitor) {
					frameMonitor.notifyAll();
				}
				
				frameLock.release();
			}else{
				try {
					frameMonitor.wait(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			lastFrameTime = System.currentTimeMillis();
			return lastFrame;
		} else {
			return null;
		}
	}

	public BufferedImage getLastFrame() {
		if (webcam != null) {
			if(lastFrame==null){
				return getFrame();
			}else if(System.currentTimeMillis()-lastFrameTime > FrameTime){
				return getFrame();
			}
			
			return lastFrame;
		} else {
			return null;
		}
	}
	
	public int getWidth(){
		return sWidth;
	}
	
	public int getHeight(){
		return sHeight;
	}
	
	public Webcam getWebcam(){
		return webcam;
	}


}
