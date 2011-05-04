package swarmNet;

import java.util.Iterator;


public class Black2x2 extends Animal
{
	Food goalFood=null;
	Nest nest;
	int timeBetweenTrailDots=300;
	long lastDotPlaced=0;
	long goalZeroSetTime;
	int timeToSwitchToGoal1=40000;
	Animal preditor=null;
	Black2x2 goalBlack2x2; //For upgrading to 4x4
	Black4x4 goalBlack4x4; //For a 4x4 to upgrade to 6x6
	int goalBeforeInterrupted=0;
	
	public Black2x2(double x, double y, Nest nest)
	{
		super.x=x;
		super.y=y;
		this.nest=nest;
		goalZeroSetTime=System.currentTimeMillis();
		
		maxHp=10;
		setup();
	}
	
	public void doGoal()
	{
		if (goal==0)
		{
			/*scout for green 2x2 square food or dead preditors until a pheromine 
			 * is received leaving a yellow trail. If it stumbles 
			 * on a purple trail, switch to goal 4. If it can't find 
			 * food for 10 seconds, switch to goal 1. If it finds food, decrement 
			 * food's hp by 1 and switch to goal 5.*/
			
			if (System.currentTimeMillis()-goalZeroSetTime>timeToSwitchToGoal1)
			{
				goal=1;
			}
			
			
			moveRandom();
			
			//check if a purple TrailDot is within 5 pixels
			
			/*to iterate through all these pixels, select a height and find the farthest
			 * point to the left within the circle and the farthest point to the right
			 * within the circle*/
			int radius=20;
			
			for (int j=(int)(y+radius/2);j>y-radius/2;j--)
			{
				//25=(y-j)^2+bound^2
				//bound^2=25-(y-j)^2
				//bound=(25-(y-j)^2)^.5
				double bound=Math.pow(Math.pow(radius,2)-Math.pow((y-j),2),.5);
				int upperBound=(int)(x+bound);
				if (0<=j&&j<Main.sy)
				{
					for (int i=(int)(x-bound);i<upperBound;i++)
					{
						if(0<=i&&i<Main.sx)
						{
							if(singleton.foodArray[i][j]!=null)
							{
								goalFood=singleton.foodArray[i][j];
								goal=5;
							}
							else if(singleton.pixel[i][j]!=null)
							{
								if(singleton.pixel[i][j].getColor().equals("Purple"))
								{
									goal=4;
								}
							}
						}
					}
				}
			}
			
			if (System.currentTimeMillis()-lastDotPlaced>timeBetweenTrailDots)
			{
				TrailDot temp=new TrailDot(x,y,"Yellow");
				singleton.trailDots.add(temp);
				lastDotPlaced=System.currentTimeMillis();
			}
		}
		else if (goal==1)
		{
			/*sends 'upgrade pheromone' where it sprays a pheromone within a certain 
			 * radius and awaits a pheromone. If a preditor is nearby, move away from it. 
			 * If another upgrade pheromone is received, switch to goal 2.*/
			Iterator<Animal> itAnimals=singleton.animals.iterator();
			while (itAnimals.hasNext())
			{
				Animal current=itAnimals.next();
				if (current instanceof Black2x2 && current!=this)
				{
					Black2x2 receiver=(Black2x2) current;
					double deltaX=receiver.getX()-x;
					double deltaY=receiver.getY()-y;
					double mag=Math.pow(Math.pow(deltaX,2)+Math.pow(deltaY,2), .5);
					if (mag<Main.gasPheromoneRadius)
					{
						receiver.receiveFearPheromone(this);
					}
				}
			}
			
			if (preditor!=null)
			{
				if (preditor.isDestroyed())
				{
					preditor=null;
					goal=goalBeforeInterrupted;
				}
				else
				{
					double deltaX=preditor.getX()-x;
					double deltaY=Main.sy-preditor.getY()-y;
					double mag=Math.pow(Math.pow(deltaX,2)+Math.pow(deltaY,2), .5);
					if (mag>2*Main.gasPheromoneRadius)
					{
						preditor=null;
						goal=goalBeforeInterrupted;
					}
					else
					{
						double goalDir=Math.asin(deltaY/mag);
						if (preditor.getX()<=x)
						{
							goalDir=Math.PI-goalDir;
						}
						
						dir=goalDir+Math.PI;
						moveStraight();
					}
				}
			}
			else
			{
				moveRandom();
			}
			singleton.gasPheromones.add(new FearPheromone(x,y));
		}
		else if (goal==2)
		{
			/*send another 'upgrade pheromone', and move toward the sender of the 
			 * pheromone it received. When it makes physical contact with the sender, 
			 * upgrade to a black 4x4 square robot.*/
			//upgrade pheromones have been taken care of.
			if (goalBlack2x2.isDestroyed())
			{
				goal=goalBeforeInterrupted;
				doGoal();
			}
			else
			{
				double deltaX=goalBlack2x2.getX()-x;
				double deltaY=goalBlack2x2.getY()-y;
				double mag=Math.pow(Math.pow(deltaX,2)+Math.pow(deltaY,2), .5);
				if (mag<3)
				{
					goalBlack2x2.convertToBlack4x4();
					convertToBlack4x4();
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
		else if (goal==3)
		{
			/*Carry food back to the nest leaving purple dots of a 'food found 
			 * pheromone trail'. Follow a 'food seeking pheromone trail'. If a trail 
			 * ends, walk around randomly until a food seeking pheromine trail is found 
			 * or the nest is found.*/
			
			Iterator<Nest> itNests=singleton.nests.iterator();
			while (itNests.hasNext())
			{
				Nest current=itNests.next();
				double deltaX=current.getX()-x;
				double deltaY=current.getY()-y;
				double mag=Math.pow(Math.pow(deltaX,2)+Math.pow(deltaY, 2),.5);
				if (mag<Main.nestSize/2+20)
				{
					goal=6;
					nest=current;
				}
			}
			
			if(System.currentTimeMillis()-lastDotPlaced>timeBetweenTrailDots)
			{
				TrailDot temp=new TrailDot (x,y,"Purple");
				singleton.trailDots.add(temp);
				lastDotPlaced=System.currentTimeMillis();
			}
			int radius=20;
			boolean yellowFound=false;
			boolean maxAgeFound=false;
			TrailDot oldestYellow=null;
			TrailDot youngestYellow=null;
			for (int j=(int)(y+radius/2);j>y-radius/2;j--)
			{
				//25=(y-j)^2+bound^2
				//bound^2=25-(y-j)^2
				//bound=(25-(y-j)^2)^.5
				double bound=Math.pow(Math.pow(radius,2)-Math.pow((y-j),2),.5);
				int upperBound=(int)(x+bound);
				if (0<=j&&j<Main.sy)
				{
					for (int i=(int)(x-bound);i<upperBound;i++)
					{
						if(0<=i&&i<Main.sx)
						{
							if(singleton.pixel[i][j]!=null)
							{
								if(singleton.pixel[i][j].getColor().equals("Yellow"))
								{
									yellowFound=true;
									if (singleton.pixel[i][j].getAge()==Main.trailLength-1)
									{
										maxAgeFound=true;
									}
									if (oldestYellow==null)
									{
										oldestYellow=singleton.pixel[i][j];
									}
									else if (oldestYellow.getAge()<singleton.pixel[i][j].getAge())
									{
										oldestYellow=singleton.pixel[i][j];
									}
									if (youngestYellow==null)
									{
										youngestYellow=singleton.pixel[i][j];
									}
									else if (youngestYellow.getAge()>singleton.pixel[i][j].getAge())
									{
										youngestYellow=singleton.pixel[i][j];
									}
								}
							}
						}
					}
				}
			}
			if (!yellowFound)
			{
				moveRandom();
			}
			else if (!maxAgeFound)
			{
				double deltaX=oldestYellow.getX()-x;
				double deltaY=oldestYellow.getY()-y;
				double mag=Math.pow(Math.pow(deltaX,2)+Math.pow(deltaY,2),.5);
				double goalDir=Math.asin(deltaY/mag);
				if (oldestYellow.getX()<=x)
				{
					goalDir=Math.PI-goalDir;
				}
				dir=((.7+.6*Math.random())*goalDir+dir)/2;
				if (x-3<oldestYellow.getX()&&oldestYellow.getX()<x+3)
				{
					dir=goalDir;
				}
				moveStraight();
			}
			else if (youngestYellow.getAge()<Main.trailLength-10)
			{
				double deltaX=youngestYellow.getX()-x;
				double deltaY=youngestYellow.getY()-y;
				double mag=Math.pow(Math.pow(deltaX,2)+Math.pow(deltaY,2),.5);
				double goalDir=Math.asin(deltaY/mag);
				if (youngestYellow.getX()<=x)
				{
					goalDir=Math.PI-goalDir;
				}
				dir=((.7+.6*Math.random())*goalDir+dir)/2;
				if (x-3<youngestYellow.getX()&&youngestYellow.getX()<x+3)
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
		else if (goal==4)
		{
			/*Follow a 'food found pheromone trail' - move toward the oldest adjascent 
			 * dot within a certain radius (shortest path). If food is found, decrement 
			 * food's hp by 1 and switch to goal 3. If the trail ends, step off of the 
			 * trail and switch to goal 0.*/
			if (System.currentTimeMillis()-goalZeroSetTime>timeToSwitchToGoal1)
			{
				goal=1;
			}
			
			if(System.currentTimeMillis()-lastDotPlaced>timeBetweenTrailDots)
			{
				TrailDot temp=new TrailDot (x,y,"Yellow");
				singleton.trailDots.add(temp);
				lastDotPlaced=System.currentTimeMillis();
			}
			int radius=20;
			boolean purpleFound=false;
			TrailDot oldestPurple=null;
			for (int j=(int)(y+radius/2);j>y-radius/2;j--)
			{
				//25=(y-j)^2+bound^2
				//bound^2=25-(y-j)^2
				//bound=(25-(y-j)^2)^.5
				double bound=Math.pow(Math.pow(radius,2)-Math.pow((y-j),2),.5);
				int upperBound=(int)(x+bound);
				if (0<=j&&j<Main.sy)
				{
					for (int i=(int)(x-bound);i<upperBound;i++)
					{
						if(0<=i&&i<Main.sx)
						{
							if(singleton.foodArray[i][j]!=null)
							{
								goalFood=singleton.foodArray[i][j];
								goal=5;
							}
							if(singleton.pixel[i][j]!=null)
							{
								if(singleton.pixel[i][j].getColor().equals("Purple"))
								{
									purpleFound=true;
									if (oldestPurple==null)
									{
										oldestPurple=singleton.pixel[i][j];
									}
									else if (oldestPurple.getAge()<singleton.pixel[i][j].getAge())
									{
										oldestPurple=singleton.pixel[i][j];
									}
								}
							}
						}
					}
				}
			}
			if (!purpleFound)
			{
				goal=0;
			}
			else
			{
				double deltaX=oldestPurple.getX()-x;
				double deltaY=oldestPurple.getY()-y;
				double mag=Math.pow(Math.pow(deltaX,2)+Math.pow(deltaY,2),.5);
				double goalDir=Math.asin(deltaY/mag);
				if (oldestPurple.getX()<=x)
				{
					goalDir=Math.PI-goalDir;
				}
				dir=((.7+.6*Math.random())*goalDir+dir)/2;
				if (x-3<oldestPurple.getX()&&oldestPurple.getX()<x+3)
				{
					dir=goalDir;
				}
				moveStraight();
			}
		}
		else if (goal==5)
		{
			/*make the direction turn towards goalFood's location with a minimum 
			 * difference of -1, and a maximum difference of +1. When the goal is
			 * within 2 pixels, check foodArray[goalFood.getX()][goalFood.getY()]==null -
			 * it that's false damage goalFood by 1, and set goal=3*/
			if(goalFood.isDestroyed())
			{
				goalZeroSetTime=System.currentTimeMillis();
				goal=0;
			}
			
			if (System.currentTimeMillis()-lastDotPlaced>timeBetweenTrailDots)
			{
				TrailDot temp=new TrailDot(x,y,"Yellow");
				singleton.trailDots.add(temp);
				lastDotPlaced=System.currentTimeMillis();
			}
			
			double deltaX=goalFood.getX()-x;
			double deltaY=goalFood.getY()-y;
			double mag=Math.pow(Math.pow(deltaX,2)+ Math.pow(deltaY,2),.5);
			//check if it's within 2 pixels
			//ie if 4>(x-goalFood.getX())^2+(y-goalFood.getY())^2
			if (2>=mag)
			{
				if (singleton.foodArray[goalFood.getX()][goalFood.getY()]!=null)
				{
					if (damage(singleton.foodArray[goalFood.getX()][goalFood.getY()],1))
					{
						goal=3;
						System.out.println("Goal Reached");
					}
				}
			}
			else
			{
				//the food hasn't been reached yet
				
				//find the magnitude from this to the food
				
				//cos(goalDir)=deltaX/mag
				//sin(goalDir)=deltaY/mag
				double goalDir=Math.asin(deltaY/mag);
				
				if (goalFood.getX()<=x)
				{
					goalDir=Math.PI-goalDir;
				}
				
				dir=((.7+.6*Math.random())*goalDir+dir)/2;
				if (x-3<goalFood.getX()&&goalFood.getX()<x+3)
				{
					dir=goalDir;
				}
				moveStraight();
				
				
			}
		}
		else if (goal==6)
		{
			/*carry the food directly to the nest*/
			if(System.currentTimeMillis()-lastDotPlaced>timeBetweenTrailDots)
			{
				TrailDot temp=new TrailDot (x,y,"Purple");
				singleton.trailDots.add(temp);
				lastDotPlaced=System.currentTimeMillis();
			}
			
			//walk into the nest
			double deltaX=nest.getX()-x;
			double deltaY=nest.getY()-y;
			double mag=Math.pow(Math.pow(deltaX,2)+ Math.pow(deltaY,2),.5);
			
			if (mag<Main.nestSize/2)
			{
				goalZeroSetTime=System.currentTimeMillis();
				goal=0;
				System.out.println("Food returned");
				singleton.antCurrency++;
			}
			
			double goalDir=Math.asin(deltaY/mag);
			
			if (nest.getX()<=x)
			{
				goalDir=Math.PI-goalDir;
			}
			dir=((.7+.6*Math.random())*goalDir+dir)/2;
			if (x-3<nest.getX()&&nest.getX()<x+3)
			{
				dir=goalDir;
			}
			moveStraight();
		}
		else if (goal==7)
		{
			if (goalBlack4x4==null)
			{
				goal=goalBeforeInterrupted;
			}
			else if (goalBlack4x4.isDestroyed())
			{
				goal=goalBeforeInterrupted;
			}
			else
			{
				double deltaX=goalBlack4x4.getX()-x;
				double deltaY=goalBlack4x4.getY()-y;
				double mag=Math.pow(Math.pow(deltaX,2)+Math.pow(deltaY,2), .5);
				double goalDir=Math.asin(deltaY/mag);
				if (goalBlack4x4.getX()<=x)
				{
					goalDir=Math.PI-goalDir;
				}
				dir=((.7+.6*Math.random())*goalDir+dir)/2;
				if (x-3<goalBlack4x4.getX()&&goalBlack4x4.getX()<x+3)
				{
					dir=goalDir;
				}
				moveStraight();
			}
		}
	}
	public void scare(Animal preditor)
	{
		goal=1;
		this.preditor=preditor;
	}
	
	public void setDestroyed(boolean val)
	{
		destroyed=val;
	}
	
	public void receiveFearPheromone(Black2x2 goalBlack2x2)
	{
		if (goal!=2&&goal!=7)
		{
			goalBeforeInterrupted=goal;
		}
		singleton.gasPheromones.add(new AcknowlegeFearPheromonePheromone(x,y));
		goal=2;
		goalBlack2x2.receiveAcknowlegeFearPheremonePheremone(this);
		this.goalBlack2x2=goalBlack2x2;
	}
	
	public void receiveAcknowlegeFearPheremonePheremone(Black2x2 goalBlack2x2)
	{
		if (goal!=2&&goal!=7)
		{
			goalBeforeInterrupted=goal;
		}
		goal=2;
		this.goalBlack2x2=goalBlack2x2;
	}
	
	public void convertToBlack4x4()
	{
		if (!destroyed)
		{
			Black4x4 newBlack4x4=new Black4x4(x,y,(hp+goalBlack2x2.getHp())/(2*maxHp));
			singleton.upgrades.add(newBlack4x4);
			destroyed=true;
			goalBlack2x2.setDestroyed(true);
		}
	}
	
	public boolean isCarryingFood()
	{
		return goal==3||goal==6||
		(goal==1&&(goalBeforeInterrupted==3||goalBeforeInterrupted==6))||
		(goal==2&&(goalBeforeInterrupted==3||goalBeforeInterrupted==6))||
		(goal==7&&(goalBeforeInterrupted==3||goalBeforeInterrupted==6));
	}
	
	public void follow(Black4x4 target)
	{
		if (goal==1||goal==2)
		{
			goal=7;
		}
		else if (goal!=7)
		{
			goalBeforeInterrupted=goal;
			goal=7;
		}
		goalBlack4x4=target;
	}
}