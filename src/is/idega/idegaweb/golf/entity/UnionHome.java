package is.idega.idegaweb.golf.entity;


public interface UnionHome extends com.idega.data.IDOHome
{
 public Union create() throws javax.ejb.CreateException;
 public Union createLegacy();
 public Union findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public Union findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Union findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}