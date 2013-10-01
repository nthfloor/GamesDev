package com.bmnb.fly_dragonfly.flocking;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
/**
 * Firefly boid
 * @author benjamin
 */
public class FireFly extends Boid {

	public FireFly(Vector2 position, Vector2 direction, float width,
			float height, float scWidth, float scHeight) {
		super(position, direction, width, height, scWidth, scHeight);
		this.setTexture(new Texture("data/textures/firefly.png"));
	}

}
