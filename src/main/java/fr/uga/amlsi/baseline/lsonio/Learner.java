package fr.uga.amlsi.baseline.lsonio;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.utils.Pair;

/**
 * Implements the perceptron vector used for the LSO-NIO approach
 * @see <a href="https://arxiv.org/abs/1210.4889"> lsonio</a>
 * @author Maxence Grand
 *
 */
public class Learner {
	/**
	 * The training set
	 */
	private Map<Symbol, List<Pair<List<Double>,List<Double>>>> train;
	/**
	 * The set of classifiers
	 */
	private List<Classifier> classifiers;

	/**
	 *
	 * Constructs
	 * @param train The training set
	 * @param nbBit The number of bit to learn
	 */
	public Learner(
			Map<Symbol, List<Pair<List<Double>,List<Double>>>> train,
			int nbBit) {
		this.train=train;
		this.classifiers = new ArrayList<>();
		for(int i = 0; i < nbBit; i++) {
			classifiers.add(new Classifier(this.train, i));
		}
	}

	/**
	 * train all classifiers
	 */
	public void train() {
		for(int i = 0; i < this.classifiers.size(); i++) {
			classifiers.get(i).Train();
		}
	}

	/**
	 * Return the classifier combination's output
	 * @param a A predicate
	 * @param state The prior vector
	 * @return the classifier combination's output
	 */
	public List<Double> output(Symbol a, List<Double> state) {
		List<Double> res = new ArrayList<>();
		for(int i = 0; i < this.classifiers.size(); i++) {
			if(state.get(i) == null) {
				res.add(null);
			}else if(this.classifiers.get(i).Output(a, state) == -2.0) {
				res.add(null);
			}else if(this.classifiers.get(i).Output(a, state) == 0.0) {
				res.add(state.get(i));
			} else {
				res.add(-1.0*state.get(i));
			}
		}
		return res;
	}

	/**
	 * Compute the weight of a prior vector for a predicate a
	 *
	 * @param a The predicate
	 * @param state The prior vector
	 * @param bit The bit to test
	 * @return The prior vector's weight for a
	 */
	public int weight(Symbol a, List<Double> state, int bit) {
		if(state.get(bit) == null) {
				return 0;
		}else {
			return this.classifiers.get(bit).weigth(a, state);
		}
	}
}
