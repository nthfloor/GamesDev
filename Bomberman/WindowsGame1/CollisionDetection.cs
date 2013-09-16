//this class manages the collision detection of the game
//caters for bounding box and pixel tests
//Nathan Floor
//FLRNAT001

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.Xna.Framework;
using Microsoft.Xna.Framework.Graphics;

namespace WindowsGame1
{
    class CollisionDetection
    {
        public CollisionDetection()
        {
            //do nothing
        }

        //check if there is a collision between two gameGrid objects
        public bool isCollision(GameObject o1,GameObject o2)
        {
            if (checkBoundingBoxes(o1, o2))
                //if (checkPixelCollision(o1.getBoundingRectangle(), o1.getTextureData(), o2.getBoundingRectangle(), o2.getTextureData()))
                    return true;

            return false;
        }

        //do boundingbox test
        public bool checkBoundingBoxes(GameObject obj1,GameObject obj2)
        {
            //get bounding boxes
            Rectangle temp1 = obj1.getBoundingRectangle();
            Rectangle temp2 = obj2.getBoundingRectangle();

            //check collision
            if (temp1.Intersects(temp2))
                return true;
            else
                return false;
        }

        //check for pixel perfect collisions
        public bool checkPixelCollision(Rectangle rectangle1, Color[] data1,Rectangle rectangle2, Color[] data2)
        {
            //find bounds of intersection rectangle
            int top = Math.Max(rectangle1.Top, rectangle2.Top);
            int bottom = Math.Min(rectangle1.Bottom, rectangle2.Bottom);
            int left = Math.Max(rectangle1.Left, rectangle2.Left);
            int right = Math.Min(rectangle1.Right, rectangle2.Right);

            //check every pixel point in intersection rectangle
            for (int y = top; y < bottom; y++)
            {
                for (int x = left; x < right; x++)
                {
                    //retrieve color of both pixels at this point
                    Color pixel1 = data1[(x - rectangle1.Left) + (y - rectangle1.Top) * rectangle1.Width];
                    Color pixel2 = data2[(x - rectangle2.Left) + (y - rectangle2.Top) * rectangle2.Width];

                    //check if both pixels are not transparent
                    if (pixel1.A != 0 && pixel2.A != 0)
                        return true; //collision!!!
                }
            }

            //no intersection found
            return false;
        }

    }
}
