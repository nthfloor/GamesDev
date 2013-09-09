/**
	Information and properties of each grid button 
		
	@author nathan floor
	@version 30 sep 2010
*/

import javax.swing.JButton;

class GridButton extends JButton
{
	//instance varibles
	private enum GridBtnType{MINE, NUMBER, NOTHING};/**Indicates type of button*/
	private GridBtnType type;/**stores button type*/
	private int numberOfMines;/**Number of mines adjacent to*/
	private int iRow;/**row coordinate*/
	private int iCol;/**column coordinate*/
	private boolean clicked;/**indicates whether button has already been clicked*/
	private boolean flagged;/**indicates whether button has been flagged*/
	
	/**
		constructor to create button and initialse variables with defualt values
		
		@param row row coordinate
		@param col column coordinate
	*/	
	public GridButton(int row,int col)
	{
		super();	
		iRow=row;
		iCol=col;
		clicked=false;
		flagged=false;
	}
	
	/**
		Changes the number of mines adjacent to button
		@param numMines new number of mines
	*/
	public void setNumber(int numMines)
	{numberOfMines=numMines;}
	
	/**
		returns current number f adjacent mines
	*/
	public int getNumber()
	{return numberOfMines;}
	
	/**
		Changes the type of the button
		@param t new button type
	*/
	public void setType(String t)
	{type=GridBtnType.valueOf(t);}
	
	/**returns the current button type*/ 
	public String getType()
	{return type.toString();}
	
	/**returns the current row coordinate*/
	public int getRow()
	{return iRow;}
	
	/**returns the current column coordinate*/
	public int getCol()
	{return iCol;}
	
	/**
		Changes the clicked state of the button
		@param b new clicked state
	*/
	public void setClicked(boolean b)
	{clicked=b;}
	
	/**returns the current clicked state of button*/
	public boolean isClicked()
	{return clicked;}
	
	/**
		Changes the flagged status of the button
		@param bf new flagged status
	*/
	public void setFlagged(boolean bf)
	{flagged=bf;}
	
	/**returns the current flagged state of button*/
	public boolean isFlagged()
	{return flagged;}
	
	/**returns true if button is of a mine type*/
	public boolean isMine()
	{
		if(type==GridBtnType.MINE)
		{return true;}
		else{return false;}
	}
}