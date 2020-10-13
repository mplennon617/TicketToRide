import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class Panel extends JPanel implements MouseListener{
	
	private static final boolean DEBUG = false;
	
	private static final Color BORDER_COLOR = new Color(79,61,44);
	private static final Color POPUP_COLOR = new Color(178,122,69);
	private static final Font TEXT_FONT = new Font("SansSerif Bold", Font.BOLD, 28);
	private static final int WINDOW_X = 1920;
	private static final int WINDOW_Y = 1015;
	private static final int OFFSET = 10;
	
	private static final Color[] PLAYER_COLORS = {Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE};
	private static final Color[] CARD_COLORS = {Color.PINK, Color.RED, Color.ORANGE, Color.YELLOW, Color.BLUE, Color.GREEN, Color.WHITE, Color.BLACK, Color.DARK_GRAY};
	
	private GameState game;
	
	private String report;

	private ArrayList<Integer> selectArray;
	private int wildCount;
	private Color chosenColor;
	
	private boolean hideMenu;
	private boolean drawingTickets;
	private boolean showTickets;
	private boolean noteBox = false;
	private boolean endGame = false;
	
	public Panel() throws FileNotFoundException {
		game = new GameState();
		addMouseListener(this);
		
		report = "Welcome to Ticket to Ride!";
		
		selectArray = new ArrayList<Integer>();
		wildCount = 0;
		chosenColor = Color.PINK;
		
		// This essentially starts the startGame process.
		game.takeDestinationTicket(5);
	}
	
	// -----------------------------------------------------------------------------------------
	//										Paint Method
	// -----------------------------------------------------------------------------------------
	
	// The painting is in layers; the code at the bottom of this method is conditional and will appear on top of other painted UI.
	public void paint(Graphics g) {
		
		// This is when the game ends.
		if (game.checkEndGameCounter())
			endGame = true;
				
		g.setColor(Color.RED);
		
		// Draw Image
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File("img/TTR.png"));
		} catch (IOException e) {}
		g.drawImage(img, 0, 0, null);
		
		if (DEBUG)
		{
			g.setColor(Color.WHITE);
			g.drawString("DEBUG", 10, 15);
		}
		
		// Print route dots
		Scanner s = null;
		try {
			s = new Scanner(new File("RouteCoords"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int i = 0;
		
		while(s.hasNextLine() && i < 101) {
		
			int x = s.nextInt();
			int y = s.nextInt();
			
			Route r = game.getBoard().getRoutes().get(i);
			if (r.isOccupied())
				g.setColor(game.getPlayers()[r.getOwnerNum()].getColor());
			else if (game.hasEnoughTrainCards(i, wildCount, chosenColor) && game.getTCCDrawingCounter() == 2 && game.checkTwin(i))
			{
				g.setColor(Color.WHITE);
				g.fillOval(x - 12, y - 12, 24, 24);
				g.setColor(Color.BLACK);
			}
			else
				g.setColor(POPUP_COLOR);
			
			g.fillOval(x-10, y-10, 20, 20);
			i++;
		}
		
		// Helper methods.
		this.report(g);
		this.drawPublicInfo(g);
		
		this.drawNumTrainCards(g);
		this.drawFaceUpCards(g);
		this.drawCardSelectors(g);
		this.drawMenu(g);
	}
	
	// -----------------------------------------------------------------------------------------
	//										Mouse Listeners
	// -----------------------------------------------------------------------------------------
	
	public void mousePressed(MouseEvent e){}
	
	public void mouseClicked(MouseEvent e){
	
		
		
	}
	public void mouseReleased(MouseEvent e) {
		System.out.println(e.getX() + " " + e.getY());
		
		// Selects the Debug option.
		if (DEBUG)
		{
			if (e.getX() < 30 && e.getY() < 30)
				game.clearTrains();
		}
		
		// ----------------Players starting the game can only click on MouseListeners in here.----------------
		if (!game.startGameComplete())
		{
			// Selects the arrow
			if (e.getX() > 1460 && e.getY() > 215 && e.getX() < 1510 && e.getY() < 280)
				hideMenu = !hideMenu;
			
			if (!hideMenu)
			{
				// Selects a DestinationTicket
				for (int i = 0; i < 5; i++)
				{
					if (e.getX() > 420 && e.getY() > 245 + (48 * i) && e.getX() < 815 && e.getY() < 285 + (48 * i))
					{
						if (!selectArray.contains((Integer)i))
							selectArray.add((Integer)i);
						else
							selectArray.remove((Integer)i);
					}
				}
			
				// Selects OK
				if (e.getX() > 1420 && e.getY() > 422 && e.getX() < 1505 && e.getY() < 477)
				{
					if (selectArray.size() >= 3 && selectArray.size() <= 5)
					{
						game.selectDestinationTicket(selectArray);
						game.takeDestinationTicket(5);
						selectArray.clear();
					}
				}
			}
			
			// Special Dialog Box once everyone finishes drawing tickets. It's on selecting Gray routes.
			if (game.startGameComplete())
				noteBox = true;
		}
		// ----------------These MouseListeners are for the showTickets menu.----------------
		else if (showTickets == true)
		{
			// Selects the arrow
			if (e.getX() > 1460 && e.getY() > 215 && e.getX() < 1510 && e.getY() < 280)
				hideMenu = !hideMenu;
			
			// Selects OK
			if (e.getX() > 1420 && e.getY() > 622 && e.getX() < 1505 && e.getY() < 677)
				showTickets = false;
		}
		else if (noteBox == true)
		{
			// Selects OK
			if (e.getX() > 1420 && e.getY() > 422 && e.getX() < 1505 && e.getY() < 477)
				noteBox = false;
		}
		// ----------------These MouseListeners are for the drawingTickets menu (where player gets new tickets).----------------
		else if (drawingTickets == true)
		{
			// Selects the arrow
			if (e.getX() > 1460 && e.getY() > 215 && e.getX() < 1510 && e.getY() < 280)
				hideMenu = !hideMenu;
			
			if (!hideMenu)
			{
				// Selects a DestinationTicket
				for (int i = 0; i < 3; i++)
				{
					if (e.getX() > 420 && e.getY() > 245 + (48 * i) && e.getX() < 815 && e.getY() < 285 + (48 * i))
					{
						if (!selectArray.contains((Integer)i))
							selectArray.add((Integer)i);
						else
							selectArray.remove((Integer)i);
					}
				}
						
				// Selects OK
				if (e.getX() > 1420 && e.getY() > 422 && e.getX() < 1505 && e.getY() < 477)
				{
					if (selectArray.size() >= 1 && selectArray.size() <= 3)
					{
						game.selectDestinationTicket(selectArray);
						selectArray.clear();
						drawingTickets = false;
					}
				}
			}
		}
		// ----------------These MouseListeners are for the endGame results screen.----------------
		else if (endGame == true)
		{
			// Selects the arrow
			if (e.getX() > 1655 && e.getY() > 125 && e.getX() < 1690 && e.getY() < 185)
				hideMenu = !hideMenu;
		}
		// ----------------The MouseListener will listen here if no menus or special states are open.----------------
		else
		{
			// Displays Current DestinationTickets
			if (e.getX() > 380 && e.getY() > 850 && e.getX() < 490 && e.getY() < 1015)
			{
				showTickets = true;
			}
			
			// Increases WildCount
			if (game.getTCCDrawingCounter() == 2 && e.getX() > 1530 && e.getY() > 860 && e.getX() < 1580 && e.getY() < 905)
			{
				if (wildCount < game.getCurrPlayer().getHand().countTCColor(Color.DARK_GRAY))
					wildCount++;
				else
					wildCount = game.getCurrPlayer().getHand().countTCColor(Color.DARK_GRAY);
			}
			
			// Decreases WildCount
			if (game.getTCCDrawingCounter() == 2 && e.getX() > 1530 && e.getY() > 960 && e.getX() < 1580 && e.getY() < 1000)
			{
				if (wildCount > 0)
					wildCount--;
				else
					wildCount = 0;
			}
			
			// Changes ChosenColor
			if (game.getTCCDrawingCounter() == 2)
			{
				for (int i = 0; i < 8; i++)
				{
					if (e.getX() > 535 + (112 * i) && e.getY() > 810 && e.getX() < 585 + (112 * i) && e.getY() < 835)
						if (game.getCurrPlayer().getHand().countTCColor(CARD_COLORS[i]) > 0)
							chosenColor = CARD_COLORS[i];
				}
			}
			
			// Draws a TrainCarCard
			if (e.getX() > 1660 && e.getY() > 690 && e.getX() < 1870 && e.getY() < 830)
			{	
				if (game.getTCCDrawingCounter() == 1)
					this.setReport("Player "+(game.getCurrPlayerNum() + 1)+" collected TrainCarCards");
				
				game.drawTrainCard();
				wildCount = 0;
			}
			
			// Taking a faceUp card -- did the player click on a faceUp card?
			TrainCarCard[] faceUps = game.getBoard().getTrainCars();
			
			for (int i = 0; i < 5; i++)
			{
				if (e.getX() > 1662 && e.getY() > 10 + (135 * i) && e.getX() < 1865 && e.getY() < 140 + (135 * i))
				{
					if (game.getTCCDrawingCounter() == 2)
					{
						if (colorToString(faceUps[i].getColor()).equalsIgnoreCase("WILD"))
							this.setReport("Player "+(game.getCurrPlayerNum() + 1)+" collected a WILD card!");
						else if (colorToString(faceUps[i].getColor()).equalsIgnoreCase("Orange")) // Grammatically Correct :)
							this.setReport("Player "+(game.getCurrPlayerNum() + 1)+" took an Orange card");
						else
							this.setReport("Player "+(game.getCurrPlayerNum() + 1)+" took a "+colorToString(faceUps[i].getColor())+" card");
					}
					else if (game.getTCCDrawingCounter() == 1 && !colorToString(faceUps[i].getColor()).equalsIgnoreCase("WILD"))
						this.setReport("Player "+(game.getCurrPlayerNum() + 1)+" collected TrainCarCards");
					
					game.takeTrainCard(i);
					
					if (game.checkThreeWilds())
					{
						this.setReport("3 Wilds facing up, so face-up cards were reshuffled");
						game.shuffleTrainCards();
					}
					
					wildCount = 0;
				}
			}
			
			// Draws new DestinationTickets
			System.out.println(game.getBoard().getDestinationTicketDeck().size());
			
			if (game.getTCCDrawingCounter() == 2 && e.getX() > 1660 && e.getY() > 860 && e.getX() < 1870 && e.getY() < 1000)
			{	
				this.setReport("Player "+(game.getCurrPlayerNum() + 1)+" chose new DestinationTickets");
			
				game.takeDestinationTicket(3);
				wildCount = 0;
				
				if (game.getDestinationChoices()[0] != null)
					drawingTickets = true;
				else
					this.setReport("The DestinationTicket Deck is empty!");
			}
			
			// Place trains -- Did the player click on a route?
			Scanner s = null;
			try {
				s = new Scanner(new File("RouteCoords"));
			} catch (FileNotFoundException error) {
				// TODO Auto-generated catch block
				error.printStackTrace();
			}
			
			int i = 0;
			while(s.hasNextLine() && i < 101) {
			
				int x = s.nextInt();
				int y = s.nextInt();
				
				Route r = game.getBoard().getRoutes().get(i);

				if (game.getTCCDrawingCounter() == 2 && e.getX() > x - 10 && e.getY() > y - 10 && e.getX() < x + 10 && e.getY() < y + 10)
				{
					if (game.hasEnoughTrainCards(i, wildCount, chosenColor) && r.isOccupied() == false)
					{
						if (game.checkTwin(i))
						{
							this.setReport("Player "+(game.getCurrPlayerNum() + 1)+" completed "+r.getC1()+" to "+r.getC2());
							game.placeTrains(i, wildCount, chosenColor);
							wildCount = 0;
						}
						else
						{
							this.setReport("You already own "+r.getC1()+" to "+r.getC2()+"!");
						}
							
					}
					else if (r.isOccupied())
						this.setReport("This route is occupied by Player "+(r.getOwnerNum() + 1)+"!");
					else
						this.setReport("More TrainCarCards needed!");
				}
				i++;
			}
		}
		
		repaint();
	}
	
	public void mouseEntered(MouseEvent e){}
	
	public void mouseExited(MouseEvent e){}

	// -----------------------------------------------------------------------------------------
	//										Paint helpers
	// -----------------------------------------------------------------------------------------
	
	public void drawPublicInfo(Graphics g)
	{
		// Player info for all 4 players
		for (int i = 0; i < 4; i++)
		{
			g.setColor(Color.WHITE);
			if (i == game.getCurrPlayerNum())
				g.fillOval(75, 15 + (170 * i), 20, 20);
			
			if (i == game.getCurrPlayerNum())
				g.setColor(game.getPlayers()[i].getColor());
			else
				g.setColor(Color.BLACK);
			
			g.setFont(new Font("SansSerif Bold", Font.BOLD, 20));
			
			// Players' # of TrainCarCards, DestinationTickets, and Trains.
			g.setFont(new Font("SansSerif Bold", Font.BOLD, 16));
			g.drawString(""+game.getPlayers()[i].getHand().getLevelOrderTCC().size(), 230, 92 + (170 * i));
			if (game.getPlayers()[i].getHand().getDTBST().getRoot() != null)
				g.drawString(""+game.getPlayers()[i].getHand().getLevelOrderDT().size(), 230, 119 + (170 * i));
			else
				g.drawString("0", 230, 120 + (170 * i));
			g.drawString(""+game.getPlayers()[i].getNumTrains(), 230, 144 + (170 * i));
			
			if (game.getLeaderNum() == i)
				g.setColor(Color.WHITE);
			g.drawString("Player "+(i + 1)+" ["+game.getPlayers()[i].getScore()+"pts]", 100, 33 + (170 * i));
			
			g.setColor(Color.BLACK);
			g.setFont(new Font("SansSerif Bold", Font.BOLD, 24));
			
			g.drawString(game.getLeaderboard()[i], 60, 790 + (45 * i));
		}
	}
	
	public void drawNumTrainCards(Graphics g)
	{
		g.setColor(Color.BLACK);
		g.setFont(new Font("SansSerif Bold", Font.BOLD, 48));
		
		for (int i = 0; i < 9; i++)
		{
			if (game.getCurrPlayer().getHand().countTCColor(CARD_COLORS[i]) == 0)
				g.fillRect(515 + (112 * i), 850, 105, 165);
			else if (game.getCurrPlayer().getHand().countTCColor(CARD_COLORS[i]) >= 2)
				g.drawString(" x "+game.getCurrPlayer().getHand().countTCColor(CARD_COLORS[i]), 525 + (112 * i), 1000);
		}
		
		repaint();
	}
	
	public void drawCardSelectors(Graphics g)
	{
		// ChosenColor
		for (int i = 0; i < 8; i++)
		{
			if (game.getCurrPlayer().getHand().countTCColor(CARD_COLORS[i]) > 0)
			{
				if (chosenColor.hashCode() == CARD_COLORS[i].hashCode())
				{
					BufferedImage selected = null;
					try {
						selected = ImageIO.read(new File("img/CCArrowSelected.png"));
					} catch (IOException e) {}
					g.drawImage(selected, 533 + (112 * i), 795, null);
				}
				else
				{
					BufferedImage arrow = null;
					try {
		    			arrow = ImageIO.read(new File("img/ChosenColorArrow.png"));
					} catch (IOException e) {}
					g.drawImage(arrow, 533 + (112 * i), 795, null);
				}
			}
		}
		
		// WildCount
		if (game.getCurrPlayer().getHand().countTCColor(Color.DARK_GRAY) > 0)
		{
			BufferedImage up = null;
			try {
		    	up = ImageIO.read(new File("img/ArrowSelectorUp.png"));
			} catch (IOException e) {}
			g.drawImage(up, 1490, 825, null);
		
			BufferedImage down = null;
			try {
		    	down = ImageIO.read(new File("img/ArrowSelectorDown.png"));
			} catch (IOException e) {}
			g.drawImage(down, 1492, 910, null);
		
			g.setFont(new Font("SansSerif Bold", Font.BOLD, 48));
			g.setColor(Color.BLACK);
			g.drawString(""+wildCount, 1542, 950);
		}
	}
	
	public void drawFaceUpCards(Graphics g)
	{
		TrainCarCard[] faceUps = game.getBoard().getTrainCars();
		
		for (int i = 0; i < 5; i++)
		{
			BufferedImage img = null;
			try {
			    img = ImageIO.read(new File(colorToFile(faceUps[i].getColor())));
			} catch (IOException e) {}
			g.drawImage(img, 1662, 10 + (135 * i), null);
			
			// Translucent Black box around a rainbow that you can't pick up
			if (game.getTCCDrawingCounter() == 1 && faceUps[i].getColor().hashCode() == Color.DARK_GRAY.hashCode())
			{
				g.setColor(new Color(0, 0, 0, 127));
				g.fillRect(1667, 15 + (135 * i), 197, 125);
			}
		}
	}
	
	public void drawMenu(Graphics g)
	{	
		if (!game.startGameComplete())
		{
			
			// This menu is for selecting 5 destination tickets at the beginning of the game
			if (!hideMenu)
			{
				g.setColor(BORDER_COLOR);
				g.fillRect(400 - OFFSET, 200 - OFFSET, WINDOW_X - (800 - 2 * OFFSET), WINDOW_Y - (720 - 2 * OFFSET));
				g.setColor(POPUP_COLOR);
				g.fillRect(400, 200, WINDOW_X - 800, WINDOW_Y - 720);
				
				g.setColor(Color.BLACK);
				g.setFont(TEXT_FONT);
				
				g.drawString("Player "+(game.getCurrPlayerNum() + 1)+", Select 3 - 5 Tickets", 750, 232);
				
				for (int i = 0; i < 5; i++)
				{
					if (game.getDestinationChoices()[i] != null)
					{
						if (selectArray.contains(i))
							g.setColor(game.getCurrPlayer().getColor());
						else
							g.setColor(Color.BLACK);
						
						g.drawString(game.getDestinationChoices()[i].toString(), 420, 275 + (48 * i));
					}
				}
				
				g.setFont(new Font ("SansSerif Bold", Font.BOLD, 48));
				
				if (selectArray.size() >= 3 && selectArray.size() <= 5)
					g.setColor(Color.BLACK);
				else
					g.setColor(POPUP_COLOR);
				
				g.drawString("OK", 1425, 467);
				g.drawRect(1420, 422, 85, 55);
			}
			
			BufferedImage img = null;
			try {
			    img = ImageIO.read(new File("img/ArrowButton.png"));
			} catch (IOException e) {}
			g.drawImage(img, 1435, 200, null);
		}
		else if (noteBox)
		{
			g.setColor(BORDER_COLOR);
			g.fillRect(400 - OFFSET, 200 - OFFSET, WINDOW_X - (800 - 2 * OFFSET), WINDOW_Y - (720 - 2 * OFFSET));
			g.setColor(POPUP_COLOR);
			g.fillRect(400, 200, WINDOW_X - 800, WINDOW_Y - 720);
			
			g.setColor(Color.BLACK);

			g.setFont(new Font ("SansSerif Bold", Font.BOLD, 48));			
			g.drawString("OK", 1425, 467);
			g.drawRect(1420, 422, 85, 55);
			
			g.setFont(TEXT_FONT);
			g.drawString("Note", 900, 232);
			g.drawString("To complete gray routes, make sure you select", 440, 310);
			g.drawString("the arrow         that corresponds to the color", 440, 350);
			g.drawString("of the card you want to spend. If you don't,", 440, 390);
			g.drawString("you won't be able to complete\n gray routes.", 440, 430);
			BufferedImage arrow = null;
			try {
    			arrow = ImageIO.read(new File("img/ChosenColorArrow.png"));
			} catch (IOException e) {}
			g.drawImage(arrow, 572, 312, null);
		}
		else if (showTickets)
		{
			if (!hideMenu)
			{
				// Check image read outside the loop
				
				//BufferedImage img = null;
				//try {
				//    img = ImageIO.read(new File("Check.png"));
				//} catch (IOException e) {}
				
				g.setColor(BORDER_COLOR);
				g.fillRect(400 - OFFSET, 200 - OFFSET, WINDOW_X - (800 - 2 * OFFSET), WINDOW_Y - (520 - 2 * OFFSET));
				g.setColor(POPUP_COLOR);
				g.fillRect(400, 200, WINDOW_X - 800, WINDOW_Y - 520);
				
				g.setColor(Color.BLACK);
				g.setFont(TEXT_FONT);
				
				g.drawString("Your DestinationTickets", 850, 232);
				
				g.setColor(new Color(100, 0, 0));
				
				int i = 0;
				for (DestinationTicket dt : game.getCurrPlayer().getHand().getLevelOrderDT())
				{
					if (game.getBoard().getGraphs()[game.getCurrPlayerNum()].isConnected(dt.getStart(), dt.getTarget()))
					{
						g.setColor(new Color(0, 100, 0));
						// maybe a check?
					}
					else
						g.setColor(new Color(100, 0, 0));
					
					if (i < 13)
						g.drawString(dt.toString(), 460, 275 + (32 * i++));
					else
						g.drawString("Okay, is Player "+(game.getCurrPlayerNum() + 1)+" even trying to win? smh", 420, 275 + (32 * i));
					
				}
				
				g.setFont(new Font ("SansSerif Bold", Font.BOLD, 48));
				g.setColor(Color.BLACK);
				
				g.drawString("OK", 1425, 667);
				g.drawRect(1420, 622, 85, 55);
			}
			
			BufferedImage img = null;
			try {
			    img = ImageIO.read(new File("img/ArrowButton.png"));
			} catch (IOException e) {}
			g.drawImage(img, 1435, 200, null);
		}
		else if (drawingTickets)
		{
			// This menu is for selecting 5 destination tickets at the beginning of the game
			if (!hideMenu)
			{
				g.setColor(BORDER_COLOR);
				g.fillRect(400 - OFFSET, 200 - OFFSET, WINDOW_X - (800 - 2 * OFFSET), WINDOW_Y - (720 - 2 * OFFSET));
				g.setColor(POPUP_COLOR);
				g.fillRect(400, 200, WINDOW_X - 800, WINDOW_Y - 720);
				
				g.setColor(Color.BLACK);
				g.setFont(TEXT_FONT);
				
				g.drawString("Select 1 - 3 Tickets", 850, 232);
				
				for (int i = 0; i < 3; i++)
				{
					if (game.getDestinationChoices()[i] != null)
					{
						if (selectArray.contains(i))
							g.setColor(game.getCurrPlayer().getColor());
						else
							g.setColor(Color.BLACK);
						
						g.drawString(game.getDestinationChoices()[i].toString(), 420, 275 + (48 * i));
					}
				}
				
				g.setFont(new Font ("SansSerif Bold", Font.BOLD, 48));
							
				if (selectArray.size() >= 1 && selectArray.size() <= 3)
					g.setColor(Color.BLACK);
				else
					g.setColor(POPUP_COLOR);
							
				g.drawString("OK", 1425, 467);
				g.drawRect(1420, 422, 85, 55);
			}
						
			BufferedImage img = null;
			try {
			    img = ImageIO.read(new File("img/ArrowButton.png"));
			} catch (IOException e) {}
			g.drawImage(img, 1435, 200, null);
		}
		else if (endGame)
		{
			if (!hideMenu)
			{
				g.setColor(BORDER_COLOR);
				g.fillRect(200 - OFFSET, 100 - OFFSET, WINDOW_X - (400 - 2 * OFFSET), WINDOW_Y - (260 - 2 * OFFSET));
				g.setColor(POPUP_COLOR);
				g.fillRect(200, 100, WINDOW_X - 400, WINDOW_Y - 260);
				
				g.setColor(Color.BLACK);
				g.setFont(new Font("SansSerif Bold", Font.BOLD, 36));
				g.drawString("Game Over!", 850, 137);
				
				if (game.getLeaderNum() > -1)
				{
					g.setColor(game.getPlayers()[game.getLeaderNum()].getColor());
					g.drawString("Congrats, Player "+(game.getLeaderNum() + 1)+"!", 785, 202);
				}
				else
				{
					g.setColor(Color.BLACK);
					g.drawString("It's a tie! Well played!", 790, 202);
				}
				// Show everyone how everyone did on their DestinationTickets.
				Graph[] graphs = game.getBoard().getGraphs();
				Player[] players = game.getPlayers();
				
				for (int i = 0; i < 4; i++)
				{
					g.setFont(new Font("SansSerif Bold", Font.BOLD, 36));
					g.setColor(Color.BLACK);
					g.drawString("Player "+(i + 1), 320 + (375 * i), 280);
					
					g.setFont(new Font("SansSerif Bold", Font.BOLD, 18));
					
					int c = 0;
					
					Queue<DestinationTicket> tickets = players[i].getHand().getLevelOrderDT();
					
					while (!tickets.isEmpty())
					{
						DestinationTicket t = tickets.poll();
					
						if (graphs[i].isConnected(t.getStart(), t.getTarget()))
						{
							g.setColor(new Color(0, 100, 0));
							g.drawString("+"+t.getScore()+" : "+t.toString(), 245 + (375 * i), 370 + (40 * c++));
						}
						else
						{
							g.setColor(new Color(100, 0, 0));
							g.drawString("-"+t.getScore()+" : "+t.toString(), 245 + (375 * i), 370 + (40 * c++));
						}
					}
				}
				
				game.tallyDestinationTickets();
				
				ArrayList<Integer> rawScores = game.getRawScores();
				
				for (int i = 0; i < 4; i++)
				{
					g.setFont(new Font("SansSerif Bold", Font.BOLD, 24));
					g.setColor(Color.BLACK);
					g.drawString("Raw score: "+rawScores.get(i), 245 + (375 * i), 330);
					
					if (game.getLeaderNum() == i)
						g.setColor(Color.WHITE);
					g.setFont(new Font("SansSerif Bold", Font.ITALIC, 24));
					g.drawString("Final Score ", 320 + (375 * i), 745);
					g.setFont(new Font("SansSerif Bold", Font.BOLD, 24));
					g.drawString(""+players[i].getScore(), 370 + (375 * i), 795);
				}
			}
			
			BufferedImage img = null;
			try {
			    img = ImageIO.read(new File("img/ArrowButton.png"));
			} catch (IOException e) {}
			g.drawImage(img, 1625, 110, null);
		}
		
		repaint();
	}
	
	public void setReport(String report)
	{
		this.report = report;
	}
	
	public void report(Graphics g)
	{
		g.setColor(POPUP_COLOR);
		g.fillRect(450, 0, 1050, 25);
		
		g.setFont(TEXT_FONT);
		g.setColor(Color.BLACK);
		g.drawString(report, 725, 23);
	}
	
	// Helper method for converting a color to a Card PNG file name.
	private String colorToFile(Color col)
	{
		// No efficiency required! :)
		if (col.hashCode() == Color.PINK.hashCode())
			return "img/Pink.png";
		if (col.hashCode() == Color.RED.hashCode())
			return "img/Red.png";
		if (col.hashCode() == Color.ORANGE.hashCode())
			return "img/Orange.png";
		if (col.hashCode() == Color.YELLOW.hashCode())
			return "img/Yellow.png";
		if (col.hashCode() == Color.BLUE.hashCode())
			return "img/Blue.png";
		if (col.hashCode() == Color.GREEN.hashCode())
			return "img/Green.png";
		if (col.hashCode() == Color.WHITE.hashCode())
			return "img/White.png";
		if (col.hashCode() == Color.BLACK.hashCode())
			return "img/Black.png";
		if (col.hashCode() == Color.DARK_GRAY.hashCode())
			return "img/Rainbow.png";
		
		return "";
	}
	
	// Helper method for Converting a Color to a color String. For setReport().
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