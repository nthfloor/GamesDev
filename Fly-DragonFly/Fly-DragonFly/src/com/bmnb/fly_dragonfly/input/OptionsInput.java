package com.bmnb.fly_dragonfly.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.bmnb.fly_dragonfly.Fly_DragonFly;
import com.bmnb.fly_dragonfly.screens.GameScreen;
import com.bmnb.fly_dragonfly.screens.OptionsScreen;

public class OptionsInput implements InputProcessor{
	protected OptionsScreen screen;
	protected Fly_DragonFly our_game;
	protected float width, height;
	protected float menuScreenStartX, menuScreenStartY, menuScreenWidth, menuScreenHeight;
	
	public OptionsInput(Fly_DragonFly g){
		our_game = g;
		width = GameScreen.width;
		height = GameScreen.height;
		menuScreenStartX = 40;
		menuScreenStartY = 50;		
		menuScreenWidth = width-80;
		menuScreenHeight = height-350;	
	}
	public void setGameScreen(OptionsScreen in){
		screen = in;
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Keys.BACK){
			our_game.returnToMenu();
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenX = (int) (screenX * ((float) width / (float) Gdx.graphics
				.getWidth()));
		screenY = (int) (height - screenY
				* ((float) height / (float) Gdx.graphics.getHeight()));
		
		int startX = (int)(((menuScreenStartX+menuScreenWidth)/2)-(int)(width*0.47)/2+10);
		int startY = (int)(((menuScreenStartY+menuScreenHeight)/2)+(int)(height*0.14*0.5));		
		//player starts new game		
		if (screenX >  startX && 
				screenY > startY &&
				screenX < startX+(int)(width*0.47) && 
				screenY < startY+(int)(height*0.14)){
			screen.playBtnClicked();					
			return true;
		}
		startX = (int)((menuScreenStartX+menuScreenWidth)/2)-(int)(width*0.33)/2+10;
		startY = (int)((menuScreenStartY+menuScreenHeight)/2)-(int)(height*0.14*1.1);
		//player exits game
		if (screenX >  startX && 
				screenY > startY &&
				screenX < startX+(int)(width*0.33) && 
				screenY < startY+(int)(height*0.1)){
			screen.exitBtnClicked();						
			return true;
		}
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		screenX = (int) (screenX * ((float) width / (float) Gdx.graphics
				.getWidth()));
		screenY = (int) (height - screenY
				* ((float) height / (float) Gdx.graphics.getHeight()));
		
		int startX = (int)(((menuScreenStartX+menuScreenWidth)/2)-(int)(width*0.47)/2+10);
		int startY = (int)(((menuScreenStartY+menuScreenHeight)/2)+(int)(height*0.14*0.5));
		//player starts new game		
		if (screenX >  startX && 
				screenY > startY &&
				screenX < startX+(int)(width*0.47) && 
				screenY < startY+(int)(height*0.14)){
			screen.playBtnReleased();			
			return true;
		}
		startX = (int)((menuScreenStartX+menuScreenWidth)/2)-(int)(width*0.33)/2+10;
		startY = (int)((menuScreenStartY+menuScreenHeight)/2)-(int)(height*0.14*1.1);
		//player exits game
		if (screenX >  startX && 
				screenY > startY &&
				screenX < startX+(int)(width*0.33) && 
				screenY < startY+(int)(height*0.1)){
			screen.exitBtnReleased();						
			return true;
		}
		
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
