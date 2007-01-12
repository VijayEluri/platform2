/*
 * $Id: FramedWindowClassViewNode.java,v 1.1.2.1 2007/01/12 19:32:12 idegaweb Exp $
 * Created on 22.10.2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.view;

import com.idega.idegaweb.IWMainApplication;


/**
 * 
 *  Last modified: $Date: 2007/01/12 19:32:12 $ by $Author: idegaweb $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.1.2.1 $
 */
public class FramedWindowClassViewNode extends FramedApplicationViewNode {

	private Class windowClass;
	
	/**
	 * @return Returns the windowClass.
	 */
	public Class getWindowClass() {
		return this.windowClass;
	}
	/**
	 * @param windowClass The windowClass to set.
	 */
	public void setWindowClass(Class windowClass) {
		this.windowClass = windowClass;
	}
	/**
	 * @param viewId
	 * @param parent
	 */
	public FramedWindowClassViewNode(String viewId, ViewNode parent) {
		super(viewId, parent);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param iwma
	 */
	public FramedWindowClassViewNode(IWMainApplication iwma) {
		super(iwma);
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * This method is recommended to call to get the window url becase of a potential servlet context prefix path.
	 * @param context
	 * @return
	 */
	public String getFrameUrl(){
		return super.getIWMainApplication().getWindowOpenerURI(getWindowClass());
	}
	
}
