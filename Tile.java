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
		else if(rotation == 3){ 						
			finalPlacedOrientation = new TerrainOnSide(cardBeingPlaced.terrainOnSide.left,
													   cardBeingPlaced.terrainOnSide.up,
													   cardBeingPlaced.terrainOnSide.right,
													   cardBeingPlaced.terrainOnSide.bottom);
		}
		else if (rotation == 2){						
			finalPlacedOrientation = new TerrainOnSide(cardBeingPlaced.terrainOnSide.bottom,
													   cardBeingPlaced.terrainOnSide.left,
													   cardBeingPlaced.terrainOnSide.up,
													   cardBeingPlaced.terrainOnSide.right);
		}
		else{				// rotation == 1						
			finalPlacedOrientation = new TerrainOnSide(cardBeingPlaced.terrainOnSide.right,
													   cardBeingPlaced.terrainOnSide.bottom,
													   cardBeingPlaced.terrainOnSide.left,
													   cardBeingPlaced.terrainOnSide.up);
		}
		
	}

	public String returnCardCode(){
		 return super.CardCode;
	}

	//modifies meepleSlots after card placement and generates section of the 3x3 array like in the rules
	void generateMeepleSlots()
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
				
				break;
			case "lake" :
				lakeCount++;
				this.terrainSections[1] = "lake";
				
				break;
			case "game-trail" :
				gameTrailCount++;
				this.terrainSections[1] = "game-trail";
				
				break;
			default :
				System.out.println("unable to identify terrain type on card");
		}
		
		switch(super.terrainOnSide.right){
			case "jungle" :
				jungleCount++;
				this.terrainSections[5] = "jungle";
			
				break;
			case "lake" :
				lakeCount++;
				this.terrainSections[5] = "lake";
			
				break;
			case "game-trail" :
				gameTrailCount++;
				this.terrainSections[5] = "game-trail";
			
				break;
			default :
				System.out.println("unable to identify terrain type on card");
		}
		
		switch(super.terrainOnSide.left){
			case "jungle" :
				jungleCount++;
				this.terrainSections[3] = "jungle";
			
				break;
			case "lake" :
				lakeCount++;
				this.terrainSections[3] = "lake";
			
				break;
			case "game-trail" :
				gameTrailCount++;
				this.terrainSections[3] = "game-trail";
			
				break;
			default :
				System.out.println("unable to identify terrain type on card");
		}
		
		switch(super.terrainOnSide.bottom){
			case "jungle" :
				jungleCount++;
				this.terrainSections[7] = "jungle";
			
				break;
			case "lake" :
				lakeCount++;
				this.terrainSections[7] = "lake";
			
				break;
			case "game-trail" :
				gameTrailCount++;
				this.terrainSections[7] = "game-trail";
			
				break;
			default :
				System.out.println("unable to identify terrain type on card");
		}
		
		
		if(super.den){
			
			this.terrainSections[4] = "den";
		}
		
		
		
		//handle slots for lake
		if(super.lakesConnected){
			if(lakeCount>0){
				 
				if((super.terrainOnSide.up.equals("lake") && super.terrainOnSide.bottom.equals("lake")) || (super.terrainOnSide.left.equals("lake") && super.terrainOnSide.right.equals("lake"))){
					this.terrainSections[4] = "lake";
				}
				 
				 
			}
			 
			if(lakeCount == 4){
				
				
				this.terrainSections[4] = "lake";
			}
			 
			else if(lakeCount == 3 && gameTrailCount == 0){
				
				this.terrainSections[4] = "lake";
			}
			
			else if(lakeCount == 3 && gameTrailCount == 1){
				
				this.terrainSections[4] = "end-trail";
			}
			
			else if(lakeCount == 2){
				if(gameTrailCount == 0){
					//check for connected lakes that are accross from each other
					if((super.terrainOnSide.up.equals("lake")&&super.terrainOnSide.bottom.equals("lake"))||(super.terrainOnSide.left.equals("lake")&&super.terrainOnSide.right.equals("lake"))){
						
						this.terrainSections[4] = "lake";
					}
					//otherwise it is two lakes that cut through the tile diagonally and only 1 jungle exists
					else{
						
					}
				}
				
			}
			
			
		}
		//handle special case of lakes not connected with jungle in between
		else{
			if(lakeCount == 2){	

				boolean sideUp = super.terrainOnSide.up.equals("lake");
				boolean sideBottom = super.terrainOnSide.bottom.equals("lake");
				boolean sideRight = super.terrainOnSide.right.equals("lake");
				boolean sideLeft = super.terrainOnSide.left.equals("lake");
				
				if( (sideUp && sideBottom) || (sideLeft && sideRight) ){
					this.terrainSections[4] = "jungle";
				}
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
		if(super.lakesConnected){
			
			//[0]
			if(this.terrainSections [3].equals("lake") && this.terrainSections[1].equals("lake")){
				this.terrainSections[0] = "lake";
			}

			else if(this.terrainSections[3].equals("game-trail") && this.terrainSections[1].equals("game-trail")){
				this.terrainSections[0] = "jungle";
			}

			else{
				this.terrainSections[0] = "jungle";
			}


			//[2]
			if(this.terrainSections[5].equals("lake") && this.terrainSections[1].equals("lake")){
				this.terrainSections[2] = "lake";
			}

			else if(this.terrainSections[5].equals("game-trail") && this.terrainSections[1].equals("game-trail")){
				this.terrainSections[2] = "jungle";
			}

			else{
				this.terrainSections[2] = "jungle";
			}

			//[6]
			if(this.terrainSections[3].equals("lake") && this.terrainSections[7].equals("lake")){
				this.terrainSections[6] = "lake";
			}

			else if(this.terrainSections[3].equals("game-trail") && this.terrainSections[7].equals("game-trail")){
				this.terrainSections[6] = "jungle";
			}

			else{
				this.terrainSections[6] = "jungle";
			}

			//[8]
			if(this.terrainSections[5].equals("lake") && this.terrainSections[7].equals("lake")){
				this.terrainSections[8] = "lake";
			}

			else if(this.terrainSections[5].equals("game-trail") && this.terrainSections[7].equals("game-trail")){
				this.terrainSections[8] = "jungle";
			}

			else{
				this.terrainSections[8] = "jungle";
			}


		}

		else{ //if lakes are not connected, corners are always jungle!
			this.terrainSections[0] = "jungle";
			this.terrainSections[2] = "jungle";
			this.terrainSections[8] = "jungle";
			this.terrainSections[6] = "jungle";
		}

		
		//most of the center cases were also taken care of, only one that needs to be done if it is an intersection and it needs to be assigned end-trail or if it is a jungle when all 4 sides are jungle
		if( ((super.terrainOnSide.up.equals("game-trail") && super.terrainOnSide.right.equals("game-trail"))  || 
		   (super.terrainOnSide.bottom.equals("game-trail") && super.terrainOnSide.right.equals("game-trail")) ||
		   (super.terrainOnSide.bottom.equals("game-trail") && super.terrainOnSide.left.equals("game-trail")) ||
		   (super.terrainOnSide.up.equals("game-trail") && super.terrainOnSide.left.equals("game-trail")) ) && gameTrailCount == 2)
		{
			this.terrainSections[4] = "game-trail";
		}
		
		else if(jungleCount == 4){
			if(!super.den){
				this.terrainSections[4] = "jungle";
			}

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

