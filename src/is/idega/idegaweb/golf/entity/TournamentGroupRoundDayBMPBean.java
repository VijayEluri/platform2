//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/





/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.2

*/

package is.idega.idegaweb.golf.entity;



import java.sql.*;

import com.idega.data.*;



public class TournamentGroupRoundDayBMPBean extends is.idega.idegaweb.golf.entity.GolfEntityBMPBean implements is.idega.idegaweb.golf.entity.TournamentGroupRoundDay {



	public TournamentGroupRoundDayBMPBean(){

		super();

	}



	public TournamentGroupRoundDayBMPBean(int id)throws SQLException{

		super(id);

	}



	public void initializeAttributes(){



		addAttribute(getIDColumnName());

		addAttribute("tournament_group_id","M�tsh�pur",true,true,"java.lang.Integer","many-to-one","is.idega.idegaweb.golf.entity.TournamentGroup");

		addAttribute("tournament_round_id","Umfer�",true,true,"java.lang.Integer","many-to-one","is.idega.idegaweb.golf.entity.TournamentRound");

		addAttribute("tournament_day_id","M�tsdagur",true,true,"java.lang.Integer","many-to-one","is.idega.idegaweb.golf.entity.TournamentDay");

		addAttribute("startingtime_begin","R�stimar hefjast",true,true,"java.sql.Timestamp");

                addAttribute("startingtime_end","R�stimar enda",true,true,"java.sql.Timestamp");

	}



	public String getEntityName(){

		return "tournament_group_round_day";

	}



        public String getName(){

          return this.getTournamentRound().getName()+" me� "+this.getTournamentGroup().getName()+" � "+this.getTournamentDay().getName();

        }



        public TournamentGroup getTournamentGroup(){

          return (TournamentGroup)getColumnValue("tournament_group_id");

        }



        public TournamentRound getTournamentRound(){

           return (TournamentRound)getColumnValue("tournament_round_id");

        }



        public TournamentDay getTournamentDay(){

           return (TournamentDay)getColumnValue("tournament_day_id");

        }









}

