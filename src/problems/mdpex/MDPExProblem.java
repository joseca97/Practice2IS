package problems.mdpex;

import java.util.ArrayList;
import java.util.Collection;

import utils.Utils;

import learning.State;
import learning.Action;
import learning.StateActionTransModel;
import learning.MDPLearningProblem;

/** 
 * Implements the markov decision process used in class for the examples. 
 */
public class MDPExProblem extends MDPLearningProblem {

	// These data structures are internal for the problem, and should never be
	// accessed by the algorithms.
	
	/** States */
	private State[] states;
	
	/** The model is represented with 3-tuples storing the transition probabilities */
	private double probs[][][];// fromState, action, toState
	
	
	// These functions implement the inherited methods, and must be accessed by
	// the learning algorithms.
	
	/** Generates an instance of the problem */
	@Override
	public void generateInstance(int size, int seed) {
		// Instances do not use random numbers, and are always the same. So they are only generated once.
		if (states!=null)
			return;
		// Initializes the data structures
		states = new State[4];
		probs = new double[4][4][4];
		// Generates the four states
		for (int stateId=0;stateId<4;stateId++)
			states[stateId]= new MDPExState(stateId);
		// Transition values
		probs[0][0][0]=0.1;
		probs[0][0][1]=0.9;
		probs[0][1][2]=1;
		probs[1][0][0]=0.9;
		probs[1][0][2]=0.1;
		probs[1][1][3]=1;
		probs[2][2][2]=0.5;
		probs[2][2][3]=0.5;
		// The initial state, in case of being necessary, will be 0.
		initialState = new MDPExState(0);
	}

	/** Sets the initial state .*/
	@Override
	public void setInitialState(State state) {
		initialState = state;
	}
	
	/** Returns the random state .*/
	@Override
	public State getRandomState() {
		return states[Utils.randGen.nextInt(4)];
	}

	/** Returns a list with all the states */
	@Override
	public Collection<State> getAllStates() {
		ArrayList<State> allStates = new ArrayList<State>();
		for (int i=0;i<4;i++)
			allStates.add(states[i]);
		return allStates;
	}

	/** Whether state is final */
	@Override
	public boolean isFinal(State state) {
		return (((MDPExState)state).id()==3);
	}
	
	/** Returns the reward corresponding to a state */
	@Override
	public double getReward(State state) {
		if (((MDPExState)state).id()==3)
			return 1;
		return 0;
	}

	/** Returns the reward of a transition. For this problem, it is 0. */
	@Override
	public double getTransitionReward(State fromState, Action action,State toState) {
		return 0;
	}
	
	/** Returns the transition model for a pair state-action. */
	@Override
	public StateActionTransModel getTransitionModel(State state, Action action) {	
		int stateId = ((MDPExState)state).id();
		int actionId = Integer.parseInt(((MDPExAction)action).getId());
		
		// Determines the number of reachable states.
		int numReachableStates = 0;
		for (int i=0;i<4;i++){
			if (probs[stateId][actionId][i]>0)
				numReachableStates++;
		}
		
		// Creates the data structures with reachable states and their probabilities.
		State []  reachable = new State[numReachableStates];
		double[] probsReachable = new double[numReachableStates];
		int reachableState = 0;
		for (int i=0;i<4;i++){
			if (probs[stateId][actionId][i]>0){
				reachable[reachableState]=states[i];
				probsReachable[reachableState++]=probs[stateId][actionId][i];
			}
		}
		
		// Returns the transition model.
		return new StateActionTransModel(reachable, probsReachable);
	}

	/** Returns the action that can be applied to each state. */
	@Override
	public ArrayList<Action> getPossibleActions(State state) {
		// List of action.
		ArrayList<Action> posActions = new ArrayList<Action>();
		// Gets the id of the state
		int stateId = ((MDPExState)state).id();
		// Returns the actions depending on the state (for 0 and 1 are the same)
		switch (stateId){
			case 0:
			case 1:
				posActions.add(MDPExAction.A0);
				posActions.add(MDPExAction.A1);
				break;
			case 2:
				posActions.add(MDPExAction.A2);
		}	
		return posActions;
	}
}
