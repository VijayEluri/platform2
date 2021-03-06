package is.idega.idegaweb.campus.nortek.presentation;

import is.idega.idegaweb.campus.nortek.business.NortekBusiness;
import is.idega.idegaweb.campus.nortek.data.Card;
import is.idega.idegaweb.campus.presentation.CampusBlock;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.DataTable;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

public class UserCardList extends CampusBlock {

	public final static String BUNDLE_IDENTIFIER = "is.idega.idegaweb.campus.nortek";

	protected final static String LABEL_CARD_DECODED = "nt_card_decoded";

	protected final static String LABEL_CARD = "nt_card";

	protected final static String LABEL_USER = "nt_user";

	protected final static String LABEL_VALID = "nt_valid";
	
	protected final static String LABEL_DELETE = "nt_delete";

	protected final static String LABEL_DELETE_CHECK = "nt_delete_check";

	protected final static String LABEL_CHANGE = "nt_change";

	protected final static String LABEL_ADD = "nt_add";

	protected final static String LABEL_CANCEL = "nt_cancel";

	protected final static String LABEL_SAVE = "nt_save";

	protected final static String PARAMETER_CHANGE = "nt_param_change";

	protected final static int ACTION_SHOW_LIST = 1;

	protected final static int ACTION_EDIT = 2;

	protected final static int ACTION_SAVE = 3;

	protected final static int ACTION_DELETE = 4;

	private String cardNumber = null;
	
	private String userSSN = null;
	
	private boolean valid = false;
	
	private boolean showValid = true;

	public String getBundleIdentifier() {
		return BUNDLE_IDENTIFIER;
	}

	protected void control(IWContext iwc) {
		int action = parseAction(iwc);

		System.out.println("action = " + action);
		
		switch (action) {
		case ACTION_EDIT: 
			add(getEditForm(iwc));
			break;
		case ACTION_SAVE:
			saveCard(iwc);
			add(getEditForm(iwc));
			add(getCardList(iwc));
			break;
		case ACTION_DELETE:
			deleteCards(iwc);
			add(getEditForm(iwc));
			add(getCardList(iwc));
			break;
		default:
			add(getEditForm(iwc));
			add(getCardList(iwc));
		}
	}

	public void main(IWContext iwc) throws Exception {
		control(iwc);
	}

	private int parseAction(IWContext iwc) {
		if (iwc.isParameterSet(LABEL_SAVE)) {
			return ACTION_SAVE;
		}

		if (iwc.isParameterSet(LABEL_DELETE)) {
			return ACTION_DELETE;
		}
		
		if (iwc.isParameterSet(LABEL_CARD)) {
			try {
				Card card = getBusiness(iwc).getCard(iwc.getParameter(LABEL_CARD));
				if (card != null) {
					cardNumber = card.getDecodedCardSerialNumber();
					if (card.getUser() != null) {
						userSSN = card.getUser().getPersonalID();
					}
					valid = card.getIsValid();
				}
			} catch (IBOLookupException e) {
			} catch (RemoteException e) {
			}
			
			return ACTION_EDIT;
		}

		return ACTION_SHOW_LIST;
	}

	private void saveCard(IWContext iwc) {
		String cardID = iwc.getParameter(LABEL_CARD_DECODED);
		String ssn = iwc.getParameter(LABEL_USER);
		String valid = iwc.getParameter(LABEL_VALID);
		
		try {
			getBusiness(iwc).saveCard(cardID, ssn, valid);
		} catch (IBOLookupException e) {
		} catch (RemoteException e) {
		} catch (CreateException e) {
		} catch (FinderException e) {
		}
	}

	private void deleteCards(IWContext iwc) {
		String [] cards = iwc.getParameterValues(LABEL_DELETE_CHECK);
		
		try {
			getBusiness(iwc).deleteCards(cards);
		} catch (IBOLookupException e) {
		} catch (RemoteException e) {
		} catch (CreateException e) {
		} catch (FinderException e) {
		}
	}

