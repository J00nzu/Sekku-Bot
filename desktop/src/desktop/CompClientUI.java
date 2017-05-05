package desktop;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class CompClientUI extends Thread implements ActionListener {
	private static final int SIZEX = 300;
	private static final int SIZEY = 300;
	
	boolean autoBool = false;
	
	JFrame uiFrame;
	JLabel explanation;
	JLabel autoInfoBox;
	JButton buttonRight;
	JButton buttonLeft;
	JButton buttonStop;
	JButton buttonAuto;
	CompClientBlu bluetooth;
	
	public CompClientUI(CompClientBlu blub){
		this.bluetooth = blub;
	}
	
	public void run(){
		bluetooth.start();
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
		uiFrame.add(autoInfoBox, constra);
		
		uiFrame.setSize(SIZEX, SIZEY);
		uiFrame.setVisible(true);
	}
	
	private GridBagConstraints changeConstraints(GridBagConstraints con, double weight, int x, int y){
		con.fill = GridBagConstraints.HORIZONTAL;
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
			} else {
				autoBool = true;
				autoInfoBox.setText("Auto is ON");
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
		}
	}
}
