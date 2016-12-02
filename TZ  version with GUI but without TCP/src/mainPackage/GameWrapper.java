package mainPackage;

import java.util.Scanner;

public class GameWrapper {
	
	Game games[];
	Game AIvsServer;
	Game ServerVsAI;
	WindowThread windowThread;
	int renderGameNumber=-1;
	int count=0;
	
	GameWrapper()
	{
		IO.currentGame=0;
		AIvsServer = new Game("AI","SERVER",0);
		IO.currentGame=1;
		ServerVsAI = new Game("SERVER","AI",1);
		games=new Game[2];
		IO.currentGame=0;
		games[0]=AIvsServer;
		IO.currentGame=1;
		games[1]=ServerVsAI;
		windowThread = new WindowThread();
		IO.currentCrad[1]=ServerVsAI.deck.NextCardInDeck();
	}
	PlayerMoveInformation playRoundInGameA(PlayerMoveInformation formattedResponseFromServer)
	{
		System.out.println("PLAYING A START");
		IO.currentGame=0;		
		IO.moveInfoFromServer[IO.currentGame]=formattedResponseFromServer;
		
		AIvsServer.playTurn();//server player make move based on "formattedResponseFromServer" to modify our borad			
		AIvsServer.playTurn();//our AI make move and fills IO.moveInfoToServer[IO.currentGame] with data
		IO.currentCrad[IO.currentGame]=AIvsServer.deck.NextCardInDeck();
		
		renderGame();
		return IO.moveInfoToServer[IO.currentGame];
	}
	PlayerMoveInformation playRoundInGameA()
	{
		System.out.println("PLAYING A START");
		IO.currentGame=0;
		AIvsServer.playTurn();//our AI make move and fills IO.moveInfoToServer[IO.currentGame] with data	
		IO.currentCrad[IO.currentGame]=AIvsServer.deck.NextCardInDeck();
		renderGame();
		return IO.moveInfoToServer[IO.currentGame];
	}
	PlayerMoveInformation playRoundInGameB(PlayerMoveInformation formattedResponseFromServer)
	{
		System.out.println("PLAYING B");
		IO.currentGame=1;	
		
		IO.moveInfoFromServer[IO.currentGame]=formattedResponseFromServer;		
		ServerVsAI.playTurn();//server player make move based on "formattedResponseFromServer" to modify our borad			
		ServerVsAI.playTurn();//our AI make move and fills IO.moveInfoToServer[IO.currentGame] with data
		IO.currentCrad[IO.currentGame]=ServerVsAI.deck.NextCardInDeck();
		
		renderGame();
		
		return IO.moveInfoToServer[IO.currentGame];		
	}
	PlayerMoveInformation setDataInstedOfServerInGameNumber(int gameNumber)
	{		

			Scanner reader = new Scanner(System.in);  // Reading from System.in
			System.out.println("Enter cardRow(0-155), cardColumn(0-155), cardRotation(0-4 clockwise), true/false(for tiger placement), tigerRow(0-2), tigerColumn(0-2)");
			int cardRow = reader.nextInt();
			int cardColumn = reader.nextInt();
			int cardOrientation = reader.nextInt();
			boolean tigerPlaced = reader.nextBoolean();
			int tigerRow = reader.nextInt();
			int tigerColumn = reader.nextInt();
		//  VVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVVV we formed (filled) var below with info from input. this data will be used in player.makemove in case of server player
			return new PlayerMoveInformation(IO.currentCrad[IO.currentGame],cardRow,cardColumn,cardOrientation,tigerPlaced,cardRow*3+tigerRow,cardColumn*3+tigerColumn);
	}
	
	void setGameToRender(int num)
	{
		renderGameNumber=num;
		IO.gameToRender=num;
		count=0;
	}

	void renderGame()
	{
		if(count==0)
		{			
			if(renderGameNumber==1||renderGameNumber==0)
			{				
				windowThread.setGame(games[renderGameNumber]);
				windowThread.start();
				System.out.println("STARTING RENDER");
			}			
		}
		count++;
	}
	
}


