/**
 * 
 */
package fsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fsm.Example;
import learning.Observation;
import learning.ObservedExample;

/**
 * @author Maxence Grand
 *
 */
public class RandomTaskTrace extends Trace{
	/**
	 * The action sequence
	 */
	private Example example;
	/**
	 * The tasks sequence
	 */
	private Example tasks;
	/**
	 * index of tasks end in the action sequence
	 */
	private List<Integer> endIdx;
	/**
	 * index of tasks end in the action sequence
	 */
	private List<Integer> beginIdx;
	
	
	/**
	 * Constructs 
	 * @param example
	 * @param counter
	 * @param tasks
	 * @param endIdx
	 */
	public RandomTaskTrace(
			Example example, 
			Example tasks, 
			List<Integer> endIdx, 
			List<Integer> beginIdx) {
		this.example = example;
		this.tasks = tasks;
		this.endIdx = endIdx;
		this.beginIdx = beginIdx;
		if(this.getExample().size() <= this.endIdx.get(this.endIdx.size()-1)) {
			throw new IllegalArgumentException();
		}
		if(!(this.tasks.size() == this.beginIdx.size() && this.tasks.size() == this.endIdx.size())) {
			throw new IllegalArgumentException();
		}
	}

	
	/**
	 * Getter of example
	 * @return the example
	 */
	public Example getExample() {
		return example;
	}


	/**
	 * Setter example
	 * @param example the example to set
	 */
	public void setExample(Example example) {
		this.example = example;
	}


	/**
	 * Getter of tasks
	 * @return the tasks
	 */
	public Example getTasks() {
		return tasks;
	}


	/**
	 * Setter tasks
	 * @param tasks the tasks to set
	 */
	public void setTasks(Example tasks) {
		this.tasks = tasks;
	}


	/**
	 * Getter of endIdx
	 * @return the endIdx
	 */
	public List<Integer> getEndIdx() {
		return endIdx;
	}


	/**
	 * Setter endIdx
	 * @param endIdx the endIdx to set
	 */
	public void setEndIdx(List<Integer> endIdx) {
		this.endIdx = endIdx;
	}

	
	/**
	 * Getter of beginIdx
	 * @return the beginIdx
	 */
	public List<Integer> getBeginIdx() {
		return beginIdx;
	}


	/**
	 * Setter beginIdx
	 * @param beginIdx the beginIdx to set
	 */
	public void setBeginIdx(List<Integer> beginIdx) {
		this.beginIdx = beginIdx;
	}


	/**
	 * 
	 */
	@Override
	public List<Symbol> getActionSequences() {
		// TODO Auto-generated method stub
		return this.example.getActionSequences();
	}
	
	/**
	 * 
	 */
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.example.size();
	}
	
	/**
	 * 
	 */
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append("\n+"+"Task:\n");
		str.append(this.example+"\n");
		str.append(this.tasks+"\n");
		str.append(this.endIdx+"\n");
		str.append(this.beginIdx+"\n");
		return str.toString();
	}
	
	/**
	 * 
	 * @param o
	 * @return
	 */
	public ObservedExample getObservedTask(List<Observation> o) {
		List<Observation> obs = new ArrayList<>();
		obs.add(o.get(0));
		int currentObs = 0;
		for(int idx = 0; idx < this.getTasks().size(); idx++) {
			Symbol op = this.getTasks().get(idx);
			if(op instanceof Method) {
				if(this.getEndIdx().get(idx) == 0) {
					obs.add(o.get(0));
				} else {
					obs.add(o.get(this.getEndIdx().get(idx)+1));
					currentObs=this.getEndIdx().get(idx)+1;
				}
			} else {
				currentObs++;
				obs.add(o.get(currentObs));
			}
		}
		return new ObservedExample(this.getTasks().getActionSequences(), obs);
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<Symbol, Map<Integer, List<Symbol>>> mapedAction() {
		Map<Symbol, Map<Integer, List<Symbol>>> res = new LinkedHashMap<>();
		for(int i = 0; i < this.tasks.size(); i++) {
			Symbol t = this.tasks.get(i);
			if(!res.containsKey(t)) {
				res.put(t, new HashMap<>());
			}
			List<Symbol> l = new ArrayList<>();
			for(int j = this.getBeginIdx().get(i); 
					j <= this.getEndIdx().get(i); j++) {
				l.add(this.example.get(j));
			}
			res.get(t).put(i, l);
		}
		return res;
	}
}
