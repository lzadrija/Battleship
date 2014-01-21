package com.github.lzadrija.dataProviders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.github.lzadrija.exceptions.DataNotAvailableException;

/**
 * This class provides information about sea map using web service. It uses token to request data from 
 * Google's service access servers.
 *         
 * @author Lucija Zadrija
 *  
 */
public class DataProviderImpl implements DataProvider {

	private static String token;
	private static int nColumns, nRows;

	private static final int HIT_CODE = 1;
	private static final int MISS_CODE = 0;
	private static final String TOKEN_URL = "https://script.google.com/macros/s/"
			+ "AKfycbzrtCcdp_GUx3rEypG6WgDZJ4lExmZy8IU-gxb2HwaYt_jPM2s/exec?method=GetToken";
	private static final String URL_PREFIX = "https://script.google.com/macros/s/"
			+ "AKfycbzrtCcdp_GUx3rEypG6WgDZJ4lExmZy8IU-gxb2HwaYt_jPM2s/exec?token=";
	private static final String GET_NROWS_METHOD = "&method=GetRowCount";
	private static final String GET_NCOLUMNS_METHOD = "&method=GetColumnCount";
	private static final String GET_CELL_METHOD = "&method=GetCellAt";
	private static final String CELL_ROW = "&row=";
	private static final String CELL_COLUMN = "&col=";

	/**
	 * Constructor used when user provides token.
	 * 
	 * @param token
	 *            Token to request data from Google's service access servers.
	 * @throws NullPointerException
	 *             If token is null.
	 */
	public DataProviderImpl(String token) {

		if (null == token) {
			throw new NullPointerException("Invalid token.");
		}
		DataProviderImpl.token = token;
	}

	/**
	 * Default constructor.
	 */
	public DataProviderImpl() {

	}

	@Override
	public void retrieveToken() throws DataNotAvailableException {
		String token = getResult(TOKEN_URL);
		DataProviderImpl.token = token;
	}

	@Override
	public int getCellContent(int xCoordinate, int yCoordinate) throws DataNotAvailableException {

		StringBuilder urlSb = new StringBuilder();
		urlSb.append(URL_PREFIX).append(token).append(GET_CELL_METHOD).append(CELL_ROW).append(yCoordinate).append(CELL_COLUMN).append(xCoordinate);
		String url = urlSb.toString();

		String cellContentString = getResult(url);

		int cellContent;
		try {
			cellContent = getNumericalResult(cellContentString);
		} catch (NumberFormatException numberFormatException) {
			throw new DataNotAvailableException(numberFormatException);
		}
		return cellContent;
	}

	/**
	 * Retrieves result using the given URL
	 * 
	 * @param URLString
	 *            URL.
	 * @return String result retrieved using the given URL
	 * @throws DataNotAvailableException. If
	 *             connection cannot be established or I/O error occurs.
	 */
	private static String getResult(String URLString) throws DataNotAvailableException {

		URLConnection connection = getConnection(URLString);

		String inputLine = null;
		String result = null;

		try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
			inputLine = in.readLine();
			result = inputLine.trim();
		} catch (IOException ioException) {
			throw new DataNotAvailableException(ioException);
		}
		return result;
	}

	/**
	 * Used for retrieving a connection to the remote object referred to by the
	 * URL.
	 * 
	 * @param URLString
	 *            String representing URL
	 * @return URLConnection instance that represents a connection to the remote
	 *         object referred to by the URL.
	 * @throws DataNotAvailableException
	 *             if an I/O exception occurs or when creating URL from String -
	 *             if no protocol is specified, or an unknown protocol is found,
	 *             or spec is null.
	 */
	private static URLConnection getConnection(String URLString) throws DataNotAvailableException {

		URL url = null;
		URLConnection connection = null;

		try {
			url = new URL(URLString);
			connection = url.openConnection();
		} catch (MalformedURLException malformedURLException) {
			throw new DataNotAvailableException(malformedURLException);
		} catch (IOException ioException) {
			throw new DataNotAvailableException(ioException);
		}

		return connection;
	}

	@Override
	public int retrieveNRows() throws DataNotAvailableException {

		if (0 == nRows) {
			StringBuilder urlSb = new StringBuilder();
			urlSb.append(URL_PREFIX).append(token).append(GET_NROWS_METHOD);
			String url = urlSb.toString();

			String nRowsString = getResult(url);

			try {
				nRows = getNumericalResult(nRowsString);
			} catch (NumberFormatException numberFormatException) {
				throw new DataNotAvailableException(numberFormatException);
			}
		}
		return nRows;
	}

	@Override
	public int retrieveNColumns() throws DataNotAvailableException {

		if (0 == nColumns) {
			StringBuilder urlSb = new StringBuilder();
			urlSb.append(URL_PREFIX).append(token).append(GET_NCOLUMNS_METHOD);
			String url = urlSb.toString();

			String nColumnsString = getResult(url);

			try {
				nColumns = getNumericalResult(nColumnsString);
			} catch (NumberFormatException numberFormatException) {
				throw new DataNotAvailableException(numberFormatException);
			}
		}
		return nColumns;
	}

	/**
	 * Parses string argument as a signed decimal integer and returns the
	 * result.
	 * 
	 * @param resultString
	 *            String containing the int representation to be parsed.
	 * @return int representation of string.
	 * @throws NumberFormatException
	 *             If the string does not contain a parsable integer.
	 */
	private static int getNumericalResult(String resultString) {

		int result = Integer.parseInt(resultString);

		return result;
	}

	@Override
	public int getHitCode() {
		return HIT_CODE;
	}

	@Override
	public int getMissCode() {
		return MISS_CODE;
	}

	/**
	 * Returns representation of point. The exact details of the representation
	 * are subject to change, but the following may be regarded as typical:
	 * 
	 * Token: 867711
	 */
	@Override
	public String toString() {
		return String.format("Token: %s", token);
	}
}
