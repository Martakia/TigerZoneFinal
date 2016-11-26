package mainPackage;
import java.util.ArrayList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.TitlePaneLayout;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.util.*;
public class Board {
	public Tile tileArray [][];	 //row/column, upper left corner is 0 0
	public Boolean tileTracker[][]; //row/column, upper left corner is 0 0
	public Boolean tileWasRendered[][]; //row/column, upper left corner is 0 0
	public SectionInfo [][] sectionArray;
	public int boardColumnNumber;
	public int boardRowNumber;	
	public int count;
	public volatile int test;//for testing multithreading 
	public Tile centralTile;
	
	// each instance of the board keeps track of scores independently
	// the game class "AKA Server" will have its own Board 
	// player 1 will have its own board to keep track of
	// player 2 will have its own board to keep track of
	private int PlayerOneScore;
	private int PlayerTwoScore;
	private int PlayerOneMeepleCount;
	private int PlayerTwoMeepleCount;
	
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
//		possibleTigerSlots = new Vector<Coordinates>(0); 
		
		// initialize 2D array of booleans that keeps track if there is a tile placed at all at location, if false, then empty and nothing there
		// if true, something has been placed there
		tileTracker = new Boolean[boardRowNumber][boardColumnNumber];
		tileWasRendered = new Boolean[boardRowNumber][boardColumnNumber];
		sectionArray = new SectionInfo [boardRowNumber*3][boardColumnNumber*3];		
		
