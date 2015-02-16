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

package thothbot.parallax.demo.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.MetaElement;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

public class ShareFaceBook 
{

	public static void prepareFBShareButton(String title, HTML description, String imageUrl) 
	{
		NodeList<Element> tags = Document.get().getElementsByTagName("meta");
		
		updateOrAddMetaTag( tags, "og:type", "website" );
		updateOrAddMetaTag( tags, "og:site_name", "Parallax 3D library" );
		updateOrAddMetaTag( tags, "og:title", title );
		updateOrAddMetaTag( tags, "og:description", removeHtmlTags(description) );
		updateOrAddMetaTag( tags, "og:image", getCurrentPageUrl() + imageUrl );
		updateOrAddMetaTag( tags, "og:url", Window.Location.getHref() );

		Document.get().getElementById("fb-share-button").setAttribute("data-href", Window.Location.getHref());
		
		// Parse only on production
		if(GWT.isProdMode()) FBParse();
	}

	private static String getCurrentPageUrl() {
        String regex = "/([^/]+)$";
        return Window.Location.getHref().replaceFirst(regex, "") + "/";
	}
	
	private static String removeHtmlTags(HTML html) {
        String regex = "(<([^>]+)>)";
        return html.toString().replaceAll(regex, "");
	}

	private static void updateOrAddMetaTag(NodeList<Element> tags, String property, String content) 
	{
		boolean updated = false;
		// Update
		for (int i = 0; i < tags.getLength(); i++) 
		{
	        MetaElement metaTag = ((MetaElement) tags.getItem(i));
			if(metaTag.getAttribute("property").equals(property)) 
			{
				metaTag.setContent(content);
				updated = true;
				break;
			}
		}

		// Add new
		if(!updated) 
		{
			Element head = Document.get().getElementsByTagName("head").getItem(0);
			
			MetaElement newMeta = Document.get().createMetaElement();
			newMeta.setAttribute("property", property);
			newMeta.setContent(content);
			head.appendChild(newMeta);
		}
	}
	
	private static native void FBParse() /*-{
		if($wnd.FB)
			$wnd.FB.XFBML.parse();
	}-*/;
}
