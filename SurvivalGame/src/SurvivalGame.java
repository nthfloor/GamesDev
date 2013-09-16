

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Random;
import java.util.Vector;

import javax.swing.JOptionPane;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import GameEngine.Game;
import GameEngine.GameFont;
import GameEngine.GameTexture;


//==================================================================================================
//==================================================================================================

public class SurvivalGame extends Game
{
	// Offset of the screen
	private Point2D.Float offset = new Point2D.Float(0,0);

	private boolean alive = true;

	// A Collection of GridGameObjects in the world that will be used with the collision detection system
	protected static Vector<GridGameObject> objects = new Vector<GridGameObject>();

	// Grid GridGameObjects
	protected static GridGameObject [] [] gridTile;

	// Important GridGameObjects
	private PlayerObject player; // the player
	private DisplayObject hp_bar; //health-bar
	private DisplayObject currentWeapon;
	private DisplayObject currentAmmo;
	private DisplayObject picketUpAmmo;
	protected static Vector<EnemyObject> enemies;
	private int displayPickupDelay;
	private boolean ammoCollected = false;

	//Textures that will be used
	private GameTexture shotgunbulletTexture;
	private GridGameObject riflebullet;
	private GridGameObject rocketbullet;
	private GameTexture ammoTexture;

	//GameFonts that will be used
	private GameFont arial, serif;

	//size of grid
	int gridSize = 0;
	protected static Dimension screenSize;

	// The position of the mouse
	private Point2D.Float mousePos = new Point2D.Float (0,0);

	// check if game is paused/finished
	private boolean paused;
	private boolean aborted;
	private boolean missionSuccess;
	private boolean allEnemiesAreDead;
	private int pausePressedDelay;
	private boolean isBarracksExists = true;
	
	protected static String objectives;
	private int AIcounter = 0;

	//collision detection class
	CollisionDetector collisions = null;
	LevelGenerator levels = null;
	int currentLevel;
	int conscriptionTime = 1000;
	int numberOfConscriptions = 0;
	
	//animation variables
	private boolean explodeTime;
	private int animationDelay;
	private int animationTextureCounter;
	private GridGameObject explosionObj;
	
	//sound variables
	protected static AudioStream audio_background;
	protected static AudioStream audio_heli;
	protected static AudioStream audio_tank;
	AudioStream audio;
	InputStream input;
	private int weaponUsed;
	int backgroundLoopCounter = 0;
	
	int heliLoopCounter = 0;
	int tankCounter = 0;
	
	boolean tankExists = true;
	boolean heliExists = true;
	
	// Information for the random line at the bottom of the screen
	Point2D.Float [] linePositions = {new Point2D.Float(0,0), new Point2D.Float(100,100)};
	float [][] lineColours = {{1.0f,1.0f,1.0f,1.0f},{1.0f,0.0f,0.0f,1.0f}};

	//==================================================================================================

	public SurvivalGame (int GFPS, int level) {   	
		super(GFPS);
		objects = new Vector<GridGameObject>();
		gridTile = null;
		enemies = null;
		screenSize = null;
		currentLevel = level;
	}

	//==================================================================================================

