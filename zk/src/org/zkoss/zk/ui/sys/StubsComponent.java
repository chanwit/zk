/* StubsComponent.java

	Purpose:
		
	Description:
		
	History:
		Sat Jun  4 21:24:11 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.sys;

import org.zkoss.zk.ui.Component;

/**
 * Represents a tree of {@link StubComponent} that are merged into
 * a single component.
 *
 * @author tomyeh
 * @since 5.1.0
 */
public class StubsComponent extends StubComponent {
	//@Override
	public void replace(Component comp, boolean bFellow, boolean bListener) {
		replace(comp, bFellow, bListener, false); //remove all children
	}

	/** Returns the widget class, "#stubs".
	 */
	public String getWidgetClass() {
		return "#stubs";
	}
}
