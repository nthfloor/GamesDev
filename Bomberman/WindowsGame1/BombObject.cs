//this class represents a bobm object
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
    public class BombObject : GameObject
    {
        public float scaleFactor { get; set; }
        private int cooldownTimer;

        //for the explosion animation
        private Texture2D explosionTexture;
        private Texture2D oldTexture;
        private int explosionCounter = 21;
        private int currentrow = 1;
        private int currentcol = 1;
        public GameObject source { get; set; }

        //defualt constructor
        public BombObject(Texture2D texture, Texture2D explosion,int rows, int columns, int x, int y,float scale)
            : base(texture, rows, columns, x, y)
        {
            explosionTexture = explosion;
            scaleFactor = scale;
            cooldownTimer = 100;
        }

        //destroys bomb instance after certain cool-down time
        int count = 0;
        bool animationStarted = true;
        public bool animationCanStart { get; set; }
        public void update()
        {
            if (cooldownTimer > 0)
                cooldownTimer--;
            else
                setMarkedForDestruction(true);
                           
            if(animationCanStart)
            {
                if (animationStarted)
                {
                    //start explosion animation 
                    oldTexture = this.getTexture();
                    this.setTexture(explosionTexture);
                    this.numberOfColumns = 5;
                    this.numberOfRows = 5;
                    scaleFactor = 2.5f;
                    animationStarted = false;                    
                }

                count++;    
                if (count >= 3)
                {
                    //for explosion animation
                    if (explosionCounter > 0)
                    {
                        currentcol++;
                        if (currentcol > 3)
                        {
                            currentrow++;
                            currentcol = 1;
                        }
                        explosionCounter--;
                    }
                    else
                    {
                        //once animation is finished
                        MainGameClass.objects.Remove(this);
                        int xcoord = (int)((this.getPosition().X + oldTexture.Width / 2) / 48 - 1);
                        int ycoord = (int)(((this.getPosition().Y + oldTexture.Height / 2) / 48));
                    }
                    count = 0;
                }
            }
        }

        //draws texture at player's position
        public void Draw(SpriteBatch spriteBatch)
        {
            int width = objectTexture.Width / numberOfColumns;
            int height = objectTexture.Height / numberOfRows;
            Rectangle source = new Rectangle(width * (currentcol - 1), height * (currentrow - 1), width, height);
            Vector2 destination;

            //adjusts for the explosion's animation texture(using sprite sheet)
            if (animationCanStart)
                destination = new Vector2(position.X - this.explosionTexture.Width / 8 - 30, position.Y - this.explosionTexture.Height / 6 - 10);
            else
                destination = position;

            spriteBatch.Draw(objectTexture, destination, source, Color.White,0f,Vector2.Zero,scaleFactor,SpriteEffects.None,0f);
        }
    }
}
