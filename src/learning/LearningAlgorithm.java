package learning;

import learning.Policy;
import learning.LearningProblem;

/** 
 * All learning algorithms must extend this class.
 */
public abstract class LearningAlgorithm {
	
	/** Gamma value for reward discounting. (Can be changed). */
	protected double gamma = 1;

	/** Learning problem to be solved. */
	protected LearningProblem problem=null;
	
	/** Policy that will be returned as solution. */
	protected Policy policy= new Policy();
	
	/** Fixes the parameters of the algorithm*/
	public abstract void setParams(String[] args);
	
	/** This method must be implemented by each learning algorithm. Learns the 
	 *  policy and stores it in the variable policy.  */
	protected abstract void learnPolicy();	
	
	/** Learns and returns the policy.Calls the method learnPolicy() 
	 *  specific for each algorithm. */
	public Policy learnPolicy(LearningProblem problem, double gamma){
		// Resets the policy.
		policy.reset();
		// Fixes the values for both problem and gamma
		setProblem(problem);
		setGamma(gamma);
		// This is the main method that must be implemented.
		learnPolicy();
		return policy;
	}	
	
	/** Sets the problem. */
	public void setProblem(LearningProblem problem){
		this.problem = problem;
	}
	
	/** Sets the value for gamma. */
	public void setGamma(double gamma){
		this.gamma = gamma;
	}
	
	/** Creates an instance of the algorithm whose name has been passed as name */
	public static LearningAlgorithm generateAlgorithm(String algorithmName){
		try{
			@SuppressWarnings("unchecked")
			Class<LearningAlgorithm> algorithmClass = (Class<LearningAlgorithm>) Class.forName(algorithmName);
			LearningAlgorithm algorithm = algorithmClass.newInstance();
			return algorithm;
		}
		catch (Exception E){
			System.out.println("The algorithm "+algorithmName+" can't be built.");
			System.exit(-1);
		}
		return null;
	}
}
