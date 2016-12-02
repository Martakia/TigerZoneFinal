package mainPackage;
import java.lang.*;
import java.util.Scanner;
import java.util.Vector;
import java.util.ArrayList;
public class Player {	
	
	int whichPlayerAmI;	
	Board localVersionOfBoard;		
	boolean isAI;
	boolean isHuman;
	boolean isServer;
	boolean playerMadeMove;
	
	Player(int whichPlayerAmI,String type, ArrayList<Card> officialDeck)
	{
		this.localVersionOfBoard = new Board();
		this.whichPlayerAmI = whichPlayerAmI; 
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
			isHuman = true;
		}
		else
		{
			System.out.println("ERROR : Invalid player type in constructor\n");
		}
	}
	
	public PlayerMoveInformation makeMove(Card cardToPlace)
	{
		System.out.println("CHECKPOINT: Player "+this.whichPlayerAmI+ " begins move");
		
		PlayerMoveInformation response = new PlayerMoveInformation();
		if(isHuman)
		{			
			waitForPlayerToPlaceTile(cardToPlace);		
			modifyTmpBoard(response);// we don't modify actuall board in makeMove. all changes are made to board copy
			waitForPlayerToPlaceTiger();
			response=fillResponseWithData(response);
		}	
		if(isAI)
		{	
			response=aiMakeMove(cardToPlace);// we don't modify actuall board in makeMove. all changes are made to board copy
			IO.moveInfoToServer[IO.currentGame]=response;
		}
//		System.out.println(IO.boardTmp[IO.currentGame].possiblePlacementTracker.size()+" ppt "+IO.board[IO.currentGame].possiblePlacementTracker.size());
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		if(isServer)
		{
			response=IO.moveInfoFromServer[IO.currentGame];
			IO.tigerPlacementSlotsAI[IO.currentGame].add(new Coordinates(response.tigerLocationRow,response.tigerLocationColumn,whichPlayerAmI));//just for rendering tigers
			
			System.out.println(response.tigerPlaced+" "+response.tigerLocationRow+" "+response.tigerLocationColumn);
		}
		
		return response;
	}
	
	void waitForPlayerToPlaceTile(Card cardToPlace)
	{
		IO.boardTmp[IO.currentGame]= new Board(localVersionOfBoard);//create copy of board to perform all modifications on it
		IO.setCard(cardToPlace);
		IO.setPossibilities(IO.boardTmp[IO.currentGame].generatePossibleCardPlacements(cardToPlace));
		IO.updatePlacments[IO.currentGame]=true;
		do
		/*loop untill card is placed on board. When it will be done Window class will set playerMadeMove to true*/
		{
			do
			{
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}while(!IO.playerPlacedCard[IO.currentGame]);
			IO.playerPlacedCard[IO.currentGame]=false;
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}while(!IO.boardTmp[IO.currentGame].isMoveValid(new PlayerMoveInformation(IO.currentCrad[IO.currentGame],  IO.row[IO.currentGame],IO.column[IO.currentGame], IO.rotation[IO.currentGame], false, 0,0)));
	}

	void modifyTmpBoard(PlayerMoveInformation response)
	{
		response = new PlayerMoveInformation(IO.currentCrad[IO.currentGame],IO.row[IO.currentGame],IO.column[IO.currentGame],IO.rotation[IO.currentGame],false,0,0);
		IO.boardTmp[IO.currentGame].placeCard(response);
		IO.boardTmp[IO.currentGame].addSectionTerrain(response);
		IO.tigerPlacementSlots[IO.currentGame]= IO.boardTmp[IO.currentGame].generatePossibleTigerSlots(response);
		IO.tigerSlotsUpdated[IO.currentGame]=true;
	}
	
	void waitForPlayerToPlaceTiger()
	{
		do
		{
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}while(!IO.playerPlacedMeeple[IO.currentGame]);
	}
	
	PlayerMoveInformation aiMakeMove(Card cardToPlace)
	{
		
/////////////////////////////////////////////////////SETTING SOME DATA//////////////////////////////////////////////////////	
		
		
		IO.boardTmp[IO.currentGame]= new Board(localVersionOfBoard);
		IO.setCard(cardToPlace);
		IO.updatePlacments[IO.currentGame]=true;		
		PlacementPossibility selectedByAIPossibility;
		PlayerMoveInformation responseTmpAI;
		Vector<Coordinates> possibleTigerPlacements;
		ArrayList<PlacementPossibility> possibleCardPlacements;
		IO.boardTmp[IO.currentGame]= new Board(localVersionOfBoard);		
		int selectedPossibilityID=0;
		
		System.out.println("AI starts move");
		
		
/////////////////////////////////////////////////////SELECTING POSSIBLE TILE PLACEMENT//////////////////////////////////////////////////////		
		
		
		possibleCardPlacements=IO.boardTmp[IO.currentGame].generatePossibleCardPlacements(cardToPlace);//generate card placement possibilities
		
		if(possibleCardPlacements.size()!=0)//if possibilities are present we select one randomly and check if it is not take yet using tileTracker data
		{				
			do
			{					
				selectedPossibilityID = (int) (Math.random()*possibleCardPlacements.size());
//				System.out.println("AI fails pos size "+possibleCardPlacements.size()+" rand= "+selectedPossibilityID);
			}while(IO.board[IO.currentGame].tileTracker[possibleCardPlacements.get(selectedPossibilityID).row][possibleCardPlacements.get(selectedPossibilityID).column]);
			
		}
		else//if there is no placement possibilities we return response with row and column = -1
		{
			System.out.println("=============================AI didn't find placement and skipped turn===============================");
			return new PlayerMoveInformation(cardToPlace,
					-1,
					-1,
					0,
					false,
					0,
					0);
		}		
		
		System.out.println("AI selects placement");		
		System.out.println(possibleCardPlacements.get(selectedPossibilityID).row+ " "+
				possibleCardPlacements.get(selectedPossibilityID).column+ " "+
				possibleCardPlacements.get(selectedPossibilityID).rotatio);
		responseTmpAI= new PlayerMoveInformation(cardToPlace,					
				possibleCardPlacements.get(selectedPossibilityID).row,
				possibleCardPlacements.get(selectedPossibilityID).column,
				(4-possibleCardPlacements.get(selectedPossibilityID).rotatio)%4,//some weird stuff forceS us to rotate in opposite direction
				false,0,0);
		
		
		IO.boardTmp[IO.currentGame].placeCard(responseTmpAI);//now we place current card in selected position on temporary board that we made
		
		
/////////////////////////////////////////////////////SELECTING POSSIBLE TIGER PLACEMENT//////////////////////////////////////////////////////		
		
		
		possibleTigerPlacements=IO.boardTmp[IO.currentGame].generatePossibleTigerSlots(responseTmpAI);//now we generate possible tiger placements using this temporary board
		if(possibleTigerPlacements.size()!=0)//if tiger placements are present w select one randomly and add tiger data to response
		{
			int selectedPlacementID = (int)Math.random()*possibleTigerPlacements.size();			
			IO.tigerPlacementSlotsAI[IO.currentGame].add(new Coordinates(possibleTigerPlacements.get(selectedPlacementID ).row,possibleTigerPlacements.get(selectedPlacementID ).column,whichPlayerAmI));
			
			System.out.println("AI ends move");
			//form final response 
			return new PlayerMoveInformation(cardToPlace,
					possibleCardPlacements.get(selectedPossibilityID).row,
					possibleCardPlacements.get(selectedPossibilityID).column,
					(4-possibleCardPlacements.get(selectedPossibilityID).rotatio)%4,//some weird stuff forceS us to rotate in opposite direction
					true,
					possibleTigerPlacements.get(selectedPlacementID ).row,
					possibleTigerPlacements.get(selectedPlacementID ).column);	
			}
		else//if no placements present we return following message
		{
			System.out.println("AI ends move");
			return new PlayerMoveInformation(cardToPlace,
					possibleCardPlacements.get(selectedPossibilityID).row,
					possibleCardPlacements.get(selectedPossibilityID).column,
					(4-possibleCardPlacements.get(selectedPossibilityID).rotatio)%4,//some weird stuff forceS us to rotate in opposite direction
					false,
					0,
					0);
		}

		
	}

	PlayerMoveInformation fillResponseWithData(PlayerMoveInformation response)
	{
		if(IO.didPlayerDecideToPlaceMeeple[IO.currentGame])
		{
			response = new PlayerMoveInformation(IO.currentCrad[IO.currentGame],IO.row[IO.currentGame],IO.column[IO.currentGame],IO.rotation[IO.currentGame],IO.didPlayerDecideToPlaceMeeple[IO.currentGame],IO.placedTiger[IO.currentGame].row,IO.placedTiger[IO.currentGame].column);
		}
		else
		{
			response = new PlayerMoveInformation(IO.currentCrad[IO.currentGame],IO.row[IO.currentGame],IO.column[IO.currentGame],IO.rotation[IO.currentGame],IO.didPlayerDecideToPlaceMeeple[IO.currentGame],-1,-1);
		}
		
		IO.playerPlacedCard[IO.currentGame]=false;	
		IO.playerPlacedMeeple[IO.currentGame]=false;
		
		return response;
	}
}
