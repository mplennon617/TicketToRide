import java.awt.Color;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Queue;

public class GameState {
	private Board board;
	private Player[] players;

	private static final Color[] PLAYER_COLORS = {Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE};
	private static final Color[] CARD_COLORS = {Color.DARK_GRAY, Color.WHITE, Color.BLACK, Color.YELLOW, Color.RED, Color.GREEN, Color.BLUE, Color.PINK, Color.ORANGE};
	
	private ArrayList<Integer> rawScores = new ArrayList<Integer>();
	
	private int currPlayer;
	private int tccDrawingCounter;
	private int endGameCounter;
	private boolean isTallied;
	private boolean globetrotterAwarded;
	private boolean longestTrainAwarded;
	private DestinationTicket[] destinationChoices;
	
	public GameState() throws FileNotFoundException
	{
		board = new Board();
		players = new Player[4];
		
		destinationChoices = new DestinationTicket[5];
		
		startGame();
	}
	
	// -----------------------------------------------------------------------------------------------
	//									Accessors and Modifiers
	// -----------------------------------------------------------------------------------------------
	
	public Board getBoard()
	{
		return board;
	}
	
	public Player[] getPlayers()
	{
		return players;
	}
	
	public Player getCurrPlayer()
	{
		return players[currPlayer];
	}
	
	public int getCurrPlayerNum()
	{
		return currPlayer;
	}
	
	public int getLeaderNum()
	{
		int currMax = Integer.MIN_VALUE;
		int leader = -1;
		
		for (int i = 0; i < 4; i++)
		{
			if (players[i].getScore() > currMax)
			{
				leader = i;
				currMax = players[i].getScore(); 
			}
			// No preference to ties
			else if (players[i].getScore() == currMax)
				leader = -1;
		}
		
		return leader;
	}
	
	public int getTCCDrawingCounter()
	{
		return tccDrawingCounter;
	}
	
	public int getEndGameCounter()
	{
		return endGameCounter;
	}
	
	public String[] getLeaderboard()
	{
		String[] leaderboard = new String[4];
		Integer[] scores = new Integer[4];
		ArrayList<Integer> pnum = new ArrayList<Integer>();	
		
		for (int i = 0; i < 4; i++) {
			scores[i] = players[i].getScore();
			pnum.add(i);
		}
		
		Arrays.sort(scores, Collections.reverseOrder());
		
		for (int i = 0; i < 4; i++) {
			for(int j=0; j<pnum.size(); j++) {
				if(players[pnum.get(j)].getScore() == scores[i]){
					//leaderboard[i] = (i+1) + ". Player "+colorToString(players[pnum.get(j)].getColor())+": "+players[pnum.get(j)].getScore()+" pts";
					leaderboard[i] = "Player "+(i + 1)+": "+players[pnum.get(j)].getScore()+" pts";
					pnum.remove(j);
					break;
				}
			}
		}
		
		return leaderboard;
	}
	
	public Integer[] getOrderedScores()
	{
		Integer[] scores = new Integer[4];
		
		for (int i = 0; i < 4; i++) {
			scores[i] = players[i].getScore();
		}
		
		Arrays.sort(scores, Collections.reverseOrder());
		
		return scores;
	}
	
	public ArrayList<Integer> getRawScores()
	{
		return rawScores;
	}
	
	// -----------------------------------------------------------------------------------------------
	//								Game Management, StartGame, EndGame
	// -----------------------------------------------------------------------------------------------
	
	public void startGame()
	{
		// Fill Player array with 4 players
		for (int i = 0; i < players.length; i++)
			players[i] = new Player(PLAYER_COLORS[i]);
				
		currPlayer = (int) (Math.random()*4.0);
		endGameCounter = 5;
		tccDrawingCounter = 2;
		
		// Give 4 TrainCarCards to each player.
		for (int i = 0; i < players.length; i++)
		{
			for (int j = 0; j < 4; j++)
			{
				TrainCarCard card = board.drawTrainCard();
				players[i].takeTrainCard(card);
			}
		}
	}
	
	// If all 4 players have DestinationTickets, the game can start.
	public boolean startGameComplete()
	{
		// If the game is over, then it already started!
		if (checkEndGameCounter())
			return true;
		
		for (Player p : players)
		{
			if (p.getHand().getDTBST().getNumNodes() <= 0)
				return false;
		}
		return true;
	}
	
	public void cycle()
	{
		if (!checkEndGameCounter())
		{
			if (currPlayer == 3)
				currPlayer = 0;
			else
				currPlayer++;
			
			if (checkLowOnTrains() && !checkEndGameCounter())
				endGameCounter--;
		}
	}
	
