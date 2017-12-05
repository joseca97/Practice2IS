package problems.maze;

import problems.maze.utils.MazePosition;
import learning.State;

/**
 *  Represents an state, which corresponds with a position (cell) of the maze.
 */
public class MazeState extends State {
	
	/** An state is a position given by the coordinates (y,x) and the number of bites. */
	protected MazePosition position;

	/** Constructor. Receives the pair of coordinates represented by the state. */
	public MazeState(int y, int x){
		position = new MazePosition(y,x);
	}
	
	/** Constructor. Receives a MazePosition */
	public MazeState(MazePosition position){
		this.position = new MazePosition(position.getY(),position.getX());
	}	
	
	/** Returns the position represented by the state. */
	public MazePosition position(){
		return position;
	}
	
	/** Returns the coordinate y. */
	public int getY(){ 
		return position.getY(); 
	}
	
	/** Returns the coordinate x. */
	public int getX(){ 
		return position.getX(); 
	}
	
	/** 
	 * Checks if two states are similar. The method overrides the one provided by the Object class
	 * and is used by some classes in Java. For instance, the method HashSet.contains makes use of equals.
	 */
	@Override
	public boolean equals(Object anotherState){
		// If the object passed as parameter is not a state, returns false and reports an error
		if (!(anotherState instanceof MazeState)){
			System.out.println("Trying to compare two objects of different classes.");
			return false;
		}
		// If the two objects have the same class, compares their positions.
		return this.position().equals(((MazeState)anotherState).position());	
	}

	/**  Basic hashing function. Overrides the one in Object and is used in classes such as HashSet. */
	@Override
	public int hashCode(){ return position.hashCode(); }

	/**  Prints the state.*/
	public String toString(){ return position.toString();}
}
