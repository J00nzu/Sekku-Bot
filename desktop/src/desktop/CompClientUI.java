package desktop;
import javax.swing.*;

import boofcv.gui.image.ImagePanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

/**
 * Starts all other desktop threads
 * Initializes and handles User Interface
 * Transfers information to bluetooth thread
 * @author Haell
 *
 */
public class CompClientUI extends Thread implements ActionListener {
	private static final int SIZEX = 1080;
	private static final int SIZEY = 720;
	
	boolean autoBool = false;		// toggle between automatic algorithm and manual control
	boolean CompuVision = false;	// toggle between computer vision and actual camera
	boolean algoWasFollowing = false; // remembers what state the algorithm.isFollowing() had.
	
	JFrame uiFrame;				// the base UI, in which all components (below) are put
	JLabel explanation;			// textbox for basic explanation
	JLabel autoInfoBox;			// textbox to tell wether automatic control is on or off
	JButton buttonRight;		// button for right movement
	JButton buttonLeft;			// button for left movement
	JButton buttonStop;			// button for ending movement
	JButton buttonAuto;			// button for toggling automatic control on and off
	JButton buttonChange;		// button for toggling computer vision on and off
	CompClientBlu bluetooth;	// stored bluetooth thread (sets movement here)
	CompVisionAlgo algorithm;	// stored camera algorithm (gets automatic movement from here)
	CompCameraProvider camera;	// stored camera handler (gets videofeed here from here)
	ImagePanel imgPanel;		// image to store the information from camera
	
	/**
	 * Takes other threads and stores them.
	 * @param blub Initialized Desktop bluetooth thread.
	 * @param algo Initialized Desktop camera follow algorithm thread.
	 * @param camera Initialized Camera Handler class.
	 */

	public CompClientUI(CompClientBlu blub, CompVisionAlgo algo, CompCameraProvider camera){
		this.bluetooth = blub;
		this.algorithm = algo;
		this.camera = camera;
	}
	
	/**
	 * Start of the thread.
	 * Starts other threads, initializes UI 
	 * Continues to read video feed
	 * Continues to transmit data to bluetooth
	 */

	public void run(){
		// starting threads
		bluetooth.start();
		camera.open();
		algorithm.setPriority(MAX_PRIORITY);
		algorithm.start();
		
		// initializing ui base
		uiFrame = new JFrame("Controller UI");
		GridBagConstraints constra = new GridBagConstraints();
		uiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		uiFrame.setLayout(new GridBagLayout());
		
		/**
		 * Buttons connect to Autolistener
		 */

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
		imgPanel.setPreferredSize(camera.getWebcam().getViewSize());
		
		//add all UI parts (labels, buttons and an image)
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
			
			// testing for memory leaks
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

	/**
	 * Method for quickly setting GridBagConstraints for UI elements
	 * @param con Storage spot.
	 * @param weight How much extra space this element gets.
	 * @param x Column number.
	 * @param y Row number.
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
	
	/**
	 * Action listener for this thread.
	 * (Don't call this function)
	 * Listens to UI buttons and acts accordingly
	 */
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