package learning;

import java.util.List;
import java.util.Map;

import fr.uga.generator.exception.BlocException;
import fr.uga.generator.generator.Generator;
import fr.uga.generator.simulator.Oracle;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.generator.utils.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import fsm.*;
import main.Argument;

/**
 * This class implements the automata learning module.
 *
 * @author Maxence Grand
 * @version 1.0
 * @since   10-2019
 */
public class AutomataLearning {
	/**
	 * The set of predicates
	 */
	protected List<Symbol> predicates;
	/**
	 * The set of actions
	 */
	protected List<Symbol> actions;
	/**
	 * The oracle used to generate positive and negative samples.
	 */
	protected Oracle sim;
	/**
	 * A set of positive sequences. Used for experimentations.
	 */
	protected Sample testPositive;
	/**
	 * A set of negative sequences. Used for experimentations.
	 */
	protected Sample testNegative;
	/**
	 * The generator used to generate positive and negative samples
	 */
	protected Generator generator;
	/**
	 * The set of pairewise constraints R
	 */
	protected Map<Symbol, List<Symbol>> rules;
	/**
	 * The action model learning module.
	 */
	protected DomainLearning domainLearner;

	/**
	 * The constructor of the object AucleatomataLearning
	 */
	public AutomataLearning(){

	}

	/**
	 * The constructor of the object AutomataLearning
	 * @param act The set of actions.
	 *  */
	public AutomataLearning(List<Symbol> act){
		this.actions = new ArrayList<>();
		for(Symbol a : act){
			this.actions.add(a);
		}

		//Create all pairs
		this.rules = new HashMap<>();
		for(Symbol s1 : this.actions){
			rules.put(s1, new ArrayList<>());
			for(Symbol s2 : this.actions){
				this.rules.get(s1).add(s2);
			}
		}
	}

	/**
	 * The constructor of the object AutomataLearning
	 *
	 * @param pred The set of predicates
	 * @param act The set of actions
	 * @param g The generator
	 * @param domainLearner The action model learning module
	 */
	public AutomataLearning(
			List<Symbol> pred,
			List<Symbol> act,
			Generator g,
			DomainLearning domainLearner
			){

		//The set of predicates
		this.predicates = new ArrayList<>();
		for(Symbol a : pred){
			this.predicates.add(a);
		}

		//The set of actions
		this.actions = new ArrayList<>();
		for(Symbol a : act){
			this.actions.add(a);
		}

		//The genrator
		this.generator = g;

		//All rules
		//Create all pairs
		this.rules = new HashMap<>();
		for(Symbol s1 : this.actions){
			rules.put(s1, new ArrayList<>());
			for(Symbol s2 : this.actions){
				this.rules.get(s1).add(s2);
			}
		}

		//Learning module
		this.domainLearner = domainLearner;
	}

	/**
	 * Set test samples
	 * @param pos A positive test sample
	 * @param neg A negative test sample
	 */
	public void setSamples(Sample pos, Sample neg) {
		this.testPositive = pos;
		this.testNegative = neg;
	}



	/**
	 * Remove obsolete rules
	 * @param example Positive example used to update rules
	 */
	public void removeRules(Example example){
		if(example.size()>1){
			for(int i=1; i<example.size(); i++){
				if(this.rules.containsKey(example.get(i-1))) {
					this.rules.get(example.get(i-1)).remove(example.get(i));
					if(this.rules.get(example.get(i-1)).isEmpty()) {
						this.rules.remove(example.get(i-1));
					}
				}
			}
		}
	}

	/**
	 * Remove obsolete rules
	 * @param x Positive example used to update rules
	 * @param y Positive example used to update rules
	 */
	public void removeRules(Symbol x, Symbol y){
		if(this.rules.containsKey(x)) {
			this.rules.get(x).remove(y);
			if(this.rules.get(x).isEmpty()) {
				this.rules.remove(x);
			}
		}
	}
	/**
	 * Print rules
	 */
	public void printRules(){
		this.rules.forEach((k,v) -> {
			v.forEach(a -> System.out.println(k+" "+a));
		});
	}

