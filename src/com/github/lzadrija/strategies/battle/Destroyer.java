package com.github.lzadrija.strategies.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.lzadrija.exceptions.DataNotAvailableException;
import com.github.lzadrija.model.common.Point;
import com.github.lzadrija.model.map.Grid;
import com.github.lzadrija.model.ships.Ship;
import com.github.lzadrija.repositories.ShipsRepository;
import com.github.lzadrija.strategies.FireResult;
import com.github.lzadrija.strategies.IExecutor;

/**
 * @author Lucija Zadrija
 * 
 *         This service implements algorithms for ship's localization and
 *         recognition. The first algorithm is used for finding the ship's
 *         location on the grid by targeting points on the grid he that are most
 *         likely to contain ships (Localization mode). The second algorithm
 *         (Elimination mode), starts if the ship is hit. Based on the ability
 *         of the hidden ships to be positioned on the grid through a hit, most
 *         probable cells around a hit are targeted until no more ships can be
 *         positioned trough the hit area. All possible combinations of ships
 *         that can be positioned over found hit area are computed in order to
 *         find out how many ships are located on the hit area and what are
 *         their upper left coordinates.
 * 
 */
public class Destroyer implements IDestroyer {

	private static final Logger logger = Logger.getLogger(Destroyer.class
			.getName());
	private final IExecutor executor;
	private final Grid grid;

	private static TargetFinder locator;
	private static ShipsRepository shipsRepository;

	/**
	 * Constructor.
	 * 
	 * @param executor
	 *            Used for discovering grid content on the specified position.
	 * @param grid
	 *            Representation of the (sea) map.
	 * @throws NullPointerException
	 *             If executor or grid are null.
	 */
	public Destroyer(IExecutor executor, Grid grid) {

		if (null == executor) {
			throw new NullPointerException("Executor cannot be null");
		}
		if (null == grid) {
			throw new NullPointerException("Grid can not be null");
		}

		this.executor = executor;
		this.grid = grid;

		locator = ShipLocator.getInstance();
		shipsRepository = ShipsRepository.getInstance();
	}

	@Override
	public void sinkBattleShips() {

		while (!shipsRepository.isFleetSunk()) {

			Point shipPosition = locateShips();

			eliminateShipsAroundPoint(shipPosition);

			System.out.println(shipsRepository.toString());
		}
		System.out.println(grid.toString());
	}

	/**
	 * Used for finding ship's location on the grid. The grid is targeted and
	 * fired at until a cell that contains a ship is found.
	 * 
	 * @return Coordinates of the cell on the grid that contains a ship.
	 */
	private Point locateShips() {

		Point targetPosition = null;
		FireResult fireResult = FireResult.MISS;

		while (true) {
			targetPosition = locator.getNextObjective(grid, shipsRepository);
			try {
				fireResult = executor.fire(targetPosition);
			} catch (DataNotAvailableException dataNotAvailableException) {
				logger.log(Level.SEVERE, null, dataNotAvailableException);
				continue;
			}
			if (fireResult.toString().equals(FireResult.HIT.toString())) {
				break;
			}
			grid.occupyCellAt(targetPosition);
		}
		return targetPosition;
	}

	/**
	 * Used for finding the size of the hit area after the ship was located on
	 * the grid. The area around the given hit is being targeted as long as
	 * there are hidden ships that can be placed trough found hits. When the
	 * size of the hit area is found, the process of identifying ships on that
	 * area is started.
	 * 
	 * @param shipsPosition
	 *            Hit point on the grid that contains ship.
	 */
	private void eliminateShipsAroundPoint(Point shipsPosition) {
		ShipEliminator eliminator = new ShipEliminator(shipsPosition);
		Point targetPosition = null;
		FireResult fireResult = FireResult.MISS;

		while (true) {
			targetPosition = eliminator.getNextObjective(grid, shipsRepository);
			boolean areShipsEliminated = eliminator
					.areAllShipsAtThisAreaEliminated(targetPosition);
			if (areShipsEliminated) {
				break;
			}
			try {
				fireResult = executor.fire(targetPosition);
			} catch (DataNotAvailableException dataNotAvailableException) {
				logger.log(Level.SEVERE, null, dataNotAvailableException);
				continue;
			}
			if (fireResult.toString().equals(FireResult.HIT.toString())) {
				eliminator.addHit(targetPosition);
			} else {
				grid.occupyCellAt(targetPosition);
			}
		}
		grid.occupyArea(eliminator.getHitsCoordinatesList());
		identifySunkShips(eliminator.getHitsCoordinatesList());
	}

