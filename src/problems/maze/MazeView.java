package problems.maze;

import problems.ProblemView;
import problems.ProblemVisualization;
import problems.maze.Maze;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import learning.Action;
import learning.State;


/** This class allows showing a maze */
public class MazeView extends ProblemView{
	
	// Colors
	private static Color grassColor= new Color(102,153,51);
	private static Color boundsColor = new Color(200,200,200);
	private static Color holeColor = new Color(120,100,50);
	private static Color sandColor = new Color(255,222,173);
	private static Color waterColor = new Color(34, 79, 189);
	
	// Images
	public static final Image hamster = Toolkit.getDefaultToolkit().getImage("problems/maze/imgs/hamster.png");	
	public static final Image cheese = Toolkit.getDefaultToolkit().getImage("problems/maze/imgs/queso.png");	
	public static final Image cat = Toolkit.getDefaultToolkit().getImage("problems/maze/imgs/cat.png");
	public static final Image cat2 = Toolkit.getDefaultToolkit().getImage("problems/maze/imgs/cat2.png");

	// Maze and status
	private Maze maze;						// Maze	
	private int[] posHamster = {0,0};		// 2 element array with x and y coordinates of the hamster
	private boolean hiddenHamster = false;	// Whether to draw the hamster.
	
	// Some measures of interest
	private int sizePx = 600; 				// Size of the view
	private int cellSizePx;					// Size of each cell	
	private int marginPx;					// Size of the margin
	private int[] posHamsterPx = {0,0};		// Coordinates of the hamster in pixels.
	private int[] posCheesePx = {0,0};  	// Coordinates of the cheese in pixels.
	private double speedPx = 10;	    	// Speed of the hamster (pixels each 0.05s)
	
	// Shown images
	BufferedImage mazeImage;		// Image of the maze
	Image scaledCheese;			    // Scaled cheese
	Image scaledHamster;			// Scaled hamster
	Image scaledCat;			    // Scaled cat
	Image scaledCat2;			    // Scaled cat2

	/**
	 * Builds the view panel given a maze and its size in pixels
	 */
	public MazeView(Maze maze, int sizePx){
		// Calculates dimensions
		this.maze = maze;
		this.sizePx = sizePx;
		cellSizePx = (sizePx-40) / maze.size;
		marginPx = (sizePx - cellSizePx * maze.size)/2;
		speedPx = (cellSizePx*2)/20;	// pixels each 1/20 second  (Four cells/second)
		
		// Calculates the positions of both the hamster and the cheese 
		this.posHamster = maze.hamsterPosition().asVector();
		this.posHamsterPx = posToPx(this.posHamster[0],this.posHamster[1]);

		this.posCheesePx = posToPx(maze.size-1, maze.size-1);
		this.posCheesePx[0] = this.posCheesePx[0] + (int)(cellSizePx*0.1);
		this.posCheesePx[1] = this.posCheesePx[1] + (int)(cellSizePx*0.1);
		
		// Scales the images according to the size
		scaledCheese = cheese.getScaledInstance((int)(cellSizePx*0.8), (int)(cellSizePx*0.8), Image.SCALE_SMOOTH);		 	
		scaledHamster = hamster.getScaledInstance((int)(cellSizePx*0.8), (int)(cellSizePx*0.8), Image.SCALE_SMOOTH);
		scaledCat = cat.getScaledInstance((int)(cellSizePx*0.8), (int)(cellSizePx*0.8), Image.SCALE_SMOOTH);
		scaledCat2 = cat2.getScaledInstance((int)(cellSizePx*0.8), (int)(cellSizePx*0.8), Image.SCALE_SMOOTH);
		
		// Generates the background
		generateMazeImage();		
	}
	
