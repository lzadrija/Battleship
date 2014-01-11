package com.github.lzadrija;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.lzadrija.dataProviders.DataProvider;
import com.github.lzadrija.dataProviders.IDataProvider;
import com.github.lzadrija.exceptions.DataNotAvailableException;
import com.github.lzadrija.model.map.Grid;

/**
 * @author Lucija Zadrija
 * 
 *         Main class, used for initializing data and starting the game.
 */
public class Game {

	private static final Logger logger = Logger.getLogger(Game.class.getName());
	private IDataProvider dataProvider;
	private Grid grid;
	private Operation operation;

	/**
	 * Parses the input arguments in search of token.
	 * 
	 * @param args
	 *            List of arguments.
	 * @return Token or null if list of arguments is empty.
	 */
	private static String parseInputArguments(String[] args) {
		return (0 != args.length ? args[0] : null);
	}

	/**
	 * Creates data provider that provides information about sea map using web
	 * service.
	 * 
	 * @param token
	 *            Token to request data from service's access servers.
	 * @throws DataNotAvailableException
	 *             If token is null or cannot be retrieved.
	 */
	private void createDataProvider(String token)
			throws DataNotAvailableException {
		if (null != token) {
			dataProvider = new DataProvider(token);
		} else {
			dataProvider = new DataProvider();
			dataProvider.retrieveToken();
		}
	}

	/**
	 * Initializes the grid representation of the sea map and handles it to new
	 * operation object.
	 * 
	 * @throws DataNotAvailableException
	 *             If number of rows or columns cannot be retrieved.
	 */
	private void initialize() throws DataNotAvailableException {

		int gridHeight = dataProvider.retrieveNRows();
		int gridWidth = dataProvider.retrieveNColumns();

		grid = new Grid(gridHeight, gridWidth);
		operation = new Operation(dataProvider, grid);
	}

	/**
	 * Starts the game by starting the new operation.
	 */
	private void startGame() {
		System.out.println(dataProvider.toString());
		operation.startOperation();
	}

	/**
	 * Main method.
	 * 
	 * @param args
	 *            Command line arguments.
	 */
	public static void main(String[] args) {

		String inputArgument = parseInputArguments(args);

		Game newGame = new Game();
		try {
			newGame.createDataProvider(inputArgument);
			newGame.initialize();
			newGame.startGame();
		} catch (DataNotAvailableException dataNotAvailableException) {
			logger.log(Level.SEVERE, null, dataNotAvailableException);
		}
	}
}
