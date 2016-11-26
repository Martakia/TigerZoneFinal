import java.util.*;
import java.io.*;
import java.net.*;


// Starting point for the game 
public class main{

	// this will connect to the host that Dave specifies the day of the competition, as well as the port 
	final static String hostName = "192.168.1.149";
	final static int portNumber = 4444;
	final static String tournamentPassword = "PersiaRocks";
	final static String username = "Red";
	final static String password = "Obiwan77";
	
	public static void main(String args[]){

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
                  			int rotation = Integer.parseInt(serverInfo[7]);

                  		// place the first tile on the board
                  		games[0].board.placeFirstCard(cardCode, xLocation, yLocation, rotation);
                  		games[1].board.placeFirstCard(cardCode, xLocation, yLocation, rotation);
                  		
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
                  	}
             		else if(serverInfo[0].equals("MATCH") && serverInfo[1].equals("BEGINS")){
             			System.out.println("	=== Coutdown to start of match ===");
             			// match begins in specified time frame
             			
             		}
             		else if(serverInfo[0].equals("MAKE") && serverInfo[1].equals("YOUR")){
             			if(serverInfo[8].equals("SECOND")){
             				System.out.println("	=== Make move with singular amount of time ===");
             				// move with singular amount of time
             				String gameID = serverInfo[6];
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
		             					
		             					response.append("GAME " +gameID+ " MOVE " + serverInfo[7] + " PLACE " +serverInfo[12] + " AT " + info.column + " "  +info.row + " " +info.orientation);
		             					if(info.tigerPlaced){
		             						// tigerPlace is true
		             						response.append(" TIGER " + info.tigerLocation);
		             					}		
		             					fromUser = response.toString();
             				

             			}
             			else{
             				System.out.println("	=== Make move with more time ===");
             				// SECONDS, multiple amounts of second
             				String gameID = serverInfo[6];
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
		             					
		             					response.append("GAME " +gameID+ " MOVE " + serverInfo[7] + " PLACE " +serverInfo[12] + " AT " + info.column + " "  +info.row + " " +info.orientation);
		             					if(info.tigerPlaced){
		             						// tigerPlace is true
		             						response.append(" TIGER " + info.tigerLocation);
		             					}		
		             					fromUser = response.toString();

             			}	
             		}
             		else if(serverInfo[0].equals("GAME") && serverInfo[2].equals("MOVE")){
             			if(serverInfo.length > 7){
             				System.out.println("	=== Forfiet game ===");
             				// forfeit 
             			} 
             			else {
             				System.out.println("	=== Valid move confirmed by server ===");
             				// valid move, do something
             				Card card = new Card(serverInfo[7]);
             				int xLocation = Integer.parseInt(serverInfo[9]);
             				int yLocation = Integer.parseInt(serverInfo[10]);
             				int rotation = Integer.parseInt(serverInfo[11]);
             				boolean tigerPlaced = false;
             				int tigerLocation = -1;
             				if(serverInfo.length > 12){
             					tigerPlaced = true;
             					tigerLocation = Integer.parseInt(serverInfo[13]);
             				}
             				ServerMoveValidationResponse updateInfo = new ServerMoveValidationResponse(card, xLocation, yLocation, rotation, tigerPlaced, tigerLocation);
             				
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
// DONE
             		else if(serverInfo[0].equals("GAME") && serverInfo[2].equals("OVER")){
             			System.out.println("	=== Game is over ===");
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