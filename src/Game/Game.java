
//podliczanie punkt?w
//bot

package Game;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import Exceptions.CannotMergeChainsException;
import Exceptions.GameNotEndedException;
import Exceptions.IllegalInitialisationOfTerritoryException;
import Exceptions.IllegalOwnerException;
import Exceptions.InitialBreathException;//nie mo?na dodac kamienia w miejsce bez oddech?w, o ile nie bije
import Exceptions.PlaceReservedException; //nie mo?na doda kamienia w niepuste miejsce
import Exceptions.RepeatedSituationException; //nie mo?na dodac kamienia, je?li sytuacja na planszy powt?rzy si?
import Exceptions.UndeterminedTerritoryError;
import GameTest.TestTools;

/**
 * Klasa odpowiedzialna za logik? gry 
 * @author Jacek
 *
 */
public class Game implements Cloneable
{

	private int boardSize; //rozmiar planszy do gry
	public List<Chain> chains; //?a?cuch to grupa kamieni tego samego koloru, kt?re si? ze sob? stykaj?
	private Square[][] squares; //plansza jako tablica obiekt?w typu Square
	public Colour[][] board; //uproszczona plansza widzialna z zewn?trz: tablica, kt?ra wsp??rz?dnej przyporz?dkowuje kolor
	public Colour[][] previousBoard; //przechowujemy w pami?ci plansz? sprzed jednego ruchu, aby obs?u?y wyj?tek RepeatedSituationException
	public boolean[][] legalMoves; //TODO
	private Colour onMove = Colour.BLACK;// zmienna informuj?ca, kto aktualnie wykonuje posuni?cie. Zaczynaj? czarne.
	public int whiteSlaves=0,blackSlaves=0; //suma kamieni zbitych odpowiednio przez bia?ych i przez czarnych
	public boolean whitePassed=false,blackPassed=false; //czy kt?ry? z graczy spasowa??
	public boolean gameEnded = false;
	public List<Territory> territories;
	public Report results;
	public GameTest.TestTools tt;
	/**
	 * Konstruktor inicjuj?cy pola
	 * @param size
	 */
	public Game(int size)
	{
		tt = new TestTools();
		this.boardSize=size;
		chains = new ArrayList<>();
		createSquares();
	}
	/**
	 * Implementacja metody clone z interfejsu Cloneable. Zwraca uproszczon? kopi? obiektu this.
	 */
	public Object clone()
	{
		Game clonedGame = new Game(boardSize);
		//klonowanie ?a?cuch?w i p?l
		List<Chain> clonedChains = new ArrayList<Chain>();
		Chain clonedChain;
		Square clonedStone;
		for(Chain c: chains)
		{
			clonedChain = new Chain(c.getColour());
			for(Square s: c.stones)
			{
				clonedStone =clonedGame.getSquareByCoordinates(s.getxCoord(),s.getyCoord());
				clonedStone.setColour(s.getColour());
				clonedChain.addStone(clonedStone);
			}
			clonedChains.add(clonedChain);
		}	
		clonedGame.setChains(clonedChains);
		//klonowanie planszy
		for(int x=0;x<boardSize;x++)
			for(int y=0;y<boardSize;y++)
			{
				clonedGame.board[x][y] = this.board[x][y];
				clonedGame.previousBoard[x][y] = this.previousBoard[x][y];
				clonedGame.legalMoves[x][y] = this.legalMoves[x][y];
			}
		clonedGame.setOnMove(onMove);
		return clonedGame;
	}
/**
 * Metoda wywo?ywana tylko raz, przez konstruktor. Tworzy boardSize x boardSize obiekt?w typu Square i umieszcza je w tablicy squares.
 */
	private void createSquares()
	{
		squares = new Square[boardSize][boardSize];
		board = new Colour[boardSize][boardSize];
		previousBoard = new Colour[boardSize][boardSize];
		legalMoves = new boolean[boardSize][boardSize];
		for(int y=0;y<boardSize;y++)
			for(int x=0;x<boardSize;x++)
			{
				squares[x][y] = new Square(x,y,computeType(x,y));
			}
		//okre?lanie s?siad?w
		for(int y=0;y<boardSize;y++)
			for(int x=0;x<boardSize;x++)
			{
				board[x][y] = Colour.NONE;
				if(x>0)
					squares[x][y].getNeighbours()[2] = squares[x-1][y]; //W
				else
					squares[x][y].getNeighbours()[2] = null;
				if(x<boardSize-1)
					squares[x][y].getNeighbours()[3] = squares[x+1][y]; //E
				else
					squares[x][y].getNeighbours()[3] = null;
				if(y>0)
					squares[x][y].getNeighbours()[1] = squares[x][y-1]; //S
				else
					squares[x][y].getNeighbours()[1] = null;
				if(y<boardSize-1)
					squares[x][y].getNeighbours()[0] = squares[x][y+1]; //N
				else
					squares[x][y].getNeighbours()[0] = null;
			}
	}
	/**
	 * Metoda zwracaj?ca ?a?cuch, kt?rego elementem jest stone
	 * @param stone
	 * @return
	 */
	public Chain findChainByStone(Square stone)
	{
		for(Chain chain: chains)
		{
			if(chain.isStoneHere(stone))
				return chain;
		}
		return null;
	}
	/**
	 * Metoda ustalaj?ca typ pola Square, tzn., czy pole (x,y) le?y na kraw?dzi, na rogu, czy w ?rodku planszy
	 * @param x
	 * @param y
	 * @return
	 */
	private SType computeType(int x,int y)
	{
		if(x==0 && y==0)
			return SType.SW;
		if(x==boardSize-1 && y==0)
			return SType.SE;
		if(x==0 && y==boardSize-1)
			return SType.NW;
		if(x==boardSize-1 && y==boardSize-1)
			return SType.NE;
		if(x==0)
			return SType.W;
		if(x==boardSize-1 )
			return SType.E;
		if(y==0)
			return SType.S;
		if(y==boardSize-1)
			return SType.N;
		return SType.REGULAR;
	}
	/**
	 * Metoda zwracaj?ca pole planszy po jego wsp??rz?dnych
	 * @param x
	 * @param y
	 * @return
	 */
	public Square getSquareByCoordinates(int x,int y)
	{
		return squares[x][y];
	}
	/**
	 * ??czy dwa ?a?cuchy z listy c w jeden, C
	 * Usuwa z listy @chains ?a?cuchy z c.
	 * Wylicza now? liczb? oddech?w dla C.
	 * Je?li ruch okaza?by si? niedozwolony, C nie jest dodawany do listy, a c nie s? usuwane
	 * 
	 * @param c
	 *
	 */
	private void mergeChains(List<Chain> listOfChains, boolean mergeAnyway) throws CannotMergeChainsException
	{
		List<Square> sumOfSquaresLists = new ArrayList<Square>();
		for(Chain c: listOfChains)
		{
			sumOfSquaresLists.addAll(c.getStones());
		}
		Chain newChain = new Chain(sumOfSquaresLists);
		if(newChain.getBreaths()>0 || mergeAnyway)//nowy ?a?cuch ma wystarczaj?c? liczb? oddech?w
		{
			for(Chain c: listOfChains)
			{
				removeChainFromChainList(c); //usu? stare ?a?cuchy
			}
			chains.add(newChain); //dodaj nowy ?a?cuch
		}
		else
		{
			throw new CannotMergeChainsException();
		}
	}
	/**
	 * Metoda usuwaj?ca ?a?cuch z listy ?a?cuch?w
	 * @param chainToRemove
	 */
	private void removeChainFromChainList(Chain chainToRemove)
	{
		chains.remove(chainToRemove);
	}
	private boolean compareBoards(Colour[][] boardA, Colour[][] boardB)
	{
		//drawBoard(boardA, boardSize);
		//drawBoard(boardB, boardSize);
		for(int i=0;i<boardSize;i++)
			for(int j=0;j<boardSize;j++)
				if(boardA[i][j]!=boardB[i][j])
					return false;
		return true;
	}
	/** Metoda zwraca list? ?a?cuch?w do usuni?cia, w zale?no?ci od tego, czy maj? 1, czy 0 oddech?w.
	 * 
	 */
	private List<Chain> checkForDeadChains(boolean almostDead,Colour col)
	{
		List<Chain> chainsToRemove = new ArrayList<>();//trzeba utworzy kopi?, inaczej ConcurrentModificationException
		int deadlyBreaths = almostDead?1:0;
		for(Chain c: chains)
		{
			c.computeNeighbours();
			c.computeBreaths();
			if(c.getBreaths()==deadlyBreaths && c.getColour()!=col)
			{
				chainsToRemove.add(c);
			}
		}
		if(!almostDead)//?a?cuchy nie maj? oddech?w, wi?c trzeba je usun?c
		{
			removeListOfChains(chainsToRemove);
		}
		return chainsToRemove;
	}
	/** Przeci??ona metoda zwraca list? ?a?cuch?w do usuni?cia, w zale?no?ci od tego, czy maj? 1, czy 0 oddech?w.
	 * 
	 */
	private List<Chain> checkForDeadChains(boolean almostDead, Square n,Colour col)
	{
		List<Chain> chainsToRemove = new ArrayList<>();//trzeba utworzyc kopi?, inaczej ConcurrentModificationException
		int deadlyBreaths = almostDead?1:0;
		for(Chain c: chains)
		{
			c.computeNeighbours();
			c.computeBreaths();
			if(c.getBreaths()==deadlyBreaths && c.neighbours.contains(n) && c.getColour()!=col)//brakowa?o warunku o jednakowym kolorze
			{
				chainsToRemove.add(c);
			}
		}
		if(!almostDead)//?a?cuchy nie maj? oddech?w, wi?c trzeba je usun?c
		{
			removeListOfChains(chainsToRemove);
		}
		return chainsToRemove;
	}
	/**
	 * Metoda usuwaj?ca z planszy i z listy ?a?cuch?w list? ?a?cuch?w podan? w parametrze
	 * @param chainsToRemove
	 */
	public void removeListOfChains(List<Chain> chainsToRemove)
	{
		for(Chain c: chainsToRemove)
		{
			removeChainFromBoard(c);
			//removeChainFromChainList(c); //powtarza si? w removeChainFromBoard
		}
		for(Chain c: chains)//po usuni?ciu ?a?cuch?w trzeba na nowo obliczyc s?siad?w i oddechy
		{
			c.computeNeighbours();
			c.computeBreaths();
		}	
	}
	/**
	 * Metoda s?u??ca do umieszczania nowego kamienia na planszy. Metoda jest publiczna, aby mog?a zostac wykorzystana przez fasad?.
	 * Jedna z najwa?niejszych metod obiektu Game.
	 * @param x
	 * @param y
	 * @param colour
	 * @throws PlaceReservedException
	 */
	public void addStone(int x, int y, Colour colour) throws 
			PlaceReservedException, 
			InitialBreathException, 
			RepeatedSituationException
	{

		Square place = getSquareByCoordinates(x, y);
		Chain chainForSquare;
		if(place.getColour()!=Colour.NONE) //miejsce jest zaj?te
		{
			System.out.println("Miejsce zaj?te!");
			throw new PlaceReservedException();
		}
		else
		{
			//sprawdzanie s?siad?w 
			if(initialBreathCheking(place, colour)) //czy po dodaniu kamienia b?dzie oddech? 
			{
				try
				{
					chainForSquare = placeStone(place, colour,false); //zmienianie koloru pola
				}
				catch(CannotMergeChainsException e)
				{
					throw new InitialBreathException();
				}
				// usu? uduszone kamienie
				checkForDeadChains(false,colour);// licz? si? tylko wrogie ?a?cuchy!//TODO czy teraz dzia?a?
			}
			else
			{
				//mo?na postawic kamie?, je?li usunie si? wrogi ?a?cuch
				List<Chain> chainsToRemove = checkForDeadChains(true,place,colour); 
				//je?li jaki? wrogi ?aa?cuch ma tylko 1 oddech, to po dodaniu kamienia zostanie usuniety
				if(chainsToRemove.size()>0) // zasada nie dzia?a, je?li po dodaniu kamienia powt?rzy?aby si? sytuacja previousBoard
				{
					//stawiam kamie? na kopii Game, potem sprawdzam, czy sytuacja na planszy powt?rzy?a si? - ZASADA KO
					Game simulatedGame = (Game)this.clone();
					try
					{
						simulatedGame.placeStone(simulatedGame.getSquareByCoordinates(place.getxCoord(), place.getyCoord()), colour,true);
					}
					catch(CannotMergeChainsException e)
					{
						throw new InitialBreathException();
					}
					if(simulatedGame.getSquareByCoordinates(place.getxCoord(), place.getyCoord()).getColour()==Colour.NONE)
						System.out.println("Symulacja nie powiod?a si?!");
					List<Chain> clonedChainsToRemove = new ArrayList<Chain>();
					for(Chain c: chainsToRemove)
					{
						Square s = c.stones.get(0);
						Chain cc = simulatedGame.findChainByStone(getSquareByCoordinates(s.getxCoord(), s.getyCoord()));
						clonedChainsToRemove.add(cc);
					}
					//drawBoard(previousBoard, boardSize);
					//drawBoard(simulatedGame.board, boardSize);
					simulatedGame.removeListOfChains(clonedChainsToRemove);//naprawic?
					if(!compareBoards(this.previousBoard,simulatedGame.getBoard()))//previousBoard w game musi byc r??ne od symulowanej sytuacji na planszy
					{
						squaresToBoard(previousBoard);//zapami?taj obecn? plansz? w previousBoard
						try
						{
							chainForSquare = placeStone(place, colour,true); //zmienianie koloru pola
						}
						catch(CannotMergeChainsException e)
						{
							throw new InitialBreathException();
						}
						removeListOfChains(chainsToRemove); //usu? ?a?cuchy bez oddech?w
						squaresToBoard(board); //nowa plansza z dodanym kamieniem i usuni?tymi wrogimi ?a?cuchami
					}
					else
					{
						throw new RepeatedSituationException();
					}
				}
				else
				{
					System.out.println("Brak pocz?tkowych oddech?w!");
					throw new InitialBreathException();
				}
			}
		}
		checkForDeadChains(false, Colour.NONE);
	}
	/**
	 * Metoda wykonawcza dla metody addStone. 
	 * @param newStone
	 * @param colour
	 * @return
	 */
	public Chain placeStone(Square newStone, Colour colour, boolean placeAnyway) throws CannotMergeChainsException
	{
		newStone.setColour(colour);
		//dodaj kamie? do zaprzyja?nionego ?a?cucha 
		List<Chain> fChains = getFriendlyChains(newStone,colour);
		Chain fChain;
		if(fChains!=null)//istnieje zaprzyja?niony ?a?cuch
		{
			fChain = fChains.get(0);
			fChain.addStone(newStone);
			//po??cz zaprzyja?nione ?a?cuchy
			try
			{
				mergeChains(fChains,placeAnyway);
			}
			catch(CannotMergeChainsException e)
			{
				//System.out.println("Nie mo?na po??czyc ?a?cuch?w!");
				throw new CannotMergeChainsException();
			}
		}
		else //nie istnieje zaprzyja?niony ?a?cuch. stw?rz nowy ?a?cuch i dodaj go do listy ?a?cuch?w.
		{
			fChain = new Chain(newStone);
			chains.add(fChain);
			//System.out.println("Dodaj? nowy ?a?cuch");
		}
		return fChain;
	}
	/**
	 * Metoda wykonywana po ka?dym ruchu, oddaj?ca kolejk? drugiemu graczowi.
	 * TODO co je?li jaki? gracz spasowa??
	 */
	public void changeColourOnMove()
	{
		if(onMove == Colour.BLACK)
			onMove = Colour.WHITE;
		else
			onMove = Colour.BLACK;
	}
	/**
	 * Metoda s?u??ca do usuwania ?a?cucha chainToRemove z planszy
	 * @param chainToRemove
	 */
	public void removeChainFromBoard(Chain chainToRemove)
	{
		//dodawanie punkt?w przeciwnikowi
		int points = chainToRemove.getStones().size();
		if(chainToRemove.getColour()==Colour.BLACK)
		{
			whiteSlaves+=points;
		}
		else
		{
			blackSlaves=+points;
		}
		//zmienianie kolor?w p?l
		for(Square s: chainToRemove.getStones())
		{
			s.setColour(Colour.NONE);
		}
		chainToRemove.getStones().clear();//czyszczenie listy z kamieni
		//obliczanie nowych oddech?w i s?siad?w
		List<Chain> nChains = new ArrayList<Chain>();
		Chain c;
		for(Square s: chainToRemove.getNeighbours())
		{
			if(s.getColour()!=Colour.NONE)
			{
				c = findChainByStone(s);
				if(!nChains.contains(c))
				{
					nChains.add(c);
				}
			}
		}
		for(Chain nc: nChains)
		{
			nc.computeNeighbours();
			nc.computeBreaths();
		}
		//usuwanie ?a?cucha z listy ?a?cuch?w
		chains.remove(chainToRemove);
	}
	/**
	 * Metoda otrzymuje Square place jako parametr i zwraca takie s?siednie ?a?cuchy place, kt?re maj? identyczny kolor jak place
	 * @param place
	 * @return
	 */
	public List<Chain> getNeighbourChains(Square place)
	{
		List<Chain> neighbourChainList = new ArrayList<Chain>();
		Colour colour = place.getColour();
		Square[] neighbours = place.getNeighbours();
		Chain localChain;
		for(Square s: neighbours)
		{
			if(s!=null)//place mo?e le?e na brzegu
			{
				if(s.getColour()==colour) //szukamy ?a?cuchy tego samego koloru co place
				{
					localChain = findChainByStone(s);
					if(!neighbourChainList.contains(localChain)) //do listy dodajemy tylko unikatowe ?a?cuchy
						neighbourChainList.add(localChain);
				}
			}
		}
		if(neighbourChainList.size()>0)//zamiast pustej listy zwracamy null
			return neighbourChainList;
		else
			return null;
	}
	/**
	 * Metoda s?u??ca do sprawdzania, czy kamie?koloru colour dodany na pole square b?dzie mia? oddech
	 * @param square
	 * @param colour kolor dodawanego kamienia
	 * @return
	 */
	public boolean initialBreathCheking(Square square, Colour colour)
	{
		List<Square> neighbours = square.getNotNullNeighbours();
		List<Square> friendlySquares = new ArrayList<>();
		for(Square s: neighbours)
		{
			if(s.getColour() == Colour.NONE)
				return true; //wolne pole oznacza, ?e jest oddech
			if(s.getColour() == colour)
				friendlySquares.add(s);
		}
		if(friendlySquares.isEmpty()) //brak wolnych p?l oraz brak przyjaznych p?l
			return false;
		else	//sprawdzanie oddech?w przyjaznych p?l
		{
			for(Square fs: friendlySquares)
			{
				if(findChainByStone(fs).getBreaths()>1)//je?li cho jeden przyjazny ?a?cuch-s?siad ma wi?cej ni? 1 oddech, kamie? b?dzie mia? oddechy
					return true;
			}
		}
		return false;
	}
	public List<Chain> getFriendlyChains(Square square, Colour colour)
	{
		List<Square> neighbours = square.getNotNullNeighbours();
		List<Chain> friendlyChains = new ArrayList<Chain>();
		Chain tempChain;
		for(Square s: neighbours)
		{
			if(s.getColour() == colour)
			{
				tempChain = findChainByStone(s);
				if(!friendlyChains.contains(tempChain))
				{
					friendlyChains.add(tempChain);
				}
			}
		}
		if(friendlyChains.size()==0)
			return null;
		else
			return friendlyChains;
	}
	/**
	 * Metoda przepisuj?ca tablic? Squares do tablicy @board
	 * @param board
	 */
	public void squaresToBoard(Colour[][] board)
	{
		for(int y=0;y<boardSize;y++)
			for(int x=0;x<boardSize;x++)
			{
				board[x][y] = squares[x][y].getColour();
			}
		//tt.drawBoard(board, boardSize);
	}
	/**
	 * Metoda realizuj?ca rezygnacj? z ruchu przez gracza
	 * @param c kolor gracza, kt?ry pasuje
	 * @param set ustawienie warto?ci zmiennej pasowania
	 */
	public void passGame(Colour c, boolean set)
	{
		if(c==Colour.WHITE)
			whitePassed = set;
		if(c==Colour.BLACK)
			blackPassed = set;
	}
	/**
	 * Metoda sprawdzaj?ca warunki zako?czenia gry
	 * @return true, je?li gra powinna si? zako?czyc
	 */
	public boolean doesGameEnd()
	{
		if(whitePassed && blackPassed)
			return true;
		else
			return false;
	}
	public boolean playerPassed()
	{
		if(getOnMove()==Colour.BLACK)
			return blackPassed;
		else
			return whitePassed;
	}
	/**
	 * Metoda ustawiaj?ca ?a?cuch z kamieniem o wsp??rz?dnych x,y na martwy
	 * @param x
	 * @param y
	 */
	public void setDead(int x, int y) throws GameNotEndedException, IllegalOwnerException
	{
		if(!gameEnded)
			throw new GameNotEndedException();
		Square s = getSquareByCoordinates(x, y);
		s.setAlive(false);
		Colour c = s.getColour();
		if(c==Colour.NONE || c==Colour.DEAD)
			throw new IllegalOwnerException();
		else
		{
			Chain ch = findChainByStone(s);
			for(Square st: ch.getStones())
			{
				st.setAlive(false);
				st.setColour(Colour.DEAD);
			}
			if(c==Colour.BLACK)
			{
				whiteSlaves+=ch.getStones().size();
			}
			else
				if(c==Colour.WHITE)
				{
					blackSlaves+=ch.getStones().size();
				}
				else
					throw new IllegalOwnerException();
		}
	}
	/**
	 * Metoda wywo?uj?ca sekwencj? ko?czenia gry. Obliczanie terytorium, obliczanie punkt?w.
	 */
	public void endGame()
	{
		this.gameEnded = true;
	}
	public void computeTerritories()
	{
		List<Square> ptrSquares;
		Territory tmpTer;
		territories = new ArrayList<Territory>();
		//przebiegamy po ?a?cuchach
		for(Chain c: chains)
		{
			c.computeNeighbours();//przezornie obliczam s?siad?w
			ptrSquares = c.getNeighbours();
			if(c.isAlive())
			{
				//System.out.println("Kolejny ?a?cuch, s?siad?w="+ptrSquares.size());
				//przebiegamy po s?siadach ?a?cucha
				for(Square s:ptrSquares)
				{
					if(s.getColour()==Colour.NONE && !s.isMarked())//bierzemy pod uwag? puste miejsca, kt?re nie s? oznaczone
					{
						try
						{
							tmpTer = new Territory(s,c);//stw?rz terytorium
							territories.add(tmpTer);//dodaj terytorium do listy terytori?w	
							//System.out.println("Dodaj? nowe terytorium");
						}
						catch(IllegalInitialisationOfTerritoryException e){ System.out.println("B??d inicjalizacji");}//wyj?tki obs?u?one instrukcjami warunkowymi
						catch(UndeterminedTerritoryError e){ System.out.println("Niezdefiniowane terytorium");}
					}
				}
			}
		}
		territories = new ArrayList<Territory>(new HashSet<Territory>(territories));
	}
	/**
	 * Metoda zwracaj?ce ko?cowe wyniki
	 * @return
	 */
	public Report finalScore()
	{
		int wp=0,bp=0;
		//zbierz punkty z kontrolowanych obszar?w
		for(Territory t: territories)
		{
			if(t.getTerritoryOwner() == Colour.BLACK)
			{
				bp+=t.getPoints();
			}
			else
				if(t.getTerritoryOwner() == Colour.WHITE)
				{
					wp+=t.getPoints();
				}
		}
		//dodaj punkty uzyskane za je?c?w
		bp+=blackSlaves;
		wp+=whiteSlaves;
		return new Report(wp, bp);
	}
	public Colour[][] getBoard() {
		return board;
	}
	public boolean[][] getLegalMoves() {
		return legalMoves;
	}
	public Colour getOnMove() {
		return onMove;
	}
	public void setOnMove(Colour onMove) {
		this.onMove = onMove;
	}
	public Colour[][] getPreviousBoard() {
		return previousBoard;
	}
	public void setBoard(Colour[][] board) {
		this.board = board;
	}
	public void setPreviousBoard(Colour[][] previousBoard) {
		this.previousBoard = previousBoard;
	}
	public int getBoardSize() {
		return boardSize;
	}
	public void setBoardSize(int boardSize) {
		this.boardSize = boardSize;
	}
	public List<Chain> getChains() {
		return chains;
	}
	public void setChains(List<Chain> chains) {
		this.chains = chains;
	}
	public Square[][] getSquares() {
		return squares;
	}
	public void setSquares(Square[][] squares) {
		this.squares = squares;
	}
	public void setLegalMoves(boolean[][] legalMoves) {
		this.legalMoves = legalMoves;
	}
	public Report getResults() 
	{
		System.out.print("getResults(): w=");
		//rozpoznaj terytoria 
		computeTerritories();
		//oblicz punkty
		this.results = finalScore();
		System.out.println(results.getWhitePoints()+" b="+results.getBlackPoints());
		return results;
	}
	public Colour[][] getSquares2Board()
	{
		Colour[][] c = new Colour[boardSize][boardSize];
		for(int y=0;y<boardSize;y++)
			for(int x=0;x<boardSize;x++)
			{
				c[x][y] = squares[x][y].getColour();
			}
		return c;
	}
}
