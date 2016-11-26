package mainPackage;
import java.lang.*;
import java.util.Scanner;
import java.util.ArrayList;
public class Player {
	
	// Each player needs to keep track of their own information, in addition to the information that the server sends
	int ServerMyScore; // each player will keep track of their own score
	int ServerEnemyScore; // each player will also keep track of the enemy score
	int ServerMyMeepleCount;  // each player will keep track of how many meeples they have
	int ServerMyCrocCount; // keep trackj of how many crocs i have
	int ServerEnemyMeepleCount; // each player will keep track of how many meeples enemy player has
	int ServerEnemyCrocCount;//keep track of how many crocs enemy has
	
	// local version we keep track of ourselves, that is not updated by server response
	int localMyScore;
	int localEnemyScore;
	int localMyMeepleCount;
	int localMyCrocCount;
	int localEnemyMeepleCount;
	int localEnemyCrocCount;
	int lastPlacementRow, lastPlacementColumn, lastPlacementOrientation;//this vars helps to get data from user input
	
	int whichPlayerAmI; // server can treat each player as either 1 or 2, we must keep track of which one we are
	//meeple array
	
	Board localVersionOfBoard;
	private final ArrayList<Card> localVersionOfDeck;
	
	String name;
	int ID;//0 1
	boolean isAI;
	boolean isServer;
	boolean playerMadeMove;
	
	Player(int whichPlayerAmI,String type, ArrayList<Card> officialDeck)
	{
//		System.out.println("CHECKPOINT: Player Initialization in progress...");
		// score and meeple count according to server
		this.ServerMyScore =0;
		this.ServerMyMeepleCount =0;
		this.ServerMyCrocCount =0;
		this.ServerEnemyScore = 0;
		this.ServerEnemyMeepleCount = 0;
		this.ServerEnemyCrocCount =0;
		this.localVersionOfBoard = new Board();
		// score and meeple count according only to us
		this.localMyScore =0;
		this.localEnemyScore =0;
		this.localMyMeepleCount =0;
		this.localMyCrocCount =0;
		this.localEnemyMeepleCount =0;
		this.localEnemyCrocCount =0;
		
		this.localVersionOfDeck = officialDeck;
		this.whichPlayerAmI = whichPlayerAmI; // can change, server might think you are either player 1 or two	
		playerMadeMove=false;
		if(type=="AI")
		{
			isAI = true;
		}
		else if(type=="SERVER")
		{
			isServer = true;
		}
		else if(type=="HUMAN")
		{
			
		}
		else
		{
			System.out.println("ERROR : Invalid player type in constructor\n");
		}
	}
	
	public PlayerMoveInformation makeMove(Card cardToPlace)
	{
System.out.println("CHECKPOINT: Player "+this.whichPlayerAmI+ " begins move");
		if(!isAI&&!isServer)//in case of human player we:
		{
//			System.out.print("1");
			do
			/*loop untill card is placed on board. When it will be done Window class will set playerMadeMove to true*/
			{
//				System.out.print("2");
				do
				{
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}while(!IO.playerPlacedCard);
				IO.playerPlacedCard=false;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}while(!IO.board.isMoveValid(new PlayerMoveInformation(IO.currentCrad,  IO.row,IO.column, IO.rotation, false, 0,0)));
		}
		
		PlayerMoveInformation response = new PlayerMoveInformation(IO.currentCrad,IO.row,IO.column,IO.rotation,false,0,0);
		IO.board.placeCard(response);
		IO.board.addSectionTerrain(response);
		IO.tigerPlacementSlots=IO.board.generatePossibleTigerSlots(response);
		IO.tigerSlotsUpdated=true;
		do
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}while(!IO.playerPlacedMeeple);
		response = new PlayerMoveInformation(IO.currentCrad,IO.row,IO.column,IO.rotation,true,IO.placedTiger.row,IO.placedTiger.column);
		IO.playerPlacedCard=false;	
		IO.playerPlacedMeeple=false;

		return response;
	}
	
	// handles the response from the server after a player has made a move and sent it to the server, server checks if valid, does some
	// updating and then returns either valid, invalid or timeout
	public void moveValidationFromServer(ServerMoveValidationResponse moveValidationFromServer){
		//commented untill human vs human game is working
//		System.out.println("CHECKPOINT: Player " +this.whichPlayerAmI+ " recieves moveValidationFromServer");
//
//		if(moveValidationFromServer.isValid == false || moveValidationFromServer.timeout == true){
//			// player has F'd up and the game is over
//		}
//		else{
//			// we must update our local version of the board, server has updated it's own version already
//			this.localVersionOfBoard.udpateBoardFromServerResponse(moveValidationFromServer);
//			System.out.println("*****BOARD UPDATE******: Player " +this.whichPlayerAmI+ " updates their local version of the board with server response information");
//			// TODO more stuff
//		}
	}
	
	public void sendScoresToPlayers(ScoreUpdateInformation scoreInfo){
		// response from the server regarding any changes to the score or the meeple count
		
		// update local version of everything
		if(this.whichPlayerAmI == 1){
			// we are player 1 according to the server and player 2 is the enemy
			this.ServerMyScore += scoreInfo.playerOneScoreUpdate;
			this.ServerEnemyScore += scoreInfo.playerTwoScoreUpdate;
			this.ServerMyMeepleCount += scoreInfo.playerOneMeeplesReturned;
			this.ServerEnemyMeepleCount += scoreInfo.playerTwoMeeplesReturned;
		}
		else{
			// we must be player 2, and player 1 is the enemy according to the server
			this.ServerMyScore += scoreInfo.playerTwoScoreUpdate;
			this.ServerEnemyScore += scoreInfo.playerOneScoreUpdate;
			this.ServerMyMeepleCount += scoreInfo.playerTwoMeeplesReturned;
			this.ServerEnemyMeepleCount += scoreInfo.playerOneMeeplesReturned;
		}

	}
	
	void placeCardManually()
	{
		//select board tile to add current card
		//render board
	}
	
	void placeMeeple()
	{
		//check if meeple s available and place it
		// then render board
	}
	
	void setBoard(Board board)
	{
		this.localVersionOfBoard = board;
	}
	
	void countScore()
	{
		
	}
}
