/*
 * $Id: GroupNameUserContactSearch.java,v 1.1.2.1 2007/01/12 19:32:48 idegaweb Exp $
 * Created on Jan 19, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.user.block.search.business;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.FinderException;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.core.search.business.SearchPlugin;
import com.idega.core.search.business.SearchQuery;
import com.idega.core.search.data.SimpleSearchQuery;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.UnavailableIWContext;
import com.idega.presentation.IWContext;
import com.idega.user.business.GroupBusiness;
import com.idega.user.data.Group;


/**
 * 
 *  Last modified: $Date: 2007/01/12 19:32:48 $ by $Author: idegaweb $
 * 
 * This class implements the Searchplugin interface and can therefore be used in a Search block (com.idega.core.search).<br>
 * It searches for groups by their name and returns contact information for the users in the groups that it finds<br>
 * To use it simply register this class as a iw.searchplugin component in a bundle.
 * @author <a href="mailto:eiki@idega.com">Eirikur S. Hrafnsson</a>
 * @version $Revision: 1.1.2.1 $
 */
public class GroupNameUserContactSearch extends UserContactSearch implements SearchPlugin{

	public static final String SEARCH_NAME_LOCALIZABLE_KEY = "group_name_user_contact_search.name";
	public static final String SEARCH_DESCRIPTION_LOCALIZABLE_KEY = "group_name_user_contact_search.description";
	
	/**
	 * 
	 */
	public GroupNameUserContactSearch() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see com.idega.core.search.business.SearchPlugin#getSearchName()
	 */
	public String getSearchName() {
		IWBundle bundle = this.iwma.getBundle(IW_BUNDLE_IDENTIFIER);
		return bundle.getResourceBundle(IWContext.getInstance()).getLocalizedString(SEARCH_NAME_LOCALIZABLE_KEY,"Contacts by group name");
	}

	/* (non-Javadoc)
	 * @see com.idega.core.search.business.SearchPlugin#getSearchDescription()
	 */
	public String getSearchDescription() {
		IWBundle bundle = this.iwma.getBundle(IW_BUNDLE_IDENTIFIER);
		return bundle.getResourceBundle(IWContext.getInstance()).getLocalizedString(SEARCH_DESCRIPTION_LOCALIZABLE_KEY,"Searches for user contact information by group name");
	}
	
	/* (non-Javadoc)
	 * @see com.idega.user.block.search.business.UserContactSearch#getUsers(com.idega.core.search.business.SearchQuery)
	 */
	protected Collection getUsers(SearchQuery searchQuery) {
		List users = new ArrayList();
			
		try {
			GroupBusiness groupBusiness = (GroupBusiness) IBOLookup.getServiceInstance(IWContext.getInstance(),GroupBusiness.class);

			Collection groups = groupBusiness.getGroupsByGroupName(((SimpleSearchQuery)searchQuery).getSimpleSearchQuery().replace('*','%'));

			if(groups!=null){
				Iterator iterator = groups.iterator();
				while (iterator.hasNext()) {
					Group group = (Group) iterator.next();
					try {
						users.addAll(groupBusiness.getUsers(group));
					}
					catch (FinderException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
		catch (IBOLookupException e) {
			e.printStackTrace();
		}
		catch (UnavailableIWContext e) {
			e.printStackTrace();
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return users;
	}
}
