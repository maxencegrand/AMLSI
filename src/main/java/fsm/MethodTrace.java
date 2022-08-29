/**
 * 
 */
package fsm;

import java.util.List;

/**
 * @author Maxence Grand
 *
 */
public class MethodTrace extends TaskTrace{
	/**
	 * the method
	 */
	private Symbol method;
	
	/**
	 * 
	 * Constructs
	 */
	public MethodTrace() {
		super();
	}
	
	/**
	 * Constructs 
	 * @param trace
	 * @param task
	 */
	public MethodTrace(
			Example trace, 
			Symbol task, 
			List<Integer> states, 
			Symbol method) {
		super(trace, task, states);
		this.method = method;
	}
	
	/**
	 * Getter of method
	 * @return the method
	 */
	public Symbol getMethod() {
		return method;
	}
	
	/**
	 * Setter method
	 * @param method the method to set
	 */
	public void setMethod(Symbol method) {
		this.method = method;
	}
}
