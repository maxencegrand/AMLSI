/**
 * 
 */
package simulator.temporal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fsm.Example;
import fsm.Symbol;

/**
 * @author Maxence Grand
 *
 */
public class TemporalExample {
	/**
	 * 
	 */
	private Map<Float, Symbol> temporalSequence;

	/**
	 * Constructs 
	 * @param temporalSequence
	 */
	public TemporalExample(Map<Float, Symbol> temporalSequence) {
		this.temporalSequence = temporalSequence;
	}

	/**
	 * Getter of temporalSequence
	 * @return the temporalSequence
	 */
	public Map<Float, Symbol> getTemporalSequence() {
		return temporalSequence;
	}

	/**
	 * Setter temporalSequence
	 * @param temporalSequence the temporalSequence to set
	 */
	public void setTemporalSequence(Map<Float, Symbol> temporalSequence) {
		this.temporalSequence = temporalSequence;
	}

	/**
	 * @param key
	 * @return
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Symbol get(Object key) {
		return temporalSequence.get(key);
	}

	/**
	 * @return
	 * @see java.util.Map#keySet()
	 */
	public Set<Float> keySet() {
		return temporalSequence.keySet();
	}
	
	/**
	 * 
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Temporal Example\n");
		List<Float> tmp_ = new ArrayList<>(this.keySet());
		Collections.sort(tmp_);
		tmp_.forEach(t -> {
			builder.append("["+t+"] "+this.get(t)+"\n");
		});
		return builder.toString();
	}

	/**
	 * 
	 * @return
	 */
	public List<TemporalExample> generateAllNegativeExamples(
			Map<Float, List<Symbol>> neg) {
		List<TemporalExample> res = new ArrayList<>();
		Map<Float, Symbol> pos = new HashMap<>();
		List<Float> tPos = new ArrayList<>(this.keySet());
		List<Float> tNeg = new ArrayList<>(neg.keySet());
		Collections.sort(tPos);
		Collections.sort(tNeg);
		int j = 0;
		for(int i = 0; i<tPos.size(); i++) {
			while(j < tNeg.size() && tPos.get(i) >= tNeg.get(j)) {
				for(Symbol s : neg.get(tNeg.get(j))) {
					Map<Float, Symbol> tmp = new HashMap<>();
					List<Float> tmp_ = new ArrayList<>(pos.keySet());
					Collections.sort(tmp_);
					final float stp = tNeg.get(j);
					tmp_.stream().filter(k -> k < stp).forEach(k-> {
						tmp.put(k,pos.get(k));
					});
					tmp.put(tNeg.get(j), s);
					res.add(new TemporalExample(tmp));
				}
				j++;
			}
			pos.put(tPos.get(i), this.get(tPos.get(i)));
		}
		while(j < tNeg.size()) {
			for(Symbol s : neg.get(tNeg.get(j))) {
				Map<Float, Symbol> tmp = new HashMap<>();
				List<Float> tmp_ = new ArrayList<>(pos.keySet());
				Collections.sort(tmp_);
				final float stp = tNeg.get(j);
				tmp_.stream().filter(k -> k < stp).forEach(k-> {
					tmp.put(k,pos.get(k));
				});
				tmp.put(tNeg.get(j), s);
				res.add(new TemporalExample(tmp));
			}
			j++;
		}
		return res;
	}
	
	/**
	 * 
	 * @param durations
	 * @return
	 */
	public Example convertIntoSequential3Op(TemporalOracle oracle) {
		Example res = new Example();
		Map<Float, List<Symbol>> end = new HashMap<>();
		List<Symbol> current = new ArrayList<>();
		List<Float> tStart = new ArrayList<>(this.keySet());
		Collections.sort(tStart);
		int j = 0;
		for(int i = 0; i<tStart.size(); i++) {
			j=0;
			List<Float> tEnd = new ArrayList<>(end.keySet());
			Collections.sort(tEnd);
			while(j < tEnd.size() && tStart.get(i) >= tEnd.get(j)) {
				final float tmp = tEnd.get(j);
				end.get(tEnd.get(j)).forEach(a -> {
					Symbol aEnd = a.clone();
					aEnd.setName(aEnd.getName()+"-end");
					res.add(aEnd);
					end.remove(tmp);
					current.remove(a);
					for(Symbol aC : current) {
						Symbol aInv = aC.clone();
						aInv.setName(aInv.getName()+"-inv");
						res.add(aInv);
					}
				});
				j++;
			}
			Symbol aStart = this.get(tStart.get(i)).clone();
			aStart.setName(aStart.getName()+"-start");
			/*Symbol inv = this.get(tStart.get(i)).clone();
			aStart.setName(inv.getName()+"-inv");
			res.add(inv);*/
			res.add(aStart);
			current.add(this.get(tStart.get(i)));
			if(!end.containsKey(oracle.getDuration(this.get(tStart.get(i)))+tStart.get(i))) {
				end.put(oracle.getDuration(this.get(tStart.get(i)))+tStart.get(i), new ArrayList<>());
			}
			end.get(oracle.getDuration(this.get(tStart.get(i)))+tStart.get(i)).add(this.get(tStart.get(i)));
			for(Symbol a : current) {
				Symbol aInv = a.clone();
				aInv.setName(aInv.getName()+"-inv");
				res.add(aInv);
			}
		}
		j=0;
		List<Float> tEnd = new ArrayList<>(end.keySet());
		Collections.sort(tEnd);
		while(j < tEnd.size()) {
			final float tmp = tEnd.get(j);
			end.get(tEnd.get(j)).forEach(a -> {
				Symbol aEnd = a.clone();
				aEnd.setName(aEnd.getName()+"-end");
				res.add(aEnd);
				end.remove(tmp);
				current.remove(a);
				for(Symbol aC : current) {
					Symbol aInv = aC.clone();
					aInv.setName(aInv.getName()+"-inv");
					res.add(aInv);
				}
			});
			
			j++;
		}
		return res;
	}
	
