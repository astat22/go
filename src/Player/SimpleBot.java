package Player;

import java.util.Random;

import Exceptions.GameNotEndedException;
import Exceptions.IllegalMoveException;
import Exceptions.IllegalOwnerException;
import Exceptions.InitialBreathException;
import Exceptions.PlaceReservedException;
import Exceptions.PlayerPassedException;
import Exceptions.RepeatedSituationException;
import Game.Colour;
import Game.Report;

public class SimpleBot extends Thread implements IPlayerToGame {

	PlayerToGameFacade facade;
	private boolean surrender;
	private int size;
	private Random rn;
	private Colour col;
	
	public SimpleBot(int size, Colour colour)
	{
		this.surrender = false;
		rn = new Random();
		this.size = size;
		this.col = colour;
		this.run();
	}
	public void run()
	{
		int passDecision;
		while(!surrender)
		{
			synchronized(this)
			{
				if(onMove()==col)
				{
					passDecision = rn.nextInt()%100;
					if(passDecision<10)
						pass();
					else
						randomDecision();
				}
			}
		}
		synchronized(this)
		{
			endGame();
			getResults();
		}
	}
	@Override
	public void pass() {
		facade.pass();

	}

	@Override
	public Colour[][] getTable() {
		return facade.getTable();
	}

	@Override
	public void makeMove(int x, int y) throws RepeatedSituationException, InitialBreathException, PlayerPassedException, PlaceReservedException
	{
		try
		{
			facade.makeMove(x, y);
		}
		catch(RepeatedSituationException e){throw new RepeatedSituationException();}
		catch(InitialBreathException e){throw new InitialBreathException();}
		catch(PlayerPassedException e){throw new PlayerPassedException();}
		catch(PlaceReservedException e){throw new PlaceReservedException();}

	}
	public synchronized void randomDecision()
	{
		int x,y;
		x=rn.nextInt(size);
		y=rn.nextInt(size);	
		try
		{
			makeMove(x, y);
		}
		catch(RepeatedSituationException e){ randomDecision(); }
		catch(InitialBreathException e){ randomDecision(); }
		catch(PlaceReservedException e){ randomDecision();}
		catch(PlayerPassedException e){ this.surrender = true;}//ta sytuacja nie powinna si? zdarzyc, wi?c poddaj si?
	}
	@Override
	public void setDeadSquare(int x, int y, Colour owner) throws IllegalOwnerException, GameNotEndedException
	{
		facade.setDeadSquare(x, y, owner);

	}

	@Override
	public void endGame() {
		facade.endGame();

	}
	@Override 
	public Colour onMove()
	{
		return facade.onMove();
	}
	@Override
	/**
	 * Metoda niepotrzebna dla bota
	 */
	public Report getResults()
	{
		return facade.getResults();
	}
}
