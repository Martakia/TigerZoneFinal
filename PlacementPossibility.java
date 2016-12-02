
// data structure to keep track of where a certain card can be placed and at what orientation
class PlacementPossibility{
	 public int row;
	 public int column;
	 public int rotation;

	PlacementPossibility(int row, int column, int rotation){
		this.row = row;
		this.column = column;
		this.rotation = rotation; // 0 = 0, 1 = 90, 2 = 180, 3 = 270 counterclock-wise
	}

}