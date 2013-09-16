Nathan Floor (FLRNAT001)

This is the Collision Detection Hand-in for the 2D Game Project

I have implemented pixel perfect collision detection which is only tested after the bounding boxes collide. I have not changed the bounding box collision detection, the same code provided with the game was used for this test. After which I implmented a grid spatial structure which uses a 2D array of vectors to track the objects position on the screen(grid).

I have created three new classes:

1) CollisionDetector.java - coordinates all collision methodologies
2) GridStructure.java 	  - contains all the methods and variables for managing the grid
3) GridGameObject.java 	  - extends GameObject.java simply to help facilitate grid operations

Some minor changes were made in the initstep() and logicstep() methods and the code for the bounding box tests was moved to the CollisionDetector class. 

Note that Most of the more specific details concerning the algorithms used and changes made will be outlined in comments throughout the code

