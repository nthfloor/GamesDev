//This is the main game class which runs everything
//Nathan Floor
//FLRNAT001

using System;
using System.Collections.Generic;
using System.Linq;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Audio;
using Microsoft.Xna.Framework.Content;
using Microsoft.Xna.Framework.GamerServices;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Media;
using Microsoft.Xna.Framework.Net;
using Microsoft.Xna.Framework.Storage;

namespace WindowsGame1
{
    /// <summary>
    /// This is the main type for your game
    /// </summary>
    public class MainGameClass : Microsoft.Xna.Framework.Game
    {
        GraphicsDeviceManager graphics;
        SpriteBatch spriteBatch;

        //manages the state of the game
        enum GAMESTATE { Playing, Paused, StartMenu, GameOver, Controls};
        private GAMESTATE stateOfGame;
        private GAMESTATE previousState;

        //background textures
        private Texture2D startMenuTexture;
        private Texture2D pauseMenuTexture;
        private Texture2D controlsTexture;
        private Texture2D winGametexture;
        private Texture2D looseGameTexture;
        private List<Texture2D> menuOutlineTexture;

        //manages menu option navigation
        private int currentMenuOption;
        private int startMenuDelay = 7;
        
        //manages single/multiplayer modes
        public enum PLAYERMODE { SinglePlayer, TwoPlayer };
        public static PLAYERMODE playermode { get; set; }

        //bomb object variables
        private Texture2D bombTexture;
        private List<BombObject> bombsLayed1;
        private List<BombObject> bombsLayed2;
        private int bombCount1 { get; set; }
        private int bombCount2 { get; set; }
        private int bombPressDelay1 = 50;
        private int bombPressDelay2 = 50;
        private Texture2D explosionTexture; //for animation

        //bullit objects
        private Texture2D bulletTexture;
        private BullitObject bulletObj;

        //player object variables
        private SpriteFont generalfont;
        private Player player1Obj;
        private Player player2Obj;
        private Texture2D playerTexture;
        private bool player1Alive;
        private bool player2Alive;
        private GamePadState oldPad1State;
        private GamePadState oldPad2State;
        private Texture2D healthbarTexture;
        private Texture2D healthbarTopTexture;
        private int player1Score;
        private int player2Score;

        //map variables
        private GameMap map;
        private Texture2D destructableTexture;
        private Texture2D indestructableTexture;
        private List<Texture2D> gridTiles;

        CollisionDetection collisionDetector;
        private int totalTime;
        private long startTime;
        private long currentTime;
        private long pausedTime;
        private long elapsedPauseTime;
        private bool gamePaused;

        //for pausing game and movement
        private int pauseDelay = 10;
        private bool canMove_player1 = false;
        private bool canMove_player2 = false;

        //grid for list of objects 
        public static List<GameObject> objects;
        private Random randomNumber;

        //for enemies
        public static List<EnemyObject> enemyObjects;
        private Texture2D enemy_healthbar_top;
        private Texture2D enemy_healthbar;
        private int highestEnemyScore;

        //game grid for landscape
        private GameObject[,] gameGrid;
        private int gridWidth;
        private int gridHeight;
        private Vector2 gridTileDimensions;
        private int screenWidth;
        private int screenHeight;

        public MainGameClass()
        {
            graphics = new GraphicsDeviceManager(this);

            //this.IsMouseVisible = true;
            this.Window.Title = "Bomber Man";

            stateOfGame = GAMESTATE.StartMenu;
            previousState = stateOfGame;
            playermode = PLAYERMODE.SinglePlayer; //defualt
            currentMenuOption = 1;

            graphics.PreferredBackBufferHeight = 586;
            graphics.PreferredBackBufferWidth = 816;
            graphics.IsFullScreen = true;

            //grid of game objects in game
            gameGrid = null;
            gridWidth = 17;
            gridHeight = 11;
            gridTileDimensions = new Vector2(48,48);
            
            //list of available ground textures
            gridTiles = new List<Texture2D>();
            menuOutlineTexture = new List<Texture2D>();

            Content.RootDirectory = "Content";
        }

        /// <summary>
        /// Allows the game to perform any initialization it needs to before starting to run.
        /// This is where it can query for any required services and load any non-graphic
        /// related content.  Calling base.Initialize will enumerate through any components
        /// and initialize them as well.
        /// </summary>
        protected override void Initialize()
        {
            // TODO: Add your initialization logic here

            screenHeight = graphics.GraphicsDevice.Viewport.Height;
            screenWidth = graphics.GraphicsDevice.Viewport.Width;

            //used to randomize environment
            randomNumber = new Random();            

            //startNewGame();
            base.Initialize();
        }

        //initialise and start new game
        private void startNewMultiPlayerGame()
        {
            //setup grid landscape
            loadGridTiles();

            objects = new List<GameObject>();//keeps track of all objects in game
            enemyObjects = new List<EnemyObject>();//keeps track of all enemy objects
            currentMenuOption = 1;
            collisionDetector = new CollisionDetection();

            //keep track of all the scores
            player1Score = 0;
            player2Score = 0;
            highestEnemyScore = 0;

            //keep track of bombs layed by players
            bombsLayed1 = new List<BombObject>();
            bombsLayed2 = new List<BombObject>();
            bombCount1 = 0;
            bombCount2 = 0;

            //setup time variables
            startTime = System.Environment.TickCount;
            totalTime = 60;
            pausedTime = 0;
            elapsedPauseTime = 0;
            gamePaused = false;

            //initiailse map
            map = new GameMap(screenWidth, screenHeight, gridWidth, gridHeight, gridTileDimensions);
            map.initialiseMap(destructableTexture,indestructableTexture);

            //create player object positioned at the bottom, middle of screen            
            //player1
            player1Obj = new Player(playerTexture, 4, 4, playerTexture.Width/4, (gridHeight/2)*48, 400);
            player1Obj.playerID = "Player1";
            player1Alive = true;
            objects.Add(player1Obj);
            //player2
            player2Obj = new Player(playerTexture, 4, 4, (screenWidth - (playerTexture.Width / 4)) - 30, (gridHeight / 2) * 48, 400);
            player2Obj.playerID = "Player2";
            player2Alive = true;
            objects.Add(player2Obj);

            //enemies
            for (int i = 0; i < 3; i++)
            {
                EnemyObject testEnemy = new EnemyObject(playerTexture, 4, 4, (screenWidth - (playerTexture.Width / 4)) - 30 - i * 48,
                                (gridHeight / 2) * 48, 200);
                testEnemy.playerID = "Enemy" + (i + 1);
                objects.Add(testEnemy);
                enemyObjects.Add(testEnemy);
            }
        }

        //initialise and start new game
        private void startNewSinglePlayerGame()
        {
            //setup grid landscape
            loadGridTiles();

            objects = new List<GameObject>();//keeps track of all objects in game
            enemyObjects = new List<EnemyObject>();//keeps track of all enemy objects
            currentMenuOption = 1;
            collisionDetector = new CollisionDetection();
            bombsLayed1 = new List<BombObject>();
            bombCount1 = 0;

            //keep track of all the scores
            player1Score = 0;
            player2Score = 0;
            highestEnemyScore = 0;

            //setup time variables
            startTime = System.Environment.TickCount;
            totalTime = 60;
            pausedTime = 0;
            elapsedPauseTime = 0;
            gamePaused = false;

            //initiailse map
            map = new GameMap(screenWidth, screenHeight, gridWidth, gridHeight, gridTileDimensions);
            map.initialiseMap(destructableTexture,indestructableTexture);

            //create player object positioned at the bottom, middle of screen
            //player1
            player1Obj = new Player(playerTexture, 4, 4, playerTexture.Width/4, (gridHeight/2)*48, 400);
            player1Obj.playerID = "Player1";
            player1Alive = true;
            objects.Add(player1Obj);
            player2Alive = false;
            //enemies
            for (int i = 0; i < 5; i++)
            {
                EnemyObject testEnemy = new EnemyObject(playerTexture, 4, 4, (screenWidth - (playerTexture.Width / 4)) - 30 - i*48,
                                (gridHeight / 2)*48, 200);
                testEnemy.playerID = "Enemy"+(i+1);
                objects.Add(testEnemy);
                enemyObjects.Add(testEnemy);
            }
        }

