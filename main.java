import java.util.*;
import java.io.*;
import java.net.*;


// Starting point for the game 
public class main{

	// this will connect to the host that Dave specifies the day of the competition, as well as the port 
	 static String hostName; 
	 static int portNumber;
	 static String tournamentPassword;  
	 static String username;
	 static String password;


       static public int startingX =0;
       static public int startingY =0;
	
	public static void main(String args[]){


            hostName = args[0];
            portNumber = Integer.parseInt(args[1]);
            tournamentPassword = args[2];
            username = args[3];
            password = args[4];

		// In the main, the connection is established to the domain and port number specified
        try (
            Socket kkSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        ) {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser = "";
            
            
            // array that will store both of the games
            Game[] games = new Game[2];
            int numberOfRounds = -1;
            String opponent;
            boolean firstMoveMade = false;
            boolean secondMoveMade = false;
            String gidOne = "";
            String gidTwo = "";

            while ((fromServer = in.readLine()) != null) {
            	// Each of the responses will be constructed using a stringBuilder
            	StringBuilder response = new StringBuilder();

            	// Split by whitespace to better parse information
            	String[] serverInfo = fromServer.split("\\s+");

            	// Incoming messages from the server are handled here 
                System.out.println("Recieving Reponse: " + fromServer);

                
// DONE
                // Handle each of the Server Responses
	                if (fromServer.equals("THIS IS SPARTA!")){ 
	                	System.out.println("	=== Giving Tournament Password ===");
	                	// need to give the password
		                	response.append("JOIN ");
		                	response.append(tournamentPassword);
		                	fromUser = response.toString();
	                }
                  	else if(fromServer.equals("HELLO!")){
                  		System.out.println("	=== Giving authentication credentials ===");
                  		// need to give authentication response
	                  		response.append("I AM ");
	                  		response.append(username + " " + password);
	                  		fromUser = response.toString();
                  	}
                  	else if (serverInfo[0].equals("WELCOME")){
                  		System.out.println("	=== Sucesfull Authentication ===");
                  		// we have been sucesfully authenticated, YAY! 		
                  	}
                  	else if (serverInfo[0].equals("NEW") && serverInfo[1].equals("CHALLENGE")){
                  		if(serverInfo[7].equals("MATCH")){
                  			System.out.println("	=== NEW single match ===");
                  			// one match
                  			numberOfRounds = Integer.parseInt(serverInfo[6]);
                  		} 
                  		else{
                  			System.out.println("	=== Multiple new matches ===");
                  			// MATCHES, multiple matches
                  			numberOfRounds = Integer.parseInt(serverInfo[6]);
                  		}
                  	}
                  	else if(serverInfo[0].equals("BEGIN")){
                  		System.out.println("	=== Starting new round ===");
                  		// begin round, we want to initialize 2 games 
	                  		Game gameOne = new Game();
	                  		Game gameTwo = new Game();
	                  		games[0] = gameOne;
	                  		games[1] = gameTwo;
	                  	
	                  	// reset booleans, keep track of gid at the very start of the game
	             			firstMoveMade = false;
	             			secondMoveMade = false;
                  	}
                  	else if(serverInfo[0].equals("YOUR") && serverInfo[1].equals("OPPONENT")){
                  		System.out.println("	=== Opponent Player info ===");
                  		// opponent player information
                  		opponent = serverInfo[4];
                  	}
                  	else if(serverInfo[0].equals("STARTING")){
                  		System.out.println("	=== Starting tile information ===");
                  		// starting tile information
                  			String cardCode = serverInfo[3];
                  			int xLocation = Integer.parseInt(serverInfo[5]);
                  			int yLocation = Integer.parseInt(serverInfo[6]);
                  			int rotation = (Integer.parseInt(serverInfo[7]))/90;

                                    startingX = xLocation;  
                                    startingY = yLocation;

                  		// place the first tile on the board
                  		games[0].board.placeFirstCard(cardCode, 0, 0, rotation);
                  		games[1].board.placeFirstCard(cardCode, 0, 0, rotation);
                  		
                  	}
                  	else if(serverInfo[0].equals("THE") && serverInfo[1].equals("REMAINING")){
                  		System.out.println("	=== Creating deck ===");
                  		// remaining tile information
                  			int numberOfCards = Integer.parseInt(serverInfo[2]);
                  			ArrayList<Card> rawCardData = new ArrayList<Card>();
                  			for(int i=6; i<6+numberOfCards; i++){
                  				String cardData = serverInfo[i];
                  				Card card = new Card(cardData);
                  				rawCardData.add(card);
                  			}
                  		// Give copies of both of the decks to both of the games 
                  		games[0].deck.ImportCardData(rawCardData);
                  		games[1].deck.ImportCardData(rawCardData);
                  		
                  		// give copies of boards to each player
                  		games[0].player.giveBoardToPlayer(games[0].board);
                  		games[1].player.giveBoardToPlayer(games[1].board);
                  		
                  		// give copies of the decks to both the players
                  		games[0].player.giveDeckToPlayer(rawCardData);
                  		games[1].player.giveDeckToPlayer(rawCardData);
                  	}
             		else if(serverInfo[0].equals("MATCH") && serverInfo[1].equals("BEGINS")){
             			System.out.println("	=== Coutdown to start of match ===");
             			// match begins in specified time frame
             			
             		}
             		else if(serverInfo[0].equals("MAKE") && serverInfo[1].equals("YOUR")){
// MAKING A MOVE
             				System.out.println("	=== Make move ===");
             				// move with singular amount of time
             				String gameID = serverInfo[5];
             				if(!firstMoveMade && !secondMoveMade){
             					// called only at the start when we don't know the gid
             					firstMoveMade = true;
             					gidOne = gameID;
             				} else if (!secondMoveMade){
             					// called only when we have to make the second move
             					secondMoveMade = true;
             					gidTwo = gameID;
             				} else {
             					// not one of the first 2 moves
             				}
		             					PlayerMoveInformation info;
		             					Card cardToPlace = new Card(serverInfo[12]);
		             					
		             					if(gameID.equals(gidOne)){
		             						// make move for first game
		             						info = games[0].player.makeMove(cardToPlace);
		             					}
		             					else {
		             						// make move for second game
		             						info = games[1].player.makeMove(cardToPlace);
		             					}
		             					// 
		             					int subtract = (games[0].board.boardColumnNumber/2);

                                                      if(info.unplaceable){
                                                            // generate responses
                                                            response.append("GAME " + serverInfo[5] + " MOVE " +  serverInfo[10] + " TILE " +serverInfo[12] + " UNPLACEABLE ");
                                                            if(info.pass){
                                                                  // just pass, do nothing special
                                                                  response.append("PASS");
                                                            } else if(info.retrieve){
                                                                  // retrieve tiger
                                                                  response.append("RETRIEVE TIGER AT " + info.extraColumn + " " +info.extraRow);
                                                            } else if(info.another){
                                                                  // add another tiger
                                                                   response.append("ADD ANOTHER TIGER TO " + info.extraColumn + " " +info.extraRow);
                                                            }

                                                      }
                                                      else{
                                                            response.append("GAME " + serverInfo[5] + " MOVE " +  serverInfo[10] + " PLACE " +serverInfo[12] + " AT " + (info.column-subtract+startingX) + " "  +(info.row-subtract+startingY) + " " +info.orientation*90);
                                                            if(info.tigerPlaced){
                                                                  // tigerPlace is true
                                                                  response.append(" TIGER " + info.tigerLocation);
                                                            }
                                                            else if(info.crocodile){
                                                                  response.append(" CROCODILE");
                                                            } 
                                                            else{
                                                                  response.append(" NONE");
                                                            }
                                                      }		
		             					fromUser = response.toString();
             				
             		}
// RESPONDING TO MOVE INFORMATION
             		else if(serverInfo[0].equals("GAME") && serverInfo[2].equals("MOVE")){
             			if(serverInfo[6].equals("FORFEITED:")){
             				System.out.println("	=== Forfiet game ===");
             				// forfeit 
             			} 
             			else {
                                    if(serverInfo[6].equals("TILE")){
                                          // TILE response
                                          if(serverInfo[9].equals("PASSED")){
                                                // do nothing, passed
                                          } else if (serverInfo[9].equals("RETRIEVED")){
                                                // Player retrieved Tiger at some coordinate

                                                // must subtract tiger from some location
                                                int column =Integer.parseInt(serverInfo[12]);
                                                int row =Integer.parseInt(serverInfo[13]);
                                                String cardCode = serverInfo[7];
                                                // remove tiger from board
                                                      if(serverInfo[2].equals(gidOne)){
                                                            // update info for Game 1
                                                            games[0].board.removeEnemyTiger(row, column);
                                                      }
                                                      else {
                                                            // update info for Game 2
                                                            games[1].board.removeEnemyTiger(row, column);
                                                      }   

                                          } else if (serverInfo[9].equals("ADDED")){
                                                // Player adds tiger to the board at some location
                                                int column =Integer.parseInt(serverInfo[12]);
                                                int row =Integer.parseInt(serverInfo[13]);
                                                String cardCode = serverInfo[7];
                                                // add tiger to board 
                                                      if(serverInfo[2].equals(gidOne)){
                                                            // update for Game 1
                                                            games[0].board.addEnemyTiger(row, column);
                                                      }
                                                      else {
                                                            // update for Game 2
                                                            games[1].board.addEnemyTiger(row, column);
                                                      }   

                                          } else{
                                                // shouldn't happen
                                                System.out.println("error");
                                          }
                                    }
                                    else{
                                          // must be PLACED 
                                                      System.out.println("    === Valid move confirmed by server ===");
                                                      games[0].player.firstMoveMade = true;
                                                      games[1].player.firstMoveMade = true;
                                                      // valid move, do something
                                                      Card card = new Card(serverInfo[7]);
                                                      int xLocation = (Integer.parseInt(serverInfo[9])) -startingX;
                                                      int yLocation = (Integer.parseInt(serverInfo[10])) - startingY;
                                                      int rotation = Integer.parseInt(serverInfo[11]);
                                                      boolean tigerPlaced = false;
                                                      boolean crocodilePlaced = false;
                                                      int tigerLocation = -1;
                                                      if(serverInfo[12].equals("NONE")){
                                                            // splendid, do nothing
                                                      } else if(serverInfo[12].equals("CROCODILE")){
                                                            crocodilePlaced = true;
                                                      } else if(serverInfo[12].equals("TIGER")){
                                                            tigerPlaced = true;
                                                            tigerLocation = Integer.parseInt(serverInfo[13]);
                                                      } else{
                                                            // should not happen
                                                            System.out.println("error");
                                                      }

                                                      // row = ylocation
                                                      // column = xlocation
                                                      ServerMoveValidationResponse updateInfo = new ServerMoveValidationResponse(card, yLocation, xLocation, (rotation/90), tigerPlaced, tigerLocation, crocodilePlaced);
                                                      
                                                      if(serverInfo[2].equals(gidOne)){
                                                            // update info for Game 1
                                                            games[0].board.udpateBoardFromServerResponse(updateInfo);
                                                      }
                                                      else {
                                                            // update info for Game 2
                                                            games[1].board.udpateBoardFromServerResponse(updateInfo);
                                                      }     
                                    }
             			}
             		}
// DONE
             		else if(serverInfo[0].equals("GAME") && serverInfo[2].equals("OVER")){
             			System.out.println("	=== Game is over ===");
                              gidOne = "";
                              gidTwo = "";
             			// game over
             		}
             		else if(serverInfo[0].equals("END") && serverInfo[1].equals("OF")){
             			// end of round
             			if(serverInfo.length > 7){
             				System.out.println("	=== end of round, waiting for next match ===");
             				// longer response with "PLEASE WAIT FOR NEXT MATCH"
             			}
             			else {
             				System.out.println("	=== end of round ===");
             				// shorter reponse, just END OF THE ROUND
             			}
             		}
// DONE
             		else if(fromServer.equals("END OF CHALLENGES")){
             			System.out.println("	=== End of Challenges ===");
             			// end of challenges
             		}
             		else if(fromServer.equals("PLEASE WAIT FOR THE NEXT CHALLENGE TO BEGIN")){
             			System.out.println("	=== Waiting for new challenge ===");
             			// wait for next challenge
             		}
                  	else if (fromServer.equals("THANK YOU FOR PLAYING! GOODBYE")){
                  		System.out.println("	=== GG ===");
                  		// end of the game, terminate
                  		break;
                  	}
                  	else{
                  		// some unhandled case, this shouldn't happend
                  		System.out.println("	##ERROR## Unhandled response");
                  	}

      		 // Send ------------------------------------------------------------------------------------------------- //
                if (fromUser != null) {
                    System.out.println("Sending Response: " + fromUser);
                    out.println(fromUser);
                    // after sending information, we clear fromUser
                    fromUser = null;
                }
             // ------------------------------------------------------------------------------------------------------ //
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }


	}

}