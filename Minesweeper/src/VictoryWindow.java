/**
	Display window when game is won created on 6 oct 2010
	@author nathan floor	
*/

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

class VictoryWindow extends ConfirmWindow
{
	/**
		Constructor which initialises victory dialog with all its components
		
		@param text message for dialog
		@param width Dialog width
		@param height Dialog height
		@param iconName file name of icon to be used in dialog
		@param owner main window for which dialog was created
		@param title title of dialog window
		@param modal determines whether you can access owner window while dialog is still open
	*/
	public VictoryWindow(String text,int width,int height,String iconName,JFrame owner,String title,boolean modal)
	{super(text,width,height,iconName,owner,title,modal);}
	
	/**
		Resets grid if yes is selected or else does nothing
		@param e ActionEvent
	*/
	public void actionPerformed(ActionEvent e)
	{
		String command=e.getActionCommand();
		
		if(command.equals("Yes"))
		{
			GameWindow mainWindow=new GameWindow();
			ImageIcon resetIcon=new ImageIcon("smiley_happy.gif");
			mainWindow.resetBtn.setIcon(resetIcon);			
			mainWindow.initialise();
			dispose();
		}
		else if(command.equals("No"))
			{dispose();}
			else
			{System.out.println("Unexpected error in confirmwindow.");}
	}
}