	/**
	 * Check if the automaton rejects all rules
	 *
	 * @param A The fsa
	 * @return True if fsa rejects all rules
	 * @throws BlocException
	 */
	public boolean rejectRules(DFA A) throws BlocException{

		if(this.rules.isEmpty()) {
			return true;
		}
		for(Bloc b : A.getPartition().getBlocs()){
			List<Symbol> l = A.getPossibleBlocTransition(b.min());
			for(Symbol s : l){

				Bloc b2 = A.getBlocTransition(b,s);
				if(this.rules.containsKey(s)) {
					List<Symbol> l2 = A.getPossibleBlocTransition(b2.min());
					for(Symbol s2 : l2){
						if(this.rules.get(s).contains(s2)){
							return false;
						}
					}
				}

			}
		}
		return true;
	}

	/**
	 *
	 * Return all rules that fsa doesn't reject
	 * @param A the fsa
	 * @return A set of actions pair
	 * @throws BlocException
	 */
	public List<Pair<Symbol, Symbol>> getAcceptedRules(DFA A)
			throws BlocException{
		List<Pair<Symbol, Symbol>> res = new ArrayList<>();
		for(Bloc b : A.getPartition().getBlocs()){
			List<Symbol> l = A.getPossibleBlocTransition(b.min());
			for(Symbol s : l){
				Bloc b2 = A.getBlocTransition(b,s);
				if(this.rules.containsKey(s)) {
					List<Symbol> l2 = A.getPossibleBlocTransition(b2.min());
					for(Symbol s2 : l2){
						if(this.rules.get(s).contains(s2)){
							res.add(new Pair<>(s,s2));
						}
					}
				}
			}
		}
		return res;
	}

	/**
	 * return the state responsible of the acceptance of e rule
	 * @param A the fsa
	 * @param r the accepted rule
	 * @return A state
	 * @throws BlocException
	 */
	public int stateAcceptorRules(DFA A, Pair<Symbol,
			Symbol> r) throws BlocException {
		for(Bloc b : A.getPartition().getBlocs()){
			List<Symbol> l = A.getPossibleBlocTransition(b.min());
			for(Symbol s : l){
				Bloc b2 = A.getBlocTransition(b,s);
				if(this.rules.containsKey(s)) {
					List<Symbol> l2 = A.getPossibleBlocTransition(b2.min());
					for(Symbol s2 : l2){
						if(this.rules.get(s).contains(s2)){
							return b2.min();
						}
					}
				}
			}
		}
		return A.getQ().size()-1;
	}

