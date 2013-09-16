//this class represents a bullet object
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
    class BullitObject : GameObject
    {
        private int lifeSpan;
        private int speedFactor;
        private float direction;
        private GameObject sourceObj;

        public BullitObject(Texture2D texture, int rows, int columns, int x, int y,GameObject source)
            : base(texture, rows, columns, x, y)
        {
            //defualt constructor
            lifeSpan = 50;
            speedFactor = 7;
            sourceObj = source;

            //sets direction of bullit
            direction = ((Player)source).getDirection();
        }

        //moves bullet along current direction
        public void fireInDirection()
        {
            incrementPosition(new Vector2((float)Math.Sin(MathHelper.ToRadians(direction)) * speedFactor,
                -(float)Math.Cos(MathHelper.ToRadians(direction)) * speedFactor));
        }

        //returns source of bullet
        public GameObject getSource()
        {
            return sourceObj;
        }

        public void update()
        {
            //destroys bullit object after a while
            if (lifeSpan > 0)
            {
                //update bullit position
                fireInDirection();
                lifeSpan--;
            }
            else
                setMarkedForDestruction(true);
        }

        //draws texture at player's position, and as it moves
        public void Draw(SpriteBatch spriteBatch)
        {
            int width = objectTexture.Width / numberOfColumns;
            int height = objectTexture.Height / numberOfRows;
            Rectangle source = new Rectangle(0, 0, width, height);

            spriteBatch.Draw(objectTexture, position, source, Color.White);
        }
    }
}
