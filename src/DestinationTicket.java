public class DestinationTicket implements Comparable
{
	private String[] cities;
	private int score;
	
	public DestinationTicket(String c1, String c2, int sc)
	{
		cities = new String[2];
		cities[0] = c1;
		cities[1] = c2;
		score = sc;
	}
	
	public String getStart()
	{
		return cities[0];
	}
	
	public String getTarget()
	{
		return cities[1];
	}
	
	public int getScore()
	{
		return score;
	}
	
	public int compareTo(Object o)
	{
		DestinationTicket dt = (DestinationTicket)o;
		
		if(getScore() > dt.getScore())
		{
			return 1;
		}
		else if(getScore() < dt.getScore())
			return -1;
		else
			return 0;
	}
	
	public String toString()
	{
		return "("+this.getScore()+") "+this.getStart()+" == "+this.getTarget();
	}
}