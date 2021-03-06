package Game;

import java.util.ArrayList;
import java.util.List;

public class Plane 
{
	public List<Square> stones;
	public List<Square> neighbours;
	public Colour colour;
	public void computeNeighbours()
	{
		this.neighbours = computeListOfNeighbours();
	}
	public List<Square> computeListOfNeighbours()
	{
		List<Square> localNeighbours = new ArrayList<Square>();
		for(Square stone: stones)
		{
			for(Square neighbour: stone.getNeighbours())
			{
				if(canBeAddedToNeighbours(neighbour, localNeighbours))
				{
					localNeighbours.add(neighbour);
				}
			}
		}
		return localNeighbours;
	}
	public List<Square> getStones() {
		return stones;
	}
	public List<Square> getNeighbours() {
		return neighbours;
	}
	public Colour getColour() {
		return colour;
	}
	public boolean isStoneHere(Square stone)
	{
		for(Square localStone: stones)
		{
			if( localStone.getxCoord() == stone.getxCoord() && localStone.getyCoord() == stone.getyCoord())
				return true;
		}
		return false;
	}
	/**
	 * Metoda sprawdza, czy dane pola by?o ju? dodane do s?siedztwa neighbours
	 * @param stone
	 * @param neighbours
	 * @return
	 */
	public boolean canBeAddedToNeighbours(Square stone, List<Square> neighbours)
	{
		if(stone==null)
			return false;
		for(Square localStone: neighbours)
		{
			/*if(isStoneHere(localStone))
				return false;*/
			if( (localStone.getxCoord() == stone.getxCoord() && localStone.getyCoord() == stone.getyCoord() && stone.getColour()!=this.colour))
				return false;
		}
		return true;
	}
	public void addStone(Square newStone)
	{
		this.stones.add(newStone);
	}
}
