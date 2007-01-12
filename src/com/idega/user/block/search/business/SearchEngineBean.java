package com.idega.user.block.search.business;
import java.rmi.RemoteException;
import java.util.Collection;

import com.idega.business.IBOServiceBean;
import com.idega.data.IDOLookup;
import com.idega.user.block.search.event.UserSearchEvent;
import com.idega.user.data.User;
import com.idega.user.data.UserHome;
import com.idega.util.text.TextSoap;
/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */
public class SearchEngineBean extends IBOServiceBean implements SearchEngine{
	public SearchEngineBean() {
	}
	
	public Collection getResult(UserSearchEvent e) throws RemoteException {
		switch (e.getSearchType()) {
			case UserSearchEvent.SEARCHTYPE_SIMPLE :
				return getSimpleSearchResults(e.getSearchString());
			case UserSearchEvent.SEARCHTYPE_ADVANCED :
				return getAdvancedSearchResults(e);
			default :
				throw new UnsupportedOperationException("SearchType not known");
		}
		
	}
	
	/**
	 * @param usersearchevent
	 * @return the results of the search
	 */
	private Collection getAdvancedSearchResults(UserSearchEvent e) {
		try {
			UserHome userHome = (UserHome) IDOLookup.getHome(User.class);
						
			Collection entities = userHome.findUsersByConditions(e.getFirstName(),e.getMiddleName(),e.getLastName(),e.getPersonalId()
				,e.getAddress(),null,e.getGenderId(),e.getStatusId()
				,e.getAgeFloor(),e.getAgeCeil(),e.getGroups(),null,true, false);
			
			return entities;
		}
		// Remote and FinderException
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public Class getResultType(UserSearchEvent e) {
		return User.class;
	}
	
	public Collection getSimpleSearchResults(String searchString) throws RemoteException {
		return doSimpleSearch(searchString);
	}
	
	private Collection doSimpleSearch(String searchString) {
		if (searchString == null || searchString.length() <2) {
			return null;
		}
		try {
			searchString = TextSoap.removeWhiteSpaceFromBeginningAndEndOfString(searchString);
			UserHome userHome = (UserHome) IDOLookup.getHome(User.class);
			Collection entities = userHome.findUsersBySearchCondition(searchString, false);
			return entities;
		}
		// Remote and FinderException
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	
	
	
	
	
	
	
}