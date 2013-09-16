//this class keeps record and manges all map activity
//Nathan Floor
//FLRNAT001

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework;

namespace WindowsGame1
{
    class GameMap
    {
        //keep a 2D representation of the map
        public static int[,] mapPathData; //an integer representation for the Astar path finding algorithm
        public static GameObject[,] gameGrid;
        private int gridHeight;
        private int gridWidth;
        private int numberOfPointBlocks;

        public static List<String> tempscores;

        //grid cell data
        private Vector2 gridDimensions;

        //random number for random piller placement
        private Random randomize;

        //solid, indistructable objects
        public static List<StationaryObject> destructables;
        private List<StationaryObject> indestructables;
        private Texture2D destructableTexture;
        private Texture2D indestructableTexture;

        //default constructor
        public GameMap(int width,int height,int gWidth,int gHeight,Vector2 dimensions)
        {
            gridHeight = gHeight;
            gridWidth = gWidth;
            gridDimensions = dimensions;
            destructables = new List<StationaryObject>();
            indestructables = new List<StationaryObject>();
            randomize = new Random();
            numberOfPointBlocks = 0;
            tempscores = new List<String>();
     
            //initisalise integer representation of map
            mapPathData = new int[gridHeight, gridWidth];
            for (int x = 0; x < gridWidth; x++)
                for (int y = 0; y < gridHeight; y++)
                    mapPathData[y, x] = 1;

            //initisalise object representation of map
            gameGrid = new GameObject[gridHeight, gridWidth];
            for (int x = 0; x < gridWidth; x++)
                for (int y = 0; y < gridHeight; y++)
                    gameGrid[y, x] = null;
        }

        //setup map 
        public void initialiseMap(Texture2D desTex, Texture2D indesTex)
        {
            //load textures 
            destructableTexture = desTex;
            indestructableTexture = indesTex;

            //generate destructable objects on map
            for (int x = 0; x < gridWidth; x++)
            {
                for (int y = 0; y < gridHeight; y++)
                {
                    if (x != 0 && y != 0)
                    {
                        if (x != gridWidth && y != gridHeight/2)
                        {
                            StationaryObject temporaryObject = new StationaryObject(destructableTexture, 1, 1, (int)(x * gridDimensions.X), 
                                (int)(y * gridDimensions.Y), StationaryObject.PILLER.CAN_DESTROY);
                            //1st quadrant
                            int randX = randomize.Next(1, 8);
                            int randY = randomize.Next(1, 6);
                            if (x == randX && y == randY && mapPathData[y,x]!=-1)
                            {
                                destructables.Add(temporaryObject);
                                mapPathData[y, x] = -1;
                                numberOfPointBlocks++;
                            }
                            if (x < randX && y == randY && mapPathData[y, x] != -1)
                            {
                                destructables.Add(temporaryObject);
                                mapPathData[y, x] = -1;
                                numberOfPointBlocks++;
                            }

                            //2nd quadrant
                            randX = randomize.Next(9, 17);
                            randY = randomize.Next(1, 6);
                            if (x == randX && y == randY && mapPathData[y, x] != -1)
                            {
                                destructables.Add(temporaryObject);
                                mapPathData[y, x] = -1;
                                numberOfPointBlocks++;
                            }
                            if (x > randX && y > gridHeight && y < randY && mapPathData[y, x] != -1)
                            {
                                destructables.Add(temporaryObject);
                                mapPathData[y, x] = -1;
                                numberOfPointBlocks++;
                            }

                            //3rd quadrant
                            randX = randomize.Next(9, 17);
                            randY = randomize.Next(7, 11);
                            if (x == randX && y == randY && mapPathData[y, x] != -1)
                            {
                                mapPathData[y, x] = -1;
                                destructables.Add(temporaryObject);
                                numberOfPointBlocks++;
                            }
                            if (x > randX && y == randY && mapPathData[y, x] != -1)
                            {
                                destructables.Add(temporaryObject);
                                mapPathData[y, x] = -1;
                                numberOfPointBlocks++;
                            }

                            //4th quadrant
                            randX = randomize.Next(1, 8);
                            randY = randomize.Next(7, 11);
                            if (x == randX && y == randY && mapPathData[y, x] != -1)
                            {
                                destructables.Add(temporaryObject);
                                mapPathData[y, x] = -1;
                                numberOfPointBlocks++;
                            }
                            if (x < randX && y > randY && mapPathData[y, x] != -1)
                            {
                                destructables.Add(temporaryObject);
                                mapPathData[y, x] = -1;
                                numberOfPointBlocks++;
                            }
                        }
                    }
                }//end y
            }//end x      
      
            //generate indestructable objects
            int random_x;
            int random_y;
            int cntr = 0;
            while (cntr < 6)
            {
                random_x = randomize.Next(1, 17);
                random_y = randomize.Next(1, 11);
                if (mapPathData[random_y, random_x] != -1 && (random_x != gridWidth && random_y != gridHeight / 2))
                {
                    StationaryObject temporaryObject = new StationaryObject(indestructableTexture, 1, 1, (int)(random_x * gridDimensions.X),
                                (int)(random_y * gridDimensions.Y), StationaryObject.PILLER.CANNOT_DESTROY);
                    indestructables.Add(temporaryObject);
                    mapPathData[random_y, random_x] = -1;
                    cntr++;
                }
            }

            //add destructable and indestructable objects to list of game objects
            for (int i = 0; i < destructables.Count; i++)
            {
                MainGameClass.objects.Add(destructables.ElementAt(i));
            }
            for (int i = 0; i < indestructables.Count; i++)
            {
                MainGameClass.objects.Add(indestructables.ElementAt(i));
            }       
        }

