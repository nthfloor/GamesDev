//this class represents any game object
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
    public class GameObject
    {
        public Texture2D objectTexture { get; set; }

        //for sprite sheets
        public int numberOfRows { get; set; }
        public int numberOfColumns { get; set; }

        //for movement
        protected Vector2 oldPosition;
        protected Vector2 position;

        //for when object gets destroyed
        private bool markedForDetruction = false;

        public GameObject(Texture2D texture,int rows,int cols,int x,int y)
        {
            objectTexture = texture;
            numberOfRows = rows;
            numberOfColumns = cols;
            position.X = x;
            position.Y = y;
        }

        //get bounding rectangle of object
        public Rectangle getBoundingRectangle()
        {
            Rectangle boundingRec = new Rectangle((int)position.X+5,(int)position.Y+7,objectTexture.Width/numberOfColumns-4,
                objectTexture.Height/numberOfRows-3);
            return boundingRec;
        }

        //get texture's pixel data
        public Color[] getTextureData()
        {
            Color[] tempData = new Color[objectTexture.Width * objectTexture.Height];
            objectTexture.GetData(tempData);
            return tempData;
        }

        //return object's texture
        public Texture2D getTexture()
        {
            return objectTexture;
        }

        //changes texture of object
        public void setTexture(Texture2D tex)
        {
            objectTexture = tex;
        }

        /**
        * Sets the position of the object. Objects will be drawn with their center at this position
        */
        public void incrementPosition(Vector2 p)
        {
            position.X += p.X;
            position.Y += p.Y;
        }        

        // sets position of object
        public void setPosition(Vector2 point)
        {
            position.X = point.X;
            position.Y = point.Y;
        }

        //gets position of object
        public Vector2 getPosition()
        {
            return new Vector2(position.X, position.Y);
        }

        //calc distance to target object from current object
        public double getDistanceTo(GameObject obj)
        {
            float a = obj.getPosition().X - this.position.X;
            float b = obj.getPosition().Y - this.position.Y;
            double distance = Math.Sqrt(a * a + b * b);

            return distance / 2;
        }

        //marks object for destruction at end of game loop
        public void setMarkedForDestruction(bool m)
        {
            markedForDetruction = m;
        }

        //check if object needs to be destroyed
        public bool isMarkedForDestruction()
        {
            return markedForDetruction;
        } 
    }
}
