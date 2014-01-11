package com.github.lzadrija.strategies.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.github.lzadrija.model.common.Point;
import com.github.lzadrija.model.map.Grid;
import com.github.lzadrija.repositories.ShipsRepository;

/**
 * @author Lucija Zadrija
 * 
 *         This abstract class is used for finding the position of the next
 *         target on the grid.
 */
public abstract class TargetFinder {

	/**
	 * Returns the position of the next target on the grid. This target is
	 * computed based on the maximum likelihood, the highest number of ships can
	 * pass trough this point on the grid.
	 * 
	 * @param grid
	 *            Representation of the (sea) map.
	 * @param shipsRepository
	 *            Ships repository.
	 * @return Position of the next target on the grid.
	 */
	protected abstract Point getNextObjective(Grid grid,
			ShipsRepository shipsRepository);

	/**
	 * REturns the target that has the maximum likelihood (the highest number of
	 * ships can pass trough this point on the grid). If more than one points
	 * are most likely to be chosen (have the same maximum likelihood), one of
	 * them is selected randomly.
	 * 
	 * @param cellLikelihoods
	 *            2D array of cell likelihoods.
	 * @return Next target with maximum likelihood, or null if none can be found.
	 */
	protected Point getTargetPosition(int[][] cellLikelihoods) {

		Point targetPosition;
		long maxLikelihood = getMaxLikelihood(cellLikelihoods);

		List<Point> possibleTargets = getMaxLikelihoodPositions(
				cellLikelihoods, maxLikelihood);
		targetPosition = (1 == possibleTargets.size()) ? possibleTargets.get(0)
				: chooseTarget(possibleTargets);
		return targetPosition;
	}

	/**
	 * Returns maximum likelihood of a cell (The cell that contains the biggest
	 * number).
	 * 
	 * @param cellLikelihoods
	 *            2D array of cell likelihoods.
	 * @return Maximum likelihood.
	 */
	private long getMaxLikelihood(int[][] cellLikelihoods) {
		long maxLikelihood = 0;

		for (int i = 0; i < cellLikelihoods.length; i++) {
			for (int j = 0; j < cellLikelihoods[i].length; j++) {
				maxLikelihood = (cellLikelihoods[i][j] > maxLikelihood) ? cellLikelihoods[i][j]
						: maxLikelihood;
			}
		}
		return maxLikelihood;
	}

	/**
	 * Returns the array of points from a grid that have maximum likelihood.
	 * 
	 * @param cellLikelihoods
	 *            2D array of cell likelihoods.
	 * @param maxLikelihood
	 *            Maximum likelihood.
	 * @return Array of points from a grid that have maximum likelihood.
	 */
	private List<Point> getMaxLikelihoodPositions(int[][] cellLikelihoods,
			long maxLikelihood) {

		List<Point> positions = new ArrayList<>();

		for (int i = 0; i < cellLikelihoods.length; i++) {
			for (int j = 0; j < cellLikelihoods[i].length; j++) {
				if (cellLikelihoods[i][j] == maxLikelihood) {
					positions.add(new Point(j, i));
				}
			}
		}
		return positions;
	}

	/**
	 * Randomly chooses a target from the list of given points.
	 * 
	 * @param possibleTargets
	 *            Array of points.
	 * @return Chosen point.
	 */
	private Point chooseTarget(List<Point> possibleTargets) {
		Random random = new Random();

		int randomTargetIndex = random.nextInt(possibleTargets.size());

		return possibleTargets.get(randomTargetIndex);
	}
}
