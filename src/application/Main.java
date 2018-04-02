package application;
	
import java.util.concurrent.TimeUnit;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Main extends Application {
	private static int width = 1000;
	private static int height = 600;
	private static int cellSize = 30;
	private static boolean pause = false;
	private static boolean waittingToStart = true;
	private static Maze maze = new Maze(width, height, cellSize);
	private static AnimationTimer timer;
	private static Scene scene;
	
	@Override
	public void start(Stage primaryStage) {	
		int generateSpeed = 100; 
		int searchSpeed = 30;

		scene = new Scene(maze.mazePane, width, height);
		scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());
		primaryStage.setTitle("Maze Generator and Solver");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
		
		timer = new AnimationTimer() {
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
						TimeUnit.SECONDS.sleep(1);
						this.stop();
						waittingToStart = true;
						setting("Restart", 80, 30);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};

		scene.setOnKeyReleased(e -> {
			// pause
			if(!waittingToStart && e.getCode() == KeyCode.P) {
				if(!pause && (!maze.mazeGenerator.finish || !maze. mazeSolver.finish)) {
					pause = true;
					timer.stop();
				} else {
					pause = false;
					timer.start();
				}
			}
			
			// restart
			if(!waittingToStart && e.getCode() == KeyCode.R) {
				timer.stop();
				waittingToStart = true;
				setting("Restart", 80, 30);
			}
		});

		setting("Start", 50, 30);
	}
	
	private void setting(String btnStr, int btnW, int btnH) {
		Rectangle bg = getBG();
		Button btn = getBtn(btnStr, btnW, btnH);
		Slider slider = new Slider(10, 200, cellSize);
		slider.setMinWidth(150);
		slider.setLayoutX(width / 2 - slider.getMinWidth() / 2);
		slider.setLayoutY(btn.getLayoutY() + btn.getMinHeight() + 20);
		Label l1 = new Label("Size");
		l1.setLayoutX(slider.getLayoutX() - 27);
		l1.setLayoutY(slider.getLayoutY());
		Label l2 = new Label(String.valueOf((int)slider.getValue()));
		l2.setLayoutX(slider.getLayoutX() + slider.getMinWidth() + 5);
		l2.setLayoutY(slider.getLayoutY());
		slider.valueProperty().addListener(l -> {
			l2.textProperty().setValue(String.valueOf((int)slider.getValue()));
			maze = new Maze(width, height, (int)slider.getValue());
			scene.setRoot(maze.mazePane);
			maze.mazePane.getChildren().add(bg);
			maze.mazePane.getChildren().add(btn);
			maze.mazePane.getChildren().add(slider);
			maze.mazePane.getChildren().add(l1);
			maze.mazePane.getChildren().add(l2);
		});
		btn.setOnAction(e -> {
			cellSize = (int)slider.getValue();
			waittingToStart = false;
			maze = new Maze(width, height, (int)slider.getValue());
			scene.setRoot(maze.mazePane);
			timer.start();
		});
		maze.mazePane.getChildren().add(bg);
		maze.mazePane.getChildren().add(btn);
		maze.mazePane.getChildren().add(slider);
		maze.mazePane.getChildren().add(l1);
		maze.mazePane.getChildren().add(l2);
	}
	
	private Rectangle getBG() {
		Rectangle bg = new Rectangle(0, 0, width + 10, height + 10);
		bg.setFill(new Color(0, 0, 0, 0.7));
		return bg;
	}
	
	private Button getBtn(String text, int w, int h) {
		Button btn = new Button(text);
		btn.setMinWidth(w);
		btn.setMinHeight(h);
		btn.setLayoutX(width / 2 - btn.getMinWidth() / 2);
		btn.setLayoutY(height / 2 - btn.getMinHeight() / 2 - 25);
		btn.setCursor(Cursor.HAND);
		return btn;
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}