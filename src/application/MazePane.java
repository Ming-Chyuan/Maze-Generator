package application;

import javafx.scene.layout.Pane;

public class MazePane extends Pane {
	private final int rows;
	private final int cols;
	
	public Cell[][] grid;
	public final int startRow = 0;
	public final int startCol = 0;
	public final int endRow;
	public final int endCol;
	
	public static final int cellSize = 20;
	
	public MazePane(int width, int height) {
		rows = height / cellSize;
		cols = width / cellSize;
		endRow = rows - 1;
		endCol = cols - 1;
		grid = new Cell[rows][cols];
		
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				grid[i][j] = new Cell(i, j, cellSize);
				this.getChildren().add(grid[i][j]);
			}
		}
	}
}
