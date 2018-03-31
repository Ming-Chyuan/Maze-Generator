package application;
	
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Main extends Application {
	private static int width = 600;
	private static int height = 600;
	
	@Override
	public void start(Stage primaryStage) {
		int width = 600;
		int height = 600;
				
		// how many steps move in one second
		int generateSpeed = 100; 
		int searchSpeed = 30;
		
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
				try {
					if(!maze.mazeGenerator.finish) {
						maze.mazeGenerator.generateMaze();
						TimeUnit.MILLISECONDS.sleep(1000 / generateSpeed);
					} else if(!maze. mazeSolver.finish) {
							maze.mazeSolver.searchPath(maze.mazePane);
							TimeUnit.MILLISECONDS.sleep(1000 / searchSpeed);
					} else {
						// restart
						TimeUnit.SECONDS.sleep(2);
						this.stop();
						
						Rectangle bg = getBG();
						Button btn = getBtn("Restart", 60, 30);
						btn.setOnAction(e -> {
							maze.mazePane.getChildren().remove(bg);
							maze.mazePane.getChildren().remove(btn);
							maze.reset(maze.mazePane);
							this.start();
						});
						maze.mazePane.getChildren().add(bg);
						maze.mazePane.getChildren().add(btn);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		// start
		Rectangle bg = getBG();
		Button btn = getBtn("Start", 50, 30);
		btn.setOnAction(e -> {
			maze.mazePane.getChildren().remove(bg);
			maze.mazePane.getChildren().remove(btn);
			timer.start();
		});
		maze.mazePane.getChildren().add(bg);
		maze.mazePane.getChildren().add(btn);
	}
	
	private Rectangle getBG() {
		Rectangle bg = new Rectangle(0, 0, width, height);
		bg.setFill(new Color(0, 0, 0, 0.7));
		return bg;
	}
	
	private Button getBtn(String text, int w, int h) {
		Button btn = new Button(text);
		btn.setMinWidth(w);
		btn.setMinHeight(h);
		btn.setLayoutX(width / 2 - btn.getMinWidth() / 2);
		btn.setLayoutY(height / 2 - btn.getMinHeight() / 2);
		btn.setCursor(Cursor.HAND);
		btn.setStyle("-fx-background-color: #ffff37;"
				+ "-fx-font-size: 1.2em;");
		return btn;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}