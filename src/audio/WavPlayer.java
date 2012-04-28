package audio;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/*
 * http://forum.codecall.net/java-tutorials/31299-playing-simple-sampled-audio-java.html
 * http://docs.oracle.com/javase/tutorial/sound/playing.html
 * 
 */

public class WavPlayer {
    private URL url;
    private Clip clip; 
    private AudioInputStream audio;
    private float volume;

    // constructor that takes the name of a WAV file
    public WavPlayer(URL url, float volume) {
        this.url = url;
        this.volume = volume;
    }

    public void close() { 
    	
    	if(clip.isActive()) {
    		clip.stop();
    		clip.close();
    	}
    	
    	if (audio != null)
		try {
			audio.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} }

    // play the WAV/MIDI file to the sound card
    public void play() {
        try {
        	audio = AudioSystem.getAudioInputStream(url.openStream());
        	clip = AudioSystem.getClip();
        }
        catch (Exception e) {
            System.out.println("Problem playing file " + url);
            System.out.println(e);
        }

        // run in new thread to play in background
        new Thread() {
            public void run() {
                try { 
                    clip.open(audio);
                    
                    //See http://docs.oracle.com/javase/1.5.0/docs/api/javax/sound/sampled/FloatControl.Type.html#MASTER_GAIN
                    float db = (float)(Math.log(volume/100)/Math.log(10.0)*20.0);
                    FloatControl gainControl = 
                    	    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    	gainControl.setValue(db); // range -80.0 to 6.0206                    
                    
                    // Play the sound	
                    clip.start();
                }
                catch (Exception e) { System.out.println(e); }
            }
        }.start();
    }
    
}

