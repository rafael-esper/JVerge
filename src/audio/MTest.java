package audio;

import java.net.MalformedURLException;
import java.net.URL;

import audio.jmikmod.MikModApp;
import audio.gme.gme;

public class MTest implements Runnable {

	static URL play;
	Thread mTest = null;
	
	static MikModApp modPlayer;
	static Mp3Player mp3player;
	static gme vgmPlayer;
	static WavPlayer wavplayer;
	
	public static void main(String args[]) {
		
		//play = "C:\\RBP\\FRO\\Musica de fundo.mp3";
		//play = C:\\Rbp\\pessoal\\verge\\vgm\\PS IV Vgm\\02 Motavia Town.vgm";
		try {
			play = new URL("file:/C:\\Rbp\\pessoal\\verge\\phantasy.mod");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MTest m = new MTest();
		m.start(play);
		
	}
	
	public void start(URL url)
	{
		this.play = url;
		if (mTest == null)
		{
			mTest = new Thread(this);
		}
		mTest.start();
	}
	
	public boolean stop() {
		if(vgmPlayer != null)
			vgmPlayer.stopFile();
		
		if(modPlayer != null)
			modPlayer.stop();
		
		if(mp3player != null)
			mp3player.close();
		
		//mTest.interrupt();
		return true;
	}
	
	public void run() {
		if(play == null || play.getFile() == null) {
			System.err.println("No file to play.");
			return;
		}
		
		if(play.getFile().endsWith("vgm") || play.getFile().endsWith("vgz")) {
			vgmPlayer = new gme();
			System.out.println(vgmPlayer);
			vgmPlayer.playSimple("file://" + play.getFile(), 0.5); //"file:///" + play);
		}
		else if(play.getFile().endsWith("mp3") || play.getFile().endsWith("ogg")){
			mp3player = new Mp3Player(play.getFile());
			mp3player.play();
		}
		else if(play.getFile().endsWith("wav")){
			wavplayer = new WavPlayer(play.getFile());
			wavplayer.play();
		}
		else {
			modPlayer = new MikModApp();
			modPlayer.my_argv = new String[]{" -r ", play.getFile()}; 
			modPlayer.init();
			modPlayer.start();
		}
		
		
	}
	
}
