package com.idega.projects.golf.service;

import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.projects.golf.entity. *;
import com.idega.projects.golf.service.*;
import com.idega.util.*;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import java.sql.*;
import java.util.*;
import com.idega.idegaweb.*;
import java.sql.SQLException;

/**
*@author <a href="mailto:aron@idega.is">Aron Birkir</a>
*@version 1.0
*/
public class MemberReport extends com.idega.presentation.PresentationObjectContainer {

  private final int ACT1 = 1, ACT2 = 2, ACT3 = 3, ACT4 = 4;
  private final int NOACT = 0;
  private int iAction;
  private String sAction = "list_action";
  private String sActPrm = "";
  private boolean isAdmin;
  private String[] sSelectNames = {"first_name,middle_name,last_name","social_security_number","email","street,street_number","number","handicap","balance","locker_number","member_status"};
  private String[] sSelectDisplay = {"Nafn","Kennitala","Netfang","Heimili","S�mi","Forgj�f","Skuldasta�a","Sk�pur","Sta�a"};
  private String[] sSelectTables = {"member","member","member","address,member_address","phone,member_phone","member_info","account","union_member_info","union_member_info"};
  private String[] sSelectJoin = {"","","","address.address_id = member_address.address_id and member.member_id = member_address.member_id",
                                        "phone.phone_id = member_phone.phone_id and member.member_id = member_phone.member_id",
                                        "member.member_id = member_info.member_id","member.member_id = account.member_id",
                                        "member.member_id = union_member_info.member_id ",
                                        "member.member_id = union_member_info.member_id "};

  private final int CH1=0,CH2=1,CH3=2,CH4=3,CH5=4,CH6=5,CH7=6,CH8=7,CH9=8;
  private final int F=0,M=1,L=2,S=3,E=4,G=5,N=6,P=7,H=8,B=9,C=10,U=11;
  private String[] sCols = {"first_name","middle_name","last_name","social_security_number","email","street","street_number","number","handicap","balance","locker_number","member_status"};
  private int[] iCols;
  private int paneWidth=600;
  private String boxName = "kassinn";
  private String[] sHandiCapLow = {};
  private String[] sHandiCapHigh = {};
  private String sUnionId = null;
  private String sLastOrder;

  public MemberReport(){

  }

  private void control(IWContext iwc){

    try{

      if(iwc.getSession().getAttribute("golf_union_id")!=null){
        sUnionId = (String)iwc.getSession().getAttribute("golf_union_id");
      }

      if(iwc.getParameter(sAction) == null){
        doMain(iwc);
      }
      if(iwc.getParameter(sAction) != null){
        sActPrm = iwc.getParameter(sAction);
        iAction = Integer.parseInt(sActPrm);
        switch(iAction){
        case ACT1: doUpdate(iwc);     break;
        case ACT2: doTable(iwc);      break;
        case ACT3:                        break;
        case ACT4:                        break;
        default:  doMain(iwc);        break;
        }
      }
    }
    catch(SQLException S){	S.printStackTrace();	}
    }

