package com.idega.block.login.business;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.ejb.EJBException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import com.idega.builder.business.BuilderLogic;
import com.idega.business.IBOLookup;
import com.idega.business.IWEventListener;
import com.idega.core.accesscontrol.business.LoggedOnInfo;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.business.NotLoggedOnException;
import com.idega.core.accesscontrol.data.LoginInfo;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.data.GenericGroup;
import com.idega.core.user.business.UserBusiness;
import com.idega.core.user.data.User;
import com.idega.core.user.data.UserGroupRepresentative;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWException;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.user.business.UserProperties;
import com.idega.util.Encrypter;
import com.idega.util.IWTimestamp;
import com.idega.util.ListUtil;
import com.idega.util.reflect.MethodFinder;
/**
 * Title:        LoginBusiness The default login business handler for the Login presentation module
 * Description:
 * Copyright:    Copyright (c) 2000-2002 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>,<a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.1

 */

public class LoginBusiness implements IWEventListener
{
	public static String UserAttributeParameter = "user_login";
	public static String PermissionGroupParameter = "user_permission_groups";
	public static String LoginStateParameter = "login_state";
	public static String LoginStateMsgParameter = "login_state_msg";
	public static String LoginRedirectPageParameter = "login_redirect_page";
	private static String LoginAttributeParameter = "login_attributes";
	private static String UserGroupRepresentativeParameter = "ic_user_representative_group";
	private static String PrimaryGroupsParameter = "ic_user_primarygroups";
	private static String PrimaryGroupParameter = "ic_user_primarygroup";
	private static final String _APPADDRESS_LOGGED_ON_LIST = "ic_loggedon_list";
	private static final String _LOGGINADDRESS_LOGGED_ON_INFO = "ic_loggedon_info";
	public static final String USER_PROPERTY_PARAMETER = "user_properties";
	
	public static final int STATE_NO_STATE = 0;
	public static final int STATE_LOGGED_ON = 1;
	public static final int STATE_LOGGED_OUT = 2;
	public static final int STATE_NO_USER = 3;
	public static final int STATE_WRONG_PASSW=4;
	public static final int STATE_LOGIN_EXPIRED=5;	
	public static final int STATE_LOGIN_FAILED = 6;
	
	public LoginBusiness()
	{}
	public static boolean isLoggedOn(IWUserContext iwc)
	{
		if (iwc.getSessionAttribute(LoginAttributeParameter) == null)
		{
			return false;
		}
		return true;
	}
	
	
	public static void internalSetState(IWContext iwc, int state)
	{
		iwc.setSessionAttribute(LoginStateParameter, new Integer(state));
	}
	
	
	public static int internalGetState(IWContext iwc)
	{
		Integer state = (Integer) iwc.getSessionAttribute(LoginStateParameter);
		if(state!=null)
			return state.intValue();
		else
			return STATE_NO_STATE;
	}
	
