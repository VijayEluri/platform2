package is.idega.idegaweb.golf.service;

import is.idega.idegaweb.golf.entity.*;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.util.*;
import com.idega.util.text.Name;
import com.idega.util.text.*;
import java.util.*;
import java.sql.Date;
import java.sql.*;
import java.io.*;

import javax.ejb.FinderException;

import is.idega.idegaweb.golf.*;
import com.idega.util.*;
import com.idega.data.*;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author       �gir
 * @version 1.0
 */

public class FamilyDisconnecterWindow extends com.idega.presentation.ui.Window{

  private final String STORE_NAME = "STOREFAMILY";
  private UnionMemberInfo uniMemInfo = null;
  private String headerText = "Sl�ta fj�lskyldutengsl";
  private int unionId;


  public FamilyDisconnecterWindow(int memberId, int unionId)throws java.sql.SQLException, FinderException {

      setTitle("Finna fj�lskyldu");
      this.unionId = unionId;
      uniMemInfo  = ((MemberHome) IDOLookup.getHomeLegacy(Member.class)).findByPrimaryKey(memberId).getUnionMemberInfo(unionId);
  }

  public void main(IWContext modinfo) {
      this.empty();
      add(getInputTable(modinfo));
  }

  public Form getInputTable(IWContext modinfo){
      Form form = new Form();
      form.setMethod("get");
      try {


          String strStore = modinfo.getRequest().getParameter(STORE_NAME+".x");

          HeaderTable hTable = new HeaderTable();
          hTable.setHeaderText(headerText);

          Table table = new Table(1, 3);

          Text t = new Text("Ertu viss ?", true, false , false);
          t.setFontColor("red");
          table.setRowAlignment(1, "center");
          table.add(t, 1, 1);

          Table buttonTable = new Table(2,1);

          buttonTable.add(new SubmitButton(new Image("/pics/formtakks/skra.gif"), STORE_NAME, "storeval"), 1, 1);
          buttonTable.add(new CloseButton(new Image("/pics/formtakks/loka.gif")), 2, 1);

          table.add(buttonTable, 1, 3);
          if(strStore != null) {

              store(modinfo);
              close();
              setParentToReload();
          }
          hTable.add(table);
          form.add(hTable);
      }
      catch(Exception e) {
          System.err.println("\n\nVilla i getInputTable\n\n");
          e.printStackTrace();
      }
      return form;
  }

  private void store(IWContext modinfo)throws SQLException, IOException {

      Family family = (Family) IDOLookup.createLegacy(Family.class);
      family.insert();

      System.err.println("\n\nN� fj�lsk. "+ family.getID());
      System.err.println("\n\nGamla fj�lsk. "+ uniMemInfo.getFamilyId());

      uniMemInfo.setFamily(family);
      uniMemInfo.update();

      System.err.println("\n\nEftir breytingu. "+ uniMemInfo.getFamilyId());
  }

}