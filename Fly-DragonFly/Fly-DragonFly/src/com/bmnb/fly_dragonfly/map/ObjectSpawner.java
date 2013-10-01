package com.bmnb.fly_dragonfly.map;
/**
 * Abstract Datatype for Object Spawner
 * @author benjamin
 *
 */
public class ObjectSpawner {
	public enum Type {OS_FROG,OS_VENUSFT,OS_BIRD,OS_SPIDER}
	private float x,y,width,height,rotation;
	private Type type;
	public ObjectSpawner(float x, float y, float width, float height, float rotation, Type type){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.rotation = rotation;
		this.type = type;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public float getWidth() {
		return width;
	}
	public float getHeight() {
		return height;
	}
	public float getRotation() {
		return rotation;
	}
	public Type getType() {
		return type;
	}
}
