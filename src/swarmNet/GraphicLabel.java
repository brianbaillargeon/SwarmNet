package swarmNet;

public class GraphicLabel
{
	int x,y;
	String text;
	String color;
	int age=0;
	int maxAge=50;
	boolean destroyed=false;
	public GraphicLabel(int x, int y, String text, String color)
	{
		this.x=x;
		this.y=y;
		this.text=text;
		this.color=color;
	}
	public void incrementAge()
	{
		age++;
		if (age>=maxAge)
		{
			destroyed=true;
		}
	}
	public boolean isDestroyed()
	{
		return destroyed;
	}
	public int getAge()
	{
		return age;
	}
	public String getText()
	{
		return text;
	}
	public String getColor()
	{
		return color;
	}
	public int getX()
	{
		return x;
	}
	public int getY()
	{
		return y;
	}
	public int getMaxAge()
	{
		return maxAge;
	}
}
