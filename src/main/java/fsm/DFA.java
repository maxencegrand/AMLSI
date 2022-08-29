package fsm;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Queue;

import fr.uga.generator.exception.BlocException;
import fr.uga.generator.symbols.Symbol;
import fr.uga.generator.symbols.Task;
import fr.uga.generator.symbols.trace.Example;
import fr.uga.generator.symbols.trace.RandomTaskTrace;
import fr.uga.generator.symbols.trace.Sample;
import fr.uga.generator.symbols.trace.TaskTrace;
import fr.uga.generator.symbols.trace.Trace;
import fr.uga.generator.utils.Pair;
import fr.uga.generator.symbols.Method;

/**
 * This class represent a finite state automaton
 * @author Maxence Grand
 */
public class DFA{
	/**
	 * the Alphabet
	 */
	private Alphabet sigma;
	/**
	 * The set of State
	 */
	private List<Integer> Q;
	/**
	 * The set of Final state
	 */
	private List<Integer> F;
	/**
	 * The initial state
	 */
	private int q0;
	/**
	 * The set of Transition
	 */
	private Map<Integer, Map<Symbol, List<Integer>>> delta;
	/**
	 * The set of bloc transition
	 */
	private Map<Bloc, Map<Symbol, List<Bloc>>> deltaBloc;
	//private List<Pair<Pair<Bloc, Symbol>, Bloc>> deltaBloc;
	/**
	 * The bloc's partition
	 */
	private Partition partition;

	/**
	 * Constructs an automaton
	 */
	public DFA(){
		this.delta = new HashMap<>();
		this.deltaBloc = new HashMap<>();
		this.Q = new ArrayList<>();
		this.F = new ArrayList<>();
		partition = new Partition();
	}

	/**
	 * Constructs an automaton from an other automaton
	 * @param other an automaton
	 * @throws exception.BlocException
	 */
	public DFA(DFA other)throws BlocException{
		this();
		this.setQ0(other.getQ0());
		for(int q : other.getQ()){
			this.getQ().add(q);
		}
		for(int q : other.getF()){
			this.getF().add(q);
		}
		Alphabet a = new Alphabet(other.getSigma().getSymboles());
		this.setSigma(a);
		partition = other.partition.clone();
		other.delta.forEach((q,v) -> {
			v.forEach((sym,l) -> {
				l.forEach(qPrime -> {
					try {
						this.addTransition(new Pair<>(q,sym), qPrime);
					} catch (BlocException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.exit(1);
					}
				});
			});
		});
	}

	/**
	 * Clone the automaton
	 * @return An automaton
	 */
	@Override
	public DFA clone(){
		try{
			return new DFA(this);
		}catch(BlocException e){
			e.printStackTrace();
			throw new RuntimeException("impossible Clone");
		}
	}

	/**
	 * The setter of the partition
	 * @param p A partition
	 */
	public void setPartition(Partition p){
		partition = p;
	}

	/**
	 * The getter of the partition
	 * @return A partition
	 */
	public Partition getPartition(){
		return partition;
	}

	/**
	 *  Return the state resulting of &#947; (q,s)
	 * @param q The state
	 * @param s The symbol
	 * @return A state if &#947; (b,s) exists, -1 otherwise
	 */
	public int getTransition(Integer q, Symbol s){
		if(this.delta.containsKey(q)) {
			if(this.delta.get(q).containsKey(s)) {
				return this.delta.get(q).get(s).get(0);
			}
		}
		return -1;
	}

	/**
	 * Return the bloc resulting of &#947; (b,s)
	 * @param b The bloc
	 * @param s The symbol
	 * @return A bloc if &#947; (b,s) exists, a bloc BlocException otherwise
	 * @throws BlocException
	 */
	public Bloc getBlocTransition(Bloc b, Symbol s)throws BlocException{
		if(this.deltaBloc.containsKey(b)) {
			if(this.deltaBloc.get(b).containsKey(s)) {
				return this.deltaBloc.get(b).get(s).get(0);
			} else {
				throw new BlocException();
			}
		} else {
			throw new BlocException();
		}
	}
	
	/**
	 * Return the bloc resulting of &#947; (b,s)
	 * @param b The bloc
	 * @param s The symbol
	 * @return A bloc if &#947; (b,s) exists, a bloc BlocException otherwise
	 * @throws BlocException
	 */
	public List<Bloc> getAllBlocTransition(Bloc b, Symbol s)throws BlocException{
		if(this.deltaBloc.containsKey(b)) {
			if(this.deltaBloc.get(b).containsKey(s)) {
				return this.deltaBloc.get(b).get(s);
			} else {
				throw new BlocException();
			}
		} else {
			throw new BlocException();
		}
	}

	/**
	 * All transitions feasible from state q
	 * @param q A state
	 * @return A list of outgoing actions
	 */
	public List<Symbol> getPossibleTransition(int q){
		List<Symbol> trans = new ArrayList<>();
		if(this.delta.containsKey(q)) {
			trans.addAll(this.delta.get(q).keySet());
		}
		return trans;
	}

