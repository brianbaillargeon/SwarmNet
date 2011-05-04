package swarmNet;

import java.util.Iterator;



public class Red8x8 extends Animal
{
	long lastSpotted=0;
	public Red8x8(double x, double y, double hpRatio)
	{
		super.x=x;
		super.y=y;
		maxHp=400;
		goal=0;
		hp=(int)(hpRatio*maxHp);
		lastSpotted=System.currentTimeMillis();
	}
	
	public void doGoal()
	{
		/*Attacks any black unit with a damage of 2, and attacks nests with a damage of 4.
		 * If no black units are found for 20 seconds, it splits into 2 6x6 red 
		 * preditors.*/
		
		int radius=100;
		if (System.currentTimeMillis()-lastSpotted>10000&&System.currentTimeMillis()-lastAttack>10000)
		{
			destroyed=true;
			singleton.upgrades.add(new Red6x6(x,y,(1.0*hp)/(1.0*maxHp)));
			singleton.upgrades.add(new Red6x6(x,y,(1.0*hp)/(1.0*maxHp)));
		}
		Animal closestAnimal=null;
		double closestAnimalMag=0;
		Nest closestNest=null;
		double closestNestMag=0;
		Iterator<Animal> itAnimals=singleton.animals.iterator();
		while (itAnimals.hasNext())
		{
			Animal current=itAnimals.next();
			if (current instanceof Black2x2||current instanceof Black4x4||current instanceof Black6x6)
			{
				if (closestAnimal==null)
				{
					closestAnimal=current;
					closestAnimalMag=Math.pow(Math.pow(current.getX()-x,2)+Math.pow(current.getY()-y,2), .5);
				}
				else
				{
					double mag=Math.pow(Math.pow(current.getX()-x,2)+Math.pow(current.getY()-y,2), .5);
					if (mag<closestAnimalMag)
					{
						closestAnimal=current;
						closestAnimalMag=mag;
					}
				}
			}
		}
		Iterator<Nest> itNests=singleton.nests.iterator();
		while (itNests.hasNext())
		{
			Nest current=itNests.next();
			if (closestNest==null)
			{
				closestNest=current;
				closestNestMag=Math.pow(Math.pow(current.getX()-x,2)+Math.pow(current.getY()-y,2), .5);
			}
			else
			{
				double mag=Math.pow(Math.pow(current.getX()-x,2)+Math.pow(current.getY()-y,2),.5);
				if (mag<closestNestMag)
				{
					closestNest=current;
					closestNestMag=mag;
				}
			}
		}
		
		if (closestNestMag-Main.nestSize/2<closestAnimalMag&&closestNest!=null)
		{
			if (closestNestMag-Main.nestSize/2<4)
			{
				damage(closestNest,2);
			}
			else if (closestNestMag-Main.nestSize/2<radius)
			{
				double deltaY=closestNest.getY()-y;
				double goalDir=Math.asin(deltaY/closestNestMag);
				if (closestNest.getX()<=x)
				{
					goalDir=Math.PI-goalDir;
				}
				dir=((.7+.6*Math.random())*goalDir+dir)/2;
				if (x-3<closestNest.getX()&&closestNest.getX()<x+3)
				{
					dir=goalDir;
				}
				moveStraight();
				lastSpotted=System.currentTimeMillis();
			}
			else
			{
				moveRandom();
			}
		}
		else if (closestAnimalMag<4&&closestAnimal!=null)
		{
			damage(closestAnimal,2);
		}
		else if (closestAnimalMag<radius&&closestAnimal!=null)
		{
			double deltaY=closestAnimal.getY()-y;
			double goalDir=Math.asin(deltaY/closestAnimalMag);
			if (closestAnimal.getX()<=x)
			{
				goalDir=Math.PI-goalDir;
			}
			dir=goalDir;
			moveStraight();
			lastSpotted=System.currentTimeMillis();
		}
		else
		{
			moveRandom();
		}
		
	}
	public void destroy()
	{
		singleton.food.add(new Brown8x8((int)x,(int)y));
	}
}