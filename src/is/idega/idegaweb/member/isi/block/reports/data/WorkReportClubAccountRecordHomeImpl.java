package is.idega.idegaweb.member.isi.block.reports.data;


public class WorkReportClubAccountRecordHomeImpl extends com.idega.data.IDOFactory implements WorkReportClubAccountRecordHome
{
 protected Class getEntityInterfaceClass(){
  return WorkReportClubAccountRecord.class;
 }


 public WorkReportClubAccountRecord create() throws javax.ejb.CreateException{
  return (WorkReportClubAccountRecord) super.createIDO();
 }


public java.util.Collection findAllRecordsByWorkReportId(int p0)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((WorkReportClubAccountRecordBMPBean)entity).ejbFindAllRecordsByWorkReportId(p0);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllRecordsByWorkReportIdAndWorkReportGroupId(int p0,int p1)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((WorkReportClubAccountRecordBMPBean)entity).ejbFindAllRecordsByWorkReportIdAndWorkReportGroupId(p0,p1);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public java.util.Collection findAllRecordsByWorkReportIdAndWorkReportGroupIdAndWorkReportAccountKeyCollection(int p0,int p1,java.util.Collection p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	java.util.Collection ids = ((WorkReportClubAccountRecordBMPBean)entity).ejbFindAllRecordsByWorkReportIdAndWorkReportGroupIdAndWorkReportAccountKeyCollection(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.getEntityCollectionForPrimaryKeys(ids);
}

public WorkReportClubAccountRecord findRecordByWorkReportIdAndWorkReportGroupIdAndWorkReportAccountKeyId(int p0,int p1,int p2)throws javax.ejb.FinderException{
	com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
	Object pk = ((WorkReportClubAccountRecordBMPBean)entity).ejbFindRecordByWorkReportIdAndWorkReportGroupIdAndWorkReportAccountKeyId(p0,p1,p2);
	this.idoCheckInPooledEntity(entity);
	return this.findByPrimaryKey(pk);
}

 public WorkReportClubAccountRecord findByPrimaryKey(Object pk) throws javax.ejb.FinderException{
  return (WorkReportClubAccountRecord) super.findByPrimaryKeyIDO(pk);
 }



}