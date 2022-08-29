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
public class PlanException extends Exception {

    /**
	 * Serial number
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a PlanException with details message
	 *
	 * @param message The message
	 */
    public PlanException(String message){
        super(message);
    }

    /**
     * Constructs a PlanException
     */
    public PlanException() {
    	this("Plan Exception");
    }
}
