package application;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Cell extends Group {
	public final int size;
	public CellPosition pos;

	public boolean[] hasWall; // {top, right, bottom, left}
	private ArrayList<Line> walls;
	private Rectangle floor;
	
	// for maze generation algorithm
	public boolean visited;
	public boolean popped;

	// for A* algorithm
	public int fScore;
	public int gScore;
	public int hScore;

	public Cell(int row, int col, int size, int dx, int dy) {
		this.size = size;
		pos = new CellPosition(row, col, col * size + dx, row * size + dy);
		hasWall = new boolean[] {true, true, true, true};
		walls = new ArrayList<>();
		visited = false;
		popped = false;
		fScore = 0;
		gScore = 0;
		hScore = 0;
		
		buildFloor();
		buildWalls();
	}
	
	private void buildFloor() {
		floor = new Rectangle(pos.x, pos.y, size, size);
		floor.setFill(MyColor.floor);
		this.getChildren().add(floor);
	}
	
	public void buildWalls() {
		this.getChildren().removeAll(walls);
		walls.clear();
		
		int x = pos.x;
		int y = pos.y;
		
		if(hasWall[DirectionType.TOP])
			walls.add(newWall(x, y, x + size, y));
		if(hasWall[DirectionType.RIGHT])
			walls.add(newWall(x + size, y, x + size, y + size));
		if(hasWall[DirectionType.BOTTOM])
			walls.add(newWall(x, y + size, x + size, y + size));
		if(hasWall[DirectionType.LEFT])
			walls.add(newWall(x, y, x, y + size));
		
		this.getChildren().addAll(walls);
	}
	
	private Line newWall(int startX, int startY, int endX, int endY) {
		Line l = new Line(startX, startY, endX, endY);
		l.setStroke(MyColor.wall);
		return l;
	}
	
	public void setFloorColor(Color c) {
		floor.setFill(c);
	}
}
