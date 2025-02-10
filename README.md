# WallBallWonder

The author of Wall Ball Wonder Sketch is University of Galway (2024)

# Collision Detection Java program's description

The program asks user about a number of balls to create and then create this many balls. The user's input is checked to make sure it is a positive integer. If it's not, user is asked to enter positive integer. 

The sizes ans speed of the balls are chosen randomly. To make sure the balls are not too big or too small, I specified bounds between 30 and 99. 

As for the speed, the value of xVelocity designates not only direction (- or +), but also speed i.e., for how many pixels a ball is moved at a time. Thus, I made the program to choose the value within the bounds 1 and 5.

Each ball moves independently in its own direction and has own colour. Each ball has its own thread.

The balls bounce off the walls and each other when collision occurs i.e., 

when the radius of a ball is less than distance from its centre to a wall and when the distance between centres of two balls is less than the sum of their radii. 

When the collision detected, the program swaps directions and speed of two balls so they start moving away. 

Although it is not mathematically accurate as it does not consider balls’ mass and vectors, it is easy to implement and gives expected result. 
