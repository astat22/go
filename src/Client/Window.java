package Client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Game.Colour;
import Game.Report;
import Server.Order;

public class Window extends JFrame implements MouseListener
{
	public static final long serialVersionUID = 42L;
	private JPanel board, menu;
	private JButton passButton;
	public Colour[][] table;
	private int boardSize;
	private int[] coord;
	private Phase phase;
	private ActionListener al;

	//private boolean signalled = false;//prze³¹cznik informuj¹cy o tym, czy ruch zosta³ wykonany
	private Order order;
	/**
	 * Konstruktor do celów testowych
	 */
	public Window(){}
	/**
	 * W³aœciwy konstruktor.
	 * @param boardSize
	 * @param login
	 */
	public Window(int boardSize, String login)
	{
		this.phase = Phase.FIGHT;
		this.boardSize=boardSize;
		setSize(800,870);
		setTitle(login);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
        addMouseListener(this);
    	board = new JPanel() 
    	{
	    	public static final long serialVersionUID = 42L;
	        protected void paintComponent(Graphics g) 
	        {
	            super.paintComponent(g);
	            Graphics2D g2 = (Graphics2D) g.create(); 
	            Color background = new Color(218,165,32);
	            g2.setColor(background);
	            g2.fillRect(0,0,800,800);
	        	g2.setColor(Color.BLACK);
	        	for(int x=0;x<boardSize;x++)
	        		g2.drawLine(20+40*x, 0,20+40*x,  20+boardSize*40);
	        	for(int y=0;y<boardSize;y++)
	        		g2.drawLine(0, 20+40*y, 20+boardSize*40,20+40*y  );
	            if(table!=null)
	            {
	            	for(int x=0;x<boardSize;x++)
	            		for(int y=0;y<boardSize;y++)
	            		{
	            			if(table[x][y]==Colour.BLACK)
	            			{
	            				g2.setColor(Color.BLACK);
	            				g2.fillOval(40*x, 40*(boardSize-y-1), 40, 40);
	            			}
	            			else if(table[x][y]==Colour.WHITE)
	            			{
	            				g2.setColor(Color.WHITE);
	            				g2.fillOval(40*x,40*(boardSize-y-1), 40, 40);
	            			}
	            			else if(table[x][y]==Colour.REQUESTED)
	            			{
	            				g2.setColor(Color.ORANGE);
	            				g2.fillOval(40*x,40*(boardSize-y-1), 40, 40);
	            			}
	            			if(table[x][y]==Colour.DEAD)
	            			{
	            				g2.setColor(Color.GRAY);
	            				g2.fillOval(40*x,40*(boardSize-y-1), 40, 40);
	            			}
	            			if(table[x][y]==Colour.OB)
	            			{
	            				g2.setColor(Color.BLUE);
	            				g2.fillOval(40*x,40*(boardSize-y-1), 40, 40);
	            			}
	            			if(table[x][y]==Colour.OW)
	            			{
	            				g2.setColor(Color.YELLOW);
	            				g2.fillOval(40*x,40*(boardSize-y-1), 40, 40);
	            			}
	            		}
	            }
            	g2.dispose();
	        }
    	};
    	board.setBounds(0, 0, 800, 800);
        getContentPane().add(board);
        //setLocationRelativeTo(null);
        
        al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("pass"))
				{
					if(phase == Phase.FIGHT)
						order = Order.PASS;
					else if(phase == Phase.NEGOTIATIONS_WHITE || phase == Phase.NEGOTIATIONS_BLACK)
					{
						System.out.println("Koñczenie tury - button");
						order = Order.END_TURN;
					}
				}
				
			}
		};
		menu = new JPanel();
		menu.setBounds(0, 790, 800, 50);
        passButton = new JButton("Pass");
        passButton.setBounds(0, 0, 50, 30);
        passButton.addActionListener(al);
        passButton.setActionCommand("pass");
        menu.add(passButton);
        //menu.setSize(800, 20);
        getContentPane().add(menu);
        
        setVisible(true);
        board.repaint();
	}
	public void endPhase(Phase p, Colour c)
	{
		passButton.setText("Koniec");
		phase = p;
		System.out.println("Nowa faza: "+p);
		if(c==Colour.WHITE)
		{
			if(phase==Phase.NEGOTIATIONS_WHITE)
			{
				passButton.setEnabled(false);
			}
			else
			{
				passButton.setEnabled(true);
			}
		}
		else
		{
			if(phase==Phase.NEGOTIATIONS_WHITE)
			{
				passButton.setEnabled(true);
			}
			else
			{
				passButton.setEnabled(false);
			}
		}
	}
	/**
	 * Metoda zamieniaj¹ca wspó³rzêdne z ekranu na odpowiadaj¹ce im pole na planszy
	 * @param x
	 * @param y
	 * @return
	 */
	public int[] coord2Square(int x,int y)
	{
		int[] square = new int[2];
		for(int i=0;i<boardSize;i++)
		{
			if(x>40*i && x<=40*(i+1))
			{
				square[0] = i;
				break;
			}
		}
		for(int i=0;i<boardSize;i++)
		{
			if(y>40*i && y<=40*(i+1))
			{
				square[1] = boardSize-i;
				break;
			}
		}
		return square;
	}
	public void mouseClicked(MouseEvent evt)
    {
    	int x = evt.getX();
    	int y = evt.getY();
    	if(phase == Phase.FIGHT)
    	{
	    	if(isActive())
	    	{
	    		coord = coord2Square(x, y);
	    		System.out.println(coord[0]+" "+coord[1]);
	    		order = Order.PLAY;
	    	}
    	}
	    else
	    {
	    	coord = coord2Square(x, y);
	    	System.out.println("NEGOTIATE "+coord[0]+" "+coord[1]);
	    	order = Order.SET_DEAD;
	    }
    }
    public void mouseEntered(MouseEvent e){}
    public void mouseReleased(MouseEvent e){}
    public void mousePressed(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void setOrder(Order o)
    {
    	this.order = o;
    }
	public Order getOrder() 
	{
		return order;
	}
	public boolean isActive()
	{
		if(order==Order.NONE)
			return true;
		else
			return false;
	}
	public void setActive()
	{
		this.order = Order.NONE;
	}
	public int[] getCoord() {
		return coord;
	}
	public void setTable(Colour[][] table) 
	{
		this.table = table;
	}
	public void refresh()
	{
		board.repaint();
	}
	public boolean askForAcceptance()
	{
		JDialog.setDefaultLookAndFeelDecorated(true);
	    int response = JOptionPane.showConfirmDialog(this, "Czy akceptujesz pole jako martwe?", "PotwierdŸ",
	        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	    if (response == JOptionPane.NO_OPTION) {
	      return false;
	    } else if (response == JOptionPane.YES_OPTION) {
	      return true;
	    } else if (response == JOptionPane.CLOSED_OPTION) {
	      return true;//jeœli próbujesz uciec przed odpowiedzialnoœci¹, to tracisz!
	    }
	    return true;
	}
	public void displayResults(Report r)
	{
		JOptionPane.showMessageDialog(this,
			    "Bia³e: "+r.getWhitePoints()+" Czarne: "+r.getBlackPoints(),
			    "Wyniki",
			    JOptionPane.PLAIN_MESSAGE);
	}
}
