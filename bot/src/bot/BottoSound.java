package bot;

import java.io.File;

import lejos.hardware.Sound;
import lejos.utility.Delay;

public class BottoSound extends Thread{
	//löytö
	//kohteen menetys
	//sensoriäänet?
	//fnktioita ohjauskomennoille? (oikea vasen stop)
	private File sound=null;
	private boolean shouldPlay=false;
	
	
	public void targetFoundSound(){
		//max = funktion käytössä olevien äänifilujen määrä 
		int max=6;
		//generoi luvun 1 ja max välillä
		int choice = 1 + (int)(Math.random() * ((max - 1) + 1));
		switch(choice){
			case 1: sound = new File ("can_i_help_you.aiff");
					break;
			case 2: sound = new File ("hello_friend.aiff");
					break;
			case 3: sound = new File ("hi.aiff");
					break;
			case 4: sound = new File ("i_see_you.aiff");
					break;
			case 5: sound = new File ("target_found.aiff");
					break;
			case 6: sound = new File ("whos_there.aiff");
					break;
		}
		shouldPlay=true;	
	}
	
	//löytö
	public void targetLostSound(){
		int max=2;
		int choice = 1 + (int)(Math.random() * ((max - 1) + 1));
		switch(choice){
			case 1: sound = new File ("are_you_still_there.aiff");
					break;
			case 2: sound = new File ("target_lost.aiff");
					break;
		}
		shouldPlay=true;
	}
	
	//error
	public void errorSound(){
		int max=2;
		int choice = 1 + (int)(Math.random() * ((max - 1) + 1));
		switch(choice){
			case 1: sound = new File ("malfunctioning.aiff");
					break;
			case 2: sound = new File ("unknown_error.aiff");
					break;
		}
		shouldPlay=true;
	}
	
	//automode
	public void autoModeActivationSound(){
		int max=3;
		int choice = 1 + (int)(Math.random() * ((max - 1) + 1));
		switch(choice){
			case 1: sound = new File ("activated.aiff");
					break;
			case 2: sound = new File ("searching.aiff");
					break;
			case 3: sound = new File ("senntry_mode_activated.aiff");
					break;
		}
		shouldPlay=true;
	}
	
	public void run(){
		while(true){
		if(shouldPlay){
			Sound.playSample(sound,100);
			shouldPlay=false;
			}
		Delay.msDelay(100);
		}
	}
}
