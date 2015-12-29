/**
 * Copyright (C) <2015>  <Patrick Chen and Ramzi Ben Ammar>
 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.*;
import javax.swing.*;
import static java.awt.GraphicsDevice.WindowTranslucency.*;

public class Fenetre extends JFrame {
	/**
	 * default serialVersion
	 */
	private static final long serialVersionUID = 1L;
	// Textfield for write commands
	JTextField jtf = new JTextField();
	// TextArea to print commands results
	JTextArea texte = new JTextArea("");
	// interface custum different than matrix
	JAnimatedIcon[] ai = new JAnimatedIcon[20];
	JAnimatedIcon[] ai2 = new JAnimatedIcon[20];
	// the container object ,it contains the two differents fields
	JPanel container = new JPanel();
	String print = "";
	// the current path use
	String path = "/." + System.getProperty("user.dir");// "/./home/netbook/workspace";
	// list all respestories and files,it is usefull for the autocompletion
	ArrayList<File> courant = new ArrayList<File>();

	// don't forget words you need for autocompletion
	ArrayList<String> retenu = new ArrayList<String>();
	// parse the shell command
	ArrayList<String> lis = new ArrayList<String>();
	// Gestion de processus
	HashMap<Integer, Thread> listeprocessus = new HashMap<Integer, Thread>();
	// Listage de processus pour la commande Kill
	Thread[] processus = new Thread[100];
	// use for the command date
	private GregorianCalendar calendar;
	// use for the date command
	private java.util.Date time;
	// redirection case
	int redirection = 0;
	String redirect = "";

	boolean force;
	// Stop a current processus change gele
	JMenuBar bar = new JMenuBar();
	JMenu menu = new JMenu("Parameters");
	JMenuItem item1 = new JMenuItem("Stop Processus");
	JMenuItem item2 = new JMenuItem("");
	boolean haveI = true;

	/**
	 * Choisir le cote de le bon cote de la force
	 * 
	 * @param force
	 */
	public Fenetre(boolean force) {
		this.force = force;
		menu.add(item1);
		bar.add(menu);
		setJMenuBar(bar);
		if (force)
			setTitle("Jedi way R2D2");
		else
			setTitle("obscur forces");
		setSize(800, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		Font police = new Font("Arial", Font.BOLD, 20);
		texte.setFont(police);
		texte.setBackground(Color.black);
		if (force)
			texte.setForeground(new Color(20, 100, 200));
		else
			texte.setForeground(Color.RED);
		container.setLayout(new BorderLayout());
		container.add(new JScrollPane(texte), BorderLayout.CENTER);
		JPanel north = new JPanel();
		jtf.setPreferredSize(new Dimension(780, 30));
		jtf.addKeyListener(new Clavier());
		jtf.setBackground(Color.BLACK);
		jtf.setCaretColor(Color.GREEN);
		if (force)
			jtf.setForeground(new Color(20, 100, 200));
		else
			jtf.setForeground(Color.RED);
		north.add(jtf);
		north.setBackground(Color.BLACK);
		jtf.setFont(police);
		container.add(north, BorderLayout.NORTH);
		setContentPane(container);
		if (!force)
			ai[2] = new JAnimatedIcon("res/vador.gif", 80);
		else
			ai[2] = new JAnimatedIcon("res/fond2.gif", 80);
		ai2[2] = new JAnimatedIcon(false, ai[2], 600, 400);
		texte.add(ai2[2]);
		ai2[2].setLocation(470, 0);
		// ai2[2].setLocation(370, 0);
		if (!force)
			ai[6] = new JAnimatedIcon("res/light.gif", 80);
		else
			ai[6] = new JAnimatedIcon("res/holo.gif", 80);
		ai2[6] = new JAnimatedIcon(false, ai[6], 300, 300);
		texte.add(ai2[6]);
		ai2[6].setLocation(500, 650);

		ai[1] = new JAnimatedIcon("res/jarvis.gif", 80);
		ai2[1] = new JAnimatedIcon(false, ai[1], 20, 20);
		jtf.add(ai2[1]);
		ai2[1].setLocation(750, 5);
		// only if the force is with a jedi
		if (force) {
			ai[13] = new JAnimatedIcon("res/r2.gif", 80);
			ai2[13] = new JAnimatedIcon(false, ai[13], 80, 80);
			texte.add(ai2[13]);
			ai2[13].setLocation(600, 380);
		}
		setVisible(true);
		if (force)
			ai[3] = new JAnimatedIcon("res/holo.gif", 80);
		else
			ai[3] = new JAnimatedIcon("res/light.gif", 80);
		ai2[3] = new JAnimatedIcon(false, ai[3], 60, 50);
		texte.add(ai2[3]);
		ai2[3].setLocation(720, 390);

		texte.setCaretColor(Color.GREEN);
		texte.setEditable(false);

		item1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					for (Integer mapKey : listeprocessus.keySet()) {
						if (listeprocessus.get(mapKey).getName()
								.equals("music")) {
							sequencer.stop();
						}
						if (listeprocessus.get(mapKey) != null) {
							listeprocessus.get(mapKey).stop();
							listeprocessus.remove(mapKey);
						}
					}
				} catch (Exception exp) {
					texte.setText("ctrl+d");
				}
				if (processus[0] != null)
					processus[0].stop();
				gele = false;
				jtf.setText("");
			}
		});

		item1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,
				KeyEvent.CTRL_MASK));

		if (!force) {
			int pid = (int) (Math.random() * 3000);
			listeprocessus.put(pid, new Thread(new Music("res/vador.mid", 100),
					"music"));
			listeprocessus.get(pid).start();

		} else {
			int pid = (int) (Math.random() * 3000);
			listeprocessus.put(pid, new Thread(
					new Music("res/anaking.mid", 100), "music"));
			listeprocessus.get(pid).start();
		}

		texte.setText("Pour enlever les images de fond\n taper la commande :config noImage \n(pour remettre :config image)");
	}

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

	/**
	 * parse the jtf expression
	 * 
	 * @param test
	 */
	public void listage(String test) {
		String tmp = "";
		for (int i = 0; i < test.length(); i++) {
			if (test.charAt(i) == ' ') {
				if (i >= 1) {
					if (test.charAt(i - 1) != ' ') {
						lis.add(tmp);
						tmp = "";
					}
				}
			} else {
				tmp += test.charAt(i);
			}
		}
		if (!tmp.equals(""))
			lis.add(tmp);
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

	/**
	 * throw the LS thread boolean ls is used for the processus if the list of
	 * respestories is too large you can kill a ls processus !
	 * 
	 * @param pid
	 * @param chemin
	 */
	boolean ls = false;

	public void ls(int pid, String chemin, boolean red, String fic) {
		processus[0] = new Thread(new LS(pid, chemin, red, fic));
		processus[0].start();
	}

	int choix = 0;

	// met a jour la liste des elements
	public void courant() {
		courant = new ArrayList<File>();
		File f = new File(path);
		String[] paths;
		paths = f.list();
		for (File path : f.listFiles()) {
			courant.add(path);
		}
		listage(jtf.getText());
		for (int i = 0; i < courant.size(); i++) {
			if (courant.get(i).getName().length() >= lis.get(1).length()) {
				if ((lis.get(1)).equals(courant.get(i).getName()
						.substring(0, lis.get(1).length()))) {

					retenu.add(courant.get(i).getName());
				}
			}
		}
		if (choix < retenu.size()) {
			jtf.setText(lis.get(0) + " " + retenu.get(choix));
			choix++;
		} else
			choix = 0;
		lis = new ArrayList<String>();
		retenu = new ArrayList<String>();
	}

	/**
	 * Thread for LS command(classic usage ubuntu)
	 */
	class LS implements Runnable {
		int pid;
		String chemin = "";
		String name = "ls";
		String fic = "";
		boolean redirect = false;

		public LS(int pid, String chemin, boolean redirect, String fic) {
			this.pid = pid;
			this.chemin = chemin;
			this.redirect = redirect;
			this.fic = fic;
		}

		public void run() {
			// init();
			gele = true;
			File f = null;
			String[] paths;
			String pp = "\npid : " + pid + "\n\n";
			try {
				f = new File(chemin);

				paths = f.list();
				for (File path : f.listFiles()) {
					if (path.isDirectory())
						pp = pp + "\n" + "/" + path.getName();
					else
						pp = pp + "\n" + path.getName();
					texte.setText(pp);
					// pause(5);
				}
			} catch (Exception e) {
				// if any error occurs
				e.printStackTrace();
			}
			if (!redirect)
				texte.setText(pp);
			else
				ecrire(fic, pp);

			// else creer le fichier
			texte.repaint();
			gele = false;
		}
	}

	/**
	 * 
	 * Thread use for Find calls we use class to do function
	 */
	class Find implements Runnable {
		String chemin = "";
		String mode = "";
		String regrex = "";
		int pid;
		Matcher m;
		Pattern p;
		// String use for print result
		String pp;
		boolean es = false;

		public Find(String chemin, String mode, String regrex, int pid) {
			pp = "";
			this.chemin = chemin;
			this.mode = mode;

			// expression reguliere
			this.regrex = regrex;
			this.pid = pid;
			// insensitive case
			if (mode.equals("-iname"))
				p = Pattern.compile(regrex, Pattern.CASE_INSENSITIVE);
			if (mode.equals("-name"))
				p = Pattern.compile(regrex);
		}

		public void find(String chemin) {
			File fichier = new File(chemin);
			String list[];
			if (fichier.isDirectory()) {
				list = fichier.list();
				try {
					for (int i = 0; i < list.length; i++) {
						m = p.matcher(list[i]);
						if (m.matches()) {
							pp = pp + "\n" + chemin + "/" + list[i];
						}
						find(chemin + "/" + list[i]);
					}
				} catch (Exception exp) {
					texte.setText("Search ... Just wait please");
				}
			}
		}

		public void run() {
			if (mode.equals("-iname") || mode.equals("-name")) {
				find(chemin);
				if (redirection == 0)
					texte.setText(pp);
				else if (redirection == 1)
					ecrire(redirect, pp);
				else if (redirection == 2) {
					new File(path + "/" + redirect).delete();
					ecrire(redirect, pp);
				}

				redirection = 0;
			}
		}
	}

	/**
	 * Sleep command to sleep the current process with
	 */
	class Sleep implements Runnable {
		int num;
		boolean red;
		int pid;

		public Sleep(int num, boolean red, int pid) {
			this.num = num;
			this.red = red;
			this.pid = pid;
		}

		public void run() {
			if (red) {
				gele = true;
				pause(num * 1000);
				gele = false;
			} else {
				pause(num * 1000);

			}
			listeprocessus.remove(this.pid);

		}
	}

	/**
	 * Thread representing of cd command
	 */
	class CD implements Runnable {
		int pid;
		String name = "cd";

		public CD(int pid) {
			this.pid = pid;
			texte.setText("pid cd : " + pid);
		}

		public void run() {
			if (lis.size() == 1) {
				path = "/." + System.getProperty("user.home");
				setTitle(path);

			} else if (path.equals("/.")
					&& lis.get(1).substring(0, 2).equals("..")
					|| (lis.get(1).length() >= 2 && lis.get(1).substring(0, 2)
							.equals("//"))) {

			} else if (lis.get(1).substring(0, 1).equals("/")) {
				if ((new File(lis.get(1))).isDirectory()
						&& (new File(lis.get(1))).exists()
						&& !lis.get(1).equals(".")) {
					path = "/." + lis.get(1);
					setTitle(path);
				} else {
					texte.setText("Vous passez par le chemin absolu,mais le dossier n existe pas !");
				}
			} else if (lis.size() >= 2 && lis.get(1).length() >= 2) {

				if (lis.get(1).substring(0, 2).equals("..")) {
					nbrecul(lis.get(1));
					setTitle(path);
				} else {
					if ((new File(path + "/" + lis.get(1))).isDirectory()
							&& (new File(path + "/" + lis.get(1))).exists()
							&& !lis.get(1).equals(".")) {

						path += "/" + lis.get(1);
						setTitle(path);
					} else {
						texte.setText("Ce n'est pas un dossier ,impossible d'entrer");
					}
				}
			}
		}
	}

	/**
	 * check if the parameter is numerical or not
	 * 
	 * @param str
	 * @return bool
	 */
	public static boolean isNumeric(String str) {
		return str.matches("[+-]?\\d*(\\.\\d+)?");
	}

	boolean mvim = false;

	class Mvim implements Runnable {
		ArrayList<String> lis = new ArrayList<String>();

		public Mvim(ArrayList<String> lis) {
			this.lis = lis;
		}

		public void run() {
			mvim = true;
			texte.setEditable(true);

			for (int i = 1; i < lis.size(); i++) {

				if (!(new File(path + "/" + lis.get(i))).isDirectory())
					cat(path + "/" + lis.get(i));
				else
					texte.setText("Error !ceci est un dossier !");
			}
		}
	}

	/**
	 * count with a format ,for example double ,float or integer
	 */
	class Compte implements Runnable {
		int pid;
		int combien;
		String format = "%f\n";
		String name = "compte";

		public Compte(int pid, int combien, String format) {
			this.pid = pid;
			this.combien = combien;
			this.format = format;
		}

		public void run() {

			// change le format du decompte
			if (format.equals("%d")) {
				for (int k = 0; k < combien; k++) {
					texte.setText("je compte en Int :" + k);
					pause(1000);
				}
			} else if (format.equals("%f")) {
				for (double k = 0.0; k < combien; k++) {
					texte.setText("je compte en double :" + k);
					pause(1000);
				}
			} else {
				for (int k = 0; k < combien; k++) {
					texte.setText("je compte en Int :" + k);
					pause(1000);
				}
			}
			gele = false;

			listeprocessus.remove(this.pid);
		}
	}

	// retourne l indice ou lon a un slash en partant de la fin
	public int indiceCoupe() {
		for (int i = path.length() - 1; i > 0; i--) {
			if (path.charAt(i) == '/')
				return i;
		}
		// le cas echeant
		return 2;
	}

	/**
	 * grep method use JAVA compile for regrex(change into Thread after ..)
	 */
	public void grep() {
		String pp = "";
		String tmp = "";
		// freeze the shell if one arg
		if (lis.size() == 1) {
			gele = true;
			texte.setText("Grep sans arguments ! \nfaites crtl +D pour revenir au shell");
		} else if (lis.size() >= 3) {

			if (!new File(lis.get(2)).isFile()) {
				texte.setText("it's not a file");
				return;
			}

			Matcher m;
			Pattern p = Pattern.compile(lis.get(1));
			int length;
			if (lis.contains(">") || lis.contains(">>")) {
				length = lis.size() - 2;
			} else
				length = lis.size();
			for (int i = 2; i < length; i++) {
				String contenuF = affiche(lis.get(i));

				for (int ii = 0; ii < contenuF.length(); ii++) {
					if (contenuF.charAt(ii) == '\n') {
						m = p.matcher(tmp);
						if (m.find()) {
							pp += tmp + "\n";
						}
						tmp = "";
					} else {
						tmp += contenuF.charAt(ii);
					}
				}
				m = p.matcher(tmp);
				if (m.find()) {
					pp += tmp + "\n";
				}
			}
			// print the grep result
			texte.setText("	grep results: \n\n " + pp);
			if (lis.get(lis.size() - 2).equals(">>")) {
				ecrire(lis.get(lis.size() - 1), pp);
			}
			if (lis.get(lis.size() - 2).equals(">")) {
				new File(path + "/" + lis.get(lis.size() - 1)).delete();
				ecrire(lis.get(lis.size() - 1), pp);
			}

		}
	}

	/**
	 * Sed method ,apply it .
	 */
	public void sed() {
		String pp = ""; // print result

		String tmp = "";
		ArrayList<String> minilis = new ArrayList<String>();
		if (lis.size() == 1) {
			gele = true;
			texte.setText("sed sans arguments entré standard\nlu \nfaites crtl +D pour revenir au shell");
		} else if (lis.size() >= 3) {
			if (!new File(lis.get(2)).isFile()) {
				texte.setText("it's not a file");
				return;
			}
			String contfich = affiche(lis.get(2));
			if (lis.get(1).charAt(0) == 's'
					&& (lis.get(1).charAt(1) == '/'
							|| lis.get(1).charAt(1) == ':' || lis.get(1)
							.charAt(1) == ':')) {
				// little parsing of sed expression
				for (int k = 2; k < lis.get(1).length(); k++) {
					if (lis.get(1).charAt(k) == '/'
							|| lis.get(1).charAt(k) == ':'
							|| lis.get(1).charAt(k) == ';') {
						minilis.add(tmp);
						tmp = "";
					} else
						tmp += lis.get(1).charAt(k);
				}
				minilis.add(tmp);
				tmp = "";
				String tmp2 = "";
				if (minilis.size() == 2) {
					Pattern pi = Pattern.compile(minilis.get(0));
					Matcher mi;
					for (int l = 0; l < contfich.length(); l++) {
						if (contfich.charAt(l) == '\n') {
							mi = pi.matcher(tmp);
							pp += mi.replaceFirst(minilis.get(1));
							tmp = "";
						}
						tmp += contfich.charAt(l);
					}
					texte.setText(pp);
					// redirection
					if (lis.get(lis.size() - 2).equals(">>")) {
						ecrire(lis.get(lis.size() - 1), pp);
					}
					if (lis.get(lis.size() - 2).equals(">")) {
						new File(path + "/" + lis.get(lis.size() - 1)).delete();
						ecrire(lis.get(lis.size() - 1), pp);
					}

				} else if (minilis.size() == 3
						&& minilis.get(minilis.size() - 1).equals("g")) {
					Pattern pi = Pattern.compile(minilis.get(0));
					Matcher mi = pi.matcher(contfich);
					texte.setText(mi.replaceAll(minilis.get(1)));
					// redirection
					if (lis.get(lis.size() - 2).equals(">>")) {
						ecrire(lis.get(lis.size() - 1),
								mi.replaceAll(minilis.get(1)));
					}
					if (lis.get(lis.size() - 2).equals(">")) {
						new File(path + "/" + lis.get(lis.size() - 1)).delete();
						ecrire(lis.get(lis.size() - 1),
								mi.replaceAll(minilis.get(1)));
					}

				} else {
					texte.setText("An error occured,\n we need more args");
				}
			}
		}

	}

	public void removeAl() {
		for (int i = 0; i < ai2.length; i++) {
			if (ai2[i] != null)
				texte.remove(ai2[i]);
		}
	}

	public int indiceCoupe2() {
		for (int i = aRetour.length() - 1; i > 0; i--) {
			if (aRetour.charAt(i) == '/')
				return i;
		}
		return 2;
	}

	// gestion de lesperluette
	boolean gele = false;

	class Clavier implements KeyListener {
		ArrayList<String> history = new ArrayList<String>();

		int index = 0;

		public Clavier() {
			history.add("");
		}

		// keyevent to control how many times we do a command
		KeyEvent key = new KeyEvent(jtf, 1, 1, 1, 10, ' ');

		// verifie la presence de ; dans l'expression
		public boolean verif() {
			if (jtf.getText().contains(";"))
				return true;
			return false;
		}

		ArrayList<String> liste = new ArrayList<String>();

		class LotOf implements Runnable {
			String str;

			public LotOf(String str) {
				this.str = str;
			}

			/*
			 * faire plusiseurs commandes grace au separateur ;
			 */
			public void lotOf() {
				liste = new ArrayList<String>();
				String jt = str;
				String tmp = "";
				for (int inc = 0; inc < jt.length(); inc++) {
					if (jt.charAt(inc) == ';') {
						liste.add(tmp);

						tmp = "";
					} else {
						tmp += jt.charAt(inc);
					}
				}
				if (!tmp.equals("") && !tmp.equals(";") && !tmp.contains(";")) {
					liste.add(tmp);
				}

				for (int i = 0; i < liste.size(); i++) {
					listage(liste.get(i));

					keyPressed(key);
					texte.repaint();

					if (liste.get(i).contains("find")) {
						texte.setText("Wait 3 seconds ...");
						pause(3000);
					} else
						pause(1000);

				}
			}

			public void run() {
				lotOf();
			}
		}

		/**
		 * Parse each commands(more important method )
		 */
		public void keyPressed(KeyEvent arg0) {
			
			setTitle(path);

			if (!gele) {
				// TODO Auto-generated method stub
				if (arg0.getKeyCode() == 10) {

					if (force)
						jou("res/R2D2.wav");
					else
						jou("res/sabre.wav");

					history.add(jtf.getText());
					// multi commands
					if (verif()) {
						// lotOf();
						new Thread(new LotOf(jtf.getText())).start();
					}

					listage(jtf.getText());
					if (lis.size() > 0) {
						if (lis.get(0).equals("exit")) {
							System.exit(0);
						} else if (lis.get(0).equals("pwd")) {
							texte.setText("Pwd : " + path);
							if (lis.size() == 3) {
								if (lis.get(1).equals(">>")) {
									ecrire(lis.get(2), path);
								}
								if (lis.get(1).equals(">")) {
									new File(path + "/" + lis.get(2)).delete();
									ecrire(lis.get(2), path);
								}
							}
						} else if (lis.get(0).equals("clear")) {
							print = "";
							texte.setText("");
						} else if (lis.get(0).equals("ls")) {
							if (lis.size() == 1) {
								ls((int) (Math.random() * 3000), path, false,
										"");
							} else {
								if (lis.size() == 2) {
									if (new File(pathtmp(lis.get(1))).isFile()
											|| new File(pathtmp(lis.get(1)))
													.isDirectory()) {
										ls((int) (Math.random() * 3000),
												pathtmp(lis.get(1)), false, "");
									} else {
										texte.setText("Fichier non existant");
									}
								} else if ((lis.size() >= 3)) {
									if (lis.get(lis.size() - 2).equals(">>")) {
										// redirection case
										if (new File(pathtmp(lis.get(1)))
												.isFile()
												|| new File(pathtmp(lis.get(1)))
														.isDirectory()) {
											if (lis.get(lis.size() - 2).equals(
													">>"))
												ls((int) (Math.random() * 3000),
														pathtmp(lis.get(1)),
														true,
														lis.get(lis.size() - 1));

										} else {
											texte.setText("Error type: ls");
										}
									}
								} else {
									System.out.println("nonon");
									for (int i = 1; i < lis.size(); i++) {
										print += lis.get(i) + "\n";
									}
								}
								// texte.setText(print);
								// print = "";
							}
						} else if (lis.get(0).equals("w")) {
							if (mvim = true) {
								if (lis.size() == 2) {
									if (new File(lis.get(1)).exists())
										new File(lis.get(1)).delete();
									ecrire(lis.get(1), texte.getText());
								}
							}
						} else if (lis.get(0).equals("sleep")) {
							if (lis.size() == 2) {
								if (lis.get(1).charAt(lis.get(1).length() - 1) == '&') {
									if (isNumeric(lis.get(1).substring(0,
											lis.get(1).length() - 1))) {
										int pide = (int) (Math.random() * 3000);
										listeprocessus
												.put(pide,
														new Thread(
																new Sleep(
																		Integer.parseInt(lis
																				.get(1)
																				.substring(
																						0,
																						lis.get(1)
																								.length() - 1)),
																		false,
																		pide),
																"sleep"));

										listeprocessus.get(pide).start();
										texte.setText("Shell sleep ...");
									}
								} else {
									if (isNumeric(lis.get(1))) {
										int pide = (int) (Math.random() * 3000);
										listeprocessus.put(
												pide,
												new Thread(new Sleep(Integer
														.parseInt(lis.get(1)),
														true, pide), "sleep"));
										listeprocessus.get(pide).start();
										texte.setText("Shell is sleeping wait ...");

									}
								}
							} else if (lis.size() == 3) {
								if (lis.get(2).equals("&")) {
									if (isNumeric(lis.get(1))) {
										int pide = (int) (Math.random() * 3000);
										listeprocessus.put(
												pide,
												new Thread(new Sleep(Integer
														.parseInt(lis.get(1)),
														false, pide), "sleep"));
										listeprocessus.get(pide).start();
										texte.setText("Shell is sleeping wait ...");
									}
								}
							}
						} else if (lis.get(0).equals("find")) {
							if (lis.size() == 1) {
								Thread th = new Thread(new Find(pathtmp("."),
										"-iname", "[a-z]*", 100));
								th.start();
							}
							if (lis.size() >= 4) {
								if (lis.get(lis.size() - 2).equals(">>")) {
									redirect = lis.get(lis.size() - 1);
									redirection = 1;
								}
								if (lis.get(lis.size() - 2).equals(">")) {
									redirect = lis.get(lis.size() - 1);
									redirection = 2;
								}
								Thread th = new Thread(new Find(
										pathtmp(lis.get(1)), lis.get(2),
										lis.get(3), 100));
								th.start();
							} else {
								texte.setText("Error ,find Must four arguments !");
							}

						} else if (lis.get(0).equals("cd")) {
							Thread cd = new Thread(new CD(
									(int) (Math.random() * 3000)));
							cd.start();
							pause(7);
							cd.stop();

						} else if (lis.get(0).equals("sed")) {
							sed();
						} else if (lis.get(0).equals("touch")
								&& lis.size() >= 2) {
							for (int i = 1; i < lis.size(); i++) {
								try {
									(new File(path + "/" + lis.get(i)))
											.createNewFile();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									texte.setText("Une erreure s est produite");
								}
							}
						} else if (lis.get(0).equals("grep")) {
							grep();
						} else if (lis.get(0).equals("config")
								&& lis.size() == 2) {
							if (lis.get(1).equals("image")
									|| lis.get(1).equals("Image")) {
								if (!haveI) {
									if (!force)
										ai[2] = new JAnimatedIcon(
												"res/vador.gif", 80);
									else
										ai[2] = new JAnimatedIcon(
												"res/fond2.gif", 80);
									ai2[2] = new JAnimatedIcon(false, ai[2],
											600, 400);
									texte.add(ai2[2]);
									ai2[2].setLocation(470, 0);
									// ai2[2].setLocation(370, 0);
									if (!force)
										ai[6] = new JAnimatedIcon(
												"res/light.gif", 80);
									else
										ai[6] = new JAnimatedIcon(
												"res/holo.gif", 80);
									ai2[6] = new JAnimatedIcon(false, ai[6],
											300, 300);
									texte.add(ai2[6]);
									ai2[6].setLocation(500, 650);

									if (force) {
										ai[13] = new JAnimatedIcon(
												"res/r2.gif", 80);
										ai2[13] = new JAnimatedIcon(false,
												ai[13], 80, 80);
										texte.add(ai2[13]);
										ai2[13].setLocation(600, 380);
									}

									if (force)
										ai[3] = new JAnimatedIcon(
												"res/holo.gif", 80);
									else
										ai[3] = new JAnimatedIcon(
												"res/light.gif", 80);
									ai2[3] = new JAnimatedIcon(false, ai[3],
											60, 50);
									texte.add(ai2[3]);
									ai2[3].setLocation(720, 390);
									haveI = true;
								}
							}
							if (lis.get(1).equals("noImage")
									|| lis.get(1).equals("noimage")) {
								if (haveI) {
									haveI = false;
									removeAl();
								}
							}
							texte.repaint();
						} else if (lis.get(0).equals("mkdir")
								&& lis.size() >= 2) {
							for (int i = 1; i < lis.size(); i++) {
								(new File(path + "/" + lis.get(i))).mkdir();
							}
						} else if ((lis.get(0).equals("delete")
								|| lis.get(0).equals("rmdir") || lis.get(0)
								.equals("rm")) && lis.size() >= 2) {
							for (int i = 1; i < lis.size(); i++) {
								if (new File(path + "/" + lis.get(i)).exists())
									(new File(path + "/" + lis.get(i)))
											.delete();
								else
									texte.setText("Error not found");
							}

						} else if (lis.get(0).equals("cat") && lis.size() >= 2) {
							if (lis.size() == 4) {
								if (lis.get(2).equals(">>")) {
									ecrire(lis.get(3), affiche(lis.get(1)));
								}
							} else {
								for (int i = 1; i < lis.size(); i++) {
									if (!(new File(path + "/" + lis.get(i)))
											.isDirectory())
										cat(path + "/" + lis.get(i));
									else
										texte.setText("Error !c eci est un dossier !");
									if (!(new File(path + "/" + lis.get(i)))
											.exists())
										texte.setText("File not found!");
								}
							}
						}
						// We can just write in a file
						else if (lis.get(0).equals("echo")) {
							if (lis.size() >= 4) {
								if (lis.get(2).equals(">>")) {
									ecrire(path + "/" + lis.get(3), lis.get(1));
								}
								if (lis.get(2).equals(">")) {
									new File(path + "/" + lis.get(3)).delete();
									ecrire(path + "/" + lis.get(3), lis.get(1));
								}
							}
						} else if (lis.get(0).equals("cp")) {
							if (lis.size() == 3) {
								ecrire(lis.get(2), affiche(path+"/"+lis.get(1)));
								texte.setText("copy done !");
							} else {
								texte.setText("An error occured can't copy");
							}
						} else if (lis.get(0).equals("internet")) {
							String tmpp = "";
							if (lis.size() == 2)
								tmpp = lis.get(1);
							else
								tmpp = "https://google.fr";
							Desktop desktop = null;
							java.net.URI url;
							try {
								url = new java.net.URI(tmpp);
								if (Desktop.isDesktopSupported()) {
									desktop = Desktop.getDesktop();
									desktop.browse(url);
									desktop.getClass().getEnclosingMethod();
								}
							} catch (Exception ex) {
								System.out.println(ex.getMessage());
								texte.setText(ex.getMessage());
							}
						} else if (lis.get(0).equals("mvim") && lis.size() == 2) {
							int pid = (int) (Math.random() * 3000);
							listeprocessus.put(pid, new Thread(new Mvim(lis),
									"MINIVIM"));
							listeprocessus.get(pid).start();
						} else if (lis.get(0).equals("compteJusqua")
								&& isNumeric(lis.get(1))) {
							int pid = (int) (Math.random() * 3000);
							if (lis.size() == 3) {
								if (lis.get(2).charAt(lis.get(2).length() - 1) == '&') {
									gele = false;
									listeprocessus
											.put(pid,
													new Thread(
															new Compte(
																	pid,
																	(Integer.parseInt(lis
																			.get(1)) + 1),
																	lis.get(2)
																			.substring(
																					0,
																					lis.get(2)
																							.length() - 1)),
															"compte"));
									listeprocessus.get(pid).start();
								} else {
									gele = true;
									listeprocessus.put(
											pid,
											new Thread(new Compte(pid, (Integer
													.parseInt(lis.get(1)) + 1),
													lis.get(2)), "compte"));
									listeprocessus.get(pid).start();
								}
							} else if (lis.size() == 2) {
								if (lis.get(1).charAt(lis.get(1).length() - 1) == '&') {
									gele = false;
								} else
									gele = true;
								listeprocessus.put(pid, new Thread(new Compte(
										pid,
										(Integer.parseInt(lis.get(1)) + 1),
										"%d\n"), "compte"));
								listeprocessus.get(pid).start();
							} else if (lis.size() == 4) {
								if (lis.get(3).equals("&")) {
									gele = false;
								} else {
									gele = true;
								}
								listeprocessus.put(
										pid,
										new Thread(new Compte(pid, (Integer
												.parseInt(lis.get(1)) + 1), lis
												.get(2)), "compte"));
								listeprocessus.get(pid).start();
							}

						} else if (lis.get(0).equals("ps")) {
							String tmp = "";
							for (Integer mapKey : listeprocessus.keySet()) {
								tmp += "pid = " + mapKey
										+ " Le processus est : "
										+ listeprocessus.get(mapKey).getName()
										+ "\n";
							}
							texte.setText(tmp);
							if (lis.size() == 3) {
								if (lis.get(1).equals(">")) {
									if (new File(path + "/" + lis.get(2))
											.exists())
										new File(path + "/" + lis.get(2))
												.delete();
									ecrire(lis.get(2), tmp);
								}
								if (lis.get(1).equals(">>")) {
									ecrire(lis.get(2), tmp);
								}
							}
						} else if (lis.get(0).equals("man")) {
							texte.setText("~Infos~\n\n-cd path\n\n-ls path \n\n-cat path\n\n-touch path\n\n-mkdir path \n\n-rm |rmdir|delete path \n\n-pwd\n\n-echo * >path\n"
									+ "\n-internet https://*\n\n-exit\n\n-sed <format> [<fichier>]\n\n-sleep number\n\n-grep <expr. reg.> [ <fich. 1> [ <fich.2> [ ... ] ] ]\n\n-clear\n\n-cp arg0 arg1 only files(not directories)\n\n-compteJusqua number %d ou %f\n\n-date sans arguments -> year month day ,5 arguments \n"
									+ "possible d h m M y\nexemple : date +yMdh (+ au debut)\n\n-ps : liste tous les processus avec un id et le nom \n\n-mvim path = mini vim qui permet dediter un fichier txt\n dans le terminal\n\n-w arg0 =enregistreles modifications faites avec mvim dans le fichier arg0.\n\n-killall name\n\n-find path -iname|-name <expr reg java> \n\n-kill number -> detruit le processus en cours\n\nDefault commands on ubuntu if commands not found");
						} else if (lis.get(0).equals("kill")) {
							if (lis.size() == 1) {
								texte.setText("Need more than one arg");
							} else if (lis.size() >= 2) {
								for (int i = 1; i < lis.size(); i++) {
									if (isNumeric(lis.get(i))) {
										if (listeprocessus.containsKey(Integer
												.parseInt(lis.get(i)))) {
											if (listeprocessus
													.get(Integer.parseInt(lis
															.get(i))).getName()
													.equals("music")) {
												sequencer.stop();
											}
											if (listeprocessus
													.get(Integer.parseInt(lis
															.get(i))).getName()
													.equals("MINIVIM")) {
												texte.setEditable(false);
											}

											listeprocessus
													.get(Integer.parseInt(lis
															.get(i))).stop();
											listeprocessus.remove(Integer
													.parseInt(lis.get(i)));

											texte.setText("Le processus a ete tue!");
										}
									} else {
										texte.setText("arg is not a number ! ");
									}
								}
							}
						} else if (lis.get(0).equals("killall")) {
							if (lis.size() == 1) {
								texte.setText("need more arguments");
							} else if (lis.size() >= 2) {
								try {
									for (int i = 1; i < lis.size(); i++) {
										for (Integer mapKey : listeprocessus
												.keySet()) {
											if (listeprocessus.get(mapKey)
													.getName()
													.equals(lis.get(i))) {
												if (lis.get(i).equals("music")) {
													sequencer.stop();
												}
												if (lis.get(i)
														.equals("MINIVIM")) {
													texte.setEditable(false);
												}
												listeprocessus.get(mapKey)
														.stop();
												listeprocessus.remove(mapKey);
												texte.setText("Le processus a  ete tue!");
											}
										}
									}
								} catch (Exception exp) {
									texte.setText("erasing processus");
								}
							}

						} else if (lis.get(0).equals("date")) {
							// Use a simple synthaxe +Args as a String
							String formattedDate = "";
							if (lis.size() == 1) {
								calendar = (GregorianCalendar) GregorianCalendar
										.getInstance();
								time = calendar.getTime();
								SimpleDateFormat formatter = new SimpleDateFormat(
										"yyyy MMM EEE");
								formattedDate = formatter.format(time);
								texte.setText(formattedDate);
								if (lis.size() >= 3
										&& lis.get(lis.size() - 2).equals(">>")) {
									ecrire(lis.get(lis.size() - 1),
											formattedDate);
								}
								if (lis.size() >= 3
										&& lis.get(lis.size() - 2).equals(">")) {
									new File(path + "/"
											+ lis.get(lis.size() - 1)).delete();
									ecrire(lis.get(lis.size() - 1),
											formattedDate);
								}
							} else if (lis.size() >= 2
									&& lis.get(1).charAt(0) == '+') {
								String tmp = "";
								String tmp2 = "";
								for (int i = 0; i < lis.get(1).length(); i++) {
									char tmpo = lis.get(1).charAt(i);
									switch (tmpo) {
									case 'd':
										tmp2 = " EEE ";
										break;
									case 'h':
										tmp2 = " HH heures";
										break;
									case 'm':
										tmp2 = " MMM ";
										break;
									case 'M':
										tmp2 = " mm  ";
										break;
									case 'y':
										tmp2 = " yyyy ";
										break;
									}
									tmp += tmp2;
									tmp2 = "";
								}
								calendar = (GregorianCalendar) GregorianCalendar
										.getInstance();
								time = calendar.getTime();
								SimpleDateFormat formatter = new SimpleDateFormat(
										tmp);
								formattedDate = formatter.format(time);

								texte.setText(formattedDate);

							}
							if (lis.size() >= 3
									&& lis.get(lis.size() - 2).equals(">>")) {
								ecrire(lis.get(lis.size() - 1), formattedDate);
							}
							if (lis.size() >= 3
									&& lis.get(lis.size() - 2).equals(">")) {
								new File(path + "/" + lis.get(lis.size() - 1))
										.delete();
								ecrire(lis.get(lis.size() - 1), formattedDate);
							}

						} else {
							// then execute default Ubuntu commands if the
							// commands
							// is not found ,useless if on windows
							exe(lis);
						}
					}

					jtf.setText("");

					index = history.size();
					// texte.setText(print);
					lis = new ArrayList<String>();

				}
			}

			// Touche F1 pour l'auto completion
			if (arg0.getKeyCode() == 112) {
				courant();
			}
			/**
			 * don't forget what you written
			 */
			if (arg0.getKeyCode() == KeyEvent.VK_DOWN) {
				if (index + 1 <= history.size() - 1) {
					index++;
					jtf.setText(history.get(index));
				}
			}
			if (arg0.getKeyCode() == KeyEvent.VK_UP) {
				if (index - 1 >= 0) {
					index--;
					jtf.setText(history.get(index));
				}
			}

		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub

		}
	}

	// cette fonction permet d'ecrire dans un fichier existant ou non
	public void ecrire(String nomFic, String texte) {
		// on va chercher le chemin et le nom du fichier et on me tout ca dans
		// un String
		String adressedufichier = nomFic;

		// on met try si jamais il y a une exception
		try {

			FileWriter fw = new FileWriter(adressedufichier, true);

			// le BufferedWriter output auquel on donne comme argument le
			// FileWriter fw cree juste au dessus
			BufferedWriter output = new BufferedWriter(fw);

			// on marque dans le fichier ou plutot dans le BufferedWriter qui
			// sert comme un tampon(stream)
			output.write(texte);
			// on peut utiliser plusieurs fois methode write

			output.flush();
			// ensuite flush envoie dans le fichier, ne pas oublier cette
			// methode pour le BufferedWriter

			// output.close();
			// et on le ferme
			System.out.println("fichier cr");
		} catch (IOException ioe) {
			System.out.print("Erreur : ");
			ioe.printStackTrace();
		}

	}

	// Gestion simple du cas de cd
	public void nbrecul(String s) {
		String tmp = "";
		if (lis.get(0).equals("cd")) {
			if (s.equals("..")) {
				path = path.substring(0, indiceCoupe());
			}
			for (int i = 0; i < s.length(); i++) {
				if (s.charAt(i) == '/' && tmp.equals("..")) {
					if (indiceCoupe() < path.length()) {
						try {
							path = path.substring(0, indiceCoupe());
						} catch (Exception ep) {
							texte.setText("Action impossible monsieur !");
						}
						tmp = "";
					} else
						return;
				} else if (s.charAt(i) == '/' && tmp != "") {
					if ((new File(path + "/" + tmp)).isDirectory()
							|| new File(path + "/" + tmp).exists()) {
						path = path + "/" + tmp;
						tmp = "";
					} else {
						texte.setText("il ne s agit pas d un repertoire !");
					}
				} else
					tmp += s.charAt(i);
			}
		}
	}

	String aRetour = path;

	// parse une expression et renvoie la chaine mais ne change pas path
	public String pathtmp(String s) {
		// args = path
		// Variable garbage
		String tmp = "";

		if (s.equals("..")) {
			aRetour = aRetour.substring(0, indiceCoupe());
		}
		// dont move case
		if (s.equals(".")) {
			return path;
		}
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '/' && tmp.equals("..")) {
				aRetour = aRetour.substring(0, indiceCoupe2());
				tmp = "";
			} else if (s.charAt(i) == '/' && tmp != "") {
				if ((new File(aRetour + "/" + tmp)).isDirectory()) {
					aRetour = aRetour + "/" + tmp;
					tmp = "";
				}
			} else
				tmp += s.charAt(i);
		}
		String tmp2 = aRetour;

		aRetour = path;
		return tmp2;
	}

	// fonction qui gere le cat
	public void cat(String ss) {
		texte.setText(affiche(ss));
	}

	/**
	 * print datas on files
	 * 
	 * @param ss
	 * @return
	 */
	public String affiche(String ss) {
		String chaine = "";
		String fichier = ss;
		try {
			InputStream ips = new FileInputStream(fichier);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			while ((ligne = br.readLine()) != null) {
				chaine += ligne + "\n";
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e.toString());
			texte.setText(e.toString());
			return "";
		}
		return chaine;
	}

	/**
	 * Default LINUX commands this is use Runtimes. call only if command not
	 * found
	 * 
	 * @param lis
	 */
	public void exe(ArrayList<String> lis) {
		Runtime runtime = Runtime.getRuntime();
		Process p = null;
		String[] tab = new String[lis.size()];
		for (int i = 1; i < tab.length; i++) {
			tab[i] = lis.get(i);
		}
		tab[0] = lis.get(0);
		if (tab.length >= 2)
			tab[1] = path + "/" + tab[1];

		try {
			runtime = Runtime.getRuntime();
			p = runtime.exec(tab);
		} catch (Exception e) {
			texte.setText("Commande introuvable sur le systeme ubuntu");
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args) {
		// Determine if the GraphicsDevice supports translucency.
		
		  GraphicsEnvironment ge = GraphicsEnvironment
		  .getLocalGraphicsEnvironment(); GraphicsDevice gd =
		  ge.getDefaultScreenDevice();
		  
		  // If translucent windows aren't supported, exit.
		  if(!gd.isWindowTranslucencySupported(TRANSLUCENT)) {
		  System.err.println("Translucency is not supported"); 
		  
		  }
		 

		JFrame.setDefaultLookAndFeelDecorated(true);

		// Create the GUI on the event-dispatching thread
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Fenetre tw = new Fenetre(true);

				// Set the window to 55% opaque (45% translucent).
				tw.setOpacity(0.75f);

				// Display the window.
				tw.setVisible(true);
			}
		});
	}

	/*
	 * methode qui permet de jouer une musique de format .mid
	 */
	Sequencer sequencer;
	Sequence sequence;

	class Music implements Runnable {
		String str = "";
		int loop = 0;
		String name = "music";

		public Music(String str, int loop) {
			this.str = str;
			this.loop = loop;
		}

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

		public void run() {
			music(str, loop);
		}
	}

	public void musicoff() {
		if (sequencer != null)
			sequencer.stop();
	}
}
