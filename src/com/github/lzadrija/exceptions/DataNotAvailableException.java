package com.github.lzadrija.exceptions;

/**
 * @author Lucija Zadrija
 * 
 *         Thrown to indicate that grid data cannot be retrieved.
 */
public class DataNotAvailableException extends RuntimeException {

	/**
	 * Default serial version ID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public DataNotAvailableException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            Detailed message.
	 */
	public DataNotAvailableException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param cause
	 *            Exception that caused throwing of this exception.
	 */
	public DataNotAvailableException(Throwable cause) {
		super(cause);
	}

}
