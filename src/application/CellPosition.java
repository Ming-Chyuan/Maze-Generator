package application;

public class CellPosition {
	// in grid
	public final int row;
	public final int col;
	// pixel
	public final int x;
	public final int y;
	
	public CellPosition(int row, int col, int x, int y) {
		this.row = row;
		this.col = col;
		this.x = x;
		this.y = y;
	}
}
