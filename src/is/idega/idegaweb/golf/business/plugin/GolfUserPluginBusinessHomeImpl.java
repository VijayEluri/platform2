/*
 * $Id: GolfUserPluginBusinessHomeImpl.java,v 1.3 2004/12/07 15:58:30 eiki Exp $
 * Created on Dec 7, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.golf.business.plugin;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2004/12/07 15:58:30 $ by $Author: eiki $
 * 
 * @author <a href="mailto:eiki@idega.com">eiki</a>
 * @version $Revision: 1.3 $
 */
public class GolfUserPluginBusinessHomeImpl extends IBOHomeImpl implements GolfUserPluginBusinessHome {

	protected Class getBeanInterfaceClass() {
		return GolfUserPluginBusiness.class;
	}

	public GolfUserPluginBusiness create() throws javax.ejb.CreateException {
		return (GolfUserPluginBusiness) super.createIBO();
	}
}
