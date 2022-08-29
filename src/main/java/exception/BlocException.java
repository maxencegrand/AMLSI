package exception;

/**
 *
 * Thrown to indicate that a state is not in a Bloc doesn't exist
 *
 * @author Maxence Grand
 */
public class BlocException extends Exception {

	/**
	 * Serial number
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a BlocException with a detail message
	 * @param message the message
	 */
    public BlocException(String message){
        super(message);
    }

    /**
     * Constructs a BlocException
     */
    public BlocException() {
    	this("Bloc exception");
    }
}
