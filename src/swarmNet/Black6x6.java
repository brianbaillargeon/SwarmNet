package swarmNet;

import java.util.Iterator;

public class Black6x6 extends Animal
{
	public Black6x6(double x, double y)
	{
		super.x=x;
		super.y=y;
		maxHp=50;
		setup();
	}
	public Black6x6(double x, double y, double hpRatio)
	{
		this.x=x;
		this.y=y;
		maxHp=50;
		goal=0;
		hp=(int)(hpRatio*maxHp);
	}
	
	public void doGoal()
	{
		/*If any preditors are within a LARGE radius, attack them with 4 damage. If there 
		 * are none, turn into a nest.*/
		int radius=100;
		Animal closest=null;
		double magClosest=0;
		Iterator<Animal> itAnimals=singleton.animals.iterator();
		boolean noPreditors=false;
		while (itAnimals.hasNext())
		{
			Animal current=itAnimals.next();
			if (current instanceof Red4x4 || current instanceof Red6x6 || current instanceof Red8x8)
			{
				if (closest==null)
				{
					closest=current;
					magClosest=Math.pow(Math.pow(closest.getX()-x, 2)+Math.pow(closest.getY()-y,2), .5);
				}
				else
				{
					
					double magCurrent=Math.pow(Math.pow(current.getX()-x, 2)+Math.pow(current.getY()-y,2), .5);
					if (magCurrent<magClosest)
					{
						closest=current;
						magClosest=Math.pow(Math.pow(closest.getX()-x, 2)+Math.pow(closest.getY()-y,2), .5);
					}
				}
			}
		}
		if (closest!=null)
		{
			if (magClosest<4)
			{
				damage(closest,4);
			}
			else if (magClosest<radius)
			{
				double deltaY=closest.getY()-y;
				double goalDir=Math.asin(deltaY/magClosest);
				if (closest.getX()<=x)
				{
					goalDir=Math.PI-goalDir;
				}
				dir=((.7+.6*Math.random())*goalDir+dir)/2;
				if (x-3<closest.getX()&&closest.getX()<x+3)
				{
					dir=goalDir;
				}
				moveStraight();
			}
			else
			{
				noPreditors=true;
			}
		}
		else
		{
			noPreditors=true;
		}
		if (noPreditors)
		{
			int nearbyFood=0;
			Iterator<Food> itFood=singleton.food.iterator();
			while (itFood.hasNext())
			{
				Food current=itFood.next();
				double mag=Math.pow(Math.pow(current.getX()-x,2)+Math.pow(current.getY()-y,2), .5);
				if (mag<2*radius/3)
				{
					nearbyFood++;
				}
			}
			
			Iterator<Nest> itNests=singleton.nests.iterator();
			double closestNest=radius;
			while (itNests.hasNext())
			{
				Nest current=itNests.next();
				double mag=Math.pow(Math.pow(current.getX()-x,2)+Math.pow(current.getY()-y,2),.5);
				if (mag<closestNest)
				{
					closestNest=mag;
				}
			}
			if (nearbyFood>=4&&closestNest>Main.nestSize)
			{
				destroyed=true;
				singleton.nests.add(new Nest((int)x,(int)y));
			}
			else
			{
				moveRandom();
			}
		}
	}
}