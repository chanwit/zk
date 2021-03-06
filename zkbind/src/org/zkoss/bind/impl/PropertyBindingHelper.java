/* PropertyBindingHelper.java

	Purpose:
		
	Description:
		
	History:
		2011/11/14 Created by Dennis Chen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Binder;
import org.zkoss.bind.Phase;
import org.zkoss.bind.Property;
import org.zkoss.bind.ValidationContext;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.LoadPropertyBinding;
import org.zkoss.bind.sys.SavePropertyBinding;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;

/**
 * to help property-binding implementation of  BinderImpl 
 * @author dennis
 *
 */
/*package*/ class PropertyBindingHelper extends AbstractBindingHelper{
	private static final long serialVersionUID = 1L;

	private static final LogUtil log = new LogUtil(PropertyBindingHelper.class.getName());
	
	private final Map<String, List<LoadPropertyBinding>> _loadPromptBindings; //comp+_fieldExpr -> bindings (load _prompt | load on property change)
	private final Map<String, List<LoadPropertyBinding>> _loadEventBindings; //comp+evtnm -> bindings (load on event)
	private final Map<String, List<SavePropertyBinding>> _saveEventBindings; //comp+evtnm -> bindings (save on event)
	private final Map<String, List<LoadPropertyBinding>> _loadAfterBindings; //command -> bindings (load after command)
	private final Map<String, List<SavePropertyBinding>> _saveAfterBindings; //command -> bindings (save after command)
	private final Map<String, List<LoadPropertyBinding>> _loadBeforeBindings; //command -> bindings (load before command)
	private final Map<String, List<SavePropertyBinding>> _saveBeforeBindings; //command -> bindings (save before command)
	
	
	PropertyBindingHelper(BinderImpl binder) {
		super(binder);
		_loadPromptBindings = new HashMap<String, List<LoadPropertyBinding>>();
		_loadEventBindings = new HashMap<String, List<LoadPropertyBinding>>();
		_saveEventBindings = new HashMap<String, List<SavePropertyBinding>>();
		_loadAfterBindings = new HashMap<String, List<LoadPropertyBinding>>();
		_saveAfterBindings = new HashMap<String, List<SavePropertyBinding>>();
		_loadBeforeBindings = new HashMap<String, List<LoadPropertyBinding>>();
		_saveBeforeBindings = new HashMap<String, List<SavePropertyBinding>>();
	}
	
	void addLoadEventBinding(Component comp, String bindDualId, LoadPropertyBinding binding) {
		List<LoadPropertyBinding> bindings = _loadEventBindings.get(bindDualId); 
		if (bindings == null) {
			bindings = new ArrayList<LoadPropertyBinding>();
			_loadEventBindings.put(bindDualId, bindings);
		}
		bindings.add(binding);
	}
	void addLoadPromptBinding(Component comp, String bindDualId, LoadPropertyBinding binding) {
		List<LoadPropertyBinding> bindings = _loadPromptBindings.get(bindDualId); 
		if (bindings == null) {
			bindings = new ArrayList<LoadPropertyBinding>();
			_loadPromptBindings.put(bindDualId, bindings);
		}
		bindings.add(binding);
	}
	
	void addLoadBeforeBinding(String command, LoadPropertyBinding binding) {
		List<LoadPropertyBinding> bindings = _loadBeforeBindings.get(command);
		if (bindings == null) {
			bindings = new ArrayList<LoadPropertyBinding>();
			_loadBeforeBindings.put(command, bindings);
		}
		bindings.add(binding);
	}
	
	void addLoadAfterBinding(String command, LoadPropertyBinding binding) {
		List<LoadPropertyBinding> bindings = _loadAfterBindings.get(command);
		if (bindings == null) {
			bindings = new ArrayList<LoadPropertyBinding>();
			_loadAfterBindings.put(command, bindings);
		}
		bindings.add(binding);
	}

	void addSavePromptBinding(Component comp, String bindDualId, SavePropertyBinding binding) {
		List<SavePropertyBinding> bindings = _saveEventBindings.get(bindDualId); 
		if (bindings == null) {
			bindings = new ArrayList<SavePropertyBinding>();
			_saveEventBindings.put(bindDualId, bindings);
		}
		bindings.add(binding);
	}
	
	void addSaveBeforeBinding(String command, SavePropertyBinding binding) {
		List<SavePropertyBinding> bindings = _saveBeforeBindings.get(command);
		if (bindings == null) {
			bindings = new ArrayList<SavePropertyBinding>();
			_saveBeforeBindings.put(command, bindings);
		}
		bindings.add(binding);
	}
	
	void addSaveAfterBinding(String command, SavePropertyBinding binding) {
		List<SavePropertyBinding> bindings = _saveAfterBindings.get(command);
		if (bindings == null) {
			bindings = new ArrayList<SavePropertyBinding>();
			_saveAfterBindings.put(command, bindings);
		}
		bindings.add(binding);
	}
	
	//generic operation to save a property binding
	private void doSavePropertyBinding(Component comp, SavePropertyBinding binding, String command, Event evt, Set<Property> notifys) {
		final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, true, command, binding.getComponent(), evt);
		BindContextUtil.setConverterArgs(_binder, binding.getComponent(), ctx, binding);
		BindContextUtil.setValidatorArgs(_binder, binding.getComponent(), ctx, binding);
		try {
			log.debug("doSavePropertyBinding:binding.save() comp=[%s],binding=[%s],command=[%s],evt=[%s],notifys=[%s]",comp,binding,command,evt,notifys);
			doPrePhase(Phase.SAVE_BINDING, ctx);
			binding.save(ctx);
		} finally {
			doPostPhase(Phase.SAVE_BINDING, ctx);
		}
		
		final Set<Property> xnotifys = getNotifys(ctx);
		if (xnotifys != null) {
			notifys.addAll(xnotifys);
		}
	}
	
	//generic operation to load a property binding
	private void doLoadPropertyBinding(Component comp, LoadPropertyBinding binding, String command) {
		final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, false, command, binding.getComponent(), null);
		BindContextUtil.setConverterArgs(_binder, binding.getComponent(), ctx, binding);
		try { 
			log.debug("doLoadPropertyBinding:binding.load(),component=[%s],binding=[%s],context=[%s],command=[%s]",comp,binding,ctx,command);
			doPrePhase(Phase.LOAD_BINDING, ctx);
			binding.load(ctx);
		} finally {
			doPostPhase(Phase.LOAD_BINDING, ctx);
		}
	}
	
	//for event -> prompt only, no command
	void doLoadEvent(String bindDualId,Component comp, String evtnm) {
		final List<LoadPropertyBinding> bindings = _loadEventBindings.get(bindDualId);
		if (bindings != null) {
			for (LoadPropertyBinding binding : bindings) {
				doLoadPropertyBinding(comp, binding, null);
			}
		}
	}

	//for event -> prompt only, no command 
	void doSaveEventNoValidate(String bindDualId,Component comp, Event evt, Set<Property> notifys) {
		final List<SavePropertyBinding> bindings = _saveEventBindings.get(bindDualId);
		if (bindings != null) {
			for (SavePropertyBinding binding : bindings) {
				doSavePropertyBinding(comp, binding, null, evt, notifys);
			}
		}
	}
	
	boolean doSaveEvent(String bindDualId,Component comp, Event evt, Set<Property> notifys) {
		final List<SavePropertyBinding> bindings = _saveEventBindings.get(bindDualId);
		if (bindings != null) {
			//TODO, DENNIS, ISSUE, What is the Spec of validation of prompt save?, check comment of onEvent also
			for (SavePropertyBinding binding : bindings) {
				final boolean success = doValidateSaveEvent(comp, binding, evt,notifys);
				if (!success) { //failed validation
					return false;
				}
				doSavePropertyBinding(comp, binding, null, evt, notifys);
			}
		}
		return true;
	}	
	
	//validate a prompt save property binding
	private boolean doValidateSaveEvent(Component comp, SavePropertyBinding binding, Event evt, Set<Property> notifys) {
		//for a single binding, if it doesn't need to do validation, then we don't need to anything.
		if (binding.hasValidator()) {
			final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, true, null, binding.getComponent(), evt);
			BindContextUtil.setConverterArgs(_binder, binding.getComponent(), ctx, binding);
			BindContextUtil.setValidatorArgs(_binder, binding.getComponent(), ctx, binding);
			
			try {
				doPrePhase(Phase.VALIDATE, ctx);
				final Property p = binding.getValidate(ctx); 
				log.debug("doValidateSaveEvent comp=[%s],binding=[%s],evt=[%s],validate=[%s]",comp,binding,evt,p);
				if(p==null){
					throw new UiException("no main property for save-binding "+binding);
				}
				ValidationContext vctx = new ValidationContextImpl(null, p, toCollectedProperties(p), ctx, true);
				binding.validate(vctx);
				boolean valid = vctx.isValid();
				log.debug("doValidateSaveEvent result=[%s]",valid);
				
				
				final Set<Property> xnotifys = getNotifys(ctx);
				if (xnotifys != null) {
					notifys.addAll(xnotifys);
				}
				
				return valid;
			} catch (Exception e) {
				throw UiException.Aide.wrap(e);
			} finally {
				doPostPhase(Phase.VALIDATE, ctx);
			}
		}
		return true;
	}
	
	

	Map<String,Property[]> toCollectedProperties(Property validate) {
		Set<Property> cp = new HashSet<Property>();
		cp.add(validate);
		return toCollectedProperties(cp);
	}
	
	Map<String,Property[]> toCollectedProperties(Set<Property> validates) {
		if(validates==null || validates.size()==0) return Collections.emptyMap();
		Map<String,List<Property>> temp = new HashMap<String,List<Property>>(validates.size());
		
		for(Property p:validates){
			List<Property> l = temp.get(p.getProperty());
			if(l==null){
				l = new ArrayList<Property>();
				temp.put(p.getProperty(), l);
			}
			l.add(p);
		}
		
		Map<String,Property[]> collected = new HashMap<String,Property[]>(temp.size());
		for(Entry<String,List<Property>> e : temp.entrySet()){
			collected.put(e.getKey(), e.getValue().toArray(new Property[e.getValue().size()]));
		}
		return collected;
	}
	

	Map<String, List<SavePropertyBinding>> getSaveAfterBindings() {
		return _saveAfterBindings;
	}

	Map<String, List<SavePropertyBinding>> getSaveBeforeBindings() {
		return _saveBeforeBindings;
	}

	Map<String, List<SavePropertyBinding>> getSaveEventBindings() {
		return _saveEventBindings;
	}
	
	//doCommand -> doSaveBefore -> doSavePropertyBefore
	void doSavePropertyBefore(Component comp, String command, Event evt, Set<Property> notifys) {
		final List<SavePropertyBinding> bindings = _saveBeforeBindings.get(command);
		if (bindings != null) {
			for (SavePropertyBinding binding : bindings) {
				doSavePropertyBinding(comp, binding, command, evt, notifys);
			}
		}
	}
	
	void doSavePropertyAfter(Component comp, String command, Event evt, Set<Property> notifys) {
//		final BindEvaluatorX eval = getEvaluatorX(); 
		final List<SavePropertyBinding> bindings = _saveAfterBindings.get(command);
		if (bindings != null) {
			for (SavePropertyBinding binding : bindings) {
				doSavePropertyBinding(comp, binding, command, evt, notifys);
			}
		}
	}
	
	void doLoadPropertyBefore(Component comp, String command) {
		final List<LoadPropertyBinding> bindings = _loadBeforeBindings.get(command);
		if (bindings != null) {
			for (LoadPropertyBinding binding : bindings) {
				doLoadPropertyBinding(comp, binding, command);
			}
		}
	}
	
	void doLoadPropertyAfter(Component comp, String command) {
		final List<LoadPropertyBinding> bindings = _loadAfterBindings.get(command);
		if (bindings != null) {
			for (LoadPropertyBinding binding : bindings) {
				doLoadPropertyBinding(comp, binding, command);
			}
		}
	}
	
	void removeBindings( String bindDualId, Set<Binding> removed) {
		List<? extends Binding> bindingx;
		if((bindingx = _loadPromptBindings.remove(bindDualId)) !=null){
			removed.addAll(bindingx); //comp+_fieldExpr -> bindings (load _prompt)
		}
		if((bindingx = _loadEventBindings.remove(bindDualId)) !=null){
			removed.addAll(bindingx); //comp+evtnm -> bindings (load on event)
		}
		if((bindingx = _saveEventBindings.remove(bindDualId)) !=null){
			removed.addAll(bindingx); //comp+evtnm -> bindings (save on event)
		}
	}

	void removeBindings(Collection<Binding> removes) {
		_loadAfterBindings.values().removeAll(removes); //command -> bindings (load after command)
		_saveAfterBindings.values().removeAll(removes); //command -> bindings (save after command)
		_loadBeforeBindings.values().removeAll(removes); //command -> bindings (load before command)
		_saveBeforeBindings.values().removeAll(removes); //command -> bindings (save before command)
	}

	void loadComponentProperties(Component comp, String bindDualId) {
		final List<LoadPropertyBinding> propBindings = _loadPromptBindings.get(bindDualId);
		if (propBindings != null) {
			for (LoadPropertyBinding binding : propBindings) {
				final BindContext ctx = BindContextUtil.newBindContext(_binder, binding, false, null, comp, null);
				BindContextUtil.setConverterArgs(_binder, binding.getComponent(), ctx, binding);
				log.debug("loadComponentProperties:binding.load(),component=[%s],binding=[%s],context=[%s]",comp,binding,ctx);
				binding.load(ctx);
			}
		}
	}
	
}
