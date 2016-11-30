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
	}	

	public void display(){
		this.start();
		try{
			Thread.sleep(5000);
		} catch(Exception e){

		}
	}

	public void run() {

		frame = new JFrame(threadName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

			int check = 0;
			frame.setLayout(new GridLayout(10, 10));
			for(int row=5; row>-5; row--){
				for(int column=-5; column<5; column++){
					if(this.board.tileTracker[row + (this.board.boardRowNumber/2)][column + (this.board.boardColumnNumber/2)]){
						check++;
						System.out.println(check);
						// something placed at that location
						ImageIcon image3 = new ImageIcon(board.tileArray[row + (this.board.boardRowNumber/2)][column + (this.board.boardColumnNumber/2)].imagePath);
						RotatedIcon rotated;
						int rotate = board.tileArray[row + (this.board.boardRowNumber/2)][column + (this.board.boardColumnNumber/2)].rotation;
						if(rotate == 0){
							rotated = new RotatedIcon(image3, 0);
						} else if (rotate == 1){
							rotated = new RotatedIcon(image3, 270);
						} else if (rotate == 2){
							rotated = new RotatedIcon(image3, 180);
						} else {
							// must be 3 rotate
							rotated = new RotatedIcon(image3, 90);
						}
						
						frame.add(new JButton(rotated));
					} else{
						ImageIcon image4 = new ImageIcon("0.png");
						RotatedIcon ri = new RotatedIcon(image4, 0);
						frame.add(new JButton(ri));
					}
				}
			}
		
		frame.setLocationRelativeTo(null); 
		frame.pack();
		frame.setVisible(true);
	}
	public void update(){


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

