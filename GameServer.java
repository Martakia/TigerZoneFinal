// internal server using localhost in order to play our AI against itself during testing
import java.net.*;
import java.io.*;
import java.util.*;

 
public class GameServer {
	private static final String[] TILES = {"JJJJ-", "JJJJT", "JJTJT","LLLL-","TLJT-","TLTT-","JJJJX","JJJJX","JJJJX","JJJJX"
,"JLLL-","JLLL-","JLLL-","JLLL-","TLJTP","TLJTP","TLJTP","TLJTP","JJTJX","JJTJX","LLJJ-","LLJJ-","LLJJ-","LLJJ-","LLJJ-",
"JLTT-","TLLT-","TLLT-","TLLT-","TTTT-","JLJL-","JLJL-","JLJL-","JLTTB","JLTTB","TLLTB","TLLTB","TJTJ-","TJTJ-","TJTJ-","TJTJ-",
"TJTJ-","TJTJ-","TJTJ-","TJTJ-","LJLJ-","LJLJ-","LJLJ-","TLTJ-","TLTJ-","TLTJ-","LJTJ-","TJJT-",
"TJJT-","TJJT-","TJJT-","TJJT-","TJJT-","TJJT-","TJJT-","TJJT-","TJJT-","LJJJ-","LJJJ-","LJJJ-","LJJJ-","LJJJ-",
"TLTJD","TLTJD","LJTJD","LJTJD","LJTJD","TJTT-","TJTT-","TJTT-","TJTT-","JLLJ-","JLLJ-","TLLL-","TLLLC","TLLLC"
};
		
		
	private static String getRandomTile() {
		Random randIndex = new Random();
		
		int index = randIndex.nextInt(TILES.length);
		
		return TILES[index];
	}
	
	
    public static void main(String[] args) throws IOException {
         
        if (args.length != 1) {
            System.err.println("Usage: java KnockKnockServer <port number>");
            System.exit(1);
        }
 
        int portNumber = Integer.parseInt(args[0]);
 
        try ( 
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out =
                new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream()));
        ) {
         
            String inputLine, outputLine;
             
            // Initiate conversation with client
            ServerAuthProtocol kkp = new ServerAuthProtocol();
            outputLine = kkp.processInput(null);
            out.println(outputLine);
 
            while ((inputLine = in.readLine()) != null) {
                outputLine = kkp.processInput(inputLine);
                out.println(outputLine);
                if (kkp.isDone()) 
					break;
            }
			
			
			Random rand = new Random();
			int rounds = rand.nextInt(5) + 1;
			
			int cid = 101;
						
			out.println("NEW CHALLENGE " + cid + " YOU WILL PLAY " + rounds + " MATCH" + (rounds == 1 ? "":"ES"));
			
			for (int i = 0; i < rounds; i++) {
				int rid = i + 1;
				
				
				int numberOfTiles = rand.nextInt(10) + 1;
				
				// TODO: randomly genererate some tiles
				String [] tiles = new String[numberOfTiles];
				for (int tile = 0; tile < numberOfTiles; tile++)
					tiles[tile] = getRandomTile();
			
								
				out.println("BEGIN ROUND " + rid + " OF " + rounds);
							
				// METCH
				out.println("YOUR OPPONENT IS PLAYER Killer");
				
				String firstTile = getRandomTile();
				
				out.println("STARTING TILE IS " + firstTile + " AT 0 0 0");
				out.println("THE REMAINING <number_tiles> TILES ARE [ <tiles> ]");
				out.println("MATCH BEGINS IN 5 SECONDS");
				
				for (int move = 0; move < numberOfTiles; move++) {
					
					Random time_for_move = new Random();
					int second = time_for_move.nextInt(10) + 1;
					String timeToMove = second + " SECOND" + (second == 1 ? "":"S");
				
					out.println("MAKE YOUR MOVE IN GAME <gid> WITHIN " + timeToMove + ": MOVE " + (move + 1) + " PLACE " + tiles[move]);
					
					
					// TODO: send fake GAME commands
				}
				
				
				out.println("GAME <gid> OVER PLAYER <pid> <score> PLAYER <pid> <score>");
				out.println("GAME <gid> OVER PLAYER <pid> <score> PLAYER <pid> <score>");		
					
							
				out.println("END OF ROUND " + rid + " OF " + rounds);
					
				 if (rid != rounds) { 
				 	out.println("PLEASE WAIT FOR THE NEXT CHALLENGE TO BEGIN");
				}				
			 }
			 out.println("END OF CHALLENGES");
			 out.println("THANK YOU FOR PLAYING! GOODBYE");
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
