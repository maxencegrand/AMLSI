/**
 * 
 */
package learning.temporal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.uga.generator.symbols.Symbol;
import learning.Domain;
import learning.LocalSearch;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.utils.Pair;
/**
 * @author Maxence Grand
 *
 */
public class TemporalLocalSearch extends LocalSearch{
	private Map<Symbol, Float> durations;
	private List<Symbol> tempActions;
	private boolean twoOp;
	/**
	 * Constructs 
	 * @param actions
	 * @param predicates
	 */
	public TemporalLocalSearch(List<Symbol> actions, List<Symbol> predicates) {
		super(actions, predicates);
		this.durations = new HashMap<>();
		this.tempActions = new ArrayList<>();
		this.twoOp=false;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * Constructs 
	 * @param actions
	 * @param predicates
	 * @param durations
	 * @param tempActions
	 */
	public TemporalLocalSearch(
			List<Symbol> actions, 
			List<Symbol> predicates,
			Map<Symbol, Float> durations,
			List<Symbol> tempActions,
			boolean twoOp) {
		this(actions, predicates);
		this.durations.putAll(durations);
		this.tempActions.addAll(tempActions);
		this.twoOp=twoOp;
	}

	public boolean isConsistant(Domain d) {
		if(! d.isConsistant()) {
			return false;
		}
		TemporalDomain temp = 
				new TemporalDomain(
						d, 
						durations, 
						twoOp, 
						this.getPredicates(), 
						this.tempActions);
		return temp.isConsistant();
	}
	
	public List<Domain> neighbors(Domain domain) {
		List<Domain> res = new ArrayList<>();
		for(Map.Entry<Symbol, Pair<Observation, Observation>> entry :
			domain.getDomain().entrySet()) {
			for(Symbol s : entry.getValue().getX().getPredicatesSymbols()) {
				try {
					Domain clone1 = domain.clone();
					Domain clone2 = domain.clone();
					switch(entry.getValue().getX().getValue(s)) {
					case TRUE:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.ANY);
						if(isConsistant(clone1)) {
							res.add(clone1);
						} 
						break;
					case FALSE:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.ANY);
						if(isConsistant(clone1)) {
							res.add(clone1);
						}
						break;
					case ANY:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.FALSE);
						clone2.mutation(true, entry.getKey(), s, Observation.Value.TRUE);
						if(isConsistant(clone1)) {
							res.add(clone1);
						}
						if(isConsistant(clone2)) {
							res.add(clone2);
						}
						break;
					default:
						break;
					}
				} catch (Exception ex) {
					Logger.getLogger(Domain.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
			for(Symbol s : entry.getValue().getY().getPredicatesSymbols()) {
				if(TemporalDomain.isInvariantOp(entry.getKey())) {
					continue;
				}
				try {
					Domain clone1 = domain.clone();
					Domain clone2 = domain.clone();
					switch(entry.getValue().getY().getValue(s)) {
					case TRUE:
						clone1.mutation(false, entry.getKey(), s, Observation.Value.ANY);
						if(isConsistant(clone1)) {
							res.add(clone1);
						}
						break;
					case FALSE:
						clone1.mutation(false, entry.getKey(), s, Observation.Value.ANY);
						if(isConsistant(clone1)) {
							res.add(clone1);
						}
						break;
					case ANY:
						clone1.mutation(false, entry.getKey(), s, Observation.Value.FALSE);
						clone2.mutation(false, entry.getKey(), s, Observation.Value.TRUE);
						if(isConsistant(clone1)) {
							res.add(clone1);
						}
						if(isConsistant(clone2)) {
							res.add(clone2);
						}
						break;
					default:
						break;
					}
				} catch (Exception ex) {
					Logger.getLogger(Domain.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
		return res;
	}
	
}
