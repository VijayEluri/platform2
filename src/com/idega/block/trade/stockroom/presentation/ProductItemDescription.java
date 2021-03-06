package com.idega.block.trade.stockroom.presentation;

import java.rmi.RemoteException;
import javax.ejb.FinderException;
import com.idega.block.trade.stockroom.data.Product;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Text;
import com.idega.util.text.TextSoap;

/**
 *  Title: idegaWeb TravelBooking Description: Copyright: Copyright (c) 2001
 *  Company: idega
 *
 *@author     <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>
 *@created    9. mars 2002
 *@version    1.0
 */

public class ProductItemDescription extends ProductItem {

  private String defaultText = "Product Description";

  public ProductItemDescription() { }
  public ProductItemDescription(int productId) throws RemoteException, FinderException{
    super(productId);
  }
  public ProductItemDescription(Product product) throws RemoteException {
    super(product);
  }

  public void main(IWContext iwc) throws Exception {
    super.main(iwc);
    drawObject(iwc);
  }

  private void drawObject(IWContext iwc) throws RemoteException{
    Text text = getText(this.defaultText);
    if ( this._product != null ) {
      String textString = TextSoap.formatText(this._product.getProductDescription(this._productLocaleId));
      text.setText(textString);
    }
    add(text);
  }

}
