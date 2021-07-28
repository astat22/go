package Server;

import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.net.*;

import Game.Colour;
import Message.Message;
import Player.Player;

public class Server extends Thread
{
	public static List<PlayedGame> currentGames;
	private static int port = 8990;
	private List<Socket> wSocks;//gniazda graczy
	private Socket ws,bs;
	private int players = 0;
	private boolean working=true;
	private PlayedGame newGame;
	public void run()
	{
		ServerSocket listener;
		wSocks = new ArrayList<Socket>();
			System.out.println("Go game server is running");
			try
			{
				listener = new ServerSocket(port);
				 
				while (working)
				{
					/*Tworze nowa gre, do kt�rej b�d� przypisywac graczy*/
					newGame = new PlayedGame(); 
					/* Metoda accept() czeka na nadej�cie po��czenia, 
					 * je�li ��danie takie po��czenia nadejdzie, 
					 * to zwraca nowe gniazdo s�u��ce do obs�ugi tego po��czenia.*/
					try
					{
						ws = new Socket();
						ws = listener.accept();//gniazdo bia�ego gracza
						wSocks.add(ws);
						System.out.println("Nowy bia�y gracz");
						ObjectOutputStream oos = new ObjectOutputStream(wSocks.get(players).getOutputStream());
						ObjectInputStream ois = new ObjectInputStream(wSocks.get(players).getInputStream());
						//oos.writeObject(new Message(Order.SIZE,0,0));//zapytaj o rozmiar
						//Message sm = (Message)ois.readObject();//odpowied� z rozmiarem
						
						newGame.playerWhite = new Player(wSocks.get(players),Colour.WHITE,ois,oos,19);//sm.getX());
						players++;
						/*oczekiwanie na gracza czarnego*/
						bs = new Socket();
						bs = listener.accept();
						wSocks.add(bs);
						System.out.println("Nowy czarny gracz");
						oos = new ObjectOutputStream(wSocks.get(players).getOutputStream());
						ois = new ObjectInputStream(wSocks.get(players).getInputStream());
						newGame.playerBlack = new Player(wSocks.get(players),Colour.BLACK,ois,oos,null);//fasada jest jedna dla obu graczy
						//newGame = new PlayedGame(new Player(wSocks.get(players-1),Colour.WHITE,ois,oos,19), new Player(wSocks.get(players),Colour.BLACK,ois,oos,null));
						players++;
						newGame.run();
						currentGames.add(newGame);
					}
					catch(IOException e){}
				}
				listener.close();
			}catch(IOException e){}


	}
	public static void main(String[] args) throws Exception
	{
		currentGames = new ArrayList<PlayedGame>();
		/*Tworze na podanym porcie gniazdo serwera,
		 *kt�re posiada dodatkowe funkcjonalno�ci umo�liwiaj�ce r�wnoczesn� obs�ug� wielu po��cze�.
		 */
		Server s = new Server();
		s.run();
	}
}