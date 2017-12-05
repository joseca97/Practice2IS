package problems.maze;

import java.util.ArrayList;
import java.util.Collection;

import learning.Action;
import learning.LearningProblem;
import learning.State;
import learning.StateActionTransModel;
import problems.ProblemVisualizable;
import problems.ProblemVisualization;
import problems.ProblemView;
import problems.maze.utils.MazePosition;
import utils.Utils;

/** 
 * Implements the maze problem.
 */
public class MazeProblem extends LearningProblem  implements ProblemVisualizable{
	
	/** Size of the problem. Default value is 10.*/
	private int size = 10;
	
	/** Maze */
	private Maze maze;
	
	/** Constructors */
	public MazeProblem(){
		this.maze = new Maze(size,0);
		initialState = new MazeState(maze.hamsterPos.getY(), maze.hamsterPos.getX());
	}
	public MazeProblem(int size){
		this.size = size;
		this.maze = new Maze(size, 0);
	}
	public MazeProblem(int size, int seed){
		this.size = size;
		this.maze = new Maze(size,seed);
		initialState = new MazeState(maze.hamsterPos.getY(), maze.hamsterPos.getX());
	}
	public MazeProblem(Maze maze){
		this.size = maze.size;
		this.maze = maze;
		initialState = new MazeState(maze.hamsterPos.getY(), maze.hamsterPos.getX());
	}	
	
	/** Generates a random instance of the problem given the seed. */
	@Override
	public void generateInstance(int size, int seed) {
		this.size = size;
		this.maze = new Maze(size,seed);
		initialState = new MazeState(maze.hamsterPos.getY(), maze.hamsterPos.getX());
	}

	/** Returns a collection with all possible states. */
	@Override
	public Collection<State> getAllStates() {
		ArrayList<State> allStates = new ArrayList<State>();
		for (int x=0; x<size; x++)
			for (int y=0;y<size;y++)
				if (maze.cells[y][x]!=Maze.WALL)
					allStates.add(new MazeState(y,x));
		return allStates;
	}	

	/** Whether the state corresponds to a final state (CAT or CHEESE).*/
	@Override
	public boolean isFinal(State state) {
		MazeState mazeState = (MazeState) state; // Casts
 		int posX = mazeState.getX();
 		int posY = mazeState.getY();
 		// True if there is a cat or there is cheese.
 		return maze.cells[posY][posX]==Maze.CAT || maze.cells[posY][posX]==Maze.CHEESE;
	}

	/** Returns a random state. */
	@Override
	public State getRandomState() {
		// Returns only positions corresponding to empty cells.
		int posX, posY;
		do{
			posX = Utils.randGen.nextInt(size);
			posY = Utils.randGen.nextInt(size);
		} while (maze.cells[posY][posX]!=Maze.EMPTY);
		return new MazeState(posY,posX);
	}
	
	/** Returns the set of actions that can be done at each step. */
	@Override
	public ArrayList<Action> getPossibleActions(State state) {
		MazeState mazeState = (MazeState) state;
		ArrayList<Action> possibleActions = new ArrayList<Action>();
		int stateX = mazeState.getX();
		int stateY = mazeState.getY();
		
		// Adds the actions. The agent can move to any position inside the maze if there is no wall on it.
		if ((stateX<maze.size-1) && (maze.cells[stateY][stateX+1]!=Maze.WALL))
				possibleActions.add(MazeAction.RIGHT);
		
		if ((stateX>0) && (maze.cells[stateY][stateX-1]!=Maze.WALL))		
				possibleActions.add(MazeAction.LEFT);

		if ((stateY<maze.size-1) && (maze.cells[stateY+1][stateX]!=Maze.WALL))		
				possibleActions.add(MazeAction.DOWN);
		
		if ((stateY>0) && (maze.cells[stateY-1][stateX]!=Maze.WALL))		
				possibleActions.add(MazeAction.UP);	
		
		// If there is a hole, the agent can dive. 
		if (maze.cells[stateY][stateX]==Maze.HOLE)
				possibleActions.add(MazeAction.DIVE);
		
		// Returns the actions.
		return possibleActions;
	}

	/** 
	 * Returns the state resulting of applying an action to a certain state. 
	 * Note that the maze transition model is hidden.
	 */ 
	@Override
	public State applyAction(State state, Action action) {
		StateActionTransModel model = mazeTransitionModel(state, action);
		return model.genNextState();
	}	
	
	/** Returns the reward of an state. */	
	@Override
	public double getReward(State state){
		// Gets the position of the reached state
		int toX,toY;
		toX = ((MazeState) state).getX();
		toY = ((MazeState) state).getY();	
		
		// If there is a cat the reward is -100
		if (maze.cells[toY][toX]==Maze.CAT)
			return -100;
		
		// If there is cheese, the reward is 100
		if (maze.cells[toY][toX]==Maze.CHEESE)
			return 100;	
		
		// Otherwise returns 0
		return 0;
	}
	
