//idega 2000 - Gimmi



package is.idega.idegaweb.golf.entity;



//import java.util.*;

import java.sql.*;





public class RankingBMPBean extends is.idega.idegaweb.golf.entity.GolfEntityBMPBean implements is.idega.idegaweb.golf.entity.Ranking {



	public RankingBMPBean(){

		super();	

	}

	

	public RankingBMPBean(int id)throws SQLException{

		super(id);

	}

	

		public String getEntityName(){

		return "ranking";

	}



	public void initializeAttributes(){

		addAttribute(getIDColumnName());

		addAttribute("member_id","n�mer me�lims",true,true,"java.lang.Integer");

		addAttribute("score","sta�a",true, true , "java.lang.Integer");

		addAttribute("hole","hola",true, true , "java.lang.Integer");

		addAttribute("abbrevation","stytting",true,true, "java.lang.String");

		addAttribute("tournament_group_id","h�pn�mer",true,true, "java.lang.Integer");

	}

	

	public void setTournamentGroupID(int id) {

		setColumn("tournament_group_id",id);	

	}

	

	public int getTournamentGroupID() {

		return getIntColumnValue("tournament_group_id");

	}

	

	

	public void setAbbrevation(String abbr) {

		setColumn("abbrevation",abbr);

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



	public String getAbbrevation() {

		return (String) getColumnValue("abbrevation");

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

