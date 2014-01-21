package com.github.lzadrija.dataProviders;

import com.github.lzadrija.exceptions.DataNotAvailableException;

/**
 * This interface provides methods for sea map data extraction, such as number of rows and columns and if ship is located at a certain
 * position on the map.
 *         
 * @author Lucija Zadrija
 *  
 */
public interface DataProvider {

	/**
	 * Retrieves token.
	 * 
	 * @throws DataNotAvailableException
	 *             If token is null or cannot be retrieved.
	 */
	void retrieveToken() throws DataNotAvailableException;

	/**
	 * Returns the number of rows of sea map.
	 * 
	 * @return Number of rows in a map.
	 * @throws DataNotAvailableException
	 *             If number of rows cannot be retrieved.
	 */
	int retrieveNRows() throws DataNotAvailableException;

	/**
	 * Returns the number of columns of sea map.
	 * 
	 * @return Number of columns in a map.
	 * @throws DataNotAvailableException
	 *             If number of columns cannot be retrieved.
	 */
	int retrieveNColumns() throws DataNotAvailableException;

	/**
	 * Retrieves the content of sea map in a certain position.
	 * 
	 * @param xCoordinate
	 *            x coordinate
	 * @param yCoordinate
	 *            y coordinate
	 * @return 1 - Ship is located in this position on the map 0 - There is no ship on this position on the map
	 * @throws DataNotAvailableException
	 *             If cell content cannot be retrieved.
	 */
	int getCellContent(int xCoordinate, int yCoordinate)
			throws DataNotAvailableException;

	/**
	 * Returns the numerical representation of a HIT (if a ship is located at a certain position on the map). 1 is typically returned.
	 * 
	 * @return the numerical representation of a HIT
	 */
	int getHitCode();

	/**
	 * Returns the numerical representation of a MISS (if a ship is not located at a certain position on the map). 0 is typically returned.
	 * 
	 * @return the numerical representation of a MISS
	 */
	int getMissCode();
}
