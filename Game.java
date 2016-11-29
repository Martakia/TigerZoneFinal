import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;

public class Game implements Runnable{
	
	Player player;
	Board board;
	Deck deck;

	private Thread t;
	private String threadName;
	private String gameName;
	private JFrame frame;
	
	//creates players, deck and board that belong to this instance of the game
	public Game(String gameName) 
	{
		// Deck, Player, and Board initialized 
		this.deck = new Deck();
		this.player = new Player();
		this.board = new Board();
		this.gameName = gameName;
		this.threadName = gameName;
		this.start();
	}	

		public void run() {

		frame = new JFrame(threadName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

		JLabel textLabel = new JLabel("This is working",SwingConstants.CENTER); 
		textLabel.setPreferredSize(new Dimension(500, 500)); 
		frame.getContentPane().add(textLabel, BorderLayout.CENTER); 
		
		frame.setLocationRelativeTo(null); 
		frame.pack();
		frame.setVisible(true);
	}

	public void stop(){
		this.frame.dispose();
	}

	public void start(){
		if(t == null){
			t = new Thread(this, threadName);
			t.start();
		}
	}
	
}

