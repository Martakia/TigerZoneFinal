import java.net.*;
import java.io.*;
import java.util.*;

public class ServerAuthProtocol {
		private static final int AUTH_WAITING  = 0;
		private static final int AUTH_JOIN	   = 1;
		private static final int AUTH_I_AM     = 2;
		private static final int CHALL_START   = 10;

    			
		private int state = AUTH_WAITING;
		
		
		int pid = 4;
		int cid = 12;

	
		public boolean isDone() {
			return state == CHALL_START;
		}
			
	    public String processInput(String theInput) {
	        String theOutput = null;
			String[] inputTokens = null;
			
			if (theInput != null)
				inputTokens = theInput.split("\\s+");
					
	        if (state == AUTH_WAITING) {
	            theOutput = "THIS IS SPARTA!";
	            state = AUTH_JOIN;
	        } else if (state == AUTH_JOIN) {
				if (inputTokens[0].equalsIgnoreCase("JOIN")) {
	                theOutput = "HELLO!";
	                state = AUTH_I_AM;
	            } else {
	                state = AUTH_WAITING;
	            }
	        } else if (state == AUTH_I_AM) {
				if (inputTokens[0].equalsIgnoreCase("I") && inputTokens[1].equalsIgnoreCase("AM") ) {
					theOutput = "WELCOME " + pid + " PLEASE WAIT FOR THE NEXT CHALLENGE ";
					state = CHALL_START;
					
	            } else {
	                state = AUTH_WAITING;
	            }
	        } 
			
	        return theOutput;
	   }
	
	
    	
}