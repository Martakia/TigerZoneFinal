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

## To run the TigerZone Client with a GUI:

#### Change lines 10 and 11 inside main.java

  Change the *booleans* TURN_ON_GUI and DEBUG_MODE from false to true.
  
  Optionally, you can change the delay boolean to increase the amount of time in which you see the GUI in between moves.
  
#### Then follow the same instructions as listed above ^
  
  *WARNING: The GUI will appear distorted unless it runs with a very high resolution.*

---

## Acceptance Testing:

For acceptance testing, we tested our AI (Player.java) against the Server created by the Server Team. The information sent between the client and the server was loggend into files and checked for discrepancies. Additionally, we set up a local server (localhost) in which we would generate a random deck of 6 tiles and our AI would play itself. With the output, we manually checked that everything was logically correct (meeple placement, tile placement, etc).
