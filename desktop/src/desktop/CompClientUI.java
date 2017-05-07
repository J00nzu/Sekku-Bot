package desktop;
import javax.swing.*;
import boofcv.gui.image.ImagePanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class CompClientUI extends Thread implements ActionListener {
	private static final int SIZEX = 1080;
	private static final int SIZEY = 720;
	
	boolean autoBool = false;
	boolean CompuVision = false;
	
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
		
		//add label
		constra = changeConstraints(constra, 0,1,0);
		uiFrame.add(explanation, constra);
		
		constra = changeConstraints(constra, 0.5,0,1);
		uiFrame.add(buttonLeft, constra);
		
		constra = changeConstraints(constra, 0.5,1,1);
		uiFrame.add(buttonStop, constra);
		
		constra = changeConstraints(constra, 0.5,2,1);
		uiFrame.add(buttonRight, constra);
		
		constra = changeConstraints(constra, 0,1,2);
		uiFrame.add(buttonAuto, constra);
		
		constra = changeConstraints(constra, 0,1,3);
		uiFrame.add(buttonChange, constra);
		
		constra = changeConstraints(constra, 0,1,4);
		uiFrame.add(autoInfoBox, constra);
		
		constra = changeConstraints(constra, 0,1,5);
		uiFrame.add(imgPanel, constra);
		
		uiFrame.setSize(SIZEX, SIZEY);
		uiFrame.setVisible(true);
		
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