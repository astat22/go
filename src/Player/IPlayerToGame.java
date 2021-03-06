//TODO ko?czenie gry, podliczanie punkt?w
package Player;

import Exceptions.GameNotEndedException;
import Exceptions.IllegalOwnerException;
import Exceptions.InitialBreathException;
import Exceptions.PlaceReservedException;
import Exceptions.PlayerPassedException;
import Exceptions.RepeatedSituationException;
import Game.Colour;
import Game.Report;

public interface IPlayerToGame 
{
	/**
	 * Bie??cy gracz zrzeka si? ruchu
	 */
	public void pass();
	/**
	 * Metoda pobieraj?ca plansz? wraz z aktualnie stoj?cymi na niej kamieniami
	 * @return
	 */
	public Colour[][] getTable();
	/**
	 * Metoda s?u??ca do wykonywania ruchu. Bie??cy gracz stawia na planszy sw?j kamie? w miejsce (x,y).
	 * @param x
	 * @param y
	 * @throws InitialBreathException Miejsce, w kt?rym chcesz postawic kamie?, jest martwe
	 * @throws PlaceReservedException Miejsce, w kt?rym chcesz postawi kamie?, jest zaj?te przez inny kamie?
	 * @throws RepeatedSituationException Gdyby? postawi? w ten spos?b kamie?, z?ama?by? regu?? Ko
	 * @throws PlayerPassedException Gracz, kt?ry spasowa?, nie mo?e wykona ruchu
	 */
	public void makeMove(int x,int y) throws 
		InitialBreathException,
		PlaceReservedException, 
		RepeatedSituationException, 
		PlayerPassedException;
	/**
	 * 
	 * @param x
	 * @param y
	 * @param owner Gracz, kt?ry oznacza kamie?
	 * @throws GameNotEndedException
	 * @throws IllegalOwnerException
	 */
	public void setDeadSquare(int x,int y, Colour owner) throws GameNotEndedException, IllegalOwnerException;
	/**
	 * Metoda ko?cz?ca gr?. Gra przechodzi do fazy oznaczania martwych p?l. Dopiero wywo?anie metody getResults() doprowadzi do wyliczenia terytorium i pobrania punkt?w.
	 */
	public void endGame();
	/**
	 * Metoda zwracaj?ca kolor gracza wykonuj?cego posuni?cie.
	 * @return
	 */
	public Colour onMove();
	public Report getResults();
}