	/** Generates the main maze image (Background) */
	private void generateMazeImage(){
		// Creates the image
		mazeImage=new BufferedImage(sizePx, sizePx, BufferedImage.TYPE_INT_RGB);
		Graphics2D mazeGraphics2D = mazeImage.createGraphics();		
		mazeGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Creates the graphics
		mazeGraphics2D.setColor(sandColor);
		mazeGraphics2D.fillRect(0, 0, sizePx, sizePx);	
		
		// Paints the walls
		mazeGraphics2D.setColor(grassColor);
		int posX, posY;
		int[] posWallPx = {0,0};
		for (posX=0;posX<maze.size;posX++){
			for (posY=0;posY<maze.size;posY++)
				if (maze.cells[posY][posX]==Maze.WALL){
					// Notice that for the screen we use x,y instead of y,x
					posWallPx= posToPx(posY,posX);
					RoundRectangle2D wallShape = new RoundRectangle2D.Double(posWallPx[1]+cellSizePx*0, posWallPx[0]+cellSizePx*0, cellSizePx-cellSizePx*0, cellSizePx-cellSizePx*0, 10, 10);
					mazeGraphics2D.fill(wallShape);						
				}
		}
		
		// Paints the water
		mazeGraphics2D.setColor(waterColor);
		int[] posWaterPx = {0,0};
		for (posX=0;posX<maze.size;posX++){
			for (posY=0;posY<maze.size;posY++)
				if (maze.cells[posY][posX]==Maze.WATER){
					// Notice that for the screen we use x,y instead of y,x
					posWaterPx= posToPx(posY,posX);
					RoundRectangle2D waterShape = new RoundRectangle2D.Double(posWaterPx[1]+cellSizePx*0, posWaterPx[0]+cellSizePx*0, cellSizePx-cellSizePx*0, cellSizePx-cellSizePx*0, 10, 10);
					mazeGraphics2D.fill(waterShape);						
				}
		}	
		
		// Paints the holes
		mazeGraphics2D.setColor(holeColor);
		int[] posHolePx = {0,0};
		for (posX=0;posX<maze.size;posX++){
			for (posY=0;posY<maze.size;posY++)
				if (maze.cells[posY][posX]==Maze.HOLE){
					// Notice that for the screen we use x,y instead of y,x
					posHolePx= posToPx(posY,posX);
					Ellipse2D holeShape = new Ellipse2D.Double(posHolePx[1]+cellSizePx*0.1, posHolePx[0]+cellSizePx*0.1, cellSizePx*0.8, cellSizePx*0.8);
					mazeGraphics2D.fill(holeShape);	
				}
		}		
		
		// Paints the bounds
		mazeGraphics2D.setColor(boundsColor);
		int posBoundPx = 0;
		for (int pos=0;pos<maze.size;pos++){
			posBoundPx= posToPx(pos);
			// Upper
			mazeGraphics2D.fill3DRect(posBoundPx, marginPx/2, cellSizePx, marginPx/2,true);	
			// Bottom
			if (pos!=maze.size-1)			
			mazeGraphics2D.fill3DRect(posBoundPx, sizePx-marginPx, cellSizePx, marginPx/2,true);
			// Left
			mazeGraphics2D.fill3DRect(marginPx/2, posBoundPx, marginPx/2, cellSizePx,true);
			// Right
			mazeGraphics2D.fill3DRect(sizePx-marginPx, posBoundPx, marginPx/2, cellSizePx,true);			
		}
	
		// Corners
		mazeGraphics2D.fill3DRect(marginPx/2, marginPx/2, marginPx/2, marginPx/2,true);
		mazeGraphics2D.fill3DRect(sizePx-marginPx, marginPx/2, marginPx/2, marginPx/2,true);
		mazeGraphics2D.fill3DRect(marginPx/2, sizePx-marginPx, marginPx/2, marginPx/2,true);
		mazeGraphics2D.fill3DRect(sizePx-marginPx, sizePx-marginPx, marginPx/2, marginPx/2,true);
		
	}
	
	
	/** Paints the component */
	public void paintComponent(Graphics graphics) {
		Graphics2D graphics2D = (Graphics2D) graphics;
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,	RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.drawImage(mazeImage,0,0,this);	
				
//		posHamsterPx[0] = posHamsterPx[0] + (int)(cellSizePx*0.1); // To center the hamster
//		posHamsterPx[1] = posHamsterPx[1] + (int)(cellSizePx*0.1);
		
		// Paints the cats
		int posX, posY;
		int[] posCatPx = {0,0};
		for (posX=0;posX<maze.size;posX++){
			for (posY=0;posY<maze.size;posY++)
				if (maze.cells[posY][posX]==Maze.CAT){
					posCatPx= posToPx(posY,posX);
					if ((posHamster[0]!=posY) || (posHamster[1]!=posX))	
						graphics2D.drawImage(scaledCat2, posCatPx[1], posCatPx[0],this);	
				}
		}	

		// Paints the hamster only if it is not hidden and there is no cat on its position.
		if (!hiddenHamster)
			if (maze.cells[posHamster[0]][posHamster[1]]!=Maze.CAT)
				graphics2D.drawImage(scaledHamster, posHamsterPx[1], posHamsterPx[0], this);
			else{
				graphics2D.drawImage(scaledCat, posHamsterPx[1], posHamsterPx[0],this);	
			}

			// 
		
	    // Only paints the cheese when the hamster has not reached it.
	    if ((posHamster[1]!=maze.size-1) || (posHamster[0]!=maze.size-1)) 
	    	graphics2D.drawImage(scaledCheese, posCheesePx[1], posCheesePx[0], this);
	    
		
		graphics2D.dispose();		
	}
	
	/** Hides the hamster */
	public void hideHamster(){
			hiddenHamster = true;
	}	
	
	/** Shows the hamster */
	public void showHamster(){
			hiddenHamster = false;		
	}
	
