/**
 * 
 */
package fsm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maxence Grand
 *
 */
public class TaskTrace extends Trace{
	/**
	 * The task sequence
	 */
	private Example trace;
	/**
	 * the task
	 */
	private Symbol task;
	/**
	 * All states visited
	 */
	private List<Integer> states;
	
	/**
	 * 
	 * Constructs
	 */
	public TaskTrace() {
		this.trace = new Example();
		this.task = new Task();
		this.states = new ArrayList<>();
	}
	
	/**
	 * Constructs 
	 * @param trace
	 * @param task
	 */
	public TaskTrace(Example trace, Symbol task, List<Integer> states) {
		this();
		this.trace = trace;
		this.task = task;
		for(int s : states) {
			this.states.add(s);
		}
	}
	
	/**
	 * Getter of trace
	 * @return the trace
	 */
	public Example getTrace() {
		return trace;
	}

	/**
	 * Setter trace
	 * @param trace the trace to set
	 */
	public void setTrace(Example trace) {
		this.trace = trace;
	}

	/**
	 * Getter of task
	 * @return the task
	 */
	public Symbol getTask() {
		return task;
	}

	
	/**
	 * Getter of states
	 * @return the states
	 */
	public List<Integer> getStates() {
		return states;
	}
	/**
	 * 
	 */
	@Override
	public List<Symbol> getActionSequences() {
		// TODO Auto-generated method stub
		return this.trace.getActionSequences();
	}
	
