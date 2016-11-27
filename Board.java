import java.util.ArrayList;

import java.util.*;
public class Board {
	public Tile tileArray [][];	 //row/column, upper left corner is 0 0
	public Boolean tileTracker[][]; //row/column, upper left corner is 0 0
	public Boolean tileWasRendered[][]; //row/column, upper left corner is 0 0
	public int boardColumnNumber;
	public int boardRowNumber;	
	public volatile int test;//for testing multithreading 
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
	void placeFirstCard(String cardCode, int xLocation, int yLocation, int rotation)
	{
		// create card for the information that was given and then create the tile
		Card card = new Card(cardCode); 
		Tile tile = new Tile(card, rotation, xLocation+(int)boardColumnNumber/2 , yLocation+ (int)boardRowNumber/2, false, false, false, false);
		
		// position the tile at the starting location and then mark it off as existing
		this.tileArray[(int)boardColumnNumber/2][(int)boardRowNumber/2] = tile;
		this.tileArray[(int)boardColumnNumber/2][(int)boardRowNumber/2].isPlacedOnBoard = true;
		this.tileTracker[(int)boardColumnNumber/2][(int)boardRowNumber/2] = true;
		
		this.findEmptyTilesAround(xLocation+(int)boardColumnNumber/2 , yLocation+ (int)boardRowNumber/2);
	}

	public ArrayList<PlacementPossibility> generatePossibleCardPlacements(Card card)//here  we generate possible placment
	{
//		System.out.println(tileArray[77][77].finalPlacedOrientation.up+tileArray[77][77].finalPlacedOrientation.right+tileArray[77][77].finalPlacedOrientation.bottom+tileArray[77][77].finalPlacedOrientation.left);
		ArrayList<PlacementPossibility> possibilities = new ArrayList<PlacementPossibility>();	

		this.printBoard();
		
		for(int q=0; q<this.possiblePlacementTracker.size(); q++)//first we go trough vector that holds coordinates of all tiles adjaecent to tiles that we already placed
		{		
			
			int row = this.possiblePlacementTracker.get(q).row;
			int column = this.possiblePlacementTracker.get(q).column;				
			//we form arrays of terrain names to make work with them more convenient
			String adjecentSides [] = new String [4];//this terrain around current card
			String currentSides [] = new String [4];//this terrain of current card
			
			for(int k=0;k<4;k++)
			{
				adjecentSides[k]=new String("");
				currentSides[k]=new String("");
			}
			
			currentSides [0] = card.terrainOnSide.up;
			currentSides [1] = card.terrainOnSide.right;
			currentSides [2] = card.terrainOnSide.bottom;
			currentSides [3] = card.terrainOnSide.left;
			
			// now check each of the 4 neighbors to see that the sides match up
			if(row>0)//get side above current row/column position
			{
				if(tileTracker[row-1][column])
				{
					adjecentSides [0]=tileArray[row-1][column].finalPlacedOrientation.bottom;					
				}
			}

			if(column<boardColumnNumber-1)//get side on right from current row/column position
			{
				if(tileTracker[row][column+1])
				{
					adjecentSides [1]=tileArray[row][column+1].finalPlacedOrientation.left;						
				}
			}

			if(row<boardRowNumber-1)//get side below current row/column position
			{
				if(tileTracker[row+1][column])
				{
					adjecentSides [2]=tileArray[row+1][column].finalPlacedOrientation.up;					
				}
			}

			if(column>0)//get side on left from current row/column position
			{
				if(tileTracker[row][column-1])
				{
					adjecentSides [3]=tileArray[row][column-1].finalPlacedOrientation.right;
				}
			}		

			// TODO verify that Tiger placement is valid, as of the moment only checks positions and neighbors
			for(int k=0;k<4;k++)
			{
				if(	(currentSides[(0+k)%4].equals(adjecentSides[0])||adjecentSides[0].equals(""))&&
					(currentSides[(1+k)%4].equals(adjecentSides[1])||adjecentSides[1].equals(""))&&
					(currentSides[(2+k)%4].equals(adjecentSides[2])||adjecentSides[2].equals(""))&&
					(currentSides[(3+k)%4].equals(adjecentSides[3])||adjecentSides[3].equals("")))					
					{
						PlacementPossibility tmp = new PlacementPossibility(row,column,k);//if terrains mutch we store data about position and rotation
						possibilities.add(tmp);							
					}					
			}	
		}
		
		System.out.println(possibilities.size());
		return possibilities;
	}
	

