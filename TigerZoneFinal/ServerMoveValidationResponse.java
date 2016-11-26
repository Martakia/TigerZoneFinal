// this is the initial response that the server sends back to tell if a response is good, bad or it timeout
public class ServerMoveValidationResponse {

	// the card that the move was played with along with location
	final public Card card;
	final public int xLocation;
	final public int yLocation;
	final public int cardOrientation;
	final public boolean tigerPlaced;
	final public int tigerLocation;
	// TODO Tiger placement information
	

	// final and public because we want to share it with the other players, but final so no modifications can be made after initialization of object
	ServerMoveValidationResponse( Card card, int xLocation, int yLocation, int cardOrientation, boolean tigerPlaced, int tigerLocation){
		this.card = card;
		this.xLocation = xLocation;
		this.yLocation = yLocation;
		this.cardOrientation = cardOrientation;
		this.tigerPlaced = tigerPlaced;
		this.tigerLocation = tigerLocation;
	}
}