	/**
	 * All transitions feasible from bloc where is the state q
	 * @param q A state
	 * @return A list of outgoing actions
	 * @throws BlocException
	 */
	public List<Symbol> getPossibleBlocTransition(int q)throws BlocException{
		Bloc bloc = partition.getBloc(q);
		List<Symbol> trans = new ArrayList<>();
		if(this.deltaBloc.containsKey(bloc)) {
			trans.addAll(this.deltaBloc.get(bloc).keySet());
		}
		
		return trans;
	}

	/**
	 * Add a new transition &#947;;(p.X, p.Y) : i
	 * @param p &#947;; input
	 * @param i &#947;; output
	 * @throws BlocException
	 * @see fsm.Pair
	 */
	public void addTransition(Pair<Integer,Symbol> p, Integer i)
			throws BlocException{
		//System.out.println("ADD Transition ("+p.getX()+" "+p.getY()+") -> "+i);
		if(!this.sigma.getSymboles().contains(p.getY())) {
			List<Symbol> tmp = new ArrayList<>();
			tmp.add(p.getY());
			tmp.addAll(this.sigma.getSymboles());
			this.sigma = new Alphabet(tmp);
		}
		if(! this.delta.containsKey(p.getX())) {
			this.delta.put(p.getX(), new HashMap<>());
		}
		if(! this.delta.get(p.getX()).containsKey(p.getY())) {
			this.delta.get(p.getX()).put(p.getY(), new ArrayList<>());
		}
		if(! this.delta.get(p.getX()).get(p.getY()).contains(i)) {
			this.delta.get(p.getX()).get(p.getY()).add(i);
		}

		Bloc b1 = partition.getBloc(p.getX());
		if(! this.deltaBloc.containsKey(b1)) {
			this.deltaBloc.put(b1, new HashMap<>());
		}
		if(! this.deltaBloc.get(b1).containsKey(p.getY())) {
			this.deltaBloc.get(b1).put(p.getY(), new ArrayList<>());
		}
		Bloc b2 = partition.getBloc(i);
		if(! this.deltaBloc.get(b1).get(p.getY()).contains(b2)) {
			this.deltaBloc.get(b1).get(p.getY()).add(b2);
		}
	}

	/**
	 * Remove transition &#947;;(p.X, p.Y) : i
	 * @param p &#947;; input
	 * @param i &#947;; output
	 * @throws BlocException
	 * @see fsm.Pair
	 */
	public void removeTransition(Pair<Integer,Symbol> p, Integer i)
			throws BlocException{
		Bloc b1 = partition.getBloc(p.getX());
		if(!this.getDeltaBloc().containsKey(b1)) {
			return;
		}
		this.deltaBloc.get(b1).remove(p.getY());
	}

	/**
	 * Remove the transition &#947;;(p.X, p.Y) : i
	 * @param p &#947;; input
	 * @param i &#947;; output
	 * @throws BlocException
	 * @see fsm.Pair
	 */
	public void delTransition(Pair<Integer,Symbol> p, Integer i)
			throws BlocException{
		if(this.delta.containsKey(p.getX())) {
			if(this.delta.get(p.getX()).containsKey(p.getY())) {
				this.delta.get(p.getX()).get(p.getY()).remove(i);
				if(this.delta.get(p.getX()).get(p.getY()).isEmpty()) {
					this.delta.get(p.getX()).remove(p.getY());
				}
			}
		}
		Bloc b1 = partition.getBloc(p.getX());
		Bloc b2 = partition.getBloc(i);
		if(this.deltaBloc.containsKey(b1)) {
			if(this.deltaBloc.get(b1).containsKey(p.getY())) {
				this.deltaBloc.get(b1).get(p.getY()).remove(b2);
				if(this.deltaBloc.get(b1).get(p.getY()).isEmpty()) {
					this.deltaBloc.get(b1).remove(p.getY());
				}
			}
		}
	}

