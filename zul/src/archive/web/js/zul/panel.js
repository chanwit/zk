/* panel.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Tue Jun 17 10:44:03 TST 2008, Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zk.load("zul.zul"); //zul and msgzul

/**
 * A Panel component
 * @since 3.5.0
 */
zkPanel = {
	init: function (cmp) {
		zkPanel.onVisi = zkPanel.onSize;
		zkPanel._initBtn(cmp);
		if (zkPanel.isFloatable(cmp))
			zkPanel._initFloat(cmp);
		if (getZKAttr(cmp, "maximizable") == "true" && getZKAttr(cmp, "maximized") == "true")
			zk.addInitLater(function () {zkPanel.maximize(cmp, true, true);});
	},
	_initBtn: function (cmp) {
		if (getZKAttr(cmp, "closable") == "true") {
			var close = $e(cmp.id + "!close");
			if (close) {
				zk.listen(close, "click", this.onCloseClick);
				zk.listen(close, "mouseover", this.onCloseMouseover);
				zk.listen(close, "mouseout", this.onCloseMouseout);
				if (!close.style.cursor) close.style.cursor = "default";
			}
		}
		if (getZKAttr(cmp, "collapsible") == "true") {
			var exp = $e(cmp.id + "!exp");
			if (exp) {
				zk.listen(exp, "click", zkPanel.onOpen);
				zk.listen(exp, "mouseover", this.onToggleMouseover);
				zk.listen(exp, "mouseout", this.onToggleMouseout);
				if (!exp.style.cursor) exp.style.cursor = "default";
			}
		}
		if (getZKAttr(cmp, "maximizable") == "true") {
			var max = $e(cmp.id + "!maximize");
			if (max) {
				zk.listen(max, "click", zkPanel.onMaximize);
				zk.listen(max, "mouseover", this.onMaximizeMouseover);
				zk.listen(max, "mouseout", this.onMaximizeMouseout);
				if (!max.style.cursor) max.style.cursor = "default";
			}
		}
		if (getZKAttr(cmp, "minimizable") == "true") {
			var min = $e(cmp.id + "!minimize");
			if (min) {
				zk.listen(min, "click", zkPanel.onMinimize);
				zk.listen(min, "mouseover", this.onMinimizeMouseover);
				zk.listen(min, "mouseout", this.onMinimizeMouseout);
				if (!min.style.cursor) min.style.cursor = "default";
			}
		}
	},
	_initFloat: function (cmp) {
		if (!cmp.style.top && !cmp.style.left) {
			var xy = zk.revisedOffset(cmp);
			cmp.style.left = xy[0] + "px";
			cmp.style.top = xy[1] + "px";
		}
		zkau.fixZIndex(cmp);
		zkau._overlaps.push(cmp.id);
		cmp.style.position = "absolute";
		if (zkPanel.isMovable(cmp))
			zkPanel._initMove(cmp);
		zkPanel.initShadow(cmp);
		zkPanel._show(cmp);
	},
	_initMove: function (cmp) {
		var handle = $e(cmp.id + "!caption");
		if (handle) {
			handle.style.cursor = "move";
			cmp.style.position = "absolute"; //just in case
			zul.initMovable(cmp, {
				handle: handle, starteffect: zkPanel._startMove, stackup: true,
				change: zkau.hideCovered, ignoredrag: zkPanel._ignoreMove,
				endeffect: zkPanel._endMove});
		}
	},
	// Drag&Drop
	_ignoreMove: function (cmp, pointer, event) {
		var target = Event.element(event);
		if (!target || target.id.indexOf("!close") > -1
			|| target.id.indexOf("!exp") > -1 || target.id.indexOf("!minimize") > -1
			|| target.id.indexOf("!maximize") > -1) return true;
		return false;
	},
	_startMove: function (cmp, handle) {
		zkPanel.hideShadow(cmp);
		if(cmp.style.top && cmp.style.top.indexOf("%") >= 0)
			 cmp.style.top = cmp.offsetTop + "px";
		if(cmp.style.left && cmp.style.left.indexOf("%") >= 0)
			 cmp.style.left = cmp.offsetLeft + "px";
		zkau.closeFloats(cmp, handle);
	},
	/** Called back when overlapped and popup is moved. */
	_endMove: function (cmp, evt) {
		var keys = "";
		if (evt) {
			if (evt.altKey) keys += 'a';
			if (evt.ctrlKey) keys += 'c';
			if (evt.shiftKey) keys += 's';
		}
		zkPanel.syncShadow(cmp);
		zkau.sendOnMove(cmp, keys);
	},
	cleanup: function (cmp) {
		if (cmp._lastSize) {
			cmp._lastSize = null;
		}
		zkau._overlaps.remove(cmp.id);
		zkPanel.cleanupShadow(cmp);
	},
	isFloatable: function (cmp) {
		return getZKAttr(cmp, "floatable") == "true";
	},
	isMovable: function (cmp) {
		return getZKAttr(cmp, "movable") == "true";
	},
	isCollapsible: function (cmp) {
		return getZKAttr(cmp, "collapsible") == "true";
	},
	onCloseMouseover: function (evt) {
		if (!evt) evt = window.event;
		var btn = Event.element(evt),
			cmp = $parentByType(btn, "Panel");
		zk.addClass(btn, getZKAttr(cmp, "zcls") + "-close-over");
	},
	onCloseMouseout: function (evt) {
		if (!evt) evt = window.event;
		var btn = Event.element(evt),
			cmp = $parentByType(btn, "Panel");
		zk.rmClass(btn, getZKAttr(cmp, "zcls") + "-close-over");
	},
	onCloseClick: function (evt) {
		if (!evt) evt = window.event;
		zkau.sendOnClose($parentByType(Event.element(evt), "Panel"), true);
		Event.stop(evt);
	},
	onToggleMouseover: function (evt) {
		if (!evt) evt = window.event;
		var btn = Event.element(evt),
			cmp = $parentByType(btn, "Panel");
		zk.addClass(btn, getZKAttr(cmp, "zcls") + "-exp-over");
	},
	onToggleMouseout: function (evt) {
		if (!evt) evt = window.event;
		var btn = Event.element(evt),
			cmp = $parentByType(btn, "Panel");
		zk.rmClass(btn, getZKAttr(cmp, "zcls") + "-exp-over");
	},
	onMinimizeMouseover: function (evt) {
		if (!evt) evt = window.event;
		var btn = Event.element(evt),
			cmp = $parentByType(btn, "Panel");
		zk.addClass(btn, getZKAttr(cmp, "zcls") + "-min-over");
	},
	onMinimizeMouseout: function (evt) {
		if (!evt) evt = window.event;
		var btn = Event.element(evt),
			cmp = $parentByType(btn, "Panel");
		zk.rmClass(btn, getZKAttr(cmp, "zcls") + "-min-over");
	},
	onMaximizeMouseover: function (evt) {
		if (!evt) evt = window.event;
		var btn = Event.element(evt),
			cmp = $parentByType(btn, "Panel"),
			cls = getZKAttr(cmp, "zcls");
		if (getZKAttr(cmp, "maximized") == "true")
			zk.addClass(btn, cls + "-maxd-over");
		zk.addClass(btn, cls + "-max-over");
	},
	onMaximizeMouseout: function (evt) {
		if (!evt) evt = window.event;
		var btn = Event.element(evt),
			cmp = $parentByType(btn, "Panel"),
			cls = getZKAttr(cmp, "zcls");
		if (getZKAttr(cmp, "maximized") == "true")
			zk.rmClass(btn, cls + "-maxd-over");
		zk.rmClass(btn, cls + "-max-over");
	},
	onOpen: function (evt) {
		if (!evt) evt = window.event;

		var cmp = $parentByType(Event.element(evt), "Panel");
		if (cmp) {
			if (getZKAttr(cmp, "collapsible") != "true")
				return;

			var body = $e(cmp.id + "!body");
			if (body)
				zkPanel.open(cmp, !$visible(body), false, true);
		}
	},
	open: function (cmp, open, silent, ignorable) {
		var cmp = $e(cmp);
		if (cmp) {
			var body = $e(cmp.id + "!body");
			if (body && open != $visible(body)
			&& (!ignorable || !getZKAttr(body, "animating"))) {
				var cls = getZKAttr(cmp, "zcls");
				if (open) {
					zk.rmClass(cmp, cls + "-colpsd");
					anima.slideDown(body);
				} else {
					zk.addClass(cmp, cls + "-colpsd");
					zkPanel.hideShadow(cmp);

					// windows 2003 with IE6 will cause an error when user toggles the panel in portallayout.
					if (zk.ie6Only && !cmp.style.width)
						cmp.runtimeStyle.width = "100%";

					anima.slideUp(body);
				}

				if (!silent)
					zkau.sendasap({uuid: cmp.id, cmd: "onOpen", data: [open]});
					setZKAttr(cmp, "open", open ? "true" : "false");
				if (open)
					setTimeout(function(){
						zkPanel.hideShadow(cmp);
						zkPanel._fixHgh(cmp);
						zkPanel.syncShadow(cmp);
					}, 500); //after slide down
			}
		}
	},
	onMaximize: function (evt) {
		if (!evt) evt = window.event;

		var cmp = $parentByType(Event.element(evt), "Panel");
		if (cmp) {
			if (getZKAttr(cmp, "maximizable") != "true")
				return;

			zkPanel.maximize(cmp, getZKAttr(cmp, "maximized") != "true", false);
		}
	},
	maximize: function (cmp, maximized, silent) {
		var cmp = $e(cmp);
		if (cmp) {
			var isRealVisible = zk.isRealVisible(cmp);
			if (!isRealVisible && maximized) return;

			var l, t, w, h, s = cmp.style, cls = getZKAttr(cmp, "zcls");
			if (maximized) {
				zk.addClass($e(cmp.id + "!maximize"), cls + "-maxd");
				zkPanel.hideShadow(cmp);

				if (zkPanel.isCollapsible(cmp) && getZKAttr(cmp, "open") != "true") {
					zk.rmClass(cmp, cls + "-colpsd");
					var body = $e(cmp.id + "!body");
					if (body) body.style.display = "";
				}

				var floated = zkPanel.isFloatable(cmp), op = floated ? zPos.offsetParent(cmp) : cmp.parentNode;
				l = s.left;
				t = s.top;
				w = s.width;
				h = s.height;

				// prevent the scroll bar.
				s.top = "-10000px";
				s.left = "-10000px";

				// Sometimes, the clientWidth/Height in IE6 is wrong.
				var sw = zk.ie6Only && op.clientWidth == 0 ? (op.offsetWidth - zk.getBorderWidth(op)) : op.clientWidth,
					sh = zk.ie6Only && op.clientHeight == 0 ? (op.offsetHeight - zk.getBorderHeight(op)) : op.clientHeight;
				if (!floated) {
					sw -= zk.getPaddingWidth(op);
					sw = zk.revisedSize(cmp, sw);
					sh -= zk.getPaddingHeight(op);
					sh = zk.revisedSize(cmp, sh, true);
				}
				s.width = zk.px(sw);
				s.height = zk.px(sh);
				cmp._lastSize = {l:l, t:t, w:w, h:h};

				// restore.
				s.top = "0";
				s.left = "0";
			} else {
				var max = $e(cmp.id + "!maximize");
				zk.rmClass(max, cls + "-maxd");
				zk.rmClass(max, cls + "-maxd-over");
				if (cmp._lastSize) {
					s.left = cmp._lastSize.l;
					s.top = cmp._lastSize.t;
					s.width = cmp._lastSize.w;
					s.height = cmp._lastSize.h;
					cmp._lastSize = null;
				}
				l = s.left;
				t = s.top;
				w = s.width;
				h = s.height;
				if (zkPanel.isCollapsible(cmp) && getZKAttr(cmp, "open") != "true") {
					zk.addClass(cmp, cls + "-colpsd");
					var body = $e(cmp.id + "!body");
					if (body) body.style.display = "none";
				}
				var body = $e(getZKAttr(cmp, "children"));
				if (body)
					body.style.width = body.style.height = "";
			}

			setZKAttr(cmp, "maximized", maximized ? "true" : "false");
			if (!silent)
				zkau.sendasap({uuid: cmp.id, cmd: "onMaximize", data: [l, t, w, h, maximized]});

			if (isRealVisible) {
				cmp._maximized = true;
				zk.beforeSizeAt(cmp);
				zk.onSizeAt(cmp);
				zk.fire(cmp, "maximize", [cmp, maximized]);
			}
		}
	},
	onMinimize: function (evt) {
		if (!evt) evt = window.event;

		var cmp = $parentByType(Event.element(evt), "Panel");
		if (cmp) {
			if (getZKAttr(cmp, "minimizable") != "true")
				return;

			zkPanel.minimize(cmp, getZKAttr(cmp, "minimized") != "true", false, true);
		}
	},
	minimize: function (cmp, minimized, silent) {
		var cmp = $e(cmp);
		if (cmp) {
			var s = cmp.style, l = s.left, t = s.top, w = s.width, h = s.height;
			if (minimized) {
				zkPanel._hide(cmp);
			} else {
				zkPanel._show(cmp);
				zk.onSizeAt(cmp);
			}

			setZKAttr(cmp, "minimized", minimized ? "true" : "false");
			if (!silent)
				zkau.sendasap({uuid: cmp.id, cmd: "onMinimize", data: [l, t, w, h, minimized]});
		}
	},
	/** Fixed the content div's height. */
	onSize: function (cmp) {
		zkPanel.hideShadow(cmp);
		if (getZKAttr(cmp, "maximized") == "true") {
			if (!cmp._maximized)
				zkPanel.syncMaximized(cmp);
			cmp._maximized = false;
		}
		zkPanel._fixWdh(cmp);
		zkPanel._fixHgh(cmp);
		zkPanel.syncShadow(cmp);
		
		// it seems no longer to be fixed with this, commented by jumperchen on 2009/05/26 
		// if (zk.ie) zk.redoCSS(cmp); //Bug 2685842
	},
	syncMaximized: function (cmp) {
		if (!cmp._lastSize) return;
		var floated = zkPanel.isFloatable(cmp), op = floated ? zPos.offsetParent(cmp) : cmp.parentNode,
			s = cmp.style;

		// Sometimes, the clientWidth/Height in IE6 is wrong.
		var sw = zk.ie6Only && op.clientWidth == 0 ? (op.offsetWidth - zk.getBorderWidth(op)) : op.clientWidth;
		if (!floated) {
			sw -= zk.getPaddingWidth(op);
			sw = zk.revisedSize(cmp, sw);
		}
		s.width = zk.px(sw);
		if (getZKAttr(cmp, "open") == "true") {
			var sh = zk.ie6Only && op.clientHeight == 0 ? (op.offsetHeight - zk.getBorderHeight(op)) : op.clientHeight;
			if (!floated) {
				sh -= zk.getPaddingHeight(op);
				sh = zk.revisedSize(cmp, sh, true);
			}
			s.height = zk.px(sh);
		}
	},
	_fixWdh: zk.ie7 ? function (cmp) {
		if (getZKAttr(cmp, "framable") != "true" || !zk.isRealVisible(cmp)) return;
		var body = $e(getZKAttr(cmp, "children"));
		if (!body) return;
		var wdh = cmp.style.width,
			tl = zk.firstChild(cmp, "DIV"),
			hl = $e(cmp, "caption") ? zk.nextSibling(tl, "DIV") : null,
			bl = zk.lastChild(zk.lastChild(cmp, "DIV"), "DIV"),
			bb = $e(cmp, "bb"),
			fl = $e(cmp, "fb") ? zk.previousSibling(bl, "DIV"): null,
			n = body.parentNode;
		if (!wdh || wdh == "auto") {
			var diff = zk.getPadBorderWidth(n.parentNode) + zk.getPadBorderWidth(n.parentNode.parentNode);
			if (tl) {
				tl.firstChild.style.width = zk.px(n.offsetWidth + diff);
			}
			if (hl) {
				hl.firstChild.firstChild.style.width = zk.px(n.offsetWidth - (zk.getPadBorderWidth(hl)
					+ zk.getPadBorderWidth(hl.firstChild) - diff));
			}
			if (bb) bb.style.width = zk.revisedSize(bb, body.offsetWidth);
			if (fl) {
				fl.firstChild.firstChild.style.width = zk.px(n.offsetWidth - (zk.getPadBorderWidth(fl)
					+ zk.getPadBorderWidth(fl.firstChild) - diff));
			}
			if (bl) {
				bl.firstChild.style.width = zk.px(n.offsetWidth + diff);
			}
		} else {
			if (tl) tl.firstChild.style.width = "";
			if (hl) hl.firstChild.style.width = "";
			if (bb) bb.style.width = "";
			if (fl) fl.firstChild.style.width = "";
			if (bl) bl.firstChild.style.width = "";
		}
	} : zk.voidf,
	_fixHgh: function (cmp) {
		if (!zk.isRealVisible(cmp)) return;
		var body = $e(getZKAttr(cmp, "children"));
		if (!body) return;
		var hgh = cmp.style.height;
		if (zk.ie6Only && ((hgh && hgh != "auto" )|| body.style.height)) body.style.height = "0px";
		if (hgh && hgh != "auto")
			zk.setOffsetHeight(body, zkPanel.getOffsetHeight(cmp));
		if (zk.ie6Only) zk.redoCSS(body);
	},
	getOffsetHeight: function (cmp) {
		var h = cmp.offsetHeight - 1;
		h -= zkPanel.getTitleHeight(cmp);
		if (getZKAttr(cmp, "framable") == "true") {
			var body = $e(getZKAttr(cmp, "children")),
				bl = zk.lastChild($e(cmp.id + "!body"), "DIV"),
				title = $e(cmp.id + "!caption");
			h -= bl.offsetHeight;
			if (body)
				h -= zk.getPadBorderHeight(body.parentNode);
			if (title)
				h -= zk.getPadBorderHeight(title.parentNode);
		}
		h -= zk.getPadBorderHeight(cmp);
		var tb = $e(cmp.id + "!tb"),
			bb = $e(cmp.id + "!bb"),
			fb = $e(cmp.id + "!fb");
		if (tb) h -= tb.offsetHeight;
		if (bb) h -= bb.offsetHeight;
		if (fb) h -= fb.offsetHeight;

		return h;
	},
	getTitleHeight: function (cmp) {
		var title = $e(cmp.id + "!caption"), top = 0;
		if (getZKAttr(cmp, "framable") == "true")
			top = zk.firstChild(cmp, "DIV").offsetHeight;
		return title ? title.offsetHeight + top : top;
	},
	_show: function (cmp) {
		if (getZKAttr(cmp, "visible") == "true")
			zk.cleanVisibility(cmp);
		zk.show(cmp);
		zkPanel.syncShadow(cmp);
	},
	_hide: function (cmp) {
		zkPanel.hideShadow(cmp);
		zk.hide(cmp);
	},
	// Panel Shadow
	/**
	 * Returns the shadow instance of the specified component.
	 */
	getShadow: function (cmp) {
		return cmp._shadow;
	},
	hideShadow: function (cmp) {
		if (zkPanel.getShadow(cmp)) zkPanel.getShadow(cmp).hide();
	},
	/**
	 * Initializes the shadow object for the specified component.
	 */
	initShadow: function (cmp) {
		cmp._shadow = new zk.Shadow(cmp, {left: -4, right: 4, top: -2, bottom: 3});
	},
	/**
	 * Clean the shadow object for the specified component.
	 */
	cleanupShadow: function (cmp) {
		if (cmp._shadow) {
			cmp._shadow.cleanup();
			cmp._shadow = null;
		}
	},
	/**
	 * Sync the region of the shadow from the specified component.
	 */
	syncShadow: function (cmp) {
		if (!zkPanel.isFloatable(cmp) || !zkPanel.getShadow(cmp)) return;
		if (getZKAttr(cmp, "maximized") == "true" || getZKAttr(cmp, "minimized") == "true") {
			zkPanel.hideShadow(cmp);
			return;
		}
		var body = $e(cmp.id + "!body");
		if (body && $visible(body)) // if only title, nothing to do.
			zkPanel.getShadow(cmp).sync();
	},
	setAttr: function (cmp, nm, val) {
		switch (nm) {
			case "visibility":
				if (getZKAttr(cmp, "maximized") == "true") {
					zkPanel.maximize(cmp, false);
				} else if (getZKAttr(cmp, "minimized") == "true") {
					zkPanel.minimize(cmp, false);
				}
				zkau.setAttr(cmp, nm, val);
				return true;
			case "z.open":
				zkPanel.open(cmp, val == "true", true);
				return true; //no need to store z.open
			case "z.maximized":
				zkau.setAttr(cmp, nm, val);
				if (getZKAttr(cmp, "minimized") == "true") {
					zkPanel.minimize(cmp, false);
				}
				zkPanel.maximize(cmp, val == "true", true);
				return true;
			case "z.minimized":
				zkau.setAttr(cmp, nm, val);
				if (getZKAttr(cmp, "maximized") == "true") {
					zkPanel.maximize(cmp, false);
				}
				zkPanel.minimize(cmp, val == "true", true);
				return true;
			case "style":
			case "style.height":
				zkPanel.hideShadow(cmp);
				zkau.setAttr(cmp, nm, val);
				if (nm == "style.height") {
					zk.beforeSizeAt(cmp);
					zk.onSizeAt(cmp); // Note: IE6 is broken, because its offsetHeight doesn't update.
				} else {
					zkPanel._fixHgh(cmp);
					zkPanel.syncShadow(cmp);
				}
				return true;
			case "style.width":
				zkau.setAttr(cmp, nm, val);
				zk.beforeSizeAt(cmp);
				zk.onSizeAt(cmp);
				return true;
			case "style.top":
			case "style.left":
				zkPanel.hideShadow(cmp);
				if (nm == "style.top") {
					cmp.style.top = val;
				} else {
					cmp.style.left = val;
				}
				zkPanel.syncShadow(cmp);
				return true;
			}
		return false;
	}
};
/**
 * A Panelchildren component.
 * @since 3.5.0
 */
zkPanelchild = {
	setAttr: function (cmp, nm, val) {
		switch (nm) {
			case "style":
				var panel = $parentByType(cmp, "Panel");
				zkPanel.hideShadow(panel);
				zkau.setAttr(cmp, nm, val);
				zkPanel._fixHgh(panel);
				zkPanel.syncShadow(panel);
				return true;
			}
		return false;
	}
};