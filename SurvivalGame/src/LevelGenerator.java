

import java.util.Random;

import GameEngine.Game.ResourceLoader;
import GameEngine.GameTexture;

public class LevelGenerator {
	private EnemyStructure barracks;
	private ResourceLoader loader;
	private static EnemyObject soldier;
	private GameTexture mountains;
	private static int gridSize;
	
	public void generateLevel1(ResourceLoader l, GameTexture groundTexture,int grid){
		loader = l;
		gridSize = grid;
		
		//load textures
		GameTexture rockyTexture = loader.load("Textures/ground/ground_mud.jpg");
//		GameTexture waterTexture = loader.load("Textures/ground/ground_water.jpg");
//		GameTexture targetTexture = loader.load("Textures/ground/ground_target.jpg");
      
		//create towers and structures
//        GameTexture towerTexture = loader.load("Textures/structures/tower2.png");        
       
		//cannon  1
		EnemyStructure structure = new EnemyStructure((float)(gridSize*0.5*64) , (float)(gridSize*0.8*64), 150, 50,EnemyStructure.StructureType.ATTACK);
		for (int i = 0 ; i < 72 ; i++) {
			structure.addTexture(loader.load("Textures/structures/heavycannon/heavycannon_sm"+i+".png"), 16, 16);
		} 
		structure.setDamage(30);
		SurvivalGame.objects.add(structure);
		SurvivalGame.enemies.add(structure);
        //turret 1
        structure = new EnemyStructure((float)(gridSize*0.26*64) , (float)(gridSize*0.66*64), 100, 25,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/turret/turret_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(15);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure);
        //turret 2
        structure = new EnemyStructure((float)(gridSize*0.78*64) , (float)(gridSize*0.66*64), 100, 25,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/turret/turret_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(15);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure); 
        //anti-air 1
        structure = new EnemyStructure((float)(gridSize*0.29*64) , (float)(gridSize*0.8*64), 75, 18,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/anti_air/anti_air_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(9);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure);
        //anti-air 2
        structure = new EnemyStructure((float)(gridSize*0.75*64) , (float)(gridSize*0.8*64), 75, 18,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/anti_air/anti_air_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(9);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure);
        
        //barracks
        GameTexture crystalTowerTexture = loader.load("Textures/structures/barracks.png");  
        barracks = new EnemyStructure((float)((gridSize*64) /2) , (float)(gridSize*64 - 100), 250, 50,EnemyStructure.StructureType.PASSIVE);
        barracks.addTexture(crystalTowerTexture,16,16);
        SurvivalGame.objects.add(barracks);
        SurvivalGame.enemies.add(barracks);
        
        //walls TODO
//        GameTexture wallTexture = loader.load("Textures/walls/verticalWall.png");
//        WallObject wall;
//        //vertical walls
//        for(int i=0;i < 5;i++){
//        	 wall = new WallObject((float)(gridSize*0.26*64),(float)(gridSize*0.65*64 + 64*i),150,50);
//             wall.addTexture(wallTexture);
//             SurvivalGame.objects.add(barracks);
//        }
//        //horizontal walls
//        wallTexture = loader.load("Textures/walls/horizontalWall.png");
//        for(int i=0;i < 4;i++){
//       	 wall = new WallObject((float)(gridSize*0.26*64),(float)(gridSize*0.65*64 + 64*i),150,50);
//            wall.addTexture(wallTexture);
//            SurvivalGame.objects.add(barracks);
//       }

		// Creating and place enemies 
        //spawning object
        float x,y;
        x = (float) ((gridSize-10)*64);
        y = (float) ((gridSize-3)*64);            
        soldier = new EnemyObject(x, y, 50,50);        
        for (int i = 0 ; i < 72 ; i++) {
        	soldier.addTexture(loader.load("Textures/enemies/soldier/enemysoldier_sm"+i+".png"), 16, 16);
        }                
            
        //enemy 1
    	x = (float) (gridSize*64*0.64);
        y = (float) (gridSize*64 - 75);        
        spawnSoldier( x, y);
        //enemy2
        x = (float) (gridSize*64*0.21);
        y = (float) (gridSize*64*0.62);        
        spawnSoldier( x, y);
        //enemy3
        x = (float) (gridSize*64*0.80);
        y = (float) (gridSize*64*0.62);        
        spawnSoldier( x, y);
        //tank
        x = (float) (gridSize*64*0.37);
        y = (float) (gridSize*64 - 80);           
        EnemyObject tank  = new EnemyObject(x, y, 150,75);        
        for (int i = 0 ; i < 72 ; i++) {
        	tank.addTexture(loader.load("Textures/enemies/smalltank/smalltank_sm"+i+".png"), 16, 16);
        }
        tank.setDamage(25);
        tank.setCanExplode(true);
        tank.setEnemyType(EnemyObject.EnemyType.TANK);
        SurvivalGame.objects.add(tank);
        SurvivalGame.enemies.add(tank);        
        
        //generate mountains/rocks over map etc...
        mountains = loader.loadTexture("Textures/rock.png");   
        spawnRock((float)(gridSize*64*0.4),(float)(gridSize*64*0.5));
        spawnRock((float)(gridSize*64*0.15),(float)(gridSize*64*0.7));
        spawnRock((float)(gridSize*64*0.48),(float)(gridSize*64*0.43));
        spawnRock((float)(gridSize*64*0.80),(float)(gridSize*64*0.54));
        spawnRock((float)(gridSize*64*0.9),(float)(gridSize*64*0.80));
        spawnRock((float)(gridSize*64*0.85),(float)(gridSize*64*0.70));
        
        // creating the floor objects         
        SurvivalGame.gridTile = new GridGameObject[gridSize][gridSize];
        for (int i = 0 ; i < gridSize ; i++ ) {
        	for (int j = 0 ; j < gridSize ; j++ ) {
        		SurvivalGame.gridTile[i][j] = new GridGameObject(groundTexture.getWidth()*i,groundTexture.getHeight()*j,0,0);
//        		if((i == gridSize - 10) && (j == gridSize - 3)) {
//        			SurvivalGame.gridTile[i][j].addTexture(targetTexture, 0, 0);
//        		}
        		if((i > gridSize*0.2) && (i < gridSize*0.8) && (j > gridSize*0.62)) {
        			SurvivalGame.gridTile[i][j].addTexture(rockyTexture, 0, 0);
        		}        			
        		else{        			
        			SurvivalGame.gridTile[i][j].addTexture(groundTexture, 0, 0);
        		}
            }
        }    
        
	}//end level 1
	
