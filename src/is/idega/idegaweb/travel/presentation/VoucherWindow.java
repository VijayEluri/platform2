package is.idega.idegaweb.travel.presentation;

import java.rmi.RemoteException;
import is.idega.idegaweb.travel.interfaces.Booking;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.*;
import com.idega.idegaweb.*;

import is.idega.idegaweb.travel.presentation.Voucher;
import is.idega.idegaweb.travel.data.GeneralBooking;


/**
 * Title:        idegaWeb Travel
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href mailto:"gimmi@idega.is">Gr�mur J�nsson</a>
 * @version 1.0
 */

public class VoucherWindow extends Window {

  public static String parameterBookingId = "voucherWindowBookingId";

  protected IWResourceBundle iwrb;
  protected IWBundle iwb;

  private static String searchAction = "voucherWindowSearchAction";
  private static String searchMethodReferenceNumber = "voucherWindowSearchMethodReferenceNumber";
  private static String searchMethodNumber = "voucherWindowSearchMethodNumber";
  private static String parameterReferenceNumber = "voucherWindowReferenceNumber";
  private static String parameterNumber = "voucherWindowNumber";

  public static String IW_BUNDLE_IDENTIFIER="is.idega.travel";
  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public VoucherWindow() {
    super.setWidth(Voucher.width+40);
    super.setHeight(500);
    super.setTitle("Voucher");
    super.setResizable(true);
    super.setMenubar(true);
  }

  public void main(IWContext iwc) throws Exception {
    iwb = getBundle(iwc);
    iwrb = iwb.getResourceBundle(iwc);

    String sBookingId = iwc.getParameter(this.parameterBookingId);
    String searchAction = iwc.getParameter(this.searchAction);
    boolean error = false;

    Table table  = new Table();
      table.setCellpaddingAndCellspacing(0);

    if (sBookingId != null) {
      Voucher voucher = new Voucher(iwc, Integer.parseInt(sBookingId));
      table.add(voucher.getVoucher(iwc));
    }else if (searchAction != null){
      if (searchAction.equals(searchMethodReferenceNumber)) {
        String refMethod = iwc.getParameter(this.parameterReferenceNumber);
        if (refMethod != null && !refMethod.equals("")) {
          GeneralBooking[] gBooking = (GeneralBooking[]) (is.idega.idegaweb.travel.data.GeneralBookingBMPBean.getStaticInstance(GeneralBooking.class)).findAllByColumn(is.idega.idegaweb.travel.data.GeneralBookingBMPBean.getReferenceNumberColumnName(), refMethod);
          if (gBooking.length > 0) {
            Voucher voucher = new Voucher(iwc, gBooking[0].getID());
            table.add(voucher.getVoucher(iwc));
          }else {
            error = true;
          }
        }else {
          error = true;
        }
      }else if (searchAction.equals(this.searchMethodNumber)) {
        String numMethod = iwc.getParameter(this.parameterNumber);
        if (numMethod != null && !numMethod.equals("")) {
          GeneralBooking[] gBooking = (GeneralBooking[]) (is.idega.idegaweb.travel.data.GeneralBookingBMPBean.getStaticInstance(GeneralBooking.class)).findAllByColumn(is.idega.idegaweb.travel.data.GeneralBookingBMPBean.getStaticInstance(GeneralBooking.class).getIDColumnName(), (Integer.parseInt(numMethod) - Voucher.voucherNumberChanger));
          if (gBooking.length > 0) {
            Voucher voucher = new Voucher(iwc, gBooking[0].getID());
            table.add(voucher.getVoucher(iwc));
          }else {
            error = true;
          }
        }else {
          error = true;
        }
      }else {
        error = true;
      }
    }

    if (error) {
      table.add(iwrb.getLocalizedString("travel.voucher_not_found","Voucher not found"));
    }else {
      table.add(Text.BREAK, 1, 2);
      table.add(new PrintButton(iwrb.getImage("buttons/print.gif")), 1,2);
      table.setAlignment(1, 2, Table.HORIZONTAL_ALIGN_RIGHT);
    }

    add(table);
  }

  public static Form getReferenceNumberForm(IWResourceBundle iwrb) {
    Form form = new Form();
      form.setWindowToOpen(VoucherWindow.class);

    Table table = new Table();
      form.add(table);

    Text refText = new Text(iwrb.getLocalizedString("travel.reference_number","Reference number"));
      refText.setStyle(TravelManager.theTextStyle);
      refText.setFontColor(TravelManager.WHITE);
    TextInput refInp = new TextInput(parameterReferenceNumber);
      refInp.setSize(25);
    SubmitButton searchRef = new SubmitButton(iwrb.getLocalizedImageButton("travel.search","Search"), searchAction, searchMethodReferenceNumber);

    Text numText = new Text(iwrb.getLocalizedString("travel.voucher_number","Voucher number"));
      numText.setStyle(TravelManager.theTextStyle);
      numText.setFontColor(TravelManager.WHITE);
    TextInput numInp = new TextInput(parameterNumber);
      numInp.setSize(25);
    SubmitButton searchNum = new SubmitButton(iwrb.getLocalizedImageButton("travel.search","Search"), searchAction, searchMethodNumber);


    table.add(refText,1,1);
    table.add(refInp,2,1);
    table.add(searchRef,3,1);

    table.add(numText,1,2);
    table.add(numInp,2,2);
    table.add(searchNum,3,2);

    return form;
  }

  public static Link getVoucherLink(Booking booking) throws RemoteException{
    Link link = new Link("Voucher");
      link.setWindowToOpen(VoucherWindow.class);
      link.addParameter(parameterBookingId, booking.getID());
    return link;
  }

}
