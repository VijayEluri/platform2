/*
 * $Id: Search.java,v 1.4 2005/01/19 01:48:30 eiki Exp $
 * Created on 18.1.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.search.business;

import java.util.Collection;


/**
 * <p>
 * An instance of this class corresponds to an individual search performed by a user.
 * </p>
 *  Last modified: $Date: 2005/01/19 01:48:30 $ by $Author: eiki $
 * 
 * @author <a href="mailto:tryggvil@idega.com">tryggvil</a>
 * @version $Revision: 1.4 $
 */
public interface Search {
	
	/**
	 * An identifier for the type of search, e.g. "user","group","document" etc. <br>
	 * Could be the same as the protocol you set on the URI the SearchResult returns.<br>
	 * Could be used to group search results together.
	 * @return An identifier for the type of search.
	 */
	public String getSearchType();
	
	/**
	 * @return A displayable, preferably localized name.
	 */
	public String getSearchName();
	/**
	 * @return The parameter "Map" used to create the search.
	 */
	public SearchQuery getSearchQuery();
	/**
	 * @return Returns a collection of SearchResult objects.
	 */
	public Collection getSearchResults();
	
	/**
	 * @return The number of results the search yielded.
	 */
	public long getNumberOfResults();
}
