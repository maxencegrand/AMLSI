package fr.uga.amlsi.fsm;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import fr.uga.generator.exception.BlocException;
/**
 * This class represents the state and bloc partition &#960; of an automaton
 * @author Maxence Grand
 */
public class Partition{
    /**
     * The set of bloc of the partition
     */
    private List<Bloc> blocs;
    
    /**
     * COnstructs an empty partition
     */
    public Partition(){
        this.blocs = new ArrayList<>();
    }

    /**
     * Constructs a partition of N states and N non empty block
     * @param N The number of states
     */
    public Partition(int N){
        this();
        for(int i=0; i<N; i++){
            List<Integer> tmp = new ArrayList<>();
            tmp.add(i);
            this.blocs.add(new Bloc(tmp));
        }
    }
    
    
    /**
     * Constructs the partition from a set of blocs
     * @param blocs A set of blocs
     */
    public Partition(List<Bloc> blocs){
        this();
        for(Bloc bloc : blocs){
            this.getBlocs().add(bloc);
        }
    }

    /**
     * Constructs the partition from an other partition
     * @param other A partition
     */
    public Partition(Partition other) {
        this(other.getBlocs());
    }

    /**
     * Clone the partition
     * @return A partition
     */
    public Partition clone(){
        return new Partition(this);
    }

    /**
     * Getter of bloc
     * @return The set of blocs 
     */
    public List<Bloc> getBlocs(){
        return this.blocs;
    }

    /**
     * Get the bloc where a given state is
     * @param state A state
     * @return A bloc
     * @throws BlocException 
     */
    public Bloc getBloc(int state) throws BlocException{
        for(Bloc bloc : this.blocs){
            if(bloc.contains(state)){
                return bloc;
            }
        }
        throw new BlocException("State"+state+" unknown");
    }

    /**
     * Return the minimal state of the partition
     * @return A state
     */
    public int min(){
        int m = this.blocs.get(0).min();
        for(Bloc bloc : this.blocs){
            if(m > bloc.min()){
                m = bloc.min();
            }
        }
        return m;
    }

    /**
     * Return the maximal state of the partition
     * @return A state
     */
    public int max(){
        int m = this.blocs.get(0).max();
        for(Bloc bloc : this.blocs){
            if(m < bloc.max()){
                m = bloc.max();
            }
        }
        return m;
    }

    /**
     * Check if two partitionn are equal
     * @param o A partition
     * @return True if the partitions are equal
     */
    public boolean equals(Object o){
        if(o instanceof Partition){
            Partition other = (Partition)o;
            if(other.getBlocs().size() == this.getBlocs().size()){
                boolean b = true;
                for(Bloc bloc : this.getBlocs()){
                    b &= other.getBlocs().contains(bloc);
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
     * The hashcode
     * @return The hashcode
     */
    @Override
    public int hashCode(){
        int hash = 0;
        for(Bloc bloc : this.blocs){
            hash += bloc.hashCode();
        }
        hash*=this.blocs.size();
        return hash;
    }

    /**
     * String representation of a partition
     * @return A string
     */
    @Override
    public String toString(){
        return this.blocs.toString();
    }

    /**
     * Merge two states
     * @param state1 The first state
     * @param state2 The second state
     * @throws BlocException 
     */
    public void merge(int state1, int state2) throws BlocException{
        Bloc b1 = this.getBloc(state1);
        Bloc b2 = this.getBloc(state2);
        Bloc newBloc = new Bloc(b1);
        for(int state : b2.getStates()){
            newBloc.addState(state);
        }
        List<Bloc> newBlocs = new ArrayList<>();

        for(Bloc bloc : this.blocs){
            if(bloc.equals(b1)){
                newBlocs.add(newBloc);
            }else if(bloc.equals(b2)){
                continue;
            }else{
                newBlocs.add(bloc);
            }
        }

        this.blocs = newBlocs;
    }

    /**
     * Undo the greater merge of a given state
     * @param state The state
     * @throws BlocException 
     */
    public void fission(int state) throws BlocException{
        Bloc b = this.getBloc(state); 
        Bloc newBloc1 = new Bloc(b);
        newBloc1.removeState(state);
        Bloc newBloc2 = new Bloc();
        newBloc2.addState(state);
        List<Bloc> newBlocs = new ArrayList<>();
        for(Bloc bloc : this.blocs){
            if(bloc.equals(b)){
                newBlocs.add(newBloc1);
                newBlocs.add(newBloc2);
            }else{
                newBlocs.add(bloc);
            }
        }
        this.blocs = newBlocs;
    }

    /**
     * Map the bloc state of the partition with another partition
     * @param p A partition
     * @return A Map
     * @throws BlocException 
     */
    public Map<Integer, Integer> B(Partition p) throws BlocException{
        Map<Integer, Integer> B = new HashMap<>();
        for(Bloc bloc : this.blocs){
            for(int state : bloc.getStates()){
                B.put(state, p.getBloc(state).min());
            }
        }
        return B;
    }

    /**
     * Check if the partition contains a given state
     * @param state A state
     * @return True if the partition contains state
     */
    public boolean contains(int state){
        boolean b = false;
        for(Bloc bloc : this.blocs){
            b |= bloc.contains(state);
        }
        return b;
    }

    /**
     * Get the set of minimal states of each of blocs of the partition
     * @return A set of states
     */
    public List<Integer> getMins() {
        List<Integer> res = new ArrayList<>();

        for(Bloc b : this.blocs) {
            res.add(b.min());
        }
        
        return res;
    }
    
    public List<Integer> getMins(int i) {
        List<Integer> res = new ArrayList<>();

        for(Bloc b : this.blocs) {
        	if(b.min() < i) {
        		res.add(b.min());
        	}
        }
        
        return res;
    }
}
