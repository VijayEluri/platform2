/*
 * $Id: URIManager.java,v 1.1 2005/01/18 17:11:31 tryggvil Exp $
 * Created on 18.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;


/**
 * 
 *  Last modified: $Date: 2005/01/18 17:11:31 $ by $Author: tryggvil $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1 $
 */
public class URIManager {
	
	//static variables:
	private static URIManager instance;
	
	//instance variables:
	private URIHandler defaultHandler;
	
	public static URIManager getInstance(){
		if(instance==null){
			instance=new URIManager();
		}
		return instance;
	}
	private URIManager(){}
	
	private Map handlerMap;
	
	protected Map getHandlerMap() {
		return handlerMap;
	}
	protected void setHandlerMap(Map handlerMap) {
		this.handlerMap = handlerMap;
	}
	/**
	 * @param scheme is the first part of the URI (e.g. http,mailto or file)
	 * @param handler
	 */
	public void registerHandler(String scheme,URIHandler handler){
		getHandlerMap().put(scheme,handler);
	}
	
	public URIHandler getHandler(String sUri){
		URI uri;
		try {
			uri = new URI(sUri);
			String scheme = uri.getScheme();
			
			URIHandler handler = (URIHandler) getHandlerMap().get(scheme);
			if(handler!=null){
				return handler;
			}
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		//fallback to default handler>
		return getDefaultHandler();
	}
	
	public URIHandler getDefaultHandler(){
		if(defaultHandler==null){
			defaultHandler=new DefaultURIHandler();
		}
		return defaultHandler;
	}
	
}