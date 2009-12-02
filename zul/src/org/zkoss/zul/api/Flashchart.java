/* Flashchart.java

	Purpose:
		
	Description:
		
	History:
		Nov 26, 2009 12:19:18 AM , Created by joy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.api;

import org.zkoss.zul.ChartModel;

/**
 * A generic flashchart component.
 * 
 * @author Joy Lo
 * @date Created at Nov 25, 2009 4:28:49 PM
 * @since 5.0.0
 */
public interface Flashchart extends Flash {
	/**
	 * Sets the type of chart
	 * <p>Default: "pie"
	 * <p>Types: pie, line, bar, column
	 */
	public void setType(String type);
	/**
	 * Returns the type of chart
	 */
	public String getType();
	/**
	 * Sets the model of chart.
	 * <p>Only implement models which matched the allowed types
	 * @param model
	 * @see #setType(String)
	 */
	public void setModel(ChartModel model);
	/**
	 * Returns the model of chart
	 */
	public ChartModel getModel();
	/**
	 * Returns a string which prepares to use in javascript as a chart style
	 */
	public String getStyles();
	/**
	 * Sets the style of chart
	 * @param styles
	 */
	public void setStyles(String styles);
}