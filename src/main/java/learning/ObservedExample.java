/**
 * 
 */
package learning;

import java.util.ArrayList;
import java.util.List;

import fsm.DFA;
import fsm.Example;
import fsm.Symbol;

/**
 * This class represents an example with observations
 * @author Maxence Grand
 *
 */
public class ObservedExample extends Example{
	/**
	 * Observations sequence
	 */
	private List<Observation> observations;
	
	/**
	 * Constructs 
	 * @param actionSequences
	 */
	public ObservedExample(
			List<Symbol> actionSequences, 
			List<Observation> observations) {
		super(actionSequences);
		if(this.size() + 1 != observations.size()) {
			throw new IllegalArgumentException("Observation sequence size must "
									+"be equals to action sequence size + 1");
		}
		this.observations = observations;
	}
	
	/**
	 * 
	 */
	public ObservedExample(
			List<Symbol> actionSequences,
			DFA A,
			Mapping map,
			Observation initial) {
			super(actionSequences);
			this.observations = new ArrayList<>();
			this.observations.add(initial);
			for(int i = 0; i < this.size(); i++ ) {
				int q = A.getStates(new Example(this.actionSequences), A.getQ0()).get(i+1);
//				map.getStates(q, this.getActionSequences().get(i)).getPredicates().forEach((k,v) -> {
//					obs.addObservation(k, v);
//				});
				this.observations.add(map.getStates(q, this.getActionSequences().get(i)));
			}
	}

	/**
	 * Getter of observations
	 * @return the observations
	 */
	public List<Observation> getObservations() {
		return observations;
	}
	
	/**
	 * 
	 * @return
	 */
	public Observation getInitialState() {
		return this.observations.get(0);
	}
	
	/**
	 * 
	 * @param a
	 * @return
	 */
	public Observation ante(int a) {
		return this.observations.get(a);
	}

	/**
	 * 
	 * @param a
	 * @return
	 */
	public Observation post(int a) {
		return this.observations.get(a+1);
	}
	
	/**
	 * 
	 * @param a
	 * @return
	 */
	public Observation postComp(int a) {
		Observation res = new Observation();
		this.observations.get(a+1).getPredicates().forEach((k,v) -> {
			if(this.getActionSequences().get(a).compatible(k)) {
				res.addObservation(k, v);
			}
		});
		return res;
	}
	
	public String toString() {
		String str = this.getInitialState()+"\n";
		for(int i=  0; i < size(); i++) {
			str += this.getActionSequences().get(i)+"\n";
			str += this.postComp(i)+"\n";
		}
		return str;
	}
}
