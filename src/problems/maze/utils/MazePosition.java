package problems.maze.utils;

/** 
 * This class is used to encapsulate pairs of (Y,X) coordinates. 
 * Makes it easier the use of some data structures, such as HashMaps or HashSets
 */
public class MazePosition{
	
	/* Coordinates. */
	protected int y;
	protected int x;
	
	/** Constructors */
	public MazePosition(){
		y=0;
		x=0;
	}
	public MazePosition(int[] position){
		y = position[0];
		x = position[1];
	}	
	public MazePosition(int y, int x){
		this.y = y;
		this.x = x;
	}	
	
	/** Returns the coordinate y. */
	public int getY(){ return y; }
	
	/** Returns the coordinate x. */
	public int getX(){ return x; }
	
	/** Returns the coordinates as a vector. */
	public int[] asVector(){
		int []coords = {y,x};
		return coords;
	}
	
	/** Hash code. */
	@Override
	public int hashCode(){
		return 10000*y + x;
	}	
	
	/** Prints the position */
	public String toString(){
		return "["+y+","+x+"]";
	}	
	
	/** Whether two positions are similar. */
	@Override
	public boolean equals(Object anotherPosition){
		// If the object passed as parameter is not a state, returns false and
		// reports an error
		if (!(anotherPosition instanceof MazePosition)){
			System.out.println("Trying to compare two objects of different classes.");
			return false;
		}
		// If the two objects have the same class, compares x and y.
		if(x!=((MazePosition)anotherPosition).getX() || y!=((MazePosition) anotherPosition).getY()) 
			return false;
		else
			return true;
	}	
}
