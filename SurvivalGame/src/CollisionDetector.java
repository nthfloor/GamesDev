//Class to manage collisions
//Nathan Floor (FLRNAT001)

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.nio.IntBuffer;
import java.util.Vector;
import GameEngine.GameTexture;

//class to deal with collisions 

public class CollisionDetector {
	private GridStructure grid = null;
	
	public CollisionDetector(int x,int y,int width,int height) {
		//initialise grid structure
		grid = new GridStructure(x, y, width, height);		
	}
	
	//check collisions grid, then bounding box then pixel tests	
	public boolean checkForCollision(Vector<GridGameObject> objects, GridGameObject o1, GridGameObject o2){
//		
//		Vector<GridGameObject> collisions = checkGridCollisions(objects,o1);
//		GridGameObject tempObj = null;
//		
//		for(int i=0;i < collisions.size();i++){
//			tempObj = collisions.elementAt(i);
//			
//			//check o1& o2 are close enough to collide
//			if(tempObj == o2){
//				System.out.println(collisions.size());
			
				//bounding box check	
				if(boxIntersectBox(o1.getAABoundingBox(), o2.getAABoundingBox())){					
					//pixel level test				
					if (o1 instanceof WallObject && o2 instanceof WallObject){
						//do nothing
						//since we don't want the walls doing pixel tests all the time
					}
					else if(o1 instanceof EnemyStructure && o2 instanceof WallObject){
						
					}
					else if(o2 instanceof EnemyStructure && o1 instanceof WallObject){
						
					}
					else 
						if(checkPixels(o1,o2,o1.getCurrentTexture(),o2.getCurrentTexture()))
							return true;				
				}
//			}
//		}
		
		return false;
	}
	
	//check nearby if object1 is close enough to object2 to collide
	public Vector<GridGameObject> checkGridCollisions(Vector<GridGameObject> objects,GridGameObject tempObject1){
//		GridGameObject tempObject1 = null;
		GridGameObject tempObject2 = null;			   
		Vector<GridGameObject> neightbourTiles = new Vector<GridGameObject>();
		Vector<GridGameObject> temp = new Vector<GridGameObject>();
		
		neightbourTiles = grid.checkNeighbouringTiles(tempObject1);
		
//			tempObject1 = objects.elementAt(i);		
			
		for(int j=0;j < neightbourTiles.size();j++){
			tempObject2 = neightbourTiles.get(j);
				
			//check if its the same tile
			if(tempObject1 != tempObject2){						
				temp.add(tempObject2);						
			}
		}	
		return temp;
	}
	
	public void registerOnGrid(GridGameObject object){
		grid.registerObject(object);
	}
	
	public void deregisterFromGrid(GridGameObject object){
		grid.deregisterObject(object);
	}
	
	//NOTE: next two methods provided with original code
	private static boolean isPointInBox(final Point2D.Float point, final Rectangle2D.Float box) {
        return box.contains(point.x, point.y);
    }
    
    // This is a pretty bad implementation and faster ones exist, it is suggested you find a better one. At least try make use of the Rectangle2D's createIntersection method.
    public static boolean boxIntersectBox (final Rectangle2D.Float d, final Rectangle2D.Float d2) {
       return  isPointInBox(new Point2D.Float (d.x, d.y), d2) ||
                isPointInBox(new Point2D.Float (d.x, d.y+d.height), d2) ||
                isPointInBox(new Point2D.Float (d.x+d.width, d.y), d2) ||
                isPointInBox(new Point2D.Float (d.x+d.width, d.y+d.height), d2) ||
                isPointInBox(new Point2D.Float (d2.x, d2.y), d) ||
                isPointInBox(new Point2D.Float (d2.x, d2.y+d2.height), d) ||
                isPointInBox(new Point2D.Float (d2.x+d2.width, d2.y), d) ||
                isPointInBox(new Point2D.Float (d2.x+d2.width, d2.y+d2.height), d);                
    }
	
	//tests for a pixel collision using bitmasks
	public boolean checkPixels(GridGameObject o1, GridGameObject o2, GameTexture texture1, GameTexture texture2)
	{
		// initialization 		
		Rectangle image1 = o1.getIntAABoundingBox();
		Rectangle image2 = o2.getIntAABoundingBox();		
		Rectangle overlap = image1.intersection(image2);
		
		int x1 = Math.abs((int)overlap.x - (int)image1.getX()),
				x2 = Math.abs((int)overlap.x - (int)image2.getX()),
				y1 = Math.abs((int)overlap.y - (int)image1.getY()),
				y2 = Math.abs((int)overlap.y - (int)image2.getY());
		
		IntBuffer buffer1 = texture1.getIntBuffer();
	    IntBuffer buffer2 = texture2.getIntBuffer();
	    int[][] object1Array = new int[(int)texture1.getHeight()][(int)texture1.getWidth()];
	    int[][] object2Array = new int[(int)texture2.getHeight()][(int)texture2.getWidth()];	    
		
		//read images into integer arrays
		int x,y;
		for(y=0;y < (int)texture1.getHeight();y++)
			for(x=0;x < (int)texture1.getWidth();x++)
				object1Array[y][x] = buffer1.get();
			
		for(y=0;y<(int)texture2.getHeight();y++)
			for(x=0;x<(int)texture2.getWidth();x++)
				object2Array[y][x] = buffer2.get();		
		
		//loop through intersection rectangle	
		for (y=0;y < overlap.height;y++){ 
			for (x=0;x < overlap.width;x++) {     
				try {					
					if((object1Array[y + y1][x + x1] != 0) && (object2Array[y + y2][x + x2] != 0)){				
							// collide!!	
							return true;	
						}     
					} catch (Exception e) {
						e.printStackTrace();
						System.exit(0);
						}  
			} 
		}  
		return false;		
	}
}
