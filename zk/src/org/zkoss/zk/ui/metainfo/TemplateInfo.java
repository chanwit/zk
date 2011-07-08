/* TemplateInfo.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 14:30:36 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.ui.util.ConditionImpl;

/**
 * Represent a template element.
 * @author tomyeh
 * @since 5.1.0
 */
public class TemplateInfo extends BranchInfo {
	private final String _name;

	/** Creates a template.
	 *
	 * @param parent the parent node (never null)
	 * @param name the name of the template (never null)
	 */
	public TemplateInfo(NodeInfo parent, String name, ConditionImpl cond) {
		super(parent, cond);

		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("null");
		_name = name;
	}

	/** Returns the name of the template info.
	 */
	public String getName() {
		return _name;
	}

	//Object//
	public String toString() {
		return "[template:" + _name + ']';
	}
}
