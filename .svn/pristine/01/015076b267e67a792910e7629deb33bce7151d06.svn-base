package Server;

import java.io.IOException;

import javax.naming.spi.DirStateFactory.Result;

import Client.Phase;
import Exceptions.GameNotEndedException;
import Exceptions.IllegalOwnerException;
import Exceptions.InitialBreathException;
import Exceptions.PlaceReservedException;
import Exceptions.PlayerPassedException;
import Exceptions.RepeatedSituationException;
import Game.Colour;
import Game.Report;
import Message.Message;
import Player.*;

public class PlayedGame extends Thread
{
	public Player playerWhite;
	public Player playerBlack;
	public int boardSize = 19;
	private int count=0;
	private Phase phase = Phase.FIGHT;
	private PlayerToGameFacade facade;
	/*public PlayedGame(Player playerWhite, Player playerBlack)
	{
		synchronized(this)
		{
			this.playerWhite = playerWhite;
			this.playerBlack = playerBlack;
		}
		run();
	}*/
	public void run()
	{
		playerBlack.setFacade(playerWhite.getFacade());
		this.facade = playerWhite.getFacade();
		setOpponent();
		System.out.println("Rozpoczêcie nowej gry - serwer powiadamia graczy");
		noticeStart();
    	Player pom = playerOnMove();
  		Message order;
		while(!pom.isGameEnded())//inny warunek TODO
		{
			try
			{
				order = (Message) pom.input.readObject();
				followOrders(order);
			}
			catch(IOException e){}
			catch(ClassNotFoundException e){}
			if(phase == Phase.FIGHT)
				pom = playerOnMove();
			else if(phase == Phase.NEGOTIATIONS_WHITE)
				pom = playerWhite;
			else if(phase == Phase.NEGOTIATIONS_BLACK)
				pom = playerBlack;
		}
	}
	public void setOpponent()
	{
		playerBlack.setOpponent(playerWhite);
		playerWhite.setOpponent(playerBlack);
	}
    public void noticeStart()
    {
    	try
    	{
    		Message startMessageW = new Message(Order.START,Colour.WHITE);
    		Message startMessageB = new Message(Order.START,Colour.BLACK);
    		playerWhite.output.writeObject(startMessageW);
    		playerBlack.output.writeObject(startMessageB);
    		//this.run();
    	}
    	catch(IOException e){}
    }
    private Player playerOnMove()
    {
    	Colour onMove = playerBlack.onMove();
    	if(onMove==Colour.BLACK)
    		return playerBlack;
    	else
    		return playerWhite;
    }
    private Player playerNextToMove()
    {
    	Colour onMove = playerBlack.onMove();
    	if(onMove==Colour.BLACK)
    		return playerWhite;
    	else
    		return playerBlack; 	
    }
    public void followOrders(Message m)
    {
    	Player pom = playerOnMove();
    	Player np = playerNextToMove();
    	try
    	{
    		pom.output.reset();
    		np.output.reset();
    	} catch(IOException e){}
    	Message responsePOM;
    	Message responseNP;
    	switch(m.getOrder())
    	{
        	case PLAY:
        		System.out.println("min: PLAY "+m.getX()+" "+m.getY());
        		try/* jako odpowiedŸ zwró planszê*/
        		{
	        		try
	        		{
	        			pom.makeMove(m.getX(),m.getY());
	            			Colour[][] table = pom.getTable();
	            			responsePOM = new Message(Order.TABLE,table);
	            			responseNP = new Message(Order.PLAY,table);
	            			count++;
	            			pom.output.writeObject(responsePOM);
	            			np.output.writeObject(responseNP);
	            			//konieczne 
	            			//System.out.println("mout: TABLE");
	            			/*System.out.println("W grze:");
	            			facade.game.tt.drawBoard(facade.game.board, 19);
	            			System.out.println("Wysy³ana:");
	            			facade.game.tt.drawBoard(responseNP.getTable(), 19);//dzia³a*/
	            		}
	        		catch(InitialBreathException e)
	        		{
	        			System.out.println("B³¹d - brak pocz¹tkowych oddechów");
	        			responsePOM = new Message(Order.DENY);
	        			pom.output.writeObject(responsePOM);
	        		}
	        		catch(PlaceReservedException e)
	        		{
	        			System.out.println("B³¹d - miejsce zajête");
	        			responsePOM = new Message(Order.DENY);
	        			pom.output.writeObject(responsePOM);
	        		}
	        		catch(RepeatedSituationException e)
	        		{
	        			System.out.println("B³¹d - regu³a Ko");
	        			responsePOM = new Message(Order.DENY);
	        			pom.output.writeObject(responsePOM);
	        		}
	        		catch(PlayerPassedException e)
	        		{
	        			System.out.println("B³¹d - gracz zrezygnowa³ z ruchu");
	        			responsePOM = new Message(Order.DENY);
	        			pom.output.writeObject(responsePOM);
	        		}
        		}
        		catch(IOException e){}
        		break;
        	case GET_TABLE:
        		try/* jako odpowiedŸ zwró planszê*/
        		{
        			Colour[][] table = pom.getTable();
        			Message response = new Message(Order.TABLE,table,0,0,0,Colour.NONE);
        			playerBlack.output.writeObject(response);
        			playerWhite.output.writeObject(response);
        		}
        		catch(IOException e){}
        		break;
        	case PASS:// TODO Obsluzyc martwe pola
        		pom.pass();
        		System.out.println("min: PASS");
        		if(pom.getFacade().isGameEnded())
        		{
        			try
        			{
        				Report result = facade.getResults();
	        			Message response = new Message(Order.NEXT_PHASE,result.getWhitePoints(),result.getBlackPoints());
	        			phase = Phase.NEGOTIATIONS_WHITE;
	        			playerBlack.output.writeObject(response);
	        			playerWhite.output.writeObject(response);
        			}
        			catch(IOException e){ System.out.println("IOException");}
        			/*Report report = pom.getResults();
        			Message results = new Message(Order.RESULTS,report);
        			try
        			{
            			playerBlack.output.writeObject(results);
            			playerWhite.output.writeObject(results);
            		}
            		catch(IOException e){}*/
        		}
        		else
        		{
        			try
            		{
            			np.output.writeObject(new Message(Order.PLAY,facade.getTable()));
            		}
            		catch(IOException e){}
        		}
        		break;
        	case SET_DEAD:
        		Colour[][] table = pom.getTable();
        		table[m.getX()][m.getY()] = Colour.REQUESTED; //ustal jako miejsce sporne
        		responsePOM = new Message(Order.TABLE,table);//¿¹daj¹cemu wyœlij tablicê
        		responseNP = new Message(Order.SET_DEAD,table);//wyœlij tablicê do zaakceptowania
        		Phase currentPhase = m.phase;
        		negotiations(responsePOM,responseNP,currentPhase,m.getX(),m.getY());
        		break;
        	case END_TURN:
        		//TODO
        		break;
        	default:
        		break;
    	}
    }
    public void negotiations(Message pom, Message np, Phase p,int x,int y)
    {
    	Message acceptance;
    	if(p==Phase.NEGOTIATIONS_WHITE)
    	{
    		try
    		{
    			playerWhite.output.writeObject(pom);
    			playerBlack.output.writeObject(np);
    			acceptance = (Message) playerBlack.input.readObject();//czekaj na akceptacjê gracza poza ruchem
    			if(acceptance.getOrder() == Order.ACCEPT)
    			{
    				try
    				{
    					facade.setDeadSquare(x, y, Colour.BLACK);
    					playerWhite.output.writeObject(new Message(Order.ACCEPT,pom.getTable()));
    				}
    				catch(IllegalOwnerException e)
    				{
    					System.out.println("IllegalOwnerException");
    					playerWhite.output.writeObject(new Message(Order.DENY_SDS,pom.getTable()));
    				}
    				catch(GameNotEndedException e){}
    			}
    			else if(acceptance.getOrder() == Order.DENY)
    			{
    				playerWhite.output.writeObject(new Message(Order.DENY_SDS,pom.getTable()));
    			} else
    			{
    				playerWhite.output.writeObject(new Message(Order.DENY_SDS,pom.getTable()));
    				System.out.println("Nieoczekiwany rozkaz!");
    			}
    		}
    		catch(IOException e){} catch(ClassNotFoundException e){}
    	}
    	else
    	{
    		try
    		{
				playerBlack.output.writeObject(pom);
				playerWhite.output.writeObject(np);
				acceptance = (Message) playerWhite.input.readObject();//czekaj na akceptacjê gracza poza ruchem
				if(acceptance.getOrder() == Order.ACCEPT)
				{
					try
					{
						facade.setDeadSquare(x, y, Colour.WHITE);
						playerBlack.output.writeObject(new Message(Order.ACCEPT,pom.getTable()));
					}
					catch(IllegalOwnerException e)
					{
						System.out.println("IllegalOwnerException");
						playerBlack.output.writeObject(new Message(Order.DENY_SDS,pom.getTable()));
					}
					catch(GameNotEndedException e){}
				}
				else if(acceptance.getOrder() == Order.DENY)
				{
					playerBlack.output.writeObject(new Message(Order.DENY_SDS,pom.getTable()));
				} else
				{
					playerBlack.output.writeObject(new Message(Order.DENY_SDS,pom.getTable()));
					System.out.println("Nieoczekiwany rozkaz!");
				}
			}
			catch(IOException e){} catch(ClassNotFoundException e){}   		
    	}
    }
}