        /// <summary>
        /// LoadContent will be called once per game and is the place to load
        /// all of your content.
        /// </summary>
        protected override void LoadContent()
        {
            // Create a new SpriteBatch, which can be used to draw textures.
            spriteBatch = new SpriteBatch(GraphicsDevice);

            // TODO: use this.Content to load your game content here

            //map texture
            destructableTexture = Content.Load<Texture2D>("tiles/marioblock");
            indestructableTexture = Content.Load<Texture2D>("tiles/steel");

            //explosion texture
            explosionTexture = Content.Load<Texture2D>("bombs/explosion_medium");

            //load bullet and bomb texture
            bombTexture = Content.Load<Texture2D>("bombs/bomb");
            bulletTexture = Content.Load<Texture2D>("bullet");

            //health bar texture
            healthbarTexture = Content.Load<Texture2D>("healthbars/healthbar");
            healthbarTopTexture = Content.Load<Texture2D>("healthbars/healthbar_top");
            enemy_healthbar_top = Content.Load<Texture2D>("healthbars/enemy_health_top");
            enemy_healthbar = Content.Load<Texture2D>("healthbars/enemy_health");

            //load grid landscape textures
            gridTiles.Insert(0,Content.Load<Texture2D>("tiles/grey_gridfloor"));
            gridTiles.Insert(1,Content.Load<Texture2D>("tiles/stonefloor"));
            gridTiles.Insert(2,Content.Load<Texture2D>("tiles/paving_ground"));
            gridTiles.Insert(3,Content.Load<Texture2D>("tiles/pathtile"));
            gridTiles.Insert(4,Content.Load<Texture2D>("tiles/floorgrid"));
            gridTiles.Insert(5,Content.Load<Texture2D>("tiles/ground"));

            //load backgrounds
            startMenuTexture = Content.Load<Texture2D>("backgrounds/start_eiffel");
            controlsTexture = Content.Load<Texture2D>("backgrounds/black_controller");
            pauseMenuTexture = Content.Load<Texture2D>("backgrounds/pause_game");
            winGametexture = Content.Load<Texture2D>("backgrounds/win_game");
            looseGameTexture = Content.Load<Texture2D>("backgrounds/loose_game");

            //load menu option outlines
            menuOutlineTexture.Insert(0,Content.Load<Texture2D>("option_outlines/start_outline"));
            menuOutlineTexture.Insert(1, Content.Load<Texture2D>("option_outlines/control_outline"));
            menuOutlineTexture.Insert(2, Content.Load<Texture2D>("option_outlines/pause_outline"));
            menuOutlineTexture.Insert(3, Content.Load<Texture2D>("option_outlines/gameover_outline"));
            menuOutlineTexture.Insert(4, Content.Load<Texture2D>("option_outlines/scores_outline"));

            //setup fonts
            generalfont = Content.Load<SpriteFont>("General");

            //player texture
            playerTexture = Content.Load<Texture2D>("player");
        }

        //randomly select a different ground texture for each new game started
        private void loadGridTiles()
        {            
            Texture2D gridTile;
            int rand = randomNumber.Next(1, 7);
            if (rand == 1)
                gridTile = gridTiles[0];
            else if (rand == 2)
                gridTile = gridTiles[1];
            else if (rand == 3)
                gridTile = gridTiles[2];
            else if (rand == 4)
                gridTile = gridTiles[3];
            else if (rand == 5)
                gridTile = gridTiles[4];
            else
                gridTile = gridTiles[5];

            gameGrid = new GameObject[gridWidth, gridHeight];
            for (int i = 0; i < gridWidth; i++)
            {
                for (int j = 0; j < gridHeight; j++)
                {
                    gameGrid[i, j] = new GameObject(gridTile, 1, 1, gridTile.Width * i, gridTile.Height * j);
                }
            }
        }

        /// <summary>
        /// UnloadContent will be called once per game and is the place to unload
        /// all content.
        /// </summary>
        protected override void UnloadContent()
        {
            // TODO: Unload any non ContentManager content here
        }

        //spawns new enemies for the game
        private void spawnNewEnemies(int numEnemies)
        {
            int rand = randomNumber.Next(0,10);

            if (enemyObjects.Count < numEnemies)
            {
                EnemyObject testEnemy = new EnemyObject(playerTexture, 4, 4, (screenWidth - (playerTexture.Width / 4)) - 30 - rand * 48,
                                (gridHeight / 2) * 48, 100);
                testEnemy.playerID = "Enemy" + (enemyObjects.Count + 1);
                objects.Add(testEnemy);
                enemyObjects.Add(testEnemy);
            }
        }

