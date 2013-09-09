import java.awt.Dimension;

//class for storing gameObject shapes on map
public class ShapeDimensions{
	public Dimension shapeSize;
	public int X,Y;
	public String object;
	public float rotation;
	public int dispersion;
	public int id;
	public int numberOfBoids;

	public ShapeDimensions(String shape,float r,int disp,int id,int numBoids,Dimension size, int x, int y){
		shapeSize = new Dimension(size);
		this.X = x;
		this.Y = y;

		numberOfBoids = numBoids;
		if(shape.equals("circle"))
			object = "frog";
		else if(shape.equals("square"))
			object = "spider";
		else if(shape.equals("diamond"))
			object = "flytrap";
		else if(shape.equals("triangle"))
			object = "bird";
		else if(shape.equals("bench"))
			object = "mosquitoes";
		else if(shape.equals("star"))
			object = "tutorial";
		else 
			object = shape;

		this.rotation = r;
		dispersion = disp;
		this.id = id;
	}
}
