package com.bmnb.fly_dragonfly;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Fly-DragonFly";
		cfg.useGL20 = true;
		cfg.width = 300;
		cfg.height = 492;
		cfg.resizable = false;
		
		new LwjglApplication(new Fly_DragonFly(), cfg);
	}
}