  private void doMain(IWContext iwc) throws SQLException {

    FramePane pane = new FramePane("Listar");
    pane.setWidth(paneWidth);
    add(pane);

    Form form = new Form();
    pane.add(form);

    Table T = new Table();
    Table T2 = new Table();

    SelectionDoubleBox box = new SelectionDoubleBox(boxName,"Reitir","Val/R��un");
    T.add(box,1,1);
    T.add(T2,4,1);
    form.add(T);

    SelectionBox box1 = box.getLeftBox();
    box1.keepStatusOnAction();
    SelectionBox box2 = box.getRightBox();
    box1.keepStatusOnAction();
    box2.addUpAndDownMovers();
    box1.addMenuElement(CH1,sSelectDisplay[CH1]);
    box1.addMenuElement(CH2,sSelectDisplay[CH2]);
    box1.addMenuElement(CH3,sSelectDisplay[CH3]);
    box1.addMenuElement(CH4,sSelectDisplay[CH4]);
    box1.addMenuElement(CH5,sSelectDisplay[CH5]);
    box1.addMenuElement(CH6,sSelectDisplay[CH6]);
    box1.addMenuElement(CH7,sSelectDisplay[CH7]);
    box1.addMenuElement(CH8,sSelectDisplay[CH8]);
    box1.addMenuElement(CH9,sSelectDisplay[CH9]);

    box1.setHeight(10);
    box2.setHeight(10);
    box2.selectAllOnSubmit();

    T2.add("H�pur",1,1);
    T2.add("Sta�a",1,2);
    T2.add("Kyn",1,3);
    T2.add("Aldur",1,4);
    T2.add("Forgj�f",1,5);

    T2.add(this.drpGroup("list_grp"),2,1);
    T2.add(this.drpStatus("list_status"),2,2);
    T2.add(this.drpGender("list_gender"),2,3);
    T2.add(this.drpInt("list_agefrom","Fr�",120),2,4);
    T2.add(this.drpInt("list_ageto","Til",120),2,4);
    T2.add(this.drpInt("list_hndcpfrom","Fr�",101),2,5);
    T2.add(this.drpInt("list_hndcpto","Til",101),2,5);


    form.add(new SubmitButton("�fram",this.sAction,String.valueOf(ACT1)));
  }

  private void doUpdate(IWContext iwc) throws SQLException{
    String[] selectedValues = iwc.getParameterValues(boxName);
    if(selectedValues!=null){
      int iGroup        = Integer.parseInt(iwc.getParameter("list_grp" ));
      String sStatus    = iwc.getParameter("list_status");
      String sGender    = iwc.getParameter("list_gender");
      int iAgefrom      = Integer.parseInt(iwc.getParameter("list_agefrom"));
      int iAgeto        = Integer.parseInt(iwc.getParameter("list_ageto" ));
      int iHndCpfrom    = Integer.parseInt(iwc.getParameter("list_hndcpfrom"));
      int iHndCpto    = Integer.parseInt(iwc.getParameter("list_hndcpto"));

      Vector vSelect = new Vector();
      Vector vTables = new Vector();
      Vector vJoin = new Vector();
      Vector vWhere = new Vector();
      Vector vOrder = new Vector();

      vSelect.addElement("select distinct ");
      vTables.addElement(" from ");
      vOrder.addElement(" order by ");
      vJoin.addElement(" where ");

      if(sUnionId != null && !sUnionId.equalsIgnoreCase("1")){
        vJoin.addElement( this.sSelectJoin[CH8] );
        vWhere.addElement( "union_member_info.union_id = "+sUnionId );
        vTables.addElement( this.sSelectTables[CH8]);
      }

      for (int i = 0; i < selectedValues.length; i++) {
        int a = Integer.parseInt(selectedValues[i]);
        if(!vSelect.contains(this.sSelectNames[a]))
          vSelect.addElement(this.sSelectNames[a]);
        if(!vTables.contains(this.sSelectTables[a]))
          vTables.addElement(this.sSelectTables[a]);
        if(!vJoin.contains(this.sSelectJoin[a]))
          vJoin.addElement(this.sSelectJoin[a]);
        if(!vOrder.contains(this.sSelectNames[a]))
          vOrder.addElement(this.sSelectNames[a]);
      }


      if(iGroup != 0){
        if(!vTables.contains("group_member"))
          vTables.addElement("group_member");
        if(!vJoin.contains("member.member_id = group_member.member_id"))
          vJoin.addElement("member.member_id = group_member.member_id");
        vWhere.addElement(" group_member.group_id ='"+iGroup+"' ");
      }
      if(!sStatus.equalsIgnoreCase("0")){
        if(!vTables.contains(sSelectTables[CH8]))
          vTables.addElement(sSelectTables[CH8]);
        vWhere.addElement(" union_member_info.member_status = '"+sStatus+"' ");
      }
      if(!sGender.equalsIgnoreCase("0")){
        if(!vTables.contains(sSelectTables[CH1]))
          vTables.addElement(sSelectTables[CH1]);
        vWhere.addElement(" member.gender = '"+sGender+"' ");
      }
      int thisYear = idegaTimestamp.RightNow().getYear();

      if(iAgefrom != 0){
        if(!vTables.contains(sSelectTables[CH1]))
          vTables.addElement(sSelectTables[CH1]);
        int year = thisYear - iAgefrom  ;
        vWhere.addElement(" member.date_of_birth <= '"+year+"-01-01'");
      }
      if(iAgeto != 0){
        if(!vTables.contains(sSelectTables[CH1]))
          vTables.addElement(sSelectTables[CH1]);
        int year = thisYear - iAgeto ;
        vWhere.addElement(" member.date_of_birth >= '"+year+"-01-01'");
      }
      if(iHndCpfrom != 0){
        if(!vTables.contains(sSelectTables[CH6])){
          vTables.addElement(sSelectTables[CH6]);
          vJoin.addElement(sSelectJoin[CH6]);
        }
        vWhere.addElement(" member_info.handicap >= '"+iHndCpfrom+"'");
      }
      if(iHndCpto != 0){
        if(!vTables.contains(sSelectTables[CH6])){
          vTables.addElement(sSelectTables[CH6]);
           vJoin.addElement(sSelectJoin[CH6]);
        }
        vWhere.addElement(" member_info.handicap <= '"+iHndCpto+"'");
      }

      String sql = makeSQL(vSelect,vTables,vJoin,vWhere,vOrder);
      Vector mbs = searchInDatabase(sql);
      iwc.getSession().setAttribute("mbsvector",mbs);
      iwc.getSession().setAttribute("icols",iCols);
      add(new Link("Listi","/list"));
      add(Text.getBreak());
      add("Fj�ldi "+mbs.size());
      OrderVector(mbs,1,false);
      add(this.makeMemberTable(this.makeMemberStrings(mbs)));
    }
  }

