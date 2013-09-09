/**
	Creates main game window and all its components and manages interaction with interface 
	
	@author nathan floor
	@version 29 sep 2010
*/

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Timer;
import java.util.Random;

class GameWindow extends JFrame
{
	//instance variables
	private enum GridBtnType{MINE, NUMBER, NOTHING};/**Indicates type of button*/
	private static GridBtnType[][] initialGrid;/**used for randomly generating and storing mines on grid*/
	private static GridButton[][] grid;/**grid of button objects*/
	private static int gridRow;/**number of rows in grid*/
	private static int gridCol;/**number of columns in grid*/
	private static int gridWidth;/**width of main window*/
	private static int gridHeight;/**height of main window*/
	private static int numberOfMines;/**number of mines in grid*/
	private static Timer gameTime;/**timer*/
	private static int timeInSec;/**play time in seconds*/
	private static int timeInMin;/**play time in minutes*/
	private static boolean firstClick;/**indicates when to start timer*/
	private static int minesLeft;/**indicates number of mines left to be flagged*/
	
	//panes and forms and other gui components
	private static JPanel infoPane;/**contains timer, reset button and number of mines left*/
	public static JPanel gridPane;/**contains grid of buttons*/
	public static JButton resetBtn;/**reset button*/
	private static JMenu mainMenu;/**main menu for options and exiting*/
	private static JMenuItem exitChoice;/**close program*/
	private static JMenuItem optionChoice;/**open options dialog*/
	private static JMenuBar bar;/**menu bar*/
	private static JDialog options;/**reference options dialog*/
	private static JLabel timeLabel;/**label to display duration of play*/
	private static JLabel mineLabel;/**label to diplay mines still left*/
	
	/** constructor to do nothing other than create a reference to this object to access certain methods and components*/
	public GameWindow()
	{}
	
	/**
		constructor to create main window and initialise all components
		
		@param title Title of window
		@param row number rows for grid
		@param col number of columns for grid
		@param width Width of window
		@param height Height of window
		@param mines number of mines in grid
	*/
	public GameWindow(String title,int row,int col,int width,int height,int mines)
	{
		super(title);
		gridRow=row;
		gridCol=col;
		gridWidth=width;
		gridHeight=height;
		numberOfMines=mines;
		
		Dimension min=new Dimension(512,500);
		Dimension max=new Dimension(1000,850);
		setMinimumSize(min);
		setMaximumSize(max);
		setUndecorated(true);
			
		minesLeft=numberOfMines;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new MainListener());
		
		//initialise base layout and consequent layouts
		setLayout(new BorderLayout());
		
		//menu bar and items
		mainMenu=new JMenu("File");
		optionChoice=new JMenuItem("Options");
		optionChoice.addActionListener(new MenuListener());
		mainMenu.add(optionChoice);
		
		exitChoice=new JMenuItem("Exit");
		exitChoice.addActionListener(new MenuListener());
		mainMenu.add(exitChoice);
		
		bar=new JMenuBar();
		bar.add(mainMenu);
		setJMenuBar(bar);
		
		//side panels for look
		JPanel leftSide=new JPanel();
		add(leftSide, BorderLayout.WEST);
		JPanel rightSide=new JPanel();
		add(rightSide, BorderLayout.EAST);
		JPanel underSide=new JPanel();
		add(underSide, BorderLayout.SOUTH);
		
		//set up info pane for reset button,timer and points
		infoPane=new JPanel();
		add(infoPane, BorderLayout.NORTH);
		infoPane.setLayout(new FlowLayout());		
		//timer
		gameTime=new Timer(1000,new TimerListener());
		timeInSec=0;
		timeInMin=0;
		firstClick=false;
		timeLabel=new JLabel("Time Taken: "+Integer.toString(timeInMin)+"m "+Integer.toString(timeInSec)+"s");
		infoPane.add(timeLabel);		
		//reset button
		ImageIcon resetIcon=new ImageIcon("smiley_happy.gif");
		resetBtn=new JButton(resetIcon);
		resetBtn.addMouseListener(new ResetListener());
		infoPane.add(resetBtn);
		//mines left
		mineLabel=new JLabel("Mines left: "+Integer.toString(minesLeft));
		infoPane.add(mineLabel);
		
