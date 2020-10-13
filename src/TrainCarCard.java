import java.awt.Color;
public class TrainCarCard implements Comparable
{
	private Color color;
	
	public  TrainCarCard(Color c)
	{
		color = c;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	// ?????????
	public int compareTo(Object o)
	{
		TrainCarCard c = (TrainCarCard) o;
		if(color.hashCode() > c.getColor().hashCode())
			return 1;
		else if(color.hashCode() < c.getColor().hashCode())
			return -1;
		return 0;
	}
}