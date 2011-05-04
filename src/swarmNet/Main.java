package swarmNet;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

//Black units are called 'Binalack' and red units are called 'Briad'
public class Main extends Applet implements MouseListener, Runnable{

	final static int sx=800;
	final static int sy=600;
	
	final static int nestSize=50;//pixel size
	
	Singleton singleton;
	
	
	int season=0;//used to increment the age of trailDots
	final static int trailLength=75;
	
	
	
	final static int attackTime=200;
	
	final static int gasPheromoneRadius=50;
	final static int maxGasPheromoneAge=50;
	
	Thread t=new Thread(this);
	
	final static int pheromoneLimit=500;
	
	Color enemyThemeColor=new Color(220,0,220);
	
	long startTime;
	long loseTime=0;
	
	public void init()
	{
		singleton=Singleton.getInstance();
		
		singleton.nests.add(new Nest(Main.nestSize/2,Main.sy/2));
		
		
		
		Red4x4 red4x4=new Red4x4(Main.sx,Main.sy/2);
		singleton.animals.add(red4x4);
		
		
		
		for (int i=0;i<Main.sx;i++)
		{
			for (int j=0;j<Main.sy;j++)
			{
				singleton.pixel[i][j]=null;
				singleton.foodArray[i][j]=null;
			}
		}
		
		
		for (int i=0;i<200;i++)
		{
			int x=(int)(Math.random()*Main.sx);
			int y=(int)(Math.random()*Main.sy);
			singleton.food.add(new Food2x2(x,y));
		}
		
		setSize(sx,sy);
		this.setPreferredSize(new Dimension(sx,sy));
		resize(sx,sy);
		
		startTime=System.currentTimeMillis();
		t.start();
		
		addMouseListener(this);
		
	}
	
	
	public void run()
	{
		boolean redFound;
		boolean blackFound;
		while(true)
		{
			long t=System.currentTimeMillis()+25;
			updateVars();
			redFound=false;
			blackFound=false;
			Iterator<Animal> itAnimals=singleton.animals.iterator();
			while (itAnimals.hasNext())
			{
				Animal current=itAnimals.next();
				if (current instanceof Red4x4||current instanceof Red6x6||current instanceof Red8x8)
				{
					redFound=true;
				}
				if (current instanceof Black2x2||current instanceof Black4x4|| current instanceof Black6x6||singleton.nests.size()>0)
				{
					blackFound=true;
				}
			}
			updateGraphics();
			if (!redFound)
			{
				if (loseTime==0)
				{
					loseTime=(System.currentTimeMillis()-startTime)/1000;
				}
				drawRedExtinct();
			}
			else if (!blackFound)
			{
				if (loseTime==0)
				{
					loseTime=(System.currentTimeMillis()-startTime)/1000;
				}
				drawBlackExtinct();
			}
			else
			{
				drawDetails();
				drawTime();
			}
			while(System.currentTimeMillis()<t)
			{
				
			}
			season++;
			if (season>4)
			{
				season=0;
			}
		}
	}
	
	public void updateVars()
	{
		
		//Make all the animals do their goals
		Iterator<Animal> itAnimals1=singleton.animals.iterator();
		while (itAnimals1.hasNext())
		{
			try
			{
				itAnimals1.next().doGoal();
			}
			catch(ConcurrentModificationException e)
			{
				System.out.println("Concurrent Modification Exception 1");
			}
		}
		
		Iterator<Animal> itAnimals2=singleton.upgrades.iterator();
		while (itAnimals2.hasNext())
		{
			singleton.animals.add(itAnimals2.next());
		}
		
		singleton.upgrades.clear();
		
		Iterator<Food> itFood=singleton.foodUpgrades.iterator();
		while (itFood.hasNext())
		{
			singleton.food.add(itFood.next());
		}
		singleton.foodUpgrades.clear();
		
		//at end remove anything that's been destroyed
		
		LinkedList<GraphicLabel> newGraphicLabels=(LinkedList<GraphicLabel>) singleton.graphicLabels.clone();
		Iterator<GraphicLabel> itGraphicLabels=singleton.graphicLabels.iterator();
		while (itGraphicLabels.hasNext())
		{
			GraphicLabel current=itGraphicLabels.next();
			current.incrementAge();
			if (current.isDestroyed())
			{
				newGraphicLabels.remove(current);
			}
		}
		singleton.graphicLabels=newGraphicLabels;
		
		LinkedList<Food> newFood=(LinkedList<Food>) singleton.food.clone();
		itFood=singleton.food.iterator();
		while(itFood.hasNext())
		{
			Food current=itFood.next();
			if(current.isDestroyed())
			{
				newFood.remove(current);
			}
		}
		singleton.food=newFood;
		
		LinkedList<GasPheromone> newGasPheromones=(LinkedList<GasPheromone>)singleton.gasPheromones.clone();
		Iterator<GasPheromone> itGasPheromones=singleton.gasPheromones.iterator();
		while (itGasPheromones.hasNext())
		{
			GasPheromone current=itGasPheromones.next();
			current.incrementAge();
			if (current.isDestroyed())
			{
				newGasPheromones.remove(current);
			}
		}
		singleton.gasPheromones=newGasPheromones;
		
		LinkedList<TrailDot> newTrailDots=(LinkedList<TrailDot>)singleton.trailDots.clone();
		Iterator<TrailDot> itTrailDots=singleton.trailDots.iterator();
		while(itTrailDots.hasNext())
		{
			TrailDot current=itTrailDots.next();
			if (season==0)
			{
				current.incAge();
			}
			if (current.getAge()>=trailLength)
			{
				newTrailDots.remove(current);
				try
				{
					singleton.pixel[current.getX()][current.getY()]=null;
				}
				catch(ArrayIndexOutOfBoundsException e)
				{
					
				}
			}
		}
		singleton.trailDots=newTrailDots;
		
		LinkedList <Animal> newAnimals=(LinkedList<Animal>)singleton.animals.clone();
		Iterator<Animal> itAnimals=singleton.animals.iterator();
		while (itAnimals.hasNext())
		{
			Animal current=itAnimals.next();
			if (current.isDestroyed())
			{
				newAnimals.remove(current);
			}
		}
		singleton.animals=newAnimals;
		
		LinkedList <Nest> newNests=(LinkedList<Nest>)singleton.nests.clone();
		Iterator<Nest> itNests=singleton.nests.iterator();
		while(itNests.hasNext())
		{
			Nest current=itNests.next();
			if(current.isDestroyed())
			{
				newNests.remove(current);
			}
		}
		singleton.nests=newNests;
		
		if (newNests.size()>0)
		{
			int randomNest=(int)(Math.random()*newNests.size());
			newNests.get(randomNest).attemptSpawnBlack2x2();
		}
		
	}
	
