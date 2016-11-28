

public class Tile extends Card {
	
	/* each card that is on the tile maintains its ORIGINAL Terrain information in base Card class as well 
	 * as special information stored in 6 booleans,
	 * here we keep track of placed orientation, where it is placed, tigers placed,
	 * if tigers can be placed at locations etc.
	*/
	public MeepleSlots meepleSlots;
	
	// neighbors can change as more cards are being added 
	public Neighbors neighbors;
	
	// orientation final as well, cannot be changed after placed on board
	final TerrainOnSide finalPlacedOrientation;
	// final because position cannot be changed after it is placed 
	final int column;
	final int row;
	final int rotation;
	boolean croc;
	boolean isPlacedOnBoard;
	boolean wasRendered;// this thing helps gui to work well
	
	public int[] ownershipSections;
	public String[] terrainSections;
	public boolean[] placeableSections;

	
	Tile(Card cardBeingPlaced, int rotation,int row,int column, boolean topNeighbor, boolean rightNeighbor, boolean bottomNeighbor, boolean leftNeighbor )
	{

		// original card information is stored in the parent class, what is on the board, is kept in this tile class
		super(cardBeingPlaced.CardCode);
		this.ownershipSections = new int[9];		
		this.terrainSections = new String[9];
		this.placeableSections = new boolean[9];
		this.column = column;
		this.row = row; 
		this.rotation=rotation;
		
		// After the card is placed on the TILE and BOARD, it cannot be rotated or moved
		if(rotation == 0){ 								// 0 = no rotation 
			finalPlacedOrientation = new TerrainOnSide(cardBeingPlaced.terrainOnSide.up,
													   cardBeingPlaced.terrainOnSide.right,
													   cardBeingPlaced.terrainOnSide.bottom,
													   cardBeingPlaced.terrainOnSide.left);
		}
		else if(rotation == 3){ 						// 3 = rotation 90 degrees clockwise
			finalPlacedOrientation = new TerrainOnSide(cardBeingPlaced.terrainOnSide.left,
													   cardBeingPlaced.terrainOnSide.up,
													   cardBeingPlaced.terrainOnSide.right,
													   cardBeingPlaced.terrainOnSide.bottom);
		}
		else if (rotation == 2){						//2 = rotation 180 degrees
			finalPlacedOrientation = new TerrainOnSide(cardBeingPlaced.terrainOnSide.bottom,
													   cardBeingPlaced.terrainOnSide.left,
													   cardBeingPlaced.terrainOnSide.up,
													   cardBeingPlaced.terrainOnSide.right);
		}
		else{											// rotation 270 degrees clockwise
			finalPlacedOrientation = new TerrainOnSide(cardBeingPlaced.terrainOnSide.right,
													   cardBeingPlaced.terrainOnSide.bottom,
													   cardBeingPlaced.terrainOnSide.left,
													   cardBeingPlaced.terrainOnSide.up);
		}
		
	}

