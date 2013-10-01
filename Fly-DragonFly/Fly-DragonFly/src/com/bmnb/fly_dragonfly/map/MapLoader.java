package com.bmnb.fly_dragonfly.map;


import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.bmnb.fly_dragonfly.map.MoziSpawner.SpawnerType;
import com.bmnb.fly_dragonfly.map.ObjectSpawner.Type;
/**
 * Map loader class that parses Fly-Dragonfly XML maps
 * @author benjamin
 */
public class MapLoader {
	XmlReader rdr;
	ArrayList<ObjectSpawner> gameObjects;
	ArrayList<MoziSpawner> spawners;
	ArrayList<TutorialScreenSpawner> tutorialScreens;
	/**
	 * Constructor for Map Loader. Parses the XML map file
	 * @param filename path to the map to load
	 * @throws Exception
	 */
	public MapLoader(String filename) throws Exception{
		gameObjects = new ArrayList<ObjectSpawner>();
		spawners = new ArrayList<MoziSpawner>();
		tutorialScreens = new ArrayList<TutorialScreenSpawner>();
		rdr = new XmlReader();
		FileHandle fh = Gdx.files.internal(filename);
		Element node = null;
		try {
			node = rdr.parse(fh);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		assert(node != null);
		//--------------------------------------------------------------
		//Load Objects
		//--------------------------------------------------------------
		for (Element child: node.getChildrenByName("obj")){
			String type = child.getAttribute("type");
			Element subChild = null;
			subChild = child.getChildByName("x");
			assert(subChild != null);
			float x = Float.parseFloat(subChild.getText());
			subChild = child.getChildByName("y");
			assert(subChild != null);
			float y = Float.parseFloat(subChild.getText());
			subChild = child.getChildByName("width");
			assert(subChild != null);
			float width = Float.parseFloat(subChild.getText());
			subChild = child.getChildByName("height");
			assert(subChild != null);
			float height = Float.parseFloat(subChild.getText());
			subChild = child.getChildByName("rotation");
			assert(subChild != null);
			float rotation = Float.parseFloat(subChild.getText());
			//Gdx.app.log("ML:", type + " " + x + " " + y + " " + width + " " + height + " " + rotation);
			if (type.equals("frog")) gameObjects.add(new ObjectSpawner(x,y,width,height,rotation,Type.OS_FROG));
			else if (type.equals("spider")) gameObjects.add(new ObjectSpawner(x,y,width,height,rotation,Type.OS_SPIDER));
			else if (type.equals("flytrap")) gameObjects.add(new ObjectSpawner(x,y,width,height,rotation,Type.OS_VENUSFT));
			else if (type.equals("bird")) gameObjects.add(new ObjectSpawner(x,y,width,height,rotation,Type.OS_BIRD));
			else throw new Exception("Unknown game object element in map. Check types.");
		}
		//--------------------------------------------------------------
		//Load flock spawners
		//--------------------------------------------------------------
		for (Element child: node.getChildrenByName("moziSpawner")){
			String type = child.getAttribute("type");
			
			Element subChild = null;
			subChild = child.getChildByName("x");
			assert(subChild != null);
			float x = Float.parseFloat(subChild.getText());
			subChild = child.getChildByName("y");
			assert(subChild != null);
			float y = Float.parseFloat(subChild.getText());
			subChild = child.getChildByName("numberOfBoids");
			assert(subChild != null);
			int numBoids = Integer.parseInt(subChild.getText());
			subChild = child.getChildByName("dispersion");
			assert(subChild != null);
			float deviation = Float.parseFloat(subChild.getText());
			//Gdx.app.log("ML:", type + " " + x + " " + y + " " + numBoids + " " + deviation);
			
			if (type.equals("mosquitoes")){
				spawners.add(new MoziSpawner(new Vector2(x,y),deviation,numBoids,SpawnerType.mosquitoes));	
			} else if (type.equals("fireflies")){
				spawners.add(new MoziSpawner(new Vector2(x,y),deviation,numBoids,SpawnerType.fireflies));
			} else throw new Exception("Unknown spawner element in map. Check types.");
		}
		//--------------------------------------------------------------
		//Load tutorial screen spawners
		//--------------------------------------------------------------
		for (Element child: node.getChildrenByName("tutorialScreen")){
			Element subChild = null;
			subChild = child.getChildByName("id");
			assert(subChild != null);
			int id = Integer.parseInt(subChild.getText());
			subChild = child.getChildByName("y");
			assert(subChild != null);
			float y = Float.parseFloat(subChild.getText());
			tutorialScreens.add(new TutorialScreenSpawner(id, y));
		}
	}
	//--------------------------------------------------------------
	//Accessors for the three lists of stored objects
	//--------------------------------------------------------------
	public ArrayList<ObjectSpawner> getGameObjects() {
		return gameObjects;
	}
	public ArrayList<MoziSpawner> getSpawners() {
		return spawners;
	}
	public ArrayList<TutorialScreenSpawner> getTutorialScreens() {
		return tutorialScreens;
	}
}
