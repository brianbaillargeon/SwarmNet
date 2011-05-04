package swarmNet;

public class TrailDot
{
	int age;
	double x, y;
	String color;
	Singleton singleton;
	public TrailDot(double x, double y, String color)
	{
		singleton=Singleton.getInstance();
		this.x=x;
		this.y=y;
		this.color=color;
		age=0;
		try
		{
			if (singleton.pixel[(int)x][(int)y]==null)
			{
				singleton.pixel[(int)x][(int)y]=this;
			}
			else if (!singleton.pixel[(int)x][(int)y].getColor().equals(color))
			{
				singleton.pixel[(int)x][(int)y]=this;
			}
		}
		catch(ArrayIndexOutOfBoundsException e)
		{
			
		}
	}
	
	public void incAge()
	{
		age++;
	}
	
	public int getAge()
	{
		return age;
	}
	
	public int getX()
	{
		return (int)x;
	}
	public int getY()
	{
		return (int)y;
	}
	
	public String getColor()
	{
		return color;
	}
}