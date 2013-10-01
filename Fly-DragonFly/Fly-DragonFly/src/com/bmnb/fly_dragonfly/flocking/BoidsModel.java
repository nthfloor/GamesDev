package com.bmnb.fly_dragonfly.flocking;
import java.util.Vector;

import com.badlogic.gdx.math.Vector2;
import com.bmnb.fly_dragonfly.objects.Frog;
import com.bmnb.fly_dragonfly.objects.GameObject;
import com.bmnb.fly_dragonfly.screens.GameScreen;

/**
 * Flocking Model (Boids) with fleeing behavior
 * @author benjamin
 * 
 * Acknowledgments: 
 * I've builded this more advanced flocking model from pointers provided in the following blog:
 * Harry Brundage. Neat Algorithms - Flocking. Available at [http://harry.me/2011/02/17/neat-algorithms---flocking/]
 */
public class BoidsModel {
	private static final float COHERE_RADIUS = 150;
	private static final float ALIGN_RADIUS = 140;
	private static final float SEPARATE_RADIUS = 60;
	private static final float SEPARATION_SCALE = 1800f;
	private static final float COHERE_SCALE = 1f;
	private static final float ALIGN_SCALE = 4.0f;	
	private static final float DRAW_TO_BOTTOM_SCALE = 0.002f;
	private static final float FLEE_SCALE = 0.63f;
	private static final float DESTRUCTION_BUFFER = 150;
	private static final float CREATION_BUFFER = 250;
	public enum BoidsType {Mosquitoes,FireFlies}
	public static final int MAX_BOIDS = 50;
	private Vector<Boid> elements;
	/**
	 * Method to compute cohesion for each element (based on the cohesion radius)
	 * The method works based on the mean of the position within radius.
	 */
	private void cohere(){
		for(int i = 0; i < elements.size() - 1; ++i){
			Boid b1 = elements.get(i);
			Vector2 meanPos = new Vector2();
			int countInRadius = 0;
			for (int j = i + 1; j < elements.size(); ++j){
				Boid b2 = elements.get(j);
				double dist = distSq(b1.getPosition(),b2.getPosition());
				if (dist < COHERE_RADIUS * COHERE_RADIUS){
					meanPos.add(b2.getPosition());
					countInRadius++;
				}
			}
			if (countInRadius > 0){
				meanPos.div(countInRadius);
				b1.setVelocity(b1.getVelocity().add(meanPos.sub(b1.getPosition()).nor().mul(COHERE_SCALE)));
			}
		}
	}
	/**
	 * Method to compute alignment for each element (based on the velocity, and thus direction, of neighbouring boids).
	 * These calculations are based on the alignment radius.
	 */
	private void align(){
		for(int i = 0; i < elements.size() - 1; ++i){
			Boid b1 = elements.get(i);
			Vector2 meanVel = new Vector2();
			int countInRadius = 0;
			for (int j = i + 1; j < elements.size(); ++j){
				Boid b2 = elements.get(j);
				double dist = distSq(b1.getPosition(),b2.getPosition());
				if (dist < ALIGN_RADIUS * ALIGN_RADIUS){
					meanVel.add(b2.getVelocity());
					countInRadius++;
				}
			}
			if (countInRadius > 0){
				b1.setVelocity(b1.getVelocity().add(meanVel.div(countInRadius).nor().mul(ALIGN_SCALE)));
			}
		}
	}
	/**
	 * Method to compute separation of boids that are very close together (think of repulsion).
	 * The calculations are based on the separation radius (simply a matter of adding a mean distance to the velocity)
	 * 
	 */
	private void separate(){
		for(int i = 0; i < elements.size() - 1; ++i){
			Boid b1 = elements.get(i);
			Vector2 meanVel = new Vector2();
			int countInRadius = 0;
			for (int j = i + 1; j < elements.size(); ++j){
				Boid b2 = elements.get(j);
				double dist = distSq(b1.getPosition(),b2.getPosition());
				if (dist < SEPARATE_RADIUS * SEPARATE_RADIUS){
					meanVel.add(b1.getPosition().cpy().sub(b2.getPosition()).nor()
							.div((float)Math.sqrt(dist+0.000000000001f)));
					countInRadius++;
				}
			}
			if (countInRadius > 0){
				b1.setVelocity(b1.getVelocity().add(meanVel.div(countInRadius).mul(SEPARATION_SCALE)));
			}
		}
	}
	/**
	 * The boids ultimately need to go down the screen, so make them chase their projections onto the x-axis
	 * @param delta
	 */
	private void drawToBottom(){
		for (Boid b: elements){
			b.setVelocity(b.getVelocity().add((new Vector2(b.getPosition().x,0)).sub(b.getPosition()).mul(DRAW_TO_BOTTOM_SCALE)));
		}
	}
	/**
	 * Method to make boids flee if they are in a specified radius from specified position
	 * @param pos
	 * @param radius
	 */
	private void fleeFromObject(Vector2 pos, float radSq){
		for (Boid b: elements){
			double dist = distSq(b.getPosition(),pos);
			if (dist < radSq)
				b.setVelocity(b.getVelocity().sub(pos.cpy().sub(b.getPosition()).mul(FLEE_SCALE)));
		}
	}
	
