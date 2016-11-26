package mainPackage;
import java.util.ArrayList;
import java.util.Vector;

public class IO {
	public static Card currentCrad;
	public static int row, column, rotation;
	public static ArrayList<PlacementPossibility> possibilities;
	public static boolean possibilityUpdated=false, cardUpdated=false, playerPlacedCard=false, 
							playerPlacedMeeple=false, updatePlacments=false, tigerSlotsUpdated=false;
	public static Board board;
	public static Tile lastPlacedTile;
	public static Coordinates placedTiger;
	public static Vector<Coordinates> tigerPlacementSlots;
	static void setCard(Card newCard)
	{
		currentCrad=newCard;
		cardUpdated = true;
		rotation =0;
//		System.out.println("card was set");
	}
	public static void setPossibilities(ArrayList<PlacementPossibility> possibilitiesIn)
	{
		possibilities = possibilitiesIn;
		possibilityUpdated = true;
	}
	public static void setBoard(Board boardIn)
	{
		board = new Board();
		board = boardIn;
	}
}
