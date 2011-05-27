/* AuInsertBefore.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:30:18     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import java.util.Collection;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.StubComponent;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ext.Native;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to insert an unparsed HTML before the specified component
 * at the client.
 * <p>data[0]: the uuid of the component before which the HTML will insert<br>
 * data[1]: the unparsed HTML (aka., content)
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuInsertBefore extends AuResponse {
	/**
	 * @param contents a collection of contents (in String objects).
	 * Each content is the output of a component.
	 * @since 5.0.7
	 */
	public AuInsertBefore(Component anchor, Collection contents) {
		super("addBfr", anchor, toArray(anchor, contents));
	}
	private static Object[] toArray(Component anchor, Collection contents) {
		if (anchor instanceof Native || anchor instanceof StubComponent)
			throw new UiException("Adding a component before a native one not allowed: "+anchor);

		return AuAppendChild.toArray(anchor.getUuid(), contents);
	}
}
