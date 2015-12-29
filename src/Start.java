/**
 * Copyright (C) <2015> <Patrick Chen and Ramzi Ben Ammar> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */

import java.io.File;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.swing.JFrame;


public class Start extends JFrame {
	/**
	 * default serialization
	 */
	private static final long serialVersionUID = 1L;
	// Simple graphic interface
	Simple s = new Simple(this);

	/**
	 * Constructor to build the begining
	 */
	public Start() {
		music("res/main.mid", 100);
		setTitle("Welcome sir");
		setResizable(false);
		setSize(820, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setContentPane(s);
		setVisible(true);
		new Thread(new Avance()).start();
	}

	// Sound manager
	Sequencer sequencer;
	Sequence sequence;

	/**
	 * Song manager
	 * 
	 * @param s
	 * @param repet
	 */
	public void music(String s, int repet) {
		// si il y a deja du son on le ferme
		if (sequencer != null)
			sequencer.stop();
		try {
			sequence = MidiSystem.getSequence(new File(s));
			sequencer = MidiSystem.getSequencer();
			sequencer.open();
			sequencer.setSequence(sequence);
			sequencer.setLoopCount(repet);
			sequencer.start();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// Animation at the begining
	class Avance implements Runnable {
		public void run() {
			for (int i = 0; i < 100; i++) {
				s.cory -= 10;
				s.repaint();
				s.pause(600);
			}
		}
	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		Start tw = new Start();
		tw.setOpacity(0.85f);
		tw.setVisible(true);
	}
}
