/* BindListitemRenderer.java

	Purpose:
		
	Description:
		
	History:
		Aug 17, 2011 3:35:42 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.impl;

import org.zkoss.bind.IterationStatus;
import org.zkoss.lang.Objects;
import org.zkoss.xel.VariableResolverX;
import org.zkoss.xel.XelContext;
import org.zkoss.xel.XelException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Template;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

/**
 * Listitem renderer for binding.
 * @author henrichen
 *
 */
public class BindListitemRenderer implements ListitemRenderer {
	public void render(final Listitem item, final Object data) throws Exception {
		final Listbox listbox = (Listbox)item.getParent();
		final Template tm = listbox.getTemplate("model");
		if (tm == null) {
			item.setLabel(Objects.toString(data));
			item.setValue(data);
		} else {
			//will call into BindUiLifeCycle#afterComponentAttached, and apply binding management there
			final String varnm = (String) listbox.getAttribute(BinderImpl.VAR); //see BinderImpl#initRendererIfAny
			final String itervarnm = (String) listbox.getAttribute(BinderImpl.ITERATION_VAR); //see BinderImpl#initRendererIfAny
			final Component[] items = tm.create(listbox, item, 
				new VariableResolverX() {//this resolver is for EL ${} not for binding 
					public Object resolveVariable(String name) {
						//shall never call here
						return varnm.equals(name) ? data : null;
					}

					public Object resolveVariable(XelContext ctx, Object base, Object name) throws XelException {
						if (base == null) {
							if(varnm.equals(name)){
								return data;
							}else if(itervarnm.equals(name)){//iteration status
								return new IterationStatus(){
									@Override
									public int getIndex() {
										return Integer.valueOf(item.getIndex());
									}
								};
							}
						}
						return null;
					}
				}, null);
			if (items.length != 1)
				throw new UiException("The model template must have exactly one item, not "+items.length);

			final Listitem nli = (Listitem)items[0];
			nli.setAttribute(varnm, data); //kept the value
			
			nli.setAttribute(itervarnm, new IterationStatus(){//provide iteration status in this context
				@Override
				public int getIndex() {
					return Integer.valueOf(nli.getIndex());
				}
			});
			
			if (nli.getValue() == null) //template might set it
				nli.setValue(data);
			item.setAttribute("org.zkoss.zul.model.renderAs", nli);
				//indicate a new item is created to replace the existent one
			item.detach();
		}
	}
}
