//idega 2000 - Laddi

package is.idega.idegaweb.golf.entity;

//import java.util.*;
import com.idega.data.GenericEntity;


public class CurrentPositionBMPBean extends GenericEntity implements CurrentPosition{

	public String getEntityName(){
		return "current_position";
	}

	public void initializeAttributes(){
		addAttribute(getIDColumnName());
		addAttribute("member_id","n�mer me�lims",true,true,"java.lang.Integer");
		addAttribute("score","sta�a",true, true , "java.lang.Integer");
		addAttribute("hole","hola",true, true , "java.lang.Integer");
		addAttribute("tournament_round_id","hringn�mer",true,true, "java.lang.Integer");
	}

	public void setTournamentRoundID(int id) {
		setColumn("tournament_round_id",id);
	}

	public int getTournamentRoundID() {
		return getIntColumnValue("tournament_round_id");
	}


	public void setMemberID(int id) {
		setColumn("member_id",id);
	}

	public void setScore(int score) {
		setColumn("score",score);
	}

	public void setHole(int hole) {
		setColumn("hole",hole);
	}

	public int getMemberID() {
		return getIntColumnValue("member_id");
	}

	public int getScore() {
		return getIntColumnValue("score");
	}

	public int getHole() {
		return getIntColumnValue("hole");
	}




}
