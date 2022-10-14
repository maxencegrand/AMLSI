/**
 * 
 */
package fr.uga.amlsi.learning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.uga.amlsi.learning.simulator.Movement;

/**
 * @author Maxence Grand
 *
 */
public class SearchStorage {
	
	/**
	 * 
	 * @author Maxence Grand
	 *
	 */
	private class Node {
		/**
		 * 
		 */
		private float value;
		/**
		 * 
		 */
		private Node previous;
		/**
		 * 
		 */
		private Map<Movement, Node> next;
		/**
		 * 
		 */
		private Movement m;
		/**
		 * Constructs 
		 * @param value
		 * @param previous
		 * @param next
		 */
		public Node(float value, Node previous, Movement m) {
			this.value = value;
			this.previous = previous;
			this.next = new HashMap<>();
			this.m = m;
		}
		/**
		 * Getter of value
		 * @return the value
		 */
		public float getValue() {
			return value;
		}
		/**
		 * Setter value
		 * @param value the value to set
		 */
		public void setValue(float value) {
			this.value = value;
		}
		/**
		 * Getter of previous
		 * @return the previous
		 */
		public Node getPrevious() {
			return previous;
		}
		/**
		 * Setter previous
		 * @param previous the previous to set
		 */
		public void setPrevious(Node previous) {
			this.previous = previous;
		}
		/**
		 * Getter of next
		 * @return the next
		 */
		public Map<Movement, Node> getNext() {
			return next;
		}
		
		/**
		 * Getter of m
		 * @return the m
		 */
		public Movement getM() {
			return m;
		}
		/**
		 * Setter m
		 * @param m the m to set
		 */
		public void setM(Movement m) {
			this.m = m;
		}
		
		/**
		 * 
		 * @return
		 */
		public List<Movement> allMovements() {
			if(this.previous == null) {
				List<Movement> res = new ArrayList<>();
				if(this.m != null) {
					res.add(this.m);
				}
				return res;
			} else {
				List<Movement> res = this.previous.allMovements();
				res.add(m);
				return res;
			}
		}
	}
	
	/**
	 * 
	 */
	private Node original;
	
	/**
     * The maximum number of candidates
     */
	private int N;
    /**
     * The current number of candidate
     */
	private int number;
    /**
     * The minimal fitness score of all candidates
     */
	private float min;
    /**
     * The maximal fitness score of all candidates
     */
	private float max;
    /**
     * The candidates
     */
	private Node [] indiv;
    /**
     * Candidates' fitness
     */
	private float[] fitness;
    /**
     * Tabu list
     */
	private List<Node> tabou;
	
	/**
     * Construct a queue
     * @param N The size of the queue
     * @param f initial value
     */
    public SearchStorage(int N, float f){
        this.N = N;
        this.number = 0;
        this.indiv = new Node[N];
        this.fitness = new float[N];
        this.tabou = new ArrayList<>();
        this.original = new Node(f, null, null);
        this.indiv[0]=original;
        this.fitness[0]=f;
    }
    
    /**
     * Add a candidate with the fitness
     * @param previous Previous candidates
	 * @param m The candidate
     * @param fit The candidate's fitness
     */
    public void add(List<Movement> previous, Movement m, float fit) {
    	List<Movement> l = new LinkedList<>();
    	l.addAll(previous);
    	l.add(m);
    	l = this.uniformize(l);
    	Node current = this.add(l);
//    	if(current.value == -1) {
    		current.value = fit;
	    	this.number ++;
	        if(this.number < this.N){
	            this.indiv[this.number] = current;
	            this.fitness[this.number] = fit;
	        }else{
	            this.number = N-1;
	            if(this.fitness[N-1] < fit) {
	                this.indiv[N-1] = current;
	                this.fitness[N-1] = fit;
	            }else{
	                return;
	            }
	            
	        }
	        this.sort();
	        this.max = this.fitness[0];
	        this.min = this.fitness[this.number];
//    	}
    }
    
    private Node add(List<Movement> l) {
    	Node current = this.original;
    	for(int i = 0; i < l.size(); i++) {
    		if(current.next.containsKey(l.get(i))) {
    			current = current.next.get(l.get(i));
    		} else {
    			Node newNode = new Node(-1, current, l.get(i));
    			current.next.put(l.get(i), newNode);
    			current = newNode;
    		}
    	}
    	return current;
    }
    
    /**
     * 
     * @param previous
     * @param m
     * @return
     */
    public boolean contains(List<Movement> previous, Movement m) {
    	List<Movement> l = new LinkedList<>();
    	l.addAll(previous);
    	l.add(m);
    	l = this.uniformize(l);
    	try {
    		Node n = this.getNode(l);
    		return (n.value >= 0);
    	} catch(Exception e) {
    		return false;
    	}
    }
    /**
     * Sort all candidates wrt fitness
     */
    public void sort() {
        for(int i = this.number; i > 0; i--) {
            if(this.fitness[i] > this.fitness[i-1]){
                Node tmp = this.indiv[i-1];
                this.indiv[i-1] = this.indiv[i];
                this.indiv[i] = tmp;
                
                float tmp2 = this.fitness[i-1];
                this.fitness[i-1] = this.fitness[i];
                this.fitness[i] = tmp2;
            }
        }
    }
    
    /**
     * 
     * @param l
     * @return
     */
    private Node getNode(List<Movement> l) {
    	Collections.sort(l);
    	Node current = original;
    	for(int i = 0; i < l.size(); i++) {
    		if(current.getNext().get(l.get(i)) == null) {
    			throw new IllegalArgumentException();
    		}
    		current = current.getNext().get(l.get(i));
    	}
    	return current;
    }
    
    /**
     * 
     * @return
     */
    public List<Movement> next() {
    	for(int i = 0; i <= this.number; i++) {
    		if(tabou.contains(this.indiv[i])) {
    			continue;
    		}
    		this.tabou.add(this.indiv[i]);
    		return this.indiv[i].allMovements();
    	}
    	throw new IllegalArgumentException("Max found");
    }
    
    /**
     * 
     * @param d
     * @return
     */
    public Domain getMaxDomain(Domain d) {
    	d.makeMovement(this.indiv[0].allMovements());
    	return d;
    }
    
    public void printFitness(int n) {
    	for(int i = 0; i < n; i++) {
    		System.out.print(this.fitness[i]+" ");
    	}
    	System.out.println();
    }
    
    /**
     * 
     * @param l
     * @return
     */
    public List<Movement> uniformize(List<Movement> l) {
    	List<Movement> res = new LinkedList<>();
    	Collections.sort(l);
    	for(int i = 0; i < l.size(); i++) {
    		Movement mi = l.get(i);
    		boolean b = true;
    		for(int j = 0; j < res.size() && b; j++) {
    			Movement mj = res.get(j);
    			if(mi.inverse().equals(mj)) {
    				res.remove(j);
    			}
    		}
    		if(b) {
    			res.add(mi);
    		}
    	}
    	return l;
    }
}
