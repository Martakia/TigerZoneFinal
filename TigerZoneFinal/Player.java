
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
		PlayerMoveInformation response = new PlayerMoveInformation(cardToPlace, 0,0,0,false,0);
		return response;
	}
	
	void setBoard(Board board)
	{
		this.localVersionOfBoard = board;
	}
	
}
