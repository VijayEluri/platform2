/*
 * $Id: IBPageHandler.java,v 1.6 2003/04/03 09:10:10 laddi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.handler;

import java.util.List;
import java.util.Map;

import com.idega.builder.business.PageTreeNode;
import com.idega.builder.presentation.IBPageChooser;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IBPageHandler implements PropertyHandler {
	public IBPageHandler() {
	}

	public List getDefaultHandlerTypes() {
		return (null);
	}

	public PresentationObject getHandlerObject(String name, String value, IWContext iwc) {
		IBPageChooser chooser = new IBPageChooser(name);
		try {
			if (value != null && !value.equals("")) {
				Map tree = PageTreeNode.getTree(iwc);
				if (tree != null) {
					PageTreeNode node = (PageTreeNode) tree.get(Integer.valueOf(value));
					if (node != null)
						chooser.setSelectedPage(node.getNodeID(), node.getNodeName());
				}
			}
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return (chooser);
	}

	public void onUpdate(String values[], IWContext iwc) {
	}
}
