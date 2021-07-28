package Player;
import Exceptions.GameNotEndedException;
import Exceptions.IllegalOwnerException;
import Exceptions.InitialBreathException;
import Exceptions.PlaceReservedException;
import Exceptions.PlayerPassedException;
import Exceptions.RepeatedSituationException;
import Game.*;
import Message.Message;
import Server.Order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.ObjectInputStream.GetField;
import java.net.Socket;

public class Player implements IPlayerToGame
{
		PlayerToGameFacade facade;
	    Player opponent;
        Socket socket;
        Colour colour;
    	public ObjectInputStream input;
    	public ObjectOutputStream output;
		private boolean gameEnded = false;
		private boolean botAsOpponent;
        int boardSize = 19;
        
        public Player(Socket socket, Colour c, ObjectInputStream input, ObjectOutputStream output,int size)
        {
            this.boardSize = size;
        	this.facade = new PlayerToGameFacade(boardSize);
            this.socket = socket;
            this.colour = c;
            this.input = input;
            this.output = output;
            this.botAsOpponent = false;
            System.out.println("Player W pomyœlnie utworzony na serwerze");
        }
        public Player(Socket socket, Colour c, ObjectInputStream input, ObjectOutputStream output, PlayerToGameFacade fac)
        {
        	this.facade = fac;
            this.socket = socket;
            this.colour = c;
            this.input = input;
            this.output = output;
            this.botAsOpponent = false;
            System.out.println("Player B pomyœlnie utworzony na serwerze");
        }
        /**
         * Konstruktor do gry z botem
         * @param c
         */
        public Player(Colour c)
        {
        	
        }
        @Override
        public void setDeadSquare(int x,int y,Colour owner) throws GameNotEndedException, IllegalOwnerException
        {
        	facade.setDeadSquare(x, y, owner);
        }
        @Override
        public Colour[][] getTable()
        {
        	return facade.getTable();
        }
        @Override
        public void endGame()
        {
        	facade.endGame();
        }
        @Override
        public Colour onMove()
        {
        	return facade.onMove();
        }
        @Override
        public void makeMove(int x,int y) throws InitialBreathException, PlaceReservedException, RepeatedSituationException, PlayerPassedException
        {
        	facade.makeMove(x, y);
        }
        @Override
        public void pass()
        {
        	facade.pass();
        }
        public void setOpponent(Player opponent) 
        {
            this.opponent = opponent;
        }
        @Override
    	public Report getResults()
    	{
        	return facade.getResults();
    	}
		public PlayerToGameFacade getFacade() {
			return facade;
		}
		public boolean isGameEnded() {
			return gameEnded;
		}
		public void setGameEnded(boolean gameEnded) {
			this.gameEnded = gameEnded;
		}
		public void setFacade(PlayerToGameFacade facade) {
			this.facade = facade;
		}
		
}
      		
  