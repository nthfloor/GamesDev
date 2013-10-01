/**
 * 
 */
package com.bmnb.fly_dragonfly.screens;

import java.util.ArrayList;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.bmnb.fly_dragonfly.Fly_DragonFly;
import com.bmnb.fly_dragonfly.flocking.Boid;
import com.bmnb.fly_dragonfly.flocking.BoidsModel;
import com.bmnb.fly_dragonfly.flocking.FireFly;
import com.bmnb.fly_dragonfly.graphics.GameParticleEmitter.Particle;
import com.bmnb.fly_dragonfly.graphics.GameParticleEmitter.ParticleType;
import com.bmnb.fly_dragonfly.graphics.Meter;
import com.bmnb.fly_dragonfly.graphics.ScrollingBackground;
import com.bmnb.fly_dragonfly.graphics.flashAnim;
import com.bmnb.fly_dragonfly.input.GameInput;
import com.bmnb.fly_dragonfly.map.MapLoader;
import com.bmnb.fly_dragonfly.map.Spawner;
import com.bmnb.fly_dragonfly.objects.Enemy;
import com.bmnb.fly_dragonfly.objects.GameObject;
import com.bmnb.fly_dragonfly.objects.Player;
import com.bmnb.fly_dragonfly.objects.Tongue;
import com.bmnb.fly_dragonfly.objects.Web;
import com.bmnb.fly_dragonfly.sound.MediaPlayer;

/**
 * Game screen controls the drawing update, everything for the game
 * 
 * @author Brandon James Talbot
 * 
 */
public class GameScreen implements Screen {

	/**
	 * Final static vars for global use
	 */
	public static final float width = 800, height = 1280, scrollSpeed = 200, TIME_POINTS_INCREASE_RATE = 35f;

	/**
	 * global variables for class
	 */
	protected SpriteBatch batch;
	protected OrthographicCamera camera;
	protected Player player;
	protected ScrollingBackground scroller;
	protected BoidsModel boidsmodel;
	protected MapLoader map;
	protected Spawner spawner;
	protected Meter manaMeter;
	protected BitmapFont font;

	protected Texture livesTex;
	protected Texture livesTexBack;
	protected CharSequence playerScore;
	protected float survivalTime;
	protected Fly_DragonFly game;
	
	public static ArrayList<Integer> recentPoints;
	protected float accumulatedTime = 0;
	public TutorialScreens tutScreen;

	protected static Sprite flashSprite;
	protected static TweenManager flashman;

	private FrameBuffer m_fbo = null;
	private TextureRegion m_fboRegion = null;
	/**
	 * Static vars for static methods
	 */
	protected static ArrayList<GameObject> objects, fireParticles,
			poisonParticles, enemies, rocks, boids;

	public GameScreen(Fly_DragonFly g){
		game = g;
	}
	
