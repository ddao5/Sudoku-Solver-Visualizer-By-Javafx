package com.ddao5.sudoku;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.StrokeTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;


/**
 * A class represents each spot of the current puzzle.
 * @author ddao5
 *
 */
public class Tile extends StackPane {
	/**
	 * Border of the tile.
	 */
	protected Rectangle border;
	/**
	 * Value of the tile.
	 */
	protected int value;
	/**
	 * Used to represent value of the tile.
	 */
	protected Text text;
	/**
	 * If current tile is selected or not.
	 */
	protected boolean isSelected = false;
	/*
	 * Hold temporary value of the tile.
	 */
	private Label temp = null;
	/*
	 * Position of the tile in the board.
	 */
	private int row;
	/*
	 * Position of the tile in the board.
	 */
	private int col;
	/*
	 * List to hold wrong icons.
	 */
	public static List<ImageView> wrong_icons = new ArrayList<>();
	/**
	 * Constructor for the tile.
	 * @param stringNum value of the tile
	 * @param row row where the tile is being placed
	 * @param col column where the tile is being placed
	 */
	public Tile(String stringNum, int row, int col) {
		this.row = row;
		this.col = col;
		value = Integer.parseInt(stringNum);
		border = new Rectangle(70, 70);
		border.setFill(null);
		border.setStroke(Color.BLACK);
		border.setStrokeType(StrokeType.INSIDE);
		if (value == 0) {
			text = new Text("");
			this.setOnMouseClicked(e -> {
				pressed();
				if(Visualizer.selected != null) {
					Visualizer.selected.pressed();
					Visualizer.selected.getParent().requestFocus();
					Visualizer.selected = null;
				}
				if (isSelected) {
					Visualizer.selected = this;
					this.requestFocus();
				}
			});
			this.setOnKeyPressed(k -> checkKey(k));
		} else {
			text = new Text(stringNum);
		}
		text.setFont(Font.font(30));
		this.setAlignment(Pos.CENTER);
		this.getChildren().addAll(border, text);
	}
	/**
	 * When changing value of a tile, its border will thicken as well as the stroke color will
	 * change from black to blue violet. If the current value of the tile leads to wrong solution,
	 * the stroke color will transit to red.
	 * @param give a new value to the current tile 
	 * @return a javafx transition including all the animation during the process of changing value of the tile
	 */
	public ParallelTransition changeTextAnimation(String newNum) {
		KeyValue initText, endText, initStroke, endStroke;
		KeyFrame initFrameText, endFrameText, initFrameStroke, endFrameStroke;
		Timeline tlText;
		Timeline tlStrokeWidth;
		StrokeTransition strokeColor = new StrokeTransition(Duration.seconds(0.025), this.border);

		// parallel transition including checking numbers and changing stroke width each
		// tile
		ParallelTransition pt = new ParallelTransition();
		value = Integer.parseInt(newNum);

		Color old = (Color) this.border.getStroke();
		strokeColor.setFromValue(old);
		initText = new KeyValue(text.textProperty(), text.getText());
		initStroke = new KeyValue(border.strokeWidthProperty(), border.getStrokeWidth());

		initFrameText = new KeyFrame(Duration.ZERO, initText);
		initFrameStroke = new KeyFrame(Duration.ZERO, initStroke);
		endStroke = new KeyValue(border.strokeWidthProperty(), 2.0);
		if (value != 0) {
			strokeColor.setToValue(Color.BLUEVIOLET);
			endText = new KeyValue(text.textProperty(), newNum);
		} else {
			strokeColor.setToValue(Color.RED);
			endText = new KeyValue(text.textProperty(), "");

		}
		endFrameText = new KeyFrame(Duration.seconds(0.025), endText);
		endFrameStroke = new KeyFrame(Duration.seconds(0.025), endStroke);
		tlText = new Timeline(initFrameText, endFrameText);
		tlStrokeWidth = new Timeline(initFrameStroke, endFrameStroke);
		pt.getChildren().addAll(strokeColor, tlText, tlStrokeWidth);
		return pt;
	}

