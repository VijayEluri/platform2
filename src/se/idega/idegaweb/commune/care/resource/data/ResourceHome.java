package se.idega.idegaweb.commune.care.resource.data;


public interface ResourceHome extends com.idega.data.IDOHome
{
 public Resource create() throws javax.ejb.CreateException;
 public Resource findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllResources()throws javax.ejb.FinderException;
 public java.util.Collection findAssignRightResourcesByGrpId(java.lang.Integer p0)throws javax.ejb.FinderException;
 public java.util.Collection findBySchCategory(java.lang.String p0)throws javax.ejb.FinderException;
 public java.util.Collection findBySchoolType(int p0)throws javax.ejb.FinderException;
 public Resource findResourceByName(java.lang.String p0)throws javax.ejb.FinderException;
 public java.util.Collection findViewRightResourcesByGrpId(java.lang.Integer p0)throws javax.ejb.FinderException;

}