	@Override
	public void show() {
		// setting up of major devices
		batch = new SpriteBatch();
		camera = new OrthographicCamera(width, height);
		camera.translate(width / 2f, height / 2f);
		camera.update();
		batch.setProjectionMatrix(camera.combined); // needs only be done once,
													// since camera does not
													// move
		// init the arrays
		objects = new ArrayList<GameObject>();
		fireParticles = new ArrayList<GameObject>();
		poisonParticles = new ArrayList<GameObject>();
		enemies = new ArrayList<GameObject>();
		rocks = new ArrayList<GameObject>();
		boids = new ArrayList<GameObject>();
		recentPoints = new ArrayList<Integer>();

		// init flash sprite
		flashSprite = new Sprite(new Texture("data/textures/flash.png"));
		flashSprite.setPosition(0, 0);
		flashSprite.setSize(width, height);
		flashSprite.setColor(1, 1, 1, 0);
		flashman = new TweenManager();

		// init player
		addObject(player = new Player(new Vector2(width / 2f, 50), 150, 150,
				500, width, height));

		// init the scroller
		scroller = new ScrollingBackground(new String[] {
				"data/backgrounds/bg_final_flat_1.png",
				"data/backgrounds/bg_final_flat_2.png",
				"data/backgrounds/bg_final_flat_3.png" }, width, height,
				scrollSpeed);

		livesTex = new Texture("data/textures/health_bar_dragonfly.png");
		livesTexBack = new Texture(
				"data/textures/health_bar_dragonfly_grey.png");
		survivalTime = 0;		

		// set the input processor
		Gdx.input.setInputProcessor(new GameInput(width, height, player,game,this));
		((GameInput) Gdx.input.getInputProcessor()).setGameScreen(this);

		// Add the flocking models:
		boidsmodel = new BoidsModel();
		
		// Load map
		try {
			this.map = new MapLoader("data/maps/map_final.xml");
			this.spawner = new Spawner(map, boidsmodel, this, scrollSpeed,
					player);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// Init mana meter
		manaMeter = new Meter(new Vector2(width / 6, height - height / 30),
				width / 3, height / 15, width, height, false, new Texture(
						"data/textures/flametop.png"), new Texture(
						"data/textures/flamebot.png"), 1, player);

		// Load font
		MediaPlayer.loadSound("data/sound/toad.wav");
		MediaPlayer.loadSound("data/sound/eagle.wav");
		font = new BitmapFont(Gdx.files.internal("data/font/commicsans.fnt"),
				Gdx.files.internal("data/font/commicsans.png"), false);
		MediaPlayer.loadMusic("data/sound/flydragonfly_bg_music.mp3");
		MediaPlayer.playMusic("data/sound/flydragonfly_bg_music.mp3", true);
		MediaPlayer.setMusicVolume("data/sound/flydragonfly_bg_music.mp3", 1.0f);
		
		//initialise tut screen manager
		tutScreen = new TutorialScreens(font,game,this);
	}

	/**
	 * Returns the enemies array
	 * 
	 * @return The array of enemies
	 */
	public static ArrayList<GameObject> getEnemies() {
		return enemies;
	}

	/**
	 * Flashes the screen.
	 */
	public static void flash() {
		Tween.registerAccessor(Sprite.class, new flashAnim());

		Tween.to(flashSprite, flashAnim.ALPHA, 0.7f).target(1)
				.repeatYoyo(3, 0f).ease(TweenEquations.easeInCirc)
				.start(flashman);
	}

	@Override
	public void render(float delta) {
		// clear the screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (m_fbo == null) {
			// m_fboScaler increase or decrease the antialiasing quality

			m_fbo = new FrameBuffer(Format.RGBA4444, (int) (width),
					(int) (height), false);
			m_fboRegion = new TextureRegion(m_fbo.getColorBufferTexture());
			m_fboRegion.flip(false, true);
		}

		m_fbo.begin();
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		// draw everything
		batch.begin();
		// draw objects
		for (int i = 0; i < objects.size(); ++i)
			objects.get(i).draw(batch, delta);

		// draw lives
		for (int i = 1; i <= 4; i++) {
			batch.draw(livesTexBack, width - (110) * i, height - 120, 100, 100,
					0, 0, livesTexBack.getWidth(), livesTexBack.getHeight(),
					false, false);
			if (i <= player.getNumLives())
				batch.draw(livesTex, width - (110) * i, height - 120, 100, 100,
						0, 0, livesTex.getWidth(), livesTex.getHeight(), false,
						false);
		}

		// draw tutorial screen
		if (tutScreen.draw_tutorial) {
			tutScreen.drawTutorialScreen(batch,delta);
		} else {
			if (player.getNumLives() < 0){
				tutScreen.showTutorialScreen(7); //gameover
			}
			scroller.update(delta);

			// draw score
			playerScore = "" + player.getScore();
			font.draw(batch, playerScore, 10, height - livesTex.getHeight()
					- 50);
			player.increaseScoreBy(delta*TIME_POINTS_INCREASE_RATE);
			
			//draw recent points scored by player
			int temp = 0;
			if(recentPoints.size() > 5)
				temp = 5;
			else
				temp = recentPoints.size();

			for(int i=0;i<temp;i++){
				font.setColor(Color.RED);
				playerScore = "+"+recentPoints.get(i);
				font.draw(batch, playerScore, width-110, height/2 - 50*i);	
				font.setColor(Color.WHITE);
			}
			accumulatedTime += Math.ceil(delta);
			if(accumulatedTime>200){
				accumulatedTime = 0;
				if(recentPoints.size() > 0)
					recentPoints.remove(0);
			}

			// update objects
			for (int i = 0; i < objects.size(); ++i)
				objects.get(i).update(delta);

			boidsmodel.update(delta, this);
			spawner.update(delta);
			flashman.update(delta);
		}

		manaMeter.setProgress(player.getMana() / player.getMaxMana());
		manaMeter.draw(batch, delta);

		batch.end();
		m_fbo.end();

		batch.begin();
		// draw background
		scroller.draw(batch, delta);
		batch.draw(m_fboRegion, 0, 0, width, height);
		flashSprite.draw(batch);
		batch.end();
		// do collision
		doCollisionDetection();

		// remove all dead objects
		removeDeadObjects();
	}

	public Player getPlayer() {
		return player;
	}

