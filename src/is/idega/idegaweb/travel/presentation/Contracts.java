package is.idega.idegaweb.travel.presentation;

import javax.ejb.*;
import java.rmi.RemoteException;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.idegaweb.*;
import is.idega.idegaweb.travel.business.TravelStockroomBusiness;
import java.util.*;
import com.idega.core.data.*;
import com.idega.block.trade.stockroom.data.*;
import com.idega.block.trade.stockroom.business.*;
import java.sql.SQLException;
import is.idega.idegaweb.travel.data.*;
import is.idega.idegaweb.travel.business.*;
import com.idega.util.idegaTimestamp;
import com.idega.block.trade.data.Currency;
import com.idega.data.*;

/**
 * Title:        idegaWeb TravelBooking
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class Contracts extends TravelManager {
  private IWBundle bundle;
  private IWResourceBundle iwrb;

  private Supplier supplier;
  private Reseller reseller;
  private TravelStockroomBusiness tsb;

  private String sAction = "contractAction";
  private String parameterAssignReseller = "contractAssignReseller";
  private String parameterResellerId = "contractResellerId";
  private String parameterProductId = "contractProductId";
  private String parameterSaveProductInfo = "contractSaveProductInfo";
  private String parameterContractId = "contractContractId";
  private String parameterAddReseller = "contractAddReseller";
  private String parameterAddResellerSave = "contractAddResellerSave";
  private String parameterCheckBox = "parameterCheckBox";
  private String paramaterDeleteContract = "parameterDeleteContract";

  private String parameterSupplierId = "parameterSupplierId";
  private String parameterViewContract = "parameterViewContract";
  private String parameterViewProducts = "parameterViewProducts";

  Iterator resellers = null;
  Supplier[] suppliers = {};

  public Contracts() {
  }

  public void main(IWContext iwc) throws Exception{
    super.main(iwc);
    initialize(iwc);

    if (super.isLoggedOn(iwc)) {
        String action = iwc.getParameter(this.sAction);
            if (action == null) action = "";

        if (action.equals("")) {
          mainMenu(iwc);
        }else if (action.equals(this.parameterAssignReseller)) {
          assignReseller(iwc);
        }else if (action.equals(this.parameterSaveProductInfo)) {
          saveProductInfo(iwc);
          assignReseller(iwc);
        }else if (action.equals(this.parameterAddReseller)) {
          selectReseller(iwc);
        }else if (action.equals(this.parameterAddResellerSave)) {
          addResellers(iwc);
          selectReseller(iwc);
        }else if (action.equals(this.parameterViewContract)) {
          add(viewContract(iwc, ProductBusiness.getProduct(Integer.parseInt(iwc.getParameter(this.parameterProductId)))));
        }else if (action.equals(this.parameterViewProducts)) {
          assignReseller(iwc);
          //add(viewProducts(iwc));
        }else if (action.equals(this.paramaterDeleteContract)) {
          deleteContract(iwc);
          assignReseller(iwc);
        }
    }else {
      add(super.getLoggedOffTable(iwc));
    }


  }

  public void initialize(IWContext iwc) throws RemoteException{
      bundle = super.getBundle();
      iwrb = super.getResourceBundle();
      supplier = super.getSupplier();
      reseller = super.getReseller();
      tsb  = getTravelStockroomBusiness(iwc);

      //if (supplier != null)
      resellers = getResellers();

      if (reseller != null) {
        suppliers = super.getContractBusiness(iwc).getSuppliersWithContracts(reseller.getID(), com.idega.block.trade.stockroom.data.SupplierBMPBean.getColumnNameName());
//        suppliers = ResellerManager.getSuppliersWithContracts(reseller.getID(), com.idega.block.trade.stockroom.data.SupplierBMPBean.getColumnNameName() );
      }

  }

  private Iterator getResellers() {
    Iterator returner = com.idega.util.ListUtil.getEmptyList().iterator();
    if (supplier != null) {
     returner = ResellerManager.getResellers(supplier,com.idega.block.trade.stockroom.data.ResellerBMPBean.getColumnNameName());
    } else if (reseller != null) {
     returner = ResellerManager.getResellerChilds(reseller,com.idega.block.trade.stockroom.data.ResellerBMPBean.getColumnNameName());
    }
    return returner;
  }

  public void mainMenu(IWContext iwc) {
      Form form = new Form();
      /*
      ShadowBox sb = new ShadowBox();
        sb.add(form);
        sb.setWidth("90%");
      */

      form.add(Text.BREAK);
      Table linkTable = new Table(1,1);
        linkTable.setAlignment("center");
        linkTable.setAlignment(1,1,"left");
        linkTable.setWidth("90%");

        linkTable.add(Text.NON_BREAKING_SPACE);

      Link addReseller = new Link(iwrb.getImage("buttons/add.gif"));
        addReseller.addParameter(this.sAction,this.parameterAddReseller);
        linkTable.add(addReseller,1,1);

      //if (supplier != null)
