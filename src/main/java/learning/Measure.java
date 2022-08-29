package learning;

import java.util.List;

/**
 * Different measures use to test the algorithm
 * @author Maxence Grand
 **/
public class Measure {

    /**
     * Compute the recall
     * @param accPos The number of accepted positive accepted sequences
     * @param size The number of positive sequences
     *
     * @return the recall
     **/
    public static float R(int accPos, int size) {
        return (float) accPos / size;
    }

    /**
     * Compute the precision
     * @param accPos The number of accepted positive accepted sequences
     * @param accNeg The number of accepted negative accepted sequences
     *
     * @return the precision
     **/
    public static float P(int accPos, int accNeg) {
        return (float) accPos / (accPos + accNeg); 
    }

    /**
     * Compute the FScore
     *
     * @param R the recall
     * @param P the precision
     *
     * @return the fscore
     */
    public static float FScore(float R, float P) {
        if(R == 0 || P == 0) {
            return 0;
        }
        return (2*P*R) / (P+R);
    }

    /**
     * The accuracy of plannification task
     * @param succ the number of success plan
     * @param N the number of problem
     *
     * @return the accuracy   
     **/
    public static float accuracy(float succ, float N) {
        return succ / N;
    }
    
    /**
     * Compute the error rate
     * @param e The number of errors
     * @param p The number of predicates
     * @return The error rate
     */
    public static float erroRate(List<Integer> e, List<Integer> p) {
        if(e.size() != p.size()) {
            throw new IllegalArgumentException("Bad size");
        }
        
        int sumError = 0;
        int sumPrecond = 0;
        
        for(int i = 0; i < e.size(); i++) {
            sumError += e.get(i);
            sumPrecond += p.get(i);
        }
        
        return (float) sumError / sumPrecond;
    }
}
