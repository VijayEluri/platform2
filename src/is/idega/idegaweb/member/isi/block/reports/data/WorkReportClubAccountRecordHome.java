package is.idega.idegaweb.member.isi.block.reports.data;


public interface WorkReportClubAccountRecordHome extends com.idega.data.IDOHome
{
 public WorkReportClubAccountRecord create() throws javax.ejb.CreateException;
 public WorkReportClubAccountRecord findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllRecordsByWorkReportId(int p0)throws javax.ejb.FinderException;
 public java.util.Collection findAllRecordsByWorkReportIdAndWorkReportGroupId(int p0,int p1)throws javax.ejb.FinderException;
 public java.util.Collection findAllRecordsByWorkReportIdAndWorkReportGroupIdAndWorkReportAccountKeyCollection(int p0,int p1,java.util.Collection p2)throws javax.ejb.FinderException;

}