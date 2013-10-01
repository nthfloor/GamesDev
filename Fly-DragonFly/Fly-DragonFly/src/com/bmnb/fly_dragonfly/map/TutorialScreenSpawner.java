package com.bmnb.fly_dragonfly.map;
/**
 * Abstract Data Type for Tutorial Screen information as read from the map
 * @author benjamin
 */
public class TutorialScreenSpawner {
	int id;
	float y;
	public TutorialScreenSpawner(int id, float y){
		this.id = id;
		this.y = y;
	}
	public int getId() {
		return id;
	}
	public float getY() {
		return y;
	}
}
