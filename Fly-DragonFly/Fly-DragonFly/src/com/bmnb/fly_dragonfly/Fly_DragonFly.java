package com.bmnb.fly_dragonfly;

import com.badlogic.gdx.Game;
import com.bmnb.fly_dragonfly.screens.GameScreen;
import com.bmnb.fly_dragonfly.screens.MenuScreen;
import com.bmnb.fly_dragonfly.screens.OptionsScreen;

/**
 * Main class to start the game This calls upon the first screen (splash screen)
 * 
 * @author Brandon James Talbot
 * 
 */

public class Fly_DragonFly extends Game {

	@Override
	public void create() {
		returnToMenu();	
	}
	//start new game, change to game screen
	public void startGame(){
		setScreen(new GameScreen(this));
	}
	
	//open up main menu screen
	public void goToOptions(){
		setScreen(new OptionsScreen(this));
	}

	//open up main menu screen
	public void returnToMenu(){
		setScreen(new MenuScreen(this));
	}
}
