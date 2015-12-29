/**
 * Copyright (C) <2015> <Patrick CHEN and Ramzi BEN AMMAR> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details. You should have received
 * a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.*;
import javax.swing.*;
import static java.awt.GraphicsDevice.WindowTranslucency.*;

public class Simple extends JPanel {
	// print text at the begining
	final String titre = "La force est de retour!\n la legende des jedi \n"
			+ "est bien reel !\n insuffler votre force \n"
			+ "pour donner vie a\n votre shell ! \n"
			+ "mais attention chaque\n choix a ses consequences !\n"
			+ "faire attention au\n cote auquel on tombe ! \n"
			+ "cliquer sur la partie de GAUCHE \npour infirmer\n que vous etes un JEDI !\n"
			+ "sinon cliquer sur la DROITE pour\n montrer votre cote obscur !\n"
			+ "Allez vous sombrer dans les\n tenebres \ncomme anakin ? \n"
			+ "ou bien garder la voie du jedi ?\nseul \nvotre force nous le dira";

	public Simple(final Start s) {
		setCursor(Cursor.getPredefinedCursor(12));
		this.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.getX() <= 400) {
					setCursor(Cursor.getPredefinedCursor(3));
					jou("res/sabre.wav");
					pause(700);
					s.dispose();
					// Determine if the GraphicsDevice supports translucency.

					GraphicsEnvironment ge = GraphicsEnvironment
							.getLocalGraphicsEnvironment();
					GraphicsDevice gd = ge.getDefaultScreenDevice();

					// If translucent windows aren't supported, exit.
					if (!gd.isWindowTranslucencySupported(TRANSLUCENT)) {
						System.err.println("Translucency is not supported");
						Fenetre tw = new Fenetre(true);
					} else {

						JFrame.setDefaultLookAndFeelDecorated(true);

						// Create the GUI on the event-dispatching thread
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								Fenetre tw = new Fenetre(true);

								// Set the window to 55% opaque (45%
								// translucent).
								tw.setOpacity(0.75f);

								// Display the window.
								tw.setVisible(true);
							}
						});
					}
					// new Fenetre(true).setOpacity(0.75f);
					s.sequencer.stop();
					jou("res/R2D2.wav");
				} else {
					setCursor(Cursor.getPredefinedCursor(3));
					jou("res/sabre.wav");
					pause(700);
					s.dispose();
					// Determine if the GraphicsDevice supports translucency.

					GraphicsEnvironment ge = GraphicsEnvironment
							.getLocalGraphicsEnvironment();
					GraphicsDevice gd = ge.getDefaultScreenDevice();

					// If translucent windows aren't supported, exit.
					if (!gd.isWindowTranslucencySupported(TRANSLUCENT)) {
						System.err.println("Translucency is not supported");
						Fenetre tw = new Fenetre(false);
					} else {

						JFrame.setDefaultLookAndFeelDecorated(true);

						// Create the GUI on the event-dispatching thread
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								Fenetre tw = new Fenetre(false);

								// Set the window to 55% opaque (45%
								// translucent).
								tw.setOpacity(0.75f);

								// Display the window.
								tw.setVisible(true);
							}
						});
					}
					// new Fenetre(false).setOpacity(0.75f);
					s.sequencer.stop();
				}
			}
		});

		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
			}

			public void mouseMoved(MouseEvent e) {

			}
		});
	}

	/**
	 * Sleep the processus a seconds
	 * 
	 * @param a
	 */
	public void pause(int a) {
		try {
			Thread.sleep(a);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void drawString(Graphics g, String text, int x, int y) {
		for (String line : text.split("\n"))
			g.drawString(line, x, y += g.getFontMetrics().getHeight());
	}

	/**
	 * make sound
	 * 
	 * @param str
	 */
	public void jou(String str) {
		File f = new File(str);
		try {

			AudioInputStream stream;
			AudioFormat format;
			DataLine.Info info;
			Clip clip;

			stream = AudioSystem.getAudioInputStream(f);
			format = stream.getFormat();
			info = new DataLine.Info(Clip.class, format);
			clip = (Clip) AudioSystem.getLine(info);
			clip.open(stream);
			clip.start();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	int cory = 400;

	/**
	 * print images during the begining
	 */
	public void paintComponent(Graphics g) {

		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 800, 500);

		ImageIcon i1 = new ImageIcon("res/a.jpg");
		Image i11 = i1.getImage();
		g.drawImage(i11, 0, 0, 400, 500, this);

		ImageIcon i2 = new ImageIcon("res/v.jpg");
		Image i22 = i2.getImage();
		g.drawImage(i22, 400, 0, 400, 500, this);

		g.setColor(Color.BLUE);
		g.setFont(new Font("Arial", Font.BOLD, 25));
		g.drawString("de la FORCE voulu", 400, 50);
		g.setColor(Color.RED);
		g.drawString("Choisir le cote ", 190, 50);
		g.setColor(Color.YELLOW);
		drawString(g, titre, 230, cory);
	}
}
