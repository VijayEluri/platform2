package se.idega.idegaweb.commune.message.business;

import com.idega.business.IBOSessionBean;
import com.idega.idegaweb.IWPropertyList;
import com.idega.user.business.UserProperties;

/**
 * @author Laddi
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MessageSessionBean extends IBOSessionBean implements MessageSession {

	public static final String MESSAGE_PROPERTIES = "message_properties";
	public static final String USER_PROP_SEND_TO_MESSAGE_BOX = "msg_send_box";
	public static final String USER_PROP_SEND_TO_EMAIL = "msg_send_email";
	
	protected UserProperties getUserPreferences() throws Exception {
		UserProperties property = getUserContext().getUserProperties();
		return property;
	}
	
	protected IWPropertyList getUserMessagePreferences() {
		try{
			return getUserPreferences().getProperties(MESSAGE_PROPERTIES);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public boolean getIfUserPreferesMessageByEmail(){
		IWPropertyList propertyList = getUserMessagePreferences();
		if (propertyList != null) {
			String property = propertyList.getProperty(USER_PROP_SEND_TO_EMAIL);
			if(property!=null)
				return Boolean.valueOf(property).booleanValue();
		}
		return true;
	}

	public boolean getIfUserPreferesMessageInMessageBox(){
		IWPropertyList propertyList = getUserMessagePreferences();
		if (propertyList != null) {
			String property = propertyList.getProperty(USER_PROP_SEND_TO_MESSAGE_BOX);
			if(property!=null)
				return Boolean.valueOf(property).booleanValue();
		}
		return true;
	}

	public void setIfUserPreferesMessageByEmail(boolean preference){
		IWPropertyList propertyList = getUserMessagePreferences();
		propertyList.setProperty(USER_PROP_SEND_TO_EMAIL, new Boolean(preference));
	}

	public void setIfUserPreferesMessageInMessageBox(boolean preference){
		IWPropertyList propertyList = getUserMessagePreferences();
		propertyList.setProperty(USER_PROP_SEND_TO_MESSAGE_BOX, new Boolean(preference));
	}

}
