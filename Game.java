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

	// for debugging, turns on the board at the very end of each round for a certain period of time 
	
	JButton[][] grid = new JButton[10][10];

	//creates players, deck and board that belong to this instance of the game
	public Game(String gameName) 
	{
		// Deck, Player, and Board initialized 
		this.deck = new Deck();
		this.player = new Player();
		this.board = new Board();
		this.gameName = gameName;
		this.threadName = gameName;

		// to display on the board
		for(int i=0; i<10; i++){
			for(int j=0; j<10; j++){
				// empty blank tile with no rotation
				ImageIcon blankImage = new ImageIcon("0.png");
				RotatedIcon rotatedImage = new RotatedIcon(blankImage, 0);
				grid[i][j] = new JButton(rotatedImage);
			}
		}

	}	

	public void run() {

		frame = new JFrame(threadName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

			frame.setLayout(new GridLayout(10, 10));
			for(int row=5, row2 = 0; row>-5; row--, row2++){
				for(int column=-5, column2 = 0; column<5; column++, column2++){
					if(this.board.tileTracker[row + (this.board.boardRowNumber/2)][column + (this.board.boardColumnNumber/2)]){
						
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
						JButton addButton = new JButton(rotated);
						grid[row2][column2] = addButton;
						frame.add(grid[row2][column2]);
					} else{
						ImageIcon image4 = new ImageIcon("0.png");
						RotatedIcon rotated = new RotatedIcon(image4, 0);
						JButton addButton = new JButton(rotated);
						grid[row2][column2] = addButton;
						frame.add(grid[row2][column2]);
					}
				}
			}
		
		frame.setLocationRelativeTo(null); 
		frame.pack();
		frame.setVisible(true);
	}
	public void update(){

		for(int row=0; row<10; row++){
				for(int column=0; column<10; column++){
					// remove everything that is currently on the board 
					frame.remove(grid[row][column]);
			}	
		}
		// now we update the grid array
		for(int row=5, row2 = 0; row>-5; row--, row2++){
				for(int column=-5, column2 =0; column<5; column++, column2++){
					if(this.board.tileTracker[row + (this.board.boardRowNumber/2)][column + (this.board.boardColumnNumber/2)]){

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
						grid[row2][column2] = new JButton(rotated);
					} else{
						ImageIcon image4 = new ImageIcon("0.png");
						RotatedIcon rotated = new RotatedIcon(image4, 0);
						grid[row2][column2] = new JButton(rotated);
					}		
			}
		}

		// now we update the UI
		for(int row=0; row<10; row++){
				for(int column=0; column<10; column++){
					// remove everything that is currently on the board 
					frame.add(grid[row][column]);
			}	
		}

		SwingUtilities.updateComponentTreeUI(frame);

	}

	// stop GUI
	public void stop(){
			this.frame.dispose();
	}

	// start GUI
	public void start(){
		if(t == null){
			t = new Thread(this, threadName);
			t.start();
		}
	}
	
}