package com.bmnb.fly_dragonfly.objects;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.bmnb.fly_dragonfly.graphics.GameParticleEmitter;

public class VenusFlytrap extends StaticEnemy {
	private static final float VACUUM_RADIUS = 2.5f;
	private static final float MAX_PULL_SPEED = 200;
	private static final float ALPHA = 0.000002f;
	private static final float COUNT_DOWN_MAX = 20;
	private float countDown = COUNT_DOWN_MAX;
	private GameParticleEmitter poisonGas;

	public VenusFlytrap(Vector2 position, float width, float height,
			float speed, float scWidth, float scHeight, Player player) {
		super(position, width, height, speed, scWidth, scHeight, player, 50);

		sortVal = 2;

		if (this.getPosition().x < screenWidth/2f)
			setTexture(new Texture("data/textures/flytrap_left.png"));
		else
			setTexture(new Texture("data/textures/flytrap_right.png"));
		try {
			poisonGas = new GameParticleEmitter(new BufferedReader(
					new InputStreamReader(Gdx.files
							.internal("data/particleEffects/flytrapSpit").read()), 512),
					new Texture("data/particleEffects/particle.png"),
					GameParticleEmitter.ParticleType.spit);

			poisonGas.setContinuous(false);
		} catch (Exception e) {
			Gdx.app.log("error", e.getMessage());
		}
	}
	
	@Override
	public Rectangle getBoundingRectangle() {
		Rectangle r =  super.getBoundingRectangle();
		
		//r.x += this.getWidth() * 0.38f;
		r.width -= this.getWidth() * 0.2f;
		r.height -= this.getHeight() * 0.2f;
		return r;
	}

	@Override
	public void update(float delta) {
		super.update(delta);

		// this is for death animation
		if (isDead()) {
			removeable = true; // do anim till its donw then call this
		}

		poisonGas.setPosition(this.getX(), this.getY());

		if (!isDead()) {
			Vector2 v = (this.getPosition().sub(player.getPosition()));
			float radSq = VACUUM_RADIUS * VACUUM_RADIUS * (new Vector2(this.getWidth()/2,this.getHeight()/2)).len2();
			if (v.len2() < radSq) {
				player.setPosition(player
						.getPosition()
						.add(v.nor()
								.mul(MAX_PULL_SPEED
										* (float) Math.pow(1 - v.len()
												/ VACUUM_RADIUS, ALPHA) * delta)));
				player.setPosition(player.getX(), Math.max(player.getY(), player.getHeight()/2));
			}
			if (countDown-- <= 0) {
				poisonGas.setContinuous(true);
				countDown = COUNT_DOWN_MAX;
			} else
				poisonGas.setContinuous(false);
		}

	}

	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {
		super.draw(spriteBatch, delta);
		
		poisonGas.draw(spriteBatch, delta);
	}
}