        //methods respawns objects onto grid 
        public void spawnNewDestructables()
        {
            //if there are too few point blocks on the map, add some more
            if (numberOfPointBlocks < 22)
            {               
                int randX = randomize.Next(1, 17);
                int randY = randomize.Next(1, 11);

                if (mapPathData[randY, randX] != -1 && (randX != gridWidth && randY != gridHeight / 2))
                {
                    //checks that no blocks are drawn over existing enemy objects
                    if (MainGameClass.playermode == MainGameClass.PLAYERMODE.SinglePlayer && MainGameClass.enemyObjects.Count != 0)
                    {
                        StationaryObject temporaryObject = new StationaryObject(destructableTexture, 1, 1,
                            (int)(randX * gridDimensions.X), (int)(randY * gridDimensions.Y), StationaryObject.PILLER.CAN_DESTROY);
                        destructables.Add(temporaryObject);
                        mapPathData[randY, randX] = -1;
                        numberOfPointBlocks++;
                        MainGameClass.objects.Add(temporaryObject);

                    }
                    else
                    {
                        StationaryObject temporaryObject = new StationaryObject(destructableTexture, 1, 1, (int)(randX * gridDimensions.X),
                                        (int)(randY * gridDimensions.Y), StationaryObject.PILLER.CAN_DESTROY);
                        destructables.Add(temporaryObject);
                        mapPathData[randY, randX] = -1;
                        numberOfPointBlocks++;
                        MainGameClass.objects.Add(temporaryObject);
                    }
                }
            }
        }

        /*
         * The following method is used to retrieve data regarding the map
         * for the Astar path finding algorithm
         * returns the value for the point supplied
         * if -1, then unpassable or off the grid.
         */
        public static int getDataFromMap(Vector2 point)
        {
            int maxY = mapPathData.GetUpperBound(0);
            int maxX = mapPathData.GetUpperBound(1);
            if (point.X < 0 || point.X > maxX)
                return -1;
            else if (point.Y < 0 || point.Y > maxY)
                return -1;
            else
                return mapPathData[(int)point.Y,(int)point.X];
        }