  private void doTable(IWContext iwc){
    if(iwc.getSession().getAttribute("mbsvector")!=null){
      Vector mbs = (Vector) iwc.getSession().getAttribute("mbsvector");
      if(iwc.getSession().getAttribute("lastorder")!=null)
        this.sLastOrder = (String) iwc.getSession().getAttribute("lastorder");
      else
        this.sLastOrder = "";
      if(iwc.getSession().getAttribute("icols")!=null){
      iCols = (int[]) iwc.getSession().getAttribute("icols");
        if(iwc.getParameter("order")!= null){
          String sOrd = iwc.getParameter("order");
          boolean reverse = false;
          if(this.sLastOrder.equalsIgnoreCase(sOrd))
            reverse = true;
          int order = Integer.parseInt(iwc.getParameter("order"));

          OrderVector(mbs,order,reverse);
          iwc.getSession().setAttribute("lastorder",sOrd);
          add(new Link("Listi","/list"));
          add(Text.getBreak());
          add("Fj�ldi "+mbs.size());
          add(this.makeMemberTable(this.makeMemberStrings(mbs)));
        }
      }
    }
  }

  private void OrderVector(Vector mbs,int order,boolean reverse){
    int sortint = ReportMemberComparator.NAME;
    switch (order) {
      case F: sortint = ReportMemberComparator.NAME;     break;
      case S: sortint = ReportMemberComparator.SOCIAL;   break;
      case E: sortint = ReportMemberComparator.EMAIL;    break;
      case G: sortint = ReportMemberComparator.ADDRESS;  break;
      case H: sortint = ReportMemberComparator.HANDICAP; break;
      case B: sortint = ReportMemberComparator.BALANCE;  break;
      case C: sortint = ReportMemberComparator.LOCKER;   break;
      case U: sortint = ReportMemberComparator.STATUS;   break;
    }
    ReportMemberComparator RMC = new ReportMemberComparator(sortint);
    if(reverse)
      Collections.reverse(mbs);
    else
      Collections.sort(mbs,RMC);
  }

  private Table makeMemberTable(String[][] s){
    Table T= new Table();
    for(int j = 0; j < iCols.length ;j++){
      Link L = new Link(getReportMemberDisplay(j));
      L.addParameter(this.sAction,this.ACT2);
      L.addParameter("order",iCols[j]);
      T.add(L,j+1,1);
    }
    for(int i =1; i< s.length;i++){
        for(int j = 0; j < s[i].length;j++){
          T.add(s[i][j],j+1,i+2);
      }
    }
    return T;
  }

