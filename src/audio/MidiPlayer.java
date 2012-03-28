package audio;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/*
 * http://stackoverflow.com/tags/javasound/info
 * 
 */

public class MidiPlayer {
    private URL url;
    private Sequence sequence;
    private Sequencer sequencer;

    // constructor that takes the name of an MP3 file
    public MidiPlayer(URL url) {
        this.url = url;
    }

    public void close() { 
    	
    	if(sequencer != null && sequencer.isOpen()) {
    		try {
    			sequencer.close();
    		}
    		catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    }

    // play the MIDI file to the sound card
    public void play() {
        try {
       	 
        	sequence = MidiSystem.getSequence(url);
            sequencer = MidiSystem.getSequencer(false);
        	
        	sequencer.open();
            sequencer.setSequence(sequence);
        }
        catch (Exception e) {
            System.out.println("Problem playing file " + url);
            System.out.println(e);
        }

        // run in new thread to play in background
        new Thread() {
            public void run() {
                try { 
                    sequencer.start();
                }
                catch (Exception e) { System.out.println(e); }
            }
        }.start();
    }

    
    public static void main(String args[]) throws MalformedURLException {
    	MidiPlayer player = new MidiPlayer(new URL("file:/C:\\WINDOWS\\Media\\town.mid"));
    	player.play();
        for(int i=0;i<10000000; i++) {
        	System.out.println(i);
        }
    }
    
}

