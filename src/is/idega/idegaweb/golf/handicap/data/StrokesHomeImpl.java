package is.idega.idegaweb.golf.handicap.data;


public class StrokesHomeImpl extends com.idega.data.IDOFactory implements StrokesHome {
	
	protected Class getEntityInterfaceClass(){
	  return Strokes.class;
	}

	public Strokes create(is.idega.idegaweb.golf.handicap.data.StrokesKey p0)throws javax.ejb.CreateException{
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((StrokesBMPBean)entity).ejbCreate(p0);
		this.idoCheckInPooledEntity(entity);
		try{
			return this.findByPrimaryKey(pk);
		}
		catch(javax.ejb.FinderException fe){
			throw new com.idega.data.IDOCreateException(fe);
		}
		catch(Exception e){
			throw new com.idega.data.IDOCreateException(e);
		}
	}
	
	public java.util.Collection findAllByScorecard(java.lang.Object p0)throws javax.ejb.FinderException{
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((StrokesBMPBean)entity).ejbFindAllByScorecard(p0);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
	
	public Strokes findByPrimaryKey(is.idega.idegaweb.golf.handicap.data.StrokesKey p0)throws javax.ejb.FinderException{
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((StrokesBMPBean)entity).ejbFindByPrimaryKey(p0);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
	
	public Strokes findStrokesByScorecardAndHole(java.lang.Object p0,java.lang.Object p1)throws javax.ejb.FinderException{
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((StrokesBMPBean)entity).ejbFindStrokesByScorecardAndHole(p0,p1);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
	
	public Strokes findStrokesByScorecardAndHoleNumber(java.lang.Object p0,int p1)throws javax.ejb.FinderException{
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((StrokesBMPBean)entity).ejbFindStrokesByScorecardAndHoleNumber(p0,p1);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}
	
	private Strokes findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
	  return (Strokes) super.findByPrimaryKeyIDO(pk);
	}
}