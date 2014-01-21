package com.github.lzadrija.model.ships;

import java.util.Comparator;

/**
 * This class is used for comparing ship type's using their structure size.
 *         
 * @author Lucija Zadrija
 *  
 */
public class ShipTypeComparator implements Comparator<ShipType> {

	@Override
	public int compare(ShipType shipType1, ShipType shipType2) {
		return shipType1.getStructureSize() - shipType2.getStructureSize();
	}
}