        /// <summary>
        /// Allows the game to run logic such as updating the world,
        /// checking for collisions, gathering input, and playing audio.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        /// 
        int enemy_direction = 0;
        protected override void Update(GameTime gameTime)
        {
            //update delay variables for better user interaction
            if (pauseDelay > 0)
                pauseDelay--;
            if (bombPressDelay1 > 0)
                bombPressDelay1--;
            if (bombPressDelay2 > 0)
                bombPressDelay2--;
            if (startMenuDelay > 0)
                startMenuDelay--;

            //determine method of input
            if(GamePad.GetState(PlayerIndex.One).IsConnected)
                handleXboxControls();
            else
                handleKeyboardControls();            

            if (stateOfGame == GAMESTATE.StartMenu)//navigate start menu
            {
                //do nothing
            }
            else if (stateOfGame == GAMESTATE.Playing)//play game
            {
                //game over because player(s) have all died
                if (!player2Alive && !player1Alive)
                {
                    //add bonus to enemy score for killing you
                    highestEnemyScore += 50;

                    stateOfGame = GAMESTATE.GameOver;
                }

                //keep record of player scores    
                if (player1Alive)
                    player1Score = player1Obj.score;
                if (player2Alive)
                    player2Score = player2Obj.score;

                //keep record of heighest enemy score
                for (int i = 0; i < enemyObjects.Count; i++)
                    if (enemyObjects[i].score > highestEnemyScore)
                        highestEnemyScore = enemyObjects[i].score;

                //check if game had previously been paused
                if (gamePaused)
                {
                    elapsedPauseTime = System.Environment.TickCount - pausedTime;
                    gamePaused = false;
                    startTime = startTime + elapsedPauseTime;
                }
                currentTime = System.Environment.TickCount - (startTime);
                if ((int)(currentTime / 1000) > totalTime)
                    stateOfGame = GAMESTATE.GameOver;//win conditions    

                //checks if you've lasted for long enough
                currentTime = System.Environment.TickCount - (startTime);
                if ((int)(currentTime / 1000) > totalTime)
                    stateOfGame = GAMESTATE.GameOver;//win conditions

                //update enemy objects
                if (enemyObjects != null)
                {
                    for (int i = 0; i < enemyObjects.Count; i++)
                    {
                        enemyObjects[i].bombLayingDelay++;
                        enemyObjects[i].firingDelay++;
                    }
                }                

                //update map objects                
                map.spawnNewDestructables();

                //spawn new enemies if needed
                if (playermode == PLAYERMODE.SinglePlayer)
                    spawnNewEnemies(4);
                else
                    spawnNewEnemies(3);
              
                //collision detection
                for (int i = 0; i < objects.Count; i++)
                {
                    for (int j = i + 1; j < objects.Count; j++)
                    {
                        GameObject object1 = objects.ElementAt(i);
                        GameObject object2 = objects.ElementAt(j);

                        if (!object1.isMarkedForDestruction() && !object2.isMarkedForDestruction())
                        {
                            if (collisionDetector.isCollision(object1, object2))
                            {
                                if ((object1 is Player) && (object2 is Player))
                                {
                                    //do nothing
                                }
                                else if (((object1 is BombObject) && (object2 is Player)))
                                {
                                    //do nothing
                                }
                                else if (((object2 is BombObject) && (object1 is Player)))
                                {
                                    //do nothing
                                }
                                else if ((object1 is BullitObject) && (object2 is StationaryObject))
                                {
                                    ((BullitObject)object1).setMarkedForDestruction(true);
                                }
                                else if ((object2 is BullitObject) && (object1 is StationaryObject))
                                {
                                    ((BullitObject)object2).setMarkedForDestruction(true);
                                }
                                else if ((object1 is BullitObject) && (object2 is EnemyObject))
                                {
                                    if (!object2.Equals(((BullitObject)object1).getSource()))
                                    {
                                        ((EnemyObject)object2).healthLeft -= 5;
                                        ((EnemyObject)object2).checkHealth(object1);
                                        ((BullitObject)object1).setMarkedForDestruction(true);
                                    }
                                }
                                else if ((object2 is BullitObject) && (object1 is EnemyObject))
                                {
                                    if (!object1.Equals(((BullitObject)object2).getSource()))
                                    {
                                        ((EnemyObject)object1).healthLeft -= 5;
                                        ((EnemyObject)object1).checkHealth(object2);
                                        ((BullitObject)object2).setMarkedForDestruction(true);
                                    }
                                }
                                else if ((object1 is BullitObject) && (object2 is Player))
                                {                                    
                                    if (! object2.Equals(((BullitObject)object1).getSource()))
                                    {
                                        ((Player)object2).healthLeft -= 5;
                                        ((Player)object2).checkHealth(object1);
                                        ((BullitObject)object1).setMarkedForDestruction(true);
                                    }
                                }
                                else if ((object2 is BullitObject) && (object1 is Player))
                                {                                    
                                    if (! object1.Equals(((BullitObject)object2).getSource()))
                                    {
                                        ((Player)object1).healthLeft -= 5;
                                        ((Player)object1).checkHealth(object2);
                                        ((BullitObject)object2).setMarkedForDestruction(true);
                                    }
                                }
                                else if (object1 is EnemyObject && (object2 is StationaryObject))
                                {
                                    //enemy collides with something and needs to change direction
                                    EnemyObject temp_enemy = ((EnemyObject)object1);
                                    int rand = randomNumber.Next(1, 3);
                                    if (rand == 1)
                                        temp_enemy.setDirection(temp_enemy.getDirection() + 90);
                                    else if (rand == 2)
                                        temp_enemy.setDirection(temp_enemy.getDirection() - 90);
                                    temp_enemy.samePathCounter = 0;
                                    temp_enemy.revertPosition();

                                    //lay bomb
                                    if (temp_enemy.bombCount < 1 && temp_enemy.bombLayingDelay > 150)
                                    {
                                        BombObject temp = new BombObject(bombTexture, explosionTexture, 1, 1, 
                                            (int)temp_enemy.getPosition().X - 16, (int)temp_enemy.getPosition().Y + 4, 0.5f);
                                        temp.animationCanStart = false;
                                        temp.source = temp_enemy;
                                        objects.Add(temp);
                                        temp.source = temp_enemy;
                                        temp_enemy.bombsLayed.Add((BombObject)temp);
                                        temp_enemy.bombCount++;
                                        temp_enemy.bombLayingDelay = 0;
                                    }
                                }
                                else if (object2 is EnemyObject && (object1 is StationaryObject))
                                {
                                    //enemy collides with something and needs to change direction
                                    EnemyObject temp_enemy = ((EnemyObject)object2);
                                    int rand = randomNumber.Next(1,3);
                                    if(rand == 1)
                                        temp_enemy.setDirection(temp_enemy.getDirection() + 90);
                                    else if (rand == 2)
                                        temp_enemy.setDirection(temp_enemy.getDirection() - 90);

                                    temp_enemy.samePathCounter = 0;
                                    temp_enemy.revertPosition();

                                    //lay bomb
                                    if (temp_enemy.bombCount < 1 && temp_enemy.bombLayingDelay > 150)
                                    {
                                        BombObject temp = new BombObject(bombTexture, explosionTexture, 1, 1,
                                            (int)temp_enemy.getPosition().X - 16, (int)temp_enemy.getPosition().Y + 4, 0.5f);
                                        temp.animationCanStart = false;
                                        temp.source = temp_enemy;
                                        objects.Add(temp);
                                        temp.source = temp_enemy;
                                        temp_enemy.bombsLayed.Add((BombObject)temp);
                                        temp_enemy.bombCount++;
                                        temp_enemy.bombLayingDelay = 0;
                                    }
                                }
                                else if ((object2 is Player) && !(object1 is Player))
                                    ((Player)object2).revertPosition();
                                else if ((object1 is Player) && !(object2 is Player))
                                    ((Player)object1).revertPosition();

                            }//if there is a cillision
                        }//if neither object is marked for destruction                        
                    }//end j-loop
                }//end i-loop

                //call update method on all objects
                for (int i = 0; i < objects.Count; i++)
                {
                    if (objects[i] is BombObject)
                        ((BombObject)objects[i]).update();
                    else if (objects[i] is BullitObject)
                        ((BullitObject)objects[i]).update();
                    else if (objects[i] is EnemyObject)
                    {
                        //the enemy  AI begins here
                        //move enemy object   
                        EnemyObject temp_enemy = (EnemyObject)objects[i];
                        Vector2 tempPos = new Vector2();
                       
                        //stop object from fallingoff edge of window
                        tempPos.X = MathHelper.Clamp(temp_enemy.getPosition().X, 0,
                            screenWidth - temp_enemy.getTexture().Width / temp_enemy.numberOfColumns);
                        tempPos.Y = MathHelper.Clamp(temp_enemy.getPosition().Y, 0,
                            screenHeight - (temp_enemy.getTexture().Height / temp_enemy.numberOfRows) - 58);

                        //randomly moves enemy object around map, and allows for random responses to environment
                        //if objects collides with edge of grid or hasn't started moving yet                        
                        if (tempPos.X != temp_enemy.getPosition().X || tempPos.Y != temp_enemy.getPosition().Y)
                        {
                            int rand = randomNumber.Next(1, 3);

                            if (rand == 1)
                                enemy_direction = (int)(temp_enemy.getDirection() + 90);
                            else
                                enemy_direction = (int)(temp_enemy.getDirection() - 90);
                            
                            temp_enemy.samePathCounter = 0;
                        }
                        else if (!temp_enemy.startedMoving)//start moving object
                        {
                            int rand = randomNumber.Next(1, 3);

                            if(rand == 1)
                                enemy_direction = 0;
                            else
                                enemy_direction = 180;
                            temp_enemy.startedMoving = true;
                        }
                        else
                        {
                            int rand = randomNumber.Next(1, 3);

                            //check to see if enemy has been moving in the same direction for too long or not
                            if (temp_enemy.samePathCounter < 200)
                            {
                                enemy_direction = (int)(temp_enemy.getDirection());
                                temp_enemy.samePathCounter++;
                            }
                            else
                            {
                                if (rand == 1)
                                    enemy_direction = (int)(temp_enemy.getDirection() + 90);
                                else
                                    enemy_direction = (int)(temp_enemy.getDirection() - 90);
                                temp_enemy.samePathCounter = 0;
                            }
                        }

                        temp_enemy.setPosition(tempPos);
                        temp_enemy.moveInDirection(enemy_direction);
                        if (player1Alive && temp_enemy.getDistanceTo(player1Obj) < 100)
                        {
                            //close enough to shoot
                            if (temp_enemy.firingDelay > 30)
                            {
                                fireBullet(temp_enemy, temp_enemy.getDirectionTo(player1Obj.getPosition()));
                                temp_enemy.firingDelay = 0;
                            }

                            //close enough to player object to lay bombs
                            if (temp_enemy.bombCount < 2 && temp_enemy.bombLayingDelay > 150)
                            {
                                BombObject temp = new BombObject(bombTexture, explosionTexture, 1, 1, (int)temp_enemy.getPosition().X - 16,
                                    (int)temp_enemy.getPosition().Y + 4, 0.5f);
                                temp.animationCanStart = false;
                                temp.source = temp_enemy;
                                objects.Add(temp);
                                temp.source = temp_enemy;
                                temp_enemy.bombsLayed.Add((BombObject)temp);
                                temp_enemy.bombCount++;
                                temp_enemy.bombLayingDelay = 0;
                            }
                        }
                        if (player2Alive && temp_enemy.getDistanceTo(player2Obj) < 100)
                        {
                            //close enough to shoot
                            if (temp_enemy.firingDelay > 30)
                            {
                                fireBullet(temp_enemy, temp_enemy.getDirectionTo(player2Obj.getPosition()));
                                temp_enemy.firingDelay = 0;
                            }

                            //close enough to player object to lay bombs
                            if (temp_enemy.bombCount < 2 && temp_enemy.bombLayingDelay > 150)
                            {
                                BombObject temp = new BombObject(bombTexture, explosionTexture, 1, 1, (int)temp_enemy.getPosition().X - 16,
                                    (int)temp_enemy.getPosition().Y + 4, 0.5f);
                                temp.animationCanStart = false;
                                temp.source = temp_enemy;
                                objects.Add(temp);
                                temp.source = temp_enemy;
                                temp_enemy.bombsLayed.Add((BombObject)temp);
                                temp_enemy.bombCount++;
                                temp_enemy.bombLayingDelay = 0;
                            }
                        }

                        //update enemy sprite
                        ((EnemyObject)objects[i]).update();
                    }
                }
                if (canMove_player1 && player1Alive)
                    player1Obj.update();
                else
                {
                    //set LOOSE conditions TODO
                    if (!player1Alive && playermode == PLAYERMODE.SinglePlayer)
                        stateOfGame = GAMESTATE.GameOver;
                }
                if (canMove_player2 && player2Alive)
                    player2Obj.update();    

                //delete objects that have been destroyed
                for (int i = 0; i < objects.Count; i++)
                    if (objects[i].isMarkedForDestruction())
                    {
                        GameObject temp = objects[i];
                        if (temp is BombObject)
                        {
                            if (((BombObject)temp).source is EnemyObject)
                            {
                                BombObject currentBomb = (BombObject)temp;
                                if (((EnemyObject)currentBomb.source).bombsLayed.Contains((BombObject)temp))
                                {
                                    EnemyObject enemy = (EnemyObject)(((BombObject)temp).source);
                                    //bomb explodes!
                                    map.bombExplosion((BombObject)temp);
                                    enemy.bombsLayed.Remove((BombObject)temp);
                                    enemy.bombCount--;
                                }
                            }
                            else if ((((BombObject)temp).source).Equals(player1Obj) && bombsLayed1.Contains(((BombObject)temp)))
                            {
                                //bomb explodes!
                                map.bombExplosion((BombObject)temp);
                                bombsLayed1.Remove((BombObject)temp);
                                bombCount1--;
                            }
                            else if ((((BombObject)temp).source).Equals(player2Obj) && bombsLayed2.Contains(((BombObject)temp)))
                            {
                                //bomb explodes!
                                map.bombExplosion((BombObject)temp);
                                bombsLayed2.Remove((BombObject)temp);
                                bombCount2--;
                            }                                
                        }
                        else if (temp.Equals(player1Obj))
                        {
                            objects.Remove(temp);
                            player1Alive = false;
                            player1Obj = null;
                        }
                        else if (temp.Equals(player2Obj))
                        {
                            objects.Remove(temp);
                            player2Alive = false;
                            player2Obj = null;
                        }
                        else if (temp is EnemyObject)
                        {
                            enemyObjects.Remove((EnemyObject)temp);                            
                            objects.Remove(temp);
                            temp = null;
                        }
                        else if (!(temp is BombObject))
                            objects.Remove(temp);
                    }
            }
            else if (stateOfGame == GAMESTATE.Paused)//navigate pause menu
            {
                //do nothing
            }
            base.Update(gameTime);
        }//end update method

        //pause/resume game using keyboard
        public void handlePauseControl()
        {
            KeyboardState keyState = Keyboard.GetState();

            if (keyState.IsKeyDown(Keys.P) && pauseDelay <= 0)
            {
                if (stateOfGame == GAMESTATE.Paused)
                    stateOfGame = GAMESTATE.Playing;  
                else if (stateOfGame == GAMESTATE.Playing)
                {
                    stateOfGame = GAMESTATE.Paused;
                    pausedTime = System.Environment.TickCount;
                    gamePaused = true;
                }
                currentMenuOption = 1;
                pauseDelay = 7;
            }
        }

