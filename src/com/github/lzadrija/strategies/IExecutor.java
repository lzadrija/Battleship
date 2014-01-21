package com.github.lzadrija.strategies;

import com.github.lzadrija.exceptions.DataNotAvailableException;
import com.github.lzadrija.model.common.Point;

/**
 * This interface is used for discovering grid content on the specified position.
 *         
 * @author Lucija Zadrija
 *  
 */
public interface IExecutor {

	/**
	 * Discovers the grid content on the given position. Returns HIT if this position contains part of the ship, or MISS 
	 * if the water was hit (there is no ship on the given position).
	 * 
	 * @param point
	 *            Position on the grid.
	 * @return HIT if this position contains part of the ship, or MISS if the water was hit (there is no ship on the given position).
	 * @throws DataNotAvailableException
	 *             If data could not be retrieved.
	 */
	public FireResult fire(Point point) throws DataNotAvailableException;
}
