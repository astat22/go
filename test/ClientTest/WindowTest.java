package ClientTest;

import org.junit.*;

import Client.Window;
import Game.Report;


public class WindowTest 
{
	private Window win;
	@Before
	public void runBeforeTest()
	{
		win = new Window();
	}
	@Test
	public void askForAcceptanceTest()
	{
		Assert.assertTrue(win.askForAcceptance());
	}
	@Test
	public void displayResultsTest()
	{
		win.displayResults(new Report(5,10));
		Assert.assertTrue(true);
	}
}