        //handles keyboard user input
        public void handleKeyboardControls()
        {
            KeyboardState keyState = Keyboard.GetState();

            //exit game using keyboard
            if (keyState.IsKeyDown(Keys.Escape))
                this.Exit();

            if (stateOfGame == GAMESTATE.StartMenu && startMenuDelay <= 0)
            {
                if (keyState.IsKeyDown(Keys.Down))//move down menu
                {
                    if (currentMenuOption < 4)
                        currentMenuOption++;
                }
                if (keyState.IsKeyDown(Keys.Up))//move up menu
                {
                    if (currentMenuOption > 1)
                        currentMenuOption--;
                }
                startMenuDelay = 7;
                if (keyState.IsKeyDown(Keys.Enter))//select a menu option
                {
                    if (currentMenuOption == 1)//start new single player game
                    {
                        startNewSinglePlayerGame();
                        playermode = PLAYERMODE.SinglePlayer;
                        stateOfGame = GAMESTATE.Playing;
                    }
                    else if (currentMenuOption == 2)//start new multiplayer game
                    {
                        startNewMultiPlayerGame();
                        playermode = PLAYERMODE.TwoPlayer;
                        stateOfGame = GAMESTATE.Playing;
                    }
                    else if (currentMenuOption == 3)//look at xbox controls
                    {
                        previousState = GAMESTATE.StartMenu;
                        stateOfGame = GAMESTATE.Controls;
                    }
                    else if (currentMenuOption == 4)//exit game
                        this.Exit();

                    startMenuDelay = 10;
                }                
            }
            else if (stateOfGame == GAMESTATE.Controls && startMenuDelay <= 0)
            {
                if (keyState.IsKeyDown(Keys.Enter))//return to previous menu
                {
                    stateOfGame = previousState;
                    currentMenuOption = 1;
                }
                startMenuDelay = 10;
            }
            else if (stateOfGame == GAMESTATE.Playing)
            {
                handlePauseControl();
                int direction1 = 0;
                int direction2 = 0;

                //allow input only if player1 is alive
                if (player1Alive)
                {
                    //for player1
                    if (keyState.IsKeyDown(Keys.W))//move up
                    {
                        canMove_player1 = true;
                        if (keyState.IsKeyDown(Keys.A) && !(keyState.IsKeyDown(Keys.D)))
                            direction1 = -45;
                        else if (keyState.IsKeyDown(Keys.D) && !(keyState.IsKeyDown(Keys.A)))
                            direction1 = 45;
                        else
                            direction1 = 0;
                    }
                    else if (keyState.IsKeyDown(Keys.S))//move down
                    {
                        canMove_player1 = true;
                        if (keyState.IsKeyDown(Keys.A) && !(keyState.IsKeyDown(Keys.D)))
                            direction1 = 225;
                        else if (keyState.IsKeyDown(Keys.D) && !(keyState.IsKeyDown(Keys.A)))
                            direction1 = 135;
                        else
                            direction1 = 180;
                    }
                    else if (keyState.IsKeyDown(Keys.A) && !(keyState.IsKeyDown(Keys.D)))//move left
                    {
                        canMove_player1 = true;
                        direction1 = 270;
                    }
                    else if (keyState.IsKeyDown(Keys.D) && !((keyState.IsKeyDown(Keys.A))))//move right
                    {
                        canMove_player1 = true;
                        direction1 = 90;
                    }
                    else
                        canMove_player1 = false;

                    //lay bombs for player1
                    if (keyState.IsKeyDown(Keys.Space) && (bombPressDelay1 <= 0))
                    {
                        if (bombCount1 < 2)
                        {
                            BombObject temp = new BombObject(bombTexture, explosionTexture, 1, 1, (int)player1Obj.getPosition().X - 16, (int)player1Obj.getPosition().Y + 4, 0.5f);
                            temp.animationCanStart = false;
                            temp.source = player1Obj;
                            objects.Add(temp);
                            bombsLayed1.Add(temp);
                            bombCount1++;
                        }
                        bombPressDelay1 = 50;
                    }

                    //player1 fires bullet
                    if (player1Obj.cooldownTimer <= 0)
                    {
                        if (keyState.IsKeyDown(Keys.B))
                        {
                            fireBullet(player1Obj, 90 + player1Obj.getDirection());
                        }
                    }
                    player1Obj.cooldownTimer--;

                    //sprint
                    if (keyState.IsKeyDown(Keys.LeftShift))
                        player1Obj.setSprinting(true);
                    else
                        player1Obj.setSprinting(false);

                    //update player1 object's position
                    if (canMove_player1)
                    {
                        //stop player from fallingoff edge of window
                        Vector2 tempPos = new Vector2();
                        tempPos.X = MathHelper.Clamp(player1Obj.getPosition().X, 0, screenWidth - player1Obj.getTexture().Width / player1Obj.numberOfColumns);
                        tempPos.Y = MathHelper.Clamp(player1Obj.getPosition().Y, 0, screenHeight - (player1Obj.getTexture().Height / player1Obj.numberOfRows) - 58);
                        player1Obj.setPosition(tempPos);
                        player1Obj.moveInDirection(direction1);
                    }
                }//player1 is alive

                //allow input only if player2 is alive
                if (player2Alive)
                {
                    //for player2
                    if (keyState.IsKeyDown(Keys.Up))//move up
                    {
                        canMove_player2 = true;
                        if (keyState.IsKeyDown(Keys.Left) && !(keyState.IsKeyDown(Keys.Right)))
                            direction2 = -45;
                        else if (keyState.IsKeyDown(Keys.Right) && !(keyState.IsKeyDown(Keys.Left)))
                            direction2 = 45;
                        else
                            direction2 = 0;
                    }
                    else if (keyState.IsKeyDown(Keys.Down))//move down
                    {
                        canMove_player2 = true;
                        if (keyState.IsKeyDown(Keys.Left) && !(keyState.IsKeyDown(Keys.Right)))
                            direction2 = 225;
                        else if (keyState.IsKeyDown(Keys.Right) && !(keyState.IsKeyDown(Keys.Left)))
                            direction2 = 135;
                        else
                            direction2 = 180;
                    }
                    else if (keyState.IsKeyDown(Keys.Left) && !(keyState.IsKeyDown(Keys.Right)))//move left
                    {
                        canMove_player2 = true;
                        direction2 = 270;
                    }
                    else if (keyState.IsKeyDown(Keys.Right) && !((keyState.IsKeyDown(Keys.Left))))//move right
                    {
                        canMove_player2 = true;
                        direction2 = 90;
                    }
                    else
                        canMove_player2 = false;

                    //lay bombs for player2
                    if (keyState.IsKeyDown(Keys.RightAlt) && (bombPressDelay2 <= 0))
                    {
                        if (bombCount2 < 2)
                        {
                            BombObject temp = new BombObject(bombTexture, explosionTexture, 1, 1, (int)player2Obj.getPosition().X - 16, (int)player2Obj.getPosition().Y + 4, 0.5f);
                            temp.animationCanStart = false;
                            temp.source = player2Obj;
                            bombsLayed2.Add(temp);
                            bombCount2++;
                            objects.Add(temp);
                        }
                        bombPressDelay2 = 50;
                    }

                    //player2 fires bullet
                    if (player2Obj.cooldownTimer <= 0)
                    {
                        if (keyState.IsKeyDown(Keys.RightControl))
                        {
                            fireBullet(player2Obj, 90 + player2Obj.getDirection());
                        }
                    }
                    player2Obj.cooldownTimer--;

                    //sprint
                    if (keyState.IsKeyDown(Keys.RightShift))
                        player2Obj.setSprinting(true);
                    else
                        player2Obj.setSprinting(false);

                    //update player2 object's position
                    if (canMove_player2)
                    {
                        //stop player from fallingoff edge of window
                        Vector2 tempPos = new Vector2();
                        tempPos.X = MathHelper.Clamp(player2Obj.getPosition().X, 0, screenWidth - player2Obj.getTexture().Width / player2Obj.numberOfColumns);
                        tempPos.Y = MathHelper.Clamp(player2Obj.getPosition().Y, 0, screenHeight - (player2Obj.getTexture().Height / player2Obj.numberOfRows) - 58);
                        player2Obj.setPosition(tempPos);
                        player2Obj.moveInDirection(direction2);
                    }
                }  //player2 is alive 
            }
            else if (stateOfGame == GAMESTATE.Paused && startMenuDelay <= 0)
            {
                if (keyState.IsKeyDown(Keys.Down))//move down menu
                {
                    if (currentMenuOption < 5)
                        currentMenuOption++;
                }
                if (keyState.IsKeyDown(Keys.Up))//move up menu
                {
                    if (currentMenuOption > 1)
                        currentMenuOption--;
                }
                startMenuDelay = 7;
                if (keyState.IsKeyDown(Keys.Enter))//select a menu option
                {
                    if (currentMenuOption == 1) // resume game
                        stateOfGame = GAMESTATE.Playing;
                    else if (currentMenuOption == 2)
                    {
                        if (playermode == PLAYERMODE.TwoPlayer)//restart current mode of game
                            startNewMultiPlayerGame();
                        else
                            startNewSinglePlayerGame();
                        stateOfGame = GAMESTATE.Playing;
                    }
                    else if (currentMenuOption == 3)//look at controls
                    {
                        stateOfGame = GAMESTATE.Controls;
                        previousState = GAMESTATE.Paused;
                    }
                    else if (currentMenuOption == 4)//exit to main menu
                    {
                        stateOfGame = GAMESTATE.StartMenu;
                        currentMenuOption = 1;
                    }
                    else if (currentMenuOption == 5)//exit game
                        this.Exit();

                    startMenuDelay = 10;
                }
                
            }
            else if (startMenuDelay <= 0)//display winning/loosing screen
            {
                if (keyState.IsKeyDown(Keys.Down))//move down menu
                {
                    if (currentMenuOption < 4)
                        currentMenuOption++;
                }
                if (keyState.IsKeyDown(Keys.Up))//move up menu
                {
                    if (currentMenuOption > 1)
                        currentMenuOption--;
                }
                startMenuDelay = 7;
                if (keyState.IsKeyDown(Keys.Enter))//select a menu option
                {
                    if (currentMenuOption == 1)
                    {
                        if (playermode == PLAYERMODE.TwoPlayer)//restart current mode of game
                            startNewMultiPlayerGame();
                        else
                            startNewSinglePlayerGame();
                        stateOfGame = GAMESTATE.Playing;
                    }
                    else if (currentMenuOption == 2)//look at controls
                    {
                        stateOfGame = GAMESTATE.Controls;
                        previousState = GAMESTATE.GameOver;
                    }
                    else if (currentMenuOption == 3)//return to main menu
                    {
                        stateOfGame = GAMESTATE.StartMenu;
                        currentMenuOption = 1;
                    }
                    else if (currentMenuOption == 4)//exit game
                        this.Exit();

                    startMenuDelay = 10;
                }                
            }
        }

