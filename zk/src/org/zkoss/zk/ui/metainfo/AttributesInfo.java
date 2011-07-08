/* AttributesInfo.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 13 09:06:16     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.impl.Utils;

/**
 * Represents a map of custom attributes of a component definition
 * ({@link ComponentDefinition}).
 * It is equivalent to the custom-attributes element.
 *
 * <p>Note: it is serializable.
 *
 * @author tomyeh
 */
public class AttributesInfo extends ConditionLeafInfo {
	/** Map(String name, ExValue/ExValue[]/Map value). */
	private final Map _attrs;
	private final int _composite;
	private final int _scope;

	/** Constructor.
	 * @param attrs the custom attributes (String name, String value).
	 * Once called, the caller shall not access attrs again -- it belongs
	 * to this object.
	 * @param scope specifies the scope.
	 * @param composite indicates the composite type.
	 * It can be one of "none", "list" or "map".
	 * If null or empty, "none" is assumed.
	 * @exception IllegalArgumentException if the composite type is illegal.
	 * @since 3.0.6
	 */
	public AttributesInfo(NodeInfo parent,
	Map attrs, String scope, String composite, ConditionImpl cond) {
		super(parent, cond);

		if (composite == null || composite.length() == 0
		|| composite.equals("none"))
			_composite = Utils.SCALAR;
		else if (composite.equals("list"))
			_composite = Utils.LIST;
		else if (composite.equals("map"))
			_composite = Utils.MAP;
		else
			throw new IllegalArgumentException("Unkonwn composite: "+composite);

		_attrs = attrs;
		if (_attrs != null) {
			for (Iterator it = _attrs.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				me.setValue(
					Utils.parseComposite(
						(String)me.getValue(), Object.class, _composite));
			}
		}

		_scope = scope == null ?
			Component.COMPONENT_SCOPE: Components.getScope(scope);
	}
	/** The same as AttributesInfo(parent, attrs, scope, "none", cond).
	 *
	 * @param attrs the custom attributes (String name, String value).
	 * Once called, the caller shall not access attrs again -- it belongs
	 * to this object.
	 * @param scope specifies the scope.
	 */
	public AttributesInfo(NodeInfo parent, Map attrs, String scope, ConditionImpl cond) {
		this(parent, attrs, scope, null, cond);
	}

	/** Returns the scope.
	 * @since 3.0.6
	 */
	public String getScope() {
		return Components.scopeToString(_scope);
	}
	/** Returns the composite type: "none", "list" or "map".
	 * @since 3.0.6
	 */
	public String getComposite() {
		return _composite == Utils.LIST ? "list":
			_composite == Utils.MAP ? "map": "none";
	}

	/** Applies the custom attributes.
	 * <p>Note: this method does nothing if {@link #isEffective} returns false.
	 */
	public void apply(Component comp) {
		if (_attrs != null && isEffective(comp)) {
			final Evaluator eval = getEvaluator();
			for (Iterator it = _attrs.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String name = (String)me.getKey();
				final Object value = me.getValue();
				comp.setAttribute(
					name, Utils.evaluateComposite(eval, comp, value), _scope);
			}
		}
	}
	/** Applies the custom attributes.
	 * <p>Note: this method does nothing if {@link #isEffective} returns false.
	 */
	public void apply(Page page) {
		if (_attrs != null && isEffective(page)) {
			final Evaluator eval = getEvaluator();
			for (Iterator it = _attrs.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String name = (String)me.getKey();
				final Object value = me.getValue();
				page.setAttribute(name,
					Utils.evaluateComposite(eval, page, value), _scope);
			}
		}
	}

	//Object//
	public String toString() {
		final StringBuffer sb = new StringBuffer(40).append("[custom-attributes:");
		if (_attrs != null)
			for (Iterator it = _attrs.keySet().iterator(); it.hasNext();)
				sb.append(' ').append(it.next());
		return sb.append(']').toString();
	}
}
