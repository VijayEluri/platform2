package is.idega.idegaweb.travel.presentation;

import java.sql.SQLException;
import java.util.*;
import com.idega.util.idegaTimestamp;
import com.idega.block.trade.presentation.CurrencyCalculator;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import is.idega.idegaweb.travel.business.Booker;
import is.idega.idegaweb.travel.data.*;
/**
 * Title:        idegaWeb TravelBooking
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class BookingDeleterWindow extends TravelWindow {

  public static final String bookingIdParameter = "bookDelWind_bookingId";
  private static final String parameterBookingIdForForm = "bookDelWind_bookingIdForForm";
  private static final String parameterDeleteBId = "bookDelWind_deleteBId";
  private static final String sAction = "bookDelWind_action";
  private static final String paramDelete = "bookDelWind_delete";
  private static final String paramClose  = "bookDelWind_close";

  int _bookingId = -1;
  GeneralBooking _booking = null;
  Service _service = null;
  List _bookings = new Vector();

  public BookingDeleterWindow() {
  }

  public void main(IWContext iwc) {
    super.main(iwc);
    setTitle("idegaWEB travel");
    init(iwc);

    if (_bookings != null && _bookings.size() > 0) {
      String action = iwc.getParameter(sAction);
      if (action == null) {
        displayForm(iwc);
      }else if (action.equals(this.paramDelete)) {
        if (handleDelete(iwc)) {
          completed(iwc);
        } else {
          error(iwc);
        }
      }else if (action.equals(this.paramClose)) {
        super.close(true);
      }
    }else {
      error(iwc);
    }
  }

  private void init(IWContext iwc) {
    String sBookingId = iwc.getParameter(bookingIdParameter);
    if (sBookingId != null) {
      try {
        _bookingId = Integer.parseInt(sBookingId);
        _booking = ((is.idega.idegaweb.travel.data.GeneralBookingHome)com.idega.data.IDOLookup.getHomeLegacy(GeneralBooking.class)).findByPrimaryKeyLegacy(_bookingId);
        _bookings = Booker.getMultibleBookings(_booking);
        _service = _booking.getService();
      }catch (NumberFormatException n) {
        n.printStackTrace(System.err);
      }catch (SQLException sql) {
        sql.printStackTrace(System.err);
      }
    }
  }

  private Text getText(String content) {
    Text text = (Text) super.text.clone();
      text.setText(content);
    return text;
  }

  private Text getTextBold(String content) {
    Text text = getText(content);
      text.setBold(true);
    return text;
  }

  private Text getTextWhiteBold(String content) {
    Text text = new Text(content);
      text.setBold(true);
      text.setFontColor(TravelManager.WHITE);
    return text;
  }

  private void displayForm(IWContext iwc) {
    Form form = new Form();
    form.addParameter(this.bookingIdParameter, _bookingId);


    Table table = new Table();
      table.setCellpadding(1);
      table.setCellspacing(1);
      table.setColor(TravelManager.WHITE);
      table.setAlignment("center");
    form.add(table);

    int row = 1;
    GeneralBooking booking;
    int bookingId;
    CheckBox box;

    Text header = getText(iwrb.getLocalizedString("travel.delete_bookings","Delete booking/s ?"));
      header.setBold(true);
    table.mergeCells(1,row, 4, row);
    table.add(header);


    ++row;
    table.add(getTextWhiteBold(iwrb.getLocalizedString("travel.name","Name")), 1, row);
    table.add(getTextWhiteBold(iwrb.getLocalizedString("travel.service","Service")), 2, row);
    table.add(getTextWhiteBold(iwrb.getLocalizedString("travel.date","Date")), 3, row);
    table.add(getTextWhiteBold(iwrb.getLocalizedString("travel.delete","Delete")), 4, row);
    table.setRowColor(row, TravelManager.backgroundColor);

    for (int i = 0; i < _bookings.size(); i++) {
      ++row;
      table.setRowColor(row, TravelManager.GRAY);
      booking = (GeneralBooking) _bookings.get(i);
      bookingId = booking.getID();
      table.add(getText(booking.getName()), 1, row);
      table.add(getText(_service.getName()), 2, row);
      table.add(getText(new idegaTimestamp(booking.getBookingDate()).getLocaleDate(iwc)), 3, row);
      box = new CheckBox(this.parameterDeleteBId+bookingId);
      table.add(box, 4, row);
      table.add(new HiddenInput(this.parameterBookingIdForForm, Integer.toString(bookingId)), 4,row);

      if (bookingId == this._bookingId) {
        //Merkja �� sem var valin
      }
    }
    ++row;
    table.mergeCells(1, row, 2, row);
    table.mergeCells(3, row, 4, row);
    table.add(new SubmitButton(iwrb.getLocalizedImageButton("travel.close","Close"), sAction, paramClose), 1, row);
    table.add(new SubmitButton(iwrb.getLocalizedImageButton("travel.delete","Delete"), sAction, paramDelete), 3, row);
    table.setAlignment(3, row, "right");

    add(form);
  }

  private void error(IWContext iwc) {
    Form form = new Form();
    form.addParameter(this.bookingIdParameter, _bookingId);
    Table table = new Table();
      table.setAlignment("center");
    form.add(table);

    table.add(getTextBold(iwrb.getLocalizedString("travel.error","Error")), 1, 1);
    table.add(new SubmitButton(iwrb.getLocalizedImageButton("travel.close","Close"), sAction, paramClose), 1, 2);

    table.setAlignment(1,1, "center");
    table.setAlignment(1,2, "center");

    add(form);
  }

  private void completed(IWContext iwc) {
    Form form = new Form();
    form.addParameter(this.bookingIdParameter, _bookingId);
    Table table = new Table();
      table.setAlignment("center");
    form.add(table);

    table.add(getTextBold(iwrb.getLocalizedString("travel.bookings_were_deleted","Booking/s were deleted")), 1, 1);
    table.add(new SubmitButton(iwrb.getLocalizedImageButton("travel.close","Close"), sAction, paramClose), 1, 2);

    table.setAlignment(1,1, "center");
    table.setAlignment(1,2, "center");

    add(form);
  }

  private boolean handleDelete(IWContext iwc) {
    String[] ids = iwc.getParameterValues(this.parameterBookingIdForForm);
    if (ids != null) {
      GeneralBooking booking;
      String del;
      for (int i = 0; i < ids.length; i++) {
        del = iwc.getParameter(this.parameterDeleteBId+ids[i]);
        if (del != null) {
          if (!Booker.deleteBooking(Integer.parseInt(ids[i]))) {
            return false;
          }
        }
      }
    }
    return true;
  }

}
