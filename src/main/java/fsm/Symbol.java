package fsm;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import learning.TypeHierarchy;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represent a symbol of an alphabet
 * @author Maxence Grand
 */
public abstract class Symbol implements Comparable<Symbol>{
    /**
     * Symbol's name
     */
    protected String name;
    /**
     * Symbol's parameters
     */
    protected Map<String, String> parameters;
    /**
     * Constructs the symbol
     */
    protected Map<String, String> abstractParameters;
    /**
     * 
     * @return
     */
    public Map<String, String> getAbstractParameters() {
		return abstractParameters;
	}
    /**
     * 
     * @param abstractParameters
     */
	public void setAbstractParameters(Map<String, String> abstractParameters) {
		this.abstractParameters = new LinkedHashMap<>();
		if(abstractParameters != null) {
			abstractParameters.forEach((k,v) -> {
				this.abstractParameters.put(k, v);
			});
		}
	}
	/**
	 * 
	 * @param parameters
	 */
	public void setParameters(Map<String, String> parameters) {
		this.parameters = new LinkedHashMap<>();
		parameters.forEach((k,v) -> {
			this.parameters.put(k, v);
		});
	}

	/**
     * Constructs a Symbol
     */
    public Symbol() {
        this.name = "";
        this.parameters = new LinkedHashMap<>();
    }
    
    /**
     * Constructs the Symbol from a given name
     * 
     * @param name Symbol's name
     */
    public Symbol (String name){
        this();
        this.name = name;
    }

