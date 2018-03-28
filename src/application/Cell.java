package application;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Cell extends Group {
	public final int size;
	private Point pos;
	private Rectangle floor;
	
	public boolean visited = false;
	public boolean popped = false;

	private ArrayList<Line> walls = new ArrayList<>();
	public boolean[] hasWalls = {true, true, true, true}; // top right bottom left

	// A* algorithm
	public int fScore = 0;
	public int gScore = 0;
	public int hScore = 0;
	
	public Cell(int row, int col, int size) {
		pos = new Point(row, col, col * size, row * size);
		this.size = size;
		
		buildFloor();
		buildWalls();
	}
	
	private void buildFloor() {
		floor = new Rectangle(pos.x, pos.y, size, size);
		floor.setFill(MyColor.floorColor);
		this.getChildren().add(floor);
	}
	
	public void buildWalls() {
		this.getChildren().removeAll(walls);
		walls.clear();
		
		int x = pos.x;
		int y = pos.y;
		
		if(hasWalls[DirectionType.TOP])
			walls.add(newWall(x, y, x + size, y));
		if(hasWalls[DirectionType.RIGHT])
			walls.add(newWall(x + size, y, x + size, y + size));
		if(hasWalls[DirectionType.BOTTOM])
			walls.add(newWall(x, y + size, x + size, y + size));
		if(hasWalls[DirectionType.LEFT])
			walls.add(newWall(x, y, x, y + size));
		
		this.getChildren().addAll(walls);
	}
	
	private Line newWall(int startX, int startY, int endX, int endY) {
		Line l = new Line(startX, startY, endX, endY);
		l.setStroke(MyColor.wallColor);
		return l;
	}
	
	public void setFloorColor(Color c) {
		floor.setFill(c);
	}
	
	public Point getPos() {
		return pos;
	}
}