	public void initStep(ResourceLoader loader) {

		//Loading up some fonts
		arial = loader.loadFont(  new Font("Arial", Font.ITALIC, 48) );
		serif = loader.loadFont(  new Font("Serif", Font.BOLD, 12) );

		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

		GameTexture grassTexture = loader.load("Textures/ground/ground_dirt.jpg");   

		//load bullet textures, etc.
		riflebullet = new GridGameObject(0, 0, 0, 0);
		rocketbullet = new GridGameObject(0, 0, 0, 0);        
		shotgunbulletTexture = loader.loadTexture("Textures/bullets/shotgunbullet.png"); 
		for (int i = 0 ; i < 72 ; i++) {
			riflebullet.addTexture(loader.loadTexture("Textures/bullets/rifle/riflebullet_sm"+i+".png"));
		}   
		for (int i = 0 ; i < 72 ; i++) {
			rocketbullet.addTexture(loader.loadTexture("Textures/bullets/rocket/rocketbullet_sm"+i+".png"));
		} 
		ammoTexture = loader.loadTexture("Textures/weapons/ammobox.png");

		gridSize = 30; 
		screenSize = Runner.getScreenDimensions();
		paused = false;
		aborted = false;
		missionSuccess = false;
		allEnemiesAreDead = false;
		pausePressedDelay = 50;

		//checking each unit against each other unit for collisions
		collisions = new CollisionDetector(0,0,64*gridSize,64*gridSize);

		GameScreen.loading.setVisible(false);
		GameScreen.progress.setVisible(false);
		GameScreen.listScroller.setVisible(false);
		GameScreen.isLoaded = true;
		try{
			GameScreen.progressThread.join();
		}catch(Exception e){
			System.out.println("Progress bar error");
		}        

		enemies = new Vector<EnemyObject>();
		levels = new LevelGenerator();

		if(currentLevel == 1){
			levels.generateLevel1(loader, grassTexture, gridSize);
			numberOfConscriptions = 20;
			objectives = "Destroy the barracks or kill everyone...";
		}
		else if(currentLevel == 2){
			levels.generateLevel2(loader, grassTexture, gridSize);
			numberOfConscriptions = 25;
			objectives = "Destroy the barracks or kill everyone...";
		}
		else if(currentLevel == 3){
			levels.generateLevel3(loader, grassTexture, gridSize);
			numberOfConscriptions = 25;
			objectives = "Destroy the barracks or kill everyone...";
		}
		else
			System.out.println("Wrong or no level selected to be loaded");
     
		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

		// Creating the player's ship
		player = new PlayerObject(
				(float)(screenSize.width/2),
				(float)(100),1000,20);

		for (int i = 0 ; i < 72 ; i++) {
			player.addTexture(loader.load("Textures/marine/marine_sm"+i+".png"), 16, 16);
			//        	player.addTexture(loader.load("Textures/ship/spaceship_sm"+i+".gif"), 16, 16);
		}
		objects.add(player);
		//players staring point (house)
		GameTexture house = loader.loadTexture("Textures/structures/house.png");
		WallObject houseObj = new WallObject((float)(screenSize.width/2 - 50),(float)(50),1000,20);
		houseObj.addTexture(house);
		objects.add(houseObj);		

		// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

		//object for health bar
		hp_bar = new DisplayObject(135,60 , 0, 0);
		GameTexture healthbarTexture = loader.loadTexture("Textures/hp-bar.png");
		hp_bar.addTexture(healthbarTexture);

		//display current weapon being used
		GameTexture weaponTexture;
		currentWeapon = new DisplayObject(screenSize.width - 114,45,0,0);
		weaponTexture = loader.loadTexture("Textures/weapons/automatic.png");
		currentWeapon.addTexture(weaponTexture);
		weaponTexture = loader.loadTexture("Textures/weapons/shotgun.png");        
		currentWeapon.addTexture(weaponTexture);
		weaponTexture = loader.loadTexture("Textures/weapons/rifle.png");        
		currentWeapon.addTexture(weaponTexture);
		weaponTexture = loader.loadTexture("Textures/weapons/rocketlauncher.png");        
		currentWeapon.addTexture(weaponTexture);

		//display ammo icon
		currentAmmo = new DisplayObject(screenSize.width - 50,30, 0, 0);
		weaponUsed = 1;
		picketUpAmmo = new DisplayObject(screenSize.width - 50,200, 0, 0);
		//        picketUpAmmo = new DisplayObject(screenSize.width/2+35,30, 0, 0);

		weaponTexture = loader.load("Textures/weapons/riflebullet.png");
		currentAmmo.addTexture(weaponTexture);
		picketUpAmmo.addTexture(weaponTexture);
		weaponTexture = loader.load("Textures/weapons/shotgunbullet.png");
		currentAmmo.addTexture(weaponTexture);
		picketUpAmmo.addTexture(weaponTexture);
		weaponTexture = loader.load("Textures/weapons/rocketbullet.png");
		currentAmmo.addTexture(weaponTexture);
		picketUpAmmo.addTexture(weaponTexture);

		displayPickupDelay = 500;
		
		//load explosion animation
		explodeTime = false;
		animationDelay = 0;
		animationTextureCounter = 0;
		explosionObj = new GridGameObject(0, 0, 0, 0);
		for(int n=1;n < 26;n++){
			explosionObj.addTexture(loader.loadTexture("Textures/explosions/explosion_sm"+n+".png"));
		}
	}

	//==================================================================================================

	// this method is used to fire a bullet 
	public void fireBullet(GridGameObject obj, float dir, GridGameObject source) {

		obj.setCoolDown(obj.getCoolDownRate());
		BulletObject bullet = null;
		BulletObject[] bullets = null;

		//bullet.setVelocity(player.getVelocity());        
		if(source instanceof PlayerObject){
			if(((PlayerObject)obj).getAmmo() > 0){ //check player is out of ammo
				((PlayerObject)obj).decreaseAmmo();

				if(((PlayerObject)obj).getWeapon() == PlayerObject.Weapon.AUTOMATIC){
					bullet = new BulletObject(
							obj.getPosition().x + (float)Math.sin(Math.toRadians(dir))*32, 
							obj.getPosition().y - (float)Math.cos(Math.toRadians(dir))*32, 1f, 300, 0,0,source);
					bullet.setDestroyTimer(45);
					for (int i = 0 ; i < 72 ; i++) {
						bullet.addTexture(riflebullet.getCurrentTexture(), 16, 16);
						riflebullet.setActiveTexture(i);
					}
					bullet.addTexture(shotgunbulletTexture);
					bullet.applyForceInDirection(dir, 6f);
					objects.add(bullet);
				}
				else if(((PlayerObject)obj).getWeapon() == PlayerObject.Weapon.SHOTGUN){
					bullets = new BulletObject[3];

					bullets[0] = new BulletObject(
							obj.getPosition().x + (float)Math.sin(Math.toRadians(dir))*16, 
							obj.getPosition().y - (float)Math.cos(Math.toRadians(dir))*16, 1f, 300,0,0,source);        		
					bullets[1] = new BulletObject(
							obj.getPosition().x + (float)Math.sin(Math.toRadians(dir))*32, 
							obj.getPosition().y - (float)Math.cos(Math.toRadians(dir))*32, 1f, 300,0,0,source);        		
					bullets[2] = new BulletObject(
							obj.getPosition().x + (float)Math.sin(Math.toRadians(dir))*48, 
							obj.getPosition().y - (float)Math.cos(Math.toRadians(dir))*48, 1f, 300,0,0,source);

					for(int i=0;i < 3;i++){
						bullets[i].addTexture(shotgunbulletTexture);
						bullets[i].setDestroyTimer(35);
						objects.add(bullets[i]);
					}	
					//for spray effect
					bullets[0].applyForceInDirection(dir-4, 6f);    			
							bullets[1].applyForceInDirection(dir, 6f);
							bullets[2].applyForceInDirection(dir+4, 6f);
				}
				else if(((PlayerObject)obj).getWeapon() == PlayerObject.Weapon.RIFLE){
					bullet = new BulletObject(
							obj.getPosition().x + (float)Math.sin(Math.toRadians(dir))*32, 
							obj.getPosition().y - (float)Math.cos(Math.toRadians(dir))*32, 1f, 300,0,0,source);
					bullet.setDestroyTimer(70);
					for (int i = 0 ; i < 72 ; i++) {
						bullet.addTexture(riflebullet.getCurrentTexture(), 16, 16);
						riflebullet.setActiveTexture(i);
					}
					bullet.applyForceInDirection(dir, 10f);
					objects.add(bullet);
				}
				else if(((PlayerObject)obj).getWeapon() == PlayerObject.Weapon.ROCKET){
					bullet = new BulletObject(
							obj.getPosition().x + (float)Math.sin(Math.toRadians(dir))*32, 
							obj.getPosition().y - (float)Math.cos(Math.toRadians(dir))*32, 1f, 300,0,0,source);
					bullet.setDestroyTimer(300);
					for (int i = 0 ; i < 72 ; i++) {
						bullet.addTexture(rocketbullet.getCurrentTexture(), 16, 16);
						rocketbullet.setActiveTexture(i);
					}
					//induce fake inacurracy
					Random randomNum = new Random();
					int inacurracy = randomNum.nextInt(10);
					if(Math.random() <= 0.5)
						inacurracy *= -1;

					bullet.applyForceInDirection(dir+inacurracy, 3f);
					objects.add(bullet);
				}
				//sounds
				try{
					switch(weaponUsed){
					case 1:
						input = new FileInputStream("sounds/gunshot.wav");
						break;
					case 2:
						input = new FileInputStream("sounds/shotgun.wav");
						break;
					case 3:
						input = new FileInputStream("sounds/sniper.wav");
						break;
					case 4:
						input = new FileInputStream("sounds/rocket.wav");					
					}					
					audio = new AudioStream(input);
					AudioPlayer.player.start(audio);
				}catch(Exception e){

				}
			}//no ammo
		}
		else{
			if(source instanceof EnemyStructure){
				bullet = new BulletObject(
						obj.getPosition().x + (float)Math.sin(Math.toRadians(dir))*32, 
						obj.getPosition().y - (float)Math.cos(Math.toRadians(dir))*32, 1f, 300,0,0,source);
				bullet.setDestroyTimer(120);
			}
			else{
				bullet = new BulletObject(
						obj.getPosition().x + (float)Math.sin(Math.toRadians(dir))*32, 
						obj.getPosition().y - (float)Math.cos(Math.toRadians(dir))*32, 1f, 300,0,0,source);
				bullet.setDestroyTimer(50);
			}
			bullet.addTexture(shotgunbulletTexture);
			bullet.applyForceInDirection(dir, 6f);
			objects.add(bullet);
		}        
	}    

