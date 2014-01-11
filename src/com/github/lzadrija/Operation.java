package com.github.lzadrija;

import java.util.List;

import com.github.lzadrija.dataProviders.IDataProvider;
import com.github.lzadrija.model.factories.ShipsFactory;
import com.github.lzadrija.model.map.Grid;
import com.github.lzadrija.model.ships.Ship;
import com.github.lzadrija.repositories.ShipsRepository;
import com.github.lzadrija.strategies.Executor;
import com.github.lzadrija.strategies.IExecutor;
import com.github.lzadrija.strategies.battle.Destroyer;
import com.github.lzadrija.strategies.battle.IDestroyer;

/**
 * @author Lucija Zadrija
 * 
 *         This class initializes default group of ships and starts the process
 *         of their localization.
 */
public class Operation {

	private final IDataProvider dataProvider;

	private final Grid grid;
	private static ShipsRepository shipsRepository;

	private final IExecutor executor;
	private final IDestroyer destroyer;

	/**
	 * Constructor.
	 * 
	 * @param dataProvider
	 *            Data provider used for retrieving data about sea grid status.
	 * @param grid
	 *            Represents sea grid.
	 * @throws NullPointerException
	 *             If the parameters are null.
	 */
	public Operation(IDataProvider dataProvider, Grid grid) {

		this.dataProvider = dataProvider;
		this.grid = grid;

		shipsRepository = ShipsRepository.getInstance();
		createFleet();

		executor = new Executor(this.dataProvider);
		destroyer = new Destroyer(executor, this.grid);
	}

	/**
	 * Used for initializing ships.
	 */
	private void createFleet() {
		List<Ship> fleet = ShipsFactory.createFleet();
		shipsRepository.addShips(fleet);
	}

	/**
	 * Starts the ship localization process.
	 */
	public void startOperation() {
		destroyer.sinkBattleShips();
	}
}