		for(int row=0; row<this.boardRowNumber; row++){
			for(int column=0; column<this.boardColumnNumber; column++){
				tileTracker[row][column] = false;	
				tileWasRendered [row][column] = false;
				
			}
		}	
		for(int row=0; row<this.boardRowNumber*3; row++)
		{
			for(int column=0; column<this.boardColumnNumber*3; column++)
			{
				sectionArray[row][column]= new SectionInfo();
				sectionArray[row][column].tiger=0;
				sectionArray[row][column].count=-1;
				sectionArray[row][column].terrain="";				
				sectionArray[row][column].upTravercable=true;
				sectionArray[row][column].rightTravercable=true;
				sectionArray[row][column].botTravercable=true;
				sectionArray[row][column].leftTravercable=true;
			}				
		}
		this.PlayerOneScore =0;
		this.PlayerTwoScore = 0;
		this.PlayerOneMeepleCount =7;
		this.PlayerTwoMeepleCount =7;
		System.out.println("CHECKPOINT: Board Initialization complete");
		count=0;
	}
	int placeFirstCard()
	{
		// the starting card is the same every game, this card is NOT included in deck that is given to the players
		Card startingCard = new Card("game-trail", "lake", "game-trail", "jungle", false, false, false, false, false, false);
		startingCard.imagePath="19.png";
		// to start off, we will place the card at a position in the middle of the board which will be at [74][74]
		
		// we will have to pick a random orientation for the card to start off with, this will generate either 0,1,2 or 3
		int startingOrientation = (int)(Math.random()*4);
		
		// we pass in the first card, the orientation, position, position, and it's neighors, therea are none at the start so all 4 are set to false
		Tile firstTile = new Tile(startingCard, startingOrientation, (int)boardRowNumber/2, (int)boardColumnNumber/2, false, false, false, false);
		actualPlacementTracker.add(new Coordinates((int)boardRowNumber/2, (int)boardColumnNumber/2));//add coordinates of placed tile 
		findEmptyTilesAround((int)boardRowNumber/2, (int)boardColumnNumber/2);//updates possiblePlacementTracker
//		System.out.println("actualPlacementTracker.size() "+actualPlacementTracker.size());
		
		// now we position it in the 2D array that stores all the tiles
		this.tileArray[(int)boardRowNumber/2][(int)boardColumnNumber/2] = firstTile;
		
		// after it is placed, we check if off
		this.tileArray[(int)boardRowNumber/2][(int)boardColumnNumber/2].isPlacedOnBoard = true;
		
		// last, we update the 2D array that stores if a information if something is stored at a location
		this.tileTracker[(int)boardRowNumber/2][(int)boardColumnNumber/2] = true;

		System.out.println("CHECKPOINT: First tile is placed on the board");
		System.out.println("TILE INFO: STARTING TILE PLACED AT LOCATION: "+(int)boardRowNumber/2+" "+(int)boardColumnNumber/2+"  WITH 0 NEIGHBORS AND PROPERTIES: " +startingCard.terrainOnSide.up+ " "
													   +startingCard.terrainOnSide.right+ " "
													   +startingCard.terrainOnSide.bottom+ " "
													   +startingCard.terrainOnSide.left);
		// return starting orientation so that the game server can send first move information to each of the players
		addSectionTerrain(new PlayerMoveInformation(startingCard, (int)boardRowNumber/2, (int)boardColumnNumber/2, startingOrientation, false, 0, 0));
		return startingOrientation;
	}
	
	// method that will be used to check if the card can be placed in at least 1 position on the board
	public boolean canCardBePlaced(Card nextDrawnCard){
		// TODO implement this method, right now returning true, but in some cases might be false
		
		return true;
	}
	public void placeCard(PlayerMoveInformation response)
	{
		tileArray[IO.row][IO.column]= new Tile(IO.currentCrad,IO.rotation,IO.row,IO.column,false,false,false,false);
 		actualPlacementTracker.add(new Coordinates(IO.row,IO.column));
 		tileWasRendered [IO.row][IO.column]= false;
 		tileTracker [IO.row][IO.column]= true;
 		for(int g=0;g<IO.board.possiblePlacementTracker.size();g++)
 		{
 			if(possiblePlacementTracker.get(g).row==response.row&&possiblePlacementTracker.get(g).column==IO.column)
 			{
 				possiblePlacementTracker.remove(g);
 			}
 		}
 		findEmptyTilesAround(IO.row, IO.column);
 		
	}
	public void placeTiger(PlayerMoveInformation response,int currentPlayer)
	{
		sectionArray[response.tigerLocationRow][response.tigerLocationColumn].tiger=currentPlayer;
//		System.out.println(response.tigerLocationRow+ " FFF "+ response.tigerLocationColumn);

//				System.out.print("section terrain"+sectionArray[response.tigerLocationRow][response.tigerLocationColumn].terrain+" ");
		count++;
		traverce500by500(response.tigerLocationRow,response.tigerLocationColumn, currentPlayer+1,count);
		count++;
				
	}
	public void addSectionTerrain(PlayerMoveInformation response)//called only after placeCard()
	{
//		System.out.println("-------------------terrain sections will be added now----------------------");
		int row = response.row;
		int column = response.column;
		int gameTrailCount=0, jungleCount=0, lakeCount=0;
		Tile currentTile = tileArray[row][column];
		//assign terrain to central section of each side
		SectionInfo sectionArrayTmpRowColumnsExchanged[][] = new SectionInfo [3][3];
		for(int iRow=0;iRow<3;iRow++)
		{
			for(int jColumn=0;jColumn<3;jColumn++)
			{
				sectionArrayTmpRowColumnsExchanged[iRow][jColumn] = new SectionInfo();
				sectionArrayTmpRowColumnsExchanged[iRow][jColumn].upTravercable=true;
				sectionArrayTmpRowColumnsExchanged[iRow][jColumn].rightTravercable=true;
				sectionArrayTmpRowColumnsExchanged[iRow][jColumn].botTravercable=true;
				sectionArrayTmpRowColumnsExchanged[iRow][jColumn].leftTravercable=true;
			}
		}
		sectionArrayTmpRowColumnsExchanged[1][0].terrain=currentTile.finalPlacedOrientation.up;
		sectionArrayTmpRowColumnsExchanged[2][1].terrain=currentTile.finalPlacedOrientation.right;
		sectionArrayTmpRowColumnsExchanged[1][2].terrain=currentTile.finalPlacedOrientation.bottom;
		sectionArrayTmpRowColumnsExchanged[0][1].terrain=currentTile.finalPlacedOrientation.left;
		int dr[]={1,2,1,0};
		int dc[]={0,1,2,1};
		boolean adjacentUnconnectedLakesCard=false;
		if(		tileArray[row][column].terrainOnSide.up.equals("jungle")&&
				tileArray[row][column].terrainOnSide.right.equals("lake")&&
				tileArray[row][column].terrainOnSide.bottom.equals("lake")&&
				tileArray[row][column].terrainOnSide.left.equals("jungle"))
		{
			System.out.println("this weird card with lakes");
			adjacentUnconnectedLakesCard=true;
			
		}
		for(int g=0;g<4;g++)
		{
			if(sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].terrain.equals("game-trail"))
			{
				gameTrailCount++;
				if(dr[g]==1)
				{
					sectionArrayTmpRowColumnsExchanged[dr[g]+1][dc[g]].terrain="jungle";
					sectionArrayTmpRowColumnsExchanged[dr[g]-1][dc[g]].terrain="jungle";
				}
				else
				{
					sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+1].terrain="jungle";
					sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-1].terrain="jungle";
				}				
			}
			else if(sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].terrain.equals("jungle"))
			{
				jungleCount++;
				if(dr[g]==1)
				{
					sectionArrayTmpRowColumnsExchanged[dr[g]+1][dc[g]].terrain="jungle";
					sectionArrayTmpRowColumnsExchanged[dr[g]-1][dc[g]].terrain="jungle";
				}
				else
				{
					sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+1].terrain="jungle";
					sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-1].terrain="jungle";
				}
			}
			else if(sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].terrain.equals("lake"))
			{
				lakeCount++;
				if(!adjacentUnconnectedLakesCard)
				{
					if(sectionArrayTmpRowColumnsExchanged[dr[(g+1)%4]][dc[(g+1)%4]].terrain.equals("lake"))//here we assign section that goes next on counterclockwise dir.from middle section with lake
					{
						if(dr[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].terrain="lake";							
						}
						else
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].terrain="lake";							
						}
					}
					else 
					{System.out.println("here61");
						if(dr[g]==1&&dc[g]==0)
						{
//							System.out.println("here4");
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].upTravercable=false;							
						}
						else if(dr[g]==1&&dc[g]==2)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].botTravercable=false;
						}
						else if(dr[g]==0&&dc[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].leftTravercable=false;							
						}
						else if(dr[g]==2&&dc[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].rightTravercable=false;	
						}
					}
					
					if(sectionArrayTmpRowColumnsExchanged[dr[Math.abs((g-1+4)%4)]][dc[Math.abs((g-1+4)%4)]].terrain.equals("lake"))
					{
						if(dr[g]==1)
						{System.out.println("here331");
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].terrain="lake";																 
						}
						else
						{System.out.println("herewww");
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].terrain="lake";															 
						}
					}
					else 
					{	
						System.out.println("here1");
						if(dr[g]==1&&dc[g]==0)
						{							
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].upTravercable=false;							
						}
						else if(dr[g]==1&&dc[g]==2)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].botTravercable=false;
						}
						else if(dr[g]==0&&dc[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].leftTravercable=false;	
						}
						else if(dr[g]==2&&dc[g]==1)
						{							
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].rightTravercable=false;							
						}
					}
				}
				else
				{
					if(sectionArrayTmpRowColumnsExchanged[dr[(g+1)%4]][dc[(g+1)%4]].terrain.equals("lake"))
					{
						if(dr[g]==1&&dr[g]==0)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].terrain="jungle";	
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].upTravercable=false;
						}
						else if(dr[g]==1&&dr[g]==2)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].terrain="jungle";	
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].botTravercable=false;
						}
						else if(dr[g]==0&&dr[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].terrain="jungle";
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].leftTravercable=false;
						}
						else if(dr[g]==2&&dr[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].terrain="jungle";
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].rightTravercable=false;
						}
					}
					else
					{
						if(dr[g]==1&&dr[g]==0)
						{								
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].upTravercable=false;
						}
						else if(dr[g]==1&&dr[g]==2)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].botTravercable=false;
						}
						else if(dr[g]==0&&dr[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].leftTravercable=false;
						}
						else if(dr[g]==2&&dr[g]==1)
						{							
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].rightTravercable=false;
						}
					}
					if(sectionArrayTmpRowColumnsExchanged[dr[Math.abs((g-1+4)%4)]][dc[Math.abs((g-1+4)%4)]].terrain.equals("lake"))
					{
						if(dr[g]==1&&dr[g]==0)
						{		
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].terrain="jungle";
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].upTravercable=false;
						}
						else if(dr[g]==1&&dr[g]==2)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].terrain="jungle";
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].botTravercable=false;
						}
						else if(dr[g]==0&&dr[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].terrain="jungle";
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].leftTravercable=false;
						}
						else if(dr[g]==2&&dr[g]==1)
						{							
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].terrain="jungle";
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].rightTravercable=false;
						}
					}
					else
					{

						if(dr[g]==1&&dr[g]==0)
						{		
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].upTravercable=false;
						}
						else if(dr[g]==1&&dr[g]==2)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].botTravercable=false;
						}
						else if(dr[g]==0&&dr[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].leftTravercable=false;
						}
						else if(dr[g]==2&&dr[g]==1)
						{							
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].rightTravercable=false;
						}
					}
					
				}
			}
			else
			{
				System.out.println("FATAL ERROR : board addSectionTerrain: unknown terrain");
				System.exit(0);
			}
		}
		//now we handle central sgment
		if(currentTile.den)
		{
			sectionArrayTmpRowColumnsExchanged[1][1].terrain="den";
		}

		else if(gameTrailCount==2)
		{
			sectionArrayTmpRowColumnsExchanged[1][1].terrain="game-trail";
		}
		else if(gameTrailCount>2)
		{
			sectionArrayTmpRowColumnsExchanged[1][1].terrain="game-trailEnd";
		}
		else if(gameTrailCount==1&&lakeCount==1)
		{
			sectionArrayTmpRowColumnsExchanged[1][1].terrain="game-trail";
		}
		else if(currentTile.junglesConnected)
		{
			sectionArrayTmpRowColumnsExchanged[1][1].terrain="jungle";
		}
		else if(currentTile.lakesConnected)
		{
			sectionArrayTmpRowColumnsExchanged[1][1].terrain="lake";
		}
		else if(jungleCount==4)
		{
			sectionArrayTmpRowColumnsExchanged[1][1].terrain="jungle";
		}
		else if(lakeCount==4)
		{
			sectionArrayTmpRowColumnsExchanged[1][1].terrain="lake";
		}
		else
		{
			sectionArrayTmpRowColumnsExchanged[1][1].terrain="jungle";
		}
		

		for(int iRow=0;iRow<3;iRow++)
		{
			for(int jColumn=0;jColumn<3;jColumn++)
			{
				sectionArray[row*3+iRow][column*3+jColumn] = sectionArrayTmpRowColumnsExchanged[jColumn][iRow];
			}
		}