	public boolean checkEndGameCounter()
	{
		if (endGameCounter == 0)
			return true;
		return false;
	}
	
	public boolean checkLowOnTrains()
	{
		for (int i = 0; i < 4; i++)
			if (players[i].getNumTrains() <= 2 && players[i].getNumTrains() >= 0)
				return true;
		return false;
	}
	
	public void rewardLongestTrainWinner()
	{
		// This method will only "activate" once even if it's called on a loop (which it is).
		if (!longestTrainAwarded)
		{
			int winner = this.getLongestTrainWinner();
		
			if (winner >= 0)
				players[winner].addPoints(10);
			
			longestTrainAwarded = true;
		}
	}
		
	// Will return -1 in the event of a tie.
	public int getLongestTrainWinner()
	{
		boolean tie = false;
		
		int longest = board.getLength(0);
		int maxI = 0;
		for(int i = 1; i < players.length; i++)
		{
			int temp = board.getLength(i);
			if(temp > longest)
			{
				tie = false;
				maxI = i;
				longest = temp;
			}
			else if (temp == longest)
				tie = true;
		}
		
		if (tie == false)
			return maxI;
		else
			return -1;
	}
	
	public void rewardGlobetrotterWinner()
	{
		// This method will only "activate" once even if it's called on a loop (which it is).
		if (!globetrotterAwarded)
		{
			int winner = this.getGlobetrotterWinner();
			
			if (winner >= 0)
				players[winner].addPoints(15);
			
			globetrotterAwarded = true;
		}
	}
	
	// Will return -1 in the event of a tie.
	public int getGlobetrotterWinner()
	{
		boolean tie = false;
		
		int mostCompleted = 0;
		int maxI = 0;
		for(int i = 0; i < players.length; i++)
		{
			Queue<DestinationTicket> tickets = players[i].getHand().getLevelOrderDT();
			
			int c = 0;
			
			for (DestinationTicket dt : tickets)
			{
				if(board.getGraphs()[i].isConnected(dt.getStart(), dt.getTarget()))
					c++;
			}
				
			if(c > mostCompleted)
			{
				tie = false;
				maxI = i;
				mostCompleted = c;
			}
			else if (c == mostCompleted)
				tie = true;
		}
		
		if (tie == false)
			return maxI;
		else
			return -1;
	}
	
	public int getTicketsCompleted(int i)
	{
		Queue<DestinationTicket> tickets = players[i].getHand().getLevelOrderDT();
		
		int c = 0;
		
		for (DestinationTicket dt : tickets)
		{
			if(board.getGraphs()[i].isConnected(dt.getStart(), dt.getTarget()))
				c++;
		}
		
		return c;
	}
	
	// Called at the end of the game. Takes all DestinationTickets and adjusts player scores
	public void tallyDestinationTickets()
	{
		// This method will only "activate" once even if it's called on a loop (which it is).
		if (!isTallied)
		{
			Graph[] g = board.getGraphs();
			for(int x = 0; x < players.length; x++)
			{
				int toAdd = 0;
				Queue<DestinationTicket> temp = players[x].getHand().getLevelOrderDT();
				Graph t = g[x];
				for(DestinationTicket dt : temp)
				{
					if(t.isConnected(dt.getStart(),dt.getTarget()))
					{
						toAdd += dt.getScore();
					}
					else
						toAdd -= dt.getScore();
				}
				players[x].addPoints(toAdd);
			}
			
			isTallied = true;
		}
	}
	
	// Done once for reporting at the end of the game.
	public void fillRawScores()
	{
		for (Player p : players)
			rawScores.add(p.getScore());
	}
	
	// -----------------------------------------------------------------------------------------------
	//										Destination Ticket
	// -----------------------------------------------------------------------------------------------
	
	public DestinationTicket[] getDestinationChoices()
	{
		return destinationChoices;
	}
	
	// <<Player Action 1>>: cycle() called at the end of SelectDestinationTicket
	public void takeDestinationTicket(int n) 
	{
		DestinationTicket[] tickets = board.takeTickets(n);
		
		if (tickets != null) // The RARE NullPointer :>
		{
			for (int i = 0; i < tickets.length; i++)
			{
				if (i < destinationChoices.length)
					destinationChoices[i] = tickets[i];
			}
		}
	}
	
