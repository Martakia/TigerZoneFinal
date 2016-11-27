
public class PlayerMoveInformation {

	// first 3 handle the position of the card, and its orientation
	final public Card card;
	final public int row;
	final public int column;
	final public int orientation;
	final public boolean tigerPlaced;
	final public int tigerLocation;
	final public boolean crocodile;
	final public boolean unplaceable;
	final public boolean pass;
	final public boolean retrieve;
	final public boolean another;
	final public int extraRow;
	final public int extraColumn;

	
	public PlayerMoveInformation(Card card, int row, int column, int orientation, boolean tigerPlaced, int tigerLocation, boolean crocodile, boolean unplaceable, boolean pass, boolean retrieve, boolean another, int extraRow, int extraColumn){
		this.card = card;
		this.row = row;
		this.column = column;
		this.orientation = orientation;
		this.tigerPlaced = tigerPlaced;
		this.tigerLocation = tigerLocation;
		this.crocodile = crocodile;
		this.unplaceable = unplaceable;
		this.pass = pass;
		this.retrieve = retrieve;
		this.another = another;
		this.extraRow = extraRow;
		this.extraColumn = extraColumn;
	}

}