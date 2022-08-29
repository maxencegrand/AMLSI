package learning;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import fsm.Symbol;
import fsm.Trace;
import fsm.DFA;
import fsm.Sample;
import exception.BlocException;

/**
 * This class represent the mapping "Action" "state in automaton, observed state"
 * @author Maxence Grand
 */
public class Mapping {
    /**
     * The mapping
     */
    private Map<Symbol, Map<Integer, Observation>> map;
    
    /**
     * Constructs the mapping
     */
    public Mapping(){
        map = new HashMap<>();
    }

    /**
     * Construct the mapping
     * @param mapping the mapping
     * @param preds all predicates
     */
    public Mapping(Map<Symbol, Map<Integer, List<Observation>>> mapping,
                   List<Symbol> preds){
        this();
        for(Map.Entry<Symbol, Map<Integer, List<Observation>>> entry :
                mapping.entrySet()) {
//        	System.out.println("-Action "+entry.getKey());
            map.put(entry.getKey(), new HashMap<>());
            for(Map.Entry<Integer, List<Observation>> entry2 :
                    entry.getValue().entrySet()) {
//            	System.out.println("\t"+"State "+entry2.getKey());
//            	entry2.getValue().forEach(o -> System.out.println("\t"+o));
                map.get(entry.getKey()).put(entry2.getKey(),
                                            new Observation(entry2.getValue(),
                                                            preds));
            }
        }
    }
    
    /**
     * Get the mapping ante
     * @param seqObs All observed sequences
     * @param A the automaton
     * @param actions all actions
     * @param preds all predicates
     * @return A mapping
     * @throws BlocException 
     */
    public static Mapping getMappingAnte (
    		Sample pos, 
    		DFA A,
    		List<Symbol> actions, 
    		List<Symbol> preds ) throws BlocException{
//    	System.out.println("MAPPING ANTE");
        Map<Symbol, Map<Integer, List<Observation>>> mapping = new HashMap<>();
        for(Symbol act : actions) {
            mapping.put(act, new HashMap<>());
        }
        for(Trace example : pos.getExamples()){
        	ObservedExample observation = (ObservedExample) example;
        	
        	List<Integer> states = new ArrayList<>();
        	states.add(A.getQ0());
        	states.addAll(A.getBlocs(example));
        	for(int i= 0; i < observation.size(); i++){
        		if(!mapping.get(observation.get(i))
        				.containsKey(states.get(i))){
                    mapping.get(observation.get(i))
                    	.put(states.get(i), new ArrayList<>());
                }
        		mapping.get(observation.get(i)).get(states.get(i))
        			.add(observation.ante(i));
        	}
//            Bloc b = A.getPartition().getBloc(A.getQ0());
//            for(int i= 0; i < observation.size(); i++){
//                if(!mapping.get(observation.get(i)).containsKey(b.min())){
//                    mapping.get(observation.get(i)).put(b.min(), new ArrayList<>());
//                }
//                mapping.get(observation.get(i)).get(b.min()).add(observation.ante(i));
//                
//                b = A.getBlocTransition(b, observation.get(i));
//            }
        }
        
        return new Mapping(mapping, preds);
    }

    /**
     * Get the mapping post
     * @param seqObs All observed sequences
     * @param A the automaton
     * @param actions all actions
     * @param preds all predicates
     * @return A mapping
     * @throws BlocException 
     */
    public static Mapping getMappingPost (Sample pos, DFA A, 
    		List<Symbol> actions, List<Symbol> preds ) throws BlocException{
//    	System.out.println("MAPPING POST");
        Map<Symbol, Map<Integer, List<Observation>>> mapping = new HashMap<>();
        for(Symbol act : actions) {
            mapping.put(act, new HashMap<>());
        }
        
        for(Trace example : pos.getExamples()){
        	ObservedExample observation = (ObservedExample) example;
        	List<Integer> states = new ArrayList<>();
        	states.add(A.getQ0());
        	states.addAll(A.getBlocs(example));
        	for(int i= 0; i < observation.size(); i++){
        		if(!mapping.get(observation.get(i))
        				.containsKey(states.get(i+1))){
                    mapping.get(observation.get(i))
                    	.put(states.get(i+1), new ArrayList<>());
                }
        		mapping.get(observation.get(i)).get(states.get(i+1))
        			.add(observation.post(i));
        	}
//            Bloc b = A.getPartition().getBloc(A.getQ0());
//            for(int i= 0; i < observation.size(); i++){
//                Bloc b2 = A.getBlocTransition(b, observation.get(i));
//                if(!mapping.get(observation.get(i)).containsKey(b2.min())){
//                    mapping.get(observation.get(i)).put(b2.min(), new ArrayList<>());
//                }
//                mapping.get(observation.get(i)).get(b2.min()).add(observation.post(i));
//                
//                b = b2;
//            }
        }
        
        return new Mapping(mapping, preds);
    }
        
    /**
     * Get the observed state for a given action and automaton state
     * @param q the automaton state
     * @param act the action
     * @return an observed state
     */
    public Observation getStates(int q, Symbol act){
        return map.get(act).get(q);
    }
    
    /**
     * Get the set of mapping "state automaton" "observed state" for a given
     * action
     * 
     * @param act the action
     * @return a mapping
     */
    public Map<Integer,Observation> getSet(Symbol act){
        return map.get(act);
    }
    
    /**
     * String representation of the mapping
     * @return A string
     */
    @Override
    public String toString() {
            return ""+map;
    }

	/**
	 * Setter map
	 * @param map the map to set
	 */
	public void setMap(Map<Symbol, Map<Integer, Observation>> map) {
		this.map = map;
	}
    
    
}
