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
	 * Constructs a CompCameraProvider with the desired parameters <br><br>
	 * --Example of use--
	 * 
	 * <pre>
	 * 
	 * 	String camName = CompCameraProvider.getDefaultWebcam();
	 * 	CompCameraProvider camera = new CompCameraProvider(camName, 640, 480);
	 * 	camera.open();
	 * 
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

	/**
	 * Opens the webcam with the desired device name
	 */
	public void open() {
		if (webcam == null) {
			webcam = UtilWebcamCapture.openDevice(camName, sWidth, sHeight);

		}
	}

	/**
	 * Closes the webcam
	 */
	public void close() {
		if (webcam != null) {
			webcam.close();
		}
	}

	/**
	 * Gets all the available webcams on this system
	 * @return A list of strings with the webcam names
	 */
	public static List<String> getAvailableWebcams() {
		List<Webcam> webcams = Webcam.getWebcams();

		ArrayList<String> strings = new ArrayList<String>();

		for (Webcam wc : webcams) {
			strings.add(wc.getName());
		}

		return strings;
	}

	/**
	 * Gets the default webcam on the system
	 * @return A String of the webcam name
	 */
	public static String getDefaultWebcam() {
		String camN = "";
		Webcam cam = Webcam.getDefault();

		if (cam != null) {
			camN = cam.getName();
		}

		return camN;
	}

	/**
	 * Gets the next frame from the camera.<br>
	 * If another thread is already polling for a frame, this will wait until the frame is available.
	 * @return A BufferedImage of the next frame
	 */
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

	/**
	 * If last captured frame is reasonably fresh,<br> will return the last captured frame without waiting for a new one.
	 * @return The last frame from the camera
	 */
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
	
	/**
	 * Gets the width of this camera
	 * @return The width of the camera
	 */
	public int getWidth(){
		return sWidth;
	}
	
	/**
	 * Gets the height of this camera
	 * @return The height of the camera
	 */
	public int getHeight(){
		return sHeight;
	}
	
	/**
	 * Gets the actual Webcam object of this CameraProvider
	 * @return The Webcam or null if not yet opened
	 */
	public Webcam getWebcam(){
		return webcam;
	}


}
