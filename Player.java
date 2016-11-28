
import java.util.ArrayList;
public class Player {
	
	public Board localVersionOfBoard;
	public ArrayList<Card> localVersionOfDeck;
	boolean firstMoveMade;
	public int tigerCount;
	public int crocCount;

	public boolean prioritizeDen;
	public int denRow;
	public int denColumn;
	
	Player()
	{
		firstMoveMade = false;
		this.tigerCount = 8;
		this.crocCount = 2;
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
		// AI stuff, if there is a den on the card, we always want to place a tiger there

		// at this point the AI picks a random tile out of the available options 
		int random = (int)(Math.random()*stuff.size());
		System.out.println("random is " +random);	

		boolean denOnCard = false;

		System.out.println("if there is a den " +cardToPlace.den);
		if(cardToPlace.den){
			denOnCard = true;
		}


		PlayerMoveInformation response;
			if(stuff.size() == 0){
				// there are no possibilities, we have to default to the UNPLACEABLE RESPONSE

				// this is the PASS response
				 response = new PlayerMoveInformation(cardToPlace,0, 0, 0,false,0, false,true, true, false, false, 0,0);
			}
			else if(denOnCard && (this.tigerCount>0)){
				response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation,true,5,false, false, false, false, false, 0,0);
				this.tigerCount--;
				// now update local version of board before sending out the response 
				this.localVersionOfBoard.updateBoard(response);

				// now we want to prioritize placing a tiles around the tile that has the den 
				this.prioritizeDen = true;
				this.denRow = stuff.get(random).row;
				this.denColumn = stuff.get(random).column;
			}
			else if(!firstMoveMade){
				response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation,true,1,false, false, false, false, false, 0,0);
				this.tigerCount--;
				this.localVersionOfBoard.updateBoard(response);
				firstMoveMade = true;
			}
			else if(((cardToPlace.deer || cardToPlace.boar || cardToPlace.buffalo) && cardToPlace.croc == false) && (this.crocCount > 0)) {

				response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation,false,0,true, false, false, false, false, 0,0);
				
				// now update local version of board before sending out the response 
				this.localVersionOfBoard.updateBoard(response);
				this.crocCount--;
			}
			else{
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
