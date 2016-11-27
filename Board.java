import java.util.ArrayList;

import java.util.*;
public class Board {
	public Tile tileArray [][];	 //row/column, upper left corner is 0 0
	public Boolean tileTracker[][]; //row/column, upper left corner is 0 0
	public Boolean tileWasRendered[][]; //row/column, upper left corner is 0 0
	public int boardColumnNumber;
	public int boardRowNumber;	
	public Tile centralTile;
	
	public volatile Vector<Coordinates> possiblePlacementTracker; /*this vector updates each move to keep coordinates
	 												of tiles that are adjacent to placed cards
	 												and where we can place card next turn*/
	public volatile Vector<Coordinates> actualPlacementTracker; /*this vector updates each turn and o keep coordinates
	 												of tiles that are placed. It exists to avoid going through 
	 												tileArray 155*155 array too often*/
	
	Board()
	{
		this.boardColumnNumber = 155;
		this.boardRowNumber = 155;		
		tileArray = new Tile [boardRowNumber][boardColumnNumber];		
		possiblePlacementTracker = new Vector<Coordinates>(0);
		actualPlacementTracker = new Vector<Coordinates>(0);
		
		// initialize 2D array of booleans that keeps track if there is a tile placed at all at location, if false, then empty and nothing there
		// if true, something has been placed there
		tileTracker = new Boolean[boardRowNumber][boardColumnNumber];
		tileWasRendered = new Boolean[boardRowNumber][boardColumnNumber];
		
		
		for(int row=0; row<this.boardRowNumber; row++){
			for(int column=0; column<this.boardColumnNumber; column++){
				tileTracker[row][column] = false;	
				tileWasRendered [row][column] = false;
				
			}
		}	

		System.out.println("CHECKPOINT: Board Initialization complete");
	}
	void placeFirstCard(String cardCode, int row, int column, int rotation)
	{
		// create card for the information that was given and then create the tile
		Card card = new Card(cardCode); 
		Tile tile = new Tile(card, rotation, (row+(int)boardColumnNumber/2) , (column+ (int)boardRowNumber/2), false, false, false, false);
		
		// position the tile at the starting location and then mark it off as existing
		this.tileArray[(int)boardRowNumber/2][(int)boardColumnNumber/2] = tile;
		this.tileArray[(int)boardRowNumber/2][(int)boardColumnNumber/2].isPlacedOnBoard = true;
		this.tileTracker[(int)boardRowNumber/2][(int)boardColumnNumber/2] = true;
	
	}

	public ArrayList<PlacementPossibility> generatePossibleCardPlacements(Card card)
	{
		ArrayList<PlacementPossibility> possibilities = new ArrayList<PlacementPossibility>();	

		// TODO copy over code to find possibilities on board of where to place a card
		

		return possibilities;
	}

	public ArrayList<Integer> generatePossibleTigerPlacements(int row, int column){
		ArrayList<Integer> possibilities = new ArrayList<Integer>();

		// TODO copy over code to find possible TigerPlacements 


		return possibilities;
	}
	

	public void updateBoard(PlayerMoveInformation response){
		// when player makes move, coordinates are already 0-155 so no need to add 77 to each one to get middle index
		boolean northNeighbor = false;
		boolean eastNeighbor = false;
		boolean southNeighbor = false;
		boolean westNeighbor = false;
		if(this.tileTracker[response.row+1][response.column] == true){
			northNeighbor = true;
		}
		if(this.tileTracker[response.row][response.column+1] == true){
			eastNeighbor = true;
		}
		if(this.tileTracker[response.row-1][response.column] == true){
			southNeighbor = true;
		}
		if(this.tileTracker[response.row][response.column-1] == true){
			westNeighbor = true;
		}	
		
		Tile tile = new Tile(response.card, response.row, response.column, response.orientation, northNeighbor, eastNeighbor, southNeighbor, westNeighbor); 
		
		// mark as placed with tileTracker array
		this.tileTracker[response.row][response.column] = true;

		// now position new tile and mark it off as set
		this.tileArray[response.row][response.column] = tile;
		this.tileArray[response.row][response.column].isPlacedOnBoard = true;
	}
	
	
	public void udpateBoardFromServerResponse(ServerMoveValidationResponse response){

		// responses come back 0 indexed while our board 0 is represented by 77 
		int add = (this.boardRowNumber/2);

		boolean northNeighbor = false;
		boolean eastNeighbor = false;
		boolean southNeighbor = false;
		boolean westNeighbor = false;
		if(this.tileTracker[response.row+1+add][response.column+add] == true){
			northNeighbor = true;
		}
		if(this.tileTracker[response.row+add][response.column+1+add] == true){
			eastNeighbor = true;
		}
		if(this.tileTracker[response.row-1+add][response.column+add] == true){
			southNeighbor = true;
		}
		if(this.tileTracker[response.row+add][response.column-1+add] == true){
			westNeighbor = true;
		}	

		Tile tileToPlace = new Tile(response.card, response.cardOrientation, response.row+add, response.column+add, northNeighbor, eastNeighbor, southNeighbor, westNeighbor); 

		// mark as placed with tileTracker array
		this.tileTracker[response.row+add][response.column+add] = true;

		// now position new tile and mark it off as set
		this.tileArray[response.row+add][response.column+add] = tileToPlace;
		this.tileArray[response.row+add][response.column+add].isPlacedOnBoard = true;

	}

	public void removeEnemyTiger(int row, int column){

		// TODO implement, this is the response we get back from the server
	}
	

	public void addEnemyTiger(int row, int column){

		// TODO implement, this is the response we get back from the server
	}

	public void printBoard()
	{
		System.out.println("----------------");
		for(int i=0; i<this.boardColumnNumber; i++){
			for(int j=0; j<this.boardRowNumber; j++){
				if(this.tileTracker[i][j] == true){
					System.out.println("--------");
					System.out.println("| " + this.tileArray[i][j].CardCode + " |");
					System.out.println("--------");
					System.out.print(this.tileArray[i][j].column );
					System.out.print(this.tileArray[i][j].row );
				}
				else{
					
				}
			}
		}
		System.out.println("----------------");

	}

}

