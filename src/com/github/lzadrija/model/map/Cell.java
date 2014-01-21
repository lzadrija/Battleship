package com.github.lzadrija.model.map;

/**
 * Represents a cell in a grid that represents sea map. The cell can have two values - it can be occupied which means that something is
 * located in this cell on the sea grid (water or ship) or it can be undefined - meaning that what lays in this cell is not yet discovered.
 *         
 * @author Lucija Zadrija
 *  
 */
public enum Cell {

	OCCUPIED_CELL(1), UNDEFINED_CELL(0);

	private final int code_;

	/**
	 * Constructor. Sets the cell code.
	 * 
	 * @param code
	 *            Cell's code
	 */
	private Cell(int code) {
		code_ = code;
	}

	/**
	 * Returns the cell code, 0 for undefined cell and 1 for occupied cell.
	 * 
	 * @return code Cell's code
	 */
	public int getCode() {
		return code_;
	}
}
