package swarmNet;


public class Animal
{
	long lastAttack=0;
	
	double x=0;
	double y=0;
	
	double dir=Math.PI/2; //direction
	
	int goal=0;
	int maxHp;
	int hp;
	boolean destroyed=false;
	
	Singleton singleton;
	public Animal(double x, double y)
	{
		maxHp=10;
		setup();
		
		this.x=x;
		this.y=y;
		
		singleton=Singleton.getInstance();
	}
	
	public Animal()
	{
		maxHp=10;
		setup();
		singleton=Singleton.getInstance();
	}
	
	
	public void setup()
	{
		hp=maxHp;
		goal=0;
	}
	
	public void damage(int dam)
	{
		hp-=dam;
		if (hp<=0)
		{
			destroy();
			destroyed=true;
		}
	}
	
	public boolean damage(Food food, int amount)
	{
		if (System.currentTimeMillis()-lastAttack>Main.attackTime)
		{
			food.damage(amount);
			lastAttack=System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	public boolean damage(Animal animal, int amount)
	{
		if (System.currentTimeMillis()-lastAttack>Main.attackTime)
		{
			if (animal instanceof Black2x2)
			{
				Black2x2 temp=(Black2x2)animal;
				temp.scare(this);
			}
			animal.damage(amount);
			lastAttack=System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	public boolean damage(Nest nest,int amount)
	{
		if (System.currentTimeMillis()-lastAttack>Main.attackTime)
		{
			nest.damage(amount);
			lastAttack=System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	//if they become food, this will be overridden
	public void destroy()
	{
	}
	
	public void moveStraight()
	{
		x+=Math.cos(dir);
		y+=Math.sin(dir);
		if (x<=0)
		{
			x=2;
			dir+=Math.PI;
		}
		else if (x>=Main.sx)
		{
			x=Main.sx-2;
			dir+=Math.PI;
		}
		if (y<=0)
		{
			y=2;
			dir+=Math.PI;
		}
		else if (y>=Main.sy)
		{
			y=Main.sy-2;
			dir+=Math.PI;
		}
	}
	
	public void moveRandom()
	{
		dir+=-.5+Math.random();
		moveStraight();
		if (x<=2)
		{
			x=2;
			dir+=Math.PI;
		}
		else if(x>=Main.sx)
		{
			x=Main.sx;
			dir+=Math.PI;
		}
		if(y<=2)
		{
			y=2;
			dir+=Math.PI;
		}
		else if(y>=Main.sy)
		{
			y=Main.sy;
			dir+=Math.PI;
		}
	}
	
	//this will be overridden
	public void doGoal()
	{
	}
	
	public void turnRight()
	{
		dir+=2*Math.PI/20;
	}
	public void turnLeft()
	{
		dir-=2*Math.PI/20;
	}
	
	public boolean isDestroyed()
	{
		return destroyed;
	}
	
	public int getX()
	{
		return (int)x;
	}
	public int getY()
	{
		return (int)y;
	}
	public int getHp()
	{
		return hp;
	}
	
}