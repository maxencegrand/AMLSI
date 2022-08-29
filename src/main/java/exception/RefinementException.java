/**
 *
 */
package exception;

/**
 * @author Maxence Grand
 *
 */
@SuppressWarnings("serial")
public class RefinementException extends Exception {

	/**
	 * Constructs a RefinementException with a detail message
	 * @param message the message
	 */
	public RefinementException(String message){
		super(message);
	}

	/**
	 * Constructs a RefinementException
	 */
	public RefinementException() {
		this("Bloc exception");
	}

}
