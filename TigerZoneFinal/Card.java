
import java.lang.StringBuilder;
// Card Class contains raw Card information, Tile class handles positioning on board, neighbors, tigers etc. 
public class Card{
	
	// handles 4 terrain sides
	 TerrainOnSide terrainOnSide;
	
	// Special information
	final boolean deer;
	final boolean boar;
	final boolean buffalo;
	final boolean den;
	final boolean lakesConnected;
	final boolean junglesConnected;
	final String CardCode;
	public String imagePath;//path to png files
	
	
	Card(String code){
		
		// TODO finish implementation
		
		// just placeholders TODO TODO TODO 
		this.deer = false;
		this.boar = false;
		this.buffalo = false;
		this.den = false;
		this.lakesConnected = false;
		this.junglesConnected = false;
		this.CardCode = "TODO";
		
	}
	
	Card(String terrainUp, String terrainRight, String terrainBottom, String terrainLeft, 
			boolean deer, boolean boar, boolean buffalo,boolean den, boolean lakesConnected,boolean junglesConnected)
		{
			this.terrainOnSide = new TerrainOnSide(terrainUp, terrainRight, terrainBottom, terrainLeft);
			this.deer = deer;
			this.boar = boar;
			this.buffalo = buffalo;
			this.den = den;
			this.lakesConnected = lakesConnected;
			this.junglesConnected = junglesConnected;

			
			
			StringBuilder temp = new StringBuilder();
			
			// top-face
				if(terrainUp.equals("jungle")){
					temp.append("J");
				}
				else if(terrainUp.equals("lake")){
					temp.append("L");
				}
				else {
					temp.append("T");
				}
			
			// right-face
				if(terrainRight.equals("jungle")){
					temp.append("J");
				}
				else if(terrainRight.equals("lake")){
					temp.append("L");
				}
				else {
					temp.append("T");
				}
			// bottom-face
				if(terrainBottom.equals("jungle")){
					temp.append("J");
				}
				else if(terrainBottom.equals("lake")){
					temp.append("L");
				}
				else {
					temp.append("T");
				}
			// left-face
				if(terrainLeft.equals("jungle")){
					temp.append("J");
				}
				else if(terrainLeft.equals("lake")){
					temp.append("L");
				}
				else {
					temp.append("T");
				}
			
			// special codes
				int specialCodes = 0;
				if(deer){
					temp.append("D");
					specialCodes++;
				}
				if(boar){
					temp.append("P");
					specialCodes++;
				}
				if(buffalo){
					temp.append("B");
					specialCodes++;
				}
				if(den){
					temp.append("X");
					specialCodes++;
				}			
				if(specialCodes == 0){
					temp.append("-");
				}
			
			this.CardCode = temp.toString();
}

	public String returnCardInformation(){
		StringBuilder response = new StringBuilder();
			response.append(this.terrainOnSide.up + " ");
			response.append(this.terrainOnSide.right + " ");
			response.append(this.terrainOnSide.bottom + " ");
			response.append(this.terrainOnSide.left + " ");
			response.append(this.deer + " ");
			response.append(this.boar + " ");
			response.append(this.buffalo + " ");
			response.append(this.den + " ");
			response.append(this.lakesConnected + " ");
			response.append(this.junglesConnected);
		return response.toString();
	}

	public void setPathToImage(String path)
	{
		imagePath = new String(path);//sets path to png files
	}

}


