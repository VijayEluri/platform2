/*
<<<<<<< SerieAccountBMPBean.java
 * 
 * $Id: SerieAccountBMPBean.java,v 1.3 2004/06/05 07:35:42 aron Exp $
 * 
 * 
 * 
=======

 * $Id: SerieAccountBMPBean.java,v 1.3 2004/06/05 07:35:42 aron Exp $

 *

>>>>>>> 1.2
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 * 
 * 
 * 
 * This software is the proprietary information of Idega hf.
 * 
 * Use is subject to license terms.
 * 
 * 
 *  
 */
package is.idega.idegaweb.campus.data;

import java.sql.SQLException;
/**
 * 
 * Title:
 * 
 * Description:
 * 
 * Copyright: Copyright (c) 2000-2001 idega.is All Rights Reserved
 * 
 * Company: idega
 * 
 * @author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * 
 * @version 1.1
 *  
 */
public class SerieAccountBMPBean
	extends com.idega.data.GenericView
	implements is.idega.idegaweb.campus.data.SerieAccount {
	/*
	 * 
	 * CREATE VIEW "V_SERIE_ACCOUNTS" (
	 * 
	 * "BSERIE",
	 * 
	 * "ASERIE",
	 * 
	 * "FIN_ACCOUNT_ID",
	 * 
	 * "BU_APARTMENT_ID"
	 *  ) AS
	 * 
	 * 
	 * 
	 * select b.serie,a.serie,acc.fin_account_id ,a.bu_apartment_id
	 * 
	 * from bu_apartment a,bu_floor f,bu_building b,cam_contract c,fin_account
	 * acc
	 * 
	 * where a.bu_floor_id = f.bu_floor_id
	 * 
	 * and f.bu_building_id = b.bu_building_id
	 * 
	 * and a.bu_apartment_id = c.bu_apartment_id
	 * 
	 * and acc.ic_user_id = c.ic_user_id
	 * 
	 * and c.status = 'S'
	 * 
	 * and acc.account_type = 'FINANCE'
	 *  ;
	 *  
	 */
	public static String getEntityTableName() {
		return "V_SERIE_ACCOUNTS";
	}
	public static String getColumnNameBuildingSerie() {
		return "BSERIE";
	}
	public static String getColumnNameApartmentSerie() {
		return "ASERIE";
	}
	public static String getColumnNameAccountId() {
		return "FIN_ACCOUNT_ID";
	}
	public static String getColumnNameApartmentId() {
		return "BU_APARTMENT_ID";
	}
	public SerieAccountBMPBean() {
	}
	public SerieAccountBMPBean(int id) throws SQLException {
	}
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(getColumnNameBuildingSerie(), "BSerie", true, true, String.class);
		addAttribute(getColumnNameApartmentSerie(), "ASerie", true, true, String.class);
		addAttribute(getColumnNameApartmentId(), "Apartment id", true, true, java.lang.Integer.class);
		addAttribute(getColumnNameAccountId(), "Account id", true, true, java.lang.Integer.class);
	}
	public String getEntityName() {
		return (getEntityTableName());
	}
	public String getBuildingSerie() {
		return getStringColumnValue(getColumnNameBuildingSerie());
	}
	public String getApartmentSerie() {
		return getStringColumnValue(getColumnNameApartmentSerie());
	}
	public int getApartmentId() {
		return getIntColumnValue(getColumnNameApartmentId());
	}
	public int getAccountId() {
		return getIntColumnValue(getColumnNameAccountId());
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.IDOView#getCreationSQL()
	 */
	public String getCreationSQL() {
		StringBuffer sql = new StringBuffer();
		sql.append("CREATE VIEW V_SERIE_ACCOUNTS (");
		sql.append(" BSERIE,  ");
		sql.append(" ASERIE, ");
		sql.append(" FIN_ACCOUNT_ID, ");
		sql.append(" BU_APARTMENT_ID ");
		sql.append(" ) AS ");
		sql.append(" select b.serie,a.serie,acc.fin_account_id ,a.bu_apartment_id ");
		sql.append(" from bu_apartment a,bu_floor f,bu_building b,cam_contract c,fin_account acc ");
		sql.append(" where a.bu_floor_id = f.bu_floor_id ");
		sql.append(" and f.bu_building_id = b.bu_building_id ");
		sql.append(" and a.bu_apartment_id = c.bu_apartment_id ");
		sql.append(" and acc.ic_user_id = c.ic_user_id ");
		sql.append(" and c.status = 'S' ");
		sql.append(" and acc.account_type = 'FINANCE' ");
		return sql.toString();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.data.GenericView#getViewName()
	 */
	public String getViewName() {
		return getEntityTableName();
	}
}
