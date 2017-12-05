package learning.algorithms.qlearning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import learning.Action;
import learning.LearningProblem;
import learning.State;

/** 
 * This class allows storing and managing the values for Q(s,a)
 */
public class QTable {
	
	/** Contains the main table. For each state, there is a map
	 *  with the qvalues corresponding to each action. */
	private HashMap<State, HashMap<Action, Double>> table;
	
	/** Constructor. Constructs an empty table.*/
	public QTable(){
		table = new HashMap<State, HashMap<Action, Double>>();
	}
	
	/** This constructor builds a table, assuming that all states of the problem are known. */
	public QTable(LearningProblem problem){
		// Creates the qtable.		
		table = new HashMap<State, HashMap<Action, Double>>();
		
		// Adds an empty entry for each state.
		for (State state: problem.getAllStates()){
			table.put(state,new HashMap<Action, Double>());
			// Creates the entry for each action and initializes the q(s,a) to 0
			ArrayList<Action> possibleActions = problem.getPossibleActions(state);
			for (Action action: possibleActions)
				 table.get(state).put(action, 0.0);
		}
	}
	
	/** Test whether the table contains the entries corresponding to a given state. */
	public boolean contains(State state){
		return table.containsKey(state);
	}
	
	/** Sets a value in an entry of the table: Q(state,action)=value. */
	public void setQValue(State state, Action action, double value){
		// If the table contains the state, assigns the value.
		if (table.containsKey(state))
			table.get(state).put(action, value);
		// Otherwise creates the entry and adds the value.
		else{
			 table.put(state,new HashMap<Action, Double>());
			 table.get(state).put(action, value);
		}
	}	
	
	/** Gets the value in an entry of the table, Q(state,action), or null if the
	 *  value does not exist. */
	public double getQValue(State state, Action action){
		return table.get(state).get(action);
	}	
	
	/** Returns the action that maximizes Q(state,action) given the state.*/
	public Action getActionMaxValue(State state){
		// If there is no entry for the state, returns null.
		if (!table.containsKey(state) || table.get(state).isEmpty())
			return null;
		
		// If there is, looks for the best action.
		Action bestAction=null;
		double bestValue=Double.NEGATIVE_INFINITY;

		for (Entry<Action,Double> actionValue: table.get(state).entrySet()){
			if (actionValue.getValue()>bestValue){
				bestAction = actionValue.getKey();
				bestValue = actionValue.getValue();
			}		
		}
		// Returns the corresponding action.
		return bestAction;
	}
	
	/** Returns the maximum value q(state,action) for a state. */
	public double getMaxQValue(State state){
		Action bestAction = getActionMaxValue(state);
		return table.get(state).get(bestAction);
	}
	
	
	/** Allows printing the table. */
	public String toString(){
		String output = "";
		// Gets the states in the table.
		Set<State> states = table.keySet();
		// Prints each one. 
		for (State state: states){
			output += state.toString()+" --> ";
			for (Entry<Action,Double> actionValue: table.get(state).entrySet()){
				output+= "\t "+actionValue.getKey().getId()+" ("+actionValue.getValue()+")";
			}
			output += "\n";
		}
		return output;
	}
	
	/** Main function. Allows testing the class. */	
	public static void main(String[] args) {
//		QTable qtable = new QTable();
//		
//		State state = new problems.maze.MazeState(1,3);
//		qtable.setQValue(state, problems.maze.MazeAction.DOWN, 1.2);
//		qtable.setQValue(state, problems.maze.MazeAction.UP, 2);
//		
//	    state = new problems.maze.MazeState(1,2);
//		qtable.setQValue(state, problems.maze.MazeAction.LEFT, 3);
//		qtable.setQValue(state, problems.maze.MazeAction.RIGHT, 2.1);
//
//		System.out.println(qtable);
//				
//	    System.out.println(state +" --->  MaxQ: "+qtable.getMaxQValue(state)+". Action: "+qtable.getActionMaxValue(state));		
	}		

}
