package mainPackage;


public class main {//execution begins here

	public static void main(String[] args) {
		boolean GameRunning = true;

		Game game = new Game();//game class handles board, deck and player. In particular it passes control from one player to another
		WindowThread windowThread = new WindowThread();
		windowThread.setGame(game);
		windowThread.start();
		

		while(GameRunning)//each loop iteration is one game
		{   
			// draws the top card that is currently in the deck, in the actual implementation the server will handle this 
			Card drawnCard = game.deck.NextCardInDeck();
//			System.out.println("\nCHECKPOINT: New card drawn from deck");
			System.out.println("---NEW CARD INFO---: " +drawnCard.returnCardInformation());
			
			// check if the card can be placed at all on the deck, in the actual implementation the server will make sure that it can be placed
			// we want to make sure the card is good before we give it to the player
			boolean validCardFound = false;
			
			
			// this validation and card drawing would be handled by the server
			while(validCardFound != true){
				if(game.verifyDrawnCard(drawnCard)){
					// card is good, proceed forward
					validCardFound = true;
				}
				else{
					// card is bad, we must draw again, but first check if deck is empty
					if(game.deck.IsEmpty()){
						GameRunning = false;
						
						break;
					}
					else{
						// draw next card if the deck is not empty
						drawnCard = game.deck.NextCardInDeck();
						validCardFound = true;
					}	
				}
			}
			// after the card is drawn it must be given to the next player to make a move
			// this is the part where we are told that it is our turn and what the tile is
			
			
			PlayerMoveInformation response = game.playNextMove(drawnCard);
			
			// now the server has to confirm if the move is valid and update its local version of the board
			// in addition server sends responses to players regarding score updates, timeouts etc. 
			game.validatePlayerMove(response);
			game.board.placeCard(response);
//			game.board.addSectionTerrain(response);
			game.board.placeTiger(response, game.currentTurn);		
			
			// Server checks if deck is empty to continue game 
			if(game.deck.IsEmpty()){
				// this means that the last card was drawn and the deck is now empty,
				// terminate the game
				GameRunning = false;
			}
			// switch players that card is sent to
			game.switchPlayerTurns();
			
		}
		

	}

}
