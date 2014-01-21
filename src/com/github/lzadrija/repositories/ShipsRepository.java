package com.github.lzadrija.repositories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.lzadrija.model.common.Point;
import com.github.lzadrija.model.services.ShipsCombinationsComputer;
import com.github.lzadrija.model.ships.Ship;

/**
 * Repository for the fleet of ships.
 *         
 * @author Lucija Zadrija
 *  
 */
public class ShipsRepository {

	private List<Ship> ships;
	private static ShipsRepository instance = new ShipsRepository();

	/**
	 * Default constructor.
	 */
	private ShipsRepository() {
		ships = new ArrayList<>();
	}

	public static ShipsRepository getInstance() {
		return instance;
	}

	public void addShip(Ship ship) {
		ships.add(ship);
	}

	public void addShips(List<Ship> ships) {
		this.ships.addAll(ships);
	}

	/**
	 * Sets the position of the given ship.
	 * 
	 * @param ship
	 *            Ship.
	 * @param position
	 *            Position (upper left point on the grid).
	 * @throws IllegalArgumentException
	 *             If this repository does not contain the given ship.
	 */
	public void exposeShip(Ship ship, Point position) {

		if (!ships.contains(ship)) {
			throw new IllegalArgumentException();
		}

		int index = ships.indexOf(ship);

		ship.setPosition(position);
		ships.set(index, ship);
	}

	/**
	 * Retrieves the ships whose position on the grid is not yet discovered.
	 * 
	 * @return List of ships whose position on the grid is undiscovered.
	 */
	private List<Ship> getHiddenShips() {
		List<Ship> hiddenShips = new ArrayList<>();

		for (Iterator<Ship> iterator = ships.iterator(); iterator.hasNext();) {
			Ship ship = iterator.next();

			if (ship.isShipHidden()) {
				hiddenShips.add(ship);
			}
		}
		return hiddenShips;
	}

	/**
	 * Returns the indexes of the undiscovered ships.
	 * 
	 * @return List of indexes.
	 */
	public List<Integer> getHiddenShipsIndexes() {
		List<Integer> hiddenShipsIndexes = new ArrayList<>();

		for (int i = 0; i < ships.size(); i++) {
			Ship ship = ships.get(i);
			if (ship.isShipHidden()) {
				hiddenShipsIndexes.add(i);
			}
		}
		return hiddenShipsIndexes;
	}

	/**
	 * Retrieves the height of the ship with the given index.
	 * 
	 * @param Index
	 *            Ships index in the list of ships.
	 * @return Ship's height.
	 */
	public int getShipsHeight(int index) {

		Ship ship = ships.get(index);

		return ship.getHeight();
	}

	/**
	 * Retrieves the width of the ship with the given index.
	 * 
	 * @param index
	 *            Ships index in the list of ships.
	 * @return Ship's width.
	 */
	public int getShipsWidth(int index) {

		Ship ship = ships.get(index);

		return ship.getWidth();
	}

	/**
	 * Returns the ship's structure.
	 * 
	 * @param index
	 *            Ships index in the list of ships.
	 * @return Ship's structure
	 */
	public List<Point> getShipsStructure(int index) {
		Ship ship = ships.get(index);

		return ship.getStructure();
	}

	/**
	 * Returns all possible upper left coordinates of the ship with the given index on the grid based on the ship's structure.
	 * 
	 * @param index
	 *            Ships index in the list of ships.
	 * @param initialPoint
	 *            Point on the grid that is the first upper left coordinate from which all other upper left coordinates are computed.
	 * @return List of all possible upper left coordinates.
	 */
	public List<Point> getShipsAllPossibleUpperLeftCoordinates(int index, Point initialPoint) {
		Ship ship = ships.get(index);
		List<Point> coordinatesList = ship.getAllPossibleUpperLeftCoordinates(initialPoint);
		return coordinatesList;
	}

	/**
	 * Checks if all ships' positions were discovered.
	 * 
	 * @return true if all ships were sunk, false otherwise.
	 */
	public boolean isFleetSunk() {
		return getHiddenShips().isEmpty();
	}

	/**
	 * As ships can be placed right next to each other on the grid, when they are sunk, it must be determined which ship is at 
	 * which position. In order to determine this, all combinations of the hidden ships' placements are computed using the number 
	 * of hit cells on the grid.
	 * 
	 * @param hitShipsGroupSize
	 *            Number of hit cells on the grid (number of coordinates of the hit combinations of ships).
	 * @return List of ships' placement combinations.
	 */
	public List<List<Ship>> getAllHiddenShipsSizeCombinations(int hitShipsGroupSize) {

		List<Ship> hiddenShips = getHiddenShips();

		List<List<Ship>> shipsSizeCombinations = ShipsCombinationsComputer.getInstance().getAllShipsSizeCombinations(hitShipsGroupSize,
																													 hiddenShips);
		return shipsSizeCombinations;
	}

	/**
	 * Returns representation of ships repository. The exact details of the representation are subject to change, but the 
	 * following may be regarded as typical:
	 * 
	 * Type = PETAR_KRESIMIR, position = [x = 16, y = 33]
	 * 
	 * Type = X_WING, position = unknown
	 * 
	 * Type = Y_WING, position = [x = 11, y = 7]
	 * 
	 * Type = TIE_FIGHTER, position = unknown
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Iterator<Ship> iterator = ships.iterator(); iterator.hasNext();) {
			Ship ship = iterator.next();

			sb.append(ship.toString()).append("\n");
		}
		return sb.toString();
	}
}