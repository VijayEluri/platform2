//idega 2000 - eiki

package com.idega.projects.lv.entity;

//import java.util.*;
import java.sql.*;
import com.idega.data.*;

public class Division extends GenericEntity{

	public Division(){
		super();
	}

	public Division(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("division_name","Nafn deildar",true,true,"java.lang.String");
                addAttribute("parent_id","pabba id",true,true,"java.lang.Integer");
	}

	public String getEntityName(){
		return "division";
	}

        public int getParentId() {
            return getIntColumnValue("parent_id");
        }

        public void setParentId(int parent_id) {
          setColumn("parent_id",(new Integer(parent_id)));
        }

	public String getName() {
		return getDivisionName();
	}

	public void setName(String name) {
		setDivisionName(name);
	}

	public String getDivisionName() {
		return getStringColumnValue("division_name");
	}

	public void setDivisionName(String name) {
		setColumn("division_name",name);
	}

}
