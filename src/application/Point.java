package application;

public class Point {
	// in grid
	public final int row;
	public final int col;
	// pixel
	public final int x;
	public final int y;
	
	public Point(int row, int col, int x, int y) {
		this.row = row;
		this.col = col;
		this.x = x;
		this.y = y;
	}
}
