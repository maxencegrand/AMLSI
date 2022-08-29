/**
 * 
 */
package fr.uga.amlsi.learning.hierarchical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.uga.generator.symbols.Method;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.CompressedNegativeExample;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.symbols.trace.ObservedExample;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.utils.Pair;
import fr.uga.amlsi.learning.Domain;
import fr.uga.amlsi.learning.DomainsQueue;
import fr.uga.amlsi.learning.LocalSearch;
import fr.uga.amlsi.learning.simulator.DomainSimulator;
import fr.uga.amlsi.learning.simulator.Movement;
import fr.uga.amlsi.learning.simulator.hierarchical.HtnDomainSimulator;

/**
 * @author Maxence Grand
 *
 */
public class HierarchicalLocalSearch extends LocalSearch{

	/**
	 * Constructs 
	 * @param actions
	 * @param predicates
	 */
	public HierarchicalLocalSearch(List<Symbol> actions, List<Symbol> predicates) {
		super(actions, predicates);
	}

	/**
	 * Compute the fitness of an Domain
	 * @param indiv the Domain
	 * @param data the observations
	 * @param pos I+
	 * @param neg I-
	 * @param is The initial state
	 * @return fitness score
	 */
	public float fitness(
			Domain indiv, Sample pos, Sample neg, 
			List<CompressedNegativeExample> compressed, String is) {
		//System.out.println("PASS");
		float fit;

		ObservedExample obs = (ObservedExample) pos.getExamples().get(0);
		Observation initial = obs.getInitialState();
		
		HtnDomainSimulator tabu = new HtnDomainSimulator(
				indiv, 
				this.getPredicates(), 
				this.getActions(), 
				initial);
		
		return tabu.fitness(pos, compressed);
	}
	
