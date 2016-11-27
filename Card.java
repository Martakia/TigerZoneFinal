
import java.lang.StringBuilder;
// Card Class contains raw Card information, Tile class handles positioning on board, neighbors, tigers etc. 
public class Card{
	
	// handles 4 terrain sides
	 TerrainOnSide terrainOnSide;
	
	// Special information
	 boolean deer;
	 boolean boar;
	 boolean buffalo;
	 boolean den;
	 boolean lakesConnected;
	 boolean junglesConnected;
	 boolean croc;
	 String CardCode;
	public String imagePath;//path to png files
	
	Card(String string){
		
		this.CardCode = string;
		terrainOnSide = new TerrainOnSide();
		for(int i=0; i<string.length();i++){
			// this.terrainOnSide = new TerrainOnSide(terrainUp, terrainRight, terrainBottom, terrainLeft);
			if(i==0){
				if(string.charAt(i) == 'J'){
					this.terrainOnSide.up ="Jungle";
				}
				else if(string.charAt(i) == 'L'){
					this.terrainOnSide.up ="Lake";
				}
				else if(string.charAt(i) == 'T'){
					this.terrainOnSide.up = "Trail";
				}
			}
			if(i==1){
				if(string.charAt(i) == 'J'){
					this.terrainOnSide.right = "Jungle";
				}
				else if(string.charAt(i) == 'L'){
					this.terrainOnSide.right ="Lake";
				}
				else if(string.charAt(i) == 'T'){
					this.terrainOnSide.right ="Trail";
				}
			}
			if(i==2){
				if(string.charAt(i) == 'J'){
					this.terrainOnSide.bottom ="Jungle";
				}
				else if(string.charAt(i) == 'L'){
					this.terrainOnSide.bottom ="Lake";
				}
				else if(string.charAt(i) == 'T'){
					this.terrainOnSide.bottom ="Trail";
				}
			}
			if(i==3){
				if(string.charAt(i) == 'J'){
					this.terrainOnSide.left ="Jungle";
				}
				else if(string.charAt(i) == 'L'){
					this.terrainOnSide.left ="Lake";
				}
				else if(string.charAt(i) == 'T'){
					this.terrainOnSide.left ="Trail";
				}
			}
			if(i==4){
				if( string.charAt(i) == '-'){
					this.croc=false;
					deer=false;
					boar=false;
					buffalo=false;
					den=false;
				}
				else if(string.charAt(i) =='X'){
					croc=false;
					deer=false;
					boar=false;
					buffalo=false;
					den=true;
				}
				else if(string.charAt(i) =='B'){
					croc=false;
					deer=false;
					boar=false;
					buffalo=true;
					den=false;
				}
				else if(string.charAt(i) =='C'){
					croc=true;
					deer=false;
					boar=false;
					buffalo=false;
					den=false;
				}
				else if(string.charAt(i) =='D'){
					croc=false;
					deer=true;
					boar=false;
					buffalo=false;
					den=false;
				}
				else if(string.charAt(i) == 'P'){
					croc=false;
					deer=false;
					boar=true;
					buffalo=false;
					den=false;
				}
			}
		}
		if(string.equals("LLLL-")||string.equals("JLLL-")||string.equals("LLJJ-")||string.equals("TLLT-")||string.equals("JLJL-")||string.equals("TLLTB")||string.equals("TLLL-")||string.equals("TLLLC")){
			this.lakesConnected = true;
		}
		else if(string.equals("JJJJ-")||string.equals("JJJJX")||string.equals("JJTJX")||string.equals("LLJJ-")||string.equals("LJLJ-")||string.equals("TJJT-")||string.equals("LJJJ-")||string.equals("JLLJ-")){
			this.junglesConnected = true;
		}
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

}


