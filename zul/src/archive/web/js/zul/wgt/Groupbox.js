/* Groupbox.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 12:39:24     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Groups a set of child elements to have a visual effect.
 * <p>Default {@link #getZclass}: "z-fieldset". If {@link #getMold()} is 3d,
 * "z-groupbox" is assumed.
 *
 * <p>Events: onOpen.
 *
 */
zul.wgt.Groupbox = zk.$extends(zul.Widget, {
	_open: true,
	_closable: true,

	$define: { //zk.def
		/** Returns whether this groupbox is open.
		 * <p>Default: true.
		 * @return boolean
		 */
		/** Opens or closes this groupbox.
		 * @param boolean open
		 */
		open: function (open, fromServer) {
			var node = this.$n();
			if (node && this._closable) {
				if (this.isLegend()) { //legend
					if (!open) zWatch.fireDown('onHide', this);
					jq(node)[open ? 'removeClass': 'addClass'](this.getZclass() + "-colpsd");
					if (zk.ie6_) // Bug Z35-groupbox-002.zul
						zk(this).redoCSS();
					if (open) zWatch.fireDown('onShow', this);
				} else {
					zk(this.getCaveNode())[open?'slideDown':'slideUp'](this);
				}
				if (!fromServer) this.fire('onOpen', {open:open});
			}
		},
		/** Returns whether user can open or close the group box.
		 * In other words, if false, users are no longer allowed to
		 * change the open status (by clicking on the title).
		 *
		 * <p>Default: true.
		 * @return boolean
		 */
		/** Sets whether user can open or close the group box.
		 * @param boolean closable
		 */
		closable: _zkf = function () {
			this._updDomOuter();
		},
		/** Returns the CSS style for the content block of the groupbox.
		 * Used only if {@link #getMold} is not default.
		 * @return String
		 */
		/** Sets the CSS style for the content block of the groupbox.
		 * Used only if {@link #getMold} is not default.
		 *
		 * <p>Default: null.
		 * @param String contentStyle
		 */
		contentStyle: _zkf,
		/** Returns the style class used for the content block of the groupbox.
		 * Used only if {@link #getMold} is not default.
		 * @return String
		 */
		/** Sets the style class used for the content block.
		 * @param String contentSclass
		 */
		contentSclass: _zkf
	},
	/** Returns whether this groupbox is in the legend mold.
	 * By the legend mold we mean this group box is rendered with
	 * HTML FIELDSET tag.
	 *
	 * <p>Default: the legend mold is assumed if {@link #getMold}
	 * returns "default".
	 *
	 * @return boolean
	 */
	isLegend: function () {
		return this._mold == 'default';
	},

	_updDomOuter: function () {
		this.rerender(zk.Skipper.nonCaptionSkipper);
	},
	_contentAttrs: function () {
		var html = ' class="', s = this._contentSclass;
		if (s) html += s + ' ';
		html += this.getZclass() + '-cnt"';

		s = this._contentStyle;
		if (!this.isLegend()) {
			if (this.caption) s = 'border-top:0;' + (s||'');
			if (!this._open) s = 'display:none;' + (s||'');
		}
		if (s) html += ' style="' + s + '"';
		return html;
	},
	_redrawCave: function (out, skipper) { //reserve for customizing
		out.push('<div id="', this.uuid, '-cave"', this._contentAttrs(), '>');
	
		if (!skipper)
			for (var w = this.firstChild, cap = this.caption; w; w = w.nextSibling)
				if (w != cap)
					w.redraw(out);

		out.push('</div>');
	},

	setHeight: function () {
		this.$supers('setHeight', arguments);
		if (this.desktop) this._fixHgh();
	},
	_fixWdh: function () {
		var wdh = this.$n().style.width;
		if (wdh && wdh.indexOf('px') >= 0) {
			var n;
			if (n = this.$n('cave')) {
				n.style.width = wdh;
			}
		}
	},
	_fixHgh: function () {
		var hgh = this.$n().style.height;
		if (hgh && hgh != "auto") {
			var n;
			if (n = this.$n('cave')) {
				if (zk.ie6_) n.style.height = "";
				var wgt = this,
					$n = zk(n),
					fix = function() {
					n.style.height = wgt.isLegend()? hgh:
						$n.revisedHeight($n.vflexHeight(), true)
						+ "px";
				};
				fix();
				if (zk.gecko) setTimeout(fix, 0);
					//Gecko bug: height is wrong if the browser visits the page first time
					//(reload won't reproduce the problem) test case: test/z5.zul
			}
		}
	},
	getParentSize_: zk.ie6_ ? function (p) {
		var zkp = zk(p),
			hgh,
			wdh,
			s = p.style;
		if (s.width.indexOf('px') >= 0) {
			wdh = zk.parseInt(s.width);
		} else {
			// Fixed for B50-3357573.zul
			var n = this.$n(),
				oldVal = n.style.display;
			n.style.display = 'none';
			wdh = zkp.revisedWidth(p.offsetWidth);
			n.style.display = oldVal;
		}
		if (s.height.indexOf('px') >= 0) {
			hgh = zk.parseInt(s.height);
		}
		return {height: hgh || zkp.revisedHeight(p.offsetHeight),
					width: wdh || zkp.revisedWidth(p.offsetWidth)};
	} : function(p) {
		return this.$supers('getParentSize_', arguments);
	},
	//watch//
	onSize: _zkf = function () {
		this._fixHgh();
		if (!this.isLegend()) {
			setTimeout(this.proxy(this._fixShadow), 500);
			//shadow rarely needs to fix so OK to delay for better performance
		} else {
			this._fixWdh();
		} 
	},
	onShow: _zkf,
	_fixShadow: function () {
		var sdw = this.$n('sdw');
		if (sdw)
			sdw.style.display =
				zk.parseInt(jq(this.$n('cave')).css("border-bottom-width")) ? "": "none";
				//if no border-bottom, hide the shadow
	},
	updateDomStyle_: function () {
		this.$supers('updateDomStyle_', arguments);
		if (this.desktop) this.onSize();
	},

	//super//
	focus_: function (timeout) {
		var cap = this.caption;
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w != cap && w.focus_(timeout))
				return true;
		return cap && cap.focus_(timeout);
	},
	getZclass: function () {
		var zcls = this._zclass;
		return zcls ? zcls: this.isLegend() ? "z-fieldset": "z-groupbox";
	},
	bind_: function () {
		this.$supers(zul.wgt.Groupbox, 'bind_', arguments);
		// Bug: B50-3205292: vflex with legend Groupbox
		//if (!this.isLegend())
		zWatch.listen({onSize: this, onShow: this});
	},
	unbind_: function () {
		// Bug: B50-3205292: vflex with legend Groupbox
		//if (!this.isLegend())
		zWatch.unlisten({onSize: this, onShow: this});
		this.$supers(zul.wgt.Groupbox, 'unbind_', arguments);
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.wgt.Caption))
			this.caption = child;
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.caption)
			this.caption = null;
	},

	domClass_: function () {
		var html = this.$supers('domClass_', arguments);
		if (!this._open) {
			if (html) html += ' ';
			html += this.getZclass() + '-colpsd';
		}
		return html;
	}
});
