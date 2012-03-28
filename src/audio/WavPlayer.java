package audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/*
 * http://forum.codecall.net/java-tutorials/31299-playing-simple-sampled-audio-java.html
 * http://docs.oracle.com/javase/tutorial/sound/playing.html
 * 
 */

public class WavPlayer {
    private String filename;
    private Clip clip; 
    private AudioInputStream audio;

    // constructor that takes the name of a WAV file
    public WavPlayer(String filename) {
        this.filename = filename;
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
        	audio = AudioSystem.getAudioInputStream(new File(filename));
        	clip = AudioSystem.getClip();
        }
        catch (Exception e) {
            System.out.println("Problem playing file " + filename);
            System.out.println(e);
        }

        // run in new thread to play in background
        new Thread() {
            public void run() {
                try { 
                    clip.open(audio);
                    clip.start();
                }
                catch (Exception e) { System.out.println(e); }
            }
        }.start();
    }

    
    public static void main(String args[]) {
    	WavPlayer player = new WavPlayer("C:\\WINDOWS\\Media\\Inicialização do Windows XP.wav");
    	//MidiPlayer player = new MidiPlayer("C:\\WINDOWS\\Media\\town.mid");
    	player.play();
        for(int i=0;i<10000000; i++) {
        	System.out.println(i);
        }
    }
    
}