	// After the player makes selections, handle all the logistics of where the cards go.
	// Clear destinationChoices at the end of this.
	public void selectDestinationTicket(ArrayList<Integer> selections)
	{
		
		// Only perform actions if the player selected enough indeces. Threshold sets this.
		int threshold = 0;
		
		if (getDCSize() >= 5)
			threshold = 3;
		else if (getDCSize() >= 3)
			threshold = 1;
		
		if (selections.size() >= threshold)
		{
			ArrayList<DestinationTicket> refills = new ArrayList<DestinationTicket>();
			
			for (int i = 0; i < destinationChoices.length; i++)
			{
				if (selections.contains(i))
				{
					players[currPlayer].takeDestinationTicket(destinationChoices[i]);
				}
				else
					refills.add(destinationChoices[i]);
			}
			
			board.refillTickets(refills);
		}
		
		for (int i = 0; i < destinationChoices.length; i++)
			destinationChoices[i] = null;
		
		cycle();
	}
	
	// Helper method for selectDestinationTicket. Finds size of destinationChoices.
	private int getDCSize()
	{
		int c = 0;
		
		for (int i = 0; i < destinationChoices.length; i++)
		{
			if (destinationChoices[i] != null)
				c++;
		}
		
		return c;
	}
	
	// -----------------------------------------------------------------------------------------------
	//										Drawing TrainCarCard
	// -----------------------------------------------------------------------------------------------
	
	// <<Player Action 2>>
	public void takeTrainCard(int n) {
		
		if(tccDrawingCounter == 2) {
			TrainCarCard card = board.takeTrainCard(n);
			players[currPlayer].takeTrainCard(card);
			if(card.getColor().equals(Color.DARK_GRAY)) {
				tccDrawingCounter = 0;
			}
			else {
				tccDrawingCounter = 1;
			}
		}
		else if(tccDrawingCounter == 1){
			if(!board.getTrainCars()[n].getColor().equals(Color.DARK_GRAY)) {
				players[currPlayer].takeTrainCard(board.takeTrainCard(n));
				tccDrawingCounter = 0;
			}
		}
		else {
			
		}
		
		// If the faceUp trainCarCards have 3 wilds, get 5 new faceUp cards.
		// This is called within Panel now.
		
		//if (board.checkThreeWilds())
		//{
		//	board.shuffleTrainCards();
		//}
		checkCycle(); // only cycles if tccDrawingCounter is 0
	}
	
	public boolean checkThreeWilds()
	{
		if (board.checkThreeWilds())
			return true;
		return false;
	}
	
	public void shuffleTrainCards()
	{
		board.shuffleTrainCards();
	}
	
	// <<Player Action 3>>
	public void drawTrainCard()
	{
		if (tccDrawingCounter > 0)
		{
			players[currPlayer].takeTrainCard(board.drawTrainCard());
			tccDrawingCounter--;
		}
		checkCycle();
	}
	
	private void checkCycle()
	{
		if (tccDrawingCounter <= 0 || board.getTrainDeck().isEmpty())
		{
			cycle();
			tccDrawingCounter = 2;
		}
	}

	// -----------------------------------------------------------------------------------------------
	//											Placing Trains
	// -----------------------------------------------------------------------------------------------
	
	// <<Player Action 4>> -- This method only activates if the player can place the trains.
	public void placeTrains(int i, int wildCount, Color chosenColor)
	{
		if (hasEnoughTrains(i) && hasEnoughTrainCards(i, wildCount, chosenColor))
		{
			Route r = board.getRoutes().get(i);
			
			Route twin = null;
			if (i > 0)
				if(board.getRoutes().get(i-1).getC1().equals(r.getC1()) && board.getRoutes().get(i-1).getC2().equals(r.getC2()))
					twin = board.getRoutes().get(i-1);
			if (i < board.getRoutes().size() - 1)
				if(board.getRoutes().get(i+1).getC1().equals(r.getC1()) && board.getRoutes().get(i+1).getC2().equals(r.getC2()))
					twin = board.getRoutes().get(i+1);

			
			if (!r.isOccupied())
			{
				if(twin == null)
				{
					awardPlacePoints(i);
					spendTrainCards(i, wildCount, chosenColor);
					board.fillRoute(currPlayer, i);
					players[currPlayer].placeTrain(r.getLength());
					cycle();
				}
				else if(twin.getOwnerNum() != getCurrPlayerNum())
				{
					awardPlacePoints(i);
					spendTrainCards(i, wildCount, chosenColor);
					board.fillRoute(currPlayer, i);
					players[currPlayer].placeTrain(r.getLength());
					cycle();
				}
			}
		}
	}
	
