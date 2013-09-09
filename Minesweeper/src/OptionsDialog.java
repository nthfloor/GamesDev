/**
	For Selecting new grid size and diffculty levels
	
	@author nathan floor
	@version 3 oct 2010
*/

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

class OptionsDialog extends JDialog
{
	/// buttons for grid size 
	private JButton smallGrid;/**Button for selecting a small grid*/
	private JButton mediumGrid;/**Button for selecting a medium grid*/
	private JButton largeGrid;/**Button for selecting a large grid*/
	
	// Instance variables
	private int numMines;/**indicates number of mines on grid*/
	private boolean hasNewSize;/**check if a new size for grid has been selected*/
	private boolean hasNewMines;/**check if more or less mines have been chosen for grid*/
	private boolean isEasy;/**check if easy level has been selected*/
	private boolean isMedium;/**check if medium level has been selected*/
	private boolean isHard;/**check if medium level has been selected*/ 
	private int newRow;/**indicates new row count*/
	private int newCol;/**indicates new column count*/
	private int newWidth;/**indicates new main window width*/
	private int newHeight;/**indicates new main window height*/
	private GameWindow mainWindow;/**reference to main window*/ 
	
	/** 
		Constructor to initialise options dialog and set up all components
		@param owner main window which called the dialog
		@param title Title of dialog window 
		@param modal determines whether you can access owner window while dialog is still open
		@param row Row count of main grid
		@param col Column count of main grid
		@param width Width of main window
		@param height Height of main window
		@param mines Number of mines in main grid
	*/
	public OptionsDialog(JFrame owner, String title, boolean modal,int row,int col,int width,int height,int mines)
	{
		super(owner, title);
		setSize(350,190);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setLayout(new BorderLayout());
		JPanel optionPane=new JPanel(new GridLayout(2,1));
		add(optionPane,BorderLayout.CENTER);
		setModal(modal);		
		
		//initialise variables
		numMines=mines;	
		hasNewSize=false;
		hasNewMines=false;
		isEasy=true;
		isMedium=false;
		isHard=false;
		newRow=row;
		newCol=col;
		newWidth=width;
		newHeight=height;
		mainWindow=(GameWindow)owner;
				
		//done button
		JButton doneBtn=new JButton("Done");
		JPanel donePane=new JPanel(new FlowLayout());
		add(donePane, BorderLayout.SOUTH);
		donePane.add(doneBtn);
		doneBtn.addActionListener(new DifficultyListener());
		
		//setup size panel
		JPanel sizePane=new JPanel(new GridLayout(1,2));
		optionPane.add(sizePane);
		sizePane.setSize(250,90);
		JLabel sizeLabel=new JLabel("Select New Grid Size:");
		sizePane.add(sizeLabel);
		//add buttons
		JPanel sizeBtnPane=new JPanel();
		sizeBtnPane.setLayout(new GridLayout(3,1));
		sizePane.add(sizeBtnPane);
				
		smallGrid=new JButton("Small(10x10)");
		smallGrid.addActionListener(new SizeListener());
		sizeBtnPane.add(smallGrid);
		mediumGrid=new JButton("Medium(15x15)");
		mediumGrid.addActionListener(new SizeListener());
		sizeBtnPane.add(mediumGrid);
		largeGrid=new JButton("Large(20x20)");
		largeGrid.addActionListener(new SizeListener());
		sizeBtnPane.add(largeGrid);
					
		//set up difficulty panel
		JPanel diffPane=new JPanel(new FlowLayout());
		optionPane.add(diffPane);		
		JLabel diffLabel=new JLabel("Select Difficulty:");
		diffPane.add(diffLabel);
		JButton easyBtn=new JButton("Easy");
		easyBtn.addActionListener(new DifficultyListener());
		diffPane.add(easyBtn);		
		JButton mediumBtn=new JButton("Medium");
		mediumBtn.addActionListener(new DifficultyListener());
		diffPane.add(mediumBtn);		
		JButton hardBtn=new JButton("Hard");
		hardBtn.addActionListener(new DifficultyListener());
		diffPane.add(hardBtn);
				
		//side panels for look
		JPanel leftSide=new JPanel();
		add(leftSide, BorderLayout.WEST);
		leftSide.setSize(20,90);
		JPanel rightSide=new JPanel();
		add(rightSide, BorderLayout.EAST);
		rightSide.setSize(20,90);
		JPanel topSide=new JPanel();
		add(topSide, BorderLayout.NORTH);
		rightSide.setSize(320,90);	
	}

	/**
		Inner class to Listen for events on buttons regarding difficulty and the done button
	*/
	private class DifficultyListener implements ActionListener
	{
		/**
			Calculates new number of mines depending on difficulty level, and reinitialises main grid and window with new values
			@param e ActionEvent 
		*/
		public void actionPerformed(ActionEvent e)
		{
			String command=e.getActionCommand();
			if(command.equals("Done"))
			{
				if(isEasy)//calculates new number of mines for grid
				{numMines=Math.round((float)((newRow*newCol)*0.16));}
				else if(isMedium)
					{numMines=Math.round((float)((newRow*newCol)*0.20));}
					else if(isHard)
						{numMines=Math.round((float)((newRow*newCol)*0.24));}
				setVisible(false);
				if(hasNewSize)//creates new grid with new dimensions
				{					
					mainWindow.updateVariables(newRow,newCol,newWidth,newHeight,numMines);
					mainWindow.gridPane.removeAll();	
					mainWindow.updateGrid();					
					mainWindow.gridPane.validate();
					mainWindow.setSize(newWidth,newHeight);
				}				
				if(hasNewMines)//re-initialises grid with new number of mines
				{
					mainWindow.updateVariables(newRow,newCol,newWidth,newHeight,numMines);
					mainWindow.initialise();
				}
			}
			else
			{				
				if(command.equals("Easy"))
				{
					isEasy=true;
					isMedium=false;
					isHard=false;
				}
				else if(command.equals("Medium"))
					{
						isEasy=false;
						isMedium=true;
						isHard=false;
					}
					else if(command.equals("Hard"))
						{
							isEasy=false;
							isMedium=false;
							isHard=true;
						}
						else
						{
							ConfirmWindow check=new ConfirmWindow("Unexpected gui error, do you want to exit?",200,100,"stop_sign.gif",mainWindow,"Error",true);
							check.setVisible(true);
						}
				hasNewMines=true;
			}
		}
	}//end of diffListener class
	
	/**Inner class for size buttons*/
	private class SizeListener implements ActionListener
	{
		/**
			Indicates what the new dimensions are for main grid
			@param e ActionEvent 
		*/
		public void actionPerformed(ActionEvent e)
		{
			String command=e.getActionCommand();
			if(command.equals("Small(10x10)"))
			{
				newRow=10;
				newCol=10;
				newWidth=515;
				newHeight=500;
			}
			else if(command.equals("Medium(15x15)"))
				{				
					newRow=15;
					newCol=15;		
					newWidth=800;
					newHeight=700;
				}
				else if(command.equals("Large(20x20)"))
					{	
						newRow=20;
						newCol=20;				
						newWidth=1000;
						newHeight=850;
					}
					else
					{
						ConfirmWindow check=new ConfirmWindow("Unexpected gui error, do you want to exit?",200,100,"stop_sign.gif",mainWindow,"Error",true);
						check.setVisible(true);
					}
			hasNewSize=true;
		}
	}//end of size listener
}