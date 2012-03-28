package audio;

import java.net.MalformedURLException;
import java.net.URL;

import audio.jmikmod.MikModApp;
import audio.gme.gme;

public class STest implements Runnable {

	static URL play;
	Thread mTest = null;
	
	static Mp3Player mp3player;
	static WavPlayer wavplayer;
	
	public static void main(String args[]) {
		
		try {
			play = new URL("file:/C:\\Rbp\\RPG\\PS\\PS\\MENU.WAV");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		STest m = new STest();
		m.start(play);
        for(int i=0;i<1000000; i++) {
        	System.out.println(i);
        }
		
	}
	
	public void start(URL url)
	{
		STest.play = url;
		if (mTest == null)
		{
			mTest = new Thread(this);
		}
		mTest.start();
	}
	
	public void run() {
		if(play == null || play.getFile() == null) {
			System.err.println("No file to play.");
			return;
		}
		
		if(play.getFile().toLowerCase().endsWith("mp3")){
			mp3player = new Mp3Player(play.getFile());
			mp3player.play();
		}
		else if(play.getFile().toLowerCase().endsWith("wav")){
			wavplayer = new WavPlayer(play.getFile());
			wavplayer.play();
		}
		
	}
	
}