	public void updateBoard(PlayerMoveInformation response){
		boolean northNeighbor = false;
		boolean eastNeighbor = false;
		boolean southNeighbor = false;
		boolean westNeighbor = false;
		if(this.tileTracker[response.column][response.row+1] == true){
			northNeighbor = true;
		}
		if(this.tileTracker[response.column+1][response.row] == true){
			eastNeighbor = true;
		}
		if(this.tileTracker[response.column][response.row-1] == true){
			southNeighbor = true;
		}
		if(this.tileTracker[response.column-1][response.row+1] == true){
			westNeighbor = true;
		}	
		
		Tile tile = new Tile(response.card, response.column, response.row, response.orientation, northNeighbor, eastNeighbor, southNeighbor, westNeighbor); 
		
		// mark as placed with tileTracker array
		this.tileTracker[response.column][response.row] = true;

		// now position new tile and mark it off as set
		this.tileArray[response.column][response.row] = tile;
		this.tileArray[response.column][response.row].isPlacedOnBoard = true;
	}
	
	
	public void udpateBoardFromServerResponse(ServerMoveValidationResponse response){

		boolean northNeighbor = false;
		boolean eastNeighbor = false;
		boolean southNeighbor = false;
		boolean westNeighbor = false;
		if(this.tileTracker[response.xLocation][response.yLocation+1] == true){
			northNeighbor = true;
		}
		if(this.tileTracker[response.xLocation+1][response.yLocation] == true){
			eastNeighbor = true;
		}
		if(this.tileTracker[response.xLocation][response.yLocation-1] == true){
			southNeighbor = true;
		}
		if(this.tileTracker[response.xLocation-1][response.yLocation+1] == true){
			westNeighbor = true;
		}	

		Tile tileToPlace = new Tile(response.card, response.xLocation, response.yLocation, response.cardOrientation, northNeighbor, eastNeighbor, southNeighbor, westNeighbor); 

		// mark as placed with tileTracker array
		this.tileTracker[response.xLocation][response.yLocation] = true;

		// now position new tile and mark it off as set
		this.tileArray[response.xLocation][response.yLocation] = tileToPlace;
		this.tileArray[response.xLocation][response.yLocation].isPlacedOnBoard = true;

	}

	public void findEmptyTilesAround(int row, int column)/*looks for empty tiles around tile with coordinates
															row and column and updates possiblePlacementTracker
															with new values*/
	{
		if(row+1<boardRowNumber)
		{
			if(!tileTracker[row+1][column])
				possiblePlacementTracker.add(new Coordinates(row+1,column));
		}	
		if(column+1<boardRowNumber)
		{
			if(!tileTracker[row][column+1])
				possiblePlacementTracker.add(new Coordinates(row,column+1));
		}
		if(row-1>=0)
		{
			if(!tileTracker[row-1][column])
				possiblePlacementTracker.add(new Coordinates(row-1,column));
		}
		if(column-1>=0)
		{
			if(!tileTracker[row][column-1])
				possiblePlacementTracker.add(new Coordinates(row,column-1));
		}
	}
	
	public void printBoard()
	{
<<<<<<< HEAD
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
=======
		String temp = "";
		String sideInfoOne;
		String sideInfoTwo;
		for(int i = 0; i<this.boardRowNumber;i++){
			for(int j = 0;j<this.boardColumnNumber;j++){
				if(!this.tileArray[i][j]){
					temp = temp+"|     |";
				}
				else{
					switch(this.tileArray[i][j].terrainOnSide.up){
						case "jungle" : sideInfoOne = "J"; break;
						case "lake"	  : sideInfoOne = "L"; break;
						case "game-trail": sideInfoOne = "G"; break;
						default : sideInfoOne = "?";
					}

					temp = temp +"|  " + sideInfoOne + "  |";
				}
			}

			System.out.println(temp);
			temp = "";

			for(int j = 0;j<this.boardColumnNumber;j++){
				if(!this.tileArray[i][j]){
					temp = temp+"|     |";
				}
				else{
					switch(this.tileArray[i][j].terrainOnSide.left){
						case "jungle" : sideInfoOne = "J"; break;
						case "lake"	  : sideInfoOne = "L"; break;
						case "game-trail": sideInfoOne = "G"; break;
						default : sideInfoOne = "?";
					}

					switch(this.tileArray[i][j].terrainOnSide.right){
						case "jungle" : sideInfoTwo = "J"; break;
						case "lake"	  : sideInfoTwo = "L"; break;
						case "game-trail": sideInfoTwo = "G"; break;
						default : sideInfoTwo = "?";
					}

					temp = temp+"|"+sideInfoOne+"   "+sideInfoTwo+"|";

					}
			}

			System.out.println(temp);
			temp = "";

			for(int j = 0;j<this.boardColumnNumber;j++){
				if(!this.tileArray[i][j]){
					temp = temp+"|     |";
				}
				else{
					switch(this.tileArray[i][j].terrainOnSide.bottom){
						case "jungle" : sideInfoOne = "J"; break;
						case "lake"	  : sideInfoOne = "L"; break;
						case "game-trail": sideInfoOne = "G"; break;
						default : sideInfoOne = "?";
					}

					temp = temp +"|  " + sideInfoOne + "  |";
				}

			}

			System.out.println(temp);
			temp = "";

			for(int j = 0;j<this.boardColumnNumber;j++){
				temp = temp+"_______";

			}

			System.out.println(temp);
			temp = "";
		}
>>>>>>> origin/master
	}

}