  private String[][] makeMemberStrings(Vector members){
    int len = members.size();
    String[][] s = new String[len][iCols.length];
    ReportMember rm;
    for(int i = 0; i < len; i++){
      rm = (ReportMember)members.elementAt(i);
      for(int j = 0; j < iCols.length ;j++){
        s[i][j] = getReportMemberString(rm,j);
      }
    }
    return s;
  }

  private String getReportMemberDisplay(int col){
    int colnr = iCols[col];
    String rs = "";
    switch (colnr) {
      case F: rs = "Nafn";        break;
      case S: rs = "Kennitala";   break;
      case E: rs = "NetFang";     break;
      case G: rs = "Heimili";     break;
      case H: rs = "Forgj�f";     break;
      case B: rs = "SkuldaSta�a"; break;
      case C: rs = "Sk�pur";      break;
      case U: rs = "Sta�a";       break;
    }
    return rs;
  }

  private String getReportMemberString(ReportMember rm,int col){
    int colnr = iCols[col];
    String rs = "";
    switch (colnr) {
      case F: rs = rm.getName();                    break;
      case S: rs = rm.getSocial();                  break;
      case E: rs = rm.getEmail();                   break;
      case G: rs = rm.getAddress();                 break;
      case H: rs = rm.getHandicap().toString();     break;
      case B: rs = String.valueOf(rm.getBalance()); break;
      case C: rs = rm.getLocker();                  break;
      case U: rs = getStatus(rm.getStatus());       break;
    }
    return rs!=null?rs:"";
  }

  private String makeSQL(Vector Select,Vector From,Vector Join,Vector Where,Vector Order){
    StringBuffer sql = new StringBuffer();
    int len = Select.size();
    for(int i = 0; i < len ; i++){
      sql.append(Select.elementAt(i));
      if(i > 0 && i < len-1)
        sql.append(",");
    }
    sql.append(" ");
    len = From.size();
    for(int i = 0; i < len ; i++){
      sql.append(From.elementAt(i));
      if(i > 0 && i < len-1)
        sql.append(",");
    }
    sql.append(" ");

    len = Join.size();
    int wlen = Where.size();
    if(len > 1 || wlen >0){
      for(int i = 0; i < len ; i++){
        sql.append(Join.elementAt(i));
        if(i > 1 && i < len-1)
          sql.append(" and ");
      }
    }

    int old = len;
    sql.append(" ");
    len = Where.size();
    if(len > 0 && old > 2)
    if(old > 2 && len > 0)    sql.append(" and ");
    for(int i = 0; i < len ; i++){
      sql.append(Where.elementAt(i));
      if( i < len-1)
        sql.append(" and ");
    }
    sql.append(" ");
    len = Order.size();
    for(int i = 0; i < len ; i++){
      sql.append(Order.elementAt(i));
      if(i > 0 && i < len-1 )
        sql.append(",");
    }

    //add(sql.toString());
    return sql.toString();
  }

