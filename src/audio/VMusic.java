package audio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;

import audio.jmikmod.MikModApp;
import audio.gme.gme;

public class VMusic implements Runnable {

	private static URL play;
	private int volume;
	Thread mTest = null;
	
	static MikModApp modPlayer;
	static Mp3Player mp3player;
	static gme vgmPlayer;
	static WavPlayer wavplayer;
	
	public VMusic() { 
		this.volume = 100;
	}
	public VMusic(int volume) { 
		this.volume = volume;
	}
	
	public int getVolume() {
		return this.volume;
	}
	
	public synchronized void start(URL url)
	{
		this.setPlay(url);
		if (mTest == null)
		{
			mTest = new Thread(this);
		}
		mTest.start();
	}
	
	public synchronized boolean stop() {
		if(vgmPlayer != null)
			vgmPlayer.stopFile();
		
		if(modPlayer != null)
			modPlayer.stop();
		
		if(mp3player != null)
			mp3player.close();
		
		//mTest.interrupt();
		return true;
	}
	
	public synchronized void run() {
		if(getPlay() == null || getPlay().getFile() == null) {
			System.err.println("No file to play.");
			return;
		}
		String extension = getPlay().getFile().substring(getPlay().getFile().length()-3);
		
		if(extension.equalsIgnoreCase("vgm") || extension.equalsIgnoreCase("vgz")) {
			vgmPlayer = new gme();
			System.out.println(vgmPlayer);
			double dv = (double)volume / 100;
			vgmPlayer.playSimple(getPlay(), dv); //"file:///" + play);
		}
		else if(extension.equalsIgnoreCase("mp3")){
			mp3player = new Mp3Player(getPlay(), volume);
			mp3player.play();
		}
		else if(extension.equalsIgnoreCase("wav")){
			wavplayer = new WavPlayer(getPlay(), volume);
			wavplayer.play();
		}
		else if (extension.equalsIgnoreCase("mod") || 
				extension.equalsIgnoreCase("s3m") || 
				extension.equalsIgnoreCase(".xm") || 
				extension.equalsIgnoreCase(".it"))
		{
			modPlayer = new MikModApp();
			try {
				
				// TODO: Still necessary?
				// Rafael: JAR files can't be read with RandomAccessFile. So we
				// need to copy the music file to a temporary file, in order to 
				// MikMod app read it like a RandomAccessFile.
				/*if(play.getProtocol().equals("jar")) {
					File tempFile = File.createTempFile("temp" + (int)(Math.random()*10), extension);
					FileOutputStream fos = new FileOutputStream(tempFile);
					InputStream openStream = play.openStream();
					
					int b = openStream.read();
					while(b != -1) {
						fos.write(b);
						b = openStream.read();
					}
					openStream.close();
					fos.flush();
					fos.close();
					
					//Get temporary file path
		    		String absolutePath = tempFile.getAbsolutePath();
					
					modPlayer.my_argv = new String[]{" -r ", absolutePath};
				}
				else {*/
					modPlayer.my_argv = new String[]{" -r ", " auto"};//, play.getFile()};
					modPlayer.url = getPlay();
					// TODO Volume?
				//}
			} catch (Exception e) {
				e.printStackTrace();
			} 
			modPlayer.init();
			modPlayer.start();
		}
		
		
	}

	public static URL getPlay() {
		return play;
	}

	public static void setPlay(URL play) {
		VMusic.play = play;
	}

	public void setVolume(int v) {
		if(vgmPlayer != null) {
			double dv = (double)v / 100;
			System.out.println("VGM volume changed to:" + dv);
			vgmPlayer.setVolume(dv);
		}
		
	}
	
}
