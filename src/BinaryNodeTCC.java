@SuppressWarnings("unchecked")

public class BinaryNodeTCC implements Comparable{
	
	private TrainCarCard data;
	private BinaryNodeTCC left;
	private BinaryNodeTCC right;	
	
	public BinaryNodeTCC(TrainCarCard data){
		this.data = data;
		left = null;
		right = null;
	}
	
	public TrainCarCard data() {
		return data;
	}
	
	public TrainCarCard setData(TrainCarCard dt) {
		return data;
	}
	
	public BinaryNodeTCC left() {
			return left;
	}
	
	public BinaryNodeTCC right() {
			return right;
	}
	
	public void setLeft(BinaryNodeTCC l) {
		this.left = l;
	}
	
	public void setRight(BinaryNodeTCC r) {
		this.right = r;
	}

	public int compareTo(Object other) 
	{
		BinaryNodeTCC temp = (BinaryNodeTCC)other;
		if(data.compareTo(temp.data()) > 0)
			return 1;
		else if(data.compareTo(temp.data()) < 0)
			return -1;
		else
			return 0;
	}
}