	/*
    public static boolean isPointInBox(final Point2D.Float point, final Rectangle2D.Float box) {
        return box.contains(point.x, point.y);
    }

    // This is a pretty bad implementation and faster ones exist, it is suggested you find a better one. At least try make use of the Rectangle2D's createIntersection method.
    public static boolean boxIntersectBox (final Rectangle2D.Float d, final Rectangle2D.Float d2) {
       return  isPointInBox(new Point2D.Float (d.x, d.y), d2) ||
                isPointInBox(new Point2D.Float (d.x, d.y+d.height), d2) ||
                isPointInBox(new Point2D.Float (d.x+d.width, d.y), d2) ||
                isPointInBox(new Point2D.Float (d.x+d.width, d.y+d.height), d2) ||
                isPointInBox(new Point2D.Float (d2.x, d2.y), d) ||
                isPointInBox(new Point2D.Float (d2.x, d2.y+d2.height), d) ||
                isPointInBox(new Point2D.Float (d2.x+d2.width, d2.y), d) ||
                isPointInBox(new Point2D.Float (d2.x+d2.width, d2.y+d2.height), d);                
    }*/

	private void handleControls(GameInputInterface gii) {

		//----------------------------------

		// This isn't so great, there are better and neater ways to do this, you are encouraged to implement a better one
		boolean move = false;
		float directionToMove = 0;

		if(gii.keyDown(KeyEvent.VK_W)) {
			move = true;
			if(gii.keyDown(KeyEvent.VK_A) && !gii.keyDown(KeyEvent.VK_D))
				directionToMove = 225;
			else if(gii.keyDown(KeyEvent.VK_D) && !gii.keyDown(KeyEvent.VK_A))
				directionToMove = 135;
			else
				directionToMove = 180;
		}
		else if(gii.keyDown(KeyEvent.VK_S)) {
			move = true;
			if(gii.keyDown(KeyEvent.VK_A) && !gii.keyDown(KeyEvent.VK_D))
				directionToMove = -45;
			else if(gii.keyDown(KeyEvent.VK_D) && !gii.keyDown(KeyEvent.VK_A))
				directionToMove = 45;
			else
				directionToMove = 0;
		}
		else if(gii.keyDown(KeyEvent.VK_A) && !gii.keyDown(KeyEvent.VK_D)) {
			move = true;
			directionToMove = 270;
		}
		else if(gii.keyDown(KeyEvent.VK_D) && !gii.keyDown(KeyEvent.VK_A)) {
			move = true;
			directionToMove = 90;
		}
		//pause/resume game-play
		if(gii.keyDown(KeyEvent.VK_P)){
			if(move)
				move = false;
			else
				move = true;           	       	
		}
		
		//change weapons
		if(gii.keyDown(KeyEvent.VK_1)){
			player.setWeapon(PlayerObject.Weapon.AUTOMATIC);
			player.setCoolDownRate(17);
			player.setDamage(5);
			currentWeapon.setActiveTexture(0);
			currentAmmo.setActiveTexture(0);
			weaponUsed = 1;
		}
		else if(gii.keyDown(KeyEvent.VK_2)){
			player.setWeapon(PlayerObject.Weapon.SHOTGUN);
			player.setCoolDownRate(70);
			player.setDamage(13);
			currentWeapon.setActiveTexture(1);
			currentAmmo.setActiveTexture(1);
			weaponUsed = 2;
		}
		else if(gii.keyDown(KeyEvent.VK_3)){
			player.setWeapon(PlayerObject.Weapon.RIFLE);
			player.setCoolDownRate(200);
			player.setDamage(15);
			currentWeapon.setActiveTexture(2);
			currentAmmo.setActiveTexture(0);
			weaponUsed = 3;
		}
		else if(gii.keyDown(KeyEvent.VK_4)){
			player.setWeapon(PlayerObject.Weapon.ROCKET);
			player.setCoolDownRate(200);
			player.setDamage(70);
			currentWeapon.setActiveTexture(3);
			currentAmmo.setActiveTexture(2);
			weaponUsed = 4;
		}

		if (move){
			//stop player from moving off edge of map
			//y-values
			if((player.getPosition().y < gridSize*64 - 20) && (player.getPosition().y > 20)){
				//x-values
				if((player.getPosition().x < gridSize*64 - 10) && (player.getPosition().x > 20)){
					player.moveInDirection(directionToMove);
				}
				else
					player.revertPosition();
			}		
			else
				player.revertPosition();
		}

		if (player.getCoolDown() <= 0) {
			if(gii.keyDown(KeyEvent.VK_ENTER) || gii.mouseButtonDown(MouseEvent.BUTTON1)) {
				fireBullet(player,90+player.getDegreesTo(mousePos),player);
			}
			if(gii.keyDown(MouseEvent.BUTTON2))
				player.setCoolDownRate(10);
		}
		player.decrementCoolDown();

	}