        //remove objects within blast areas
        public void bombExplosion(BombObject bombObj)
        {
            int xcoord = (int)((bombObj.getPosition().X+bombObj.getTexture().Width/2) / gridDimensions.X-1);
            int ycoord = (int)(((bombObj.getPosition().Y+bombObj.getTexture().Height/2) / gridDimensions.Y));

            //mapPathData[ycoord, xcoord] = -1;              

            //loop through blast area  
            for (int x = xcoord - 1; x < xcoord + 2; x++)
            {
                for (int y = ycoord - 1; y < ycoord + 2; y++)
                {
                    //loop through all the objects
                    for (int t = 0; t < MainGameClass.objects.Count; t++)
                    {
                        GameObject temp = MainGameClass.objects[t];
                        int temp_x;
                        int temp_y;

                        //checks if the object being examined is the player object due to the use of sprite sheets
                        if (!(temp is Player) && !(temp is EnemyObject))
                        {
                            temp_x = (int)((temp.getPosition().X + temp.getTexture().Width / 2) / gridDimensions.X);
                            temp_y = (int)((temp.getPosition().Y + temp.getTexture().Height / 2) / gridDimensions.Y);
                        }
                        else
                        {
                            temp_x = (int)(((temp.getPosition().X + (temp.getTexture().Width/temp.numberOfColumns) / 2) / gridDimensions.X));
                            temp_y = (int)((temp.getPosition().Y + (temp.getTexture().Height/temp.numberOfRows) / 2) / gridDimensions.Y);
                        }

                        //if within blast area
                        if (temp_x == x && temp_y == y)
                        {     
                            if (!temp.Equals(bombObj) && temp is BombObject)
                            {
                                temp.setMarkedForDestruction(true);
                            }
                            else if (!temp.Equals(bombObj) && temp is EnemyObject)
                            {
                                ((EnemyObject)temp).healthLeft -= 40;
                                ((EnemyObject)temp).checkHealth(bombObj);                                
                            }
                            else if (!temp.Equals(bombObj) && temp is Player)
                            {
                                ((Player)temp).healthLeft -= 40;
                                ((Player)temp).checkHealth(bombObj);
                            }
                            else if (!temp.Equals(bombObj) && (temp is StationaryObject))
                            {
                                //checks if the block can be destroyed
                                if (((StationaryObject)temp).getState() == StationaryObject.PILLER.CAN_DESTROY)
                                {
                                    destructables.Remove((StationaryObject)temp);
                                    numberOfPointBlocks--;
                                    MainGameClass.objects.Remove(temp);
                                    temp.setMarkedForDestruction(true);

                                    //add points to player
                                    if (bombObj.source is Player)
                                    {                                        
                                        ((Player)bombObj.source).score+=5;
                                        tempscores.Add(((Player)bombObj.source).playerID+ ": +5");
                                    }
                                }
                            }
                            else if(!temp.Equals(bombObj))
                            {
                                MainGameClass.objects.Remove(temp);
                                temp.setMarkedForDestruction(true);

                                //add points to source
                                if (bombObj.source is Player)
                                {
                                    ((Player)bombObj.source).score+=5;
                                    tempscores.Add(((Player)bombObj.source).playerID + ": +5");
                                }
                            }                            
                        }  
                    }//end loop through objects                                      
                }//end loop through rowa
            } //end loop through columns
            bombObj.animationCanStart = true;   //indicates to bomb object that animation can start         
        }

        //draw map to screen
        public void Draw(SpriteBatch spriteBatch, SpriteFont font)
        {
            //restrict the amount of scores saved
            if (tempscores.Count > 8)
                tempscores.RemoveAt(0);

            //continuous score update
            for(int i=0;i<tempscores.Count;i++)
                spriteBatch.DrawString(font, tempscores[i], new Vector2(10, 10 + i*20), Color.Cyan);

        }
    }
}
