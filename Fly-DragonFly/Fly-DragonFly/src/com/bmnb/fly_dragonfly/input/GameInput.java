/**
 * 
 */
package com.bmnb.fly_dragonfly.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.bmnb.fly_dragonfly.Fly_DragonFly;
import com.bmnb.fly_dragonfly.map.TutorialScreenSpawner;
import com.bmnb.fly_dragonfly.objects.Player;
import com.bmnb.fly_dragonfly.screens.GameScreen;
import com.bmnb.fly_dragonfly.screens.TutorialScreens;
import com.bmnb.fly_dragonfly.sound.MediaPlayer;

/**
 * @author Brandon
 * 
 */
public class GameInput implements InputProcessor {

	protected float width, height;
	protected Player player;
	protected int movePointer = -1, shootPointer = -1;
	protected Fly_DragonFly our_game;
	protected GameScreen gs;
	// debug
	protected GameScreen screen;
	
	public GameInput(float width, float height, Player player, Fly_DragonFly g, GameScreen gs) {
		this.width = width;
		this.height = height;
		this.player = player;
		our_game = g;
		this.gs = gs;
	}

	public void setGameScreen(GameScreen in){
		screen = in;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#keyDown(int)
	 */
	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK){
			our_game.returnToMenu();
			MediaPlayer.stopMusic("data/sound/flydragonfly_bg_music.mp3");
			MediaPlayer.stopAllSoundInstances();
			return true;
		}
		/*else if(keycode == Input.Keys.X){
			screen.tutScreen.showTutorialScreen(1);
			return true;
		}*/
		
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#keyUp(int)
	 */
	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#keyTyped(char)
	 */
	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#touchDown(int, int, int, int)
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenX = (int) (screenX * ((float) width / (float) Gdx.graphics
				.getWidth()));
		screenY = (int) (height - screenY
				* ((float) height / (float) Gdx.graphics.getHeight()));
//	 	debug
		/*if (screenX > width * 0.4f && screenX < width * 0.6f && screenY > height * 0.8f){
			screen.spawnBoids();
			return true;
		}*/
//	 	debug
//		if (screenX < width * 0.2f && screenY > height * 0.8f){
//			player.convertWeaponFireflies();
//			return true;
//		}
////	 	debug
//		if (screenX > width * 0.8f && screenY > height * 0.8f){
//			player.convertWeaponMossies();
//			return true;
//		}

		//player presses button area on to remove tutorial screen
		if (screenX > ((30+(width-70))/2)-50 && screenY > ((height/3)+70) &&
				screenX < ((30+(width-70))/2)-50+100 && screenY < ((height/3)+70)+50 && screen.tutScreen.isShowingTutorialScreen()){
			screen.tutScreen.okBtnClicked();
			return true;
		}

		if (shootPointer == -1) {
			if (screenX <= (width * 0.15f) && screenY <= (height * 0.35f + width * 0.075f) && screenY >= (height * 0.35f - width * 0.075f)) {
				player.startShooting();
				shootPointer = pointer;
				return true;
			}
		}

		if (movePointer == -1) {
			player.moveToFinger(new Vector2(screenX, screenY));
			movePointer = pointer;
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#touchUp(int, int, int, int)
	 */
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		screenX = (int) (screenX * ((float) width / (float) Gdx.graphics
				.getWidth()));
		screenY = (int) (height - screenY
				* ((float) height / (float) Gdx.graphics.getHeight()));

		//player presses button area on to remove tutorial screen
		if (screenX > ((30+(width-70))/2)-50 && screenY > ((height/3)+70) &&
				screenX < ((30+(width-70))/2)-50+100 && screenY < ((height/3)+70)+50 && screen.tutScreen.isShowingTutorialScreen()){
			screen.tutScreen.okBtnReleased();	
			if (gs.tutScreen.getTutID() == 0 || gs.tutScreen.getTutID() == 7){
				MediaPlayer.stopMusic("data/sound/flydragonfly_bg_music.mp3");
				MediaPlayer.stopAllSoundInstances();
				our_game.returnToMenu();
			}
			return true;
		}
		
		if (shootPointer == pointer) {
			player.stopShooting();
			shootPointer = -1;
			return true;
		}

		if (pointer == movePointer) {
			player.stopMovingToFinger();
			movePointer = -1;
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#touchDragged(int, int, int)
	 */
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		screenX = (int) (screenX * ((float) width / (float) Gdx.graphics
				.getWidth()));
		screenY = (int) (height - screenY
				* ((float) height / (float) Gdx.graphics.getHeight()));

		if (pointer == movePointer) {
			player.moveToFinger(new Vector2(screenX, screenY));
			return true;
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#mouseMoved(int, int)
	 */
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		screenX = (int) (screenX * ((float) width / (float) Gdx.graphics
				.getWidth()));
		screenY = (int) (height - screenY
				* ((float) height / (float) Gdx.graphics.getHeight()));
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.badlogic.gdx.InputProcessor#scrolled(int)
	 */
	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
