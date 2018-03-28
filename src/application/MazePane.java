package application;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class MazePane extends Pane {
	private int rows;
	private int cols;
	private int startRow = 0;
	private int startCol = 0;
	private Cell[][] grid;
	private Cell currentCell;

	private Stack<Cell> stack = new Stack<>();
	
	public boolean finish = false;

	public MazePane(int width, int height, int cellSize) {
		rows = height / cellSize;
		cols = width / cellSize;
		grid = new Cell[rows][cols];
		
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				grid[i][j] = new Cell(i, j, cellSize);
				this.getChildren().add(grid[i][j]);
			}
		}
		
		currentCell = grid[startRow][startCol];
	}
	
	public void generateMaze() {		
		currentCell.visited = true;
		currentCell.setFloorColor(MyColor.visitedColor);
		
		if(currentCell.popped)
			currentCell.setFloorColor(MyColor.backtrackColor);
		
		// WIKI: Maze generation algorithm
		Cell nextCell = checkNeighbors(currentCell);
		if(nextCell != null) {
			// has neighbor
			stack.push(currentCell);
			
			removeWalls(currentCell, nextCell);
			
			currentCell = nextCell;
			currentCell.setFloorColor(MyColor.currnetColor);
		} else if(!stack.isEmpty()) {
			// current cell is dead end 
			currentCell.setFloorColor(MyColor.backtrackColor);
			
			// backtrack to the previous cell
			currentCell = stack.pop();
			currentCell.popped = true;
			currentCell.setFloorColor(MyColor.currnetColor);
		} else if(stack.isEmpty()) {
			finish = true;
			for(Cell[] cArr : grid) {
				for(Cell c : cArr) {
					c.setFloorColor(MyColor.floorColor);
				}
			}
		}
	}
	
	private void removeWalls(Cell a, Cell b) {
		int i = a.getPos().row - b.getPos().row;
		if(i == 1) {
			a.hasWalls[DirectionType.TOP] = false;
			b.hasWalls[DirectionType.BOTTOM] = false;
		} else if(i == -1) {
			b.hasWalls[DirectionType.TOP] = false;
			a.hasWalls[DirectionType.BOTTOM] = false;
		}
		
		int j = a.getPos().col - b.getPos().col;
		if(j == 1) {
			a.hasWalls[DirectionType.LEFT] = false;
			b.hasWalls[DirectionType.RIGHT] = false;
		} else if(j == -1) {
			b.hasWalls[DirectionType.LEFT] = false;
			a.hasWalls[DirectionType.RIGHT] = false;
		}

		a.buildWalls();
		b.buildWalls();
	}
	
	private Cell checkNeighbors(Cell cell) {
		int i = cell.getPos().row;
		int j = cell.getPos().col;

		Cell top = isInsideTheGrid(i - 1, j) ? grid[i - 1][j] : null;
		Cell right = isInsideTheGrid(i, j + 1) ? grid[i][j + 1] : null;
		Cell bottom = isInsideTheGrid(i + 1, j) ? grid[i + 1][j] : null;
		Cell left = isInsideTheGrid(i, j - 1) ? grid[i][j - 1] : null;
		
		ArrayList<Cell> cellList = new ArrayList<>(); 
		
		if(top != null && !top.visited)
			cellList.add(top);
		if(right != null && !right.visited)
			cellList.add(right);
		if(bottom != null && !bottom.visited) 
			cellList.add(bottom);
		if(left != null && !left.visited) 
			cellList.add(left);
		
		if(cellList.size() > 0) {
			return cellList.get(new Random().nextInt(cellList.size()));
		}
		return null;
	}
	
	private boolean isInsideTheGrid(int i, int j) {
		if(0 <= i && i < rows && 0 <= j && j < cols)
			return true;
		return false;
	}
	
	public Cell[][] getGrid() {
		return grid;
	}
}
