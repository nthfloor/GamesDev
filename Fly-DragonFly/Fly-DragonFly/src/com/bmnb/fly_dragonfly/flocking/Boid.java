package com.bmnb.fly_dragonfly.flocking;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.bmnb.fly_dragonfly.objects.GameObject;
/**
 * Generic Boid class
 * @author benjamin
 *
 */
public class Boid extends GameObject{
	private static final float MAX_SPEED = 210f;
	private static final float MIN_SPEED = 200f;
	
	private Vector2 oldPosition;
	/**
	 * Default constructors
	 * @param position
	 * @param direction
	 * @param width of boid
	 * @param height of boid
	 * @param scWidth of screen
	 * @param scHeight of screen
	 */
	public Boid(Vector2 position, Vector2 direction, float width,
			float height, float scWidth, float scHeight){
		super(position,width,height,(float)Math.random()*MAX_SPEED,scWidth,scHeight);
		this.direction = direction; 
		setTexture(new Texture("data/boid.png"));
		
		sortVal = 1;
	}
	
	@Override
	public void kill() {
		this.removeable = true;
		super.kill();
	}

	/**
	 * Sets speed for the boid
	 * @param i
	 */
	public void setSpeed(float i){
		assert(i >= 0);
		this.speed = i;
	}
	/**
	 * gets old position
	 * @return old position
	 */
	public Vector2 getOldPosition() {
		return oldPosition.cpy();
	}
	/**
	 * Sets old position
	 * @param oldPosition
	 */
	public void setOldPosition(Vector2 oldPosition) {
		this.oldPosition = oldPosition;
	}
	/**
	 * gets the velocity vector (direction*speed)
	 * @return velocity vector2
	 */
	public Vector2 getVelocity() {
		return direction.cpy().mul(speed);
	}
	/**
	 * Sets the velocity (extracts speed and direction from the input)
	 * @param velocity
	 */
	public void setVelocity(Vector2 velocity) {
		this.speed = velocity.len();
		this.direction = velocity.cpy().nor();
	}
	@Override
	protected void move(float delta) {
		setOldPosition(getPosition());
		speed = Math.max(MIN_SPEED, Math.min(speed, MAX_SPEED)); //clamp speed 
		setPosition(getPosition().add(getVelocity().mul(delta)));
	}
	@Override
	public void draw(SpriteBatch batch, float delta){
		this.rotate(direction.angle());
		super.draw(batch,delta);
		this.rotate(-direction.angle());
	}
}