	/** Moves the hamster one position Left*/
	public void moveLeft(){
		if (posHamster[1]>0)
			moveHamsterToPosition(posHamster[0], posHamster[1]-1);
	}
	/** Moves the hamster one position Right*/

	public void moveRight(){
		if (posHamster[1]<maze.size-1)
			moveHamsterToPosition(posHamster[0], posHamster[1]+1);
	}
	
	/** Moves the hamster one position Up*/
	public void moveUp(){
		if (posHamster[0]>0)
			moveHamsterToPosition(posHamster[0]-1, posHamster[1]);
	}
	
	/** Moves the hamster one position Down*/
	public void moveDown(){
		if (posHamster[0]<maze.size-1)
			moveHamsterToPosition(posHamster[0]+1, posHamster[1]);
	}
		
	public void setHamsterPosition(int y, int x){
		posHamster[0]=y;
		posHamster[1]=x;
		posHamsterPx = posToPx(posHamster[0], posHamster[1]);
	}
	
	/** Moves the hamster to a certain position.*/
	public void moveHamsterToPosition(int y, int x){
		// Calculates the final position
		int[] goalHamsterPx = posToPx(y, x);
//		goalHamsterPx[0] = goalHamsterPx[0] + (int)(cellSizePx*0.1); // To center the hamster
//		goalHamsterPx[1] = goalHamsterPx[1] + (int)(cellSizePx*0.1); 

		// Calculates the distance
		int distY = goalHamsterPx[0]-posHamsterPx[0];
		int distX = goalHamsterPx[1]-posHamsterPx[1];
		
		double dist = Math.sqrt(distX*distX+distY*distY);
		
		// Calculates the number of frames and moves the mouse.
		int numFrames = (int) (dist/speedPx);
		if (maze.cells[posHamster[0]][posHamster[1]]==Maze.WATER)
			numFrames = numFrames*2;
		for ( ; numFrames>0; numFrames--){
			posHamsterPx[0]=posHamsterPx[0]+(goalHamsterPx[0]-posHamsterPx[0])/numFrames;
			posHamsterPx[1]=posHamsterPx[1]+(goalHamsterPx[1]-posHamsterPx[1])/numFrames;
			repaint();
			// Waits 0.05 seconds
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
		}
		
		// The hamster must be located in the right position in the last frame.
		posHamsterPx[0] = goalHamsterPx[0];
		posHamsterPx[1] = goalHamsterPx[1];
		posHamster[0]=y;
		posHamster[1]=x;
		repaint();
	}
	

	/** Changes a position to pixels */
	private int posToPx(int x){
		return (int) (x * cellSizePx + marginPx); 
	}
	
	/** Changes a position to pixels */
	private int[] posToPx(int y, int x){
		int[] posPx = {0,0};
		posPx[0] = (int) (y * cellSizePx + marginPx); 
		posPx[1] = (int) (x * cellSizePx + marginPx); 
		return posPx;
	}
	
	/** Returns the dimension of the view */
    public Dimension getPreferredSize() {
        return new Dimension(sizePx, sizePx);
    }	
    
    /* Problem view */
    
	/** Sets the current state. */
	public void setStateView(State state){
		MazeState currentState = ((MazeState) state);
		setHamsterPosition(currentState.getY(), currentState.getX());
		repaint();
	}
	
	/** Performs an action in the view. */
	public void takeActionView(Action action, State toState){
		MazeAction mazeAction = ((MazeAction) action);
		MazeState mazeToState = ((MazeState) toState);
		
		// If the action is dive, hides the hamster and moves.
		if (maze.cells[mazeToState.getY()][mazeToState.getX()]== Maze.HOLE && mazeAction==MazeAction.DIVE){
			hideHamster();
			moveHamsterToPosition(mazeToState.getY(), mazeToState.getX());
			showHamster();
		}
		// Otherwise, moves.
		else
			moveHamsterToPosition(mazeToState.getY(), mazeToState.getX());		
	}
	
	
	/** Main function, used for testing. */
	public static void main(String[] args) {
		MazeProblem mazeProblem = new MazeProblem(15, 5);
		ProblemView mazeProblemView = mazeProblem.getView(600);
		// Creates the window with the view.
		JFrame window = new JFrame("Problem visualization");
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
						System.exit(0);
			}
		});
		window.getContentPane().add(mazeProblemView);
		window.pack();
		window.setVisible(true);
		
		mazeProblemView.setStateView(new MazeState(1,1));
		// Notice that visualization does not depend on the action.
		mazeProblemView.takeActionView(null, new MazeState(1,2));
		mazeProblemView.takeActionView(null, new MazeState(1,5));
		mazeProblemView.takeActionView(null, new MazeState(1,6));
		mazeProblemView.takeActionView(null, new MazeState(1,7));
		mazeProblemView.takeActionView(null, new MazeState(2,7));
		mazeProblemView.takeActionView(null, new MazeState(3,7));




	}

}
