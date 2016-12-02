package mainPackage;
import java.util.ArrayList;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.TitlePaneLayout;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.util.*;
public class Board {
	/*FORMAT FOR NOTATIONS :row/column, upper left corner is [0 0],rotation is clockwise*/
	public Tile tileArray [][];	 //row/column, upper left corner is 0 0
	public Boolean tileTracker[][];//says if particular tile was placed //row/column, upper left corner is 0 0
	public Boolean tileWasRendered[][]; 
	public SectionInfo [][] sectionArray;//holds info about all tile sections 
	public int boardColumnNumber;
	public int boardRowNumber;	
	public int count,count2;
	public Tile centralTile;	
	public volatile Vector<Coordinates> possiblePlacementTracker; //hold location of places where tile can be placed this turn
	public volatile Vector<Coordinates> actualPlacementTracker; //hold location of placed tiles
	
	Board()
	{
		this.boardColumnNumber = 155;
		this.boardRowNumber = 155;		
		tileArray = new Tile [boardRowNumber][boardColumnNumber];		
		possiblePlacementTracker = new Vector<Coordinates>(0);
		actualPlacementTracker = new Vector<Coordinates>(0);
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
				sectionArray[row][column].count2=-1;
				sectionArray[row][column].terrain="";				
				sectionArray[row][column].upTravercable=true;
				sectionArray[row][column].rightTravercable=true;
				sectionArray[row][column].botTravercable=true;
				sectionArray[row][column].leftTravercable=true;
				sectionArray[row][column].lakeUpTrav=false;
				sectionArray[row][column].lakeRightTrav=false;
				sectionArray[row][column].lakeBotTrav=false;
				sectionArray[row][column].lakeLeftTrav=false;
			}				
		}
		System.out.println("CHECKPOINT: Board Initialization complete");
		count=0;
		count2=0;
	}
	Board(Board board)
	{
		this.tileArray=board.tileArray.clone();
		this.tileTracker=board.tileTracker.clone();
		this.tileWasRendered=board.tileWasRendered.clone(); 
		this.sectionArray=board.sectionArray.clone();
		this.boardColumnNumber=board.boardColumnNumber;
		this.boardRowNumber=board.boardRowNumber;	
		this.count=board.count;
		this.count=board.count2;
		this.centralTile=board.centralTile;	
		this.possiblePlacementTracker=new Vector<Coordinates> (board.possiblePlacementTracker);
		this.actualPlacementTracker=new Vector<Coordinates> (board.actualPlacementTracker);
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
	int placeFirstCard(Card card, int row, int column, int rotation)
	{
		return 0;
	}
	// method that will be used to check if the card can be placed in at least 1 position on the board
	public boolean canCardBePlaced(Card nextDrawnCard){
		// TODO implement this method, right now returning true, but in some cases might be false
		
		return true;
	}
	//places card according to info in response
	public void placeCard(PlayerMoveInformation response)
	{
		if(response.row==-1&&response.column==-1)
		{
			return;
		}
		tileArray[response.row][response.column]= new Tile(response.card,response.orientation,response.row,response.column,false,false,false,false);
 		actualPlacementTracker.add(new Coordinates(response.row,response.column));
 		tileWasRendered [response.row][response.column]= false;
 		tileTracker [response.row][response.column]= true;
 		for(int g=0;g<possiblePlacementTracker.size();g++)
 		{
 			if(possiblePlacementTracker.get(g).row==response.row&&
 					possiblePlacementTracker.get(g).column==response.column)
 			{
 				possiblePlacementTracker.remove(g);
 			}
 		}
 		findEmptyTilesAround(response.row, response.column);
 		addSectionTerrain(response); 	
 		addPlayerInfoToSectors(response);
	}
	//places tiger according to response info
	public void placeTiger(PlayerMoveInformation response,int currentPlayer)
	{
		if(response.row==-1&&response.column==-1)
		{
			return;
		}
		if(response.tigerPlaced)
		{
			sectionArray[response.tigerLocationRow][response.tigerLocationColumn].tiger=currentPlayer;
			count++;
			traverce500by500(response.tigerLocationRow,response.tigerLocationColumn, currentPlayer+1,count,sectionArray[response.tigerLocationRow][response.tigerLocationColumn].terrain);
			count++;
		}				
	}
	//after card was placed we update our section array that hold terrain type for each of 9 sections of all tiles
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
				sectionArrayTmpRowColumnsExchanged[iRow][jColumn].lakeUpTrav=false;
				sectionArrayTmpRowColumnsExchanged[iRow][jColumn].lakeRightTrav=false;
				sectionArrayTmpRowColumnsExchanged[iRow][jColumn].lakeBotTrav=false;
				sectionArrayTmpRowColumnsExchanged[iRow][jColumn].lakeLeftTrav=false;
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
					else if(sectionArrayTmpRowColumnsExchanged[dr[(g+1)%4]][dc[(g+1)%4]].terrain.equals("game-trail"))
					{
						if(dr[g]==1&&dc[g]==0)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].upTravercable=false;	
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeLeftTrav=true;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeRightTrav=true;
						}
						else if(dr[g]==1&&dc[g]==2)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].botTravercable=false;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeLeftTrav=true;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeRightTrav=true;
						}
						else if(dr[g]==0&&dc[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].leftTravercable=false;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeUpTrav=true;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeBotTrav=true;
						}
						else if(dr[g]==2&&dc[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].rightTravercable=false;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeUpTrav=true;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeBotTrav=true;
						}
					}
					else 
					{
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
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].terrain="lake";																 
						}
						else
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].terrain="lake";															 
						}
					}
					else if(sectionArrayTmpRowColumnsExchanged[dr[Math.abs((g-1+4)%4)]][dc[Math.abs((g-1+4)%4)]].terrain.equals("game-trail"))
					{
						if(dr[g]==1&&dc[g]==0)
						{								
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].upTravercable=false;	
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeLeftTrav=true;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeRightTrav=true;
						}
						else if(dr[g]==1&&dc[g]==2)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].botTravercable=false;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeLeftTrav=true;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeRightTrav=true;
						}
						else if(dr[g]==0&&dc[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].leftTravercable=false;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeUpTrav=true;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeBotTrav=true;
						}
						else if(dr[g]==2&&dc[g]==1)
						{							
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].rightTravercable=false;		
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeUpTrav=true;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeBotTrav=true;
						}
						if(sectionArrayTmpRowColumnsExchanged[dr[Math.abs((g-1+4)%4)]][dc[Math.abs((g-1+4)%4)]].terrain.equals("game-trail")&&
						   sectionArrayTmpRowColumnsExchanged[dr[(g+1)%4]][dc[(g+1)%4]].terrain.equals("lake"))//if we have lake "between trail and jungles we need to set corner between two lakes to partiular value
						{
							if(dr[g]==1&&dc[g]==0)
							{
								sectionArrayTmpRowColumnsExchanged[2][0].lakeLeftTrav=true;	
								sectionArrayTmpRowColumnsExchanged[2][0].lakeBotTrav=true;	
							}
							else if(dr[g]==1&&dc[g]==2)
							{
								sectionArrayTmpRowColumnsExchanged[0][2].lakeUpTrav=true;
								sectionArrayTmpRowColumnsExchanged[0][2].lakeRightTrav=true;
							}
							else if(dr[g]==0&&dc[g]==1)
							{
								sectionArrayTmpRowColumnsExchanged[0][0].lakeBotTrav=true;		
								sectionArrayTmpRowColumnsExchanged[0][0].lakeRightTrav=true;	
							}
							else if(dr[g]==2&&dc[g]==1)
							{
								sectionArrayTmpRowColumnsExchanged[2][2].lakeUpTrav=true;	
								sectionArrayTmpRowColumnsExchanged[2][2].lakeLeftTrav=true;
							}
						}
						if(sectionArrayTmpRowColumnsExchanged[dr[Math.abs((g-1+4)%4)]][dc[Math.abs((g-1+4)%4)]].terrain.equals("lake")&&
								   sectionArrayTmpRowColumnsExchanged[dr[(g+1)%4]][dc[(g+1)%4]].terrain.equals("game-trail"))
						{
							if(dr[g]==1&&dc[g]==0)
							{
								sectionArrayTmpRowColumnsExchanged[0][0].lakeRightTrav=true;	
								sectionArrayTmpRowColumnsExchanged[0][0].lakeBotTrav=true;	
							}
							else if(dr[g]==1&&dc[g]==2)
							{
								sectionArrayTmpRowColumnsExchanged[2][2].lakeUpTrav=true;
								sectionArrayTmpRowColumnsExchanged[2][2].lakeLeftTrav=true;
							}
							else if(dr[g]==0&&dc[g]==1)
							{
								sectionArrayTmpRowColumnsExchanged[0][2].lakeUpTrav=true;		
								sectionArrayTmpRowColumnsExchanged[0][2].lakeRightTrav=true;	
							}
							else if(dr[g]==2&&dc[g]==1)
							{
								sectionArrayTmpRowColumnsExchanged[2][0].lakeBotTrav=true;	
								sectionArrayTmpRowColumnsExchanged[2][0].lakeLeftTrav=true;
							}
						}
					}
					else 
					{	
						
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
				else//weird card with unconnected adjacent lakes
				{
					if(sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].terrain.equals("lake"))
					{
						if(dr[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeLeftTrav=true;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeRightTrav=true;							
						}
						else
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeUpTrav=true;
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]].lakeBotTrav=true;	
						}
					}
					if(sectionArrayTmpRowColumnsExchanged[dr[(g+1)%4]][dc[(g+1)%4]].terrain.equals("lake"))
					{
						if(dr[g]==1&&dc[g]==0)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].terrain="jungle";	
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].upTravercable=false;
						}
						else if(dr[g]==1&&dc[g]==2)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].terrain="jungle";	
							sectionArrayTmpRowColumnsExchanged[dr[g]+(1-dc[g])][dc[g]].botTravercable=false;
						}
						else if(dr[g]==0&&dc[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].terrain="jungle";
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].leftTravercable=false;
						}
						else if(dr[g]==2&&dc[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].terrain="jungle";
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]-(1-dr[g])].rightTravercable=false;
						}
					}
					else
					{
						if(dr[g]==1&&dc[g]==0)
						{								
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
						if(dr[g]==1&&dr[g]==0)
						{		
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].terrain="jungle";
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].upTravercable=false;
						}
						else if(dr[g]==1&&dc[g]==2)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].terrain="jungle";
							sectionArrayTmpRowColumnsExchanged[dr[g]-(1-dc[g])][dc[g]].botTravercable=false;
						}
						else if(dr[g]==0&&dc[g]==1)
						{
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].terrain="jungle";
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].leftTravercable=false;
						}
						else if(dr[g]==2&&dc[g]==1)
						{							
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].terrain="jungle";
							sectionArrayTmpRowColumnsExchanged[dr[g]][dc[g]+(1-dr[g])].rightTravercable=false;
						}
					}
					else
					{

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
//		generatePossibleTigerSlots(response);
	}
	//we look through sections on currently placed tile and find one that are good for placement 
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
				count2++;
				traverce3by3(sectionAddedToSlot,iRow,jColumn,sectionAddedToSlot[iRow][jColumn],row*3,column*3,sectionArray[iRow+row*3][jColumn+column*3].terrain,count2);
				count2++;
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
//		if(adjacentUnconnectedLakesCard)//needs to be doublechecked
//		{
//			if(tileArray[row][column].rotation==0)
//				possibleTigerSlots.remove(2);
//			if(tileArray[row][column].rotation==1)
//				possibleTigerSlots.remove(1);
//			if(tileArray[row][column].rotation==2)
//				possibleTigerSlots.remove(1);
//			if(tileArray[row][column].rotation==3)
//				possibleTigerSlots.remove(1);
//		}
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
					traverce500by500(row*3-1, column*3+g, sectionArray[row*3-1][column*3+g].tiger,count, sectionArray[row*3-1][column*3+g].terrain);
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
					traverce500by500(row*3+g, column*3-1, sectionArray[row*3+g][column*3-1].tiger,count,sectionArray[row*3+g][column*3-1].terrain);
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
					traverce500by500(row*3+3, column*3+g, sectionArray[row*3+3][column*3+g].tiger,count,sectionArray[row*3+3][column*3+g].terrain);
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
					traverce500by500(row*3+g, column*3+3, sectionArray[row*3+g][column*3+3].tiger,count,sectionArray[row*3+g][column*3+3].terrain);
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
		for(int g=0;g<possibleTigerSlots.size();g++)
		{
			if(sectionArray[possibleTigerSlots.get(g).row][possibleTigerSlots.get(g).column].tiger!=0)
			{
				System.out.println("----------------------REMOVED-----------------------------------------------");
				possibleTigerSlots.remove(g);
			}
		}
		return possibleTigerSlots;
	}
	//works with 3by3 array of sections of single tile to group all similar adjacent terrains
	public void traverce3by3(int sectionAddedToSlot[][],int row,int column,int sectionGroupID,int row3,int column3, String currentTerrain,int count2)
	{	
		sectionArray[row3+row][column3+column].count2=count2;
		if(row>0)
		{			
				if(currentTerrain.equals(sectionArray[row3+row-1][column3+column].terrain))
				{
					if(sectionAddedToSlot[row-1][column]==-1)
					{					
						sectionAddedToSlot[row-1][column]=sectionGroupID;
						traverce3by3(sectionAddedToSlot,row-1,column,sectionGroupID,row3,column3,currentTerrain,count2);
					}
				}
				else if(currentTerrain.equals("jungle")&&sectionArray[row3+row-1][column3+column].terrain.equals("lake"))
				{
					if((sectionArray[row3+row-1][column3+column].lakeUpTrav||
					   sectionArray[row3+row-1][column3+column].lakeRightTrav||
					   sectionArray[row3+row-1][column3+column].lakeBotTrav||
					   sectionArray[row3+row-1][column3+column].lakeLeftTrav)
							&&
							sectionArray[row3+row-1][column3+column].count2!=count2)
					{
						traverce3by3(sectionAddedToSlot,row-1,column,sectionGroupID,row3,column3,currentTerrain,count2);
					}
				}
				
			
		}
		if(column>0)
		{
			
				if(currentTerrain.equals(sectionArray[row3+row][column3+column-1].terrain))
				{
					if(sectionAddedToSlot[row][column-1]==-1)
					{
//						System.out.println("col>0");
						sectionAddedToSlot[row][column-1]=sectionGroupID;
						traverce3by3(sectionAddedToSlot,row,column-1,sectionGroupID,row3,column3,currentTerrain,count2);
					}

				}
				else if(currentTerrain.equals("jungle")&&sectionArray[row3+row][column3+column-1].terrain.equals("lake"))
				{
					if((sectionArray[row3+row][column3+column-1].lakeUpTrav||
					   sectionArray[row3+row][column3+column-1].lakeRightTrav||
					   sectionArray[row3+row][column3+column-1].lakeBotTrav||
					   sectionArray[row3+row][column3+column-1].lakeLeftTrav)
							&&
							sectionArray[row3+row][column3+column-1].count2!=count2)
					{
						traverce3by3(sectionAddedToSlot,row,column-1,sectionGroupID,row3,column3,currentTerrain,count2);
					}
				}
				
		}
		if(row<2)
		{
			
				if(currentTerrain.equals(sectionArray[row3+row+1][column3+column].terrain))
				{
					if(sectionAddedToSlot[row+1][column]==-1)
					{
						sectionAddedToSlot[row+1][column]=sectionGroupID;
						traverce3by3(sectionAddedToSlot,row+1,column,sectionGroupID,row3,column3,currentTerrain,count2);
					}
		
				}
				else if(currentTerrain.equals("jungle")&&sectionArray[row3+row+1][column3+column].terrain.equals("lake"))
				{
					if((sectionArray[row3+row+1][column3+column].lakeUpTrav||
					   sectionArray[row3+row+1][column3+column].lakeRightTrav||
					   sectionArray[row3+row+1][column3+column].lakeBotTrav||
					   sectionArray[row3+row+1][column3+column].lakeLeftTrav)
							&&
							sectionArray[row3+row+1][column3+column].count2!=count2)
					{
						traverce3by3(sectionAddedToSlot,row+1,column,sectionGroupID,row3,column3,currentTerrain,count2);
					}
				}
				
			
		}
		if(column<2)
		{
			
				if(currentTerrain.equals(sectionArray[row3+row][column3+column+1].terrain))
				{
					if(sectionAddedToSlot[row][column+1]==-1)
					{
						sectionAddedToSlot[row][column+1]=sectionGroupID;
						traverce3by3(sectionAddedToSlot,row,column+1,sectionGroupID,row3,column3,currentTerrain,count2);
					}

				}
				else if(currentTerrain.equals("jungle")&&sectionArray[row3+row][column3+column+1].terrain.equals("lake"))
				{
					if((sectionArray[row3+row][column3+column+1].lakeUpTrav||
					   sectionArray[row3+row][column3+column+1].lakeRightTrav||
					   sectionArray[row3+row][column3+column+1].lakeBotTrav||
					   sectionArray[row3+row][column3+column+1].lakeLeftTrav)
							&&
							sectionArray[row3+row][column3+column+1].count2!=count2)
					{
						traverce3by3(sectionAddedToSlot,row,column+1,sectionGroupID,row3,column3,currentTerrain,count2);
					}
				}
				
		}
	}
	//checks if placement is valid
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
	//generate vector of possible placemnets
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
	//we keep track of tiles where tile can be placed with function above
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
	//traverces 465 by 465 matrix to set correct sector ownership
	public void traverce500by500(int row,int column, int player, int count, String currentTerrain)
	{
		
		if(sectionArray[row][column].terrain.equals(currentTerrain))
		{
			sectionArray[row][column].tiger=player;
		}	
		sectionArray[row][column].count=count;
		if(sectionArray[row][column].terrain.equals(""))
		{
			return;
		}
		if(row>0&&sectionArray[row][column].upTravercable)
		{
//			System.out.println(sectionArray[row-1][column].count+" up "+count);
//			System.out.println(sectionArray[row][column].terrain+" e "+sectionArray[row-1][column].terrain);
			if(!currentTerrain.equals("jungle"))
			{
				if(sectionArray[row-1][column].count!=count&&sectionArray[row][column].terrain.equals(sectionArray[row-1][column].terrain))
				{
//					System.out.println("up");

					traverce500by500(row-1,column,player,count,currentTerrain);
				}
			}
			else//in case of jungles we can traverse lakes so we need to treat this separately
			{
				if(sectionArray[row-1][column].count!=count&&	("jungle".equals(sectionArray[row-1][column].terrain)
																||
																("lake".equals(sectionArray[row-1][column].terrain)&&"jungle".equals(sectionArray[row][column].terrain)&&
																		(sectionArray[row-1][column].lakeUpTrav||sectionArray[row-1][column].lakeRightTrav||sectionArray[row-1][column].lakeBotTrav||sectionArray[row-1][column].lakeLeftTrav))
																||
																("lake".equals(sectionArray[row][column].terrain)&& sectionArray[row][column].lakeUpTrav)))
				{
//					System.out.println("up");
					traverce500by500(row-1,column,player,count,currentTerrain);
				}
			}

		}
		if(column>0&&sectionArray[row][column].leftTravercable)
		{
//			System.out.println(sectionArray[row][column-1].count+" left "+count);
			if(!currentTerrain.equals("jungle"))
			{
				if(sectionArray[row][column-1].count!=count&&sectionArray[row][column].terrain.equals(sectionArray[row][column-1].terrain))
				{
//					System.out.println("left");
					traverce500by500(row,column-1,player,count,currentTerrain);
				}
			}
			else//in case of jungles we can traverse lakes so we need to treat this separately
			{
				if(sectionArray[row][column-1].count!=count&&	("jungle".equals(sectionArray[row][column-1].terrain)
																||
																("lake".equals(sectionArray[row][column-1].terrain)&&"jungle".equals(sectionArray[row][column].terrain)&&
																		(sectionArray[row][column-1].lakeUpTrav||sectionArray[row][column-1].lakeRightTrav||sectionArray[row][column-1].lakeBotTrav||sectionArray[row][column-1].lakeLeftTrav))
																||
																("lake".equals(sectionArray[row][column].terrain)&&sectionArray[row][column].lakeLeftTrav)))
				{
//					System.out.println("left");

					traverce500by500(row,column-1,player,count,currentTerrain);
				}
			}
				


		}
		if(row<boardRowNumber*3-1&&sectionArray[row][column].botTravercable)
		{
//			System.out.println(sectionArray[row+1][column].count+" down "+count);
			if(!currentTerrain.equals("jungle"))
			{
				if(sectionArray[row+1][column].count!=count&&sectionArray[row][column].terrain.equals(sectionArray[row+1][column].terrain))
				{
//					System.out.println("down");
					traverce500by500(row+1,column,player,count,currentTerrain);
				}
			}
			else
			{
				if(sectionArray[row+1][column].count!=count&&	("jungle".equals(sectionArray[row+1][column].terrain)
																||
																("lake".equals(sectionArray[row+1][column].terrain)&&"jungle".equals(sectionArray[row][column].terrain)&&
																		(sectionArray[row+1][column].lakeUpTrav||sectionArray[row+1][column].lakeRightTrav||sectionArray[row+1][column].lakeBotTrav||sectionArray[row+1][column].lakeLeftTrav))
																||
																("lake".equals(sectionArray[row][column].terrain)&&sectionArray[row][column].lakeBotTrav)))
{
	//				System.out.println("down");
					traverce500by500(row+1,column,player,count,currentTerrain);
				}
			}

		}
		if(column<boardColumnNumber*3-1&&sectionArray[row][column].rightTravercable)
		{
//			System.out.println(sectionArray[row][column+1].count+" right "+count);
			if(!currentTerrain.equals("jungle"))
			{
				if(sectionArray[row][column+1].count!=count&&sectionArray[row][column].terrain.equals(sectionArray[row][column+1].terrain))
				{
//					System.out.println("right");
					traverce500by500(row,column+1,player,count,currentTerrain);
				}
			}
			else
			{				
				if(sectionArray[row][column+1].count!=count&&	("jungle".equals(sectionArray[row][column+1].terrain)
																||
																("lake".equals(sectionArray[row][column+1].terrain)&&"jungle".equals(sectionArray[row][column].terrain)&&
																		(sectionArray[row][column+1].lakeUpTrav||sectionArray[row][column+1].lakeRightTrav||sectionArray[row][column+1].lakeBotTrav||sectionArray[row][column+1].lakeLeftTrav))
																||
																("lake".equals(sectionArray[row][column].terrain)&&sectionArray[row][column].lakeRightTrav)))
	//				System.out.println("right");
					traverce500by500(row,column+1,player,count,currentTerrain);
				}
			}

		}
	//after card was just placed we need to assign sector ownership values to its sectors according to tiles around it
	public void addPlayerInfoToSectors(PlayerMoveInformation response)
	{
		int row = response.row;
		int column = response.column;
		if(row>0)//we look at sections adjacent to our 3*3 array of sections to see if this adjacent sections belong to players
		{
			
			for(int g=0;g<3;g++)
			{
				if(sectionArray[row*3-1][column*3+g].tiger!=0&&sectionArray[row*3][column*3+g].terrain.equals(sectionArray[row*3-1][column*3+g].terrain)&&
						sectionArray[row*3][column*3+g].upTravercable)
				{	
//					System.out.println("trav top");
					count++;
					traverce500by500(row*3-1, column*3+g, sectionArray[row*3-1][column*3+g].tiger,count, sectionArray[row*3-1][column*3+g].terrain);
					count++;					
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
					traverce500by500(row*3+g, column*3-1, sectionArray[row*3+g][column*3-1].tiger,count,sectionArray[row*3+g][column*3-1].terrain);
					count++;					
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
					traverce500by500(row*3+3, column*3+g, sectionArray[row*3+3][column*3+g].tiger,count,sectionArray[row*3+3][column*3+g].terrain);
					count++;					
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
					traverce500by500(row*3+g, column*3+3, sectionArray[row*3+g][column*3+3].tiger,count,sectionArray[row*3+g][column*3+3].terrain);
					count++;					
				}
			}
		}
	}
}




