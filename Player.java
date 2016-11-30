import java.util.ArrayList;
public class Player {
	
	public Board localVersionOfBoard;
	public ArrayList<Card> localVersionOfDeck;
	boolean firstMoveMade;
	public int tigerCount;
	public int crocCount;

	public boolean denPriority;
	public int denRow, denColumn;
	
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
			else if(!firstMoveMade){
				System.out.println("-------Placed a TIGER on first move--------");

				if(denOnCard) {
					response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation,true,5,false, false, false, false, false, 0,0);
				}
				else {
					response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation,true,1,false, false, false, false, false, 0,0);
				}

				this.tigerCount--;
				this.localVersionOfBoard.updateBoard(response);
				firstMoveMade = true;
			}
			else if(denOnCard && (this.tigerCount>0)){
				System.out.println("******Placed a den!!");
				response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation,true,5,false, false, false, false, false, 0,0);
				this.tigerCount--;
				// now update local version of board before sending out the response 
				this.localVersionOfBoard.updateBoard(response);
				// now we want to prioritize placing a tiles around the tile that has the den 
				this.denPriority = true;
				this.denRow= stuff.get(random).row;
				this.denColumn = stuff.get(random).column;
			}
			else if(((cardToPlace.deer || cardToPlace.boar || cardToPlace.buffalo) && cardToPlace.croc == false) && (this.crocCount > 0)) {
				System.out.println("******Chose crocodile :D");
				response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation,false,0,true, false, false, false, false, 0,0);
				
				// now update local version of board before sending out the response 
				this.localVersionOfBoard.updateBoard(response);
				this.crocCount--;
			}
			else if(denPriority) {
				ArrayList<PlacementPossibility> denPossibilities = this.localVersionOfBoard.generatePossibleCardPlacements(cardToPlace);

				boolean solutionFound = false;
				System.out.println("*****DEN PRIORITY IS TRUE BABYYYYYYYY :)");

				for(int i = 0; i < denPossibilities.size(); i++) {
				// we itterate over the neighbors of where the den is placed and try to prioritize placing around it to maximize points

						// check if we can place it above at a location
						int rowTest = denPossibilities.get(i).row;
						int columnTest = denPossibilities.get(i).column;
						int orientationTest = denPossibilities.get(i).rotation;

						if((this.denRow == rowTest) && (this.denColumn == columnTest +1) ){
							// check if can be placed to the left
							solutionFound = true;
							response = new PlayerMoveInformation(cardToPlace, denPossibilities.get(i).row, denPossibilities.get(i).column, denPossibilities.get(i).rotation, false, 0, false, false, false, false, false, 0, 0);
							break;
						}
						else if((this.denRow == rowTest) && (this.denColumn == columnTest -1 ) ){
							// check if can be placed to the right
							solutionFound = true;
							response = new PlayerMoveInformation(cardToPlace, denPossibilities.get(i).row, denPossibilities.get(i).column, denPossibilities.get(i).rotation, false, 0, false, false, false, false, false, 0, 0);
						    break;
						} 
						else if((this.denRow == rowTest-1) && (this.denColumn == columnTest) ){
							// check if can be placed above
							solutionFound = true;
							response = new PlayerMoveInformation(cardToPlace, denPossibilities.get(i).row, denPossibilities.get(i).column, denPossibilities.get(i).rotation, false, 0, false, false, false, false, false, 0, 0);
							break;
						}

						else if((this.denRow == rowTest+1) && (this.denColumn == columnTest) ){
							// check if it can be placed below
							solutionFound = true;
							response = new PlayerMoveInformation(cardToPlace, denPossibilities.get(i).row, denPossibilities.get(i).column, denPossibilities.get(i).rotation, false, 0, false, false, false, false, false, 0, 0);
							break;
						}
						// else if((this.denRow == rowTest+1) && (this.denColumn == columnTest+1) ){
						// 	solutionFound = true;
						// 	response = new PlayerMoveInformation(cardToPlace, denPossibilities.get(i).row, denPossibilities.get(i).column, denPossibilities.get(i).rotation, false, 0, false, false, false, false, false, 0, 0);
						// }
						// else if((this.denRow == rowTest-1) && (this.denColumn == columnTest-1) ){
						// 	solutionFound = true;
						// 	response = new PlayerMoveInformation(cardToPlace, denPossibilities.get(i).row, denPossibilities.get(i).column, denPossibilities.get(i).rotation, false, 0, false, false, false, false, false, 0, 0);
						// }
						// else if((this.denRow == rowTest+1) && (this.denColumn == columnTest-1) ){
						// 	solutionFound = true;
						// 	response = new PlayerMoveInformation(cardToPlace, denPossibilities.get(i).row, denPossibilities.get(i).column, denPossibilities.get(i).rotation, false, 0, false, false, false, false, false, 0, 0);
						// }
						// else if((this.denRow == rowTest-1) && (this.denColumn == columnTest+1) ){
						// 	solutionFound = true;
						// 	response = new PlayerMoveInformation(cardToPlace, denPossibilities.get(i).row, denPossibilities.get(i).column, denPossibilities.get(i).rotation, false, 0, false, false, false, false, false, 0, 0);
						// }

				}
					if(!solutionFound){
						System.out.println("-------no solution found!!!!!");
						response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation, false, 0, false, false, false, false, false, 0, 0);
					}
					else{
						System.out.println("PRIORITIZATION WORKING!");
					}

				this.localVersionOfBoard.updateBoard(response);
			}
			else {
				System.out.println("INSIDE IF NUMBER 5");
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
