import java.awt.Color;

public class Player implements Comparable {
	private Color color;
	private int score, numTrains;
	private Hand hand;
	
	public Player(Color c) {
		color = c;
		score = 0;
		numTrains = 45;
		hand = new Hand();
	}
	
	public void takeTrainCard(TrainCarCard card) {
		hand.addTC(card);
	}
	
	public void takeDestinationTicket(DestinationTicket ticket) {
		hand.addDT(ticket);
	}
	
	public TrainCarCard spendTrainCard(TrainCarCard tc) {
		return hand.removeTCC(tc);
	}
	
	public void placeTrain(int Tlength) {
		numTrains -= Tlength;
		
		if (numTrains < 0)
			numTrains = 0;
	}
	
	public void addPoints(int n){
		score += n;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getNumTrains() {
		return numTrains;
	}
	
	public int getScore() {
		return score;
	}
	
	public Hand getHand()
	{
		return hand;
	}
	
	public int compareTo(Object other)
	{
		Player p = (Player)other;
		if (this.getScore() > p.getScore())
			return 1;
		else if (this.getScore() < p.getScore())
			return -1;
		return 0;
	}
}