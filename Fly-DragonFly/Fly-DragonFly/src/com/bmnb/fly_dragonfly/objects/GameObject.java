/**
 * 
 */
package com.bmnb.fly_dragonfly.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.bmnb.fly_dragonfly.tools.SpriteAnimator;

/**
 * @author Brandon
 * 
 */
public abstract class GameObject extends Sprite implements Comparable<GameObject>{

	/**
	 * global vars
	 */
	protected boolean dead = false, removeable = false;
	protected float screenWidth, screenHeight, speed;
	protected Vector2 direction;
	protected SpriteAnimator animator = null;
	protected int sortVal = 0;	
	
	public GameObject(Vector2 position, float width, float height, float speed,
			float scWidth, float scHeight) {
		super();
		setSize(width, height);
		setPosition(position);

		this.speed = speed;
		this.screenWidth = scWidth;
		this.screenHeight = scHeight;
		this.direction = new Vector2();
	}

	/**
	 * @param sprite
	 */
	public GameObject(Sprite sprite) {
		super(sprite);
	}

	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {
		super.draw(spriteBatch);
//		update(delta);
	}

	/**
	 * Updates the object
	 * 
	 * @param delta
	 *            The delta time for the game
	 */
	public void update(float delta) {
		move(delta);
		if (animator != null)
			animator.update(delta);
	}

	/**
	 * Moves the object
	 * 
	 * @param delta
	 *            the delta time for the gamme
	 */
	protected abstract void move(float delta);

	@Override
	public void setPosition(float x, float y) {
		this.setX(x - this.getWidth() / 2f);
		this.setY(y - this.getHeight() / 2f);
	}

	/**
	 * setter for position
	 * 
	 * @param pos
	 *            position for the object
	 */
	public void setPosition(Vector2 pos) {
		this.setX(pos.x - this.getWidth() / 2f);
		this.setY(pos.y - this.getHeight() / 2f);
	}

	/**
	 * Gets the postion as a vector 2
	 * 
	 * @return A vector2 for the objects position
	 */
	public Vector2 getPosition() {
		return new Vector2(this.getX(), this.getY());
	}

	@Override
	public float getX() {
		return super.getX() + this.getWidth() / 2f;
	}

	@Override
	public float getY() {
		return super.getY() + this.getHeight() / 2f;
	}

	/**
	 * Getter for the dead boolean
	 * 
	 * @return Whether the object is dead or not
	 */
	public boolean isDead() {
		return dead;
	}

	/**
	 * Kills the object
	 */
	public void kill() {
		dead = true;
	}

	/**
	 * Checks if the object is now removeable (for death animations)
	 * 
	 * @return If the object is removeable
	 */
	public boolean isRemovable() {
		return removeable;
	}
	
	@Override
	public void setTexture(Texture texture) {
		texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		super.setTexture(texture);
		setRegion(0, 0, texture.getWidth(), texture.getHeight());
//		setColor(1, 1, 1, 1);
		setSize(Math.abs(getWidth()), Math.abs(getHeight()));
		setOrigin(getWidth() / 2, getHeight() / 2);
	}
	
	public void setRemovable(){
		removeable = true;
	}

	@Override
	public int compareTo(GameObject o) {
		return this.sortVal < o.sortVal ? 1 : this.sortVal > o.sortVal ? -1 : 0;
	}	
}