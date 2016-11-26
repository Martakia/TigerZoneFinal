
import java.util.ArrayList;
public class Player {
	
	public Board localVersionOfBoard;
	public ArrayList<Card> localVersionOfDeck;
	
	Player()
	{
		// nothing really needs initialization
	}
	
	// Same deck and board is given to the Game class
	public void giveDeckToPlayer(ArrayList<Card> deck){
		this.localVersionOfDeck = deck;
	}
	public void giveBoardToPlayer(Board board){
		this.localVersionOfBoard = board;
	}
	public PlayerMoveInformation makeMove(Card cardToPlace)
	{	
	
		// TODO implement
		ArrayList<PlacementPossibility> stuff = this.localVersionOfBoard.generatePossibleCardPlacements(cardToPlace);
		int random = (int)(Math.random()*stuff.size());
		
		System.out.println("random is " +random);	
		PlayerMoveInformation response = new PlayerMoveInformation(cardToPlace,stuff.get(random).column, stuff.get(random).row, stuff.get(random).rotation,false,0);
		
		// now update local version of board before sending out the response 
		this.localVersionOfBoard.updateBoard(response);
		return response;
	}
	
	void setBoard(Board board)
	{
		this.localVersionOfBoard = board;
	}
	
}
