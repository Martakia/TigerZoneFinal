package mainPackage;
public class ScoreUpdateInformation {
	/*
	 * This class will be used to simulate the response from the server regarding the score updates, each response from the server will contain
	 * 
	 * 		-score that each player earned
	 * 		-number of meeples each player gets back
	 */
	
	
	// Server will return this information to players if there is a score update
	public int playerOneScoreUpdate;
	public int playerTwoScoreUpdate;
	public int playerOneMeeplesReturned;
	public int playerTwoMeeplesReturned;
	
	ScoreUpdateInformation(int playerOneScoreUpdate, int playerTwoScoreUpdate, int playerOneMeeplesReturned, int playerTwoMeeplesReturned){
		this.playerOneScoreUpdate = playerOneScoreUpdate;
		this.playerTwoScoreUpdate = playerTwoScoreUpdate;
		this.playerOneMeeplesReturned = playerOneMeeplesReturned;
		this.playerTwoMeeplesReturned = playerTwoMeeplesReturned;	
	}	
	
}
