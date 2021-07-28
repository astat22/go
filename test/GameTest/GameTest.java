package GameTest;


import org.junit.*;

import Exceptions.IllegalMoveException;
import Game.*;


public class GameTest
{
	Game game;
	int dummyGameX[] = {0,1,2,3,0,1,0,0,10};
	int dummyGameY[] = {0,0,0,0,1,1,2,3,10};
	@Before
	public void runBeforeTest()
	{
		game = new Game(19);
	}
	@Test
	public void createSquaresTest()
	{
		Assert.assertTrue("Type test", SType.SW == game.getSquareByCoordinates(0, 0).getType());
		Assert.assertTrue("Type test", SType.NE == game.getSquareByCoordinates(18, 18).getType());
		Assert.assertTrue("Type test", SType.REGULAR == game.getSquareByCoordinates(17, 17).getType());
		Assert.assertEquals("Neighbour test", null , game.getSquareByCoordinates(0, 0).getNeighbours()[1]);
		Assert.assertEquals("Neighbour test", game.getSquareByCoordinates(0, 1) , game.getSquareByCoordinates(0, 0).getNeighbours()[0]);
		Assert.assertEquals("Neighbour test", game.getSquareByCoordinates(4, 5) , game.getSquareByCoordinates(5, 5).getNeighbours()[2]);
		Assert.assertNotEquals("Neighbour test", game.getSquareByCoordinates(4, 6) , game.getSquareByCoordinates(5, 5).getNeighbours()[2]);
	}
	@Test
	public void addStoneTest()
	{
		try
		{
			for(int i=0;i<dummyGameX.length;i++)
			{
				game.addStone(dummyGameX[i], dummyGameY[i], game.getOnMove());
				game.changeColourOnMove();
			}
		}
		catch(IllegalMoveException e)
		{
			System.out.println("Nielegalny ruch!");
		}
		Assert.assertEquals(6,game.chains.size());
		Assert.assertEquals(1,game.chains.get(0).getBreaths());
		Assert.assertEquals(2,game.findChainByStone(game.getSquareByCoordinates(3, 0) ).getBreaths());
		Assert.assertEquals(1,game.findChainByStone(game.getSquareByCoordinates(2, 0) ).getBreaths());
		Assert.assertEquals(4,game.findChainByStone(game.getSquareByCoordinates(10, 10) ).getBreaths());
		Assert.assertTrue("Colour Test",game.getSquareByCoordinates(3, 0).getColour()==Colour.WHITE);
	}
	@Test
	public void removeChainFromBoardTest()
	{
		try
		{
			for(int i=0;i<dummyGameX.length;i++)
			{
				game.addStone(dummyGameX[i], dummyGameY[i], game.getOnMove());
				game.changeColourOnMove();
			}
		}
		catch(IllegalMoveException e)
		{
			System.out.println("Nielegalny ruch!");
		}
		game.removeChainFromBoard(game.findChainByStone(game.getSquareByCoordinates(3, 0)));
		Assert.assertEquals(5,game.chains.size());
		Assert.assertEquals(2,game.findChainByStone(game.getSquareByCoordinates(2, 0) ).getBreaths());
		Assert.assertTrue("Colour Test",game.getSquareByCoordinates(3, 0).getColour()==Colour.NONE);
		try
		{
			game.addStone(1, 2, game.getOnMove());//jeden ³añcuch powinien zosta zbity
			game.changeColourOnMove();
		}	catch(IllegalMoveException e){System.out.println("Nielegalny ruch!");}
		Assert.assertEquals(4,game.chains.size());
		Assert.assertTrue("Colour Test",game.getSquareByCoordinates(0, 0).getColour()==Colour.NONE);
		Assert.assertEquals(3,game.findChainByStone(game.getSquareByCoordinates(0, 3) ).getBreaths());
	}
	@Test
	public void cloneTest()
	{
		try
		{
			for(int i=0;i<dummyGameX.length;i++)
			{
				game.addStone(dummyGameX[i], dummyGameY[i], game.getOnMove());
				game.changeColourOnMove();
			}
		}
		catch(IllegalMoveException e)
		{
			System.out.println("Nielegalny ruch!");
		}
		Game g = (Game) game.clone();
		Assert.assertEquals(6,game.chains.size());
	}
}
