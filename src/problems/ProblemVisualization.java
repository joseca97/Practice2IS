package problems;

import javax.swing.JFrame;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import learning.State;
import learning.Action;
import learning.Policy;
import learning.LearningProblem;
import problems.ProblemView;

/** 
 * This class allows visualizing the result of applying a policy on a problem 
 * which implements the interface ProblemView.  
 */
public class ProblemVisualization{

	/** Reference to the problem */
	protected LearningProblem problem;	

	/** Graphical view of the problem. */
	public ProblemView view;  	

	/** Window. */
	private JFrame window;					

	/** 
	 * Constructor. Takes a problem and the size of the window (in pixels) used 
	 * to display the view. 
	 */
	public ProblemVisualization(LearningProblem problem, int sizePx){
		// If the problem can not be visualized, reports the error.
		if (!(problem instanceof ProblemVisualizable)){
			System.out.println("This problem is not visualizable.");
			return;
		}
		this.problem = problem;
		
		// Creates the problem view
		this.view = ((ProblemVisualizable) problem).getView(sizePx);
		
		// Creates the window with the view.
		window = new JFrame("Problem visualization");
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		window.getContentPane().add(view);
		window.pack();
		window.setVisible(true);
	}
	
	/** 
	 * Visualizes the policy. Starts in the initial state and executes actions until 
	 * reaching a terminal state.
	 */
	public void visualizePolicy(Policy policy){
		// Initial state.
		State currentState = problem.getInitialState();
		view.setStateView(currentState);
		
		// Iterates until the current state is final.
		while (!problem.isFinal(currentState)){
			// Calculates the next state
			Action action =  policy.getAction(currentState);
			State newState = problem.applyAction(currentState, action);
			// Moves
			view.takeActionView(action, newState);
			// Updates the current state.
			currentState = newState;	
		}
	}
	
	/** Closes the window */
    public void close(){
    	window.dispose();
    }
}
