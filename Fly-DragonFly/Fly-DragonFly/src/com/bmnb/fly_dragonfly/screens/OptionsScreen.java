package com.bmnb.fly_dragonfly.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bmnb.fly_dragonfly.Fly_DragonFly;
import com.bmnb.fly_dragonfly.input.OptionsInput;
import com.bmnb.fly_dragonfly.sound.MediaPlayer;

public class OptionsScreen implements Screen{
	private Texture audio_on_tex;
	private Texture audio_on_tex_clicked;
	private Texture audio_off_tex;
	private Texture audio_off_tex_clicked;
	private Texture back_tex;
	private Texture back_tex_clicked;
	private Texture menu_back;
	private Texture dragon_menu;
	
	private boolean back_clicked = false;
	private boolean audio_clicked = false;
	private boolean backToMenu = false;
	private boolean muteSound = false;
	private int counter;
	
	protected CharSequence message = "";
	protected float screenHeight, screenWidth;

	protected SpriteBatch batch;
	protected OrthographicCamera camera;
	protected Fly_DragonFly game;
	protected BitmapFont font;
	
	public OptionsScreen(Fly_DragonFly g){
		game = g;
	}

	public void playBtnClicked(){
		audio_clicked = true;
	}
	public void playBtnReleased(){
		audio_clicked = false;
		muteSound = true;
		counter = 0;
	}

	public void exitBtnClicked(){
		back_clicked = true;
	}
	public void exitBtnReleased(){
		back_clicked = false;
		backToMenu = true;
		counter = 0;
	}
	
