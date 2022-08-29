/**
 * 
 */
package fsm;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maxence Grand
 *
 */
public class Method extends Action{
	/**
	 * The task to decompose
	 */
	private Task toDecompose;
	/**
	 * All subtasks
	 */
	private List<Action> subtasks;
	
	/**
	 * Constructs an Method
	 */
	public Method(String name) {
		super();
		this.toDecompose = new Task();
		this.subtasks = new ArrayList<>();
	}

	/**
	 * Constructs an Method
	 *
	 * @param name the Method's name
	 */
	public Method (String name, Task toDecompose){
		super(name);
		this.toDecompose = new Task(
				toDecompose.getName(),
				toDecompose.getParameters(),
				toDecompose.getAbstractParameters());
		this.subtasks = new ArrayList<>();
	}

	/**
	 * Constructs an Method
	 *
	 * @param name the Method's name
	 * @param parameters all parameters with types
	 */
	public Method(String name, Map<String, String> parameters, Task toDecompose){
		super(name,parameters);
		this.toDecompose = new Task(
				toDecompose.getName(),
				toDecompose.getParameters(),
				toDecompose.getAbstractParameters());
		this.subtasks = new ArrayList<>();
	}

	/**
	 * Constructs
	 * @param name Method name
	 * @param parameters Method parameters
	 * @param parametersAbs Method abstract parameters
	 */
	public Method(
			String name, 
			Map<String, String> parameters,
			Map<String, String> parametersAbs, 
			Task toDecompose){
		super(name,parameters);
		this.setAbstractParameters(parametersAbs);
		this.toDecompose = new Task(
				toDecompose.getName(),
				toDecompose.getParameters(),
				toDecompose.getAbstractParameters());
		this.subtasks = new ArrayList<>();
	}

	public Method clone() {
		Method clone = new Method(
				this.name, 
				this.parameters, 
				this.abstractParameters,
				this.toDecompose);
		this.subtasks.forEach(t -> clone.add(t));
		return clone;
	}
	/**
	 * Getter of toDecompose
	 * @return the toDecompose
	 */
	public Task getToDecompose() {
		return toDecompose;
	}

	/**
	 * Setter toDecompose
	 * @param toDecompose the toDecompose to set
	 */
	public void setToDecompose(Task toDecompose) {
		this.toDecompose = new Task(
				toDecompose.getName(),
				toDecompose.getParameters(),
				toDecompose.getAbstractParameters());
	}

	/**
	 * Getter of subtasks
	 * @return the subtasks
	 */
	public List<Action> getSubtasks() {
		return subtasks;
	}

