//idega 2000 - Eiki

package is.idega.idegaweb.golf.entity;

//import java.util.*;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.idega.data.GenericEntity;
import com.idega.data.IDOLookup;

public class PollBMPBean extends GenericEntity implements Poll{

	public void setDefaultValues(){
		setColumn("in_use",new Boolean(false));
	}
	
	public void setUnionID(int ID){
		setColumn("union_id",ID);
	}

	public void setUnionID(String ID){
		setUnionID(Integer.parseInt(ID));
	}
	
	
	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("question", "Spurning", true, true, "java.lang.String", 2000);
		addAttribute("start_time", "Hefst", true, true, "java.sql.Timestamp");
		addAttribute("end_time", "L�kur", true, true, "java.sql.Timestamp");
		addAttribute("union_id", "F�lag", true, true, "java.lang.Integer","many-to-one","is.idega.idegaweb.golf.entity.Union");
		addAttribute("in_use","� Notkun", false, false, "java.lang.Boolean");
	}
	
	public String getQuestion(){
		return getStringColumnValue("question");
	}

	public Timestamp getStartTime(){
		return (Timestamp)getColumnValue("start_time");
	}	
	
	public Timestamp getEndTime() {
		return (Timestamp)getColumnValue("end_time");
	}	

	public Union getUnion() {
		return (Union)getColumnValue("union_id");
	}

	public int getUnionID() {
		return getIntColumnValue("union_id");
	}
	
	
	public String getEntityName(){
		return "poll";
	}
	
	public String getName(){
		return getQuestion();
	}
		
	public Poll_option[] findOptions()throws SQLException{
		return (Poll_option[]) findAssociated((Poll_option) IDOLookup.instanciateEntity(Poll_option.class));
	}
	
	public void delete() throws SQLException{
		Poll_option[] options = (Poll_option[])((Poll_option) IDOLookup.instanciateEntity(Poll_option.class)).findAllByColumn("poll_id",Integer.toString(this.getID()));
		for (int i = 0; i < options.length; i++){
			options[i].delete();
		}		
		super.delete();
	}

	public boolean getIfInUse(){
		return getBooleanColumnValue("in_use");
	}

	public int getID() {
		return  getIntColumnValue("poll_id");
	}	

	public void setInUse(boolean inUse){
		setColumn("in_use",new Boolean(inUse));
	}
	
}
