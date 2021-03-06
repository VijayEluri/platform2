/*
 * Created on Sep 28, 2004
 *
 */
package is.idega.idegaweb.travel.presentation;

import java.rmi.RemoteException;
import javax.ejb.FinderException;
import com.idega.block.creditcard.business.CreditCardClient;
import com.idega.block.trade.stockroom.data.Product;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.BooleanInput;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.util.IWTimestamp;


/**
 * @author birna
 *
 */
public class CreditCardPropertiesSetter extends TravelWindow {
	
	public static final String PARAMETER_PRODUCT_ID = "product_id";
	private static String ACTION = "ccps_action";
	private static String ACTION_SAVE = "ccps_save";
	private static String ACTION_CLOSE = "ccps_close";
	private static String prmIsAuthOn = "prm_is_auth_on";
	
	private int productID;
	private Product product;
	private CreditCardClient client;
	
	public CreditCardPropertiesSetter() {
    super.setWidth(300);
    super.setHeight(250);
    super.setTitle("idegaWeb Travel");
    super.setStatus(true);
	}
	
  public void main(IWContext iwc) throws Exception{
    super.main(iwc);
    init(iwc);
    mainMenu(iwc);
  }
  
  private void init(IWContext iwc) throws RemoteException{
    iwrb = super.iwrb;
    try {
      String sProductId = iwc.getParameter(PARAMETER_PRODUCT_ID);
      if (sProductId != null) {
        productID = Integer.parseInt(sProductId);
        product = getProductBusiness(iwc).getProduct(productID);
        try {
        	client = getCreditCardBusiness(iwc).getCreditCardClient(super.getTravelSessionManager(iwc).getSupplier(), IWTimestamp.RightNow());
        } catch (Exception e) {
        	e.printStackTrace();
        }
      }
    }catch (FinderException sql) {
      sql.printStackTrace(System.err);
    }catch (NumberFormatException n) {
      n.printStackTrace(System.err);
    }
  }
  
  private void mainMenu(IWContext iwc) throws RemoteException{
    String action = iwc.getParameter(ACTION);

    add(Text.BREAK);

    if (product != null && client != null) {
      if (action == null) {
        add(getMainForm(iwc));
      }else if (action.equals(ACTION_SAVE)) {
        add(saveCCProp(iwc));
      }else if (action.equals(ACTION_CLOSE)) {
        super.close(true);
      }
    }
  }
  
  private Form getMainForm(IWContext iwc) throws RemoteException {
  		IWResourceBundle iwrb = getResourceBundle(iwc);
  		Form form = new Form();
  		form.maintainAllParameters();
  		
  		Table table = new Table();
  		table.setCellpadding(2);
  		table.setCellspacing(1);
  		table.setAlignment("center");
  		
  		Text headerText = getTextHeader(iwrb.getLocalizedString("ccps.header_text", "Creditcard properties"));
//  		headerText.setFontColor(TravelManager.WHITE);
//  		headerText.setBold();
  		
  		Text authText = getText(iwrb.getLocalizedString("ccps.use_delayed_transaction", "Use delayed transaction"));
  		BooleanInput authOn = new BooleanInput(prmIsAuthOn);
  		authOn.setSelected(product.getAuthorizationCheck());
  		SubmitButton save = new SubmitButton(iwrb.getImage("/buttons/save.gif"), ACTION, ACTION_SAVE);
  		CloseButton close = new CloseButton(iwrb.getImage("/buttons/close.gif"));
  		table.setHeight(1,1,20);
  		table.mergeCells(1,1,2,1);
  		table.setRowColor(1,TravelManager.backgroundColor);
  		table.add(headerText,1,1);
  		table.setColor(1,2,TravelManager.GRAY);
  		table.setColor(2,2,TravelManager.GRAY);
  		table.setColor(1,3,TravelManager.GRAY);
  		table.setColor(2,3,TravelManager.GRAY);
  		table.add(authText,1,2);
  		if (client.supportsDelayedTransactions()) {
  			table.add(authOn,2,2);
  		} else {
  			table.add(getText(iwrb.getLocalizedString("ccps.unsupported", "Unsupported")), 2, 2);
  		}
  		table.add(close,1,3);
  		table.add(save,2,3);
  		
  		table.mergeCells(1, 5, 2, 5);
  		table.add(iwrb.getLocalizedString("ccps.delayed_transaction_eplained", "Delayed transaction: \nIf this is set to TRUE then the actual transaction does not happen until you \"accept\" the booking."), 1, 5);
  		form.add(table);
  		
  		return form;
  }
  
  private Form saveCCProp(IWContext iwc) throws RemoteException {
  		
  		String authorization = iwc.getParameter(prmIsAuthOn);
  		
  		if(product != null && authorization != null) {
  			if(authorization.equals("Y")) {
  				product.setAuthorizationCheck(true);
  			}
  			else {
  				product.setAuthorizationCheck(false);
  			}
  			product.store();
  		}
  		
  		return getMainForm(iwc);
  	
  }

}