	private PresentationObject getEditForm(IWContext iwc) {
		Form f = new Form();
		f.setStyleClass("cardEdit");

		DataTable inputTable = new DataTable();
		inputTable.setTitlesHorizontal(true);
		inputTable.getContentTable().setCellpadding(3);
		inputTable.getContentTable().setCellspacing(1);
		inputTable.setWidth("100%");
		
		int row = 1;
		int column = 1;
		inputTable.add(new Text(this.iwrb.getLocalizedString(LABEL_CARD_DECODED, "Card decoded")), column++, row);
		inputTable.add(new Text(this.iwrb.getLocalizedString(LABEL_USER, "User")), column++, row);
		inputTable.add(new Text(this.iwrb.getLocalizedString(LABEL_VALID, "Valid")), column++, row);
		inputTable.add(new Text(""), column++, row++);
		
		TextInput cardNumberInput = new TextInput(LABEL_CARD_DECODED);
		cardNumberInput.setMaxlength(4);
		if (cardNumber != null) {
			cardNumberInput.setValue(cardNumber);
		}
		
		TextInput ssnInput = new TextInput(LABEL_USER);
		ssnInput.setMaxlength(10);
		if (userSSN != null) {
			ssnInput.setValue(userSSN);
		}
		
		CheckBox validInput = new CheckBox(LABEL_VALID, "checked");
		validInput.setChecked(valid);

		SubmitButton submit = new SubmitButton(this.iwrb.getLocalizedString(LABEL_SAVE, "Save"), LABEL_SAVE, "save");
		SubmitButton cancel = new SubmitButton(this.iwrb.getLocalizedString(LABEL_CANCEL, "Cancel"), LABEL_CANCEL, "cancel");

		column = 1;
		inputTable.add(cardNumberInput, column++, row);
		inputTable.add(ssnInput, column++, row);
		inputTable.add(validInput, column++, row);
		inputTable.add(submit, column, row);
		inputTable.add(cancel, column, row);

		f.add(inputTable);
		return f;
	}
	
	private PresentationObject getCardList(IWContext iwc) {
		Form f = new Form();
		f.setStyleClass("cardList");
		DataTable table = new DataTable();
		table.setTitlesHorizontal(true);
		table.getContentTable().setCellpadding(3);
		table.getContentTable().setCellspacing(1);
		table.setWidth("100%");

		int row = 1;
		int column = 1;
		table.add(new Text(this.iwrb.getLocalizedString(LABEL_CARD_DECODED, "Card decoded")), column++, row);
		table.add(new Text(this.iwrb.getLocalizedString(LABEL_CARD, "Card")), column++, row);
		table.add(new Text(this.iwrb.getLocalizedString(LABEL_USER, "User")), column++, row);
		table.add(new Text(this.iwrb.getLocalizedString(LABEL_DELETE, "Delete")), column, row++);

		Collection cards = null;
		try {
			cards = getBusiness(iwc).getCardsByValdi(this.showValid);
		} catch (IBOLookupException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		if (cards != null) {
			Iterator iter = cards.iterator();
			while (iter.hasNext()) {
				column = 1;
				Card card = (Card) iter.next();
				String text = "x";
				if (card.getDecodedCardSerialNumber() != null) {
					text = card.getDecodedCardSerialNumber();
				}
				Link link = new Link(new Text(text));
				link.addParameter(LABEL_CARD, card.getCardSerialNumber());
				table.add(link, column++, row);
				table.add(new Text(card.getCardSerialNumber()), column++, row);
				if (card.getUser() != null) {
					table.add(new Text(card.getUser().getName() + " (" + card.getUser().getPersonalID() + ")"), column++, row);
				} else {
					table.add(new Text(""), column++, row);
				}
				CheckBox check = new CheckBox(LABEL_DELETE_CHECK, card.getPrimaryKey().toString());
				check.setChecked(false);
				table.add(check, column, row++);
			}
			
			SubmitButton submit = new SubmitButton(this.iwrb.getLocalizedString(LABEL_DELETE, "Delete"), LABEL_DELETE, "delete");
			table.add(submit, column, row);

		}

		f.add(table);
		return f;
	}

	private NortekBusiness getBusiness(IWContext iwc) throws IBOLookupException {
		NortekBusiness bus1 = (NortekBusiness) IBOLookup.getServiceInstance(
				iwc, NortekBusiness.class);

		return bus1;
	}
	
	public void setShowValid(boolean showValid) {
		this.showValid = showValid;
	}
	
	public boolean getShowValid() {
		return this.showValid;
	}
}