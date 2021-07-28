//Adapter powinien przesy�ac informacj� o ruchu zar�wno do Game gracza, jak i Game przeciwnika:
//Game gracza i Game przeciwnika powinny wykonac takie same operacje
/**
 * Pe�na dokumentacji nale�y szukac w dokumentacji interfejsu.
 */

package Player;

import Exceptions.GameNotEndedException;
import Exceptions.IllegalMoveException;
import Exceptions.IllegalOwnerException;
import Exceptions.InitialBreathException;
import Exceptions.PlaceReservedException;
import Exceptions.PlayerPassedException;
import Exceptions.RepeatedSituationException;
import Game.*;
import GameTest.TestTools;

public class PlayerToGameFacade implements IPlayerToGame 
{

	public Game game;
	public boolean gameEnded = false;
	public PlayerToGameFacade(int boardSize) 
	{
		game = new Game(boardSize);
		//System.out.println("Pomy�lnie utworzono fasad�");
	}
	/**
	 * Tylko ustawia stan gry na zako�czon�. Mo�na teraz oznacza martwe kamienie.
	 */
	@Override
	public void endGame() 
	{
		game.endGame();
		gameEnded = true;
	}

	@Override
	public void pass() 
	{
		game.passGame(game.getOnMove(), true);
		game.changeColourOnMove();
		if(game.doesGameEnd())
			endGame();

	}

	@Override
	public Colour[][] getTable() 
	{
		//System.out.println("Facade: getTable()");
		//GameTest.TestTools tt = new TestTools();
		//tt.drawBoard(game.getBoard(), 19);
		//game.tt.drawBoard(game.board, 19);
		//game.squaresToBoard(game.board);
		//return game.getBoard();
		return game.getSquares2Board();
	}

	@Override
	public void makeMove(int x, int y) throws 
	InitialBreathException, 
	PlaceReservedException, 
	RepeatedSituationException, 
	PlayerPassedException
	{
		Colour nom = game.getOnMove()==Colour.BLACK?Colour.WHITE:Colour.BLACK;
		if(! game.playerPassed() )
		{
			Colour[][] prev = game.getPreviousBoard();
			Colour[][] now = game.getBoard();
			game.squaresToBoard(game.getPreviousBoard());
			try{
				game.addStone(x, y, game.getOnMove());
				//game.squaresToBoard(game.getBoard());
				game.changeColourOnMove();
				//je�li wykona�e� ruch, to zdejmij pass z wroga
				if(game.playerPassed())
				{
					game.passGame(nom, false);
				}
			}
			catch(IllegalMoveException e)
			{
				//wracamy do stanu pocz�tkowego
				game.setPreviousBoard(prev);
				game.setBoard(now);
				//przeka� wyj�tki dalej
				if(e instanceof InitialBreathException)
					throw new InitialBreathException();
				if(e instanceof PlaceReservedException)
					throw new PlaceReservedException();
				if(e instanceof RepeatedSituationException)
					throw new RepeatedSituationException();
				if(e instanceof PlayerPassedException)
					throw new PlayerPassedException();
			}
		}
		else
		{
			throw new PlaceReservedException();
		}
	}

	@Override
	public void setDeadSquare(int x, int y, Colour owner) throws GameNotEndedException, IllegalOwnerException
	{
		try
		{
			game.setDead(x, y);
		}
		catch(GameNotEndedException e)
		{
			throw new GameNotEndedException();
		}
		catch(IllegalOwnerException e)
		{
			throw new IllegalOwnerException();
		}
	}
	
	@Override
	public Colour onMove()
	{
		return game.getOnMove();
	}
	@Override
	/**
	 * Oblicza terytoria i zwraca wynik - Raport.
	 */
	public Report getResults()
	{
		return game.getResults();
	}
	public boolean isGameEnded() {
		return gameEnded;
	}
	
}
