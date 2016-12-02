all: Player.class Board.class Game.class main.class

Player.class: Player.java
	javac Player.java
Board.class: Board.java
	javac Board.java
Game.class: Game.java
	javac Game.java
main.class: main.java
	javac main.java

clean:
	rm Player.class
	rm Board.class
	rm main.class