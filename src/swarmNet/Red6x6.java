package swarmNet;

import java.util.Iterator;

public class Red6x6 extends Animal
{
	long lastSpotted=0;//the time since this animal has spotted something it can attack
	public Red6x6(double x, double y)
	{
		super.x=x;
		super.y=y;
		maxHp=50;
		setup();
		lastAttack=System.currentTimeMillis();
	}
	public Red6x6(double x, double y, double hpRatio)
	{
		super.x=x;
		super.y=y;
		maxHp=50;
		goal=0;
		this.hp=(int)(hpRatio*maxHp);
		lastAttack=System.currentTimeMillis();
	}
	public void doGoal()
	{
		/*0: Attack the nearest 2x2 and 4x4 black square robots with a damage of 2. If 
		 * none are within a certain radius, it splits into 2 4x4 red preditors. When 
		 * destroyed it turns into 6x6 food.*/
		int radius=100;
		
		
		/*If we haven't SEEN an enemy in 10 seconds, we split into 2*/
		//this means last attack has to update if there's an enemy within radius
		if (System.currentTimeMillis()-lastAttack>10000&&System.currentTimeMillis()-lastSpotted>10000)
		{
			destroyed=true;
			singleton.upgrades.add(new Red4x4(x,y,(1.0*hp)/(1.0*maxHp)));
			singleton.upgrades.add(new Red4x4(x,y,(1.0*hp)/(1.0*maxHp)));
		}
		
		Animal closestAnimal=null;
		double closestAnimalMag=0;
		Nest closestNest=null;
		double closestNestMag=0;
		Iterator<Animal> itAnimals=singleton.animals.iterator();
		while (itAnimals.hasNext())
		{
			Animal current=itAnimals.next();
			if (current instanceof Black2x2||current instanceof Black4x4 || current instanceof Black6x6)
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
				if (!closestNest.isDestroyed())
				{
					damage(closestNest,2);
					if (closestNest.isDestroyed())
					{
						convertToRed8x8();
					}
				}
				else
				{
					moveRandom();
				}
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
		singleton.food.add(new Brown6x6((int)x,(int)y));
	}
	public void convertToRed8x8()
	{
		destroyed=true;
		Red8x8 upgrade=new Red8x8(x,y,(1.0*hp)/(maxHp*1.0));
		singleton.upgrades.add(upgrade);
	}
}
