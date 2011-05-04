package swarmNet;

import java.util.LinkedList;

public class Nest
{
	int x;
	int y;
	LinkedList<Black2x2> black2x2s;
	int hp;
	boolean destroyed=false;
	long lastSpawn=0;
	int black2x2Cost=10;
	Singleton singleton;
	public Nest(int x, int y)
	{
		singleton=Singleton.getInstance();
		hp=100;
		black2x2s=new LinkedList<Black2x2>();
		this.x=x;
		this.y=y;
		for (int i=0;i<6;i++)
		{
			Black2x2 temp=new Black2x2(x,y,this);
			black2x2s.add(temp);
			singleton.upgrades.add(temp);
		}
	}
	
	public void damage(int dam)
	{
		hp-=dam;
		if (hp<=0)
		{
			destroyed=true;
		}
	}
	
	public boolean isDestroyed()
	{
		return destroyed;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public LinkedList<Black2x2> getBlack2x2s()
	{
		return black2x2s;
	}
	
	public void attemptSpawnBlack2x2()
	{
		if (System.currentTimeMillis()-lastSpawn>=15000 &&singleton.antCurrency>=black2x2Cost)// && singleton.trailDots.size()<Main.pheromoneLimit)
		{
			Black2x2 temp=new Black2x2(x,y,this);
			black2x2s.add(temp);
			singleton.animals.add(temp);
			lastSpawn=System.currentTimeMillis();
			singleton.antCurrency-=black2x2Cost;
			System.out.println("New Black2x2");
			System.out.println("Phereomones: "+singleton.trailDots.size());
		}
	}
}
