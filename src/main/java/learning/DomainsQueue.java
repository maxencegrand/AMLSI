package learning;

import java.util.List;

/**
 * This class represent the queue of candidates
 * @author Maxence Grand
 *
 */
public class DomainsQueue {
    /**
     * The maximum number of candidates
     */
    final int N;
    /**
     * The current number of candidate
     */
    int number;
    /**
     * The minimal fitness score of all candidates
     */
    float min;
    /**
     * The maximal fitness score of all candidates
     */
    float max;
    /**
     * The candidates
     */
    Domain [] indiv;
    /**
     * Candidates' fitness
     */
    float[] fitness;
    /**
     * Tabu list
     */
    List<Domain> tabou;
    
    /**
     * Construct a queue
     * @param N The size of the queue
     * @param tabou A Tabu list
     */
    public DomainsQueue(int N, List<Domain> tabou){
        this.N = N;
        this.number = -1;
        this.indiv = new Domain[N];
        this.fitness = new float[N];
        this.tabou = tabou;
    }

    /**
     * Add a candidate with the fitness
     * @param Domain The candidate
     * @param fit The candidate's fitness
     */
    public void add(Domain Domain, float fit) {
        if(this.present(Domain)) {
            return ;
        }
        this.number ++;
        if(this.number < this.N){
            this.indiv[this.number] = Domain;
            this.fitness[this.number] = fit;
        }else{
            this.number = N-1;
            if(this.fitness[N-1] < fit) {
                this.indiv[this.N-1] = Domain;
                this.fitness[this.N-1] = fit;
            }else{
                return;
            }
            
        }
        this.sort();
        this.max = this.fitness[0];
        this.min = this.fitness[this.number];
    }
    
    /**
     * 
     */
    public void clean() {
    	this.number = -1;
    }
    
    /**
     * Sort all candidates wrt fitness
     */
    public void sort() {
        for(int i = this.number; i > 0; i--) {
            if(this.fitness[i] > this.fitness[i-1]){
                Domain tmp = this.indiv[i-1];
                this.indiv[i-1] = this.indiv[i];
                this.indiv[i] = tmp;
                
                float tmp2 = this.fitness[i-1];
                this.fitness[i-1] = this.fitness[i];
                this.fitness[i] = tmp2;
            }
        }
    }

    /**
     * Get the best candidate which we doesn't visited yet
     * @return The best candidate
     */
    public Domain next() {
        for(int i = 0; i<=this.number; i++) {
            if(! this.tabou.contains(this.indiv[i])) {
                this.tabou.add(this.indiv[i]);
                //Maybe delete indiv[i] of the queue ?
                return this.indiv[i];
            }
        }
        throw new IllegalArgumentException("Max found");
    }

    /**
     * Get the best fitness
     * @return The best fitness
     */
    public float getMaxFitness() {
        return this.max;
    }

    /**
     * Get the candidate with the best fitness
     * @return the best candidate
     */
    public Domain getMaxDomain() {
        return this.indiv[this.number];
    }

    /**
     * Get the worst fitness
     * @return The worst fitness
     */
    public float getMinFitness() {
        return this.min;
    }

    /**
     * Get the candidate with the worst fitness
     * @return the worst candidate
     */
    public Domain getMinDomain() {
        return this.indiv[0];
    }

    /**
     * Get the list off all visited (ie tabu) candidates
     * @return VIsited candidates
     */
    public List<Domain> getTabou() {
        return tabou;
    }
   
    /**
     * String representation
     * @return A string
     */
    @Override
    public String toString() {
        String res = "{ ";
        for(int i =0; i<this.number + 1; i++) {
            res += this.fitness[i]+" ";
        }
        res += "}";
        return res;
    }

    /**
     * Clear the queue
     */
    public void clear() {
        this.number = -1;
        this.min = Float.MAX_VALUE;
        this.max = Float.MIN_VALUE;
    }

    /**
     * CHeck the presence of a given candidate
     * @param Domain the candidate
     * @return True if the candidate is present
     */
    public boolean present(Domain Domain) {
        boolean b = false;
        for(int i = 0; i<= this.number; i++) {
            b |= this.indiv[i].equals(Domain);
        }
        return b;
    }

    /**
     * CHeck the presence of a given candidate in the tabu list
     * @param Domain the candidate
     * @return True if the candidate is present in the tabu list
     */
    public boolean inTabou(Domain Domain) {
        return tabou.contains(Domain);
    }

    /**
     * Get the number of candidates in the queue
     * @return he number of candidates
     */
    public int size() {
        return this.number + 1;
    }
}
