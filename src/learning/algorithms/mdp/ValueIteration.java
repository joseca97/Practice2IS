package learning.algorithms.mdp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import learning.*;

import javax.rmi.CORBA.Util;

/** 
 * Implements the value iteration algorithm for Markov Decision Processes 
 */
public class ValueIteration extends LearningAlgorithm {
	
	/** Stores the utilities for each state */;
	private HashMap<State, Double> utilities;
	
	/** Max delta. Controls convergence.*/
	private double maxDelta = 0.01;
	
	/** 
	 * Learns the policy (notice that this method is protected, and called from the 
	 * public method learnPolicy(LearningProblem problem, double gamma) in LearningAlgorithm.
	 */
	@Override
	protected void learnPolicy() {
		// This algorithm only works for MDPs
		if (!(problem instanceof MDPLearningProblem)){
			System.out.println("The algorithm ValueIteration can not be applied to this problem (model is not visible).");
			System.exit(0);
		}

		double delta = 0;
		double diff;
		HashMap<State, Double> newUtilities = new HashMap<State, Double>();
		utilities = new HashMap<State, Double>();
		double Umax = -10000.0;

		for (State s : problem.getAllStates()){
			if (problem.isFinal(s)){
				utilities.put(s, problem.getReward(s));
				System.out.println(problem.getReward(s));
			} else
				utilities.put(s, new Double(0));
		}

		System.out.println(((1.0 - gamma) / gamma));

		do {
			delta = 0;
			for ( State s : problem.getAllStates()){


				if(problem.isFinal(s)){
					Umax = problem.getReward(s);
				}else {
					for (Action a : problem.getPossibleActions(s)) {
						double U = ((MDPLearningProblem) problem).getExpectedUtility(s, a, utilities, gamma);

						if (U > Umax)
							Umax = U;
					}
				}
				newUtilities.put(s, Umax);
				diff = Umax - utilities.get(s);

				if (Math.abs(diff) > delta)
					delta = Math.abs(diff);
				System.out.println("    " + utilities.get(s) + " pasa a ser " + Umax);
				System.out.println("Delta = "+ delta);

				Umax = -10000.0;


			}

			utilities.putAll(newUtilities);

		} while(delta > maxDelta*((1.0 - gamma)/gamma));

		// Putting the optimal policy.
		Action best = null;
		Double max = -10000.0;
		Double utility;


		for (State s : problem.getAllStates()){
			for(Action a : problem.getPossibleActions(s)){
				utility = ((MDPLearningProblem)problem).getExpectedUtility(s, a, utilities, gamma);
				if(utility > max){
					best = a;
					max = utility;
				}
			}

			policy.setAction(s, best);
			max = -10000.0;
		}



		 //****************************/
		 //
		 // TO DO
		 // 
		 // 
		 //***************************/

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
		// Prints the utilities.
		System.out.println("Value Iteration\n");
		System.out.println("Utilities");
		for (Entry<State,Double> entry: utilities.entrySet()){
			State state = entry.getKey();
			double utility = entry.getValue();
			System.out.println("\t"+state +"  ---> "+utility);
		}
		// Prints the policy
		System.out.println("\nOptimal policy");
		System.out.println(policy);
	}
	
	
	/** Main function. Allows testing the algorithm with MDPExProblem */
	public static void main(String[] args){
		LearningProblem mdp = new problems.mdpex.MDPExProblem();
		mdp.generateInstance(0,0);
		ValueIteration vi = new ValueIteration();
		vi.setProblem(mdp);
		vi.learnPolicy(mdp, 0.5);
		vi.printResults();
	
	}

}
