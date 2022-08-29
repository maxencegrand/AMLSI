/**
 * 
 */
package fr.uga.amlsi.learning.temporal;

import java.util.ArrayList;
import java.util.List;

import fr.uga.amlsi.learning.Domain;

/**
 * @author Maxence Grand
 *
 */
public class BufferTemporal {
	/**
	 * 
	 */
	List<Domain> indiv;
	/**
	 * 
	 */
    private boolean available	;
    
    /**
     * 
     * Constructs
     */
    public BufferTemporal () {
    	this.indiv= new ArrayList<>();
    	available=true;
    }
    
    /**
     * 
     */
    public synchronized boolean indivWating(Domain i) {
    	boolean b = false;
        while (!available) {
            try {
            	wait();
            	available=true;}
            catch (InterruptedException e) { }
        }
        available = false;
        b = !this.indiv.contains(i);
        if(b) {
        	this.indiv.add(i);
        }
        available=true;
        notifyAll();
        return b;
    }

}
