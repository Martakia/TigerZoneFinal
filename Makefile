all: Card.class Coordinates.class RotatedIcon.class PlacementPossibility.class
all: PlayerMoveInformation.class TigerInformation.class Deck.class Log.class
all: Board.class Player.class Game.class main.class

Card.class: Card.java
	javac Card.java
Coordinates.class: Coordinates.java
	javac Coordinates.java
RotatedIcon.class: RotatedIcon.java
	javac RotatedIcon.java
PlacementPossibility.class: PlacementPossibility.java
	javac PlacementPossibility.java
PlayerMoveInformation.class: PlayerMoveInformation.java
	javac PlayerMoveInformation.java
TigerInformation.class: TigerInformation.java
	javac TigerInformation.java
Deck.class: Deck.java
	javac Deck.java
Log.class: Log.java
	javac Log.java
Board.class: Board.java
	javac Board.java
Player.class: Player.java
	javac Player.java
Game.class: Game.java
	javac Game.java
main.class: main.java
	javac main.java

clean:
	rm Card.class
	rm Coordinates.class
	rm RotatedIcon.class
	rm PlacementPossibility.class
	rm PlayerMoveInformation.class
	rm TigerInformation.class
	rm Deck.class
	rm Log.class
	rm Player.class
	rm Board.class
	rm Game.class
	rm main.class