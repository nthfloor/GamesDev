//this class contains the main method which starts the game
//Nathan Floor
//FLRNAT001

using System;

namespace WindowsGame1
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main(string[] args)
        {
            using (MainGameClass game = new MainGameClass())
            {
                game.Run();
            }
        }
    }
}

