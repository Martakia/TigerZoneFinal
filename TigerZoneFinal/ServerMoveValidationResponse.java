// this is the initial response that the server sends back to tell if a response is good, bad or it timeout
public class ServerMoveValidationResponse {
	
	// timeout and validation response from server 
	final public boolean isValid;
	final public boolean timeout;

	// the card that the move was played with along with location
	final public Card card;
	final public int xLocation;
	final public int yLocation;
	final public int cardOrientation;
	final public boolean tigerPlaced;
	// TODO Tiger placement information
	

	// final and public because we want to share it with the other players, but final so no modifications can be made after initialization of object
	ServerMoveValidationResponse(boolean isValid, boolean timeout, Card card, int xLocation, int yLocation, int cardOrientation, boolean tigerPlaced){
		this.isValid = isValid;
		this.timeout = timeout;
		this.card = card;
		this.xLocation = xLocation;
		this.yLocation = yLocation;
		this.cardOrientation = cardOrientation;
		this.tigerPlaced = tigerPlaced;
	}
}
