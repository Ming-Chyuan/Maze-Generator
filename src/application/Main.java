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
	private static final int cellSize = 20;
	private static final int speed = 100; // how many steps move in one second
	
	private static MazePane mazePane;

	private static void envirInit(Stage primaryStage) {
		mazePane = new MazePane(width, height, cellSize);
		
		Scene scene = new Scene(mazePane, width, height);
		primaryStage.setTitle("Maze Generator");
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
				if(!mazePane.finishGeneration) {
					mazePane.generateMaze();
				} else {
					if(!mazePane.finishSerch)
						mazePane.findPath();
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