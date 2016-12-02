package mainPackage;
import java.util.Stack;
import java.io.FileReader;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Collections;
public class Deck {
	
	private int cardsCreated;
	private final ArrayList<Card> cards;
	private Stack<Card> finalShuffledDeck;
	
	Deck()
	{
		System.out.println("CHECKPOINT: Deck initialization has began");
		// card count initially set to 0
		this.cardsCreated = 0;
		this.cards = new ArrayList<Card>();

		// import card information from files and create card objects and add to arrayList
		ImportDeckFile("CardData.txt");
		
		// after the deck has been created with all the cards, we have to shuffle each the array
		ShuffleDeck();
		
		// initialize stack that will hold each of the cards
		this.finalShuffledDeck = new Stack<Card>();
		
		// after array is shuffled, we want to throw everything to a stack 
		for(int i=0; i<cards.size(); i++){
			// push each of the Cards in the shuffled array to the stack
			finalShuffledDeck.push(cards.get(i));
		}
		System.out.println("CHECKPOINT: Deck initialization completed");
	}
	
	private void ShuffleDeck()
	{
		Collections.shuffle(cards);
		System.out.println("CHECKPOINT: Deck has been succsefully shuffled");
	}
	
	// method that will get the top card in the stack, will be returned to the player
	public Card NextCardInDeck(){
		Card tmp=finalShuffledDeck.pop();
		System.out.println("Card draw "+tmp.terrainOnSide.up+" "+tmp.terrainOnSide.right+" "+tmp.terrainOnSide.bottom+" "+tmp.terrainOnSide.left);
			return tmp ;
	}
	
	// used to let main game know when it is over, if deck size is 0 no more cards remaining and game can end
	public boolean IsEmpty(){
		return finalShuffledDeck.isEmpty();
	}
	
	// when the games starts up, the players will be told all the cards and order, they will be able to use this infomration to plan moves, strategies etc.
	public ArrayList<Card> shareDeckToPlayers(){
		
		ArrayList<Card> copy = new ArrayList<Card>();
		
		// create shallow copy of each card and add them to new arrayList, each card is initilized using final so OK to have shallow copy
		for(int i=0; i<this.cards.size(); i++){
			Card shallowCopy = this.cards.get(i);
			copy.add(shallowCopy);	
		}
		return copy;
	}
	
	private void ImportDeckFile(String fileName)
	{	
		// reading data from file which specifies all the cards that needs to be created
		try{
			Scanner in = new Scanner(new FileReader(fileName));
			while(in.hasNextLine()){
				String current = in.nextLine();
				if(current.equals("") || current.equals(null) ){
					// do nothing, empty line
				}
				else if(current.charAt(0)== '/' && current.charAt(1) == '/'){
					// comment do nothing with this line
				}

				else{
					// Split String information in each line
					String[] cardInfo = current.split(",");
					
					// format of imported data is, String, String, String, String, boolean, boolean, boolean, boolean, boolean, boolean
					Card newCard = new Card(cardInfo[0].trim(), cardInfo[1].trim(), cardInfo[2].trim(), cardInfo[3].trim(),
							Boolean.valueOf(cardInfo[4].trim()), Boolean.valueOf(cardInfo[5].trim()), Boolean.valueOf(cardInfo[6].trim()),
							Boolean.valueOf(cardInfo[7].trim()),Boolean.valueOf(cardInfo[8].trim()), Boolean.valueOf(cardInfo[9].trim()));
					newCard.setPathToImage(cardInfo[10].trim());
					
					cards.add(newCard);
					cardsCreated++;
				}				
			}			
		}
		catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("CHECKPOINT: Card import sucessfull, imported " +cardsCreated+ " total cards");
	}

}