	//mission 2
	public void generateLevel2(ResourceLoader l, GameTexture groundTexture,int grid){        
        loader = l;
		gridSize = grid;
		
		//load textures
		GameTexture rockyTexture = loader.load("Textures/ground/ground_mud.jpg");
//		GameTexture targetTexture = loader.load("Textures/ground/ground_target.jpg");        
		
		//cannon  1
		EnemyStructure structure = new EnemyStructure((float)(gridSize*0.73*64) , (float)(gridSize*0.9*64), 150, 50,EnemyStructure.StructureType.ATTACK);
		for (int i = 0 ; i < 72 ; i++) {
			structure.addTexture(loader.load("Textures/structures/heavycannon/heavycannon_sm"+i+".png"), 16, 16);
		} 
		structure.setDamage(30);
		SurvivalGame.objects.add(structure);
		SurvivalGame.enemies.add(structure);
        //turret 1
        structure = new EnemyStructure((float)(gridSize*0.41*64) , (float)(gridSize*0.66*64), 100, 25,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/turret/turret_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(15);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure);
        //turret 2
        structure = new EnemyStructure((float)(gridSize*0.95*64) , (float)(gridSize*0.66*64), 100, 25,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/turret/turret_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(15);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure); 
        //turret 3
        structure = new EnemyStructure((float)(gridSize*0.63*64) , (float)(gridSize*0.66*64), 100, 25,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/turret/turret_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(15);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure); 
        //anti-air 1
        structure = new EnemyStructure((float)(gridSize*0.90*64) , (float)(gridSize*0.76*64), 75, 18,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/anti_air/anti_air_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(9);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure);
        //anti-air 2
        structure = new EnemyStructure((float)(gridSize*0.24*64) , (float)(gridSize*0.9*64), 75, 18,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/anti_air/anti_air_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(9);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure);
        
        //barracks
        GameTexture crystalTowerTexture = loader.load("Textures/structures/barracks.png");  
        barracks = new EnemyStructure((float)(gridSize*64 - 100) , (float)(gridSize*64 - 100), 250, 50,EnemyStructure.StructureType.PASSIVE);
        barracks.addTexture(crystalTowerTexture,16,16);
        SurvivalGame.objects.add(barracks);
        SurvivalGame.enemies.add(barracks);
        
        //walls TODO
//        GameTexture wallTexture = loader.load("Textures/walls/verticalWall.png");
//        WallObject wall;
//        //vertical walls
//        for(int i=0;i < 5;i++){
//        	 wall = new WallObject((float)(gridSize*0.26*64),(float)(gridSize*0.65*64 + 64*i),150,50);
//             wall.addTexture(wallTexture);
//             SurvivalGame.objects.add(barracks);
//        }
//        //horizontal walls
//        wallTexture = loader.load("Textures/walls/horizontalWall.png");
//        for(int i=0;i < 4;i++){
//       	 wall = new WallObject((float)(gridSize*0.26*64),(float)(gridSize*0.65*64 + 64*i),150,50);
//            wall.addTexture(wallTexture);
//            SurvivalGame.objects.add(barracks);
//       }

		// Creating and place enemies 
        //spawning object
        float x,y;           
        soldier = new EnemyObject(0, 0, 50,50);        
        for (int i = 0 ; i < 72 ; i++) {
        	soldier.addTexture(loader.load("Textures/enemies/soldier/enemysoldier_sm"+i+".png"), 16, 16);
        }                
            
        //enemy 1
    	x = (float) (gridSize*64*0.95);
        y = (float) (gridSize*64*0.57);        
        spawnSoldier( x, y);
        //enemy2
        x = (float) (gridSize*64*0.79);
        y = (float) (gridSize*64*0.68);        
        spawnSoldier( x, y);
        //enemy3
        x = (float) (gridSize*64*0.85);
        y = (float) (gridSize*64*0.62);        
        spawnSoldier( x, y);
        //enemy 4
        x = (float) (gridSize*64*0.5);
        y = (float) (gridSize*64*0.64);        
        spawnSoldier( x, y);
        //enemy 5
        x = (float) (gridSize*64*0.32);
        y = (float) (gridSize*64*0.75);        
        spawnSoldier( x, y);
        //enemy 6
        x = (float) (gridSize*64*0.32);
        y = (float) (gridSize*64*0.79);        
        spawnSoldier( x, y);
        //enemy 7
        x = (float) (gridSize*64*0.1);
        y = (float) (gridSize*64*0.7);        
        spawnSoldier( x, y);
        //tank 1
        x = (float) (gridSize*64*0.42);
        y = (float) (gridSize*64 - 80);           
        EnemyObject tank  = new EnemyObject(x, y, 100,75);        
        for (int i = 0 ; i < 72 ; i++) {
        	tank.addTexture(loader.load("Textures/enemies/smalltank/smalltank_sm"+i+".png"), 16, 16);
        }
        tank.setDamage(25);
        tank.setCanExplode(true);
        tank.setEnemyType(EnemyObject.EnemyType.TANK);
        SurvivalGame.objects.add(tank);
        SurvivalGame.enemies.add(tank);  
        //tank 2
        x = (float) (gridSize*64*0.48);
        y = (float) (gridSize*64 - 80);           
        tank  = new EnemyObject(x, y, 150,75);        
        for (int i = 0 ; i < 72 ; i++) {
        	tank.addTexture(loader.load("Textures/enemies/shermantank/shermantank_sm"+i+".png"), 16, 16);
        }
        tank.setDamage(25);
        tank.setCanExplode(true);
        tank.setEnemyType(EnemyObject.EnemyType.TANK);
        SurvivalGame.objects.add(tank);
        SurvivalGame.enemies.add(tank);  
        //tank 2
        x = (float) (gridSize*64*0.54);
        y = (float) (gridSize*64 - 80);           
        tank  = new EnemyObject(x, y, 150,75);        
        for (int i = 0 ; i < 72 ; i++) {
        	tank.addTexture(loader.load("Textures/enemies/shermantank/shermantank_sm"+i+".png"), 16, 16);
        }
        tank.setDamage(25);
        tank.setCanExplode(true);
        tank.setEnemyType(EnemyObject.EnemyType.TANK);
        SurvivalGame.objects.add(tank);
        SurvivalGame.enemies.add(tank);  
        //helicopter          
        EnemyObject heli  = new EnemyObject((float) (gridSize*64*0.85), (float) (gridSize*64*0.86), 200,80);        
        for (int i = 0 ; i < 72 ; i++) {
        	heli.addTexture(loader.load("Textures/enemies/helicopter/helicopter_sm0"+i+".png"), 16, 16);
        }
        heli.setDamage(35);
        heli.setSpeed(3);
        heli.setCanExplode(true);
        heli.setEnemyType(EnemyObject.EnemyType.AIR);
        SurvivalGame.objects.add(heli);
        SurvivalGame.enemies.add(heli); 
        
        //generate mountains/rocks over map etc...
        mountains = loader.loadTexture("Textures/rock.png");   
        spawnRock((float)(gridSize*64*0.16),(float)(gridSize*64*0.83));
        spawnRock((float)(gridSize*64*0.28),(float)(gridSize*64*0.61));
        spawnRock((float)(gridSize*64*0.1),(float)(gridSize*64*0.59));
        spawnRock((float)(gridSize*64*0.15),(float)(gridSize*64*0.52));
        spawnRock((float)(gridSize*64*0.4),(float)(gridSize*64*0.48));
        spawnRock((float)(gridSize*64*0.76),(float)(gridSize*64*0.6));
        spawnRock((float)(gridSize*64*0.9),(float)(gridSize*64*0.58));
        
        // creating the floor objects         
        SurvivalGame.gridTile = new GridGameObject[gridSize][gridSize];
        for (int i = 0 ; i < gridSize ; i++ ) {
        	for (int j = 0 ; j < gridSize ; j++ ) {
        		SurvivalGame.gridTile[i][j] = new GridGameObject(groundTexture.getWidth()*i,groundTexture.getHeight()*j,0,0);
        		if((i > gridSize*0.35) && (j > gridSize*0.62)) {
        			SurvivalGame.gridTile[i][j].addTexture(rockyTexture, 0, 0);
        		}        			
        		else{        			
        			SurvivalGame.gridTile[i][j].addTexture(groundTexture, 0, 0);
        		}
            }
        }       
        
	}//end level 2
	
