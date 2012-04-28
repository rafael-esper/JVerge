package audio;

import java.net.URL;

public class VSound implements Runnable {

	static URL play;
	static float volume;
	Thread sTest = null;
	
	static Mp3Player mp3player;
	static WavPlayer wavplayer;
	
	public void start(URL url, float volume)
	{
		VSound.volume = volume;
		VSound.play = url;
		if (sTest == null)
		{
			sTest = new Thread(this);
		}
		sTest.start();
	}
	
	public void run() {
		if(play == null || play.getFile() == null) {
			System.err.println("No file to play.");
			return;
		}
		
		if(play.getFile().toLowerCase().endsWith("mp3")){
			mp3player = new Mp3Player(play, volume);
			mp3player.play();
		}
		else if(play.getFile().toLowerCase().endsWith("wav")){
			wavplayer = new WavPlayer(play, volume);
			wavplayer.play();
		}
		
	}
	
}
