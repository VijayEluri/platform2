package is.idega.idegaweb.member.isi.block.reports.business;


public interface WorkReportStatsBusiness extends com.idega.business.IBOSession
{
 public com.idega.block.datareport.util.ReportableCollection getAllMembersForWorkReportId(java.lang.Integer p0)throws java.rmi.RemoteException, java.rmi.RemoteException;
 public com.idega.block.datareport.util.ReportableCollection getClubMemberStatisticsForRegionalUnions(Integer p0,java.util.Collection p1)throws java.rmi.RemoteException, java.rmi.RemoteException;
}