        //handles Xbox user input TODO
        public void handleXboxControls()
        {
            GamePadState pad1State = GamePad.GetState(PlayerIndex.One);
            GamePadState pad2State = GamePad.GetState(PlayerIndex.Two);

            // Allows the game to exit using XBox
            if (pad1State.Buttons.Back == ButtonState.Pressed)
                this.Exit();

            if (stateOfGame == GAMESTATE.StartMenu && startMenuDelay <=0)
            {
                if (pad1State.ThumbSticks.Left.Y <= -0.5)//move down menu
                {
                    if (currentMenuOption < 4)
                        currentMenuOption++;
                }
                if (pad1State.ThumbSticks.Left.Y >= 0.5)//move up menu
                {
                    if (currentMenuOption > 1)
                        currentMenuOption--;
                }
                startMenuDelay = 7;
                if (pad1State.Buttons.A == ButtonState.Pressed && oldPad1State.Buttons.A == ButtonState.Released)//select a menu option
                {
                    if (currentMenuOption == 1)//start new single player game
                    {
                        startNewSinglePlayerGame();
                        playermode = PLAYERMODE.SinglePlayer;
                        stateOfGame = GAMESTATE.Playing;
                    }
                    if (currentMenuOption == 2)//start new multiplayer game
                    {
                        startNewMultiPlayerGame();
                        playermode = PLAYERMODE.TwoPlayer;
                        stateOfGame = GAMESTATE.Playing;
                    }
                    else if (currentMenuOption == 3)//llok at xbox controls
                    {
                        stateOfGame = GAMESTATE.Controls;
                        previousState = GAMESTATE.StartMenu;
                    }
                    else if (currentMenuOption == 4)//exit game
                        this.Exit();

                    bombPressDelay1 = 50;
                    bombPressDelay2 = 50;
                    startMenuDelay = 10;
                }                
                oldPad1State = pad1State;
            }
            else if (stateOfGame == GAMESTATE.Controls && startMenuDelay <= 0)
            {
                if (pad1State.Buttons.A == ButtonState.Pressed && oldPad1State.Buttons.A == ButtonState.Released)//return to previous menu
                {
                    stateOfGame = previousState;
                    currentMenuOption = 1;
                }
                oldPad1State = pad1State;
                startMenuDelay = 10;
            }
            else if (stateOfGame == GAMESTATE.Playing)
            {
                //handle pause condition
                if (pad1State.Buttons.Start == ButtonState.Pressed && oldPad1State.Buttons.Start == ButtonState.Released)
                {
                    if (stateOfGame == GAMESTATE.Paused)
                    {
                        stateOfGame = GAMESTATE.Playing;
                        pausedTime = System.Environment.TickCount;
                        gamePaused = true;
                    }
                    else if (stateOfGame == GAMESTATE.Playing)
                        stateOfGame = GAMESTATE.Paused;
                    currentMenuOption = 1;
                    bombPressDelay1 = 50;
                    bombPressDelay2 = 50;
                }
                oldPad1State = pad1State;

                int direction1 = 0;
                int direction2 = 0;

                //allow input only if player1 is alive
                if (player1Alive)
                {
                    //for player1
                    if (pad1State.ThumbSticks.Left.Y >= 0.5)//move up
                    {
                        canMove_player1 = true;
                        if (pad1State.ThumbSticks.Left.X <= -0.5)
                            direction1 = -45;
                        else if (pad1State.ThumbSticks.Left.X >= 0.5)
                            direction1 = 45;
                        else
                            direction1 = 0;
                    }
                    else if (pad1State.ThumbSticks.Left.Y <= -0.5)//move down
                    {
                        canMove_player1 = true;
                        if (pad1State.ThumbSticks.Left.X <= -0.5)
                            direction1 = 225;
                        else if (pad1State.ThumbSticks.Left.X >= 0.5)
                            direction1 = 135;
                        else
                            direction1 = 180;
                    }
                    else if (pad1State.ThumbSticks.Left.X <= -0.5)//move left
                    {
                        canMove_player1 = true;
                        direction1 = 270;
                    }
                    else if (pad1State.ThumbSticks.Left.X >= 0.5)//move right
                    {
                        canMove_player1 = true;
                        direction1 = 90;
                    }
                    else
                        canMove_player1 = false;

                    //lay bombs for player1
                    if (pad1State.Buttons.A == ButtonState.Pressed && bombPressDelay1 <= 0)
                    {
                        if (bombCount1 < 2)
                        {
                            BombObject temp = new BombObject(bombTexture, explosionTexture, 1, 1, (int)player1Obj.getPosition().X - 16, (int)player1Obj.getPosition().Y + 4, 0.5f);
                            temp.animationCanStart = false;
                            temp.source = player1Obj;
                            objects.Add(temp);
                            bombsLayed1.Add(temp);
                            bombCount1++;
                        }
                        bombPressDelay1 = 50;
                    }
                    oldPad1State = pad1State;

                    //player1 fires bullet
                    if (player1Obj.cooldownTimer <= 0)
                    {
                        if (pad1State.Triggers.Right >= 0.6)
                        {
                            fireBullet(player1Obj, 90 + player1Obj.getDirection());
                        }
                    }
                    player1Obj.cooldownTimer--;

                    //sprint
                    if (pad1State.Buttons.B == ButtonState.Pressed)
                        player1Obj.setSprinting(true);
                    else
                        player1Obj.setSprinting(false);

                    //update player1 object's position
                    if (canMove_player1)
                    {
                        //stop player from fallingoff edge of window
                        Vector2 tempPos = new Vector2();
                        tempPos.X = MathHelper.Clamp(player1Obj.getPosition().X, 0, screenWidth - player1Obj.getTexture().Width / player1Obj.numberOfColumns);
                        tempPos.Y = MathHelper.Clamp(player1Obj.getPosition().Y, 0, screenHeight - (player1Obj.getTexture().Height / player1Obj.numberOfRows) - 58);
                        player1Obj.setPosition(tempPos);
                        player1Obj.moveInDirection(direction1);
                    }
                }//player1 is alive

                //allow input only if player2 is alive
                if (player2Alive)
                {
                    //for player2
                    if (pad2State.ThumbSticks.Left.Y >= 0.5)//move up
                    {
                        canMove_player2 = true;
                        if (pad2State.ThumbSticks.Left.X <= -0.5)
                            direction2 = -45;
                        else if (pad2State.ThumbSticks.Left.X >= 0.5)
                            direction2 = 45;
                        else
                            direction2 = 0;
                    }
                    else if (pad2State.ThumbSticks.Left.Y <= -0.5)//move down
                    {
                        canMove_player2 = true;
                        if (pad2State.ThumbSticks.Left.X <= -0.5)
                            direction2 = 225;
                        else if (pad2State.ThumbSticks.Left.X >= 0.5)
                            direction2 = 135;
                        else
                            direction2 = 180;
                    }
                    else if (pad2State.ThumbSticks.Left.X <= -0.5)//move left
                    {
                        canMove_player2 = true;
                        direction2 = 270;
                    }
                    else if (pad2State.ThumbSticks.Left.X >= 0.5)//move right
                    {
                        canMove_player2 = true;
                        direction2 = 90;
                    }
                    else
                        canMove_player2 = false;

                    //lay bombs for player2
                    if (pad2State.Buttons.A == ButtonState.Pressed && bombPressDelay2 <= 0)
                    {
                        if (bombCount2 < 2)
                        {
                            BombObject temp = new BombObject(bombTexture, explosionTexture, 1, 1, (int)player2Obj.getPosition().X - 16, (int)player2Obj.getPosition().Y + 4, 0.5f);
                            temp.animationCanStart = false;
                            temp.source = player2Obj;
                            objects.Add(temp);
                            bombsLayed2.Add(temp);
                            bombCount2++;
                        }
                        bombPressDelay2 = 100;
                    }
                    oldPad2State = pad2State;

                    //player2 fires bullet
                    if (player2Obj.cooldownTimer <= 0)
                    {
                        if (pad2State.Triggers.Right > 0.5)
                        {
                            fireBullet(player2Obj, 90 + player2Obj.getDirection());
                        }
                    }
                    player2Obj.cooldownTimer--;

                    //sprint
                    if (pad2State.Buttons.B == ButtonState.Pressed)
                        player2Obj.setSprinting(true);
                    else
                        player2Obj.setSprinting(false);

                    //update player2 object's position
                    if (canMove_player2)
                    {
                        //stop player from fallingoff edge of window
                        Vector2 tempPos = new Vector2();
                        tempPos.X = MathHelper.Clamp(player2Obj.getPosition().X, 0, screenWidth - player2Obj.getTexture().Width / player2Obj.numberOfColumns);
                        tempPos.Y = MathHelper.Clamp(player2Obj.getPosition().Y, 0, screenHeight - (player2Obj.getTexture().Height / player2Obj.numberOfRows) - 58);
                        player2Obj.setPosition(tempPos);
                        player2Obj.moveInDirection(direction2);
                    }
                }  //player2 is alive 
            }
            else if (stateOfGame == GAMESTATE.Paused && startMenuDelay <= 0)
            {
                if (pad1State.ThumbSticks.Left.Y <= -0.5)//move down menu
                {
                    if (currentMenuOption < 5)
                        currentMenuOption++;
                }
                if (pad1State.ThumbSticks.Left.Y >= 0.5)//move up menu
                {
                    if (currentMenuOption > 1)
                        currentMenuOption--;
                }
                startMenuDelay = 7;
                if (pad1State.Buttons.A == ButtonState.Pressed && oldPad1State.Buttons.A == ButtonState.Released)//select a menu option
                {
                    if (currentMenuOption == 1)//resume current game
                        stateOfGame = GAMESTATE.Playing;
                    else if (currentMenuOption == 2)
                    {
                        if (playermode == PLAYERMODE.TwoPlayer)//restart current mode of playermode
                            startNewMultiPlayerGame();
                        else
                            startNewSinglePlayerGame();
                        stateOfGame = GAMESTATE.Playing;
                    }
                    else if (currentMenuOption == 3)//look at xbox controls
                    {
                        stateOfGame = GAMESTATE.Controls;
                        previousState = GAMESTATE.Paused;
                    }
                    else if (currentMenuOption == 4)//exit to main menu
                    {
                        stateOfGame = GAMESTATE.StartMenu;
                        currentMenuOption = 1;
                    }
                    else if (currentMenuOption == 5)//exit game
                        this.Exit();

                    bombPressDelay1 = 50;
                    bombPressDelay2 = 50;
                    startMenuDelay = 10;
                }                
                oldPad1State = pad1State;
            }
            else if (startMenuDelay <= 0)//display winning/loosing screen
            {
                if (pad1State.ThumbSticks.Left.Y <= -0.5)//move down menu
                {
                    if (currentMenuOption < 4)
                        currentMenuOption++;
                }
                if (pad1State.ThumbSticks.Left.Y >= 0.5)//move up menu
                {
                    if (currentMenuOption > 1)
                        currentMenuOption--;
                }
                startMenuDelay = 7;
                if (pad1State.Buttons.A == ButtonState.Pressed && oldPad1State.Buttons.A == ButtonState.Released)//select a menu option
                {
                    if (currentMenuOption == 1)
                    {
                        if (playermode == PLAYERMODE.TwoPlayer)//restart current mode of playermode
                            startNewMultiPlayerGame();
                        else
                            startNewSinglePlayerGame();
                        stateOfGame = GAMESTATE.Playing;
                    }
                    else if (currentMenuOption == 2)//look at xbox controls
                    {
                        stateOfGame = GAMESTATE.Controls;
                        previousState = GAMESTATE.GameOver;
                    }
                    else if (currentMenuOption == 3)//exit to main menu
                    {
                        stateOfGame = GAMESTATE.StartMenu;
                        currentMenuOption = 1;
                    }
                    else if (currentMenuOption == 4)//exit game
                        this.Exit();

                    bombPressDelay1 = 50;
                    bombPressDelay2 = 50;
                    startMenuDelay = 10;
                }                
                oldPad1State = pad1State;
            }
        }

