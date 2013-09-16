


public class BulletObject extends PhysicalObject {
    private int destroyTimer = 0;
    public GridGameObject sourceObj;
    
    public BulletObject (float x, float y, float m, int time, int health,int iCooldown,GridGameObject source) {
        super (x, y,health, m,iCooldown);
        setDestroyTimer(time);
        sourceObj = source;
    }
    
    public void setDestroyTimer(int time) {
        destroyTimer = time;
    }
    
    public void doTimeStep() {
        destroyTimer--;
        if (destroyTimer == 0)
            setMarkedForDestruction(true);
        
        super.doTimeStep();
    }
    
    public GridGameObject getSource(){
    	return sourceObj;
    }
    
    public void setSource(GridGameObject source){
    	sourceObj = source;
    }
}