//		System.out.println(row*3+" + "+column*3);
//		System.out.println(sectionArray[row*3][column*3].terrain+" "+sectionArray[row*3][column*3+1].terrain+" "+sectionArray[row*3][column*3+2].terrain );
//		System.out.println(sectionArray[row*3+1][column*3].terrain+" "+sectionArray[row*3+1][column*3+1].terrain+" "+sectionArray[row*3+1][column*3+2].terrain );
//		System.out.println(sectionArray[row*3+2][column*3].terrain+" "+sectionArray[row*3+2][column*3+1].terrain+" "+sectionArray[row*3+2][column*3+2].terrain );
//		System.out.println("-------------------terrain section were added----------------------");
		generatePossibleTigerSlots(response);
	}
	public Vector<Coordinates> generatePossibleTigerSlots(PlayerMoveInformation response)
	{
//		System.out.println();
//		System.out.println("-------------------possilbe tiger slots will be generated now----------------------");
		int row = response.row;
		int column = response.column;
		int slotCount=0;
		Tile currentTile = tileArray[row][column];
		Vector<Coordinates> possibleTigerSlots;
		possibleTigerSlots =new Vector<Coordinates>(0);
		int sectionAddedToSlot[][] = new int [3][3];
		boolean adjacentUnconnectedLakesCard=false;
		if(		tileArray[row][column].terrainOnSide.up.equals("jungle")&&
				tileArray[row][column].terrainOnSide.right.equals("lake")&&
				tileArray[row][column].terrainOnSide.bottom.equals("lake")&&
				tileArray[row][column].terrainOnSide.left.equals("jungle"))
		{
//			System.out.println("this weird card with lakes");
			adjacentUnconnectedLakesCard=true;
		}
		for(int iRow=0;iRow<3;iRow++)
		{
			for(int jColumn=0;jColumn<3;jColumn++)
			{
				sectionAddedToSlot[iRow][jColumn]=-1;
			}
		}
		
		for(int iRow=0;iRow<3;iRow++)
		{
			for(int jColumn=0;jColumn<3;jColumn++)
			{
				if(sectionAddedToSlot[iRow][jColumn]==-1)
				{
					sectionAddedToSlot[iRow][jColumn]=slotCount;
					slotCount++;
				}
				traverce3by3(sectionAddedToSlot,iRow,jColumn,slotCount,row*3,column*3);
			}
		}
//		System.out.println(sectionAddedToSlot[0][0]+" "+sectionAddedToSlot[0][1]+" "+sectionAddedToSlot[0][2]);
//		System.out.println(sectionAddedToSlot[1][0]+" "+sectionAddedToSlot[1][1]+" "+sectionAddedToSlot[1][2]);
//		System.out.println(sectionAddedToSlot[2][0]+" "+sectionAddedToSlot[2][1]+" "+sectionAddedToSlot[2][2]);
//		System.out.println("-------------------possilbe tiger slots were generated----------------------");
		int currentSlotNumber=0;
		for(int iRow=0;iRow<3;iRow++)
		{
			for(int jColumn=0;jColumn<3;jColumn++)
			{
				if(sectionAddedToSlot[iRow][jColumn]==currentSlotNumber)
				{
					possibleTigerSlots.add(new Coordinates(iRow,jColumn,currentSlotNumber));
					currentSlotNumber++;
				}				
			}
		}
		if(adjacentUnconnectedLakesCard)//needs to be doublechecked
		{
			if(tileArray[row][column].rotation==0)
				possibleTigerSlots.remove(3);
			if(tileArray[row][column].rotation==1)
				possibleTigerSlots.remove(2);
			if(tileArray[row][column].rotation==2)
				possibleTigerSlots.remove(2);
			if(tileArray[row][column].rotation==3)
				possibleTigerSlots.remove(2);
		}
//		for(int g=0;g<possibleTigerSlots.size();g++)
//		{
//			System.out.println(possibleTigerSlots.get(g).row+" "+possibleTigerSlots.get(g).column);
//		}
		for(int h=0;h<possibleTigerSlots.size();h++)
		{
			if(sectionArray[possibleTigerSlots.get(h).row+row*3][possibleTigerSlots.get(h).column+column*3].terrain.equals("game-trailEnd"))
			{
//				System.out.println("__________________________REMOVED__________________________");
				possibleTigerSlots.remove(h);
			}
		}
		if(row>0)//we look at sections adjacent to our 3*3 array of sections to see if this adjacent sections belong to players
		{
			
			for(int g=0;g<3;g++)
			{
				if(sectionArray[row*3-1][column*3+g].tiger!=0&&sectionArray[row*3][column*3+g].terrain.equals(sectionArray[row*3-1][column*3+g].terrain)&&
						sectionArray[row*3][column*3+g].upTravercable)
				{	
//					System.out.println("trav top");
					count++;
					traverce500by500(row*3-1, column*3+g, sectionArray[row*3-1][column*3+g].tiger,count);
					count++;
					for(int h=0;h<possibleTigerSlots.size();h++)
					{
						if(possibleTigerSlots.get(h).count==sectionAddedToSlot[0][g])
						{
							possibleTigerSlots.remove(h);
							break;
						}
					}
				}
			}
		}		
		if(column>0)//we look at sections adjacent to our 3*3 array of sections to see if this adjacent sections belong to players
		{
			for(int g=0;g<3;g++)
			{
//				System.out.println(sectionArray[row*3+g][column*3].terrain+ " t " +sectionArray[row*3+g][column*3-1].terrain);
//				System.out.println("tiger = "+sectionArray[row*3+g][column*3-1].tiger);
//				System.out.println((row*3+g)+" "+(column*3-1));
				
				if(sectionArray[row*3+g][column*3-1].tiger!=0&&sectionArray[row*3+g][column*3].terrain.equals(sectionArray[row*3+g][column*3-1].terrain)&&
						sectionArray[row*3+g][column*3].leftTravercable)
				{
//					System.out.println("trav left");
					count++;
					traverce500by500(row*3+g, column*3-1, sectionArray[row*3+g][column*3-1].tiger,count);
					count++;
					for(int h=0;h<possibleTigerSlots.size();h++)
					{
						if(possibleTigerSlots.get(h).count==sectionAddedToSlot[g][0])
						{
							possibleTigerSlots.remove(h);
							break;
						}
					}
				}
			}
		}		
		if(row<boardRowNumber-1)//we look at sections adjacent to our 3*3 array of sections to see if this adjacent sections belong to players
		{
			for(int g=0;g<3;g++)
			{
//				System.out.println(sectionArray[row*3+2][column*3+g].terrain+ " t " +sectionArray[row*3+4][column*3+g].terrain);
//				System.out.println("tiger = "+sectionArray[row*3+g][column*3+4].tiger);
//				System.out.println((row*3+g)+" "+(column*3+4));
				
				if(sectionArray[row*3+3][column*3+g].tiger!=0&&sectionArray[row*3+2][column*3+g].terrain.equals(sectionArray[row*3+3][column*3+g].terrain)&&
						sectionArray[row*3+2][column*3+g].botTravercable)
				{
//					System.out.println("trav  bot");
					count++;
					traverce500by500(row*3+3, column*3+g, sectionArray[row*3+3][column*3+g].tiger,count);
					count++;
					for(int h=0;h<possibleTigerSlots.size();h++)
					{
						if(possibleTigerSlots.get(h).count==sectionAddedToSlot[2][g])
						{
							possibleTigerSlots.remove(h);
							break;
						}
					}
				}
			}
		}		
		if(column<boardColumnNumber-1)//we look at sections adjacent to our 3*3 array of sections to see if this adjacent sections belong to players
		{
			for(int g=0;g<3;g++)
			{
//				System.out.println(sectionArray[row*3+g][column*3+3].terrain+ " t " +sectionArray[row*3+g][column*3+4].terrain);
//				System.out.println("tiger = "+sectionArray[row*3+g][column*3+4].tiger);
//				System.out.println((row*3+g)+" "+(column*3+4));
				
				if(sectionArray[row*3+g][column*3+3].tiger!=0&&sectionArray[row*3+g][column*3+2].terrain.equals(sectionArray[row*3+g][column*3+3].terrain)&&
						sectionArray[row*3+g][column*3+2].rightTravercable)
				{
//					System.out.println("trav right");
					count++;
					traverce500by500(row*3+g, column*3+3, sectionArray[row*3+g][column*3+3].tiger,count);
					count++;
					for(int h=0;h<possibleTigerSlots.size();h++)
					{					
						if(possibleTigerSlots.get(h).count==sectionAddedToSlot[g][2])
						{
							possibleTigerSlots.remove(h);
							break;
						}
					}
				}
			}
		}
//		System.out.println();
		for(int h=0;h<possibleTigerSlots.size();h++)
		{
			possibleTigerSlots.get(h).row+=row*3;
			possibleTigerSlots.get(h).column+=column*3;
		}
		return possibleTigerSlots;
	}
	public void traverce3by3(int sectionAddedToSlot[][],int row,int column,int count,int row3,int column3)
	{		
		if(row>0)
		{
			if(sectionAddedToSlot[row-1][column]==-1)
				if(sectionArray[row3+row][column3+column].terrain.equals(sectionArray[row3+row-1][column3+column].terrain))
				{
//					System.out.println("row>0"+sectionAddedToSlot[row][column]);
					sectionAddedToSlot[row-1][column]=sectionAddedToSlot[row][column];
					traverce3by3(sectionAddedToSlot,row-1,column,count,row3,column3);
				}
		}
		if(column>0)
		{
			if(sectionAddedToSlot[row][column-1]==-1)
				if(sectionArray[row3+row][column3+column].terrain.equals(sectionArray[row3+row][column3+column-1].terrain))
				{
//					System.out.println("col>0");
					sectionAddedToSlot[row][column-1]=sectionAddedToSlot[row][column];
					traverce3by3(sectionAddedToSlot,row,column-1,count,row3,column3);
				}
		}
		if(row<2)
		{
			if(sectionAddedToSlot[row+1][column]==-1)
				if(sectionArray[row3+row][column3+column].terrain.equals(sectionArray[row3+row+1][column3+column].terrain))
				{
					sectionAddedToSlot[row+1][column]=sectionAddedToSlot[row][column];
					traverce3by3(sectionAddedToSlot,row+1,column,count,row3,column3);
				}
		}
		if(column<2)
		{
			if(sectionAddedToSlot[row][column+1]==-1)
				if(sectionArray[row3+row][column3+column].terrain.equals(sectionArray[row3+row][column3+column+1].terrain))
				{
					sectionAddedToSlot[row][column+1]=sectionAddedToSlot[row][column];
					traverce3by3(sectionAddedToSlot,row,column+1,count,row3,column3);
				}
		}
	}
	public boolean isMoveValid(PlayerMoveInformation response){
		// TODO implement this method, given the response from the player, we need to check if the card position, orientation and meeple is valid
//		System.out.println("Start verification");		
		// first check if a tile is placed at the current location	
		if(tileTracker[response.row][response.column] == true){
			// something already placed, cannot place anything new
			System.out.println("Tile is taken");	
			return false;
		}
		
		int row = response.row;
		int column = response.column;
		//we form arrays of terrain names to make work with them more convenient
		String adjecentSides [] = new String [4];//this terrain around current card
		String currentSides [] = new String [4];//this terrain of current card
		
		currentSides [(0+response.orientation)%4] = response.card.terrainOnSide.up;
		currentSides [(1+response.orientation)%4] = response.card.terrainOnSide.right;
		currentSides [(2+response.orientation)%4] = response.card.terrainOnSide.bottom;
		currentSides [(3+response.orientation)%4] = response.card.terrainOnSide.left;
		
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
//		System.out.println(currentSides[0]+" "+currentSides[1]+" "+currentSides[2]+" "+currentSides[3]);
//		System.out.println(adjecentSides[0]+" "+adjecentSides[1]+" "+adjecentSides[2]+" "+adjecentSides[3]);
		for(int k=0;k<4;k++)
		{
			if(adjecentSides[k]==null)
			{
				
			}
			else if(adjecentSides[k].equals(currentSides[k]))
			{
				
			}
			else
			{
//				System.out.println("----------------------------Verification failed-----------------------------");
				return false;
			}
		}
//		System.out.println("Move verified");
		return true;	
	}

	public ArrayList<PlacementPossibility> generatePossibleCardPlacements(Card card)//here  we generate possible placment
	{
//		System.out.println(tileArray[77][77].finalPlacedOrientation.up+tileArray[77][77].finalPlacedOrientation.right+tileArray[77][77].finalPlacedOrientation.bottom+tileArray[77][77].finalPlacedOrientation.left);
		ArrayList<PlacementPossibility> possibilities = new ArrayList<PlacementPossibility>();	

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
		return possibilities;
	}
	
	// method used by the mock server to update it's version of the board or by the player currently making the move
	public ScoreUpdateInformation updateBoardFromPlayerMoveInformation(PlayerMoveInformation response){
		
		// When updating the board, we must indicate if there are any neighbors 
		boolean northNeighbor = false;
		boolean eastNeighbor = false;
		boolean southNeighbor = false;
		boolean westNeighbor = false;
		if(this.tileTracker[response.row][response.column+1] == true){
			northNeighbor = true;
		}
		if(this.tileTracker[response.row+1][response.column] == true){
			eastNeighbor = true;
		}
		if(this.tileTracker[response.row][response.column-1] == true){
			southNeighbor = true;
		}
		if(this.tileTracker[response.row-1][response.column+1] == true){
			westNeighbor = true;
		}	

		// create new tile 	
		Tile tileToPlace = new Tile(response.card, response.row, response.column, response.orientation, northNeighbor, eastNeighbor, southNeighbor, westNeighbor); 
		
		// mark as placed with tileTracker array
		this.tileTracker[response.row][response.column] = true;

		// now position new tile and mark it off as set
		this.tileArray[response.row][response.column] = tileToPlace;
		this.tileArray[response.row][response.column].isPlacedOnBoard = true;

		// boolean used to denote if there is a change in score count for players or meeple count 
		// TODO determine if change in score or meeple count
		boolean updateToScore = true;
		
		if(updateToScore){
			// if the score changes, server needs to tell players about the changes,
			// we must package the changes to the scores
			
			// TODO
			// right now scores for each players update by 1 and meeples returned is both 0
			ScoreUpdateInformation scoreUpdateInfo = new ScoreUpdateInformation(1,1,0,0);
			return scoreUpdateInfo;
			
		}
		else{
			// no change in score
			ScoreUpdateInformation scoreUpdateInfo = new ScoreUpdateInformation(0,0,0,0);
			return scoreUpdateInfo;
		}	
		
	}

	/* this method is called by the other player who is not currently making a move to update their board
	after the server returns a response, the response informaition contains info regarding what the other player
	just did, where their card was placed, orientation etc. 
	*/
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
	public void traverce500by500(int row,int column, int player, int count)
	{
		sectionArray[row][column].tiger=player;
		sectionArray[row][column].count=count;
		if(sectionArray[row][column].terrain.equals(""))
		{
			return;
		}
		if(row>0&&sectionArray[row][column].upTravercable)
		{
//			System.out.println(sectionArray[row-1][column].count+" up "+count);
//			System.out.println(sectionArray[row][column].terrain+" e "+sectionArray[row-1][column].terrain);
			if(sectionArray[row-1][column].count!=count&&sectionArray[row][column].terrain.equals(sectionArray[row-1][column].terrain))
			{
//				System.out.println("up");

				traverce500by500(row-1,column,player,count);
			}
		}
		if(column>0&&sectionArray[row][column].leftTravercable)
		{
//			System.out.println(sectionArray[row][column-1].count+" left "+count);
			if(sectionArray[row][column-1].count!=count&&sectionArray[row][column].terrain.equals(sectionArray[row][column-1].terrain))
			{
//				System.out.println("left");
				traverce500by500(row,column-1,player,count);
			}
		}
		if(row<boardRowNumber*3-1&&sectionArray[row][column].botTravercable)
		{
//			System.out.println(sectionArray[row+1][column].count+" down "+count);
			if(sectionArray[row+1][column].count!=count&&sectionArray[row][column].terrain.equals(sectionArray[row+1][column].terrain))
			{
//				System.out.println("down");
				traverce500by500(row+1,column,player,count);
			}
		}
		if(column<boardColumnNumber*3-1&&sectionArray[row][column].rightTravercable)
		{
//			System.out.println(sectionArray[row][column+1].count+" right "+count);
			if(sectionArray[row][column+1].count!=count&&sectionArray[row][column].terrain.equals(sectionArray[row][column+1].terrain))
			{
//				System.out.println("right");
				traverce500by500(row,column+1,player,count);
			}
		}
	}

}


