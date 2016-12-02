package mainPackage;

import java.util.Vector;

import javafx.application.Application;
import javafx.scene.input.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import java.io.IOException;
import java.net.URL;
import javafx.animation.*;
import javafx.event.*;
import javafx.scene.control.Label;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.util.Calendar;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;


import java.util.Timer;
import java.util.TimerTask;

public class Window extends Application {
	
	public static Group root;
	public Board board;
	int sizeX, sizeY, viewOffsetX, viewOffsetY, tileSize, viewMoveSpeed;
	public myRectangle rectangles [][];
	Vector<Vector2d> possiblePosRect;
	Vector<myCircle> circlesForTigerSlots;
	Vector<myCircle> circlesForTigers;
	Rectangle currentRectanngle;
	Text textCurrent [];
	Text text [][][];
	Scene scene;	
	public static Game game;	
	
	public Window()
	{
		
	}
	public Window(Game game)
	{
		this.game = game;
	}
	
    @Override
    public void start(Stage primaryStage) {   	
 	
    	//renderBoard();   
		board = new Board(); 		
		possiblePosRect = new Vector<Vector2d>(0);
		circlesForTigerSlots = new Vector<myCircle>(0);
		circlesForTigers = new Vector<myCircle>(0);
		
		setViewportData();
		setRoot(primaryStage);		
		setTimeline();
		initCurrentCard();
		initRectangles();
    	handleMouseAndKeyboard();   
    }  
    private void handleMouseAndKeyboard()
    {
    	root.setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent me) {

                //log mouse move to console, method listed below
            	root.requestFocus();
            }

        });
        root.setOnKeyPressed(new EventHandler<KeyEvent>() {

            public void handle(KeyEvent ke) {

            	root.requestFocus();
            	if(ke.getCode() == KeyCode.W)
            	{
            		root.setTranslateY(root.getTranslateY()+viewMoveSpeed);
            		currentRectanngle.setY(-root.getTranslateY() + sizeY - tileSize*2 - 10);       
            		renderCurrentCard();
            	}
            	if(ke.getCode() == KeyCode.S)
            	{
            		root.setTranslateY(root.getTranslateY()-viewMoveSpeed);
            		currentRectanngle.setY(-root.getTranslateY() + sizeY - tileSize*2 - 10);  
            		renderCurrentCard();
            	}
            	if(ke.getCode() == KeyCode.D)
            	{
            		root.setTranslateX(root.getTranslateX()+viewMoveSpeed);
            		currentRectanngle.setX(-root.getTranslateX() + sizeX - tileSize*2 - 10);            
            		renderCurrentCard();
            	}
            	if(ke.getCode() == KeyCode.A)
            	{
            		root.setTranslateX(root.getTranslateX()-viewMoveSpeed);
            		currentRectanngle.setX(-root.getTranslateX() + sizeX - tileSize*2 - 10);  
            		renderCurrentCard();
            	}
            	if(ke.getCode() == KeyCode.R)
            	{
            		IO.rotation[IO.currentGame]=(IO.rotation[IO.currentGame]+1)%4;    
            		currentRectanngle.setRotate(IO.rotation[IO.currentGame]*90);
            	}
            	if(ke.getCode() == KeyCode.C)
            	{
            		IO.playerPlacedMeeple[IO.currentGame]=true;
					IO.tigerSlotsUpdated[IO.currentGame]=false;		
					IO.didPlayerDecideToPlaceMeeple[IO.currentGame]=false;
					for(int g=0;g<circlesForTigerSlots.size();g++)
					{
						root.getChildren().remove(circlesForTigerSlots.get(g));
					}
					circlesForTigerSlots.clear();
					
            	}
//            	System.out.println(root.getTranslateX() + " " + root.getTranslateY());
            }

        });
