@SuppressWarnings("unchecked")

public class BinaryNodeDT implements Comparable{
	
	private DestinationTicket data;
	private BinaryNodeDT left;
	private BinaryNodeDT right;	
	
	public BinaryNodeDT(DestinationTicket data){
		this.data = data;
		left = null;
		right = null;
	}
	
	public DestinationTicket data() {
		return data;
	}
	
	public void setData(DestinationTicket dt) {
		data = dt;
	}
	
	public BinaryNodeDT left() {
			return left;
	}
	
	public BinaryNodeDT right() {
			return right;
	}
	
	public void setLeft(BinaryNodeDT l) {
		this.left = l;
	}
	
	public void setRight(BinaryNodeDT r) {
		this.right = r;
	}
	
	public int compareTo(Object other) 
	{
		BinaryNodeDT temp = (BinaryNodeDT)other;
		if(data.compareTo(temp.data()) > 0)
			return 1;
		else if(data.compareTo(temp.data()) < 0)
			return -1;
		else
			return 0;
	}
}