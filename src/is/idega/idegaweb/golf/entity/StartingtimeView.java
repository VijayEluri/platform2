//idega 2000 - Gimmi

package is.idega.idegaweb.golf.entity;

import java.sql.*;
import com.idega.data.GenericEntity;

public class StartingtimeView extends GenericEntity{

	public StartingtimeView(){
		super();
	}

        public String getEntityName(){
		return "startingtime_view";
	}

	public void initializeAttributes(){
		addAttribute("startingtime_id","n�mer r�stima",true,true,"java.lang.Integer");
		addAttribute("field_id","V�llur",true,true,"java.lang.Integer");
		addAttribute("member_id","Me�limur",true,true,"java.lang.Integer");
		addAttribute("startingtime_date","dagsetning r�stima",true,true,"java.sql.Date");
		addAttribute("grup_num","R�sh�pur",true,true, "java.lang.Integer");
		addAttribute("first_name","Fornafn",true,true,"java.lang.String");
		addAttribute("middle_name","Mi�nafn",true,true,"java.lang.String");
		addAttribute("last_name","Eftirnafn",true,true,"java.lang.String");
		addAttribute("social_security_number","Eftirnafn",true,true,"java.lang.String");
		addAttribute("abbrevation", "Skammst�fun", true, true, "java.lang.String");
		addAttribute("handicap","Leikforgj�f",true,true,"java.lang.Float");
		addAttribute("union_id","n�mer me�lims",true,true,"java.lang.Integer");
	}


        public int getId() {
            return getStartingtimeId();
        }

        public int getStartingtimeId() {
            return getIntColumnValue("startingtime_id");
        }

        public int getMemberId() {
            return getIntColumnValue("member_id");
        }

        public int getFieldId() {
            return getIntColumnValue("field_id");
        }

        public Date getDate() {
            return getStartingtimeDate();
        }

        public Date getStartingtimeDate(){
            return (Date)getColumnValue("startingtime_date");
        }

  	public int getGroupNumber() {
		return getIntColumnValue("grup_num");
	}

	public String getName(){
          StringBuffer nameBuffer = new StringBuffer();
		if ((getFirstName() != null) && (getMiddleName() != null) && (getLastName() != null)){
			 nameBuffer.append(getFirstName());
                          nameBuffer.append(" ");
                           nameBuffer.append(getMiddleName());
                            nameBuffer.append(" ");
                             nameBuffer.append(getLastName());
		}
		else if ((getFirstName() != null) && (getLastName() != null)){
                   nameBuffer.append(getFirstName());
                    nameBuffer.append(" ");
                     nameBuffer.append(getLastName());
		}
		else if(getLastName() != null){
                  nameBuffer.append(getLastName());
		}
		else if (getFirstName() != null){
                  nameBuffer.append(getFirstName());
		}
		return  nameBuffer.toString();
	}

	public String getFirstName(){
		return (String) getColumnValue("first_name");
	}

	public String getMiddleName(){
		return (String) getColumnValue("middle_name");
	}

	public String getLastName(){
		return (String) getColumnValue("last_name");
	}

        public String getSocialSecurityNumber() {
            return getStringColumnValue("social_security_number");
        }

        public String getAbbrevation() {
            return getStringColumnValue("abbrevation");
        }

        public float getHandicap() {
            return getFloatColumnValue("handicap");
        }

        public int getUnionId() {
            return getIntColumnValue("union_id");
        }


        public void insert() {}
        public void delete() {}
        public void update() {}

}