	/**
	 * 
	 */
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return this.trace.size();
	}
	
	/**
	 * 
	 * @return
	 */
	public TaskTrace generalize() {
		Example e = new Example();
		Symbol t = this.task.generalize();
		this.trace.getActionSequences().forEach(ex -> 
		e.add(ex.generalize(this.task.getAbstractParameters())));
		return new TaskTrace(e,t, this.states);
	}
	
	/**
	 * 
	 */
	public String toString() {
		StringBuilder str = new StringBuilder("\n");
		str.append(task)
				.append("\n")
				.append(trace)
				.append("\n")
				.append(states);
		return str.toString();
	}
	
	/**
	 * 
	 * @return
	 */
	public int lastState() {
		return this.states.get(this.states.size()-1);
	}
	
	/**
	 * 
	 * @return
	 */
	public int firstState() {
		return this.states.get(0);
	}
	
	public boolean equals(Object other) {
		if(! (other instanceof TaskTrace)) {
			return false;
		}
		TaskTrace t = (TaskTrace)other;
		if(!t.getTask().equals(this.getTask())) {
			return false;
		}
		if(! (t.firstState() == this.firstState())) {
			return false;
		}
		if(! (t.lastState() == this.lastState())) {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @param others
	 * @return
	 */
	public TaskTrace recompose(List<TaskTrace> others) {
		List<Integer> states_ = this.getStates();
		List<Symbol> trace_ = this.getTrace().getActionSequences();
//		System.out.println("**** "+task+" "+trace_+" "+states_);
		for(TaskTrace t : others) {
			if(t.getStates().size() <= 1) {
				continue;
			}
			if(t.getTask().equals(this.task)) {
				continue;
			}
			if(!states_.contains(t.firstState())) {
				continue;
			}
			if(!states_.contains(t.lastState())) {
				continue;
			}
			if(this.firstState() == t.firstState() &&
					this.lastState() == t.lastState()) {
				continue;
			}
			int idxFirst = states_.indexOf(t.firstState());
			if(idxFirst+t.getStates().size() > this.getStates().size()) {
				continue;
			}
			boolean b = true;
			for(int i = 0; i < t.getStates().size(); i++) {
				b &= t.getStates().get(i) == this.getStates().get(i+idxFirst);
			}
			if(!b) {
				continue;
			}
			
			int idxLast = states_.indexOf(t.lastState());
			
			List<Integer> tmpS = new ArrayList<>();
			List<Symbol> tmpT = new ArrayList<>();
			tmpS.add(this.firstState());
			for(int i = 0; i < trace_.size(); i++) {
				if(i < idxFirst || i >= idxLast) {
					tmpS.add(states_.get(i+1));
					tmpT.add(trace_.get(i));
				} else if (i==idxFirst) {
					tmpS.add(t.lastState());
					tmpT.add(t.getTask());
				} else {
					continue;
				}
			}
			trace_ = tmpT;
			states_ = tmpS;
//			System.out.println(t.getTask()+""+t.getStates()+"->"+trace_+" "+states_);
		};
		
		return new TaskTrace(new Example(trace_), this.getTask(), states_);
	}
	
	/**
	 * 
	 * @return
	 */
	public TaskTrace recompose2(List<TaskTrace> others) {
		List<TaskTrace> res = new ArrayList<>();
		List<Task> dejaVu = new ArrayList<>();
		List<TaskTrace> others_ = new ArrayList<>();
		for(TaskTrace t : others) {
			if(t.getStates().size() <= 1) {
				continue;
			}
//			if(t.getTask().equals(this.task)) {
//				continue;
//			}
			if(!states.contains(t.firstState())) {
				continue;
			}
			if(!states.contains(t.lastState())) {
				continue;
			}
			if(this.firstState() == t.firstState() &&
					this.lastState() == t.lastState()) {
				continue;
			}
			int idxFirst = states.indexOf(t.firstState());
			if(idxFirst+t.getStates().size() > this.getStates().size()) {
				continue;
			}
			boolean b = true;
			for(int i = 0; i < t.getStates().size(); i++) {
				b &= t.getStates().get(i) == this.getStates().get(i+idxFirst);
			}
			if(!b) {
				continue;
			}
			others_.add(t);
		}
		
		List<List<TaskTrace>> idxOthers = new ArrayList<>();
		for(int i = 0; i < this.getStates().size(); i++) {
			int s = this.getStates().get(i);
			idxOthers.add(i,new ArrayList<>());
			for(TaskTrace t : others_) {
				if(t.firstState() == s) {
					idxOthers.get(i).add(t);
				}
			}
		}
		
		for(int i = 0; i < idxOthers.size(); i++) {
//			System.out.print("Idx "+i+" :");
//			idxOthers.get(i).forEach(t -> System.out.print(" ("+t.getTask()+" "+t.getStates().size()+")"));
//			System.out.println();
		}
		List<Symbol> subtasks = new ArrayList<>();
		List<Integer> states = new ArrayList<>();
		states.add(this.firstState());
		int i = 0; 
		while(i < this.getStates().size()-1) {
			TaskTrace max = null;
			int idxMax = 0;
			for(TaskTrace t : idxOthers.get(i)) {
				int tmp = this.getStates().indexOf(t.lastState());
				if(tmp > idxMax) {
					idxMax = tmp;
					max = t;
				} else if (tmp == idxMax 
						&& max != null 
						&& t.getTask().getName().equals(this.getTask().getName())) {
					max = t;
				}
			}
			if(max == null) {
				subtasks.add(this.getActionSequences().get(i));
				states.add(this.getStates().get(i+1));
				i += 1;
			} else {
				subtasks.add(max.getTask());
				states.add(max.lastState());
				i += max.getActionSequences().size();	
			}
		}
		
		return new TaskTrace(new Example(subtasks), this.getTask(), states);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<TaskTrace> getAllRecomposition(List<TaskTrace> others) {
		List<TaskTrace> res = new ArrayList<>();
		List<Task> dejaVu = new ArrayList<>();
		List<TaskTrace> others_ = new ArrayList<>();
		for(TaskTrace t : others) {
			if(!states.contains(t.firstState())) {
				continue;
			}
			if(!states.contains(t.lastState())) {
				continue;
			}
			if(this.firstState() == t.firstState() &&
					this.lastState() == t.lastState()) {
				continue;
			}
			int idxFirst = states.indexOf(t.firstState());
			if(idxFirst+t.getStates().size() > this.getStates().size()) {
				continue;
			}
			boolean b = true;
			for(int i = 0; i < t.getStates().size(); i++) {
				b &= t.getStates().get(i) == this.getStates().get(i+idxFirst);
			}
			if(!b) {
				continue;
			}
			others_.add(t);
		}
		
		List<List<TaskTrace>> idxOthers = new ArrayList<>();
		for(int i = 0; i < this.getStates().size(); i++) {
			int s = this.getStates().get(i);
			idxOthers.add(i,new ArrayList<>());
			for(TaskTrace t : others_) {
				if(t.firstState() == s) {
					idxOthers.get(i).add(t);
				}
			}
		}
		
		//res.add(this);
		dejaVu.add((Task) this.getTask());
		res.addAll(this.getAllRecomposition(
				idxOthers, 
				0, 
				new ArrayList<>(), 
				new ArrayList<>(), 
				dejaVu));
		return res;
	}
	
	public List<TaskTrace> getAllRecomposition(
			List<TaskTrace> others,
			List<Task> available) {
		List<TaskTrace> res = new ArrayList<>();
		if(this.getStates().size() <= 1) {
			res.add(this);
			return res;
		}
		List<Task> dejaVu = new ArrayList<>();
		List<TaskTrace> others_ = new ArrayList<>();
		for(TaskTrace t : others) {
//			if(t.getStates().size() <= 1) {
//				continue;
//			}
			if(!(t.getTask().generalize().equals(this.task.generalize()) ||
					available.contains(t.getTask().generalize()))) {
				continue;
			}
			if(!states.contains(t.firstState())) {
				continue;
			}
			if(!states.contains(t.lastState())) {
				continue;
			}
			if(this.firstState() == t.firstState() &&
					this.lastState() == t.lastState()) {
				continue;
			}
			int idxFirst = states.indexOf(t.firstState());
			if(idxFirst+t.getStates().size() > this.getStates().size()) {
				continue;
			}
			boolean b = true;
			for(int i = 0; i < t.getStates().size(); i++) {
				b &= t.getStates().get(i) == this.getStates().get(i+idxFirst);
			}
			if(!b) {
				continue;
			}
			others_.add(t);
		}
		
		List<List<TaskTrace>> idxOthers = new ArrayList<>();
		for(int i = 0; i < this.getStates().size(); i++) {
			int s = this.getStates().get(i);
			idxOthers.add(i,new ArrayList<>());
			for(TaskTrace t : others_) {
				if(t.firstState() == s) {
					idxOthers.get(i).add(t);
				}
			}
		}
		
		//res.add(this);
		dejaVu.add((Task) this.getTask());
		res.addAll(this.getAllRecomposition(
				idxOthers, 
				0, 
				new ArrayList<>(), 
				new ArrayList<>(), 
				dejaVu));
//		System.out.println("coucou");
		return res;
	}
	
	/**
	 * 
	 * @param others
	 * @param idx
	 * @param states_
	 * @param trace_
	 * @return
	 */
	public List<TaskTrace> getAllRecomposition(
			List<List<TaskTrace>> others, 
			int idx,
			List<Integer> states_,
			List<Symbol> trace_,
			List<Task> dejaVu) {
//		if(states_.size() >= 3) {
//			if(states_.get(states_.size()-1) == states_.get(states_.size()-3)) {
//				System.out.println(this.getStates()+" "+states_);
//				return new ArrayList<>();
//			}
//		}
		List<TaskTrace> res = new ArrayList<>();
		if(idx >= this.getStates().size()-1) {
			//System.out.println("Call i"+" idx= "+idx+" "+this.getStates().size()+" "+trace_);
			List<Integer> tmpS = new ArrayList<>();
			tmpS.addAll(states_);
			tmpS.add(this.lastState());
			res.add(new TaskTrace(new Example(trace_), this.getTask(), tmpS));
//			System.out.println("*"+trace_.size());
			//System.out.println("i "+res.size()+" idx= "+idx);
//			System.out.println(res.size());
			return res;
		} else {
			for(TaskTrace t : others.get(idx)) {
				//System.out.println("Call ii"+" idx= "+idx+" "+this.getStates().size()+" "+trace_);
				if(t.firstState() != this.getStates().get(idx)) {
					continue;
				}
				if(t.getStates().size() > this.getStates().size()-idx) {
					continue;
				}
				boolean b = true;
				for(int i = 0; i < t.getStates().size(); i++) {
					if(t.getStates().get(i) != this.getStates().get(i+idx)) {
						b = false;
					}
				}
				if(!b) {
					continue;
				}
				if(t.getStates().size() <= 1) {
					if(states_.size() >= 2) {
						if(states_.get(states_.size()-1) == states_.get(states_.size()-2)) {
							continue;
						}
					}
				}
				if(dejaVu.contains(t.getTask())) {
					continue;
				}
				List<Integer> tmpS = new ArrayList<>();
				tmpS.addAll(states_);
				List<Symbol> tmpT = new ArrayList<>();
				tmpT.addAll(trace_);
				tmpT.add(t.getTask());
				tmpS.add(this.getStates().get(idx));
				states_.add(this.getStates().get(idx));
				List<Task> tmpD = new ArrayList<>();
				tmpD.addAll(dejaVu);
				tmpD.add((Task) t.getTask());
				//System.out.println(t.getTask()+" "+(idx+t.getStates().size()-1)+" "+tmpS+" "+tmpT);
				res.addAll(this.getAllRecomposition(
						others, 
						idx+t.getStates().size()-1,
						tmpS,
						tmpT,
						tmpD));
			}
			if(idx < this.getStates().size()-1) {
				//System.out.println("Call iii"+" idx= "+idx+" "+this.getStates().size()+" "+trace_);
				List<Integer> tmpS = new ArrayList<>();
				tmpS.addAll(states_);
				List<Symbol> tmpT = new ArrayList<>();
				tmpT.addAll(trace_);
				tmpS.add(this.getStates().get(idx));
				tmpT.add(this.getActionSequences().get(idx));
				//System.out.println((idx+1)+" "+tmpS+" "+tmpT);
				res.addAll(this.getAllRecomposition(
						others, 
						idx+1, 
						tmpS, 
						tmpT,
						dejaVu));
			}
//			System.out.println("!"+res.size());
			return res;
		}
		
	}
	
	/**
	 * 
	 * @return
	 */
	public Symbol getMethod() {
		String methodeName =  getTask().getName();
		Map<String, String> param = new LinkedHashMap<>();
		Map<String, String> absParam = new LinkedHashMap<>();
		getTask().getListParameters().forEach(pa -> {
			if(!param.containsKey(pa)) {
				param.put(pa, getTask().getParameters().get(pa));
				absParam.put(pa, getTask().getAbstractParameters().get(pa));
			}
		});
		getActionSequences().forEach(a -> {
			a.getListParameters().forEach(pa -> {
				if(!param.containsKey(pa)) {
					param.put(pa, a.getParameters().get(pa));
					absParam.put(pa, a.getAbstractParameters().get(pa));
				}
			});
		});
		Method m = new Method(
				methodeName,
				param,
				absParam,
				(fsm.Task)getTask());
		getActionSequences().forEach(a -> {m.add((fsm.Action)a);});
		return m;
	}
	
	/**
	 * 
	 * @return
	 */
	public Map<String, String> contants() {
		Map<String, String> res = super.contants();
		this.getTask().getParameters().forEach((k,v) -> {
			res.put(k, v);
		});
		return res;
	}
	
	public boolean equals(TaskTrace t) {
		boolean b = (this.task.equals(t.task) 
				&& this.trace.equals(t.trace));
		if(b) {
			for(int i = 0; i < this.states.size(); i++) {
				if(this.states.get(i) != t.states.get(i)) {
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 */
	public int hashCode() {
		int hash = 0;
		hash += this.task.hashCode();
		hash += this.trace.hashCode();
		hash *= (this.firstState()+1);
		return hash;
	}
}