	private void handelPauseControl(GameInputInterface gii) {
		if(gii.keyDown(KeyEvent.VK_P) && (pausePressedDelay <= 0)){
			if(paused){
				paused = false;
			}
			else{
				paused = true;        		
			}        	
			pausePressedDelay = 50;
		}   

	}
	
	//animations - set texture to simulate explosion
	private void explosionAnimation(){
		if(animationTextureCounter < 24){
			if(animationDelay%5 == 0){
				explosionObj.setActiveTexture(animationTextureCounter);
				animationTextureCounter++;
			}
			animationDelay++;
		}
		else{
			animationTextureCounter = 0;
			animationDelay = 0;
			explosionObj.setMarkedForDestruction(true);
			explodeTime = false;
		}
	}
	
	//initialise explosion
	private void setAnimation(GridGameObject o){
		explosionObj.setMarkedForDestruction(false);
		explosionObj.setPosition(o.getPosition());			
		objects.add(explosionObj);
		explodeTime = true;
	}

	//==================================================================================================

	public void logicStep(GameInputInterface gii) {

		/*if(gii.keyDown(KeyEvent.VK_W)) {
            offset.y -= 3.0;
        }
        if(gii.keyDown(KeyEvent.VK_S)) {
            offset.y += 3.0;
        }
        if(gii.keyDown(KeyEvent.VK_A)) {
            offset.x += 3.0;
        }
        if(gii.keyDown(KeyEvent.VK_D)) {
            offset.x -= 3.0;
        }
        if(gii.keyDown(KeyEvent.VK_ESCAPE)) {
            endGame();
        }*/

		if(pausePressedDelay > 0)
			pausePressedDelay--;

		mousePos.x = (float)gii.mouseXScreenPosition() - offset.x;
		mousePos.y = (float)gii.mouseYScreenPosition() - offset.y;

		//----------------------------------

		handelPauseControl(gii);

		if (alive) {	
			if((! paused) && (! missionSuccess) && (! aborted)){

				handleControls(gii);
				player.setDirection(90+player.getDegreesTo(mousePos));
				
				//game background sounds
				AudioPlayer.player.stop(WelcomeScreen.audio);
				if(backgroundLoopCounter <= 0){
					try{			
						input = new FileInputStream("sounds/background_battle.wav");
						audio_background = new AudioStream(input);
						AudioPlayer.player.start(audio_background);
					}catch(Exception e){

					}
					backgroundLoopCounter = 2513;
				}
				else
					backgroundLoopCounter--;
				
				//check for animation
				if(explodeTime)
					explosionAnimation();
				
				//randomly generate enemy troops from barracks if it still exists
				if((isBarracksExists) && (enemies.size() <= numberOfConscriptions)){
					if(conscriptionTime <= 0){
						LevelGenerator.spawnSoldier((float)(gridSize*64*0.45),(float)(gridSize*64*0.77));
						conscriptionTime = 1500;
					}
					else
						conscriptionTime--;			
				}
				
				//AI
				EnemyObject temp;
				for(int i=0;i < enemies.size();i++){
					//move enemy objects
					temp = enemies.elementAt(i);
					if(AIcounter%5 == 0){
						if(! (temp instanceof EnemyStructure)){
							//check if air or ground unit
							if(temp.getEnemyType() != EnemyObject.EnemyType.AIR){
								if((temp.getDistanceTo(player) > 70) && (temp.getDistanceTo(player) < 200)){
									temp.isMoving = false;
									temp.moveInDirection(90+temp.getDegreesTo(player));
									temp.hasBeenHit = false;
									
									//tank sounds
									if(temp.getEnemyType() == EnemyObject.EnemyType.TANK){										
										if((tankCounter <= 0) && (tankExists)){
											try{
												input = new FileInputStream("sounds/tank.wav");
												audio_tank = new AudioStream(input);
												AudioPlayer.player.start(audio_tank);
											}
											catch(Exception e){

											}
											tankCounter = 602;
										}
										else
											tankCounter--;
									}
								}
								else if((temp.hasBeenHit) && (temp.getDistanceTo(player) > 70)){
									temp.moveInDirection(90+temp.getDegreesTo(player)); // move to player if shot
									//tank sounds
									if(temp.getEnemyType() == EnemyObject.EnemyType.TANK){
										if((tankCounter <= 0) && (tankExists)){
											try{
												input = new FileInputStream("sounds/tank.wav");
												audio_tank = new AudioStream(input);
												AudioPlayer.player.start(audio_tank);
											}
											catch(Exception e){

											}
											tankCounter = 602;
										}
										else
											tankCounter--;
									}
								}
								else if(temp.getDistanceTo(player) < 70)
									temp.setDirection(90+temp.getDegreesTo(player));
								else{
									temp.isMoving = false;
								}
							}
							else{
								if((temp.getDistanceTo(player) > 30) && (temp.getDistanceTo(player) < 300)){
									temp.isMoving = false;
									temp.moveInDirection(90+temp.getDegreesTo(player));
									temp.hasBeenHit = false;
									if((heliLoopCounter <= 0) && (heliExists)){
										//helicopter sound
										try{
											input = new FileInputStream("sounds/helicopter.wav");
											audio_heli = new AudioStream(input);
											AudioPlayer.player.start(audio_heli);
										}
										catch(Exception e){

										}
										heliLoopCounter = 1006;
									}
									else
										heliLoopCounter--;
								}
								else if((temp.hasBeenHit) && (temp.getDistanceTo(player) > 30)){
									temp.moveInDirection(90+temp.getDegreesTo(player)); // move to player if shot
									if((heliLoopCounter <= 0) && (heliExists)){
										//helicopter sound
										try{
											input = new FileInputStream("sounds/helicopter.wav");
											audio_heli = new AudioStream(input);
											AudioPlayer.player.start(audio_heli);
										}
										catch(Exception e){

										}
										heliLoopCounter = 1006;
									}
									else
										heliLoopCounter--;
								}
								else if(temp.getDistanceTo(player) < 30)
									temp.setDirection(90+temp.getDegreesTo(player));
								else{
									temp.isMoving = false;
								}	
							}

						}
						else{					
							if(temp.getDistanceTo(player) < 200){
								temp.setDirection(90+temp.getDegreesTo(player));
							}						
							AIcounter = 0;
						}
					}
					else
						AIcounter++;
					
					//fire at player
					if (temp.getCoolDown() <= 0) {
						if((temp instanceof EnemyStructure)){
							EnemyStructure enemy = (EnemyStructure)(temp);
							if(enemy.getStructure() == EnemyStructure.StructureType.ATTACK){
								if(temp.getDistanceTo(player) < 200){
									fireBullet(temp,90+temp.getDegreesTo(player),temp);
									//enemyshot sound
									try{
										input = new FileInputStream("sounds/enemyshot.wav");
										audio = new AudioStream(input);
										AudioPlayer.player.start(audio);
									}
									catch(Exception e){

									}
								}
							}
						}
						else
							if(temp.getEnemyType() != EnemyObject.EnemyType.AIR){
								if(temp.getDistanceTo(player) < 80){
									fireBullet(temp,90+temp.getDegreesTo(player),temp);
									//enemyshot sound
									try{
										input = new FileInputStream("sounds/enemyshot.wav");
										audio = new AudioStream(input);
										AudioPlayer.player.start(audio);
									}
									catch(Exception e){

									}
								}
							}
							else{
								if(temp.getDistanceTo(player) < 150){
									fireBullet(temp,90+temp.getDegreesTo(player),temp);
									//enemyshot sound
									try{
										input = new FileInputStream("sounds/enemyshot.wav");
										audio = new AudioStream(input);
										AudioPlayer.player.start(audio);
									}
									catch(Exception e){

									}
								}
							}

					}
					temp.decrementCoolDown();     		

				}    //end AI
					

				// NOTE: you must call doTimeStep for ALL game objects once per frame!
				// updating step for each object
				for (int i = 0 ; i < objects.size() ; i++) {
					objects.elementAt(i).doTimeStep();
				}

				// setting the camera offset (keeps screen from moving when you reach the end of the map)
				if(!(player.getPosition().y <= Runner.getScreenDimensions().height/2) && !(player.getPosition().y >= (gridSize * 64) - Runner.getScreenDimensions().height/2)) {
					offset.y = -player.getPosition().y + (this.getViewportDimension().height/2);
				}        
				// x values
				if (!(player.getPosition().x <= Runner.getScreenDimensions().width/2) && !(player.getPosition().x >= (gridSize * 64) - Runner.getScreenDimensions().width/2)){
					offset.x = -player.getPosition().x + (this.getViewportDimension().width/2);
				}


				//        Vector<GridGameObject> gridCollisions = new Vector<GridGameObject>();        
				for (int i = 0 ; i < objects.size() ; i++) {        	
					for (int j = i+1 ; j < objects.size() ; j++) {    
						GridGameObject o1 = objects.elementAt(i);
						GridGameObject o2 = objects.elementAt(j);
						//            	gridCollisions = collisions.checkGridCollisions(objects,o1);            	

						if((! o2.isMarkedForDestruction()) && (! o1.isMarkedForDestruction())){

							if(collisions.checkForCollision(objects, o1,o2)){
								//collision detection, grid structure, bounding-box, pixel tests
								//            		if (o1 instanceof WallObject && o2 instanceof WallObject) {
								//            			//do nothing
								//            		}
								if ((o1 instanceof BulletObject && o2 instanceof WallObject) || (o1 instanceof WallObject && o2 instanceof BulletObject)) {
									// just destroy the bullet, not the wall
									if (o1 instanceof BulletObject)
										o1.setMarkedForDestruction(true);
									else
										o2.setMarkedForDestruction(true);            			
								}
								else if((o1 instanceof PowerUpObject && o2 instanceof PlayerObject)||(o2 instanceof PowerUpObject && o1 instanceof PlayerObject)){
									//pick up object
									int ammoPack;
									if(o1 instanceof PowerUpObject){
										o1.setMarkedForDestruction(true);
										ammoPack = ((PlayerObject)o2).increaseAmmo();           				
									}
									else{
										o2.setMarkedForDestruction(true);
										ammoPack = ((PlayerObject)o1).increaseAmmo();
									}
									ammoCollected = true;
									displayPickupDelay = 500;

									if(ammoPack == -1){
										//do nothing
									}
									else if(ammoPack == 0)
										picketUpAmmo.setActiveTexture(0);
									else if(ammoPack == 1)
										picketUpAmmo.setActiveTexture(1);
									else if(ammoPack == 2)
										picketUpAmmo.setActiveTexture(0);
									else			
										picketUpAmmo.setActiveTexture(2);
								}
								else if((o1 instanceof PowerUpObject && o2 instanceof EnemyObject)||(o2 instanceof PowerUpObject && o1 instanceof EnemyObject)){
									//do nothing            			
								}
								else if((o1 instanceof PowerUpObject && o2 instanceof BulletObject)||(o2 instanceof PowerUpObject && o1 instanceof BulletObject)){
									//do nothing
								}
								else if((o1 instanceof EnemyObject) && (o2 instanceof EnemyObject)){
									EnemyObject enemy = (EnemyObject)(o1);
									if(enemy.getEnemyType() == EnemyObject.EnemyType.AIR){
										 //fly over
									}
									else{
										enemy = (EnemyObject)(o2);
										if(enemy.getEnemyType() == EnemyObject.EnemyType.AIR){
											//fly over
										}
										else{
											if(o2 instanceof EnemyStructure)
												((EnemyObject)o1).revertPosition();
											else
												((EnemyObject)o2).revertPosition();
										}											 
									}
									
								}
								else if((o1 instanceof EnemyObject) && (o2 instanceof BulletObject)){
									BulletObject bullet = (BulletObject)((PhysicalObject)o2);

									//checks if player is shooting enemy
									if(bullet.getSource() instanceof PlayerObject){
										o1.decreaseHealth(bullet.getSource().getDamage());
										o2.decreaseHealth(o1.getDamage());
									}            				
								}
								else if((o2 instanceof EnemyObject) && (o1 instanceof BulletObject)){
									BulletObject bullet = (BulletObject)((PhysicalObject)o1);

									if(bullet.getSource() instanceof PlayerObject){
										o1.decreaseHealth(o2.getDamage());
										o2.decreaseHealth(bullet.getSource().getDamage());
									}
								}
								else if((o1 instanceof PlayerObject) && (o2 instanceof BulletObject)){
									BulletObject bullet = (BulletObject)((PhysicalObject)o2);
									o1.decreaseHealth(bullet.getSource().getDamage());
									o2.setMarkedForDestruction(true);
								}
								else if((o2 instanceof PlayerObject) && (o1 instanceof BulletObject)){
									BulletObject bullet = (BulletObject)((PhysicalObject)o1);
									o2.decreaseHealth(bullet.getSource().getDamage());
									o1.setMarkedForDestruction(true);
								}
								else if((o1 instanceof PlayerObject && o2 instanceof EnemyObject) || (o2 instanceof PlayerObject && o1 instanceof EnemyObject)){
									//check if player collides with chopper
									if(o2 instanceof EnemyObject){
										EnemyObject enemy = (EnemyObject)(o2);
										if(enemy.getEnemyType() == EnemyObject.EnemyType.AIR){
											//do nothing
										}
										else{
											((PlayerObject)o1).revertPosition ();
										}
									}
									else{
										EnemyObject enemy = (EnemyObject)(o1);
										if(enemy.getEnemyType() == EnemyObject.EnemyType.AIR){
											//do nothing
										}
										else{
											((PlayerObject)o2).revertPosition ();
										}
									}
								}
								else if (o1 instanceof PlayerObject){
									((PlayerObject)o1).revertPosition ();
								}
								else if (o2 instanceof PlayerObject){
									((PlayerObject)o2).revertPosition ();
								}
								else if((o1 instanceof WallObject && o2 instanceof EnemyObject) || (o2 instanceof WallObject && o1 instanceof EnemyObject)){
									if(o1 instanceof EnemyObject){
										EnemyObject enemy = (EnemyObject)(o1);
										if((enemy.getEnemyType() == EnemyObject.EnemyType.GROUND) || (enemy.getEnemyType() == EnemyObject.EnemyType.TANK))
											((EnemyObject)o1).revertPosition();
									}										
									else{
										EnemyObject enemy = (EnemyObject)(o2);
										if((enemy.getEnemyType() == EnemyObject.EnemyType.GROUND) || (enemy.getEnemyType() == EnemyObject.EnemyType.TANK))
											((EnemyObject)o2).revertPosition();
									}										
								}
							}            	
						}//if marked for destruction
					}
				}

				// destroying units that need to be destroyed
				GridGameObject tempObject;
				if(enemies.size()-1 <= 0){
					allEnemiesAreDead = true;
				}
				
				for (int i = 0 ; i < objects.size() ; i++) {
					tempObject = objects.elementAt(i);
					if (tempObject.isMarkedForDestruction()) {
						if (tempObject == player) {
							alive = false;
							player = null;
						}
						// removing object from list of GridGameObjects
						objects.remove(i);

						if(tempObject instanceof EnemyStructure){
							if(((EnemyStructure)tempObject).getStructure() == EnemyStructure.StructureType.PASSIVE)
								isBarracksExists = false;
							
							//simulate explosion
							setAnimation(tempObject);
							
							//sound of destruction
							try{
								input = new FileInputStream("sounds/building_destroyed.wav");
								audio = new AudioStream(input);
								AudioPlayer.player.start(audio);
							}
							catch(Exception e){

							}
						}
						else if(tempObject instanceof EnemyObject){ //drop ammo/weapon pack
							if(Math.random() < 0.7){
								PowerUpObject ammo = new PowerUpObject(tempObject.getPosition().x, tempObject.getPosition().y, 0, 0);
								ammo.addTexture(ammoTexture);
								objects.add(ammo);
							}
							
							if(((EnemyObject)tempObject).getEnemyType() == EnemyObject.EnemyType.AIR){
								AudioPlayer.player.stop(audio_heli);
								heliExists = false;
								heliLoopCounter = 0;
							}
							else if(((EnemyObject)tempObject).getEnemyType() == EnemyObject.EnemyType.TANK){
								AudioPlayer.player.stop(audio_tank);
								tankExists = false;
								tankCounter = 0;
							}
							if(((EnemyObject) tempObject).canEnemyExplode()){
								//simulate explosion
								setAnimation(tempObject);
							}
						}               

						i--;
					}
				}
			}//end of pause		
		}//you're dead
	}
	
