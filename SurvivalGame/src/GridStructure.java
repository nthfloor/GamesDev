//class containing methods for grid spatial structure and all operations involved
//Nathan Floor (FLRNAT001)

import java.awt.geom.Point2D;
import java.util.Vector;


public class GridStructure {
	Vector<GridGameObject>[][] collisionGrid = null;
	private final int GRID_TILE_SIZE = 64;	
//	private int x,y;	
	
	public GridStructure(int x,int y, int width,int height){
		//initializations
//		this.x = x;
//		this.y = y;
		collisionGrid = new Vector[(width/GRID_TILE_SIZE)+1][(height/GRID_TILE_SIZE)+1];	
		
		
		//initialize grid structure
		for(int row = 0;row < collisionGrid[0].length; row++)
				for(int col = 0;col < collisionGrid.length;col++)
					collisionGrid[row][col] = new Vector<GridGameObject>();
	}
	
	//insert object into grid and update pointers to grid-tiles
	public void registerObject(GridGameObject object){
			//remove object from grid first
			deregisterObject(object);
			
			//height and width of texture
			int textWidth = object.getIntAABoundingBox().width/GRID_TILE_SIZE;
			int textHeight = object.getIntAABoundingBox().height/GRID_TILE_SIZE;
		
			int textX,textY,objX,objY;
			
			for(int i=0 ;i <= textHeight;i++){				
				for(int j=0;j <= textWidth;j++){
					textX = object.getIntAABoundingBox().x;
					textY = object.getIntAABoundingBox().y;
					
					//convert object's coord's to coord's for array
					objX = Math.round((textX-j*GRID_TILE_SIZE)/GRID_TILE_SIZE)+1;
					objY = Math.round((textY-i*GRID_TILE_SIZE)/GRID_TILE_SIZE)+1;
					
					if((objX >= 0) && (objX < collisionGrid.length) && (objY >= 0) && (objY < collisionGrid[0].length)){
						collisionGrid[objX][objY].add(object);
						object.setNewLocation(new Point2D.Float(objX,objY));
					}
				}					
			}		
		}
		
		//clear pointers to grid and remove object from grid
		public void deregisterObject(GridGameObject object){
			for(int i=0; i < object.getCapacity();i++){
				Point2D.Float tile = object.getLocation(i);
				collisionGrid[Math.round(tile.x)][Math.round(tile.y)].remove(object);
			}
			object.removeAllLocations();
		}
		
		public Vector<GridGameObject> checkNeighbouringTiles(GridGameObject object){	
			//vector of objects near to current object
			Vector<GridGameObject> neighbours = new Vector<GridGameObject>();			
			Point2D.Float tile;
			int tileX,tileY;
			
			//loop through tiles containing object to test for potential collisions
			for(int i=0;i < object.getCapacity();i++){
				tile = object.getLocation(i);
				tileX = Math.round(tile.x);
				tileY = Math.round(tile.y);
				
				checkForDuplicates(neighbours,tileX, tileY);
				checkForDuplicates(neighbours,tileX-1, tileY);
				checkForDuplicates(neighbours,tileX-1, tileY-1);
				checkForDuplicates(neighbours,tileX+1, tileY-1);
				checkForDuplicates(neighbours,tileX+1, tileY);
				checkForDuplicates(neighbours,tileX+1, tileY+1);
				checkForDuplicates(neighbours,tileX, tileY+1);
				checkForDuplicates(neighbours,tileX-1, tileY+1);
				checkForDuplicates(neighbours,tileX, tileY-1);
			}
			return neighbours;
		}
		
		//checking surrounding tiles for potential collisions
		private void checkForDuplicates(Vector<GridGameObject> nearByObj,int xcoord,int ycoord){	
			
			 if((xcoord >= 0) && (xcoord < collisionGrid.length) && (ycoord >= 0) && (ycoord < collisionGrid[0].length)){
				 for(int j = 0;j < collisionGrid[xcoord][ycoord].size(); j++)
					 if(! nearByObj.contains(collisionGrid[xcoord][ycoord].get(j)))
						 nearByObj.add(collisionGrid[xcoord][ycoord].get(j));
			 }
			 
		}

}//end
