/**
	Create confirm window for main program for shutting-down created on the 6 oct 2010
	@author nathan floor
*/

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ImageIcon;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Color;

class ConfirmWindow extends JDialog implements ActionListener
{
	/**
		Constructor which initialises confirm dialog with all its components
		
		@param text message for dialog
		@param width Dialog width
		@param height Dialog height
		@param iconName file name of icon to be used in dialog
		@param owner main window for which dialog was created
		@param title title of dialog window
		@param modal determines whether you can access owner window while dialog is still open
	*/
	public ConfirmWindow(String text,int width,int height,String iconFileName,JFrame owner, String title, boolean modal)
	{
		super(owner,title);	
		setLayout(new BorderLayout());
		setModal(modal);
		setBounds(200,200,width,height);
		
		//panel for icon and message
		JPanel messagePane=new JPanel();
		add(messagePane,BorderLayout.CENTER);
		messagePane.setLayout(new FlowLayout());
		
		ImageIcon messageIcon=new ImageIcon(iconFileName);
		JLabel iconLabel=new JLabel(messageIcon);
		messagePane.add(iconLabel);
		
		JLabel lblConfirm=new JLabel(text);
		messagePane.add(lblConfirm);
		
		//confirm button panel
		JPanel buttonPane=new JPanel();
		add(buttonPane,BorderLayout.SOUTH);
		buttonPane.setLayout(new FlowLayout());		
		JButton exitBtn=new JButton("Yes");
		exitBtn.addActionListener(this);
		buttonPane.add(exitBtn);		
		JButton cancelBtn=new JButton("No");
		cancelBtn.addActionListener(this);
		buttonPane.add(cancelBtn);		
	}
	
	/**
		Closes entire program if yes is selected or else does nothing
		@param e ActionEvent
	*/
	public void actionPerformed(ActionEvent e)
	{
		String command=e.getActionCommand();
		
		if(command.equals("Yes"))
		{System.exit(0);}
		else if(command.equals("No"))
			{dispose();}
			else
			{System.out.println("Unexpected error in confirmwindow.");}
	}
}