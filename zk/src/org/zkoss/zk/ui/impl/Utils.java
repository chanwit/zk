/* Utils.java

	Purpose:
		
	Description:
		
	History:
		Thu May  5 11:09:46 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.impl;

import org.zkoss.lang.Classes;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.sys.WebAppCtrl;

/**
 * Utilities for implementation.
 * @author tomyeh
 * @since 5.0.7
 */
public class Utils {
	/** Marks the per-desktop information of the given key will be generated,
	 * and returns true if the information is not generated yet
	 * (i.e., this method is NOT called with the given key).
	 * You could use this method to minimize the bytes to be sent to
	 * the client if the information is required only once per desktop.
	 */
	public static
	boolean markClientInfoPerDesktop(Desktop desktop, String key) {
		return !(desktop instanceof DesktopImpl) //always gen if unknown
		|| ((DesktopImpl)desktop).markClientInfoPerDesktop(key);
	}

	/** Instantiates a composer of the given object.
	 * This method will invoke {@link org.zkoss.zk.ui.sys.UiFactory#newComposer}
	 * to instantiate the composer if page is not null.
	 * @param page the page that the composer will be created for.
	 * Ignored if null.
	 * @param o the composer instance, the class of the composer to instantiate,
	 * or the name of the class of the composer.
	 * If <code>o</code> is an instance of {@link Composer}, it is returned
	 * directly.
	 */
	public static Composer newComposer(Page page, Object o)
	throws Exception {
		Class cls;
		if (o instanceof String) {
			cls = page != null ? page.resolveClass((String)o):
				Classes.forNameByThread(((String)o).trim());
		} else if (o instanceof Class) {
			cls = (Class)o;
		} else
			return (Composer)o;

		if (page != null) 
			return ((WebAppCtrl)page.getDesktop().getWebApp()).getUiFactory().newComposer(cls, page);
		return (Composer)cls.newInstance();
	}
}
