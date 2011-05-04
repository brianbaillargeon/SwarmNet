package swarmNet;

public class Food
{
	int x, y;
	int maxHp=10;
	int hp;
	boolean destroyed=false;
	long lastAttack=0;
	Singleton singleton;
	public Food()
	{
		singleton=Singleton.getInstance();
		singleton.foodLeft++;
	}
	
	public void setup(int x, int y)
	{
		singleton=Singleton.getInstance();
		this.x=x;
		this.y=y;
		singleton.foodArray[x][y]=this;
	}
	
	public void damage(int dam)
	{
		hp-=dam;
		if (hp<=0&&!destroyed)
		{
			singleton.foodLeft--;
			destroyed=true;
			singleton.foodArray[x][y]=null;
		}
		lastAttack=System.currentTimeMillis();
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
}