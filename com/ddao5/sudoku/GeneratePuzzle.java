package com.ddao5.sudoku;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * A JAVA class used to generate a new sudoku puzzle.
 * @author ddao5
 *
 */
public class GeneratePuzzle {
	/**
	 * List of numbers from 1 -> 9.
	 */
	private Integer[] numList = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
	/**
	 * Number of unfilled spots.
	 */
	private int unfilledSpots = 81;
	/**
	 * Number solutions for the current grid.
	 */
	private int numSol = 0;
	/**
	 * Unfilled grid.
	 */
	private int[][] grid = { { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0 } };
	/**
	 * Solution to the puzzle.
	 */
	private int[][] filledGrid = new int[9][9];
	
	/**
	 * Shuffle number list.
	 */
	private void shuffleArray() {
		List<Integer> list = Arrays.asList(numList);
		Collections.shuffle(list);
		list.toArray(numList);
	}
	/**
	 * Initialize the generator.
	 */
	public GeneratePuzzle() {
		fillGrid(grid,unfilledSpots,0,0,0);
		print(grid);
		System.out.println("----------------------------------------");
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				filledGrid[i][j]= grid[i][j];
			}
		}
		generatePuzzle(grid);
		print(grid);
	}
	/**
	 * Get the puzzle.
	 * @return the puzzle
	 */
	public int[][] getGrid() {
		return this.grid;
	}
	/**
	 * Get solution to the puzzle.
	 * @return solution
	 */
	public int[][] getFilledGrid() {
		return filledGrid;
	}
	/**
	 * Get number of empty spots of the puzzle.
	 * @return number of empty spots
	 */
	public int getNumEmptySpots() {
		return numEmptySpots(grid);
	}
	/**
	 * Check if place a given value into a given position is valid.
	 * @param grid grid to be filled
	 * @param row current row that value is placed
	 * @param col current column that value is placed
	 * @param val current value that is placed into the grid
	 * @return true if it is safe, false otherwise
	 */
	private boolean isSafe(int[][] grid, int row, int col, int val) {
		int root_row = row - (row % 3);
		int root_col = col - (col % 3);
		for (int i = 0; i < 9; i++) {
			if (grid[row][i] == val) {
				return false;
			}
		}
		for (int i = 0; i < 9; i++) {
			if (grid[i][col] == val) {
				return false;
			}
		}
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (grid[root_row + i][root_col + j] == val) {
					return false;
				}
			}
		}
		return true;
	}
	/**
	 * Get number of empty spots.
	 * @param grid the puzzle
	 * @return number of empty spots
	 */
	private int numEmptySpots(int grid[][]) {
		int num = 0;
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if (grid[i][j] == 0) {
					num++;
				}
			}
		}
		return num;
	}
	/**
	 * Solve a given sudoku puzzle.
	 * @param grid sudoku puzzle
	 * @param emptySpots number of empty spots
	 * @param row current row
	 * @param col current column
	 * @return true the puzzle is solved, false otherwise
	 */
	private boolean solveGrid(int[][] grid, int emptySpots, int row, int col) {
		if (emptySpots == 0) {
			return true;
		}

		int next_col = 0;
		int next_row = 0;
		for (int i = row; i < 9; i++) {
			for (int j = col; j < 9; j++) {
				if (grid[i][j] != 0) {
					if (j == 8) {
						col = 0;
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
					if (isSafe(grid, i, j, val)) {
						grid[i][j] = val;
						if (numEmptySpots(grid) == 0) {
							numSol++;
							break;
						} else {
							if (solveGrid(grid, emptySpots - 1, next_row, next_col)) {
								return true;
							}
						}

					}
				}
				grid[i][j] = 0;
				return false;
			}
		}
		return false;
	}
	/**
	 * Fill the empty grid with numbers following by the rules of sudoku.
	 * @param grid empty grid
	 * @param unfilledSpots number of unfilled grid
	 * @param row current row
	 * @param col current column
	 * @param numIteration max is 3
	 * @return true if the grid is filled, false otherwise.
	 */
	private boolean fillGrid(int[][] grid, int unfilledSpots, int row, int col, int numIteration) {
		if (unfilledSpots == 0) {
			return true;
		}
		int next_c = 0;
		int next_r = 0;
		int next_i = 0;
		for (int i = numIteration; i < 3; i++) {
			for (int c = col; c < 9; c++) {
				if (c % 3 == 0) {
					shuffleArray();
				}
				for (int r = row; r < 3; r++) {
					int currR = 3 * i + r;
					if (grid[currR][c] != 0) {
						if (r == 2) {
							row = 0;
						}
						continue;
					}
					if (r == 2) {
						next_r = 0;
						if (c == 8) {
							next_c = 0;
							next_i = i + 1;
						} else {
							next_c = c + 1;
						}
					} else {
						next_r = r + 1;
						next_c = c;
						next_i = i;
					}

					for (int number : numList) {
						if (isSafe(grid, currR, c, number)) {
							grid[currR][c] = number;
							if (fillGrid(grid, unfilledSpots - 1, next_r, next_c, next_i)) {
								return true;
							}
						}
					}
					grid[currR][c] = 0;
					return false;
				}
			}
		}
		return false;
	}
	/**
	 * Taking off random numbers to create sudoku puzzle.
	 * @param grid filled grid.
	 */
	private void generatePuzzle(int[][] grid) {
		int attempts = 5;
		int temp = 0;
		while (attempts > 0) {
			Random rand = new Random();
			int row = rand.nextInt(9);
			int col = rand.nextInt(9);
			while (grid[row][col] == 0) {
				row = rand.nextInt(8) + 1;
				col = rand.nextInt(8) + 1;
			}
			temp = grid[row][col];
			grid[row][col] = 0;
			int[][] copy = new int[9][9];
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					copy[i][j] = grid[i][j];
				}
			}
			numSol = 0;
			solveGrid(copy, numEmptySpots(copy), 0, 0);
			if (numSol != 1) {
				grid[row][col] = temp;
				attempts--;
			}
		}
		return;
	}
	/**
	 * Print current grid.
	 * @param grid grid
	 */
	private void print(int[][] grid) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				System.out.print(grid[i][j] + " ");
			}
			System.out.println("");
		}
	}
}
