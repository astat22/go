package Game;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Exceptions.IllegalInitialisationOfTerritoryException;
import Exceptions.UndeterminedTerritoryError;

public class Territory extends Plane
{
	private Colour territoryOwner;//w³aœciciel terytorium
	private int points; //liczba punktów za terytorium
	/**
	 * Konstruktor terytorium. Jako parametr przyjmuje puste pole na planszy, place, le¿¹ce obok kamienia requester.
	 * Konstruktor dodaje kolejne pola do listy, przeszukuj¹c wynikek planszy ograniczony kamieniami graczy.
	 * @param place Pole, od którego zaczniemy uslalanie terytorium
	 * @param requester kamieñ gracza, który roœci sobie prawo do terytorium
	 */
	public Territory(Square place, Chain requester) throws IllegalInitialisationOfTerritoryException, UndeterminedTerritoryError
	{
		if(place.getColour()!=Colour.NONE || !requester.isAlive())//terytorium inicjalizuje siê polem, które nie nale¿y do nikogo lub z miejsca, które jest martwe
			throw new IllegalInitialisationOfTerritoryException();
		else
		{
			stones = new ArrayList<Square>();
			stones.add(place);//tu by³ b³¹d?
			place.setMarked(true);
			exploreTerritory();//ustalamy terytorium
			computeNeighbours();//ustalamy s¹siadów
			//ustalamy w³aœciciela
			try
			{
				this.territoryOwner = determineOwner(requester.getColour(), this.neighbours);
			}
			catch(UndeterminedTerritoryError e)//przekazywanie wyj¹tku wy¿ej
			{
				throw new UndeterminedTerritoryError();
			}
			//ustalamy liczbê punktów
			this.points = stones.size();
			for(Square s: stones)
			{
				s.setColour(territoryOwner==Colour.BLACK?Colour.OB:Colour.OW);
			}
			System.out.println("Rozmiar terytorium="+stones.size()+" Ziarno pocz¹tkowe="+place.getxCoord()+","+place.getyCoord()+" W³aœciciel: "+territoryOwner);
		}
		
	}
	/**
	 * Rekurencyjna metoda wype³niania terytorium
	 */
	private void exploreTerritory()
	{
		computeNeighbours();
		int check = 0;
		for(Square s: this.neighbours)
		{
			if((!stones.contains(s)) && s!=null && s.getColour()==Colour.NONE) //na liœcie nie ma jeszcze tego pustego pola
			{
				stones.add(s);//dodajemy kamieñ
				//System.out.println("Dodajê kamieñ="+s.getxCoord()+","+s.getyCoord());
				s.setMarked(true);//oznaczamy kamieñ jako nale¿¹cy do jakiegoœ terytorium
				check++;
			}
		}
		//if(listEqualsNoOrder(neighbours, computeListOfNeighbours()))//warunek koñcowy: s¹siedzi przed obliczeniami i po obliczeniach s¹ tacy sami
		if(check>0)//³atwiejszy warunek koñcowy: jeœli w przebiegu funkcji dodaliœmy jakiekolwiek pole, to wywo³ujemy funkcjê rekurencyjnie
		{
			exploreTerritory();
		}
	}
	/**
	 * Metoda ze StackOverflow do porównywania, czy dwie listy maj¹ tê sam¹ zawartoœc
	 * @param l1
	 * @param l2
	 * @return
	 */
	public static <T> boolean listEqualsNoOrder(List<T> l1, List<T> l2) 
	{
	    final Set<T> s1 = new HashSet<>(l1);
	    final Set<T> s2 = new HashSet<>(l2);

	    return s1.equals(s2);
	}
	/**
	 * Metoda s³u¿¹ca do ustalenia, kto jest w³aœcicielem terytorium.
	 * @param claimer Gracz roszcz¹cy sobie prawa do terytorium
	 * @param neighbours Lista s¹siadów terytorium
	 * @return Kolor gracza claimer, jeœli wszyscy s¹siedzi s¹ koloru claimer, Colour.NONE wpp.
	 * @throws UndeterminedTerritoryError B³¹d, jeœli istniej¹ s¹siedzi bez koloru.
	 */
	private Colour determineOwner(Colour claimer, List<Square> neighbours) throws UndeterminedTerritoryError
	{
		for(Square n: neighbours)
		{
			if(n.getColour()==Colour.NONE)
				throw new UndeterminedTerritoryError();
			else
				if(n.getColour()!=claimer && n.isAlive())//jeœli w s¹siedztwie terytorium le¿y ¿ywy kamieñ innego koloru ni¿ roszczeniowiec, terytorium jest niczyje
					return Colour.NONE;
		}
		return claimer;
	}
	public Colour getTerritoryOwner() {
		return territoryOwner;
	}
	public int getPoints() {
		return points;
	}

	@Override
	public List<Square> computeListOfNeighbours()
	{
		List<Square> localNeighbours = new ArrayList<Square>();
		for(Square stone: stones)
		{
			for(Square neighbour: stone.getNeighbours())
			{
				if((!localNeighbours.contains(neighbour)) && (!stones.contains(neighbour)) && neighbour!= null)
				{
					localNeighbours.add(neighbour);
				}
			}
		}
		return localNeighbours;
	}
	@Override
	public void computeNeighbours()
	{
		this.neighbours = computeListOfNeighbours();
	}
}