	@Override
	public void render(float delta) {
		//clear screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); 
		
		//draw everything
		batch.begin();
		
		//draw main menu
		float menuScreenStartX = 40;
		float menuScreenStartY = 50;		
		float menuScreenWidth = screenWidth-80;
		float menuScreenHeight = screenHeight-350;
		
		//draw logo
		batch.draw(dragon_menu, ((menuScreenStartX+menuScreenWidth)/2)-120, 
				menuScreenStartY + menuScreenHeight, 300, 250, 0, 0, 
				dragon_menu.getWidth(), dragon_menu.getHeight(), false, false);
		batch.draw(menu_back, menuScreenStartX, menuScreenStartY, menuScreenWidth, menuScreenHeight, 0, 0, 
				menu_back.getWidth(), menu_back.getHeight(), false, false);
		
		//draw menu buttons				
		if(!audio_clicked){
			if(MediaPlayer.isSoundOn())
				batch.draw(audio_on_tex, ((menuScreenStartX+menuScreenWidth)/2)-(int)(screenWidth*0.47)/2+10, 
						((menuScreenStartY+menuScreenHeight)/2)+(int)(screenHeight*0.2*0.2),
						(int)(screenWidth*0.47), (int)(screenHeight*0.2), 0, 0, 
						audio_on_tex.getWidth(), audio_on_tex.getHeight(), false, false);	
			else
				batch.draw(audio_off_tex, ((menuScreenStartX+menuScreenWidth)/2)-(int)(screenWidth*0.47)/2+10, 
						((menuScreenStartY+menuScreenHeight)/2)+(int)(screenHeight*0.2*0.2),
						(int)(screenWidth*0.47), (int)(screenHeight*0.2), 0, 0, 
						audio_off_tex.getWidth(), audio_off_tex.getHeight(), false, false);	
		}
		else{
			if(MediaPlayer.isSoundOn())
				batch.draw(audio_on_tex_clicked, ((menuScreenStartX+menuScreenWidth)/2)-(int)(screenWidth*0.47)/2+10, 
						((menuScreenStartY+menuScreenHeight)/2)+(int)(screenHeight*0.2*0.2),
						(int)(screenWidth*0.47), (int)(screenHeight*0.2), 0, 0, 
						audio_on_tex_clicked.getWidth(), audio_on_tex_clicked.getHeight(), false, false);	
			else
				batch.draw(audio_off_tex_clicked, ((menuScreenStartX+menuScreenWidth)/2)-(int)(screenWidth*0.47)/2+10, 
						((menuScreenStartY+menuScreenHeight)/2)+(int)(screenHeight*0.2*0.2),
						(int)(screenWidth*0.47), (int)(screenHeight*0.2), 0, 0, 
						audio_off_tex_clicked.getWidth(), audio_off_tex_clicked.getHeight(), false, false);
		}
		
		if(!back_clicked)
			batch.draw(back_tex, ((menuScreenStartX+menuScreenWidth)/2)-(int)(screenWidth*0.33)/2+10, 
					((menuScreenStartY+menuScreenHeight)/2)-(int)(screenHeight*0.14*1.1), 
					(int)(screenWidth*0.33), (int)(screenHeight*0.1), 0, 0, 
					back_tex.getWidth(), back_tex.getHeight(), false, false);
		else
			batch.draw(back_tex_clicked, ((menuScreenStartX+menuScreenWidth)/2)-(int)(screenWidth*0.33)/2+10, 
					((menuScreenStartY+menuScreenHeight)/2)-(int)(screenHeight*0.14*1.1), 
					(int)(screenWidth*0.33), (int)(screenHeight*0.1), 0, 0, 
					back_tex_clicked.getWidth(), back_tex_clicked.getHeight(), false, false);
		
		//display click animation
		if(muteSound){
			counter++;
			if(counter > 5){
				if(MediaPlayer.isSoundOn()){
					MediaPlayer.setSoundOn(false);					
				}
				else{
					MediaPlayer.setSoundOn(true);
				}
				muteSound = false;
			}
		}
		if(backToMenu){
			counter++;
			if(counter > 5)
				game.returnToMenu();
		}
		
		font.setColor(Color.BLACK);
		font.setScale(2.0f);
		message = "Options";			
		font.draw(batch, message, ((menuScreenStartX+menuScreenWidth)/2)-170,
				menuScreenStartY+menuScreenHeight-30);	
		
		batch.end();		
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void show() {
		screenHeight = GameScreen.height;
		screenWidth = GameScreen.width;
		// setting up of major devices
		batch = new SpriteBatch();
		camera = new OrthographicCamera(screenWidth, screenHeight);
		camera.translate(screenWidth / 2f, screenHeight / 2f);
		camera.update();
		batch.setProjectionMatrix(camera.combined); // needs only be done once,
		// since camera does not
		// move
		
		dragon_menu = new Texture("data/menu/dragonfly.png");
		menu_back = new Texture("data/menu/tutorial_bg.png");
		audio_off_tex = new Texture("data/menu/audio_off.png");
		audio_on_tex = new Texture("data/menu/audio_on.png");
		audio_off_tex_clicked = new Texture("data/menu/audio_off_clicked.png");
		audio_on_tex_clicked = new Texture("data/menu/audio_on_clicked.png");
		back_tex = new Texture("data/menu/back_button.png");
		back_tex_clicked = new Texture("data/menu/back_button_clicked.png");
		
		// set the input processor
		Gdx.input.setInputProcessor(new OptionsInput(game));
		Gdx.input.setCatchBackKey(true);
		Gdx.input.setCatchMenuKey(true);
		((OptionsInput)Gdx.input.getInputProcessor()).setGameScreen(this);

		//Load font
		font = new BitmapFont(Gdx.files.internal("data/font/commicsans.fnt"),
				Gdx.files.internal("data/font/commicsans.png"), false);	
	}

	@Override
	public void hide() {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {
		dragon_menu.dispose();
		menu_back.dispose();
		audio_off_tex.dispose();
		audio_on_tex.dispose();
		audio_off_tex_clicked.dispose();
		audio_on_tex_clicked.dispose();
		back_tex_clicked.dispose();
		back_tex.dispose();		
	}			
}
