

import java.awt.geom.Point2D;

class PlayerObject extends GridGameObject {
    
    int numberOfDirectionTextures = 72;
    float direction;    
    Point2D.Float oldPosition;
    private Weapon currentWeapon;
    private int autoAmmo = 50;
    private int shotgunAmmo = 10;
    private int rifleAmmo = 7;
    private int rocketAmmo = 5;
    private int ammoPickedUp = 0;
    public enum Weapon{AUTOMATIC,SHOTGUN,RIFLE,ROCKET};
    
	//==================================================================================================
    public PlayerObject (float x, float y, int health,int iCooldown) {
        super (x, y,health,iCooldown);
        currentWeapon = Weapon.AUTOMATIC;
        oldPosition = new Point2D.Float(x - 100, y - 100);
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
    
    public int getAmmo(){
    	if(currentWeapon == Weapon.AUTOMATIC)
    		return autoAmmo;
    	else if(currentWeapon == Weapon.SHOTGUN)
    		return shotgunAmmo;
    	else if(currentWeapon == Weapon.RIFLE)
    		return rifleAmmo;
    	else if(currentWeapon == Weapon.ROCKET)
    		return rocketAmmo;
    	else			
    		return 50;
    }
    
    public int increaseAmmo(){
    	double prob = Math.random();
    	
    	if(prob < 0.4){
    		autoAmmo += 25;
    		ammoPickedUp = 25;
    		return 0;
    	}
    	else if(prob < 0.6){
    		shotgunAmmo += 5;
    		ammoPickedUp = 5;
    		return 1;
    	}
    	else if(prob < 0.8){
    		rifleAmmo += 5;
    		ammoPickedUp = 5;
    		return 2;
    	}
    	else if(prob <= 1.0){
    		rocketAmmo += 2;
    		ammoPickedUp = 2;
    		return 3;
    	}
    	else
    		return -1;
    }
    
    public int getPickedAmmo(){
    	return ammoPickedUp;
    }
    
    public void decreaseAmmo(){
    	if(currentWeapon == Weapon.AUTOMATIC)
    		autoAmmo--;
    	else if(currentWeapon == Weapon.SHOTGUN)
    		shotgunAmmo--;
    	else if(currentWeapon == Weapon.RIFLE)
    		rifleAmmo--;
    	else if(currentWeapon == Weapon.ROCKET)
    		rocketAmmo--;
    }
    
    public void revertPosition () {
    	this.setPosition(oldPosition);
    }
    
    public float getDirection() {
		return direction;
	}

    //change weapon
    public void setWeapon(Weapon w){
    	currentWeapon = w;
    }
    public Weapon getWeapon(){
    	return currentWeapon;
    }
    

	public void moveInDirection(float direction) {
		oldPosition = this.getPosition();
		incrementPosition((float)Math.sin(Math.toRadians(direction))*2, -(float)Math.cos(Math.toRadians(direction))*2);
		setDirection(direction);
	}
}
