//idega 2000 - Gimmi

package is.idega.idegaweb.golf.entity;

//import java.util.*;
import is.idega.idegaweb.golf.block.text.data.TextModule;

import java.sql.SQLException;

import com.idega.data.GenericEntity;

public class HoleTextBMPBean extends GenericEntity implements HoleText{

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
                addAttribute("field_id","N�mer vallar",true,true,"java.lang.Integer","many-to-one","is.idega.idegaweb.golf.entity.Field");
                addAttribute("hole_number","N�mer holu",true,true,"java.lang.Integer");
		addAttribute("text_id", "F�lag", true, true, Integer.class,"many-to-one",TextModule.class);
	}

        public String getEntityName(){
		return "hole_text";
	}

        public void setFieldId(int field_id) {
            setColumn("field_id",new Integer(field_id));
        }

        public int getFieldId() {
            return getIntColumnValue("field_id");
        }

        public void setHoleNumber(int hole_number) {
            setColumn("hole_number",new Integer(hole_number));
        }

        public int getHoleNumber() {
            return getIntColumnValue("hole_number");
        }

	public void setTextId(int text_id) {
            setColumn("text_id",new Integer(text_id));
        }

        public int getTextId() {
            return getIntColumnValue("text_id");
        }
}