	/**
	 * This removes all objects that need to be.
	 */
	protected void removeDeadObjects() {
		for (int i = 0; i < objects.size(); ++i) {
			if (objects.get(i).isRemovable()) {
				// enemies
				if (objects.get(i) instanceof Enemy)
					enemies.remove(objects.get(i));

				// boids
				if (objects.get(i) instanceof Boid)
					boids.remove(objects.get(i));
				// objects
				objects.remove(i);
				--i;
			}
		}
		for (int i = 0; i < fireParticles.size(); ++i) {
			if (fireParticles.get(i).isDead()) {
				fireParticles.remove(i);
				--i;
			}
		}
		for (int i = 0; i < poisonParticles.size(); ++i) {
			if (poisonParticles.get(i).isDead()) {
				poisonParticles.remove(i);
				--i;
			}
		}
	}

	protected void doCollisionDetection() {
		// first enemies
		Rectangle particlesBox = getParticleRect();

		for (GameObject o : enemies) {
			if (!o.isDead())
				if (particlesBox != null)
					if (o.getBoundingRectangle().overlaps(particlesBox)) {
						for (GameObject p : fireParticles) {
							if (!p.isDead())
								if (p.getBoundingRectangle().overlaps(
										o.getBoundingRectangle())) {
									if (o instanceof Tongue) {
										if (((Tongue) o).checkCollision(p
												.getBoundingRectangle())) {
											((Particle) p).kill();
											((Enemy) o).doDamage(player
													.getDamage());
										}
									}
									if (o instanceof Web) {
										((Particle) p).kill();
										((Enemy) o)
												.doDamage(player.getDamage());
									} else {
										// Gdx.app.log("Coll - ", "true");
										((Particle) p).kill();
										((Enemy) o)
												.doDamage(player.getDamage());
									}
								}
						}
					}
			// do for player with checks
			if (!o.isDead()) {

				if (o.getBoundingRectangle().overlaps(
						player.getBoundingRectangle())) {
					if (o instanceof Tongue) {
						if (((Tongue) o).checkCollision(player
								.getBoundingRectangle())) {
							player.playerHitAnimation();
							((Tongue) o).stopGrowing();
						}
					}
					if (o instanceof Web) {
						if (player.playerHitAnimation())
							o.kill();
					} else {
						player.playerHitAnimation();
					}
				}
			}
		}

		// add player check with boids
		for (GameObject o : boids) {
			if (!o.isDead()) {
				if (o.getBoundingRectangle().overlaps(
						player.getBoundingRectangle())) {
					o.kill();
					player.increaseScoreBy(10);
					recentPoints.add(10);
					if (o instanceof FireFly)
						player.convertWeaponFireflies();
					else
						player.convertWeaponMossies();
				}
			}
		}
	}

	/**
	 * Add an object to the list
	 * 
	 * @param o
	 *            Object to add
	 */
	public static void addObject(GameObject o) {
		if (o instanceof Particle) {
			if (((Particle) o).type == ParticleType.fire) {
				if (!fireParticles.contains(o))
					fireParticles.add(o);
			} else if (!poisonParticles.contains(o))
				poisonParticles.add(o);

		} else {
			if (o instanceof Enemy)
				enemies.add(o);

			if (o instanceof Boid)
				boids.add(o);

			if (objects.size() == 0)
				objects.add(o);
			else {
				boolean added = false;
				for (int i = 0; i < objects.size(); ++i) {
					if (o.compareTo(objects.get(i)) < 0) {
						objects.add(i, o);
						added = true;
						break;
					}
				}
				if (!added)
					objects.add(o);
			}
		}
	}

	/**
	 * Calculates a Bounding rectangle for all the particles in order to keep
	 * efficiency
	 * 
	 * @return The bounding rectangle for all the particles
	 */
	protected Rectangle getParticleRect() {
		int xmin = Integer.MAX_VALUE, ymin = Integer.MAX_VALUE;
		int xmax = Integer.MIN_VALUE, ymax = Integer.MIN_VALUE;
		for (GameObject o : fireParticles) {
			xmin = (int) Math.min(xmin, o.getX() - o.getWidth());
			ymin = (int) Math.min(ymin, o.getY() - o.getHeight());
			xmax = (int) Math.min(xmax, o.getX() + o.getWidth());
			ymax = (int) Math.min(ymax, o.getY() + o.getHeight());
		}

		if (xmin == Integer.MAX_VALUE || ymin == Integer.MAX_VALUE)
			return null;
		else
			return new Rectangle(xmin, ymin, Math.abs(xmax - xmin),
					Math.abs(ymax - ymin));
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		batch.dispose();
		objects = new ArrayList<GameObject>();
		fireParticles = new ArrayList<GameObject>();
		enemies = new ArrayList<GameObject>();
		rocks = new ArrayList<GameObject>();
		boids = new ArrayList<GameObject>();
		MediaPlayer.stopAllSoundInstances();
		MediaPlayer.stopMusic("data/sound/flydragonfly_bg_music.mp3");
		MediaPlayer.disposeInstances();
		livesTex.dispose();
		livesTexBack.dispose();
	}
}
