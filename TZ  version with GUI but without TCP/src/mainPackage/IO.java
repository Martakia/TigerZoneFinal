package mainPackage;
import java.util.ArrayList;
import java.util.Vector;
//this class transmits data from window and input to game and back 
public class IO {
	public static void init()
	{
		currentGame=1;
		currentCrad=new Card[2];
		row=new int[2];
		column=new int[2];
		rotation=new int[2];
		possibilityUpdated=new boolean[2];
		cardUpdated=new boolean[2];
		playerPlacedCard=new boolean[2];
		playerPlacedMeeple=new boolean[2];
		updatePlacments=new boolean[2];
		tigerSlotsUpdated=new boolean[2];
		didPlayerDecideToPlaceMeeple=new boolean[2];
		board=new Board[2];
		boardTmp=new Board[2];
		placedTiger=new Coordinates[2];
		moveInfoFromServer = new PlayerMoveInformation[2];
		moveInfoToServer = new PlayerMoveInformation[2];
		noCardsLeft = new boolean [2];
		tigerPlacementSlots=(Vector<Coordinates>[])new Vector[2];	
		tigerPlacementSlotsAI=(Vector<Coordinates>[])new Vector[2];	
		possibilities=(ArrayList<PlacementPossibility>[])new ArrayList[2];

		for(int g=0;g<2;g++)
		{
			noCardsLeft[g]=false;
			possibilityUpdated[g]=false;
			cardUpdated[g]=false;
			playerPlacedCard[g]=false;
			playerPlacedMeeple[g]=false;
			updatePlacments[g]=false;
			tigerSlotsUpdated[g]=false;
			didPlayerDecideToPlaceMeeple[g]=false;	
			tigerPlacementSlots[g]=new Vector<Coordinates>();
			tigerPlacementSlotsAI[g]=new Vector<Coordinates>();
			possibilities[g]=new ArrayList<PlacementPossibility>();
		}
	}
	public static Card currentCrad[];
	public static int row[], column[], rotation[],tmp[],currentGame, gameToRender;	
	public static boolean possibilityUpdated[], cardUpdated[], playerPlacedCard[], noCardsLeft[],
						  playerPlacedMeeple[], updatePlacments[], tigerSlotsUpdated[], didPlayerDecideToPlaceMeeple[];
	public static Board board[];
	public static Board boardTmp[];
	public static Coordinates placedTiger[];
	public static Vector<Coordinates> tigerPlacementSlots[],tigerPlacementSlotsAI[];
	public static ArrayList<PlacementPossibility> possibilities[];
	public static PlayerMoveInformation moveInfoFromServer[];
	public static PlayerMoveInformation moveInfoToServer[];
	
	static void setCard(Card newCard)
	{
		currentCrad[currentGame]=newCard;
		cardUpdated[currentGame] = true;
		rotation[currentGame] =0;
	}
	public static void setPossibilities(ArrayList<PlacementPossibility> possibilitiesIn)
	{
		possibilities[currentGame] = possibilitiesIn;
		possibilityUpdated[currentGame] = true;
	}
	public static void setBoard(Board boardIn)
	{		
		board[currentGame] = boardIn;
	}
}
