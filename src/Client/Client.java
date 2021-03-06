/*TODO
 * nazwa u?ytkownika
 * obiekt Player i komunikacja z nim
 * implementacja MouseListener
 */
package Client;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowListener;
import java.awt.geom.Line2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import GameTest.TestTools;

import javax.swing.*;

import Game.Colour;
import Game.Report;
import Message.Message;
import Server.Order;

//import Player.Player;

public class Client extends Thread
{
	public static final long serialVersionUID = 42L;
	private Window frame;
    private JLabel messageLabel;
	
	private int PORT = 8990;
    private Socket socket;
    private String login;
	private ObjectInputStream clInput;
	private ObjectOutputStream clOutput;
	private int boardSize = 19;
	private Colour myColour;
	private Phase phase;

	private TestTools tools;
	
    public Client(String serverAddress, String login) 
    {
    	this.login = login;
    	phase = Phase.FIGHT;
    	try
    	{
        	socket = new Socket(serverAddress, PORT);
			clOutput = new ObjectOutputStream(socket.getOutputStream());
			clInput = new ObjectInputStream(socket.getInputStream());
    	}
    	catch(IOException e){}
//trzeba poczekac na przeciwnika    
    	startGame();
    	frame = new Window(boardSize,login);
        run();
    }
    
    public void run()
    {
    	tools = new TestTools();
    	//czarny gracz rozpoczyna od ruchu
    	if(myColour==Colour.BLACK)
    	{
    		System.out.println(login+" Jestem graczem czarnym, rozpoczynam gr?");
			frame.setActive();// odblokuj u?ytkownikowi mo?liwo?c wykonania akcji
			waitForOrder();//czekaj na akcj? u?ytkownika
    	}
    	else
    	{
    		System.out.println(login+" Jestem graczem bia?ym, czekam");
    		frame.setOrder(Order.NOT_STARTING);//ka? bia?emu czekac
    	}
    	Message incommingMessage;
    	while(phase == Phase.FIGHT)
    	{
    		try
    		{
    			incommingMessage = (Message)clInput.readObject();//czekaj na wiadomo?c
    			//if(incommingMessage.getOrder()==Order.PLAY) tools.drawBoard(incommingMessage.getTable(), boardSize);
    			//try {sleep(40);}catch(InterruptedException e){}//rozkaz odbiera dobrze, ale dlaczego table ?le?
    			followOrders(incommingMessage);//obs?u? wiadomo?c
    		}
    		catch(IOException e){System.out.println("IOException");}
    		catch(ClassNotFoundException e){}
    	}
    	negotiate(Phase.NEGOTIATIONS_WHITE);
    	negotiate(Phase.NEGOTIATIONS_BLACK);
    }
    public static void main(String[] args) throws Exception 
    {
        String serverAddress="localhost";
        String login = "Jacek";
        JOptionPane.showInputDialog("Podaj adres serwera",serverAddress);//(args.length == 0) ? "localhost" : args[1];
        login = JOptionPane.showInputDialog("Podaj login",login);
        String[] opponent = {"Bot","Losowy przeciwnik"};
        JFrame question = new JFrame("Wyb?r przeciwnika");
        String opponentName = (String) JOptionPane.showInputDialog(question, 
                "Wybierz przeciwnika",
                "Opponent",
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                opponent, 
                opponent[1]);
        Client client;
        if(!opponentName.equals("Bot"))
        	 client = new Client(serverAddress,login);
        else//graj z botem TODO
        	;
    }
    
