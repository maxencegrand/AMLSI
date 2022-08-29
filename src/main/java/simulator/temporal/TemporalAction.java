/**
 * 
 */
package simulator.temporal;

import fr.uga.pddl4j.problem.operator.Action;
import fr.uga.pddl4j.problem.operator.Condition;
import fr.uga.pddl4j.problem.operator.Effect;
import fr.uga.pddl4j.problem.operator.Action;
/**
 * @author Maxence Grand
 *
 */
public class TemporalAction {
	/**
	 * 
	 */
	private Action start;
	/**
	 * 
	 */
	private Action inv;
	/**
	 * 
	 */
	private Action end;
	/**
	 * 
	 */
	private float duration;
	
	/**
	 * Constructs 
	 * @param start
	 * @param inv
	 * @param end
	 */
	public TemporalAction(Action start, Action inv, Action end) {
		this.start = start;
		this.inv = inv;
		this.end = end;
	}
	
	/**
	 * Constructs
	 */
	public TemporalAction() {
		this.start = null;
		this.inv = null;
		this.end = null;
	}
	
	/**
	 * 
	 * Constructs 
	 * @param other
	 */
	public TemporalAction(TemporalAction other) {
		this.start = other.start != null ? new Action(other.start) : null;
		this.end = other.end != null ? new Action(other.end) : null;
		this.inv = other.inv != null ? new Action(other.inv) : null;
		this.duration = other.duration;
	}
	
	/**
	 * Getter of start
	 * @return the start
	 */
	public Action getStart() {
		return start;
	}
	/**
	 * Setter start
	 * @param start the start to set
	 */
	public void setStart(Action start) {
		//System.out.println("******"+start.getDuration().getValue());
		this.start = start;
	}
	/**
	 * Getter of inv
	 * @return the inv
	 */
	public Action getInv() {
		return inv;
	}
	/**
	 * Setter inv
	 * @param inv the inv to set
	 */
	public void setInv(Action inv) {
		this.inv = inv;
	}
	/**
	 * Getter of end
	 * @return the end
	 */
	public Action getEnd() {
		return end;
	}
	/**
	 * Setter end
	 * @param end the end to set
	 */
	public void setEnd(Action end) {
		this.end = end;
	}
	
	
	/**
	 * Get the start precondition
	 * @return the precondition
	 */
	public Condition getAtStartPrecondition() {
		return this.start.getPrecondition();
	}
	
	/**
	 * Get the end precondition
	 * @return the precondition
	 */
	public Condition getAtEndPrecondition() {
		return this.end.getPrecondition();
	}
	
	/**
	 * Get the overall precondition
	 * @return the precondition
	 */
	public Condition getOverAllPrecondition() {
		return this.inv.getPrecondition();
	}
	
	/**
	 * Get the start effect
	 * @return the effect
	 */
	public Effect getAtStartEffect() {
		return this.start.getUnconditionalEffect();
	}
	
	/**
	 * Get the end effect
	 * @return the effect
	 */
	public Effect getAtEndEffect() {
		return this.end.getUnconditionalEffect();
	}
	
	/**
	 * Get the duration
	 * @return the duration
	 */
	public float getDuration() {
		return this.duration;
	}
	
	/**
	 * Setter duration
	 * @param duration the duration to set
	 */
	public void setDuration(float duration) {
		this.duration = duration;
	}

	/**
	 * CHeck if the temporal action is complete i.e. the temporal has a at start
	 * at end and over all action
	 * @return True if the temporal action is complete
	 */
	public boolean isComplete() {
		return this.start != null &&
				this.inv != null &&
				this.end != null;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getName() {
		String[] tmp = this.start.getName().split("_");
		String name = this.start.getName().substring(0, this.start.getName().length()-tmp[tmp.length-1].length()-1);
		return name;
	}
}
