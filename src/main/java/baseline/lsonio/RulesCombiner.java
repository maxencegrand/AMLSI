package baseline.lsonio;
import java.util.Map;
import java.util.PriorityQueue;

import fr.uga.generator.symbols.Symbol;

import java.util.ArrayList;
import java.util.List;
import fr.uga.generator.utils.Pair;
import learning.Measure;

/**
 * This class implements algorithm to combine rules
 * @author Maxence Grand
 *
 */
public class RulesCombiner {
	/**
	 * All rules
	 */
	private Map<Symbol, PriorityQueue<Rule>> rules;
	/**
	 * All training data
	 */
	private Map<Symbol, List<Pair<List<Double>,List<Double>>>> training;
	/**
	 * The baseline
	 */
	private Pair<List<Double>, List<Integer>> baseline;
	/**
	 * locked precondition
	 */
	private List<Integer> locks;
	/**
	 * The voted perceptron
	 */
	private Learner learner;



	/**
	 * Constructs
	 * @param rules All rules
	 * @param training All data
	 * @param learner The voted perceptron
	 */
	public RulesCombiner(
			Map<Symbol, PriorityQueue<Rule>> rules,
			Map<Symbol, List<Pair<List<Double>, List<Double>>>> training,
			Learner learner) {
		this.rules = rules;
		this.training = training;
		this.baseline = new Pair<>();
		this.locks = new ArrayList<>();
		this.learner = learner;
	}

	/**
	 * Getter of training
	 * @return the training
	 */
	public Map<Symbol, List<Pair<List<Double>, List<Double>>>> getTraining() {
		return training;
	}

	/**
	 * Setter training
	 * @param training the training to set
	 */
	public void setTraining(Map<Symbol, List<Pair<List<Double>, List<Double>>>> training) {
		this.training = training;
	}

	/**
	 * Getter of baseline
	 * @return the baseline
	 */
	public Pair<List<Double>, List<Integer>> getBaseline() {
		return baseline;
	}

	/**
	 * Setter baseline
	 * @param baseline the baseline to set
	 */
	public void setBaseline(Pair<List<Double>, List<Integer>> baseline) {
		this.baseline = baseline;
	}

	/**
	 * Getter of locks
	 * @return the locks
	 */
	public List<Integer> getLocks() {
		return locks;
	}

	/**
	 * Setter locks
	 * @param locks the locks to set
	 */
	public void setLocks(List<Integer> locks) {
		this.locks = locks;
	}

	/**
	 * Getter of learner
	 * @return the learner
	 */
	public Learner getLearner() {
		return learner;
	}

	/**
	 * Setter learner
	 * @param learner the learner to set
	 */
	public void setLearner(Learner learner) {
		this.learner = learner;
	}

	/**
	 * Getter of rules
	 * @return the rules
	 */
	public Map<Symbol, PriorityQueue<Rule>> getRules() {
		return rules;
	}

	/**
	 * Setter rules
	 * @param rules the rules to set
	 */
	public void setRules(Map<Symbol, PriorityQueue<Rule>> rules) {
		this.rules = rules;
	}

	/**
	 * COmpute fscore
	 * @param a The symbol to learn
	 * @param bit The effect bit
	 * @param pre The precondition
	 * @param training The data
	 * @return The fscore
	 */
	private static float fscore(Symbol a, int bit, List<Double> pre,
			Map<Symbol, List<Pair<List<Double>,List<Double>>>> training) {
		int nAccept = 0;
		int nRelevant = 0;
		int tAccept = 0;
		for(Pair<List<Double>,List<Double>> p : training.get(a)) {
			if(p.getY().get(bit) == null || p.getX().get(bit) == null) {
				continue;
			}
			if(p.getY().get(bit) != p.getX().get(bit)) {
				nRelevant++;
				if(LSONIO.contains(p.getX(), pre)) {
					nAccept++;
					tAccept++;
				}
			} else {
				if(LSONIO.contains(p.getX(), pre)) {
					nAccept++;
				}
			}
		}
		float R = Measure.R(tAccept, nRelevant);
		float P = Measure.P(tAccept, nAccept);
		return Measure.FScore(R, P);
	}

