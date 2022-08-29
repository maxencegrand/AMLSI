package exception;

/**
 *
 *
 * Thrown an exception when there are a problem with the generation of a plan
 * (time break or unrosolvable problem)
 *
 * @author Maxence Grand
 *
 */
public class InstantiationException extends Exception {

    /**
	 * Serial number
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a PlanException with details message
	 *
	 * @param message The message
	 */
    public InstantiationException(String message){
        super(message);
    }

    /**
     * Constructs a PlanException
     */
    public InstantiationException() {
    	this("Plan Exception");
    }
}