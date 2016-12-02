import java.util.ArrayList;
public class Player {
	
	public Board localVersionOfBoard;
	public ArrayList<Card> localVersionOfDeck;
	boolean firstMoveMade;			// if the first move of the game has been made
	public int tigerCount;			// keep track of our tigers
	public int crocCount;			// keep track of our crocodiles

	public boolean denPriority;		// used for when a tile has a den
	public int denRow, denColumn;	// location of the most recently placed den tile

	final public int tigerLimit = 7;
	
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
		int random = (int)(Math.random()* stuff.size());
		System.out.println("random is " + random);	

		boolean denOnCard = false;		// if tile has a den

		System.out.println("if there is a den " + cardToPlace.den);
		if(cardToPlace.den){
			denOnCard = true;
		}

		boolean trailPriority = false;
		int trailCount = 0;
			if(cardToPlace.CardCode.charAt(0) == 'T'){
				trailCount++;
			}
			if(cardToPlace.CardCode.charAt(1) == 'T'){
				trailCount++;
			}
			if(cardToPlace.CardCode.charAt(2) == 'T'){
				trailCount++;
			}
			if(cardToPlace.CardCode.charAt(3) == 'T'){
				trailCount++;
			}
			if(trailCount >= 3){
				trailPriority = true;
			}

		boolean oneLakePriority = false;
		int lakeCount = 0;
			if(cardToPlace.CardCode.charAt(0) == 'L'){
				lakeCount++;
			}
			if(cardToPlace.CardCode.charAt(1) == 'L'){
				lakeCount++;
			}
			if(cardToPlace.CardCode.charAt(2) == 'L'){
				lakeCount++;
			}
			if(cardToPlace.CardCode.charAt(3) == 'L'){
				lakeCount++;
			}
			if(lakeCount == 1){
				oneLakePriority = true;
			}

		PlayerMoveInformation response = null;

			// if there are no possibilities, we have to default to the UNPLACEABLE RESPONSE
			if(stuff.size() == 0){

				// this is the PASS response
				response = new PlayerMoveInformation(cardToPlace,0, 0, 0,false,0, false,true, true, false, false, 0,0);
			}
			// if Player goes first and it's their first turn, place a tiger. We are guaranteed no forfeits from invalid meeple placement
			else if(!firstMoveMade){
				System.out.println("------- Placed a TIGER on first move --------");

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
			// if tile has a den, place a tiger on it
			else if(denOnCard && (this.tigerCount>0)){
				System.out.println("------- Placed a TIGER on den --------");
				response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation,true,5,false, false, false, false, false, 0,0);
				this.tigerCount--;

				// now update local version of board before sending out the response 
				this.localVersionOfBoard.updateBoard(response);

				// now we want to prioritize placing a tiles around the tile that has the den 
				this.denPriority = true;
				this.denRow= stuff.get(random).row;
				this.denColumn = stuff.get(random).column;
			}
			// if the card has a special character that is not a croc or den, place a crocodile one it to hopefully lessen the score of opponent
			else if(((cardToPlace.deer || cardToPlace.boar || cardToPlace.buffalo) && cardToPlace.croc == false) && (this.crocCount > 0)) {
				System.out.println("------- Placed a CROC on tile --------");
				response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation,false,0,true, false, false, false, false, 0,0);
				
