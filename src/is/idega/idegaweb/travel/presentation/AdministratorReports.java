package is.idega.idegaweb.travel.presentation;
import java.util.Vector;
import java.util.List;
import com.idega.util.ListUtil;
import com.idega.block.trade.stockroom.data.SupplierHome;
import com.idega.data.IDOLookup;
import java.sql.SQLException;
import java.rmi.RemoteException;
import com.idega.presentation.ui.*;
import com.idega.block.trade.stockroom.data.Supplier;
import com.idega.presentation.text.*;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.idegaweb.IWResourceBundle;

/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gimmi@idega.is>Gr�mur J�nsson</a>
 * @version 1.0
 */

public class AdministratorReports extends Reports {

  private Supplier _supplier;
  private List _suppliers;
  protected AdministratorReport _report;
//  private static String ACTION = "adRep_ac";
//  private static final String PARAMATER_DATE_FROM = "active_from";
//  private static final String PARAMATER_DATE_TO = "active_to";
  private static final String PARAMETER_ONLINE_REPORT = OnlineBookingReport.class.toString();//"adRep_or";
  private String PARAMETER_SUPPLIER_ID = "adRep_spID";

  public AdministratorReports() {
  }

  public void main(IWContext iwc) throws Exception {
//    super.main(iwc);
    init(iwc);

    add(Text.BREAK);
    String _action = iwc.getParameter(ACTION);
    if (_action == null) {
      reportList(iwc);
    }else {
      Form form = new Form();
        form.maintainParameter(this.ACTION);
      form.add(topTable(iwc));
      form.add(report(iwc));
      form.add(Text.BREAK);
      add(form);
    }

    Table table = new Table();
//      table.setWidth("90%");
      table.add(getBackLink());
    add(table);

  }

  protected void init(IWContext iwc) throws Exception {
    super.init(iwc);

    String action = iwc.getParameter(ACTION);
    if (action == null) action = "";

    if (action.equals(PARAMETER_ONLINE_REPORT)) {
      _report = new OnlineBookingReport(iwc);
    }

    _suppliers = new Vector();
    String suppId = iwc.getParameter(PARAMETER_SUPPLIER_ID);
    if (suppId != null && !suppId.equals("-1")) {
      SupplierHome suppHome = (SupplierHome) IDOLookup.getHome(Supplier.class);
      _supplier = suppHome.findByPrimaryKey(new Integer(suppId));
      _suppliers.add(_supplier);
    }else if (suppId != null && suppId.equals("-1")) {
      Supplier[] supps = new Supplier[]{};
      try {
        supps = com.idega.block.trade.stockroom.data.SupplierBMPBean.getValidSuppliers();
        for (int i = 0; i < supps.length; i++) {
          _suppliers.add(supps[i]);
        }
      }catch (SQLException sql) {
        sql.printStackTrace(System.out);
      }

    }

  }

  protected Table topTable(IWContext iwc) {
      Table topTable = new Table(5,3);
        topTable.setBorder(0);
        topTable.setWidth("90%");

      Text tframeText = (Text) theText.clone();
          tframeText.setText(_iwrb.getLocalizedString("travel.timeframe_only","Timeframe"));
          tframeText.addToText(":");


      Supplier[] supps = new Supplier[]{};
      try {
        supps = com.idega.block.trade.stockroom.data.SupplierBMPBean.getValidSuppliers();
      }catch (SQLException sql) {
        sql.printStackTrace(System.out);
      }
      DropdownMenu trip = new DropdownMenu(supps, PARAMETER_SUPPLIER_ID);

//        trip = ProductBusiness.getDropdownMenuWithProducts(iwc, _supplier.getID(), PARAMETER_PRODUCT_ID);
        if (_supplier != null) {
            trip.setSelectedElement(Integer.toString(_supplier.getID()));
        }
        trip.addMenuElementFirst("-1", _iwrb.getLocalizedString("travel.all_suppliers","All suppliers"));


      DateInput active_from = new DateInput(PARAMATER_DATE_FROM);
          active_from.setDate(_stamp.getSQLDate());

      DateInput active_to = new DateInput(PARAMATER_DATE_TO);
          active_to.setDate(_toStamp.getSQLDate());


      Text nameText = (Text) theText.clone();
          nameText.setText(_iwrb.getLocalizedString("travel.supplier","Supplier"));
          nameText.addToText(":");
      Text timeframeFromText = (Text) theText.clone();
          timeframeFromText.setText(_iwrb.getLocalizedString("travel.from","From"));
          timeframeFromText.addToText(":");
      Text timeframeToText = (Text) theText.clone();
          timeframeToText.setText(_iwrb.getLocalizedString("travel.to","To"));
          timeframeToText.addToText(":");



      topTable.setAlignment(1,1, "right");
      topTable.setAlignment(2,1, "left");
      topTable.setAlignment(3,1, "right");
      topTable.setAlignment(4,1, "left");
      topTable.setAlignment(3,2, "right");
      topTable.setAlignment(4,2, "left");
      topTable.add(nameText,1,1);
      topTable.add(trip,2,1);
      topTable.add(timeframeFromText,3,1);
      topTable.add(active_from,4,1);

      if (_report != null && _report.useTwoDates()) {
        topTable.add(timeframeToText,3,2);
        topTable.add(active_to,4,2);
      }

      topTable.mergeCells(1,2,2,2);

      topTable.setAlignment(1,3,"left");
      topTable.setAlignment(5,3,"right");
      topTable.add(new SubmitButton(_iwrb.getImage("/buttons/get.gif")),5,3);

      return topTable;
  }


  protected void reportList(IWContext iwc) throws Exception {
    Table table = super.getTable();

    OnlineBookingReport obr = new OnlineBookingReport(iwc);

    int row = 1;
    table.add(getHeaderText(_iwrb.getLocalizedString("travel.report","Report")), 1, row);
    table.add(getHeaderText(_iwrb.getLocalizedString("travel.description","Description")), 2, row);
    table.setRowColor(row, super.backgroundColor);


    ++row;
    Link obrLink = new Link(obr.getReportName());
      obrLink.addParameter(ACTION, PARAMETER_ONLINE_REPORT);
    table.add(obrLink, 1, row);
    table.add(getText(obr.getReportDescription()), 2, row);
    table.setRowColor(row, super.GRAY);

    add(table);
  }


  protected Table report(IWContext iwc) throws Exception{
    Table table = new Table();
      table.setWidth("90%");
      table.setAlignment("center");
      table.setCellpaddingAndCellspacing(0);


    if (_report.useTwoDates()) {
      table.add(_report.getReport(_suppliers, iwc, _stamp, _toStamp));
    }else {
      table.add(_report.getReport(_suppliers, iwc, _stamp));
    }

    return table;
  }

}