/*
 * $Id: IBDomainBMPBean.java,v 1.14 2004/07/16 10:05:38 gummi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.builder.data;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.ejb.FinderException;

import com.idega.builder.business.BuilderLogic;
import com.idega.core.builder.data.ICDomain;
import com.idega.core.builder.data.ICDomainHome;
import com.idega.core.builder.data.ICPage;
import com.idega.core.builder.data.ICPageHome;
import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;
import com.idega.data.IDOQuery;
import com.idega.data.IDORelationshipException;
import com.idega.user.data.GroupDomainRelation;
import com.idega.user.data.GroupDomainRelationHome;
import com.idega.user.data.GroupDomainRelationTypeBMPBean;

/**
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class IBDomainBMPBean extends GenericEntity implements ICDomain {
  public static final String tableName = "IB_DOMAIN";
  public static final String domain_name = "DOMAIN_NAME";
  public static final String domain_url = "URL";
  public static final String start_page = "START_IB_PAGE_ID";
  public static final String start_template = "START_IB_TEMPLATE_ID";
  public static final String COLUMNNAME_GROUP_ID = "GROUP_ID";
  public static final String COLUMNNAME_SERVER_NAME = "SERVER_NAME";

  private static Map cachedDomains;

  public IBDomainBMPBean() {
    super();
  }

  private IBDomainBMPBean(int id) throws java.sql.SQLException {
    super(id);
  }

  public void initializeAttributes() {
    addAttribute(getIDColumnName());

    addAttribute(getColumnDomainName(),"Domain name",true,true,String.class);
    addAttribute(getColumnURL(),"Domain URL",true,true,String.class,1000);
    addAttribute(getColumnStartPage(),"Start Page",true,true,Integer.class,"many-to-one",ICPage.class);
    addAttribute(getColumnStartTemplate(),"Start Template",true,true,Integer.class,"many-to-one",ICPage.class);
    addAttribute(COLUMNNAME_SERVER_NAME,"Server NAME",true,true,String.class);
//    this.addManyToManyRelationShip(Group.class);
//    addAttribute(COLUMNNAME_GROUP_ID,"Group ID",true,true,Integer.class,"one-to-one",Group.class);

  }

  public static ICDomain getDomain(int id)throws SQLException {
    ICDomain theReturn;
    theReturn = (ICDomain)getDomainsMap().get(new Integer(id));
    if (theReturn == null) {
      theReturn = ((ICDomainHome)IDOLookup.getHomeLegacy(ICDomain.class)).findByPrimaryKeyLegacy(id);
      if (theReturn != null) {
        getDomainsMap().put(new Integer(id),theReturn);
      }
    }
    return(theReturn);
  }

  private static Map getDomainsMap() {
    if (cachedDomains==null) {
      cachedDomains = new HashMap();
    }
    return(cachedDomains);
  }

  public void insertStartData() throws Exception {
    BuilderLogic instance = BuilderLogic.getInstance();
    ICDomainHome dHome = (ICDomainHome)getIDOHome(ICDomain.class);
    ICDomain domain = dHome.create();
    domain.setName("Default Site");

	ICPageHome pageHome = (ICPageHome)getIDOHome(ICPage.class);

    ICPage page = pageHome.create();
    page.setName("Web root");
    page.setType(com.idega.builder.data.IBPageBMPBean.PAGE);
    page.store();
    instance.unlockRegion(Integer.toString(page.getID()),"-1",null);

    ICPage page2 = pageHome.create();
    page2.setName("Default Template");
    page2.setType(com.idega.builder.data.IBPageBMPBean.TEMPLATE);
    page2.store();

    instance.unlockRegion(Integer.toString(page2.getID()),"-1",null);

    page.setTemplateId(page2.getID());
    page.store();

    domain.setIBPage(page);
    domain.setStartTemplate(page2);
    domain.store();

    instance.setTemplateId(Integer.toString(page.getID()),Integer.toString(page2.getID()));
    instance.getIBXMLPage(page2.getID()).addUsingTemplate(Integer.toString(page.getID()));
  }

  public String getEntityName() {
    return(tableName);
  }

  public static String getColumnDomainName() {
    return(domain_name);
  }

  public static String getColumnURL() {
    return(domain_url);
  }

  public static String getColumnStartPage() {
    return(start_page);
  }

  public static String getColumnStartTemplate() {
    return(start_template);
  }

  public ICPage getStartPage() {
    return((ICPage)getColumnValue(getColumnStartPage()));
  }

  public int getStartPageID() {
    return(getIntColumnValue(getColumnStartPage()));
  }

  public ICPage getStartTemplate() {
    return((ICPage)getColumnValue(getColumnStartTemplate()));
  }

  public int getStartTemplateID() {
    return(getIntColumnValue(getColumnStartTemplate()));
  }

//  public Group getGroup() {
//    return((Group)getColumnValue(COLUMNNAME_GROUP_ID));
//  }
//
//  public int getGroupID() {
//    return(getIntColumnValue(COLUMNNAME_GROUP_ID));
//  }

  public String getName() {
    return(getDomainName());
  }

  public String getDomainName() {
    return(getStringColumnValue(getColumnDomainName()));
  }

  public String getURL() {
    return(getStringColumnValue(getColumnURL()));
  }

  public Collection getTopLevelGroupsUnderDomain() throws IDORelationshipException, RemoteException, FinderException{

    Collection relations = ((GroupDomainRelationHome)IDOLookup.getHome(GroupDomainRelation.class)).findGroupsRelationshipsUnderDomainByRelationshipType(this.getID(),GroupDomainRelationTypeBMPBean.RELATION_TYPE_TOP_NODE);
//TODO do this in one sql command like in groupbmpbean and grouprelation
    Iterator iter = relations.iterator();
    Collection groups = new Vector();
    while (iter.hasNext()) {
      GroupDomainRelation item = (GroupDomainRelation)iter.next();
        groups.add(item.getRelatedGroup());
    }

    return groups;
  }

  public void setIBPage(ICPage page) {
     setColumn(getColumnStartPage(),page);
  }

  public void setStartTemplate(ICPage template) {
    setColumn(getColumnStartTemplate(),template);
  }

//  public void setGroup(Group group) {
//     setColumn(COLUMNNAME_GROUP_ID,group);
//  }

  public void setName(String name) {
    setColumn(getColumnDomainName(),name);
  }

  public Collection ejbFindAllDomains() throws FinderException {
    String sql = "select * from " + getTableName();
    return super.idoFindIDsBySQL(sql);
  }
  
  public Collection ejbFindAllDomainsByServerName(String serverName) throws FinderException{
  	IDOQuery query = idoQueryGetSelect();
  	query.appendWhereEqualsWithSingleQuotes(COLUMNNAME_SERVER_NAME,serverName);
  	System.out.println(query.toString());
  	return idoFindPKsByQuery(query);
  }
}