				// now update local version of board before sending out the response 
				this.localVersionOfBoard.updateBoard(response);
				this.crocCount--;
			}
			// if tile has exactly one lake, there's a high chance we can place a meeple on it without invalid placement, check if possible
			else if(oneLakePriority && this.tigerCount >0){

				boolean solutionFound = false;

				for(int i=0; i<stuff.size(); i++){

					boolean north = this.localVersionOfBoard.tileTracker[stuff.get(i).row+1][stuff.get(i).column];
					boolean east = 	this.localVersionOfBoard.tileTracker[stuff.get(i).row][stuff.get(i).column+1];
					boolean south = this.localVersionOfBoard.tileTracker[stuff.get(i).row-1][stuff.get(i).column];
					boolean west = this.localVersionOfBoard.tileTracker[stuff.get(i).row][stuff.get(i).column-1];

					// check north location
					if(cardToPlace.CardCode.charAt(0) == 'L' && !north && stuff.get(i).rotation == 0){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 2, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(1) == 'L' && !north && stuff.get(i).rotation == 1){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 2, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(2) == 'L' && !north && stuff.get(i).rotation == 2){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 2, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(3) == 'L' && !north && stuff.get(i).rotation == 3){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 2, false, false, false, false, false, 0, 0);
					}
					// check west location
					else if(cardToPlace.CardCode.charAt(0) == 'L' && !west && stuff.get(i).rotation == 1){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 4, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(1) == 'L' && !west && stuff.get(i).rotation == 2){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 4, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(2) == 'L' && !west && stuff.get(i).rotation == 3){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 4, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(3) == 'L' && !west && stuff.get(i).rotation == 0){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 4, false, false, false, false, false, 0, 0);
					}
					// check south location
					else if(cardToPlace.CardCode.charAt(0) == 'L' && !south && stuff.get(i).rotation == 2){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 8, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(1) == 'L' && !south && stuff.get(i).rotation == 3){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 8, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(2) == 'L' && !south && stuff.get(i).rotation == 0){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 8, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(3) == 'L' && !south && stuff.get(i).rotation == 1){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 8, false, false, false, false, false, 0, 0);
					}
					// check west location
					else if(cardToPlace.CardCode.charAt(0) == 'L' && !east && stuff.get(i).rotation == 3){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 6, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(1) == 'L' && !east && stuff.get(i).rotation == 0){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 6, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(2) == 'L' && !east && stuff.get(i).rotation == 1){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 6, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(3) == 'L' && !east && stuff.get(i).rotation == 2){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 6, false, false, false, false, false, 0, 0);
					}
					else{
						solutionFound = false;
					}
				}

				// use random tile placement if solution not found, otherwise decrease tiget count
				if(!solutionFound){
					System.out.println("------- no solution found!!!!!");
					response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation, false, 0, false, false, false, false, false, 0, 0);
				}
				else{
					System.out.println("POINTS FOR LAKE!!");
					this.tigerCount--;
				}
			}

			// prioritizes placing a tiger on a trail that is not connected 
			else if(trailPriority && (tigerCount >0)){

				boolean solutionFound = false;

				for(int i=0; i<stuff.size(); i++){

					boolean north = this.localVersionOfBoard.tileTracker[stuff.get(i).row+1][stuff.get(i).column];
					boolean east = 	this.localVersionOfBoard.tileTracker[stuff.get(i).row][stuff.get(i).column+1];
					boolean south = this.localVersionOfBoard.tileTracker[stuff.get(i).row-1][stuff.get(i).column];
					boolean west = this.localVersionOfBoard.tileTracker[stuff.get(i).row][stuff.get(i).column-1];
					// check north location
					if(cardToPlace.CardCode.charAt(0) == 'T' && !north && stuff.get(i).rotation == 0){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 2, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(1) == 'T' && !north && stuff.get(i).rotation == 1){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 2, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(2) == 'T' && !north && stuff.get(i).rotation == 2){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 2, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(3) == 'T' && !north && stuff.get(i).rotation == 3){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 2, false, false, false, false, false, 0, 0);
					}
					// check west location
					else if(cardToPlace.CardCode.charAt(0) == 'T' && !west && stuff.get(i).rotation == 1){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 4, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(1) == 'T' && !west && stuff.get(i).rotation == 2){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 4, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(2) == 'T' && !west && stuff.get(i).rotation == 3){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 4, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(3) == 'T' && !west && stuff.get(i).rotation == 0){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 4, false, false, false, false, false, 0, 0);
					}
					// check south location
					else if(cardToPlace.CardCode.charAt(0) == 'T' && !south && stuff.get(i).rotation == 2){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 8, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(1) == 'T' && !south && stuff.get(i).rotation == 3){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 8, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(2) == 'T' && !south && stuff.get(i).rotation == 0){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 8, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(3) == 'T' && !south && stuff.get(i).rotation == 1){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 8, false, false, false, false, false, 0, 0);
					}
					// check west location
					else if(cardToPlace.CardCode.charAt(0) == 'T' && !east && stuff.get(i).rotation == 3){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 6, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(1) == 'T' && !east && stuff.get(i).rotation == 0){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 6, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(2) == 'T' && !east && stuff.get(i).rotation == 1){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 6, false, false, false, false, false, 0, 0);
					}
					else if (cardToPlace.CardCode.charAt(3) == 'T' && !east && stuff.get(i).rotation == 2){
						solutionFound = true;
						response = new PlayerMoveInformation(cardToPlace, stuff.get(i).row, stuff.get(i).column, stuff.get(i).rotation, true, 6, false, false, false, false, false, 0, 0);
					}
					else{
						solutionFound = false;
					}

				}

				// use random tile placement if solution not found, otherwise decrease tiget count
				if(!solutionFound){
					System.out.println("------- prioritization will work for Trail");
					response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation, false, 0, false, false, false, false, false, 0, 0);
				}
				else{
					System.out.println("POINTS FOR TRIAL !!!");
					this.tigerCount--;
				}
			}



			// if a tiger has been placed on a den previously, we want to prioritize placing tiles around it to get more points
			else if(denPriority) {
				ArrayList<PlacementPossibility> denPossibilities = this.localVersionOfBoard.generatePossibleCardPlacements(cardToPlace);

				boolean solutionFound = false;
				System.out.println("*****DEN PRIORITY IS TRUE BABYYYYYYYY :)");

				for(int i = 0; i < denPossibilities.size(); i++) {
				// we iterate over the neighbors of where the den is placed and try to prioritize placing around it to maximize points

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

						/* This checks the corners of a den, but would only be used if those are possibilities */
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
					// if tile cant be placed around the den, place tile randomly in a valid position
					if(!solutionFound){
						System.out.println("------- no solution found!!!!!");
						response = new PlayerMoveInformation(cardToPlace, stuff.get(random).row, stuff.get(random).column, stuff.get(random).rotation, false, 0, false, false, false, false, false, 0, 0);
					}
					else{
						System.out.println("PRIORITIZATION WORKING!");
					}

				this.localVersionOfBoard.updateBoard(response);
			}
			// if not the first move, and none of the other conditions are met, place tile randomly in a valid spot
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
