//idega 2000 - Laddi

package is.idega.idegaweb.golf.entity;

//import java.util.*;
import java.sql.*;


public class TournamentParticipants extends GolfEntity{

	public TournamentParticipants(){
		super();
	}

        public String getEntityName(){
		return "tournament_participants";
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
		addAttribute("group_name","R�sh�psnafn",true,true, "java.lang.String");
	}

	public int getMemberID() {
		return getIntColumnValue("member_id");
	}

	public String getSocialSecurityNumber(){
		return (String) getColumnValue("social_security_number");
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

	public String getAbbrevation(){
		return getStringColumnValue("abbrevation");
	}

	public int getTournamentID(){
		return getIntColumnValue("tournament_id");
	}

	public int getTournamentGroupID(){
		return getIntColumnValue("tournament_group_id");
	}

	public int getScorecardID(){
		return getIntColumnValue("scorecard_id");
	}

	public java.sql.Timestamp getScorecardDate(){
		return (java.sql.Timestamp) getColumnValue("scorecard_date");
	}

	public int getTournamentRoundID(){
		return getIntColumnValue("tournament_round_id");
	}

	public int getRoundNumber(){
		return getIntColumnValue("round_number");
	}

	public int getHolesPlayed(){
		return getIntColumnValue("holes_played");
	}

	public float getRoundHandicap(){
		return getFloatColumnValue("round_handicap");
	}

	public int getStrokesWithoutHandicap(){
		return getIntColumnValue("strokes_without_handicap");
	}

	public int getStrokesWithHandicap(){
		return getIntColumnValue("strokes_with_handicap");
	}

	public int getTotalPoints(){
		return getIntColumnValue("total_points");
	}

	public int getTotalPar(){
		return getIntColumnValue("total_par");
	}

	public int getDifference(){
		return getIntColumnValue("difference");
	}

        public String getGroupName() {
          return(getStringColumnValue("group_name"));
        }

        public static is.idega.idegaweb.golf.entity.TournamentParticipants getTournamentParticipants(int member_id,int tournament_id) {
            is.idega.idegaweb.golf.entity.TournamentParticipants returner = null;
            try {
                java.util.List members = com.idega.data.EntityFinder.findAllByColumn(new is.idega.idegaweb.golf.entity.TournamentParticipants(),"member_id",member_id+"","tournament_id",tournament_id+"");
                if (members != null) {
                    if (members.size()  > 0) returner = (is.idega.idegaweb.golf.entity.TournamentParticipants) members.get(0);
                }
            }
            catch (SQLException sq) {
                sq.printStackTrace(System.err);
            }

            return returner;
        }

        public void insert(){}
        public void update(){}
        public void delete(){}
}