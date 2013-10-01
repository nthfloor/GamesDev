//This is the main class which manages the entire environment
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

namespace _3DTank_temp
{
    /// <summary>
    /// This is the main type for your game
    /// </summary>
    public class Game1 : Microsoft.Xna.Framework.Game
    {
        GraphicsDeviceManager graphics;
        SpriteBatch spriteBatch;
        SpriteFont spriteFont;

        KeyboardState currentKeybrd = new KeyboardState();
        GamePadState currentGamePadState = new GamePadState();
        KeyboardState lastKeybrd = new KeyboardState();
        GamePadState lastGamePadState = new GamePadState();

        //camera
        Camera camera;
        Vector3 targetPosition = new Vector3(0, 0, 0);
        float aspectRatio;

        //3D models
        private Model[] ground;
        private Matrix[] groundWorlds;
        private Texture2D groundTex;
        Tank tank;
        //random objects
        private Model[] randomObjects;
        private Matrix[] randomObjWorlds;
        private Texture2D[] randomObjTexs;
        private Random random;
        private const int numRandomObjects = 10;
        private const int numGroundObjects = 1;

        //matrix variables
        private Matrix world = Matrix.CreateTranslation(new Vector3(0, 0, 0));
        private Matrix view = Matrix.CreateLookAt(new Vector3(0, 600, 1300), new Vector3(0, 0, 0), Vector3.UnitY);
        private Matrix projection;

        //constructor
        public Game1()
        {
            graphics = new GraphicsDeviceManager(this);
            graphics.PreferredBackBufferWidth = 853;
            graphics.PreferredBackBufferHeight = 480;
            IsMouseVisible = true;
            Content.RootDirectory = "Content";

            //create tank object
            tank = new Tank();

            //create cameras
            camera = new Camera(tank);
            random = new Random();
        }

        /// <summary>
        /// Allows the game to perform any initialization it needs to before starting to run.
        /// This is where it can query for any required services and load any non-graphic
        /// related content.  Calling base.Initialize will enumerate through any components
        /// and initialize them as well.
        /// </summary>
        protected override void Initialize()
        {
            aspectRatio = graphics.GraphicsDevice.Viewport.Width / (float)graphics.GraphicsDevice.Viewport.Height;
            projection = Matrix.CreatePerspectiveFieldOfView(MathHelper.ToRadians(45), aspectRatio, 1f, 10000f);

            //initialise ground objects
            ground = new Model[numGroundObjects];
            groundWorlds = new Matrix[numGroundObjects];
            for (int i = 0; i < numGroundObjects; i++)
                groundWorlds[i] = Matrix.Identity * Matrix.CreateScale(0.1f) * 
                    Matrix.CreateTranslation(new Vector3(0,0,0)); //scale down for size

            //initialise random object array
            randomObjects = new Model[numRandomObjects];
            randomObjTexs = new Texture2D[numRandomObjects];            

            base.Initialize();

            //initialize random object's world matrices
            //note must be after base.Initialize() so that the randomObjects can be loaded into memory
            randomObjWorlds = new Matrix[numRandomObjects];
            for (int i = 0; i < numRandomObjects; i++)
            {
                randomObjWorlds[i] = RandomlyPlaceObject(randomObjects[i]);                
            }
        }

        //randomly place the supplied model in the environment
        private Matrix RandomlyPlaceObject(Model randomObject)
        {
            int rand_x, rand_z, rand_minus;
            //allow for negatives on the x-axis
            rand_minus = random.Next(0, 2);
            if (rand_minus == 0)
                rand_x = random.Next(5000);
            else
                rand_x = -random.Next(5000);
            //allow for negatives on z-axis
            rand_minus = random.Next(0, 2);
            if (rand_minus == 0)
                rand_z = random.Next(1200, 5000);
            else
                rand_z = -random.Next(1200, 5000);

            //calculate randomized world matrix for provided object
            Matrix temp = Matrix.Identity;
            temp = Matrix.CreateScale(100) * Matrix.CreateRotationX(MathHelper.ToRadians(90.0f)) *
                Matrix.CreateTranslation(new Vector3(rand_x, randomObject.Meshes[0].BoundingSphere.Radius * 100 + 100, rand_z));

            return temp;
        }

