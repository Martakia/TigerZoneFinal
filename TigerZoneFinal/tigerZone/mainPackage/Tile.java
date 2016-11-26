package mainPackage;
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

	final int rotation;//is CLOCKWISE -> need to be translated to COUNTER CLOCKWISE for server response



	boolean croc;
	String imagePath;
	boolean isPlacedOnBoard;
	boolean wasRendered;// this thing helps gui to work well
	
	public int[] ownershipSections;
	public String[] terrainSections;
	public boolean[] placeableSections;
	//	0	1	2						1	2	3
	//	3	4	5	code  =	 rules doc	4	5	6
	//	6	7	8						7	8	9
	
	// corners can consist of 2 terrains
	// terrain of 0 = terrain of 1 + terrain of 3
	// terrain of 2 = terrain of 1 + terrain of 5
	// terrain of 8 = terrain of 5 + terrain of 7
	// terrain of 6 - terrain of 3 + terrain of 7
	
	// the terrain of the center varies depending on if there is a den or not, 
	// if the lakes or gametrails cross it, and what the terrains are for 1, 3, 5, and 7
	
	// ownership is set to 0 initial for each section, but is updated depending on the ownership of the
	// neighbors. Ownership of the center (4) should be set to -1 in the case that it is an intersection
	// of a game trail to make it reflect a dead end. 
	// The terrain section type for this scenario will be end-trail.
	
	Tile(Card cardBeingPlaced, int rotation,  int row,int column, boolean topNeighbor, boolean rightNeighbor, boolean bottomNeighbor, boolean leftNeighbor )
	{

		// original card information is stored in the parent class, what is on the board, is kept in this tile class
		super(cardBeingPlaced.terrainOnSide.up, cardBeingPlaced.terrainOnSide.right, cardBeingPlaced.terrainOnSide.bottom, cardBeingPlaced.terrainOnSide.left,
				cardBeingPlaced.deer, cardBeingPlaced.boar, cardBeingPlaced.buffalo ,cardBeingPlaced.den, cardBeingPlaced.lakesConnected, cardBeingPlaced.junglesConnected);
		
		this.croc = croc;
		this.ownershipSections = new int[9];		
		this.terrainSections = new String[9];
		this.placeableSections = new boolean[9];

		this.meepleSlots = new MeepleSlots();		
		// first base Card object is initialized in parent class in order to store original card information 
		

		// first base Card object is initialized in parent class in order to store original card information 
	
		// now the positions of where the Tile is on the board is specified
		this.column = column;
		this.row = row; 
		this.rotation=rotation;
		
		// After the card is placed on the TILE and BOARD, it cannot be rotated or moved
		if(rotation == 0){ 								// 0 = no rotation made, card in original position
			finalPlacedOrientation = new TerrainOnSide(cardBeingPlaced.terrainOnSide.up,
													   cardBeingPlaced.terrainOnSide.right,
													   cardBeingPlaced.terrainOnSide.bottom,
													   cardBeingPlaced.terrainOnSide.left);
		}
		else if(rotation == 1){ 						// 1 = rotation 90 degrees clockwise
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
		
		//now to take care of the neighbors 
		 Neighbors neighbors = new Neighbors(topNeighbor, rightNeighbor, bottomNeighbor, leftNeighbor);
		
		 // when Tile defined and declared it is not immediatly placed on the board
		 this.isPlacedOnBoard = false;
		 this.wasRendered = false;
//		 System.out.println(cardBeingPlaced.imagePath);
		 this.imagePath=cardBeingPlaced.imagePath;
		 
	}
	
}

class TerrainOnSide{
	public String up;
	public String right;
	public String bottom;
	public String left;

	TerrainOnSide(){
		// empty constructor so that sides can be added not at initialization
	}
	
	TerrainOnSide(String up, String right, String bottom, String left){
		this.up = up;
		this.right = right;
		this.bottom = bottom;
		this.left = left;
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

