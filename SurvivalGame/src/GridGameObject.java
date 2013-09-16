//class to help facilitate grid collision detection
//extends GameObject
//Nathan Floor (FLRNAT001)

import java.awt.geom.Point2D;
import java.util.Vector;

import GameEngine.GameObject;


public class GridGameObject extends GameObject{
	private Vector<Point2D.Float> locations;
	protected int health;
	private int maxHealth;
	private int cooldownTimer;
	protected boolean isMoving;
	protected boolean hasBeenHit;
	private int cooldown;
	private int damage;
	private int healthcooldown = 150;
	
	/**
     * Basic Constructor for the GameObject
     *
     * @param x Position along the x coordinate
     * @param y Position along the y coordinate
     */
	public GridGameObject(float x, float y,int iHealth,int iCooldown){
		super(x,y);		
		locations = new Vector<Point2D.Float>();
		health = iHealth;
		maxHealth = iHealth;
		cooldown = iCooldown;
		cooldownTimer = 0;
		damage = 10;
	}
	
	public void setNewLocation(Point2D.Float newPositon){
		if(locations != null)
			locations.add(newPositon);
	}
	
	public Point2D.Float getLocation(int index){
		if(locations != null)
			return locations.get(index);
		else
			return null;
	}
	
	public int getCapacity(){
		if(locations == null)
			return 0;
		else
			return locations.size();
	}
	
	public void removeAllLocations(){
		if(locations != null)
			locations.clear();
	}
	
	// this method is used to fire a bullet 
    public int getCoolDown() {    	
        return cooldownTimer;     
    }
    public void setCoolDown(int cooldown){
    	cooldownTimer = cooldown;
    }
    public void decrementCoolDown(){
    	cooldownTimer--;
    }
    public void setCoolDownRate(int c){
    	cooldown = c;
    }
    public int getCoolDownRate(){
    	return cooldown;
    }
    
    //damage
    public void setDamage(int d){
    	damage = d;
    }
    public int getDamage(){
    	return damage;
    }

  //calculates distance from current object to desired object
    public double getDistanceTo(GridGameObject obj){
    	float a = obj.getPosition().x - this.position.x;
    	float b = obj.getPosition().y - this.position.y;
    	double distance = Math.sqrt(a*a + b*b);    	
    	
    	return distance/2;
    }
    
	//decreases health by specified amount
	public void decreaseHealth(int idamage){
		if(health <= idamage){
			setMarkedForDestruction(true);
			SurvivalGame.enemies.remove(this);
		}
		else if(health > idamage)
			health -= idamage;	
		
		if(! hasBeenHit)
			hasBeenHit = true;
	}
	
	//regenerates health
	public void doTimeStep(){
		if((this instanceof PlayerObject)&&(health <= (maxHealth-10))){
			if(healthcooldown <= 0){
				health += 10;
				healthcooldown = 150;
			}
			else
				healthcooldown--;
		}
	}
	
	public float getPercentage(){
		return (float)((float) health / (float) maxHealth);
	}
}
