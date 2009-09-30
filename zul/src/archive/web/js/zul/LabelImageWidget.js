/* LabelImageWidget.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 14:59:07     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.LabelImageWidget = zk.$extends(zul.Widget, {
	_label: '',

	$define: {
		label: function () {
			this.updateDomContent_();
		},
		image: function (v) {
			var n = this.getImageNode();
			if (n) n.src = v || '';
		},
		hoverImage: null
	},

	updateDomContent_: function () {
		this.rerender();
	},
	domImage_: function () {
		var img = this._image;
		return img ? '<img src="' + img + '" align="absmiddle" />': '';
	},
	domLabel_: function () {
		return zUtl.encodeXML(this.getLabel());
	},
	/** A combination of image ([[#domImage_]]) and label ([[#domLabel_]]). */
	domContent_: function () {
		var label = this.domLabel_(),
			img = this.domImage_();
		return img ? label ? img + ' ' + label: img: label;
	},
	doMouseOver_: function () {
		var himg = this._hoverImage;
		if (himg) {
			var n = this.getImageNode();
			if (n) n.src = himg;
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function () {
		if (this._hoverImage) {
			var n = this.getImageNode();
			if (n) n.src = this._image;
		}
		this.$supers('doMouseOut_', arguments);
	},
	getImageNode: function () {
		if (!this._eimg && this._image) {
			var n = this.$n();
			if (n) this._eimg = jq(n).find('img:first')[0];
		}
		return this._eimg;
	},
	unbind_: function () {
		this._eimg = null;
		this.$supers('unbind_', arguments);
	}
});