/**
 * 
 */
package test.fsm;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import fsm.Action;
import junit.framework.TestCase;

/**
 * @author Maxence Grand
 *
 */
public class ActionTest extends TestCase {

	private static Action getActionWithoutParameter(String name) {
		Action act = new Action(name);
		Map<String, String> param = new LinkedHashMap<>();
		Map<String, String> paramAbstract = new HashMap<>();
		act.setAbstractParameters(paramAbstract);
		act.setParameters(param);
		return act;
	}

	private static Action getActionWithParameter(String name) {
		Action act = new Action(name);
		Map<String, String> param = new LinkedHashMap<>();
		param.put("x", "t");
		param.put("y", "t");
		Map<String, String> paramAbstract = new HashMap<>();
		paramAbstract.put("t", "object");
		act.setAbstractParameters(paramAbstract);
		act.setParameters(param);
		return act;
	}

	private static Action getActionWithParameterGeneralized(String name) {
		Action act = new Action(name);
		Map<String, String> param = new LinkedHashMap<>();
		param.put("?x1", "t");
		param.put("?x2", "t");
		Map<String, String> paramAbstract = new HashMap<>();
		paramAbstract.put("t", "object");
		act.setAbstractParameters(paramAbstract);
		act.setParameters(param);
		return act;
	}

	/**
	 * Test method for {@link fsm.Action#toString()}.
	 */
	public void testToString() {
		Action a = ActionTest.getActionWithoutParameter("test");
		assertEquals(a.toString(),"test()");
		a = ActionTest.getActionWithParameter("test");
		assertEquals(a.toString(),"test( x y)");

	}

	/**
	 * Test method for {@link fsm.Action#toStringType()}.
	 */
	public void testToStringType() {
		Action a = ActionTest.getActionWithParameter("test");
		assertEquals(a.toStringType(),"test( x - t y - t)");
	}

	/**
	 * Test method for {@link fsm.Action#generalize()}.
	 */
	public void testGeneralize() {
		Action a = ActionTest.getActionWithParameter("test");
		Action a2 = ActionTest.getActionWithParameterGeneralized("test");
		assertEquals(a.generalize(), a2);
	}

	/**
	 * Test method for {@link fsm.Action#generalize(java.util.Map)}.
	 */
	public void testGeneralizeMapOfStringString() {
		Action a = ActionTest.getActionWithParameter("test");
		Action a2 = ActionTest.getActionWithParameterGeneralized("test");
		Map<String, String> map = new HashMap<>();
		map.put("x", "?x1");
		map.put("y", "?x2");
		assertEquals(a.generalize(map), a2);
	}

	/**
	 * Test method for {@link fsm.Action#clone()}.
	 */
	public void testClone() {
		Action a = ActionTest.getActionWithParameter("test");
		Action aClone = ActionTest.getActionWithParameter("test");
		assertEquals(a.clone(), aClone);
	}

	/**
	 * Test method for {@link fsm.Action#Action()}.
	 */
	public void testAction() {
		Action a = new Action();
		assertEquals(a.getName(),"");
		assertNull(a.getAbstractParameters());
		assertEquals(a.getParameters(), new LinkedHashMap<>());
	}

	/**
	 * Test method for {@link fsm.Action#Action(java.lang.String)}.
	 */
	public void testActionString() {
		Action a = new Action("test");
		assertEquals(a.getName(),"test");
		assertNull(a.getAbstractParameters());
		assertEquals(a.getParameters(), new LinkedHashMap<>());
	}

	/**
	 * Test method for {@link fsm.Action#Action(java.lang.String, java.util.Map)}.
	 */
	public void testActionStringMapOfStringString() {
		Map<String, String> param = new LinkedHashMap<>();
		param.put("x", "t");
		param.put("y", "t");
		Action a = new Action("test", param);
		assertEquals(a.getName(),"test");
		assertNull(a.getAbstractParameters());
		assertEquals(a.getParameters(), param);
	}

	/**
	 * Test method for {@link fsm.Action#Action(java.lang.String, java.util.Map, java.util.Map)}.
	 */
	public void testActionStringMapOfStringStringMapOfStringString() {
		Map<String, String> param = new LinkedHashMap<>();
		param.put("x", "t");
		param.put("y", "t");
		Map<String, String> paramAbstract = new HashMap<>();
		paramAbstract.put("t", "object");
		Action a = new Action("test", param, paramAbstract);
		assertEquals(a.getName(),"test");
		assertEquals(a.getAbstractParameters(), paramAbstract);
		assertEquals(a.getParameters(), param);
	}

	/**
	 * Test method for {@link fsm.Action#getAllPredicates(java.util.List, learning.TypeHierarchy)}.
	 */
	public void testGetAllPredicates() {
		
	}


	/**
	 * Test method for {@link fsm.Symbol#equals(java.lang.Object)}.
	 */
	public void testEquals() {
		assertEquals(getActionWithoutParameter("test"), 
				getActionWithoutParameter("test"));
		assertEquals(getActionWithParameter("test"), 
				getActionWithParameter("test"));
		assertFalse(getActionWithoutParameter("test").equals( 
				getActionWithoutParameter("test2")));
		assertFalse(getActionWithoutParameter("test").equals( 
				getActionWithParameter("test")));
	}

	/**
	 * Test method for {@link fsm.Symbol#setAbstractParameters(java.util.Map)}.
	 */
	public void testSetAbstractParameters() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link fsm.Symbol#setParameters(java.util.Map)}.
	 */
	public void testSetParameters() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link fsm.Symbol#compatible(fsm.Symbol)}.
	 */
	public void testCompatible() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link fsm.Symbol#compatibleType(fsm.Symbol, learning.TypeHierarchy)}.
	 */
	public void testCompatibleType() {
	}

	/**
	 * Test method for {@link fsm.Symbol#mapping()}.
	 */
	public void testMapping() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link fsm.Symbol#getParametersMapping(fsm.Symbol)}.
	 */
	public void testGetParametersMapping() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link fsm.Symbol#getListParametersType()}.
	 */
	public void testGetListParametersType() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link fsm.Symbol#getListType()}.
	 */
	public void testGetListType() {
		fail("Not yet implemented");
	}

}
