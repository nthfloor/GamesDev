package com.bmnb.fly_dragonfly.graphics;

import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.bmnb.fly_dragonfly.objects.Player;

/**
 * Fancy image-based progress bar class (supports vertical and horizontal
 * progress indication)
 * 
 * @author benjamin
 * 
 */
public class Meter extends Sprite {
	protected float screenWidth, screenHeight, progress;
	protected boolean vertProgress;
	protected Texture bg, fg;
	protected TweenManager manager;
	protected Player player;

	/**
	 * Default constructor
	 * 
	 * @param position
	 *            of top left corner
	 * @param width
	 *            of progress bar
	 * @param height
	 *            of progress bar
	 * @param scWidth
	 *            screen width
	 * @param scHeight
	 *            screen height
	 * @param vertProgress
	 *            should the progress be indicated vertically
	 * @param texFG
	 *            foreground texture
	 * @param texBG
	 *            background texture
	 * @param progress
	 *            intial progress
	 */
	public Meter(Vector2 position, float width, float height, float scWidth,
			float scHeight, boolean vertProgress, Texture texFG, Texture texBG,
			float progress, Player player) {
		super();
		this.player = player;
		setSize(width, height);
		setPosition(position);
		this.progress = progress;
		this.screenWidth = scWidth;
		this.screenHeight = scHeight;
		this.vertProgress = vertProgress;
		assert (texBG != null);
		assert (texFG != null);
		bg = texBG;
		fg = texFG;

		setColor(1, 1, 1, 1);
	}

	/**
	 * Position setter
	 * 
	 * @param pos
	 */
	public void setPosition(Vector2 pos) {
		this.setX(pos.x - this.getWidth() / 2f);
		this.setY(pos.y - this.getHeight() / 2f);
	}

	/**
	 * Position getter
	 * 
	 * @return vector of x and y
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

	@Override
	public void setTexture(Texture texture) {
		super.setTexture(texture);
		setRegion(0, 0, texture.getWidth(), texture.getHeight());
		setSize(Math.abs(getWidth()), Math.abs(getHeight()));
		setOrigin(getWidth() / 2, getHeight() / 2);
	}

	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {

		float[] col = player.getFireColour();
		// Gdx.app.log("Alpha", this.getColor().a + "");
		// First draw full background texture
		this.setTexture(bg);
		super.draw(spriteBatch);
		// then draw portion of the foreground texture
		this.setTexture(fg);
		this.setColor(col[0], col[1], col[2], 1);
		float oldWidth = this.getWidth(), oldHeight = this.getHeight();
		if (this.vertProgress) {
			setRegion(0,
					(int) (fg.getHeight() - this.progress * fg.getHeight()),
					fg.getWidth(), (int) (this.progress * fg.getHeight()));
			setSize(Math.abs(getWidth()), Math.abs(this.progress * getHeight()));
			setOrigin(getWidth() / 2, getHeight() / 2);
		} else {
			setRegion(0, 0, (int) (progress * fg.getWidth()), fg.getHeight());
			setSize(Math.abs(this.progress * getWidth()), Math.abs(getHeight()));
			setOrigin(getWidth() / 2, getHeight() / 2);
		}
		super.draw(spriteBatch);

		// restore size
		this.setSize(oldWidth, oldHeight);
		this.setColor(1, 1, 1, 1);
	}

	/**
	 * gets the progress between 1 and 0
	 * 
	 * @return progress
	 */
	public float getProgress() {
		return progress;
	}

	/**
	 * sets the progess (expects something between 1 and 0 inclusive)
	 * 
	 * @param progress
	 */
	public void setProgress(float progress) {
		this.progress = Math.max(Math.min(progress, 1), 0);
	}
}
