package swarmNet;

import java.util.Iterator;

public class Black4x4 extends Animal
{
	int numCollected=0;
	Animal goalPreditor=null;
	Black2x2 goalBlack2x2=null;
	int numToConvert=4;
	public Black4x4(double x, double y)
	{
		super.x=x;
		super.y=y;
		
		maxHp=20;
		setup();
	}
	
	public Black4x4(double x, double y, double hpRatio)
	{
		this.x=x;
		this.y=y;
		maxHp=20;
		goal=0;
		hp=(int)(hpRatio*maxHp);
	}
	
	public void doGoal()
	{
		/*0: Search for 2x2 black square robots and preditors. Upon collecting 4, upgrades 
		 * to the 6x6 black square robot. If a 4x4 red preditor is nearby, switch to goal 1. 
		 * 1: Attacks 4x4 red preditors with a damage of 2. Upon destruction of a preditor, 
		 * switch to goal 0.
		 * 2: Swerve towards a Black2x2 to collect it. Once collected set hp=maxHp*/
		if (goal==0)
		{
			int radius=30;
			
			double closest2x2Mag=0;
			if (goalBlack2x2==null)
			{
				
			}
			else if (goalBlack2x2.isDestroyed())
			{
				goalBlack2x2=null;
			}
			else
			{
				closest2x2Mag=Math.pow(Math.pow(goalBlack2x2.getX()-x,2)+Math.pow(goalBlack2x2.getY()-y, 2), .5);
			}
			
			double closestPredMag=0;
			if (goalPreditor==null)
			{
				
			}
			else if (goalPreditor.isDestroyed())
			{
				goalPreditor=null;
			}
			else
			{
				closestPredMag=Math.pow(Math.pow(goalPreditor.getX()-x,2)+Math.pow(goalPreditor.getY()-y,2), .5);
			}
			
			
			Iterator<Animal> itAnimals=singleton.animals.iterator();
			while (itAnimals.hasNext())
			{
				Animal current=itAnimals.next();
				if (!current.isDestroyed())
				{
					if (current instanceof Black2x2)
					{
						Black2x2 selected=(Black2x2) current;
						double mag=Math.pow(Math.pow(current.getX()-x,2)+Math.pow(current.getY()-y,2), .5);
						if (goalBlack2x2==null)
						{
							goalBlack2x2=selected;
							closest2x2Mag=mag;
						}
						else if (mag<closest2x2Mag)
						{
							goalBlack2x2=selected;
							closest2x2Mag=mag;
						}
						if (mag<radius)
						{
							goal=2;
						}
					}
					if (current instanceof Red4x4 ||current instanceof Red6x6 || current instanceof Red8x8)
					{
						
						double mag=Math.pow(Math.pow(current.getX()-x,2)+Math.pow(current.getY()-y,2), .5);
						if (goalPreditor==null)
						{
							goalPreditor=current;
							closestPredMag=mag;
						}
						else if (mag<closestPredMag)
						{
							goalPreditor=current;
							closestPredMag=mag;
						}
						if (goal!=2 && mag<radius)
						{
							goal=1;
						}
					}
				}
			}
			moveRandom();
		}
		else if (goal==1)
		{
			//attack a goalPreditor with damage of 2
			if (goalPreditor==null)
			{
				goal=0;
			}
			else if (goalPreditor.isDestroyed())
			{
				goal=0;
			}
			else
			{
				double deltaX=goalPreditor.getX()-x;
				double deltaY=goalPreditor.getY()-y;
				double mag=Math.pow(Math.pow(deltaX,2)+Math.pow(deltaY, 2), .5);
				if (mag<5)
				{
					damage(goalPreditor,2);
					if (goalPreditor.isDestroyed())
					{
						if (goalPreditor instanceof Red4x4)
						{
							numCollected++;
						}
						else if (goalPreditor instanceof Red6x6)
						{
							numCollected+=2;
						}
						else
						{
							numCollected+=4;
						}
						
						if (numCollected==1)
						{
							singleton.graphicLabels.add(new GraphicLabel((int)x,(int)y,"1","red"));
						}
						else if (numCollected==2)
						{
							singleton.graphicLabels.add(new GraphicLabel((int)x,(int)y,"2","blue"));
						}
						else if (numCollected==3)
						{
							singleton.graphicLabels.add(new GraphicLabel((int)x,(int)y,"3","green"));
						}
						else if (numCollected>=4)
						{
							singleton.graphicLabels.add(new GraphicLabel((int)x,(int)y,"UPGRADE","yellow"));
						}
						goal=0;
						if (numCollected>=numToConvert)
						{
							convertToBlack6x6();
						}
					}
				}
				else
				{
					double goalDir=Math.asin(deltaY/mag);
					if (goalPreditor.getX()<=x)
					{
						goalDir=Math.PI-goalDir;
					}
					dir=((.7+.6*Math.random())*goalDir+dir)/2;
					if (x-3<goalPreditor.getX()&&goalPreditor.getX()<x+3)
					{
						dir=goalDir;
					}
					moveStraight();
				}
			}
		}
		else if (goal==2)
		{
			//swerve towards the black2x2
			if (goalBlack2x2==null)
			{
				goal=0;
			}
			else if (goalBlack2x2.isDestroyed())
			{
				goal=0;
			}
			else
			{
				goalBlack2x2.follow(this);
				double deltaX=goalBlack2x2.getX()-x;
				double deltaY=goalBlack2x2.getY()-y;
				double mag=Math.pow(Math.pow(deltaX,2)+Math.pow(deltaY,2), .5);
				if (mag<3)
				{
					if (goalBlack2x2.isCarryingFood())
					{
						singleton.antCurrency++;
					}
					goalBlack2x2.setDestroyed(true);
					hp=maxHp;
					numCollected++;
					if (numCollected==1)
					{
						singleton.graphicLabels.add(new GraphicLabel((int)x,(int)y,"1","red"));
					}
					else if (numCollected==2)
					{
						singleton.graphicLabels.add(new GraphicLabel((int)x,(int)y,"2","blue"));
					}
					else if (numCollected==3)
					{
						singleton.graphicLabels.add(new GraphicLabel((int)x,(int)y,"3","green"));
					}
					else if (numCollected==4)
					{
						singleton.graphicLabels.add(new GraphicLabel((int)x,(int)y,"UPGRADE","yellow"));
					}
					goal=0;
					if (numCollected==numToConvert)
					{
						convertToBlack6x6();
					}
				}
				else
				{
					double goalDir=Math.asin(deltaY/mag);
					if (goalBlack2x2.getX()<=x)
					{
						goalDir=Math.PI-goalDir;
					}
					dir=((.7+.6*Math.random())*goalDir+dir)/2;
					if (x-3<goalBlack2x2.getX()&&goalBlack2x2.getX()<x+3)
					{
						dir=goalDir;
					}
					moveStraight();
				}
			}
		}
	}
	public void convertToBlack6x6()
	{
		Black6x6 new6x6=new Black6x6(x,y,hp/maxHp);
		singleton.upgrades.add(new6x6);
		destroyed=true;
	}
}