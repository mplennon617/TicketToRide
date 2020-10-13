import java.util.TreeSet;
import java.util.Queue;
import java.util.Scanner;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;


public class Board 
{
	private Queue<TrainCarCard> trainDeck;
	private ArrayList<Route> routes;
	private Graph[] graphs;
	private TrainCarCard[] trainCars;
	private ArrayList<TrainCarCard> usedTrains;
	private Queue<DestinationTicket> destinationTicketDeck;
	
	private TreeSet<String> cityNames; // For the Graph constructor.
	
	private static final Color[] COLORS = {Color.DARK_GRAY, Color.WHITE, Color.BLACK, Color.YELLOW, Color.RED, Color.GREEN, Color.BLUE, Color.PINK, Color.ORANGE};
	
	private static Scanner routeScan;
	private static Scanner ticketScan;
	
	public Board() throws FileNotFoundException
	{
		trainDeck = new LinkedList<TrainCarCard>();
		routes = new ArrayList<Route>();
		graphs = new Graph[4];
		trainCars = new TrainCarCard[5];
		usedTrains = new ArrayList<TrainCarCard>();
		destinationTicketDeck = new LinkedList<DestinationTicket>();
		
		// Setup cityNames.
		cityNames = new TreeSet<String>();
		
		routeScan = new Scanner(new File("routes.txt"));
		
		while (routeScan.hasNextLine())
		{
			String r = routeScan.nextLine();
			String[] rSplit = r.split(" ");
			
			cityNames.add(rSplit[2]);
			cityNames.add(rSplit[3]);
		}
		
		// Setup the board.
		this.setupBoard();
	}
	
	public Graph[] getGraphs()
	{
		return graphs;
	}
	
	public ArrayList<Route> getRoutes()
	{
		return routes;
	}
	
	public TrainCarCard[] getTrainCars()
	{
		return trainCars;
	}
	
	public Queue<TrainCarCard> getTrainDeck()
	{
		return trainDeck;
	}
	
	public ArrayList<TrainCarCard> getUsedTrains()
	{
		return usedTrains;
	}
	
	public Queue<DestinationTicket> getDestinationTicketDeck()
	{
		return destinationTicketDeck;
	}
	
	// Private because the Board constructor instantiates this method
	private void setupBoard() throws FileNotFoundException
	{
		routeScan = new Scanner(new File("routes.txt"));
		
		while (routeScan.hasNextLine())
		{
			String r = routeScan.nextLine();
			String[] rSplit = r.split(" ");
			
			Route route = new Route(this.stringToColor(rSplit[0]), Integer.parseInt(rSplit[1]), rSplit[2], rSplit[3]);
			routes.add(route);
		}
		
		// Setup the 4 player graphs.
		for (int i = 0; i < 4; i++)
		{
			graphs[i] = new Graph(cityNames);
		}
		
		// Setup the Deck of TrainCarCards. Adds to usedTrains first so that way it can be shuffled
		for (int i = 0; i < 9; i++)
		{
			for (int j = 0; j < 12; j++)
			{
				TrainCarCard newCard = new TrainCarCard(COLORS[i]);
				usedTrains.add(newCard);
			}
		}
		
		usedTrains.add(new TrainCarCard(Color.DARK_GRAY));
		usedTrains.add(new TrainCarCard(Color.DARK_GRAY));
		shuffleDeck();
		
		// Fills the 5 faceUp cards.
		// If there's 3 wilds for some reason, try again.
		for (int i = 0; i < trainCars.length; i++)
		{
			trainCars[i] = trainDeck.poll();
		}
		
		while (checkThreeWilds())
			shuffleTrainCards();
		
		// Setup the Deck of DestinationTickets.
		ArrayList<DestinationTicket> unshuffled = new ArrayList<DestinationTicket>();
		
		ticketScan = new Scanner(new File("tickets.txt"));
		
		while (ticketScan.hasNextLine())
		{
			String t = ticketScan.nextLine();
			String[] tSplit = t.split(" ");
			
			DestinationTicket ticket = new DestinationTicket(tSplit[1], tSplit[2], Integer.parseInt(tSplit[0]));
			unshuffled.add(ticket);
		}
		
		Collections.shuffle(unshuffled);
		destinationTicketDeck.addAll(unshuffled);
	}
	
	// Give the route an owner, then add it to the owner's Graph.
	// i is the index in routes.
	public Route fillRoute(int playNum, int i)
	{
		if (i < routes.size())
		{
			Route r = routes.get(i);
		
			r.setOwnerNum(playNum);
			graphs[playNum].connect(r.getC1(), r.getC2(), r.getLength());
		
			return r;
		}
		return null;
	}
	