	public void updateGraphics()
	{
		Image offImage=createImage(sx,sy);
		Graphics g=offImage.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0,0,sx,sy);
		
		Iterator<GasPheromone> itGasPheromones=singleton.gasPheromones.iterator();
		while (itGasPheromones.hasNext())
		{
			GasPheromone current=itGasPheromones.next();
			if (current instanceof FearPheromone)
			{
				g.setColor(new Color(255,255,current.getAge()*255/maxGasPheromoneAge));
				g.fillOval(current.getX()-current.getAge()*gasPheromoneRadius/maxGasPheromoneAge, sy-current.getY()-current.getAge()*gasPheromoneRadius/maxGasPheromoneAge, 2*current.getAge()*gasPheromoneRadius/maxGasPheromoneAge, 2*current.getAge()*gasPheromoneRadius/maxGasPheromoneAge);
			}
			
		}
		itGasPheromones=singleton.gasPheromones.iterator();
		while (itGasPheromones.hasNext())
		{
			GasPheromone current=itGasPheromones.next();
			if (current instanceof AcknowlegeFearPheromonePheromone)
			{
				g.setColor(new Color (255,current.getAge()*255/maxGasPheromoneAge,255));
				g.fillOval(current.getX()-current.getAge()*gasPheromoneRadius/maxGasPheromoneAge, sy-current.getY()-current.getAge()*gasPheromoneRadius/maxGasPheromoneAge, 2*current.getAge()*gasPheromoneRadius/maxGasPheromoneAge, 2*current.getAge()*gasPheromoneRadius/maxGasPheromoneAge);
			}
		}
		
		
		
		
		
		Iterator<Nest> itNests=singleton.nests.iterator();
		g.setColor(Color.black);
		while (itNests.hasNext())
		{
			Nest current=itNests.next();
			int x=current.getX()-nestSize/2;
			int y=sy-current.getY()-nestSize/2;
			//color is already black
			g.drawOval(x,y,nestSize,nestSize);
		}
		
		
		
		
		Iterator<Food> itFood=singleton.food.iterator();
		while(itFood.hasNext())
		{
			Food current=itFood.next();
			int x=current.getX();
			int y=sy-current.getY()-2;
			if (current instanceof Food2x2)
			{
				g.setColor(new Color(150,150,50));
				g.fillRect(x,y,2,2);
			}
			else if (current instanceof Brown4x4)
			{
				
				g.setColor(new Color(200,120,0));
				g.fillRect(x,y,4,4);
			}
			else if (current instanceof Brown6x6)
			{
				g.setColor(new Color(200,120,0));
				g.fillRect(x,y,6,6);
			}
			else if (current instanceof Brown8x8)
			{
				g.setColor(new Color(200,120,0));
				g.fillRect(x,y,8,8);
			}
			
		}
		
		
		Iterator<TrailDot> itTrailDots=singleton.trailDots.iterator();
		while (itTrailDots.hasNext())
		{
			TrailDot current=itTrailDots.next();
			int x=current.getX();
			int y=sy-current.getY();
			Color c=Color.black;
			
			int shade=current.getAge()*255/trailLength;
			
			if (current.getColor()=="Yellow")
			{
				int shade2=200+current.getAge()*55/trailLength;
				c=new Color(255,shade2,shade);
			}
			else if (current.getColor()=="Purple")
			{
				c=new Color(220,shade,220);
			}
			g.setColor(c);
			g.drawLine(x,y,x,y);
		}
		