	/**  In this case, the transition reward penalizes distance. */	
	@Override
	public double getTransitionReward(State fromState, Action action, State toState) {
		
		// Gets the position of the reached state
		int toX,toY;
		toX = ((MazeState) toState).getX();
		toY = ((MazeState) toState).getY();	

		// If there is no transition, returns 0.
		if ((fromState==null) || (toState==null))
			return 0;
		
		// First, penalizes distance
		int fromX,fromY;
		fromX = ((MazeState) fromState).getX();
		fromY = ((MazeState) fromState).getY();

		// Calculate the euclidean distance
		double distance = Math.sqrt(Math.pow(fromX-toX, 2) + Math.pow(fromY-toY, 2));
		
		// Calculates the reward.
		double reward = -1*distance;
		
		// If the current state is water, the reward doubles.
		if (maze.cells[fromY][fromX]==Maze.WATER)
			reward = reward*2;
		
		// If it is a tunnel and action is dive, runs faster.
		if (action==MazeAction.DIVE)
			reward = reward*0.5;
		
		// Returns the reward
		return reward;
	}

	
	/** Generates the transition model for a certain state in this particular problem. 
	  * Assumes that the  action can be applied. This method is private.*/
	private StateActionTransModel mazeTransitionModel(State state, Action action){
		// Structures contained by the transition model.
		State[] reachable;
		double[] probs;	
		
		// Coordinates of the current state
		int fromX,fromY;
		fromX = ((MazeState) state).getX();
		fromY = ((MazeState) state).getY();	
		
		/* First considers diving. */
		if (action==MazeAction.DIVE){
			// It must be a hole. Gets the outputs.
			MazePosition inputHolePos = new MazePosition(fromY,fromX);
			// It considers all holes but one
			reachable = new State[maze.numHoles-1];
			probs = new double[maze.numHoles-1];
			int holeNum=0;
			for (MazePosition holePos: maze.holeList){
				if (holePos.equals(inputHolePos))
					continue;
				reachable[holeNum]=new MazeState(holePos);
				probs[holeNum++] = 1.0/(maze.numHoles-1);
			}
			// Returns 
			return new StateActionTransModel(reachable, probs);
		}
		
		
		/* Otherwise it is a simple movement.*/
		
		// Considers first it must count all reachable positions.
		int numReachablePos = 0;
		if ((fromY>0) && (maze.cells[fromY-1][fromX]!=Maze.WALL)) numReachablePos++;	        //UP
		if ((fromY<maze.size-1) && (maze.cells[fromY+1][fromX]!=Maze.WALL)) numReachablePos++;  //DOWN	
		if ((fromX>0) && (maze.cells[fromY][fromX-1]!=Maze.WALL)) numReachablePos++;            //LEFT
		if ((fromX<maze.size-1) && (maze.cells[fromY][fromX+1]!=Maze.WALL)) numReachablePos++;  //RIGHT
		
		// Creates the transition model.
		reachable = new State[numReachablePos];
		probs = new double[numReachablePos];
		
		// Probability of error 0.1 times each position.
		double probError = 0.1;
		double probSuccess = 1.0 - probError*(numReachablePos-1);
		
		int ind=0;
		if ((fromY>0) && (maze.cells[fromY-1][fromX]!=Maze.WALL)) { // UP
			reachable[ind] = new MazeState(fromY-1,fromX);
			if (action==MazeAction.UP)
				probs[ind]=probSuccess;
			else
				probs[ind]=probError;
			ind++;
		}
		
		if ((fromY<maze.size-1) && (maze.cells[fromY+1][fromX]!=Maze.WALL)) { // DOWN
			reachable[ind] = new MazeState(fromY+1,fromX);
			if (action==MazeAction.DOWN)
				probs[ind]=probSuccess;
			else
				probs[ind]=probError;
			ind++;
		}
		
		if ((fromX>0) && (maze.cells[fromY][fromX-1]!=Maze.WALL)) { // LEFT
			reachable[ind] = new MazeState(fromY,fromX-1);
			if (action==MazeAction.LEFT)
				probs[ind]=probSuccess;
			else
				probs[ind]=probError;
			ind++;
		}
		
		if ((fromX<maze.size-1) && (maze.cells[fromY][fromX+1]!=Maze.WALL)) { // RIGHT
			reachable[ind] = new MazeState(fromY,fromX+1);
			if (action==MazeAction.RIGHT)
				probs[ind]=probSuccess;
			else
				probs[ind]=probError;
			ind++;
		}
		
		// Returns 
		return new StateActionTransModel(reachable, probs);
	}
	
	
	/* Visualization */
	
	/** Returns a panel with the view of the problem. */
	public ProblemView getView(int sizePx){
		MazeView mazeView = new MazeView(maze, sizePx);
		return mazeView;
	}
	
	/* Test */
	public static void main(String[] args) {
		MazeProblem mazeProblem = new MazeProblem(15, 5);
		ProblemVisualization mainWindow = new ProblemVisualization(mazeProblem,600);		// Main window
		
		State currentState = mazeProblem.getInitialState();
		System.out.println("Current state: "+currentState);
		System.out.println("Reward: "+mazeProblem.getReward(currentState));
		System.out.println("Is final: "+mazeProblem.isFinal(currentState));
		
		System.out.println("Possible actions: ");
		ArrayList<Action> actions = mazeProblem.getPossibleActions(currentState);
		for (Action action: actions)
			System.out.println("\t"+action);
		
		System.out.println("Apply action UP.\n");
		State newState = mazeProblem.applyAction(currentState, MazeAction.UP);
		System.out.println("New state: "+newState);
		System.out.println("Transition reward:"+ mazeProblem.getTransitionReward(currentState, MazeAction.UP, newState));
	}
}
