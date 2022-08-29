package learning;

import java.util.Map;

import fr.uga.pddl4j.planners.statespace.search.Node;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import fsm.Symbol;
import fsm.Pair;

/**
 * This class represent an observation. An observation can be a set of effects,
 * a set of preconditions or an observed state
 * @author Maxence Grand
 */
public class Observation{

    /**
     * The different possible values for an observed predicate
     */
    public enum Value{
    	/**
    	 * Value True
    	 */
        TRUE,
        /**
         * Value False
         */
        FALSE,
        /**
         * Neither true nor false
         */
        ANY,
        /**
         * Missed value
         */
        MISSED
    }

    /**
     * Map each predicate with it value
     */
    private Map<Symbol, Value> predicates;

    /**
     *
     * Constructs the observation
     */
    public Observation(){
        predicates = new HashMap<>();
    }

    /**
     *
     * Constructs from a list of predicate. Each predicates is evaluated to
     * false
     *
     * @param p The list of predicates
     */
    public Observation(List<Symbol> p){
        this();
        for(Symbol s : p){
            this.predicates.put(s, Value.FALSE);
        }
    }

    public Observation(Observation o, List<Symbol> subset) {
    	this(subset);
    	o.getPredicates().forEach((k,v) -> {
    		if(subset.contains(k)) {
    			this.addObservation(k, v);
    		}
    	});
    }
    /**
     * Return an observation where all value are set to ANY
     * @param p The list of predicates
     * @return An observation
     */
    public static  Observation getAnyObservation(List<Symbol> p){
        Observation o = new Observation();
        for(Symbol pred : p) {
            o.addAnyObservation(pred);
        }
        return o;
    }

    /**
     * Constructs an observation from a set of observation. We use the reduction
     * algorithm ie a predicate is evaluated to true (resp false) iff this
     * predicate always set to true (resp false) in the set, it is evaluated to
     * ANY otherwise
     *
     * @param Q The set of observation
     * @param sym All predicates
     */
    public Observation(List<Observation> Q, List<Symbol> sym) {
        this();
        List<Observation> Q2 = new ArrayList<>();
        for(Observation obs : Q){
            if(!obs.isEmpty()){
                Q2.add(obs);
            }
        }
        if(Q2.size() == 1) {
            for(Map.Entry<Symbol, Value> entry : Q2.get(0).predicates
                    .entrySet()) {
                this.predicates.put(entry.getKey(), entry.getValue());
            }
        }else if(Q2.size() > 1) {
            Map<Symbol, Pair<Integer, Integer>> map = new HashMap<>();
            for(Symbol predicate : sym){
                map.put(predicate, new Pair<>(0,0));
            }
            List<Symbol> anyList = new ArrayList<>();
	    List<Symbol> missedList = new ArrayList<>();
            for(int i=0; i<Q2.size(); i++){
                Map<Symbol, Observation.Value> m = Q2.get(i).getPredicates();
                for(Symbol predicate : sym){
                    Pair<Integer, Integer> pair = map.get(predicate);
                    if(! m.containsKey(predicate)){
                        anyList.add(predicate);
                    } else {
                        switch (m.get(predicate)) {
                        case TRUE:
                            map.put(predicate, new Pair<>(pair.getX()+1,
                                                          pair.getY()));
                            break;
                        case FALSE:
                            map.put(predicate, new Pair<>(pair.getX(),
                                                          pair.getY()+1));
                            break;
                        case ANY:
                            anyList.add(predicate);
                            break;
                        case MISSED:
                            missedList.add(predicate);
                            break;
                        }
                    }
                }
            }
            for(Symbol predicate : sym){
                Pair<Integer, Integer> pair = map.get(predicate);
                if(anyList.contains(predicate)){
                    addAnyObservation(predicate);
                }else if(pair.getX() == 0 && pair.getY() == 0 &&
                		missedList.contains(predicate)){
                    predicates.put(predicate, Value.MISSED);
                }else if(pair.getX() > 0 && pair.getY() == 0){
                    addTrueObservation(predicate);
                }else if(pair.getY() > 0 && pair.getX() == 0){
                    addFalseObservation(predicate);
                }else{
                    addAnyObservation(predicate);
                }
            }
        }
    }