	/**
	 * Learn an automaton
	 * @param pos The positive learning sample
	 * @param neg The positive learning sample
	 * @return A pair containing an automaton and the corresponding partition
	 * @throws BlocException
	 */
	public Pair<DFA, Partition>RPNIsemi(Sample pos, Sample neg)
			throws BlocException{
		//		neg.sort();
		Sample newPos = new Sample();
		int idx = 0;
		DFA A = null;
		Partition pi = null;
		do {
			newPos.addExample(pos.getExamples().get(idx));
			if(idx == 0) {
				A = FSAFactory.PTA(pos, new Alphabet(actions));
			} else {
				if(A.accept((Example)pos.getExamples().get(idx))) {
					pi = A.extension((Example) pos.getExamples().get(idx), pi);
					idx++;
					continue;
				} else {
					pi = A.extension((Example) pos.getExamples().get(idx), pi);
				}
			}
			int N = A.getQ().size();
			if(idx == 0) {
				pi= new Partition(N);
			}
			idx++;
			Map<Integer, List<Integer>> testPairBloc = new HashMap<>();
			for(int i=1; i<N; i++){
				List<Integer> testedBloc = new ArrayList<>();
				for(int j =0; j<i; j++){
					 //System.out.println(i+" "+j+" "+N);
					//Check if we have already test these blocs
					if(pi.getBloc(i).min() == pi.getBloc(j).min()){
						continue;
					}
					if(testedBloc.contains(pi.getBloc(j).min())){
						continue;
					}
					if(testPairBloc.containsKey(pi.getBloc(i).min()) &&
							testPairBloc.get(pi.getBloc(i).min()).contains(
									pi.getBloc(j).min())) {
						continue;
					}
					if(! testPairBloc.containsKey(pi.getBloc(i).min())) {
						testPairBloc.put(pi.getBloc(i).min(), new ArrayList<>());
					}
					if(! testPairBloc.containsKey(pi.getBloc(j).min())) {
						testPairBloc.put(pi.getBloc(j).min(), new ArrayList<>());
					}
					testPairBloc.get(pi.getBloc(j).min()).add(pi.getBloc(i).min());
					testPairBloc.get(pi.getBloc(i).min()).add(pi.getBloc(j).min());
					testedBloc.add(pi.getBloc(j).min());;
	
					Partition piPrime = pi.clone();
					piPrime.merge(j,i);
	
					//Determinist merge
					DFA APrime = FSAFactory.derive(A, piPrime, pi);
					Partition piPrimePrime = APrime.determinization();
	
					//Check if I- and R are reject
					if(this.rejectRules(APrime)) {
						if(! APrime.acceptOneOf(neg)){
								pi = piPrimePrime;
								A = APrime;
						}
					}
				}
			}
		}while(idx < pos.meanSize());
		return new Pair<>(A,pi);
	}

