package application;

import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;

public class MazeSolver {
	public boolean finish = false;
	public boolean clickForStartCell = true;

	private final int rows;
	private final int cols;
	private int originRow;
	private int originCol;
	private int destinationRow;
	private int destinationCol;
	private int solveCount;
	private int gScoreWeight;
	private int hScoreWeight;
	private Cell[][] grid;
	private Cell currentCell;
	private Color pathColor;
	private ArrayList<Line> linePath;
	
	// The set of cells already evaluated
	private ArrayList<Cell> closeSet;
	// The set of currently discovered cells that are not evaluated yet.
	private ArrayList<Cell> openSet;
	// cell -> the cell which came from
	private HashMap<Cell, Cell> previousCellMap;
	
	public MazeSolver(Cell[][] grid) {
		this.grid = grid;
		rows = grid.length;
		cols = grid[0].length;
		solveCount = 0;
		linePath = new ArrayList<>();
		closeSet = new ArrayList<>();
		openSet = new ArrayList<>();
		previousCellMap = new HashMap<>();
	}
	
	// A* algorithm
	public void searchPath(Pane pathPane) {
		if(!openSet.isEmpty()) {			
			currentCell = getCellWithLowestFInOpenset();
			
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
			if(currentCell == grid[destinationRow][destinationCol]) {  
				finish = true;
			}
			
			openSet.remove(currentCell);
			closeSet.add(currentCell);
			
			for(Cell neighbor : getNeighbors(currentCell)) {
				if(!closeSet.contains(neighbor)) { // the neighbor is not already evaluated					
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
		
		drawStep(pathPane);
	}
	
	private Cell getCellWithLowestFInOpenset() {
		int lowestIndex = 0;
		for(int i = 0; i < openSet.size(); i++) {
			if(openSet.get(i).fScore < openSet.get(lowestIndex).fScore) {
				lowestIndex = i;
			}
		}
		return openSet.get(lowestIndex);
	}
	
	private int heuristic(Cell c) {
		// Manhattan distance between c and destination
		return Math.abs(c.pos.row - destinationRow) + Math.abs(c.pos.col - destinationCol);
	}
	
	private ArrayList<Cell> getNeighbors(Cell c) {
		ArrayList<Cell> neightbors = new ArrayList<>();
		
		for(int i = 0; i < c.hasWall.length; i++) {
			if(!c.hasWall[i]) {
				Cell neightbor = getNeighbor(c, i);
				if(neightbor != null)
					neightbors.add(neightbor);
			}
		}
		
		return neightbors;
	}
	
	private Cell getNeighbor(Cell c, int direction) {
		int row, col;
		
		if(direction == DirectionType.TOP) {
			row = c.pos.row - 1;
			col = c.pos.col;
		} else if(direction == DirectionType.RIGHT) {
			row = c.pos.row;
			col = c.pos.col + 1;
		} else if(direction == DirectionType.BOTTOM) {
			row = c.pos.row + 1;
			col = c.pos.col;
		} else { // left
			row = c.pos.row;
			col = c.pos.col - 1;
		}
		
		return isInsideTheGrid(row, col) ? grid[row][col] : null;
	}
	
	private void drawStep(Pane pane) {
		for(Cell c : openSet) {
			c.setFloorColor(MyColor.openSet);
		}

		for(Cell c : closeSet) {
			c.setFloorColor(MyColor.closeSet);
		}
		
		drawPath(pane);

		grid[originRow][originCol].setFloorColor(MyColor.originFloor);
		grid[destinationRow][destinationCol].setFloorColor(MyColor.destinationFloor);
	}
	
	private void drawPath(Pane pane) {
		pane.getChildren().removeAll(linePath);
		linePath.clear();
		
		for(Cell c : getCellsOnPath()) {
			c.setFloorColor(MyColor.floor);
			
			Cell cPre = previousCellMap.get(c);
			if(cPre == null) break;
			
			Line l = createLineBetweenCells(c, cPre);
			linePath.add(l);
			pane.getChildren().add(l);
		}
		
		currentCell.setFloorColor(MyColor.currnet);
	}
	
	private ArrayList<Cell> getCellsOnPath() {
		// this list contains all cells of the path
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
	
	private Line createLineBetweenCells(Cell c1, Cell c2) {
		int s = c1.size / 2;
		int c1X = c1.pos.x + s;
		int c2X = c2.pos.x + s;
		int c1Y = c1.pos.y + s;
		int c2Y = c2.pos.y + s;
		
		Line l = new Line(c1X, c1Y, c2X, c2Y);
		l.setStroke(pathColor);
		l.setStrokeWidth(c1.size / 2 * Math.pow(0.7, solveCount));
		l.setStrokeLineCap(StrokeLineCap.ROUND);
		return l;
	}

	private boolean isInsideTheGrid(int row, int col) {
		if(0 <= row && row < rows && 0 <= col && col < cols) return true;
		return false;
	}
	
	public void reset() {
		closeSet.clear();
		openSet.clear();
		previousCellMap.clear();
		linePath.clear();

		finish = false;
		solveCount++;
		openSet.add(grid[originRow][originCol]);
	}
	
	public int getLengthOfPath() {
		return linePath.size();
	}
	
	public void setWeight(int gW, int hW) {
		gScoreWeight = gW;
		hScoreWeight = hW;
	}
	
	public void setPathColor(Color c) {
		pathColor = c;
	}
	
	public void setOriginCell(int row, int col) {
		clickForStartCell = !clickForStartCell;
		originRow = row;
		originCol = col;
		openSet.clear();
		
		drawOriginAndDestination();
		openSet.add(grid[originRow][originCol]);
	}
	
	public void setDestinationCell(int row, int col) {
		clickForStartCell = !clickForStartCell;
		destinationRow = row;
		destinationCol = col;
		drawOriginAndDestination();
	}

	private void drawOriginAndDestination() {
		for(Cell[] cArr : grid) {
			for(Cell c : cArr) {
				c.setFloorColor(MyColor.floor);
			}
		}
		
		Cell origin = grid[originRow][originCol];
		origin.setFloorColor(MyColor.originFloor);
		Cell destination = grid[destinationRow][destinationCol];
		destination.setFloorColor(MyColor.destinationFloor);
		
		if(origin == destination) {
			origin.setFloorColor(MyColor.originIsDestination);
		}
	}
}