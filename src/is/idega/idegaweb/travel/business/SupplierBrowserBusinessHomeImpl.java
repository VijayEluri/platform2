/*
 * $Id: SupplierBrowserBusinessHomeImpl.java,v 1.2 2005/08/24 13:19:33 gimmi Exp $
 * Created on Aug 19, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package is.idega.idegaweb.travel.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2005/08/24 13:19:33 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.2 $
 */
public class SupplierBrowserBusinessHomeImpl extends IBOHomeImpl implements SupplierBrowserBusinessHome {

	protected Class getBeanInterfaceClass() {
		return SupplierBrowserBusiness.class;
	}

	public SupplierBrowserBusiness create() throws javax.ejb.CreateException {
		return (SupplierBrowserBusiness) super.createIBO();
	}
}
