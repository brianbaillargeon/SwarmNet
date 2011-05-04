package swarmNet;

import java.util.LinkedList;

public class Singleton {

	private static Singleton instance = null;
	
	LinkedList <Animal> animals;
	LinkedList <Animal> upgrades;
	LinkedList <Nest> nests;
	LinkedList <TrailDot> trailDots;
	LinkedList <GasPheromone> gasPheromones;
	LinkedList <GraphicLabel> graphicLabels;
	Food foodArray[][];
	LinkedList<Food> food;
	LinkedList<Food> foodUpgrades;
	TrailDot [][] pixel;
	
	int antCurrency=0;
	
	int foodLeft=0;
	
	protected Singleton()
	{
		pixel=new TrailDot[Main.sx][Main.sy];
		foodArray=new Food[Main.sx][Main.sy];
		food=new LinkedList<Food>();
		foodUpgrades=new LinkedList<Food>();
		animals=new LinkedList<Animal>();
		upgrades=new LinkedList<Animal>();
		nests=new LinkedList<Nest>();
		trailDots=new LinkedList<TrailDot>();
		gasPheromones=new LinkedList<GasPheromone>();
		graphicLabels=new LinkedList<GraphicLabel>();
		
		
		
		
	}
	
	static public Singleton getInstance()
	{
		if (instance==null)
		{
			instance= new Singleton();
		}
		return instance;
	}
	
}
