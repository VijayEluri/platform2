package com.idega.block.tpos.presentation;

import java.sql.SQLException;
import java.util.List;

import com.idega.block.tpos.business.TPosClient;
import com.idega.block.trade.stockroom.data.Supplier;
import com.idega.core.contact.data.Phone;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Table;
import com.idega.presentation.text.HorizontalRule;
import com.idega.presentation.text.Text;
import com.idega.util.IWTimestamp;
import com.idega.util.text.TextSoap;

/**
 * Title:        idegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 * @version 1.0
 */

public class Receipt extends PresentationObjectContainer{

  private IWResourceBundle iwrb;
  private IWBundle bundle;
  private TPosClient _client;
  private Supplier _supplier;

  public static final String IW_BUNDLE_IDENTIFIER = "com.idega.block.tpos";

  public Receipt(TPosClient client, Supplier supplier) {
    _client = client;
    _supplier = supplier;
  }

  public void main(IWContext iwc) {
    init(iwc);
    createReceipt(iwc);
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  private void init(IWContext iwc) {
    bundle = getBundle(iwc);
    iwrb = bundle.getResourceBundle(iwc);
  }

  private Text getText(String content) {
    Text text = new Text(content);
    return text;
  }

  private Text getTextBold(String content) {
    Text text = getText(content);
      text.setBold(true);
    return text;
  }

  private void createReceipt(IWContext iwc) {
    Table table = new Table();
//      table.setBorder(1);
      table.setColor("#FFFFFF");
    int row = 1;

    try {
      table.mergeCells(1,row,2,row);
      table.setRowAlignment(row, "center");
      table.add(getTextBold(_supplier.getName()), 1,row);
      ++row;
      table.mergeCells(1,row,2,row);
      table.setRowAlignment(row, "center");
      table.add(getText(_supplier.getAddress().getStreetName()), 1,row);
      List phones = _supplier.getHomePhone();
      if (phones != null && phones.size() > 0) {
        ++row;
        table.mergeCells(1,row,2,row);
        table.setRowAlignment(row, "center");
        table.add(getText(((Phone) phones.get(phones.size()-1)).getNumber()), 1,row);
      }

      HorizontalRule hr = new HorizontalRule("100%");
        hr.setColor("#000000");
      ++row;
      table.mergeCells(1, row, 2, row);
      table.add(hr,1 ,row);
      ++row;
      IWTimestamp stamp = _client.getIdegaTimestamp();
      table.add(getText(iwrb.getLocalizedString("date","Date")+" : "+stamp.getLocaleDate(iwc)), 1, row);
      ++row;
      table.add(getText(iwrb.getLocalizedString("time","Time")+" : "+TextSoap.addZero(stamp.getHour())+":"+TextSoap.addZero(stamp.getMinute())), 1, row);

      table.mergeCells(1, row, 2, row);
      table.add(hr,1 ,row);
      ++row;

      ++row;
      table.add(getTextBold(_client.getCardTypeName()),1,row);
      ++row;
      String ccNumber = _client.getCCNumber();
      if (ccNumber.length() <5) {
        table.add(getText(ccNumber),1,row);
      }else {
        for (int i = 0; i < ccNumber.length() -4; i++) {
          table.add(getText("*"),1,row);
        }
        table.add(getText(ccNumber.substring(ccNumber.length()-4, ccNumber.length())),1,row);

      }


      String expire =_client.getExpire();
      table.add(getText(iwrb.getLocalizedString("valid","Valid")+Text.NON_BREAKING_SPACE), 2, row);
      table.add(getText(expire.substring(2,4)+"/"+expire.substring(0,2)), 2, row);

      ++row;
      table.add(getText(Text.NON_BREAKING_SPACE), 1,row);
      ++row;
      table.add(getTextBold(iwrb.getLocalizedString("amount","Amount")), 1,row);
      table.add(getTextBold(TextSoap.decimalFormat(_client.getAmount(), 2)), 2, row);
      table.add(getTextBold(Text.NON_BREAKING_SPACE+_client.getCurrency()), 2, row);

      ++row;
      table.add(getText(Text.NON_BREAKING_SPACE), 1,row);
      ++row;
      table.add(getText(iwrb.getLocalizedString("autorization_number","Autorization number")), 1,row);
      table.add(getText(_client.getAutorIdentifyRSP()), 2, row);
    }catch (SQLException sql) {
      sql.printStackTrace(System.err);
    }

    table.setColumnAlignment(2, "right");

    add(table);
  }

  public synchronized Object clone() {
    Receipt obj = null;
    try {
      obj = (Receipt)super.clone();
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }
    return obj;
  }
}
