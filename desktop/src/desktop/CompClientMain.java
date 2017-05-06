package desktop;

public class CompClientMain {

	public static void main(String[] args) {
		//initialization of Algorithm thread here, and give it to UI thread
		//Algorithm someName = new Algorithm();
		CompClientBlu blu = new CompClientBlu();
		CompClientUI ui = new CompClientUI(blu);
		ui.start();
	}

}
