package exception;

/**
 *
 * @author Maxence Grand
 *
 */
public class TypeException extends Exception{

	/**
	 * Serial number
	 */
	private static final long serialVersionUID = 1L;

    /**
	 * Constructs a TypeException with a details message
	 * @param message the message
	 */
	public TypeException(String message) {
		super(message);
	}

    /**
	 * Constructs a TypeException
	 */
	public TypeException() {
		this("Bad type exception");
	}
}
