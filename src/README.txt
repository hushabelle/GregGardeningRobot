README

Gregg the farmer
=================

## Project description

Gregg the farmer maps his bounding boundary, spirals within the boundary whilst continuously checking for a brightly coloured flower. When a flower is spotted, he takes his current pose and stores it into a waypoint. When the spiral flower mapping is done, he then visits each stored waypoint and turns his arm to water the flower. After all waypoints being visited, he returns to his starting position.

## Features

Gregg.java - Main class of Gregg containing the main method. Initialises all shared objects.

MapEnvironment.java - Maps the bounding boundary Gregg is placed in and stores its width and length.

MapFlowers.java - Spirals within mapped boundary to detect brightly coloured flowers. Stores each pose into an array.

Move.java - Receives the pose array from MapFlowers and creates waypoints. Navigates to each waypoint.

ColourCheck.java - Monitors the change in colour in the environment, looks for brightly coloured flowers. 

DistanceCheck.java - Continuously samples the distance from the ultrasonic sensor to obstacles.

LowBattery.java - Detects when the robot is on a low charge level, alerts on the LCD and shuts down.

PlaySound.java - Displays an emoticon on the LCD and plays sound file "sadasf.wav" when its touch sensor is pressed.

EmergencyExit.java - Ends the program at any point in time when the grey interrupt button is pressed.
