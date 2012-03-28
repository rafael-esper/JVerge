package audio;

import javax.sound.midi.*;
import javax.swing.JOptionPane;
import java.net.URL;

class PlayMidi {

    public static void main(String[] args) throws Exception {
        URL url = new URL("file:/C:\\WINDOWS\\Media\\town.mid");

        Sequence sequence = MidiSystem.getSequence(url);
        Sequencer sequencer = MidiSystem.getSequencer(false);

        sequencer.open();
        sequencer.setSequence(sequence);

        sequencer.start();
        JOptionPane.showMessageDialog(null, "Everlasting Love");
    }
}
