package com.idega.core.accesscontrol.data;



/*

*Copyright 2000 idega.is All Rights Reserved.

*/



import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

import javax.ejb.FinderException;

import com.idega.core.user.data.User;
import com.idega.data.IDOQuery;



public class LoginTableBMPBean extends com.idega.data.GenericEntity implements com.idega.core.accesscontrol.data.LoginTable,com.idega.util.EncryptionType {



        public static String className = LoginTable.class.getName();
        public static String _COLUMN_PASSWORD = "usr_password";

        private transient String unEncryptedUserPassword;

	public LoginTableBMPBean(){
		super();
	}


	public LoginTableBMPBean(int id)throws SQLException{
		super(id);
	}



	public void initializeAttributes(){
          addAttribute(this.getIDColumnName());
          addAttribute(getColumnNameUserID(),"Notandi",true,true,Integer.class,"many-to-one",User.class);
          addAttribute(getUserLoginColumnName(),"Notandanafn",true,true,String.class,32);
          addAttribute(getNewUserPasswordColumnName(),"Lykilor�",true,true,String.class,255);
          //deprecated column
          addAttribute(getOldUserPasswordColumnName(),"Lykilor�",true,true,String.class,20);
          addAttribute(getLastChangedColumnName(),"S��ast breytt",true,true,Timestamp.class);
          addAttribute(getLoginTypeColumnName(),"Tegund a�gagns",true,true,String.class,32);
          setNullable(getUserLoginColumnName(), false);
          setUnique(getUserLoginColumnName(),true);
	}



        public void setDefaultValues(){
          setColumn(getOldUserPasswordColumnName(),"rugl");
        }



	public String getEntityName(){
		return "ic_login";
	}



        public static String getUserLoginColumnName(){

          return "user_login";

        }



        public static String getOldUserPasswordColumnName(){

          return "user_password";

        }



        public static String getNewUserPasswordColumnName(){

          return _COLUMN_PASSWORD;

        }



        public static String getLastChangedColumnName() {

          return("last_changed");

        }
        
			public static String getLoginTypeColumnName() {
	
			  return("login_type");
	
			}



        public static String getUserPasswordColumnName(){

          System.out.println("LoginTable - getUserPassordColumnName()");

          System.out.println("caution: not save because of changes in entity");

          Exception e = new Exception();

          e.printStackTrace();

          return _COLUMN_PASSWORD;

        }



        public static String getColumnNameUserID(){

          return com.idega.core.user.data.UserBMPBean.getColumnNameUserID();

        }



/*        public void insertStartData() throws SQLException {

          LoginTable login = ((com.idega.core.accesscontrol.data.LoginTableHome)com.idega.data.IDOLookup.getHomeLegacy(LoginTable.class)).createLegacy();

          LoginInfo li = ((com.idega.core.accesscontrol.data.LoginInfoHome)com.idega.data.IDOLookup.getHomeLegacy(LoginInfo.class)).createLegacy();

          List user = EntityFinder.findAllByColumn(com.idega.core.user.data.UserBMPBean.getStaticInstance(), com.idega.core.user.data.UserBMPBean.getColumnNameFirstName(),com.idega.core.user.data.UserBMPBean.getAdminDefaultName());

          User adminUser = null;

          if(user != null){

            adminUser = ((User)user.get(0));

          }else{

            adminUser = ((com.idega.core.user.data.UserHome)com.idega.data.IDOLookup.getHomeLegacy(User.class)).createLegacy();

            adminUser.setFirstName(com.idega.core.user.data.UserBMPBean.getAdminDefaultName());

            adminUser.insert();

          }



          try {

          /*

            login.setUserId(adminUser.getID());

            login.setUserLogin(com.idega.core.user.data.UserBMPBean.getAdminDefaultName());

            login.setUserPassword("idega");

            login.insert();

            li.setID(login.getID());

            li.insert();

            */

/*            LoginDBHandler.createLogin(adminUser.getID(), com.idega.core.user.data.UserBMPBean.getAdminDefaultName(), "idega", Boolean.TRUE, null, -1, Boolean.FALSE, Boolean.TRUE, Boolean.TRUE, EncryptionType.MD5);

          }

          catch (Exception ex) {

            System.err.println(ex.getMessage());

            ex.printStackTrace();

            throw new SQLException("Login Not created");

          }



          AccessControl control = new AccessControl();

          GenericGroup group = com.idega.core.accesscontrol.data.PermissionGroupBMPBean.getStaticPermissionGroupInstance().findGroup(AccessControl.getAdministratorGroupName());

          if(group != null){

            control.addUserToPermissionGroup((PermissionGroup)group,adminUser.getID());

          }else{

            int[] userId = new int[1];

            userId[0] = adminUser.getID();

            control.createPermissionGroup(AccessControl.getAdministratorGroupName(),null,null,userId,null);

          }

        }

*/

