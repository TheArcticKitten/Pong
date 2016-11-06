import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import java.io.IOException;
import javax.swing.JPanel;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.Timer;
import java.awt.Rectangle;
import java.awt.Font;
import java.lang.InterruptedException;
public class Pong extends JFrame
{
	static final int WIDTH = 1200;
	static final int HEIGHT = 800;
	Panel oPan;
	public static void main(String[] sArgs)throws IOException
	{
		new Pong();
	}

	public Pong()
	{
		setTitle("Pong: By Noah Ortega");
		setSize(WIDTH, HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		oPan = new Panel(this);
		add(oPan);
		setBackground(Color.BLACK);
		setVisible(true);
	}
}

class Panel extends JPanel implements KeyListener, ActionListener
{
	Rack pOne, pTwo;
	int scoreOne, scoreTwo;
	Ball ball;
	Pong game;
	boolean gameOver;
	public Panel(Pong game)
	{
		pOne = new Rack(KeyEvent.VK_UP, KeyEvent.VK_DOWN, 100, game);
		pTwo = new Rack(KeyEvent.VK_NUMPAD8, KeyEvent.VK_NUMPAD2, 1064, game);
		ball = new Ball(5, 6, game);
		gameOver = false;
		scoreOne = 0;
		scoreTwo = 0;
		this.game = game;
		addKeyListener(this);
		Timer timer = new Timer(1000/60, this);
        timer.start();
		setBackground(Color.BLACK);
		setFocusable(true);
	}

	public void updatePanel()
	{
		pOne.update();
		pTwo.update();
		ball.update();
	}

	public void keyPressed(KeyEvent e)
	{
		//System.out.println("Key Pressed!");
		pOne.keyPressed(e.getKeyCode());
		pTwo.keyPressed(e.getKeyCode());
		//updatePanel();
		//repaint();
		//System.out.println("Updated!");
	}

	public void actionPerformed(ActionEvent e)
	{
		updatePanel();
		repaint();
	}

	public void keyReleased(KeyEvent e)
	{
		//System.out.println("Key Released!");
		pOne.keyReleased(e.getKeyCode());
		pTwo.keyReleased(e.getKeyCode());
		//updatePanel();
		//repaint();
	}

	public void gameOver()
	{
		gameOver = true;
	}

	public void keyTyped(KeyEvent e)
	{
		//useless method but had to use bcus interface
	}

	public void paintComponent(Graphics g)
	{
		//System.out.println("Painted!");
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		g.drawLine(582,0, 582, game.HEIGHT);
		g.setFont(new Font("SansSerif", Font.PLAIN, 36));
		g.drawString("" + scoreOne,582-33, 40);
		g.drawString("" + scoreTwo,582+10, 40);
		pOne.paint(g);
		pTwo.paint(g);
		ball.paint(g);
	}
}

class Rack
{
	final int HEIGHT = 100;
	final int WIDTH = 20;
	Pong game;
	int up, down, x, velocity;
	int y = 500;
	public Rack(int up, int down, int x, Pong game)
	{
		this.up = up;
		this.down = down;
		this.x = x;
		this.game = game;
		velocity = 0;
	}

	public void paint(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.fillRect(x, y-139, WIDTH, HEIGHT);
		//g.setColor(Color.RED);
		//g.drawRect(x,y-139,WIDTH,HEIGHT); // used for devlopment of hitbox and intersection detection
	}

	public void keyPressed(int kC)
	{
		//System.out.println("velocity: " + velocity);
		if(kC == up)
		{
			velocity-= 8;
			if(velocity < -8)velocity = -8;
		}
		if(kC == down)
		{
			velocity += 8;
			if(velocity > 8)velocity = 8;
		}
	}

	public void keyReleased(int kC)
	{
		if(kC == up || kC == down)velocity = 0;
	}

	public void update()
	{
		//System.out.println("Y: " + y + " velocity: " + velocity);
		if((y + velocity) >= 139 && (y + velocity) <= game.HEIGHT)y += velocity;
		else
		{
			if((y + velocity) <= 139)y = 139;
			else y = game.HEIGHT;
		}
	}

	public Rectangle getRect()
	{
		return new Rectangle(x, y - 139, WIDTH, HEIGHT);
	}
}

class Ball
{
	int x, y, velX, velY;
	final int WIDTH = 20;
	final int HEIGHT = 20;
	Pong game;
	public Ball(int velX, int velY, Pong game)
	{
		this.game = game;
		this.velX = velX;
		this.velY = velY;
		x = 572;
		y = 400;
	}

	public void paint(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.fillOval(x, y, WIDTH, HEIGHT);
	//	g.setColor(Color.RED);
	//	g.drawRect(x,y,WIDTH,HEIGHT); //used for hitbox detection
	}

	public void update()
	{
		x += velX;
		y += velY;
		if(game.oPan.pOne.getRect().intersects(new Rectangle(x, y, WIDTH, HEIGHT)) || game.oPan.pTwo.getRect().intersects(new Rectangle(x, y, WIDTH, HEIGHT)))//if it hits a racket
		{
			//System.out.println("Collision!");
			velX = -velX;
			//velY = -velY;
		}

		if(x < -20)//if it's completely off the screen to the left
		{
			game.oPan.scoreTwo++;
			if(game.oPan.scoreTwo == 7)game.oPan.gameOver();
			setInitial();
		}
		if(x < -20 || x > game.WIDTH -16)//if it's completely off the screen to the right
		{
			game.oPan.scoreOne++;
			if(game.oPan.scoreOne == 7)game.oPan.gameOver();
			setInitial();
		}

		if(y < 0 || y > game.HEIGHT-59)velY = -velY;//if it hits the top or bottom
	}

	public void setInitial()
	{
		x = 572;
		y = 400;
	}
}