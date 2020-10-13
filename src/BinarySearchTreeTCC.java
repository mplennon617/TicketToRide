import java.awt.Color;

import java.util.ArrayList;

import java.util.LinkedList;

import java.util.Queue;

@SuppressWarnings("unchecked")



public class BinarySearchTreeTCC 

{

	private BinaryNodeTCC root;

	private int count = 0;

	

	public BinarySearchTreeTCC()

	{

		root = null;

	}

	

	public BinaryNodeTCC getRoot()

	{

		return root;

	}

	

	public void add(BinaryNodeTCC d)

	{

		add(d, getRoot());

	}

	

	public void add(BinaryNodeTCC d, BinaryNodeTCC cur)

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

	

	public BinaryNodeTCC remove(BinaryNodeTCC d)

	{

		if(contains(d))

			return rem(d,getRoot());

		else

			return null;

	}

	

	private BinaryNodeTCC rem(BinaryNodeTCC d, BinaryNodeTCC cur)

	{

		if(getRoot().compareTo(d) == 0)

		{

			if(getRoot().left() == null && getRoot().right() == null)

			{

				BinaryNodeTCC temp = getRoot();

				root = null;

				return temp;

			}

			else if(getRoot().left() == null && getRoot().right() != null)

			{

				BinaryNodeTCC temp = getRoot();

				root = getRoot().right();

				temp.setRight(null);

				return temp;

			}

			else if(getRoot().left() != null && getRoot().right() == null)

			{

				BinaryNodeTCC temp = getRoot();

				root = getRoot().left();

				temp.setLeft(null);

				return temp;

			}

			else

			{

				BinaryNodeTCC temp = getRoot();

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

				return rem(true,cur,cur.left());

			else

				return rem(d,cur.left());

		}

		else

		{

			if(cur.right().compareTo(d) == 0)

				return rem(false,cur,cur.right());

			else

				return rem(d,cur.right());

		}

	}

	

	private BinaryNodeTCC rem(Boolean direction, BinaryNodeTCC parent, BinaryNodeTCC cur)

	{

		if(cur.left() == null && cur.right() == null)

		{

			if(direction == true)

			{

				parent.setLeft(null);

				return cur;

			}

			else

			{

				parent.setRight(null);

				return cur;

			}

		}

		else if(cur.left() == null && cur.right() != null)

		{

			if(direction == true)

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

			if(direction == true)

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

			if(direction == true)

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

	

	public Boolean contains(BinaryNodeTCC d)

	{

		return con(d,getRoot());

	}

	

	private Boolean con(BinaryNodeTCC d, BinaryNodeTCC cur)

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

	

	private int cnt(BinaryNodeTCC cur)

	{

		if(cur == null)

			return 0;

		else

			return 1 + cnt(cur.right()) + cnt(cur.left());

	}

	

	public int countTCCColor(Color c)

	{

		return cnt(c, root);

	}

	

	private int cnt(Color c, BinaryNodeTCC cur)

	{

		int cnt = 0;

		if(cur == null)

			return 0;

		else if(cur.data().getColor().equals(c))

			cnt++;

		return cnt + cnt(c, cur.left()) + cnt(c, cur.right());

	}

	

	// Does this work? :>>>>>>>>

	public Queue<TrainCarCard> getLevelOrder()

	{

		//System.out.println("In getLevelOrder");

		

		Queue<TrainCarCard> q = new LinkedList<TrainCarCard>();

		Queue<BinaryNodeTCC> tQ = new LinkedList<BinaryNodeTCC>();

		

		int c = 0;

		

		if (root != null)

			tQ.add(root);

		//System.out.println("root: "+root);

		

		while (!tQ.isEmpty() && c < 68)

		{

			BinaryNodeTCC temp = tQ.remove();

			

			if (temp != null)

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