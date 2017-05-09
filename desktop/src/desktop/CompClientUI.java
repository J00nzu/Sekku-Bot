package desktop;
import javax.swing.*;
import boofcv.gui.image.ImagePanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 
 * @author haell
 * ActionListener is there to method to make the buttons work when pressed
 * We decide the size of the window
 */

public class CompClientUI extends Thread implements ActionListener {
	private static final int SIZEX = 1080;
	private static final int SIZEY = 720;
	
	/**
	 * If false (default) manual control is used
	 */
	boolean autoBool = false;
	/**
	 * If false (default) shows normal camera feed
	 */
	boolean CompuVision = false;
	
	/*
	 * Create the button and picture objects
	 */
	
	JFrame uiFrame;
	JLabel explanation;
	JLabel autoInfoBox;
	JButton buttonRight;
	JButton buttonLeft;
	JButton buttonStop;
	JButton buttonAuto;
	JButton buttonChange;
	CompClientBlu bluetooth;
	CompVisionAlgo algorithm;
	CompCameraProvider camera;
	ImagePanel imgPanel;
	
	 /**
	  * 
	  * @param blub
	  * 		Bluetooth 
	  * @param algo
	  * 		Algorithhm
	  * @param camera
	  * 		Camera
	  */
	
	public CompClientUI(CompClientBlu blub, CompVisionAlgo algo, CompCameraProvider camera){
		this.bluetooth = blub;
		this.algorithm = algo;
		this.camera = camera;
	}
	
	/**
	 * Start the program
	 * Open Bluetooth, camera and Algorithm
	 * GridBagConstraints to decide how objects are laid out in the frame
	 */
	
	public void run(){
		bluetooth.start();
		camera.open();
		algorithm.start();
		uiFrame = new JFrame("Controller UI");
		GridBagConstraints constra = new GridBagConstraints();
		uiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		uiFrame.setLayout(new GridBagLayout());
		
		/**
		 * Buttons connect to Autolistener
		 */
		
		explanation = new JLabel("Text here");
		autoInfoBox = new JLabel("Auto is OFF");
		buttonRight = new JButton(">");
		buttonRight.addActionListener(this);
		buttonLeft = new JButton("<");
		buttonLeft.addActionListener(this);
		buttonStop = new JButton("STOP");
		buttonStop.addActionListener(this);
		buttonAuto = new JButton("Auto");
		buttonAuto.addActionListener(this);
		imgPanel = new ImagePanel();
		buttonChange = new JButton("CompuVision 2000TM");
		buttonChange.addActionListener(this);
		
		imgPanel.setPreferredSize(camera.getWebcam().getViewSize());
		
		/**
		 * Decide the position of each button
		 */
		
		constra = changeConstraints(constra, 1,0);
		uiFrame.add(explanation, constra);
		
		constra = changeConstraints(constra, 0,1);
		uiFrame.add(buttonLeft, constra);
		
		constra = changeConstraints(constra, 1,1);
		uiFrame.add(buttonStop, constra);
		
		constra = changeConstraints(constra, 2,1);
		uiFrame.add(buttonRight, constra);
		
		constra = changeConstraints(constra, 1,2);
		uiFrame.add(buttonAuto, constra);
		
		constra = changeConstraints(constra, 1,3);
		uiFrame.add(buttonChange, constra);
		
		constra = changeConstraints(constra, 1,4);
		uiFrame.add(autoInfoBox, constra);
		
		constra = changeConstraints(constra, 1,5);
		uiFrame.add(imgPanel, constra);
		
		uiFrame.setSize(SIZEX, SIZEY);
		uiFrame.setVisible(true);
		
		/**
		 * To check if we use Bluetooth or automatic control
		 * To check if we show normal camera view or computer vision
		 */
		
		while(true){
			if (autoBool){
				bluetooth.changeTurnFloat(algorithm.getTurnVector());
			}
			if (CompuVision) {
				imgPanel.setBufferedImageSafe(algorithm.GetVisualizedImage());
			}else{
			imgPanel.setBufferedImageSafe(camera.getFrame());
			}
		}
	}
	/**
	 * 		con.fill makes sure the area between the left and right buttons is filled with a button
	 * @param con
	 * 		The position on y-axis
	 * @param x
	 * 		The position on x-axis
	 * @param y
	 * 		Returns the con
	 * @return
	 */
	private GridBagConstraints changeConstraints(GridBagConstraints con, int x, int y){
		con.fill = GridBagConstraints.HORIZONTAL;
		con.gridx = x;
		con.gridy = y;
		return con;
		
	}
	/**
	 * This is called when a button is pressed
	 * @param event
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		System.out.println("A button was pressed...");
		if (event.getSource() == buttonAuto){
			if (autoBool){
				autoBool = false;
				autoInfoBox.setText("Auto is OFF");
				bluetooth.changeTurnFloat(0);
			} else {
				autoBool = true;
				autoInfoBox.setText("Auto is ON");
				bluetooth.changeSoundInt(2);
			}
		}
		if (!autoBool){
			if (event.getSource() == buttonRight){
				bluetooth.changeTurnFloat(1.0f);
			} else if (event.getSource() == buttonLeft){
				bluetooth.changeTurnFloat(-1.0f);
			} else if (event.getSource() == buttonStop){
				bluetooth.changeTurnFloat(0f);
			}
			if (event.getSource() == buttonChange){
				if (CompuVision){
				CompuVision = false;
				}else{
					CompuVision = true;
				}
			}
		}
	}
}