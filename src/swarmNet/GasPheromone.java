package swarmNet;

public class GasPheromone
{
	int x,y,age;
	boolean destroyed=false;
	public GasPheromone()
	{
		setup(0,0);
	}
	public void setup(double x, double y)
	{
		this.x=(int)x;
		this.y=(int)y;
		age=0;
	}
	public void incrementAge()
	{
		age++;
		if (age>=Main.maxGasPheromoneAge)
		{
			destroyed=true;
		}
	}
	public int getAge()
	{
		return age;
	}
	
	public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}
	
	public boolean isDestroyed()
	{
		return destroyed;
	}
}