	public boolean checkTwin(int i)
	{
		Route r = board.getRoutes().get(i);
		
		Route twin = null;
		if (i > 0)
			if(board.getRoutes().get(i-1).getC1().equals(r.getC1()) && board.getRoutes().get(i-1).getC2().equals(r.getC2()))
				twin = board.getRoutes().get(i-1);
		if (i < board.getRoutes().size() - 1)
			if(board.getRoutes().get(i+1).getC1().equals(r.getC1()) && board.getRoutes().get(i+1).getC2().equals(r.getC2()))
				twin = board.getRoutes().get(i+1);
		
		if (twin != null)
		{
			if (twin.getOwnerNum() == getCurrPlayerNum())
				return false;
		}
		return true;
	}
	
	public boolean hasEnoughTrains(int i) {
		int length = 46;
		
		Route r = board.getRoutes().get(i);
		length = r.getLength();
		
		if(players[currPlayer].getNumTrains() >= length)
			return true;
		
		return false;
	}
	
	public void awardPlacePoints(int i)
	{
		final int[] POINTS = {1, 2, 4, 7, 10, 15};
		players[currPlayer].addPoints(POINTS[board.getRoutes().get(i).getLength() - 1]);
	}
	
	public void spendTrainCards(int i, int wildCount, Color chosenColor)
	{
		
		Route r = board.getRoutes().get(i);
		
		if (r != null)
		{
			if (hasEnoughTrainCards(i, wildCount, chosenColor))
			{
				
				int length = r.getLength();
			
				for (int j = 0; j < length; j++)
				{
					if (j < wildCount)
					{
						board.addToUsed(players[currPlayer].spendTrainCard(new TrainCarCard(Color.DARK_GRAY)));
					}
					else if (r.getColor().hashCode() == Color.GRAY.hashCode())
					{
						board.addToUsed(players[currPlayer].spendTrainCard(new TrainCarCard(chosenColor)));
					}
					else
					{
						board.addToUsed(players[currPlayer].spendTrainCard(new TrainCarCard(r.getColor())));
					}
				}
			}
		}
	}
	
	// This method passes 2 cities instead of a Route in the event that this method is used independently.
	
	// chosenColor only matters for a gray route.
	public boolean hasEnoughTrainCards(int i, int wildCount, Color chosenColor)
	{
		Route r = board.getRoutes().get(i);
		
		int numCorrectColor = 0;
		
		// Does the player (given a preferred number of wilds to spend) have enough cards to spend?
		if (r.getColor().hashCode() == Color.GRAY.hashCode())
		{
			// If chosenColor is GRAY, use the best case scenario.
			if (chosenColor.hashCode() == Color.GRAY.hashCode())
			{
				for (Color c : CARD_COLORS)
					if (players[currPlayer].getHand().countTCColor(c) > numCorrectColor)
						numCorrectColor = players[currPlayer].getHand().countTCColor(c);
			}
			else
			{
				numCorrectColor = players[currPlayer].getHand().countTCColor(chosenColor);
			}
		}
		else
		{
			numCorrectColor = players[currPlayer].getHand().countTCColor(r.getColor());
		}
		
		int numWilds = players[currPlayer].getHand().countTCColor(Color.DARK_GRAY); // Wild
		
		if (wildCount <= numWilds)
		{
			if (numCorrectColor + wildCount >= r.getLength())
				return true;
			return false;
		}
		return false; // Oops, wildCount is too high!
	}
	
	// -----------------------------------------------------------------------------------------------
	//											Debug methods
	// -----------------------------------------------------------------------------------------------
	
	public void clearTrains()
	{
		for (int i = 0; i < 4; i++)
			players[i].placeTrain(45);
	}
	
	// <<Player Action 4>>
	public void placeTrainsDebug(int i)
	{
		Route r = board.getRoutes().get(i);
			
		if (!r.isOccupied())
		{
			board.fillRoute(currPlayer, i);
			players[currPlayer].placeTrain(r.getLength());
		}
			
		//cycle();
	}
	
	// Helper method for Converting a Color to a color String. For getLeaderboard().
	private static String colorToString(Color c)
	{
		if (c.hashCode() == Color.DARK_GRAY.hashCode())
			return "WILD";
		if (c.hashCode() == Color.WHITE.hashCode())
		return "White";
		if (c.hashCode() == Color.YELLOW.hashCode())
			return "Yellow";
		if (c.hashCode() == Color.RED.hashCode())
			return "Red";
		if (c.hashCode() == Color.GREEN.hashCode())
			return "Green";
		if (c.hashCode() == Color.ORANGE.hashCode())
			return "Orange";
		if (c.hashCode() == Color.BLUE.hashCode())
			return "Blue";
		if (c.hashCode() == Color.BLACK.hashCode())
			return "Black";
		if (c.hashCode() == Color.PINK.hashCode())
			return "Pink";
		
		return "gray";
	}
}