/**
 * 
 */
package learning.ADL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fsm.Symbol;
import learning.Observation;

/**
 * @author Maxence Grand
 *
 */
public class Equality extends Formula{
	/**
	 * 
	 */
	private String var1, var2, param1, param2;	
	
	/**
	 * Constructs 
	 * @param var1
	 * @param var2
	 * @param equal
	 */
	public Equality(String var1, String var2, String param1, String param2) {
		this.var1 = var1;
		this.var2 = var2;
		this.param1 = param1;
		this.param2 = param2;
	}

	
	/**
	 * Getter of var1
	 * @return the var1
	 */
	public String getVar1() {
		return var1;
	}


	/**
	 * Setter var1
	 * @param var1 the var1 to set
	 */
	public void setVar1(String var1) {
		this.var1 = var1;
	}


	/**
	 * Getter of var2
	 * @return the var2
	 */
	public String getVar2() {
		return var2;
	}


	/**
	 * Setter var2
	 * @param var2 the var2 to set
	 */
	public void setVar2(String var2) {
		this.var2 = var2;
	}


	

	/**
	 * Getter of param1
	 * @return the param1
	 */
	public String getParam1() {
		return param1;
	}


	/**
	 * Setter param1
	 * @param param1 the param1 to set
	 */
	public void setParam1(String param1) {
		this.param1 = param1;
	}


	/**
	 * Getter of param2
	 * @return the param2
	 */
	public String getParam2() {
		return param2;
	}


	/**
	 * Setter param2
	 * @param param2 the param2 to set
	 */
	public void setParam2(String param2) {
		this.param2 = param2;
	}


	/**
	 * 
	 */
	@Override
	public String toString() {
		String str = "(= "+var1+" "+var2+")";
		return str;
	}


	@Override
	public Formula checkLinkedVar(Symbol act) {
		// TODO Auto-generated method stub
		return this;
	}


	@Override
	public boolean equivalence(Formula other) {
		// TODO Auto-generated method stub
		if(other instanceof Equality) {
			Equality eq = (Equality) other;
			if(eq.var1.equals(var1)) {
				if(eq.var2.equals(var2)) {
					return true;
				} else {
					return false;
				}
			} else if(eq.var1.equals(var2)) {
				if(eq.var2.equals(var1)) {
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}


	@Override
	public Formula generalize(Map<String, String> mapping) {
		// TODO Auto-generated method stub
		String v1 = mapping.containsKey(var1) ? mapping.get(var1) : var1;
		String v2 = mapping.containsKey(var2) ? mapping.get(var2) : var2;
		return new Equality(v1,v2,param1,param2);
	}


	@Override
	public Map<String, String> getVar() {
		// TODO Auto-generated method stub
		Map<String, String> res = new HashMap<>();
		res.put(var1, param1);
		res.put(var2, param2);
		return res;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o instanceof Equality) {
			Equality other = (Equality)o;
			return this.var1.equals(other.var1) &&
					this.var2.equals(other.var2) &&
					this.param1.equals(other.param1) &&
					this.param2.equals(other.param2);
		}
		return false;
	}


	@Override
	public boolean produce(Formula F) {
		// TODO Auto-generated method stub
		return this.equals(F);
	}


	@Override
	public boolean check(Observation o, Symbol a) {
		// TODO Auto-generated method stub
		return var1.equals(var2);
	}
	
	@Override
	public boolean only(List<String> p) {
		// TODO Auto-generated method stub
		return p.contains(var1) && p.contains(var2);
	}
}
