//this class represents any stationary objects 
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
    class StationaryObject : GameObject
    {
        public enum PILLER {CAN_DESTROY, CANNOT_DESTROY}; //different types of pillers/blocks
        private PILLER state;

        public StationaryObject(Texture2D texture, int r, int c, int x, int y,PILLER s)
            : base(texture, r, c, x, y)
        {
            state = s;
        }

        //returns the type of block/piller
        public PILLER getState()
        {
            return state;
        }

        //only draws the part of the texture atlas that we want or which is the current frame
        public void Draw(SpriteBatch spriteBatch)
        {
            int width = objectTexture.Width / numberOfColumns;
            int height = objectTexture.Height / numberOfRows;
            Rectangle source = new Rectangle(0, 0, width, height);
            spriteBatch.Draw(objectTexture, position, source, Color.White);
        }
    }
}
