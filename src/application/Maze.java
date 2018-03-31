package application;

import javafx.scene.layout.Pane;

public class Maze {
	private Cell[][] grid;
	private final int rows;
	private final int cols;
	private final int cellSize = 20;
	private final int startRow = 0;
	private final int startCol = 0;
	private final int endRow;
	private final int endCol;
	
	public Pane mazePane = new Pane();
	public MazeGenerator mazeGenerator;
	public MazeSolver mazeSolver;
	
	public Maze(int width, int height) {
		rows = height / cellSize;
		cols = width / cellSize;
		endRow = rows - 1;
		endCol = cols - 1;

		grid = new Cell[rows][cols];
		
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				grid[i][j] = new Cell(i, j, cellSize);
				mazePane.getChildren().add(grid[i][j]);
			}
		}
		
		mazeGenerator = new MazeGenerator(grid, startRow, startCol);
		mazeSolver = new MazeSolver(grid, startRow, startCol, endRow, endCol);
	}
	
	public void reset(Pane mazePane) {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				grid[i][j].reset();
			}
		}
		
		mazeGenerator.finish = false;
		mazeGenerator.reset();
		mazeSolver.finish = false;
		mazeSolver.reset(mazePane);
	}
}
