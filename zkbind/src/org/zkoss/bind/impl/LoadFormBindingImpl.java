/* LoadFormBinding.java

	Purpose:
		
	Description:
		
	History:
		Aug 9, 2011 6:25:44 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Form;
import org.zkoss.bind.sys.BindEvaluatorX;
import org.zkoss.bind.sys.ConditionType;
import org.zkoss.bind.sys.LoadFormBinding;
import org.zkoss.bind.xel.zel.BindELContext;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;

/**
 * Implementation of {@link LoadFormBinding}
 * @author henrichen
 *
 */
public class LoadFormBindingImpl extends FormBindingImpl implements	LoadFormBinding {
	private int _len;
	private Set<String> _doneDependsOn = new HashSet<String>(4);
	
	public LoadFormBindingImpl(Binder binder, Component comp, String formId, Form form, String loadExpr, 
			ConditionType conditionType,String command, Map<String, Object> bindingArgs) {
		super(binder, comp, formId, form, loadExpr, conditionType, command, bindingArgs);
	}

	public void load(BindContext ctx) {
		final Binder binder = getBinder();
		final BindEvaluatorX eval = binder.getEvaluatorX();
		final Component comp = ctx.getComponent();
		final Form form = getFormBean();
		for (String field : form.getLoadFieldNames()) {
			final ExpressionX expr = getFieldExpression(eval, field);
			if (expr != null) {
				final Object value = eval.getValue(ctx, comp, expr);
				form.setField(field, value);
			}
		}
		((FormImpl)form).initFields(); //initial loading, mark form as clean
		
		binder.notifyChange(form, "*"); //notify change of fx.*
	}
	
	public void setSeriesLength(int len) {
		_len = len;
	}
	
	public int getSeriesLength() {
		return _len;
	}

	/**
	 * Internal Use Only.
	 */
	public void addDependsOnTrackings(Method m, String basepath, List<String> srcpath, String[] props) {
		if (srcpath != null) {
			final String src = BindELContext.pathToString(srcpath);
			if (_doneDependsOn.contains(src)) { //this method has already done @DependsOn in this binding
				return;
			}
			_doneDependsOn.add(src); //mark method as done @DependsOn
		}
		for(String prop : props) {
			BindELContext.addDependsOnTracking(m, basepath, srcpath, prop, this);
		}
	}
}
