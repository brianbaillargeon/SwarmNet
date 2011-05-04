package swarmNet;

import java.util.Iterator;



public class Red4x4 extends Animal
{
	int numThingsEaten=0;
	int radius=30;
	long lastFeast=0;
	public Red4x4(double x, double y)
	{
		super.x=x;
		super.y=y;
		
		maxHp=20;
		setup();
		lastFeast=System.currentTimeMillis();
	}
	
	public Red4x4(double x, double y, double hpRatio)
	{
		super.x=x;
		super.y=y;
		
		maxHp=20;
		hp=(int)(hpRatio*maxHp);
		goal=0;
		lastFeast=System.currentTimeMillis();
		
	}
	
	public void doGoal()
	{
		/*goals: 
		0: Does 1 damage to the nearest 2x2 square robots and sets their goal to 1. 
		If no 2x2 square robots are around it does 10 (probably should be less) damage to green 2x2 square food. 
		Upon destroying 10 things, it upgrades to the 6x6 red preditor. If no 2x2 square 
		robots or 2x2 square food is around, it attacks the nearest nest with a damage of 
		1. If it destroys a nest it upgrades to a 6x6 red preditor. When destroyed it 
		turns into 4x4 brown food.*/
		
		boolean drawText=false;
		if (System.currentTimeMillis()-lastFeast>8750)
		{
			numThingsEaten++;
			lastFeast=System.currentTimeMillis();
			drawText=true;
		}
		
		if (numThingsEaten>=10)
		{
			convertToRed6x6();
		}
		else if (goal==0)
		{
			
			Black2x2 closest=null;
			double closestMag=0;
			Food closestFood=null;
			double closestFoodMag=0;
			Nest closestNest=null;
			double closestNestMag=0;
			Iterator<Animal> itAnimals=singleton.animals.iterator();
			while (itAnimals.hasNext())
			{
				Animal current=itAnimals.next();
				if (!current.isDestroyed())
				{
					if (current instanceof Black2x2)
					{
						if (closest==null)
						{
							closest=(Black2x2)current;
							closestMag=Math.pow(Math.pow(x-closest.getX(),2)+Math.pow(y-closest.getY(),2), .5);
						}
						else
						{
							double mag=Math.pow(Math.pow(x-current.getX(), 2)+Math.pow(y-current.getY(),2),.5);
							if (mag<closestMag)
							{
								closest=(Black2x2)current;
								closestMag=mag;
								
							}
						}
					}
				}
			}
			
			Iterator<Food> itFood=singleton.food.iterator();
			while(itFood.hasNext())
			{
				Food current=itFood.next();
				if (!current.isDestroyed() && current instanceof Food2x2)
				{
					if (closestFood==null)
					{
						closestFood=current;
						closestFoodMag=Math.pow(Math.pow(x-closestFood.getX(),2)+Math.pow(y-closestFood.getY(),2),.5);
					}
					else
					{
						double mag=Math.pow(Math.pow(x-current.getX(),2)+Math.pow(y-current.getY(),2), .5);
						if (mag<closestFoodMag)
						{
							closestFood=current;
							closestFoodMag=mag;
						}
					}
				}
			}
			
			Iterator<Nest> itNests=singleton.nests.iterator();
			while (itNests.hasNext())
			{
				Nest current=itNests.next();
				if (!current.isDestroyed())
				{
					if (closestNest==null)
					{
						closestNest=current;
						closestNestMag=Math.pow(Math.pow(x-closestNest.getX(),2)+Math.pow(y-closestNest.getY(),2),.5);
					}
					else
					{
						double mag=Math.pow(Math.pow(x-current.getX(), 2)+Math.pow(y-current.getY(),2), .5);
						if (mag<closestNestMag)
						{
							closestNest=current;
							closestNestMag=mag;
						}
					}
				}
			}
		
			boolean moveToEnemy=false;
			boolean moveToFood=false;
			boolean dontMove=false;
			if (closest!=null)
			{
				if (closestFood==null||closestMag<closestFoodMag)
				{
					if (!closest.isDestroyed())
					{
						if (closestMag<3)
						{
							damage(closest,1);
							dontMove=true;
							if (closest.isDestroyed())
							{
								numThingsEaten+=2;
								drawText=true;
								lastFeast=System.currentTimeMillis();
							}
						}
						else if (closestMag<radius)
						{
							moveToEnemy=true;
						}
					}
					else
					{
						moveRandom();
					}
				}
				else
				{
					if (!closestFood.isDestroyed())
					{
						if (closestFoodMag<3)
						{
							damage(closestFood,1);
							if (closestFood.isDestroyed())
							{
								numThingsEaten++;
								drawText=true;
								lastFeast=System.currentTimeMillis();
							}
							dontMove=true;
						}
						else if (closestFoodMag<radius)
						{
							moveToFood=true;
						}
					}
					else
					{
						moveRandom();
					}
				}
			}
			else
			{
				if (closestFood!=null)
				{
					if (!closestFood.isDestroyed())
					{
						if (closestFoodMag<3)
						{
							damage(closestFood,1);
							if (closestFood.isDestroyed())
							{
								numThingsEaten++;
								drawText=true;
								lastFeast=System.currentTimeMillis();
							}
							dontMove=true;
						}
						else if (closestFoodMag<radius)
						{
							moveToFood=true;
						}
					}
				}
			}
			if (moveToEnemy)
			{
				double deltaY=closest.getY()-y;
				double goalDir=Math.asin(deltaY/closestMag);
				if (closest.getX()<=x)
				{
					goalDir=Math.PI-goalDir;
				}
				dir=goalDir-.3+.6*Math.random();
				moveStraight();
			}
			else if (moveToFood)
			{
				double deltaY=closestFood.getY()-y;
				double goalDir=Math.asin(deltaY/closestFoodMag);
				if (closestFood.getX()<=x)
				{
					goalDir=Math.PI-goalDir;
				}
				dir=((.7+.6*Math.random())*goalDir+dir)/2;
				if (x-3<closestFood.getX()&&closestFood.getX()<x+3)
				{
					dir=goalDir;
				}
				moveStraight();
			}
			else if (!dontMove)
			{
				if (closestNest!=null&&closestNestMag<Main.nestSize/2+3)
				{
					damage(closestNest,1);
					if (closestNest.isDestroyed())
					{
						convertToRed6x6();
					}
				}
				else if (closestNest!=null&&closestNestMag<radius+Main.nestSize/2)
				{
					double deltaY=closestNest.getY()-y;
					double goalDir=Math.asin(deltaY/closestNestMag);
					if (closestNest.getX()<x)
					{
						goalDir=Math.PI-goalDir;
					}
					dir=((.7+.6*Math.random())*goalDir+dir)/2;
					if (x-3>closestNest.getX()&&closestNest.getX()<x+3)
					{
						dir=goalDir;
					}
					moveStraight();
				}
				else
				{
					moveRandom();
				}
			}
		}
		if (drawText)
		{
			GraphicLabel gl=null;
			if (numThingsEaten==1)
			{
				gl=new GraphicLabel((int)x,(int)y,"1","purple");
			}
			else if (numThingsEaten==2)
			{
				gl=new GraphicLabel((int)x,(int)y,"2","magenta");
			}
			else if (numThingsEaten==3)
			{
				gl=new GraphicLabel((int)x, (int)y,"3","pink");
			}
			else if (numThingsEaten==4)
			{
				gl=new GraphicLabel((int)x,(int)y,"4","red");
			}
			else if (numThingsEaten==5)
			{
				gl=new GraphicLabel((int)x,(int)y,"5","orange");
			}
			else if (numThingsEaten==6)
			{
				gl=new GraphicLabel((int)x,(int)y,"6","yellow");
			}
			else if (numThingsEaten==7)
			{
				gl=new GraphicLabel((int)x,(int)y,"7","cyan");
			}
			else if (numThingsEaten==8)
			{
				gl=new GraphicLabel((int)x,(int)y,"8","green");
			}
			else if (numThingsEaten==9)
			{
				gl=new GraphicLabel((int)x,(int)y,"9","blue");
			}
			else if (numThingsEaten>=10)
			{
				gl=new GraphicLabel((int)x,(int)y,"UPGRADE","yellow");
			}
			singleton.graphicLabels.add(gl);
		}
	
	}
	public void convertToRed6x6()
	{
		destroyed=true;
		Red6x6 upgrade=new Red6x6(x,y,(hp*1.0)/(maxHp*1.0));
		singleton.upgrades.add(upgrade);
	}
	//recall that this is overriding a method that is called in public void damage(int)
	public void destroy()
	{
		Brown4x4 newFood=new Brown4x4((int)x,(int)y);
		singleton.food.add(newFood);
	}
}