
import learning.LearningAlgorithm;
import learning.Action;
import learning.Policy;
import learning.State;
import learning.LearningProblem;

import problems.ProblemVisualizable;
import problems.ProblemVisualization;

/** 
 * This class provides the necessary methods to evaluate the algorithms.
 */
public class Test{
	
	/* Maximum number of actions allowed. */ 
	public static int maxSteps=500;
	
	/* Number of evaluations required to evaluate a policy. */
	public static int numEvaluations;
	
	/** 
	 * Applies the policy to the problem and returns the utility.
	 */
	public static double applyPolicy(LearningProblem problem, State initialState, Policy policy, double gamma){
		State currentState, newState;
		Action nextAction;
		double utility; // Total utility
		double powGamma = 1.0; // Power of gamma.
		int step=0;
		// Fixes the initial state.
		currentState = initialState;
		// Adds R_0. In some problems, it is possible to obtain the utility of the initial state like this.
		utility = problem.getReward(currentState); 
		// While the currentState is not final moves.
		while (!problem.isFinal(currentState) && step<maxSteps){
			nextAction = policy.getAction(currentState);
			newState = problem.applyAction(currentState, nextAction);
			// System.out.println(currentState +" "+nextAction+"-->\t"+newState);
			powGamma = powGamma * gamma;
			// Adds boths de rewards of the state and transition.
			utility = utility + powGamma*(problem.getReward(newState) + problem.getTransitionReward(currentState, nextAction, newState));
			// Updates the current state.
			currentState = newState;
			
			step++;
		}
		return utility;
	}
	
	/** 
	 * Applies the policy to the problem and returns the utility.
	 */
	public static double evalPolicy(LearningProblem problem, Policy policy, double gamma){
		double averageUtility = 0;
		for (int it=0;it<numEvaluations;it++){
			State initialState = problem.getRandomState();
			averageUtility += applyPolicy(problem,initialState, policy, gamma);
		}
		return averageUtility/numEvaluations;
	}
	

	/** Main function. Allows testing the class. */
	public static void main(String[] args) {
		// Number of evaluations (used to evaluate the policy)
		Test.numEvaluations = 1000000;
		
		// Creates the problem.
		LearningProblem problem = LearningProblem.generateProblem("problems."+args[0]);
		problem.generateInstance(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
		
		// Reads the gamma parameter
		double gamma = Double.parseDouble(args[3]);
		
		// Creates the algorithm
		LearningAlgorithm algorithm =  LearningAlgorithm.generateAlgorithm("learning.algorithms."+args[4]);
		
		// Sets the parameters of the algorithm (as strings)
		String[] argsAlgorithm = new String[args.length-5];
		for (int nArg=0;nArg<argsAlgorithm.length;nArg++)
			argsAlgorithm[nArg]=args[nArg+5];
		algorithm.setParams(argsAlgorithm);
		
		// Learns the policy
		Policy policy = algorithm.learnPolicy(problem, gamma);
		System.out.println("Policy:\n"+policy);
		
		// Test the policy with the initial state provided in the algorithm. 
		// This can be useful for debugging and gain some view.
		State initialState = problem.getInitialState();
		double utility = applyPolicy(problem, initialState, policy, gamma);
		System.out.println("\nUtility starting in the initial state (one run): "+utility);
		
		// Shows the policy graphically if the problem provides this interface.
		if (problem instanceof ProblemVisualizable){
			ProblemVisualization visualization = new ProblemVisualization(problem, 600);
			visualization.visualizePolicy(policy);
		}
		
		// Evaluates the policy. Besides randomness, starts in different (random) states.
		double policyValue = evalPolicy(problem, policy, gamma);
		System.out.println("\n\nEvaluating policy: ");
		System.out.println("\t Average utility over "+Test.numEvaluations+" executions: "+policyValue);
	}

}
