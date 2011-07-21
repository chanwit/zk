/* TemplateInfo.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 14:30:36 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.metainfo;

import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Collections;

import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ConditionImpl;

/**
 * Represent a template element.
 * @author tomyeh
 * @since 5.1.0
 */
public class TemplateInfo extends BranchInfo {
	private final String _name;
	private final ExValue _src;
	private final Map _params;

	/** Creates a template.
	 *
	 * @param parent the parent node (never null)
	 * @param name the name of the template (never null)
	 * @param params the map of parameters. Igored if null.<br/>
	 * Notice that, once assigned, the map belongs to this object, and the caller
	 * shall not access it again
	 */
	public TemplateInfo(NodeInfo parent, String name, String src, Map params, ConditionImpl cond) {
		super(parent, cond);

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("null");
		_name = name;
		_src = src != null ? new ExValue(src, String.class): null;

		if (params != null && !params.isEmpty()) {
			for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				me.setValue(new ExValue((String)me.getValue(), Object.class));
			}
			_params = params;
		} else
			_params = null;
	}

	/** Returns the name of the template info.
	 */
	public String getName() {
		return _name;
	}
	/** Returns the URI to create the template from, or null if not specified.
	 */
	public String getSrc(Component comp) {
		return _src != null ? (String)_src.getValue(_evalr, comp): null;
	}
	/** Evaluates and returns a readonly map of parameters assigned
	 * to this template (never null).
	 */
	public Map resolveParameters(Component comp) {
		if (_params == null)
			return Collections.EMPTY_MAP;

		final Map params = new LinkedHashMap(); //eval order is important
		for (Iterator it = _params.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			params.put(me.getKey(),
				((ExValue)me.getValue()).getValue(_evalr, comp));
		}
		return params;
	}

	//Object//
	public String toString() {
		return "[template:" + _name + ']';
	}
}