	private void missionFailed(){
		AudioPlayer.player.stop(audio_background);
		Object[] options = {"Restart Mission","Main Menu","Abort"};
		int response = JOptionPane.showOptionDialog(Runner.gameframe, "You have failed the mission!!","Defeat!!!",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.ERROR_MESSAGE,null,options,options[2]);
		Runner.gameframe.requestFocus();
		if(response == JOptionPane.YES_OPTION){
			//restart level
			Runner.restartCurrentGame();
		}
		else if(response == JOptionPane.NO_OPTION){
			//return user to main menu
			Runner.displayWelcomeScreen();
		}
		else if(response == JOptionPane.CANCEL_OPTION){
			//do nothing
			aborted = true;
		}
		else if(response == JOptionPane.CLOSED_OPTION){
			//do nothing
			aborted = true;
		}
	}

	//proceed to next level or go back to main menu
	private void missionAccomplished(){
		Runner.finishedLevel(currentLevel);
		AudioPlayer.player.stop(audio_background);
		
		Object[] options = {"Proceed","Main Menu","Abort"};
		int response = JOptionPane.showOptionDialog(null, "Well done, you have completed Level "+currentLevel+"\n Would you like to continue to Level "+
						(currentLevel+1)+"?","Victory!!!",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[2]);
		if(response == JOptionPane.YES_OPTION){
			//proceed to next level
			Runner.restartCurrentGame();
		}
		else if(response == JOptionPane.NO_OPTION){
			//return user to main menu
			Runner.displayWelcomeScreen();
		}
		else if(response == JOptionPane.CANCEL_OPTION){
			//do nothing
			missionSuccess = true;
		}
		else if(response == JOptionPane.CLOSED_OPTION){
			//do nothing
			missionSuccess = true;
		}		
		allEnemiesAreDead = false;
		isBarracksExists = true;
		paused = true;
	}
	//==================================================================================================


