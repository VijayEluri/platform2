package is.idega.idegaweb.campus.data;


public class HabitantHomeImpl extends com.idega.data.IDOFactory implements HabitantHome
{
 protected Class getEntityInterfaceClass(){
  return Habitant.class;
 }

 public Habitant create() throws javax.ejb.CreateException{
  return (Habitant) super.idoCreate();
 }

 public Habitant createLegacy(){
	try{
		return create();
	}
	catch(javax.ejb.CreateException ce){
		throw new RuntimeException("CreateException:"+ce.getMessage());
	}

 }

 public Habitant findByPrimaryKey(int id) throws javax.ejb.FinderException{
  return (Habitant) super.idoFindByPrimaryKey(id);
 }

 public Habitant findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (Habitant) super.idoFindByPrimaryKey(pk);
 }

 public Habitant findByPrimaryKeyLegacy(int id) throws java.sql.SQLException{
	try{
		return findByPrimaryKey(id);
	}
	catch(javax.ejb.FinderException fe){
		throw new java.sql.SQLException(fe.getMessage());
	}

 }


}