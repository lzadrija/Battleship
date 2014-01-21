package com.github.lzadrija.model.ships;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.lzadrija.model.common.Point;

/**
 * This class represents a ship. Ever ship has its type and position on the grid (sea map). If ship's position is not yet undefined, it is
 * considered that the ship is hidden. Ships can be compared by their type (by their structures's size).
 *         
 * @author Lucija Zadrija
 *  
 */
public class Ship implements Comparable<Ship> {

	private final ShipType type;
	private Point position;

	/**
	 * Constructor, sets the ship's type.
	 * 
	 * @param type
	 *            Ship's type.
	 */
	public Ship(ShipType type) {

		this.type = type;
		position = null;
	}

	/**
	 * Sets the position on the grid.
	 * 
	 * @param position
	 *            Position on the grid.
	 */
	public void setPosition(Point position) {

		this.position = position;
	}

	/**
	 * Based on the ships structure, computes all possible upper left coordinates of the ship on the grid. This is done in a way that 
	 * the given initial point is every time put as a different relative coordinate of the ship, so that the upper left coordinate 
	 * is calculated using this initial point.
	 * 
	 * @param initialPoint
	 *            Point from the grid, first upper left coordinate
	 * @return List of all possible upper left coordinates of this ship on the
	 *         grid.
	 */
	public List<Point> getAllPossibleUpperLeftCoordinates(Point initialPoint) {

		List<Point> upperLeftPointsList = new ArrayList<>();

		for (Iterator<Point> iterator = getStructure().iterator(); iterator.hasNext();) {
			Point relativePosition = iterator.next();

			Point reverseRelativePosition = new Point(-relativePosition.getX(), -relativePosition.getY());
			Point newPoint = initialPoint.getPointRelativeToThis(reverseRelativePosition);

			upperLeftPointsList.add(newPoint);
		}
		return upperLeftPointsList;
	}

	/**
	 * Returns the upper left coordinate of the ship on the grid.
	 * 
	 * @return Upper left coordinate of the ship on the grid.
	 * @throws IllegalStateException
	 *             If the ship's position is undefined.
	 */
	public Point getUpperLeftCoordinates() {

		if (null == position) {
			throw new IllegalStateException("Point not discovered");
		}

		return position;
	}

	/**
	 * Checks if the ship's position is undefined (if ship is hidden).
	 * 
	 * @return True if ships location on the grid is discovered, false otherwise.
	 */
	public boolean isShipHidden() {
		return null == position;
	}

	@Override
	public int compareTo(Ship ship) {
		ShipTypeComparator comparator = new ShipTypeComparator();
		return comparator.compare(type, ship.type);
	}

	/**
	 * Returns representation of ships repository. The exact details of the representation are subject to change, but the 
	 * following may be regarded as typical:
	 * 
	 * Type = PETAR_KRESIMIR, position = unknown
	 * 
	 * or 
	 * 
	 * Type = PETAR_KRESIMIR, position = [5, 6]
	 */
	@Override
	public String toString() {

		String positionString = (null != position) ? position.toString() : "unknown";

		return String.format("Type = %s, position = %s", type, positionString);
	}

	public ShipType getType() {
		return type;
	}

	public int getHeight() {
		return type.getHeight();
	}

	public int getWidth() {
		return type.getWidth();
	}

	public List<Point> getStructure() {
		return type.getStructure();
	}

	public int getStructureSize() {
		return type.getStructureSize();
	}
}
