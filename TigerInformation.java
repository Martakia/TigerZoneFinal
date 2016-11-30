class TigerInformation{
	
	final int row;
	final int column;
	final int index; // 1-9 location on the tile
	final boolean enemy;

	TigerInformation(int row, int column, int index, boolean enemy){
		this.row = row;
		this.column = column;
		this.index = index;
		this.enemy = enemy;
	}

}