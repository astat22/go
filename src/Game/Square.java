package Game;

import java.util.ArrayList;
import java.util.List;

public class Square 
{
	private int xCoord;
	private int yCoord;
	private Colour colour;
	private boolean isAlive = true;
	private SType type;
	private boolean marked = false;
	public SType getType() {
		return type;
	}
	private Square[] neighbours;
	/**
	 * Konstruktor podstawowy
	 * @param x
	 * @param y
	 * @param type 
	 */
	public Square(int x, int y, SType type)
	{
		this.xCoord=x;
		this.yCoord=y;
		this.type=type;
		this.colour = Colour.NONE;
		neighbours = new Square[4]; // S?siedzi w tablicy s? w kolejno?ci N S W E
	}
	/**
	 * Konstruktor uproszczony u?ywany do procesu klonowania
	 * @param x
	 * @param y
	 * @param colour
	 */
	public Square(int x, int y, Colour colour)
	{
		this.xCoord=x;
		this.yCoord=y;
		this.colour = colour;
	}
	public Colour getColour() 
	{
		return colour;
	}
	public void setColour(Colour colour) 
	{
		this.colour = colour;
	}
	public Square[] getNeighbours() 
	{
		return neighbours;
	}
	/**
	 * Metoda zwracaj?ca list? niepustych s?siad?w bie??cego pola
	 * @return
	 */
	public List<Square> getNotNullNeighbours()
	{
		List<Square> notNullNeighbours = new ArrayList<Square>();
		for(Square s: neighbours)
			if(s!=null)
				notNullNeighbours.add(s);
		return notNullNeighbours;
	}
	public void setNeighbours(Square[] neighbours) 
	{
		this.neighbours = neighbours;
	}
	public int getxCoord() 
	{
		return xCoord;
	}
	public int getyCoord() 
	{
		return yCoord;
	}
	public boolean isAlive() {
		return isAlive;
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	public boolean isMarked() {
		return marked;
	}
	public void setMarked(boolean marked) {
		this.marked = marked;
	}
}
