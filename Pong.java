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
	int widthC, heightC;
	boolean gameOver, enableGameOver, developer;
	int cells;
	public Panel(Pong game)
	{
		pOne = new Rack(87, 83, 100, game);
		pTwo = new Rack(KeyEvent.VK_UP, KeyEvent.VK_DOWN, 1064, game);
		ball = new Ball(-6, 6, game);//difficulty
		gameOver = false;
		developer = false;
		enableGameOver = true;
		cells = 23;
		widthC = game.WIDTH;
		heightC = game.HEIGHT-27;
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
		if(!gameOver || !enableGameOver)
		{
			pOne.update();
			pTwo.update();
			ball.update();
		}
	}

	public void keyPressed(KeyEvent e)
	{
		//System.out.println("Key Pressed! KeyCode :" + e.getKeyCode());
		if(e.getKeyCode() == KeyEvent.VK_F8)//toggle developer mode
		{
			enableGameOver = !enableGameOver;
			developer = !developer;
			if(!developer && (scoreOne >= 7 || scoreTwo >= 7))
			{
				gameOver = false;
				scoreOne = 0;
				scoreTwo = 0;
			}
		}
		if(e.getKeyCode() == KeyEvent.VK_F7)//restart game
		{
			scoreOne = 0;
			scoreTwo = 0;
			ball.setInitial();
			gameOver = false;
		}
		if(e.getKeyCode() == KeyEvent.VK_F6)ball.increaseVel(1);//increase velocity
		if(e.getKeyCode() == KeyEvent.VK_F5)ball.decreaseVel(1);//decrease velocity
		pOne.keyPressed(e.getKeyCode());
		pTwo.keyPressed(e.getKeyCode());
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
	}

	public void gameOver()
	{
		gameOver = true;
	}

	public void keyTyped(KeyEvent e)
	{
		//useless method but had to use bcus interface
	}

	public boolean[][] serialize(int cellSize)
	{
		cells = cellSize;
		boolean[][] input = new boolean[cells][cells];
		int o = 0; int p = 0;//i, j
		for(int i = 0; i < cells*((int)(widthC/cells));i += (int)(widthC/cells))
		{
			o++;
			for(int j = 0; j < cells*((int)(heightC/cells));j += (int)(heightC/cells))
			{
				p++;
				if(j != ((int)(game.HEIGHT/cells))*cells)
				{
					if((new Rectangle(i, j, (int)(widthC/cells), (int)(heightC/cells))).intersects(ball.getRect()))input[o][p] = true;
					else input[o][p] = false;
				}
			}
		}
		return null;
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		if(!developer)
		{
			g.drawLine(582,0, 582, game.HEIGHT);
			g.setFont(new Font("SansSerif", Font.PLAIN, 36));
			g.drawString("" + scoreOne,582-33, 40);
			g.drawString("" + scoreTwo,582+10, 40);
		}
		pOne.paint(g);
		pTwo.paint(g);
		ball.paint(g);
		//g.setColor(Color.BLUE);
		//g.drawRect(0, 0, 1181, 752);
		if(developer)
		{
			for(int i = 0; i < cells*((int)(widthC/cells));i += (int)(widthC/cells))
			{
				for(int j = 0; j < cells*((int)(heightC/cells));j += (int)(heightC/cells))
				{
					if(j != ((int)(game.HEIGHT/cells))*cells)
					{
						g.setColor(Color.YELLOW);
						if((new Rectangle(i, j, (int)(widthC/cells), (int)(heightC/cells))).intersects(ball.getRect()))g.fillRect(i, j, (int)(widthC/cells), (int)(heightC/cells));
						else g.drawRect(i, j, (int)(widthC/cells), (int)(heightC/cells));
					}
				}
			}
		}
		if(gameOver && enableGameOver)
		{
			g.setFont(new Font("SansSerif", Font.PLAIN, 96));
			g.drawString("GAME OVER", 285, 440);
		}
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
	}

	public void keyPressed(int kC)
	{
		//System.out.println("velocity: " + velocity);
		if(kC == up)
		{
			velocity-= 8;
			if(velocity < -8)velocity = -8;
		}
		if(kC == down)//may be redundant
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
		return new Rectangle(x, y - 140, WIDTH, HEIGHT-1);
	}

	public Rectangle getTop()
	{
		return new Rectangle(x, y-139, WIDTH , 1);
	}

	public Rectangle getBot()
	{
		return new Rectangle(x, y-39, WIDTH , 1);
	}

}

class Ball
{
	int x, y, velX, velY;
	final int WIDTH = 20;
	final int HEIGHT = 20;
	int iniX, iniY;
	final int escapeConstant = 11; 
	boolean velocityChanged = false;
	Pong game;
	public Ball(int velX, int velY, Pong game)
	{
		this.game = game;
		this.velX = velX;
		this.velY = velY;
		iniX = velX;
		iniY = velY;
		x = 572;
		y = 400;
	}

	public void paint(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.fillOval(x, y, WIDTH, HEIGHT);
		if(game.oPan.developer)
		{
			g.setColor(Color.RED);
			g.drawRect(x,y,WIDTH,HEIGHT); //used for hitbox detection
		}
	}

	public Rectangle getRect()
	{
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}

	public void update()
	{
		x += velX;
		y += velY;
		checkInitial();
		if(game.oPan.pOne.getRect().intersects(getRect()) || game.oPan.pTwo.getRect().intersects(getRect()))//if it hits a racket
		{
			//System.out.println("Collision!");
			velX = -velX;
		}
		if(game.oPan.pOne.getTop().intersects(getRect()) || game.oPan.pTwo.getTop().intersects(getRect()))
		{
			velY = -escapeConstant;
			//velX = -velX;
		}
		if(game.oPan.pOne.getBot().intersects(getRect()) || game.oPan.pTwo.getBot().intersects(getRect()))
		{
			velY = escapeConstant;
			//velX = -velX;
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

	public void increaseVel(int inc)
	{
		//System.out.println("increase");
		if(velX < 0)velX -= inc;
		else velX += inc;
		if(velY < 0)velY -= inc;
		else velY += inc;
		velocityChanged = true;
		iniY = velY;
	}

	public void decreaseVel(int inc)
	{
		//System.out.println("decrease");
		if(velX < 0)velX += inc;
		else velX -= inc;
		if(velY < 0)velY += inc;
		else velY -= inc;
		velocityChanged = true;
		iniY = velY;
	}

	public void checkInitial()
	{
		if(velY == -escapeConstant && !velocityChanged)velY = -iniY;
		if(velY == escapeConstant && !velocityChanged)velY = iniY;
		if(velocityChanged && iniY < escapeConstant && velY == -escapeConstant && velY < 0)velY = -iniY;
		if(velocityChanged && iniY < escapeConstant && velY == escapeConstant && velY > 0)velY = iniY;

	}
	//TODO Fix so that decreaseVel and increaseVel will work when vel == 13 

	public void setInitial()
	{
		x = 572;
		y = 400;
	}
}