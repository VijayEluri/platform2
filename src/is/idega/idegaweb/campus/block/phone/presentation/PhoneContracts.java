package is.idega.idegaweb.campus.block.phone.presentation;

import is.idega.idegaweb.campus.presentation.Edit;
import is.idega.idegaweb.campus.block.allocation.data.Contract;
import is.idega.idegaweb.campus.block.phone.business.PhoneFinder;
import is.idega.idegaweb.campus.block.phone.data.CampusPhone;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;
import com.idega.presentation.Table;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.block.building.data.*;
import com.idega.block.building.business.*;
import com.idega.data.IDOLegacyEntity;
import com.idega.data.EntityFinder;
import com.idega.event.IWPageEventListener;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.util.IWTimestamp;
import com.idega.util.text.TextFormat;
import java.util.*;
import java.rmi.RemoteException;
import java.text.DateFormat;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.business.IBOLookup;



/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */
public class PhoneContracts extends Block {

  private final static String IW_BUNDLE_IDENTIFIER="is.idega.idegaweb.campus";
  protected IWResourceBundle iwrb;
  protected IWBundle iwb;
  private DateFormat df;
  private UserBusiness ub;
  private TextFormat tf;

  protected boolean isAdmin = false;

  public String getLocalizedNameKey(){
    return "phone_contracts";
  }

  public String getLocalizedNameValue(){
    return "Phone Contracts";
  }

  protected void control(IWContext iwc)throws RemoteException{
    //debugParameters(iwc);
    if(isAdmin){
       add(getSeachForm(iwc));
       if (iwc.isParameterSet("numbers")){
       		add(getPhoneTable(iwc,parseNumbers(iwc.getParameter("numbers"))));
       }
    }
    else
      add(Edit.formatText(iwrb.getLocalizedString("access_denied","Access denied")));
    //add(String.valueOf(iSubjectId));
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }
  
  public String[] parseNumbers(String numbers){
  	StringTokenizer tokener = new StringTokenizer(numbers,",;: ");
  	String[] nums = new String[tokener.countTokens()];
   	for (int i = 0; i < nums.length; i++) {
		nums[i] = tokener.nextToken();
	}
	return nums;
  }

  public PresentationObject makeLinkTable(int menuNr){
    Table LinkTable = new Table(6,1);

    return LinkTable;
  }
  
  private PresentationObject getSeachForm(IWContext iwc){
  	Form F = new Form();
  	Table T = new Table();
  	TextInput numberInput = new TextInput("numbers");
  	if(iwc.isParameterSet("numbers"))
  		numberInput.setContent(iwc.getParameter("numbers"));
  	SubmitButton search = new SubmitButton("Search");
  	T.add(numberInput,1,1);
  	T.add(search,2,1);
  	F.add(T);
  	return F;
  }

  private PresentationObject getPhoneTable(IWContext iwc,String[] phoneNumbers)throws RemoteException{
  	Table T = new Table();
  	int row = 1;
  	if(phoneNumbers!=null){
  		for (int i = 0; i < phoneNumbers.length; i++) {
			List contracts = PhoneFinder.listOfPhoneContracts(phoneNumbers[i]);
		  	if(contracts!=null){
		  		Contract contract;
		  		User user;
		  		Iterator iter = contracts.iterator();
		  		T.add(tf.format( phoneNumbers[i],tf.HEADER),1,row++);
		  		while(iter.hasNext()){
		  			contract = (Contract) iter.next();
		  			user = (User) ub.getUser(contract.getUserId().intValue());
		  			T.add(tf.format(user.getName()),2,row);
		  			T.add(tf.format(df.format(contract.getValidFrom())+" - "+df.format(contract.getValidTo())),3,row);
		  			T.add(tf.format(BuildingCacher.getApartmentString(contract.getApartmentId().intValue())),4,row);
		  			row++;
		  		}
		  	
		  	}
		  row++;row++;
		}
  	}
  	return T;
  }

  public void main(IWContext iwc)throws RemoteException{
  	iwrb = getResourceBundle(iwc);
	iwb = getBundle(iwc);
    isAdmin = iwc.hasEditPermission(this);
    df = DateFormat.getDateInstance(DateFormat.SHORT,iwc.getCurrentLocale());
    ub = (UserBusiness)IBOLookup.getServiceInstance(iwc,UserBusiness.class);
    tf = TextFormat.getInstance();
    control(iwc);
  }


}