//      form.add(linkTable);

      form.add(Text.BREAK);

      Table table = new Table();
        form.add(table);
        table.setAlignment("center");
        table.setWidth("90%");
        table.setCellspacing(1);
        table.setColor(super.WHITE);

      int row = 1;

      Text resName;
      Text refNum;
      Link assign;
      Link edit;

      resName = (Text) theText.clone();
        resName.setBold();
        resName.setText(iwrb.getLocalizedString("travel.reseller","Reseller"));
      refNum = (Text) theText.clone();
        refNum.setBold();
        refNum.setText(iwrb.getLocalizedString("travel.reference_number","Reference number"));
      table.add(resName,1,row);
      table.add(refNum,2,row);
      table.add(Text.NON_BREAKING_SPACE,3,row);
      table.setRowColor(row, super.backgroundColor);


      String theColor = super.GRAY;
      //if (supplier != null) {
      Reseller tempReseller = null;

        while (resellers.hasNext()) {
          tempReseller = (Reseller) resellers.next();
        //}
        //for (int i = 0; i < resellers.length; i++) {
            ++row;
            resName = (Text) theText.clone();
              resName.setFontColor(super.BLACK);
              resName.setText(tempReseller.getName());
            refNum = (Text) theText.clone();
              refNum.setText(tempReseller.getReferenceNumber());
              refNum.setFontColor(super.BLACK);
            assign = new Link(iwrb.getImage("buttons/closer.gif"));
              assign.addParameter(this.sAction,this.parameterAssignReseller);
              assign.addParameter(this.parameterResellerId,tempReseller.getID());

            table.add(resName,1,row);
            table.add(refNum,2,row);
            table.add(assign,3,row);
            table.setRowColor(row,theColor);
            table.setAlignment(3,row,"right");

        }
      //}else
       if (reseller != null) {
        if (reseller.getParent() == null) {
          ++row;
          table.setRowColor(row, super.backgroundColor);
          Text suppName = (Text) theText.clone();
            suppName.setBold();
            suppName.setText(iwrb.getLocalizedString("travel.supplier","Supplier"));
          table.add(suppName, 1,row);

          for (int i = 0; i < suppliers.length; i++) {
              ++row;
              resName = (Text) theText.clone();
                resName.setFontColor(super.BLACK);
                resName.setText(suppliers[i].getName());
              assign = new Link(iwrb.getImage("buttons/closer.gif"));
                assign.addParameter(this.sAction,this.parameterViewProducts);
                assign.addParameter(this.parameterSupplierId,suppliers[i].getID());

              table.add(resName,1,row);
              table.add(assign,3,row);
              table.setRowColor(row,theColor);
              table.setAlignment(3,row,"right");
          }
        }else {
          ++row;
          Reseller parent = (Reseller) reseller.getParent();
          table.setRowColor(row, super.backgroundColor);
          Text suppName = (Text) theText.clone();
            suppName.setBold();
            suppName.setText(iwrb.getLocalizedString("travel.Reseller","Reseller"));
          table.add(suppName, 1,row);

          ++row;
          resName = (Text) theText.clone();
            resName.setFontColor(super.BLACK);
            resName.setText(parent.getName());
          assign = new Link(iwrb.getImage("buttons/closer.gif"));
            assign.addParameter(this.sAction,this.parameterAssignReseller);
            assign.addParameter(this.parameterResellerId,parent.getID());

          table.add(resName,1,row);
          table.add(assign,3,row);
          table.setRowColor(row,theColor);
          table.setAlignment(3,row,"right");
        }

      }

      add(form);
  }


  private void assignReseller(IWContext iwc) throws SQLException, RemoteException, FinderException {
    String sProductId = iwc.getParameter(this.parameterProductId);
    int productId = -1;
    if (sProductId != null) productId = Integer.parseInt(sProductId);

    String sResellerId = iwc.getParameter(this.parameterResellerId);

    int resellerId = -1;
    Reseller tReseller = null;
    if (supplier != null) {
      if (sResellerId != null) resellerId = Integer.parseInt(sResellerId);
      tReseller = ((com.idega.block.trade.stockroom.data.ResellerHome)com.idega.data.IDOLookup.getHomeLegacy(Reseller.class)).findByPrimaryKeyLegacy(Integer.parseInt(sResellerId));
    }else if (reseller != null) {
      if (sResellerId != null) {
        resellerId = Integer.parseInt(sResellerId);
      }else {
        resellerId = reseller.getID();
      }
      tReseller = ((com.idega.block.trade.stockroom.data.ResellerHome)com.idega.data.IDOLookup.getHomeLegacy(Reseller.class)).findByPrimaryKeyLegacy(resellerId);
    }

//    if (reseller != null) tReseller = reseller;

    Form form = new Form();
    Table table = new Table();
      table.setAlignment("center");
      table.setWidth("90%");
      table.setBorder(0);
      table.setCellspacing(1);
      table.setCellpadding(0);
      table.setColor(super.WHITE);
      form.add(table);


    Product[] products = null;
    String supplier_id = null;
    if (supplier != null) {
      products = tsb.getProducts(supplier.getID());
    }else if (reseller != null) {
      supplier_id = iwc.getParameter(this.parameterSupplierId);
      if (supplier_id == null) {
        supplier_id = "-1";
      }/*
      Reseller res = (Reseller) reseller.getParent();
      if (res == null) {
        products = ResellerManager.getProductsWithContracts(reseller.getID(), Integer.parseInt(supplier_id), com.idega.block.trade.stockroom.data.ProductBMPBean.getColumnNameProductName());
      }else {
        products = ResellerManager.getProductsWithContracts(res, reseller.getID(), com.idega.block.trade.stockroom.data.ProductBMPBean.getColumnNameProductName());
      }*/
      products = super.getContractBusiness(iwc).getProductsWithContracts(reseller, com.idega.block.trade.stockroom.data.ProductBMPBean.getColumnNameProductName());
//      products = ResellerManager.getProductsWithContracts(reseller, com.idega.block.trade.stockroom.data.ProductBMPBean.getColumnNameProductName());
      //products = tsb.getProducts(Integer.parseInt(supplier_id));
    }
//    System.err.println("products.length = "+products.length);

    int[] serviceDays;
    int row = 1;


    Text header = (Text) theBoldText.clone();
      header.setText(tReseller.getName());
    table.add(header,1,row);
    table.setRowColor(row, super.backgroundColor);
    table.mergeCells(1,row,4,row+1);


    Text pName;
    Text pIsActive;

    Timeframe timeframe;

    Link closerLook = new Link(iwrb.getImage("buttons/closer.gif"));
      closerLook.addParameter(this.sAction, this.parameterAssignReseller);
      if (tReseller != null) {
        closerLook.addParameter(this.parameterResellerId , tReseller.getID());
      }
      if (reseller != null) {
        closerLook.addParameter(this.parameterSupplierId, supplier_id);
      }

    Link closeLook = new Link(iwrb.getImage("buttons/close.gif"));
      closeLook.addParameter(this.sAction, this.parameterAssignReseller);
      if (tReseller != null) {
        closeLook.addParameter(this.parameterResellerId , tReseller.getID());
      }
      if (reseller != null) {
        closeLook.addParameter(this.parameterSupplierId, supplier_id);
      }
    Link temp;

    ++row;
    table.setAlignment(4,row,"left");
    String theColor = super.GRAY;

    for (int i = 0; i < products.length; i++) {
        ++row;
        table.setRowColor(row, theColor);
        pName = (Text) theBoldText.clone();
          pName.setText(ProductBusiness.getProductNameWithNumber(products[i]));
          pName.setFontColor(super.BLACK);

        table.add(pName,1,row);
//        table.mergeCells(1,row,3,row);

//          if (supplier != null) {
            if (super.getContractBusiness(iwc).isActiveContract(tReseller.getID(), products[i].getID()) ) {
//            if (ResellerManager.isActiveContract( tReseller.getID(), products[i].getID())) {
//            if (ResellerManager.isActiveContract(supplier.getID() , tReseller.getID(), products[i].getID())) {
              pIsActive = (Text) theBoldText.clone();
                pIsActive.setFontColor(BLACK);
                pIsActive.setText(iwrb.getLocalizedString("travel.active_contract","Active contract"));
                pIsActive.addToText(Text.NON_BREAKING_SPACE );
              table.add(pIsActive, 3, row);
              table.setAlignment(3,row,"right");
              table.mergeCells(1,row,2,row);
            }else {
              table.mergeCells(1,row,3,row);
            }
//          }

        if (products[i].getID() == productId) {
//          table.mergeCells(1,row,3,row);
            form.add(new HiddenInput(this.parameterResellerId , Integer.toString(tReseller.getID())));
            form.add(new HiddenInput(this.parameterProductId , Integer.toString(products[i].getID())));

            table.setAlignment(4,row,"right");
            temp = (Link) closeLook.clone();
            table.add(temp,4,row);
            table.add(Text.NON_BREAKING_SPACE,4,row);

            ++row;
            table.mergeCells(1,row,4,row);
            table.add(viewContract(iwc,products[i]),1,row);
        }else {
          temp = (Link) closerLook.clone();
            temp.addParameter(this.parameterProductId, products[i].getID());
          table.setAlignment(4,row,"right");
          table.add(temp,4,row);
          table.add(Text.NON_BREAKING_SPACE,4,row);
        }
        //table.setRowColor(row, super.DARKBLUE);
    }





    add(Text.getBreak());
    add(form);
  }

  private void saveProductInfo(IWContext iwc) {
    String sResellerId = iwc.getParameter(this.parameterResellerId);
    String sProductId = iwc.getParameter(this.parameterProductId);
    String sContractId = iwc.getParameter(this.parameterContractId);

    String discount = iwc.getParameter("discount");
      if (discount.equals("")) discount = "0";
    String alotment = iwc.getParameter("alotment");
      if (alotment.equals("")) alotment = "0";

    String allDays = iwc.getParameter("all_days");
    String mondays = iwc.getParameter("mondays");
    String tuesdays = iwc.getParameter("tuesdays");
    String wednesdays = iwc.getParameter("wednesdays");
    String thursdays = iwc.getParameter("thursdays");
    String fridays = iwc.getParameter("fridays");
    String saturdays = iwc.getParameter("saturdays");
    String sundays = iwc.getParameter("sundays");

    String activeFrom = iwc.getParameter("from");
    String activeTo = iwc.getParameter("to");
    String valid = iwc.getParameter("valid");
      if (valid.equals("")) valid = "0";

    javax.transaction.TransactionManager tm = com.idega.transaction.IdegaTransactionManager.getInstance();

    try {
        tm.begin();
        int resellerId = Integer.parseInt(sResellerId);
        int productId = Integer.parseInt(sProductId);
        int contractId = -1;
        if (sContractId != null) contractId = Integer.parseInt(sContractId);


        Service service = ((is.idega.idegaweb.travel.data.ServiceHome)com.idega.data.IDOLookup.getHome(Service.class)).findByPrimaryKey(new Integer(productId));
        Reseller reseller = ((com.idega.block.trade.stockroom.data.ResellerHome)com.idega.data.IDOLookup.getHomeLegacy(Reseller.class)).findByPrimaryKeyLegacy(resellerId);

        idegaTimestamp from = new idegaTimestamp(activeFrom);
        idegaTimestamp to = new idegaTimestamp(activeTo);


        Contract contract;
        if (contractId != -1) {
          contract = ((is.idega.idegaweb.travel.data.ContractHome)com.idega.data.IDOLookup.getHome(Contract.class)).findByPrimaryKey(new Integer(contractId));
        }else {
          contract = ((is.idega.idegaweb.travel.data.ContractHome)com.idega.data.IDOLookup.getHome(Contract.class)).create();
        }

          contract.setAlotment(Integer.parseInt(alotment));
          contract.setDiscount(discount);
          contract.setServiceId(productId);
          contract.setResellerId(resellerId);
          contract.setFrom(from.getTimestamp());
          contract.setTo(to.getTimestamp());
          contract.setExpireDays(Integer.parseInt(valid));

        if (contractId != -1) {
          contract.store();
          tsb.removeResellerHashtables(iwc);
        }else {
          contract.store();
        }


        int[] tempDays = new int[7];
        int counter = 0;
          if (allDays != null) {
            try {
              ServiceDayHome sdayHome = (ServiceDayHome) IDOLookup.getHome(ServiceDay.class);
              ServiceDay sDay = sdayHome.create();
              tempDays = sDay.getDaysOfWeek(productId);
            }catch (Exception e) {
              e.printStackTrace(System.err);
            }
            //tempDays = is.idega.idegaweb.travel.data.ServiceDayBMPBean.getDaysOfWeek(productId);
            counter = tempDays.length;
          }else {
            if (sundays != null) tempDays[counter++] = java.util.GregorianCalendar.SUNDAY;
            if (mondays != null) tempDays[counter++] = java.util.GregorianCalendar.MONDAY;
            if (tuesdays != null) tempDays[counter++] = java.util.GregorianCalendar.TUESDAY;
            if (wednesdays != null) tempDays[counter++] = java.util.GregorianCalendar.WEDNESDAY;
            if (thursdays != null) tempDays[counter++] = java.util.GregorianCalendar.THURSDAY;
            if (fridays != null) tempDays[counter++] = java.util.GregorianCalendar.FRIDAY;
            if (saturdays != null) tempDays[counter++] = java.util.GregorianCalendar.SATURDAY;
          }

        ResellerDayHome resDayHome = (ResellerDayHome) IDOLookup.getHome(ResellerDay.class);
//        ResellerDay resDay = resDayHome.findByPrimaryKey(new Integer(resellerId));
        Collection tempToDelete = resDayHome.findResellerDays(reseller, service);
        Iterator iter = tempToDelete.iterator();
        while (iter.hasNext()) {
          ResellerDay item = (ResellerDay) iter.next();
          item.remove();
        }
/*        ResellerDay[] oldToDelete = (ResellerDay[]) resDay.findAllByColumn(is.idega.idegaweb.travel.data.ResellerDayBMPBean.getColumnNameResellerId() , Integer.toString(resellerId), is.idega.idegaweb.travel.data.ResellerDayBMPBean.getColumnNameServiceId(), Integer.toString(productId));
        for (int i = 0; i < oldToDelete.length; i++) {
          oldToDelete[i].delete();
        }
*/
        ResellerDay resDay;
        int[] activeDays = new int[counter];
        System.arraycopy(tempDays,0,activeDays,0,counter);
        for (int i = 0; i < activeDays.length; i++) {
          resDay = ((is.idega.idegaweb.travel.data.ResellerDayHome)com.idega.data.IDOLookup.getHome(ResellerDay.class)).create();
            resDay.setDayOfWeek(resellerId, productId, activeDays[i]);
          resDay.store();
        }

        tm.commit();
    }catch (Exception e) {
        e.printStackTrace(System.err);
        try {
          tm.rollback();
        }catch (javax.transaction.SystemException se) {
          se.printStackTrace(System.err);
        }
    }

  }

  public void selectReseller(IWContext iwc) {
    Form form = new Form();
    Table table = new Table();
      form.add(table);
      table.setCellspacing(1);
      table.setColor(super.WHITE);
      table.setWidth("90%");

    Text nameTxt = (Text) theText.clone();
      nameTxt.setText(iwrb.getLocalizedString("travel.name","Name"));
      nameTxt.setFontColor(super.WHITE);

    Text addTxt = (Text) theText.clone();
      addTxt.setText(iwrb.getLocalizedString("travel.add","Add"));
      addTxt.setFontColor(super.WHITE);

    try {
//      Reseller[] tResellers = (Reseller[]) (com.idega.block.trade.stockroom.data.ResellerBMPBean.getStaticInstance(Reseller.class)).findAllOrdered(com.idega.block.trade.stockroom.data.ResellerBMPBean.getColumnNameName());
      List tResellers = null;
      if (reseller != null) tResellers = ResellerManager.getResellersAvailable(reseller, com.idega.block.trade.stockroom.data.ResellerBMPBean.getColumnNameName());
      if (supplier != null) tResellers = EntityFinder.findAllOrdered(com.idega.block.trade.stockroom.data.ResellerBMPBean.getStaticInstance(Reseller.class),com.idega.block.trade.stockroom.data.ResellerBMPBean.getColumnNameName());

      if (tResellers == null) tResellers = com.idega.util.ListUtil.getEmptyList();
      Iterator itResellers = this.getResellers();
      List myResellers = new Vector();
      Reseller tReseller;
      while (itResellers.hasNext()) {
        tReseller = (Reseller) itResellers.next();
        myResellers.add(tReseller);
      }
      if (reseller != null) {
        myResellers.remove(reseller);

        if (reseller.getParent() != null) {
          myResellers.remove((Reseller) reseller.getParent());
        }
      }
      /*int[] resellerIds = new int[myResellers.size()];
      for (int i = 0; i < resellerIds.length; i++) {
        resellerIds[i] = ((Reseller) myResellers.get(i)).getID();
      }*/


      int row = 1;

//      Link newReseller = new Link(iwrb.getImage("buttons/newreseller.gif"));
//        newReseller.addParameter(this.sAction,this.parameterNewReseller);

      if (reseller != null) {
        if (reseller.getParent() == null) {
          table.mergeCells(1, row, 2, row);
          //table.add(newReseller,1,row);
          table.setRowColor(row, super.backgroundColor);
          ++row;
        }
      }else if (supplier != null) {
        table.mergeCells(1, row, 2, row);
        //table.add(newReseller,1,row);
        table.setRowColor(row, super.backgroundColor);
        ++row;
    }

      CheckBox box;
      Text nameText;

      table.setRowColor(row, super.backgroundColor);
      table.add(nameTxt,1,row);
      table.add(addTxt,2,row);
      table.setAlignment(2,row,"right");
      table.setRowColor(row, super.backgroundColor);
      int resId;

      Reseller tempReseller;
      Reseller useReseller;

      for (int i = 0; i < tResellers.size(); i++) {
        useReseller = (Reseller) tResellers.get(i);
        resId = useReseller.getID();
        ++row;

        nameText = (Text) nameTxt.clone();
          nameText.setFontColor(super.BLACK);
          nameText.setText(useReseller.getName());
        box = new CheckBox(this.parameterCheckBox+"_"+resId);

        for (int j = 0 ; j < myResellers.size() ; j++) {
          tempReseller = (Reseller) myResellers.get(j);
          if (resId == tempReseller.getID()) {
            box.setChecked(true);
          }
        }/*
        for (int j = 0; j < resellerIds.length; j++) {
          if (resId == resellerIds[j]) box.setChecked(true);
        }*/

        table.setRowColor(row, super.GRAY);
        table.add(new HiddenInput(this.parameterResellerId,Integer.toString(useReseller.getID())));
        table.add(nameText,1,row);
        table.add(box,2,row);
        table.setAlignment(2,row,"right");
      }


    }catch (SQLException sql) {
      sql.printStackTrace(System.err);
    }

    Table tableTwo= new Table(2,1);
      form.add(tableTwo);
      tableTwo.setWidth("90%");
      tableTwo.setAlignment("center");
      tableTwo.setAlignment(2,1,"right");

    SubmitButton saveBtn = new SubmitButton(iwrb.getImage("buttons/save.gif"),this.sAction ,this.parameterAddResellerSave);
      tableTwo.add(saveBtn,2,1);

    Link backLnk = new Link(iwrb.getImage("buttons/back.gif"));
      tableTwo.add(backLnk,1,1);

    add(Text.BREAK);
    add(form);
  }


  private void addResellers(IWContext iwc) {
    String[] ids = iwc.getParameterValues(this.parameterResellerId);
    if (ids == null) ids = new String[0];
    String box;

    Reseller tReseller;
    for (int i = 0; i < ids.length; i++) {
      box = iwc.getParameter(this.parameterCheckBox+"_"+ids[i]);
      try {
        if (box != null) {
          if (supplier != null) {
            supplier.addTo(Reseller.class, Integer.parseInt(ids[i]));
          }
          if (reseller != null) {
            tReseller = ((com.idega.block.trade.stockroom.data.ResellerHome)com.idega.data.IDOLookup.getHomeLegacy(Reseller.class)).findByPrimaryKeyLegacy(Integer.parseInt(ids[i]));
            reseller.addChild(tReseller);
          }
        }else {
          tReseller = ((com.idega.block.trade.stockroom.data.ResellerHome)com.idega.data.IDOLookup.getHomeLegacy(Reseller.class)).findByPrimaryKeyLegacy(Integer.parseInt(ids[i]));
          if (supplier != null) {
            supplier.removeFrom(tReseller);
          }
          if (reseller != null) {
            reseller.removeChild(tReseller);
          }
        }
      }catch (SQLException sql) {
      }
    }

  }


  private Table viewContract(IWContext iwc, Product product) throws RemoteException, FinderException, SQLException {
    int productId = product.getID();
    String sResellerId = iwc.getParameter(this.parameterResellerId);

    int resellerId = -1;
    Reseller tReseller = null;
    if (supplier != null) {
      if (sResellerId != null) resellerId = Integer.parseInt(sResellerId);
      //resellerId = Integer.parseInt(sResellerId);
      tReseller = ((com.idega.block.trade.stockroom.data.ResellerHome)com.idega.data.IDOLookup.getHomeLegacy(Reseller.class)).findByPrimaryKeyLegacy(resellerId);
    }else if (reseller != null) {
      if (sResellerId != null) {
        resellerId = Integer.parseInt(sResellerId);
        tReseller = ((com.idega.block.trade.stockroom.data.ResellerHome)com.idega.data.IDOLookup.getHomeLegacy(Reseller.class)).findByPrimaryKeyLegacy(resellerId);
      }else {
        resellerId = reseller.getID();
        tReseller = reseller;
      }
    }

    Contract contract = null;
    if (product != null) {
      Contract[] contracts = (Contract[]) (is.idega.idegaweb.travel.data.ContractBMPBean.getStaticInstance(Contract.class)).findAllByColumn(is.idega.idegaweb.travel.data.ContractBMPBean.getColumnNameResellerId(), Integer.toString(resellerId), is.idega.idegaweb.travel.data.ContractBMPBean.getColumnNameServiceId(), Integer.toString(productId));
      if (contracts.length > 0) {
        contract = contracts[0];
      }
    }
    Text tNumberOfSeatsPerTour = (Text) theText.clone();
      tNumberOfSeatsPerTour.setText(iwrb.getLocalizedString("travel.number_of_seats_per_tour","Number of seats per tour"));
      tNumberOfSeatsPerTour.addToText("&nbsp;&nbsp;");
    Text tWeekdays = (Text) theText.clone();
      tWeekdays.setText(iwrb.getLocalizedString("travel.weekdays","Weekdays"));
    Text tDiscount = (Text) theText.clone();
      tDiscount.setText(iwrb.getLocalizedString("travel.discount","Discount"));
    Text tTimeframe = (Text) theText.clone();
      tTimeframe.setText(iwrb.getLocalizedString("travel.timeframe","Timeframe"));
    Text tValidUntil = (Text) theText.clone();
      tValidUntil.setText(iwrb.getLocalizedString("travel.valid_until","Valid until"));
    Text tDaysBefore = (Text) theText.clone();
      tDaysBefore.setText(iwrb.getLocalizedString("travel_days_before_departure","days before departure"));
    Text tfFromText = (Text) theText.clone();
      tfFromText.setText(iwrb.getLocalizedString("travel.from","from"));
    Text tfToText = (Text) theText.clone();
      tfToText.setText(iwrb.getLocalizedString("travel.to","to"));

    Text pName = null;
    TextInput pAlot;
    DateInput pFrom = new DateInput("from");
    DateInput pTo = new DateInput("to");
    TextInput pDays;
    TextInput pDiscount;

    Timeframe timeframe;

    Link closerLook = new Link(iwrb.getImage("buttons/closer.gif"));
      closerLook.setFontColor(super.textColor);
      closerLook.addParameter(this.sAction, this.parameterAssignReseller);
      closerLook.addParameter(this.parameterResellerId , tReseller.getID());

    Link closeLook = new Link(iwrb.getImage("buttons/close.gif"));
      closeLook.addParameter(this.sAction, this.parameterAssignReseller);
      closeLook.addParameter(this.parameterResellerId , tReseller.getID());

    Link temp;

            pAlot = new TextInput("alotment");
              pAlot.setSize(3);
            pDays = new TextInput("valid");
              pDays.setSize(3);
            pDiscount = new TextInput("discount");
              pDiscount.setSize(3);

            int[] serviceDays = new int[]{};// = is.idega.idegaweb.travel.data.ServiceDayBMPBean.getDaysOfWeek(productId);
            try {
              ServiceDayHome sdayHome = (ServiceDayHome) IDOLookup.getHome(ServiceDay.class);
              ServiceDay sDay = sdayHome.create();
              serviceDays = sDay.getDaysOfWeek(productId);
            }catch (Exception e) {
              e.printStackTrace(System.err);
            }

          CheckBox allDays = new CheckBox("all_days");
          CheckBox mondays = new CheckBox("mondays");
          CheckBox tuesdays = new CheckBox("tuesdays");
          CheckBox wednesdays = new CheckBox("wednesdays");
          CheckBox thursdays = new CheckBox("thursdays");
          CheckBox fridays = new CheckBox("fridays");
          CheckBox saturdays = new CheckBox("saturdays");
          CheckBox sundays = new CheckBox("sundays");

          Table weekdayFixTable = new Table(9,2);
            weekdayFixTable.setCellpadding(0);
            weekdayFixTable.setCellspacing(1);
            weekdayFixTable.setWidth("350");
            weekdayFixTable.setColumnAlignment(1,"center");
            weekdayFixTable.setColumnAlignment(2,"center");
            weekdayFixTable.setColumnAlignment(3,"center");
            weekdayFixTable.setColumnAlignment(4,"center");
            weekdayFixTable.setColumnAlignment(5,"center");
            weekdayFixTable.setColumnAlignment(6,"center");
            weekdayFixTable.setColumnAlignment(7,"center");
            weekdayFixTable.setColumnAlignment(8,"center");
            weekdayFixTable.setColumnAlignment(9,"center");

            Text alld = (Text) smallText.clone();
                alld.setText(iwrb.getLocalizedString("travel.all_days","All"));
            Text mond = (Text) smallText.clone();
                mond.setText(iwrb.getLocalizedString("travel.mon","mon"));
            Text tued = (Text) smallText.clone();
                tued.setText(iwrb.getLocalizedString("travel.tue","tue"));
            Text wedd = (Text) smallText.clone();
                wedd.setText(iwrb.getLocalizedString("travel.wed","wed"));
            Text thud = (Text) smallText.clone();
                thud.setText(iwrb.getLocalizedString("travel.thu","thu"));
            Text frid = (Text) smallText.clone();
                frid.setText(iwrb.getLocalizedString("travel.fri","fri"));
            Text satd = (Text) smallText.clone();
                satd.setText(iwrb.getLocalizedString("travel.sat","sat"));
            Text sund = (Text) smallText.clone();
                sund.setText(iwrb.getLocalizedString("travel.sun","sun"));


            weekdayFixTable.add(alld,1,1);
            weekdayFixTable.add(mond,3,1);
            weekdayFixTable.add(tued,4,1);
            weekdayFixTable.add(wedd,5,1);
            weekdayFixTable.add(thud,6,1);
            weekdayFixTable.add(frid,7,1);
            weekdayFixTable.add(satd,8,1);
            weekdayFixTable.add(sund,9,1);

            weekdayFixTable.add(allDays,1,2);

            if (getServiceDayHome().getIfDay(productId,is.idega.idegaweb.travel.data.ServiceDayBMPBean.MONDAY))
              weekdayFixTable.add(mondays,3,2);
            if (getServiceDayHome().getIfDay(productId,is.idega.idegaweb.travel.data.ServiceDayBMPBean.TUESDAY))
            weekdayFixTable.add(tuesdays,4,2);
            if (getServiceDayHome().getIfDay(productId,is.idega.idegaweb.travel.data.ServiceDayBMPBean.WEDNESDAY))
            weekdayFixTable.add(wednesdays,5,2);
            if (getServiceDayHome().getIfDay(productId,is.idega.idegaweb.travel.data.ServiceDayBMPBean.THURSDAY))
            weekdayFixTable.add(thursdays,6,2);
            if (getServiceDayHome().getIfDay(productId,is.idega.idegaweb.travel.data.ServiceDayBMPBean.FRIDAY))
            weekdayFixTable.add(fridays,7,2);
            if (getServiceDayHome().getIfDay(productId,is.idega.idegaweb.travel.data.ServiceDayBMPBean.SATURDAY))
            weekdayFixTable.add(saturdays,8,2);
            if (getServiceDayHome().getIfDay(productId,is.idega.idegaweb.travel.data.ServiceDayBMPBean.SUNDAY))
            weekdayFixTable.add(sundays,9,2);

            pFrom = new DateInput("from");
            pTo = new DateInput("to");

            Table infoTable = new Table();
              infoTable.setBorder(0);
              infoTable.setCellspacing(0);
              infoTable.setWidth("100%");
              infoTable.setColor(super.backgroundColor);

            int infoRow = 1;


            if (contract != null) {
                pAlot.setContent(Integer.toString(contract.getAlotment()));
                pDays.setContent(Integer.toString(contract.getExpireDays()) );
                pDiscount.setContent(contract.getDiscount());
                int[] days = {};
                try {
                  days = getResellerDayHome().create().getDaysOfWeek(resellerId, productId);
                }catch (CreateException ce) {
                  throw new FinderException(ce.getMessage());
                }
                for (int j = 0; j < days.length; j++) {
                  if (days[j] == is.idega.idegaweb.travel.data.ResellerDayBMPBean.MONDAY) mondays.setChecked(true);
                  if (days[j] == is.idega.idegaweb.travel.data.ResellerDayBMPBean.TUESDAY) tuesdays.setChecked(true);
                  if (days[j] == is.idega.idegaweb.travel.data.ResellerDayBMPBean.WEDNESDAY) wednesdays.setChecked(true);
                  if (days[j] == is.idega.idegaweb.travel.data.ResellerDayBMPBean.THURSDAY) thursdays.setChecked(true);
                  if (days[j] == is.idega.idegaweb.travel.data.ResellerDayBMPBean.FRIDAY) fridays.setChecked(true);
                  if (days[j] == is.idega.idegaweb.travel.data.ResellerDayBMPBean.SATURDAY) saturdays.setChecked(true);
                  if (days[j] == is.idega.idegaweb.travel.data.ResellerDayBMPBean.SUNDAY) sundays.setChecked(true);
                }

                infoTable.add(new HiddenInput(this.parameterContractId,contract.getPrimaryKey().toString()));
            }


            try {
              if (contract == null) {
                timeframe = tsb.getTimeframe(product);
                pFrom.setDate(new idegaTimestamp(timeframe.getFrom()).getSQLDate());
                pTo.setDate(new idegaTimestamp(timeframe.getTo()).getSQLDate());
              }else {
                pFrom.setDate(new idegaTimestamp(contract.getFrom()).getSQLDate());
                pTo.setDate(new idegaTimestamp(contract.getTo()).getSQLDate());
              }
            }catch (TimeframeNotFoundException tnfe) {
              tnfe.printStackTrace(System.err);
              timeframe = null;
            }catch (ServiceNotFoundException snfe) {
              snfe.printStackTrace(System.err);
              timeframe = null;
            }



            infoTable.add(tDiscount,1,infoRow);
            infoTable.add(pDiscount,3,infoRow);
            if (product != null) {
              Text what = (Text) theText.clone();
              if (product.getDiscountTypeId() == com.idega.block.trade.stockroom.data.ProductBMPBean.DISCOUNT_TYPE_ID_PERCENT) {
                what.setText(" %");
              }else if (product.getDiscountTypeId() == com.idega.block.trade.stockroom.data.ProductBMPBean.DISCOUNT_TYPE_ID_AMOUNT) {
                pDiscount.setSize(10);
                what.setText(" "+((com.idega.block.trade.data.CurrencyHome)com.idega.data.IDOLookup.getHomeLegacy(Currency.class)).findByPrimaryKeyLegacy(tsb.getCurrencyIdForIceland()).getCurrencyAbbreviation());
              }
              infoTable.add(what,3,infoRow);
            }

            ++infoRow;
            infoTable.add(tNumberOfSeatsPerTour,1,infoRow);
            infoTable.add(pAlot,3,infoRow);

            ++infoRow;
            infoTable.add(tWeekdays,1,infoRow);
            infoTable.add(weekdayFixTable,3,infoRow);
            infoTable.mergeCells(3,infoRow,4,infoRow);

            ++infoRow;
            infoTable.add(tTimeframe,1,infoRow);
            infoTable.mergeCells(3,infoRow,4,infoRow);
            infoTable.add(tfFromText,2,infoRow);
            infoTable.add(pFrom,3,infoRow);
            infoTable.add(tfToText,3,infoRow);
            infoTable.add(pTo,3,infoRow);

            ++infoRow;
            infoTable.add(tValidUntil,1,infoRow);
            infoTable.mergeCells(3,infoRow,4,infoRow);
            infoTable.add(pDays,3,infoRow);
            infoTable.add(tDaysBefore,3,infoRow);

            ++infoRow;
            int tResParId = -1;
            if (tReseller.getParent() != null) {
              tResParId = tReseller.getParent().getID();
            }
            //table.setRowColor(row, theColor);
            if (supplier != null || ( (tReseller != null && reseller != null) && tResParId == this.reseller.getID())) {
              if (contract != null) {
                SubmitButton deleter = new SubmitButton(iwrb.getImage("buttons/delete.gif"), this.sAction, this.paramaterDeleteContract);
                infoTable.add(deleter,4,infoRow);
                infoTable.add(Text.NON_BREAKING_SPACE,4,infoRow);

                SubmitButton submit = new SubmitButton(iwrb.getImage("buttons/update.gif"),this.sAction,this.parameterSaveProductInfo);
                infoTable.add(submit,4,infoRow);
                infoTable.add(Text.NON_BREAKING_SPACE,4,infoRow);
              }else {
                SubmitButton submit = new SubmitButton(iwrb.getImage("buttons/save.gif"),this.sAction,this.parameterSaveProductInfo);
                infoTable.add(submit,4,infoRow);
                infoTable.add(Text.NON_BREAKING_SPACE,4,infoRow);
              }
            }
            infoTable.setAlignment(4,infoRow,"right");
            //table.mergeCells(1,row,3,row);
            //table.setRowColor(row, theColor);
      return infoTable;
  }

  private void deleteContract(IWContext iwc) throws RemoteException, FinderException, RemoveException{
    String contractId = iwc.getParameter(this.parameterContractId);
    if (contractId != null) {
      Contract con = ((is.idega.idegaweb.travel.data.ContractHome)com.idega.data.IDOLookup.getHome(Contract.class)).findByPrimaryKey( new Integer( contractId));
        con.remove();
    }
  }

  private ResellerDayHome getResellerDayHome() throws RemoteException {
    ResellerDayHome rdHome = (ResellerDayHome) IDOLookup.getHome(ResellerDay.class);
    return rdHome;
  }

  private ServiceDayHome getServiceDayHome() throws RemoteException{
    ServiceDayHome sdHome = (ServiceDayHome) IDOLookup.getHome(ServiceDay.class);
    return sdHome;
  }

}