	/**
	 * To get the userame of the current log-in attempt
	 * @return The username the current user is trying to log in with. Returns null if no log-in attemt is going on.
	 */
	protected String getLoginUserName(IWContext iwc)
	{
		return iwc.getParameter("login");
	}
	/**
	 * To get the password of the current log-in attempt
	 * @return The password the current user is trying to log in with. Returns null if no log-in attemt is going on.
	 */
	protected String getLoginPassword(IWContext iwc)
	{
		return iwc.getParameter("password");
	}
	/**
	 * @return True if logIn was succesful, false if it failed
	 */
	protected boolean logInUser(IWContext iwc, String username, String password)
	{
		try
		{
			int didLogin = verifyPasswordAndLogin(iwc, username, password);
			if (didLogin==STATE_LOGGED_ON)
			{
				//internalSetState(iwc, "loggedon");
				internalSetState(iwc,STATE_LOGGED_ON);
				return true;
			}
			return false;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	/**
	 * @return True if logOut was succesful, false if it failed
	 */
	protected boolean logOutUser(IWContext iwc)
	{
		try
		{
			logOut(iwc);
			//internalSetState(iwc, "loggedoff");
			internalSetState(iwc,STATE_LOGGED_OUT);
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	/**
	 * Used for the LoggedOnInfo object to be able to log off users when their session expires.
	 * @return True if logOut was succesful, false if it failed
	 */
	public static boolean logOutUserOnSessionTimeout(HttpSession session, LoggedOnInfo logOnInfo)
	{
		try
		{
			List ll = getLoggedOnInfoList(session);
			int indexOfLoggedOfInfo = ll.indexOf(logOnInfo);
			if (indexOfLoggedOfInfo > -1){
				LoggedOnInfo _logOnInfo = (LoggedOnInfo) ll.remove(indexOfLoggedOfInfo);
				LoginDBHandler.recordLogout(_logOnInfo.getLoginRecordId());
			}
			else return false;
			
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
		
	/**
	 * Invoked when the login failed
	 * Can be overrided in subclasses to alter behaviour
	 * By default this sets the state to "login failed" and does not log in a user
	 */
	protected void onLoginFailed(IWContext iwc,int loginState)
	{
		logOutUser(iwc);
		//internalSetState(iwc, "loginfailed");
		internalSetState(iwc,loginState);
	}
	/**
	 * Invoked when the login was succesful
	 * Can be overrided in subclasses to alter behaviour
	 * By default this sets the state to "logged on"
	 */
	protected void onLoginSuccessful(IWContext iwc)
	{
		//internalSetState(iwc, "loggedon");
		internalSetState(iwc,STATE_LOGGED_ON);
	}

	 public static boolean isLogOnAction(IWContext iwc){
	  	return "login".equals(getControlActionValue(iwc));
	  }

	  protected static boolean isLogOffAction(IWContext iwc){
	  	return "logoff".equals(getControlActionValue(iwc));
	  }

	  protected static boolean isTryAgainAction(IWContext iwc){
	  	return "tryagain".equals(getControlActionValue(iwc));
	  }

	  private static String getControlActionValue(IWContext iwc){
	  	return iwc.getParameter(LoginBusiness.LoginStateParameter);
	  }

	/**
	 * The method invoked when the login presentation module sends a login to this class
	 */
	public boolean actionPerformed(IWContext iwc) throws IWException
	{
		//System.out.println("LoginBusiness.actionPerformed");
		try
		{
			if (isLoggedOn(iwc))
			{
				if (isLogOffAction(iwc))
				{
					//logOut(iwc);
					//internalSetState(iwc,"loggedoff");
					logOutUser(iwc);
				}
			}
			else
			{

					if (isLogOnAction(iwc))
					{
						int canLogin = STATE_LOGGED_OUT;
						String username = getLoginUserName(iwc);
						String password = getLoginPassword(iwc);
						if ((username != null) && (password != null))
						{
							canLogin = verifyPasswordAndLogin(iwc, username, password);
							if (canLogin==STATE_LOGGED_ON)
							{
								//isLoggedOn(iwc);
								//internalSetState(iwc,"loggedon");
								// addon
								if (iwc.isParameterSet(LoginRedirectPageParameter))
								{
									//System.err.println("redirect parameter is set");
									BuilderLogic.getInstance().setCurrentPriorityPageID(
										iwc,
										iwc.getParameter(LoginRedirectPageParameter));
								}
								onLoginSuccessful(iwc);
							}
							else
							{
								//logOut(iwc);
								//internalSetState(iwc,"loginfailed");
								onLoginFailed(iwc,canLogin);
							}
						}
					}
					else if (isTryAgainAction(iwc))
					{
						//internalSetState(iwc, "loggedoff");
						internalSetState(iwc,STATE_LOGGED_OUT);
					}

			}
		}
		catch (Exception ex)
		{
			try
			{
				logOut(iwc);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			ex.printStackTrace(System.err);
			//throw (IdegaWebException)ex.fillInStackTrace();
		}
		return true;
	}
	/*

	  public boolean isAdmin(IWContext iwc)throws Exception{

	    return iwc.isAdmin();

	  }

	*/
	public static void setLoginAttribute(String key, Object value, IWUserContext iwc) throws NotLoggedOnException
	{
		if (isLoggedOn(iwc))
		{
			Object obj = iwc.getSessionAttribute(LoginAttributeParameter);
			((Hashtable) obj).put(key, value);
		}
		else
		{
			throw new NotLoggedOnException();
		}
	}
	public static Object getLoginAttribute(String key, IWUserContext iwc) throws NotLoggedOnException
	{
		if (isLoggedOn(iwc))
		{
			Object obj = iwc.getSessionAttribute(LoginAttributeParameter);
			if (obj == null)
			{
				return null;
			}
			else
			{
				return ((Hashtable) obj).get(key);
			}
		}
		else
		{
			throw new NotLoggedOnException();
		}
	}
	
	
	public static void removeLoginAttribute(String key, IWContext iwc)
	{
		if (isLoggedOn(iwc))
		{
			Object obj = iwc.getSessionAttribute(LoginAttributeParameter);
			if (obj != null)
			{
				((Hashtable) obj).remove(key);
			}
		}
		else if (iwc.getSessionAttribute(LoginAttributeParameter) != null)
		{
			iwc.removeSessionAttribute(LoginAttributeParameter);
		}
	}
	public static User getUser(IWUserContext iwc) /* throws NotLoggedOnException */
	{
		try
		{
			return (User) LoginBusiness.getLoginAttribute(UserAttributeParameter, iwc);
		}
		catch (NotLoggedOnException ex)
		{
			return null;
		}
		/*Object obj = iwc.getSessionAttribute(UserAttributeParameter);

		if (obj != null){

		  return (User)obj;

		}else{

		  throw new NotLoggedOnException();

		}

		*/
	}
	public static List getPermissionGroups(IWUserContext iwc) throws NotLoggedOnException
	{
		return (List) LoginBusiness.getLoginAttribute(PermissionGroupParameter, iwc);
	}
	public static UserGroupRepresentative getUserRepresentativeGroup(IWUserContext iwc) throws NotLoggedOnException
	{
		return (UserGroupRepresentative) LoginBusiness.getLoginAttribute(UserGroupRepresentativeParameter, iwc);
	}
	public static GenericGroup getPrimaryGroup(IWUserContext iwc) throws NotLoggedOnException
	{
		return (GenericGroup) LoginBusiness.getLoginAttribute(PrimaryGroupParameter, iwc);
	}
	protected static void setUser(IWUserContext iwc, User user)
	{
		LoginBusiness.setLoginAttribute(UserAttributeParameter, user, iwc);
	}
	protected static void setPermissionGroups(IWUserContext iwc, List value)
	{
		LoginBusiness.setLoginAttribute(PermissionGroupParameter, value, iwc);
	}
	protected static void setUserRepresentativeGroup(IWUserContext iwc, UserGroupRepresentative value)
	{
		LoginBusiness.setLoginAttribute(UserGroupRepresentativeParameter, value, iwc);
	}
	protected static void setPrimaryGroup(IWUserContext iwc, GenericGroup value)
	{
		LoginBusiness.setLoginAttribute(PrimaryGroupParameter, value, iwc);
	}
	private boolean logIn(IWContext iwc, LoginTable loginTable, String login) throws Exception
	{
		com.idega.core.user.data.UserHome uHome = (com.idega.core.user.data.UserHome) com.idega.data.IDOLookup.getHome(User.class);
		User user = uHome.findByPrimaryKey(loginTable.getUserId());
		
		iwc.setSessionAttribute(LoginAttributeParameter, new Hashtable());
		LoginBusiness.setUser(iwc, user);
		//List groups = AccessControl.getPermissionGroups(user);
		com.idega.user.business.UserBusiness userbusiness =  (com.idega.user.business.UserBusiness)com.idega.business.IBOLookup.getServiceInstance(iwc,com.idega.user.business.UserBusiness.class);
		com.idega.user.data.User newUser = com.idega.user.Converter.convertToNewUser(user);
		List groups = ListUtil.convertCollectionToList(userbusiness.getUserGroups(newUser));
		//List groups = UserBusiness.getUserGroups(user);
		if (groups != null)
		{
			LoginBusiness.setPermissionGroups(iwc, groups);
		}
		int userGroupId = user.getGroupID();
		if (userGroupId != -1)
		{
			LoginBusiness.setUserRepresentativeGroup(
				iwc,
				(
					(com.idega.core.user.data.UserGroupRepresentativeHome) com.idega.data.IDOLookup.getHomeLegacy(
						UserGroupRepresentative.class)).findByPrimaryKeyLegacy(
					userGroupId));
		}
		if (user.getPrimaryGroupID() != -1)
		{
			GenericGroup primaryGroup =
				(
					(com.idega.core.data.GenericGroupHome) com.idega.data.IDOLookup.getHomeLegacy(
						GenericGroup.class)).findByPrimaryKeyLegacy(
					user.getPrimaryGroupID());
			LoginBusiness.setPrimaryGroup(iwc, primaryGroup);
		}
		int loginRecordId = LoginDBHandler.recordLogin(loginTable.getID(), iwc.getRemoteIpAddress());
		LoggedOnInfo lInfo = new LoggedOnInfo();
		lInfo.setLogin(login);
		//lInfo.setSession(iwc.getSession());
		lInfo.setTimeOfLogon(IWTimestamp.RightNow());
		lInfo.setUser(user);
		lInfo.setLoginRecordId(loginRecordId);
		getLoggedOnInfoList(iwc).add(lInfo);
		setLoggedOnInfo(lInfo, iwc);
		
		iwc.setSessionAttribute(user.getID()+"-LoginInfo",lInfo);
		
		UserProperties properties = new UserProperties(iwc.getApplication(),user.getID());
	  iwc.setSessionAttribute(USER_PROPERTY_PARAMETER,properties);
	
		return true;
	}
	private int verifyPasswordAndLogin(IWContext iwc, String login, String password) throws Exception
	{
		LoginTable[] login_table =(LoginTable[]) (com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance()).findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(),	login);
		if(login_table == null)
			return STATE_NO_USER;
		//if (login_table != null && login_table.length > 0)
		if(login_table.length>0){
			LoginTable loginTable = login_table[0];
			if(isLoginExpired(loginTable))
				return STATE_LOGIN_EXPIRED;
			if (Encrypter.verifyOneWayEncrypted(login_table[0].getUserPassword(), password))
			{
				//returner = logIn(iwc, login_table[0], login);
				if(logIn(iwc, login_table[0], login))
					return STATE_LOGGED_ON;
			}
			else
				return STATE_WRONG_PASSW;
		}
		else
			return STATE_NO_USER;
		
		return STATE_LOGIN_FAILED;
	}
	public static boolean verifyPassword(User user, String login, String password) throws IOException, SQLException
	{
		boolean returner = false;
		LoginTable[] login_table =
			(LoginTable[]) (com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance()).findAllByColumn(
				com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserIDColumnName(),
				Integer.toString(user.getID()),
				com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(),
				login);
		if (login_table != null && login_table.length > 0)
		{
			if (Encrypter.verifyOneWayEncrypted(login_table[0].getUserPassword(), password))
			{
				returner = true;
			}
		}
		return returner;
	}
	private void logOut(IWContext iwc) throws Exception
	{
		if (iwc.getSessionAttribute(LoginAttributeParameter) != null)
		{
			// this.getLoggedOnInfoList(iwc).remove(this.getLoggedOnInfo(iwc));
			List ll = this.getLoggedOnInfoList(iwc);
			int indexOfLoggedOfInfo = ll.indexOf(getLoggedOnInfo(iwc));
			if (indexOfLoggedOfInfo > -1)
			{
				LoggedOnInfo _logOnInfo = (LoggedOnInfo) ll.remove(indexOfLoggedOfInfo);
				LoginDBHandler.recordLogout(_logOnInfo.getLoginRecordId());
			}
			iwc.removeSessionAttribute(LoginAttributeParameter);
			
			UserProperties properties = (UserProperties) iwc.getSessionAttribute(USER_PROPERTY_PARAMETER);
			if ( properties != null )
				properties.store();
		  iwc.removeSessionAttribute(USER_PROPERTY_PARAMETER);
		}
	}
		
	/**

	 * returns empty List if no one is logged on

	 */
	public static List getLoggedOnInfoList(IWContext iwc)
	{
		List loggedOnList = (List) iwc.getApplicationAttribute(_APPADDRESS_LOGGED_ON_LIST);
		if (loggedOnList == null)
		{
			loggedOnList = new Vector();
			iwc.setApplicationAttribute(_APPADDRESS_LOGGED_ON_LIST, loggedOnList);
		}
		return loggedOnList;
	}
	
	public static List getLoggedOnInfoList(HttpSession session){
		List loggedOnList = null;
		MethodFinder finder = MethodFinder.getInstance();
		ServletContext context = null;
		
		
		try {
			Method method = finder.getMethodsWithNameAndNoParameters(HttpSession.class,"getServletContext");
			try {
				context = (ServletContext) method.invoke(session,null);
			}
			catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			}
			catch (IllegalAccessException e1) {
				e1.printStackTrace();
			}
			catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
		}
		catch (NoSuchMethodException e) {
			System.out.println("The method session.getServletContext() is not in this implementation of the Servlet spec.");
			e.printStackTrace();
		}
		 
		
		if(context!=null){
			loggedOnList = (List) context.getAttribute(_APPADDRESS_LOGGED_ON_LIST);
		}
		
		
		if (loggedOnList == null){
			loggedOnList = new Vector();
			if(context!=null){
				context.setAttribute(_APPADDRESS_LOGGED_ON_LIST, loggedOnList);
			}
		}
		return loggedOnList;
	}
	
	
	
	public static LoggedOnInfo getLoggedOnInfo(IWUserContext iwc) throws NotLoggedOnException
	{
		return (LoggedOnInfo) getLoginAttribute(_LOGGINADDRESS_LOGGED_ON_INFO, iwc);
	}
	
	
	public static void setLoggedOnInfo(LoggedOnInfo lInfo, IWContext iwc) throws NotLoggedOnException
	{
		setLoginAttribute(_LOGGINADDRESS_LOGGED_ON_INFO, lInfo, iwc);
	}
	public static LoginContext changeUserPassword(User user, String password) throws Exception
	{
		LoginTable login = LoginDBHandler.getUserLogin(user.getID());
		LoginDBHandler.changePassword(login, password);
		LoginContext loginContext = new LoginContext(user, login.getUserLogin(), password);
		return loginContext;
	}
	public static LoginContext createNewUser(
		String fullName,
		String email,
		String preferredUserName,
		String preferredPassword)
	{
		UserBusiness ub = new UserBusiness();
		StringTokenizer tok = new StringTokenizer(fullName);
		String first = "";
		String middle = "";
		String last = "";
		if (tok.hasMoreTokens())
			first = tok.nextToken();
		if (tok.hasMoreTokens())
			middle = tok.nextToken();
		if (tok.hasMoreTokens())
			last = tok.nextToken();
		else
		{
			last = middle;
			middle = "";
		}
		LoginContext loginContext = null;
		try
		{
			User user = ub.insertUser(first, middle, last, "", null, null, null, null);
			String login = preferredUserName;
			String pass = preferredPassword;
			if (user != null)
			{
				if (email != null && email.length() > 0)
					ub.addNewUserEmail(user.getID(), email);
				if (login == null)
					login = LoginCreator.createLogin(user.getName());
				if (pass == null)
					pass = LoginCreator.createPasswd(8);
				LoginDBHandler.createLogin(user.getID(), login, pass);
				loginContext = new LoginContext(user, login, pass);
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return loginContext;
	}

	// added for cookie login maybe unsafe ( Aron )
	protected boolean logInUnVerified(IWContext iwc,String login) throws Exception{
    boolean returner = false;
    LoginTable[] login_table = (LoginTable[]) (com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance()).findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getUserLoginColumnName(),login);
    if(login_table != null && login_table.length > 0){
        returner = logIn(iwc,login_table[0],login);
        if(returner)
          onLoginSuccessful(iwc);
    }
    return returner;
  }

	public boolean logInByPersonalID(IWContext iwc, String personalID) throws Exception{
		boolean returner = false;
		try {
			com.idega.user.data.User user = getUserBusiness(iwc).getUser(personalID);
			LoginTable[] login_table = (LoginTable[]) (com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance()).findAllByColumn(com.idega.core.accesscontrol.data.LoginTableBMPBean.getColumnNameUserID(),user.getPrimaryKey().toString());
			if(login_table != null && login_table.length > 0){
					returner = logIn(iwc,login_table[0],login_table[0].getUserLogin());
					if(returner)
						onLoginSuccessful(iwc);
			}
		}
		catch (EJBException e) {
			returner = false;
		}
		return returner;
	}
	
	public boolean isLoginExpired(LoginTable loginTable){
		LoginInfo loginInfo = LoginDBHandler.getLoginInfo(loginTable.getID());
		return loginInfo.isLoginExpired();	
	}
	
	protected com.idega.user.business.UserBusiness getUserBusiness(IWApplicationContext iwac) throws RemoteException {
		return (com.idega.user.business.UserBusiness) IBOLookup.getServiceInstance(iwac,com.idega.user.business.UserBusiness.class);
	}

}
