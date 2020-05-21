package com.ddao5.sudoku;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.animation.SequentialTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
/**
 * A javafx application that allows users to solve a sudoku puzzle,
 * or they can watch the application solve the current puzzle.
 * @author ddao5
 */
public class Visualizer extends Application {
	

	/**
	 * Number of incorrect choices.
	 */
	protected static int numWrong = 0;
	/**
	 * The button that when it is clicked, the application will start solving the puzzle.
	 */
	protected static Button solve;
	/**
	 * The root layout of the application.
	 */
	protected static Pane root;
	/**
	 * The puzzle board.
	 */
	protected static List<List<Tile>> board;
	/**
	 * An array representing the puzzle used to visualize the board.
	 */
	private static int[][] grid = new int[9][9];
	/**
	 * Holding solution of the current puzzle to validate user's choices.
	 */
	protected static int[][] solution = new int[9][9];
	/**
	 * Determined whether is solved or not.
	 */
	protected static boolean isSolved = false;
	/**
	 * Javafx transition used to visualize the process of solving the current puzzle.
	 */
	protected static SequentialTransition st = new SequentialTransition();
	/**
	 * Window of the application.
	 */
	private static Stage window;
	/**
	 * Layout to hold our puzzle board.
	 */
	private static Pane layoutBoard;
	/**
	 * The tile that is being selected.
	 */
	protected static Tile selected = null;
	/**
	 * Dialog to make user to choose to solve another puzzle or quit the application.
	 */
	private static VBox dialog = null;
	/**
	 * Font used for text in dialog.
	 */
	private static Font font = Font.font("Arial", FontWeight.BOLD, 15);
	/**
	 * Media player to play background music.
	 */
	private MediaPlayer mediaPlayer;
	/**
	 * Number of empty tiles.
	 */
	protected static int emptySpots;
	/**
	 * Main method to run the application.
	 * @param args not used
	 */
	public static void main(String[] args) {
		launch(args);
	}
	/**
	 * Creating the puzzle and lay it out into the applicaiton's window.
	 */
	private void createContent() {
		root = new Pane();
		root.setPrefSize(650, 720);
		
		layoutBoard = new Pane();
		layoutBoard.setTranslateX(15);
		layoutBoard.setTranslateY(10);
		layoutBoard.setPrefSize(630, 630);
		layoutBoard.requestFocus();
		layoutBoard.setFocusTraversable(true);
		BorderStrokeStyle style = new BorderStrokeStyle(StrokeType.OUTSIDE, StrokeLineJoin.MITER, StrokeLineCap.SQUARE,
				1, 0, null);
		BorderStroke stroke_border = new BorderStroke(Color.BLACK, style, CornerRadii.EMPTY, new BorderWidths(4), null);
		layoutBoard.setBorder(new Border(stroke_border));
		

		makeBoard();
		solve = new Button("SOLVE");
		solve.setOnAction(e -> handleSolve());
		solve.setTranslateY(660);
		solve.setTranslateX(10);
		solve.setPrefSize(70, 50);
		root.getChildren().addAll(layoutBoard, solve);
	}
	/**
	 * Start the application.
	 * @param stage main window
	 */
	public void start(Stage stage) throws Exception {
		window = stage;
		createContent();
		window.setScene(new Scene(root));
		window.setTitle("Sudoku Solver");
		window.setResizable(false);
		Media sound = new Media(new File("C:\\Users\\ducda\\eclipse-workspace\\BSSTUFF\\src\\com\\ddao5\\sudoku\\background_song.mp3").toURI().toString());
		mediaPlayer = new MediaPlayer(sound);
		mediaPlayer.setVolume(0.1);
		mediaPlayer.setAutoPlay(true);
		
		mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
	
		window.show();
		
	}
	/**
	 * Generating a new puzzle after the current puzzle is solved or
	 * user has made two incorrect choices.
	 */
	public static void newGame() {
		for (int i = 0; i < 9; i++) {
			layoutBoard.getChildren().removeAll(board.get(i));
		}
		makeBoard();
		root.getChildren().removeAll(Tile.wrong_icons);
		numWrong = 0;
		isSolved = false;
		st.getChildren().removeAll(SudokuSolver.animationList);
		SudokuSolver.animationList = new LinkedList<>();
	}
	/**
	 * A helper method to create a board from an array representing
	 * the current puzzle.
	 */
	private static void makeBoard() {
		board = new ArrayList<>();
		GeneratePuzzle gp = new GeneratePuzzle();
		grid = gp.getGrid();
		solution = gp.getFilledGrid();
		emptySpots = gp.getNumEmptySpots();
		for (int i = 0; i < 9; i++) {
			List<Tile> row = new ArrayList<>();
			for (int j = 0; j < 9; j++) {
				int num = grid[i][j];
				row.add(new Tile(Integer.toString(num), i, j));
			}
			board.add(row);
		}
		for (int i = 0; i < 9; i++) {
			List<Tile> row = board.get(i);
			for (int j = 0; j < 9; j++) {
				Tile tile = row.get(j);
				tile.setTranslateX(70 * j);
				tile.setTranslateY(70 * i);
				if (i == 0 || i == 1 || i == 2 || i == 6 || i == 7 || i == 8) {
					if (j == 3 || j == 4 || j == 5) {
						tile.border.setFill(Color.GREENYELLOW);
					}
				} else if (i == 3 || i == 4 || i == 5) {
					if (j == 0 || j == 1 || j == 2 || j == 6 || j == 7 || j == 8) {
						tile.border.setFill(Color.GREENYELLOW);
					}
				}
				layoutBoard.getChildren().add(tile);
			}
		}
	}
	
