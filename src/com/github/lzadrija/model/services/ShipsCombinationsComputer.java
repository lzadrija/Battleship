package com.github.lzadrija.model.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.github.lzadrija.model.ships.Ship;

/**
 * @author Lucija Zadrija
 * 
 *         This class represents a service that computes all combinations of the
 *         hidden ships' placements using the* number of hit cells on the grid.
 */
public class ShipsCombinationsComputer {

	private static ShipsCombinationsComputer instance = new ShipsCombinationsComputer();

	public static ShipsCombinationsComputer getInstance() {
		return instance;
	}

	/**
	 * Computes all combinations of the hidden ships' placements using the
	 * number of hit cells on the grid.
	 * 
	 * @param hitShipsGroupSize
	 *            Number of hit cells on the grid (number of coordinates of the
	 *            hit combinations of ships).
	 * @param ships
	 *            Ships list.
	 * @return List of ships' placement combinations.
	 */
	public List<List<Ship>> getAllShipsSizeCombinations(int hitShipsGroupSize,
			List<Ship> ships) {

		List<List<Ship>> shipsSizeCombinations = new ArrayList<>();

		for (Iterator<Ship> iterator = ships.iterator(); iterator.hasNext();) {
			Ship ship = iterator.next();
			iterator.remove();

			List<Ship> rightListPart = new ArrayList<>(ships);
			List<Ship> leftListPart = new ArrayList<>();
			leftListPart.add(ship);

			computeAllCombinationsWithOneShip(hitShipsGroupSize, leftListPart,
					rightListPart, shipsSizeCombinations);
		}
		return shipsSizeCombinations;
	}

	/**
	 * Recursive method. List of ships that is divided in two, computes a tree
	 * of all possible combinations of ships based on their size. One ship from
	 * first (left) list is combined from one from the second (right) list. If
	 * the sum of their sizes matches the number of hit cells from the grid, the
	 * combination of ships is than added to the list as a possible solution for
	 * the hit area on the grid, if not, the recursive search is then continued
	 * with changed (increased and decreased) lists.
	 * 
	 * @param shipsGroupSize
	 *            Number of hit cells on the grid (number of coordinates of the
	 *            hit combinations of ships).
	 * @param leftListPart
	 *            First part of the ship's list.
	 * @param rightListPart
	 *            Second part of the ship's list.
	 * @param shipsSizeCombinations
	 *            List of ships' placement combinations.
	 */
	private void computeAllCombinationsWithOneShip(int shipsGroupSize,
			List<Ship> leftListPart, List<Ship> rightListPart,
			List<List<Ship>> shipsSizeCombinations) {

		int shipsCombinationSize = getShipsGroupSize(leftListPart);
		if (shipsGroupSize == shipsCombinationSize) {
			shipsSizeCombinations.add(leftListPart);

		} else if (shipsCombinationSize < shipsGroupSize) {
			for (Iterator<Ship> iterator = rightListPart.iterator(); iterator
					.hasNext();) {
				Ship ship = iterator.next();
				iterator.remove();

				leftListPart.add(ship);
				shipsCombinationSize = getShipsGroupSize(leftListPart);

				if (shipsGroupSize >= shipsCombinationSize) {
					computeAllCombinationsWithOneShip(shipsGroupSize,
							new ArrayList<>(leftListPart), new ArrayList<>(
									rightListPart), shipsSizeCombinations);
				}
				leftListPart.remove(ship);
			}
		}
	}

	/**
	 * Computes and returns the sum of the structure size of the given ships.
	 * 
	 * @param combination
	 *            List of ships that make a combination.
	 * @return Size of the given combination.
	 */
	private int getShipsGroupSize(List<Ship> combination) {

		int size = 0;
		for (Iterator<Ship> iterator = combination.iterator(); iterator
				.hasNext();) {
			Ship ship = iterator.next();

			size += ship.getStructureSize();
		}
		return size;
	}
}
