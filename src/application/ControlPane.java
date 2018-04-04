package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ControlPane extends VBox {
	public final Button generateBtn;
	public final Button solveBtn;
	public final Slider cellSizeSlider;
	public final Slider revisitProbSlider;
	public final Slider gWeightSlider;
	public final Slider hWeightSlider;
	public final Label generatorTimeLabel;
	public final Label solverTimeLabel;
	public final Label pathLengthLabel;
	public final ObservableList<Info> logData;

	private final Label cellSizeSliderValueLabel;
	private final Label revisitProbValueLabel;
	private final Label gWeightSliderValueLabel;
	private final Label hWeightSliderValueLabel;
	private final TableView<Info> log;
	
	public ControlPane(int w) {
		generateBtn = new Button("Generate Maze");
		solveBtn = new Button("Solve Maze");

		cellSizeSlider = new Slider(10, 200, 100);
		cellSizeSliderValueLabel = new Label(String.valueOf((int)cellSizeSlider.getValue()));
		cellSizeSliderValueLabel.setAlignment(Pos.CENTER_RIGHT);
		cellSizeSliderValueLabel.setPrefWidth(30);
		
		revisitProbSlider = new Slider(0, 40, 20);
		revisitProbValueLabel = new Label(String.valueOf((int)revisitProbSlider.getValue()));
		revisitProbValueLabel.setAlignment(Pos.CENTER_RIGHT);
		revisitProbValueLabel.setPrefWidth(30);

		gWeightSlider = new Slider(0, 10, 1);
		gWeightSlider.setBlockIncrement(1);
		gWeightSliderValueLabel = new Label(String.valueOf((int)gWeightSlider.getValue()));
		gWeightSliderValueLabel.setAlignment(Pos.CENTER_RIGHT);
		gWeightSliderValueLabel.setPrefWidth(30);
		
		hWeightSlider = new Slider(0, 10, 1);
		hWeightSlider.setBlockIncrement(1);
		hWeightSliderValueLabel = new Label(String.valueOf((int)hWeightSlider.getValue()));
		hWeightSliderValueLabel.setAlignment(Pos.CENTER_RIGHT);
		hWeightSliderValueLabel.setPrefWidth(30);
		
		generatorTimeLabel = new Label("Generator takes _ seconds");
		generatorTimeLabel.setAlignment(Pos.CENTER_LEFT);
		generatorTimeLabel.setPrefWidth(180);
		
		solverTimeLabel = new Label("Solver takes _ seconds");
		solverTimeLabel.setAlignment(Pos.CENTER_LEFT);
		solverTimeLabel.setPrefWidth(180);

		pathLengthLabel = new Label("Log:\n  ");
		pathLengthLabel.setAlignment(Pos.TOP_LEFT);
		pathLengthLabel.setPrefWidth(180);
		pathLengthLabel.setStyle("-fx-border-color: white;");

		TableColumn<Info, String> lengthCol = new TableColumn<>("Length");
		lengthCol.setPrefWidth(82);
		lengthCol.setResizable(false);
		lengthCol.setSortable(false);
		lengthCol.setCellValueFactory(new PropertyValueFactory<Info, String>("length"));
		
		TableColumn<Info, String> timeCol = new TableColumn<>("Time");
		timeCol.setPrefWidth(82);
		timeCol.setResizable(false);
		timeCol.setSortable(false);
		timeCol.setCellValueFactory(new PropertyValueFactory<Info, String>("time"));
		
		logData = FXCollections.observableArrayList();
		
		log = new TableView<>();
		log.setMaxHeight(122);
		log.setItems(logData);
		log.getColumns().addAll(lengthCol, timeCol);
		
		cellSizeSlider.valueProperty().addListener(l -> {
			cellSizeSliderValueLabel.textProperty().setValue(String.valueOf((int)cellSizeSlider.getValue()));
		});
		
		revisitProbSlider.valueProperty().addListener(l -> {
			revisitProbValueLabel.textProperty().setValue(String.valueOf((int)revisitProbSlider.getValue()));
		});

		gWeightSlider.valueProperty().addListener(l -> {
			gWeightSliderValueLabel.textProperty().setValue(String.valueOf((int)gWeightSlider.getValue()));
		});

		hWeightSlider.valueProperty().addListener(l -> {
			hWeightSliderValueLabel.textProperty().setValue(String.valueOf((int)hWeightSlider.getValue()));
		});

		this.getChildren().add(createSliderGroup("Size of cell", cellSizeSlider, cellSizeSliderValueLabel));
		this.getChildren().add(createSliderGroup("Revisit probability", revisitProbSlider, revisitProbValueLabel));
		this.getChildren().add(generateBtn);
		this.getChildren().add(new Label("Click cell to determine\norigin and destination"));
		this.getChildren().add(createSliderGroup("Weight of G\n(origin <-> current cell)", gWeightSlider, gWeightSliderValueLabel));
		this.getChildren().add(createSliderGroup("Weight of H\n(current cell <-> destination)", hWeightSlider, hWeightSliderValueLabel));
		this.getChildren().add(solveBtn);
		this.getChildren().add(generatorTimeLabel);
		this.getChildren().add(solverTimeLabel);
		this.getChildren().add(log);
		
		this.setPrefWidth(w);
		this.setSpacing(20);
		this.setId("control-pane");
	}
	
	private VBox createSliderGroup(String text, Slider slider, Label valueLabel) {
		valueLabel.setAlignment(Pos.CENTER_RIGHT);
		valueLabel.setPrefWidth(30);
		
		HBox sliderHBox = new HBox(10);
		sliderHBox.getChildren().add(slider);
		sliderHBox.getChildren().add(valueLabel);

		Label sliderLabel = new Label(text);
		
		VBox sliderVBox = new VBox(5);
		sliderVBox.getChildren().add(sliderLabel);
		sliderVBox.getChildren().add(sliderHBox);
		
		return sliderVBox;
	}
}
