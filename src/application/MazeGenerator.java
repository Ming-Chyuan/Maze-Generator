package application;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class MazeGenerator {
	public boolean finish;

	private final int rows;
	private final int cols;
	private final int cellRevisitedProbability; // percent
	private final Cell[][] grid;
	private Cell currentCell;
	private Stack<Cell> stack;

	public MazeGenerator(Cell[][] grid, int revisitProb) {
		this.grid = grid;
		finish = false;
		rows = grid.length;
		cols = grid[0].length;
		cellRevisitedProbability = revisitProb;
		currentCell = grid[new Random().nextInt(rows)][new Random().nextInt(cols)];
		stack = new Stack<>();
	}
	
	// maze generation algorithm
	public void generateMaze() {
		currentCell.visited = true;
		currentCell.setFloorColor(MyColor.visited);
		
		if(currentCell.popped)
			currentCell.setFloorColor(MyColor.backtrack);
		
		Cell nextCell = getNeighbor(currentCell);
		if(nextCell != null) {
			// has neighbor

			if(new Random().nextInt(100) < cellRevisitedProbability) {
				// can revisit this cell
				// 額外新增功能 讓 cell 有機率會再被訪問 讓路線更多樣化
				currentCell.visited = false;
				currentCell.setFloorColor(MyColor.floor);
			} else {
				stack.push(currentCell);
			}
			
			removeWalls(currentCell, nextCell);
						
			currentCell = nextCell;
			currentCell.setFloorColor(MyColor.currnet);
		} else if(!stack.isEmpty()) {
			// current cell is dead end 
			currentCell.setFloorColor(MyColor.backtrack);
			
			// backtrack to the previous cell
			currentCell = stack.pop();
			currentCell.popped = true;
			currentCell.setFloorColor(MyColor.currnet);
		} else if(stack.isEmpty()) {
			finish = true;
			for(Cell[] cArr : grid) {
				for(Cell c : cArr) {
					c.setFloorColor(MyColor.floor);
				}
			}
		}
	}
	
	private void removeWalls(Cell a, Cell b) {
		int i = a.pos.row - b.pos.row;
		if(i == 1) {
			a.hasWall[DirectionType.TOP] = false;
			b.hasWall[DirectionType.BOTTOM] = false;
		} else if(i == -1) {
			b.hasWall[DirectionType.TOP] = false;
			a.hasWall[DirectionType.BOTTOM] = false;
		}
		
		int j = a.pos.col - b.pos.col;
		if(j == 1) {
			a.hasWall[DirectionType.LEFT] = false;
			b.hasWall[DirectionType.RIGHT] = false;
		} else if(j == -1) {
			b.hasWall[DirectionType.LEFT] = false;
			a.hasWall[DirectionType.RIGHT] = false;
		}

		a.buildWalls();
		b.buildWalls();
	}
	
	private Cell getNeighbor(Cell cell) {
		int i = cell.pos.row;
		int j = cell.pos.col;

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
	
	private boolean isInsideTheGrid(int row, int col) {
		if(0 <= row && row < rows && 0 <= col && col < cols)
			return true;
		return false;
	}
}
