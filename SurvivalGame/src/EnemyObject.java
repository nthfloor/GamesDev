import java.awt.geom.Point2D;

//represents enemy objects that can move and attack
//Nathan Floor
//FLRNAT001

public class EnemyObject extends GridGameObject{
	int numberOfDirectionTextures = 72;
    float direction;
    public enum EnemyType {GROUND,AIR,TANK};
    private EnemyType type;
    Point2D.Float oldPosition;
    private int speed = 1;
    private boolean canExplode = false;
    
	//==================================================================================================
    public EnemyObject (float x, float y,int ihealth,int iCooldown) {
        super (x, y,ihealth,iCooldown);
        
        oldPosition = new Point2D.Float(x - 10, y - 10);
    }
    
    public void setEnemyType(EnemyType t){
    	type = t;
    }
    public EnemyType getEnemyType(){
    	return type;
    }
    
    //allows some enemy objects to have different speeds
    public void setSpeed(int s){
    	speed = s;
    }
    
    //methods to allow certain enemies(tanks& heli's) to explode
    public void setCanExplode(boolean b){
    	canExplode = b;
    }
    public boolean canEnemyExplode(){
    	return canExplode;
    }
    
    //==================================================================================================
    
    public void setDirection(float direction) {
        while (direction < 0.0) direction += 360.0;
        while (direction >= 360.0) direction -= 360.0;
        
        this.direction = direction;
        
        // setting the correct texture
        float offsetDirection = direction+(360.0f/numberOfDirectionTextures)/2.0f;
        while (offsetDirection >= 360.0) offsetDirection -= 360.0;
        
        setActiveTexture((int)((float)(((offsetDirection)/360.0))*numberOfDirectionTextures));
    }
    
    public void revertPosition () {
    	this.setPosition(oldPosition);
    }
    
    public float getDirection() {
		return direction;
	}
    
    //moves object
	public void moveInDirection(float direction) {
		oldPosition = this.getPosition();
		incrementPosition((float)Math.sin(Math.toRadians(direction)*speed), -(float)Math.cos(Math.toRadians(direction)*speed));
		setDirection(direction);
	}
}
