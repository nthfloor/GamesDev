/**
	driver class for MineSweeper game created on 29 sep 2010
 	@author Nathan Floor(FLRNAT001)
*/

class GameDriver
{
	/**
		Create main window and start game
	*/
	
	public static void main(String[] args)
	{
		GameWindow gameFrame=new GameWindow("Mine Sweeper",15,15,800,700,10);
		gameFrame.setVisible(true);
	}
}