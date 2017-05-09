
package bot;

import java.io.File;
import java.util.Random;

import lejos.hardware.Sound;
import lejos.utility.Delay;
/**
 * A thread controlling outgoing sound.
 * Allows the robot to play sound files when certain events occur.
 * During runtime, receives updates with changeIncCommand().
 * To end the thread, call endThread()
 * @author Leevi Junttila
 * @author Elda (Sakari)
 */
public class BottoSound extends Thread {
	private final int timeBetweenSoundsInMillis = 5000;
	
	private File sound = null; // save spot for the actual file to be played
	private boolean shouldPlay = false; // near obsolete boolean to determine wether sound should play or not.
	private boolean endBool = false; // Determines the end. (See run())
	
	//Elda variables
	private int incCommand = 0;
	private Random trueR;
	private long timeSinceLastSound;
	private int lastSoundCommand = 0;
	// saved filename lists
	private final String[] targetFoundArr = new String[]
			{"can_i_help_you.aiff","hello_friend.aiff","hi.aiff","i_see_you.aiff","target_found.aiff","whos_there.aiff"};
	private final String[] targetLostArr = new String[]
			{"are_you_still_there.aiff","target_lost.aiff"};
	private final String[] errorArr = new String[]
			{"malfunctioning.aiff","unknown_error.aiff"};
	private final String[] autoActArr = new String[]
			{"activated.aiff","searching.aiff","senntry_mode_activated.aiff"};
	
	/*
	 * Constructor.
	 * Initializes Random trueR to System.currentTimeMillis()
	 */
	public BottoSound(){
		this.trueR = new Random(System.currentTimeMillis());
		this.timeSinceLastSound = System.currentTimeMillis()-15000;
	}
	//Elda varibles END

	/**
	 * Sets a sound file used for finding a target to the sound variable
	 */
	public void targetFoundSound() {
		// max = funktion käytössä olevien äänifilujen määrä
		int max = 6;
		// generoi luvun 1 ja max välillä
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

	// löytö
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
			sound = new File("senntry_mode_activated.aiff");
			break;
		}
		shouldPlay = true;
	}
	
	/**
	 * checks endBool and incCommand to determine what to do
	 * plays set sound file when incCommand is not 0
	 * Runtime can be ended with endThread()
	 */
	public void run() {
		while (!endBool) {
			if (incCommand != 0 && 
					((timeSinceLastSound+timeBetweenSoundsInMillis < System.currentTimeMillis()) || (lastSoundCommand != incCommand))){
				lastSoundCommand = incCommand;
				timeSinceLastSound = System.currentTimeMillis();
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
				incCommand = 0; // ensures each specific command is "received" only once
				if (shouldPlay) {
					Sound.playSample(sound, 100);
					shouldPlay = false;
				}
			}
			Delay.msDelay(20);
		}
	}
	
	/**
	 * Method used to invoke specific kind of sound.
	 * @param iCommand new incCommand. Set this from 1 to 4.
	 */
	public void changeIncCommand(int iCommand){
		this.incCommand = iCommand;
	}
	
	//example of a better method
	// randomizes one filename from list and sets it to to sound
	/**
	 * Sets a sound file to the sound variable depending on the name list given to the function
	 */
	public void newSound(String[] fileNameList){
		int rand = trueR.nextInt(fileNameList.length);
		sound = new File(fileNameList[rand]);
		shouldPlay = true;
	}
	
	/**
	 * When run, softly ends the thread.
	 */
	public void endThread(){
		endBool = true;
	}
}
