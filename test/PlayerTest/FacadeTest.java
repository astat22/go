package PlayerTest;

import org.junit.*;
import org.junit.rules.ExpectedException;

import Exceptions.IllegalMoveException;
import Exceptions.InitialBreathException;
import Exceptions.PlaceReservedException;
import Exceptions.PlayerPassedException;
import Exceptions.RepeatedSituationException;
import Game.Chain;
import Game.Colour;
import Game.Square;
import Player.PlayerToGameFacade;

public class FacadeTest 
{
	int dummyGameX[] = {0,1,2,3,0,1,0,0,10,1,10,2,10};
	int dummyGameY[] = {0,0,0,0,1,1,2,3,10,2,11,1,12};
	PlayerToGameFacade facade;
	@Rule
	public final ExpectedException exception = ExpectedException.none();
	  
	@Before
	public void runBeforeTest()
	{
		facade = new PlayerToGameFacade(13);
	}
	@Test
	public void makeMoveTest()
	{
		try
		{
			for(int i=0;i<dummyGameX.length;i++)
			{
				facade.makeMove(dummyGameX[i], dummyGameY[i]);
			}
		}
		catch(IllegalMoveException e)
		{
			System.out.println("Nielegalny ruch!");
		}
		//drawBoard(facade.getTable(), 13);
		Assert.assertTrue("Colour Test",facade.getTable()[0][0]==Colour.NONE);
		
	}
	@Test 
	public void passTest()
	{		
		try
		{
			for(int i=0;i<dummyGameX.length;i++)
			{
				facade.makeMove(dummyGameX[i], dummyGameY[i]);
			}
			facade.pass();
			Assert.assertTrue("White passed",facade.game.whitePassed);
			facade.makeMove(12, 9); //facade.pass(); //
			Assert.assertTrue("White pass released",!facade.game.whitePassed);
			facade.pass();
			facade.makeMove(11, 9);
		}
		catch(PlayerPassedException e){ System.out.println("Gracz spasowa?!!");}
		catch(PlaceReservedException e){ System.out.println("Miejsce zarezerwowane");}
		catch(InitialBreathException  e){ System.out.println("Brak oddech?w");}
		catch(RepeatedSituationException e){ System.out.println("Powt?rzona sytuacja");}
		//drawBoard(facade.getTable(), 13);
	}
	@Test
	public void endGameTest() //ko?czenie gry przez podw?jne spasowanie
	{
		try
		{
			for(int i=0;i<dummyGameX.length;i++)
			{
				facade.makeMove(dummyGameX[i], dummyGameY[i]);
			}
			facade.pass();
			facade.makeMove(12, 9); //facade.pass(); //
			facade.makeMove(1, 3);
			facade.makeMove(11, 9);
			facade.pass(); 			
			facade.pass();
		}
		catch(PlayerPassedException e){ System.out.println("Gracz spasowa?!!");}
		catch(PlaceReservedException e){ System.out.println("Miejsce zarezerwowane");}
		catch(InitialBreathException  e){ System.out.println("Brak oddech?w");}
		catch(RepeatedSituationException e){ System.out.println("Powt?rzona sytuacja");}	
		Assert.assertTrue("Game ended",facade.game.doesGameEnd());
		facade.getResults();
		System.out.println("wp="+facade.game.results.whitePoints+" bp="+facade.game.results.blackPoints+" ws="+facade.game.whiteSlaves);
		//drawBoard(facade.getTable(), 13);
		Assert.assertEquals(8,facade.game.results.whitePoints);
		Assert.assertEquals(6,facade.game.results.blackPoints);
		Assert.assertEquals(4,facade.game.chains.size());
		Assert.assertEquals(4,facade.game.territories.size());

	}
	@Test//(expected=RepeatedSituationException.class)
	public void koTest()
	{
		//exception.expect(RepeatedSituationException.class);
		try
		{
			for(int i=0;i<dummyGameX.length;i++)
			{
				facade.makeMove(dummyGameX[i], dummyGameY[i]);
			}
			facade.makeMove(6, 6); 
			facade.makeMove(8, 7);
			facade.makeMove(6, 8); 
			facade.makeMove(7, 8); 
			facade.makeMove(5, 7);
			facade.makeMove(7, 6);
			facade.makeMove(7, 7);
			facade.makeMove(6, 7);
			Assert.assertTrue("Colour Test",facade.getTable()[7][7]==Colour.NONE);

		}	
		catch(PlayerPassedException e){ System.out.println("Gracz spasowa?!!");}
		catch(PlaceReservedException e){ System.out.println("Miejsce zarezerwowane");}
		catch(InitialBreathException  e){ System.out.println("Brak oddech?w");}
		catch(RepeatedSituationException e){}	
		try
		{
			facade.makeMove(7, 7);
			Assert.fail("RepeatedSituationException nie dzia?a");
		}
		catch(PlayerPassedException e){ System.out.println("Gracz spasowa?!!");}
		catch(PlaceReservedException e){ System.out.println("Miejsce zarezerwowane");}
		catch(InitialBreathException  e){ System.out.println("Brak oddech?w");}
		catch(RepeatedSituationException e){System.out.println("Powt?rzona sytuacja!");}	
		//drawBoard(facade.game.previousBoard, 13);
		drawBoard(facade.getTable(),13);//  game.board, 13);
		//printProperties();
	}
	public void drawBoard(Colour[][] board,int boardSize)
	{
		for(int i=0;i<boardSize;i++)
		{
			for(int j=0;j<boardSize;j++)
			{
				switch(board[i][j])
				{
					case NONE:
						System.out.print("_");
						break;
					case BLACK:
						System.out.print("X");
						break;
					case WHITE:
						System.out.print("O");
						break;
					default:
						break;
					
				}
			}
			System.out.print("\n");
		}
	}
	public void printProperties()
	{
		for(Chain c: facade.game.chains)
		{
			System.out.print("Oddechy="+c.getBreaths()+" Kamienie: ");
			for(Square s: c.stones)
			{
				System.out.print("("+s.getxCoord()+","+s.getyCoord()+"),");
			}
			System.out.print("S?siedzi: ");
			for(Square s: c.neighbours)
			{
				System.out.print("("+s.getxCoord()+","+s.getyCoord()+"),");
			}
			System.out.println("");
		}		
	}
}
