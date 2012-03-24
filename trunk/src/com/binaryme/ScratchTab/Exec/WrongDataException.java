package com.binaryme.ScratchTab.Exec;

/** Exception is thrown, when the Data inside of the slot, can not be converted to the excpected Type.
 *  E.g. we need a double inside the slot, and the slot content gives us a String.
 *  This is exception is not avoidable, if we allow variables, which can save both : String and numbers.
 *     
 *  This exception is thrown by slots and handled by ExceptionHandler.*/
public class WrongDataException extends InternalError {

	/**
	 * Class needs that to be serializable
	 */
	private static final long serialVersionUID = 1L;

	public WrongDataException(String string) {
		super(string);
	}

}
