/*
 * Copyright 2012 Alex Usachev, thothbot@gmail.com
 * 
 * This file is part of Parallax project.
 * 
 * Parallax is free software: you can redistribute it and/or modify it 
 * under the terms of the Creative Commons Attribution 3.0 Unported License.
 * 
 * Parallax is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the Creative Commons Attribution 
 * 3.0 Unported License. for more details.
 * 
 * You should have received a copy of the the Creative Commons Attribution 
 * 3.0 Unported License along with Parallax. 
 * If not, see http://creativecommons.org/licenses/by/3.0/.
 */

package org.parallax3d.parallax.demo.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Window;

public class ShareFaceBook 
{

	public static void prepareFBShareButton(String widgetURL) 
	{	
		Document.get().getElementById("fb-share-button").setAttribute("data-href", getCurrentPageUrl() + "demo/fb/" + widgetURL + ".html");
		
		// Parse only on production
		if(GWT.isProdMode()) FBParse();
	}

	private static String getCurrentPageUrl() {
        String regex = "/([^/]+)$";
        return Window.Location.getHref().replaceFirst(regex, "") + "/";
	}
		
	private static native void FBParse() /*-{
		if($wnd.FB)
			$wnd.FB.XFBML.parse();
	}-*/;
}
