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
	private Colour territoryOwner;//w�a�ciciel terytorium
	private int points; //liczba punkt�w za terytorium
	/**
	 * Konstruktor terytorium. Jako parametr przyjmuje puste pole na planszy, place, le��ce obok kamienia requester.
	 * Konstruktor dodaje kolejne pola do listy, przeszukuj�c wynikek planszy ograniczony kamieniami graczy.
	 * @param place Pole, od kt�rego zaczniemy uslalanie terytorium
	 * @param requester kamie� gracza, kt�ry ro�ci sobie prawo do terytorium
	 */
	public Territory(Square place, Chain requester) throws IllegalInitialisationOfTerritoryException, UndeterminedTerritoryError
	{
		if(place.getColour()!=Colour.NONE || !requester.isAlive())//terytorium inicjalizuje si� polem, kt�re nie nale�y do nikogo lub z miejsca, kt�re jest martwe
			throw new IllegalInitialisationOfTerritoryException();
		else
		{
			stones = new ArrayList<Square>();
			stones.add(place);//tu by� b��d?
			place.setMarked(true);
			exploreTerritory();//ustalamy terytorium
			computeNeighbours();//ustalamy s�siad�w
			//ustalamy w�a�ciciela
			try
			{
				this.territoryOwner = determineOwner(requester.getColour(), this.neighbours);
			}
			catch(UndeterminedTerritoryError e)//przekazywanie wyj�tku wy�ej
			{
				throw new UndeterminedTerritoryError();
			}
			//ustalamy liczb� punkt�w
			this.points = stones.size();
			for(Square s: stones)
			{
				s.setColour(territoryOwner==Colour.BLACK?Colour.OB:Colour.OW);
			}
			System.out.println("Rozmiar terytorium="+stones.size()+" Ziarno pocz�tkowe="+place.getxCoord()+","+place.getyCoord()+" W�a�ciciel: "+territoryOwner);
		}
		
	}
	/**
	 * Rekurencyjna metoda wype�niania terytorium
	 */
	private void exploreTerritory()
	{
		computeNeighbours();
		int check = 0;
		for(Square s: this.neighbours)
		{
			if((!stones.contains(s)) && s!=null && s.getColour()==Colour.NONE) //na li�cie nie ma jeszcze tego pustego pola
			{
				stones.add(s);//dodajemy kamie�
				//System.out.println("Dodaj� kamie�="+s.getxCoord()+","+s.getyCoord());
				s.setMarked(true);//oznaczamy kamie� jako nale��cy do jakiego� terytorium
				check++;
			}
		}
		//if(listEqualsNoOrder(neighbours, computeListOfNeighbours()))//warunek ko�cowy: s�siedzi przed obliczeniami i po obliczeniach s� tacy sami
		if(check>0)//�atwiejszy warunek ko�cowy: je�li w przebiegu funkcji dodali�my jakiekolwiek pole, to wywo�ujemy funkcj� rekurencyjnie
		{
			exploreTerritory();
		}
	}
	/**
	 * Metoda ze StackOverflow do por�wnywania, czy dwie listy maj� t� sam� zawarto�c
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
	 * Metoda s�u��ca do ustalenia, kto jest w�a�cicielem terytorium.
	 * @param claimer Gracz roszcz�cy sobie prawa do terytorium
	 * @param neighbours Lista s�siad�w terytorium
	 * @return Kolor gracza claimer, je�li wszyscy s�siedzi s� koloru claimer, Colour.NONE wpp.
	 * @throws UndeterminedTerritoryError B��d, je�li istniej� s�siedzi bez koloru.
	 */
	private Colour determineOwner(Colour claimer, List<Square> neighbours) throws UndeterminedTerritoryError
	{
		for(Square n: neighbours)
		{
			if(n.getColour()==Colour.NONE)
				throw new UndeterminedTerritoryError();
			else
				if(n.getColour()!=claimer && n.isAlive())//je�li w s�siedztwie terytorium le�y �ywy kamie� innego koloru ni� roszczeniowiec, terytorium jest niczyje
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