    /**
     * Constructs an observation from a set of observation. We use the reduction
     * algorithm ie a predicate is evaluated to true (resp false) iff this
     * predicate always set to true (resp false) in the set, it is evaluated to
     * ANY otherwise
     *
     * @param Q The set of observation
     * @param sym All predicates
     */
    public static Observation merge(List<Observation> Q, List<Symbol> sym) {
        Observation res = new Observation();
        List<Observation> Q2 = new ArrayList<>();
        for(Observation obs : Q){
        	Q2.add(obs);
        }
        if(Q2.size() == 1) {
            for(Map.Entry<Symbol, Value> entry : Q2.get(0).predicates
                    .entrySet()) {
            	res.predicates.put(entry.getKey(), entry.getValue());
            }
        }else if(Q2.size() > 1) {
            Map<Symbol, Pair<Integer, Integer>> map = new HashMap<>();
            for(Symbol predicate : sym){
                map.put(predicate, new Pair<>(0,0));
            }
            List<Symbol> anyList = new ArrayList<>();
	    List<Symbol> missedList = new ArrayList<>();
            for(int i=0; i<Q2.size(); i++){
                Map<Symbol, Observation.Value> m = Q2.get(i).getPredicates();
                for(Symbol predicate : sym){
                    Pair<Integer, Integer> pair = map.get(predicate);
                    if(! m.containsKey(predicate)){
                        anyList.add(predicate);
                    } else {
                        switch (m.get(predicate)) {
                        case TRUE:
                            map.put(predicate, new Pair<>(pair.getX()+1,
                                                          pair.getY()));
                            break;
                        case FALSE:
                            map.put(predicate, new Pair<>(pair.getX(),
                                                          pair.getY()+1));
                            break;
                        case ANY:
                            anyList.add(predicate);
                            break;
                        case MISSED:
                            missedList.add(predicate);
                            break;
                        }
                    }
                }
            }

            for(Symbol predicate : sym){
                Pair<Integer, Integer> pair = map.get(predicate);
                if(anyList.contains(predicate)){
                	res.addAnyObservation(predicate);
                }else if(pair.getX() == 0 && pair.getY() == 0 &&
                		missedList.contains(predicate)){
                	res.predicates.put(predicate, Value.MISSED);
                }else if(pair.getX() > 0 && pair.getY() == 0){
                	res.addTrueObservation(predicate);
                }else if(pair.getY() > 0 && pair.getX() == 0){
                	res.addFalseObservation(predicate);
                }else{
                	res.addAnyObservation(predicate);
                }
            }
        }
        return res;
    }
    /**
     *
     * Constructs an observation from a mapping between predicates and value
     * @param predicates the mapping
     */
    public Observation(Map<Symbol, Value> predicates){
        this();
        for(Map.Entry<Symbol, Value> entry : predicates.entrySet()){
            this.predicates.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     *
     * Constructs an observation from another Observation
     * @param other An observation
     */
    public Observation(Observation other){
        this(other.getPredicates());
    }

    /**
     * Clone the observation
     * @return  An observation
     */
    public Observation clone(){
        return new Observation(this);
    }

    /**
     * Construct an Observation from a BitState
     * @param state A BitState
     * @see fr.uga.pddl4j.util.BitState
     */
    public Observation(Node state ){
        this();
    }

    /**
     * Getter of predicates
     * @return predicates
     */
    public Map<Symbol, Value> getPredicates(){
        return this.predicates;
    }

    /**
     * Setter of predicates
     * @param predicates
     */
    public void setPredicates(Map<Symbol, Value> predicates){
        this.predicates.clear();
        for(Map.Entry<Symbol, Value> entry : predicates.entrySet()){
            this.predicates.put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Add an observation
     * @param s The pedicate to addr
     * @param v the value of the aded predicate
     */
    public void addObservation(Symbol s, Value v){
        this.predicates.put(s,v);
    }

    /**
     * Add a false observation
     * @param s the addedpredicate
     * */
    public void addFalseObservation(Symbol s){
        this.predicates.put(s,Value.FALSE);
    }

    /**
     * Add an any observation
     * @param s the added predicate
     */
    public void addAnyObservation(Symbol s){
        this.predicates.put(s,Value.ANY);
    }

    /**
     * Add a true observation
     * @param s the added predicate
     */
    public void addTrueObservation(Symbol s){
        this.predicates.put(s,Value.TRUE);
    }

    /**
     * Get the value of an observed predicate
     * @param predicate The predocate which we xant the value
     * @return The value
     */
    public Value getValue(Symbol predicate){
        if(! this.predicates.containsKey(predicate)) {
            return Value.MISSED;
        }
        return this.predicates.get(predicate);
    }

    /**
     * Equality test
     * @param o
     * @return true if o is equal to this
     */
    public boolean equals(Object o){
        if(o instanceof Observation){
            Observation other = (Observation) o;
            if(other.predicates.size() != this.predicates.size()){
                return false;
            }
            for(Map.Entry<Symbol, Value> entry : this.predicates.entrySet()){
                if(other.predicates.containsKey(entry.getKey())){
                    if(other.predicates.get(entry.getKey()) != entry.getValue()){
                        return false;
                    }
                }else{
                    return false;
                }
            }
            return true;
        }else{
            return false;
        }
    }

    /**
     * hashcode
     * @return hashcode
     */
    @Override
    public int hashCode(){
        return this.predicates.hashCode();
    }

    /**
     * Reduce the observation with another observation
     * @param obs The observation
     */
    public void reduce(Observation obs){
        Map<Symbol, Value> p = new HashMap<>();
        for(Map.Entry<Symbol, Value> entry : obs.predicates.entrySet()){

            if(! this.predicates.containsKey(entry.getKey())
               && ( entry.getValue() == Value.ANY ||
                    this.predicates.get(entry.getKey()) == Value.ANY)){
                continue;
            }

            if(entry.getValue() == this.predicates.get(entry.getKey())
               ){
                p.put(entry.getKey(), entry.getValue());
            }
        }
        this.predicates = p;
    }

    /**
     * Improve the observation ie add to the observation all opposite value,
     * change true to false if it's in the other observation and vice versa
     * @param obs An other observation
     */
    public void improve(Observation obs){
        for(Map.Entry<Symbol, Value> entry : obs.predicates.entrySet()){

            if(! this.predicates.containsKey(entry.getKey()) ||
               this.predicates.get(entry.getKey()) == Value.ANY){
                this.predicates.put(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Getter the list of predicates
     * @return The list of predicates
     */
    public List<Symbol> getPredicatesSymbols(){
        return new ArrayList<>(predicates.keySet());
    }

    /**
     * String representation
     * @return A string
     */
    @Override
    public String toString(){
        return this.predicates.toString();
    }

    /**
     * Compute an observation including all predicate where values of the
     * observation are different of values of the given observation. Only
     * for True/False value.
     * @param obs An Observation
     * @return An observation
     */
    public Observation diff(Observation obs){
        Observation res = new Observation();
        for(Map.Entry<Symbol, Value> entry : obs.predicates.entrySet()){
            Symbol p = entry.getKey();
            if(this.predicates.containsKey(p)) {
                Value currentValue = this.predicates.get(p);
                Value nextValue = entry.getValue();
                switch(currentValue) {
                case TRUE:
                    switch(nextValue){
                    case FALSE:
                        res.addFalseObservation(p);
                        break;
                    default:
                        break;
                    }
                    break;
                case FALSE:
                    switch(nextValue){
                    case TRUE:
                        res.addTrueObservation(p);
                        break;
                    default:
                        break;
                    }
                    break;
                default:
                    break;
                }
            }
        }
        return res;
    }

    public Observation diff2(Observation obs){
        Observation res = new Observation();
        for(Map.Entry<Symbol, Value> entry : obs.predicates.entrySet()){
            Symbol p = entry.getKey();
            if(this.predicates.containsKey(p)) {
                Value currentValue = this.predicates.get(p);
                Value nextValue = entry.getValue();
                switch(currentValue) {
                case TRUE:
                    switch(nextValue){
                    case FALSE:
                        res.addFalseObservation(p);
                        break;
                    default:
                        break;
                    }
                    break;
                case FALSE:
                    switch(nextValue){
                    case TRUE:
                        res.addTrueObservation(p);
                        break;
                    default:
                        break;
                    }
                    break;
                default:
                    break;
                }
            } else {
            	res.addObservation(entry.getKey(), entry.getValue());
            }
        }
        return res;
    }
    /**
     * Check if the observation contains another observation ie if all
     * predicates of the given observation are presents in the observation with
     * the same value
     *
     * @param obs An observation
     * @return True if the observation contains the other observation
     */
    public boolean contains(Observation obs) {
    	 for(Map.Entry<Symbol, Value> entry : obs.predicates.entrySet()){
            if(this.predicates.containsKey(entry.getKey())) {
                if(this.predicates.get(entry.getKey()) == (entry.getValue())) {
                    continue;
                } else {
                    return false;
                }
            }else {
                return false;
            }
        }
        return true;
    }

    /**
     * Count the number of predicates evaluated to false
     * @return The number of predicates evaluated to false
     */
    public int countNegative() {
        int res = 0;
        for(Map.Entry<Symbol, Value> entry : this.predicates.entrySet()){
            res += entry.getValue() == Value.TRUE ? 1 : 0;
        }
        return res;
    }

    /**
     * Count the number of predicates evaluated to true
     * @return The number of predicates evaluated to true
     */
    public int countPositive() {
        int res = 0;
        for(Map.Entry<Symbol, Value> entry : this.predicates.entrySet()){
            res += entry.getValue() == Value.TRUE ? 1 : 0;
        }
        return res;
    }

    /**
     * Count the number of predicates evaluated to false or true
     * @return The number of predicates evaluated to false or true
     */
    public int countPositiveNegative() {
        return countPositive()+countNegative();
    }

    /**
     * Remove all predicates evaluated to any
     */
    public void removeAny() {
        Map<Symbol, Value> p = new HashMap<>();
        for(Map.Entry<Symbol, Value> entry : predicates.entrySet()){
            if(entry.getValue() != Value.ANY){
                p.put(entry.getKey(), entry.getValue());
            }
        }
        this.predicates = p;
    }

    /**
     * The number of predicate in the observation whatever the value
     * @return The size
     */
    public int size() {
        return this.predicates.size();
    }

    /**
     * Check if observation contains a given predicate whatever the value
     * @param pred The predicate
     * @return True if the observation contains the predicate
     */
    public boolean containsPredicate(Symbol pred) {
        return this.predicates.containsKey(pred);
    }

    /**
     * Check if there don't exist predicates with true or false value
     *
     * @return True if the observation is empty
     */
    public boolean isEmpty() {
        for(Map.Entry<Symbol, Value> entry : predicates.entrySet()){
            switch(entry.getValue()){
            case TRUE:
                return false;
            case FALSE:
                return false;
            default:
                break;
            }
        }
        return true;
    }

    /**
     * Generalize all predicates with the given parameters' map
     * @param param The map
     * @return An observation
     * @see fsm.Symbol
     */
    public Observation instanciate(Map<String, String> param) {
        Observation obs = new Observation();
        for(Map.Entry<Symbol, Value> entry : predicates.entrySet()){
            switch(entry.getValue()){
            case TRUE:
                obs.addTrueObservation(entry.getKey().generalize(param));
                break;
            case FALSE:
                obs.addFalseObservation(entry.getKey().generalize(param));
                break;
            default:
                break;
            }
        }
        return obs;
    }

    /**
     * Add positive and negative predicate allowing to go from the observation
     * to another observation
     *
     * @param other An ibservation
     * @return An observation
     */
    public Observation addEffect(Observation other) {
        Observation obs = new Observation(this);
        for(Map.Entry<Symbol, Value> entry : other.predicates.entrySet()){
            switch(entry.getValue()){
            case TRUE:
                obs.addTrueObservation(entry.getKey());
                break;
            case FALSE:
                obs.addFalseObservation(entry.getKey());
                break;
            default:
                break;
            }
        }
        return obs;
    }

    /**
     * Remove a given predicate
     * @param pred the predicate
     */
    public void removePredicate(Symbol pred) {
        predicates.remove(pred);
    }

    /**
     * Count the number of difference between the observation and another
     * observation
     *
     * @param other An observation
     * @return The number of difference
     */
    public int countDifferent(Observation other) {
        int res = 0;
        for(Symbol p : getPredicatesSymbols()) {
            switch(getValue(p)){
                case TRUE:
                switch(other.getValue(p)){
                    case FALSE:
                        res++;
                        break;
                    default:
                        break;
                }
                break;
                case FALSE:
                switch(other.getValue(p)){
                    case TRUE:
                        res++;
                        break;
                    default:
                        break;
                }
                break;
                default:
                    break;
            }
        }
        return res;
    }

    /**
     * Count the number of similarities between the observation and another
     * observation
     *
     * @param other An observation
     * @return The number of similarities
     */
    public int countEqual(Observation other) {
        int res = 0;
        for(Symbol p : getPredicatesSymbols()) {
            switch(getValue(p)){
                case TRUE:
                switch(other.getValue(p)){
                    case TRUE:
                        res++;
                        break;
                    default:
                        break;
                }
                break;
                case FALSE:
                switch(other.getValue(p)){
                    case FALSE:
                        res++;
                        break;
                    default:
                        break;
                }
                break;
                default:
                    break;
            }
        }
        return res;
    }

    /**
     * Count the number of difference between the observation and another
     * observation
     *
     * @param other An observation
     * @return The number of difference
     */
    public int countAllDifferent(Observation other) {
        int res = 0;
        for(Symbol p : getPredicatesSymbols()) {
            switch(getValue(p)){
                case TRUE:
                switch(other.getValue(p)){
                    case FALSE:
                        res++;
                        break;
                    default:
                        break;
                }
                break;
                case FALSE:
                switch(other.getValue(p)){
                    case TRUE:
                        res++;
                        break;
                    default:
                        break;
                }
                break;
                default:
                    break;
            }
        }
        return res;
    }

    /**
     * Add noise ie switch value true/false for a given predicate
     * @param predicate the predicate
     */
    public void addNoise(Symbol predicate) {
        if(getPredicatesSymbols().contains(predicate)) {
            Value v = getValue(predicate);
            switch(v) {
            case TRUE:
                addFalseObservation(predicate);
                break;
            case FALSE:
                addTrueObservation(predicate);
                break;
            default:
                break;
            }
        }
    }

    /**
     * Get all predicate set to false
     * @return A list of predicates
     */
    public List<Symbol> getNegativePredicate(){
        List<Symbol> res = new ArrayList<>();
        this.predicates.forEach((p, v)->{
            switch(v) {
                case FALSE:
                    res.add(p);
                    break;
                default:
                    break;
            }
        });
        return res;
    }

    /**
     * Get all predicate set to true
     * @return A list of predicates
     */
    public List<Symbol> getPositivePredicate(){
        List<Symbol> res = new ArrayList<>();
        this.predicates.forEach((p, v)->{
            switch(v) {
                case TRUE:
                    res.add(p);
                    break;
                default:
                    break;
            }
        });
        return res;
    }

    /**
     * Switch the value of to Missed
     * @param pred the predicate
     */
    public void missingPredicate(Symbol pred) {
        this.predicates.put(pred, Value.MISSED);
    }

    /**
     * Check if a predicate is present with a value different of Missed
     * @param pred The predicate
     * @return True if a predicate is present
     */
    public boolean isPresent(Symbol pred){
        return (this.predicates.containsKey(pred) &&
                getValue(pred) != Value.MISSED);
    }

    /**
     * Give the common pattern between the observation and another observation
     * @param other An Observation
     * @return The commom pattern
     */
    public Observation common(Observation other) {
        Observation obs =  new Observation();
        this.predicates.forEach((p, v) -> {
            if(other.containsPredicate(p)) {
                if(other.getValue(p) == v /*&& v != Value.MISSED || other.getValue(p) == Value.MISSED*/) {
                    obs.predicates.put(p,v);
                }else{
                    obs.predicates.put(p,Value.ANY);
                }
            }else{
                obs.predicates.put(p,Value.ANY);
            }
        });
        return obs;
    }

    /**
     * Convert all missed predicates into any predicates
     */
    public void missedToAny() {
        this.getPredicatesSymbols().forEach(p -> {
            switch(getValue(p)){
                case MISSED:
                    predicates.put(p, Value.ANY);
                default:
                    break;
            }
        });
    }
    
    /**
     * Remove all predicates without some specified
     * @param toNotRemove Specified predicates
     */
    public void removeAllExcept(List<Symbol> toNotRemove) {
    	this.predicates.forEach((p,v) -> {
    		if(! toNotRemove.contains(p) && v != Observation.Value.ANY) {
    			this.addAnyObservation(p);
    		}
    	});
    }
    
    public void removeAll(List<Symbol> toNotRemove) {
    	this.predicates.forEach((p,v) -> {
    		if(toNotRemove.contains(p) && v != Observation.Value.ANY) {
    			this.addAnyObservation(p);
    		}
    	});
    }
    
    /**
     * 
     * @param obs
     * @return
     */
    public boolean noIdentical(Observation obs) {
    	for(Symbol sym : this.predicates.keySet()) {
    		switch(this.getValue(sym)) {
    		case TRUE:
    			switch(obs.getValue(sym)) {
    			case TRUE:
    				return false;
    			default:
    				break;
    			}
    			break;
    		case FALSE:
    			switch(obs.getValue(sym)) {
    			case FALSE:
    				return false;
    			default:
    				break;
    			}
    			break;
    		default:
    			break;
    		}
    	};
    	return true;
    }
    
    /**
     * 
     * @param obs
     * @return
     */
    public boolean noInverse(Observation obs) {
    	for(Symbol sym : this.predicates.keySet()) {
    		switch(this.getValue(sym)) {
    		case TRUE:
    			switch(obs.getValue(sym)) {
    			case FALSE:
    				return false;
    			default:
    				break;
    			}
    			break;
    		case FALSE:
    			switch(obs.getValue(sym)) {
    			case TRUE:
    				return false;
    			default:
    				break;
    			}
    			break;
    		default:
    			break;
    		}
    	};
    	return true;
    }
}