	public DFA RPNI(Sample pos, Sample neg)
			throws BlocException{
		DFA A = FSAFactory.PTA(pos, new Alphabet(actions));
		neg = A.getRejected(neg);
		int N = A.getQ().size();
		boolean[][] compatibilityMatrix = new boolean[N][N];
		for(int i = 0; i < N; i++) {
			for(int j = 0; j < N; j++) {
				compatibilityMatrix[i][j] = true;
			}
		}
		/*List<List<Symbol>> imp = impossibleTransition(A, neg);
		for(int i = 0; i < N; i++) {
			List<Symbol> act = A.getPossibleTransition(i);
			for(int j = 0; j < N; j++) {
				for(Symbol a : imp.get(j)) {
					if(act.contains(a)) {
						compatibilityMatrix[i][j] = false;
						compatibilityMatrix[j][i] = false;
//						System.outs.println(i+" "+j);
					}
				}
			}
		}*/
		//System.out.println(Arrays.deepToString(compatibilityMatrix));
//		System.exit(1);
		Partition pi = new Partition(N);
		Map<Integer, List<Integer>> testPairBloc = new HashMap<>();
		for(int i=1; i<N; i++){
			List<Integer> testedBloc = new ArrayList<>();
			for(int j =0; j<i; j++){
				//System.out.println(i+" "+j+" "+N);
				//Check if we have already test these blocs
				if(pi.getBloc(i).min() == pi.getBloc(j).min()){
					continue;
				}
				if(testedBloc.contains(pi.getBloc(j).min())){
					continue;
				}
				if(testPairBloc.containsKey(pi.getBloc(i).min()) &&
						testPairBloc.get(pi.getBloc(i).min()).contains(
								pi.getBloc(j).min())) {
					continue;
				}
				if(! testPairBloc.containsKey(pi.getBloc(i).min())) {
					testPairBloc.put(pi.getBloc(i).min(), new ArrayList<>());
				}
				if(! testPairBloc.containsKey(pi.getBloc(j).min())) {
					testPairBloc.put(pi.getBloc(j).min(), new ArrayList<>());
				}
				testPairBloc.get(pi.getBloc(j).min()).add(pi.getBloc(i).min());
				testPairBloc.get(pi.getBloc(i).min()).add(pi.getBloc(j).min());
				testedBloc.add(pi.getBloc(j).min());;

				Partition piPrime = pi.clone();
				piPrime.merge(j,i);
				
				for(int k = 1; k<piPrime.getBloc(i).getStates().size(); k++) {
					int q1 = piPrime.getBloc(i).getStates().get(k-1);
					int q2 = piPrime.getBloc(i).getStates().get(k);
					if(!compatibilityMatrix[q1][q2]) {
						continue;
					}
				}
				//Determinist merge
				DFA APrime = FSAFactory.derive(A, piPrime, pi);
				Partition piPrimePrime = APrime.determinization();
				boolean reject = true;
				for(Bloc b1 : APrime.getPartition().getBlocs()) {
					if(b1.min() < i || b1.getStates().size()<=1) {
						continue;
					}
					for(int k = 1; k<b1.getStates().size(); k++) {
						int q1 = b1.getStates().get(k-1);
						int q2 = b1.getStates().get(k);
						reject &= compatibilityMatrix[q1][q2];
						reject &= compatibilityMatrix[q2][q1];
					}
				}
//				if(stop) {
//					continue;
//				}
				//Check if I- and R are reject
				if(reject) {
					if(this.rejectRules(APrime) && ! APrime.acceptOneOf(neg)){
							pi = piPrimePrime;
							A = APrime;
					}
				}
			}
		}
		return A;
	}
	/**
	 * Learn an automaton incrementally
	 * @param pos The positive learning sample already used
	 * @param neg The new learning sample already used
	 * @param newPos All new positive examples
	 * @param newNeg All new negative examples
	 * @param A The current fsa
	 * @param pi The current partition
	 * @return A pair containing the update automaton and the corresponding partition
	 * @throws BlocException
	 */
	public DFA RPNI2(Sample pos, Sample neg,
			Sample newPos,
			Sample newNeg,
			DFA A,
			Partition pi)
					throws BlocException{
		boolean b = true;

		for(Trace x : newPos.getExamples()) {
			if(A.accept((Example)x)){
				pi = A.extension((Example)x, pi);
				b &= true;
			}else{
				pi = A.extension((Example)x, pi);
				b = false;
			}
		}
		for(Trace x : newNeg.getExamples()) {
			if(! A.accept((Example)x)){
				b &= true;
			}else{
				b = false;
			}
			if(!neg.getExamples().contains(x)) {
				neg.addExample(x);
			}
		}
		if(b) {
			return A;
		}
		A = FSAFactory.PTA(pos, new Alphabet(actions));
		int N = A.getQ().size();
		pi = new Partition(N);
		DFA APrime;


		//States merges
		Map<Integer, List<Integer>> testPairBloc = new HashMap<>();
		for(int i=1; i<N; i++){
			List<Integer> testedBloc = new ArrayList<>();
			for(int j = 0; j < i; j++){
			//for(int j : A.getPartition().getMins(i)){
				//Check if we have already test these blocs
				if(pi.getBloc(i).min() == pi.getBloc(j).min()){
					continue;
				}
				if(testedBloc.contains(pi.getBloc(j).min())){
					continue;
				}
				if(testPairBloc.containsKey(pi.getBloc(i).min()) &&
						testPairBloc.get(pi.getBloc(i).min()).contains(
								pi.getBloc(j).min())) {
					continue;
				}
				if(testPairBloc.containsKey(pi.getBloc(i).min()) &&
						testPairBloc.get(pi.getBloc(i).min()).contains(
								pi.getBloc(j).min())) {
					continue;
				}
				if(! testPairBloc.containsKey(pi.getBloc(i).min())) {
					testPairBloc.put(pi.getBloc(i).min(), new ArrayList<>());
				}
				if(! testPairBloc.containsKey(pi.getBloc(j).min())) {
					testPairBloc.put(pi.getBloc(j).min(), new ArrayList<>());
				}
				testPairBloc.get(pi.getBloc(j).min()).add(pi.getBloc(i).min());
				testPairBloc.get(pi.getBloc(i).min()).add(pi.getBloc(j).min());
				testedBloc.add(pi.getBloc(j).min());

				//Compute pi'
				Partition piPrime = pi.clone();
				piPrime.merge(j,i);
				APrime = FSAFactory.derive(A, piPrime, pi);
				Partition piPrimePrime = APrime.determinization();

				//Check if the new automaton reject I- and R
				if(this.rejectRules(APrime)){
					if(! APrime.acceptOneOf(neg)) {
						A  = APrime;
						pi = piPrimePrime;
						break;
					}
				}
			}
		}

		return A;
	}
	
	

