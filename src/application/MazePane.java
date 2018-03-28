package application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;

public class MazePane extends Pane {
	private int rows;
	private int cols;
	private final int startRow = 0;
	private final int startCol = 0;
	private final int endRow;
	private final int endCol;
	private Cell[][] grid;
	private Cell currentCell;

	private Stack<Cell> stack = new Stack<>();
	
	// The set of cells already evaluated
	private ArrayList<Cell> closeSet = new ArrayList<>();
	// The set of currently discovered cells that are not evaluated yet.
	private ArrayList<Cell> openSet = new ArrayList<>();
	// cell -> the cell which came from
	private HashMap<Cell, Cell> previousCellMap = new HashMap<>();
	
	private ArrayList<Line> linePath = new ArrayList<>();
	
	public boolean finishGeneration = false;
	public boolean finishSerch = false;

	public MazePane(int width, int height, int cellSize) {
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
		
		currentCell = grid[startRow][startCol];
		
		openSet.add(grid[startRow][startCol]);
	}

	// ------------ maze generation algorithm ------------
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
			finishGeneration = true;
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
	
	private boolean isInsideTheGrid(int row, int col) {
		if(0 <= row && row < rows && 0 <= col && col < cols)
			return true;
		return false;
	}

	// ------------ A* search algorithm ------------
	public void findPath() {
		if(!openSet.isEmpty()) {			
			currentCell = openSet.get(findLowestIndexInOpenSet());
			
			/*
			 * 	Object o
			 * 	o is actually a pointer to an Object
			 * 
			 * 	when list.add(a) , b = list.get(0) if size of list is 1
			 * 	which mean a and b are both point to the same address
			 * 
			 * 	## so a.equal(b) and a == b are both return true
			 * 
			 * 	see the answer with the second highest score:
			 * 	https://stackoverflow.com/questions/40480/is-java-pass-by-reference-or-pass-by-value
			 */
			if(currentCell == grid[endRow][endCol]) {  
				finishSerch = true;
			}
			
			openSet.remove(currentCell);
			closeSet.add(currentCell);
			
			ArrayList<Cell> neighborList = getNeighborList(currentCell);
			
			for(Cell neighbor : neighborList) {
				if(!closeSet.contains(neighbor)) {
					// the neighbor which is not already evaluated
					int tempG = currentCell.gScore + 1;
					
					if(openSet.contains(neighbor)) {
						if(tempG < neighbor.gScore) {
							neighbor.gScore = tempG;
						}
					} else {
						// Discover a new cell
						neighbor.gScore = tempG;
						openSet.add(neighbor);
					}
					
					previousCellMap.put(neighbor, currentCell); // record which cell came from
					neighbor.hScore = heuristic(neighbor);
					neighbor.fScore = neighbor.gScore + neighbor.hScore;
				}
			}
		} else {
			// no solution
		}
		
		drawAStarSolution();
	}
	
	private int findLowestIndexInOpenSet() {
		int lowestIndex = 0;
		for(int i = 0; i < openSet.size(); i++) {
			if(openSet.get(i).fScore < openSet.get(lowestIndex).fScore) {
				lowestIndex = i;
			}
		}
		return lowestIndex;
	}
	
	private int heuristic(Cell c) {
		// Manhattan distance between c and end
		return Math.abs(c.getPos().row - endRow) + Math.abs(c.getPos().col - endCol);
	}
	
	private ArrayList<Cell> getNeighborList(Cell c) {
		ArrayList<Cell> neightborList = new ArrayList<>();
		
		for(int i = 0; i < c.hasWalls.length; i++) {
			if(!c.hasWalls[i]) {
				Cell neightbor = getNeighbor(c, i);
				if(neightbor != null)
					neightborList.add(neightbor);
			}
		}
		
		return neightborList;
	}
	
	private Cell getNeighbor(Cell c, int direction) {
		Point pos = c.getPos();		
		int row, col;
		
		if(direction == DirectionType.TOP) {
			row = pos.row - 1;
			col = pos.col;
		} else if(direction == DirectionType.RIGHT) {
			row = pos.row;
			col = pos.col + 1;
		} else if(direction == DirectionType.BOTTOM) {
			row = pos.row + 1;
			col = pos.col;
		} else { // left
			row = pos.row;
			col = pos.col - 1;
		}
		
		return isInsideTheGrid(row, col) ? grid[row][col] : null;
	}
	
	private void drawAStarSolution() {
		for(Cell c : openSet) {
			c.setFloorColor(MyColor.openSetColor);
		}

		for(Cell c : closeSet) {
			c.setFloorColor(MyColor.closeSetColor);
		}
		
		currentCell.setFloorColor(MyColor.currnetColor);
		
		drawLinePath();
	}
	
	private void drawLinePath() {
		this.getChildren().removeAll(linePath);
		linePath.clear();
		
		// use all cells in the path to draw lines
		for(Cell c : getCellPath()) {
			c.setFloorColor(MyColor.floorColor);
			
			Cell cPre = previousCellMap.get(c);
			if(cPre == null)
				break;
			Line l = getLineBetweenCells(c, cPre);
			linePath.add(l);
			this.getChildren().add(l);
		}
	}
	
	private ArrayList<Cell> getCellPath() {
		// cells in the list which are all in the path
		ArrayList<Cell> cellPath = new ArrayList<>();
		
		Cell c = currentCell;
		Cell cPre = previousCellMap.get(c);
		
		cellPath.add(c);
		while(cPre != null) {
			cPre = previousCellMap.get(c);
			cellPath.add(cPre);
			c = cPre;
		}
		return cellPath;
	}
	
	private Line getLineBetweenCells(Cell c1, Cell c2) {
		int s = c1.size / 2;
		
		int c1X = c1.getPos().x + s;
		int c2X = c2.getPos().x + s;
		int c1Y = c1.getPos().y + s;
		int c2Y = c2.getPos().y + s;
		
		Line l;
		// avoid overlaps 
		if(c1X > c2X) {
			l = new Line(c1X, c1Y, c2X + 1, c2Y);
		} else if(c1X < c2X) {
			l = new Line(c1X, c1Y, c2X - 1, c2Y);
		} else if(c1Y > c2Y) {
			l = new Line(c1X, c1Y, c2X, c2Y + 1);
		} else {
			l = new Line(c1X, c1Y, c2X, c2Y - 1);
		}
		
		l.setStroke(MyColor.pathColor);
		return l;
	}
	
	// ------------------------
	
	public Cell[][] getGrid() {
		return grid;
	}
}
