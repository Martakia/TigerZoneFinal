import java.util.ArrayList;
public class Game {
	
	Player player;
	Board board;
	Deck deck;
	
	//creates players, deck and board that belong to this instance of the game
	public Game() 
	{
		// Deck, Player, and Board initialized 
		this.deck = new Deck();
		this.player = new Player();
		this.board = new Board();
	}	
	
}

