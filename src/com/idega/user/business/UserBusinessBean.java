package com.idega.user.business;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import com.idega.builder.data.IBDomain;
import com.idega.builder.data.IBPage;
import com.idega.builder.data.IBPageHome;
import com.idega.business.IBOLookup;
import com.idega.business.IBORuntimeException;
import com.idega.core.accesscontrol.business.AccessControl;
import com.idega.core.accesscontrol.business.LoginCreateException;
import com.idega.core.accesscontrol.business.LoginDBHandler;
import com.idega.core.accesscontrol.data.ICPermission;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.business.AddressBusiness;
import com.idega.core.data.Address;
import com.idega.core.data.AddressHome;
import com.idega.core.data.AddressType;
import com.idega.core.data.Country;
import com.idega.core.data.CountryHome;
import com.idega.core.data.Email;
import com.idega.core.data.EmailHome;
import com.idega.core.data.Phone;
import com.idega.core.data.PhoneHome;
import com.idega.core.data.PostalCode;
import com.idega.core.data.PostalCodeHome;
import com.idega.data.IDOCreateException;
import com.idega.data.IDOLookup;
import com.idega.data.IDOQuery;
//import com.idega.data.IDORelationshipException;
import com.idega.data.IDOStoreException;
import com.idega.data.IDOUtil;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.IWContext;
import com.idega.user.data.Gender;
import com.idega.user.data.GenderHome;
import com.idega.user.data.Group;
import com.idega.user.data.GroupBMPBean;
import com.idega.user.data.GroupDomainRelation;
import com.idega.user.data.GroupDomainRelationType;
import com.idega.user.data.GroupHome;
import com.idega.user.data.GroupRelation;
import com.idega.user.data.GroupRelationHome;
import com.idega.user.data.User;
import com.idega.user.data.UserGroupPlugIn;
import com.idega.user.data.UserHome;
import com.idega.util.IWTimestamp;
import com.idega.util.text.Name;

 /**
  * <p>Title: idegaWeb</p>
  * <p>Description: </p>
  * <p>Copyright: Copyright (c) 2002</p>
  * <p>Company: idega Software</p>
  * @author <a href="gummi@idega.is">Gu?mundur ?g?st S?mundsson</a>
  * @version 1.0
  */

public class UserBusinessBean extends com.idega.business.IBOServiceBean implements UserBusiness{

  // remove use of "null" when metadata can be removed
  private static final String NULL = "null";
  
  private static final String JOB_META_DATA_KEY = "job";
	private static final String WORKPLACE_META_DATA_KEY = "workplace";

	private static final String SESSION_KEY_TOP_NODES =  "top_nodes_for_user";

  private GroupHome groupHome;
  private UserHome userHome;

  private EmailHome emailHome;
  private AddressHome addressHome;
  private PhoneHome phoneHome;
  
  private Gender male,female;

  public UserBusinessBean() {
  }