		Iterator<Animal> itAnimals=singleton.animals.iterator();
		while(itAnimals.hasNext())
		{
			Animal current=itAnimals.next();
			int x=current.getX();
			int y=sy-current.getY();
			if (current instanceof Black2x2)
			{
				y-=2;
				g.setColor(Color.black);
				g.fillRect(x,y,2,2);
			}
			else if (current instanceof Black4x4)
			{
				y-=2;
				g.setColor(Color.black);
				g.fillRect(x,y,4,4);
			}
			else if (current instanceof Black6x6)
			{
				y-=3;
				g.setColor(Color.black);
				g.fillRect(x,y,6,6);
			}
			else if (current instanceof Red4x4)
			{
				y-=2;
				g.setColor(Color.red);
				g.fillRect(x,y,4,4);
			}
			else if (current instanceof Red6x6)
			{
				y-=3;
				g.setColor(Color.red);
				g.fillRect(x,y,6,6);
			}
			else if (current instanceof Red8x8)
			{
				y-=4;
				g.setColor(Color.red);
				g.fillRect(x,y,8,8);
			}
		}
		
		Iterator<GraphicLabel> itGraphicLabels=singleton.graphicLabels.iterator();
		while (itGraphicLabels.hasNext())
		{
			GraphicLabel current=itGraphicLabels.next();
			if (current.getColor().equals("red"))
			{
				int shade=current.getAge()*255/current.getMaxAge();
				g.setColor(new Color(255,shade,shade));
			}
			else if (current.getColor().equals("blue"))
			{
				int shade=current.getAge()*255/current.getMaxAge();
				g.setColor(new Color(shade,shade,255));
			}
			else if (current.getColor().equals("green"))
			{
				int shade=current.getAge()*255/current.getMaxAge();
				g.setColor(new Color(shade, 255, shade));
			}
			else if (current.getColor().equals("yellow"))
			{
				int shade=current.getAge()*255/current.getMaxAge();
				g.setColor(new Color(255, 255, shade));
			}
			else if (current.getColor().equals("purple"))
			{
				int shade=current.getAge()*255/current.getMaxAge();
				g.setColor(new Color(255,shade,255));
			}
			else if (current.getColor().equals("magenta"))
			{
				int shade=current.getAge()*255/current.getMaxAge();
				int shade2=150+(current.getAge()*105)/current.getMaxAge();
				g.setColor(new Color(255,shade,shade2));
			}
			else if (current.getColor().equals("pink"))
			{
				int shade=125+(current.getAge()*130)/current.getMaxAge();
				g.setColor(new Color(255,shade,shade));
			}
			else if (current.getColor().equals("orange"))
			{
				int shade=100+(current.getAge()*155)/current.getMaxAge();
				int shade2=current.getAge()*255/current.getMaxAge();
				g.setColor(new Color(255,shade,shade2));
			}
			else if (current.getColor().equals("cyan"))
			{
				int shade=current.getAge()*255/current.getMaxAge();
				g.setColor(new Color(shade,255,255));
			}
			if (current.getText().equals("UPGRADE"))
			{
				g.drawString(current.getText(),current.getX()-30,sy-current.getY()-5);
			}
			else
			{
				g.drawString(current.getText(),current.getX()-2,sy-current.getY()-5);
			}
		}
		getGraphics().drawImage(offImage,0,0,this);
		
	}
	
	public void drawRedExtinct()
	{
		Graphics g=getGraphics();
		g.setColor(Color.red);
		g.drawString("The Briad are extinct", 5, sy-10);
		g.drawString("Last Briad killed at: "+loseTime,sx-124-8*((int)(Math.log10(loseTime))),sy-10);
		
	}
	public void drawBlackExtinct()
	{
		Graphics g=getGraphics();
		g.setColor(Color.black);
		g.drawString("The Binalack are extinct", 5, sy-10);
		g.drawString("Last Binalack killed at: "+loseTime,sx-142-8*((int)(Math.log10(loseTime))),sy-10);
	}
	public void drawDetails()
	{
		Graphics g=getGraphics();
		g.setColor(enemyThemeColor);
		if (singleton.antCurrency==1)
		{
			g.drawString("Enemy: 1 yum yum", 5, sy-25);
		}
		else
		{
			g.drawString("Binalack: "+singleton.antCurrency+" yum yums", 5, sy-25);
		}
		g.setColor(Color.black);
		g.drawString("Yum yum sources left: "+singleton.foodLeft, 5, sy-10);
	}
	
	public void drawTime()
	{
		Graphics g=getGraphics();
		int timeElapsed=(int)((System.currentTimeMillis()-startTime)/1000);
		g.drawString("Time Elapsed: "+timeElapsed,sx-100-8*((int)(Math.log10(timeElapsed))),sy-10);
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) 
	{
		singleton.foodUpgrades.add(new Food2x2(e.getX(),sy-e.getY()));
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
