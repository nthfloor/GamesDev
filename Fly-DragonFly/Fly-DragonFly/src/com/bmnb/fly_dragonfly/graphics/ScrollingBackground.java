package com.bmnb.fly_dragonfly.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * This class holds the methods required to draw the background which scrolls
 * dynamically
 * 
 * @author Brandon James Talbot
 * 
 */

public class ScrollingBackground {

	/**
	 * Globals
	 */
	protected Sprite[] sprites;
	protected float width, height, speed;

	/**
	 * Constructor
	 * 
	 * @param textures
	 *            path to the textures
	 * @param scwidth
	 *            The screen width
	 * @param scheight
	 *            The screen height
	 * @param speed
	 *            The speed for the scrolling
	 */
	public ScrollingBackground(String[] textures, float scwidth,
			float scheight, float speed) {
		sprites = new Sprite[textures.length];
		width = scwidth;

		for (int i = 0; i < textures.length; ++i) {
			Texture t = new Texture(textures[i]);
			height = width / t.getWidth() * t.getHeight();
			sprites[i] = new Sprite(t);
			sprites[i].setSize(width, height);
			sprites[i].setPosition(0, i * height);
			sprites[i].setColor(1.0f, 1.0f, 1.0f, 1);
		}

		this.speed = speed;
	}

	/**
	 * Updates the backgrounds positions
	 * @param delta The games delta change
	 */
	public void update(float delta) {
		for (int i = 0; i < sprites.length; ++i) {
			sprites[i].translateY(-speed * delta);

			// move the tex above when it leaves the screen
			if (sprites[i].getY() + height <= 0) {
				sprites[i].translateY(height * sprites.length);
			}
		}
	}

	/**
	 * Draws the background
	 * 
	 * @param batch
	 *            Sprite batch to draw with
	 * @param delta
	 *            The delta time for the game
	 */
	public void draw(SpriteBatch batch, float delta) {
		for (Sprite s : sprites)
			s.draw(batch);
	}
}
