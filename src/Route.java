import java.awt.Color;

public class Route 
{
	
	private int length;
	private int ownerNum;
	private boolean isOccupied;
	private Color color;
	private String[] cities;
	
	public Route(Color color, int length, String c1, String c2)
	{
		cities = new String[2];
		
		this.cities[0] = c1;
		this.cities[1] = c2;
		
		this.length = length;
		this.color = color;
		
		ownerNum = -1;
		isOccupied = false;
	}
	
	// Accessors / Modifiers
	
	public int getLength()
	{
		return length;
	}
	
	public boolean isOccupied()
	{
		return isOccupied;
	}
	
	public String[] getCities()
	{
		return cities;
	}
	
	public String getC1()
	{
		return cities[0];
	}
	
	public String getC2()
	{
		return cities[1];
	}
	
	public Color getColor()
	{
		return color;
	}

	public int getOwnerNum()
	{
		return ownerNum;
	}
	
	public void setOwnerNum(int ownerNum)
	{
		this.ownerNum = ownerNum;
		isOccupied = true;
	}
}