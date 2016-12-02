package mainPackage;

import java.util.ArrayList;
//class that are responsible for passing control from one player to another
public class Game {
	
	public Player [] players;
	Board board;
	Deck deck;
	boolean gameIsOver;
	int currentTurn;
	int gameID;
	public Card currentCrad;
	ArrayList<PlacementPossibility>possibilities;
	
	public Game(String player1Type, String player2Type, int gameID)//player types="AI""SERVER""HUMAN"
	{
		
		// at the start of the game we want to generate the deck, this will be the one deck that both players will draw cards from
		this.deck = new Deck();			
		this.players = new Player[2];
		System.out.println("CHECKPOINT: Calling Board Initialization");
		this.board = new Board();
		IO.board[gameID]=this.board;
		
		/* Create each of the two players, the first one will be for the human player, the second one will be for the AI, also give copies of the offical deck so players
		 know the order to the cards, what kind of cards exist, etc. will be used by players to plan out moves
		 */
		players[0] = new Player(0, player1Type, this.deck.shareDeckToPlayers());
		players[1] = new Player(1, player2Type, this.deck.shareDeckToPlayers());
		players[0].localVersionOfBoard=board;
		players[1].localVersionOfBoard=board;				
		
		this.currentTurn = 0; 	
		this.gameID=gameID;
		currentCrad=new Card("game-trail", "lake", "game-trail", "jungle", false, false, false, false, false, false);
		int startingOrientation =this.board.placeFirstCard();
		Card startingCard = new Card("game-trail", "lake", "game-trail", "jungle", false, false, false, false, false, false);
		gameIsOver=true;
	}

	public PlayerMoveInformation playNextMove(Card drawnCard){		
		PlayerMoveInformation response = players[currentTurn].makeMove(drawnCard);
		return response;
	}
	
	public void playTurn()
	{
		Card drawnCard = deck.NextCardInDeck();		
		if(deck.IsEmpty())
			IO.noCardsLeft[IO.currentGame]=true;
		currentCrad=drawnCard;
		PlayerMoveInformation response = playNextMove(drawnCard);//Player (or human or AI or Server gets card and returns response)
		board.placeCard(response);//modify board according to response
		board.placeTiger(response, currentTurn);//modify board according to response	
		switchPlayerTurns();	
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void switchPlayerTurns(){
		if(this.currentTurn == 0){
			this.currentTurn = 1;
		}
		else{
			this.currentTurn = 0;
		}
	}	

	
}

