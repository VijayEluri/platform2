/*
 * $Id: StockroomBusinessHome.java,v 1.3 2005/05/13 04:36:59 gimmi Exp $
 * Created on 12.5.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.trade.stockroom.business;

import com.idega.business.IBOHome;


/**
 * 
 *  Last modified: $Date: 2005/05/13 04:36:59 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.3 $
 */
public interface StockroomBusinessHome extends IBOHome {

	public StockroomBusiness create() throws javax.ejb.CreateException, java.rmi.RemoteException;
}
