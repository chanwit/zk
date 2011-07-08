/* LeafInfo.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul  6 19:46:33 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.metainfo;

import java.util.List;
import java.util.Collections;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.EvaluatorRef;

/**
 * Used to implement a leaf node that does not allow any children.
 * <p>Notice that it is serializable.
 * Also notice that all nodes except the root must be an instance of
 * {@link LeafInfo} or its derives.
 * @author tomyeh
 * @since 5.1.0
 */
/*package*/ abstract class LeafInfo implements NodeInfo, java.io.Serializable {
	//Note: getEvaluatorRef() is the same as getPageDefintion().getEvaluatorRef()
	//However, we store _evalr since PageDefinition is not serializable
	/*package*/ EvaluatorRef _evalr;
	//transient since it is maintained by BranchInfo.readObject()
	private transient NodeInfo _parent;

	/*package*/ LeafInfo() {
	}
	/*package*/ LeafInfo(NodeInfo parent) {
		parent.appendChild(this);
	}
	/** Used only by {@link ComponentInfo#duplicate} to make a virtual copy.
	 */
	/*package*/ LeafInfo(LeafInfo from) {
		_parent = from._parent; //direct copy since it is 'virtual'
		_evalr = from._evalr;
	}

	//@Override
	public EvaluatorRef getEvaluatorRef() {
		return _evalr;
	}
	//@Override
	public PageDefinition getPageDefinition() {
		return _evalr != null ? _evalr.getPageDefinition(): null;
	}
	//@Override
	public Evaluator getEvaluator() {
		return _evalr != null ? _evalr.getEvaluator(): null;
	}

	//@Override
	public NodeInfo getParent() {
		return _parent;
	}
	//@Override
	public List getChildren() {
		return Collections.EMPTY_LIST;
	}

	/** Sets a parent directly without maintaining the parent/child relationship.
	 */
	/*package*/ void setParentDirectly(NodeInfo parent) {
		_parent = parent;
	}

	//@Override
	public void appendChild(NodeInfo child) {
		throw new UiException(this+" does not allow any children");
	}
	//@Override
	public boolean removeChild(NodeInfo child) {
		return false;
	}
}
