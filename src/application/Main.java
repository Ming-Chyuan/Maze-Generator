package application;
	
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;


public class Main extends Application {
	private static int mazeWidth = 1000;
	private static int mazeHeight = 600;
	private static int controlWidth = 200;
	private static Maze maze;
	private static ControlPane controlPane;
	private static long startTime;
	
	@Override
	public void start(Stage primaryStage) {		
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, mazeWidth + controlWidth, mazeHeight);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

		controlPane = new ControlPane(controlWidth);
		maze = new Maze(mazeWidth, mazeHeight, (int)controlPane.cellSizeSlider.getValue(), (int)controlPane.revisitProbSlider.getValue());
		
		root.setCenter(maze.mazePane);
		root.setRight(controlPane);

		AnimationTimer generatorAnimation = new AnimationTimer() {
			@Override
			public void handle(long arg0) {
				if(!maze.mazeGenerator.finish) {
					maze.mazeGenerator.generateMaze();
				} else {
					maze.mazeSolver.setOriginCell(maze.originInitRow, maze.originInitCol);
					maze.mazeSolver.setDestinationCell(maze.destinationInitRow, maze.destinationInitCol);
					this.stop();
				}
				controlPane.generatorTimeLabel.setText("Generator takes " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
				
			}
		};
		
		AnimationTimer solverAnimation = new AnimationTimer() {
			@Override
			public void handle(long arg0) {
				if(!maze.mazeSolver.finish) {
					maze.mazeSolver.searchPath(maze.mazePane);
				} else {
					controlPane.logData.add(new Info(maze.mazeSolver.getLengthOfPath(), (System.currentTimeMillis() - startTime) / 1000 + " sec"));
					controlPane.pathLengthLabel.setText(
							controlPane.pathLengthLabel.getText() + 
							"Path lenth: " + maze.mazeSolver.getLengthOfPath() + 
							"\t" + (System.currentTimeMillis() - startTime) / 1000 + " seconds"
					);
					this.stop();
				}
				controlPane.solverTimeLabel.setText("Solver takes " + (System.currentTimeMillis() - startTime) / 1000 + " seconds");
			}
		};
		
		controlPane.cellSizeSlider.valueProperty().addListener(l -> {
			maze = new Maze(mazeWidth, mazeHeight, (int)controlPane.cellSizeSlider.getValue(), (int)controlPane.revisitProbSlider.getValue());
			root.setCenter(maze.mazePane);
			generatorAnimation.stop();
		});
		

		controlPane.generateBtn.setOnAction(e -> {
			controlPane.generatorTimeLabel.setText("Generator takes _ seconds");
			controlPane.solverTimeLabel.setText("Solver takes _ seconds");
			controlPane.pathLengthLabel.setText("Log:\n  ");
			maze = new Maze(mazeWidth, mazeHeight, (int)controlPane.cellSizeSlider.getValue(), (int)controlPane.revisitProbSlider.getValue());
			root.setCenter(maze.mazePane);
			startTime = System.currentTimeMillis();
			generatorAnimation.start();
		});
		
		controlPane.solveBtn.setOnAction(e -> {
			if(maze.mazeGenerator.finish) {
				for(Cell[] cArr : maze.grid) {
					for(Cell c : cArr) {
						c.setFloorColor(MyColor.floor);
					}
				}
				
				int gW = (int)controlPane.gWeightSlider.getValue();
				int hW = (int)controlPane.hWeightSlider.getValue();
				
				if(gW == 0 && hW == 0) {
					maze.mazeSolver.setWeight(1, 1);
					controlPane.gWeightSlider.setValue(1);
					controlPane.hWeightSlider.setValue(1);
					Alert alert = new Alert(AlertType.ERROR);
					alert.setHeaderText("Reset the weight");
					alert.setContentText("G and H cannot both be zero");
					alert.showAndWait();
					return;
				} else {
					maze.mazeSolver.setWeight(gW, hW);
				}
				maze.mazeSolver.reset();
				maze.mazeSolver.setPathColor(Color.web("hsl("+ (int)(Math.random() * 360) + ",50%,100%)"));
				
				startTime = System.currentTimeMillis();
				solverAnimation.start();
			}
		});
		
		primaryStage.setTitle("Maze");
		primaryStage.setResizable(false);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
