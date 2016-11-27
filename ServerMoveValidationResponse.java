// this is the initial response that the server sends back to tell if a response is good, bad or it timeout
public class ServerMoveValidationResponse {

	// the card that the move was played with along with location
	final public Card card;
	final public int row;
	final public int column;
	final public int cardOrientation;
	final public boolean tigerPlaced;
	final public int tigerLocation;
	final public boolean crocPlaced;

	// final and public because we want to share it with the other players, but final so no modifications can be made after initialization of object
	ServerMoveValidationResponse( Card card, int row, int column, int cardOrientation, boolean tigerPlaced, int tigerLocation, boolean crocPlaced){
		this.card = card;
		this.row = row;
		this.column = column;
		this.cardOrientation = cardOrientation;
		this.tigerPlaced = tigerPlaced;
		this.tigerLocation = tigerLocation;
		this.crocPlaced = crocPlaced;
	}
}
