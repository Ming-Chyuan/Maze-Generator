package application;

import javafx.scene.layout.Pane;

public class Maze {
	public final Cell[][] grid;
	public final Pane mazePane;
	public final MazeGenerator mazeGenerator;
	public final MazeSolver mazeSolver;
	public final int originInitRow;
	public final int originInitCol;
	public final int destinationInitRow;
	public final int destinationInitCol;

	private final int rows;
	private final int cols;
	
	public Maze(int width, int height, int cellSize, int revisitProb) {
		mazePane = new Pane();
		
		rows = height / cellSize;
		cols = width / cellSize;
		originInitRow = 0;
		originInitCol = 0;
		destinationInitRow = rows - 1;
		destinationInitCol = cols - 1;

		grid = new Cell[rows][cols];

		// let grid in the center of pane
		int dx = width % cellSize / 2 + 5;
		int dy = height % cellSize / 2 + 5;
		
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				grid[i][j] = new Cell(i, j, cellSize, dx, dy);
				mazePane.getChildren().add(grid[i][j]);
			}
		}
		
		mazePane.setId("maze-pane");
		
		mazeGenerator = new MazeGenerator(grid, revisitProb);
		mazeSolver = new MazeSolver(grid);
		
		for(Cell[] cArr : grid) {
			for(Cell c : cArr) {
				c.setOnMouseClicked(e -> {
					if(mazeGenerator.finish) {
						if(mazeSolver.clickForStartCell) {
							mazeSolver.setOriginCell(c.pos.row, c.pos.col);
						} else {
							mazeSolver.setDestinationCell(c.pos.row, c.pos.col);
						}
					}
				});
			}
		}
	}
}