    /**
     * Metoda oczekuj?ca na sygna? do rozpocz?cia gry
     */
    public void startGame()
    {
    	//System.out.println("Oczekiwanie na przeciwnika");
    	try
    	{
    		Message startMessage = (Message) clInput.readObject();
    		myColour = startMessage.col;
    		System.out.println("min: START");
    		/*if(startMessage.getOrder()==Order.SIZE)//serwer pyta o rozmiar
    		{
    			String[] size = {"19x19","13x13","9x9"};
    			JFrame question = new JFrame("Wyb?r przeciwnika");
    	        String sizeName = (String) JOptionPane.showInputDialog(question, 
    	                "Wybierz rozmiar planszy",
    	                "Size",
    	                JOptionPane.QUESTION_MESSAGE, 
    	                null, 
    	                size, 
    	                size[0]);
    	        //wy?lij odpowied?
    			startGame();//czekaj na drugiego gracza
    		}*/
    	}
    	catch(IOException e){ System.out.println("IO Error");}
    	catch(ClassNotFoundException e){}
    }
    public void followOrders(Message m)
    {
    	switch(m.getOrder())
    	{
    		case TABLE:
    			System.out.println("min: TABLE");
    			frame.setTable(m.int2Col());
    			frame.refresh();
    			break;
    		case PLAY:
    			System.out.println("min: PLAY");
    			frame.setTable(m.int2Col());
    			frame.refresh();
    			System.out.print(login+" - m?j ruch:");
    			frame.setActive();// odblokuj u?ytkownikowi mo?liwo?c wykonania akcji
    			waitForOrder();//czekaj na akcj? u?ytkownika
    			break;
    		case DENY://ruch odrzucony
    			System.out.print(login+" - ruch odrzucony:");
    			frame.setActive();// powt?rz ruch
    			waitForOrder();//czekaj na akcj? u?ytkownika
    			break;
    		case NEXT_PHASE:
    			Report results = new Report(m.getX()+5, m.getY());
    			System.out.println("Nast?pna faza.");
    			frame.displayResults(results);
    			if(this.phase == Phase.FIGHT)
    			{
    				this.phase = Phase.NEGOTIATIONS_WHITE;
    				//frame.endPhase(Phase.NEGOTIATIONS_WHITE, myColour);
    			}
    			else
    			{
    				this.phase = Phase.NEGOTIATIONS_BLACK;
    				//frame.endPhase(Phase.NEGOTIATIONS_BLACK, myColour);
    			}
    			
    			break;
    		case SET_DEAD://drugi gracz ustawi? pole na martwe
    			System.out.println("min: "+Order.SET_DEAD);
    			frame.setTable(m.getTable());//zmie? kolor pola pole
    			frame.refresh();
    			boolean acceptance = frame.askForAcceptance();		//zapytaj o akceptacj?
    			Order resp = acceptance?Order.ACCEPT:Order.DENY;
    			try{clOutput.writeObject(new Message(resp));  } catch(IOException e){}
    			break;
    		case ACCEPT:
    			frame.setTable(m.getTable());//zmie? kolor pola pole
    			frame.refresh();   
    			frame.setActive();// powt?rz ruch
    			waitForOrder();//czekaj na akcj? u?ytkownika
    			break;
    		case DENY_SDS:
    			frame.setTable(m.getTable());//zmie? kolor pola pole
    			frame.refresh();   
    			frame.setActive();// powt?rz ruch
    			waitForOrder();//czekaj na akcj? u?ytkownika
    			break;
    		default:
    			break;
    	}
    }
    public void waitForOrder()
    {
    	Message letter;
    	while(frame.isActive()) //czekaj a? u?ytkownik wykona akcj?
    		try{
    			sleep(300); 
    			//System.out.print(".");
    			}catch(InterruptedException e){}
    	//System.out.println("Wykona?e? akcj?");
    	switch(frame.getOrder())
    	{
    		case PLAY:
    			letter = new Message(frame.getOrder(),frame.getCoord()[0],frame.getCoord()[1]);
    			try
    			{
    				clOutput.writeObject(letter);
    			}
    			catch(IOException e){}
    			break;
    		case PASS:
    			System.out.println(" pas");
    			letter = new Message(frame.getOrder());
    			try
    			{
    				clOutput.writeObject(letter);
    				letter = null;//uwaga tu!
    			}
    			catch(IOException e){}
    			break;
    		case SET_DEAD://ustaw martwy kamie?
    			letter = new Message(frame.getOrder(),frame.getCoord()[0],frame.getCoord()[1],phase);
    			try
    			{
    				clOutput.writeObject(letter);
    				System.out.println("mout: SET_DEAD");
    			}
    			catch(IOException e){}
    			break;
    		case END_TURN://ko?cz? swoj? kolejk? w ustawianiu martwych kamieni
    			letter = new Message(Order.END_TURN);
    			try
    			{
    				clOutput.writeObject(letter);
    				System.out.println("mout: END_TURN");
    			}
    			catch(IOException e){}
    			break;
    		default:
    			break;
    	}
    	
    }
	public void negotiate(Phase p)
	{
		
		if(p==Phase.NEGOTIATIONS_WHITE)
		{
			frame.endPhase(Phase.NEGOTIATIONS_WHITE, myColour);
			System.out.println("Faza ustawiania martwych kamieni przez bia?e");
			if(myColour == Colour.WHITE)
			{
				System.out.println("Moja faza negocjacji");
				frame.setActive();
				waitForOrder();
			}
		}
		else
		{
			frame.endPhase(Phase.NEGOTIATIONS_BLACK, myColour);
			System.out.println("Faza ustawiania martwych kamieni przez czarne");
			if(myColour == Colour.BLACK)
			{
				System.out.println("Moja faza negocjacji");
				frame.setActive();
				waitForOrder();
			}
		}
		Message m;
		while(phase == p)
		{
			waitForOrder();
			try
			{
				m = (Message)clInput.readObject();//czekaj na wiadomo?c (akceptacja, odrzucenie)
				System.out.println("min:"+m.getOrder());
				followOrders(m);
			}catch(IOException e){}catch(ClassNotFoundException e){}
		}
	}
}