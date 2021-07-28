package GameTest;
import Game.Colour;


public class TestTools 
{
	public synchronized void drawBoard(Colour[][] board,int boardSize)
	{
		if(board!=null)
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
		else
			System.out.println("drawBoard: board=null");
	}

}
