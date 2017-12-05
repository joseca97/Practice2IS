package learning;

import java.util.HashMap;

/** 
 * This class extends the learning problem to provide access to the underlying model
 * so that it can be solved as a MDP. 
 */
public abstract class MDPLearningProblem extends LearningProblem {
	
	/** Returns the entry of the transition model for a pair state/action */ 
	public abstract StateActionTransModel getTransitionModel(State state, Action action);
	
	
	/** Returns the expected utility for an state-action given the utilities of all states.  */
	public double getExpectedUtility(State state, Action action, HashMap<State, Double> utilities, double gamma){
		// Extracts the transition model for the state-action (reachable states and probability of reaching them).
		StateActionTransModel transModel = getTransitionModel(state, action);
		State[] reachableStates = transModel.getReachableStates();
		double[] probs = transModel.getProbs();
		
		// Calculates the expected utility. In this problem, the reward not only depends 
		// on the state, but how it has been reached.
		double utility = getReward(state); 									// U<s> = R<s>
		for (int stateIdx=0;stateIdx<reachableStates.length;stateIdx++){    
			// U<s> +=  gamma *  T<s,a,s'>  * (  R <s,a,s'> + U<s'> )
			utility +=  gamma * probs[stateIdx]*(getTransitionReward(state, action,reachableStates[stateIdx]) + utilities.get(reachableStates[stateIdx]));	
		}	 
		return utility;
	}
	
	/** Applies an action. For all MDPs, this function is common.*/
	@Override
	public State applyAction(State state, Action action) {
		StateActionTransModel model = getTransitionModel(state, action);
		return model.genNextState();
	}		
}
