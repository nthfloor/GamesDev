package com.bmnb.fly_dragonfly.objects;

import com.badlogic.gdx.math.Vector2;
import com.bmnb.fly_dragonfly.screens.GameScreen;

public abstract class Enemy extends GameObject{

	protected float health = 100;
	protected float enemykillReward;
	protected Player player;
	
	public Enemy(Vector2 position, float width, float height, float speed,
			float scWidth, float scHeight, Player player, float killreward) {
		super(position, width, height, speed, scWidth, scHeight);
		this.player = player;
		enemykillReward = killreward;
	}
	
	public void doDamage(float amount){
		health -= amount;
		
		if (health <= 0 && !dead){
			player.increaseScoreBy(enemykillReward);
			GameScreen.recentPoints.add(Math.round(enemykillReward));
			kill();
		}
	}
}
