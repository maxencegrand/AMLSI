package fr.uga.amlsi.fsm;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import fr.uga.generator.exception.BlocException;
import fr.uga.generator.symbols.Action;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.generator.utils.Pair;

/**
 * Factories of remarkable automaton
 * @author Maxence Grand
 */
public class FSAFactory{
	/**
	 * The action representing the empty word &#949;
	 */
	public final static Symbol EPSILON = new Action("_");

	/**
	 * Constructs Prefix Tree Acceptor PTA
	 * @param I The sample
	 * @param a The alphabet
	 * @return The pta
	 * @throws BlocException
	 */
	public static DFA PTA(Sample I, Alphabet a)
			throws BlocException{
		DFA fsa = new DFA();
		fsa.setSigma(a);
		int t = 1;
		fsa.setQ0(0);
		fsa.getQ().add(0);
		fsa.getF().add(0);
		fsa.getPartition().getBlocs().add(new Bloc(0));
		int maxSize = 0;
		for(Trace example : I.getExamples()){
			if(maxSize < example.size()){
				maxSize = example.size();
			}
		}
		Queue<List<Symbol>> prefixes = new LinkedList<>();
		for(int i=1; i<=maxSize; i++){
			for(Trace trace : I.getExamples()){
				Example example = (Example)trace;
				if(example.size() >= i){
					List<Symbol> l = new ArrayList<>();
					for(int j =0; j<i; j++){
						l.add(example.get(j));
					}
					if(!prefixes.contains(l)){
						prefixes.offer(l);
					}
				}else{
					continue;
				}
			}
		}
		while(! prefixes.isEmpty()){
			List<Symbol> seq = prefixes.poll();
			int q = fsa.getQ0();
			for(Symbol s : seq){
				if(fsa.containsTransition(new Pair<>(q,s))){
					q = fsa.getTransition(q,s);
				}else{
					fsa.getQ().add(t);
					fsa.getPartition().getBlocs().add(new Bloc(t));
					fsa.addTransition(new Pair<>(q, s), t);
					q = (t++);
				}
			}
		}
		for(Trace trace : I.getExamples()){
			Example example = (Example)trace;
			int q = fsa.getQ0();
			for(Symbol s : example.getActionSequences()){
				q = fsa.getTransition(q,s);
			}
			fsa.getF().add(q);
		}
		for(int q : fsa.getQ()){
			if(! fsa.getF().contains(q)){
				fsa.getF().add(q);
			}
		}
		return fsa;
	}

	/**
	 * Constructs Prefix Tree Acceptor PTA
	 * @param I The sample
	 * @param a The alphabet
	 * @return The pta
	 * @throws BlocException
	 */
	public static DFA PTA_(Sample I, Alphabet a)
			throws BlocException{
		DFA fsa = new DFA();
		fsa.setSigma(a);
		for(Trace trace : I.getExamples()){
			Example example = (Example)trace;
			//First Example
			if(fsa.getQ().size() == 0){
				int n = example.size();
				for(int i=0; i<=n;i++){
					fsa.getQ().add(i);
					fsa.getPartition().getBlocs().add(new Bloc(i));
				}
				fsa.setQ0(0);
				fsa.getF().add(n);
				int q = 0;
				for(Symbol s : example.getActionSequences()){
					fsa.addTransition(new Pair<>(q,s),q+1);
					q++;
				}
			}else{
				int q = fsa.getQ0();
				int i=1;
				int l = example.size();
				for(Symbol s : example.getActionSequences()){
					if(fsa.containsTransition(new Pair<>(q,s))){
						q = fsa.getTransition(q,s);
					}else{
						int qNext = fsa.getQ().size();
						fsa.getQ().add(qNext);
						fsa.getPartition().getBlocs().add(new Bloc(qNext));
						fsa.addTransition(new Pair<>(q, s), qNext);
						if(i == l){
							fsa.getF().add(qNext);
						}
						q = qNext;
					}
					i++;
				}
			}
		}
		if(! fsa.getF().contains(fsa.getQ0())){
			fsa.getF().add(fsa.getQ0());
		}
		return fsa;
	}

	/**
	 * Constructs the universal automaton ua
	 * @param a The alphab
	 * @return The ua
	 * @throws BlocException
	 */
	public static DFA UA(Alphabet a)
			throws BlocException{
		DFA fsa = new DFA();
		fsa.setSigma(a);
		fsa.getQ().add(0);
		fsa.setPartition(new Partition(fsa.getQ().size()));
		fsa.getF().add(0);
		fsa.setQ0(0);
		for(Symbol s : fsa.getSigma().getSymboles()){
			fsa.addTransition(new Pair<>(fsa.getQ0(), s),fsa.getQ0());
		}
		return fsa;
	}

