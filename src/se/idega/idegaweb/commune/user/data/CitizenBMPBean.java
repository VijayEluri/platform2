/*
 * Created on 19.8.2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package se.idega.idegaweb.commune.user.data;

import java.sql.Date;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.block.school.data.SchoolClassMember;
import com.idega.data.IDOCompositePrimaryKeyException;
import com.idega.data.IDOEntityDefinition;
import com.idega.data.IDOEntityField;
import com.idega.data.IDOException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.data.IDOQuery;
import com.idega.data.IDOStoreException;
import com.idega.user.data.Group;
import com.idega.user.data.GroupRelation;
import com.idega.user.data.User;
import com.idega.user.data.UserBMPBean;
import com.idega.util.ListUtil;

/**
 * Title:		CitizenBMPBean
 * Description:
 * Copyright:	Copyright (c) 2003
 * Company:		idega Software
 * @author		2003 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class CitizenBMPBean extends UserBMPBean implements Citizen {

	/* (non-Javadoc)
	 * @see com.idega.data.IDOEntity#store()
	 */
	public void store() throws IDOStoreException {
		super.store();
	}
	
	public Collection ejbFindAllCitizensRegisteredToSchool(Date firstBirthDateInPeriode, Date lastBirthDateInPeriode,Date currentDate) throws FinderException, IDOLookupException{
		//select usr.* from ic_user usr, sch_class_member sch where usr.DATE_OF_BIRTH>='1993-01-01' AND usr.DATE_OF_BIRTH<='1993-10-02' and sch.ic_user_id=usr.ic_user_id and sch.register_date <='2003-01-23' and (sch.removed_date is null  or sch.removed_date > '2003-01-23') order by usr.last_name, usr.first_name, usr.middle_name

		try {
			//preparing
			IDOEntityDefinition schClassMemberDef = IDOLookup.getEntityDefinitionForClass(SchoolClassMember.class);
			IDOEntityDefinition usrDef = IDOLookup.getEntityDefinitionForClass(User.class);
			  	
			String[] tables = new String[2];
			String[] variables = new String[2];
			//table name
			tables[0] = usrDef.getSQLTableName();
			//as variable
			variables[0] = "usr";
			//table name
			tables[1] = schClassMemberDef.getSQLTableName();
			//as variable
			variables[1] = "schcm";
						  	
			//constructing query
			IDOQuery query = idoQuery();
			//select
			query.appendSelect();
			query.append(variables[0]);
			query.append(".* ");
			//from
			query.appendFrom(tables,variables);
			//where
			query.appendWhere();
			query.append(variables[0]);
			query.append(".");
			query.append(getColumnNameDateOfBirth());
			query.appendGreaterThanOrEqualsSign();
			query.append(firstBirthDateInPeriode);
			//and
			query.appendAnd();
			query.append(variables[0]);
			query.append(".");
			query.append(getColumnNameDateOfBirth());
			query.appendLessThanOrEqualsSign();
			query.append(lastBirthDateInPeriode);
			
			//and sch.child_id=usr.ic_user_id 
			query.appendAnd();
			query.append(variables[1]);
			query.append(".");
			query.append(schClassMemberDef.findFieldByUniqueName(SchoolClassMember.FIELD_MEMBER).getSQLFieldName());
			query.appendEqualSign();
			query.append(variables[0]);
			query.append(".");
			query.append(usrDef.getPrimaryKeyDefinition().getField().getSQLFieldName());
			
			
			
			//and sch.register_date <='currentDate' 
			query.appendAnd();
			query.append(variables[1]);
			query.append(".");
			query.append(schClassMemberDef.findFieldByUniqueName(SchoolClassMember.FIELD_REGISTER_DATE).getSQLFieldName());
			query.appendLessThanOrEqualsSign();
			query.append(currentDate);
				
			//and (sch.removed_date is null 
			query.appendAnd();
			query.appendLeftParenthesis();
			query.append(variables[1]);
			query.append(".");
			query.append(schClassMemberDef.findFieldByUniqueName(SchoolClassMember.FIELD_REMOVED_DATE).getSQLFieldName());
			query.appendIsNull();
				
			//or sch.removed_date > 'currentDate' )
			query.appendOr();
			query.append(variables[1]);
			query.append(".");
			query.append(schClassMemberDef.findFieldByUniqueName(SchoolClassMember.FIELD_REMOVED_DATE).getSQLFieldName());
			query.appendGreaterThanSign();
			query.append(currentDate);
			query.appendRightParenthesis();
			
			//order by usr.last_name, usr.first_name, usr.middle_name
			String[] order = new String[3];
			order[0] = variables[0]+"."+usrDef.findFieldByUniqueName(User.FIELD_LAST_NAME).getSQLFieldName();
			order[1] = variables[0]+"."+usrDef.findFieldByUniqueName(User.FIELD_MIDDLE_NAME).getSQLFieldName();
			order[2] = variables[0]+"."+usrDef.findFieldByUniqueName(User.FIELD_MIDDLE_NAME).getSQLFieldName();
			query.appendOrderBy(order); 
			  	
			  	
			System.out.println("SQL -> "+this.getClass()+":"+query);
			return idoFindPKsByQuery(query); 
		  	
		} catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace();
			return ListUtil.getEmptyList();
		}
	}
	
	
	public Collection ejbFindCitizensNotAssignedToClassOnGivenDate(Group citizenGroup, Date date, Collection classes, Date firstDateOfBirth, Date lastDateOfBirth) throws IDOException, IDOLookupException, FinderException{
		try {
			
			IDOEntityDefinition usrDef = IDOLookup.getEntityDefinitionForClass(User.class);
			IDOEntityDefinition scmDef = IDOLookup.getEntityDefinitionForClass(SchoolClassMember.class);
			IDOEntityDefinition grRelDef = IDOLookup.getEntityDefinitionForClass(GroupRelation.class);

			String usrIdColumn = usrDef.getPrimaryKeyDefinition().getField().getSQLFieldName();
			IDOEntityField dateOfBirthField = usrDef.findFieldByUniqueName(User.FIELD_DATE_OF_BIRTH);

			IDOEntityField memberField = scmDef.findFieldByUniqueName(SchoolClassMember.FIELD_MEMBER);
			IDOEntityField registerDateField = scmDef.findFieldByUniqueName(SchoolClassMember.FIELD_REGISTER_DATE);
			IDOEntityField schoolClassField = scmDef.findFieldByUniqueName(SchoolClassMember.FIELD_SCHOOLCLASS);
			
			//relationStatus could be as parameter to this method
			String[] relationStatus = new String[1];
			relationStatus[0] = GroupRelation.STATUS_ACTIVE;
		  	
			String[] tables = new String[2];
			String[] variables = new String[2];
			//table name
			tables[0] = usrDef.getSQLTableName();
			//	as variable
			variables[0] = "u";
			//table name
			tables[1] = grRelDef.getSQLTableName();
			//	as variable
			variables[1] = "gr_rel";
			  	
			//constructing query
			IDOQuery query = idoQuery();
			//select
			query.appendSelect();
			query.append(variables[0]);
			query.append(".* ");
			//from
			query.appendFrom(tables,variables);
			//where
			query.appendWhere();
			query.append(variables[1]);
			query.append(".");
			query.append(grRelDef.findFieldByUniqueName(GroupRelation.FIELD_GROUP).getSQLFieldName());
			query.appendEqualSign();
			query.append(citizenGroup.getPrimaryKey());
			//and
			query.appendAnd();
			query.append(variables[1]);
			query.append(".");
			query.append(grRelDef.findFieldByUniqueName(GroupRelation.FIELD_RELATED_GROUP).getSQLFieldName());
			query.appendEqualSign();
			query.append(variables[0]);
			query.append(".");
			query.append(usrIdColumn);
			
			
			//and if relationstatus
			if(relationStatus!= null){
				//and
				query.appendAnd();
				query.append(variables[1]);
				query.append(".");
				query.append(grRelDef.findFieldByUniqueName(GroupRelation.FIELD_STATUS).getSQLFieldName());
				query.appendInArrayWithSingleQuotes(relationStatus);		
			}

			query.appendAnd();
			query.append(variables[0]);
			query.append(".");
			query.append(dateOfBirthField);
			query.appendGreaterThanOrEqualsSign();
			query.append(firstDateOfBirth);
			
			query.appendAnd();
			query.append(variables[0]);
			query.append(".");
			query.append(dateOfBirthField);
			query.appendLessThanOrEqualsSign();
			query.append(lastDateOfBirth);
			
			query.appendAnd();
			query.append(usrIdColumn);
			
			IDOQuery subQuery = this.idoQuery();
			
			subQuery.appendSelect();
			subQuery.append(" usr.");
			subQuery.append(usrIdColumn);
			subQuery.appendFrom();
			subQuery.append(scmDef.getSQLTableName());
			subQuery.append(" cm, ");
			subQuery.append(usrDef.getSQLTableName());
			subQuery.append(" usr ");
			subQuery.appendWhere();
			subQuery.append("usr.");
			subQuery.append(usrIdColumn);
			subQuery.appendEqualSign();
			subQuery.append("cm.");
			subQuery.append(memberField);
			
			subQuery.appendAnd();
			subQuery.append("cm.");
			subQuery.append(registerDateField);
			subQuery.appendLessThanOrEqualsSign();
			subQuery.append(date);
			
			subQuery.appendAnd();
			subQuery.append("cm.");
			subQuery.append(schoolClassField);
			subQuery.appendInCollection(classes);
			
			query.appendNotIn(subQuery);
			
			//order by usr.last_name, usr.first_name, usr.middle_name
			String[] order = new String[3];
			order[0] = variables[0]+"."+usrDef.findFieldByUniqueName(User.FIELD_LAST_NAME).getSQLFieldName();
			order[1] = variables[0]+"."+usrDef.findFieldByUniqueName(User.FIELD_MIDDLE_NAME).getSQLFieldName();
			order[2] = variables[0]+"."+usrDef.findFieldByUniqueName(User.FIELD_MIDDLE_NAME).getSQLFieldName();
			query.appendOrderBy(order); 

			
			System.out.println("SQL -> "+this.getClass()+":"+query);
					
			return idoFindPKsByQuery(query);
			//Temp debug
			//return idoFindPKsByQuery(query,100);
		} catch (IDOCompositePrimaryKeyException e) {
			e.printStackTrace();
		} 
		return ListUtil.getEmptyList();
	}

	
	

}