        //for firing bullets
        public void fireBullet(GameObject source,float dir)
        {
            ((Player)source).cooldownTimer = 50;
            bulletObj = null;
            if (source is EnemyObject)
            {
                //calculate direction bullet should take realtive to source
                int xpos = (int)((source.getPosition().X) + Math.Sin(MathHelper.ToRadians(dir) *48) +10);
                int ypos = (int)((source.getPosition().Y) + Math.Cos(MathHelper.ToRadians(dir) *48) +20);
                bulletObj = new BullitObject(bulletTexture, 1, 1, xpos, ypos, source);
                objects.Add(bulletObj);
            }
            else if (source is Player)
            {
                //calculate direction bullet should take realtive to source
                int xpos = (int)((source.getPosition().X) + Math.Sin(MathHelper.ToRadians(dir) * 32) + 10);
                int ypos = (int)((source.getPosition().Y) + Math.Cos(MathHelper.ToRadians(dir) * 32) + 20);
                bulletObj = new BullitObject(bulletTexture, 1, 1, xpos, ypos, source);
                objects.Add(bulletObj);
            }        
        }

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {
            if (stateOfGame == GAMESTATE.StartMenu) //if game ha just started or you're accessing the main-menu
            {
                GraphicsDevice.Clear(Color.Black);
                spriteBatch.Begin();
                //draw startmenu background and meu outline to see options
                spriteBatch.Draw(startMenuTexture, new Rectangle(0, 0, screenWidth, screenHeight), Color.White);
                spriteBatch.Draw(menuOutlineTexture[0], new Rectangle(screenWidth-165,(screenHeight/2) -115, 
                    menuOutlineTexture[0].Width, menuOutlineTexture[0].Height), Color.White);
                //display menu options
                if (currentMenuOption == 1)
                {
                    spriteBatch.DrawString(generalfont, "Single Player", new Vector2(screenWidth - 150, (screenHeight / 2) - 100), Color.Red);
                    spriteBatch.DrawString(generalfont, "Multiplayer", new Vector2(screenWidth - 150, (screenHeight / 2) - 50), Color.White);
                    spriteBatch.DrawString(generalfont, "Controls", new Vector2(screenWidth - 150, screenHeight / 2), Color.White);
                    spriteBatch.DrawString(generalfont, "Exit", new Vector2(screenWidth - 150, (screenHeight / 2) + 50), Color.White);
                }
                if (currentMenuOption == 2)
                {
                    spriteBatch.DrawString(generalfont, "Single Player", new Vector2(screenWidth - 150, (screenHeight / 2) - 100), Color.White);
                    spriteBatch.DrawString(generalfont, "Multiplayer", new Vector2(screenWidth - 150, (screenHeight / 2) - 50), Color.Red);
                    spriteBatch.DrawString(generalfont, "Controls", new Vector2(screenWidth - 150, screenHeight / 2), Color.White);
                    spriteBatch.DrawString(generalfont, "Exit", new Vector2(screenWidth - 150, (screenHeight / 2) + 50), Color.White);
                }
                if (currentMenuOption == 3)
                {
                    spriteBatch.DrawString(generalfont, "Single Player", new Vector2(screenWidth - 150, (screenHeight / 2) - 100), Color.White);
                    spriteBatch.DrawString(generalfont, "Multiplayer", new Vector2(screenWidth - 150, (screenHeight / 2) - 50), Color.White);
                    spriteBatch.DrawString(generalfont, "Controls", new Vector2(screenWidth - 150, screenHeight / 2), Color.Red);
                    spriteBatch.DrawString(generalfont, "Exit", new Vector2(screenWidth - 150, (screenHeight / 2) + 50), Color.White);
                }
                if (currentMenuOption == 4)
                {
                    spriteBatch.DrawString(generalfont, "Single Player", new Vector2(screenWidth - 150, (screenHeight / 2) - 100), Color.White);
                    spriteBatch.DrawString(generalfont, "Multiplayer", new Vector2(screenWidth - 150, (screenHeight / 2) - 50), Color.White);
                    spriteBatch.DrawString(generalfont, "Controls", new Vector2(screenWidth - 150, screenHeight / 2), Color.White);
                    spriteBatch.DrawString(generalfont, "Exit", new Vector2(screenWidth - 150, (screenHeight / 2) + 50), Color.Red);
                }
                spriteBatch.End();
            }
            else if (stateOfGame == GAMESTATE.Controls)
            {
                GraphicsDevice.Clear(Color.Black);
                spriteBatch.Begin();
                //draw controllers background
                spriteBatch.Draw(controlsTexture, new Rectangle(0, 0, screenWidth, screenHeight), Color.White);
                spriteBatch.Draw(menuOutlineTexture[1], new Rectangle(screenWidth - 65, (screenHeight / 2) - 115,
                    menuOutlineTexture[1].Width, menuOutlineTexture[1].Height), Color.White);
                spriteBatch.DrawString(generalfont, "Back", new Vector2(screenWidth - 57, (screenHeight / 2) - 100), Color.Red);
                
                spriteBatch.End();
            }
            else if (stateOfGame == GAMESTATE.Playing)//playing game
            {
                GraphicsDevice.Clear(Color.Black);
                spriteBatch.Begin();

                //draw grid tiles
                for (int i = 0; i < gridWidth; i++)
                    for (int j = 0; j < gridHeight; j++)
                    {
                        Texture2D temp = gameGrid[i, j].objectTexture;
                        spriteBatch.Draw(temp, new Vector2(i * gridTileDimensions.X, j * gridTileDimensions.Y),
                                            new Rectangle(0, 0, temp.Width, temp.Height), Color.White);
                    }

                //displaying time left
                String minutes = "";
                int minutes_numeric = 0;
                String seconds = "";
                int seconds_numeric = (int)(currentTime / 1000);
                int timeLeft_seconds = totalTime - seconds_numeric;

                if (timeLeft_seconds >= 60)
                {
                    minutes_numeric = timeLeft_seconds / 60;
                    timeLeft_seconds = timeLeft_seconds - minutes_numeric * 60;
                    if (minutes_numeric < 10)
                        minutes = "0" + minutes_numeric;
                    else
                        minutes = "" + minutes_numeric;
                }
                if (timeLeft_seconds < 10)
                    seconds = "0" + timeLeft_seconds;
                else
                    seconds = "" + timeLeft_seconds;
                if (minutes.Equals(""))
                    minutes = "00";
                spriteBatch.DrawString(generalfont, "Time Left: 00:" + minutes + ":" + seconds,
                    new Vector2(screenWidth / 2 - 80, screenHeight - 35), Color.Gold);

                Rectangle source_bar;
                Vector2 destination;               

                //draw player1 stats/info 
                if (player1Alive)
                {
                    //game info for playe1
                    source_bar = new Rectangle(0, 0, (int)(healthbarTexture.Width * (player1Obj.getHealthPercentage() / 100)),
                        healthbarTexture.Height);
                    destination = new Vector2(70, screenHeight - 33); // for health-bar

                    //health bar
                    spriteBatch.DrawString(generalfont, "Health: ", new Vector2(10, screenHeight - 35), Color.Red);
                    spriteBatch.Draw(healthbarTexture, destination, new Rectangle(0, 0, healthbarTexture.Width, healthbarTexture.Height),
                        Color.White);
                    spriteBatch.Draw(healthbarTopTexture, destination, source_bar, Color.White);
                    spriteBatch.DrawString(generalfont, player1Obj.getHealthPercentage() + "%", new Vector2(150, screenHeight - 35),
                        Color.Black);

                    //general info
                    spriteBatch.DrawString(generalfont, "Bombs left:" + (2 - bombCount1), new Vector2(10,
                        screenHeight - 55), Color.Red);
                    spriteBatch.DrawString(generalfont, "Score:" + player1Obj.score + "  " + player1Score, new Vector2(150,
                        screenHeight - 55), Color.Red);                    
                }

                //draw player2 stats/info
                if (player2Alive)
                {
                    //game info for player2
                    destination = new Vector2(screenWidth - 230, screenHeight - 33);
                    source_bar = new Rectangle(0, 0, (int)(healthbarTexture.Width * (player2Obj.getHealthPercentage() / 100)), healthbarTexture.Height);
                    //health bar
                    spriteBatch.DrawString(generalfont, "Health: ", new Vector2(screenWidth - 290, screenHeight - 35), Color.Red);
                    spriteBatch.Draw(healthbarTexture, destination, new Rectangle(0, 0, healthbarTexture.Width, healthbarTexture.Height), Color.White);
                    spriteBatch.Draw(healthbarTopTexture, destination, source_bar, Color.White);
                    spriteBatch.DrawString(generalfont, player2Obj.getHealthPercentage() + "%", new Vector2(screenWidth - 150, screenHeight - 35), Color.Black);

                    //general info
                    spriteBatch.DrawString(generalfont, "Bombs left:" + (2 - bombCount2), new Vector2(screenWidth - 290,
                        screenHeight - 55), Color.Red);
                    spriteBatch.DrawString(generalfont, "Score:" + player2Score, new Vector2(screenWidth - 140,
                        screenHeight - 55), Color.Red);                    
                }                              

                //draw all game objects in system
                for (int i = 0; i < objects.Count; i++)
                {
                    if (objects[i] is StationaryObject)
                        ((StationaryObject)objects[i]).Draw(spriteBatch);
                    else if (objects[i] is EnemyObject)
                        ((EnemyObject)objects[i]).Draw(spriteBatch);
                    else if (objects[i] is Player)
                    {
                        if (objects[i].Equals(player1Obj))
                        {
                            ((Player)objects[i]).Draw(spriteBatch);
                            //indicate player1
                            spriteBatch.DrawString(generalfont, "Player1", new Vector2(player1Obj.getPosition().X - 10,
                                player1Obj.getPosition().Y - 10), Color.Green);
                        }
                        else if (objects[i].Equals(player2Obj))
                        {
                            ((Player)objects[i]).Draw(spriteBatch);
                            //indicate player2
                            spriteBatch.DrawString(generalfont, "Player2", new Vector2(player2Obj.getPosition().X - 10,
                                player2Obj.getPosition().Y - 10), Color.Blue);
                        }
                    }
                    else if (objects[i] is BullitObject)
                        ((BullitObject)objects[i]).Draw(spriteBatch);
                    else if (objects[i] is BombObject)
                        ((BombObject)objects[i]).Draw(spriteBatch);
                }
                //draw enemy health bars
                if (enemyObjects != null)
                {
                    for (int i = 0; i < enemyObjects.Count; i++)
                    {
                        EnemyObject testEnemy = enemyObjects[i];
                        // for health-bar
                        source_bar = new Rectangle(0, 0, (int)(enemy_healthbar_top.Width * (testEnemy.getHealthPercentage() / 100)),
                            enemy_healthbar_top.Height);
                        destination = new Vector2(testEnemy.getPosition().X - 9, testEnemy.getPosition().Y - 5);
                        spriteBatch.Draw(enemy_healthbar, destination, new Rectangle(0, 0, enemy_healthbar.Width,
                            enemy_healthbar.Height), Color.White);
                        spriteBatch.Draw(enemy_healthbar_top, destination, source_bar, Color.White);
                    }
                }  

                //draw scores  
                map.Draw(spriteBatch, generalfont);
                spriteBatch.End();
            }
            else if (stateOfGame == GAMESTATE.Paused)//pause game menu
            {
                GraphicsDevice.Clear(Color.Black);
                spriteBatch.Begin();                

                //draw paused menu background and menu option outline to see options
                spriteBatch.Draw(pauseMenuTexture, new Rectangle(0, 0, screenWidth, screenHeight), Color.White);
                spriteBatch.Draw(menuOutlineTexture[2], new Rectangle(screenWidth - 215, (screenHeight / 2) - 115,
                    menuOutlineTexture[2].Width, menuOutlineTexture[2].Height), Color.White);

                //draw menu options
                if (currentMenuOption == 1)
                {
                    spriteBatch.DrawString(generalfont, "Resume Game", new Vector2(screenWidth - 200, screenHeight / 2 - 100), Color.Red);
                    spriteBatch.DrawString(generalfont, "Restart Game", new Vector2(screenWidth - 200, screenHeight / 2 - 50), Color.White);
                    spriteBatch.DrawString(generalfont, "Controls", new Vector2(screenWidth - 200, screenHeight / 2), Color.White);
                    spriteBatch.DrawString(generalfont, "Back to Main Menu", new Vector2(screenWidth - 200, screenHeight / 2 + 50), Color.White);
                    spriteBatch.DrawString(generalfont, "Exit Game", new Vector2(screenWidth - 200, screenHeight / 2 + 100), Color.White);
                }
                else if (currentMenuOption == 2)
                {
                    spriteBatch.DrawString(generalfont, "Resume Game", new Vector2(screenWidth - 200, screenHeight / 2 - 100), Color.White);
                    spriteBatch.DrawString(generalfont, "Restart Game", new Vector2(screenWidth - 200, screenHeight / 2 - 50), Color.Red);
                    spriteBatch.DrawString(generalfont, "Controls", new Vector2(screenWidth - 200, screenHeight / 2), Color.White);
                    spriteBatch.DrawString(generalfont, "Back to Main Menu", new Vector2(screenWidth - 200, screenHeight / 2 + 50), Color.White);
                    spriteBatch.DrawString(generalfont, "Exit Game", new Vector2(screenWidth - 200, screenHeight / 2 + 100), Color.White);
                }
                else if (currentMenuOption == 3)
                {
                    spriteBatch.DrawString(generalfont, "Resume Game", new Vector2(screenWidth - 200, screenHeight / 2 - 100), Color.White);
                    spriteBatch.DrawString(generalfont, "Restart Game", new Vector2(screenWidth - 200, screenHeight / 2 - 50), Color.White);
                    spriteBatch.DrawString(generalfont, "Controls", new Vector2(screenWidth - 200, screenHeight / 2), Color.Red);
                    spriteBatch.DrawString(generalfont, "Back to Main Menu", new Vector2(screenWidth - 200, screenHeight / 2 + 50), Color.White);
                    spriteBatch.DrawString(generalfont, "Exit Game", new Vector2(screenWidth - 200, screenHeight / 2 + 100), Color.White);
                }
                else if (currentMenuOption == 4)
                {
                    spriteBatch.DrawString(generalfont, "Resume Game", new Vector2(screenWidth - 200, screenHeight / 2 - 100), Color.White);
                    spriteBatch.DrawString(generalfont, "Restart Game", new Vector2(screenWidth - 200, screenHeight / 2 - 50), Color.White);
                    spriteBatch.DrawString(generalfont, "Controls", new Vector2(screenWidth - 200, screenHeight / 2), Color.White);
                    spriteBatch.DrawString(generalfont, "Back to Main Menu", new Vector2(screenWidth - 200, screenHeight / 2 + 50), Color.Red);
                    spriteBatch.DrawString(generalfont, "Exit Game", new Vector2(screenWidth - 200, screenHeight / 2 + 100), Color.White);
                }
                else if (currentMenuOption == 5)
                {
                    spriteBatch.DrawString(generalfont, "Resume Game", new Vector2(screenWidth - 200, screenHeight / 2 - 100), Color.White);
                    spriteBatch.DrawString(generalfont, "Restart Game", new Vector2(screenWidth - 200, screenHeight / 2 - 50), Color.White);
                    spriteBatch.DrawString(generalfont, "Controls", new Vector2(screenWidth - 200, screenHeight / 2), Color.White);
                    spriteBatch.DrawString(generalfont, "Back to Main Menu", new Vector2(screenWidth - 200, screenHeight / 2 + 50), Color.White);
                    spriteBatch.DrawString(generalfont, "Exit Game", new Vector2(screenWidth - 200, screenHeight / 2 + 100), Color.Red);
                }
                spriteBatch.End();
            }
            else if (stateOfGame == GAMESTATE.GameOver)//game over screen
            {
                GraphicsDevice.Clear(Color.Black);
                spriteBatch.Begin();

                //win/loose backgrounds
                if (playermode == PLAYERMODE.SinglePlayer)
                {
                    if (player1Score > highestEnemyScore)
                        spriteBatch.Draw(winGametexture, new Rectangle(0, 0, screenWidth, screenHeight), Color.White); //draw win game 
                    else
                        spriteBatch.Draw(looseGameTexture, new Rectangle(0, 0, screenWidth, screenHeight), Color.White);//draw loose game        
                }
                else
                {
                    if (player1Score > player2Score && player1Score > highestEnemyScore)
                    {
                        //draw win background
                        spriteBatch.Draw(winGametexture, new Rectangle(0, 0, screenWidth, screenHeight), Color.White);
                    }
                    else if (player1Score < player2Score && player2Score > highestEnemyScore)
                    {
                        //draw win background
                        spriteBatch.Draw(winGametexture, new Rectangle(0, 0, screenWidth, screenHeight), Color.White);
                    }
                    else
                    {
                        //draw loose game background
                        spriteBatch.Draw(looseGameTexture, new Rectangle(0, 0, screenWidth, screenHeight), Color.White);
                    }
                }
                //draw menu option outline to see options
                spriteBatch.Draw(menuOutlineTexture[3], new Rectangle(screenWidth - 215, (screenHeight / 2) - 115,
                            menuOutlineTexture[3].Width, menuOutlineTexture[3].Height), Color.White);

                //menu options
                if (currentMenuOption == 1)
                {
                    spriteBatch.DrawString(generalfont, "Restart Game", new Vector2(screenWidth - 200, screenHeight / 2 - 100), Color.Red);
                    spriteBatch.DrawString(generalfont, "Controls", new Vector2(screenWidth - 200, screenHeight / 2 - 50), Color.White);
                    spriteBatch.DrawString(generalfont, "Back to Main Menu", new Vector2(screenWidth - 200, screenHeight / 2),
                        Color.White);
                    spriteBatch.DrawString(generalfont, "Exit Game", new Vector2(screenWidth - 200, screenHeight / 2 + 50), Color.White);
                }
                else if (currentMenuOption == 2)
                {
                    spriteBatch.DrawString(generalfont, "Restart Game", new Vector2(screenWidth - 200, screenHeight / 2 - 100), Color.White);
                    spriteBatch.DrawString(generalfont, "Controls", new Vector2(screenWidth - 200, screenHeight / 2 - 50), Color.Red);
                    spriteBatch.DrawString(generalfont, "Back to Main Menu", new Vector2(screenWidth - 200, screenHeight / 2),
                        Color.White);
                    spriteBatch.DrawString(generalfont, "Exit Game", new Vector2(screenWidth - 200, screenHeight / 2 + 50), Color.White);
                }
                else if (currentMenuOption == 3)
                {
                    spriteBatch.DrawString(generalfont, "Restart Game", new Vector2(screenWidth - 200, screenHeight / 2 - 100), Color.White);
                    spriteBatch.DrawString(generalfont, "Controls", new Vector2(screenWidth - 200, screenHeight / 2 - 50), Color.White);
                    spriteBatch.DrawString(generalfont, "Back to Main Menu", new Vector2(screenWidth - 200, screenHeight / 2),
                        Color.Red);
                    spriteBatch.DrawString(generalfont, "Exit Game", new Vector2(screenWidth - 200, screenHeight / 2 + 50), Color.White);
                }
                else if (currentMenuOption == 4)
                {
                    spriteBatch.DrawString(generalfont, "Restart Game", new Vector2(screenWidth - 200, screenHeight / 2 - 100), Color.White);
                    spriteBatch.DrawString(generalfont, "Controls", new Vector2(screenWidth - 200, screenHeight / 2 - 50), Color.White);
                    spriteBatch.DrawString(generalfont, "Back to Main Menu", new Vector2(screenWidth - 200, screenHeight / 2),
                        Color.White);
                    spriteBatch.DrawString(generalfont, "Exit Game", new Vector2(screenWidth - 200, screenHeight / 2 + 50), Color.Red);
                }

                //display scores
                spriteBatch.Draw(menuOutlineTexture[4], new Rectangle(screenWidth/2 - 115, (screenHeight / 2) - 115,
                            menuOutlineTexture[4].Width, menuOutlineTexture[4].Height), Color.White);
                orderScores();
                spriteBatch.End();
            }
            base.Draw(gameTime);
        }

