package com.github.lzadrija.model.ships;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.github.lzadrija.model.common.Point;

/**
 * @author Lucija Zadrija
 * 
 *         This class represents the four types of ships: Petar Kresimir, X
 *         Wing, Y Wing and TIE Fighter. Each type of ship has a unique
 *         structure which is represented by the list of relative coordinates
 *         where is the upper left coordinate (0, 0) is the beginning of the
 *         coordinate system, and all other relative coordinates are computed
 *         from this point from left to right.
 */
public enum ShipType {

	PETAR_KRESIMIR(Arrays.asList(new Point(0, 0), new Point(0, 1), new Point(0,
			2), new Point(0, 3))),

	X_WING(Arrays.asList(new Point(0, 0), new Point(2, 0), new Point(1, 1),
			new Point(0, 2), new Point(2, 2))),

	Y_WING(Arrays.asList(new Point(0, 0), new Point(2, 0), new Point(0, 1),
			new Point(2, 1), new Point(1, 2))),

	TIE_FIGHTER(Arrays.asList(new Point(0, 0), new Point(2, 0),
			new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2),
			new Point(2, 2)));

	private final List<Point> structure;
	private final int structureSize;

	private final int height, width;

	/**
	 * Constructor, sets the ship type's structure.
	 * 
	 * @param structure
	 *            Ship type's structure.
	 */
	private ShipType(List<Point> structure) {

		this.structure = structure;
		structureSize = structure.size();

		height = computeHeight();
		width = computeWidth();
	}

	protected List<Point> getStructure() {
		return structure;
	}

	protected int getStructureSize() {
		return structureSize;
	}

	protected int getHeight() {
		return height;
	}

	protected int getWidth() {
		return width;
	}

	/**
	 * Computes height of a ship of this type based on the structure.
	 * 
	 * @return Height.
	 */
	private int computeHeight() {

		int height = 0;
		for (Iterator<Point> iterator = structure.iterator(); iterator
				.hasNext();) {
			Point point = iterator.next();

			height = (point.getY() > height) ? point.getY() : height;
		}
		height++;
		return height;
	}

	/**
	 * Computes width of a ship of this type based on the structure.
	 * 
	 * @return Width.
	 */
	private int computeWidth() {

		int width = 0;
		for (Iterator<Point> iterator = structure.iterator(); iterator
				.hasNext();) {
			Point point = iterator.next();

			width = (point.getX() > width) ? point.getX() : width;
		}
		width++;
		return width;
	}
}
