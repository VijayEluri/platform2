/*
 * $Id: AdultEducationSessionHomeImpl.java,v 1.10 2005/08/10 00:19:27 laddi Exp $
 * Created on Aug 9, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package se.idega.idegaweb.commune.adulteducation.business;

import com.idega.business.IBOHomeImpl;


/**
 * Last modified: $Date: 2005/08/10 00:19:27 $ by $Author: laddi $
 * 
 * @author <a href="mailto:laddi@idega.com">laddi</a>
 * @version $Revision: 1.10 $
 */
public class AdultEducationSessionHomeImpl extends IBOHomeImpl implements AdultEducationSessionHome {

	protected Class getBeanInterfaceClass() {
		return AdultEducationSession.class;
	}

	public AdultEducationSession create() throws javax.ejb.CreateException {
		return (AdultEducationSession) super.createIBO();
	}
}
