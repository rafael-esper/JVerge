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

	static URL play;
	Thread mTest = null;
	
	static MikModApp modPlayer;
	static Mp3Player mp3player;
	static gme vgmPlayer;
	static WavPlayer wavplayer;
	
	public synchronized void start(URL url)
	{
		this.play = url;
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
		if(play == null || play.getFile() == null) {
			System.err.println("No file to play.");
			return;
		}
		String extension = play.getFile().substring(play.getFile().length()-3);
		
		if(extension.equalsIgnoreCase("vgm") || extension.equalsIgnoreCase("vgz")) {
			vgmPlayer = new gme();
			System.out.println(vgmPlayer);
			vgmPlayer.playSimple(play, 0.5); //"file:///" + play);
		}
		else if(extension.equalsIgnoreCase("mp3")){
			mp3player = new Mp3Player(play, 100);
			mp3player.play();
		}
		else if(extension.equalsIgnoreCase("wav")){
			wavplayer = new WavPlayer(play, 100);
			wavplayer.play();
		}
		else if (extension.equalsIgnoreCase("mod") || 
				extension.equalsIgnoreCase("s3m") || 
				extension.equalsIgnoreCase(".xm") || 
				extension.equalsIgnoreCase(".it"))
		{
			modPlayer = new MikModApp();
			try {
				
				// Rafael: JAR files can't be read with RandomAccessFile. So we
				// need to copy the music file to a temporary file, in order to 
				// MikMod app read it like a RandomAccessFile.
				if(play.getProtocol().equals("jar")) {
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
				else {
					modPlayer.my_argv = new String[]{" -r ", play.getFile()};
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
			modPlayer.init();
			modPlayer.start();
		}
		
		
	}
	
}
