
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
	
		// Method that will return the possible locations of the tiles that can be placed
		ArrayList<PlacementPossibility> stuff = this.localVersionOfBoard.generatePossibleCardPlacements(cardToPlace);
		
		System.out.println("number of possibilities is " + stuff.size());
		for(int i=0; i<stuff.size(); i++){
			System.out.println(stuff.get(i).row + " " + stuff.get(i).column + " " + stuff.get(i).rotation );
		}


		// at this point the AI picks a random tile out of the available options 
		int random = (int)(Math.random()*stuff.size());
		System.out.println("random is " +random);	

		PlayerMoveInformation response;
			if(stuff.size() == 0){
				// there are no possibilities, we have to default to the UNPLACEABLE RESPONSE

				// this is the PASS response
				 response = new PlayerMoveInformation(cardToPlace,0, 0, 0,false,0, false,true, true, false, false, 0,0);
			}
			else{
				// TODO implement random choice of picking tiger location, right now set to false
				 response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation,false,0,false, false, false, false, false, 0,0);
				
				// now update local version of board before sending out the response 
				this.localVersionOfBoard.updateBoard(response);
			}
		return response;
	}
	
	void setBoard(Board board)
	{
		this.localVersionOfBoard = board;
	}
	
}
