package com.github.lzadrija.strategies.battle;

import java.util.Iterator;
import java.util.List;

import com.github.lzadrija.model.common.Point;
import com.github.lzadrija.model.map.Grid;
import com.github.lzadrija.repositories.ShipsRepository;

/**
 * @author Lucija Zadrija
 * 
 *         Used for finding the next target on the grid when in Localization
 *         mode. Ship's location in unknown, so the next target must be chosen
 *         in a way the highest number of yet undiscovered ships can be placed
 *         over it. for each hidden ship and each location on the grid (map) it
 *         is checked if this ship can be placed in this position (in a way that
 *         it does not collide with other ships, nor it is placed over cells
 *         that are known to contain only water). If a ship can be placed over a
 *         cell, cell's likelihood is then increased.
 */
public class ShipLocator extends TargetFinder {

	private static ShipLocator instance = new ShipLocator();

	public static ShipLocator getInstance() {
		return instance;
	}

	@Override
	protected Point getNextObjective(Grid grid, ShipsRepository shipsRepository) {

		int[][] gridOccupationLikelihoods = new int[grid.getHeight()][grid
				.getWidth()];
		Point targetsPosition = null;

		for (Iterator<Integer> iterator = shipsRepository
				.getHiddenShipsIndexes().iterator(); iterator.hasNext();) {
			int i = iterator.next();
			for (int j = 0; j < grid.getHeight(); j++) {

				int shipsHeight = shipsRepository.getShipsHeight(i);
				if (!grid.doesStructureFitInColumn(shipsHeight, j)) {
					break;
				}
				for (int k = 0; k < grid.getWidth(); k++) {

					int shipsWidth = shipsRepository.getShipsWidth(i);
					if (!grid.doesStructureFitInRow(shipsWidth, k)) {
						break;
					}
					Point shipsUpperLeftPoint = new Point(k, j);
					List<Point> shipsStructure = shipsRepository
							.getShipsStructure(i);
					boolean isGridAvailable = grid.isGridPortionUndefined(
							shipsUpperLeftPoint, shipsStructure);
					if (isGridAvailable) {
						increaseGridOccupationLikelihood(shipsUpperLeftPoint,
								shipsStructure, gridOccupationLikelihoods);
					}
				}
			}
		}
		targetsPosition = getTargetPosition(gridOccupationLikelihoods);
		return targetsPosition;
	}

	/**
	 * Increases the likelihood that the ship can be placed over cells on the
	 * grid.
	 * 
	 * @param shipsUpperLeftPoint
	 *            Ship's upper left coordinate on the grid.
	 * @param shipsStructure
	 *            Ship's structure.
	 * @param cellLikelihoods
	 *            2D array of cell likelihoods.
	 */
	protected void increaseGridOccupationLikelihood(Point shipsUpperLeftPoint,
			List<Point> shipsStructure, int[][] cellLikelihoods) {

		for (Iterator<Point> iterator = shipsStructure.iterator(); iterator
				.hasNext();) {
			Point relativeCellPosition = iterator.next();

			int x = shipsUpperLeftPoint.getX() + relativeCellPosition.getX();
			int y = shipsUpperLeftPoint.getY() + relativeCellPosition.getY();

			cellLikelihoods[y][x]++;
		}

	}
}
