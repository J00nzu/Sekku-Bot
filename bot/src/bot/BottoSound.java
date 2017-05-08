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
	
	public void changeIncCommand(int iCommand){
		this.incCommand = iCommand;
	}
	
	public void newSound(String[] fileNameList){
		int rand = trueR.nextInt(fileNameList.length);
		sound = new File(fileNameList[rand]);
		shouldPlay = true;
	}
	
	public void endThread(){
		endBool = true;
	}
}
