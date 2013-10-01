//This manages the tank object and movement
//Nathan Floor
//FLRNAT001

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;
using Microsoft.Xna.Framework.Input;
using Microsoft.Xna.Framework.Content;

namespace _3DTank_temp
{
    class Tank
    {
        #region Constant Variables

        private const float maxVelocity = 7;
        private const float wheelRadius = 18;
        private const float turnSpeed = 0.025f;
        private const float mass = 1.0f;
        private const float thrustForce = 24000.0f; //max force applied in tank direction
        private const float dragFactor = 0.97f; //to approximate drag  

        #endregion

        #region Variables

        //models and textures
        public Model tank;

        private Vector3 position;//pos of tank
        public Vector3 Position
        {
            get { return position; }
        }
        private Vector3 oldPosition;

        private Vector3 direction;//direction vector tank 
        public Vector3 Direction
        {
            get { return direction; }
        }
        public float FacingDirection//direction tank in radians
        {
            get { return facingDirection; }
        }
        private float facingDirection;

        public Vector3 Up;
        public Vector3 velocity;
        private Matrix orientation = Matrix.Identity;    

        //tank's world transform. matrix
        private Matrix world;
        public Matrix World
        {
            get { return world; }
        }
        private Matrix wheelRotation = Matrix.Identity; //calc based on dist moved

        //variables used for animations
        ModelBone leftBackWheel;
        ModelBone rightBackWheel;
        ModelBone leftFrontWheel;
        ModelBone rightFrontWheel;
        ModelBone leftSteer;
        ModelBone rightSteer;
        ModelBone turretBone;
        ModelBone cannonBone;
        //transform matrices
        Matrix leftBackWheelTrans;
        Matrix rightBackWheelTrans;
        Matrix leftFrontWheelTrans;
        Matrix rightFrontWheelTrans;
        Matrix leftSteerTrans;
        Matrix rightSteerTrans;
        Matrix turretTrans;
        Matrix cannonTrans;

        Matrix[] boneTransforms; //array holding all bone transforms

        // Current animation positions.
        float wheelRotationValue;
        float steerRotationValue;
        float turretRotationValue;
        float cannonRotationValue;

        //used for turret,wheel and cannon animations
        float turretRot = 0;
        float cannonElevation = 0.005f;
        float wheelRot = 0;
        float steerRot = 0;

        #endregion

        #region Animation Properties

        public float WheelRotation
        {
            get { return wheelRotationValue; }
            set { wheelRotationValue = value; }
        }

        public float SteerRotation
        {
            get { return steerRotationValue; }
            set { steerRotationValue = value; }
        }

        public float TurretRotation
        {
            get { return turretRotationValue; }
            set { turretRotationValue = value; }
        }

        public float CannonRotation
        {
            get { return cannonRotationValue; }
            set { cannonRotationValue = value; }
        }

        #endregion

        //default constructor
        public Tank()
        {
            ResetPosition();
        }

        //resets tank to starting position/state
        public void ResetPosition()
        {
            position = new Vector3(0, 0, 0);
            oldPosition = position;
            direction = new Vector3(0,0,0);
            Up = Vector3.Up;
            velocity = Vector3.Zero;
        }

        //load the tank and look-up animation models for animations
        public void LoadContent(ContentManager content)
        {
            tank = content.Load<Model>("tank");

            //lookup bones which control wheels
            // Look up shortcut references to the bones we are going to animate.
            leftBackWheel = tank.Bones["l_back_wheel_geo"];
            rightBackWheel = tank.Bones["r_back_wheel_geo"];
            leftFrontWheel = tank.Bones["l_front_wheel_geo"];
            rightFrontWheel = tank.Bones["r_front_wheel_geo"];
            leftSteer = tank.Bones["l_steer_geo"];
            rightSteer = tank.Bones["r_steer_geo"];
            turretBone = tank.Bones["turret_geo"];
            cannonBone = tank.Bones["canon_geo"];

            // Store the original transform matrix for each animating bone.
            leftBackWheelTrans = leftBackWheel.Transform;
            rightBackWheelTrans = rightBackWheel.Transform;
            leftFrontWheelTrans = leftFrontWheel.Transform;
            rightFrontWheelTrans = rightFrontWheel.Transform;
            leftSteerTrans = leftSteer.Transform;
            rightSteerTrans = rightSteer.Transform;
            turretTrans = turretBone.Transform;
            cannonTrans = cannonBone.Transform;

            // Allocate the transform matrix array.
            boneTransforms = new Matrix[tank.Bones.Count];
        }

        float time;
        public void Update(GameTime gameTime)
        {
            float elapsed = (float)gameTime.ElapsedGameTime.TotalSeconds;
            time = (float)gameTime.TotalGameTime.TotalSeconds;
            handleKeyControls();

            // Update all the animation properties
            WheelRotation = wheelRot * 5;
            SteerRotation = (float)Math.Sin(steerRot * 0.75f) * 0.5f;
            TurretRotation = turretRot * 0.333f * 1.25f;
            CannonRotation = cannonElevation * 0.25f * 0.333f - 0.333f;
        }

        public void RevertPosition()
        {
            position = oldPosition;
        }

