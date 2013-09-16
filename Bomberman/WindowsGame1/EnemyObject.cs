//class represents an enemy object
//exetends player object to inherit functionality
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
    public class EnemyObject: Player
    {
        public bool canMove { get; set; }
        public int bombCount { get; set; }
        public List<BombObject> bombsLayed;
        public int bombLayingDelay { get; set; }
        public int firingDelay { get; set; }
        public int samePathCounter { get; set; }
        public bool startedMoving { get; set; }

        public EnemyObject(Texture2D texture, int rows, int columns, int x, int y, int health)
            : base(texture, rows, columns, x, y,health)
        {
            bombCount = 0;
            bombLayingDelay = 0;
            firingDelay = 0;
            this.setSpeed(3);
            samePathCounter = 0;            
            startedMoving = false;
            bombsLayed = new List<BombObject>();
        }

        //returns direction from this object to supplied object
        public int getDirectionTo(Vector2 target)
        {
            int temp_dir = 0;

            //gets the angle between target and this object
            float degreesTo = MathHelper.ToDegrees((float)Math.Atan2(target.Y - this.position.Y, target.X - this.position.X));

            //determine direction that this object needs to travel to get to target
            temp_dir = (int)degreesTo + 90; 

            return temp_dir;
        }
    }
}
