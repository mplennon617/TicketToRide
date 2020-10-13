import java.util.ArrayList;

import java.util.LinkedList;

import java.util.Queue;

@SuppressWarnings("unchecked")

public class BinarySearchTreeDT 
{
	private BinaryNodeDT root;
	
	public BinarySearchTreeDT()
	{
		root = null;
	}
	
	public BinaryNodeDT getRoot()
	{
		return root;
	}
	
	public void clear()
	{
		root = null;
	}
	
	public void add(BinaryNodeDT d)
	{
		add(d, getRoot());
	}
	
	public void add(BinaryNodeDT d, BinaryNodeDT cur)
	{
		if(root == null)
		{
			root = d;
		}
		else
		{
			if(cur.compareTo(d) > 0)
			{
				if(cur.left() != null)
					add(d,cur.left());
				else
					cur.setLeft(d);
			}
			else
			{
				if(cur.right() != null)
					add(d,cur.right());
				else
					cur.setRight(d);
			}
		}
	}
	
	public BinaryNodeDT remove(BinaryNodeDT d)
	{
		if(contains(d))
			return rem(d,getRoot());
		else
			return null;
	}
	
	private BinaryNodeDT rem(BinaryNodeDT d, BinaryNodeDT cur)
	{
		if(getRoot().compareTo(d) == 0)
		{
			if(getRoot().left() == null && getRoot().right() == null)
			{
				BinaryNodeDT temp = getRoot();
				root = null;
				return temp;
			}
			else if(getRoot().left() == null && getRoot().right() != null)
			{
				BinaryNodeDT temp = getRoot();
				root = getRoot().right();
				temp.setRight(null);
				return temp;
			}
			else if(getRoot().left() != null && getRoot().right() == null)
			{
				BinaryNodeDT temp = getRoot();
				root = getRoot().left();
				temp.setLeft(null);
				return temp;
			}
			else
			{
				BinaryNodeDT temp = getRoot();
				root = getRoot().left();
				add(temp.right());
				temp.setLeft(null);
				temp.setRight(null);
				return temp;
			}
		}
		else if(cur.compareTo(d) > 0)
		{
			if(cur.left().compareTo(d) == 0)
				return rem(d,cur,cur.left());
			else
				return rem(d,cur.left());
		}
		else
		{
			if(cur.right().compareTo(d) == 0)
				return rem(d,cur,cur.right());
			else
				return rem(d,cur.right());
		}
	}
	
	private BinaryNodeDT rem(BinaryNodeDT d, BinaryNodeDT parent, BinaryNodeDT cur)
	{
		if(cur.left() == null && cur.right() == null)
		{
			if(cur.compareTo(parent.left()) == 0)
			{
				parent.setLeft(null);
				return d;
			}
			else
			{
				parent.setRight(null);
				return d;
			}
		}
		else if(cur.left() == null && cur.right() != null)
		{
			if(cur.compareTo(parent.left()) == 0)
			{
				parent.setLeft(cur.right());
				cur.setRight(null);
				return cur;
			}
			else
			{
				parent.setRight(cur.right());
				cur.setRight(null);
				return cur;
			}
		}
		
		else if(cur.right() == null && cur.left() != null)
		{
			if(cur.compareTo(parent.left()) == 0)
			{
				parent.setLeft(cur.left());
				cur.setLeft(null);
				return cur;
			}
			else
			{
				parent.setRight(cur.left());
				cur.setLeft(null);
				return cur;
			}
		}
		
		else
		{
			if(cur.compareTo(parent.left()) == 0)
			{
				parent.setLeft(cur.left());
				add(cur.right());
				cur.setLeft(null);
				cur.setRight(null);
				return cur;
			}
			else
			{
				parent.setRight(cur.left());
				add(cur.right());
				cur.setLeft(null);
				cur.setRight(null);
				return cur;
			}
		}
	}
	
	public Boolean contains(BinaryNodeDT d)
	{
		return con(d,getRoot());
	}
	
	private Boolean con(BinaryNodeDT d, BinaryNodeDT cur)
	{
		if(cur == null)
			return false;
		else if(cur.compareTo(d) == 0)
			return true;
		else
		{
			if(cur.compareTo(d) > 0)
				return con(d,cur.left());
			else
				return con(d,cur.right());
		}
		
	}
	
	public int getNumNodes()
	{
		return cnt(getRoot());
	}
	
	private int cnt(BinaryNodeDT cur)
	{
		if(cur == null)
			return 0;
		else
			return 1 + cnt(cur.right()) + cnt(cur.left());
	}
	
	// Does this work? :>>>>>>>>
	public Queue<DestinationTicket> getLevelOrder()
	{
		//System.out.println("In getLevelOrder");
		
		Queue<DestinationTicket> q = new LinkedList<DestinationTicket>();
		Queue<BinaryNodeDT> tQ = new LinkedList<BinaryNodeDT>();
		
		int c = 0;
		
		if (root != null)
			tQ.add(root);
		
		while (!tQ.isEmpty() && c < 68)
		{
			BinaryNodeDT temp = tQ.remove();
			q.add(temp.data());
			
			if (temp.left() != null)
				tQ.add(temp.left());
			if (temp.right() != null)
				tQ.add(temp.right());
			
			c++;
		}
		
		return q;
	}
}