/**
 * 
 */
package learning.hierarchical;

import java.util.List;

import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.Observation;


/**
 * @author Maxence Grand
 *
 */
public class Space {
	/**
	 * 
	 */
	private final static int NECESSARY = 0;
	/**
	 * 
	 */
	private final static int POSSIBLE = 1;
	/**
	 * 
	 */
	private final static int IMPOSSIBLE = 2;
	
	/**
	 * 
	 */
	private Symbol op;
	/**
	 * 
	 */
	private Observation[] preconditions;
	/**
	 * 
	 */
	private Observation[] effects;
	
	/**
	 * 
	 * Constructs 
	 * @param op
	 */
	public Space(Symbol op) {
		this.op = op;
		this.preconditions = new Observation[3];
		this.effects = new Observation[3];
	}
	
	/**
	 * 
	 * @param obs
	 * @param precondition
	 */
	public void addNecessary(Observation obs, boolean precondition) {
		Observation newObs = new Observation();
		obs.getPredicates().forEach((p,v) -> {
			if(this.op.compatible(p)) {
				newObs.addObservation(p, v);
			}
		});
		newObs.removeAny();
		if(precondition) {
			this.preconditions[NECESSARY] = newObs;
		} else {
			this.effects[NECESSARY] = newObs;
		}
	}
	
	/**
	 * 
	 * @param obs
	 * @param precondition
	 */
	public void addPossible(Observation obs, boolean precondition) {
		Observation newObs = new Observation();
		obs.getPredicates().forEach((p,v) -> {
			if(this.op.compatible(p)) {
				newObs.addObservation(p, v);
			}
		});
		newObs.removeAny();
		if(precondition) {
			this.preconditions[POSSIBLE] = newObs;
		} else {
			this.effects[POSSIBLE] = newObs;
		}
	}
	
	/**
	 * 
	 * @param obs
	 * @param precondition
	 */
	public void addImpossible(Observation obs, boolean precondition) {
		Observation newObs = new Observation();
		obs.getPredicates().forEach((p,v) -> {
			if(this.op.compatible(p)) {
				newObs.addObservation(p, v);
			}
		});
		newObs.removeAny();
		if(precondition) {
			this.preconditions[IMPOSSIBLE] = newObs;
		} else {
			this.effects[IMPOSSIBLE] = newObs;
		}
	}
	
	/**
	 * 
	 * @param precondition
	 * @return
	 */
	public Observation getNecessary(boolean precondition) {
		return precondition ? this.preconditions[NECESSARY] :
			this.effects[NECESSARY];
	}
	
	/**
	 * 
	 * @param precondition
	 * @return
	 */
	public Observation getPossible(boolean precondition) {
		return precondition ? this.preconditions[POSSIBLE] :
			this.effects[POSSIBLE];
	}
	
	/**
	 * 
	 * @param precondition
	 * @return
	 */
	public Observation getImpossible(boolean precondition) {
		return precondition ? this.preconditions[IMPOSSIBLE] :
			this.effects[IMPOSSIBLE];
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Operator: ").append(this.op).append("\n");
		str.append("Preconditions:\n");
		str.append("\tNecessary: ").append(this.getNecessary(true)).append("\n");
		str.append("\tPossible: ").append(this.getPossible(true)).append("\n");
		str.append("\tImpossible: ").append(this.getImpossible(true)).append("\n");
		str.append("Effects:\n");
		str.append("\tNecessary: ").append(this.getNecessary(false)).append("\n");
		str.append("\tPossible: ").append(this.getPossible(false)).append("\n");
		str.append("\tImpossible: ").append(this.getImpossible(false)).append("\n");
		return str.toString();
	}
}