        /// <summary>
        /// LoadContent will be called once per game and is the place to load
        /// all of your content.
        /// </summary>
        protected override void LoadContent()
        {
            // Create a new SpriteBatch, which can be used to draw textures.
            spriteBatch = new SpriteBatch(GraphicsDevice);
            spriteFont = Content.Load<SpriteFont>("Arial");

            for (int i = 0; i < numRandomObjects; i++)
            {
                //load random objects
                int temp = random.Next(0, 4);
                if (temp == 0)
                    randomObjects[i] = Content.Load<Model>("Cube");
                else if (temp == 1)
                    randomObjects[i] = Content.Load<Model>("Cone");
                else if (temp == 2)
                    randomObjects[i] = Content.Load<Model>("Cylinder");
                else
                    randomObjects[i] = Content.Load<Model>("SphereHighPoly");

                //load random textures
                temp = random.Next(0, 3);
                if (temp == 0)
                    randomObjTexs[i] = Content.Load<Texture2D>("grassTerrain");
                else
                    randomObjTexs[i] = Content.Load<Texture2D>("water");
            }
            //load ground object and texture
            for (int i = 0; i < numGroundObjects; i++)
                ground[i] = Content.Load<Model>("Ground");
            groundTex = Content.Load<Texture2D>("grassTerrain");
            tank.LoadContent(Content);

        }

        /// <summary>
        /// UnloadContent will be called once per game and is the place to unload
        /// all content.
        /// </summary>
        protected override void UnloadContent()
        {
            // TODO: Unload any non ContentManager content here
        }

        //check for collisions using bounding spheres
        private bool ObjectsCollide(Model object1, Matrix world1, Model object2, Matrix world2)
        {
            for (int index1 = 0; index1 < object1.Meshes.Count; index1++)
            {
                BoundingSphere sphere1 = object1.Meshes[index1].BoundingSphere;
                sphere1 = sphere1.Transform(world1);

                for (int index2 = 0; index2 < object2.Meshes.Count; index2++)
                {
                    BoundingSphere sphere2 = object2.Meshes[index2].BoundingSphere;
                    sphere2 = sphere2.Transform(world2);

                    if (sphere1.Intersects(sphere2))
                        return true;
                }
            }

            return false;
        }

        /// <summary>
        /// Allows the game to run logic such as updating the world,
        /// checking for collisions, gathering input, and playing audio.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Update(GameTime gameTime)
        {
            // Allows the game to exit
            if (GamePad.GetState(PlayerIndex.One).Buttons.Back == ButtonState.Pressed)
                this.Exit();

            handleKeyControls();

            //collision detection
            for (int i = 0; i < numRandomObjects; i++)
            {
                if (ObjectsCollide(tank.tank, tank.World, randomObjects[i], randomObjWorlds[i])) //Collide!
                {
                    tank.RevertPosition();
                }
            }

            //update tank position
            tank.Update(gameTime);

            camera.Update(tank.World);
            view = camera.View;

            base.Update(gameTime);
        }

        int camModeCount = 1;
        bool controls = false;
        private void handleKeyControls()
        {
            currentKeybrd = Keyboard.GetState();
            currentGamePadState = GamePad.GetState(PlayerIndex.One);

            //exit if esc key pressed
            if (currentKeybrd.IsKeyDown(Keys.Escape))
            {
                Exit();
            }

            //toggle camera
            if ((currentKeybrd.IsKeyDown(Keys.R) && lastKeybrd.IsKeyUp(Keys.R)) || ((currentGamePadState.Buttons.A == ButtonState.Pressed) && (lastGamePadState.Buttons.A == ButtonState.Released)))
            {
                bool resetCam = false;
                if (camModeCount == 0)
                    camera.currentCameraMode = Camera.CamMode.ORBIT;
                if (camModeCount == 1)
                    camera.currentCameraMode = Camera.CamMode.CHASE;
                if (camModeCount == 2)
                {
                    camera.currentCameraMode = Camera.CamMode.TURRET;
                    resetCam = true;
                }

                camera.ResetCam();
                if (resetCam)
                    camModeCount = 0;
                else
                    camModeCount++;
            }

            //toggle view of controls
            if ((currentKeybrd.IsKeyDown(Keys.F) && lastKeybrd.IsKeyUp(Keys.F)) || ((currentGamePadState.Buttons.B == ButtonState.Pressed) && (lastGamePadState.Buttons.B == ButtonState.Released)))
            {
                if (controls)
                    controls = false;
                else
                    controls = true;
            }

            lastKeybrd = currentKeybrd;
            lastGamePadState = currentGamePadState;
        }

