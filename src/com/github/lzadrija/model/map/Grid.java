package com.github.lzadrija.model.map;

import java.util.Iterator;
import java.util.List;

import com.github.lzadrija.model.common.Point;

/**
 * @author Lucija Zadrija
 * 
 *         This class represents the (sea) map as a grid which has its height
 *         and its width. Every position on the grid, a cell, is either
 *         occupied, meaning that it contains structure (ship) or nothing
 *         (water), or undefined - meaning that it is unknown what lays on this
 *         position.
 */
public class Grid {

	private final int height;
	private final int width;

	private Cell[][] grid;

	/**
	 * Constructor.
	 * 
	 * @param heigh
	 *            Height.
	 * @param width
	 *            Width.
	 * @throws IndexOutOfBoundsException
	 *             If height or width are zero or negative.
	 */
	public Grid(int height, int width) {

		boolean isHeightValid = isDimensionValid(height);
		if (!isHeightValid) {
			throw new IndexOutOfBoundsException("Grid height out of range: "
					+ height);
		}

		boolean isWidthValid = isDimensionValid(width);
		if (!isWidthValid) {
			throw new IndexOutOfBoundsException("Grid weight out of range: "
					+ width);
		}
		this.width = width;
		this.height = height;

		grid = new Cell[height][width];
		initializeGrid();
	}