	/**
	 * Test an automaton
	 * @param A The automaton to test
	 * @return the fscore
	 * @throws BlocException
	 */
	public float test(DFA A) throws BlocException {
		int posAcc = 0, negAcc = 0;

		//Compute the number of accepted positive test example
		for(Trace posExample : testPositive.getExamples()) {
			posAcc += A.accept((Example)posExample) ? 1 : 0;
		}

		//Compute the number of accepted negative test example
		for(Trace negExample : testNegative.getExamples()) {
			negAcc += A.accept((Example)negExample) ? 1 : 0;
		}
		
		System.out.println("Recall = "+Measure.R(posAcc,
				testPositive.getExamples().size()));
		System.out.println("Precision = "+Measure.P(posAcc, negAcc));

		//Compute the fscore
		return Measure.FScore(Measure.R(posAcc, testPositive.getExamples().
				size()),
				Measure.P(posAcc, negAcc));
	}

	/**
	 * Update R
	 * @param examples Positive sample
	 */
	public void constructOptimalR(Sample examples) {
		for(Trace s : examples.getExamples()) {
			this.removeRules((Example)s);
		}
	}

	/**
	 * Clean R to use RPNI for regular grammar induction
	 */
	public void emptyR() {
		this.rules = new HashMap<>();
	}

	/**
	 * Decompose all positive example
	 * @param pos I+
	 * @return A positive sample
	 */
	public Sample decompose(Sample pos) {
		List<Trace> p = new ArrayList<>();
		for(Trace seq : pos.getExamples()){
			Example seqBis = (Example) seq;
			int M = seqBis.size();
			for(int i=1; i< M+1; i++){
				Example x = new Example();
				for(int j =0 ; j<i; j++){
					x.add(seqBis.get(j));
				}
				p.add(x);
			}
		}
		return new Sample(p);
	}

	/**
	 * Give compression data
	 * @param A The automaton
	 * @param pos The positive sample
	 */
	public void writeDataCompression(DFA A, Sample pos) {
		float nbObs = ( pos.size() * pos.meanSize());
		int nbState = A.getPartition().getBlocs().size();
		float compressionLevel = (float) nbObs / nbState;
		System.out.println("#Observed states : "+nbObs);
		System.out.println("#States : "+nbState);
		System.out.println("#Transitions : "+A.getNumberTransitions());
		System.out.println("Compression level : "+compressionLevel);
	}
	
	/**
	 * 
	 * @param pta
	 * @param I
	 * @return
	 */
	private static List<List<Symbol>> impossibleTransition(
			DFA pta, 
			Sample I) {
		List<List<Symbol>> res = new ArrayList<>();
		for(int q : pta.getQ()) {
			res.add(new ArrayList<>());
		}
		for(Trace t : I.getExamples()) {
			if(t.size()==1) {
				res.get(0).add(t.getActionSequences().get(0));
				continue;
			}
			List<Symbol> tmp = new ArrayList<>();
			for(int i = 0; i < t.size()-1; i++) {
				tmp.add(t.getActionSequences().get(i));
			}
			Trace newT = new Example(tmp);
			int q = pta.getState(newT, 0);
			res.get(q).add(t.getActionSequences().get(t.size()-1));
		}
		return res;
	}
}
