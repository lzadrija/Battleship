package com.github.lzadrija.model.factories;

import java.util.ArrayList;
import java.util.List;

import com.github.lzadrija.model.ships.Ship;
import com.github.lzadrija.model.ships.ShipType;

/**
 * Used for ships creation.
 *         
 * @author Lucija Zadrija
 *  
 */
public class ShipsFactory {

	/**
	 * Creates and returns fleet of ship objects.
	 * 
	 * @return List of of ship objects.
	 */
	public static List<Ship> createFleet() {
		List<Ship> fleet = new ArrayList<>();

		for (ShipType shipType : ShipType.values()) {
			fleet.add(new Ship(shipType));
		}

		return fleet;
	}
}
