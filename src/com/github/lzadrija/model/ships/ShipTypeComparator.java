package com.github.lzadrija.model.ships;

import java.util.Comparator;

/**
 * @author Lucija Zadrija
 * 
 *         This class is used for comparing ship type's using their structure
 *         size.
 */
public class ShipTypeComparator implements Comparator<ShipType> {

	@Override
	public int compare(ShipType shipType1, ShipType shipType2) {
		return shipType1.getStructureSize() - shipType2.getStructureSize();
	}
}
