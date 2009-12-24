/* _.java

	Purpose:
		
	Description:
		
	History:
		Wed Dec 16 10:47:51 TST 2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package _;

/**
 * It is not really a class but a collection of global functions and
 * variables.
 *
 * @author tomyeh
 */
public class _ {
	private _() {}
	/** Callback when the URL/bookmark of an iframe has been changed.
	 * This method is used to allow ZK to work with other technology.
	 * For example, if the iframe's content is not ZK, it can invoke
	 * the parent window's onIframeURLChange to notify ZK, if exist,
	 * as follows.
	 * <pre><code>
if (parent.onIframeURLChange)
	parent.onIframeURLChange(myid, myurl);
	 *</code></pre>
	 * <p>On the other hand, if the container is generated by other technology
	 * and you want to know if the URL of the ZK page inside an iframe
	 * is changed, you can implement this global function.
	 * Refer to <a href="http://docs.zkoss.org/wiki/Work_with_HTML_Tags#Integrate_with_Other_Technologies">here</a>
	 * for more information
	 * @param uuid the component UUID
	 * @param url the new URL
	 */
	public void onIframeURLChange(String uuid, String url);
}