    /**
     * Constructs the Symbol from a given name and given parameters
     * @param name Symbol's name
     * @param parameters Symbol's parameters
     */
    public Symbol(String name, Map<String, String> parameters){
        this(name);
        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            this.parameters.put(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * String representation of the symbol
     *
     * @return String representation of the symbol
     */
    public abstract String toStringType();
    
    /**
     * Hashcode
     *
     *@return the hash code
     */
    @Override
    public int hashCode(){
        return toString().hashCode();
    }
    
    /**
     *
     * Eqality test
     *
     * @return true if this is equal to other, false otherwise
     */
    @Override
    public boolean equals(Object other){
    	if(other instanceof Symbol){
            Symbol o = (Symbol) other;
            boolean b = o.name.equals(name);
            if(o.parameters.size() != parameters.size()) {
                return false;
            }
            for(int i = 0; i < parameters.size(); i++) {
                b &= getListParameters().get(i).equals
                    (o.getListParameters().get(i));
            }
            return b;
        }
        return false;
    }

    /**
     * CHeck if another symbol is compatible with the symbol ie check if each
     * parameters if another symbol are also a parameter of the symbol
     * 
     * @param other A symbol
     * @return if other is compatible with the symbol 
     */
    public boolean compatible(Symbol other) {
        boolean b = true;
        for(Map.Entry<String, String> entry : other.getParameters().entrySet()) {
            b &= parameters.containsKey(entry.getKey());
        }
        return b;
    }
    
    /**
     * CHeck if another symbol is compatible with the symbol ie check if each
     * parameters if another symbol are also a parameter of the symbol
     * 
     * @param other A symbol
     * @return if other is compatible with the symbol 
     */
    public boolean compatible(List<String> other) {
        boolean b = true;
        for(String p : other) {
            b &= parameters.containsKey(p);
        }
        return b;
    }

    /**
     * CHeck if another symbol is compatible with the symbol ie check if each
     * parameters if another symbol are also a parameter of the symbol taking
     * into the type of each parameters
     * 
     * @param other A symbol
     * @return true if other is compatible with the symbol 
     */
    public boolean compatibleType(Symbol other, TypeHierarchy h) {
        boolean b = true;
        List<String> types = new LinkedList<>();
        for(Map.Entry<String, String> entry : getParameters().entrySet()) {
            types.add(entry.getValue());
        }
        List<String> typesO = new LinkedList<>();
        for(Map.Entry<String, String> entry : other.getParameters().entrySet()) {
            typesO.add(entry.getValue());
        }
        List<Integer> tabou = new ArrayList<>();
        for(int i = 0; i<typesO.size(); i++) {
            boolean bb = false;
            for(int j = 0; j<types.size() && !bb; j++) {
                if(h.descendants(typesO.get(i)).contains(types.get(j))
                		&& ! tabou.contains(j)) {
                    bb = true;
                    tabou.add(j);
                }
            }
            b &= bb;
        }
        return b;
    }

    /**
     * Return an abstract mapping ie map each parameters with the abstract
     * parameter ?xi
     * 
     * @return Parameter's mapping
     */
    public Map<String, String> mapping() {
        Map<String, String> res = new LinkedHashMap<>();
        int i = 1;
        for(Map.Entry<String, String> entry : parameters.entrySet()) {
            res.put(entry.getKey(), "?x"+i);
            i++;
        }
        return res;
    }

    /**
     * The abstract generalization ie the generalization where each parameters
     * is replaced by the corresponding abstract parameters ?xi
     * 
     * @return A symbol
     * @see fsm.Symbol#mapping()
     */
    public abstract Symbol generalize();

    /**
     * The generalization of a symbol from a given mapping
     * @param mapping The parameter's mapping
     * @return A symbol
     */
    public abstract Symbol generalize(Map<String, String> mapping);

    /**
     * Getter of the symbol's name
     * @return Symbol's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter of symbol's parameter without type
     * @return Symbol's parameter
     */
    public List<String> getListParameters() {
        return new ArrayList<String>(parameters.keySet());
    }

    /**
     * Getter of symbol's parameter with type
     * @return Symbol's parameter
     */
    public Map<String,String> getParameters() {
        return parameters;
    }

    /**
     * Map symbol's parameters with another symbol's parameters
     * @param other A symbol
     * @return symbols' parameters map
     */
    public Map<String,String> getParametersMapping(Symbol other) {
    	Map<String, String> res = new LinkedHashMap<>();
    	for(int i = 0; i < this.getListParameters().size(); i++) {
    		res.put(this.getListParameters().get(i), 
    				other.getListParameters().get(i));
    	}
        return res;
    }
    
    /**
     * Getter of symbol's parameters' types in a set of string of the following
     * form : parameter - parameter's type
     * 
     * @return Symbol's parameters' type
     */
    public List<String> getListParametersType() {
        List<String> res = new ArrayList<String>();
        for(String str : getListParameters()) {
            res.add(str+" - "+parameters.get(str));
        }
        return res;
    }

    /**
     * Getter of symbol's parameters' types 
     * 
     * @return Symbol's parameters' type
     */
    public List<String> getListType() {
        List<String> res = new ArrayList<String>();
        for(String str : getListParameters()) {
            res.add(parameters.get(str));
        }
        return res;
    }

    /**
     * Clone the symbol
     * @return A symbol
     */
    @Override
    public abstract Symbol clone();
    
    /**
     * 
     * @param name
     */
    public void setName(String name) {
    	this.name = name;
    }
    
    /**
     * Return all instance of a Symbol
     * @param p Parameters map
     * @return A set of Symbol
     */
    public List<Symbol> allInstances (Map<String, String> p) {
//    	System.out.println(p+" "+this.toString()+" "+this.parameters);
    	if(this.getListParameters().isEmpty()) {
    		return new ArrayList<>();
    	}
    	List<Symbol> res = new ArrayList<>();
    	List<List<String>> allParams = new ArrayList<>();
    	this.parameters.forEach((k,v) -> {
    		List<String> tmp = new ArrayList<>();
    		p.forEach((kk,vv) -> {
    			if(v.equals(vv)) {
    				tmp.add(kk);
    			}
    		});
    		allParams.add(tmp);
    	});
    	int[] idxs = new int[allParams.size()];
    	for(int i = 0; i < idxs.length; i++) {
    		idxs[i]=0;
    	}
    	
    	for(List<String> tmp : allParams) {
    		if(tmp.isEmpty()) {
    			return new ArrayList<>();
    		}
    	}
    	while(idxs[0] < allParams.get(0).size()) {
    		Map<String, String> newParams = new LinkedHashMap<>();
    		boolean b = true;
    		for(int i =0; i < idxs.length && b; i++) {
    			if(newParams.containsKey(allParams.get(i).get(idxs[i]))) {
    				b = false;
    			} else {
    				String str = allParams.get(i).get(idxs[i]);
    				newParams.put(str, p.get(str));
    			}
    		}
    		if(b) {
    			List<String> np = new LinkedList<>(newParams.keySet());
    			Map<String, String> newParamsForG = new LinkedHashMap<>();
    			for(int i = 0; i < np.size(); i++) {
    				newParamsForG.put(
    						this.getListParameters().get(i),
    						np.get(i));
    			}
//    			System.out.println(newParamsForG);
    			res.add(this.generalize(newParamsForG));
    		}
    		for(int i = idxs.length-1; i >=0; i--) {
    			idxs[i]++;
    			if(idxs[i] >= allParams.get(i).size() && i>0) {
    				idxs[i] = 0;
    			} else {
    				break;
    			}
    		}
    	}
    	return res;
    }
    
    public int compareTo(Symbol arg0) {
    	return this.toString().compareTo(arg0.toString());
    }
}
