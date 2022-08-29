package fsm;

import java.util.List;
import java.util.ArrayList;
/**
 * 
 * This class represents a bloc of states
 * 
 * @author Maxence Grand
 * 
 */
public class Bloc {
    /**
     * The set of states belonging to the bloc
     */
    private List<Integer> states;

    /**
     * Constructs a Bloc
     */
    public Bloc(){
        states = new ArrayList<>();
    }

    /**
     * Constructs a Bloc with a list of states
     * @param states A list of states
     */
    public Bloc(List<Integer> states){
        this();
        for(int s : states){
            this.states.add(s);
        }
    }

    /**
     * Constructs a Bloc with only one states
     * @param state A state
     */
    public Bloc(int state){
        this();
        this.states.add(state);
    }

    /**
     * Constructs a Bloc copying another Bloc
     * @param other An other bloc
     */
    public Bloc(Bloc other){
        this(other.getStates());
    }

    /**
     * Clone the bloc
     * @return A ne Bloc
     */
    public Bloc clone(){
        return new Bloc(this);
    }

    /**
     * Getter of the set of state belonging to the bloc
     * @return a list of states
     */
    public List<Integer> getStates() {
        return this.states;
    }

    /**
     * Add a new state to the bloc
     * @param s A state
     */
    public void addState(int s){
        if(! this.contains(s)){
            this.states.add(s);
        }
    }

    /**
     * Remove a state from the bloc
     * @param s The state to remove
     * @return True if the bloc is modified
     */
    public boolean removeState(int s){
        if(this.states.contains(s)){
            this.states.remove(new Integer(s));
            return true;
        }
        return false;
    }

    /**
     * Remove all states
     */
    public void clear(){
        this.states.clear();
    }

    /**
     * Check if a state is in the bloc
     * @param s The state
     * @return True if s is in the bloc
     */
    public boolean contains(int s){
        return this.getStates().contains(s);
    }
    
    /**
     * Check if two bloc are equivalent
     * @param other A bloc
     * @return True if other and this are equivalent
     */
    @Override
    public boolean equals(Object other){
        if(other instanceof Bloc){
            Bloc o = (Bloc) other;
            if(this.getStates().size() == o.getStates().size()){
                boolean b = true;
                for(int s : this.getStates()){
                    b &= o.getStates().contains(s);
                }
                return b;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     * The bloc's hashcode
     * @return AN hashcode
     */
    @Override
    public int hashCode(){
        return this.min();
    }

    /**
     * Return the minimal state
     * @return A state
     */
    public int min(){
        int m = this.getStates().get(0);
        for(int s : this.getStates()){
            if(m > s){
                m=s;
            }
        }
        return m;
    }

    /**
     * Return the maximal state
     * @return A state
     */
    public int max(){
        int m = this.getStates().get(0);
        for(int s : this.getStates()){
            if(m < s){
                m=s;
            }
        }
        return m;
    }

    /**
     * String representation of a bloc
     * @return A string
     */
    @Override
    public String toString(){
        return this.states.toString();
    }
}
