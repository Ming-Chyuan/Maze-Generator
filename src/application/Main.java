package application;
	
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;


public class Main extends Application {	
	private static final int width = 600;
	private static final int height = 600;
	private static final int speed = 100; // how many steps move in one second
		
	private static MazePane mazePane;

	private static MazeGenerator mazeGenerator;
	private static MazeSolver mazeSolver;
	
	private static void envirInit(Stage primaryStage) {
		mazePane = new MazePane(width, height);
		mazeGenerator = new MazeGenerator(mazePane.grid, mazePane.startRow, mazePane.startCol);
		mazeSolver = new MazeSolver(mazePane.grid, mazePane.startRow, mazePane.startCol, mazePane.endRow, mazePane.endCol);
		
		Scene scene = new Scene(mazePane, width, height);
		primaryStage.setTitle("Maze Generator and Solver");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.show();
	}
	
	@Override
	public void start(Stage primaryStage) {
		envirInit(primaryStage);
		
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long arg0) { 
				if(!mazeGenerator.finish) {
					mazeGenerator.generateMaze();
				} else {
					if(!mazeSolver.finish)
						mazeSolver.searchPath(mazePane);
					else
						this.stop();
				}
				
				try {
					TimeUnit.MILLISECONDS.sleep(1000 / speed);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		timer.start();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}