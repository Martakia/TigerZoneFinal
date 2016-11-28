
import java.lang.StringBuilder;
// Card Class contains raw Card information, Tile class handles positioning on board, neighbors, tigers etc. 
public class Card{
	
	// handles 4 terrain sides
	 public TerrainOnSide terrainOnSide;
	
	// Special information
	public boolean deer;
	public boolean boar;
	public boolean buffalo;
	public boolean den;
	public boolean croc;
	public boolean lakesConnected;
	public boolean junglesConnected;
	public String CardCode;
	public String imagePath;//path to png files


	Card(String string){
		
		this.CardCode = string;
		this.terrainOnSide = new TerrainOnSide();
		
		if(string.charAt(0) == 'J'){
			//System.out.println(string.charAt(0));
			this.terrainOnSide.up ="jungle";
			//System.out.println(this.terrainOnSide.up);
		}
		else if(string.charAt(0) == 'L'){
			this.terrainOnSide.up ="lake";
		}
		else if(string.charAt(0) == 'T'){
			this.terrainOnSide.up = "game-trail";
		} else{

		}

			
		if(string.charAt(1) == 'J'){
			this.terrainOnSide.right = "jungle";
		}
		else if(string.charAt(1) == 'L'){
			this.terrainOnSide.right ="lake";
		}
		else if(string.charAt(1) == 'T'){
			this.terrainOnSide.right ="game-trail";
		} else{

		}
		
			
		if(string.charAt(2) == 'J'){
			this.terrainOnSide.bottom ="jungle";
		}
		else if(string.charAt(2) == 'L'){
			this.terrainOnSide.bottom ="lake";
		}
		else if(string.charAt(2) == 'T'){
			this.terrainOnSide.bottom ="game-trail";
		} else{

		}
		
			
		if(string.charAt(3) == 'J'){
			this.terrainOnSide.left ="jungle";
		}
		else if(string.charAt(3) == 'L'){
			this.terrainOnSide.left ="lake";
		}
		else if(string.charAt(3) == 'T'){
			this.terrainOnSide.left ="game-trail";
		} else{

		}
			
		if( string.charAt(4) == '-'){
			this.croc=false;
			this.deer=false;
			this.boar=false;
			this.buffalo=false;
			this.den=false;
		}
		else if(string.charAt(4) =='X'){
			this.croc=false;
			this.deer=false;
			this.boar=false;
			this.buffalo=false;
			this.den=true;
		}
		else if(string.charAt(4) =='B'){
			this.croc=false;
			this.deer=false;
			this.boar=false;
			this.buffalo=true;
			this.den=false;
		}
		else if(string.charAt(4) =='C'){
			this.croc=true;
			this.deer=false;
			this.boar=false;
			this.buffalo=false;
			this.den=false;
		}
		else if(string.charAt(4) =='D'){
			this.croc=false;
			this.deer=true;
			this.boar=false;
			this.buffalo=false;
			this.den=false;
		}
		else if(string.charAt(4) == 'P'){
			this.croc=false;
			this.deer=false;
			this.boar=true;
			this.buffalo=false;
			this.den=false;
		} else{

		}
			
		
		if(string.equals("LLLL-")||string.equals("JLLL-")||string.equals("LLJJ-")||string.equals("TLLT-")||string.equals("JLJL-")||string.equals("TLLTB")||string.equals("TLLL-")||string.equals("TLLLC")){
			this.lakesConnected = true;
		}
		else if(string.equals("JJJJ-")||string.equals("JJJJX")||string.equals("JJTJX")||string.equals("LLJJ-")||string.equals("LJLJ-")||string.equals("TJJT-")||string.equals("LJJJ-")||string.equals("JLLJ-")){
			this.junglesConnected = true;
		}
		else{
			this.lakesConnected = false;
			this.junglesConnected = false;
		}

		if(string.equals("JJJJ-")) this.imagePath = "1.png";
		else if(string.equals("LLLL-")) this.imagePath = "2.png";
		else if(string.equals("TLJT-")) this.imagePath = "3.png";
		else if(string.equals("TLTT-")) this.imagePath = "4.png";
		else if(string.equals("JJJJX")) this.imagePath = "5.png";
		else if(string.equals("JLLL-")) this.imagePath = "6.png";
		else if(string.equals("TLJTP")) this.imagePath = "7.png";
		else if(string.equals("TLTTP")) this.imagePath = "8.png";
		else if(string.equals("JJTJX")) this.imagePath = "9.png";
		else if(string.equals("LLJJ-")) this.imagePath = "10.png";
		else if(string.equals("JLTT-")) this.imagePath = "11.png";
		else if(string.equals("TLLT-")) this.imagePath = "12.png";
		else if(string.equals("TTTT-")) this.imagePath = "13.png";
		else if(string.equals("JLJL-")) this.imagePath = "14.png";
		else if(string.equals("JLTTB")) this.imagePath = "15.png";
		else if(string.equals("TLLTB")) this.imagePath = "16.png";
		else if(string.equals("TJTJ-")) this.imagePath = "17.png";
		else if(string.equals("LJLJ-")) this.imagePath = "18.png";
		else if(string.equals("TLTJ-")) this.imagePath = "19.png";
		else if(string.equals("LJTJ-")) this.imagePath = "20.png";
		else if(string.equals("TJJT-")) this.imagePath = "21.png";
		else if(string.equals("LJJJ-")) this.imagePath = "22.png";
		else if(string.equals("TLTJD")) this.imagePath = "23.png";
		else if(string.equals("LJTJD")) this.imagePath = "24.png";
		else if(string.equals("TJTT-")) this.imagePath = "25.png";
		else if(string.equals("JLLJ-")) this.imagePath = "26.png";
		else if(string.equals("TLLL-")) this.imagePath = "27.png";
		else if(string.equals("TLLLC")) this.imagePath = "28.png";
		else{

		}
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

class TerrainOnSide{
		String up;
		String right;
		String left;
		String bottom;

		TerrainOnSide(){
			this.up = "";
			this.right = "";
			this.bottom = "";
			this.left = "";
		}
		TerrainOnSide(String up, String right, String bottom, String left){
			this.up = up;
			this.right = right;
			this.bottom = bottom;
			this.left = left;
		}


}

