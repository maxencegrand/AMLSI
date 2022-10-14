/**
 * 
 */
package fr.uga.amlsi.learning.simulator;

import java.util.List;

import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.symbols.trace.Observation.Value;

/**
 * @author Maxence Grand
 *
 */
public class Movement implements Comparable<Movement>{
	/**
	 * 
	 */
	private Symbol operator;
	/**
	 * 
	 */
	private Symbol predicate;
	/**
	 * 
	 */
	private boolean precondition;
	/**
	 * 
	 */
	private Value value;
	/**
	 * 
	 */
	private Value previous;
	/**
	 * 
	 * Constructs 
	 * @param operator
	 * @param predicate
	 * @param precondition
	 * @param value
	 * @param previous
	 */
	public Movement(
			Symbol operator, 
			Symbol predicate, 
			boolean precondition, 
			Value value,
			Value previous) {
		this.operator = operator;
		this.predicate = predicate;
		this.precondition = precondition;
		this.value = value;
		this.previous = previous;
	}
	/**
	 * Getter of operator
	 * @return the operator
	 */
	public Symbol getOperator() {
		return operator;
	}
	/**
	 * Setter operator
	 * @param operator the operator to set
	 */
	public void setOperator(Symbol operator) {
		this.operator = operator;
	}
	/**
	 * Getter of predicates
	 * @return the predicates
	 */
	public Symbol getPredicate() {
		return predicate;
	}
	/**
	 * Setter predicates
	 * @param predicate the predicate to set
	 */
	public void setPredicates(Symbol predicate) {
		this.predicate = predicate;
	}
	
	/**
	 * Getter of precondition
	 * @return the precondition
	 */
	public boolean isPrecondition() {
		return precondition;
	}
	/**
	 * Setter precondition
	 * @param precondition the precondition to set
	 */
	public void setPrecondition(boolean precondition) {
		this.precondition = precondition;
	}
	/**
	 * Getter of value
	 * @return the value
	 */
	public Observation.Value getValue() {
		return value;
	}
	/**
	 * Setter value
	 * @param value the value to set
	 */
	public void setValue(Observation.Value value) {
		this.value = value;
	}
	
	/**
	 * 
	 * @return
	 */
	public Movement inverse() {
		return new Movement(operator, predicate, precondition, previous, value);
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("Movement : [");
		str.append(this.getOperator());
		str.append(", ");
		str.append(this.getPredicate());
		str.append(", ");
		str.append(this.getValue());
		str.append(", ");
		str.append(this.isPrecondition());
		str.append("]");
		return str.toString();
	}
	
	/**
	 * 
	 * @param others
	 * @return
	 */
	public boolean isReduce(List<Movement> others) {
		if(this.value != Observation.Value.ANY) {
			return false;
		}
		for(int i = others.size()-1; i >= 0; i--) {
			Movement m = others.get(i);
			if(m.isPrecondition() == this.isPrecondition() && 
					m.getOperator().equals(this.getOperator()) && 
					m.getPredicate().equals(this.getPredicate())) {
				return true;
			}
		}
		return false;
	}
	@Override
	public int compareTo(Movement arg0) {
		// TODO Auto-generated method stub
		if(arg0.isPrecondition()) {
			if(!this.isPrecondition()) {
				return -1;
			} else {
				if(arg0.getOperator().equals(this.getOperator())) {
					if(arg0.getPredicate().equals(this.getPredicate())) {
						switch(arg0.getValue()) {
						case ANY:
							switch(this.getValue()) {
							case ANY:
								return 0;
							case FALSE:
								return 1;
							case TRUE:
								return 1;
							default:
								return 0;
							}
						case FALSE:
							switch(this.getValue()) {
							case ANY:
								return -1;
							case FALSE:
								return 0;
							case TRUE:
								return 1;
							default:
								return 0;
							}
						case TRUE:
							switch(this.getValue()) {
							case ANY:
								return -1;
							case FALSE:
								return -1;
							case TRUE:
								return 0;
							default:
								return 0;
							}
						default:
							return 0;
						}
					} else {
						arg0.getPredicate().compareTo(this.getPredicate());
					}
				} else {
					arg0.getOperator().compareTo(this.getOperator());
				}
			}
		} else {
			if(this.isPrecondition()) {
				return 1;
			} else {
				if(arg0.getOperator().equals(this.getOperator())) {
					if(arg0.getPredicate().equals(this.getPredicate())) {
						switch(arg0.getValue()) {
						case ANY:
							switch(this.getValue()) {
							case ANY:
								return 0;
							case FALSE:
								return 1;
							case TRUE:
								return 1;
							default:
								return 0;
							}
						case FALSE:
							switch(this.getValue()) {
							case ANY:
								return -1;
							case FALSE:
								return 0;
							case TRUE:
								return 1;
							default:
								return 0;
							}
						case TRUE:
							switch(this.getValue()) {
							case ANY:
								return -1;
							case FALSE:
								return -1;
							case TRUE:
								return 0;
							default:
								return 0;
							}
						default:
							return 0;
						}
					} else {
						arg0.getPredicate().compareTo(this.getPredicate());
					}
				} else {
					arg0.getOperator().compareTo(this.getOperator());
				}
			}
		}
		return 0;
	}
}