	// This is an overloaded method that fills the route based on city names.
	// Since this doesn't deal with Double-Routes, using this method is unadvised.
	// However, this may be more useful in fringe situations.
	public Route fillRoute(int playNum, String c1, String c2)
	{
		for (Route r : routes)
		{
			String[] rCities = r.getCities();
			
			if ((rCities[0].equalsIgnoreCase(c1) && rCities[1].equalsIgnoreCase(c2)) || (rCities[0].equalsIgnoreCase(c2) && rCities[1].equalsIgnoreCase(c1)))
			{
				if (!r.isOccupied())
				{
					r.setOwnerNum(playNum);
					graphs[playNum].connect(c1, c2, r.getLength());
				}
				return r;
			}
		}
		
		return null;
	}
	
	public int getLength(int playNum)
	{
		return graphs[playNum].getLength();
	}
	
	// -----------------------------------------------------------------------------------------------
	//											TrainCarCard
	// -----------------------------------------------------------------------------------------------
	
	public TrainCarCard takeTrainCard(int i)
	{
		if (trainDeck.isEmpty())
			shuffleDeck();
		
		TrainCarCard toRemove = trainCars[i];
		TrainCarCard toReplace = this.drawTrainCard();
		
		trainCars[i] = toReplace;
		return toRemove;
	}
	
	public TrainCarCard drawTrainCard()
	{
		
		TrainCarCard toRemove = trainDeck.poll();
		
		if (trainDeck.isEmpty())
			shuffleDeck();
		
		return toRemove;
	}
	
	// Are there 3 wilds in the 5 faceUp TrainCarCards?
	
	// If there are 3 wilds, change the 5 faceUp TrainCarCards. Also, shuffle the deck.
	public void shuffleTrainCards()
	{
		if (checkThreeWilds())
		{
			// Place cards into deck
			for (int i = 0; i < 5; i++)
			{
				TrainCarCard toRemove = trainCars[i];
				trainCars[i] = null;
				
				usedTrains.add(toRemove);
			}
			
			this.shuffleDeck();
			
			// Draw 5 new ones
			for (int i = 0; i < 5; i++)
			{
				trainCars[i] = trainDeck.poll();
			}
		}
	}
	
	public boolean checkThreeWilds()
	{
		int c = 0;
		for (TrainCarCard tc : trainCars)
		{
			if (tc != null) // Very unlikely tc == null
				if (tc.getColor().hashCode() == Color.DARK_GRAY.hashCode())
					c++;
		}
		
		if (c >= 3)
			return true;
		return false;
	}
	
	public void addToUsed(TrainCarCard tc)
	{
		usedTrains.add(tc);
		
		if (trainDeck.isEmpty())
			shuffleDeck();
	}
	
	// -----------------------------------------------------------------------------------------------
	//										Destination Ticket
	// -----------------------------------------------------------------------------------------------
	
	public DestinationTicket[] takeTickets(int n)
	{
		DestinationTicket[] taken = new DestinationTicket[n];
		
		for (int i = 0; i < n; i++)
		{
			if (!destinationTicketDeck.isEmpty())
					taken[i] = destinationTicketDeck.poll();
		}
		
		// If destinationTicket is empty, change the size of the Array.
		// Lengthy code; we may change it later.
		if (destinationTicketDeck.isEmpty())
		{
			int c = 0;
			for (int i = 0; i < n; i++)
			{
				if (taken[i] == null)
					break;
				c++;
			}
			
			if (c > 0)
			{
				DestinationTicket[] newTaken = new DestinationTicket[c];
			
				for (int i = 0; i < c; i++)
					newTaken[i] = taken[i];
			
				return newTaken;
			}
			else
				return null;
		}
		
		return taken;
	}
	
	public void refillTickets(ArrayList<DestinationTicket> tickets)
	{
		for (DestinationTicket dt : tickets)
			destinationTicketDeck.add(dt);
	}
	
	// Helper method for Converting a color String to a Color. For Routes.
	private Color stringToColor(String s)
	{
		if (s.equalsIgnoreCase("gray"))
			return Color.GRAY;
		if (s.equalsIgnoreCase("white"))
			return Color.WHITE;
		if (s.equalsIgnoreCase("yellow"))
			return Color.YELLOW;
		if (s.equalsIgnoreCase("red"))
			return Color.RED;
		if (s.equalsIgnoreCase("green"))
			return Color.GREEN;
		if (s.equalsIgnoreCase("orange"))
			return Color.ORANGE;
		if (s.equalsIgnoreCase("blue"))
			return Color.BLUE;
		if (s.equalsIgnoreCase("black"))
			return Color.BLACK;
		if (s.equalsIgnoreCase("pink"))
			return Color.PINK;
		
		return Color.GRAY;
	}

	// Shuffles all cards in usedTrains, then places the shuffled deck back into the TrainDeck Queue.
	private void shuffleDeck()
	{
		Collections.shuffle(usedTrains);
		trainDeck.addAll(usedTrains);
		usedTrains.clear();
	}
}