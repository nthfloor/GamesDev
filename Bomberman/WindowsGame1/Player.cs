//this class represents each player object
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
    public class Player : GameObject
    {
        private int totalFrames;
        private int currentrow = 1;
        private int currentCol = 1;
        private float direction;
        public int cooldownTimer { get; set; }
        private int ammoLeft = 50;
        public int healthLeft {get; set;}
        public int totalHealth { get; set; }
        public int score { get; set; }
        public String playerID { get; set; }

        //allows for variation of speed
        private int speedFactor;

        public Player(Texture2D texture, int rows, int columns,int x, int y, int health)
            : base(texture,rows,columns ,x, y)
        {
            speedFactor = 2;
            cooldownTimer = 0;
            score = 0;
            totalFrames = numberOfRows * numberOfColumns;
            totalHealth = health;
            healthLeft = health;
        }

        //returns amount of health left as percentage
        public float getHealthPercentage()
        {
            return ((float)healthLeft / (float)totalHealth)*100;
        }

        //checks if object is dead
        public void checkHealth(GameObject obj)
        {
            //check if health is finished
            if (healthLeft <= 0)
            {
                this.setMarkedForDestruction(true);
                healthLeft = 0;

                //get points for destroying an enemy/player object
                if (obj is BombObject)
                {
                    BombObject bombObj = (BombObject)obj;
                    if (bombObj.source is Player)
                    {
                        if (bombObj.source.Equals(this))
                            GameMap.tempscores.Add(this.playerID + " committed suicide.");
                        else
                        {
                            ((Player)(bombObj).source).score += 15;
                            GameMap.tempscores.Add(((Player)bombObj.source).playerID + ": +15");//(killed an enemy with a bomb!)
                            GameMap.tempscores.Add(this.playerID + " was blown up.");
                        }
                    }
                }
                else if(obj is BullitObject)
                {                    
                    if (((BullitObject)obj).getSource()is Player)
                    {
                        Player temp = (Player)((BullitObject)obj).getSource();
                        temp.score += 10;
                        GameMap.tempscores.Add(temp.playerID + ": +10");//(killed an enemy with bulltis!)
                        GameMap.tempscores.Add(this.playerID + " was shot.");
                    }
                }
            }
        }

        //allows you to change the speed of the object
        protected void setSpeed(int s)
        {
            speedFactor = s;
        }

        public int getAmmo()
        {
            return ammoLeft;
        }

        public void decreaseAmmo()
        { ammoLeft--; }
        //enables sprint feature
        public void setSprinting(bool sprinting)
        {
            if (sprinting)
                speedFactor = 4;
            else
                speedFactor = 2;
        }
        
        //for movement
        //also sets row for animated figure
        public void setDirection(float dir)
        {
            while (dir < 0) dir += 360;
            while (dir >= 360) dir -= 360;
            direction = dir;

            if (dir > 315 || dir <= 45)//up
                currentrow = 4;
            else if (dir > 225 && dir <= 315)//left
                currentrow = 2;
            else if (dir > 135 && dir <= 225)//down
                currentrow = 1;
            else if (dir > 45 && dir <= 135)//right
                currentrow = 3;            
        }

        public float getDirection()
        {
            return direction;
        }

        public void moveInDirection(float dir)
        {
            oldPosition = this.getPosition();            

            incrementPosition(new Vector2((float)Math.Sin(MathHelper.ToRadians(dir)) * speedFactor, 
                -(float)Math.Cos(MathHelper.ToRadians(dir)) * speedFactor));
            setDirection(dir);
        }

        //if collision with another object or edge of screen
        public void revertPosition()
        {
            this.setPosition(oldPosition);
        }

        int cnt = 0;
        public void update()
        {
            cnt++;
            //changes to next frame to simulate movement
            if (cnt >= 10)
            {
                currentCol++;
                if (currentCol > 4)
                    currentCol = 1;
                cnt = 0;
            }
        }

        //only draws the part of the texture atlas that we want or which is the current frame
        public void Draw(SpriteBatch spriteBatch)
        {
            int width = objectTexture.Width / numberOfColumns;
            int height = objectTexture.Height / numberOfRows;
            Rectangle source = new Rectangle(width * (currentCol - 1), height * (currentrow - 1), width, height);
            
            spriteBatch.Draw(objectTexture, position, source, Color.White);
        }
    }
}