	public void renderStep(GameDrawer drawer) {
		//For every object that you want to be rendered, you must call the draw function with it as a parameter

		// NOTE: Always draw transparent objects last!

		// Off-setting the world so that all objects are drawn 
		drawer.setWorldOffset(offset.x, offset.y);
		drawer.setColour(1.0f, 1.0f, 1.0f, 1.0f);

		// drawing the ground tiles
		for (int i = 0 ; i < gridTile.length ; i++ ) {
			for (int j = 0 ; j < gridTile[i].length ; j++ ) {
				drawer.draw(gridTile[i][j], -1);
			}
		}

		// drawing all the objects in the game
		for (GridGameObject o: objects) {
			drawer.draw(o, 1.0f, 1.0f, 1.0f, 1.0f, 0);
		}

		//draw lines for cursor TODO - rocket trail
		//        if (player != null) {
		//        	Point2D.Float [] cursor_vert_Line = {new Point2D.Float(mousePos.x,mousePos.y-10), new Point2D.Float(mousePos.x,mousePos.y+10)};
		//        	Point2D.Float [] cursor_hori_Line = {new Point2D.Float(mousePos.x-10,mousePos.y), new Point2D.Float(mousePos.x+10,mousePos.y)};
		//        	drawer.draw(GameDrawer.LINES, cursor_hori_Line, lineColours, 0.5f);
		//        	drawer.draw(GameDrawer.LINES, cursor_vert_Line, lineColours, 0.5f);
		//        }

		drawer.setColour(1.0f,1.0f,1.0f,1.0f);

		//health bar for each enemy object
		Point2D.Float h1,h2,h3,h4,h2c,h3c;
		Point2D.Float [] healthbar;
		int heightOfBar = 0;

		for(EnemyObject o : enemies){
			if(o instanceof EnemyStructure)
				heightOfBar = 65;
			else
				heightOfBar = 7;

			h1 = new Point2D.Float(o.getPosition().x,o.getPosition().y + heightOfBar + 2);
			h2 = new Point2D.Float(o.getPosition().x + 30,o.getPosition().y + heightOfBar + 2);
			h3 = new Point2D.Float(o.getPosition().x + 30,o.getPosition().y + heightOfBar);
			h4 = new Point2D.Float(o.getPosition().x,o.getPosition().y + heightOfBar);
			healthbar = new Point2D.Float[] {h1,h2,h3,h4};
			drawer.draw(GameDrawer.POLYGON,healthbar,1.0f,0f,0f,1f,0.2f);
			h2c = new Point2D.Float((o.getPosition().x + (30 * o.getPercentage())),o.getPosition().y + heightOfBar + 2);
			h3c = new Point2D.Float((o.getPosition().x + (30 * o.getPercentage())),o.getPosition().y + heightOfBar);
			healthbar = new Point2D.Float[] {h1,h2c,h3c,h4};
			drawer.draw(GameDrawer.POLYGON,healthbar,0f,1.0f,0f,1f,0.2f);  
			drawer.draw(serif, (int)(o.getPercentage()*100)+"%", new Point2D.Float(o.getPosition().x,o.getPosition().y+heightOfBar + 6), 0.0f, 0.0f, 0.0f, 0.7f, 0.3f);
		}

		// Changing the offset to 0 so that drawn objects won't move with the camera
		drawer.setWorldOffset(0, 0);		
		
		if(missionSuccess)
			drawer.draw(arial, "Mission Accomplished!!!", new Point2D.Float((int)(Runner.getScreenDimensions().width*0.26),(int)(Runner.getScreenDimensions().height*0.48)), 0.0f, 1.0f, 0.0f, 0.7f, 0.1f);
		else if(aborted){
			drawer.draw(arial, "Mission Failed!!!@#$&", new Point2D.Float((int)(Runner.getScreenDimensions().width*0.29),(int)(Runner.getScreenDimensions().height*0.48)), 1.0f, 0.0f, 0.0f, 0.7f, 0.1f);
		}
		else if(! alive){
			missionFailed();
			paused = true;
		}
		else if(allEnemiesAreDead || (! isBarracksExists)){  
			//if you kill all the enemies or destroy target
			if(currentLevel == 3){
				drawer.draw(arial, "Well done, you have survived the AfterMath", new Point2D.Float((int)(Runner.getScreenDimensions().width*0.18),(int)(Runner.getScreenDimensions().height*0.5)), 1.0f, 0.0f, 0.0f, 0.7f, 0.1f);
				drawer.draw(arial, "May the force be with you...", new Point2D.Float((int)(Runner.getScreenDimensions().width*0.3),(int)(Runner.getScreenDimensions().height*0.4)), 1.0f, 0.0f, 0.0f, 0.7f, 0.1f);
				paused = true;
			}
			else
				missionAccomplished();
		}
		else if(paused) //pause label
			drawer.draw(arial, "Game paused...", new Point2D.Float((int)(Runner.getScreenDimensions().width*0.35),(int)(Runner.getScreenDimensions().height*0.5)), 0.0f, 0.0f, 0.0f, 0.7f, 0.1f);

		//health bar for player
		if(alive){
			h1 = new Point2D.Float(122,68);
			h2 = new Point2D.Float(254 - (132 - (132 * player.getPercentage())),68);
			h3 = new Point2D.Float(254 - (132 - (132 * player.getPercentage())),42.5f);
			h4 = new Point2D.Float(122,42.5f);
			healthbar = new Point2D.Float[] {h1,h2,h3,h4};
			drawer.draw(hp_bar,1.0f,1.0f,1.0f,1.0f,0.1f);

			if(h2.x >= h1.x){
				if(player.getPercentage() > 0.6)
					drawer.draw(GameDrawer.POLYGON,healthbar,0f,1.0f,0f,1f,0.2f);
				else if(player.getPercentage() > 0.35)
					drawer.draw(GameDrawer.POLYGON,healthbar,0.7f,1.0f,0f,1f,0.2f);	
				else
					drawer.draw(GameDrawer.POLYGON,healthbar,1.0f,0f,0f,1f,0.2f);        	

				drawer.draw(serif, (int)(player.getPercentage()*100)+"%", new Point2D.Float(58,51), 0.0f, 0.0f, 0.0f, 0.7f, 0.3f);
			}

			//display the current weapon being used
			drawer.draw(currentWeapon,1.0f,1.0f,1.0f,1.0f,0.1f);
			//display ammo available
			drawer.draw(serif, (int)(player.getAmmo()) + "", new Point2D.Float(screenSize.width - 85,28), 0.0f, 0.0f, 0.0f, 0.7f, 0.3f);
			drawer.draw(currentAmmo, 1.0f, 1.0f, 1.0f, 1.0f, 0.1f);
			//display what ammo was picked up

			if(ammoCollected){
				if(displayPickupDelay >=0 ){
					drawer.draw(serif,player.getPickedAmmo()+"", new Point2D.Float(screenSize.width - 100,200), 1.0f, 0.0f, 0.0f, 0.7f, 0.3f);
					drawer.draw(picketUpAmmo, 1.0f, 1.0f, 1.0f, 1.0f, 0.1f);
					displayPickupDelay--;
				}
				else{
					displayPickupDelay = 500;
					ammoCollected = false;
				}
			}//end ammo

		}//end health        

		// Some debug type info to demonstrate the font drawing
		//        if (player != null) {
		//        	drawer.draw(arial, ""+player.getDirection(), new Point2D.Float(20,120), 1.0f, 0.5f, 0.0f, 0.7f, 0.1f);
		//        }
		//        drawer.draw(arial, ""+mouseWheelTick, new Point2D.Float(20,68), 1.0f, 0.5f, 0.0f, 0.7f, 0.1f);
		//        drawer.draw(serif, ""+mousePos.x +":"+mousePos.y, new Point2D.Float(20,20), 1.0f, 0.5f, 0.0f, 0.7f, 0.1f);
	}
}








