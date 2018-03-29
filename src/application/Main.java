package application;
	
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;


public class Main extends Application {	
	@Override
	public void start(Stage primaryStage) {
		int width = 600;
		int height = 600;
		int speed = 100; // how many steps move in one second
		
		Maze maze = new Maze(width, height);
		
		Scene scene = new Scene(maze.mazePane, width, height);
		primaryStage.setTitle("Maze Generator and Solver");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.initStyle(StageStyle.UTILITY);
		primaryStage.show();
		
		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long arg0) { 
				if(!maze.mazeGenerator.finish) {
					maze.mazeGenerator.generateMaze();
				} else {
					if(!maze.mazeSolver.finish)
						maze.mazeSolver.searchPath(maze.mazePane);
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