	//level 3
	public void generateLevel3(ResourceLoader l, GameTexture groundTexture,int grid){
		loader = l;
		gridSize = grid;
		
		//load textures
		GameTexture rockyTexture = loader.load("Textures/ground/ground_mud.jpg");
//		GameTexture targetTexture = loader.load("Textures/ground/ground_target.jpg");        
		
		//cannon  1
		EnemyStructure structure = new EnemyStructure((float)(gridSize*0.97*64) , (float)(gridSize*0.69*64), 150, 50,EnemyStructure.StructureType.ATTACK);
		for (int i = 0 ; i < 72 ; i++) {
			structure.addTexture(loader.load("Textures/structures/heavycannon/heavycannon_sm"+i+".png"), 16, 16);
		} 
		structure.setDamage(30);
		SurvivalGame.objects.add(structure);
		SurvivalGame.enemies.add(structure);
        //cannon 2
        structure = new EnemyStructure((float)(gridSize*0.78*64) , (float)(gridSize*0.95*64), 150, 50,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/heavycannon/heavycannon_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(30);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure);
        //turret 1
        structure = new EnemyStructure((float)(gridSize*0.80*64) , (float)(gridSize*0.66*64), 100, 25,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/turret/turret_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(15);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure); 
        //turret 2
        structure = new EnemyStructure((float)(gridSize*0.15*64) , (float)(gridSize*0.56*64), 100, 25,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/turret/turret_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(15);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure); 
        //turret 3
        structure = new EnemyStructure((float)(gridSize*0.67*64) , (float)(gridSize*0.79*64), 100, 25,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/turret/turret_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(15);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure); 
        //anti-air 1
        structure = new EnemyStructure((float)(gridSize*0.95*64) , (float)(gridSize*0.95*64), 75, 18,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/anti_air/anti_air_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(9);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure);
        //anti-air 2
        structure = new EnemyStructure((float)(gridSize*0.12*64) , (float)(gridSize*0.89*64), 75, 18,EnemyStructure.StructureType.ATTACK);
        for (int i = 0 ; i < 72 ; i++) {
        	structure.addTexture(loader.load("Textures/structures/anti_air/anti_air_sm"+i+".png"), 16, 16);
        }         
        structure.setDamage(9);
        SurvivalGame.objects.add(structure);
        SurvivalGame.enemies.add(structure);
        
        //barracks
        GameTexture crystalTowerTexture = loader.load("Textures/structures/barracks.png");  
        barracks = new EnemyStructure((float)(gridSize*64*0.90) , (float)(gridSize*64 - 70), 250, 50,EnemyStructure.StructureType.PASSIVE);
        barracks.addTexture(crystalTowerTexture,16,16);
        SurvivalGame.objects.add(barracks);
        SurvivalGame.enemies.add(barracks);
        
        //walls TODO
//        GameTexture wallTexture = loader.load("Textures/walls/verticalWall.png");
//        WallObject wall;
//        //vertical walls
//        for(int i=0;i < 5;i++){
//        	 wall = new WallObject((float)(gridSize*0.26*64),(float)(gridSize*0.65*64 + 64*i),150,50);
//             wall.addTexture(wallTexture);
//             SurvivalGame.objects.add(barracks);
//        }
//        //horizontal walls
//        wallTexture = loader.load("Textures/walls/horizontalWall.png");
//        for(int i=0;i < 4;i++){
//       	 wall = new WallObject((float)(gridSize*0.26*64),(float)(gridSize*0.65*64 + 64*i),150,50);
//            wall.addTexture(wallTexture);
//            SurvivalGame.objects.add(barracks);
//       }

		// Creating and place enemies 
        //spawning object
        float x,y;           
        soldier = new EnemyObject(0, 0, 50,50);        
        for (int i = 0 ; i < 72 ; i++) {
        	soldier.addTexture(loader.load("Textures/enemies/soldier/enemysoldier_sm"+i+".png"), 16, 16);
        }                
            
        //enemy 1
    	x = (float) (gridSize*64*0.1);
        y = (float) (gridSize*64*0.55);        
        spawnSoldier( x, y);
        //enemy2
        x = (float) (gridSize*64*0.18);
        y = (float) (gridSize*64*0.54);        
        spawnSoldier( x, y);
        //enemy3
        x = (float) (gridSize*64*0.36);
        y = (float) (gridSize*64*0.73);        
        spawnSoldier( x, y);
        //enemy 4
        x = (float) (gridSize*64*0.83);
        y = (float) (gridSize*64*0.64);        
        spawnSoldier( x, y);
        //enemy 5
        x = (float) (gridSize*64*0.88);
        y = (float) (gridSize*64*0.64);        
        spawnSoldier( x, y);
        //enemy 6
        x = (float) (gridSize*64*0.77);
        y = (float) (gridSize*64*0.83);        
        spawnSoldier( x, y);
        //enemy 7
        x = (float) (gridSize*64*0.4);
        y = (float) (gridSize*64*0.76);        
        spawnSoldier( x, y);
        //tank 1
        x = (float) (gridSize*64*0.2);
        y = (float) (gridSize*64*0.57);           
        EnemyObject tank  = new EnemyObject(x, y, 100,75);        
        for (int i = 0 ; i < 72 ; i++) {
        	tank.addTexture(loader.load("Textures/enemies/smalltank/smalltank_sm"+i+".png"), 16, 16);
        }
        tank.setDamage(25);
        tank.setCanExplode(true);
        tank.setEnemyType(EnemyObject.EnemyType.TANK);
        SurvivalGame.objects.add(tank);
        SurvivalGame.enemies.add(tank);  
        //tank 2
        x = (float) (gridSize*64*0.87);
        y = (float) (gridSize*64*0.71);           
        tank  = new EnemyObject(x, y, 150,75);        
        for (int i = 0 ; i < 72 ; i++) {
        	tank.addTexture(loader.load("Textures/enemies/shermantank/shermantank_sm"+i+".png"), 16, 16);
        }
        tank.setDamage(25);
        tank.setCanExplode(true);
        tank.setEnemyType(EnemyObject.EnemyType.TANK);
        SurvivalGame.objects.add(tank);
        SurvivalGame.enemies.add(tank);  
        //tank 3
        x = (float) (gridSize*64*0.80);
        y = (float) (gridSize*64*0.95);           
        tank  = new EnemyObject(x, y, 150,75);        
        for (int i = 0 ; i < 72 ; i++) {
        	tank.addTexture(loader.load("Textures/enemies/shermantank/shermantank_sm"+i+".png"), 16, 16);
        }
        tank.setDamage(25);
        tank.setCanExplode(true);
        tank.setEnemyType(EnemyObject.EnemyType.TANK);
        SurvivalGame.objects.add(tank);
        SurvivalGame.enemies.add(tank);  
        //tank 4
        x = (float) (gridSize*64*0.6);
        y = (float) (gridSize*64*0.7);           
        tank  = new EnemyObject(x, y, 150,75);        
        for (int i = 0 ; i < 72 ; i++) {
        	tank.addTexture(loader.load("Textures/enemies/shermantank/shermantank_sm"+i+".png"), 16, 16);
        }
        tank.setDamage(25);
        tank.setCanExplode(true);
        tank.setEnemyType(EnemyObject.EnemyType.TANK);
        SurvivalGame.objects.add(tank);
        SurvivalGame.enemies.add(tank); 
        //tank 5
        x = (float) (gridSize*64*0.5);
        y = (float) (gridSize*64*0.67);           
        tank  = new EnemyObject(x, y, 150,75);        
        for (int i = 0 ; i < 72 ; i++) {
        	tank.addTexture(loader.load("Textures/enemies/smalltank/smalltank_sm"+i+".png"), 16, 16);
        }
        tank.setDamage(25);
        tank.setCanExplode(true);
        tank.setEnemyType(EnemyObject.EnemyType.TANK);
        SurvivalGame.objects.add(tank);
        SurvivalGame.enemies.add(tank); 
        //tank 6
        x = (float) (gridSize*64*0.4);
        y = (float) (gridSize*64*0.78);           
        tank  = new EnemyObject(x, y, 150,75);        
        for (int i = 0 ; i < 72 ; i++) {
        	tank.addTexture(loader.load("Textures/enemies/shermantank/shermantank_sm"+i+".png"), 16, 16);
        }
        tank.setDamage(25);
        tank.setCanExplode(true);
        tank.setEnemyType(EnemyObject.EnemyType.TANK);
        SurvivalGame.objects.add(tank);
        SurvivalGame.enemies.add(tank); 
        //helicopter 1        
        EnemyObject heli  = new EnemyObject((float) (gridSize*64*0.1), (float) (gridSize*64*0.95), 200,80);        
        for (int i = 0 ; i < 72 ; i++) {
        	heli.addTexture(loader.load("Textures/enemies/helicopter/helicopter_sm0"+i+".png"), 16, 16);
        }
        heli.setDamage(35);
        heli.setSpeed(3);
        heli.setCanExplode(true);
        heli.setEnemyType(EnemyObject.EnemyType.AIR);
        SurvivalGame.objects.add(heli);
        SurvivalGame.enemies.add(heli); 
        //helicopter 2      
        heli  = new EnemyObject((float) (gridSize*64*0.25), (float) (gridSize*64*0.95), 200,80);        
        for (int i = 0 ; i < 72 ; i++) {
        	heli.addTexture(loader.load("Textures/enemies/helicopter/helicopter_sm0"+i+".png"), 16, 16);
        }
        heli.setDamage(35);
        heli.setSpeed(3);
        heli.setCanExplode(true);
        heli.setEnemyType(EnemyObject.EnemyType.AIR);
        SurvivalGame.objects.add(heli);
        SurvivalGame.enemies.add(heli); 
        //helicopter 3         
        heli  = new EnemyObject((float) (gridSize*64*0.35), (float) (gridSize*64*0.95), 200,80);        
        for (int i = 0 ; i < 72 ; i++) {
        	heli.addTexture(loader.load("Textures/enemies/helicopter/helicopter_sm0"+i+".png"), 16, 16);
        }
        heli.setDamage(35);
        heli.setSpeed(3);
        heli.setCanExplode(true);
        heli.setEnemyType(EnemyObject.EnemyType.AIR);
        SurvivalGame.objects.add(heli);
        SurvivalGame.enemies.add(heli); 
        //helicopter  4        
        heli  = new EnemyObject((float) (gridSize*64*0.55), (float) (gridSize*64*0.95), 200,80);        
        for (int i = 0 ; i < 72 ; i++) {
        	heli.addTexture(loader.load("Textures/enemies/helicopter/helicopter_sm0"+i+".png"), 16, 16);
        }
        heli.setDamage(35);
        heli.setSpeed(3);
        heli.setCanExplode(true);
        heli.setEnemyType(EnemyObject.EnemyType.AIR);
        SurvivalGame.objects.add(heli);
        SurvivalGame.enemies.add(heli); 
        //helicopter 5          
        heli  = new EnemyObject((float) (gridSize*64*0.65), (float) (gridSize*64*0.95), 200,80);        
        for (int i = 0 ; i < 72 ; i++) {
        	heli.addTexture(loader.load("Textures/enemies/helicopter/helicopter_sm0"+i+".png"), 16, 16);
        }
        heli.setDamage(35);
        heli.setSpeed(3);
        heli.setCanExplode(true);
        heli.setEnemyType(EnemyObject.EnemyType.AIR);
        SurvivalGame.objects.add(heli);
        SurvivalGame.enemies.add(heli); 
        
        //generate mountains/rocks over map etc...
        mountains = loader.loadTexture("Textures/rock.png");   
        spawnRock((float)(gridSize*64*0.1),(float)(gridSize*64*0.38));
        spawnRock((float)(gridSize*64*0.2),(float)(gridSize*64*0.43));
        spawnRock((float)(gridSize*64*0.35),(float)(gridSize*64*0.57));
        spawnRock((float)(gridSize*64*0.45),(float)(gridSize*64*0.52));
        spawnRock((float)(gridSize*64*0.7),(float)(gridSize*64*0.5));
        spawnRock((float)(gridSize*64*0.76),(float)(gridSize*64*0.6));
        spawnRock((float)(gridSize*64*0.95),(float)(gridSize*64*0.61));
        
        //mountain range
        for(int n=0;n < 21;n++){
        	spawnRock((float)(64*n),(float)(gridSize*64*0.86));
        }
        
        // creating the floor objects         
        SurvivalGame.gridTile = new GridGameObject[gridSize][gridSize];
        for (int i = 0 ; i < gridSize ; i++ ) {
        	for (int j = 0 ; j < gridSize ; j++ ) {
        		SurvivalGame.gridTile[i][j] = new GridGameObject(groundTexture.getWidth()*i,groundTexture.getHeight()*j,0,0);
        		if(j > 25) {
        			SurvivalGame.gridTile[i][j].addTexture(rockyTexture, 0, 0);
        		}   
        		else if((j < 27) && (j > 20) && (i > 20)){
        			SurvivalGame.gridTile[i][j].addTexture(rockyTexture, 0, 0);
        		}
        		else{        			
        			SurvivalGame.gridTile[i][j].addTexture(groundTexture, 0, 0);
        		}
            }
        }
	}//end level 3
	
	//spawns rocks on map
	private void spawnRock(float x, float y){
		WallObject mountainObj = new WallObject(x, y, 0, 0);
        mountainObj.addTexture(mountains);
        SurvivalGame.objects.add(mountainObj);
	}
	
	//generate troops from point provided
	public static void spawnSoldier(float x,float y){
		//to randomise spawning location
		Random randomNum = new Random();		
		int randomizelocation = randomNum.nextInt(10);
		if(Math.random() < 0.5)
			randomizelocation *= -1;
		
		EnemyObject temp = new EnemyObject(x + randomizelocation, y + randomizelocation, 50, 50);
		
		for(int i=0;i < 72;i++){
			temp.addTexture(soldier.getCurrentTexture());
			soldier.setActiveTexture(i);
		}
		
		temp.setEnemyType(EnemyObject.EnemyType.GROUND);
		SurvivalGame.objects.add(temp);
        SurvivalGame.enemies.add(temp);
	}
}