	public String returnCardCode(){
		 return super.CardCode;
	}

	
	void generateMeepleSlots()//modifies meepleSlots after card placement and generates section of the 3x3 array like in the rules
	{
		int jungleCount = 0;
		int lakeCount = 0;
		int gameTrailCount = 0;
		//first check each side and assign some of the terrain sections based on them
		//this counting also takes care of game trails and valid slots for them
		switch(super.terrainOnSide.up){
			case "jungle" :
				jungleCount++;
				this.terrainSections[1] = "jungle";
				this.meepleSlots.gameTrailUp = -1;
				break;
			case "lake" :
				lakeCount++;
				this.terrainSections[1] = "lake";
				this.meepleSlots.gameTrailUp = -1;
				break;
			case "game-trail" :
				gameTrailCount++;
				this.terrainSections[1] = "game-trail";
				this.meepleSlots.gameTrailUp = 0;
				break;
			default :
				System.out.println("unable to identify terrain type on card");
		}
		
	switch(super.terrainOnSide.right){
		case "jungle" :
			jungleCount++;
			this.terrainSections[5] = "jungle";
			this.meepleSlots.gameTrailRight = -1;
			break;
		case "lake" :
			lakeCount++;
			this.terrainSections[5] = "lake";
			this.meepleSlots.gameTrailRight = -1;
			break;
		case "game-trail" :
			gameTrailCount++;
			this.terrainSections[5] = "game-trail";
			this.meepleSlots.gameTrailRight = 0;
			break;
		default :
			System.out.println("unable to identify terrain type on card");
	}
		
	switch(super.terrainOnSide.left){
		case "jungle" :
			jungleCount++;
			this.terrainSections[3] = "jungle";
			this.meepleSlots.gameTrailLeft = -1;
			break;
		case "lake" :
			lakeCount++;
			this.terrainSections[3] = "lake";
			this.meepleSlots.gameTrailLeft = -1;
			break;
		case "game-trail" :
			gameTrailCount++;
			this.terrainSections[3] = "game-trail";
			this.meepleSlots.gameTrailLeft = 0;
			break;
		default :
			System.out.println("unable to identify terrain type on card");
	}
		
	switch(super.terrainOnSide.bottom){
		case "jungle" :
			jungleCount++;
			this.terrainSections[7] = "jungle";
			this.meepleSlots.gameTrailBottom = -1;
			break;
		case "lake" :
			lakeCount++;
			this.terrainSections[7] = "lake";
			this.meepleSlots.gameTrailBottom = -1;
			break;
		case "game-trail" :
			gameTrailCount++;
			this.terrainSections[7] = "game-trail";
			this.meepleSlots.gameTrailBottom = 0;
			break;
		default :
			System.out.println("unable to identify terrain type on card");
	}
		
		
		if(super.den){
			this.meepleSlots.den = 0;
			this.terrainSections[4] = "den";
		}
		
		else{
			this.meepleSlots.den = -1;
		}
		
		//handle slots for lake
		if(super.lakesConnected){
			if(lakeCount>0){
				this.meepleSlots.lakeOne = 0;
				this.meepleSlots.lakeTwo = -1;
				 
				if((super.terrainOnSide.up.equals("lake") && super.terrainOnSide.bottom.equals("lake")) || (super.terrainOnSide.left.equals("lake") && super.terrainOnSide.right.equals("lake"))){
					this.terrainSections[4] = "lake";
				}
				 
				 
			}
			 
			if(lakeCount == 4){
				this.meepleSlots.jungleOne = -1;
				this.meepleSlots.jungleTwo = -1;
				this.meepleSlots.jungleThree = -1;
				this.meepleSlots.jungleFour = -1;
				
				this.terrainSections[4] = "lake";
			}
			 
			else if(lakeCount == 3 && gameTrailCount == 0){
				this.meepleSlots.jungleOne = 0;
				this.meepleSlots.jungleTwo = -1;
				this.meepleSlots.jungleThree = -1;
				this.meepleSlots.jungleFour = -1;
				this.terrainSections[4] = "lake";
			}
			
			else if(lakeCount == 3 && gameTrailCount == 1){
				this.meepleSlots.jungleOne = 0;
				this.meepleSlots.jungleTwo = 0;
				this.meepleSlots.jungleThree = -1;
				this.meepleSlots.jungleFour = -1;
				this.terrainSections[4] = "end-trail";
			}
			
			else if(lakeCount == 2){
				if(gameTrailCount == 0){
					//check for connected lakes that are accross from each other
					if((super.terrainOnSide.up.equals("lake")&&super.terrainOnSide.bottom.equals("lake"))||(super.terrainOnSide.left.equals("lake")&&super.terrainOnSide.right.equals("lake"))){
						this.meepleSlots.jungleOne = 0;
						this.meepleSlots.jungleTwo = 0;
						this.meepleSlots.jungleThree = -1;
						this.meepleSlots.jungleFour = -1;
						
						this.terrainSections[4] = "lake";
					}
					//otherwise it is two lakes that cut through the tile diagonally and only 1 jungle exists
					else{
						this.meepleSlots.jungleOne = 0;
						this.meepleSlots.jungleTwo = -1;
						this.meepleSlots.jungleThree = -1;
						this.meepleSlots.jungleFour = -1;
					}
				}
				
				else if(gameTrailCount ==1){
					this.meepleSlots.jungleOne = 0;
					this.meepleSlots.jungleTwo = -1;
					this.meepleSlots.jungleThree = -1;
					this.meepleSlots.jungleFour = -1;
				}
				
				else if(gameTrailCount == 2){
					this.meepleSlots.jungleOne = 0;
					this.meepleSlots.jungleTwo = 0;
					this.meepleSlots.jungleThree = -1;
					this.meepleSlots.jungleFour = -1;
				}
			}
			
			else if(lakeCount == 3){
				if(gameTrailCount == 0){
					this.meepleSlots.jungleOne = 0;
					this.meepleSlots.jungleTwo = -1;
					this.meepleSlots.jungleThree = -1;
					this.meepleSlots.jungleFour = -1;
				}
				
				else if (gameTrailCount == 1){
					this.meepleSlots.jungleOne = 0;
					this.meepleSlots.jungleTwo = 0;
					this.meepleSlots.jungleThree = -1;
					this.meepleSlots.jungleFour = -1;
				}
			}
		}
		
		else{
			if(lakeCount==0){
				this.meepleSlots.lakeOne = -1;
				this.meepleSlots.lakeTwo = -1;
			}
			else if(lakeCount==1){
				this.meepleSlots.lakeOne = 0;
				this.meepleSlots.lakeTwo = -1;
			}
			else if(lakeCount == 2){
				this.meepleSlots.lakeOne = 0;
				this.meepleSlots.lakeTwo = 0;
				
				if((super.terrainOnSide.up.equals("lake")&&super.terrainOnSide.bottom.equals("lake"))||(super.terrainOnSide.left.equals("lake")&&super.terrainOnSide.right.equals("lake"))){
					
					
					this.terrainSections[4] = "jungle";
				}
			}
			
			if((gameTrailCount == 0 || gameTrailCount == 1) && lakeCount!=1){ //if the jungle count is 0, it is purely dependent on the number of roads and lakes
				this.meepleSlots.jungleOne = 0;
				this.meepleSlots.jungleTwo = -1;
				this.meepleSlots.jungleThree = -1;
				this.meepleSlots.jungleFour = -1;
			}
			
			else if(gameTrailCount == 1 && lakeCount==1){ 
				//could add an extra check for if the lake and trail are on opposite sides
				//but since there are not other cards with 1 lake and 1 trail
				//it can just be added later if needed
				this.meepleSlots.jungleOne = 0;
				this.meepleSlots.jungleTwo = 0;
				this.meepleSlots.jungleThree = -1;
				this.meepleSlots.jungleFour = -1;
			}
			
			else if(gameTrailCount == 2){
				this.meepleSlots.jungleOne = 0;
				this.meepleSlots.jungleTwo = 0;
				this.meepleSlots.jungleThree = -1;
				this.meepleSlots.jungleFour = -1;
			}
			
			else if (gameTrailCount == 3){
				this.meepleSlots.jungleOne = 0;
				this.meepleSlots.jungleTwo = 0;
				this.meepleSlots.jungleThree = 0;
				this.meepleSlots.jungleFour = -1;
			}
			
			else if(gameTrailCount == 4){
				this.meepleSlots.jungleOne = 0;
				this.meepleSlots.jungleTwo = 0;
				this.meepleSlots.jungleThree = 0;
				this.meepleSlots.jungleFour = 0;
			}
			
			
		}
		
				
		
		//special cases that need to be checked for (eg: road on top& bottom or left &right, assigning slot 4 to road)
		//all slot generation is done at this point, just need to do terrain specification in the array
		
		//slots 1, 3, 5, and 7 are all done when the sides are checked, can just use those to come up with the corners
		// corners can consist of 2 terrains
		// terrain of 0 = terrain of 3 + terrain of 1
		// terrain of 2 = terrain of 1 + terrain of 5
		// terrain of 8 = terrain of 5 + terrain of 7
		// terrain of 6 - terrain of 3 + terrain of 7
		//using a delimiter , between the two sections. the sections are listed left to right, as if you were looking at the tile. The one on the left takes priority as to what meeple slot is on it
		this.terrainSections[0] = this.terrainSections[3] + "," + this.terrainSections[1];
		this.terrainSections[2] = this.terrainSections[1] + "," + this.terrainSections[5];
		this.terrainSections[8] = this.terrainSections[7] + "," + this.terrainSections[5];
		this.terrainSections[6] = this.terrainSections[3] + "," + this.terrainSections[7];
		
		//most of the center cases were also taken care of, only one that needs to be done if it is an intersection and it needs to be assigned end-trail or if it is a jungle when all 4 sides are jungle
		if((super.terrainOnSide.up.equals("game-trail") && super.terrainOnSide.right.equals("game-trail")) || 
		   (super.terrainOnSide.bottom.equals("game-trail") && super.terrainOnSide.right.equals("game-trail")) ||
		   (super.terrainOnSide.bottom.equals("game-trail") && super.terrainOnSide.left.equals("game-trail")) ||
		   (super.terrainOnSide.up.equals("game-trail") && super.terrainOnSide.left.equals("game-trail")))
		{
			this.terrainSections[4] = "game-trail";
		}
		
		else if(jungleCount == 4){
			this.terrainSections[4] = "jungle";
		}
		
		
		
		
			
	}

	
	
}

class MeepleSlots
{
	int den;
	
	int gameTrailUp;
	int gameTrailRight;
	int gameTrailBottom;
	int gameTrailLeft;
	
	int lakeOne;
	int lakeTwo;
	
	int jungleOne;
	int jungleTwo;
	int jungleThree;
	int jungleFour;
}

// this will keep track of there are placed cards on tiles around the current tile
class Neighbors
{
	boolean up;
	boolean right;
	boolean bottom;
	boolean left;
	
	Neighbors(boolean up, boolean right, boolean bottom, boolean left)
	{
		this.up = up;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
	}

}

