/*
 * $Id: PostalCodeHome.java,v 1.5 2005/06/02 16:14:28 gimmi Exp $
 * Created on 2.6.2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.core.location.data;

import java.rmi.RemoteException;
import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.IDOHome;


/**
 * 
 *  Last modified: $Date: 2005/06/02 16:14:28 $ by $Author: gimmi $
 * 
 * @author <a href="mailto:gimmi@idega.com">gimmi</a>
 * @version $Revision: 1.5 $
 */
public interface PostalCodeHome extends IDOHome {

	public PostalCode create() throws javax.ejb.CreateException;

	public PostalCode findByPrimaryKey(Object pk) throws javax.ejb.FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindByPostalCodeAndCountryId
	 */
	public PostalCode findByPostalCodeAndCountryId(String code, int countryId) throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindAllByCountryIdOrderedByPostalCode
	 */
	public Collection findAllByCountryIdOrderedByPostalCode(int countryId) throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbHomeGetUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName
	 */
	public Collection getUniquePostalCodeNamesByCountryIdOrderedByPostalCodeName(int countryId) throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindByNameAndCountry
	 */
	public Collection findByNameAndCountry(String name, Object countryPK) throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindByCountry
	 */
	public Collection findByCountry(Object countryPK) throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindAllUniqueNames
	 */
	public Collection findAllUniqueNames() throws RemoteException, FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindAll
	 */
	public Collection findAll() throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindAllOrdererByCode
	 */
	public Collection findAllOrdererByCode() throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindByPostalCodeFromTo
	 */
	public Collection findByPostalCodeFromTo(String codeFrom, String codeTo) throws FinderException;

	/**
	 * @see com.idega.core.location.data.PostalCodeBMPBean#ejbFindByPostalCodeFromTo
	 */
	public Collection findByPostalCodeFromTo(String[] codeFrom, String[] codeTo) throws FinderException;
}