        //handle keyboard and xbox controls
        public void handleKeyControls()
        {
            KeyboardState currentKeyboardState = Keyboard.GetState();
            GamePadState currentGamePadState = GamePad.GetState(PlayerIndex.One);            

            //turn tank object and enable steering animation
            float turnAmount = 0;
            Vector3 movement = Vector3.Zero;

            //check for xbox input
            if (currentGamePadState.IsConnected)
            {
                turnAmount = -currentGamePadState.ThumbSticks.Left.X;
                steerRot += -currentGamePadState.ThumbSticks.Left.X;
                movement.Z = currentGamePadState.ThumbSticks.Left.Y;
                wheelRot -= movement.Z * 5;

                turretRot += -currentGamePadState.ThumbSticks.Right.X;
                cannonElevation += -currentGamePadState.ThumbSticks.Right.Y;
            }

            steerRot += -currentGamePadState.ThumbSticks.Left.X;
            bool turning = false;
            if (currentKeyboardState.IsKeyDown(Keys.A))//left
            {
                turnAmount += 1;
                steerRot += 0.1f;
                turning = true;
            }
            if (currentKeyboardState.IsKeyDown(Keys.D))//right
            {
                turnAmount -= 1;
                steerRot -= 0.1f;
                turning = true;
            }

            steerRot = MathHelper.Clamp(steerRot, -1, 1);            
            turnAmount = MathHelper.Clamp(turnAmount, -1, 1);
            facingDirection += turnAmount * turnSpeed;

            // move tank forward/back  
            //movement.Z = currentGamePadState.ThumbSticks.Left.Y;
            if (currentKeyboardState.IsKeyDown(Keys.W))//forward
            {
                movement.Z = 1;
                wheelRot -= 5;
                if(! turning)
                    steerRot = 0;
            }
            if (currentKeyboardState.IsKeyDown(Keys.S))//back
            {
                movement.Z = -1;
                wheelRot += 5;
                if (! turning)
                    steerRot = 0;
            }

            // create a rotation matrix and use it to transform the velocity vector.
            orientation = Matrix.CreateRotationY(FacingDirection);
            Vector3 velocity = Vector3.Transform(movement, orientation);
            velocity *= maxVelocity;
            Vector3 newPos = Position + velocity;            

            orientation.Up = Vector3.Up;
            orientation.Right = Vector3.Cross(orientation.Forward, orientation.Up);
            orientation.Right = Vector3.Normalize(orientation.Right);
            orientation.Forward = Vector3.Cross(orientation.Up, orientation.Right);
            orientation.Forward = Vector3.Normalize(orientation.Forward);

            //update direction vector for tank
            direction += movement;

            //for wheel animation
            float distanceMoved = Vector3.Distance(Position, newPos);
            float theta = distanceMoved / wheelRadius;
            int rollDirection = movement.Z > 0 ? 1 : -1;
            wheelRotationValue = theta * rollDirection;

            //update tank position
            oldPosition = position;
            position = newPos;            
            world = Matrix.Identity;
            world = -Matrix.CreateRotationY(facingDirection);
            world.Up = Up;
            world.Right = Vector3.Right;
            world.Translation = Position;            

            //aiming turret and cannon
            //turretRot = -currentGamePadState.ThumbSticks.Left.X;
            if (currentKeyboardState.IsKeyDown(Keys.Left))//rotate left
            {
                turretRot += 0.1f;
            }
            if (currentKeyboardState.IsKeyDown(Keys.Right))//rotate right
            {
                turretRot -= 0.1f;
            }
            //cannonElevation = -currentGamePadState.ThumbSticks.Left.Y;
            if (currentKeyboardState.IsKeyDown(Keys.Up))//pitch up
            {
                cannonElevation -= 0.1f;
            }
            if (currentKeyboardState.IsKeyDown(Keys.Down))//pitch down
            {
                cannonElevation += 0.1f;
            }
            cannonElevation = MathHelper.Clamp(cannonElevation, -5, 5);
            
        }

        //draw method
        public void Draw(Matrix viewMatrix,Matrix projectionMatrix)
        {
            // Set the world matrix as the root transform of the model.
            Matrix worldMatrix = orientation * Matrix.CreateTranslation(position);

            // Calculate matrices based on the current animation position.
            Matrix wheelRotation = Matrix.CreateRotationX(wheelRotationValue);
            Matrix steerRotation = Matrix.CreateRotationY(steerRotationValue);
            Matrix turretRotation = Matrix.CreateRotationY(turretRotationValue);
            Matrix cannonRotation = Matrix.CreateRotationX(cannonRotationValue);

            // Apply matrices to the relevant bones.
            leftBackWheel.Transform = wheelRotation * leftBackWheelTrans;
            rightBackWheel.Transform = wheelRotation * rightBackWheelTrans;
            leftFrontWheel.Transform = wheelRotation * leftFrontWheelTrans;
            rightFrontWheel.Transform = wheelRotation * rightFrontWheelTrans;
            leftSteer.Transform = steerRotation * leftSteerTrans;
            rightSteer.Transform = steerRotation * rightSteerTrans;
            turretBone.Transform = turretRotation * turretTrans;
            cannonBone.Transform = cannonRotation * cannonTrans;

            // Look up combined bone matrices for the entire model.
            tank.CopyAbsoluteBoneTransformsTo(boneTransforms);            

            foreach (ModelMesh mesh in tank.Meshes)
            {
                foreach (BasicEffect effect in mesh.Effects)
                {
                    effect.World = boneTransforms[mesh.ParentBone.Index] * worldMatrix;
                    effect.View = viewMatrix;
                    effect.Projection = projectionMatrix;

                    effect.EnableDefaultLighting();
                    effect.PreferPerPixelLighting = true;

                    // Set the fog to match the black background color
                    effect.FogEnabled = true;
                    effect.FogColor = Color.CornflowerBlue.ToVector3();
                    effect.FogStart = 2500;
                    effect.FogEnd = 5000;
                }
                mesh.Draw();
            }
        }
    }
}