  public UserHome getUserHome(){
    if(userHome==null){
      try{
        userHome = (UserHome)IDOLookup.getHome(User.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return userHome;
  }

//  public UserGroupRepresentativeHome getUserGroupRepresentativeHome(){
//    if(userRepHome==null){
//      try{
//        userRepHome = (UserGroupRepresentativeHome)IDOLookup.getHome(UserGroupRepresentative.class);
//      }
//      catch(RemoteException rme){
//        throw new RuntimeException(rme.getMessage());
//      }
//    }
//    return userRepHome;
//  }

  public GroupHome getGroupHome(){
    if(groupHome==null){
      try{
        groupHome = (GroupHome)IDOLookup.getHome(Group.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return groupHome;
  }



  public EmailHome getEmailHome(){
    if(emailHome==null){
      try{
        emailHome = (EmailHome)IDOLookup.getHome(Email.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return emailHome;
  }


  public AddressHome getAddressHome(){
    if(addressHome==null){
      try{
        addressHome = (AddressHome)IDOLookup.getHome(Address.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return addressHome;
  }


  public PhoneHome getPhoneHome(){
    if(phoneHome==null){
      try{
        phoneHome = (PhoneHome)IDOLookup.getHome(Phone.class);
      }
      catch(RemoteException rme){
        throw new RuntimeException(rme.getMessage());
      }
    }
    return phoneHome;
  }

  /**
   * @deprecated replaced with createUser
   */
  public User insertUser(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group) throws CreateException,RemoteException{
      return createUser(firstname,middlename,lastname,displayname,null,description,gender,date_of_birth,primary_group);
  }
  
  /**
 * Method createUserByPersonalIDIfDoesNotExist either created a new user or updates an old one.
 * @param fullName
 * @param personalID
 * @param gender
 * @param dateOfBirth
 * @return User
 * @throws CreateException
 * @throws RemoteException
 */
  public User createUserByPersonalIDIfDoesNotExist(String fullName,String personalID, Gender gender, IWTimestamp dateOfBirth) throws CreateException,RemoteException{
    User user;
    try{
      user = getUserHome().findByPersonalID(personalID);
      user.setFullName(fullName);
      
      user.setGender( (Integer)gender.getPrimaryKey() );
      user.setDateOfBirth(dateOfBirth.getDate());
      user.store();
    }
    catch(FinderException ex){
      Name name = new Name(fullName);
    		
      user = createUser(name.getFirstName(), name.getMiddleName() , name.getLastName() , personalID, gender, dateOfBirth);
    }

    return user;
  }
  
/**
 * Method createUserByPersonalIDIfDoesNotExist does what is says.
 * @param firstName
 * @param middleName
 * @param lastName
 * @param personalID
 * @param gender
 * @param dateOfBirth
 * @return User
 * @throws CreateException
 * @throws RemoteException
 */
  public User createUserByPersonalIDIfDoesNotExist(String firstName, String middleName, String lastName,String personalID, Gender gender, IWTimestamp dateOfBirth) throws CreateException,RemoteException{
    User user;
    StringBuffer fullName = new StringBuffer();

	  firstName = (firstName==null) ? "" : firstName;
	  middleName = (middleName==null) ? "" : middleName;
	  lastName = (lastName==null) ? "" : lastName;
	
	  fullName.append(firstName).append(" ").append(middleName).append(" ").append(lastName);

      user = createUserByPersonalIDIfDoesNotExist(fullName.toString(),personalID,gender,dateOfBirth);

    return user;
  }
  
	public User createUser(String firstName, String middleName, String lastName, String displayname, String personalID, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group) throws CreateException,RemoteException{
		return createUser(firstName, middleName, lastName, displayname, personalID, description, gender, date_of_birth, primary_group,null);
	}


  public User createUser(String firstName, String middleName, String lastName, String displayname, String personalID, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group,String fullName) throws CreateException,RemoteException{
    try{
      User userToAdd = getUserHome().create();

			if (fullName == null) {
	      StringBuffer fullNameBuffer = new StringBuffer();

  	    firstName = (firstName==null) ? "" : firstName;
    	  middleName = (middleName==null) ? "" : middleName;
      	lastName = (lastName==null) ? "" : lastName;

	      fullNameBuffer.append(firstName).append(" ").append(middleName).append(" ").append(lastName);
	      fullName = fullNameBuffer.toString();
			}

      userToAdd.setFullName(fullName);

      
      /*userToAdd.setFirstName(firstName);
      userToAdd.setMiddleName(middleName);
      userToAdd.setLastName(lastName);*/
      
      if(displayname != null){
        userToAdd.setDisplayName(displayname);
      }
      if(description != null){
        userToAdd.setDescription(description);
      }
      if(personalID!=null){
        userToAdd.setPersonalID(personalID);
      }
      if(gender != null){
        userToAdd.setGender(gender);
      }
      if(date_of_birth != null){
        userToAdd.setDateOfBirth(date_of_birth.getSQLDate());
      }
      if(primary_group != null){
        userToAdd.setPrimaryGroupID(primary_group);
      }
      userToAdd.store();
      setUserUnderDomain(this.getIWApplicationContext().getDomain(), userToAdd, (GroupDomainRelationType)null);
  //    UserGroupRepresentative group = (UserGroupRepresentative)this.getUserGroupRepresentativeHome().create();
  //    group.setName(userToAdd.getName());
  //    group.setDescription("User representative in table ic_group");
  //    group.store();
  //    userToAdd.setGroup(group);
  //    userToAdd.store();

      if(primary_group != null){
        Group prgr = userToAdd.getPrimaryGroup();
        prgr.addGroup(userToAdd);
      }
      return userToAdd;
    }
    catch(Exception e){
      throw new IDOCreateException(e);
    }

  }


  public void setUserUnderDomain(IBDomain domain, User user, GroupDomainRelationType type) throws CreateException,RemoteException{
    GroupDomainRelation relation = (GroupDomainRelation)IDOLookup.create(GroupDomainRelation.class);
    relation.setDomain(domain);
    relation.setRelatedUser(user);

    if(type != null){
      relation.setRelationship(type);
    }

    relation.store();
  }

  /**
   * Generates a login for a user with a random password and a login derived from the users name (or random login if all possible logins are taken)
	 * @param userId the id for the user.
	 * @throws LoginCreateException If an error occurs creating login for the user.
	 */   
  public LoginTable generateUserLogin(int userID)throws LoginCreateException, RemoteException{
    //return this.generateUserLogin(userID);
    return LoginDBHandler.generateUserLogin(userID);
  }

  /**
   * Generates a login for a user with a random password and a login derived from the users name (or random login if all possible logins are taken)
   */
  public LoginTable generateUserLogin(User user)throws LoginCreateException, RemoteException{
    //return LoginDBHandler.generateUserLogin(user);
    int userID = ((Integer)user.getPrimaryKey()).intValue();
    return this.generateUserLogin(userID);
  }

  /**
   * Creates a user with a firstname,middlename, lastname, where middlename can be null
   */
  public User createUser(String firstname, String middlename, String lastname) throws CreateException,RemoteException{
    return createUser(firstname,middlename,lastname,(String)null);
  }


  /**
   * Creates a new user with a firstname,middlename, lastname and personalID where middlename and personalID can be null
   */
  public User createUser(String firstname, String middlename, String lastname,String personalID) throws CreateException,RemoteException{
      return createUser(firstname,middlename,lastname,null,personalID,null,null,null,null);
  }
  /**
   * Creates a new user with a firstname,middlename, lastname and primaryGroupID where middlename can be null
   */
   public User createUser(String firstName, String middleName, String lastName, int primary_groupID) throws CreateException,RemoteException{
 		return createUser(firstName,middleName,lastName,null,null,null,null,null,new Integer(primary_groupID));
  }
  
  /**
   * Creates a new user with a firstname,middlename, lastname and primaryGroupID where middlename can be null but primary_group can not be noull
   */
  public User createUser(String firstName, String middleName, String lastName, Group primary_group) throws CreateException,RemoteException{
 		return createUser(firstName,middleName,lastName,null,null,null,null,null,(Integer)primary_group.getPrimaryKey());
  }

  /**
   * Creates a new user with a firstname,middlename, lastname ,personalID and gender where middlename and personalID can be null
   */
  public User createUser(String firstname, String middlename, String lastname,String personalID, Gender gender) throws CreateException,RemoteException{
      return createUser(firstname,middlename,lastname,null,personalID,null,(Integer)gender.getPrimaryKey(),null,null);
  }

  /**
   * Creates a new user with a firstname,middlename, lastname ,personalID, gender and date of birth where middlename,personalID,gender,dateofbirth can be null
   * @throws NullPointerException if primaryGroup is null
   */
  public User createUser(String firstname, String middlename, String lastname,String personalID, Gender gender, IWTimestamp dateOfBirth,Group primaryGroup) throws CreateException,RemoteException{
      return createUser(firstname,middlename,lastname,null,personalID,null,(Integer)gender.getPrimaryKey(),dateOfBirth,(Integer)primaryGroup.getPrimaryKey());
  }

  /**
   * Creates a new user with a firstname,middlename, lastname ,personalID, gender and date of birth where middlename,personalID,gender,dateofbirth can be null
   */
  public User createUser(String firstname, String middlename, String lastname,String personalID, Gender gender, IWTimestamp dateOfBirth) throws CreateException,RemoteException{
      return createUser(firstname,middlename,lastname,null,personalID,null,(Integer)gender.getPrimaryKey(),dateOfBirth,null);
  }

	public User createUserWithLogin(String firstname, String middlename, String lastname, String SSN, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime,String encryptionType) throws CreateException {
		return createUserWithLogin(firstname, middlename, lastname, SSN, displayname, description, gender, date_of_birth, primary_group, userLogin, password, accountEnabled, modified, daysOfValidity, passwordExpires, userAllowedToChangePassw,changeNextTime,encryptionType,null);
	}


	public User createUserWithLogin(String firstname, String middlename, String lastname, String SSN, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime,String encryptionType,String fullName) throws CreateException {
		UserTransaction transaction = this.getSessionContext().getUserTransaction();
		try{
			transaction.begin();
			User newUser;
			// added by Aron 07.01.2002 ( aron@idega.is )
			if(primary_group==null)
				primary_group = new Integer(GroupBMPBean.GROUP_ID_USERS);
        
			//newUser = insertUser(firstname,middlename, lastname,null,null,null,null,primary_group);
			newUser = createUser(firstname,middlename,lastname,displayname,SSN,description,gender,date_of_birth,primary_group,fullName);

			if (userLogin != null && password != null && !userLogin.equals("") && !password.equals("")) {
				LoginDBHandler.createLogin(newUser,userLogin,password,accountEnabled,modified,
																	 daysOfValidity,passwordExpires,userAllowedToChangePassw,changeNextTime,encryptionType);
			}
			transaction.commit();
			return newUser;
		}
		catch(Exception e){
			e.printStackTrace();
			try{
				transaction.rollback();
			}
			catch(SystemException se){}
			throw new CreateException(e.getMessage());
		}
		
	}

  public User createUserWithLogin(String firstname, String middlename, String lastname, String displayname, String description, Integer gender, IWTimestamp date_of_birth, Integer primary_group, String userLogin, String password, Boolean accountEnabled, IWTimestamp modified, int daysOfValidity, Boolean passwordExpires, Boolean userAllowedToChangePassw, Boolean changeNextTime,String encryptionType) throws CreateException{
  	return createUserWithLogin(firstname,middlename,lastname,null,displayname,description,gender,date_of_birth,primary_group,userLogin,password,accountEnabled,modified,daysOfValidity,passwordExpires,userAllowedToChangePassw,changeNextTime,encryptionType);
  }


/*
  public User getUser(int userGroupRepresentativeID) throws SQLException {
    List l = EntityFinder.findAllByColumn(com.idega.user.data.UserBMPBean.getStaticInstance(User.class),com.idega.user.data.UserBMPBean._COLUMNNAME_USER_GROUP_ID,userGroupRepresentativeID);
    if(l != null && l.size() > 0){
      return ((User)l.get(0));
    }
    return null;
  }

  public int getUserID(int userGroupRepresentativeID) throws SQLException {
    User user = getUser(userGroupRepresentativeID);
    if(user != null){
      return user.getID();
    }
    return -1;
  }
*/

/**
 * This methods remoces this useer from all groups and deletes his login.
 */
  public  void deleteUser(int userId, User currentUser) throws RemoveException {
    User delUser = getUser(userId);
    deleteUser(delUser, currentUser);
  }



/**
 * This methods remoces this useer from all groups and deletes his login.
 */
  public  void deleteUser(User delUser, User currentUser) throws RemoveException {
    
    try {

			Collection groups = getGroupBusiness().getParentGroups(delUser);
			
			if( groups!=null && !groups.isEmpty() ){
				Iterator iter = groups.iterator();
				while (iter.hasNext()) {
				Group group = (Group) iter.next();
				removeUserFromGroup(delUser, group, currentUser);
				}
			}
			
			LoginDBHandler.deleteUserLogin(delUser.getID());
			
      //delUser.removeAllAddresses();
      //delUser.removeAllEmails();
      //delUser.removeAllPhones();

      
      
			/*try {
      	this.getGroupBusiness().deleteGroup(groupId);
			}catch (FinderException fe) {
				System.out.println("[UserBusinessBean] : cannot find group to delete with user");	
			}*/
			
	
      delUser.delete(currentUser.getID());
      delUser.store();
    }
    catch(Exception e){
  		//e.printStackTrace(System.err);
      throw new RemoveException(e.getMessage());
    }
  }
  
  public void removeUserFromGroup(int userId, Group group, User currentUser) {
    User user = getUser(userId);
    removeUserFromGroup(user, group, currentUser);
  }
  
  
  public void removeUserFromGroup(User user, Group group, User currentUser)  {
    group.removeUser(user, currentUser);
  }

  public  void setPermissionGroup(User user, Integer primaryGroupId) throws IDOStoreException,RemoteException {
    if(primaryGroupId != null){
      user.setPrimaryGroupID(primaryGroupId);
      user.store();
    }
  }

  /**
   * Male: M, male, 0
   * Female: F, female, 1
   */
  public Integer getGenderId(String gender) throws Exception{
    try{
      GenderHome home = (GenderHome) this.getIDOHome(Gender.class);

      if(gender == "M" || gender == "male" || gender == "0" ){
        if(male == null){
          male = home.getMaleGender();
        }
        return (Integer) male.getPrimaryKey();
      }
      else if(gender == "F" || gender == "female" || gender == "1" ){
        if(female == null){
          female = home.getFemaleGender();
        }
        return (Integer) female.getPrimaryKey();
      }
      else{
        //throw new RuntimeException("String gender must be: M, male, 0, F, female or 1 ");
        return null;
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }

  }

  public  Phone[] getUserPhones(int userId)throws RemoteException{
    try {
      Collection phones = this.getUser(userId).getPhones();
//	  if(phones != null){
        return (Phone[])phones.toArray(new Phone[phones.size()]);
//	  }
      //return (Phone[]) ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
    }
    catch (EJBException ex) {
      ex.printStackTrace();
      return null;
    }
  }
  
	public  Phone[] getUserPhones(User user)throws RemoteException{
		try {
			Collection phones = user.getPhones();
//		if(phones != null){
				return (Phone[])phones.toArray(new Phone[phones.size()]);
//		}
			//return (Phone[]) ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
		}
		catch (EJBException ex) {
			ex.printStackTrace();
			return null;
		}
	}

  public  Phone getUserPhone(int userId, int phoneTypeId)throws RemoteException{
    try {
      Phone[] result = this.getUserPhones(userId);
      //IDOLegacyEntity[] result = ((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).findRelated(com.idega.core.data.PhoneBMPBean.getStaticInstance(Phone.class));
      if(result != null){
        for (int i = 0; i < result.length; i++) {
          if(((Phone)result[i]).getPhoneTypeId() == phoneTypeId){
            return (Phone)result[i];
          }
        }
      }
      return null;
    }
    catch (EJBException ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Email getUserMail(int userId) {
    return getUserMail(this.getUser(userId));
  }

  public  Email getUserMail(User user) {
    try {
      Collection L = user.getEmails();
      if(L != null){
        if ( ! L.isEmpty() )
          return (Email)L.iterator().next();
      }
      return null;
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public void updateUserPhone(int userId, int phoneTypeId, String phoneNumber) throws EJBException {
    try{
    Phone phone = getUserPhone(userId,phoneTypeId);
    boolean insert = false;
    if ( phone == null ) {
      phone = this.getPhoneHome().create();
      phone.setPhoneTypeId(phoneTypeId);
      insert = true;
    }

    if ( phoneNumber != null ) {
      phone.setNumber(phoneNumber);
    }

    phone.store();
    if(insert){
      //((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).addTo(phone);
      this.getUser(userId).addPhone(phone);
    }

    }
    catch(Exception e){
      e.printStackTrace();
      throw new EJBException(e.getMessage());
    }


  }

  public void updateUserMail(int userId, String email) throws CreateException,RemoteException {
    Email mail = getUserMail(userId);
    boolean insert = false;
    if ( mail == null ) {
      mail = this.getEmailHome().create();
      insert = true;
    }

    if ( email != null ) {
      mail.setEmailAddress(email);
    }
    mail.store();
    if(insert){
      //((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(userId).addTo(mail);
      try{
       this.getUser(userId).addEmail(mail);
      }
      catch(Exception e){
        throw new RemoteException(e.getMessage());
      }
    }

  }

  public void updateUserJob(int userId, String job) {
    if (job == null || job.length() == 0)
      job = NULL;
    User user = getUser(userId);
    user.setMetaData(JOB_META_DATA_KEY, job);
    user.store();
  }
    
  public String getUserJob(User user)  {
    String job = (String) user.getMetaData(JOB_META_DATA_KEY);
    if (job == null || NULL.equals(job))
      return "";
    else
      return job;
  }  

	public void updateUserWorkPlace(int userId, String workPlace) {
		if (workPlace == null || workPlace.length() == 0)
		workPlace = NULL;
		User user = getUser(userId);
		user.setMetaData(WORKPLACE_META_DATA_KEY, workPlace);
		user.store();
	}
    
	public String getUserWorkPlace(User user)  {
		String workPlace = (String) user.getMetaData(WORKPLACE_META_DATA_KEY);
		if (workPlace == null || NULL.equals(workPlace))
			return "";
		else
			return workPlace;
	}  


  /**
   * @deprecated user getUsersMainAddress instead.
   * Gets the users main address and returns it.
   * @returns the address if found or null if not.
   */
  public Address getUserAddress1(int userID) throws EJBException,RemoteException{
 		return getUsersMainAddress(userID);
  }
  
  /**
   * Gets the user's main address by addresstype and returns it.
   * @returns the address if found or null if not.
   */
  public Address getUserAddressByAddressType(int userID,AddressType type) throws EJBException,RemoteException{
  	try {
  		return getAddressHome().findUserAddressByAddressType(userID,type);
  	}
  	catch (FinderException fe) {
  		return null;
  	}
  }
  
   /**
   * Gets the user's main address and returns it.
   * @returns the address if found or null if not.
   */
  public Address getUsersMainAddress(int userID) throws EJBException,RemoteException{
  	try {
  		return getAddressHome().findPrimaryUserAddress(userID);
  	}
  	catch (FinderException fe) {
  		return null;
  	}
  }
  
  /**
   * Gets the users main addresses and returns them.
   * @returns a collection of addresses if found or null if not.
   */
  public Collection getUsersMainAddresses(String[] userIDs) throws EJBException,RemoteException{
  	try {
  		return getAddressHome().findPrimaryUserAddresses(userIDs);
  	}
  	catch (FinderException fe) {
  		return null;
  	}
  }
  
	public Collection getUsersMainAddresses(IDOQuery query) throws EJBException,RemoteException{
		try {
			return getAddressHome().findPrimaryUserAddresses(query);
		}
		catch (FinderException fe) {
			return null;
		}
	}
  
  /**
   * Gets the users main address and returns it.
   * @returns the address if found or null if not.
   */
  public Address getUsersMainAddress(User user) throws RemoteException{
    return getUsersMainAddress(((Integer)user.getPrimaryKey()).intValue());
  }

  /**
   * Gets the users co address and returns it.
   * @returns the address if found or null if not.
   */
  public Address getUsersCoAddress(User user) throws RemoteException{
    return getUsersCoAddress(((Integer)user.getPrimaryKey()).intValue());
  }

  /**
   * Gets the users co address and returns it.
   * @returns the address if found or null if not.
   */
  public Address getUsersCoAddress(int userId) throws RemoteException{
    AddressType coAddressType = getAddressHome().getAddressType2();
    return getUserAddressByAddressType(userId, coAddressType);
  }


  /**
   * Gets the users and returns them.
   * @returns a collection of users if found or null if not.
   */
  public Collection getUsers(String[] userIDs) throws EJBException,RemoteException{
  	try {
  		return getUserHome().findUsers(userIDs);
  	}
  	catch (FinderException fe) {
  		return null;
  	}
  }
  
	public Collection getUsers(IDOQuery query) throws EJBException,RemoteException{
		try {
			return getUserHome().findUsersInQuery(query);
		}
		catch (FinderException fe) {
			return null;
		}
	}
  
	/**
	 * Method updateUsersMainAddressOrCreateIfDoesNotExist. This method can both be used to update the user main address or to create one<br>
	 * if one does not exist. Only userId and StreetName(AndNumber) are required to be not null others are optional.
	 * @param userId
	 * @param streetNameAndNumber
	 * @param postalCodeId
	 * @param countryName
	 * @param city
	 * @param province
	 * @param poBox
	 * @return Address the address that was created or updated
	 * @throws CreateException
	 * @throws RemoteException
	 */
	public Address updateUsersMainAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber, Integer postalCodeId, String countryName, String city, String province, String poBox) throws CreateException,RemoteException {
    AddressType mainAddressType = getAddressHome().getAddressType1();
    return updateUsersAddressOrCreateIfDoesNotExist(userId, streetNameAndNumber, postalCodeId, countryName, city, province, poBox, mainAddressType);
  }
    
  /**
   * Method updateUsersCoAddressOrCreateIfDoesNotExist. This method can both be used to update the user co address or to create one<br>
   * if one does not exist. Only userId and StreetName(AndNumber) are required to be not null others are optional.
   * @param userId
   * @param streetNameAndNumber
   * @param postalCodeId
   * @param countryName
   * @param city
   * @param province
   * @param poBox
   * @return Address the address that was created or updated
   * @throws CreateException
   * @throws RemoteException
   */
  public Address updateUsersCoAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber, Integer postalCodeId, String countryName, String city, String province, String poBox) throws CreateException,RemoteException {
    AddressType mainAddressType = getAddressHome().getAddressType2();
    return updateUsersAddressOrCreateIfDoesNotExist(userId, streetNameAndNumber, postalCodeId, countryName, city, province, poBox, mainAddressType);
  }    
    
  private Address updateUsersAddressOrCreateIfDoesNotExist(Integer userId, String streetNameAndNumber, Integer postalCodeId, String countryName, String city, String province, String poBox, AddressType addressType) throws CreateException,RemoteException {  
     Address address = null;
      if( streetNameAndNumber!=null && userId!=null ){
      	try{
      		AddressBusiness addressBiz = getAddressBusiness();
	        String streetName = addressBiz.getStreetNameFromAddressString(streetNameAndNumber);
	        String streetNumber = addressBiz.getStreetNumberFromAddressString(streetNameAndNumber);
	        
	        User user = getUser(userId);
	        address = getUserAddressByAddressType(userId.intValue(), addressType);
	        
	        Country country = null;
	        
	        if( countryName!=null ){
	        	country = ((CountryHome)getIDOHome(Country.class)).findByCountryName(countryName);
	        }
	        
	        PostalCode code = null;
	        if( postalCodeId!=null){
	        	code = ((PostalCodeHome)getIDOHome(PostalCode.class)).findByPrimaryKey(postalCodeId);
	        }
	                
	        
	        boolean addAddress = false;/**@todo is this necessary?**/
	
	        if( address == null ){
            AddressHome addressHome = addressBiz.getAddressHome();
            address = addressHome.create();
	          address.setAddressType(addressType);
	          addAddress = true;
	        }
	
	        if( country!=null ) address.setCountry(country);
	        if( code!=null ) address.setPostalCode(code);
	        if( province!=null ) address.setProvince(province);
	        if( city!=null ) address.setCity(city);
          if( poBox!=null)  address.setPOBox(poBox);
	        
	        address.setStreetName(streetName);
	        if( streetNumber!=null ) address.setStreetNumber(streetNumber);
	
	        address.store();
	
	        if(addAddress){
	          user.addAddress(address);
	        }
      	}
      	catch(Exception e){
      		e.printStackTrace();
      		System.err.println("Failed to update or create address for userid : "+userId.toString());	
      	}
      	  
      }
        else throw new CreateException("No streetname or userId is null!");
        
        return address;
	}


  public void updateUser(int userId, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group ) throws EJBException,RemoteException {
    User userToUpdate = this.getUser(userId);
    updateUser(userToUpdate, firstname, middlename, lastname, displayname, description, gender, personalID, date_of_birth, primary_group, null);
  }

	public void updateUser(int userId, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group, String fullname ) throws EJBException,RemoteException {
		User userToUpdate = this.getUser(userId);
		updateUser(userToUpdate, firstname, middlename, lastname, displayname, description, gender, personalID, date_of_birth, primary_group, fullname);
	}

  public void updateUser(User userToUpdate, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group ) throws EJBException,RemoteException {
	updateUser(userToUpdate, firstname, middlename, lastname, displayname, description, gender, personalID, date_of_birth, primary_group, null);
  }

  public void updateUser(User userToUpdate, String firstname, String middlename, String lastname, String displayname, String description, Integer gender, String personalID, IWTimestamp date_of_birth, Integer primary_group, String fullname ) throws EJBException,RemoteException {

    if(firstname != null){
      userToUpdate.setFirstName(firstname);
    }
    if(middlename != null){
      userToUpdate.setMiddleName(middlename);
    }
    if(lastname != null){
      userToUpdate.setLastName(lastname);
    }
    if(displayname != null){
      userToUpdate.setDisplayName(displayname);
    }
    if(description != null){
      userToUpdate.setDescription(description);
    }
    if(gender != null){
      userToUpdate.setGender(gender);
    }
    if(date_of_birth != null){
      userToUpdate.setDateOfBirth(date_of_birth.getSQLDate());
    }

    if(primary_group != null){
      userToUpdate.setPrimaryGroupID(primary_group);
    }
    if (personalID != null){
    	userToUpdate.setPersonalID(personalID);
    }
    if (fullname != null) {
    	userToUpdate.setFullName(fullname);
    }

    userToUpdate.store();
  }

  /**
   * Gets all the Emails registered to a User with id iUserId
   * @param iUserId an ID of a User
   * @return Collection of Emails for the User or Null if no emails are found.
   */
  public Collection listOfUserEmails(int iUserId){
    try {
      return this.getEmailHome().findEmailsForUser(iUserId);
    }
    catch (Exception ex) {

    }
    return null;
  }

  public void addNewUserEmail(int iUserId,String sNewEmailAddress){
    try {
      Email eEmail = lookupEmail(sNewEmailAddress);
      if(eEmail==null){
        eEmail = this.getEmailHome().create();
        eEmail.setEmailAddress(sNewEmailAddress);
        eEmail.store();
      }
      User U = getUser(iUserId);
      U.addEmail(eEmail);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public  Email lookupEmail(String EmailAddress){
    try {
      //EntityFinder.debug = true;
      //java.util.Collection c = EntityFinder.getInstance().findAllByColumn(Email.class,com.idega.core.data.EmailBMPBean.getColumnNameAddress(),EmailAddress);
      Email email = getEmailHome().findEmailByAddress(EmailAddress);
      return email;
    }
    catch (Exception ex) {

    }
    return null;
  }

  /**
   * @deprecated use  getUserGroupsDirectlyRelated(int iUserId)
   */
  public  Collection listOfUserGroups(int iUserId){
      return  getUserGroupsDirectlyRelated(iUserId);
  }


  public Collection getUserGroups(int iUserId)throws EJBException{
    try {
      return getUserGroups(this.getUser(iUserId));
      //return getUserGroups(((com.idega.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).findByPrimaryKeyLegacy(iUserId).getGroupID());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

/**
 * Gets all users that are directly children of the group with id iGroupId
 * @return Collection of User objects.
 * @see com.idega.user.business.UserBusiness#getUsersInGroup(Group)
 */
  public Collection getUsersInGroup(int iGroupId) {
    try{
    	//EntityFinder.findRelated(group,com.idega.user.data.UserBMPBean.getStaticInstance());
    	Collection groupList = this.getGroupBusiness().getUsers(iGroupId);
      	return castUserGroupsToUsers(groupList);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

/**
 * Gets all users that are directly children of the group aGroup
 * @return Collection of User objects. * @see com.idega.user.business.UserBusiness#getUsersInGroup(Group) */
  public Collection getUsersInGroup(Group aGroup) {
    try {
    	int groupID = ((Integer)aGroup.getPrimaryKey()).intValue();
    	return getUsersInGroup(groupID);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }


 /**
  * Gets a collection of all users in the system.
  * @return Collection of User entities  * @see com.idega.user.business.UserBusiness#getUsers()  */
  public  Collection getUsers() throws FinderException,RemoteException{
    //Collection l = EntityFinder.findAll(com.idega.user.data.UserBMPBean.getStaticInstance());
    Collection l = this.getUserHome().findAllUsers();
    return l;
  }

	

  /**
   *  Returns User from userid, throws an unchecked EJBException if not found
   * @throws EJBException if nothing found or an error occured
   */
  public  User getUser(int iUserId){
    return getUser(new Integer(iUserId));
  }

  /**
   *  Returns User from userid, throws EJBException if not found
   */
  public  User getUser(Integer iUserId){
    try {
      return getUserHome().findByPrimaryKey(iUserId);
    }
    catch (Exception ex) {
      throw new EJBException("Error getting user for id: "+iUserId.toString()+" Message: "+ex.getMessage());
    }
    //return null;
  }

	/**
	 *  Returns User from personal id, throws EJBException if not found
	 */
	public  User getUser(String personalID) throws EJBException {
		try {
			return getUserHome().findByPersonalID(personalID);
		}
		catch (Exception ex) {
			throw new EJBException("Error getting user for personal id: "+personalID+" Message: "+ex.getMessage());
		}
		//return null;
	}
	

  public  Collection getUsersInNoGroup() throws SQLException  {
    //return EntityFinder.findNonRelated(com.idega.user.data.GroupBMPBean.getStaticInstance(),com.idega.user.data.UserBMPBean.getStaticInstance());
    //Collection nonrelated = EntityFinder.findNonRelated(com.idega.user.data.GroupBMPBean.getStaticInstance(),com.idega.user.data.GroupBMPBean.getStaticInstance());
    //return UserGroupBusiness.getUsersForUserRepresentativeGroups(nonrelated);
    throw new java.lang.UnsupportedOperationException("method getUsersInNoGroup() not implemented");
  }

  public  Collection getUserGroupsDirectlyRelated(int iUserId){
    try {
      return getUserGroupsDirectlyRelated(this.getUser(iUserId));
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getUsersInPrimaryGroup(Group group){
    try {
      //return EntityFinder.findAllByColumn(com.idega.user.data.UserBMPBean.getStaticInstance(),com.idega.user.data.UserBMPBean._COLUMNNAME_PRIMARY_GROUP_ID,group.getID());
      return this.getUserHome().findUsersInPrimaryGroup(group);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

  public  Collection getUserGroupsDirectlyRelated(User user){
    try {
      return getGroupBusiness().getParentGroups(user.getGroupID()); //  EntityFinder.findRelated(user,com.idega.user.data.GroupBMPBean.getStaticInstance());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }


/**
 * Gets all the groups that are indirect parents (grand parents etc.) of the user with id iUserId
 *@param iUserId the ID of the user to get indirect parents for
 *@return Collection of Group entities that are not direct parents of the specified user
 * */
	public  Collection getParentGroupsInDirectForUser(int iUserId){
  //public  Collection getUserGroupsNotDirectlyRelated(int iUserId){
    try {
      User user = this.getUser(iUserId);
      /*Collection isDirectlyRelated = getUserGroupsDirectlyRelated(user);
      Collection AllRelatedGroups = getUserGroups(user);

      if(AllRelatedGroups != null){
        if(isDirectlyRelated != null){
          Iterator iter = isDirectlyRelated.iterator();
          while (iter.hasNext()) {
            Object item = iter.next();
            AllRelatedGroups.remove(item);
            //while(AllRelatedGroups.remove(item)){}
          }
        }
        return AllRelatedGroups;
      }else {
        return null;
      }
      */
      return this.getGroupBusiness().getParentGroupsInDirect(user.getGroupID());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }

/**
 * Returns all the groups that are not a direct parent of the User with id iUserId. That is both groups that are indirect parents of the user or not at all parents of the user.
 * @see com.idega.user.business.GroupBusiness#getAllGroupsNotDirectlyRelated(int)
 * @return Collection of non direct parent groups
 */
  public Collection getNonParentGroups(int iUserId){
    try {

      User user = getUser(iUserId);
      /*Collection isDirectlyRelated = getUserGroupsDirectlyRelated(user);
      Collection AllGroups = UserGroupBusiness.getAllGroups(); //EntityFinder.findAll(com.idega.user.data.GroupBMPBean.getStaticInstance());

      if(AllGroups != null){
        if(isDirectlyRelated != null){
          Iterator iter = isDirectlyRelated.iterator();
          while (iter.hasNext()) {
            Object item = iter.next();
            AllGroups.remove(item);
            //while(AllGroups.remove(item)){}
          }
        }
        return AllGroups;
      }else{
        return null;
      }
      */
      return getGroupBusiness().getNonParentGroups(user.getGroupID());
    }
    catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }


/**
 * Gets all the groups that the user is in recursively up the group tree with all availble group types.
 * @param aUser a User to find parent Groups for
 * @return Collection of Groups found recursively up the tree
 * @throws EJBException If an error occured
 */
  public  Collection getUserGroups(User aUser) throws EJBException{
    //String[] groupTypesToReturn = new String[2];
    //groupTypesToReturn[0] = com.idega.user.data.GroupBMPBean.getStaticInstance().getGroupTypeValue();
    //groupTypesToReturn[1] = com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getStaticPermissionGroupInstance().getGroupTypeValue();
    return getUserGroups(aUser,null,false);
  }


/**
 * Gets all the groups that the user is in recursively up the group tree filtered with specified groupTypes
 * @param aUser a User to find parent Groups for
 * @param groupTypes the Groups a String array of group types of which the Groups to be returned must be
= * @return Collection of Groups found recursively up the tree
 * @throws EJBException If an error occured
 */
  public Collection getUserGroups(User aUser, String[] groupTypes) throws EJBException{
    return getUserGroups(aUser,groupTypes, false);
  }


/**
 * Returns recursively up the group tree parents of User aUser with filtered out with specified groupTypes
 * @param aUser a User to find parent Groups for
 * @param groupTypes the Groups a String array of group types to be filtered with
 * @param returnSpecifiedGroupTypes if true it returns the Collection with all the groups that are of the types specified in  groupTypes[], else it returns the opposite (all the groups that are not of any of the types specified by groupTypes[])
 * @return Collection of Groups found recursively up the tree
 * @throws EJBException If an error occured
 */
  public Collection getUserGroups(User aUser, String[] groupTypes, boolean returnSepcifiedGroupTypes) throws EJBException{
    try {
		return getGroupBusiness().getParentGroupsRecursive(aUser.getGroup(),groupTypes, returnSepcifiedGroupTypes);
	} catch (RemoteException e) {
		throw new IBORuntimeException(e,this);
	}
  }


  public GroupBusiness getGroupBusiness()throws RemoteException{
    return (GroupBusiness) IBOLookup.getServiceInstance(this.getIWApplicationContext(),GroupBusiness.class);
  }

  public Collection getAllUsersOrderedByFirstName()throws FinderException,RemoteException{
    return this.getUserHome().findAllUsersOrderedByFirstName(); 
  }
  
  
  
	public Email getUsersMainEmail(User user)throws NoEmailFoundException{
		String userString = null;
		try{
			userString = user.getName();
			Collection collection = user.getEmails();
			for (Iterator iterator = collection.iterator(); iterator.hasNext();)
			{
				Email element = (Email) iterator.next();
				return element;
			}
		}
		catch(Exception e){
		}
		throw new NoEmailFoundException(userString);
	}

	public Phone getUsersHomePhone(User user)throws NoPhoneFoundException{
		String userString = null;
		try{
			userString = user.getName();
			return getPhoneHome().findUsersHomePhone(user);
		}
		catch(Exception e){
		}
		throw new NoPhoneFoundException(userString);
	}

	public Phone getUsersWorkPhone(User user)throws NoPhoneFoundException{
		String userString = null;
		try{
			userString = user.getName();
			return getPhoneHome().findUsersWorkPhone(user);
		}
		catch(Exception e){
		}
		throw new NoPhoneFoundException(userString);
	}

	public Phone getUsersMobilePhone(User user)throws NoPhoneFoundException{
		String userString = null;
		try{
			userString = user.getName();
			return getPhoneHome().findUsersMobilePhone(user);
		}
		catch(Exception e){
		}
		throw new NoPhoneFoundException(userString);
	}

	public Phone getUsersFaxPhone(User user)throws NoPhoneFoundException{
		String userString = null;
		try{
			userString = user.getName();
			return getPhoneHome().findUsersFaxPhone(user);
		}
		catch(Exception e){
		}
		throw new NoPhoneFoundException(userString);
	}


/**
 * @return Correct name of the group or user or empty string if there was an error getting the name.
 * Gets the name of the group and explicitly checks if the "groupOrUser" and if it is a user it 
 * returns the correct name of the user. Else it regularely returns the name of the group.
 **/
  public String getNameOfGroupOrUser(Group groupOrUser){
  	try{
  		String userGroupType=getUserHome().getGroupType();
  		if(groupOrUser.getGroupType().equals(userGroupType)){
  			int userID = ((Integer)groupOrUser.getPrimaryKey()).intValue();
  			return getUser(userID).getName();
  		}
  		else{
  			return groupOrUser.getName();	
  		}
  	}
  	catch(Exception e){
  		return "";
  	}
  }
  
  public UserProperties getUserProperties(User user) throws RemoteException {
  	return getUserProperties(((Integer)user.getPrimaryKey()).intValue());	
  }
  
  public UserProperties getUserProperties(int userID) {
  	UserProperties properties = new UserProperties(getIWApplicationContext().getApplication(),userID);
  	return properties;
  }

	/**
	 * @return the id of the homepage for the user if it is set, else -1
	 * Finds the homepage set for the user, 
	 * if none is set it checks on the homepage set for the users primary group, 
	 * else it returns -1
	 **/
	public int getHomePageIDForUser(User user){
		try{
			int homeID = user.getHomePageID();
			if(homeID==-1){
				homeID = user.getPrimaryGroup().getHomePageID();
				return homeID;
			}
			else{
				return homeID;	
			}
		}
		catch(Exception e){
			e.printStackTrace();	
		}
		return -1;
	}

	/**
	 * @return the id of the homepage for the user if it is set, else it throws a javax.ejb.FinderException
	 * Finds the homepage set for the user, 
	 * if none is set it checks on the homepage set for the users primary group, 
	 * else it throws a javax.ejb.FinderException
	 **/
	public com.idega.builder.data.IBPage getHomePageForUser(User user)throws javax.ejb.FinderException{
		try{
			int homeID = getHomePageIDForUser(user);
			if(homeID!=-1){
				return getIBPageHome().findByPrimaryKey(homeID);
			}
			else{
				throw new javax.ejb.FinderException("No homepage found for user");
			}
		}
		catch(Exception e){
				throw new javax.ejb.FinderException("Error finding homepage for user. Error was:"+e.getClass().getName()+" with message: "+e.getMessage());
		}
	}
	
	protected IBPageHome getIBPageHome()throws java.rmi.RemoteException{
		return (IBPageHome)com.idega.data.IDOLookup.getHome(IBPage.class);	
	}
	
	public AddressBusiness getAddressBusiness() throws RemoteException{
		return (AddressBusiness) getServiceInstance(AddressBusiness.class);
	}



	/**
	 * Cast a Group that is a "UserReresentative" Group to a User instance.
	 * @param userGroups An instance of a Group that is really a "UserReresentative" group i.e. the Group representation of the User	 * @param userGroup A instnance of a Group that is really a "UserReresentative" group i.e. the Group representation of the User	 * @return User
	 * @throws EJBException If an error occurs casting	 */
	public User castUserGroupToUser(Group userGroup)throws EJBException{
		try{
			if(userGroup instanceof User){
				return (User)userGroup;	
			}
			else{
				//try{
					return this.getUserHome().findUserForUserGroup(userGroup);
				//}
				//catch(FinderException e){
					//if(userGroup.isUser()){
					//	return this.getUserHome().findByPrimaryKey(userGroup.getPrimaryKey());
					//}
				//}
			}
		}
		catch(Exception e){
			throw new IBORuntimeException(e);	
		}
		//throw new IBORuntimeException("Error find user for group "+userGroup.toString());
	}
	

	/**
	 * Cast a Collection of Groups that is a "UserReresentative" Group to a User instance.
	 * @param userGroupCollection A Collection with instnances of a Group that are really a "UserReresentative" groups i.e. the Group representation of the User
	 * @return Collection of user instances representing the Groups
	 * @throws EJBException If an error occurs casting
	 */
	public Collection castUserGroupsToUsers(Collection userGroupCollection)throws EJBException{
		/**
		 *@todo: Possible backwards compatability bug here.
		 * Look into this (If no user_representative field set for the User)
		 **/
		try{
			boolean mayReturnWholeCollection=true;
			for (Iterator iter = userGroupCollection.iterator(); iter.hasNext();) {
				Group userGroup = (Group) iter.next();
				if(userGroup instanceof User){
					//nothing
				}
				else{
					mayReturnWholeCollection=false;
					break;	
				}
			}
			
			if(mayReturnWholeCollection){
				return userGroupCollection;
			}
			else{
				return this.getUserHome().findUsersForUserRepresentativeGroups(userGroupCollection);
			}
		}
		catch(Exception e){
			throw new IBORuntimeException(e,this);
		}
	}
	
	public boolean hasUserLogin(User user)throws RemoteException{
		LoginTable lt = LoginDBHandler.getUserLogin(((Integer)user.getPrimaryKey()).intValue());
		return lt!=null;
	}
	
	public boolean hasUserLogin(int userID)throws RemoteException{
		LoginTable lt = LoginDBHandler.getUserLogin(userID);
		return lt!=null;
	}
	
	/**
	 * Returns a collection of Groups that are this users top nodes. The nodes that he has either view or owner permissions to<br>
	 * To end up with only the top nodes we do the following:<br>
	 * For each group (key) in the parents Map we check if that group is contained within any of<br>
	 * the other groups' parents. If another group has this group as a parent it is removed and its parent list<br>
	 * and we move on to the next key. This way the map we iterate through will always get smaller until only the<br>
	 * top node groups are left.
	 * @param user
	 * @return
	 * @throws RemoteException
	 */
	public Collection getUsersTopGroupNodesByViewAndOwnerPermissions(User user, IWUserContext iwuc)throws RemoteException{
		Collection topNodes = new Vector();
		if( user != null ){
			
			topNodes = (Collection)iwuc.getSessionAttribute(SESSION_KEY_TOP_NODES+user.getPrimaryKey().toString());
			
			if( topNodes != null ) return topNodes;
			else{

				try {
					
					boolean superUser = iwuc.isSuperAdmin();
					if(superUser){
						try {
							topNodes = ((IWContext) iwuc).getDomain().getTopLevelGroupsUnderDomain();
						}
						catch (Exception e1) {
							topNodes = new Vector();
							e1.printStackTrace();
						}
					}
					else{
						System.out.println("TOP NODES fetch starts : "+ IWTimestamp.RightNow().toString());
						Map parents = new HashMap();
						Map groupMap = new HashMap();
						IDOUtil idoUtil = IDOUtil.getInstance();
						GroupBusiness groupBiz = getGroupBusiness();
						
						Collection directlyRelatedParents = getGroupBusiness().getParentGroups(user);
						Collection ownedPermissions = null;
						Collection allViewAndOwnerPermissions = new Vector();
						
						//get all view permissions for direct parent and put in a list
						Collection viewPermissions = AccessControl.getAllGroupViewPermissions(directlyRelatedParents);				
						allViewAndOwnerPermissions.addAll(viewPermissions);
											
						ownedPermissions = AccessControl.getAllGroupPermissionsOwnedByGroup( user );
						//allViewAndOwnerPermissions.removeAll(ownedPermissions);//no double entries thank you
						allViewAndOwnerPermissions.addAll(ownedPermissions);
						
						System.out.println("TOP NODES done fetching all permissions: "+ IWTimestamp.RightNow().toString());
						
						//get all (recursively) parents for permission
						Iterator permissions = allViewAndOwnerPermissions.iterator();
						while (permissions.hasNext()) {
							ICPermission perm = (ICPermission) permissions.next();
							try {
								String groupId = perm.getContextValue();
								
								Integer primaryKey = new Integer(groupId);
								
								if( !groupMap.containsKey(primaryKey) ){
									Group permissionGroup = groupBiz.getGroupByGroupID(primaryKey.intValue());
									Collection recParents = groupBiz.getParentGroupsRecursive(permissionGroup);
									Map parentMap = idoUtil.convertIDOEntityCollectionToMapOfPrimaryKeysAndEntityValues(recParents);
									parents.put(primaryKey,parentMap);
									groupMap.put(primaryKey,permissionGroup);
								}
						
							}
							catch (NumberFormatException e1) {
								e1.printStackTrace();
							}
							catch (FinderException e1) {
								System.out.println("UserBusiness: In getUsersTopGroupNodesByViewAndOwnerPermissions. group not found or passive?! "+perm.getContextValue());
							}
						}
						
						System.out.println("TOP NODES fetch ends and sort start : "+ IWTimestamp.RightNow().toString());
						//Filter out the real top nodes!
						Map skipThese = new HashMap();
						Set keys = parents.keySet();
						Iterator iter = keys.iterator();
						while (iter.hasNext()) {
							Integer myGroup = (Integer) iter.next();
						
							Iterator iter2 = parents.keySet().iterator();
							while (iter2.hasNext()) {
								Integer myGroup2 = (Integer) iter2.next();
								if(myGroup.equals(myGroup2) || skipThese.containsKey(myGroup)) continue;//dont check for self
								
								Map theParents = (Map) (parents.get(myGroup2));
								
								if(theParents!=null && theParents.containsKey(myGroup)){
									skipThese.put(myGroup2,null); 
									groupMap.remove(myGroup2);
									
								}//remove if this group is a child group of myGroup
									
							}//inner while ends
							
						}//outer while ends
						
						topNodes = groupMap.values();
						System.out.println("TOP NODES sort ends : "+ IWTimestamp.RightNow().toString());
					}
						
				}
				catch (EJBException e) {
					e.printStackTrace();
				}
			
			
			}
			
			iwuc.setSessionAttribute(SESSION_KEY_TOP_NODES+user.getPrimaryKey().toString(),topNodes);
		}
		
		return 	topNodes;
	
	}
  

  /**
   * Returns a collection of Groups. The groups that he has edit permissions to<br>
   * @param user
   * @return
   * @throws RemoteException
   */
  public Collection getAllGroupsWithEditPermission(User user, IWUserContext iwuc) {
    
    Collection resultGroups = new ArrayList();
    GroupBusiness groupBiz = null;
    try {
      groupBiz = getGroupBusiness();
    }
    catch (RemoteException ex)  {
      throw new RuntimeException(ex.getMessage());
    }
    
    Collection permissions = AccessControl.getAllGroupPermissionsOwnedByGroup(user);
    List parentGroupsList = user.getParentGroups();
    
      Collection editPermissions = AccessControl.getAllGroupEditPermissions(parentGroupsList);
     // permissions.removeAll(editPermissions); // avoid double entries
      permissions.addAll(editPermissions);

    Iterator iterator = permissions.iterator();
    while (iterator.hasNext()) {
      ICPermission perm = (ICPermission) iterator.next();
      try {
        String groupId = perm.getContextValue();
        Group group = groupBiz.getGroupByGroupID(Integer.parseInt(groupId));
        resultGroups.add(group);
      }
      catch (NumberFormatException e1) {
        e1.printStackTrace();
      }
      catch (FinderException e1) {
        System.out.println("UserBusiness: In getAllGroupsWithEditPermission. group not found"+perm.getContextValue());
      }
      catch (RemoteException ex)  {
        throw new RuntimeException(ex.getMessage());
      }
    }
    return resultGroups;
  }
  
	/**
	 * Returns a collection of Groups. The groups that he has view permissions to<br>
	 * @param user
	 * @return
	 * @throws RemoteException
	 */
	public Collection getAllGroupsWithViewPermission(User user, IWUserContext iwuc) {
    
		Collection resultGroups = new ArrayList();
		//Group userGroup = null;
		GroupBusiness groupBiz = null;
		try {
			groupBiz = getGroupBusiness();
		}
		catch (RemoteException ex)  {
			throw new RuntimeException(ex.getMessage());
		}
		
		Collection permissions = AccessControl.getAllGroupPermissionsOwnedByGroup(user);
		List parentGroupsList = user.getParentGroups();
    
			Collection viewPermissions = AccessControl.getAllGroupViewPermissions(parentGroupsList);
		 // permissions.removeAll(editPermissions); // avoid double entries
			permissions.addAll(viewPermissions);

		Iterator iterator = permissions.iterator();
		while (iterator.hasNext()) {
			ICPermission perm = (ICPermission) iterator.next();
			try {
				String groupId = perm.getContextValue();
				Group group = groupBiz.getGroupByGroupID(Integer.parseInt(groupId));
				resultGroups.add(group);
			}
			catch (NumberFormatException e1) {
				e1.printStackTrace();
			}
			catch (FinderException e1) {
				System.out.println("UserBusiness: In getAllGroupsWithEditPermission. group not found"+perm.getContextValue());
			}
			catch (RemoteException ex)  {
				throw new RuntimeException(ex.getMessage());
			}
		}
		return resultGroups;
	}

  public Map moveUsers(Collection userIds, Group parentGroup, int targetGroupId, User currentUser) {
    IWMainApplication application = getIWApplicationContext().getApplication();
    IWBundle bundle = application.getBundle("com.idega.user");
    Locale locale = application.getSettings().getDefaultLocale();
    IWResourceBundle iwrb = bundle.getResourceBundle(locale);
    Map result = new HashMap();

    // check if the source and the target are the same
    
    if (parentGroup != null) { 
      int parentGroupId = ((Integer) parentGroup.getPrimaryKey()).intValue();
      // target and source are the same do nothing
      if (parentGroupId == targetGroupId) {
        String message = iwrb.getLocalizedString("user_source_and_target_are_the_same", "Source group and target group are the same");
        // fill the result map
        Iterator iterator = userIds.iterator();
        while (iterator.hasNext())  {
          String userIdAsString = (String) iterator.next();
          Integer userId = new Integer(userIdAsString);
          result.put(userId, message);
        }
        return result;
      }
    }  
   
   GroupBusiness groupBiz = null;
   Group targetGroup = null;
   try {
     groupBiz = getGroupBusiness();
     targetGroup = groupBiz.getGroupByGroupID(targetGroupId);
   }
   catch (FinderException ex)  {
     throw new EJBException("Error getting group for id: "+ targetGroupId +" Message: "+ex.getMessage());
   }
   catch (RemoteException ex)  {
     throw new RuntimeException(ex.getMessage());
   }
   
   String userIsAlreadyAMemberOfTheGroupMessage = iwrb.getLocalizedString("user_already_member_of_the_target_group", "The user is already a member of the target group"); 
 
   // finally perform moving 
   
   Iterator iterator = userIds.iterator();
    while (iterator.hasNext()) {
      String message;
      String userIdAsString = (String) iterator.next();
      Integer userId = new Integer(userIdAsString); 
      User user = getUser(userId);
      // first check
      if (isMemberOfGroup(targetGroupId, user)) {
        message = userIsAlreadyAMemberOfTheGroupMessage;
      }
      // second check
      else {
        message = isUserSuitedForGroup(user, targetGroup);
      }
      // if there is not any problem the message is null
      if (message == null)  {
        message = moveUserWithoutTest(user, parentGroup, targetGroup, currentUser);
      }
      // if the user was sucessfully moved the message is null
      result.put(userId, message);
    }
    return result;
  }

  public Map moveUsers(Collection groupIds, String parentGroupType, User currentUser) {
    IWMainApplication application = getIWApplicationContext().getApplication();
    IWBundle bundle = application.getBundle("com.idega.user");
    Locale locale = application.getSettings().getDefaultLocale();
    IWResourceBundle iwrb = bundle.getResourceBundle(locale);
    String noSuitableGroupMessage = iwrb.getLocalizedString("user_suitable_group_could_not_be_found", "A suitable group for the user could not be found.");
   
    // key groups id, value group
    Map groupIdGroup = new HashMap();
    // key group id, value users
    Map groupIdUsers = new HashMap();
    // key group id, value id of users
    Map groupIdUsersId = new HashMap();
    
    // key user id, value user's parent group 
    Map userParentGroup = new HashMap();
    // key user id, value user's target group
    Map userTargetGroup = new HashMap();

    // get all groups 
    try {
      GroupBusiness groupBiz = null;
      groupBiz = getGroupBusiness();
      Iterator groupIdsIterator = groupIds.iterator();
      while (groupIdsIterator.hasNext())  {
        String groupId = (String) groupIdsIterator.next();
        int id = Integer.parseInt(groupId);
        Group group = groupBiz.getGroupByGroupID(id);
        // check if the group id has the specified type 
        // if the type equals to the specified type iterate over the children
        if (parentGroupType != null && (parentGroupType.equals(group.getGroupType())))  {
          //Iterator childIterator = groupBiz.getChildGroups(group).iterator();
          Iterator childIterator = group.getChildren();
          while (childIterator.hasNext()) {
            Group childGroup = (Group) childIterator.next();
            String childGroupId = childGroup.getPrimaryKey().toString();
            fillMaps(childGroup, childGroupId, groupIdGroup, groupIdUsers, groupIdUsersId);
          }
        }
        else {
          fillMaps(group, groupId, groupIdGroup, groupIdUsers, groupIdUsersId);
        }
      }
    }
    // Finder and RemoteException
    catch (Exception ex)  {
      throw new EJBException("Error getting group. Message: "+ex.getMessage());
    }  
    // iterate over all users
    Iterator groupIdsIterator = groupIdGroup.entrySet().iterator();
    // iterate over groups
    while (groupIdsIterator.hasNext())  {
      Map.Entry entry = (Map.Entry) groupIdsIterator.next();
      String parentGroupId = (String) entry.getKey();
      Group parentGroup = (Group) entry.getValue(); 
      Collection userInGroup = (Collection) groupIdUsers.get(parentGroupId);
      Iterator userIterator = userInGroup.iterator();
      // iterate over users within a group
      while (userIterator.hasNext())  {
        Group possibleTarget= null;
        boolean possibleTargetAlreadySet = false;
        User user = (User) userIterator.next();
        // test if the user is assignable to one and only one group
        Iterator targetGroupIds = groupIdGroup.entrySet().iterator();
        while (targetGroupIds.hasNext())  {
          Map.Entry targetEntry = (Map.Entry) targetGroupIds.next();
          String targetGroupId = (String) targetEntry.getKey();
          Group targetGroup = (Group) targetEntry.getValue();
          boolean result = true;
          // skip the own group
          if (! targetGroupId.equals(parentGroupId)) {
            // check if the user is already a member of the target group
            Collection userIdsOfTarget = (Collection) groupIdUsersId.get(targetGroupId);
            result = ! userIdsOfTarget.contains(user.getPrimaryKey().toString());
          }
          // check if the user is suited for the target group when the result is still true
          if (result) {
            result = (isUserSuitedForGroup(user, targetGroup) == null);
          }
          if (result) {
            if (! possibleTargetAlreadySet) {
              possibleTarget = targetGroup;
              possibleTargetAlreadySet = true;
            }
            else {
              possibleTarget = null;
            }
          }
        }
        userParentGroup.put(user, parentGroup);
        userTargetGroup.put(user, possibleTarget); 
      }
    }
    
    // perform moving
    Map result = new HashMap();
    Iterator userIterator = userTargetGroup.entrySet().iterator();
    while (userIterator.hasNext())  {
      Map.Entry entry = (Map.Entry) userIterator.next();
      User user = (User) entry.getKey();
      Group target = (Group) entry.getValue();
      Group source = (Group) userParentGroup.get(user);
      Integer sourceId = (Integer) source.getPrimaryKey();
      Map map = (Map) result.get(sourceId);
      if (map == null)  {
        map = new HashMap();
        result.put(sourceId, map);
      } 
      if (target != null) {  
        int source_id = ((Integer) source.getPrimaryKey()).intValue();  
        int target_id = ((Integer) target.getPrimaryKey()).intValue();
        if (source_id != target_id) {
          String message = moveUserWithoutTest(user, source, target, currentUser);
          // if there is not a transaction error the message is null!
          map.put(user.getPrimaryKey(), message);
        }
        else {
          map.put(user.getPrimaryKey(), null);
        }
      }
      else {
        map.put(user.getPrimaryKey(), noSuitableGroupMessage);
      }
    }
    return result;
  }

  private void fillMaps(Group group, String groupId,  Map groupIdGroup, Map groupIdUsers, Map groupIdUsersId) {
    groupIdGroup.put(groupId, group);
    Collection usersInGroup = getUsersInGroup(group);
    groupIdUsers.put(groupId, usersInGroup);
    Collection userIds = new ArrayList();
    Iterator iterator = usersInGroup.iterator();
    while (iterator.hasNext())  {
      User user = (User) iterator.next();
      userIds.add(user.getPrimaryKey().toString());
    }
    groupIdUsersId.put(groupId, userIds);
  }
  
  private boolean isMemberOfGroup(int parentGroupToTest, User user)  {
    // first check the primary group
    Group group = user.getPrimaryGroup();
    if (group != null)  {
      int primaryGroupId = ((Integer) group.getPrimaryKey()).intValue();
      if (parentGroupToTest == primaryGroupId)  {
        return true;
      }
    }
    // then check the group relations
    int userId = ((Integer) user.getPrimaryKey()).intValue();
    Collection coll;
    try{
      GroupRelationHome groupRelationHome = (GroupRelationHome)IDOLookup.getHome(GroupRelation.class);
      GroupHome groupHome = (GroupHome) IDOLookup.getHome(Group.class);
      String parentRelation = groupHome.getRelationTypeGroupParent();
      coll = groupRelationHome.findGroupsRelationshipsContainingUniDirectional(parentGroupToTest, userId, parentRelation);
    }
    // Remote and FinderException
    catch(Exception rme)  {
       throw new RuntimeException(rme.getMessage());
    }
    return ! coll.isEmpty();
  }
    
    
  private String moveUserWithoutTest(User user, Group parentGroup, Group targetGroup, User currentUser) {
    int userId = ((Integer) user.getPrimaryKey()).intValue();
    int targetGroupId = ((Integer) targetGroup.getPrimaryKey()).intValue();
    int parentGroupId = -1;
    if (parentGroup != null)  {
      parentGroupId = ((Integer) parentGroup.getPrimaryKey()).intValue();  
      if (parentGroupId == targetGroupId) {
        // there was a previous test therefore localization is it not necessary
        return "source and target are the same";
      }
    }
    int primaryGroupId = user.getPrimaryGroupID();
    // Transaction starts
    javax.transaction.TransactionManager transactionManager = com.idega.transaction.IdegaTransactionManager.getInstance();
    try {
      transactionManager.begin();
      // check if the primaryGroup is the parentGroup
      boolean targetIsSetAsPrimaryGroup = false;
      if (parentGroup != null && (targetIsSetAsPrimaryGroup = (parentGroupId == primaryGroupId)))  {
        user.setPrimaryGroup(targetGroup);
        user.store();
      }
      // remove user from parent group
      // IMPORTANT
      // if the parent group was the primary group there might(!) be 
      // a corresponding GroupRelation.
      // usually there should not be such a GroupRelation
      // therefore be sure that the method below does not throw an error if it 
      // is not able to find a group relation.
      if (parentGroup != null) {
        parentGroup.removeUser(user,currentUser);
      }
      // set target group
      if (! targetIsSetAsPrimaryGroup)  {
        targetGroup.addGroup(userId);
      }
      else  {
      /** TODO solve group relation primary group problem
       */
      // this is a hack. If the target group is already the primary group
      // it should not be necessary to add a corresponding group relation.
      // but if it is not added the group tree does not know that this user is a child 
      // of the target group
        targetGroup.addGroup(userId);
      }
      transactionManager.commit();
    }
    catch (Exception e) {
      e.printStackTrace(System.err);
      try {
        transactionManager.rollback();  
      }
      catch (javax.transaction.SystemException sy) {
        sy.printStackTrace(System.err);
      }
      // this is an unusual error therefore localization is it not necessary 
      return "Transaction failed";
    }
    return null;
  }



  private String isUserSuitedForGroup(User user, Group targetGroup) {
    try {
      Collection plugins = getGroupBusiness().getUserGroupPluginsForGroupTypeString(targetGroup.getGroupType());
      Iterator iter = plugins.iterator();
      while (iter.hasNext()) {
        UserGroupPlugIn element = (UserGroupPlugIn) iter.next();
        UserGroupPlugInBusiness pluginBiz = (UserGroupPlugInBusiness) com.idega.business.IBOLookup.getServiceInstance(getIWApplicationContext(), Class.forName(element.getBusinessICObject().getClassName()));
        String message;
        if ((message = pluginBiz.isUserSuitedForGroup(user, targetGroup)) != null) {  
          return message;
        }    
      }
    }
    catch (Exception ex)  {
      throw new RuntimeException(ex.getMessage());
    }
    return null;
  }    
   

} // Class UserBusiness
