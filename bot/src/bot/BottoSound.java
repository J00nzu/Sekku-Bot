/**
 * Allows the robot to play sound files when certain events occur
 * @author Leevi Junttila
 * @author Sakari Helokunnas
 */
package bot;

import java.io.File;
import java.util.Random;

import lejos.hardware.Sound;
import lejos.utility.Delay;

public class BottoSound extends Thread {
	
	private File sound = null;
	private boolean shouldPlay = false;
	private boolean endBool = false;
	
	//Elda variables
	private int incCommand = 0;
	private Random trueR;
	private final String[] targetFoundArr = new String[]
			{"can_i_help_you.aiff","hello_friend.aiff","hi.aiff","i_see_you.aiff","target_found.aiff","whos_there.aiff"};
	private final String[] targetLostArr = new String[]
			{"are_you_still_there.aiff","target_lost.aiff"};
	private final String[] errorArr = new String[]
			{"malfunctioning.aiff","unknown_error.aiff"};
	private final String[] autoActArr = new String[]
			{"activated.aiff","searching.aiff","senntry_mode_activated.aiff"};
	
	public BottoSound(){
		this.trueR = new Random(System.currentTimeMillis());
	}
	//Elda varibles END

	/**
	 * Sets a sound file used for finding a target to the sound variable
	 */
	public void targetFoundSound() {
		// max = funktion k�yt�ss� olevien ��nifilujen m��r�
		int max = 6;
		// generoi luvun 1 ja max v�lill�
		int choice = 1 + (int) (Math.random() * ((max - 1) + 1));
		switch (choice) {
		case 1:
			sound = new File("can_i_help_you.aiff");
			break;
		case 2:
			sound = new File("hello_friend.aiff");
			break;
		case 3:
			sound = new File("hi.aiff");
			break;
		case 4:
			sound = new File("i_see_you.aiff");
			break;
		case 5:
			sound = new File("target_found.aiff");
			break;
		case 6:
			sound = new File("whos_there.aiff");
			break;
		}
		shouldPlay = true;
	}

	// l�yt�
	/**
	 * Sets a sound file used when losing target to the sound variable
	 */
	public void targetLostSound() {
		int max = 2;
		int choice = 1 + (int) (Math.random() * ((max - 1) + 1));
		switch (choice) {
		case 1:
			sound = new File("are_you_still_there.aiff");
			break;
		case 2:
			sound = new File("target_lost.aiff");
			break;
		}
		shouldPlay = true;
	}

	// error
	/**
	 * Sets a sound file used when an error occurs to the sound variable
	 */
	public void errorSound() {
		int max = 2;
		int choice = 1 + (int) (Math.random() * ((max - 1) + 1));
		switch (choice) {
		case 1:
			sound = new File("malfunctioning.aiff");
			break;
		case 2:
			sound = new File("unknown_error.aiff");
			break;
		}
		shouldPlay = true;
	}

	// automode
	/**
	 * Sets a sound file used when activating auto-mode to the sound variable
	 */
	public void autoModeActivationSound() {
		int max = 3;
		int choice = 1 + (int) (Math.random() * ((max - 1) + 1));
		switch (choice) {
		case 1:
			sound = new File("activated.aiff");
			break;
		case 2:
			sound = new File("searching.aiff");
			break;
		case 3:
			sound = new File("sentry_mode_activated.aiff");
			break;
		}
		shouldPlay = true;
	}
	/**
	 * checks endBool and incCommand todetermine what to do
	 * plays set sound file when incCommand in not 0
	 * ends if endBool is true
	 */
	public void run() {
		while (!endBool) {
			if (incCommand != 0){
				switch(incCommand){
				case 1:
					errorSound();
					//newSound(errorArr);
					break;
				case 2:
					autoModeActivationSound();
					//newSound(autoActArr);
					break;
				case 3:
					targetFoundSound();
					//newSound(targetFoundArr);
					break;
				case 4:
					targetLostSound();
					//newSound(targetLostArr);
				default:
					shouldPlay = false;
				}
				incCommand = 0;
				if (shouldPlay) {
					Sound.playSample(sound, 100);
					shouldPlay = false;
				}
			}
			Delay.msDelay(20);
		}
	}
	/**
	 * Changes incoming bluetooth command
	 */
	public void changeIncCommand(int iCommand){
		this.incCommand = iCommand;
	}
	//example of a better method
	/**
	 * Sets a sound file to the sound variable depending on the name list given to the function
	 */
	public void newSound(String[] fileNameList){
		int rand = trueR.nextInt(fileNameList.length);
		sound = new File(fileNameList[rand]);
		shouldPlay = true;
	}
	/**
	 * ends thread
	 */
	public void endThread(){
		endBool = true;
	}
}
