//idega 2000 - Gimmi

package com.idega.jmodule.timesheet.data;


//import java.util.*;
import java.sql.*;
import com.idega.data.*;

public class ProjectExtra extends GenericEntity{

	public ProjectExtra(){
		super();
	}

	public ProjectExtra(int id)throws SQLException{
		super(id);
	}


	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("description","l�sing",true,true,"java.lang.String");
		addAttribute("goals","markmi�",true,true,"java.lang.String");
		addAttribute("finances","fj�lm�l",true,true,"java.lang.String");
		addAttribute("tasks","verkli�ir",true,true,"java.lang.String");
	}

	public String getIDColumnName() {
		return "project_extra_id";
	}

	public String getEntityName(){
		return "project_extra";
	}

	public String getDescription() {
		return getStringColumnValue("description");
	}
	
	public void setDescription(String description) {
		setColumn("description",description);
	}

	public String getGoals() {
		return getStringColumnValue("goals");
	}
	
	public void setGoals(String goals) {
		setColumn("goals",goals);
	}
	
	public String getFinances() {
		return getStringColumnValue("finances");
	}
	
	public void setFinances(String finances) {
		setColumn("finances",finances);
	}
	
	public String getTasks() {
		return getStringColumnValue("tasks");
	}
	
	public void setTasks(String tasks) {
		setColumn("tasks",tasks);
	}
	


}