	/**
	 * When a tile is clicked, its storke will thicken as well as giving option for user
	 * to choose which value of the current tile is.
	 */
	public void pressed() {
		Timeline tlStrokeWidth;
		KeyValue initStroke, endStroke;
		KeyFrame initFrameStroke, endFrameStroke;
		initStroke = new KeyValue(border.strokeWidthProperty(), border.getStrokeWidth());
		initFrameStroke = new KeyFrame(Duration.ZERO, initStroke);
		if (!isSelected) {
			endStroke = new KeyValue(border.strokeWidthProperty(), 1.5);
			isSelected = true;
		} else {
			endStroke = new KeyValue(border.strokeWidthProperty(), 1.0);
			isSelected = false;
		}
		endFrameStroke = new KeyFrame(Duration.seconds(0.025), endStroke);
		tlStrokeWidth = new Timeline(initFrameStroke, endFrameStroke);
		if (temp == null) {
			temp = new Label();
			temp.setTranslateX(-20);
			temp.setTranslateY(-20);
			temp.setTextFill(Color.BROWN);
			this.getChildren().add(temp);
		}

		tlStrokeWidth.play();
	}
	/**
	 * Change the empty tile to a certain value.
	 */
	private void changeText(String val) {
		Timeline timeline;
		KeyValue initText, endText;
		KeyFrame initFrame, endFrame;
		initText = new KeyValue(text.textProperty(), text.getText());
		initFrame = new KeyFrame(Duration.ZERO, initText);
		endText = new KeyValue(text.textProperty(), val);
		endFrame = new KeyFrame(Duration.seconds(0.025), endText);
		timeline = new Timeline(initFrame, endFrame);
		timeline.play();
	}
	/**
	 * when a digit key is pressed, the application will take that digit
	 * and place it as temp value of a tile. When enter is hit, it will validate if
	 * the temp value is correct, if yes, place that value into the empty tile.
	 * 
	 * @param k key that is pressed by user.
	 */
	private void checkKey(KeyEvent k) {
		KeyCode kc = k.getCode();
		if (kc.isDigitKey()) {
			String n = Character.toString(kc.toString().charAt(5));
			temp.setText(n);
		}
		if (kc.equals(KeyCode.ENTER)) {
			if(temp.getText().equals("")) {
				pressed();
				return;
			}
			int tempVal = Integer.parseInt(temp.getText());
			System.out.println(Visualizer.solution[row][col]);
			if (Visualizer.solution[row][col] == tempVal) {
				this.value = tempVal;
				changeText(temp.getText());
				Visualizer.emptySpots--;
				this.getChildren().remove(temp);
				this.setOnMouseClicked(null);
				this.setOnKeyPressed(null);
			} else {
				temp.setText("");
				Image wrong_icon = new Image("wrong_icon.png");
				ImageView view = new ImageView();
				view.setFitHeight(20);
				view.setFitWidth(20);
				view.setImage(wrong_icon);
				wrong_icons.add(view);
				Visualizer.root.getChildren().add(view);
				view.setTranslateX(610 - Visualizer.numWrong*20);
				view.setTranslateY(670);
				Visualizer.numWrong++;
			}
			Visualizer.selected = null;
			this.getParent().getParent().requestFocus();
			pressed();
		}
		if(Visualizer.numWrong == 2 || Visualizer.emptySpots == 0) {
			for(int i = 0; i < 9; i++) {
				List<Tile> row = Visualizer.board.get(i);
				for(int j = 0; j < 9; j++) {
					Tile tile = row.get(j);
					if(tile.value == 0) {
						tile.setOnMouseClicked(null);
						tile.setOnKeyPressed(null);
					}
				}
			}
			Visualizer.isSolved = true;
			Visualizer.handleContinue();
		}
	}

}