        public static LoginTable getStaticInstance(){

          return (LoginTable)com.idega.core.accesscontrol.data.LoginTableBMPBean.getStaticInstance(LoginTable.class);

        }







	public String getUserPassword(){

          String str = null;

          try {

            str = getStringColumnValue(getNewUserPasswordColumnName());

          }

          catch (Exception ex) {

            ex.printStackTrace();

            // str = null;

          }





          if(str == null){

            try {

              String oldPass = getStringColumnValue(getOldUserPasswordColumnName());

              if(oldPass != null){



                char[] pass = new char[oldPass.length()/2];



                try {



                  for (int i = 0; i < pass.length; i++) {

                    pass[i] = (char)Integer.decode("0x"+oldPass.charAt(i*2)+oldPass.charAt((i*2)+1)).intValue();

                  }



                  oldPass = String.valueOf(pass);

                }

                catch (Exception ex) {

                  ex.printStackTrace();

                  // oldPass = oldPass;

                }



                LoginTable table = ((com.idega.core.accesscontrol.data.LoginTableHome)com.idega.data.IDOLookup.getHomeLegacy(LoginTable.class)).findByPrimaryKeyLegacy(this.getID());

                table.setUserPassword(oldPass);

                table.update();

                this.setUserPassword(oldPass);

                return oldPass;

                //this.setColumnAsNull(getOldUserPasswordColumnName());

              }

            }

            catch (Exception ex) {

              ex.printStackTrace();

              return getStringColumnValue(getOldUserPasswordColumnName());

            }

          }

          if(str != null){

            char[] pass = new char[str.length()/2];



            try {



              for (int i = 0; i < pass.length; i++) {

                pass[i] = (char)Integer.decode("0x"+str.charAt(i*2)+str.charAt((i*2)+1)).intValue();

              }



              return String.valueOf(pass);

            }

            catch (Exception ex) {

              ex.printStackTrace();

              return str;

            }

          }



          return str;



	}





	public void setUserPassword(String userPassword){
          try {
            String str = "";
            char[] pass = userPassword.toCharArray();
            for (int i = 0; i < pass.length; i++) {
              String hex = Integer.toHexString((int)pass[i]);
              while (hex.length() < 2) {
                String s = "0";
                s += hex;
                hex = s;
              }
              str += hex;
            }
            if(str.equals("") && !userPassword.equals("")){
              str = null;
            }
            setColumn(getNewUserPasswordColumnName(), str);
          }
          catch (Exception ex) {
            ex.printStackTrace();
            setColumn(getNewUserPasswordColumnName(), userPassword);
          }
	}



	public void setUserLogin(String userLogin) {
          setColumn(getUserLoginColumnName(), userLogin);
	}

	public String getUserLogin() {
          return getStringColumnValue(getUserLoginColumnName());
	}

	public int getUserId(){
          return getIntColumnValue(getUserIDColumnName());
	}

	public void setUserId(Integer userId){
          setColumn(getUserIDColumnName(), userId);
	}

	public void setUserId(int userId) {
          setColumn(getUserIDColumnName(),userId);
	}

        public static String getUserIDColumnName(){
          return com.idega.core.user.data.UserBMPBean.getColumnNameUserID();
        }

        public void setLastChanged(Timestamp when) {
          setColumn(getLastChangedColumnName(),when);
        }

        public Timestamp getLastChanged() {
          return((Timestamp)getColumnValue(getLastChangedColumnName()));
        }

        /**
         * Sets both the intented encrypted password and the original unencrypted password for temporary retrieval
         */
        public void setUserPassword(String encryptedPassword,String unEncryptedPassword){
          this.unEncryptedUserPassword=unEncryptedPassword;
          this.setUserPassword(encryptedPassword);
        }

        /**
         * Gets the original password if the record is newly created, therefore it can be retrieved , if this is not a newly created record the exception PasswordNotKnown is thrown
         */
        public String getUnencryptedUserPassword()throws PasswordNotKnown{
          if(unEncryptedUserPassword==null){
            throw new PasswordNotKnown(this.getUserLogin());
          }
          else{
            return unEncryptedUserPassword;
          }
        }
        
        
		public void setLoginType(String loginType) {
			  setColumn(getLoginTypeColumnName(), loginType);
		}
	
		public String getLoginType() {
			  return getStringColumnValue(getLoginTypeColumnName());
		}
		
		
		public Collection ejbFindLoginsForUser(User user) throws FinderException{
			
			IDOQuery query = idoQuery();
			query.appendSelectAllFrom(this);
			query.appendWhereEquals(getColumnNameUserID(),user.getPrimaryKey());

			return idoFindPKsByQuery(query);	
		}
			
		
		
}

