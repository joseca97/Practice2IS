package problems.maze;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

import problems.ProblemView;
import problems.maze.utils.MazePosition;

/** This class allows representing and generating a maze. */
public class Maze{

	/* Types of cells */
	public static final int EMPTY = 0;
	public static final int WALL = 1;
	public static final int HOLE = 2;
	public static final int WATER = 3;
	public static final int CAT = 4;
	public static final int CHEESE = 5;
		
	/* Size */
	protected int size = 11;
	
	/* Cells of the maze */
	protected int[][] cells;
	
	/* Position of the hamster */
	protected MazePosition hamsterPos;	
	
	/* Number of holes */
	protected int numHoles;	
	
	/* List of holes */
	protected ArrayList<MazePosition> holeList = new ArrayList<MazePosition>();

	/** Creates a maze with seed equals 0 */
	public Maze(int size){
		this.size = size;
		this.cells = new int[size][size];
		generate(0);
	}
	/** Creates a maze with a given seed */
	public Maze(int size, int seed){
		this.size = size;
		this.cells = new int[size][size];
		generate(seed);
	}	
	/** Returns the hamster position. */
	public MazePosition hamsterPosition(){
		return hamsterPos;
	}	
	

	/** Generates the maze method. */
	private void generate(int seed){
		Random random = new Random();
		random.setSeed(seed);
		
		// Everything is empty at the beginning 
		for(int posY=0;posY<size;posY++)
			for(int posX=0;posX<size;posX++)
				cells[posY][posX]=EMPTY;
		
		// Generates two pools
		// 10% of the cells must be water
		int numPools = 2;
		int sizePool = (int) (size*size*0.1)/2;
		for (int nPool=1; nPool<=numPools; nPool++){
			int[][] pool = new int[sizePool][2];

			// Origin of the pool.
			int poolX; int poolY;
			do{
				poolY=random.nextInt(size);
				poolX=random.nextInt(size);
			}while (cells[poolY][poolX]!=EMPTY);
			cells[poolY][poolX] = WATER;
			pool[0][0]=poolY;
			pool[0][1]=poolX;
			
			// Grows 
			int cSizePool = 1;
			while(cSizePool<sizePool){
				// Selects one position of the pool randomly.
				int aux = random.nextInt(cSizePool);
				// Selects a coordinate to grow;
				int coord = random.nextInt(2);
				// Selects the direction;
				int dir = random.nextBoolean() ? 1 : -1;
				// New cell
				pool[cSizePool][0] = pool[aux][0];
				pool[cSizePool][1] = pool[aux][1];
				// New coordinate
				pool[cSizePool][coord] = pool[cSizePool][coord] + dir;
				// The new position must be inside the limits
				if (pool[cSizePool][coord]<=0 || pool[cSizePool][coord]>=size)
						continue;
				// If the position is valid, fills with water.
				if (cells[pool[cSizePool][0]][pool[cSizePool][1]] == EMPTY){
					cells[pool[cSizePool][0]][pool[cSizePool][1]] = WATER;
					cSizePool++;
				}
			}	
		}
		
		// Places the walls 
		for(int posY=0;posY<size;posY++)
			for(int posX=0;posX<size;posX++)
				if (cells[posY][posX]==EMPTY) 
					if (random.nextDouble()<0.2)
						cells[posY][posX] = WALL;
		
		// Places the cats 
		for(int posY=0;posY<size;posY++)
			for(int posX=0;posX<size;posX++)
				if (cells[posY][posX]==EMPTY) 
					if (random.nextDouble()<0.02)
						cells[posY][posX] = CAT;
		
        // Holes 2% of the positions are holes.
		numHoles = (int) ((size*size)*0.02);
		holeList = new ArrayList<MazePosition>();
		for (int nHole=1; nHole<=numHoles-1; nHole++){
			// Origin of the pool.
			int holeX; int holeY;
			do{
				holeY=random.nextInt(size);
				holeX=random.nextInt(size);
			}while (cells[holeY][holeX]!=EMPTY);
			cells[holeY][holeX] = HOLE;
			holeList.add(new MazePosition(holeY, holeX));
		}

		// Generates hamster position
		int hamsterY;
		int hamsterX;
		do{
			hamsterY=random.nextInt(size);
			hamsterX=random.nextInt(size);
		}while (cells[hamsterY][hamsterX]!=EMPTY);
		hamsterPos = new MazePosition(hamsterY, hamsterX);
		
		// The cheese!
		cells[size-1][size-1]=CHEESE;
		
		// Always a hole besides the cheese so that the maze can always be solved
		cells[size-3][size-1]=HOLE;
		holeList.add(new MazePosition(size-3, size-1));
		cells[size-2][size-1]=EMPTY;
	}

	/** Transforms the maze into a string. */
	public String toString(){
		char[] cellType = {' ', '*', 'o', '+', 'c', 'h'};
		String mazeAsStr = new String();
		int posX, posY;
		for(posY=0;posY<size;posY++){
			for(posX=0;posX<size;posX++)
				mazeAsStr+=cellType[cells[posY][posX]];
			mazeAsStr+='\n';
		}
		return mazeAsStr;	
	}	
	
	/** Main function, used for testing. */
	public static void main(String[] args) {
		Maze maze = new Maze(15,5);
		System.out.println(maze);
		
		// Creates a problem and a view for displaying.
		MazeView mazeView = new MazeView(maze,600);
		// Creates the window with the view.
		JFrame window = new JFrame("Problem visualization");
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
						System.exit(0);
			}
		});
		window.getContentPane().add(mazeView);
		window.pack();
		window.setVisible(true);
	}
}

	


