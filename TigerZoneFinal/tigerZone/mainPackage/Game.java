package mainPackage;

import java.util.ArrayList;

public class Game {
	
	public Player [] players;
	Board board;
	Deck deck;
	boolean gameIsOver;
	int currentTurn;
	long timeOfCommandIssue;
	boolean timeout = false;
	public volatile int test;//for testing multithreading 
	public Card currentCrad;
	ArrayList<PlacementPossibility>possibilities;
	
	public Game()//creates players, deck and board
	{
		
		// at the start of the game we want to generate the deck, this will be the one deck that both players will draw cards from
		this.deck = new Deck();
		
		// Create an array of 2 player objects
		this.players = new Player[2];
		
		/* Create each of the two players, the first one will be for the human player, the second one will be for the AI, also give copies of the offical deck so players
		 know the order to the cards, what kind of cards exist, etc. will be used by players to plan out moves
		 */
		players[0] = new Player(0, "HUMAN", this.deck.shareDeckToPlayers());
		
//		System.out.println("CHECKPOINT: Player 1 initialized succesfully");
		players[1] = new Player(1, "HUMAN", this.deck.shareDeckToPlayers());	
//		System.out.println("CHECKPOINT: Player 2 initialized succesfully");
		
		
		// since there will be only 1 deck, we can keep deck separate from the players
		
		//create board, you want to generate the board at the start of the game when you call the GAME constructor
		System.out.println("CHECKPOINT: Calling Board Initialization");
		this.board = new Board();
		IO.board=board;
//		players[0].setBoard(this.board);//players do need boards
//		players[1].setBoard(this.board);
		// first player that will make a move, 0 indexed because of array
		this.currentTurn = 0; 
		
//		System.out.println("CHECKPOINT: Creating and placing first card on board");
		int startingOrientation =this.board.placeFirstCard();

		// send first move information to each of the players
		Card startingCard = new Card("game-trail", "lake", "game-trail", "jungle", false, false, false, false, false, false);
		ServerMoveValidationResponse serverResponse = new ServerMoveValidationResponse(true, false, startingCard, this.board.boardColumnNumber/2,this.board.boardRowNumber/2, startingOrientation, false);
//		this.sendMoveResponseToPlayers(serverResponse);
	}
	
	// method to check if drawn card can be placed in at least 1 position on board, in actual implementation server will give the card
	// used to make sure card can be placed before we give it to the player
	public boolean verifyDrawnCard(Card drawnCard){
		boolean answer = this.board.canCardBePlaced(drawnCard);
		return answer;
	}
	
	public PlayerMoveInformation playNextMove(Card drawnCard){
		
		
		currentCrad = drawnCard;//player get card
		IO.setCard(drawnCard);
		IO.setPossibilities(this.board.generatePossibleCardPlacements(currentCrad));
		IO.updatePlacments=true;
		possibilities  = this.board.generatePossibleCardPlacements(currentCrad);//board generates possible placments (incorrectly now)
//		System.out.println("CHECKPOINT: There are " +possibilities.size() + " possibilities to place card");
		
		// we are given the next player that is to move, we are guaranteed that it can be placed in at least one position on the board
//		System.out.println("CHECKPOINT: Card information being sent to player " +this.currentTurn);
		// we tell the next player whose turn it is that that he can move and we give him the card
		long timeOfCommandIssue = System.currentTimeMillis() % 1000;
		PlayerMoveInformation response = players[currentTurn].makeMove(drawnCard);
		
//		board.tileArray[IO.row][IO.column]= new Tile(IO.currentCrad,IO.rotation,IO.column,IO.row,false,false,false,false);
//		board.actualPlacementTracker.add(new Coordinates(IO.row,IO.column));
//		board.tileWasRendered [IO.row][IO.column]= false;
//		IO.playerMadeMove=false;
//		System.out.println("board.actualPlacementTracker.size() "+board.actualPlacementTracker.size());
//		System.out.println("board.possiblePlacementTracker.size() "+board.possiblePlacementTracker.size());
		// REMOVE AT THE VERY END, just for testing to see if time properly works
		try{
			Thread.sleep(100); // 1/10 a second to see delay
		} catch(Exception e){
			e.printStackTrace();
		}
		long responseTime = (System.currentTimeMillis() % 1000) - timeOfCommandIssue;
		if(responseTime > 1000){
			// with keyboard input, we don't care ATM about the time, when AI will be playing this will be valid
			//this.timeout = true;
		}

//		System.out.println("RESPONSE TIME: Player took " +responseTime+ " miliseconds to respond");
		return response;
	}
	
	// server checks if the move that the player wants to do is valid 
	public void validatePlayerMove(PlayerMoveInformation response){
		
//		System.out.println("CHECKPOINT: Server now validating move response from player");
		// server checks if card placement is valid
		boolean confirmation = this.board.isMoveValid(response);
//		System.out.println("CARDCHECK: Server determines that new card placemeent is " +confirmation);
		
		// if the move is valid, then we update our local version of the board, and keep track of any changes in the score
		ScoreUpdateInformation scoreInformationForPlayers = this.board.updateBoardFromPlayerMoveInformation(response);
//		System.out.println("*****BOARD UPDATE***** Server updates its local version of the board");
		
		// send information to players regarding the status of the move 
		// first boolean is to check if move is valid, second to measure response time
//		ServerMoveValidationResponse serverResponse = new ServerMoveValidationResponse(confirmation,timeout,response.card, response.cardX, response.cardY, response.orientation, response.tigerPlaced);
//		this.sendMoveResponseToPlayers(serverResponse);
//		this.sendScoreUpdateToPlayers(scoreInformationForPlayers);

		// if there is timeout, or invalid move, then now we want to terminate the game
//		if(confirmation == false || timeout == true){
//			System.out.println("GAME ENDING- INVALID MOVE OR TIMEOUT");
//			System.exit(0);
//		}
	}
	
	// after server checks if a move made by a player, response is sent to players
//	public void sendMoveResponseToPlayers(ServerMoveValidationResponse moveInformationResponse){
//		System.out.println("CHECKPOINT: Server sending new move information to players");
//		for(int i=0; i<players.length; i++){
//			// each of the players in the game is updated with the confirmation from the server
//			players[i].moveValidationFromServer(moveInformationResponse);
//		}
//	}
//	
//	// if there is a change of score after a move, the information is sent to the players
//	public void sendScoreUpdateToPlayers(ScoreUpdateInformation scoreInfo){
//		for(int i=0; i<players.length; i++){
//			// each of the players in the game is updated with the confirmation from the server
//			players[i].sendScoresToPlayers(scoreInfo);
//		}
//	}
	
	public void switchPlayerTurns(){
		if(this.currentTurn == 0){
			this.currentTurn = 1;
		}
		else{
			this.currentTurn = 0;
		}
	}	

	
}