	/**
	 * Check if a rule is compatible with the baseline
	 * @param rule A rule
	 * @return True if the baseline and the rule are compatible
	 */
	private boolean compatibleEffects(Rule rule) {

		if(baseline.getY().contains(rule.getE())) {
			if(baseline.getX().get(rule.getE()) == null ||
					rule.getPre().get(rule.getE()) == null) {
				return true;
			}
			if(baseline.getX().get(rule.getE()) == 1.0
					&& rule.getPre().get(rule.getE()) == -1.0) {
				return false;
			}
			if(baseline.getX().get(rule.getE()) == -1.0
					&& rule.getPre().get(rule.getE()) == 1.0) {
				return false;
			}
		}

		return true;
	}

	/**
	 * backtrack effects
	 * @param a Symbol to backtrack
	 * @return New baseline
	 */
	private Pair<List<Double>, List<Integer>> backtrackEffects(Symbol a) {

		List<Integer> backtracks = new ArrayList<>();
		for(int e : baseline.getY()) {
			if(! this.acceptEffect(a, e)) {
				backtracks.add(e);
			}
		}
		List<Integer> newEff = new ArrayList<>();
		for(int e : baseline.getY()) {
			if(! backtracks.contains(e)) {
				newEff.add(e);
			}
		}
		return new Pair<>(baseline.getX(), newEff);
	}
	/**
	 * Test if an effect is correct for an action
	 * @param a The action
	 * @param testEff Effect bit to test
	 * @return True if the effect is correct
	 */
	private boolean acceptEffect(Symbol a, int testEff) {

		for(int e : baseline.getY()) {
			if(RulesCombiner.fscore(a, testEff, baseline.getX(), training) <
					0.5 * RulesCombiner.fscore(a, e, baseline.getX(),
							training)) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Check the cover of a precondition and an effect of an action
	 * @param a An action
	 * @param e An effect
	 * @param p A precondition
	 * @param training The data
	 * @return The cover
	 */
	private static int covers(Symbol a, int e, List<Double> p,
			Map<Symbol, List<Pair<List<Double>,List<Double>>>> training) {
		int res = 0;
		for(Pair<List<Double>,List<Double>> pair : training.get(a)) {
			if(pair.getY().get(e) == null || pair.getX().get(e) == null) {
				continue;
			}
			if(pair.getY().get(e) != pair.getX().get(e) &&
					LSONIO.contains(pair.getX(), p)) {
				res++;
			}
		}
		return res;
	}

	/**
	 * Check if precondition is correct
     *
     * @param a The action
     * @param next A rule
     * @param precons The precndition
     *
     * @return True if the precondition is accepted
	 */
	private boolean acceptPrecons(
			Symbol a, Rule next, Rule precons) {
		List<Integer> eff = new ArrayList<>();
		eff.addAll(this.baseline.getY());
		eff.add(next.getE());
		for(int e : eff) {
			precons.setW(learner.weight(a, precons.getPre(), e));
			if(precons.getW() <= 0.0) {
				return false;
			} else if (RulesCombiner.covers(
					a, e, precons.getPre(), training) <= 0){
				return false;
			} else if (
					RulesCombiner.fscore(a, e, precons.getPre(), training) <
					0.95 * RulesCombiner.fscore(a, e, baseline.getX(),
							training)){
				return false;
			}

		}
		return true;
	}

	/**
	 * Backtrack precondition
	 * @param a The action
	 * @param next A rule
	 * @param precons The precondition
	 * @return A new baseline
	 */
	private Rule backtrackPrecons(Symbol a, Rule next,Rule precons) {
		List<Integer> backtracks = new ArrayList<>();
		for(int p = 0; p < precons.getPre().size(); p++) {
			if(precons.getPre().get(p) == null ||
					(next.getPre().get(p) != null &&
					baseline.getX().get(p) != null)) {
				continue;
			}
			List<Double> tmp = new ArrayList<>();
			for(int i =0; i < precons.getPre().size(); i++) {
				if(i == p) {
					tmp.add(null);
				} else {
					tmp.add(precons.getPre().get(i));
				}
			}
			Rule alternative = new Rule(tmp, precons.getE(), precons.getW());
			if(this.acceptPrecons(a, next, alternative)) {
				backtracks.add(p);
			}
		}
		for(int p : backtracks) {
			precons.remove(p);
		}
		return new Rule(precons.getPre(), precons.getE(), precons.getW());
	}

	/**
	 * Combine two precondition
	 * @param a The action
	 * @param next A rule
	 * @param precons A rule
	 * @return Combined precondition
	 */
	private boolean combinePrecons(Symbol a, Rule next,	Rule precons) {
		List<Integer> conflicts = new ArrayList<>();
		for(int p = 0; p < baseline.getX().size(); p++) {
			if(baseline.getX().get(p) == null &&
					next.getPre().get(p) == null) {
				continue;
			} if(next.getPre().get(p) != null &&
					baseline.getX().get(p) != next.getPre().get(p)) {
				conflicts.add(p);
			} else {
				precons.getPre().set(p, baseline.getX().get(p));
			}
		}
		boolean b1 = true;
		for(int e : baseline.getY()) {
			b1 &= learner.weight(a, precons.getPre(), e) > 0;
		}
		if(b1) {
			this.locks.addAll(conflicts);
		} else {
			for(int p : conflicts) {
				double posweight = 0.0, negweight = 0.0;
				for(int e : baseline.getY()) {
					List<Double> tmp = new ArrayList<>();
					tmp.addAll(precons.getPre());
					tmp.set(p, 1.0);
					posweight += learner.weight(a, tmp, e);
					tmp.set(p, -1.0);
					negweight += learner.weight(a, tmp, e);
				}
				boolean b2 = true, b3 = true;
				for(int e : baseline.getY()) {
					List<Double> tmp = new ArrayList<>();
					tmp.addAll(precons.getPre());
					tmp.set(p, 1.0);
					b2 &= learner.weight(a, tmp, e) > 0;
					tmp.set(p, -1.0);
					b3 &= learner.weight(a, tmp, e) > 0;
				}
				if(b2) {
					if(posweight > negweight) {
						precons.getPre().set(p, 1.0);
					}
				} else if (b3) {
					if(posweight < negweight) {
						precons.getPre().set(p, -1.0);
					}
				} else {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Combine all rules for an action
	 * @param a The action
	 * @return The precondition and the list of effect bits
	 */
	public Pair<List<Double>, List<Integer>> combine(Symbol a) {
		Rule hwr = this.rules.get(a).peek();
		if(hwr == null) {
			return null;
		}
		this.baseline = new Pair<>(hwr.getPre(), new ArrayList<>());
		this.locks = new ArrayList<>();

		while(! this.rules.get(a).isEmpty()) {
			Rule next = this.rules.get(a).poll();
			if(this.compatibleEffects(next)) {
				List<Double> tmp = new ArrayList<>();
				for(int i = 0; i < this.baseline.getX().size(); i++) {
					tmp.add(next.getPre().get(i));
				}
				Rule newPre = new Rule(tmp, 0, 0);
				if(this.combinePrecons(a, next, newPre)) {
					this.backtrackPrecons(a, next, newPre);
					if(this.acceptPrecons(a, next, newPre)) {
						this.baseline.setX(newPre.getPre());
					}
					if(this.acceptEffect(a, next.getE())) {
						this.baseline.getY().add(next.getE());
					}
					this.backtrackEffects(a);
				}
			}
		}
		return this.baseline;
	}
}
