/*
 * Created on Dec 10, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.idega.user.presentation.inputhandler;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.business.InputHandler;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.SelectionBox;
import com.idega.user.data.GroupType;
import com.idega.user.data.GroupTypeHome;

/**
 * @author jonas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GroupTypeSelectionBoxInputHandler extends SelectionBox implements InputHandler {
	private static final String IW_BUNDLE_IDENTIFIER = "com.idega.user";
	private List _allGroupTypes = new ArrayList();
	
	public GroupTypeSelectionBoxInputHandler() {
		super();
	}
	
	public GroupTypeSelectionBoxInputHandler(String name) {
		super(name);
	}
	
	
	public void main(IWContext iwc) {
		try {
			IWResourceBundle iwrb = this.getResourceBundle(iwc);
			GroupTypeHome groupTypeHome = (GroupTypeHome) IDOLookup.getHome(GroupType.class);

			Collection groupTypes = groupTypeHome.findVisibleGroupTypes();

			if (groupTypes != null) {
				Iterator iter = groupTypes.iterator();
				while (iter.hasNext()) {
					GroupType groupType = (GroupType) iter.next();
					String name = groupType.getType();
					if(name!=null) {
						addMenuElement(name, iwrb.getLocalizedString(name,name));
						_allGroupTypes.add(name);
					}
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see com.idega.business.InputHandler#getHandlerObject(java.lang.String, java.lang.String, com.idega.presentation.IWContext)
	 */
	public PresentationObject getHandlerObject(String name, String stringValue, IWContext iwc) {
		this.setName(name);
		if (stringValue != null) {
			this.setContent(stringValue);
		}
		return this;
	}

	/* (non-Javadoc)
	 * @see com.idega.business.InputHandler#getResultingObject(java.lang.String[], com.idega.presentation.IWContext)
	 */
	public Object getResultingObject(String[] values, IWContext iwc) throws Exception {
		Collection groupTypes = null;
		if (values != null && values.length > 0) {
			groupTypes = new ArrayList();
			
			for(int i=0; i<values.length; i++) {
				groupTypes.add(values[i]);
			}
		} else {
			groupTypes = _allGroupTypes;
		}

		return groupTypes;
	}

	/* (non-Javadoc)
	 * @see com.idega.business.InputHandler#getDisplayNameOfValue(java.lang.Object, com.idega.presentation.IWContext)
	 */
	public String getDisplayForResultingObject(Object value, IWContext iwc) {
		String result = null;
		IWResourceBundle iwrb = getResourceBundle(iwc);
		if(value!=null && value instanceof Collection) {
			Collection groupTypes = (Collection) value;
			StringBuffer buf = new StringBuffer();
			Iterator gtIter = groupTypes.iterator();
			boolean isFirst = true;
			while(gtIter.hasNext()) {
				if(isFirst) {
					isFirst=false;
				} else {
					buf.append(",");
				}
				String name = (String) gtIter.next();
				
				buf.append(iwrb.getLocalizedString(name,name));
			}
			result = buf.toString();
		}
		return result;
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
}