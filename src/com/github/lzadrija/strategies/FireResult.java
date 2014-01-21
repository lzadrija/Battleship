package com.github.lzadrija.strategies;

/**
 * This class represents the state of one position on the grid. If the grid contains part of the ship at certain position 
 * and if we probe to check what is on this position, HIT is returned. if there is no ship on the given position MISS is returned.
 *         
 * @author Lucija Zadrija
 *  
 */
public enum FireResult {
	HIT, MISS;
}
