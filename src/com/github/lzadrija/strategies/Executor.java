package com.github.lzadrija.strategies;

import java.util.HashMap;
import java.util.Map;

import com.github.lzadrija.dataProviders.IDataProvider;
import com.github.lzadrija.exceptions.DataNotAvailableException;
import com.github.lzadrija.model.common.Point;

/**
 * @author Lucija Zadrija
 * 
 *         This class is used for providing information about the cell content
 *         on the given position on the map (grid).
 */
public class Executor implements IExecutor {

	private final IDataProvider dataProvider;
	private Map<Integer, FireResult> resultCodes;

	/**
	 * Constructor.
	 * 
	 * @param dataProvider
	 *            Data provider used for retrieving data about sea grid status.
	 * @throws NullPointerException
	 *             If the data provider is null.
	 */
	public Executor(IDataProvider dataProvider) {

		if (null == dataProvider) {
			throw new NullPointerException();
		}

		this.dataProvider = dataProvider;
		this.resultCodes = new HashMap<>();

		fillResultCodesMap();
	}

	@Override
	public FireResult fire(Point point) throws DataNotAvailableException {

		Integer result = null;

		result = dataProvider
				.getCellContent(point.getX() + 1, point.getY() + 1);

		FireResult fireResult = resultCodes.get(result);

		return fireResult;
	}

	/**
	 * If the sea map contains one (1) it is marked as a HIT, also if the sea
	 * map contains zero (0) it is marked as a MISS. This method stores this
	 * information in a map.
	 */
	private void fillResultCodesMap() {

		resultCodes.put(dataProvider.getHitCode(), FireResult.HIT);
		resultCodes.put(dataProvider.getMissCode(), FireResult.MISS);
	}
}
