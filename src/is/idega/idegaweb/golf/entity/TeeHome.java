package is.idega.idegaweb.golf.entity;


public interface TeeHome extends com.idega.data.IDOHome
{
 public Tee create() throws javax.ejb.CreateException;
 public Tee createLegacy();
 public Tee findByPrimaryKey(int id) throws javax.ejb.FinderException;
 public Tee findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public Tee findByPrimaryKeyLegacy(int id) throws java.sql.SQLException;

}