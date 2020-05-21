package com.ddao5.sudoku;
import java.util.LinkedList;
import java.util.List;
import javafx.animation.Animation;


/**
 * A Java class that solves a sudoku puzzle.
 * @author ddao5
 *
 */
public class SudokuSolver {
	/**
	 * Animation list.
	 */
	protected static List<Animation> animationList = new LinkedList<>();
	/**
	 * Count times that the recursive function is called.
	 */
	private static int numRecursive = 0;
	
	/**
	 * Initialize the solver.
	 * @param board the current puzzle
	 */
	public SudokuSolver(List<List<Tile>> board) {
		int emptySpots = numEmptySpot(board);
		for (List<Tile> list : board) {
			for (Tile tile : list) {
				if (tile.value == 0) {
					tile.setOnMouseClicked(null);
					tile.setOnKeyPressed(null);
				}
			}
		}
		if (sudokuSolver(board, emptySpots, 0, 0)) {
			Visualizer.isSolved = true;
			Visualizer.st.getChildren().addAll(animationList);
		}

	}

	/**
	 * Determine if placing a certain value into given position is following Sudoku's rules.
	 * @param board sudoku puzzle
	 * @param row row where the value is placed
	 * @param col column where the value is placed
	 * @param val value
	 * @return true if placing the current value into the given position is safe, false otherwise
	 */
	private boolean isSafe(List<List<Tile>> board, int row, int col, int val) {
		int root_row = row - (row % 3);
		int root_col = col - (col % 3);
		for (int i = 0; i < 9; i++) {
			Tile tile = board.get(row).get(i);
			if (tile.value == val) {
				return false;
			}
		}
		for (int i = 0; i < 9; i++) {
			Tile tile = board.get(i).get(col);
			if (tile.value == val) {
				return false;
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Tile tile = board.get(root_row + i).get(root_col + j);
				if (tile.value == val) {
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * Count how many empty spots are in the board.
	 * @param board sudoku puzzle
	 * @return number of empty spots.
	 */
	private int numEmptySpot(List<List<Tile>> board) {
		int num = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				Tile tile = board.get(i).get(j);
				if (tile.value == 0) {
					num++;
				}
			}
		}
		return num;
	}
	/**
	 * Solve the puzzle.
	 * @param board sudoku puzzle
	 * @param emptySpots number of empty spots
	 * @param row current row
	 * @param col current column
	 * @return true if the puzzle is solved, false otherwise.
	 */
	public boolean sudokuSolver(List<List<Tile>> board, int emptySpots, int row, int col) {
		if (emptySpots == 0)
			return true;
		System.out.println(++numRecursive);
		int next_col = 0;
		int next_row = 0;
		for (int i = row; i < 9; i++) {
			for (int j = col; j < 9; j++) {
				Tile tile = board.get(i).get(j);
				if (tile.value != 0) {
					if (j == 8) {
						col = 0;
					}
					if(tile.border.getStrokeWidth() == 1.0) {
						animationList.add(tile.changeTextAnimation(Integer.toString(tile.value)));
					}
					continue;
				}
				if (j == 8) {
					next_row = i + 1;
					next_col = 0;
				} else {
					next_col = j + 1;
					next_row = i;
				}
				// check if place a number from 1-9 can make the grid unsafe,
				// if safe place that number into the free cell, and move to the next cell
				for (int val = 1; val <= 9; val++) {
					if (isSafe(board, i, j, val)) {
						animationList.add(tile.changeTextAnimation(Integer.toString(val)));
						if (sudokuSolver(board, emptySpots - 1, next_row, next_col)) {
							return true;
						}
					}
				}
				animationList.add(tile.changeTextAnimation("0"));
				return false;
			}
		}
		return false;
	}
}
