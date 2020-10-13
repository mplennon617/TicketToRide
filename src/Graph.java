import java.util.TreeSet;
import java.util.HashMap;

public class Graph {

	private int numVertices;
	private TreeSet<String> cityNames;
	private HashMap<String, HashMap<String, Integer>> connections;
	
	public Graph(TreeSet<String> cityNames)
	{
		this.cityNames = cityNames;
		
		connections = new HashMap<String, HashMap<String, Integer>>();
		
		for (String c : cityNames)
			connections.put(c, new HashMap<String, Integer>());
		
	}
	
	public void connect(String c1, String c2, int length)
	{
		HashMap<String, Integer> value;
		
		//If there's a nullPointerException in this method, lines 23/27 would be the reasons why.
		value = connections.get(c1);
		System.out.println("value: "+value);
		value.put(c2, length);	
		connections.put(c1, value);
			
		value = connections.get(c2);
		value.put(c1, length);	
		connections.put(c2, value);
	}
	
	public boolean isConnected(String cMain, String c2)
	{
		TreeSet<String> visited = new TreeSet<String>();
		
		boolean connected = this.hasC2(cMain, c2, visited);
			
		return connected;
	}
	
	// Depth-first traversal helper method. Returns true when it finds c2.
	private boolean hasC2(String cCurr, String c2, TreeSet<String> visited)
	{
		//System.out.println("--------in Graph : hasC2");
		//System.out.println("connections: "+connections);
		//System.out.println("visited: "+visited);
		//System.out.println("cCurr: "+cCurr);
		//System.out.println("c2: "+c2);
		
		//visited TreeSet makes sure that a city(Vertex) isn't visited twice.
		visited.add(cCurr);
		
		if (cCurr.equalsIgnoreCase(c2))
		{
			return true;
		}
		
		HashMap<String, Integer> links = connections.get(cCurr);
		
		if (links != null)
		{
			for (String s : links.keySet())
			{
				if (!visited.contains(s))
					return (false || this.hasC2(s, c2, visited));
			}
		}
		
		return false;
	}
	
	// Serves as a Global variable for calculating the length via depth.
	private int globalMaxLength = 0;
	
	// getLength for the longest train algorithm. This calls depth for every city in the graph.
	public int getLength()
	{
		TreeSet<String> visited = new TreeSet<String>();
		
		for (String s : connections.keySet())
			depth(s, visited);
		
		return globalMaxLength;
	}
	
	// Depth is one edge...
	private void depth(String cCurr, TreeSet<String> visited)
	{
		System.out.println("in depth");
		System.out.println("globalMaxLength: "+globalMaxLength);
		System.out.println("visited.size(): "+visited.size());
		visited.add(cCurr);
		
		int currLength = 0;
		
		HashMap<String, Integer> links = connections.get(cCurr);
		
		for (String s : links.keySet())
		{
			if (!visited.contains(s))
			{
				currLength = globalMaxLength + links.get(s);
				depth(s, visited);
			}	
			//this.depth(s, visited);
		}
		
		globalMaxLength = Math.max(globalMaxLength, currLength);
	}
	
	// Counts the number of cities that are linked to cMain, via DFS. This method may not be needed, but it may be useful.
	
	/*private int countConnectedCities(String cMain)
	{
		TreeSet<String> visited = new TreeSet<String>();
		
		return this.countConnectedCities(cMain, visited, 0);
		
	}
	
	// Helper method
	private int countConnectedCities(String cCurr, TreeSet<String> visited, int count)
	{
		//visited TreeSet makes sure that a city(Vertex) isn't visited twice.
		visited.add(cCurr);
		
		HashMap<String, Integer> links = connections.get(cCurr);
		count += links.keySet().size();
		
		for (String s : links.keySet())
			if (!visited.contains(s))
				this.countConnectedCities(s, visited, count);
		
		return count;
	}*/
}