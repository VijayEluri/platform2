package is.idega.block.nationalregister.business;


public interface NationalRegisterBusiness extends com.idega.business.IBOService
{
 public is.idega.block.nationalregister.data.NationalRegister getEntryBySSN(java.lang.String p0) throws java.rmi.RemoteException;
 public boolean updateEntry(java.lang.String p0,java.lang.String p1,java.lang.String p2,java.lang.String p3,java.lang.String p4,java.lang.String p5,java.lang.String p6,java.lang.String p7,java.lang.String p8,java.lang.String p9,java.lang.String p10,java.lang.String p11,java.lang.String p12,java.lang.String p13,java.lang.String p14,java.lang.String p15,java.lang.String p16,java.lang.String p17,java.lang.String p18,java.lang.String p19) throws java.rmi.RemoteException;
}