  private Vector searchInDatabase(String SQL){
    Vector Members = new Vector();
    Connection Conn = null;
    try{
      Conn = getConnection();
      Statement stmt = Conn.createStatement();
      ResultSet RS = stmt.executeQuery(SQL);
      ResultSetMetaData MD = RS.getMetaData();
      int columnCount = MD.getColumnCount();

      ReportMember rm;
      this.iCols  = new int[columnCount];
      String colName;
      for(int i = 0; i < columnCount; i++){
        colName = MD.getColumnName(i+1);
        for(int j = 0; j < this.sCols.length;j++){
          if(colName.equalsIgnoreCase(this.sCols[j]))
            iCols[i] = j;
        }
      }
      while(RS.next()){

        String f="",m="",l="",s="",e="",g="",n="",p="",c="",u="";
        float h = -1;
        int b = 0;

        for(int i = 0; i < columnCount; i++){
          int colnr = iCols[i];
          switch (colnr) {
            case F: f = RS.getString(sCols[F]);   break;
            case M: m = RS.getString(sCols[M]);   break;
            case L: l = RS.getString(sCols[L]);   break;
            case S: s = RS.getString(sCols[S]);   break;
            case E: e = RS.getString(sCols[E]);   break;
            case G: g = RS.getString(sCols[G]);   break;
            case N: n = RS.getString(sCols[N]);   break;
            case H: h = RS.getFloat(sCols[H]);    break;
            case B: b = RS.getInt(sCols[B]);      break;
            case C: c = RS.getString(sCols[C]);   break;
            case U: u = RS.getString(sCols[U]);   break;

          }
        }
        rm = new ReportMember(f,m,l,s,g,e,b,h,p,c,u);
        Members.addElement(rm);

      }
      RS.close();
      stmt.close();
    }
    catch(SQLException e) {
      e.printStackTrace();
    }
    finally {
      freeConnection(Conn);
    }
    return Members;
  }

  private String getStatus(String status){
    if(status.equalsIgnoreCase("I"))
      return "�virkur";
    else if(status.equalsIgnoreCase("A"))
      return "Virkur";
    else if(status.equalsIgnoreCase("W"))
      return "� bi�";
    else if(status.equalsIgnoreCase("Q"))
      return "H�ttur";
    else if(status.equalsIgnoreCase("D"))
      return "L�tinn";
    else return "";
  }

  private DropdownMenu drpGroup(String sPrm) throws SQLException{
    Group[] group = (Group[]) (new Group()).findAll();
    DropdownMenu drp = new DropdownMenu(group,sPrm);
    drp.addDisabledMenuElement("0","-");
    drp.setSelectedElement("0");
    return drp;
  }
  private DropdownMenu drpHandicapGroup(String sPrm) throws SQLException{
    DropdownMenu drp = new DropdownMenu(sPrm);
    drp.addDisabledMenuElement("0","-");
    drp.addMenuElement("1","Meistaraflokkur");
    drp.addMenuElement("2","1.Flokkur");
    drp.addMenuElement("3","2.Flokkur");
    drp.addMenuElement("4","3.Flokkur");
    drp.addMenuElement("5","4.Flokkur");
    drp.addMenuElement("6","5.Flokkur");
    return drp;
  }
  private DropdownMenu drpStatus(String sPrm) throws SQLException{
    DropdownMenu drp = new DropdownMenu(sPrm);
    drp.addDisabledMenuElement("0","-");
    drp.addMenuElement("A","Virkur");
    drp.addMenuElement("I","�virkur");
    drp.addMenuElement("W","� bi�");
    drp.addMenuElement("Q","H�ttur");
    drp.addMenuElement("D","L�tinn");
    return drp;
  }

  private DropdownMenu drpGender(String sPrm) throws SQLException{
    DropdownMenu drp = new DropdownMenu(sPrm);
    drp.addDisabledMenuElement("0","-");
    drp.addMenuElement("M","Karlar");
    drp.addMenuElement("F","Konur");
    return drp;
  }

  private DropdownMenu drpInt(String sPrm,String init,int count) throws SQLException{
    DropdownMenu drp = new DropdownMenu(sPrm);
    drp.addDisabledMenuElement("0",init);
    for(int i = 1; i < count; i++){
      drp.addMenuElement(String.valueOf(i));
    }
    return drp;
  }

  private DropdownMenu drpZip() throws SQLException{
    ZipCode[] zips = (ZipCode[]) (new ZipCode()).findAllOrdered("code");
    DropdownMenu drp = new DropdownMenu("list_zip");
    drp.addDisabledMenuElement("0","P�stnr");
    for(int i = 0; i < zips.length ; i++){
      drp.addMenuElement(zips[i].getCode());
    }
    return drp;
  }

  public void main(IWContext iwc) {
    /* try{
      isAdmin = com.idega.jmodule.login.business.AccessControl.isAdmin(iwc);
    }
    catch(SQLException e){
      isAdmin = false;
    }
    */
    isAdmin = true;
    control(iwc);
  }
}//class MemberReport


