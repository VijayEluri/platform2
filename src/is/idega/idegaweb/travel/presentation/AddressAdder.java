package is.idega.idegaweb.travel.presentation;

import com.idega.data.IDOException;
import javax.ejb.FinderException;
import java.rmi.RemoteException;
import com.idega.data.IDOFinderException;
import java.util.List;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.block.trade.stockroom.business.*;
import com.idega.core.data.*;
import com.idega.core.location.data.*;
import com.idega.util.IWTimestamp;
import com.idega.block.trade.stockroom.data.*;
import java.sql.SQLException;
/**
 * Title:        idegaWeb TravelBooking
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class AddressAdder extends TravelWindow {

  public static String _parameterProductId = "parameterProductId";

  private static String _parameterAddressId = "parameterAddressId";
  private static String _parameterDelete = "parameterDelete";
  private static String parameterDeparture = "addressAdderDeparture";
  private static String parameterArrival = "addressAdderArrival";
  private static String parameterRefill = "addressAdderRefill";

  private String sAction = "addressAdderAction";
  private String parameterSave = "addressAdderSave";
  private String parameterClose = "addressAdderClose";

  private String textInputNameAddress = "addressAddress";
  private String parameterTime = "addressAdderTime";

  private Product _product;
  private int _productId = -1;
  private int extraFields = 3;

  public AddressAdder() {
    super.setWidth(500);
    super.setTitle("idegaWeb Travel");
  }

  public void main(IWContext iwc) throws Exception{
    super.main(iwc);
    init(iwc);



    if (_product != null) {
      String action = iwc.getParameter(sAction);
      if (action == null) action = "";

      if (action.equals("")) {
        drawForm(iwc);
      }else if (action.equals(parameterSave)) {
        if (saveDepartureAddress(iwc)) {
          drawForm(iwc);
        }else {
          error(iwc);
        }
      }else if (action.equals(parameterClose)) {
        super.close(true);
      }
    }else {
      error(iwc);
    }

  }

  private void init(IWContext iwc) throws RemoteException{
    try {
      String sProductId = iwc.getParameter(_parameterProductId);
      if (sProductId != null) {
        _productId = Integer.parseInt(sProductId);
        _product = getProductBusiness(iwc).getProduct(_productId);
      }
    }catch (FinderException sql) {
      sql.printStackTrace();
    }
  }

  private void error(IWContext iwc) {
    add("error");
  }

  private boolean saveDepartureAddress(IWContext iwc) throws RemoteException, IDOException{
    boolean returner = true;
      String[] name = iwc.getParameterValues(textInputNameAddress);
      String[] ids = iwc.getParameterValues(_parameterAddressId);
      int counter = 0;
      String time = "";
      String refill = "";

      for (int i = 0; i < name.length; i++) {
        try {
          ++counter;
          time = iwc.getParameter(parameterTime+counter);
          refill = iwc.getParameter(parameterRefill+counter);
          if (ids[i].equals("-1") ) {
            if (!name[i].equals("")) {
              Address newAddress = ((com.idega.core.location.data.AddressHome)com.idega.data.IDOLookup.getHomeLegacy(Address.class)).createLegacy();
                newAddress.setStreetName(name[i]);
                newAddress.setAddressTypeID(com.idega.core.location.data.AddressTypeBMPBean.getId(ProductBusinessBean.uniqueDepartureAddressType));
              newAddress.insert();

              TravelAddress tAddress = ((com.idega.block.trade.stockroom.data.TravelAddressHome)com.idega.data.IDOLookup.getHomeLegacy(TravelAddress.class)).createLegacy();
                tAddress.setAddressId(newAddress.getID());
                tAddress.setAddressTypeId(com.idega.block.trade.stockroom.data.TravelAddressBMPBean.ADDRESS_TYPE_DEPARTURE);
                tAddress.setTime(new IWTimestamp("2001-01-01 "+time));
                if (refill.equals("Y")) {
                  tAddress.setRefillStock(true);
                }else {
                  tAddress.setRefillStock(false);
                }
                tAddress.insert();
              _product.addTravelAddress(tAddress);
//              tAddress.addTo(_product);
            }
          }else {
            if (iwc.getParameter(this._parameterDelete+ids[i]) != null) {
              TravelAddress tAddress = ((com.idega.block.trade.stockroom.data.TravelAddressHome)com.idega.data.IDOLookup.getHomeLegacy(TravelAddress.class)).findByPrimaryKeyLegacy(Integer.parseInt(ids[i]));
              Address newAddress = ((com.idega.core.location.data.AddressHome)com.idega.data.IDOLookup.getHomeLegacy(Address.class)).findByPrimaryKeyLegacy(tAddress.getAddressId());
//                tAddress.removeFrom(_product);
                _product.removeTravelAddress(tAddress);
                tAddress.delete();
                newAddress.delete();
            }else if (!name[i].equals("")) {
              TravelAddress tAddress = ((com.idega.block.trade.stockroom.data.TravelAddressHome)com.idega.data.IDOLookup.getHomeLegacy(TravelAddress.class)).findByPrimaryKeyLegacy(Integer.parseInt(ids[i]));
                tAddress.setTime(new IWTimestamp("2001-01-01 "+time));
                if (refill.equals("Y")) {
                  tAddress.setRefillStock(true);
                }else {
                  tAddress.setRefillStock(false);
                }

              Address newAddress = ((com.idega.core.location.data.AddressHome)com.idega.data.IDOLookup.getHomeLegacy(Address.class)).findByPrimaryKeyLegacy(tAddress.getAddressId());
                newAddress.setStreetName(name[i]);
                newAddress.update();
              tAddress.update();
            }
          }
        }catch (Exception e) {
          // Error, nenni ekki a� ey�a loggnum � svona vitleysu
        }
      }
      return true;
  }

  private void drawForm(IWContext iwc) throws RemoteException{
    Form form = new Form();
      form.maintainParameter(_parameterProductId);

    Table table = new Table();
      form.add(table);
      table.setAlignment("center");
      table.setBorder(0);
      table.setCellpadding(2);
      table.setCellspacing(1);
    int row = 1;

    Text nameTxt = (Text) text.clone();
      nameTxt.setText(iwrb.getLocalizedString("travel.address","Address"));
      nameTxt.setFontColor(TravelManager.WHITE);
      nameTxt.setBold(true);
    Text timeTxt = (Text) text.clone();
      timeTxt.setText(iwrb.getLocalizedString("travel.time","Time"));
      timeTxt.setFontColor(TravelManager.WHITE);
      timeTxt.setBold(true);
    Text refillTxt = (Text) text.clone();
      refillTxt.setText(iwrb.getLocalizedString("travel.refill_stock","Refill stock"));
      refillTxt.setFontColor(TravelManager.WHITE);
      refillTxt.setBold(true);
    Text delTxt = (Text) text.clone();
      delTxt.setText(iwrb.getLocalizedString("travel.delete","delete"));
      delTxt.setFontColor(TravelManager.WHITE);
      delTxt.setBold(true);

    try {
      List addresses = _product.getDepartureAddresses(true);
      int addressesSize = addresses.size();
      TravelAddress tAddress;
      TextInput nameInp = new TextInput(textInputNameAddress);
      BooleanInput refill;
      CheckBox del;
      TimeInput timeInp;

      table.add(nameTxt, 1, row);
      table.add(timeTxt, 2, row);
      table.add(refillTxt, 3, row);
      table.add(delTxt, 4, row);
      table.setAlignment(4,row, "center");
      table.setRowColor(row, TravelManager.backgroundColor);
      IWTimestamp timestamp;
      int counter = 0;
      for (int i = 0; i < addressesSize; i++) {
        ++row;
        ++counter;
        tAddress = (TravelAddress) addresses.get(i);

        nameInp = new TextInput(textInputNameAddress);
          nameInp.setContent(tAddress.getStreetName());
        del = new CheckBox(this._parameterDelete+tAddress.getID());
          del.setChecked(false);
        timestamp = new IWTimestamp(tAddress.getTime());
        refill = new BooleanInput(this.parameterRefill+counter);
          refill.setSelected(tAddress.getRefillStock());
        timeInp = new TimeInput(this.parameterTime+counter);
          timeInp.setHour(timestamp.getHour());
          timeInp.setMinute(timestamp.getMinute());

        table.add(nameInp, 1,row);
        table.add(timeInp, 2,row);
        table.add(refill, 3, row);
        table.add(del, 4,row);
        table.setAlignment(3,row, "center");
        table.setAlignment(4,row, "center");
        table.add(new HiddenInput(this._parameterAddressId, Integer.toString(tAddress.getID())));

        table.setRowColor(row, TravelManager.GRAY);
      }

      for (int i = 0; i < extraFields; i++) {
        ++row;
        ++counter;

        nameInp = new TextInput(textInputNameAddress);
        timeInp = new TimeInput(this.parameterTime+counter);
        refill = new BooleanInput(this.parameterRefill+counter);
        table.add(new HiddenInput(this._parameterAddressId, "-1"));
        table.add(nameInp, 1,row);
        table.add(timeInp , 2, row);
        table.add(refill, 3, row);
        table.setAlignment(3,row, "center");
        table.setRowColor(row, TravelManager.GRAY);
      }

      SubmitButton saveBtn = new SubmitButton(iwrb.getImage("buttons/save.gif"),sAction,parameterSave);
      SubmitButton closeBtn = new SubmitButton(iwrb.getImage("buttons/close.gif"),sAction,parameterClose);


      ++row;
      table.add(closeBtn,1,row);
      table.add(saveBtn,3,row);
      table.mergeCells(3, row, 4, row);
      table.setAlignment(3,row,"right");
      table.setRowColor(row, TravelManager.GRAY);
    }catch (IDOFinderException ido) {
      ido.printStackTrace(System.err);
      error(iwc);
    }

    add(form);
  }

}
