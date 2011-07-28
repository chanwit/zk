/* ImportedClassResolver.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 27 13:17:02 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.lang;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * The class resolve that allows to import classes and packages, like Java's
 * import statment does.
 * <p>For example,
 * <pre><code>
 * ImportedClassResolver resolver = new ImportedClassResolver();
 * resolver.addImportedClass("org.zkoss.lang.*");
 * resolver.addImportedClass("org.zkoss.util.Maps");
 * resolver.resolveClass("ImportedClassResolver");</code></pre>
 *
 * @author tomyeh
 * @since 5.1.0
 * @see SimpleClassResolver
 */
public class ImportedClassResolver implements ClassResolver, java.io.Serializable {
	/** A map of classes (String nm, Class cls). */
	private Map _clses;
	/** A list of packages. */
	private List _pkgs;

	/** Adds an imported class
	 * Like Java, it is used to import a class or a package of classes, so
	 * that it simplifies the use of the apply attribute, the init directive
	 * and others.
	 * 
	 * @param clsptn the class's full-qualitified name, e.g., <code>com.foo.FooComposer</code>,
	 * a wildcard representing all classes of the give pacakge, e.g., <code>com.foo.*</code>.
	 */
	public void addImportedClass(String clsptn)
	throws ClassNotFoundException {
		if (clsptn == null || (clsptn = clsptn.trim()).length() == 0)
			throw new IllegalArgumentException("empty");

		final int j = clsptn.lastIndexOf('.');
		final String nm;
		if (j >= 0) {
			nm = clsptn.substring(j + 1);
			if ("*".equals(nm)) {
				if (_pkgs == null)
					_pkgs = new LinkedList();
				final String pkg = clsptn.substring(0, j + 1);  //including '.'
				if (!_pkgs.contains(pkg))
					_pkgs.add(pkg);
				return;
			}
		} else {
			nm = clsptn;
		}

		if (_clses == null)
			_clses = new HashMap(4);
		_clses.put(nm, Classes.forNameByThread(clsptn));
	}
	/** Returns a readonly list of the imported class.
	 */
	public List getImportedClasses() {
		final List lst = new LinkedList();
		if (_clses != null)
			for (Iterator it = _clses.values().iterator(); it.hasNext();)
				lst.add(((Class)it.next()).getName());
		if (_pkgs != null)
			for (Iterator it = _pkgs.iterator(); it.hasNext();)
				lst.add(it.next() + "*");
		return lst;
	}
	/** Adds all imported classes of the given class resolver.
	 */
	public void addAll(ImportedClassResolver resolver) {
		if (resolver._pkgs != null) {
			if (_pkgs == null)
				_pkgs = new LinkedList();
			for (Iterator it = resolver._pkgs.iterator(); it.hasNext();) {
				final String pkg = (String)it.next();
				if (!_pkgs.contains(pkg))
					_pkgs.add(pkg);
			}
		}

		if (resolver._clses != null) {
			if (_clses == null)
				_clses = new HashMap(4);
			_clses.putAll(resolver._clses);
		}
	}

	//@Override
	public Class resolveClass(String clsnm) throws ClassNotFoundException {
		if (_pkgs != null && clsnm.indexOf('.') < 0) {
			final Class cls = (Class)_clses.get(clsnm);
			if (cls != null)
				return cls;

			for (Iterator it = _pkgs.iterator(); it.hasNext();) {
				final String pkg = (String)it.next();
				try {
					return Classes.forNameByThread(pkg + clsnm);
				} catch (ClassNotFoundException ex) { //ignore
				}
			}
		}
		return Classes.forNameByThread(clsnm);
	}
}
