

/* this class will contain all the information that each player will make during a turn including
- where to place card
- orientation
- if meaple placed
- where meeple is placed if true


this information will be what each player will send to the sever when it is their turn and they have decided where to move 
*/
public class PlayerMoveInformation {

	// first 3 handle the position of the card, and its orientation
	final public Card card;
	final public int row;
	final public int column;
	final public int orientation;
	// last 3 variables handle the meeple information
	final public boolean tigerPlaced;
	final public int tigerLocation;
// TODO some variables to store where the Tigers were placed
	
	public PlayerMoveInformation(Card card, int row, int column, int orientation, boolean tigerPlaced, int tigerLocation){
		this.card = card;
		this.row = row;
		this.column = column;
		this.orientation = orientation;
		this.tigerPlaced = tigerPlaced;
		this.tigerLocation = tigerLocation;
	}

}