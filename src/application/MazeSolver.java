package application;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class MazeSolver {
	public boolean finish = false;
	
	private final int rows;
	private final int cols;
	private final int endRow;
	private final int endCol;
	private Cell[][] grid;
	private Cell currentCell;

	private final int gScoreWeight = 1;
	private final int hScoreWeight = 2;
	
	// The set of cells already evaluated
	private ArrayList<Cell> closeSet = new ArrayList<>();
	// The set of currently discovered cells that are not evaluated yet.
	private ArrayList<Cell> openSet = new ArrayList<>();
	// cell -> the cell which came from
	private HashMap<Cell, Cell> previousCellMap = new HashMap<>();
	
	private ArrayList<Line> linePath = new ArrayList<>();
	
	public MazeSolver(Cell[][] grid, int startRow, int startCol, int endRow, int endCol) {
		this.grid = grid;
		rows = grid.length;
		cols = grid[0].length;
		this.endRow = endRow;
		this.endCol = endCol;
		
		openSet.add(grid[startRow][startCol]);
	}
	
	// A* algorithm
	public void searchPath(Pane pathPane) {
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
				finish = true;
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
					neighbor.fScore = gScoreWeight * neighbor.gScore + hScoreWeight * neighbor.hScore;
				}
			}
		} else {
			// no solution
		}
		
		drawAStarSolution(pathPane);
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
		return Math.abs(c.pos.row - endRow) + Math.abs(c.pos.col - endCol);
	}
	
	private ArrayList<Cell> getNeighborList(Cell c) {
		ArrayList<Cell> neightborList = new ArrayList<>();
		
		for(int i = 0; i < c.hasWall.length; i++) {
			if(!c.hasWall[i]) {
				Cell neightbor = getNeighbor(c, i);
				if(neightbor != null)
					neightborList.add(neightbor);
			}
		}
		
		return neightborList;
	}
	
	private Cell getNeighbor(Cell c, int direction) {
		Point pos = c.pos;		
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
	
	private void drawAStarSolution(Pane pathPane) {
		for(Cell c : openSet) {
			c.setFloorColor(MyColor.openSetColor);
		}

		for(Cell c : closeSet) {
			c.setFloorColor(MyColor.closeSetColor);
		}
		
		currentCell.setFloorColor(MyColor.currnetColor);
		
		drawLinePath(pathPane);
	}
	
	private void drawLinePath(Pane pathPane) {
		pathPane.getChildren().removeAll(linePath);
		linePath.clear();
		
		// use all cells in the path to draw lines
		for(Cell c : getCellPath()) {
			c.setFloorColor(MyColor.floorColor);
			
			Cell cPre = previousCellMap.get(c);
			if(cPre == null)
				break;
			Line l = getLineBetweenCells(c, cPre);
			linePath.add(l);
			pathPane.getChildren().add(l);
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
		
		int c1X = c1.pos.x + s;
		int c2X = c2.pos.x + s;
		int c1Y = c1.pos.y + s;
		int c2Y = c2.pos.y + s;
		
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
		
		l.setStroke(MyColor.currnetColor);
		l.setStrokeWidth(c1.size / 3);
		l.setStrokeLineCap(StrokeLineCap.ROUND);
		return l;
	}

	private boolean isInsideTheGrid(int row, int col) {
		if(0 <= row && row < rows && 0 <= col && col < cols)
			return true;
		return false;
	}
}
