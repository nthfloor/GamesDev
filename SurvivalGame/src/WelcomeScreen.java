
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JWindow;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class WelcomeScreen extends JWindow {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//instance variables
	private JButton Playbutton = new JButton("Play");
	private JButton Profilebutton = new JButton("Profiles");
	private JButton LoadButton = new JButton("Load Level");
	private JButton HighScoresbtn = new JButton("Profile Stats");
	private JButton Controlbutton = new JButton("Controls");
	private JButton Exitbutton = new JButton("Exit");
	private boolean profileExists = false;
	private boolean isNewProfile;

	private Object[] profiles = new Object[1];
	private int numberOfProfiles = 0;
	private Dimension screenSize;
	private Vector<Profiles> savedProfiles;
	private Vector<String> levels = new Vector<String>();
	protected static Profiles currentProfile;
	protected static int currentLevel = 0;
	
	//sound variables
	protected static AudioStream audio;
	static InputStream input;
	private static int soundLoopCounter = 0;

	//default constructor	
	public WelcomeScreen(){
		super();
		if(! readFromFile()){
			profiles[0] = "New";
			levels.add("Level 1");
			isNewProfile = true;
			savedProfiles = new Vector<Profiles>();
		}		
		currentProfile = null;
		profileExists = false;
		isNewProfile = false;

		//initialize screen properties
		screenSize = Runner.getScreenDimensions();		
		setSize(screenSize);
		//initialize base layout
		setLayout(null);		

		Playbutton.addMouseListener(new ButtonListener(this));
		Playbutton.setBounds((int)Math.round(screenSize.width*0.8), (int)Math.round(screenSize.height*0.3), 110, 35);
		add(Playbutton);

		Profilebutton.addMouseListener(new ButtonListener(this));
		Profilebutton.setBounds((int)Math.round(screenSize.width*0.8), (int)Math.round(screenSize.height*0.3)+45, 110, 35);
		add(Profilebutton);

		LoadButton.addMouseListener(new ButtonListener(this));
		LoadButton.setBounds((int)Math.round(screenSize.width*0.8), (int)Math.round(screenSize.height*0.3)+90, 110, 35);
		add(LoadButton);

		HighScoresbtn.addMouseListener(new ButtonListener(this));
		HighScoresbtn.setBounds((int)Math.round(screenSize.width*0.8), (int)Math.round(screenSize.height*0.3)+135, 110, 35);
		add(HighScoresbtn);

		Controlbutton.addMouseListener(new ButtonListener(this));
		Controlbutton.setBounds((int)Math.round(screenSize.width*0.8), (int)Math.round(screenSize.height*0.3)+180, 110, 35);
		add(Controlbutton);

		Exitbutton.addMouseListener(new ButtonListener(this));
		Exitbutton.setBounds((int)Math.round(screenSize.width*0.8), (int)Math.round(screenSize.height*0.3)+225, 110, 35);
		add(Exitbutton);
		
		startBackgroundSound();
	}
	
	protected static void startBackgroundSound(){
		//welcome sound
		if(soundLoopCounter <= 0){
			try{
				input = new FileInputStream("sounds/21_guns_half.wav");
				audio = new AudioStream(input);
				AudioPlayer.player.start(audio);
			}catch(Exception e){

			}
			soundLoopCounter = 4635;
		}
		else
			soundLoopCounter--;
	}

	public void paint(Graphics g){
		super.paint(g);

		//redraw objects on window to make them visible
		Playbutton.repaint();
		Profilebutton.repaint();
		LoadButton.repaint();
		HighScoresbtn.repaint();
		Controlbutton.repaint();
		Exitbutton.repaint();

		if(numberOfProfiles == 0){
			LoadButton.setEnabled(false);
			HighScoresbtn.setEnabled(false);
			profileExists = false;
		}
		else{
			LoadButton.setEnabled(true);
			HighScoresbtn.setEnabled(true);
			profileExists = true;
		}
		g.drawImage(new ImageIcon("Textures/Backgrounds/Nuclear-War-text.jpg").getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
		//		g.drawImage(new ImageIcon("Textures/Backgrounds/downtown-dc.jpg").getImage(), 0, 0, this.getWidth(), this.getHeight(), this);

	}

	//save profiles details to text-file
	private void saveInfoToFile(){
		PrintWriter myfile = null;

		try{
			myfile = new PrintWriter(new FileOutputStream("profiles.txt"));
		}catch(Exception e){
			System.out.println("Error opening file");
		}

		//write to file
		for(int i=0;i < savedProfiles.size();i++){
			myfile.println(savedProfiles.elementAt(i).getName());
			myfile.println(savedProfiles.elementAt(i).getLevels().size());
			for(int j=0;j < savedProfiles.elementAt(i).getLevels().size();j++)
				myfile.println
				(savedProfiles.elementAt(i).getLevels().elementAt(j));
		}

		myfile.close();
	}

	//read profile info from text file
	private boolean readFromFile(){
		Scanner myfile = null;
		savedProfiles = new Vector<Profiles>();

		try{
			myfile = new Scanner(new FileInputStream("profiles.txt"));
		}catch(Exception e){
			System.out.println("Error reading file");
		}

		if(myfile == null)
			return false;
		else{		
			String tempName = null;
			int numLevels = 0;
			while(myfile.hasNextLine()){
				tempName = myfile.nextLine();
				numLevels = myfile.nextInt();
				myfile.nextLine();
				levels = new Vector<String>();
				for(int i=0;i < numLevels;i++)
					levels.add(myfile.nextLine());

				savedProfiles.add(new Profiles(tempName, levels));
			}
			//		currentProfile = savedProfiles.firstElement();
			//		numberOfProfiles = savedProfiles.size();
			myfile.close();
			return true;
		}		
	}

	public void updateCurrentProfile(int finishedLevel){
		if(finishedLevel+1 > currentProfile.getLevels().size()){
			int index = savedProfiles.indexOf(currentProfile);
			savedProfiles.elementAt(index).addLevel("Level "+(currentProfile.getLevels().size()+1));
			currentProfile = savedProfiles.elementAt(index);
			levels = currentProfile.getLevels();
			currentLevel = (currentProfile.getLevels().size());
			saveInfoToFile();
		}
		else{
			currentLevel = finishedLevel+1;
		}
	}

	private class ButtonListener implements MouseListener{
		private WelcomeScreen parent;

		public ButtonListener(WelcomeScreen owner){
			parent = owner;
		}

		public void mouseClicked(MouseEvent e){
			JButton caller = (JButton)e.getSource();			

			if(caller.getText().equals("Play")){
				if(currentProfile == null)
					if(selectProfile()){		
						saveInfoToFile();
						Runner.displayGameScreen();
					}
					else{
						parent.setVisible(true);
						parent.requestFocus(true);
					}
				else
					Runner.displayGameScreen();
			}
			else if(caller.getText().equals("Profiles")){
				//pop-up menu appears	
				if(selectProfile()){
					if(! isNewProfile){
						//allow users to delete profile
						Object[] options = {"Select Profile","Delete Profile","Cancel"};
						int response = JOptionPane.showOptionDialog(parent, "What do you wnt to do with this profile?", "Options", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
						if(response == JOptionPane.YES_OPTION){
							//set current profile
							saveInfoToFile();
							LoadButton.setEnabled(true);
							HighScoresbtn.setEnabled(true);
							profileExists = true;
						}
						else if(response == JOptionPane.NO_OPTION){
							//delete profile
							savedProfiles.remove(savedProfiles.indexOf(currentProfile));
							numberOfProfiles--;
							currentLevel = 0;
							currentProfile = null;
							levels = new Vector<String>();
							profileExists = false;
							if(numberOfProfiles <= 0){
								LoadButton.setEnabled(false);
								HighScoresbtn.setEnabled(false);
								isNewProfile = true;
							}
							else if(currentProfile == null){ //check if a profile has been selected
								LoadButton.setEnabled(false);
								HighScoresbtn.setEnabled(false);
							}

							saveInfoToFile();
						}
						else if(response == JOptionPane.CANCEL_OPTION){
							//do nothing
						}
						else if(response == JOptionPane.CLOSED_OPTION){
							//do nothing
						}
					}
					else{
						LoadButton.setEnabled(true);
						HighScoresbtn.setEnabled(true);
						isNewProfile = false;
					}

					saveInfoToFile();
				}
			
				parent.setVisible(true);
				parent.requestFocus(true);
			}
			else if(caller.getText().equals("Load Level")){
				if(profileExists){
					//pop up menu will appear
					Object[] list = new Object[currentProfile.getLevels().size()];
					for(int i=0;i < currentProfile.getLevels().size();i++){
						list[i] = currentProfile.getLevels().elementAt(i);
					}

					ImageIcon bomb = new ImageIcon("Textures/ground_target.jpg");
					String response = (String)JOptionPane.showInputDialog(parent,"Select a level from the List below:","Load Level",JOptionPane.PLAIN_MESSAGE,bomb,list,"Level 1");

					if(response != null){
						//start relevant level
						if(response.charAt(response.length()-1) == '1')
							currentLevel = 1;
						else if(response.charAt(response.length()-1) == '2')
							currentLevel = 2;
						else if(response.charAt(response.length()-1) == '3')
							currentLevel = 3;
						Runner.displayGameScreen();
					}
					else{
						parent.setVisible(true);
						parent.requestFocus(true);
					}						
				}				
			}
			else if(caller.getText().equals("Profile Stats")){
				if(profileExists){
					JOptionPane.showMessageDialog(parent, "Current Profile: "+currentProfile.getName()
							+"\nCurrent Level: " + currentProfile.getLevels().lastElement(),"Profile Stats",JOptionPane.PLAIN_MESSAGE,new ImageIcon("Textures/ground_target.jpg"));
					parent.setVisible(true);
					parent.requestFocus(true);
				}
			}
			else if(caller.getText().equals("Controls")){
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
				parent.setVisible(true);
				parent.requestFocus(true);
			}
			else if(caller.getText().equals("Exit"))
				Runner.closeGame();
		}

		private boolean selectProfile(){
			if(savedProfiles.size() == 0){
				return enterNewProfile();
			}
			else{
				//read vector into object array 
				profiles = new Object[savedProfiles.size()+1];
				profiles[0] = "New";
				for(int i=0;i<savedProfiles.size();i++)
					profiles[i+1] = savedProfiles.elementAt(i).getName();

				ImageIcon bomb = new ImageIcon("Textures/ground_target.jpg");								
				String response = (String)JOptionPane.showInputDialog(parent,"Select a profile from the List below:","Profiles",JOptionPane.PLAIN_MESSAGE,bomb,profiles,"New");

				if(response != null){
					if(response.equals("New"))
						return enterNewProfile();	
					else{

						//load profile data into current profile variable
						for(int i=0;i < savedProfiles.size();i++)
							if(savedProfiles.elementAt(i).getName().equals(response)){
								currentProfile = savedProfiles.elementAt(i);
								numberOfProfiles = savedProfiles.size();
								currentLevel = currentProfile.getLevels().size();
								break;
							}
					}	
					return true;
				}
				else
					return false;
			}
		}

		private boolean enterNewProfile(){
			String newProfile = "";
			boolean input_success = false;
			while(! input_success){
				newProfile = (String)JOptionPane.showInputDialog(parent,"Enter new profile below(no spaces):","Profiles",JOptionPane.INFORMATION_MESSAGE);
				//add profile to list of profiles
				if(newProfile == null){
					input_success = true; 
					return false;
				}
				else if(newProfile.equals(""))
					JOptionPane.showMessageDialog(parent, "You must enter a profile name", "Try Again", JOptionPane.ERROR_MESSAGE);				
				else if(newProfile.contains(" "))							
					JOptionPane.showMessageDialog(parent, "Incorrect format used for profile name,\nProfile must contain no spaces", "Try Again", JOptionPane.ERROR_MESSAGE);
				else if(isSavedProfile(newProfile)){
					JOptionPane.showMessageDialog(parent, "Profile Name already exists, please use another name...", "Try Again", JOptionPane.ERROR_MESSAGE);
				}
				else{
					levels = new Vector<String>();
					levels.add("Level 1");
					numberOfProfiles++;
					currentProfile = new Profiles(newProfile,levels);
					currentLevel = 1;
					savedProfiles.add(currentProfile);
					input_success = true;
					isNewProfile = true;
					profileExists = true;
				}
			}
			return true;
		}

		private boolean isSavedProfile(String name){
			for(int i=0;i < savedProfiles.size();i++)
				if(savedProfiles.elementAt(i).getName().equals(name))
					return true;
			return false;
		}

		public void mouseEntered(MouseEvent e){}

		public void mouseExited(MouseEvent e){}

		public void mousePressed(MouseEvent e){}

		public void mouseReleased(MouseEvent e){}
	}//end of ButtonListener


}//end of welcome-screen
