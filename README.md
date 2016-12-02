# TigerZone - TeamR
  Martakia - Arturo Bustamente
  
  mondily - Amanda Morales
  
  konradpabjan - Konrad Pabjan
  
  ana16 - Ana Jelacic
  
  itismynamenow - George
  
  vpeixoto1 - Vanessa Peixoto
  

## To run the TigerZone Client using a server:

#### COMPILE the client using:

  'javac Card.java Coordinates.java RotatedIcon.java PlacementPossibility.java
   PlayerMoveInformation.java TigerInformation.java Deck.java Log.java Board.java
   Player.java Game.java main.java'

#### OR just run the command:

  'make'
 
#### RUN the Client using:

  'java main < IP address > < port number> < username > < password >'



## Acceptance Testing:

For acceptance testing, we tested our AI (Player.java) against the Server created by the Server Team. The information sent between the client and the server was loggend into files and checked for discrepancies. Additionally, we set up a local server (localhost) in which we would generate a random deck of 6 tiles and our AI would play itself. With the output, we manually checked that everything was logically correct (meeple placement, tile placement, etc).