        //orders the scores, highest at the top and displays them
        private void orderScores()
        {
            if (playermode == PLAYERMODE.SinglePlayer)
            {
                spriteBatch.DrawString(generalfont, "Scores:", new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 100), Color.Gold);

                if (player1Score > highestEnemyScore)
                {
                    spriteBatch.DrawString(generalfont, "Player1: " + player1Score, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 50), Color.Gold);
                    spriteBatch.DrawString(generalfont, "Enemy Score: " + highestEnemyScore, new Vector2(screenWidth / 2 - 100,
                            screenHeight / 2 - 20), Color.Gold);
                }
                else
                {                    
                    spriteBatch.DrawString(generalfont, "Enemy Score: " + highestEnemyScore, new Vector2(screenWidth / 2 - 100,
                            screenHeight / 2 - 50), Color.Gold);
                    spriteBatch.DrawString(generalfont, "Player1: " + player1Score, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 20), Color.Gold);
                }                
            }
            else 
            {
                spriteBatch.DrawString(generalfont, "Scores:", new Vector2(screenWidth / 2 - 100, screenHeight / 2 - 100),
                        Color.Gold);

                if (player1Score >= player2Score && player1Score >= highestEnemyScore && player2Score >= highestEnemyScore)
                {
                    spriteBatch.DrawString(generalfont, "Player1: " + player1Score, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 50), Color.Gold);
                    spriteBatch.DrawString(generalfont, "Player2: " + player2Score, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 20), Color.Gold);
                    spriteBatch.DrawString(generalfont, "Enemy Score: " + highestEnemyScore, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 + 10), Color.Gold);
                }
                else if (player1Score >= player2Score && player1Score >= highestEnemyScore && player2Score < highestEnemyScore)
                {
                    spriteBatch.DrawString(generalfont, "Player1: " + player1Score, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 50), Color.Gold);
                    spriteBatch.DrawString(generalfont, "Enemy Score: " + highestEnemyScore, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 20), Color.Gold);
                    spriteBatch.DrawString(generalfont, "Player2: " + player2Score, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 + 10), Color.Gold);                    
                }
                else if (player1Score < player2Score && player1Score >= highestEnemyScore && player1Score >= highestEnemyScore)
                {
                    spriteBatch.DrawString(generalfont, "Player2: " + player2Score, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 50), Color.Gold);
                    spriteBatch.DrawString(generalfont, "Player1: " + player1Score, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 20), Color.Gold);
                    spriteBatch.DrawString(generalfont, "Enemy Score: " + highestEnemyScore, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 + 10), Color.Gold);
                }
                else if (player1Score < player2Score && player1Score < highestEnemyScore && player2Score >= highestEnemyScore)
                {
                    spriteBatch.DrawString(generalfont, "Player2: " + player2Score, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 50), Color.Gold);
                    spriteBatch.DrawString(generalfont, "Enemy Score: " + highestEnemyScore, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 20), Color.Gold);
                    spriteBatch.DrawString(generalfont, "Player1: " + player1Score, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 + 10), Color.Gold);
                }
                else if (player1Score >= player2Score && player1Score < highestEnemyScore && player2Score < highestEnemyScore)
                {
                    spriteBatch.DrawString(generalfont, "Enemy Score: " + highestEnemyScore, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 50), Color.Gold);
                    spriteBatch.DrawString(generalfont, "Player1: " + player1Score, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 20), Color.Gold);
                    spriteBatch.DrawString(generalfont, "Player2: " + player2Score, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 + 10), Color.Gold);                    
                }
                else if (player1Score < player2Score && player1Score < highestEnemyScore && player2Score < highestEnemyScore)
                {
                    spriteBatch.DrawString(generalfont, "Enemy Score: " + highestEnemyScore, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 50), Color.Gold);
                    spriteBatch.DrawString(generalfont, "Player2: " + player2Score, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 - 20), Color.Gold);
                    spriteBatch.DrawString(generalfont, "Player1: " + player1Score, new Vector2(screenWidth / 2 - 100,
                    screenHeight / 2 + 10), Color.Gold);
                }                
            }
        }
    }
}