	/**
	 * An event used when the "Solve" button is clicked.
	 * It will display a message to let user to wait for the solution
	 * of current puzzle.
	 */
	private void handleSolve() {
		if (isSolved) {
			return;
		}
		
		new SudokuSolver(board);
		dialog = new VBox();
		dialog.setTranslateX(250);
		dialog.setTranslateY(665);
		dialog.setSpacing(2);
		dialog.setAlignment(Pos.CENTER);

		if (isSolved) {
			
			Label processing = new Label("Processing the Puzzle!!\nPlease Wait!");
			processing.setFont(font);
			processing.setAlignment(Pos.CENTER);
			dialog.getChildren().add(processing);
			root.getChildren().add(dialog);
			st.play();
			
			st.setOnFinished(event -> 
			{
				dialog.getChildren().remove(processing);
				root.getChildren().remove(dialog);
				dialog = null;
				handleContinue();
			});
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					System.out.print(solution[i][j] + " ");
				}
				System.out.println();
			}
		}
		

	}
	/**
	 * Handling options after the puzzle is solved.
	 * User can choose to play another puzzle or quit the program.
	 */
	public static void handleContinue() {
		dialog = new VBox();
		dialog.setTranslateX(280);
		dialog.setTranslateY(650);
		dialog.setSpacing(2);
		dialog.setAlignment(Pos.CENTER);
		Label question = new Label("Another Puzzle ?");
		
		question.setFont(font);
		Button yes = new Button("YES");
		Button no = new Button("NO");
		yes.setFont(font);
		no.setFont(font);
		yes.prefWidthProperty().bind(dialog.widthProperty().divide(2));
		no.prefWidthProperty().bind(dialog.widthProperty().divide(2));

		yes.setOnAction(e -> handleYes());
		no.setOnAction(e -> window.close());
		dialog.getChildren().addAll(question, yes, no);
		root.getChildren().add(dialog);
	}
	/**
	 * If user chose to play another puzzle, clean up the old puzzle.
	 */
	private static void handleYes() {
		root.getChildren().remove(dialog);
		dialog = null;
		newGame();
		return;
	}

}
