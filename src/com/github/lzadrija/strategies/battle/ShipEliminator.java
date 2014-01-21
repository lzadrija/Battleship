package com.github.lzadrija.strategies.battle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.lzadrija.model.common.Point;
import com.github.lzadrija.model.map.Grid;
import com.github.lzadrija.repositories.ShipsRepository;

/**
 * Used for finding the next target on the grid when in Elimination mode. This class gets the first hit point and produces next targets
 * in order to discover all ships that are contained in this area. A ship can be placed through a hit if it passes only through undefined
 * cells or other known hits. If a ship can be placed through a cell, its probability is increased, and most probable cells around a hit
 * are targeted until no more ships can be positioned trough the hit area. The result is the list of coordinates that is proven to contain
 * part of the ship(s).
 *         
 * @author Lucija Zadrija
 *  
 */
public class ShipEliminator extends TargetFinder {

	private List<Point> hitsCoordinatesList;

	/**
	 * Constructor.
	 * 
	 * @param firstHit
	 *            Point were the ship was first hit (discovered).
	 */
	public ShipEliminator(Point firstHit) {
		hitsCoordinatesList = new ArrayList<>();
		hitsCoordinatesList.add(firstHit);
	}

	@Override
	protected Point getNextObjective(Grid grid, ShipsRepository shipsRepository) {

		int[][] gridOccupationLikelihoods = new int[grid.getHeight()][grid.getWidth()];
		Point targetsPosition = null;
		int nAvailablePositions = 0;

		for (Iterator<Point> iterator = hitsCoordinatesList.iterator(); iterator.hasNext();) {
			Point hitPoint = iterator.next();

			// pass through hit coordinate with every undiscovered ship
			for (Iterator<Integer> iterator2 = shipsRepository.getHiddenShipsIndexes().iterator(); iterator2.hasNext();) {
				int i = iterator2.next();
				List<Point> shipsAllUpperLeftPoint = shipsRepository.getShipsAllPossibleUpperLeftCoordinates(i, hitPoint);

				// check if undiscovered ship can pass through hit on grid in every way possible
				List<Point> shipsStructure = shipsRepository.getShipsStructure(i);
				for (Iterator<Point> iterator3 = shipsAllUpperLeftPoint.iterator(); iterator3.hasNext();) {
					Point shipsUpperLeftPoint = iterator3.next();

					boolean isGridAvailable = grid.isGridPortionUndefined(shipsUpperLeftPoint, shipsStructure);
					if (isGridAvailable) {
						boolean isLikeliHoodIncreased = increaseGridOccupationLikelihood(shipsUpperLeftPoint, shipsStructure,
																						 gridOccupationLikelihoods);
						nAvailablePositions += isLikeliHoodIncreased ? 1 : 0;
					}
				}
			}
		}
		if (0 != nAvailablePositions) {
			targetsPosition = getTargetPosition(gridOccupationLikelihoods);
		}
		return targetsPosition;
	}

	/**
	 * Increases the likelihood that the ship can be placed over cells on the grid.
	 * 
	 * @param shipsUpperLeftPoint
	 *            Ship's upper left coordinate on the grid.
	 * @param shipsStructure
	 *            Ship's structure.
	 * @param cellLikelihoods
	 *            2D array of cell likelihoods.
	 * @return True if ship can be placed over cells on the grid, false otherwise.
	 */
	protected boolean increaseGridOccupationLikelihood(Point shipsUpperLeftPoint, List<Point> shipsRelativeCoordinates,
													   int[][] cellLikelihoods) {
		boolean isLikeliHoodIncreased = false;
		for (Iterator<Point> iterator = shipsRelativeCoordinates.iterator(); iterator.hasNext();) {
			Point relativeCellPosition = iterator.next();

			Point newPoint = shipsUpperLeftPoint.getPointRelativeToThis(relativeCellPosition);

			if (isPointAHit(newPoint)) {
				continue;
			}
			isLikeliHoodIncreased = true;
			cellLikelihoods[newPoint.getY()][newPoint.getX()]++;
		}
		return isLikeliHoodIncreased;
	}

	/**
	 * Checks if ship was previously hit at this point.
	 * 
	 * @param point
	 *            Point to check.
	 * @return True if ship was previously hit at this point, false otherwise.
	 */
	private boolean isPointAHit(Point point) {

		for (Iterator<Point> iterator = hitsCoordinatesList.iterator(); iterator.hasNext();) {
			Point hitPoint = iterator.next();

			if (hitPoint.equals(point)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds the position of the cell that is found to be a hit to the hits list.
	 * 
	 * @param hitPoint
	 *            Position of the cell that is found to be a hit.
	 */
	public void addHit(Point hitPoint) {
		hitsCoordinatesList.add(hitPoint);
	}

	/**
	 * Checks if next target that passes through hits cannot be computed.
	 * 
	 * @param targetPosition
	 * @return True if all ships at hit area are eliminated, false otherwise.
	 */
	public boolean areAllShipsAtThisAreaEliminated(Point targetPosition) {

		return null == targetPosition;
	}

	public List<Point> getHitsCoordinatesList() {
		return hitsCoordinatesList;
	}
}
