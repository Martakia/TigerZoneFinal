
import java.util.Stack;
import java.util.ArrayList;
public class Deck {
	
	public int cardsCreated;
	public  ArrayList<Card> cards;
	
	// empty constructor
	Deck()
	{
		
	}
	
	public void ImportCardData(ArrayList<Card> cards ){
		this.cards = cards;
	}
	
}
