package fr.uga.amlsi.baseline.lsonio;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.utils.Pair;

/**
 * This class implements Kernel voted perceptron used by the LSO-NIO approach
 * @see <a href="https://arxiv.org/abs/1210.4889"> lsonio</a>
 *
 * @author Maxence Grand
 *
 */
public class Classifier {
	/**
	 * K for K-DNF kernel
	 */
	public static int K = 3;
	/**
	 * Threshhold for sign function
	 */
	public static double threshold = 0.0;
	/**
	 * Weight alpha
	 */
	private Map<Symbol, Double[]> alphas;
	/**
	 * Voted perceptron
	 */
	private Map<Symbol, Double[]> classifiers;
	/**
	 * The bit to learn
	 */
	private int bit;
	/**
	 * The training set
	 */
	private Map<Symbol, List<Pair<List<Double>,List<Double>>>> train;

	/**
	 *
	 * Constructs
	 * @param train Training set
	 * @param bit The bit to learn
	 */
	public Classifier(
			Map<Symbol, List<Pair<List<Double>,List<Double>>>> train, int bit) {
		this.train = train;
		this.bit = bit;
		alphas = new HashMap<>();
		classifiers = new HashMap<>();
		this.train.forEach((k,v) -> {
			Double[] a = new Double[v.size()];
			Double[] c = new Double[v.size()];
			for(int i = 0; i < a.length; i++) {
				a[i] = 0.0;
				c[i] = 0.0;
			}
			alphas.put(k, a);
			classifiers.put(k, c);
		});


	}
	/**
	 * Train voted perceptrons
	 */
	public void Train() {
		this.train.forEach((a,v) -> {
			int k = 0;
			for(int i = 0; i < v.size(); i++) {
				List<Double> prior = v.get(i).getX();
				List<Double> succ = v.get(i).getY();
				List<Double> diff = Classifier.diff(prior, succ);
				double sum = 0.0;
				for(int j = 0; j<= i; j++) {
					List<Double> diffJ = Classifier.diff(v.get(j).getX(), v.get(j).getY());
					sum += diffJ.get(bit) == null ? 0.0 :
						(this.alphas.get(a)[j] * diffJ.get(bit) *
								Classifier.KDNF(v.get(j).getX(), prior, K));
				}
				double y = Classifier.sign(sum);
				if(diff.get(bit) == null) {
					this.alphas.get(a)[i] = this.alphas.get(a)[i] + 1;
					this.classifiers.get(a)[i] = 1.0;
					k=i;
				} else if (succ.get(bit) == y) {
					this.classifiers.get(a)[k] = this.classifiers.get(a)[k]+1;
				} else {
					this.alphas.get(a)[i] = this.alphas.get(a)[i] + 1;
					this.classifiers.get(a)[i] = 1.0;
					k=i;
				}
			}
		});
	}

	/**
	 * Return the voted perceptron's output
	 * @param a A predicate
	 * @param state The prior vector
	 * @return the voted perceptron's output
	 */
	public double Output(Symbol a, List<Double> state) {
		if(state.get(this.bit) == null) {
			return -2;
		}
		double sum = 0.0;
		for(int i=0;i<this.train.get(a).size();i++) {
			double sum2 = 0.0;
			for(int j = 0; j <= i; j++) {
				List<Pair<List<Double>, List<Double>>> v = this.train.get(a);
				List<Double> diff = Classifier.diff(v.get(j).getX(), v.get(j).getY());
				sum2 += diff.get(bit) == null ? 0.0 :
					(this.alphas.get(a)[j] * diff.get(bit) *
							Classifier.KDNF(diff, state, K));
			}
			sum += Classifier.sign(sum2) * this.classifiers.get(a)[i];
		}

		return Classifier.sign(sum);
	}

	/**
	 * Compute the weight of a prior vector for a predicate a
	 *
	 * @param a The predicate
	 * @param state The prior vector
	 * @return The prior vector's weight for a
	 */
	public int weigth(Symbol a, List<Double> state) {
		if(state.get(this.bit) == null) {
			return 0;
		}
		int sum = 0;
		for(int i=0;i<this.train.get(a).size();i++) {
			double sum2 = 0.0;
			for(int j = 0; j <= i; j++) {
				List<Pair<List<Double>, List<Double>>> v = this.train.get(a);
				List<Double> diff = Classifier.diff(v.get(j).getX(), v.get(j).getY());
				sum2 += diff.get(bit) == null ? 0.0 :
					(this.alphas.get(a)[j] * diff.get(bit) *
							Classifier.KDNF(v.get(j).getX(), state, K));
			}
			sum += Classifier.sign2(sum2) * this.classifiers.get(a)[i];
		}

		return sum;
	}

	/**
	 * Return the sign for a double
	 * @param v the double
	 * @return the sign
	 */
	private static double sign(double v) {
		if(v > Classifier.threshold) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Return the sign for a double
	 * @param v the double
	 * @return the sign
	 */
	private static double sign2(double v) {
		if(v > 0) {
			return 1;
		} else {
			return -1;
		}
	}
	/**
	 * Computation of the combination between n and r
	 * @param n an integer
	 * @param r an integer
	 * @return Comb(n,r)
	 */
	private static int combinations(int n, int r) {
		int numerator = 1, denominator = 1;
		if (r > n - r) {
			r = n - r;
		}
		for (int i = 1; i <= r; ++i) {
			denominator *= i;
		}
		for (int i = n - r + 1; i <= n; ++i) {
			numerator *= i;
		}
		return numerator / denominator;
	}

	/**
	 * Return the number of common bits between two vectors x and y
	 * @param x A vector
	 * @param y A vector
	 * @return Number of common bits
	 */
	private static int same(List<Double> x, List<Double> y) {
		int res = 0;
		for(int i =0; i < x.size(); i++) {
			if(! (x.get(i)==null || y.get(i)==null)) {
				res += x.get(i)==y.get(i) ? 1 : 0;
			}
		}
		return res;
	}

	/**
	 * Computation of the K-DNF kernel.
	 * @param x A vector
	 * @param y A vector
	 * @param k Tke "power" of the DNF kernel
	 * @return The k-DNF kernel of <x,y>
	 */
	private static int KDNF(List<Double> x, List<Double> y,int k) {
		int res = 0;
		for(int l = 0; l <= k; l++) {
			res += Classifier.combinations(same(x,y), l);
		}
		return res;
	}

	/**
	 * Compute the diff vector
	 * @param prior The prior vector
	 * @param succ The successor vector
	 * @return The diff vector
	 */
	public static List<Double> diff(List<Double> prior, List<Double> succ) {
		List<Double> res = new LinkedList<>();
		for(int i = 0; i<prior.size(); i++) {
			if(prior.get(i) == null || succ.get(i) == null) {
				res.add(null);
			} else if(prior.get(i) == 1 && succ.get(i) == 1) {
				res.add(0.0);
			} else if(prior.get(i) == -1 && succ.get(i) == -1) {
				res.add(0.0);
			} else if(prior.get(i) == -1 && succ.get(i) == 1) {
				res.add(1.0);
			} else if(prior.get(i) == 1 && succ.get(i) == -1) {
				res.add(1.0);
			} else {
				res.add(null);
			}
		}
		return res;
	}
}
