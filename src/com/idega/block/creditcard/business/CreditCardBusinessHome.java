/*
 * $Id: CreditCardBusinessHome.java,v 1.2 2005/05/31 19:28:07 gimmi Exp $
 * Created on 4.4.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.creditcard.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2005/05/31 19:28:07 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.2 $
 */
public interface CreditCardBusinessHome extends IBOHome {

	public CreditCardBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
