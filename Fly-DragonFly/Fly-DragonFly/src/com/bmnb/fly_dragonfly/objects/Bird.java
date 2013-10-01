package com.bmnb.fly_dragonfly.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.bmnb.fly_dragonfly.objects.Enemy;
import com.bmnb.fly_dragonfly.sound.MediaPlayer;

public class Bird extends Enemy {
	private static final float SPEED = 650;
	private static final float RANDOM_COUNTDOWN = 2;
	private Vector2 direction;
	private float countdown;
	private boolean shouldCalculate = true;
	private long birdSndHnd;
	public Bird(Vector2 position, float width, float height, float speed,
			float scWidth, float scHeight, Player player) {
		super(position, width, height, speed, scWidth, scHeight, player,200);
		countdown = (float)Math.random()*RANDOM_COUNTDOWN;
		sortVal = 1;
		setTexture(new Texture("data/textures/bird1.png"));
		birdSndHnd = MediaPlayer.playSound("data/sound/eagle.wav");
		MediaPlayer.setSoundVolume("data/sound/eagle.wav",0.3f,birdSndHnd);
	}
	
	@Override
	public void kill() {
		removeable = true;
		MediaPlayer.stopSound("data/sound/eagle.wav",birdSndHnd);
		super.kill();
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
	protected void move(float delta) {
		if (countdown <= 0 ){
			if (shouldCalculate){
				direction = player.getPosition().sub(this.getPosition()).nor();
				this.rotate(direction.angle() - 90);
				shouldCalculate = false;
			}
			this.setPosition(this.getPosition().add(direction.cpy().mul(SPEED*delta)));
		} else {
			translateY(-speed * delta);
			countdown -= delta;
		}
		
		if (this.getPosition().y + this.getHeight() <= 0){
			this.kill();
			this.removeable = true;
		}
	}
	@Override
	public void draw(SpriteBatch spriteBatch, float delta){
		if (countdown >= 0 ){
			update(delta);
		}
		else super.draw(spriteBatch,delta);
	}
}
