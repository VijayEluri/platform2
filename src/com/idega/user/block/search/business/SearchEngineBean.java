package com.idega.user.block.search.business;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;
import com.idega.business.IBOServiceBean;
import com.idega.data.IDOLookup;
import com.idega.user.block.search.event.UserSearchEvent;
import com.idega.user.data.Gender;
import com.idega.user.data.Group;
import com.idega.user.data.GroupHome;
import com.idega.user.data.User;
import com.idega.user.data.UserHome;
import com.idega.util.ListUtil;
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
				return getSimpleSearchResults(e.getSearchType(), e.getSearchString());
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
						
			Collection entities = userHome.findUsersByConditions(e.getSearchString(),e.getPersonalId()
				,e.getAddress(),null,e.getGenderId(),e.getStatusId()
				,e.getAgeFloor(),e.getAgeCeil(),e.getGroups(),null,true);
			
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
	
	public Collection getSimpleSearchResults(int searchType, String searchString) throws RemoteException {
		return doSimpleSearch(searchString);
	}
	
	private Collection doSimpleSearch(String searchString) {
		if (searchString == null || searchString.length() <2)
			return null;
		try {
			UserHome userHome = (UserHome) IDOLookup.getHome(User.class);
			Collection entities = userHome.findUsersBySearchCondition(searchString);
			return entities;
		}
		// Remote and FinderException
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	
	
	
	
	
	
	
}