		//set up grid pane for game
		gridPane=new JPanel();
		gridPane.setVisible(true);
		add(gridPane, BorderLayout.CENTER);
		updateGrid();			
	}//parameterised constructor
	
	/**randomly generates coordinates for mines and inserts them randomly into grid*/ 
	public void randomize()
	{
		initialGrid=new GridBtnType[gridRow][gridCol];
		minesLeft=numberOfMines;
		//randomly generates coords for specified number of mines and enters them into grid 
		Random mines=new Random();
		int randomRow=0;
		int randomCol=0;
		for(int i=0;i<numberOfMines;i++)
		{
			randomRow=mines.nextInt(gridRow);
			randomCol=mines.nextInt(gridCol);
			if(!(initialGrid[randomRow][randomCol]==GridBtnType.MINE))
			{initialGrid[randomRow][randomCol]=GridBtnType.MINE;}
			else
			{i--;}
		}
	}
	
	/**initialise grid and populates array of buttons*/
	public void initialise()
	{
		firstClick=false;
		//mines left
		minesLeft=numberOfMines;
		mineLabel.setText("Mines left: "+Integer.toString(minesLeft));
		//timer	
		timeInSec=0;
		timeInMin=0;
		timeLabel.setText("Time taken: "+Integer.toString(timeInMin)+"m "+Integer.toString(timeInSec)+"s");			
	
		randomize();
		//initialises actual grid with nothing or number indicating mines nearby
		int nearbyMines=0;
		
		for(int row=0;row<gridRow;row++)
		{
			for(int col=0;col<gridCol;col++)
			{
				if(!(initialGrid[row][col]==GridBtnType.MINE))
				{
					nearbyMines=checkForMines(row,col);
					if(nearbyMines==0)
					{
						grid[row][col].setType("NOTHING");
						reset(row,col,nearbyMines);
						grid[row][col].setActionCommand("NOTHING");
					}
					else
					{
						
						grid[row][col].setType("NUMBER");
						reset(row,col,nearbyMines);
						grid[row][col].setActionCommand("NUMBER");
					}
				}
				else
				{
					grid[row][col].setType("MINE");
					reset(row,col,-1);
					grid[row][col].setActionCommand("MINE");
				}
			}//for col
		}//for row
	}//initialise
	
	/**resets variables for each button to defualt values*/
	public void reset(int r, int c,int numMines)
	{
		grid[r][c].setLabel("");
		ImageIcon defaultIcon=new ImageIcon("null");
		grid[r][c].setIcon(defaultIcon);
		grid[r][c].setBackground(Color.DARK_GRAY);
		grid[r][c].setNumber(numMines);
		grid[r][c].setClicked(false);
		grid[r][c].setFlagged(false);
	}
	
	/**
		method to check for adjacent mines to current button
		@param iRow row coordinate
		@param iCol column coordinate
	*/
	private int checkForMines(int iRow,int iCol)
	{
		int mineCounter=0;
		
		if (!(initialGrid[iRow][iCol]==GridBtnType.MINE))
		{
			//test points around current coords
			
			//check if in first row
			if(iRow-1>=0)
			{
				if(iCol-1>=0)
				{
					mineCounter+=isMine(iRow-1,iCol-1);
					mineCounter+=isMine(iRow,iCol-1);
				}
				if(iCol+1<gridCol)
				{
					mineCounter+=isMine(iRow,iCol+1);
					mineCounter+=isMine(iRow-1,iCol+1);
				}
				mineCounter+=isMine(iRow-1,iCol);
			}
			else
			{
				if(iCol-1>=0)
				{mineCounter+=isMine(iRow,iCol-1);}
				if(iCol+1<gridCol)
				{mineCounter+=isMine(iRow,iCol+1);}
			}
			//check if end of rows
			if(iRow+1<gridRow)
			{
				if(iCol-1>=0)
				{mineCounter+=isMine(iRow+1,iCol-1);}
				if(iCol+1<gridCol)
				{mineCounter+=isMine(iRow+1,iCol+1);}
				mineCounter+=isMine(iRow+1,iCol);
			}
		}		
		return mineCounter;
	}
	
	/**
		Checks if current button is a mine
		@param x row coordinate
		@param y column coordinate
	*/
	public int isMine(int x, int y)
	{
		if(initialGrid[x][y]==GridBtnType.MINE)
		{return 1;}
		else{return 0;}
	}
	
	/**
		method to expand buttons until reach a button near mines
		@param row row coordinate
		@param col column coordinate
	*/
	public void expand(int row,int col)
	{
		if(grid[row][col].isMine() || grid[row][col].isClicked())
		{return;}
		grid[row][col].setClicked(true);
		grid[row][col].setBackground(Color.GRAY);
		if(grid[row][col].getNumber()==0)
		{
			for(int j=row-1;j<=row+1;j++)
			{
				for(int i=col-1;i<=col+1;i++)
				{
					if( i >=0 && i < gridCol && j>=0 && j<gridRow)
					{
						if(! grid[j][i].isFlagged())
						{expand(j,i);}			
					}
				}
			}
		}//if ==0
		revealBtns();
	}
	
	/**
		Changes all the relevant variables
		
		@param row number of rows
		@param col Number of columns
		@param width Width of main window
		@param height Height of main window
		@param mines Number of mines in grid
	*/
	public void updateVariables(int row,int col,int width,int height,int mines)
	{
		gridRow=row;
		gridCol=col;
		gridWidth=width;
		gridHeight=height;
		numberOfMines=mines;
	}
	
	/**method to update grid panel and array of buttons and reinitialise grid*/
	public void updateGrid()
	{		
		gridPane.setLayout(new GridLayout(gridRow,gridCol));			
		grid=new GridButton[gridRow][gridCol];
		for(int n=0;n<gridRow;n++)
		{
			for(int m=0;m<gridCol;m++)
			{
				grid[n][m]=new GridButton(n,m);
				grid[n][m].addMouseListener(new GridListener());
			} 
		}
		
		initialise();
		
		//loop through grid and initialise buttons
		int iRow=0;
		int iCol=0;
		for(int i=0;i<(gridRow*gridCol);i++)
		{
			gridPane.add(grid[iRow][iCol]);
			if(iCol>=gridCol-1)
			{
				iCol=0;
				iRow++;
			}
			else
			{iCol++;}
		}		
//		setSize(gridWidth,gridHeight);
		
		setSize(Minesweeper.getScreenDimensions());
	}
	
	/**
		method to reveal everything from begin row and column to end row and column
		
		@param beginRow row coordinate from which to begin
		@param beginCol column coordiante from which to begin
		@param endRow row coordinate at which to stop
		@param endCol column coordinate at which to stop
	*/
	public void expand(int beginRow,int beginCol,int endRow,int endCol)
	{
		for(int i=beginRow;i<endRow;i++)
		{
			for(int k=beginCol;k<endCol;k++)
			{
				if(grid[i][k].getType().equals("NOTHING"))
				{
					if(grid[i][k].isFlagged())
					{
						ImageIcon noMineIcon=new ImageIcon("flag_no_mine.gif");
						grid[i][k].setIcon(noMineIcon);
					}
					else
					{grid[i][k].setBackground(Color.GRAY);}
					grid[i][k].setClicked(true);
				}
				else if(grid[i][k].getType().equals("NUMBER"))
					{
						if(grid[i][k].isFlagged())
						{
							ImageIcon noMineIcon=new ImageIcon("flag_no_mine.gif");
							grid[i][k].setIcon(noMineIcon);
						}
						else
						{
							grid[i][k].setLabel(Integer.toString(grid[i][k].getNumber()));
							grid[i][k].setBackground(Color.GRAY);
						}
						grid[i][k].setClicked(true);
					}
					else if(grid[i][k].getType().equals("MINE"))
						{
							if(! grid[i][k].isFlagged())
							{
								grid[i][k].setClicked(true);
								ImageIcon mineIcon=new ImageIcon("mine.gif");
								grid[i][k].setIcon(mineIcon);
								grid[i][k].setBackground(Color.GRAY);
							}
							else
							{
								ImageIcon isMineIcon=new ImageIcon("flag_mine.gif");
								grid[i][k].setIcon(isMineIcon);
							}
						}
			}
		}
	}//expand all
	
	/**method to reveal clicked buttons*/
	public void revealBtns()
	{
		for(int i=0;i<gridRow;i++)
		{
			for(int k=0;k<gridCol;k++)
			{
				if(grid[i][k].isClicked())
				{
					if(grid[i][k].getType().equals("NOTHING"))
					{grid[i][k].setBackground(Color.GRAY);}
					else if(grid[i][k].getType().equals("NUMBER"))
						{
							grid[i][k].setLabel(Integer.toString(grid[i][k].getNumber()));
							grid[i][k].setBackground(Color.GRAY);
						}
						else if(grid[i][k].getType().equals("MINE"))
							{
								expand(i,k);
								grid[i][k].setLabel("M");
								grid[i][k].setBackground(Color.GRAY);
							}
				}
			}
		}
	}//update all
	
	/**Check if only buttons not yet clicked are only mines thus victory*/ 
	public void checkIfWon()
	{
		if(minesLeft==0)//if flagged all mines
		{
			gameTime.stop();
			VictoryWindow win=new VictoryWindow("Well done, you took "+Integer.toString(timeInMin)+"m "+Integer.toString(timeInSec)+"s."+" Do you want to reset the grid?",350,160,"smiley_victory.gif",this,"Victory",true);
			win.setVisible(true);
		}
		
		//loop through entire grid to check if only mine buttons are left
		int buttonsClicked=0; 
		for(int n=0;n<gridRow;n++)
		{
			for(int m=0;m<gridCol;m++)
			{
				if(! grid[n][m].isClicked())
				{buttonsClicked++;}
			}
		}
		if(buttonsClicked==numberOfMines)
		{
			gameTime.stop();
			VictoryWindow win=new VictoryWindow("Well done, you took "+Integer.toString(timeInMin)+"m "+Integer.toString(timeInSec)+"s."+" Do you want to reset the grid?",350,160,"smiley_victory.gif",this,"Victory",true);
			win.setVisible(true);
		}
	}
	
	/**mouse listener inner class to manage mouse events*/
	private class GridListener implements MouseListener
	{
		private GameWindow mainWin;/**reference to main window object*/
		
		/**
			if grid button left-clicked, right-clicked or middle-clicked
			@param e MouseEvent
		*/
		public void mouseClicked(MouseEvent e)
		{
			GridButton tempBtn=(GridButton)(e.getSource());
			mainWin=new GameWindow();
			
			if(! firstClick)//if first cleck start timer
			{
				firstClick=true;
				gameTime.start();
			}
			
			if(e.getButton()==3)//if right-clicked or flagged
			{
				if(tempBtn.isClicked())
				{return;}
				if(tempBtn.isFlagged())//already flagged
				{
					ImageIcon dangerIcon=new ImageIcon("null");
					tempBtn.setIcon(dangerIcon);
					tempBtn.setBackground(Color.DARK_GRAY);
					tempBtn.setFlagged(false);
					if(tempBtn.getType().equals("MINE"))//if mine
					{
						minesLeft++;
						mineLabel.setText("Mines left: "+Integer.toString(minesLeft));
					}
				}
				else
				{
					tempBtn.setBackground(Color.WHITE);
					ImageIcon dangerIcon=new ImageIcon("danger_mines.gif");
					tempBtn.setIcon(dangerIcon);
					tempBtn.setFlagged(true);
					if(tempBtn.getType().equals("MINE"))//if mine
					{
						minesLeft--;
						mineLabel.setText("Mines left: "+Integer.toString(minesLeft));
					}
				}				
			}
			else if(e.getButton()==1)//if left_clicked
				{
					if(tempBtn.isFlagged()||tempBtn.isClicked())
					{return;}
					if(tempBtn.getType().equals("NOTHING"))
					{
						//disable
						tempBtn.setBackground(Color.GRAY);
						expand(tempBtn.getRow(),tempBtn.getCol());
						tempBtn.setClicked(true);
					}
					else if(tempBtn.getType().equals("NUMBER"))
						{
							//disable and reveal number of nearby mines
							tempBtn.setClicked(true);
							tempBtn.setBackground(Color.GRAY);
							tempBtn.setLabel(Integer.toString(tempBtn.getNumber()));
						}
						else if(tempBtn.getType().equals("MINE"))
							{
								//boom, change icon
								gameTime.stop();
								tempBtn.setClicked(true);
								ImageIcon sadIcon=new ImageIcon("smiley_sad.gif");
								resetBtn.setIcon(sadIcon);
								expand(0,0,gridRow,gridCol);
								tempBtn.setBackground(Color.GRAY);
								ImageIcon boomIcon=new ImageIcon("boom.gif");
								tempBtn.setIcon(boomIcon);
								//game over								
							}
							else//error
							{
								ConfirmWindow check=new ConfirmWindow("Unexpected gui error, do you want to exit?",300,140,"stop_sign.gif",mainWin,"Error",true);
								check.setVisible(true);
							}
				}
				else if(e.getButton()==2)//if middle-clicked
					{		
						if(tempBtn.isFlagged()|| ! tempBtn.isClicked())
						{return;}				
						//reveal all adjacent non mine cells if all mines have been flagged
						if(tempBtn.getType().equals("NUMBER"))
						{
							int count=0;
							for(int i=tempBtn.getRow()-1;i<=tempBtn.getRow()+1;i++)//loop to check if all adjacent mine are flagged
							{
								for(int j=tempBtn.getCol()-1;j<=tempBtn.getCol()+1;j++)
								{
									if(i >=0 && i < tempBtn.getRow()+1 && j>=0 && j<tempBtn.getCol()+1)
										if(grid[i][j].getType().equals("MINE")&& grid[i][j].isFlagged())
										{count++;}
								}
							}							
							if(count==tempBtn.getNumber())//if all adjacent buttons are flagged or not mines
							{
								if((tempBtn.getRow()-1)>=0 && (tempBtn.getRow()+1)<gridRow && (tempBtn.getCol()-1)>=0 && (tempBtn.getCol()+1)<gridCol)
								{expand(tempBtn.getRow()-1,tempBtn.getCol()-1,tempBtn.getRow()+2,tempBtn.getCol()+2);}								
							}
						}//if clickcount
					}
			checkIfWon();
		}
		
		public void mouseEntered(MouseEvent e)
		{}	
		public void mouseExited(MouseEvent e)
		{}		
		public void mousePressed(MouseEvent e)
		{
			ImageIcon worriedIcon=new ImageIcon("smiley_worried.gif");
			resetBtn.setIcon(worriedIcon);	
		}		
		public void mouseReleased(MouseEvent e)
		{
			ImageIcon resetIcon=new ImageIcon("smiley_happy.gif");
			resetBtn.setIcon(resetIcon);
		}
	}//end of mouse listener inner class
	
	/**inner class for menu listener*/
	private class MenuListener implements ActionListener
	{
		/**
			allows user to exit program or open options dialog
			
			@param e ActionEvent
		*/
		public void actionPerformed(ActionEvent e)
		{
			String command=e.getActionCommand();
			GameWindow mainWin=new GameWindow();
			
			if(command.equals("Exit"))
			{
				ConfirmWindow check=new ConfirmWindow("Are you sure you want to exit?",300,140,"stop_sign.gif",mainWin,"Confirmation",true);
				check.setVisible(true);
			}
			else if(command.equals("Options"))
				{
					//set up options dialog
					options=new OptionsDialog(mainWin,"Options",true,gridRow,gridCol,gridWidth,gridHeight,numberOfMines);	
					options.setVisible(true);
				}
				else
				{
					ConfirmWindow check=new ConfirmWindow("Unexpected gui error, do you want to exit?",300,140,"stop_sign.gif",mainWin,"Error",true);
					check.setVisible(true);
				}
		}
	}//end of inner class
	
	/**inner class for timer listener*/
	private class TimerListener implements ActionListener
	{
		/**
			registers time events
			@param e ActionEvent
		*/
		public void actionPerformed(ActionEvent e)
		{
			timeInSec++;
			if(timeInSec%60==0)//if a minute has passed
			{
				timeInMin++;
				timeInSec=0;
			}
			timeLabel.setText("Time taken: "+Integer.toString(timeInMin)+"m "+Integer.toString(timeInSec)+"s");
		}
	}
	
	/**inner class for reset button*/
	private class ResetListener implements MouseListener
	{
		/**
			resets grid with new set of randomly distributed mines
			@param e MouseEvent
		*/
		public void mouseClicked(MouseEvent e)
		{
			ImageIcon resetIcon=new ImageIcon("smiley_happy.gif");
			resetBtn.setIcon(resetIcon);
			initialise();
		}
		
		public void mouseEntered(MouseEvent e)
		{}	
		public void mouseExited(MouseEvent e)
		{}		
		public void mousePressed(MouseEvent e)
		{}		
		public void mouseReleased(MouseEvent e)
		{}
	}//end of reset listener
	
	/**inner class to capture windowevents*/
	private class MainListener implements WindowListener
	{
		/**
			confirma if user wants to close entire program 
			@param e WindowEvent
		*/
		public void windowClosing(WindowEvent e)
		{
			GameWindow mainWin=new GameWindow();
			ConfirmWindow check=new ConfirmWindow("Are you sure you want to exit?",300,140,"stop_sign.gif",mainWin,"Confirmation",true);
			check.setVisible(true);
		}
		
		public void windowOpened(WindowEvent e)
		{}		
		public void windowClosed(WindowEvent e)
		{}		
		public void windowIconified(WindowEvent e)
		{}
		public void windowDeiconified(WindowEvent e)
		{}
		public void windowActivated(WindowEvent e)
		{}
		public void windowDeactivated(WindowEvent e)
		{}
	}//end of main listener
}