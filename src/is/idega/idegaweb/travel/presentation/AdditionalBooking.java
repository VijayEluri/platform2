package is.idega.idegaweb.travel.presentation;

import javax.ejb.FinderException;
import javax.ejb.CreateException;
import java.rmi.RemoteException;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import com.idega.core.data.Address;
import is.idega.idegaweb.travel.data.*;
import com.idega.block.trade.stockroom.data.*;
import com.idega.block.trade.stockroom.business.*;
import is.idega.idegaweb.travel.business.*;
import com.idega.util.idegaTimestamp;
import is.idega.idegaweb.travel.business.Booker;
import is.idega.idegaweb.travel.service.tour.data.*;
import is.idega.idegaweb.travel.service.tour.business.*;
import is.idega.idegaweb.travel.service.tour.presentation.*;
import is.idega.idegaweb.travel.interfaces.Booking;

import java.sql.SQLException;
/**
 * Title:        idegaWeb TravelBooking
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class AdditionalBooking extends TravelWindow {

  public static String parameterServiceId = "addBookServiceId";
  public static String parameterDate = "addBookDate";
  private static String parameterDepartureAddressId = "addDepAddId";
  private static String parameterTimeframeId = "addDepTFId";

  public static String parameterSave = "addBookSave";
  public static String sAction = "addBookAction";

  public static String correction = "correction";

  private boolean isCorrection = false;

  Service service;
  Product product;
  idegaTimestamp stamp;
  Timeframe timeframe;

  public AdditionalBooking() {
    super.setHeight(450);
    super.setWidth(400);
    super.setTitle("idegaWeb Travel");
//    super.setScrollbar(true);
    super.setResizable(true);
  }

  public void main(IWContext iwc) throws Exception{
    super.main(iwc);
    initialize(iwc);

    try {
      if (service != null) {
        String action = iwc.getParameter(this.sAction);
        if (action == null || action.equals("")) {
          displayForm(iwc);
        }else if (action.equals(this.parameterSave) ) {
          saveBooking(iwc);
          super.close(true);
        }
      }else {
        add(iwrb.getLocalizedString("travel.session_has_expired","Session has expired"));
      }
    }catch (SQLException sql) {
      sql.printStackTrace(System.err);
      add(iwrb.getLocalizedString("travel.error","Error"));
    }
  }

  private void initialize(IWContext iwc) throws RemoteException{
    try {
      String sCorrection = iwc.getParameter(this.correction);
      if (sCorrection != null) {
        if (sCorrection.equals("true"))
        this.isCorrection = true;
      }
        service = ((is.idega.idegaweb.travel.data.ServiceHome)com.idega.data.IDOLookup.getHome(Service.class)).findByPrimaryKey(iwc.getParameter(this.parameterServiceId));
        product = ProductBusiness.getProduct(service.getID());
        stamp = new idegaTimestamp(iwc.getParameter(this.parameterDate));
        timeframe = ProductBusiness.getTimeframe(product, stamp);
    }catch (FinderException fe) {
      fe.printStackTrace(System.err);
    }catch (SQLException sql) {
      sql.printStackTrace(System.err);
    }
  }

  private void displayForm(IWContext iwc) throws RemoteException, SQLException{
      Form form = new Form();
      Table table = new Table();
        form.add(table);
        table.setAlignment("center");
      int row = 1;

      String addId = iwc.getParameter(this.parameterDepartureAddressId);
      String tfrId = iwc.getParameter(this.parameterTimeframeId);

      TravelAddress[] addresses = ProductBusiness.getDepartureAddresses(product);
      Timeframe[] timeframes = product.getTimeframes();
      int iAddressId = addresses[0].getID();
      int iTimeframeId = timeframe.getID();
      if (addId != null) iAddressId = Integer.parseInt(addId);
      if (tfrId != null) iTimeframeId = Integer.parseInt(tfrId);


      ProductPrice[] pPrices = com.idega.block.trade.stockroom.data.ProductPriceBMPBean.getProductPrices(service.getID(), iTimeframeId, iAddressId, false);
      PriceCategory category;

      Text header = (Text) text.clone();
        header.setFontSize(Text.FONT_SIZE_12_HTML_3);
        header.setBold();
      if (isCorrection) {
        header.setText(iwrb.getLocalizedString("travel.correction","Correction"));
        form.addParameter(this.correction, "true");
      }else {
        header.setText(iwrb.getLocalizedString("travel.extra_passenger","Extra passengers"));
      }


      Text nameText = (Text) text.clone();
          nameText.setText(iwrb.getLocalizedString("travel.name","name"));
      TextInput name = new TextInput("name");
          name.setSize(18);
          name.keepStatusOnAction();
      Text depPlaceText = (Text) text.clone();
          depPlaceText.setText(iwrb.getLocalizedString("travel.departure_place","Departure place"));
      Text tframeText = (Text) text.clone();
          tframeText.setText(iwrb.getLocalizedString("travel.timeframe","Timeframe"));


      Text pPriceCatNameText;
      ResultOutput pPriceText;
      TextInput pPriceMany;
      Text totalText = (Text) text.clone();
        totalText.setBold();
        totalText.setText(iwrb.getLocalizedString("travel.total","Total"));
      ResultOutput TotalPassTextInput = new ResultOutput("total_pass","0");
        TotalPassTextInput.setSize(5);
      ResultOutput TotalTextInput = new ResultOutput("total","0");
        TotalTextInput.setSize(8);
      DropdownMenu depAddr = new DropdownMenu(addresses, this.parameterDepartureAddressId);
        depAddr.setToSubmit();
      DropdownMenu tFrames = new DropdownMenu(timeframes, this.parameterTimeframeId);
        tFrames.setToSubmit();

      if (addId != null) {
        depAddr.setSelectedElement(addId);
      }
      if (tfrId != null) {
        tFrames.setSelectedElement(tfrId);
      }

    ++row;
    table.mergeCells(1,row,2,row);
    table.add(header,1,row);

    ++row;
    table.add(nameText,1,row);
    table.add(name,2,row);

    if (addresses.length > 1) {
      ++row;
      table.add(depPlaceText, 1, row);
      table.add(depAddr, 2,row);
    }else {
      table.add(new HiddenInput(this.parameterDepartureAddressId, Integer.toString(addresses[0].getID())));
    }

    if (timeframes.length > 1) {
      ++row;
      table.add(tframeText, 1, row);
      table.add(tFrames, 2,row);
    }else {
      table.add(new HiddenInput(this.parameterTimeframeId, Integer.toString(timeframes[0].getID())));
    }


      for (int i = 0; i < pPrices.length; i++) {
        try {
            ++row;
            category = pPrices[i].getPriceCategory();
            int price = (int) getTravelStockroomBusiness(iwc).getPrice(pPrices[i].getID(), service.getID(),pPrices[i].getPriceCategoryID(),pPrices[i].getCurrencyId(),idegaTimestamp.getTimestampRightNow(), iTimeframeId, iAddressId);
            pPriceCatNameText = (Text) text.clone();
              pPriceCatNameText.setText(category.getName());

            pPriceText = new ResultOutput("thePrice"+i,"0");
              pPriceText.setSize(8);

            pPriceMany = new TextInput("priceCategory"+i ,"0");
              pPriceMany.setSize(5);

            pPriceText.add(pPriceMany,"*"+price);
            TotalPassTextInput.add(pPriceMany);
            TotalTextInput.add(pPriceMany,"*"+price);


            table.add(pPriceCatNameText, 1,row);
            table.add(pPriceMany,2,row);
            table.add(pPriceText, 2,row);

            table.add(Integer.toString(price),2,row);
        }catch (SQLException sql) {
          sql.printStackTrace(System.err);
        }
    }

    ++row;
    table.add(totalText,1,row);
    table.add(TotalPassTextInput,2,row);
    table.add(TotalTextInput,2,row);
    ++row;
    table.mergeCells(1,row,2,row);
    table.setAlignment(1,row,"right");
    table.add(new SubmitButton(iwrb.getImage("/buttons/save.gif"),this.sAction, this.parameterSave),1,row);
    table.add(new HiddenInput(this.parameterServiceId, Integer.toString(this.service.getID())));
    table.add(new HiddenInput(this.parameterDate, stamp.toSQLDateString()));

    add(form);
  }


  public void saveBooking(IWContext iwc) throws RemoteException{
      String name = iwc.getParameter("name");
      String addressId = iwc.getParameter(this.parameterDepartureAddressId);

      String many;
      int iMany = 0;
      int iHotelId;

      ProductPrice[] pPrices = com.idega.block.trade.stockroom.data.ProductPriceBMPBean.getProductPrices(service.getID(), timeframe.getID(), Integer.parseInt(addressId), false);
      int bookingId;

      try {
        int[] manys = new int[pPrices.length];
        for (int i = 0; i < manys.length; i++) {
            many = iwc.getParameter("priceCategory"+i);
            if ( (many != null) && (!many.equals("")) && (!many.equals("0"))) {
              if (isCorrection) {
                manys[i] = -1 * Integer.parseInt(many);
                iMany += -1 * Integer.parseInt(many);
              }else {
                manys[i] = Integer.parseInt(many);
                iMany += Integer.parseInt(many);
              }
            }else {
                manys[i] = 0;
            }
        }

        int bookingTypeId = Booking.BOOKING_TYPE_ID_ADDITIONAL_BOOKING;
        if (isCorrection) {
          bookingTypeId = Booking.BOOKING_TYPE_ID_CORRECTION;
        }
        /**
         * @todo B�ti vi� formi� td. comment og anna�
         */
        int ownerId = -1;
        int userId = -1;

        bookingId = getBooker(iwc).Book(service.getID(),"",name,"","","","",stamp,iMany,bookingTypeId,"",Booking.PAYMENT_TYPE_ID_CASH, userId, ownerId, Integer.parseInt(addressId), "");

        BookingEntry bEntry;
        for (int i = 0; i < pPrices.length; i++) {
          if (manys[i] != 0) {
            bEntry = ((is.idega.idegaweb.travel.data.BookingEntryHome)com.idega.data.IDOLookup.getHome(BookingEntry.class)).create();
              bEntry.setProductPriceId(pPrices[i].getID());
              bEntry.setBookingId(bookingId);
              bEntry.setCount(manys[i]);
            bEntry.store();
          }
        }



      }catch (NumberFormatException n) {
        n.printStackTrace(System.err);
      }catch (CreateException ce) {
        ce.printStackTrace(System.err);
      }


  }

}
