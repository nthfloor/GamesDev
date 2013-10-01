package com.bmnb.fly_dragonfly.objects;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.bmnb.fly_dragonfly.graphics.GameParticleEmitter;
import com.bmnb.fly_dragonfly.graphics.GameParticleEmitter.ParticleType;
import com.bmnb.fly_dragonfly.graphics.flashAnim;
import com.bmnb.fly_dragonfly.screens.GameScreen;
import com.bmnb.fly_dragonfly.tools.MathTools;
import com.bmnb.fly_dragonfly.tools.SpriteAnimator;

/**
 * Player class, holds all methods needed for the player specifically
 * 
 * @author Brandon James Talbot
 * 
 */
public class Player extends GameObject {

	/**
	 * Static set up vars
	 */
	protected static final float maxVertPath = 0.8f, upSpeedPercent = 0.8f,
			downSpeedPercent = 1.2f, mosDamage = 0.2f, flyDamage = 0.8f,
			manaRegen = 0.5f, manaUse = 5, maxMana = 15;
	protected static final float[] mosColor = { 0.047058824f, 0.105882354f,
			0.91764706f },
			flyColor = { 0.91764706f, 0.105882354f, 0.047058824f },
			mosAngle = { 0.0f, 0.0f, 0.0f }, flyAngle = { 0.6f, 0.8f, 1.0f },
			mosVel3 = { 1.0f, 1.0f, 1.0f }, flyVel3 = { 0.6f, 0.8f, 1.0f },
			mosVel2 = { 600f, 700f }, flyVel2 = { 400f, 500f };

	/**
	 * Global vars
	 */
	protected Vector2 targetPosition;
	protected GameParticleEmitter dragonBreath;
	protected float damage = flyDamage;
	protected float mana = maxMana;
	protected int numLives;
	protected int numPoints;
	TweenManager tweenManager;
	protected boolean canBeHit = true;
	protected Texture btnTxt;

	/**
	 * Constructor
	 * 
	 * @param position
	 *            The position of the object
	 * @param width
	 *            The width of the object
	 * @param height
	 *            the height of the object
	 * @param speed
	 *            the speed for the object
	 * @param scWidth
	 *            the scren width of the game
	 * @param scHeight
	 *            the screen height of the game
	 */
	public Player(Vector2 position, float width, float height, float speed,
			float scWidth, float scHeight) {
		super(position, width, height, speed, scWidth, scHeight);

		sortVal = 0;
		mana = maxMana;

		targetPosition = position;

		numLives = 3;
		numPoints = 0;

		tweenManager = new TweenManager();

		// load textures (loading here for now, must create texture loader
		// later)
//		this.setTexture(new Texture("data/textures/dragonfly.png"));
		animator = new SpriteAnimator(new Texture("data/textures/dragonfly.png"), 2, 2, "dragon", 50, this);
		btnTxt = new Texture("data/textures/button.png");
		
		// load the flames emitter settings
		try {
			dragonBreath = new GameParticleEmitter(new BufferedReader(
					new InputStreamReader(Gdx.files.internal(
							"data/particleEffects/dragonflyBreath").read()),
					512), new Texture("data/particleEffects/particle.png"),
					ParticleType.fire);

			dragonBreath.setContinuous(false);
		} catch (Exception e) {
			Gdx.app.log("error", e.getMessage());
		}
	}

	public int getNumLives() {
		return numLives;
	}

	public int getScore() {
		return (int)Math.ceil(numPoints);
	}

	int counter = 0;;

	public void increaseScoreBy(float p) {
		numPoints += p;
		counter += p;
		if (counter > 1000) {
			counter = 0;
			addLife();
		}
	}

	private void addLife() {
		if (numLives < 4) {
			numLives++;
		}
	}

	/**
	 * Sets the new position the player should move to
	 * 
	 * @param fingerPos
	 *            The position of the finger
	 */
	public void moveToFinger(Vector2 fingerPos) {
		targetPosition.x = fingerPos.x < this.getWidth() / 2f ? this.getWidth() / 2f
				: fingerPos.x > screenWidth - this.getWidth() / 2f ? screenWidth
						- this.getWidth() / 2f
						: fingerPos.x;

		targetPosition.y = fingerPos.y < this.getHeight() / 2f ? this
				.getHeight() / 2f
				: fingerPos.y > screenHeight * maxVertPath ? screenHeight
						* maxVertPath : fingerPos.y;
	}

	/**
	 * Stops the player moving (should be called when finger is released)
	 */
	public void stopMovingToFinger() {
		targetPosition = this.getPosition().cpy();
	}

	/**
	 * Starts the shooting of the dragonBreath
	 */
	public void startShooting() {
		dragonBreath.setContinuous(true);
	}

