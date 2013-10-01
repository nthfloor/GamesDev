package com.bmnb.fly_dragonfly.map;

import com.badlogic.gdx.math.Vector2;
/**
 * Abstract Data Type for flock spawner information as read from the map
 * @author benjamin
 */
public class MoziSpawner {
	enum SpawnerType {mosquitoes,fireflies}
	
	Vector2 pos;
	float deviation;
	int numberOfBoids;
	SpawnerType type;
	public MoziSpawner(Vector2 pos,float deviation, int numberOfBoids, SpawnerType type){
		this.pos = pos.cpy();
		this.deviation = deviation;
		this.numberOfBoids = numberOfBoids;
		this.type = type;
	}
	public SpawnerType getType() {
		return type;
	}
	public Vector2 getPos() {
		return pos.cpy();
	}
	public float getDeviation() {
		return deviation;
	}
	public int getNumberOfBoids() {
		return numberOfBoids;
	}
}
