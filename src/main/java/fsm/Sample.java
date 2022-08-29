package fsm;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 * This class represents a sample
 * @author Maxence Grand
 */
public class Sample{
    /**
     * Set of example
     */
    private List<Trace> examples;
    
    /**
     * Constructs an empty sample
     */
    public Sample(){
        this.examples = new ArrayList<>();
    }

    /**
     * Constructs a sample from another sample
     * @param other A sample
     */
    public Sample(Sample other){
        this();
        for(Trace sym : other.getExamples()) {
            examples.add(sym);
        }
    }

    /**
     * Clone the sample
     * @return A sample
     */
    public Sample clone() {
        return new Sample(this);
    }

    /**
     * Constructs a sample from a set of examples
     * @param symbols
     */
    public Sample(List<Trace> symbols){
        this.examples = new ArrayList<>(symbols.size());
        for(Trace s : symbols){
        	this.examples.add(s);
        }
    }

    /**
     * Get the set of examples
     * @return A set of examples
     */
    public List<Trace> getExamples(){
        return this.examples;
    }

    /**
     * String representation of a sample
     * @return A string
     */
    @Override
    public String toString(){
        String res = "";
        int i = 1;
        for(Trace l : this.examples){
            res += i+": ";
            res+=l.toString();
            res += "\n";
            i++;
        }
        return res;
    }

    /**
     * Add an example to the sample
     * @param s A new example
     */
    public void addExample(Trace s){
        this.examples.add(s);
    }

    /**
     * Add examples of another sample to the sample
     * @param s the sample
     */
    public void add(Sample s) {
        for(Trace example : s.examples) {
            addExample(example);
        }
    }

    /**
     * The size of the sample ie the number of example
     * @return The size of the sample
     */
    public int size() {
        return this.examples.size();
    }

    /**
     * The mean size of the sample ie the mean size of each example in the
     * sample
     * @return The mean size of the sample
     */
    public float meanSize() {
        int l = 0;
        for(Trace example : examples) {
            l += example.size();
        }
        return (float) l / size();
    }

    /**
     * Remove an example
     * @param toRemove An Example
     */
    public void removeExample(Example toRemove) {
    	List<Trace> newList = new ArrayList<>();
    	this.examples.forEach(ex -> {
    		if(! ex.equals(toRemove)) {
    			newList.add(ex);
    		}
    	});
    	this.examples.clear();
    	this.examples.addAll(newList);
    }
    
    /**
     * Check if the sample contains the example e
     * @param e the example
     * @return true if e is in the sample
     */
    public boolean contains(Example e) {
    	for(Trace ex : this.examples) {
    		if(e.equals(ex)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Sort the sample
     */
    public void sort() {
    	Collections.sort(this.examples, new Comparator<Trace>(){
    	    public int compare(Trace a1, Trace a2) {
    	        return a2.size() - a1.size();
    	    }
    	});
    }
    
    /**
     * 
     * @return
     */
    public Map<Symbol, List<Symbol>> getPair() {
		Map<Symbol, List<Symbol>> res = new HashMap<>();
		this.examples.forEach(t -> {
			Map<Symbol, List<Symbol>> tmp = t.getPair();
			tmp.forEach((k,v) -> {
				if(!res.containsKey(k)) {
					res.put(k, new ArrayList<>());
				}
				v.forEach(a -> {
					if(!res.get(k).contains(a)) {
						res.get(k).add(a);
					}
				});
			});
		});
		return res;
	}
}
