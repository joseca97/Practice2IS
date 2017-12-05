package learning;

import learning.State;
import learning.Action;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This abstract class must be extended by all classes implementing reinforcement learning problems.
 */
public abstract class LearningProblem{
	
	/** Generates an instance of the problem.*/
	public abstract void generateInstance(int size, int seed);
	
	
	/** In some cases, there must be an initial state. */
	protected State initialState;	
	
	/** Returns the initial state. */
	public State getInitialState() { return initialState; }
	
	/** Allows setting the initial state. Sometimes this function must be overloaded. */
	public void setInitialState(State state){ initialState = state; }
	

	/** Returns a collection with all the states of the problem (sometimes it is not possible). */
	public abstract Collection<State> getAllStates();	
	
	/** Returns true if the state is final. */
	public abstract boolean isFinal(State state);	
	
	/** Generates and returns a random state */
	public abstract State getRandomState();	
	
	
	/** Returns the set of actions that can be applied to an state */
	public abstract ArrayList<Action> getPossibleActions(State state);
	
	/** Returns the state resulting of applying an action to a certain state. */ 
	public abstract State applyAction(State state, Action action);
	
	/** Returns the reward associated to an state. */
	public abstract double getReward(State state);
	
	/**  Returns the reward resulting of applying an action to an state.	 */
	public abstract double getTransitionReward(State fromState, Action action, State toState);
	
	
	
	/** Creates an instance of the problem whose name has been passed as name. */
	public static LearningProblem generateProblem(String problemName){
		try{
			@SuppressWarnings("unchecked")
			Class<LearningProblem> problemClass = (Class<LearningProblem>) Class.forName(problemName);
			LearningProblem problem = problemClass.newInstance();
			return problem;
		}
		catch (Exception E){
			System.out.println("The problem "+problemName+" can't be built.");
			System.exit(-1);
		}
		return null;
	}
}

