package desktop;
import javax.swing.*;
import boofcv.gui.image.ImagePanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class CompClientUI extends Thread implements ActionListener {
	private static final int SIZEX = 1080;
	private static final int SIZEY = 720;
	
	boolean autoBool = false; // toggle between automatic algorithm and manual control
	boolean CompuVision = false; // toggle between computer vision and actual camera
	boolean algoWasFollowing = false; // remembers what state the algorithm.isFollowing() had.
	
	JFrame uiFrame;
	ImagePanel imgPanel;
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
	
	public CompClientUI(CompClientBlu blub, CompVisionAlgo algo, CompCameraProvider camera){
		this.bluetooth = blub;
		this.algorithm = algo;
		this.camera = camera;
	}
	
	public void run(){
		bluetooth.start();
		camera.open();
		algorithm.start();
		uiFrame = new JFrame("Controller UI");
		GridBagConstraints constra = new GridBagConstraints();
		uiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		uiFrame.setLayout(new GridBagLayout());
		
		
		explanation = new JLabel("SEKU-BOTTO");
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
		
		//add label
		constra = changeConstraints(constra, 0,1,0);
		uiFrame.add(explanation, constra);
		
		constra = changeConstraints(constra, 1,1,1);
		uiFrame.add(imgPanel, constra);
		
		constra = changeConstraints(constra, 0.5,0,2);
		uiFrame.add(buttonLeft, constra);
		
		constra = changeConstraints(constra, 0.5,1,2);
		uiFrame.add(buttonStop, constra);
		
		constra = changeConstraints(constra, 0.5,2,2);
		uiFrame.add(buttonRight, constra);
		
		constra = changeConstraints(constra, 0,0,3);
		uiFrame.add(autoInfoBox, constra);
		
		constra = changeConstraints(constra, 0,1,3);
		uiFrame.add(buttonAuto, constra);
		
		constra = changeConstraints(constra, 0,2,3);
		uiFrame.add(buttonChange, constra);

		
		uiFrame.setSize(SIZEX, SIZEY);
		uiFrame.setVisible(true);
		
		while(true){
			if (autoBool){
				bluetooth.changeTurnFloat(algorithm.getTurnVector()); //algorithm chooses
				if (algorithm.isFollowing() && !algoWasFollowing){
					algoWasFollowing = true;
					bluetooth.changeSoundInt(3);
				} else if (!algorithm.isFollowing() && algoWasFollowing) {
					algoWasFollowing = false;
					bluetooth.changeSoundInt(4);
				}
			}
			if (CompuVision) {
				imgPanel.setBufferedImageSafe(algorithm.getVisualizedImage());
			}else{
				imgPanel.setBufferedImageSafe(camera.getFrame());
			}
		}
	}
	
	private GridBagConstraints changeConstraints(GridBagConstraints con, double weight, int x, int y){
		con.fill = GridBagConstraints.RELATIVE;
		con.weightx = weight;
		con.gridx = x;
		con.gridy = y;
		return con;
		
	}

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