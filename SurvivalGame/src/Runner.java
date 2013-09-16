

import java.awt.Dimension;
import java.awt.Toolkit;

import GameEngine.GameCanvas;
import javax.swing.*;

public class Runner {
	private static WelcomeScreen welcome = null;
	protected static GameScreen gameframe = null;
	private static SurvivalGame ag;
	
    public static void main (String [] args) {
    	//welcome user with welcome screen
    	//initial state
    	welcome = new WelcomeScreen();  	   
    	welcome.setVisible(true);
    }
    
    public static void displayGameScreen(){
    	ag = null;
    	gameframe = new GameScreen();
    	welcome.setVisible(false);
    	setGameScreen(gameframe);
    	gameframe.setVisible(true);
    
    }
    
    //update level info for profile
    public static void finishedLevel(int level){
    	welcome.updateCurrentProfile(level);
    }
    
    public static void closeGame(){
    	if(welcome != null)
    		welcome.dispose();
    	if(gameframe != null)
    		gameframe.dispose();
    	System.exit(0);
    }
    
    public synchronized static void displayWelcomeScreen(){
    	ag.endGame();
    	gameframe.dispose();
    	WelcomeScreen.startBackgroundSound();
    	welcome.setVisible(true);
    }
  
    public synchronized static void restartCurrentGame(){
    	ag.endGame();
    	ag = null;
    	gameframe.dispose();
    	gameframe = new GameScreen();
    	welcome.setVisible(false);
    	setGameScreen(gameframe);
    	gameframe.setVisible(true);
    }
    
    private static void setGameScreen(JFrame frame){
    	ag = new SurvivalGame(100, WelcomeScreen.currentLevel);
        ag.linkToFrame(frame);
        
        GameCanvas glc = new GameCanvas(ag);
        frame.add(glc);
        
//        frame.setResizable(false);
        Dimension max = getScreenDimensions();
		frame.setSize(max);
        frame.setVisible(true);
        
        // need this so that you don't have to click on the window to gain focus ;)
        glc.requestFocusInWindow();
        
    }
    
  //gets size of screen
  	public static Dimension getScreenDimensions(){
  		Toolkit tk = Toolkit.getDefaultToolkit();
  		return new Dimension(tk.getScreenSize());
  	}
}