	/**
	 * Derive The automaton a with the partition pi
	 * @param a The automaton
	 * @param pi The new partition
	 * @param oldPi The previous partition
	 * @return The automaton A \ pi
	 * @throws BlocException
	 */
	public static DFA derive(DFA a,
			Partition pi,
			Partition oldPi)
					throws BlocException{
		DFA fsa = a.clone();
		fsa.setPartition(pi);
		fsa.getDelta().clear();
		fsa.getDeltaBloc().clear();
		//Compute delta'
		a.getDelta().forEach((q,v) -> {
			v.forEach((sym,l) -> {
				l.forEach(qPrime -> {
					try {
						fsa.addTransition(new Pair<>(q,sym), qPrime);
					} catch (BlocException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.exit(1);
					}
				});
			});
		});
		return fsa;
	}

	/**
	 * Generalize the automaton fsa
	 * @param fsa The automaton
	 * @param a The alphabet
	 * @param I The negative sample
	 * @return generalized fsa
	 * @throws BlocException
	 */
	public static DFA generalize(DFA fsa,
			Alphabet a, Sample I)
					throws BlocException{

		DFA clone = fsa.clone();
		for(Bloc b : clone.getPartition().getBlocs()){
			for(Symbol s : clone.getSigma().getSymboles()){
				if(!clone.containsBlocTransition(new Pair<>(b,s))){
					clone.addTransition(new Pair<>(b.min(),s), b.min());
					if(clone.acceptOneOf(I)){
						clone.delTransition(new Pair<>(b.min(),s), b.min());
					}
				}
			}
		}

		DFA clone2 = clone.clone();
		int newQidx = 0;
		for(Bloc b : clone.getPartition().getBlocs()){
			if(clone.getPossibleBlocTransition(b.min()).size() <
					clone.getSigma().getSymboles().size()){
				int newQ = clone.getQ().size()+(newQidx++);
				clone2.getQ().add(newQ);
				clone2.getF().add(newQ);
				clone2.getPartition().getBlocs().add(new Bloc(newQ));
				clone2.addTransition(new Pair<>(newQ, EPSILON), b.min());
				clone2.addTransition(new Pair<>(b.min(), EPSILON), newQ);
				for(Symbol s : clone2.getSigma().getSymboles()){
					if(! clone.getPossibleBlocTransition(b.min()).contains(s)){
						clone2.addTransition(new Pair<>(newQ, s), newQ);
					}
				}
			}

		}
		return clone2;
	}

	/**
	 * The empty automaton ie the automaton acceptng only the empty word
	 * @param a the alphabet
	 * @return The empty automaton
	 * @throws BlocException
	 */
	public static DFA emptyFSA(Alphabet a) throws BlocException {
		DFA fsa = new DFA();
		fsa.setSigma(a);
		fsa.setQ0(0);
		fsa.getQ().add(0);
		fsa.getF().add(0);
		fsa.getPartition().getBlocs().add(new Bloc(0));
		return fsa;
	}

	/**
	 * Constructs Prefix Tree Acceptor PTA from an automaton
	 * @param A The automaton
	 * @return The pta
	 * @throws BlocException
	 */
	public static DFA PTA(DFA A)
			throws BlocException{
		DFA fsa = new DFA();
		fsa.setSigma(A.getSigma());
		fsa.setQ0(A.getQ0());
		for(int q : A.getQ()) {
			fsa.getQ().add(q);
			fsa.getF().add(q);
			fsa.getPartition().getBlocs().add(new Bloc(q));
		}
		for(int q : A.getF()) {
			fsa.getF().add(q);
		}
		A.getDelta().forEach((q,v) -> {
			Bloc b1;
			try {
				b1 = fsa.getPartition().getBloc(q);
				if(!fsa.getDelta().containsKey(q)) {
					fsa.getDelta().put(q, new HashMap<>());
				}
				if(!fsa.getDeltaBloc().containsKey(b1)) {
					fsa.getDeltaBloc().put(b1, new HashMap<>());
				}
				v.forEach((sym,l) -> {
					if(!fsa.getDelta().get(q).containsKey(sym)) {
						fsa.getDelta().get(q).put(sym, new ArrayList<>());
					}
					if(!fsa.getDeltaBloc().get(b1).containsKey(sym)) {
						fsa.getDeltaBloc().get(b1).put(sym, new ArrayList<>());
					}
					l.forEach(qPrime -> {
						try {
							fsa.getDelta().get(q).get(sym).add(qPrime);
							Bloc b2 = fsa.getPartition().getBloc(qPrime);
							fsa.getDeltaBloc().get(b1).get(sym).add(b2);
						} catch (BlocException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.exit(1);
						}
					});
				});
			} catch (BlocException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.exit(1);
			}
		});
		return fsa;
	}

}
