package application;

import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Cell extends Group {
	private final int x;
	private final int y;
	private final int size;
	private Rectangle floor;
	
	public int rowInGrid;
	public int colInGrid; 
	public boolean visited = false;
	public boolean popped = false;

	private ArrayList<Line> walls = new ArrayList<>();
	public boolean[] hasWalls = {true, true, true, true}; // top right bottom left
	
	public Cell(int row, int col, int size) {
		rowInGrid = row;
		colInGrid = col;
		x = col * size;
		y = row * size;
		this.size = size;
		
		buildFloor();
		buildWalls();
	}
	
	public void setFloorColor(Color c) {
		floor.setFill(c);
	}
	
	private void buildFloor() {
		floor = new Rectangle(x, y, size, size);
		floor.setFill(new Color(0.25, 0.25, 0.25, 1));
		this.getChildren().add(floor);
	}
	
	public void buildWalls() {
		this.getChildren().removeAll(walls);
		walls.clear();
		
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
		l.setStroke(Color.WHITE);
		return l;
	}
}
