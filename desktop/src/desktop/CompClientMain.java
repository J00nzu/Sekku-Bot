package desktop;

import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ShowImages;

// initializes all threads run in desktop side and sets program in motion
// the brick side server-like program must be started before this desktop side program will work
public class CompClientMain {

	public static void main(String[] args) {
		// webcam selection (just in case computer has multiple webcams connected)
		List<String> webcams = CompCameraProvider.getAvailableWebcams();
		String selected = "";
		
		if(webcams.size()==0){
			JOptionPane.showMessageDialog(null, "No available webcams found", "Error", JOptionPane.ERROR_MESSAGE);
		}
		else{
			int selection = JOptionPane.showOptionDialog(null, "Choose a webcam", "Choose", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, webcams.toArray(), webcams.get(0));
			if(selection != -1){
				selected = webcams.get(selection);
			}
		}
		if(selected.isEmpty()){
			return;
		}
		//  webcam selection end
		CompCameraProvider prov = new CompCameraProvider(selected, 640,480); // initializes webcam handler with chosen name
		CompClientBlu blu = new CompClientBlu(); // initializes new bluetooth thread
		CompVisionAlgo algo = new CompVisionAlgo(prov, 200); // initializes new camera algorithm thread
		CompClientUI ui = new CompClientUI(blu, algo, prov); // initializes new UI thread with all of the above
		ui.start(); // starts UI thread
		
		// TEST CODE BELOW, DOES NOT MATTER!!
		/*
		List<String> webcams = CompCameraProvider.getAvailableWebcams();
		String selected = "";
		
		if(webcams.size()==0){
			JOptionPane.showMessageDialog(null, "No available webcams found", "Error", JOptionPane.ERROR_MESSAGE);
		}
		else{
			int selection = JOptionPane.showOptionDialog(null, "Choose a webcam", "Choose", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, webcams.toArray(), webcams.get(0));
			if(selection != -1){
				selected = webcams.get(selection);
			}
		}
		if(selected.isEmpty()){
			return;
		}
		
		final CompCameraProvider camera = new CompCameraProvider(selected, 640, 480);
		camera.open();
		
		ImagePanel gui = new ImagePanel();
		gui.setPreferredSize(camera.getWebcam().getViewSize());

		ShowImages.showWindow(gui,"KLT Tracker",true);
		
		CompVisionAlgo vision = new CompVisionAlgo(camera, 500);
		
		while(true){
			gui.setBufferedImageSafe(vision.WaitNextVisualizedImage());
			System.out.println(vision.getTurnVector());
		}
		camera.close();
		*/
	}

}
