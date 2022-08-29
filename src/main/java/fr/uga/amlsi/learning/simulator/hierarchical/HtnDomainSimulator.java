/**
 * 
 */
package fr.uga.amlsi.learning.simulator.hierarchical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uga.generator.exception.PlanException;
import fr.uga.generator.symbols.Method;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.CompressedHierarchical;
import fr.uga.generator.symbols.trace.CompressedNegativeExample;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Observation;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.amlsi.learning.Domain;
import fr.uga.amlsi.learning.simulator.DomainSimulator;

/**
 * @author Maxence Grand
 *
 */
public class HtnDomainSimulator extends DomainSimulator{

	/**
	 * 
	 */
	protected Map<Symbol, List<Symbol>> mapTaskMethod;
	
	/**
	 * 
	 * Constructs 
	 * @param domain
	 * @param predicates
	 * @param positiveStaticPredicates
	 * @param actions
	 */
	public HtnDomainSimulator(
			Domain domain, 
			List<Symbol> predicates,
			List<Symbol> actions,
			Observation initial) {
		super(domain, predicates, actions, initial);
		this.mapTaskMethod = new HashMap<>();
		
		for(Symbol a : super.getActions()) {
			if(a instanceof Method) {
				Symbol task = ((Method) a).getToDecompose();
				if(!this.getMapTaskMethod().containsKey(task)) {
					this.getMapTaskMethod().put(task, new ArrayList<>());
				}
				this.getMapTaskMethod().get(task).add(a);
			}
		}
	}

	/**
	 * Getter of mapTaskMethod
	 * @return the mapTaskMethod
	 */
	public Map<Symbol, List<Symbol>> getMapTaskMethod() {
		return mapTaskMethod;
	}

	/**
	 * Setter mapTaskMethod
	 * @param mapTaskMethod the mapTaskMethod to set
	 */
	public void setMapTaskMethod(Map<Symbol, List<Symbol>> mapTaskMethod) {
		this.mapTaskMethod = mapTaskMethod;
	}
	
	/**
     * Negative fit score
     * @param compressed An example
     * @return The fit score
     */
	public float fitnessNegative(List<CompressedNegativeExample> comp) {
		float res = 0;
		for(CompressedNegativeExample plan : comp) {
    		this.reInit();
    		if(!(plan instanceof CompressedHierarchical)) {
    			continue;
    		}
    		CompressedHierarchical compressed = (CompressedHierarchical)plan;
//    		System.out.println(compressed);
    		for(int i = 0; i< compressed.getPrefix().size(); i++) {
//    			System.out.println("Index "+i);
    			//Test negative
        		for(Example n : compressed.getNegativeIndex(i)) {
        			for(Symbol act : n.getActionSequences()) {
        				if(! this.testAction(act)) {
        	    			res++;
        	    		}
        			}
        		}
    			//Test positive
    			//test task
    			Symbol op = compressed.getPrefix().get(i);
    			if(!this.testAction(op)) {
    				break;
    			} else {
    				//test decomposition
    				boolean b = true;
    				for(int j = 0; 
    						j < compressed.getDecomposition(i).size();
    						j++) {
    					Symbol op2 = compressed.getDecomposition(i).get(j);
    					if(this.testAction(op2)) {
    						try {
								this.apply(op2);
							} catch (PlanException e) {
								// TODO Auto-generated catch block
								b=false;
								break;
							}
    					} else {
    						b = false;
    						break;
    					}
    				}
    				if(!b) {
    					break;
    				}
    			}
    		}
    	}
    	return res;
    }
    
    /**
     * Positive fit score
     * @param pos Examples
     * @return The fit score
     */
    public float fitnessPositive(List<CompressedNegativeExample> comp) {
    	float res = 0f;
    	for(CompressedNegativeExample plan : comp) {
    		this.reInit();
    		if(!(plan instanceof CompressedHierarchical)) {
    			continue;
    		}
    		CompressedHierarchical compressed = (CompressedHierarchical)plan;
    		for(int i = 0; i< compressed.getPrefix().size(); i++) {
    			//Test positive
    			//test task
    			Symbol op = compressed.getPrefix().get(i);
    			if(!this.testAction(op)) {
    				break;
    			} else {
    				//test decomposition
    				boolean b = true;
    				for(int j = 0; 
    						j < compressed.getDecomposition(i).size();
    						j++) {
    					Symbol op2 = compressed.getDecomposition(i).get(j);
    					if(this.testAction(op2)) {
    						try {
								this.apply(op2);
							} catch (PlanException e) {
								// TODO Auto-generated catch block
								b=false;
								break;
							}
    					} else {
    						b = false;
    						break;
    					}
    				}
    				if(b) {
    					res++;
    					res+=compressed.getDecomposition(i).size();
    				}
    			}
    		}
    	}
    	return res;
    }
    /**
     * Fit score
     * @param pos Positive Examples
     * @param neg Negative Examples
     * @return The fit score
     */
    public float fitness(Sample pos, List<CompressedNegativeExample> comp) {
    	return
    			this.firnessPrecondition(pos) +
    			this.firnessEffect(pos) +
    			this.fitnessPositive(comp) +
    			this.fitnessNegative(comp);
    }
    
}