//        System.out.println("CHECKPOINT: IO set");
    }
    public void renderBoard()
    {  
    	if(IO.gameToRender==IO.currentGame)
    	{
    		board = IO.board[IO.currentGame];
//        	System.out.println("IO.boardTmp[IO.currentGame].actualPlacementTracker.size() "+IO.boardTmp[IO.currentGame].actualPlacementTracker.size());
            for(int q=0;q< IO.boardTmp[IO.currentGame].actualPlacementTracker.size();q++)
            {        	
            	int row = IO.boardTmp[IO.currentGame].actualPlacementTracker.get(q).row;
            	int column = IO.boardTmp[IO.currentGame].actualPlacementTracker.get(q).column;
            	if(!IO.boardTmp[IO.currentGame].tileWasRendered[row][column])
          		 {	
          			 rectangles [row][column] = new myRectangle();
          			 rectangles [row][column].setY(row*tileSize);
          			 rectangles [row][column].setX(column*tileSize);
          			 rectangles [row][column].setWidth(tileSize);
          			 rectangles [row][column].setHeight(tileSize);
          			 rectangles [row][column].setStroke(Color.WHITE);
          			 rectangles [row][column].setStrokeWidth(2);
          			 rectangles [row][column].row = row;
          			 rectangles [row][column].column = column;
          			 rectangles [row][column].toFront();
          			 if(IO.boardTmp[IO.currentGame].tileArray[row][column]!=null)
          			 {
              			 rectangles [row][column].setFill(
             	        	      new ImagePattern(
                     	    	        new Image("file:".concat(IO.boardTmp[IO.currentGame].tileArray[row][column].imagePath)), 0, 0, 1, 1, true
                     	    	      ));
              			rectangles [row][column].setRotate(IO.boardTmp[IO.currentGame].tileArray[row][column].rotation*90);
          			 }

          			 
           			 rectangles [row][column].setOnMouseClicked(new EventHandler<MouseEvent>() {

              	         public void handle(MouseEvent me) {

        					Object obj = me.getSource(); 
        					
        					if ( obj instanceof myRectangle )
        						{    							
    							int row = ((myRectangle) obj).row;
    							int column = ((myRectangle) obj).column;
    							
    							System.out.println(" row "+IO.boardTmp[IO.currentGame].tileArray[row][column].row+" column "+IO.boardTmp[IO.currentGame].tileArray[row][column].column);
    							System.out.println(IO.boardTmp[IO.currentGame].tileArray[row][column].finalPlacedOrientation.up+" "+IO.boardTmp[IO.currentGame].tileArray[row][column].finalPlacedOrientation.right+" "+IO.boardTmp[IO.currentGame].tileArray[row][column].finalPlacedOrientation.bottom+" "+IO.boardTmp[IO.currentGame].tileArray[row][column].finalPlacedOrientation.left);
    							System.out.println(IO.boardTmp[IO.currentGame].sectionArray[row*3][column*3].tiger+" "+IO.boardTmp[IO.currentGame].sectionArray[row*3][column*3+1].tiger+" "+IO.boardTmp[IO.currentGame].sectionArray[row*3][column*3+2].tiger
    									+"           "+IO.boardTmp[IO.currentGame].sectionArray[row*3][column*3].terrain+" "+IO.boardTmp[IO.currentGame].sectionArray[row*3][column*3+1].terrain+" "+IO.boardTmp[IO.currentGame].sectionArray[row*3][column*3+2].terrain);
    							System.out.println(IO.boardTmp[IO.currentGame].sectionArray[row*3+1][column*3].tiger+" "+IO.boardTmp[IO.currentGame].sectionArray[row*3+1][column*3+1].tiger+" "+IO.boardTmp[IO.currentGame].sectionArray[row*3+1][column*3+2].tiger
    									+"           "+IO.boardTmp[IO.currentGame].sectionArray[row*3+1][column*3].terrain+" "+IO.boardTmp[IO.currentGame].sectionArray[row*3+1][column*3+1].terrain+" "+IO.boardTmp[IO.currentGame].sectionArray[row*3+1][column*3+2].terrain);
    							System.out.println(IO.boardTmp[IO.currentGame].sectionArray[row*3+2][column*3].tiger+" "+IO.boardTmp[IO.currentGame].sectionArray[row*3+2][column*3+1].tiger+" "+IO.boardTmp[IO.currentGame].sectionArray[row*3+2][column*3+2].tiger
    									+"           "+IO.boardTmp[IO.currentGame].sectionArray[row*3+2][column*3].terrain+" "+IO.boardTmp[IO.currentGame].sectionArray[row*3+2][column*3+1].terrain+" "+IO.boardTmp[IO.currentGame].sectionArray[row*3+2][column*3+2].terrain);
    	    						for(int r=0;r<3;r++)
    	    						{
    	    							for(int c=0;c<3;c++)
    	    							{
    	    								if(IO.boardTmp[IO.currentGame].sectionArray[row*3+r][column*3+c].lakeUpTrav)
    	    									System.out.print("U");
    	    								else
    	    									System.out.print("_");
    	      								if(IO.boardTmp[IO.currentGame].sectionArray[row*3+r][column*3+c].lakeRightTrav)
    	    									System.out.print("R");
    	    								else
    	    									System.out.print("_");
    	      								if(IO.boardTmp[IO.currentGame].sectionArray[row*3+r][column*3+c].lakeBotTrav)
    	    									System.out.print("B");
    	    								else
    	    									System.out.print("_");
    	      								if(IO.boardTmp[IO.currentGame].sectionArray[row*3+r][column*3+c].lakeLeftTrav)
    	    									System.out.print("L");
    	    								else
    	    									System.out.print("_");
    	      								if(IO.boardTmp[IO.currentGame].sectionArray[row*3+r][column*3+c].upTravercable)
    	    									System.out.print("u");
    	    								else
    	    									System.out.print("_");
    	      								if(IO.boardTmp[IO.currentGame].sectionArray[row*3+r][column*3+c].rightTravercable)
    	    									System.out.print("r");
    	    								else
    	    									System.out.print("_");
    	      								if(IO.boardTmp[IO.currentGame].sectionArray[row*3+r][column*3+c].botTravercable)
    	    									System.out.print("b");
    	    								else
    	    									System.out.print("_");
    	      								if(IO.boardTmp[IO.currentGame].sectionArray[row*3+r][column*3+c].leftTravercable)
    	    									System.out.print("l");
    	    								else
    	    									System.out.print("_");
    	      								System.out.print(" | ");
    	    							}
    	    							System.out.println();
    	    						}
        						}	       	            	
              	            }

              	        });
          			 root.getChildren().add(rectangles [row][column]);

      				 text[row][column][0] = new Text();
      				 text[row][column][0].setText("" + IO.boardTmp[IO.currentGame].tileArray[row][column].finalPlacedOrientation.up.charAt(0));
      				 text[row][column][0].setY(row*tileSize+12);
      				 text[row][column][0].setX(column*tileSize+18);
      				 text[row][column][0].setFill(Color.RED);       				
          			 root.getChildren().add(text[row][column][0]);

      				 text[row][column][1] = new Text();
      				 text[row][column][1].setText("" + IO.boardTmp[IO.currentGame].tileArray[row][column].finalPlacedOrientation.right.charAt(0));
      				 text[row][column][1].setY(row*tileSize+25);
      				 text[row][column][1].setX(column*tileSize+31);
      				 text[row][column][1].setFill(Color.RED);
          			 root.getChildren().add(text[row][column][1]);	       			 

      				 text[row][column][2] = new Text();
      				 text[row][column][2].setText("" + IO.boardTmp[IO.currentGame].tileArray[row][column].finalPlacedOrientation.bottom.charAt(0));
      				 text[row][column][2].setY(row*tileSize+35);
      				 text[row][column][2].setX(column*tileSize+18);
      				 text[row][column][2].setFill(Color.RED);
          			 root.getChildren().add(text[row][column][2]);
    //
      				 text[row][column][3] = new Text();
      				 text[row][column][3].setText("" + IO.boardTmp[IO.currentGame].tileArray[row][column].finalPlacedOrientation.left.charAt(0));
      				 text[row][column][3].setY(row*tileSize+25);
      				 text[row][column][3].setX(column*tileSize+5);
          			 text[row][column][3].setFill(Color.RED);
          			 root.getChildren().add(text[row][column][3]);
          			 
          			IO.board[IO.currentGame].tileWasRendered[row][column] = true;   
          			
      			 }
            }
            if(IO.updatePlacments[IO.currentGame])
            {
                for(int q=0;q<possiblePosRect.size();q++)
                {
                	root.getChildren().remove(rectangles [possiblePosRect.get(q).row][possiblePosRect.get(q).column]);
                	IO.board[IO.currentGame].tileWasRendered[possiblePosRect.get(q).row][possiblePosRect.get(q).column]=false;
                }
                IO.updatePlacments[IO.currentGame]=false;

            }

            for(int q=0;q< IO.boardTmp[IO.currentGame].possiblePlacementTracker.size();q++)
            {
            	int row = IO.boardTmp[IO.currentGame].possiblePlacementTracker.get(q).row;
            	int column = IO.boardTmp[IO.currentGame].possiblePlacementTracker.get(q).column;
            	for(int r=0;r<IO.possibilities[IO.currentGame].size();r++)
            	{
            		if(IO.possibilities[IO.currentGame].get(r).row == row&&IO.possibilities[IO.currentGame].get(r).column==column)
            		{
                    	if(!IO.board[IO.currentGame].tileWasRendered[row][column])
                    	{
                    		possiblePosRect.add(new Vector2d(row,column));
                    		rectangles [row][column] = new myRectangle();
                  			 rectangles [row][column].setY(row*tileSize);
                  			 rectangles [row][column].setX(column*tileSize); 
                  			 rectangles [row][column].setWidth(tileSize);
                  			 rectangles [row][column].setHeight(tileSize);
                  			 rectangles [row][column].setStroke(Color.WHITE);
                  			 rectangles [row][column].setStrokeWidth(2);
                  			 rectangles [row][column].row = row;
                  			 rectangles [row][column].column = column;
                  			 rectangles [row][column].setFill(Color.GRAY);	 
                  			 IO.board[IO.currentGame].tileWasRendered[row][column] = true;
                  			 rectangles [row][column].setOnMouseClicked(new EventHandler<MouseEvent>() {

                  	         public void handle(MouseEvent me) {

            					Object obj = me.getSource(); 
            					
            					if ( obj instanceof myRectangle )
            						{    							
        							IO.row[IO.currentGame] = ((myRectangle) obj).row;
        							IO.column[IO.currentGame] = ((myRectangle) obj).column;
        							IO.playerPlacedCard [IO.currentGame]= true;
        							System.out.println("IO.row[IO.currentGame] "+IO.row[IO.currentGame]+" IO.column[IO.currentGame] "+IO.column[IO.currentGame]+" IO.rotation[IO.currentGame] "+IO.rotation[IO.currentGame]);
            						}	       	            	
                  	            }

                  	        });
                  			 root.getChildren().add(rectangles [row][column]);
                    	}        			
            		}
            	}	
            }	
            renderPlacementSlots();
//            if(IO.tigerSlotsUpdated[IO.currentGame])
//            {
//            	renderPlacementSlots();
//            }
            for(int g=0;g<circlesForTigers.size();g++)
            {
            	circlesForTigers.get(g).toFront();
            }
    	}
    	
    }
    public void renderCurrentCard()
    {   
    	if(IO.gameToRender==IO.currentGame)
    	{
    		  double cx = currentRectanngle.getX();
    	        double cy = currentRectanngle.getY(); 
    	        currentRectanngle.toFront();
//    	        System.out.println(IO.currentCrad[IO.currentGame].terrainOnSide.up+" "+IO.currentCrad[IO.currentGame].terrainOnSide.right+" "+IO.currentCrad[IO.currentGame].terrainOnSide.bottom+" "+IO.currentCrad[IO.currentGame].terrainOnSide.left+" "+IO.currentCrad[IO.currentGame].imagePath);
    	        currentRectanngle.setFill(
    	        	      new ImagePattern(
    	        	    	        new Image("file:".concat(IO.currentCrad[IO.currentGame].imagePath)), 0, 0, 1, 1, true
    	        	    	      ));
    	        
    	        currentRectanngle.setRotate(IO.rotation[IO.currentGame]*90);
    	        textCurrent[(0+IO.rotation[IO.currentGame])%4].setText("" + IO.currentCrad[IO.currentGame].terrainOnSide.up.charAt(0));
    	        textCurrent[0].setX(cx+tileSize-5);
    	        textCurrent[0].setY(cy+19);		
    	 
    	        textCurrent[(1+IO.rotation[IO.currentGame])%4].setText("" + IO.currentCrad[IO.currentGame].terrainOnSide.right.charAt(0));
    	        textCurrent[1].setX(cx+tileSize*2-19);
    	        textCurrent[1].setY(cy+tileSize);		

    	        textCurrent[(2+IO.rotation[IO.currentGame])%4].setText("" + IO.currentCrad[IO.currentGame].terrainOnSide.bottom.charAt(0));
    	        textCurrent[2].setX(cx+tileSize-5);
    	        textCurrent[2].setY(cy+tileSize*2-10);		
    	 
    	        textCurrent[(3+IO.rotation[IO.currentGame])%4].setText("" + IO.currentCrad[IO.currentGame].terrainOnSide.left.charAt(0));
    	        textCurrent[3].setX(cx+5);
    	        textCurrent[3].setY(cy+tileSize);
    	}
      
    }
    private void setViewportData()
    {
		sizeX = 800;
		sizeY = 800;           
		viewMoveSpeed = 5;
		tileSize = 80;		
		viewOffsetX = tileSize*board.boardColumnNumber/2;
		viewOffsetY = tileSize*board.boardRowNumber/2;
		
   	}
    private void setTimeline()
    {
        Timeline timeline = new Timeline(
      	      new KeyFrame(Duration.millis(200),
      	        new EventHandler<ActionEvent>() {
      	          @Override public void handle(ActionEvent actionEvent) {      	        	  
      	        //	  if(IO.possibilityUpdated[IO.currentGame])
      	        		  renderBoard();
      	        //	  if(IO.cardUpdated[IO.currentGame])
      	        		  renderCurrentCard();
      	          }
      	        }
      	      ),
      	      new KeyFrame(Duration.millis(200))
      	    );
      	    timeline.setCycleCount(Animation.INDEFINITE);
      	    timeline.play();
    }
    private void initRectangles()
   {
   	for(int i=0;i<board.boardRowNumber;i++)
   	{
   		for(int j=0;j<board.boardColumnNumber;j++)
   		{
   			rectangles[i][j] =  new myRectangle();
   		}
   	}
   }
    private void initCurrentCard()
    {
    	rectangles = new myRectangle [board.boardRowNumber][board.boardColumnNumber];
		currentRectanngle = new Rectangle();
		text= new Text [board.boardRowNumber][board.boardColumnNumber][4];  
		
    	currentRectanngle.setWidth(tileSize*2);
    	currentRectanngle.setHeight(tileSize*2);
    	currentRectanngle.setX(-root.getTranslateX() + sizeX - tileSize*2 - 10);
    	currentRectanngle.setY(-root.getTranslateY() + sizeY - tileSize*2 - 10);
    	currentRectanngle.setFill(Color.SLATEGRAY);
    	root.getChildren().add(currentRectanngle);
    	
    	textCurrent = new Text[4];
    	
        for(int i =0;i<4;i++)
        {
        	textCurrent[i] = new Text();
        }
        
        double cx = currentRectanngle.getX();
        double cy = currentRectanngle.getY();
        
        textCurrent[0].setText("UP");
        textCurrent[0].setX(cx+tileSize-5);
        textCurrent[0].setY(cy+19);
        textCurrent[0].setFill(Color.RED);       				
		root.getChildren().add(textCurrent[0]);
		
        textCurrent[1].setText("RT");
        textCurrent[1].setX(cx+tileSize*2-19);
        textCurrent[1].setY(cy+tileSize);
        textCurrent[1].setFill(Color.RED);       				
		root.getChildren().add(textCurrent[1]);
		
        textCurrent[2].setText("BT");
        textCurrent[2].setX(cx+tileSize-5);
        textCurrent[2].setY(cy+tileSize*2-10);
        textCurrent[2].setFill(Color.RED);       				
		root.getChildren().add(textCurrent[2]);
		
        textCurrent[3].setText("LT");
        textCurrent[3].setX(cx+5);
        textCurrent[3].setY(cy+tileSize);
        textCurrent[3].setFill(Color.RED);       				
		root.getChildren().add(textCurrent[3]);
    	
//    	System.out.println(currentRectanngle.getX() + " CR " + currentRectanngle.getY());
//    	System.out.println("board.boardRowNumber "+board.boardRowNumber+" "+board.boardRowNumber);
    }
    private void setRoot(Stage primaryStage)
    {
		root = new Group();   
		scene = new Scene(root, sizeX , sizeY);
		primaryStage.setScene(scene);        
        primaryStage.show();		
		root.setTranslateX(-viewOffsetX+sizeX/2);
		root.setTranslateY(-viewOffsetY+sizeY/2);
		root.requestFocus();
    }
    public void run(){    	
        launch();
    }   
    public void renderPlacementSlots()
    {
    	if(IO.gameToRender==IO.currentGame)
    	{
        	for(int g=0;g<IO.tigerPlacementSlots[IO.currentGame].size();g++)
        	{
        		myCircle tmpCircle = new myCircle();
        		tmpCircle.setRadius(tileSize/6);
        		tmpCircle.row = IO.tigerPlacementSlots[IO.currentGame].get(g).row;
        		tmpCircle.column = IO.tigerPlacementSlots[IO.currentGame].get(g).column;
        		tmpCircle.setCenterY(IO.tigerPlacementSlots[IO.currentGame].get(g).row*tileSize/3+tileSize/6);
        		tmpCircle.setCenterX(IO.tigerPlacementSlots[IO.currentGame].get(g).column*tileSize/3+tileSize/6); 
        		root.getChildren().add(tmpCircle);
        		circlesForTigerSlots.add(tmpCircle);
      			tmpCircle.setOnMouseClicked(new EventHandler<MouseEvent>() {

          	         public void handle(MouseEvent me) {

    					Object obj = me.getSource(); 
    					
    					if ( obj instanceof myCircle )
    						{    							
    							int row = ((myCircle) obj).row;
    							int column = ((myCircle) obj).column;
    							IO.placedTiger[IO.currentGame] = new Coordinates(row,column);
    							IO.playerPlacedMeeple[IO.currentGame]=true;
    							IO.tigerSlotsUpdated[IO.currentGame]=false;	
    							IO.didPlayerDecideToPlaceMeeple[IO.currentGame]=true;
    							for(int g=0;g<circlesForTigerSlots.size();g++)
    							{
    								root.getChildren().remove(circlesForTigerSlots.get(g));
    							}
    							circlesForTigerSlots.clear();
    							myCircle tmpCircle = new myCircle();
    							tmpCircle.row = row;
    				    		tmpCircle.column = column;
    				    		tmpCircle.setRadius(tileSize/6);
    				    		tmpCircle.setCenterY(row*tileSize/3+tileSize/6);
    				    		tmpCircle.setCenterX(column*tileSize/3+tileSize/6);
    				    		circlesForTigers.add(tmpCircle);
    				    		root.getChildren().add(tmpCircle);
    				    		if(game.currentTurn==0)
    				    		{
    					    		tmpCircle.setFill(
    			         	        	      new ImagePattern(
    			                 	    	        new Image("file:tigerRed.png"), 0, 0, 1, 1, true
    			                 	    	      ));
    				    		}
    				    		else
    				    		{
    					    		tmpCircle.setFill(
    			         	        	      new ImagePattern(
    			                 	    	        new Image("file:tigerBlue.png"), 0, 0, 1, 1, true
    			                 	    	      ));
    				    		}				    		
    						}	       	            	
          	            }

          	        });    		
        	}
        	for(int g=0;g<IO.tigerPlacementSlotsAI[IO.currentGame].size();g++)
        	{
        		if(IO.tigerPlacementSlotsAI[IO.currentGame].get(g).count!=-1)
        		{
        			myCircle tmpCircle = new myCircle();
    				tmpCircle.row = IO.tigerPlacementSlotsAI[IO.currentGame].get(g).row;
    	    		tmpCircle.column = IO.tigerPlacementSlotsAI[IO.currentGame].get(g).column;
    	    		tmpCircle.setRadius(tileSize/6);
    	    		tmpCircle.setCenterY(IO.tigerPlacementSlotsAI[IO.currentGame].get(g).row*tileSize/3+tileSize/6);
    	    		tmpCircle.setCenterX(IO.tigerPlacementSlotsAI[IO.currentGame].get(g).column*tileSize/3+tileSize/6);
    	    		circlesForTigers.add(tmpCircle);
    	    		root.getChildren().add(tmpCircle);
//    	    		System.out.println("IO.board[IO.currentGame].sectionArray[tmpCircle.row][tmpCircle.column].tiger"+IO.board[IO.currentGame].sectionArray[tmpCircle.row][tmpCircle.column].tiger);
    	    		if(IO.boardTmp[IO.currentGame].sectionArray[tmpCircle.row][tmpCircle.column].tiger==1)
    	    		{
    		    		tmpCircle.setFill(
             	        	      new ImagePattern(
                     	    	        new Image("file:tigerRed.png"), 0, 0, 1, 1, true
                     	    	      ));
    	    		}
    	    		else
    	    		{
    		    		tmpCircle.setFill(
             	        	      new ImagePattern(
                     	    	        new Image("file:tigerBlue.png"), 0, 0, 1, 1, true
                     	    	      ));
    	    		}				    	 	   		
        	   		IO.tigerPlacementSlotsAI[IO.currentGame].get(g).count=1;
        		}
     
        	}
    	}

    }
}

class Vector2d
{
	int row,column;
	Vector2d(int row,int column)
	{
		this.row=row;
		this.column=column; //row/column, upper left corner is 0 0
	}
	Vector2d()
	{
		
	}
}
