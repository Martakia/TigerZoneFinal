
import java.util.ArrayList;
public class Player {
	
	public Board localVersionOfBoard;
	public ArrayList<Card> localVersionOfDeck;
	boolean firstMoveMade;
	public int tigerCount;
	public int crocCount;

	public boolean denPriority;
	public int x, y;
	
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


		PlayerMoveInformation response = null;

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
				this.denPriority = true;
				this.x = stuff.get(random).row;
				this.y= stuff.get(random).column;
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
			else if(denPriority) {
				ArrayList<PlacementPossibility> denPossibilities = this.localVersionOfBoard.generatePossibleCardPlacements(cardToPlace);

				boolean solutionFound = false;

				for(int i = 1; i < denPossibilities.size(); i++) {
					if(x+1 == denPossibilities.get(i).row && y == denPossibilities.get(i).column) {
						//this.localVersionOfBoard[x+1][y] = cardToPlace;
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, x+1, y, stuff.get(random).rotation, false, 0, false, false, false, false, false, 0, 0);
					}
					else if(x-1 == denPossibilities.get(i).row && y == denPossibilities.get(i).column) {
						solutionFound = true;
						//this.localVersionOfBoard[x-1][y] = cardToPlace;
						response = new PlayerMoveInformation(cardToPlace, x-1, y, stuff.get(random).rotation, false, 0, false, false, false, false, false, 0, 0);
					}
					else if(x == denPossibilities.get(i).row && y+1 == denPossibilities.get(i).column) {
						solutionFound = true;
						//this.localVersionOfBoard[x][y+1] = cardToPlace;
						response = new PlayerMoveInformation(cardToPlace, x, y+1, stuff.get(random).rotation, false, 0, false, false, false, false, false, 0, 0);
					}
					else if(x == denPossibilities.get(i).row && y-1 == denPossibilities.get(i).column) {
						solutionFound = true;
						//this.localVersionOfBoard[x][y-1] = cardToPlace;
						response = new PlayerMoveInformation(cardToPlace, x, y-1, stuff.get(random).rotation, false, 0, false, false, false, false, false, 0, 0);
					}
				}
					if(!solutionFound){
						response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation, false, 0, false, false, false, false, false, 0, 0);
					}
				
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
