package fsm;

/**
 * A pair of elements
 * @author Maxence Grand
 * @param <X> The first element
 * @param <Y> The second element
 */
public class Pair<X,Y>{
    /**
     * Element x
     */
    private X x;

    /**
     * Set x
     * @param x The new value of x
     */
    public void setX(X x) {
        this.x = x;
    }

    /**
     * Set y
     * @param y The new value of y
     */
    public void setY(Y y) {
        this.y = y;
    }
    
    /**
     * The element y 
     */
    private Y y;

    public Pair() {
    	this.x = null;
    	this.y = null;
    }
    /**
     * Constructs the pair
     * @param x The element x
     * @param y The element y
     */
    public Pair(X x, Y y){
        this.x = x;
        this.y = y;
    }

    /**
     * Clone the pair
     * @return A clone
     */
    @Override
    public Pair<X,Y> clone(){
        return new Pair<>(x,y);
    } 
    
    /**
     * Return the element x
     * @return x
     */
    public X getX(){
        return x;
    }

    /**
     * Return the element y
     * @return y
     */
    public Y getY(){
        return y;
    }
    
    /**
     * The hashcode
     * @return the hashcode
     */
    @Override
    public int hashCode(){
        return x.hashCode() + y.hashCode();
    }
    
    /**
     * Check if two pairs are equals
     * @param other the pair to compare
     * @return True of the two pairs are equals
     */
    @Override
    public boolean equals(Object other){
        if (other instanceof Pair){
            Pair<?, ?> p = (Pair<?, ?>) other;
            return p.x.equals(x) && p.y.equals(y);
        }
        return false;
    }
    
    /**
     * String representation of a Pair
     * @return A string
     */
    @Override
    public String toString(){
        return "("+x+","+y+")";
    }
}
