package desktop;
import javax.swing.*;

import boofcv.gui.image.ImagePanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

//starts all other threads, initializes and handles UI and transfers information to bluetooth thread
//begins on run()
public class CompClientUI extends Thread implements ActionListener {
	private static final int SIZEX = 1080;
	private static final int SIZEY = 720;
	
	boolean autoBool = false; // toggle between automatic algorithm and manual control
	boolean CompuVision = false; // toggle between computer vision and actual camera
	boolean algoWasFollowing = false; // remembers what state the algorithm.isFollowing() had.
	
	JFrame uiFrame;			// the base UI, in which all components (below) are put
	ImagePanel imgPanel;	// image to store the information from camera
	JLabel explanation;		// textbox for basic explanation
	JLabel autoInfoBox;		// textbox to tell wether automatic control is on or off
	JButton buttonRight;	// button for right movement
	JButton buttonLeft;		// button for left movement
	JButton buttonStop;		// button for ending movement
	JButton buttonAuto;		// button for toggling automatic control on and off
	JButton buttonChange;	// button for toggling computer vision on and off
	
	CompClientBlu bluetooth;	// stored bluetooth thread (sets movement here)
	CompVisionAlgo algorithm;	// stored camera algorithm (gets automatic movement from here)
	CompCameraProvider camera;	// stored camera handler (gets videofeed here from here)
	
	// constructor
	public CompClientUI(CompClientBlu blub, CompVisionAlgo algo, CompCameraProvider camera){
		this.bluetooth = blub;
		this.algorithm = algo;
		this.camera = camera;
	}
	
	
	public void run(){
		// starting threads
		bluetooth.start();
		camera.open();
		algorithm.setPriority(MAX_PRIORITY);
		algorithm.start();
		
		// initializing ui base
		uiFrame = new JFrame("Controller UI");
		GridBagConstraints constra = new GridBagConstraints();
		uiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // this is used to close all other threads also
		uiFrame.setLayout(new GridBagLayout()); // using GridBagLayout
		
		// initializing UI parts
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
		imgPanel.setPreferredSize(camera.getWebcam().getViewSize()); // sets image size to camera image
		
		//add all UI parts (labels, buttons and an image)
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
			if (autoBool){ // if automatic control is on
				bluetooth.changeTurnFloat(algorithm.getTurnVector()); //algorithm chooses
				// checks for changes in algorithm.isFollowing, to determine the sound to be played.
				if (algorithm.isFollowing() && !algoWasFollowing){ // algorithm has just found something
					algoWasFollowing = true;
					bluetooth.changeSoundInt(3);
				} else if (!algorithm.isFollowing() && algoWasFollowing) { // algorithm has just lost it's target
					algoWasFollowing = false;
					bluetooth.changeSoundInt(4);
				}
			}
			if (CompuVision) { // see what computer sees
				imgPanel.setBufferedImageSafe(algorithm.getVisualizedImage());
			}else{ // see what camera sees
				imgPanel.setBufferedImageSafe(camera.getFrame());
			}
			
			
			Runtime runtime = Runtime.getRuntime();

			NumberFormat format = NumberFormat.getInstance();

			StringBuilder sb = new StringBuilder();
			long maxMemory = runtime.maxMemory();
			long allocatedMemory = runtime.totalMemory();
			long freeMemory = runtime.freeMemory();

			sb.append("free memory: " + format.format(freeMemory / 1024) + "\n");
			sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
			sb.append("max memory: " + format.format(maxMemory / 1024) + "\n");
			sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");
			
			System.out.println(sb.toString());
		}
	}
	
	// method for quickly setting GridBagConstraints for UI elements
	private GridBagConstraints changeConstraints(GridBagConstraints con, double weight, int x, int y){
		con.fill = GridBagConstraints.RELATIVE;
		con.weightx = weight;
		con.gridx = x;
		con.gridy = y;
		return con;
		
	}
	
	// listens to UI buttons and acts accordingly
	@Override
	public void actionPerformed(ActionEvent event) {
		System.out.println("A button was pressed...");
		if (event.getSource() == buttonAuto){
			if (autoBool){ // set auto off
				autoBool = false;
				autoInfoBox.setText("Auto is OFF");
				bluetooth.changeTurnFloat(0);
			} else { // set auto on
				autoBool = true;
				autoInfoBox.setText("Auto is ON");
				bluetooth.changeSoundInt(2);
			}
		}
		if (!autoBool){ // during automatic control, most buttons don't do anything
			if (event.getSource() == buttonRight){
				bluetooth.changeTurnFloat(1.0f);
			} else if (event.getSource() == buttonLeft){
				bluetooth.changeTurnFloat(-1.0f);
			} else if (event.getSource() == buttonStop){
				bluetooth.changeTurnFloat(0f);
			}
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