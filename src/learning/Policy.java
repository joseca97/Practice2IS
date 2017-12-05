package learning;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/** 
 * Represents and manages a policy.
 */
public class Policy {
	
	/** This HashMap contains the action related to each state (the policy). */
	private HashMap<State, Action> actionForState = new HashMap<State, Action>();
	
	/** Sets or adds the action corresponding to a certain state. */
	public void setAction(State state, Action action){
		actionForState.put(state, action);
	}
	
	/** Gets the action corresponding to a certain state. */
	public Action getAction(State state){
		return actionForState.get(state);
	}
	
	/** Resets the policy (deletes all entries).*/
	public void reset(){
		actionForState = new HashMap<State, Action>();
	}
	
	/** Compares two policies. Comparison returns true if both contain a similar set of
	 *  pair state-action. */
	public boolean equals(Object anotherPolicy){
		// If the object passed as parameter is not a state, returns false and
		// reports the error
		if (!(anotherPolicy instanceof Policy)){
			System.out.println("Trying to compare two objects of different classes.");
			return false;
		}
		// Compares the sizes of both policies.
		if (actionForState.size()!=((Policy)anotherPolicy).actionForState.size())
			return false;
		// If both have the same size, compares all the elements
		for (Entry<State,Action> entry: actionForState.entrySet()){
			State state = entry.getKey();
			Action action = entry.getValue();
			if (((Policy)anotherPolicy).getAction(state)!=action)
				return false;
		}
		// Otherwise, the policies are similar.
		return true;
	}
	
	/** Prints the policy as a list. */
	public String toString(){
		String output = "";
		// Gets the states in the table.
		Set<State> states = actionForState.keySet();
		for (State state: states){
			output += "\t" + state.toString();
			output += " -> " + actionForState.get(state)+"\n";
		}
		return output;
	}	
}
