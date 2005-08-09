/*
 * $Id: FamilyMemberBMPBean.java,v 1.8 2005/08/09 11:38:08 laddi Exp $ Created on 27.8.2004
 * 
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package is.idega.block.family.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.data.GenericEntity;
import com.idega.data.IDOQuery;
import com.idega.data.IDORelationshipException;
import com.idega.data.query.Column;
import com.idega.data.query.MatchCriteria;
import com.idega.data.query.SelectQuery;
import com.idega.data.query.Table;
import com.idega.user.data.User;
import com.idega.user.data.UserBMPBean;

/**
 * 
 * Last modified: $Date: 2005/08/09 11:38:08 $ by $Author: laddi $
 * 
 * @author <a href="mailto:Joakim@idega.com">Joakim </a>
 * @version $Revision: 1.8 $
 */
public class FamilyMemberBMPBean extends GenericEntity implements FamilyMember{

	private static final String ENTITY_NAME = "fam_family_member";

	private static final String COLUMN_FAMILY_NR = "family_nr";
	private final static String COLUMN_USER = "ic_user_id";
	private final static String COLUMN_ROLE = "role";
	public static final int MOTHER = 1;
	public static final int FATHER = 2;
	public static final int CHILD = 3;

	/*
	 * @see com.idega.data.IDOLegacyEntity#getEntityName()
	 */
	public String getEntityName() {
		return ENTITY_NAME;
	}
	
	/*
	 * @see com.idega.data.IDOLegacyEntity#initializeAttributes()
	 */
	public void initializeAttributes() {
		addAttribute(getIDColumnName());
		addAttribute(COLUMN_FAMILY_NR, "Family Number", true, true, java.lang.String.class);
		addManyToOneRelationship(COLUMN_USER, User.class);
		addAttribute(COLUMN_ROLE,"role",true,true,java.lang.Integer.class);
		
		addIndex("IDX_FAM_MEMBER_1", COLUMN_USER);
		addIndex("IDX_FAM_MEMBER_2", COLUMN_FAMILY_NR);
	}
	
	public void setFamilyNr(String familyNr) {
		setColumn(COLUMN_FAMILY_NR, familyNr);
	}

	public void setUser(User user) {
		setColumn(COLUMN_USER, user);
	}

	public void setRole(int role ) {
		setColumn(COLUMN_ROLE, role);
	}

	public String getFamilyNr() {
		return getStringColumnValue(COLUMN_FAMILY_NR);	
	}
	
	public User getUser() {
		return (User) getColumnValue(COLUMN_USER);	
	}

	public int getRole() {
		return  getIntColumnValue(COLUMN_ROLE);	
	}

	public Collection ejbFindAll() throws FinderException {
		return super.idoFindAllIDsBySQL();
	}

	public Collection ejbFindAllByFamilyNR(String familyNr) throws FinderException {
		IDOQuery sql = idoQueryGetSelect();
		sql.appendWhereEqualsQuoted(COLUMN_FAMILY_NR, familyNr);
		return this.idoFindPKsBySQL(sql.toString());
	}

	public Object ejbFindForUser(User user) throws FinderException {
		Table table = new Table(this);
		Column userCol = new Column(table, COLUMN_USER);
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addCriteria(new MatchCriteria(userCol, MatchCriteria.EQUALS, user.getPrimaryKey()));
		
		return idoFindOnePKBySQL(query.toString());
	}
	
	public Object ejbFindBySSN(String ssn) throws IDORelationshipException, FinderException {
		Table table = new Table(this);
		Table userTable = new Table(User.class);
		Column userSSN = new Column(userTable, UserBMPBean.getColumnNamePersonalID());
		
		SelectQuery query = new SelectQuery(table);
		query.addColumn(new Column(table, getIDColumnName()));
		query.addJoin(table, userTable);
		query.addCriteria(new MatchCriteria(userSSN, MatchCriteria.EQUALS, ssn));
		
		return idoFindOnePKBySQL(query.toString());
	}
}