	/**
	 * Generate all neighbors ie all consistent Domain with only one
	 * difference with Domain
	 * @return A set of Domain
	 */
	public List<Domain> neighbors(Domain domain) {
		List<Domain> res = new ArrayList<>();
		for(Map.Entry<Symbol, Pair<Observation, Observation>> entry :
			domain.getDomain().entrySet()) {
			if(! (entry.getKey() instanceof Method)) {
				continue;
			}
			for(Symbol s : entry.getValue().getX().getPredicatesSymbols()) {
				
				try {
					Domain clone1 = domain.clone();
					Domain clone2 = domain.clone();
					switch(entry.getValue().getX().getValue(s)) {
					case TRUE:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.add(clone1);
						} 
						break;
					case FALSE:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						break;
					case ANY:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.FALSE);
						clone2.mutation(true, entry.getKey(), s, Observation.Value.TRUE);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						if(clone2.isConsistant()) {
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
	
	public List<Domain> neighborsSim(Domain domain) {
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
						if(clone1.isConsistant()) {
							res.add(clone1);
						} 
						break;
					case FALSE:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						break;
					case ANY:
						clone1.mutation(true, entry.getKey(), s, Observation.Value.FALSE);
						clone2.mutation(true, entry.getKey(), s, Observation.Value.TRUE);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						if(clone2.isConsistant()) {
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
				if(entry.getKey() instanceof Method) {
					continue;
				}
				try {
					Domain clone1 = domain.clone();
					Domain clone2 = domain.clone();
					switch(entry.getValue().getY().getValue(s)) {
					case TRUE:
						clone1.mutation(false, entry.getKey(), s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.add(clone1);
						} 
						break;
					case FALSE:
						clone1.mutation(false, entry.getKey(), s, Observation.Value.ANY);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						break;
					case ANY:
						clone1.mutation(false, entry.getKey(), s, Observation.Value.FALSE);
						clone2.mutation(false, entry.getKey(), s, Observation.Value.TRUE);
						if(clone1.isConsistant()) {
							res.add(clone1);
						}
						if(clone2.isConsistant()) {
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
	
	/**
	 * 
	 * @param indiv
	 * @param pos
	 * @param neg
	 * @param compressed
	 * @param is
	 * @param size
	 * @param epoch
	 * @param tabou
	 * @return
	 */
	public Domain tabu(	
			Domain indiv, 
			Sample pos, 
			Sample neg, 
			List<CompressedNegativeExample> compressed, 
			String is,
			int size, 
			int epoch,
			List<Domain> tabou) {
//		System.out.println("Test !!!!");
		ObservedExample obs = (ObservedExample) pos.getExamples().get(0);
		Observation initial = obs.getInitialState();
		DomainsQueue queue = new DomainsQueue(size, tabou);
		HtnDomainSimulator tabu = new HtnDomainSimulator(
				indiv, 
				this.getPredicates(), 
				this.getActions(), 
				initial);
		float fit = tabu.fitness(pos, compressed);
		queue.add(indiv, fit);
		tabou.remove(indiv);
		int i =0;
		for(i =0; i < epoch; i ++) {
//			System.out.println(i+"/"+epoch);
			try {
				Domain choosen = queue.next();
				tabu = new HtnDomainSimulator(
						choosen, 
						this.getPredicates(), 
						this.getActions(), 
						initial);
				List<Domain> neigh = this.neighbors(choosen);
				for(Domain ind : neigh) {
					if(! queue.inTabou(ind)) {
						tabu = new HtnDomainSimulator(
								ind, 
								this.getPredicates(), 
								this.getActions(), 
								initial);
						fit = tabu.fitness(pos, compressed);
						queue.add(ind, fit);
					}
				}
			}catch(IllegalArgumentException e){
				break;
			}
		}
		try {
			queue.next();
		}catch(IllegalArgumentException e){
			
		}
		Domain res = tabou.get(0);
		tabu = new HtnDomainSimulator(
				res, 
				this.getPredicates(), 
				this.getActions(), 
				initial);
		float fitRef = tabu.fitness(pos, compressed);
		for(Domain ind : tabou) {
			tabu = new HtnDomainSimulator(
					ind, 
					this.getPredicates(), 
					this.getActions(), 
					initial);
			fit = tabu.fitness(pos, compressed);
			if(fit > fitRef) {
				res = ind;
				fitRef = fit;
			}
		}

		return res;
	}
	
	public Domain tabuSim(	
			Domain indiv, 
			Sample pos, 
			Sample neg, 
			List<CompressedNegativeExample> compressed, 
			String is,
			int size, 
			int epoch,
			List<Domain> tabou) {
//		System.out.println("Test !!!!");
		ObservedExample obs = (ObservedExample) pos.getExamples().get(0);
		Observation initial = obs.getInitialState();
		DomainsQueue queue = new DomainsQueue(size, tabou);
		HtnDomainSimulator tabu = new HtnDomainSimulator(
				indiv, 
				this.getPredicates(), 
				this.getActions(), 
				initial);
		float fit = tabu.fitness(pos, compressed);
		queue.add(indiv, fit);
		tabou.remove(indiv);
		int i =0;
		for(i =0; i < epoch; i ++) {
//			System.out.println(i+"/"+epoch);
			try {
				Domain choosen = queue.next();
				tabu = new HtnDomainSimulator(
						choosen, 
						this.getPredicates(), 
						this.getActions(), 
						initial);
				List<Domain> neigh = this.neighborsSim(choosen);
				for(Domain ind : neigh) {
					if(! queue.inTabou(ind)) {
						tabu = new HtnDomainSimulator(
								ind, 
								this.getPredicates(), 
								this.getActions(), 
								initial);
						fit = tabu.fitness(pos, compressed);
						queue.add(ind, fit);
					}
				}
			}catch(IllegalArgumentException e){
				break;
			}
		}
		try {
			queue.next();
		}catch(IllegalArgumentException e){
			
		}
		Domain res = tabou.get(0);
		tabu = new HtnDomainSimulator(
				res, 
				this.getPredicates(), 
				this.getActions(), 
				initial);
		float fitRef = tabu.fitness(pos, compressed);
		for(Domain ind : tabou) {
			tabu = new HtnDomainSimulator(
					ind, 
					this.getPredicates(), 
					this.getActions(), 
					initial);
			fit = tabu.fitness(pos, compressed);
			if(fit > fitRef) {
				res = ind;
				fitRef = fit;
			}
		}

		return res;
	}
}
