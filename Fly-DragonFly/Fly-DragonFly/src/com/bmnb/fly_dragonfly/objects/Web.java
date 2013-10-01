package com.bmnb.fly_dragonfly.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.bmnb.fly_dragonfly.screens.GameScreen;
import com.bmnb.fly_dragonfly.tools.SpriteAnimator;

public class Web extends StaticEnemy {
	private static final float WIDTH_EXPANSION_SPEED = 180;
	private static final float HEIGHT_EXPANSION_SPEED = 80;
	private static final float MAX_HEIGHT = 100;
	private int direction = 1;
	private float y;
	private static Texture deathTex = null;

	public Web(Vector2 position, float width, float height, float speed,
			float scWidth, float scHeight, Player player) {
		super(position, width, height, speed, scWidth, scHeight, player, 10);

		sortVal = 3;

		y = position.y;

		direction = (int) (Math.signum(player.getX() - this.getX()) * direction);
		setTexture(new Texture("data/textures/net_shoot.png"));
		if (deathTex == null)
			deathTex = new Texture("data/textures/net-death.png");
	}

	@Override
	public void kill() {
		super.kill();
	}

	@Override
	public void update(float delta) {
		super.update(delta);
		if (!dead) {
			y -= delta * speed;
			this.setSize(
					this.getWidth() < GameScreen.width ? this.getWidth()
							+ direction * WIDTH_EXPANSION_SPEED * delta : this
							.getWidth(),
					this.getHeight() < MAX_HEIGHT ? this.getHeight()
							+ HEIGHT_EXPANSION_SPEED * delta : this.getHeight());
			this.setY(y - this.getHeight() / 2);
		}
		else {
			if (animator == null)
				animator = new SpriteAnimator(deathTex, 12, 1, "web", 24, this, true);
		}
	}

}
