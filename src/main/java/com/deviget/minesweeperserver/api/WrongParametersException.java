/**
 * 
 */
package com.deviget.minesweeperserver.api;

/**
 * @author fernando
 *
 */
class WrongParametersException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param msg
	 */
	WrongParametersException(String msg) {
		super(msg);
	}
	
}