	/**
	 * Check if the transition &#947;;(p.X, p.Y) : i exists
	 * @param p &#947;; input
	 * @param i &#947;; output
	 * @return True if &#947;;(p.X, p.Y) : i exists
	 * @see fsm.Pair
	 */
	public boolean containsTransition(Pair<Integer,Symbol> p, Integer i){
		if(this.delta.containsKey(p.getX())) {
			if(this.delta.get(p.getX()).containsKey(p.getY())) {
				if(this.delta.get(p.getX()).get(p.getY()).contains(i)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Check if there exists at least one transition delta(p.X, p.Y) : i
	 * @param p Delta input
	 * @return True if there exists at least one transition delta(p.X, p.Y) : i
	 * @see fsm.Pair
	 */
	public boolean containsTransition(Pair<Integer,Symbol> p){
		if(this.delta.containsKey(p.getX())) {
			if(this.delta.get(p.getX()).containsKey(p.getY())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if there exists at least one transition &#947;;Bloc(p.X, p.Y) : i
	 * @param p &#947;; input
	 * @return True if there exists at least one transition
	 * &#947;;Bloc(p.X, p.Y) : i
	 * @see fsm.Pair
	 */
	public boolean containsBlocTransition(Pair<Bloc,Symbol> p){
		if(this.deltaBloc.containsKey(p.getX())) {
			if(this.deltaBloc.get(p.getX()).containsKey(p.getY()))  {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * Return the set of state transitions
	 * @return &#947;;
	 */
	public Map<Integer, Map<Symbol, List<Integer>>> getDelta(){
		return this.delta;
	}

	/**
	 * Return the set of bloc transitions
	 * @return &#947;;Bloc
	 */
	public Map<Bloc, Map<Symbol, List<Bloc>>> getDeltaBloc(){
		return this.deltaBloc;
	}

	/**
	 * Set the set of states
	 * @param Q The new set of states
	 */
	public void setQ(List<Integer> Q){
		this.Q.clear();
		for(int q : Q){
			this.Q.add(q);
		}
	}

	/**
	 * Set the set of final states
	 * @param F The new set of final states
	 */
	public void setF(List<Integer> F){
		this.F.clear();
		for(int q : F){
			this.F.add(q);
		}
	}

	/**
	 * Return the set of states
	 * @return Q
	 */
	public List<Integer> getQ(){
		return this.Q;
	}

	/**
	 * Return the set of final states
	 * @return F
	 */
	public List<Integer> getF(){
		return this.F;
	}

	/**
	 * Set the initial state
	 * @param q0 The new initial state
	 */
	public void setQ0(int q0){
		this.q0 = q0;
	}

	/**
	 * Return the initial state
	 * @return q0
	 */
	public int getQ0(){
		return this.q0;
	}

	/**
	 * Set the alphabet
	 * @param a The new alphabet
	 */
	public void setSigma(Alphabet a){
		this.sigma = a;
	}

	/**
	 * Return the alphabet
	 * @return Sigma
	 */
	public Alphabet getSigma(){
		return this.sigma;
	}

	/**
	 * Write the automaton in file with graphviz format
	 * @param file The file the automaton is written
	 * @throws BlocException
	 */
	public void writeDotFile(String file)throws BlocException{
		try{
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));

			bw.write("digraph fsa{\n");
			bw.write("rankdir=LR\n");
			bw.write("graph [bgcolor=white];\n");
			bw.write("start [shape=none]\n");
			bw.write("node [shape = doublecircle];");
			for(int q : this.F){
				bw.write(" "+partition.getBloc(q).min());
			}
			bw.write(";\n");
			if(Q.size() > F.size()){
				bw.write("node [shape = circle];");
				for(int q : this.Q){
					if(!this.F.contains(q)){
						bw.write(" "+partition.getBloc(q).min());
					}
				}
				bw.write(";\n");
			}
			bw.write("start->"+partition.getBloc(this.q0).min()+"\n");
//			getDeltaBloc().forEach((bloc, p) -> {
//				System.out.println(bloc.min()+" "+p.size());
//				p.forEach((a, l) -> {
//					l.forEach(bloc2 -> {
//						System.out.println(bloc.min()+", "+a+" -> "+bloc2.min());
//					});
//				});
//			});
			this.getDeltaBloc().forEach((b1,v) -> {
				v.forEach((s,l) -> {
					if(s instanceof Task) {
						l.forEach(b2 -> {
							String str = "";
							str += b1.min()+" -> "+b2.min();
							str += "[label = \""+s.toString()+
									"\", color= \"red\", fontcolor=\"red\"]";
							str+="\n";
							try {
								bw.write(str);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								System.exit(1);
							}
						});
					} else if(s instanceof Method) {
						l.forEach(b2 -> {
							String str = "";
							str += b1.min()+" -> "+b2.min();
							str += "[label = \""+s.toString()+
									"\", color= \"blue\", fontcolor=\"blue\"]";
							str+="\n";
							try {
								bw.write(str);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								System.exit(1);
							}
						});
					} else {
						l.forEach(b2 -> {
							String str = "";
							str += b1.min()+" -> "+b2.min();
							str += "[label = \""+s.toString()+"\"]";
							str+="\n";
							try {
								bw.write(str);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								System.exit(1);
							}
						});
					}
				});
			});
			bw.write("}\n");
			bw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * Check if a word is accepted by the ie all action are accepted starting
	 * from q0
	 * @param word The word to check
	 * @return True if the word is accepted
	 * @throws BlocException
	 */
	public boolean accept(Symbol s, Bloc q)throws BlocException{
		if(containsBlocTransition(new Pair<>(q,s))){
			return true;
		}else{
			return false;
		}
	}

	/**
	 * 
	 * @param s
	 * @param q
	 * @return
	 * @throws BlocException
	 */
	public Bloc execute(Symbol s, Bloc q)throws BlocException{
		return getBlocTransition(q,s);
	}

	/**
	 * Check if a word is accepted by the automaton
	 * i.e. all action are accepted starting from q0
	 * 
	 * @param word The word to check
	 * @return True if the word is accepted
	 * @throws BlocException
	 */
	public boolean accept(Example word)throws BlocException{
		Bloc q = partition.getBloc(q0);
		for(Symbol s : word.getActionSequences()){
			if(containsBlocTransition(new Pair<>(q,s))){
				q = getBlocTransition(q,s);
			}else{
				return false;
			}
		}
		for(int s : q.getStates()){
			if(this.F.contains(s)) {
				return true;
			};
		}
		return false;
	}

	/**
	 * Check if all words in the sample I are accepted
	 * @param I The sample to check
	 * @return True if all words in the sample I are accepted
	 * @throws BlocException
	 */
	public boolean acceptAll(Sample I)throws BlocException{
		boolean b = true;
		for(Trace example : I.getExamples()){
			b &= accept((Example)example);
			if(!b) {
				return b;
			}
		}
		return b;
	}

	/**
	 * Check if at least one word in the sample I is accepted
	 * @param I The sample to check
	 * @return True if at least one word in the sample I is accepted
	 * @throws BlocException
	 */
	public boolean acceptOneOf(Sample I)throws BlocException{

		for(Trace example : I.getExamples()){
			if(accept((Example)example)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determinize the automaton ie merge all states leading to a
	 * undeterministic behaviour
	 * @return The new non-deterministic Partition
	 * @throws BlocException
	 */
	public Partition determinization() throws BlocException{
		
		for(Bloc b : this.partition.getBlocs()){
			for(Symbol s : this.getPossibleBlocTransition(b.min())){
				List<Integer> statesToMerge = new ArrayList<>();

				//Search state causing non-determinism
				if(this.deltaBloc.containsKey(b)) {
					if(this.deltaBloc.get(b).containsKey(s)) {
						for(Bloc b2 : this.deltaBloc.get(b).get(s)) {
							statesToMerge.add(b2.min());
						}
					}
				}

				//Merge states responsible of the non-determinism
				if(statesToMerge.size() > 1){
					Partition newPi = this.partition.clone();
					for(int i =1 ; i < statesToMerge.size(); i++){
						newPi.merge(statesToMerge.get(i-1),
								statesToMerge.get(i));
					}
					DFA fsa = clone();
					//fsa = FSAFactory.derive(fsa, newPi, this.partition);
					this.setPartition(newPi);
					this.delta.clear();
					this.deltaBloc.clear();
//					this.Q = new ArrayList<>();
//					this.F = new ArrayList<>();
//					this.partition = new Partition();
//					this.setQ0(fsa.getQ0());
//					for(int q : fsa.getQ()){
//						this.getQ().add(q);
//					}
//					for(int q : fsa.getF()){
//						this.getF().add(q);
//					}
//					Alphabet a = new Alphabet(fsa.getSigma().getSymboles());
//					this.setSigma(a);
//					this.partition = fsa.partition.clone();
					fsa.delta.forEach((q,v) -> {
						v.forEach((sym,l) -> {
							l.forEach(qPrime -> {
								try {
									this.addTransition(new Pair<>(q,sym), qPrime);
								} catch (BlocException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									System.exit(1);
								}
							});
						});
					});
					return this.determinization();
				}
			}
		}
		return this.partition;
	}

	/**
	 * Determinize the automaton ie remove merges leading to a
	 * undeterministic behaviour
	 * @param pi the previous partition
	 * @return The new non-deterministic Partition
	 * @throws BlocException
	 */
	public Partition fission(Partition pi)
			throws BlocException{
		for(Bloc bloc : pi.getBlocs()){
			//Find the first bloc making undeterminisme
			List<Symbol> list = getPossibleBlocTransition(bloc.min());

			//Find symbol wich makes undeterminism
			Symbol undetSym = null;
			for(Symbol s : list){
				if(this.deltaBloc.get(bloc).get(s).size() > 1){
					undetSym = s;
					break;
				}
			}
			if(undetSym == null){
				continue;
			}
			//Search greatest state in the bloc wich produces non determinism
			int j = bloc.min();
			for(int q : bloc.getStates()){
				list = this.getPossibleTransition(q);
				if(list.contains(undetSym) && q > j){
					j = q;
				}
			}
			this.partition.fission(j);
			DFA other = this.clone();
			other.getDelta().clear();
			other.getDeltaBloc().clear();
			other.setPartition(this.partition);
			this.delta.forEach((q,v) -> {
				v.forEach((sym,l) -> {
					l.forEach(qPrime -> {
						try {
							other.addTransition(new Pair<>(q,sym), qPrime);
						} catch (BlocException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.exit(1);
						}
					});
				});
			});
			this.delta = new HashMap<>();
			this.deltaBloc = new HashMap<>();
			this.Q = new ArrayList<>();
			this.F = new ArrayList<>();
			this.partition = new Partition();
			this.setQ0(other.getQ0());
			for(int q : other.getQ()){
				this.getQ().add(q);
			}
			for(int q : other.getF()){
				this.getF().add(q);
			}
			Alphabet a = new Alphabet(other.getSigma().getSymboles());
			this.setSigma(a);
			this.partition = other.partition.clone();
			other.delta.forEach((q,v) -> {
				v.forEach((sym,l) -> {
					l.forEach(qPrime -> {
						try {
							this.addTransition(new Pair<>(q,sym), qPrime);
						} catch (BlocException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.exit(1);
						}
					});
				});
			});
			return this.fission(this.partition);
		}
		return this.partition;
	}

	/**
	 * Check if the automaton is deterministic
	 * @return True the automaton is deterministic
	 * @throws BlocException
	 */
	public boolean checkDeterminism()throws BlocException{
		for(Bloc b : this.partition.getBlocs()){
			int q = b.min();
			for(Symbol s : getPossibleBlocTransition(q)){
				if(this.deltaBloc.get(b).get(s).size() > 1)
					return false;
			}
		}
		return true;
	}

	/**
	 * Extend the PTA
	 * @param x The example to add
	 * @param pi The PTA
	 * @return The new partition
	 * @throws BlocException
	 */
	public Partition extension(Example x, Partition pi)
			throws BlocException{
		int q = q0;
		int maxQ = this.partition.max();
		int newQidx = 1;
		for(Symbol s : x.getActionSequences()){
			//System.out.println(this.getNumberTransitions());
			if(getPossibleTransition(q).contains(s)){
				q = getTransition(q,s);
			}else{
				int qNext = maxQ+(newQidx++);
				F.add(qNext);
				Q.add(qNext);
				if(getPossibleBlocTransition(
						this.partition.getBloc(q).min()).contains(s)){
					Bloc b = getBlocTransition(this.partition.getBloc(q),s);
					b.addState(qNext);
					//System.out.println("Addition Bloc");
				}else{
					partition.getBlocs().add(new Bloc(qNext));
				}
				addTransition(new Pair<>(q,s), qNext);
				//System.out.println("Addition "+this.getNumberTransitions());
				q = qNext;
			}
		}
		return this.partition;
	}

	/**
	 * String representation of the automaton
	 * @return A string
	 */
	@Override
	public String toString(){
		StringBuilder res = new StringBuilder("");
		res.append("pi = "+partition+"\n");
		res.append("Q = "+Q+"\n");
		res.append("F = "+F+"\n");
		res.append("q0 = "+q0+"\n");
		res.append("delta = \n");
		this.delta.forEach((q,v) -> {
			v.forEach((sym,l) -> {
				l.forEach(qPrime -> {
					res.append("\t("+q+","+sym+") -> "+qPrime+"\n");
				});
			});
		});
		return res.toString();
	}

	/**
	 * Check if the automaton accept suffixes suf from the state q
	 * @param q The state
	 * @param suf The set of suffix
	 * @return True if the automaton accept suffixes suf from the state q
	 * @throws BlocException
	 */
	public boolean acceptSuffix(int q, List<Symbol> suf)throws BlocException{
		Queue<Symbol> s = new LinkedList<>();
		Bloc b =  partition.getBloc(q);
		for(Symbol sym : suf){
			s.offer(sym);
		}

		while(! s.isEmpty()){
			Symbol sym = s.poll();
			List<Symbol> p = getPossibleBlocTransition(b.min());
			if(! p.contains(sym)){
				return false;
			}
			b = getBlocTransition(b, sym);
		}
		boolean bool = false;
		for(int state : b.getStates()){
			bool |= this.F.contains(state);
		}
		return bool;
	}

	/**
	 * Return all bloc states ie minimal states of each blocs
	 * @return  set of states
	 */
	public List<Integer> blocStates(){
		List<Integer> res = new ArrayList<>();
		for(Bloc b : partition.getBlocs()) {
			res.add(b.min());
		}
		return res;
	}


	/**
	 * The number of transitions in the automaton
	 * @return The number of transitions
	 */
	public int getNumberTransitions() {
		int res=0;
		for(Bloc b : partition.getBlocs()) {
			try {
				res += this.getPossibleBlocTransition(b.min()).size();
			} catch (BlocException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		}
		return res;
	}

	/**
	 * Return all transitions for an action
	 * @param a The action
	 * @return All Transitions
	 */
	public List<Pair<Bloc, Bloc>> getAllTransitions(Symbol a) {
		List<Pair<Bloc, Bloc>> res = new ArrayList<>();

		return res;
	}

	/**
	 * 
	 * @param S
	 * @return
	 */
	public Sample getRejected(Sample S) {
		Sample SNew = new Sample();
		for(Trace e: S.getExamples()) {
			try {
				if(this.accept((Example)e)) {
					System.out.println(e);
				} else {
					SNew.addExample(e);
				}
			} catch (BlocException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return SNew;
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	public DFA minimumGraph(Trace t) {
		DFA minimum = new DFA();
		minimum.setSigma(this.getSigma());
		int q = 0;
		minimum.setQ0(q);
		minimum.getQ().add(q);
		minimum.getF().add(q);
		minimum.getPartition().getBlocs().add(new Bloc(q));
		for(Symbol act : t.getActionSequences()) {
			try {
				int qBis = this.getBlocTransition(this.getPartition().getBloc(q), act).min();
				minimum.getQ().add(qBis);
				minimum.getF().add(qBis);
				minimum.getPartition().getBlocs().add(new Bloc(qBis));
				minimum.addTransition(new Pair<>(q,act), qBis);
				q = qBis;
			} catch (BlocException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		};
		return minimum;
	}

	/**
	 * 
	 * @param I
	 * @return
	 */
	public DFA minimumGraph(Sample I) {
		DFA minimum = new DFA();
		minimum.setSigma(this.getSigma());
		int q = 0;
		minimum.setQ0(q);
		minimum.getQ().add(q);
		minimum.getF().add(q);
		minimum.getPartition().getBlocs().add(new Bloc(q));
		for(Trace t : I.getExamples()) {
			q = 0;
			for(Symbol act : t.getActionSequences()) {
				try {
					//	System.out.println(q+" "+act);
					int qBis = this.getBlocTransition(this.getPartition().getBloc(q), act).min();
					if(!minimum.getQ().contains(qBis)) {
						minimum.getQ().add(qBis);
						minimum.getF().add(qBis);
						minimum.getPartition().getBlocs().add(new Bloc(qBis));
					}
					minimum.addTransition(new Pair<>(q,act), qBis);
					q = qBis;
				} catch (BlocException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				}
			};
		}
		return minimum;
	}

	/**
	 * 
	 * @param q
	 * @param v
	 * @return
	 * @throws BlocException
	 */
	public Symbol getTransition(int q, int v) throws BlocException {
		Bloc b = this.partition.getBloc(q);
		if(this.deltaBloc.containsKey(b)) {
			for(Symbol act : this.deltaBloc.get(b).keySet()) {
				if(this.deltaBloc.get(b).get(act).get(0).min() == v) {
					return act;
				}
			}
		}
		throw new BlocException();
	}

	/**
	 * 
	 * @param states
	 * @return
	 */
	public Trace getTrace(List<Integer> states) {
		List<Symbol> actions = new ArrayList<>();
		for(int i = 0; i < states.size()-1; i++) {
			try {
				actions.add(this.getTransition(states.get(i), states.get(i+1)));
			} catch (BlocException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		}
		return new Example(actions);
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	public Trace minimumTrace(Trace t) {
		List<Integer> states = new ArrayList<>();
		LinkedList<Integer> statesBis = new LinkedList<>();
		LinkedList<Integer> dejaVu = new LinkedList<>();
		int q = 0;
		states.add(q);
		for(Symbol act : t.getActionSequences()) {
			try {
				int qBis = this.getBlocTransition(this.getPartition().getBloc(q), act).min();
				q = qBis;
				states.add(q);
			} catch (BlocException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		};
		for(int i =0; i < states.size(); i++) {
			if(statesBis.contains(states.get(i))) {
				do {
					statesBis.removeLast();
				} while(statesBis.getLast() != states.get(i));
			} else {
				dejaVu.add(states.get(i));
				statesBis.add(states.get(i));
			}
		}
		return this.getTrace(statesBis);
	}

	/**
	 * 
	 * @param negative
	 * @return
	 */
	public boolean rejected(Map<Integer, List<Trace>> negative) {
		for(int q : negative.keySet()) {
			for(Trace t : negative.get(q)) {
				boolean bool = true;
				for(Symbol a : t.getActionSequences()) {
					try {
						Bloc b = this.getPartition().getBloc(q);
						if(this.containsBlocTransition(new Pair<>(b,a))) {
							q = this.getBlocTransition(b, a).min();
						} else {
							bool = false;
							break;
						}
					} catch(BlocException e) {
						continue;
					}
				}
				if(bool) {
					if(this.getF().contains(q)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param t
	 */
	public List<TaskTrace> addTaskTransition(RandomTaskTrace t) {
		List<TaskTrace> res = new ArrayList<>();
		int q = this.getQ0();
		//System.out.println(t);
		for(int idx = 0; idx < t.getTasks().size(); idx++) {
			Symbol op = t.getTasks().get(idx);
			//System.out.println(q+" "+op);
			if(op instanceof Task) {
				List<Integer> states = new ArrayList<>();
				List<Symbol> trace = new ArrayList<>();

				List<Symbol> act = new ArrayList<>();
				for(int j = t.getBeginIdx().get(idx); 
						j <= t.getEndIdx().get(idx); j++) {
					act.add(t.getExample().get(j));
				}
				int precQ = q;
				states.add(q);

				for(Symbol op2 : act) {
					try {
						q = this.getBlocTransition(
								this.partition.getBloc(q), op2).min();
					} catch (BlocException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.exit(1);
					}
					states.add(q);
					trace.add(op2);
				}
				res.add(new TaskTrace( new Example(trace), op, states));
//				if(this.containsTransition(new Pair<>(q,op))) {
//					continue;
//				}
				try {
					this.addTransition(new Pair<>(precQ, op), q);

				} catch (BlocException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					q = this.getBlocTransition(this.partition.getBloc(q), op)
							.min();
				} catch (BlocException e) {
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
		return res;
	}

	/**
	 * 
	 * @param traces
	 * @return
	 */
	public List<TaskTrace> addNonObservedTaskTransition(List<TaskTrace> traces) {
		List<TaskTrace> newTraces = new ArrayList<>();
		for(TaskTrace trace : traces) {
			if(trace.getActionSequences().isEmpty()) {
				continue;
			}
			for(Bloc b : this.getPartition().getBlocs()) {
					if(!this.containsBlocTransition(new Pair<>(b,trace.getTask()))) {
						int q = this.getState(
								new Example(trace.getActionSequences()),
								b.min());
						if(q > -1) {
							newTraces.add(new TaskTrace(
									trace.getTrace(),
									trace.getTask(),
									this.getStates(
											new Example(trace.getActionSequences()), 
											b.min())));
							
						}
					}
			}
		}
		return newTraces;
	}
	
	/**
	 * 
	 * @param t
	 * @param q
	 * @return
	 */
	public int getState(Trace t, int q) {
		for(Symbol s : t.getActionSequences()) {
			try {
				Bloc b = this.partition.getBloc(q);
				if(this.containsBlocTransition(new Pair<>(b,s))) {
					q = this.getBlocTransition(b, s).min();
				} else  {
					return -1;
				}
			} catch (BlocException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		}
		return q;
	}
	
	/**
	 * 
	 * @param t
	 * @param q
	 * @return
	 */
	public List<Integer> getStates(Trace t, int q) {
		List<Integer> res = new ArrayList<>();
		res.add(q);
		for(Symbol s : t.getActionSequences()) {
			try {
				Bloc b = this.partition.getBloc(q);
				if(this.containsBlocTransition(new Pair<>(b,s))) {
					q = this.getBlocTransition(b, s).min();
					res.add(q);
				}
			} catch (BlocException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		}
		return res;
	}
	/**
	 * 
	 * @param t
	 * @param statesMethods
	 * @param tableMethods
	 */
	public void addMethodTransition(
			TaskTrace t,
			Method m) {
		try {
			this.addTransition(new Pair<>(t.firstState(),m), t.lastState());
			this.removeTransition(
					new Pair<>(t.firstState(),t.getTask()), 
					t.lastState());
		} catch (BlocException e) {
			e.printStackTrace();
			System.exit(1);
		}

		return;
	}
	
	/**
	 * 
	 * @param t
	 * @return
	 */
	public Trace getMethodTrace(RandomTaskTrace t) {
		//System.err.println(t);
		List<Symbol> res = new ArrayList<>();
		int q = this.q0;
		for(int idx = 0; idx < t.getTasks().size(); idx++) {
			Symbol a = t.getTasks().get(idx);
			//System.err.println(q+" "+a);
			if(a instanceof Task) {
				try {
					List<Symbol> act = new ArrayList<>();
					for(int j = t.getBeginIdx().get(idx); 
							j <= t.getEndIdx().get(idx); j++) {
						act.add(t.getExample().get(j));
					}
					int precQ = q;
					//System.err.println(act);
					for(Symbol op2 : act) {
						try {
							precQ = this.getBlocTransition(
									this.partition.getBloc(precQ), op2).min();
							//System.err.println("\t"+op2+"->"+precQ);
						} catch (BlocException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							System.exit(1);
						}
					}
					boolean b = false;
					for(Symbol m : this.getPossibleBlocTransition(q)) {
						//System.err.println("TEST Transition\n"+m);
						if(m instanceof Method) {
							if(((Method) m).getToDecompose().equals(a)) {
								for(Bloc bb : this.getAllBlocTransition(this.partition.getBloc(q), m)) {
									int qBis = bb.min();
									//System.err.println(qBis+" "+precQ);
									if(qBis == precQ) {
										q = qBis;
										res.add(m);
										//System.err.println("ADDING Transition\n"+m);
										b=true;
										break;
									}
								}
							}
							if(b) {
								break;
							}
						}
					}
					if(!b) {
						throw new BlocException();
					}
				} catch (BlocException e) {
					e.printStackTrace();
					System.exit(1);
				}
			} else {
				try {
					q = this.getBlocTransition(this.partition.getBloc(q), a).min();
					res.add(a);
				} catch (BlocException e) {
//					return null;
					e.printStackTrace();
					System.exit(1);
				}
			}
		};
		return new Example(res);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Symbol> getTasks() {
		List<Symbol> res = new ArrayList<>();
		this.sigma.getSymboles().forEach(a -> {
			if(a instanceof Task) {
				res.add(a);
			}
		});
		return res;
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Symbol> getMethods() {
		List<Symbol> res = new ArrayList<>();
		this.sigma.getSymboles().forEach(a -> {
			if(a instanceof Method) {
				res.add(a);
			}
		});
		return res;
	}
	
	public List<Integer> getBlocs(Trace t) throws BlocException {
		return this.getBlocSuffix(0, q0, t);
	}
	
	private List<Integer> getBlocSuffix(int idx, int q, Trace t)
			throws BlocException {
		List<Integer> res = new ArrayList<>();
		if(idx == t.size()) {
			return res;
		}
		List<Bloc> next = this.getAllBlocTransition(
				this.getPartition().getBloc(q),
				t.getActionSequences().get(idx));
		if(next.size() == 1) {
			int qNext = next.get(0).min();
			res.add(qNext);
			List<Integer> suffix = this.getBlocSuffix(idx+1, qNext, t);
			res.addAll(suffix);
			return res;
		} else {
//			boolean atLeastOne = false;
			for(Bloc b : next) {
				try {
					int qNext = b.min();
					List<Integer> suffix = this.getBlocSuffix(idx+1, qNext, t);
//					atLeastOne=true;
					res.add(qNext);
					res.addAll(suffix);
					return res;
				} catch(BlocException e) {
					continue;
				}
			}
//			if(!atLeastOne) {
//				throw new BlocException();
//			}
		}
		throw new BlocException();
	}
	
	
	public Sample getSp() {
		List<Trace> l = new ArrayList<>();
		l.add(new Example(new ArrayList<>()));
		Queue<Integer> previous = new LinkedList<>();
		previous.add(0);
		Map<Integer, Trace> sp = new HashMap<>();
		sp.put(0, new Example(new ArrayList<>()));
		while(! previous.isEmpty()) {
			try {
				Bloc b = this.partition.getBloc(previous.poll());
				Trace t = sp.get(b.min());
				if(!this.getDeltaBloc().containsKey(b)) {
					continue;
				}
				this.getDeltaBloc().get(b).forEach((a,ll) -> {
					ll.forEach(b2 ->  {
						if(! sp.containsKey(b2.min())) {
							List<Symbol> tmp = new ArrayList<>();
							tmp.addAll(t.getActionSequences());
							tmp.add(a);
							Trace ex = new Example(tmp);
							sp.put(b2.min(), ex);
							l.add(ex);
							previous.add(b2.min());
						}
					});
				});
			} catch (BlocException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		}
		return new Sample(l);
	}
	
	public Sample getKernel(Sample Sp) {
		List<Trace> l = new ArrayList<>();
		l.add(new Example(new ArrayList<>()));
		Sp.getExamples().forEach(t -> {
			try {
				Bloc b = this.partition.getBloc(this.getState(t, 0));
				if(this.getDeltaBloc().containsKey(b)) {
					this.getDeltaBloc().get(b).forEach((a,ll) -> {
						ll.forEach(b2 ->  {
							List<Symbol> tmp = new ArrayList<>();
							tmp.addAll(t.getActionSequences());
							tmp.add(a);
							Trace ex = new Example(tmp);
							l.add(ex);
						});
					});
				}
			} catch (BlocException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(1);
			}
		});
		return new Sample(l);
	}
	
	public Pair<Sample, Sample> getCharacteristicSample(Sample Sp, Sample kernel) {
		List<Trace> pos = new ArrayList<>(), neg = new ArrayList<>();
		pos.addAll(kernel.getExamples());
		for(Trace x : Sp.getExamples()) {
			for(Trace y : kernel.getExamples()) {
				try {
					Bloc b1 = this.partition.getBloc(this.getState(x, q0));
					Bloc b2 = this.partition.getBloc(this.getState(y, q0));
					if(b1.min() == b2.min()) {
						continue;
					}
					boolean b = false;
					if(this.getDeltaBloc().containsKey(b1)) {
						for(Symbol a : this.getDeltaBloc().get(b1).keySet()) {
							if(this.getDeltaBloc().containsKey(b2)) {
								if(! this.getDeltaBloc().get(b2).containsKey(a)) {
									List<Symbol> tmpPos = new ArrayList<>(),tmpNeg = new ArrayList<>();
									tmpPos.addAll(x.getActionSequences());
									tmpNeg.addAll(y.getActionSequences());
									tmpPos.add(a);
									tmpNeg.add(a);
									Trace tPos = new Example(tmpPos);
									Trace tNeg = new Example(tmpNeg); 
									if(!pos.contains(tPos)) {
										pos.add(tPos);
									}
									if(!neg.contains(tNeg)) {
										neg.add(tNeg);
									}
									b=true;
									break;
								}
							}
						}
					}
					if(b) {
						continue;
					}
					if(this.getDeltaBloc().containsKey(b2)) {
						for(Symbol a : this.getDeltaBloc().get(b2).keySet()) {
							if(this.getDeltaBloc().containsKey(b1)) {
								if(! this.getDeltaBloc().get(b2).containsKey(a)) {
									List<Symbol> tmpPos = new ArrayList<>(),tmpNeg = new ArrayList<>();
									tmpPos.addAll(y.getActionSequences());
									tmpNeg.addAll(x.getActionSequences());
									tmpPos.add(a);
									tmpNeg.add(a);
									Trace tPos = new Example(tmpPos);
									Trace tNeg = new Example(tmpNeg); 
									if(!pos.contains(tPos)) {
										pos.add(tPos);
									}
									if(!neg.contains(tNeg)) {
										neg.add(tNeg);
									}
									b=true;
									break;
								}
							}
						}
					}
				} catch (BlocException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(1);
				}
			}
		}
		return new Pair<>(new Sample(pos), new Sample(neg));
	}
}
