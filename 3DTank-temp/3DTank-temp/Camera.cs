//This contains all the camera views and manipulation methods
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
    class Camera
    {
        #region General Variables

        public Vector3 Position; //current camera position
        public Vector3 Target;
        public Matrix View;

        public Matrix camRotation;
        public float yaw, pitch, roll;

        public enum CamMode
        {
            ORBIT = 0,
            CHASE = 1,
            TURRET = 2            
        }
        public CamMode currentCameraMode = CamMode.ORBIT;

        private Tank tankObj;

        #endregion

        #region Chase Cam Variables

        private Vector3 desiredPosition;
        private Vector3 desiredTarget;
        private Vector3 offsetDistance;

        public float offset_y,offset_z; //used to allow user to change pitch and distance of camera from target/viewpoint

        #endregion

        //defualt constructor
        public Camera(Tank t)
        {
            tankObj = t;
            ResetCam();
        }

        //resets camera variables and position
        public void ResetCam()
        {
            Position = new Vector3(0, 0, 50);
            Target = new Vector3();

            yaw = 0;
            roll = 0;
            pitch = 0;

            View = Matrix.Identity;
            camRotation = Matrix.Identity;

            //chase variables
            desiredPosition = Position;
            desiredTarget = Target;

            offset_z = 1300;
            offset_y = 250;
            offsetDistance = new Vector3(0, offset_y, offset_z);
        }

        //used to update all relevant variables for the camera
        public void Update(Matrix targetWorldMatrix)
        {
            handleInput();
            UpdateViewMatrix(targetWorldMatrix);
        }

        //handle user input, with Xbox support
        public void handleInput()
        {
            KeyboardState keyboardState = Keyboard.GetState();
            GamePadState currentGamePadState = GamePad.GetState(PlayerIndex.One);

            //change distance of camera from target
            float triggerAmount = 1;
            if (currentGamePadState.IsConnected)
            {
                triggerAmount = currentGamePadState.Triggers.Left;
                if (offset_z > 500)
                        offset_z -= triggerAmount*50;                

                if(currentGamePadState.Buttons.LeftShoulder == ButtonState.Pressed)
                    offset_z += 50;
            }
            else
            {
                if (keyboardState.IsKeyDown(Keys.O))//zoom in
                {
                    if (offset_z > 500)
                        offset_z -= 50;                    
                }
                if (keyboardState.IsKeyDown(Keys.L))//zoom out
                {
                    offset_z += 50;
                }
            }            

            //change pitch of camera
            triggerAmount = 1;
            if (currentGamePadState.IsConnected)
            {
                triggerAmount = currentGamePadState.Triggers.Right;
                offset_y += triggerAmount * 10;

                if (currentGamePadState.Buttons.RightShoulder == ButtonState.Pressed)
                    if (offset_y > 10)
                        offset_y -= 10.0f;
            }
            else
            {
                if (keyboardState.IsKeyDown(Keys.I))//pitch up
                {
                    offset_y += 10.0f;
                }
                if (keyboardState.IsKeyDown(Keys.K))//pitch down
                {
                    if (offset_y > 10)
                        offset_y -= 10.0f;
                }
            }
        }
       
        //used to determine what view is to be taken update all the relevant variables
        public void UpdateViewMatrix(Matrix targetWorld)
        {
            switch (currentCameraMode)//choose camera view
            {
                case CamMode.ORBIT:
                    yaw += 0.005f;

                    //calculate rotation matrix to be used to rotate cam around target
                    camRotation.Forward.Normalize();
                    camRotation = Matrix.CreateRotationX(pitch) * Matrix.CreateRotationY(yaw) * 
                        Matrix.CreateFromAxisAngle(camRotation.Forward, roll); 

                    offsetDistance = new Vector3(0, offset_y, offset_z);
                    desiredPosition = Vector3.Transform(offsetDistance, camRotation);
                    desiredPosition += targetWorld.Translation;
                    Position = desiredPosition; //update camera's position to new one

                    Target = targetWorld.Translation; // ensures that the camera rotates around the target

                    break;

                case CamMode.CHASE:
                    camRotation.Backward.Normalize();
                    targetWorld.Right.Normalize();
                    targetWorld.Up.Normalize();

                    camRotation = Matrix.CreateFromAxisAngle(camRotation.Forward, roll);

                    //get all the target's positioning varaibles to set the desired position of the camera
                    desiredTarget = targetWorld.Translation;
                    Target = desiredTarget;
                    Target += targetWorld.Right * yaw;
                    Target += targetWorld.Up * pitch;

                    offsetDistance = new Vector3(0,offset_y,offset_z);
                    desiredPosition = Vector3.Transform(offsetDistance, targetWorld);

                    //use smoothstep to create fluid movement of camera/lazi cam
                    Position = Vector3.SmoothStep(Position, desiredPosition, .07f);

                    break;

                case CamMode.TURRET:
                    camRotation.Backward.Normalize();
                    targetWorld.Right.Normalize();
                    targetWorld.Up.Normalize();

                    //36 behind tank position
                    Vector3 camPos = tankObj.Position;
                    camPos.Y += 400;

                    //view down tank forward vector
                    //Vector3 transform = Vector3.Transform(cameraReference, camRotation);
                    Vector3 camView = Vector3.TransformNormal(Vector3.Up,Matrix.CreateFromAxisAngle(Vector3.UnitY,tankObj.TurretRotation));
                    Vector3 normal = Vector3.Cross(Vector3.Up, camView);


                    //use turret rotation matrix and tank right vector to rotate with turret& cannon
                    //camRotation = 
                    Vector3 turretOffset = Vector3.Transform(camPos, camRotation);

                    Position = camPos;
                    Target = camView;

                    break;
            }

            //set view matrix
            View = Matrix.CreateLookAt(Position, Target, Vector3.Up);
        }

    }
}
