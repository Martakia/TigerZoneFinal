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
	
	ArrayList<TigerInformation> ourTigerLocations;
	ArrayList<TigerInformation> enemyTigerLocations;

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
		// used to keep track 
		ourTigerLocations = new ArrayList<TigerInformation>();
		enemyTigerLocations = new ArrayList<TigerInformation>();
		System.out.println("CHECKPOINT: Board Initialization complete");
	}

	public int numberOfTigersWePlaced(){
		return this.ourTigerLocations.size();
	}
	public int numberOfTigersEnemyPlaced(){
		return this.enemyTigerLocations.size();
	}

	void placeFirstCard(String cardCode, int row, int column, int rotation)
	{
		// create card for the information that was given and then create the tile
		Card card = new Card(cardCode); 
		Tile tile = new Tile(card, rotation, ((row+(int)boardColumnNumber/2)), ((column+ (int)boardRowNumber/2)), false, false, false, false);
		
		// position the tile at the starting location and then mark it off as existing
		this.tileArray[(int)boardRowNumber/2][(int)boardColumnNumber/2] = tile;
		this.tileArray[(int)boardRowNumber/2][(int)boardColumnNumber/2].isPlacedOnBoard = true;
		this.tileTracker[(int)boardRowNumber/2][(int)boardColumnNumber/2] = true;
	
	}
	public ArrayList<PlacementPossibility> generatePossibleCardPlacements(Card card)
	{
		ArrayList<PlacementPossibility> possibilities = new ArrayList<PlacementPossibility>();	

		ArrayList<Coordinates> checkLocations = new ArrayList<Coordinates>();

		// check 4 sides to see if there is a neighboring tile that is placed

		for(int i=1; i<this.boardRowNumber-1; i++){
			for(int j=1; j<this.boardColumnNumber-1; j++){

				boolean adjacent = false;

				//tileTracker [row][column] => [i][j]
				if(tileTracker[i+1][j]){
					// check above
					adjacent = true;
				}
				if(tileTracker[i-1][j]){
					// check below
					adjacent = true;
				}
				if(tileTracker[i][j+1]){
					// check right
					adjacent = true;
				}
				if(tileTracker[i][j-1]){
					// check left
					adjacent = true;
				}

				// if a tile exists already at the location then we don't want to calculate it
				if(tileTracker[i][j]){
					adjacent = false;
					System.out.println("ONE " + i + " " +j + " " + tileArray[i][j].returnCardCode() + " " + tileArray[i][j].finalPlacedOrientation.up + " " + tileArray[i][j].finalPlacedOrientation.right + " " + tileArray[i][j].finalPlacedOrientation.bottom + " " +tileArray[i][j].finalPlacedOrientation.left);
				}
				if(adjacent){
					// create coordinate to analyze later
					Coordinates toAdd = new Coordinates(i,j);
					checkLocations.add(toAdd);
				}
			}
		}
		// now with the possible locations, we check the 4 sides at each of the 4 rotations to see if it could work


		System.out.println(card.terrainOnSide.up);
		System.out.println(card.terrainOnSide.right);
		System.out.println(card.terrainOnSide.bottom);
		System.out.println(card.terrainOnSide.left);


		for(int i=0; i<checkLocations.size(); i++){
			// itterate through each of the possible points rotations the current card and checking if 4 sides match up
			Coordinates verify = checkLocations.get(i);

			boolean rotation0 = true;
			boolean rotation1 = true;
			boolean rotation2 = true;
			boolean rotation3 = true;
			
			if(!tileTracker[verify.row+1][verify.column]){
				// no neighbor above to check
			} else {
				// with 0 rotation
				if(card.terrainOnSide.up.equals(tileArray[verify.row+1][verify.column].finalPlacedOrientation.bottom)){
					// valid
				} else {
					rotation0 = false;
				}

				// 90 counterclockwise
				if(card.terrainOnSide.right.equals(tileArray[verify.row+1][verify.column].finalPlacedOrientation.bottom)){
					// valid 
				} else {
					rotation1 = false;
				}

				// 180 coutnerclockwise
				if(card.terrainOnSide.bottom.equals(tileArray[verify.row+1][verify.column].finalPlacedOrientation.bottom)){
					// valid
				} else {
					rotation2 = false;
				}

				// 270 counterclockwise
				if(card.terrainOnSide.left.equals(tileArray[verify.row+1][verify.column].finalPlacedOrientation.bottom)){
					// valid 
				} else {
					rotation3 = false;
				}

			}

			if(!tileTracker[verify.row][verify.column+1]){
				// no neighbor to the right
			} else {
				// with 0 rotation
				if(card.terrainOnSide.right.equals(tileArray[verify.row][verify.column+1].finalPlacedOrientation.left)){
					// valid
				} else {
					rotation0 = false;
				}

				// with 90 counterclockwise
				if(card.terrainOnSide.bottom.equals(tileArray[verify.row][verify.column+1].finalPlacedOrientation.left)){
					// valid
				} else {
					rotation1 = false;
				}

				// 180 clockwise rotation
				if(card.terrainOnSide.left.equals(tileArray[verify.row][verify.column+1].finalPlacedOrientation.left)){
					// valid
				} else {
					rotation2 = false;
				}

				// 270 counterclockwise
				if(card.terrainOnSide.up.equals(tileArray[verify.row][verify.column+1].finalPlacedOrientation.left)){
					// valid
				} else {
					rotation3 = false;
				}
			}

			if(!tileTracker[verify.row-1][verify.column]){
				// no neighbor below
			} else {
				// with 0 rotation
				if(card.terrainOnSide.bottom.equals(tileArray[verify.row-1][verify.column].finalPlacedOrientation.up)){
					// valid
				} else {
					rotation0 = false;
				}
				// 90 counter clockwise
				if(card.terrainOnSide.left.equals(tileArray[verify.row-1][verify.column].finalPlacedOrientation.up)){
					// valid
				} else {
					rotation1 = false;
				}
				// 180 rotation
				if(card.terrainOnSide.up.equals(tileArray[verify.row-1][verify.column].finalPlacedOrientation.up)){
					// valid
				} else {
					rotation2 = false;
				}
				// 270 counter
				if(card.terrainOnSide.right.equals(tileArray[verify.row-1][verify.column].finalPlacedOrientation.up)){
					// valid
				} else {
					rotation3 = false;
				}

			}


			if(!tileTracker[verify.row][verify.column-1]){
				// no neighbor to the left
			} else {
				// with 0 rotation
				if(card.terrainOnSide.left.equals(tileArray[verify.row][verify.column-1].finalPlacedOrientation.right)){
					// valid
				} else {
					rotation0 = false;
				}
				// with 90 clockwise
				if(card.terrainOnSide.up.equals(tileArray[verify.row][verify.column-1].finalPlacedOrientation.right)){
					// valid
				} else {
					rotation1 = false;
				}
				// with 180 rotation
				if(card.terrainOnSide.right.equals(tileArray[verify.row][verify.column-1].finalPlacedOrientation.right)){
					// valid
				} else {
					rotation2 = false;
				}
				// with 90 counterclockwise
				if(card.terrainOnSide.bottom.equals(tileArray[verify.row][verify.column-1].finalPlacedOrientation.right)){
					// valid
				} else {
					rotation3 = false;
				}

			}

			// now if any of the combinations exist, they are still marked as true we add it to the possibilites
			if(rotation0){
				PlacementPossibility addMe = new PlacementPossibility(verify.row, verify.column, 0);
				possibilities.add(addMe);
			}
			if(rotation1){ 
				PlacementPossibility addMe = new PlacementPossibility(verify.row, verify.column, 1);
				possibilities.add(addMe);
			}
			if(rotation2){ 
				PlacementPossibility addMe = new PlacementPossibility(verify.row, verify.column, 2);
				possibilities.add(addMe);
			}
			if(rotation3){ 
				PlacementPossibility addMe = new PlacementPossibility(verify.row, verify.column, 3);
				possibilities.add(addMe);
			}
		}

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
		
		Tile tile = new Tile(response.card, response.orientation, response.row, response.column, northNeighbor, eastNeighbor, southNeighbor, westNeighbor); 
		
		// mark as placed with tileTracker array
		this.tileTracker[response.row][response.column] = true;

		// now position new tile and mark it off as set
		this.tileArray[response.row][response.column] = tile;
		this.tileArray[response.row][response.column].isPlacedOnBoard = true;

		if(response.tigerPlaced){
			// tiger has been placed, we need to keep track of it
			TigerInformation addInfo = new TigerInformation(response.row, response.column, response.tigerLocation, false);
			ourTigerLocations.add(addInfo);
		}

		System.out.println("BOARD BEING UPDATED WITH " +response.row + " " + response.column);
		
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

		if(response.tigerPlaced){
			// tiger has been added, we need to keep track of it
			TigerInformation addInfo;
			if(response.enemy){
				addInfo = new TigerInformation(response.row, response.column, response.tigerLocation, true);
				enemyTigerLocations.add(addInfo);
			} else {
				// don't add, we already added the tigerInfo on our side before the response was sent
			}
			
		}

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