	/**
	 * Method to make the boids flee from all enemies on the game screen
	 * @param gs
	 */
	private void flee(GameScreen gs){
		for (GameObject o: GameScreen.getEnemies()){
			if (! (o.isDead() && o.isRemovable()))
				if (! (o instanceof Frog))
					fleeFromObject(new Vector2(o.getX(),o.getY()),new Vector2(o.getWidth()/2,o.getHeight()/2).len2());
		}
		fleeFromObject(gs.getPlayer().getPosition(),
				new Vector2(gs.getPlayer().getWidth(),gs.getPlayer().getHeight()).len2());
	}
	
	/**
	 * Remove the boids that are off-screen
	 */
	private void removeBoidsOutsideBounds(){
		for(int i = 0; i < elements.size(); ++i){
			Boid b = elements.get(i);
			if (b.getPosition().x < -DESTRUCTION_BUFFER ||
				b.getPosition().y < -DESTRUCTION_BUFFER ||
				b.getPosition().x > GameScreen.width+DESTRUCTION_BUFFER ||
				b.getPosition().y > GameScreen.height+DESTRUCTION_BUFFER+CREATION_BUFFER){
					elements.remove(i--);
					b.kill();
			}
		}
	}
	/**
	 * Method to add boids to the system
	 * 
	 */
	public void spawnBoids(float widthPerBoid,float heightPerBoid,float scWidth,float scHeight,
			int numBoids,float spawnOrdinate, float spawnDeviation, BoidsType type){
		for (int i = 0; i < numBoids && elements.size() < MAX_BOIDS; ++i){
			Vector2 bPos = new Vector2(spawnOrdinate + (float)Math.random()*spawnDeviation -
					(float)Math.random()*spawnDeviation,scHeight+(float)Math.random()*(0.7f*CREATION_BUFFER)+0.3f*CREATION_BUFFER);
			Boid b = type == BoidsType.Mosquitoes ? new Mosquito(bPos,new Vector2(0,-1),widthPerBoid,heightPerBoid,scWidth,scHeight) :
				new FireFly(bPos,new Vector2(0,-1),widthPerBoid,heightPerBoid,scWidth,scHeight);
			elements.add(b);
			GameScreen.addObject(b);
		}
	}
	/**
	 * Initializer. Can init boids uniformly or starting in a circle in the middle of the screen
	 */
	public BoidsModel(){
		elements = new Vector<Boid>();
	}
	/**
	 * Update method. Invoke this method to update boid positions.
	 * @param delta
	 */
	public void update(float delta, GameScreen gs){
		//boid ops
		cohere();
		align();
		separate();
		drawToBottom();
		flee(gs);
		//Now bound Boids to the viewport
		removeBoidsOutsideBounds();
	}
	/**
	 * Returns the squared distance between POINTS in homogeneous coords
	 * TODO: refactor: put in external class
	 * @param p1 First point
	 * @param p2 Second point
	 * @return Square of euclidian distance between the points
	 */
	public static double distSq(Vector2 p1, Vector2 p2){
		return (p1.x - p2.x)*(p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y);
	}
}
