//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package is.idega.idegaweb.golf.entity;

//import java.util.*;
import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class LoginTableBMPBean extends GenericEntity implements LoginTable{

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("member_id","Me�limur",true,true,"java.lang.Integer","one-to-one","is.idega.idegaweb.golf.entity.Member");
		addAttribute("user_login","Notandanafn",true,true,"java.lang.String");
		addAttribute("user_password","Lykilor�",true,true,"java.lang.String");
	}

	public String getIDColumnName(){
		return "login_table_id";
	}

	public String getEntityName(){
		return "login_table";
	}

	public String getUserPassword(){
		return (String) getColumnValue("user_password");
	}

	public void setUserPassword(String userPassword){
		setColumn("user_password", userPassword);
	}
	public void setUserLogin(String userLogin) {
		setColumn("user_login", userLogin);
	}
	public String getUserLogin() {
		return (String) getColumnValue("user_login");
	}

	public int getMemberId(){
		return getIntColumnValue("member_id");
	}

	public void setMemberId(Integer memberId){
		setColumn("member_id", memberId);
	}

        public void setMemberId(int memberId) {
                setColumn("member_id", new Integer(memberId));
        }

        public void insertStartData()throws Exception{
          Member member = ((MemberHome) IDOLookup.getHomeLegacy(Member.class)).create();
          member.setFirstName("Administrator");
          member.insert();

          Group group = ((GroupHome) IDOLookup.getHomeLegacy(Group.class)).create();
          group.setName("administrator");
          group.setGroupType("accesscontrol");
          group.setDescription("Default IdegaWeb Golf Administrator");
          group.insert();

          member.addTo(group);

          LoginTable table = ((LoginTableHome) IDOLookup.getHomeLegacy(LoginTable.class)).create();
          table.setMemberId(member.getID());
          table.setUserLogin("admin");
          table.setUserPassword("golf4u");
          table.insert();
        }

}