	/**
	 * 
	 * @param durations
	 * @return
	 */
	public Example convertIntoSequential2Op(TemporalOracle oracle) {
		Example res = new Example();
		Map<Float, List<Symbol>> end = new HashMap<>();
		List<Float> tStart = new ArrayList<>(this.keySet());
		Collections.sort(tStart);
		int j = 0;
		for(int i = 0; i<tStart.size(); i++) {
			j=0;
			List<Float> tEnd = new ArrayList<>(end.keySet());
			Collections.sort(tEnd);
			while(j < tEnd.size() && tStart.get(i) >= tEnd.get(j)) {
				//System.out.println(tEnd.get(j));
				final float tmp = tEnd.get(j);
				end.get(tEnd.get(j)).forEach(a -> {
					Symbol aEnd = a.clone();
					aEnd.setName(aEnd.getName()+"-end");
					res.add(aEnd);
					end.remove(tmp);
				});
				j++;
			}
			Symbol aStart = this.get(tStart.get(i)).clone();
			aStart.setName(aStart.getName()+"-start");
			res.add(aStart);
			if(!end.containsKey(oracle.getDuration(this.get(tStart.get(i)))+tStart.get(i))) {
				end.put(oracle.getDuration(this.get(tStart.get(i)))+tStart.get(i), new ArrayList<>());
			}
			end.get(oracle.getDuration(this.get(tStart.get(i)))+tStart.get(i)).add(this.get(tStart.get(i)));
			//System.err.println(end);
		}
		//if(positive) {
		j=0;
		List<Float> tEnd = new ArrayList<>(end.keySet());
		Collections.sort(tEnd);
		while(j < tEnd.size()) {
			final float tmp = tEnd.get(j);
			end.get(tEnd.get(j)).forEach(a -> {
				Symbol aEnd = a.clone();
				aEnd.setName(aEnd.getName()+"-end");
				res.add(aEnd);
				end.remove(tmp);
			});
			j++;
		}
		//}
		return res;
	}
	
	public Example convertIntoSequential2OpNeg(TemporalOracle oracle) {
		Example res = new Example();
		Map<Float, List<Symbol>> end = new HashMap<>();
		List<Float> tStart = new ArrayList<>(this.keySet());
		Collections.sort(tStart);
		int j = 0;
		for(int i = 0; i<tStart.size(); i++) {
			j=0;
			List<Float> tEnd = new ArrayList<>(end.keySet());
			Collections.sort(tEnd);
			while(j < tEnd.size() && tStart.get(i) >= tEnd.get(j)) {
				//System.out.println(tEnd.get(j));
				final float tmp = tEnd.get(j);
				end.get(tEnd.get(j)).forEach(a -> {
					Symbol aEnd = a.clone();
					aEnd.setName(aEnd.getName()+"-end");
					res.add(aEnd);
					end.remove(tmp);
				});
				j++;
			}
			Symbol aStart = this.get(tStart.get(i)).clone();
			aStart.setName(aStart.getName()+"-start");
			res.add(aStart);
			if(!end.containsKey(oracle.getDuration(this.get(tStart.get(i)))+tStart.get(i))) {
				end.put(oracle.getDuration(this.get(tStart.get(i)))+tStart.get(i), new ArrayList<>());
			}
			end.get(oracle.getDuration(this.get(tStart.get(i)))+tStart.get(i)).add(this.get(tStart.get(i)));
			//System.err.println(end);
		}
		
		//}
		return res;
	}
	public Example convertIntoSequential2OpBis(TemporalOracle oracle) {
		Example res = new Example();
		Map<Float, List<Symbol>> end = new HashMap<>();
		List<Float> tStart = new ArrayList<>(this.keySet());
		Collections.sort(tStart);
		int j = 0;
		for(int i = 0; i<tStart.size(); i++) {
			j=0;
			List<Float> tEnd = new ArrayList<>(end.keySet());
			Collections.sort(tEnd);
			while(j < tEnd.size() && tStart.get(i) >= tEnd.get(j)) {
				//System.out.println(tEnd.get(j));
				final float tmp = tEnd.get(j);
				end.get(tEnd.get(j)).forEach(a -> {
					Symbol aEnd = a.clone();
					aEnd.setName(aEnd.getName()+"-end");
					res.add(aEnd);
					end.remove(tmp);
				});
				j++;
			}
			Symbol aStart = this.get(tStart.get(i)).clone();
			aStart.setName(aStart.getName()+"-start");
			res.add(aStart);
			if(!end.containsKey(oracle.getDuration(this.get(tStart.get(i)))+tStart.get(i))) {
				end.put(oracle.getDuration(this.get(tStart.get(i)))+tStart.get(i), new ArrayList<>());
			}
			end.get(oracle.getDuration(this.get(tStart.get(i)))+tStart.get(i)).add(this.get(tStart.get(i)));
		}
		//if(positive) {
		j=0;
		List<Float> tEnd = new ArrayList<>(end.keySet());
		Collections.sort(tEnd);
		System.out.println("->"+tEnd);
		while(j < tEnd.size()) {
			final float tmp = tEnd.get(j);
			end.get(tEnd.get(j)).forEach(a -> {
				Symbol aEnd = a.clone();
				aEnd.setName(aEnd.getName()+"-end");
				res.add(aEnd);
				end.remove(tmp);
			});
			j++;
		}
		//}
		return res;
	}
	
	/**
	 * 
	 * @return
	 */
	public int size() {
		return this.temporalSequence.keySet().size();
	}
}
