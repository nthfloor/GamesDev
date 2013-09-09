import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.badlogic.gdx.utils.XmlWriter;


public class MainFrame extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Dimension screenSize;
	private JMenu mainMenu;
	private JMenuItem saveXMLOption;
	private JMenuItem exitOption;
	private JMenuBar menuBar;
	
	private JScrollPane scroller;
	private MapOutline mobileScreen;
	
	private String shape;
	private boolean object_selected;
	
	private JPanel objDetails;
	private JLabel objWidth;
	private SpinnerNumberModel numberModel;
	private JSpinner widthInput;
	private JLabel objHeight;
	private JSpinner heightInput;
	private JLabel objRotate;
	private JSpinner rotateInput;
	private JLabel dispersionlbl;
	private JSpinner dispInput;
	private JLabel obj_idlbl;
	private JSpinner idInput;
	private JLabel numberBoids;
	private JSpinner numBoidsInput;
	
	private XmlWriter xmlWriter;
	private ShapeDimensions grabbedObject;
	
	public MainFrame(int screenWidth,int screenHeight){
		super();
		screenSize = new Dimension(screenWidth, screenHeight);
		setLayout(new BorderLayout());
		
		setSize(screenSize);
		//initialize base layout
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);
		
		//initialize variables
		Dimension mapSize = new Dimension(800,10000);		
		shape = "selection";
		object_selected = false;
		
		//setup menu and options
		mainMenu = new JMenu("Options");		
		saveXMLOption = new JMenuItem("Save as XML");
		saveXMLOption.addActionListener(new MenuListener(this));	
		mainMenu.add(saveXMLOption);
		exitOption = new JMenuItem("Exit");
		exitOption.addActionListener(new MenuListener(this));
		mainMenu.add(exitOption);
		
		menuBar = new JMenuBar();
		menuBar.setAlignmentY(RIGHT_ALIGNMENT);
		menuBar.add(mainMenu);
		setJMenuBar(menuBar);		
		
		//buttons and icons	
		JPanel buttons = new JPanel(new GridLayout(7,2));
		buttons.setBounds(30, 15, (screenWidth/3)-70, screenHeight/2);
		add(buttons, BorderLayout.WEST);
		
		JButton selectionBtn = new JButton("Selection Tool");
		selectionBtn.addMouseListener(new ButtonListener(this));
		buttons.add(selectionBtn);
		
		JButton frogBtn = new JButton("Add Frog");
		frogBtn.addMouseListener(new ButtonListener(this));
		buttons.add(frogBtn);
		JButton spiderBtn = new JButton("Add Spider");
		spiderBtn.addMouseListener(new ButtonListener(this));
		buttons.add(spiderBtn);
		JButton flyTrapBtn = new JButton("Add FlyTrap");
		flyTrapBtn.addMouseListener(new ButtonListener(this));
		buttons.add(flyTrapBtn);
		JButton birdBtn = new JButton("Add Bird");
		birdBtn.addMouseListener(new ButtonListener(this));
		buttons.add(birdBtn);
		JButton flySpawnBtn = new JButton("Add Fly spawn point");
		flySpawnBtn.addMouseListener(new ButtonListener(this));
		buttons.add(flySpawnBtn);
		JButton tutorialBtn = new JButton("Add Tutorial");
		tutorialBtn.addMouseListener(new ButtonListener(this));
		buttons.add(tutorialBtn);
		
		//object details
		objDetails = new JPanel(new GridLayout(7,2));
		objDetails.setBounds(30, (screenSize.height/2)+30, (screenSize.width/3)-70, screenSize.height/4);		
				
		objWidth = new JLabel("Width:");
		objDetails.add(objWidth);
		numberModel = new SpinnerNumberModel(50,0,1000,1);
		widthInput = new JSpinner(numberModel);
		objDetails.add(widthInput);
		objHeight = new JLabel("Height:");
		objDetails.add(objHeight);
		numberModel = new SpinnerNumberModel(50,0,1000,1);
		heightInput = new JSpinner(numberModel);
		objDetails.add(heightInput);
		objRotate = new JLabel("Rotation:");
		objDetails.add(objRotate);
		numberModel = new SpinnerNumberModel(0,0,360,10);
		rotateInput = new JSpinner(numberModel);
		objDetails.add(rotateInput);
		dispersionlbl = new JLabel("Dispersion:");
		objDetails.add(dispersionlbl);
		dispInput = new JSpinner(numberModel);
		objDetails.add(dispInput);
		obj_idlbl = new JLabel("ID:");
		objDetails.add(obj_idlbl);
		numberModel = new SpinnerNumberModel(100,0,1000,1);
		idInput = new JSpinner(numberModel);
		objDetails.add(idInput);
		numberBoids = new JLabel("# of Flies:");
		objDetails.add(numberBoids);
		numberModel = new SpinnerNumberModel(10,0,1000,1);
		numBoidsInput = new JSpinner(numberModel);
		objDetails.add(numBoidsInput);
		
		JLabel updatelbl = new JLabel("");
		objDetails.add(updatelbl);
		JButton updateBtn = new JButton("Update");
		objDetails.add(updateBtn);
		add(objDetails);
		drawInputEdits();
		
		//scroll pane to simulate mobile device and scrolling background
		int x = screenSize.width/3;
		int y = 15;
		mobileScreen = new MapOutline(this,mapSize.width,mapSize.height);
		mobileScreen.setPreferredSize(mapSize);
		mobileScreen.addMouseListener(new MapListener(this, mobileScreen));
		mobileScreen.addMouseMotionListener(new MapListener(this, mobileScreen));
				
		scroller = new JScrollPane(mobileScreen);		
		scroller.setBounds(x, y, screenSize.width-x-15, screenSize.height-60); 
		scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroller.setVisible(true);
		scroller.addMouseMotionListener(new MapListener(this, mobileScreen));
		add(scroller, BorderLayout.EAST);		
	}
	
	public int getSelectedObjWidth(){return (Integer) widthInput.getValue();}
	public int getDispersion(){return (Integer)(dispInput.getValue());}
	public int getSelectedObjHeight(){return (Integer) heightInput.getValue();}

	//Object details& dimensions
	private void drawInputEdits(){
		if(shape.equals("circle") || shape.equals("square") || shape.equals("diamond") || shape.equals("triangle")){
			widthInput.setEnabled(true);
			heightInput.setEnabled(true);
			rotateInput.setEnabled(true);
			dispInput.setEnabled(false);
			idInput.setEnabled(false);		
			numBoidsInput.setEnabled(false);
		}
		else if(shape.equals("bench")){
			widthInput.setEnabled(false);
			heightInput.setEnabled(false);
			rotateInput.setEnabled(false);
			dispInput.setEnabled(true);
			idInput.setEnabled(false);
			numBoidsInput.setEnabled(true);
		}		
		else if(shape.equals("star")){
			widthInput.setEnabled(false);
			heightInput.setEnabled(false);
			rotateInput.setEnabled(false);
			dispInput.setEnabled(false);
			idInput.setEnabled(true);
			numBoidsInput.setEnabled(false);
		}	
		else if(shape.equals("selection")){
			widthInput.setEnabled(false);
			heightInput.setEnabled(false);
			rotateInput.setEnabled(false);
			dispInput.setEnabled(false);
			idInput.setEnabled(false);
			numBoidsInput.setEnabled(false);
		}
	}
	
	//saves map data to xml file
	private void writeToXML(){
		//ask user for file name
		String filename = JOptionPane.showInputDialog(this,"Enter xml filename:","Save as...",JOptionPane.OK_CANCEL_OPTION);

		if(filename != null){
			try{
				int pos = filename.indexOf(".");
				String temp;
				if(pos != -1)
					temp = filename.substring(pos+1);
				else
					temp = filename;
				
				if(temp.equals("xml"))			
					xmlWriter = new XmlWriter(new FileWriter(filename));
				else
					xmlWriter = new XmlWriter(new FileWriter(filename+".xml"));

				xmlWriter.write("<?xml version=\""+"1.0"+"\" encoding=\""+"UTF-8"+"\"?>\n");
				xmlWriter.element("map");
				xmlWriter = mobileScreen.compileXMLFile(xmlWriter);
				xmlWriter.pop();

				xmlWriter.flush();			
				xmlWriter.close();
			}catch(Exception e){e.printStackTrace();}		
		}
	}

	//inner class for menu listener
	private class MenuListener implements ActionListener{
		private MainFrame parent;

		public MenuListener(MainFrame frame){
			parent = frame;
		}

		public void actionPerformed(ActionEvent e){
			String command = e.getActionCommand();
			
			if(command.equals("Save as XML")){
				writeToXML();
			}
			else if(command.equals("Exit")){
				parent.dispose();
				System.exit(1);
			}

		}
	}//end of MenuListener

	//listener for buttons for adding objects to map, inner class
	private class ButtonListener implements MouseListener{
		private MainFrame parent;

		public ButtonListener(MainFrame owner){
			parent = owner;
		}

		public void mouseClicked(MouseEvent e){
			JButton caller = (JButton)e.getSource();			

			if(caller.getText().equals("Selection Tool")){
				parent.shape = "selection";
				parent.object_selected = false;
			}
			else if(caller.getText().equals("Add Frog")){
				parent.shape = "circle";
				parent.object_selected = true;
			}
			else if(caller.getText().equals("Add Spider")){
				parent.shape = "square";
				parent.object_selected = true;
			}
			else if(caller.getText().equals("Add FlyTrap")){	
				parent.shape = "diamond";
				parent.object_selected = true;
			}
			else if(caller.getText().equals("Add Bird")){	
				parent.shape = "triangle";
				parent.object_selected = true;
			}
			else if(caller.getText().equals("Add Fly spawn point")){	
				parent.shape = "bench";
				parent.object_selected = true;
			}
			else if(caller.getText().equals("Add Tutorial")){	
				parent.shape = "star";
				parent.object_selected = true;
			}
			drawInputEdits();
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {}
		@Override
		public void mouseExited(MouseEvent arg0) {}
		@Override
		public void mousePressed(MouseEvent arg0) {}
		@Override
		public void mouseReleased(MouseEvent arg0) {}
	}//end of ButtonListener inner class
	
	//Map panel mouse listener, inner class
	private class MapListener implements MouseListener, MouseMotionListener{
		private MainFrame parent;
		private MapOutline map;
		private boolean show_image;		

		public MapListener(MainFrame owner, MapOutline map){
			parent = owner;
			this.map = map;
			show_image = false;
		}

		//place object on map where user clicked.
		public void mouseClicked(MouseEvent e){
			int width = (Integer) widthInput.getValue();
			int height = (Integer) heightInput.getValue();
			float rotation = (Integer) rotateInput.getValue();
			int dispersion = (Integer) dispInput.getValue();
			int obj_id = (Integer) idInput.getValue();
			int x = e.getX()-width/2;
			int y = e.getY()-height/2;
			
			if(parent.shape.equals("selection")){
				//check if user right clicked
				if(e.getButton() == MouseEvent.BUTTON3){
					//delete selected object
					int response = JOptionPane.showConfirmDialog(parent, "Do you want to delete this object?","Confirmation",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if(response == JOptionPane.YES_OPTION)
						map.deleteObject((int)(e.getX()-scroller.getAlignmentX()), (int)(e.getY()-scroller.getAlignmentY()));
				}
			}
			else if(parent.shape.equals("circle")){	
				map.addObjectToMap("circle",rotation,0,0,0,new Dimension(width,height),x,y);
			}
			else if(parent.shape.equals("square")){
				map.addObjectToMap("square",rotation,0,0,0,new Dimension(width,height),x,y);
			}
			else if(parent.shape.equals("diamond")){	
				map.addObjectToMap("diamond",rotation,0,0,0,new Dimension(width,height),x,y);
			}
			else if(parent.shape.equals("triangle")){	
				map.addObjectToMap("triangle",rotation,0,0,0,new Dimension(width,height),x,y);
			}
			else if(parent.shape.equals("bench")){	
				map.addObjectToMap("bench",0,dispersion,0,0,new Dimension(width+dispersion,30),x-dispersion/2,y+20);
			}
			else if(parent.shape.equals("star")){	
				map.addObjectToMap("star",0,0,obj_id,0,new Dimension(25,25),x+10,y+20);
			}
							
		}

		@Override
		//display an image of object that user has selected to place on map
		public void mouseEntered(MouseEvent arg0) {
			if(parent.shape.equals("selection")){
				mobileScreen.setToolTipText("Right click to delete object");
			}
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			show_image = false;	
			map.stopDrawing();
			if(parent.shape.equals("selection")){
				mobileScreen.setToolTipText("");
			}
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if(parent.shape.equals("selection")){		
				grabbedObject = map.grabObject((int)(e.getX()-scroller.getAlignmentX()),(int)(e.getY()-scroller.getAlignmentY()));
			}			
		}

		@Override
		public void mouseReleased(MouseEvent e) {			
			if(parent.shape.equals("selection") && (grabbedObject != null)){
				map.addObjectToMap(grabbedObject);
				grabbedObject = null;
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {		
			if(parent.shape.equals("selection") && (grabbedObject != null)){
				grabbedObject.X = (int)(e.getX()-scroller.getAlignmentX()-grabbedObject.shapeSize.width/2);
				grabbedObject.Y = (int)(e.getY()-scroller.getAlignmentY()-grabbedObject.shapeSize.height/2);
				map.drawShapes(grabbedObject.object,grabbedObject.X,grabbedObject.Y);				
			}
		}

		@Override
		//move shape around map
		public void mouseMoved(MouseEvent e) {
			if(parent.object_selected)
				show_image = true;
			if(show_image){
//				System.out.println("Moved: "+parent.shape+" : "+e.getX()+" : "+e.getY());
				map.drawShapes(parent.shape,e.getX(),e.getY());
			}
		}
	}

}
