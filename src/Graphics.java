//import java.awt.Dimension;
//import java.awt.Toolkit;
import java.io.FileNotFoundException;
import javax.swing.JFrame;

public class Graphics extends JFrame{
	
	public Graphics(String frameName) throws FileNotFoundException {
		super(frameName);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		add(new Panel());
		setVisible(true);
	}
	
//	private static void makeScreen(JFrame aFrame) {
//	    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
//	    aFrame.setSize(screenSize.width, screenSize.height-20);
//	    aFrame.setLocation(0, 0);
//	}
	
	public static void main(String args[]) throws FileNotFoundException {
		Graphics window = new Graphics("Ticket to Ride");
//		makeScreen(window);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH);
//		window.setUndecorated(true);
		window.setVisible(true);
		window.repaint();
	}
}