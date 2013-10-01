package com.bmnb.fly_dragonfly.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.bmnb.fly_dragonfly.screens.GameScreen;

public class Spider extends StaticEnemy {
	private static final float DIFF_Y_TRIGGER = 900;
	private boolean hasTriggered = false;
	
	public Spider(Vector2 position, float width, float height, float speed,
			float scWidth, float scHeight, Player player) {
		super(position, width, height, speed, scWidth, scHeight, player,100);
		
		sortVal = 2;
		if (this.getPosition().x < screenWidth / 2f)
			setTexture(new Texture("data/textures/spider_withnet2.png"));
		else
			setTexture(new Texture("data/textures/spider_withnet.png"));
	}
	
	@Override
	public void kill(){
		removeable = true;
		super.kill();
	}
	
	@Override
	public void update(float delta){
		if (this.getY() - player.getY() < DIFF_Y_TRIGGER && !hasTriggered){
			hasTriggered = true;
			GameScreen.addObject(new Web(this.getPosition(),0,0,GameScreen.scrollSpeed,GameScreen.width,GameScreen.height,player));
		}

		super.update(delta);
	}
}
