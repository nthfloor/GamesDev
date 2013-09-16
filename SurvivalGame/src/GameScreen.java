import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import sun.audio.AudioPlayer;

public class GameScreen extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//variables
	private Dimension screensize;
	private JMenu mainMenu;
	private JMenuItem quitChoice;
	private JMenuItem objectivesChoice;
	private JMenuItem controlsChoice;
	private JMenuItem restartChoice;
	private JMenuBar menuBar;
	protected static JLabel loading;
	private JList Mission;
	private DefaultListModel firstMission;
	private DefaultListModel secondMission;
	private DefaultListModel thirdMission;
	protected static JScrollPane listScroller;
	protected static JProgressBar progress;
	protected static BarThread progressThread; 
	protected static boolean isLoaded = false;
	//	private JPanel background;

	public GameScreen(){
		super();
		screensize = Runner.getScreenDimensions();		
		setUndecorated(true);

		setCursor(Cursor.CROSSHAIR_CURSOR);

		//set up menu for in-game options
		mainMenu = new JMenu("Options");
		restartChoice = new JMenuItem("Restart");
		restartChoice.addActionListener(new MenuListener(this));
		//		restartChoice.setIcon(arg0)
		mainMenu.add(restartChoice);

		objectivesChoice = new JMenuItem("Objectives");
		objectivesChoice.addActionListener(new MenuListener(this));
		mainMenu.add(objectivesChoice);

		controlsChoice = new JMenuItem("Controls");
		controlsChoice.addActionListener(new MenuListener(this));
		mainMenu.add(controlsChoice);

		quitChoice = new JMenuItem("Quit");
		quitChoice.addActionListener(new MenuListener(this));
		mainMenu.add(quitChoice);

		menuBar = new JMenuBar();
		menuBar.setAlignmentY(RIGHT_ALIGNMENT);
		menuBar.add(mainMenu);
		setJMenuBar(menuBar);		

		//Mission outline labels
		firstMission = new DefaultListModel();
		firstMission.addElement("Mission 1");
		firstMission.addElement("");
		firstMission.addElement("It has been 10 years since the advent of the 3rd World War...");
		firstMission.addElement("And those responsable have established a new World Order!");
		firstMission.addElement("You must leave home and find these murderers!!!");
		firstMission.addElement("There is an outpost of the new Global Republic not far from your home.");		
		firstMission.addElement("Your first mission is to destroy the enemy barracks...");
		firstMission.addElement("As long as it stays standing, troops will still be trained.");
		firstMission.addElement("");
		firstMission.addElement("Hint: You also can win by destroying all buildings and killing all enemy units");
		firstMission.addElement("Hint: You can pick-up the enemies ammo once you've killed them(if they have any)");
		
		secondMission = new DefaultListModel();
		secondMission.addElement("Mission 2");
		secondMission.addElement("");
		secondMission.addElement("Your mission is to find and destroy the Prime Minister's house");
		secondMission.addElement("He has received numerous death threats so security has increased");
		secondMission.addElement("We have received reports that he even has an Apachy Helicopter on standby");
		secondMission.addElement("");
		secondMission.addElement("Hint: Be careful that you don't run out of ammo too soon!");
		
		thirdMission = new DefaultListModel();
		thirdMission.addElement("Mission 3 (The finale)");
		thirdMission.addElement("");
		thirdMission.addElement("It has been reported that the new President has fled...");
		thirdMission.addElement("He has taken refuge in a military base.");
		thirdMission.addElement("Your final mission is to destroy the barracks he's hiding in.");
		thirdMission.addElement("may the force be with you");
		
		//load appropriate mission outline
		switch(WelcomeScreen.currentLevel){
			case 1:
				Mission = new JList(firstMission);
				break;
			case 2:
				Mission = new JList(secondMission);
				break;
			case 3:
				Mission = new JList(thirdMission);
		}		
		
		Mission.setVisibleRowCount(-1);
		Mission.setLayoutOrientation(JList.VERTICAL);
		Mission.setFont(new Font(Font.SANS_SERIF,Font.BOLD,14));
		Mission.setVisible(true);

		listScroller = new JScrollPane(Mission);
		int x = (int)Math.round((screensize.width - 600)/2);
		int y = (int)Math.round(screensize.height*0.12);
		listScroller.setBounds(x, y, 600, 400); 
		listScroller.setVisible(true);
		add(listScroller);

		//loading label
		loading = new JLabel("Loading....");
		loading.setBounds((int)Math.round(screensize.width*0.47), (int)Math.round(screensize.height*0.81), 110, 35);
		loading.setFont(new Font(Font.SANS_SERIF,Font.BOLD,14));
		loading.setVisible(true);
		add(loading);

		//progress bar
		progressThread = new BarThread();
		progressThread.start();
	}

	public void update(Graphics g){
		super.update(g);

		progress.repaint();
		Mission.repaint();
	}

	public class BarThread extends Thread{
		public BarThread(){
			progress = new JProgressBar();
			progress.setBounds((int)Math.round(screensize.width*0.38), (int)Math.round(screensize.height*0.81) + 45, (int)Math.round((screensize.width*0.62)-(screensize.width*0.38)), 17);	
			progress.setBackground(Color.BLACK);
			progress.setForeground(Color.RED);
			add(progress);
			progress.setVisible(true);
		}

		public void run(){
			boolean load = true;
			while(load){
				if(isLoaded){
					load = false;
					this.interrupt();
				}

				progress.setIndeterminate(true);
				progress.repaint();
			}
			System.out.println("ProgressThread interrupted");
		}
	}

	public void paint(Graphics g){
		super.paint(g);

		progress.repaint();


	}

	//inner class for menu listener
	private class MenuListener implements ActionListener{
		private GameScreen parent;

		public MenuListener(GameScreen frame){
			parent = frame;
		}

		public void actionPerformed(ActionEvent e){
			String command = e.getActionCommand();

			if(command.equals("Restart")){
				AudioPlayer.player.stop(SurvivalGame.audio_background);
				AudioPlayer.player.stop(SurvivalGame.audio_heli);
				AudioPlayer.player.stop(SurvivalGame.audio_tank);
				Runner.restartCurrentGame();
			}
			if(command.equals("Objectives")){
				JOptionPane.showMessageDialog(parent, SurvivalGame.objectives,"Objectives",JOptionPane.PLAIN_MESSAGE,new ImageIcon("Textures/ground_target.jpg"));
			}
			else if(command.equals("Controls")){
				JOptionPane.showMessageDialog(parent, "Forward.......w\n"
						+"Backward...s\n"
						+"Left...............a\n"
						+"Right.............d\n"
						+"Fire................enter/left_mouse\n"
						+"1....................Assault Rifle\n"
						+"2....................Shotgun\n"
						+"3....................Sniper\n"
						+"4....................Rocket Launcher\n"
						+"Pause...........p\n","Controls",JOptionPane.PLAIN_MESSAGE,new ImageIcon("Textures/ground_target.jpg"));
			}
			else if(command.equals("Quit")){
				AudioPlayer.player.stop(SurvivalGame.audio_background);
				AudioPlayer.player.stop(SurvivalGame.audio_heli);
				Runner.displayWelcomeScreen();				
			}

		}
	}//end of MenuListener
}
