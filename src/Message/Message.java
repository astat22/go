package Message;

import java.io.Serializable;

import Client.Phase;
import Game.Colour;
import Game.Report;
import Server.Order;

public class Message implements Serializable
{
	public static final long serialVersionUID = 42L;
	private final Order order;
	public Colour[][] table;
	public int[][] board;
	public int x,y, rec, size=19;
	public String owner;
	public Colour col;
	public Phase phase;
	public Report results;
	public Message(Order order)
	{
		this.order = order;
	}
	/**
	 * 
	 * @param order
	 * @param t
	 * @param i numer ruchu
	 */
	public Message(Order order, Colour[][] t,int i,int x,int y,Colour c)
	{
		this.order = order;
		this.table = t;
		this.rec=i;
		this.x = x;
		this.y = y;
		this.col = c;
	}
	public Message(Order order, Colour[][] t)
	{
		this.order = order;
		this.table = t;
		board = colourToInt();
	}
	public Message(Order order, int x, int y)
	{
		this.order = order;
		this.x = x;
		this.y = y;
	}
	public Message(Order order, int x, int y, Phase p)
	{
		this.order = order;
		this.x = x;
		this.y = y;
		this.phase = p;
	}
	public Message(Order order,Colour c)
	{
		this.order = order;
		this.col = c;
	}
	public Message(Order order, Report rep)
	{
		this.order = order;
		this.results = rep;
	}
	private int[][] colourToInt()
	{
		int[][] board = new int[size][size];
		for(int i=0;i<size;i++)
			for(int j=0;j<size;j++)
			{
				if(table[i][j]==Colour.NONE)
					board[i][j]=0;
				else if(table[i][j]==Colour.BLACK)
					board[i][j]=1;
				else if(table[i][j]==Colour.WHITE)
					board[i][j]=2;
			}
		return board;
	}
	public Colour[][] int2Col()
	{
		table = new Colour[size][size];
		for(int i=0;i<size;i++)
			for(int j=0;j<size;j++)
			{
				if(board[i][j]==0)
					table[i][j]=Colour.NONE;
				else if(board[i][j]==1)
					table[i][j]=Colour.BLACK;
				else if(board[i][j]==2)
					table[i][j]=Colour.WHITE;
			}
		return table;
	}
	public Order getOrder() {
		return order;
	}
	public Colour[][] getTable() {
		return table;
	}
	public int getX() {
		return x;
	}
	public int getY() {
		return y;
	}
	public Colour getCol() {
		return col;
	}
	
}
