package mainPackage;

public class WindowThread extends Thread {
	
	Game game;
	Window window;
	public void setGame(Game game)
	{	
		this.game = game;		
	}
	public void run()
	{
		window = new Window(game);  
	    window.run();	    
	}
}
