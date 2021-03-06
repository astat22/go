package Game;

import java.util.ArrayList;
import java.util.List;

public class Chain extends Plane
{

	public int breaths;
	Colour colour;
	public boolean isAlive = true;
	public Chain(Square stone)
	{
		this.stones = new ArrayList<Square>();
		stones.add(stone);
		this.colour = stone.getColour();
		computeNeighbours();
		//System.out.println("Liczba s?siad?w ?a?cucha: "+ neighbours.size());
		computeBreaths();
	}
	public Chain(List<Square> chainedStones)
	{
		this.stones = chainedStones;
		this.colour = chainedStones.get(1).getColour();
		computeNeighbours();
		computeBreaths();
	}
	public Chain(Colour colour)
	{
		this.stones = new ArrayList<Square>();
		this.colour=colour;
		computeNeighbours();
		computeBreaths();
	}
	/**
	 * Metoda obliczaj?ca i ustawiaj?ca warto?c {@link breaths}. Aby dzia?a?a poprawnie, konieczne jest wcze?niejsze wywo?anie metody {@link computeNeighbours()}
	 */
	public void computeBreaths()
	{
		this.breaths = computeNumberOfBreaths();
		//System.out.println("Oddechy ?a?cucha: "+ this.breaths);
	}
	public int getBreaths() {
		return breaths;
	}
	public Colour getColour() {
		return colour;
	}
	/**
	 * Metoda zliczaj?ca oddechy ?a?cucha. Aby dzia?a?a poprawnie, konieczne jest poprawne obliczenie {@link neighbours}
	 * @return liczba oddech?w ?a?cucha
	 */
	public int computeNumberOfBreaths()
	{
		int chainBreaths=0;
		for(Square stone: neighbours)
		{
			if(stone.getColour()==Colour.NONE)
			{
				chainBreaths++;
			}
		}
		return chainBreaths;
	}
	@Override
	public void addStone(Square newStone)
	{
		this.stones.add(newStone);
		computeNeighbours();
		computeBreaths();
	}
	public boolean isAlive() {
		return isAlive;
	}
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	
}