	/**
	 * Stops the shooting of the dragonbreath
	 */
	public void stopShooting() {
		dragonBreath.setContinuous(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bmnb.fly_dragonfly.objects.GameObject#move(float)
	 */
	@Override
	protected void move(float delta) {
		// Gdx.app.log("grid pos", getLocation(0).x + " - " + getLocation(0).y);

		// check distancing
		if (this.getPosition().cpy().sub(targetPosition).len() <= speed * delta) {
			targetPosition = this.getPosition().cpy();
		}

		// calc new direction
		direction = targetPosition.cpy().sub(this.getPosition());
		direction.nor();

		// move the player
		this.translateX(speed * delta * direction.x);
		this.translateY(speed
				* (direction.y > 0 ? upSpeedPercent : downSpeedPercent) * delta
				* direction.y);

		// move the particle engine position for the fire breath
		dragonBreath.setPosition(this.getX(), this.getY() + this.getHeight()
				/ 3f);
	}

	
	
	@Override
	public Rectangle getBoundingRectangle() {
		Rectangle r =  super.getBoundingRectangle();
		
		r.x += this.getWidth() * 0.38f;
		r.width -= this.getWidth() * 0.38f * 2;
		
		return r;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bmnb.fly_dragonfly.objects.GameObject#draw(com.badlogic.gdx.graphics
	 * .g2d.SpriteBatch, float)
	 */
	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {

		dragonBreath.draw(spriteBatch, delta);

		float [] col = this.getFireColour();
		spriteBatch.setColor(col[0], col[1], col[2], 1);
		spriteBatch.draw(btnTxt, 0, screenHeight * 0.35f - screenWidth * 0.075f, screenWidth * 0.15f, screenWidth * 0.15f);
		spriteBatch.setColor(1, 1, 1, 1);

		
		super.draw(spriteBatch, delta);
	}

	@Override
	public void update(float delta) {
		tweenManager.update(delta);

		if (this.dragonBreath.isContinuous()) {
			mana = mana <= 0 ? 0 : mana - manaUse * delta;

			// Gdx.app.log("Mana:", mana + "");

			if (mana <= 0)
				stopShooting();
		} else {
			mana = mana == maxMana ? mana : mana + manaRegen * delta;
		}
		super.update(delta);
	}
	
	/**
	 * Starts a flashing animation
	 */
	public boolean playerHitAnimation() {
		if (canBeHit) {
			numLives--;
			if (numLives < 0)
				return false;

			GameScreen.flash();
			
			Tween.registerAccessor(Sprite.class, new flashAnim());

			TweenCallback cb = new TweenCallback() {

				@Override
				public void onEvent(int type, BaseTween<?> source) {
					resetHit();
				}
			};

			Tween.to(this, flashAnim.ALPHA, 0.7f).target(0)
					.repeatYoyo(3, 0f).ease(TweenEquations.easeInCirc)
					.setCallback(cb)
					.setCallbackTriggers(TweenCallback.COMPLETE)
					.start(tweenManager);
			canBeHit = false;

		}
		return numLives > 0;
	}

	/**
	 * Makes the player hitable again
	 */
	protected void resetHit() {
		canBeHit = true;
		// setColor(1,1,1,1);
	}

	/**
	 * Converts the gun into the firefly version by 1 step
	 */
	public void convertWeaponFireflies() {
		this.mana++;
		
		dragonBreath.getTint().setColors(
				MathTools.interp(dragonBreath.getTint().getColors(), flyColor,
						mosColor, 5));
		dragonBreath.getAngle().setScaling(
				MathTools.interp(dragonBreath.getAngle().getScaling(),
						flyAngle, mosAngle, 5));
		dragonBreath.getVelocity().setScaling(
				MathTools.interp(dragonBreath.getVelocity().getScaling(),
						flyVel3, mosVel3, 5));
		float vel[] = new float[2];
		vel[0] = dragonBreath.getVelocity().getHighMin();
		vel[1] = dragonBreath.getVelocity().getHighMax();
		vel = MathTools.interp(vel, flyVel2, mosVel2, 5);
		dragonBreath.getVelocity().setHigh(vel[0], vel[1]);

		damage = damage + Math.abs(flyDamage - mosDamage) * 0.05f;
		damage = damage > flyDamage ? flyDamage : damage;
	}

	/**
	 * Converts the gun into the mossy version by 1 step
	 */
	public void convertWeaponMossies() {
		this.mana++;
		
		dragonBreath.getTint().setColors(
				MathTools.interp(dragonBreath.getTint().getColors(), mosColor,
						flyColor, 5));
		dragonBreath.getAngle().setScaling(
				MathTools.interp(dragonBreath.getAngle().getScaling(),
						mosAngle, flyAngle, 5));
		dragonBreath.getVelocity().setScaling(
				MathTools.interp(dragonBreath.getVelocity().getScaling(),
						mosVel3, flyVel3, 5));
		float vel[] = new float[2];
		vel[0] = dragonBreath.getVelocity().getHighMin();
		vel[1] = dragonBreath.getVelocity().getHighMax();
		vel = MathTools.interp(vel, mosVel2, flyVel2, 5);
		dragonBreath.getVelocity().setHigh(vel[0], vel[1]);

		damage = damage - Math.abs(flyDamage - mosDamage) * 0.05f;
		damage = damage < mosDamage ? mosDamage : damage;
	}

	/**
	 * Returns the damage of the players fireBreath
	 * 
	 * @return the firebreath damage
	 */
	public float getDamage() {
		return damage;
	}

	/**
	 * Returns the dragons flies current mana
	 * 
	 * @return The mana
	 */
	public float getMana() {
		return mana;
	}

	// reduces number of player's lives before finally killing him
	@Override
	public void kill() {
		numLives--;
		counter=0;
		if (numLives < 0)
			super.kill();
	}

	/**
	 * Returns the dragon flies max mana
	 * 
	 * @return The max mana
	 */
	public float getMaxMana() {
		return Player.maxMana;
	}
	
	public float [] getFireColour(){
		float f [] = dragonBreath.getTint().getColors().clone();
		return f;
	}
}
