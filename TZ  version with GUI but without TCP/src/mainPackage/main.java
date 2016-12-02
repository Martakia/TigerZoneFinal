package mainPackage;
import java.util.Scanner;

public class main {

	public static void main(String[] args) {		

		IO.init();//declares important stuff
		WindowThread window = new WindowThread();		
		IO.currentGame=0;//code supports 2 games at one but only one can be rendered at time
		Scanner reader = new Scanner(System.in);
		int select=0;
		do
		{
			System.out.println("Select: ");
			System.out.println(" 1 - game AI vs AI");
			System.out.println(" 2 - game HUMAN vs AI");
			select = reader.nextInt();
		}while(select!=1&&select!=2);
		Game game;
		if(select==1)
			game = new Game("AI","AI",0);//set Game("AI","HUMAN",0) to play yourself
		else
		{
			game = new Game("HUMAN","AI",0);
			System.out.println("--------------use R to rotate card and C to skip tiger placement----------------------");
		}
			
		window.setGame(game);
		window.start();
		while(!IO.noCardsLeft[IO.currentGame])
			game.playTurn();
		System.out.println("END");
	}
}


