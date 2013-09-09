import java.awt.Dimension;
import java.awt.Toolkit;

/**
	driver class for MineSweeper game created on 29 sep 2010
 	@author Nathan Floor(FLRNAT001)
*/

class Minesweeper
{
	/**
		Create main window and start game
	*/
	
	public static void main(String[] args)
	{
		GameWindow gameFrame=new GameWindow("Mine Sweeper",15,15,800,700,10);
		gameFrame.setVisible(true);
	}
	
	//gets size of screen
  	public static Dimension getScreenDimensions(){
  		Toolkit tk = Toolkit.getDefaultToolkit();
  		return new Dimension(tk.getScreenSize());
  	}
}