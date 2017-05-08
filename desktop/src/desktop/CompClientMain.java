package desktop;

import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ShowImages;

public class CompClientMain {

	public static void main(String[] args) {
		String eiKissaa = CompCameraProvider.getDefaultWebcam();
		CompCameraProvider prov = new CompCameraProvider(eiKissaa, 640,480);
		CompClientBlu blu = new CompClientBlu();
		CompVisionAlgo algo = new CompVisionAlgo(prov, 200);
		CompClientUI ui = new CompClientUI(blu, algo, prov);
		ui.start();
		
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
