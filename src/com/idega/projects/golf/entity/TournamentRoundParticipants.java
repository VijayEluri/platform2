//idega 2000 - Laddi

package com.idega.projects.golf.entity;

//import java.util.*;
import java.sql.*;


public class TournamentRoundParticipants extends TournamentParticipants{

	public TournamentRoundParticipants(){
		super();
	}

        public String getEntityName(){
		return "tournament_round_participants";
	}

	public void initializeAttributes(){
		addAttribute("member_id","n�mer me�lims",true,true,"java.lang.Integer");
		addAttribute("social_security_number","Kennitala",true,true,"java.lang.String");
		addAttribute("first_name","Fornafn",true,true,"java.lang.String");
		addAttribute("middle_name","Mi�nafn",true,true,"java.lang.String");
		addAttribute("last_name","Eftirnafn",true,true,"java.lang.String");
		addAttribute("abbrevation", "Skammst�fun", true, true, "java.lang.String");
		addAttribute("tournament_id","M�t",true,true,"java.lang.Integer");
		addAttribute("tournament_group_id","M�tah�pur",true,true,"java.lang.Integer");
		addAttribute("scorecard_id","Skorkort",true,true,"java.lang.Integer");
		addAttribute("scorecard_date", "Dagsetning", true, true, "java.sql.Timestamp");
		addAttribute("tournament_round_id","M�tahringur",true,true,"java.lang.Integer");
		addAttribute("round_number","N�mer hrings",true,true,"java.lang.Integer");
		addAttribute("holes_played","Fj�ldi hola",true,true,"java.lang.Integer");
		addAttribute("round_handicap","Leikforgj�f",true,true,"java.lang.Float");
		addAttribute("strokes_without_handicap","H�gg �n forgjafar",true, true , "java.lang.Integer");
		addAttribute("strokes_with_handicap","H�gg me� forgj�f",true, true , "java.lang.Integer");
		addAttribute("total_points","Heildarpunktar",true,true,"java.lang.Integer");
		addAttribute("total_par","Par vallarins",true, true , "java.lang.Integer");
		addAttribute("difference","par",true,true, "java.lang.Integer");
		addAttribute("grup_num","R�sh�pur",true,true, "java.lang.Integer");
	}

	public int getGroupNumber() {
		return getIntColumnValue("grup_num");
	}

}