	/**
	 * Setter subtasks
	 * @param subtasks the subtasks to set
	 */
	public void setSubtasks(List<Action> subtasks) {
		this.subtasks = subtasks;
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.util.List#add(java.lang.Object)
	 */
	public boolean add(Action arg0) {
		return subtasks.add(arg0);
	}
	
	public String toString() {
		StringBuilder str = new StringBuilder();
		str.append(super.toString());
//		str.append("\n\t"+"Task : ");
//		str.append(this.toDecompose);
//		str.append("\n\t"+"Subtasks : ");
//		str.append(this.subtasks);
//		str.append("\n");
		return str.toString();
	}
	
	public String toStringShort() {
		StringBuilder str = new StringBuilder();
		str.append(super.toString());
//		str.append("\n");
		return str.toString();
	}
	
	/**
	 * 
	 * @param tasksDecompositions
	 * @return
	 */
//	public Sample getAllDecompositions(
//			Map<Task, 
//			List<Method>> tasksDecompositions,
//			List<String> currentTag) {
//		Sample res = new Sample();
//		List<Symbol> current = new LinkedList<>();
//		Symbol start = new TaggedAction(this, currentTag);
//		start.setName(start.getName()+"_start");
//		current.add(start);
//		Symbol end = new TaggedAction(this, currentTag);
//		end.setName(end.getName()+"_end");
//		List<String> newTag = new LinkedList<>();
//		currentTag.forEach(tag -> newTag.add(tag));
//		newTag.add(this.getName());
//		Sample tmp = this.getDecomposition(tasksDecompositions, current, newTag, 0);
//		for(Trace t : tmp.getExamples()) {
//			Example ex = (Example)t;
//			ex.add(end);
//			res.addExample(ex);
//		}
//		return res;
//	}
	
//	public Sample getAllDecompositions(
//	        Map<Task, 
//	        List<Method>> tasksDecompositions) {
//	    Sample res = new Sample();
//	    List<Symbol> current = new LinkedList<>();
//	    Sample tmp = this.getDecomposition(tasksDecompositions, current, 0);
//	    for(Trace t : tmp.getExamples()) {
//	        Example ex = (Example)t;
//	        res.addExample(ex);
//	    }
//	    return res;
//	}
//
//	public Sample getAllGeneralizedDecompositions(
//	        Map<Task, 
//	        List<Method>> tasksDecompositions) {
//	    Sample res = new Sample();
//	    List<Symbol> current = new LinkedList<>();
//	    Sample tmp = this.getGeneralizedDecomposition(
//	    		tasksDecompositions, 
//	    		current, 
//	    		0);
//	    for(Trace t : tmp.getExamples()) {
//	        Example ex = (Example)t;
//	        res.addExample(ex);
//	    }
//	    return res;
//	}
//	
//	private Sample getDecomposition(
//	        Map<Task, List<Method>> tasksDecompositions,
//	        List<Symbol> current,
//	        int idx) {
//	    Sample res = new Sample();
//	    if(this.subtasks.isEmpty()) {
//	    	res.addExample(new Example(new ArrayList<>()));
//	        return res;
//	    } else if(idx >= this.subtasks.size()) {
//	        res.addExample(new Example(current));
//	        return res;
//	    }
//	    if(this.subtasks.get(idx) instanceof Task) {
//	        Sample currentSample = new Sample();
//	       // System.out.println(this.subtasks.get(idx));
//	        for(Method method : tasksDecompositions.get(this.subtasks.get(idx))) {
//	            Sample tmp = method.getAllDecompositions(tasksDecompositions);
//	            for(Trace ex : tmp.getExamples()) {
//	                List<Symbol> tmp2 = new LinkedList<>();
//	                tmp2.addAll(current);
//	                tmp2.addAll(ex.getActionSequences());
//	                currentSample.addExample(new Example(tmp2));
//	            }
//	        }
//	        for(Trace ex : currentSample.getExamples()) {
//	            res.add(this.getDecomposition(
//	                    tasksDecompositions, 
//	                    ex.getActionSequences(), 
//	                    idx+1));
//	        }
//	        return res;
//	    } else {
//	        Symbol tagged = this.subtasks.get(idx).clone();
//	        current.add(tagged);
//	        return this.getDecomposition(tasksDecompositions, current, idx+1);
//	    }
//	    //return res;
//	}
//	private Sample getGeneralizedDecomposition(
//	        Map<Task, List<Method>> tasksDecompositions,
//	        List<Symbol> current,
//	        int idx) {
//	    Sample res = new Sample();
//	    if(this.subtasks.isEmpty()) {
//	    	res.addExample(new Example(new ArrayList<>()));
//	        return res;
//	    } else if(idx >= this.subtasks.size()) {
//	        res.addExample(new Example(current));
//	        return res;
//	    }
//	    if(this.subtasks.get(idx) instanceof Task) {
//	        Sample currentSample = new Sample();
//	        Task ta = (Task) this.subtasks.get(idx).generalize();
//	        for(Method method : tasksDecompositions.get(ta)) {
//	        	method = (Method) method.generalize(
//	        			Utils.reverseParamMap(ta.mapping()));
//	            Sample tmp = method.getAllGeneralizedDecompositions(
//	            		tasksDecompositions);
//	            for(Trace ex : tmp.getExamples()) {
//	                List<Symbol> tmp2 = new LinkedList<>();
//	                tmp2.addAll(current);
//	                tmp2.addAll(ex.getActionSequences());
//	                currentSample.addExample(new Example(tmp2));
//	            }
//	        }
//	        for(Trace ex : currentSample.getExamples()) {
//	            res.add(this.getGeneralizedDecomposition(
//	                    tasksDecompositions, 
//	                    ex.getActionSequences(), 
//	                    idx+1));
//	        }
//	        return res;
//	    } else {
//	        Symbol tagged = this.subtasks.get(idx).clone();
//	        current.add(tagged);
//	        return this.getGeneralizedDecomposition(
//	        		tasksDecompositions, 
//	        		current, 
//	        		idx+1);
//	    }
//	    //return res;
//	}
	
	/**
	 * 
	 */
	public int hashCode() {
		return super.toString().hashCode();
	}
	
	/**
	 * 
	 */
	public boolean equals(Object other) {
		boolean b =  super.equals(other);
		if(b) {
			if(!(other instanceof Method)) {
				return false;
			}
			Method m = (Method) other;
			if( this.subtasks.size() != m.subtasks.size()) {
				return false;
			}
			for(int i = 0; i < this.subtasks.size(); i++) {
				if(! this.subtasks.get(i).equals(m.subtasks.get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Generalize an Action
	 * 
	 * @return generalized action
	 */
	public Symbol generalize() {
    	Map<String, String> mapping = mapping();
    	Map<String, String> newParam = new LinkedHashMap<>();
    	Map<String, String> newAbsParam = new LinkedHashMap<>();
    	for(Map.Entry<String, String> entry : parameters.entrySet()) {
    		newParam.put(
            		mapping.get(entry.getKey()), 
            		this.abstractParameters.get(entry.getKey()));
    		newAbsParam.put(
            		mapping.get(entry.getKey()), 
            		this.abstractParameters.get(entry.getKey()));
        }
    	Method m = new Method(
        		this.name, 
        		newParam, 
        		newAbsParam,
        		(Task)this.getToDecompose().generalize(mapping));
        for(Action a : this.getSubtasks()) {
        	m.add((Action)a.generalize(mapping));
        }
        return m;
    }
	
	/**
	 * Map action's parameters with new parameters
	 *
	 * @param mapping Parameters' mapping
	 * @return The new action
	 */
	@Override
	public Symbol generalize(Map<String, String> mapping) {
		Map<String, String> newParam = new LinkedHashMap<>();
		Map<String, String> newAbsParam = new LinkedHashMap<>();
		for(Map.Entry<String, String> entry : parameters.entrySet()) {
			newParam.put(mapping.get(entry.getKey()), entry.getValue());
			newAbsParam.put(
					mapping.get(entry.getKey()), 
					this.getAbstractParameters().get(entry.getKey()));
		}
//		System.out.println("Generalize "+this.name);
//    	System.out.println(mapping);
//    	System.out.println("v1: "+toDecompose.getParameters()+" "+toDecompose.getAbstractParameters());
		Method m = new Method(
        		this.name, 
        		newParam, 
        		newAbsParam,
        		(Task)this.getToDecompose().generalize(mapping));
//    	System.out.println("v1: "+m.toDecompose.getParameters()+" "+m.toDecompose.getAbstractParameters());
        for(Action a : this.getSubtasks()) {
        	m.add((Action)a.generalize(mapping));
        }
        return m;
	}
}
