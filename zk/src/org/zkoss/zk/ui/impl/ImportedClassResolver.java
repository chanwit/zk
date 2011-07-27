/* ImportedClassResolver.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 27 13:17:02 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.impl;

import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Collections;

import org.zkoss.lang.ClassResolver;
import org.zkoss.lang.Classes;

/**
 * The implementation that handles the imported class for a page definition.
 * @author tomyeh
 * @since 5.1.0
 * @see SimpleClassResolver
 */
public class ImportedClassResolver implements ClassResolver, java.io.Serializable {
	/** List(String). */
	private List _impclses;

	/** Adds an imported class
	 * Like Java, it is used to import a class or a package of classes, so
	 * that it simplifies the use of the apply attribute, the init directive
	 * and others.
	 * 
	 * @param clsptn the class's full-qualitified name, e.g., <code>com.foo.FooComposer</code>,
	 * a wildcard representing all classes of the give pacakge, e.g., <code>com.foo.*</code>.
	 */
	public void addImportedClass(String clsptn) {
		if (clsptn == null || (clsptn = clsptn.trim()).length() == 0)
			throw new IllegalArgumentException("empty");

		if (_impclses == null)
			_impclses = new LinkedList();
		else if (_impclses.contains(clsptn))
			return;

		if (clsptn.endsWith(".*")) { //wildcard
			_impclses.add(clsptn);
			return;
		}

		//clsptn is not wildcard
		for (ListIterator it = _impclses.listIterator(_impclses.size());
		it.hasPrevious();) {
			final String cp = (String)it.previous();
			if (!cp.endsWith(".*")) {
				it.next();
				it.add(clsptn);
				return;
			}
		}
		_impclses.add(0, clsptn); //all wildcard or empty
	}
	/** Returns a readonly list of the imported class.
	 */
	public List getImportedClasses() {
		return _impclses != null ? _impclses: Collections.EMPTY_LIST;
	}
	/** Adds all imported classes of the given class resolver.
	 */
	public void addAll(ImportedClassResolver resolver) {
		if (resolver._impclses != null)
			for (Iterator it = resolver._impclses.iterator(); it.hasNext();)
				addImportedClass((String)it.next());
	}

	//@Override
	public Class resolveClass(String clsnm) throws ClassNotFoundException {
		if (_impclses != null && clsnm.indexOf('.') < 0) {
			for (Iterator it = _impclses.iterator(); it.hasNext();) {
				final String ptn = (String)it.next();
				final int j = ptn.lastIndexOf('.');
				if (j < 0) {
					if (ptn.equals(clsnm))
						break; //found
				} else {
					final String c = ptn.substring(j + 1);
					final boolean wildcard = "*".equals(c);
					if (wildcard || c.equals(clsnm)) {
						try {
							return Classes.forNameByThread(
								wildcard ? ptn.substring(0, j + 1) + clsnm: ptn);
						} catch (ClassNotFoundException ex) { //ignore
						}
					}
				}
			}
		}
		return Classes.forNameByThread(clsnm);
	}
}