        /// <summary>
        /// This is called when the game should draw itself.
        /// </summary>
        /// <param name="gameTime">Provides a snapshot of timing values.</param>
        protected override void Draw(GameTime gameTime)
        {
            GraphicsDevice.Clear(Color.CornflowerBlue);

            //draw ground
            for(int i =0;i <numGroundObjects;i++)
                DrawModel(ground[i], groundWorlds[i]);

            tank.Draw(view, projection);

            //draw random objects
            for (int i = 0; i < numRandomObjects; i++)
                DrawModel(randomObjects[i], randomObjWorlds[i], randomObjTexs[i]);

            // draw controls
            if (controls)
            {
                spriteBatch.Begin(SpriteBlendMode.AlphaBlend, SpriteSortMode.Deferred, SaveStateMode.SaveState);
                spriteBatch.DrawString(spriteFont, "Movement: A,W,S,D/Left thumbstick", new Vector2(10, 10), Color.Red);
                spriteBatch.DrawString(spriteFont, "Turret/Cannon: Up,Down,Left,Right/Right Thumbstick", new Vector2(10, 30), Color.Red);
                spriteBatch.DrawString(spriteFont, "Toggle Camera View: R/GamePad - A", new Vector2(10, 50), Color.Red);                
                spriteBatch.DrawString(spriteFont, "Zoom In/Out: O,L/Left Trigger,Left Shoulder", 
                    new Vector2(10, 70), Color.Red);
                spriteBatch.DrawString(spriteFont, "Pitch Up/Down: I,K/Right Trigger,Right Shoulder", 
                    new Vector2(10, 90), Color.Red);
                spriteBatch.DrawString(spriteFont, "View Controls: F/gamepad-B", new Vector2(10, 110), Color.Red);

                spriteBatch.End();
            }

            base.Draw(gameTime);
        }

        //drawing terrain/ground
        private void DrawModel(Model model,Matrix world)
        {
            Matrix[] boneTransforms = new Matrix[model.Bones.Count];
            model.CopyAbsoluteBoneTransformsTo(boneTransforms);

            foreach (ModelMesh mesh in model.Meshes)
            {
                foreach (BasicEffect effect in mesh.Effects)
                {
                    effect.World = boneTransforms[mesh.ParentBone.Index]*world;
                    effect.View = view;
                    effect.Projection = projection;

                    effect.EnableDefaultLighting();
                    establishGameLighting(effect);

                    effect.PreferPerPixelLighting = true;
                    effect.Texture = groundTex;

                    //set fog to match background
                    //effect.FogEnabled = true;
                    //effect.FogColor = Color.CornflowerBlue.ToVector3();
                    //effect.FogStart = 1500;
                    //effect.FogEnd = 6000;
                }
                mesh.Draw();
            }
        }

        //draw objects
        private void DrawModel(Model model, Matrix world, Texture2D texture)
        {
            Matrix[] transforms = new Matrix[model.Bones.Count];
            model.CopyAbsoluteBoneTransformsTo(transforms);

            foreach (ModelMesh mesh in model.Meshes)
            {
                foreach (BasicEffect effect in mesh.Effects)
                {
                    effect.EnableDefaultLighting();
                    establishGameLighting(effect);

                    effect.World = transforms[mesh.ParentBone.Index] * world;
                    effect.Texture = texture;

                    // Use the matrices provided by the chase camera
                    effect.View = view;
                    effect.Projection = projection;

                    //set fog to match background
                    effect.FogEnabled = true;
                    effect.FogColor = Color.CornflowerBlue.ToVector3();
                    effect.FogStart = 1500;
                    effect.FogEnd = 6000;
                }
                mesh.Draw();
            }
        }

        //set lighting for game environment
        private void establishGameLighting(BasicEffect e)
        {
            e.EnableDefaultLighting();

            //light1
            e.DirectionalLight0.DiffuseColor = new Vector3(0.5f, 0, 0);//red light
            e.DirectionalLight0.Direction = new Vector3(1, 0, 0);//along x-axis
            e.DirectionalLight0.SpecularColor = new Vector3(0, 1, 0);//green highlights
            //light2
            e.DirectionalLight1.DiffuseColor = new Vector3(0, 0.5f, 0);//red light
            e.DirectionalLight1.Direction = new Vector3(0, 1, 0);//along y-axis
            e.DirectionalLight1.SpecularColor = new Vector3(0, 0, 1);//blue highlights
            //light3
            e.DirectionalLight2.DiffuseColor = new Vector3(0, 0, 0.5f);//red light
            e.DirectionalLight2.Direction = new Vector3(0, 0, 1);//along z-axis
            e.DirectionalLight2.SpecularColor = new Vector3(1, 0, 0);//red highlights

            e.AmbientLightColor = new Vector3(0.2f, 0.2f, 0.2f);
            e.EmissiveColor = new Vector3(1, 0, 0);

            e.DirectionalLight0.Enabled = true;
            e.DirectionalLight1.Enabled = true;
            e.DirectionalLight2.Enabled = true;
        }
    }
}
