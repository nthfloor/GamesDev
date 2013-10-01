package com.bmnb.fly_dragonfly.tools;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.bmnb.fly_dragonfly.objects.GameObject;

public class SpriteAnimator {
	protected TextureAtlas atlas;
	protected String caller;
	protected int currentTexture, numTextures;
	protected float countDownTime, timer;
	protected GameObject parent;
	protected boolean kill = false;
	
	public SpriteAnimator(TextureAtlas atlas, String caller,
			int framesPerSecond, GameObject parent) {
		init(atlas, caller, framesPerSecond, parent);
	}

	public SpriteAnimator(Texture tex, int numTextures, int numInRow,
			String caller, int framesPerSecond, GameObject parent, boolean kill) {
		this(tex, numTextures, numInRow, caller, framesPerSecond, parent);
		this.kill = kill;
	}
	
	public SpriteAnimator(Texture tex, int numTextures, int numInRow,
			String caller, int framesPerSecond, GameObject parent) {
		atlas = new TextureAtlas();

		int width = (int) (tex.getWidth() / (float) numInRow);
		int rows = (int) Math.ceil(numTextures / (float) numInRow);
		int height = (int) (tex.getHeight() / (float) rows);

		int x, y;

		for (int r = 0; r < rows; ++r) {
			y = r * height;
			for (int c = 0; c < numInRow; ++c) {
				x = c * width;
				atlas.addRegion(caller + (c + (r * numInRow) + 1), tex, x, y,
						width, height);
			}
		}
		init(atlas, caller, framesPerSecond, parent);
	}

	private void init(TextureAtlas atlas, String caller, int framesPerSecond,
			GameObject parent) {
		this.atlas = atlas;
		this.caller = caller;
		this.parent = parent;

		currentTexture = 1;
		numTextures = atlas.getRegions().size;

		countDownTime = 1f / (float) framesPerSecond;
		timer = countDownTime;

		TextureRegion r = atlas.findRegion(caller + 1);
		parent.setRegion(r);
	}

	public void update(float delta) {
		timer -= delta;
		if (timer <= 0) {
			currentTexture++;
			if (kill)
				if (currentTexture > numTextures)
					parent.setRemovable();
			currentTexture = currentTexture > numTextures ? 1 : currentTexture;
			
			TextureRegion r = atlas.findRegion(caller + (currentTexture));
			parent.setRegion(r);

			timer = countDownTime;
		}
	}
}
