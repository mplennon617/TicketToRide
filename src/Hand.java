import java.awt.Color;
import java.util.ArrayList;
import java.util.Queue;

public class Hand {
	private BinarySearchTreeDT dtBST;
	private BinarySearchTreeTCC tcBST;
	private int count = 0;
	private ArrayList<DestinationTicket> dt = new ArrayList<DestinationTicket>();

	public Hand() {
		dtBST = new BinarySearchTreeDT();
		tcBST = new BinarySearchTreeTCC();
	}
	
	public BinarySearchTreeDT getDTBST() {
		return dtBST;
	}
	
	public BinarySearchTreeTCC getTCBST() {
		return tcBST;
	}
	
	public void addDT(DestinationTicket dt) {
		dtBST.add(new BinaryNodeDT(dt));
	}
	
	public void addTC(TrainCarCard tc) {
		tcBST.add(new BinaryNodeTCC(tc));
	}
	
	public DestinationTicket removeDT(DestinationTicket dt) {
		return dtBST.remove(new BinaryNodeDT(dt)).data();
	}
	
	public TrainCarCard removeTCC(TrainCarCard tc) {
		return tcBST.remove(new BinaryNodeTCC(tc)).data();
	}
	
	public ArrayList<DestinationTicket> collectDT(){

		Queue<DestinationTicket> tickets = dtBST.getLevelOrder();
		ArrayList<DestinationTicket> ticketList = new ArrayList<DestinationTicket>();
		
		while (!tickets.isEmpty())
			ticketList.add(tickets.poll());
		
		dtBST.clear();
		return ticketList;
	}
	
	// Hopefully this isn't needed
	
	/*private ArrayList<DestinationTicket> collect(DestinationTicket tick) {
		int index = dtBST.getNumNodes();
		while(index > 0) {
			if (dtBST.node() == null)
				return dt;
			dt.add((DestinationTicket) dtBST.remove(new BinaryNodeDT(tick)).data());
		}
		return dt;
	}*/
	
	// If color is GRAY, this method will count ALL the trainCarCards in the hand.
	// NOTE: DARK_GRAY is the color allocated to a Rainbow card.
	public int countTCColor(Color color) {
		return tcBST.countTCCColor(color);
	}
	
	public Queue<TrainCarCard> getLevelOrderTCC()
	{
		return tcBST.getLevelOrder();
	}
	
	public Queue<DestinationTicket> getLevelOrderDT()
	{
		return dtBST.getLevelOrder();
	}
}