# TigerZone - CEN3031 - TeamR
  **Martakia** - Arturo Bustamente
  
  **mondily** - Amanda Morales
  
  **konradpabjan** - Konrad Pabjan
  
  **ana16** - Ana Jelacic
  
  **itismynamenow** - George
  
  **vpeixoto1** - Vanessa Peixoto
  
 
  
---  

## To run the TigerZone Client using an external server (Tournament):

#### COMPILE the client using:

>  javac Card.java Coordinates.java RotatedIcon.java PlacementPossibility.java
>   PlayerMoveInformation.java TigerInformation.java Deck.java Log.java Board.java
>   Player.java Game.java main.java

#### OR just run the command:

>  make
 
#### RUN the Client using:

>  java main < IP address > < port number> < tournament password > < username > < password >
  
---

## To run the TigerZone Client using localhost server (Testing & Debugging):

#### COMPILE the client using the same method as above ^ AND open a second command prompt to compile the following:

>  javac ServerAuthProtocol.java
 
#### RUN the Client using main in the original command prompt and the Server in the second command prompt:

>  java main localhost 4444 a b c

>  java GameServer 4444

---

## To run the TigerZone Client with a GUI and/or Debug mode:

#### Change lines 10 and 11 inside main.java

  Change the *booleans* TURN_ON_GUI and DEBUG_MODE from false to true. Changing the first will turn on the GUI and you will be able to see each game that the AI plays updated in real time. Changing the DEBUG_MODE to true will allow the user to put mock server commands through the client.
  
  Optionally, you can change the delay boolean to increase the amount of time in which you see the GUI in between moves.
  
#### Then follow the same instructions as listed above ^ to compile and run
  
  *WARNING: The GUI will appear distorted unless it runs with a very high resolution.*

---

## Acceptance Testing:

For acceptance testing, we tested our AI against the Server created by the Server Team. The information sent between the client and the server was loggend into files and checked for discrepancies. Each time the client is ran, the log is saves as LogFile.txt and overwrites any old log file, so it is important to backup any file you may want to save. Included is a folder of some of the log files that were created when testing the client and helping the server team debug their server. 

Additionally, we set up a local mock server called GameServer.java (localhost) in which we would generate a random deck of 6 tiles and our AI would play itself. With the output, we manually checked that everything was logically correct (meeple placement, tile placement, etc). To run this test, follow the directions under the section "To run the TigerZone Client using localhost server (Testing & Debugging)"; which is further up this page.

Additionally the version of the game located in the folder named "TZ version with GUI but without TCP" is an earlier build which was used for debugging new methods and algorithms, and has a different GUI that a user can interact with to play the game, or have AIs play the game. This is expanded further in the "TZ version with GUI but without TCP" section. This project is buildable in eclipse using the  "using project from filesystem" option. The key commands are as followed:

R - rotates card
C - allows to skip tiger placement
W, A, S, D - move viewport
Left mouse click to plave card.
Left mouse click on placed card will output information about this tile as terrain types 3 by 3 matrix that indicates which tile sector is owned by which player and some other more specific debug data.

The only issues with this version are that two games can be run simultaneously but only one can be rendered at a time and some redering artifacts are present due to thread synchronization issues. Otherwise the board the board that is presented is correct. Because this code was done mostly for testing purposes, it has memory leaks and may have some stability issues when run for a long time. 

The main thing we lack in this build that we were not able to implement/test was tracking scoring, which would have improved our AI's scoring. We were also unable to transfer the Tiger placement algorithms into the final build of the project without debugging it in time for turning in the project, but it can be found in the "TZ version with GUI but without TCP" version.

---

## User Stories:

We used Pivotal Tracker to write down our user stories and keep track of the progress done to complete them. A link to the Pivotal Tracker is [here](https://www.pivotaltracker.com/n/projects/1914539).

## Architecture/Design

NOTE: All architecture/design related documentation can be found in the designDocs folder