	/**
	 * Checks which combination of hidden ships fits best the found hit area.
	 * For this to be achieved, all combinations of hidden ships based on their
	 * size are computed. Every combination is examined and the right one is
	 * found.
	 * 
	 * @param hitsPointsList
	 *            List of points from the grid that contain ship(s).
	 */
	public void identifySunkShips(List<Point> hitsPointsList) {

		int hitAreaSize = hitsPointsList.size();
		List<List<Ship>> shipsSizeCombinations = shipsRepository
				.getAllHiddenShipsSizeCombinations(hitAreaSize);

		for (Iterator<List<Ship>> iterator = shipsSizeCombinations.iterator(); iterator
				.hasNext();) {
			List<Ship> combination = iterator.next();

			Collections.sort(combination);

			Collections.sort(hitsPointsList);
			boolean match = findSunkShipsPositionsOnGrid(hitsPointsList,
					combination);

			if (match) {
				break;
			}
		}
	}

	/**
	 * Recursive method. Based on the given combination of ships whose sum of
	 * sizes is equal to the size of the hit area, the combination that fits the
	 * hit area best is found. Ships are sequentially being placed on the points
	 * from the hit area, largest first. All other ships are positioned next to
	 * it in order to find out if they fit in the hit area. When the perfect
	 * match is found, algorithm is stopped and ships positions revealed.
	 * 
	 * @param hitsPointsList
	 *            List of points from the grid that contain ship(s).
	 * @param shipsCombination
	 *            . List of ships whose sum of sizes is equal to the size of the
	 *            hit area.
	 * @return True if all ships from the combination fit into the hit area.
	 * @throws IllegalArgumentException
	 *             If this repository does not contain ship from the given
	 *             combination.
	 */
	private boolean findSunkShipsPositionsOnGrid(List<Point> hitsPointsList,
			List<Ship> shipsCombination) {

		Ship largestShip = shipsCombination.get(shipsCombination.size() - 1);

		List<Ship> tempShipsCombination = new ArrayList<>(shipsCombination);
		tempShipsCombination.remove(largestShip);

		for (Iterator<Point> iterator2 = hitsPointsList.iterator(); iterator2
				.hasNext();) {
			Point hitPoint = iterator2.next();
			List<Point> tempHitsPointsList = new ArrayList<>(hitsPointsList);

			boolean largestShipFits = isShipInModifiedHitArea(hitsPointsList,
					hitPoint, largestShip.getStructure());
			if (largestShipFits) {
				removeShipsFromHitsArea(tempHitsPointsList, hitPoint,
						largestShip.getStructure());
				if (tempHitsPointsList.isEmpty()) {
					shipsRepository.exposeShip(largestShip, hitPoint);
					return true;
				} 
				boolean match = findSunkShipsPositionsOnGrid(
						tempHitsPointsList, tempShipsCombination);
				if (true == match) {
					shipsRepository.exposeShip(largestShip, hitPoint);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Removes points that are possible coordinates of the given ship for the
	 * given upper left point from the list of hit points.
	 * 
	 * @param hitsPointsList
	 *            List of points from the grid that contain ship(s).
	 * @param upperLeftHit
	 *            Upper left coordinate that is potential upper left coordinate
	 *            of a ship from the hit area on the grid.
	 * @param shipsStructure
	 *            Ship's structure.
	 */
	private void removeShipsFromHitsArea(List<Point> hitsPointsList,
			Point upperLeftHit, List<Point> shipsStructure) {

		for (Iterator<Point> iterator = shipsStructure.iterator(); iterator
				.hasNext();) {
			Point relativePosition = iterator.next();

			Point hitCoordinate = upperLeftHit
					.getPointRelativeToThis(relativePosition);

			if (hitsPointsList.contains(hitCoordinate)) {
				hitsPointsList.remove(hitCoordinate);
			}
		}
	}

	/**
	 * Checks if the ship fits in the given hit area. This is called after some
	 * points (that other ships fit into) from the hit area were removed.
	 * 
	 * @param hitsPointsList
	 *            List of points from the grid that contain ship(s).
	 * @param upperLeftHit
	 *            Upper left coordinate that is potential upper left coordinate
	 *            of a ship from the hit area on the grid.
	 * @param shipsStructure
	 *            Ship's structure.
	 * @return True if the ship fits, false otherwise.
	 */
	private boolean isShipInModifiedHitArea(List<Point> hitsPointsList,
			Point upperLeftHit, List<Point> shipsStructure) {

		for (Iterator<Point> iterator = shipsStructure.iterator(); iterator
				.hasNext();) {
			Point relativePosition = iterator.next();

			Point hitCoordinate = upperLeftHit
					.getPointRelativeToThis(relativePosition);

			if (!hitsPointsList.contains(hitCoordinate)) {
				return false;
			}
		}
		return true;
	}
}