	/**
	 * Initializes grid. When grid is created, all cells are set as undefined.
	 */
	private void initializeGrid() {

		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				grid[j][i] = Cell.UNDEFINED_CELL;
			}
		}
	}

	/**
	 * Checks if grid dimension is zero or negative.
	 * 
	 * @param dimension
	 *            Dimension.
	 * @return true if dimension is greater than zero, false otherwise.
	 */
	private boolean isDimensionValid(int dimension) {
		return dimension > 0;
	}

	/**
	 * Checks if the cell at the given position is occupied.
	 * 
	 * @param point
	 *            position of the cell on the grid.
	 * @return true if cell is occupied, false otherwise.
	 * @throws IndexOutOfBoundsException
	 *             If cell's coordinates are out of bounds.
	 */
	public boolean isCellOccupiedAt(Point point) {

		if (!isCellPositionValid(point)) {
			throw new IndexOutOfBoundsException("Wrong coordinate(s) format: "
					+ point.toString());
		}

		Cell cellType = grid[point.getY()][point.getX()];

		boolean isCellOccupied = cellType.toString().equals(
				Cell.OCCUPIED_CELL.toString());

		return isCellOccupied;
	}

	/**
	 * Sets the cell at the given position to occupied.
	 * 
	 * @param point
	 *            Cell's position.
	 * @throws IndexOutOfBoundsException
	 *             If cell's coordinates are out of bounds.
	 */
	public void occupyCellAt(Point point) {

		if (!isCellPositionValid(point)) {
			throw new IndexOutOfBoundsException("Wrong coordinate(s) format: "
					+ point.toString());
		}
		grid[point.getY()][point.getX()] = Cell.OCCUPIED_CELL;
	}

	/**
	 * Sets the cells at the given positions on the grid to occupied.
	 * 
	 * @param area
	 *            Grid area that needs to be occupied.
	 * @throws IndexOutOfBoundsException
	 *             If cell's coordinates are out of bounds.
	 */
	public void occupyArea(List<Point> area) {

		for (Iterator<Point> iterator = area.iterator(); iterator.hasNext();) {
			Point point = iterator.next();
			occupyCellAt(point);
		}
	}

	/**
	 * Checks if the position is a valid point on the grid.
	 * 
	 * @param position
	 *            Position to be checked
	 * @return true if point's coordinates are valid
	 */
	private boolean isCellPositionValid(Point position) {
		if (!isXCoordinateValid(position.getX())
				|| !isYCoordinateValid(position.getY())) {
			return false;
		}
		return true;
	}

	/**
	 * Checks if the given point has valid x coordinate.
	 * 
	 * @param xCoordinate
	 *            x coordinate.
	 * @return true if x coordinate is valid.
	 */
	private boolean isXCoordinateValid(int xCoordinate) {
		return xCoordinate >= 0 && xCoordinate < width;
	}

	/**
	 * Checks if the given point has valid y coordinate.
	 * 
	 * @param yCoordinate
	 *            y coordinate.
	 * @return true if y coordinate is valid.
	 */
	private boolean isYCoordinateValid(int yCoordinate) {
		return yCoordinate >= 0 || yCoordinate < height;
	}

	/**
	 * Check if the structure fits on the grid by checking its height.
	 * 
	 * @param structuresHeight
	 *            Structure's height.
	 * @param yCoordinate
	 *            Structure's y coordinate on the grid.
	 * @return true if structure fits on the grid by its height, or false
	 *         otherwise.
	 */
	public boolean doesStructureFitInColumn(int structuresHeight,
			int yCoordinate) {
		int structuresPositionInColumn = (structuresHeight - 1) + yCoordinate;
		return structuresPositionInColumn <= (height - 1);
	}

	/**
	 * Check if the structure fits on the grid by checking its width.
	 * 
	 * @param structuresWidth
	 *            Structure's width.
	 * @param xCoordinate
	 *            Structure's x coordinate on the grid.
	 * @return true if structure fits on the grid by its width, or false
	 *         otherwise.
	 */
	public boolean doesStructureFitInRow(int structuresWidth, int xCoordinate) {
		int structuresPositionInRow = (structuresWidth - 1) + xCoordinate;
		return structuresPositionInRow <= (width - 1);
	}

	/**
	 * Checks if the portion of the grid is occupied, in other words, if all
	 * cells at the given positions are occupied. Cell's positions are
	 * calculated using upper left coordinates on the grid and an array of
	 * offset points that are used to compute points relative to the given upper
	 * left point.
	 * 
	 * @param upperLeftPoint
	 *            Upper left coordinates from the grid.
	 * @param offsetsList
	 *            List of offsets for computing grid portion coordinates.
	 * @return true if the given grid portion is occupied, false otherwise.
	 */
	public boolean isGridPortionOccupied(Point upperLeftPoint,
			List<Point> offsetsList) {

		for (Iterator<Point> iterator = offsetsList.iterator(); iterator
				.hasNext();) {
			Point relativeCellPosition = iterator.next();

			Point newPoint = upperLeftPoint
					.getPointRelativeToThis(relativeCellPosition);

			if (!isCellOccupiedAt(newPoint)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Checks if the portion of the grid is undefined, in other words, if all
	 * cells at the given positions are undefined. Cell's positions are
	 * calculated using upper left coordinates on the grid and an array of
	 * offset points that are used to compute points relative to the given upper
	 * left point.
	 * 
	 * @param upperLeftPoint
	 *            Upper left coordinates from the grid.
	 * @param offsetsList
	 *            List of offsets for computing grid portion coordinates.
	 * @return true if the given grid portion is undefined, false otherwise.
	 */
	public boolean isGridPortionUndefined(Point upperLeftPoint,
			List<Point> offsetsList) {

		for (Iterator<Point> iterator = offsetsList.iterator(); iterator
				.hasNext();) {
			Point relativeCellPosition = iterator.next();

			Point newPoint = upperLeftPoint
					.getPointRelativeToThis(relativeCellPosition);

			if (isCellOccupiedAt(newPoint)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns representation of point. The exact details of the representation
	 * are subject to change, but the following may be regarded as typical:
	 * 
	 * 0 0 0 0 0 0 1 0 0 0 0 1 1 1 0 0 0 0 0 1 0 0 0 
	 * 0 0 0 1 0 0 0 0 1 0 0 1 0 1 0 1 0 0 0 0 0 0 0 
	 * 0 0 0 0 0 1 0 0 0 0 0 1 0 1 0 1 0 0 1 0 0 0 0 
	 * 0 0 1 0 0 0 0 0 0 0 1 1 1 0 1 0 0 0 0 0 0 0 0 
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[i].length; j++) {
				sb.append(grid[i][j].getCode()).append(" ");
			}
			sb.append("\n");
		}
		return sb.toString();
	}

	public Cell[][] getGrid() {
		return grid;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
}
