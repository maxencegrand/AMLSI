package fsm;

import java.util.List;
import java.util.ArrayList;
/**
 * This class represent an alphabet used by an automata
 * @author Maxence Grand
 */
public class Alphabet {
    /**
     * The set of symbols in the alphabet
     */
    List<Symbol> symboles;

    /**
     * Constructor of the alphabet
     *
     * @param symboles the set of symbols
     */
    public Alphabet(List<Symbol> symboles){
        this.symboles = new ArrayList<>(symboles.size());
        for(Symbol s : symboles){
            this.symboles.add(s);
        }
    }

    /**
     * getteur of attribute symboles
     *
     * @return the attribute symboles
     */
    public List<Symbol> getSymboles(){
        return this.symboles;
    }

	/**
	 * @param e
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(Symbol e) {
		return symboles.add(e);
	}

	/**
	 * @param index
	 * @return
	 * @see java.util.List#get(int)
	 */
	public Symbol get(int index) {
		return symboles.get(index);
	}

	/**
	 * @return
	 * @see java.util.List#size()
	 */
	public int size() {
		return symboles.size();
	}

	/**
	 * @param o
	 * @return
	 * @see java.util.List#contains(java.lang.Object)
	 */
	public boolean contains(Object o) {
		return symboles.contains(o);
	}
    
    
}
