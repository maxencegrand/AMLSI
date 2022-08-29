/**
 * 
 */
package fr.uga.amlsi.fsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.uga.generator.symbols.Action;
import fr.uga.generator.symbols.Symbol;

/**
 * @author Maxence Grand
 *
 */
public class TaggedAction extends Action{
	private List<String> tag;
	
	/**
	 * 
	 * Constructs
	 */
	public TaggedAction() {
		super();
		this.tag = new ArrayList<>();
	}
	
	/**
	 * 
	 * Constructs 
	 * @param s
	 * @param tag
	 */
	public TaggedAction(Symbol s, List<String> tag) {
		super(s.getName(),s.getParameters(), s.getAbstractParameters());
		this.tag = tag;
	}
	
	/**
	 * 
	 */
	public String toString() {
		StringBuilder tag_ = new StringBuilder();
		this.tag.forEach(t -> tag_.append("_"+t));
		String res = this.name+tag_.toString()+"(";

		for(Map.Entry<String, String> entry : parameters.entrySet()) {
			res += " "+entry.getKey();
		}

		res += ")";
		return res;
	}

	/**
	 * Getter of tag
	 * @return the tag
	 */
	public List<String> getTag() {
		return tag;
	}

	/**
	 * Setter tag
	 * @param tag the tag to set
	 */
	public void setTag(List<String> tag) {
		this.tag = tag;
	}
	
	/**
	 * 
	 * @return
	 */
	public Symbol getUntaggedAction() {
		return new Action(this.name, this.parameters, this.abstractParameters);
	}
}
