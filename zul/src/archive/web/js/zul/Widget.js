/* zul.js

	Purpose:
		
	Description:
		
	History:
		Fri Nov  7 17:14:59     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The base class for XUL widget (org.zkoss.zul.impl.XulElement).
 */
zul.Widget = zk.$extends(zk.Widget, {
	/** Returns what keystrokes to intercept.
	 * <p>Default: null.
	 */
	getCtrlKeys: function () {
		return this._ctrlKeys;
	},
	/** Sets what keystrokes to intercept.
	 */
	setCtrlKeys: function (keys) {
		if (this._ctrlKeys != keys) {
			if (!keys) {
				this._ctrlKeys = this._parsedCtlKeys = null;
				return;
			}

		var parsed = ['', '', '', '', ''], which = 0; //ext(#), ctl, alt, shft
		for (var j = 0, len = keys.length; j < len; ++j) {
			var cc = keys.charAt(j); //ext
			switch (cc) {
			case '^':
			case '$':
			case '@':
				if (which)
					throw "Combination of Shift, Alt and Ctrl not supported: "+keys;
				which = cc == '^' ? 1: cc == '@' ? 2: 3;
				break;
			case '#':
				var k = j + 1;
				for (; k < len; ++k) {
					var c2 = keys.charAt(k);
					if ((c2 > 'Z' || c2 < 'A') 	&& (c2 > 'z' || c2 < 'a')
					&& (c2 > '9' || c2 < '0'))
						break;
				}
				if (k == j + 1)
					throw "Unexpected character "+cc+" in "+keys;

				var s = keys.substring(j+1, k).toLowerCase();
				if ("pgup" == s) cc = 'A';
				else if ("pgdn" == s) cc = 'B';
				else if ("end" == s) cc = 'C';
				else if ("home" == s) cc = 'D';
				else if ("left" == s) cc = 'E';
				else if ("up" == s) cc = 'F';
				else if ("right" == s) cc = 'G';
				else if ("down" == s) cc = 'H';
				else if ("ins" == s) cc = 'I';
				else if ("del" == s) cc = 'J';
				else if (s.length > 1 && s.charAt(0) == 'f') {
					var v = zk.parseInt(s.substring(1));
					if (v == 0 || v > 12)
						throw "Unsupported function key: #f" + v;
					cc = 'O'.increase(v); //'P': F1, 'Q': F2... 'Z': F12
				} else
					throw "Unknown #"+s+" in "+keys;

				parsed[which] += cc;
				which = 0;
				j = k - 1;
				break;
			default:
				if (!which || ((cc > 'Z' || cc < 'A') 
				&& (cc > 'z' || cc < 'a') && (cc > '9' || cc < '0')))
					throw "Unexpected character "+cc+" in "+keys;
				if (which == 3)
					throw "$a - $z not supported (found in "+keys+"). Allowed: $#f1, $#home and so on.";

				if (cc <= 'Z' && cc >= 'A')
					cc = cc.increase('a'.subtract('A')); //to lower case
				parsed[which] += cc;
				which = 0;
				break;
			}
		}

			this._parsedCtlKeys = parsed;
			this._ctrlKeys = keys;
		}
	}
});
