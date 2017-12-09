package learning.algorithms.mdp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import utils.Utils;
import learning.*;

public class PolicyIteration extends LearningAlgorithm {
	/** Max delta. Controls convergence.*/
	private double maxDelta = 0.05;
	
	/** 
	 * Learns the policy (notice that this method is protected, and called from the 
	 * public method learnPolicy(LearningProblem problem, double gamma) in LearningAlgorithm.
	 */	
	@Override
	protected void learnPolicy() {
		if (!(problem instanceof MDPLearningProblem)){
			System.out.println("The algorithm PolicyIteration can not be applied to this problem (model is not visible).");
			System.exit(0);
		}
		
		// Initializes the policy randomly
	    policy = new Policy();
		Policy policyAux=null;

		for (State s : problem.getAllStates()){
			ArrayList<Action> possible = problem.getPossibleActions(s);
//			if(!possible.isEmpty())
				policy.setAction(s, possible.get(new Random().nextInt(possible.size())));
		}

		do{
			policyAux = policy;
			HashMap<State, Double> Ut = policyEvaluation(policyAux);
			policy = policyImprovement(Ut);

		}while(!policyAux.equals(policy));

		 //****************************/
		 //
		 // TO DO
		 // 
		 // 
		 //***************************/
		
		// Main loop of the policy iteration.
		 //****************************/
		 //
		 // TO DO
		 // 
		 // 
		 //***************************/
	}
		
	
	/** 
	 * Policy evaluation. Calculates the utility given the policy 
	 */
	private HashMap<State,Double> policyEvaluation(Policy policy){
		
		// Initializes utilities. In case of terminal states, the utility corresponds to
		// the reward. In the remaining (most) states, utilities are zero.		
		HashMap<State,Double> utilities = new HashMap<State,Double>();
		HashMap<State,Double> newUtilities = new HashMap<State,Double>();

		for (State s : problem.getAllStates()){
			if (problem.isFinal(s)){
				utilities.put(s, problem.getReward(s));
			} else
				utilities.put(s, new Double(0));
		}

		double delta;
		double U;

		do {
			delta = 0;

			for ( State s : problem.getAllStates()){
				U = ((MDPLearningProblem)problem).getExpectedUtility(s, policy.getAction(s), utilities, gamma);
				double dif = Math.abs(U - utilities.get(s));
				if (dif > delta){
					delta = dif;
				}
				newUtilities.put(s, U);
			}

			utilities.putAll(newUtilities);

		}while(delta > maxDelta*((1.0 - gamma)/gamma));
		 //****************************/
		 //
		 // TO DO
		 // 
		 // 
		 //***************************/
		return utilities;
	}

	/** 
	 * Improves the policy given the utility 
	 */
	private Policy policyImprovement(HashMap<State,Double> utilities){
		// Creates the new policy
		Policy newPolicy = new Policy();
		Action best = null;
		Double max = -10000.0;
		Double utility;

		for ( State s : problem.getAllStates()){
			for (Action a : problem.getPossibleActions(s)){
				utility = ((MDPLearningProblem)problem).getExpectedUtility(s, a, utilities, gamma);
				if(utility > max){
					best = a;
					max = utility;
				}
			}

			newPolicy.setAction(s, best);
			max = -10000.0;
		}

		 //****************************/
		 //
		 // TO DO
		 // 
		 // 
		 //***************************/
		return newPolicy;
	}
	
	/** 
	 * Sets the parameters of the algorithm. 
	 */
	@Override
	public void setParams(String[] args) {
		// In this case, there is only one parameter (maxDelta).
		if (args.length>0){
			try{
				maxDelta = Double.parseDouble(args[0]);
			} 
			catch(Exception e){
				System.out.println("The value for maxDelta is not correct. Using 0.01.");
			}	
		}	
	}
	
	/** Prints the results */
	public void printResults(){
		System.out.println("Policy Iteration");
		// Prints the policy
		System.out.println("\nOptimal policy");
		System.out.println(policy);
	}	
	
	/** Main function. Allows testing the algorithm with MDPExProblem */
	public static void main(String[] args){
		LearningProblem mdp = new problems.mdpex.MDPExProblem();
		mdp.generateInstance(0,0);
		PolicyIteration pi = new PolicyIteration();
		pi.setProblem(mdp);
		pi.learnPolicy(mdp, 0.5);
		pi.printResults();
	}	
	
}
