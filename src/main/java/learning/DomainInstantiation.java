/**
 * 
 */
package learning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.utils.Pair;

/**
 * @author Maxence Grand
 *
 */
public class DomainInstantiation {
	/**
	 * All preconditions
	 */
	private Map<Symbol, Observation> precondition;
	/**
	 * All effects 
	 */
	private Map<Symbol, Observation> effect;
	
	/**
	 * 
	 * Constructs 
	 * @param Domain
	 * @param actions
	 */
	public DomainInstantiation(Domain Domain, List<Symbol> actions) {
		Pair<Map<Symbol, Observation>,Map<Symbol, Observation>>
		pair = Domain.decode();
		Map<Symbol, Observation> preconditions = pair.getX();
		Map<Symbol, Observation> postconditions = pair.getY();
		this.precondition = new HashMap<>();
		this.effect = new HashMap<>();
		for(Symbol act : actions) {
			Observation prec = preconditions.get(act.generalize());
			prec = prec.instanciate(utils.Utils.reverseParamMap( act.mapping()));
			Observation post = postconditions.get(act.generalize());
			post = post.instanciate(utils.Utils.reverseParamMap( act.mapping()));
			prec.removeAny();
			post.removeAny();
			precondition.put(act, prec);
			effect.put(act, post);
		}
	}
	
	/**
	 * Get precondition of an action
	 * @param s The Action
	 * @return s' preconditions
	 */
	public Observation getPrecond(Symbol s) {
		return precondition.get(s);
	}
	
	/**
	 * Get effect of an action
	 * @param s The Action
	 * @return s' effect
	 */
	public Observation getEffect(Symbol s) {
		return this.effect.get(s);
	}
	
	/**
	 * Apply effect in a state 
	 * @param s The action
	 * @param o The state
	 * @return The resulting state
	 */
	public Observation apply(Symbol s, Observation o) {
		Observation newObs = o.clone();
		Observation e = this.getEffect(s);
		e.getPredicates().forEach((k,v) -> {
			switch(v) {
			case TRUE:
			case FALSE:
				newObs.addObservation(k,v);
				break;
			default:
				break;
			}
		});
		return newObs;
	}
	
	/**
	 * 
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Precondition\n");
		this.precondition.forEach((act, obs) -> {
			builder.append("\t"+act+" : ");
			obs.getPredicates().forEach((k,v)-> {
				switch(v) {
				case TRUE:
					builder.append(k+" ");
					break;
				case FALSE:
					builder.append("not"+k+" ");
					break;
				default:
					break;
				}
			});
			builder.append("\n");
		});
		builder.append("Effect\n");
		this.effect.forEach((act, obs) -> {
			builder.append("\t"+act+" : ");
			obs.getPredicates().forEach((k,v)-> {
				switch(v) {
				case TRUE:
					builder.append(k+" ");
					break;
				case FALSE:
					builder.append("not"+k+" ");
					break;
				default:
					break;
				}
			});
			builder.append("\n");
		});
		return builder.toString();
	}
}
