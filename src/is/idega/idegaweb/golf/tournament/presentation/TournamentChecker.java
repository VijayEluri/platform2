/*
 * Created on 5.3.2004
 */
package is.idega.idegaweb.golf.tournament.presentation;

import is.idega.idegaweb.golf.entity.Member;
import is.idega.idegaweb.golf.entity.MemberHome;
import is.idega.idegaweb.golf.entity.Scorecard;
import is.idega.idegaweb.golf.entity.ScorecardHome;
import is.idega.idegaweb.golf.entity.Startingtime;
import is.idega.idegaweb.golf.entity.StartingtimeHome;
import is.idega.idegaweb.golf.entity.Tournament;
import is.idega.idegaweb.golf.entity.TournamentGroup;
import is.idega.idegaweb.golf.entity.TournamentHome;
import is.idega.idegaweb.golf.entity.TournamentRound;
import is.idega.idegaweb.golf.presentation.GolfBlock;
import is.idega.idegaweb.golf.tournament.business.TournamentController;

import java.sql.SQLException;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.data.IDOLookup;
import com.idega.data.SimpleQuerier;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TimeInput;
import com.idega.util.IWTimestamp;

/**
 * @author gimmi
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TournamentChecker extends GolfBlock {

  public void main(IWContext modinfo) throws Exception {
    String tournament_id = modinfo.getParameter("tournament_id");
    IWBundle iwb = getBundle();
    IWResourceBundle iwrb = getResourceBundle();

    if (tournament_id != null) {
        String action = modinfo.getParameter("action");
        if (action != null) {
            if (action.equals("doCheck")) {
                checkTournament(((TournamentHome) IDOLookup.getHomeLegacy(Tournament.class)).findByPrimaryKey(Integer.parseInt(tournament_id)),iwrb,iwb);
            }else if (action.equals("fix")) {
                prepareFix(modinfo,iwrb,iwb);
            }else if (action.equals("doFix")) {
                doFix(modinfo,iwrb,iwb);
            }else if (action.equals("delete")) {
                delete(modinfo,iwrb,iwb);
            }else if (action.equals("deleteOneScorecard")) {
                String scorecard_id = modinfo.getParameter("scorecard_id");
                if (scorecard_id != null) {
                    deleteScorecard(Integer.parseInt(scorecard_id));
                }
                checkTournament(((TournamentHome) IDOLookup.getHomeLegacy(Tournament.class)).findByPrimaryKey(Integer.parseInt(tournament_id)),iwrb,iwb);
            }else if (action.equals("deleteAllScorecards")) {
                String[] scorecard_ids = modinfo.getParameterValues("scorecard_id");
                if (scorecard_ids != null) {
                    for (int i = 0; i < scorecard_ids.length; i++) {
                        deleteScorecard(Integer.parseInt(scorecard_ids[i]));
                    }
                }
                checkTournament(((TournamentHome) IDOLookup.getHomeLegacy(Tournament.class)).findByPrimaryKey(Integer.parseInt(tournament_id)),iwrb,iwb);
            }else if (action.equals("deleteOneStartingtime")) {
                String startingtime_id = modinfo.getParameter("startingtime_id");
                if (startingtime_id != null) {
                    deleteStartingtime(Integer.parseInt(startingtime_id));
                }
                checkTournament(((TournamentHome) IDOLookup.getHomeLegacy(Tournament.class)).findByPrimaryKey(Integer.parseInt(tournament_id)),iwrb,iwb);
            }else if (action.equals("deleteAllStartingtimes")) {
                String[] startingtime_ids = modinfo.getParameterValues("startingtime_id");
                if (startingtime_ids != null) {
                    for (int i = 0; i < startingtime_ids.length; i++) {
                        deleteStartingtime(Integer.parseInt(startingtime_ids[i]));
                    }
                }
                checkTournament(((TournamentHome) IDOLookup.getHomeLegacy(Tournament.class)).findByPrimaryKey(Integer.parseInt(tournament_id)),iwrb,iwb);
            }


        }else{
        add(iwrb.getLocalizedString("tournament.no_tournament_selected","No tournament selected"));
        }
    }else{
        add(iwrb.getLocalizedString("tournament.no_tournament_selected","No tournament selected"));
    }
}

public void checkTournament(Tournament tournament,IWResourceBundle iwrb, IWBundle iwb) throws SQLException {

    List members = TournamentController.getMembersInTournamentList(tournament);
    TournamentRound[] tRounds = tournament.getTournamentRounds();

    int roundRows = tRounds.length;

    Image yes = iwb.getImage("shared/paid.gif");
    Image no = iwb.getImage("shared/unpaid.gif");
    Image total = null;
    IWTimestamp stamp = null;
    Link fix = null;
    Link delete = null;


    Table table = new Table();
        table.setBorder(0);
        table.setWidth("90%");
        table.setAlignment("center");

    int row = 1;
    String startingtime_id = "0";
    String scorecard_id = "0";
    Member member = null;
    String[] results = null;


    Text tournamentName = new Text(tournament.getName());
        tournamentName.setBold();
        tournamentName.setFontSize(3);
    table.add(tournamentName,1,row);
    table.mergeCells(1,row,2,row );


    for (int i = 0; i < tRounds.length; i++) {
        table.add(tRounds[i].getName(),3+i,row);
        table.addBreak(3+i,row);
        table.add(iwrb.getLocalizedString("tournament.map_tee_time","map / tee time"),3+i,row);
        table.setAlignment(3+i,row,"center");
    }

    ++row;
    table.mergeCells(1,row,2,row );

    if (members != null) {
        java.util.Collections.sort(members,new com.idega.util.GenericMemberComparator(com.idega.util.GenericMemberComparator.FIRSTLASTMIDDLE));

        for (int i = 0; i < members.size(); i++) {
            ++row;
            total = yes;
            startingtime_id = "0";
            scorecard_id = "0";
            member = (Member) members.get(i);
            table.add(member.getName(),2,row);

            for (int j = 0; j < tRounds.length; j++) {
                table.setAlignment(3+j,row,"center");
                try {
                    results = SimpleQuerier.executeStringQuery("SELECT SCORECARD_ID FROM SCORECARD WHERE TOURNAMENT_ROUND_ID = "+tRounds[j].getID()+" AND MEMBER_ID = "+member.getID()+"");
                    if (results.length == 1) {
                        table.add(yes,3+j,row);
                        scorecard_id = results[0];
                    }
                    else {
                        table.add("("+results.length+") ",3+j,row);
                        table.add(no,3+j,row);
                        total = no;
                    }
                }
                catch (Exception e) {}

                table.add(" / ",3+j,row);

                try {
                    stamp = new IWTimestamp(tRounds[j].getRoundDate());
                    results = SimpleQuerier.executeStringQuery("SELECT STARTINGTIME.STARTINGTIME_ID FROM STARTINGTIME,TOURNAMENT_ROUND_STARTINGTIME WHERE TOURNAMENT_ROUND_STARTINGTIME.TOURNAMENT_ROUND_ID = "+tRounds[j].getID()+" AND TOURNAMENT_ROUND_STARTINGTIME.STARTINGTIME_ID = STARTINGTIME.STARTINGTIME_ID AND STARTINGTIME_DATE = '"+stamp.toSQLDateString()+"' AND MEMBER_ID = "+member.getID()+"");
                    if (results.length == 1) {
                        table.add(yes,3+j,row);
                        startingtime_id = results[0];
                    }
                    else {
                        table.add(no,3+j,row);
                        table.add(" ("+results.length+") ",3+j,row);
                        if (j == 0) {
                            total = no;
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace(System.err);
                }

            }
            table.add(total,1,row);
            table.setWidth(1,row,"5%");
            fix = new Link(iwrb.getLocalizedString("tournament.fix","Fix"));
              fix.addParameter("action","fix");
              fix.addParameter("tournament_id",""+tournament.getID());
              fix.addParameter("member_id",""+member.getID());
              fix.addParameter("startingtime_id",startingtime_id);
              fix.addParameter("scorecard_id",scorecard_id);
            table.add(fix,3+roundRows,row);

            table.add(" - ",3+roundRows,row);

            delete = new Link(iwrb.getLocalizedString("tournament.delete","Delete"));
              delete.addParameter("action","delete");
              delete.addParameter("tournament_id",""+tournament.getID());
              delete.addParameter("member_id",""+member.getID());
            table.add(delete,3+roundRows,row);


        }

    }
    ++row;
    ++row;

    Text scorecardName = new Text(iwrb.getLocalizedString("tournament.extra_scorecard","Extra scorecard"));
        scorecardName.setBold();
        scorecardName.setFontSize(3);
    table.add(scorecardName,1,row);
    table.mergeCells(1,row,2,row );

    Link deleteOneScorecard = null;
    Link deleteAllScorecards = new Link(iwrb.getLocalizedString("tournament.delete_all_scorecards","Delete all scorecards"));
      deleteAllScorecards.addParameter("action","deleteAllScorecards");
      deleteAllScorecards.addParameter("tournament_id",""+tournament.getID());

    table.add(deleteAllScorecards,3+roundRows,row);


    Scorecard[] theScorecards = null;
    for (int i = 0; i < tRounds.length; i++) {
        theScorecards = (Scorecard[]) ((Scorecard) IDOLookup.instanciateEntity(Scorecard.class)).findAll("Select * from scorecard where tournament_round_id = "+tRounds[i].getID()+" AND member_id not in (Select member_id from tournament_member where tournament_id = "+tournament.getID()+")");
        for (int j = 0; j < theScorecards.length; j++) {
            ++row;
            table.add(theScorecards[j].getMember().getName(),2,row);
            deleteAllScorecards.addParameter("scorecard_id",""+theScorecards[j].getID());

            deleteOneScorecard = new Link(iwrb.getLocalizedString("tournament.delete","Delete"));
              deleteOneScorecard.addParameter("action","deleteOneScorecard");
              deleteOneScorecard.addParameter("scorecard_id",""+theScorecards[j].getID());
              deleteOneScorecard.addParameter("tournament_id",""+tournament.getID());
            table.add(deleteOneScorecard,3+roundRows,row);
        }

    }

    ++row;
    ++row;
//Eitthvad mis enntha
/*
    Text startingtimeName = new Text(iwrb.getLocalizedString("tournament.extra_tee_time","Extra tee time"));
        startingtimeName.setBold();
        startingtimeName.setFontSize(3);
    table.add(startingtimeName,1,row);
    table.mergeCells(1,row,2,row );

    Link deleteOneStartingtime = null;
    Link deleteAllStartingtimes = new Link(iwrb.getLocalizedString("tournament.delte_all_extra_tee_times","Delte all extra tee times"));
      deleteAllStartingtimes.addParameter("action","deleteAllStartingtimes");
      deleteAllStartingtimes.addParameter("tournament_id",""+tournament.getID());

    table.add(deleteAllStartingtimes,3+roundRows,row);


    Startingtime[] theStartingtimes = null;
    for (int i = 0; i < tRounds.length; i++) {
        theStartingtimes = (Startingtime[]) (new Startingtime()).findAll("Select * from startingtime where field_id = "+tournament.getFieldId()+" AND startingtime_date = '"+new idegaTimestamp(tRounds[i].getRoundDate()).toSQLDateString()+"' AND  (startingtime_id is null OR startingtime_id < 1 OR startingtime_id not in (Select startingtime_id from tournament_round_startingtime where tournament_round_id = "+tRounds[i].getID()+"))");

        for (int j = 0; j < theStartingtimes.length; j++) {
            ++row;

            table.add(theStartingtimes[j].getPlayerName(),2,row);


            deleteAllStartingtimes.addParameter("startingtime_id",""+theStartingtimes[j].getID());

            deleteOneStartingtime = new Link(iwrb.getLocalizedString("tournament.delete","Delete"));
              deleteOneStartingtime.addParameter("action","deleteOneStartingtime");
              deleteOneStartingtime.addParameter("startingtime_id",""+theStartingtimes[j].getID());
              deleteOneStartingtime.addParameter("tournament_id",""+tournament.getID());
            table.add(deleteOneStartingtime,3+roundRows,row);

        }

    }

    ++row;
    ++row;
*/

    Text txtAth = new Text(iwrb.getLocalizedString("tournament.nb","NB"));
        txtAth.setBold();
        txtAth.setFontSize(3);

    table.mergeCells(1,row,2,row);
    table.add(txtAth,1,row);
    table.add("<br>",1,row);
    table.add(iwrb.getLocalizedString("tournament.fix_delete_procedure","Fixing procedure replaces player's scorecards"),1,row);



    add("<br>");
    add(table);
    add("<br>");


}


public void doFix(IWContext modinfo, IWResourceBundle iwrb, IWBundle iwb) throws Exception {
    try {
        String tournament_id = modinfo.getParameter("tournament_id");
        String member_id = modinfo.getParameter("member_id");

        String startingtime_id = modinfo.getParameter("startingtime_id");
        if (startingtime_id == null) {
            startingtime_id = "0";
        }


        Tournament tournament = ((TournamentHome) IDOLookup.getHomeLegacy(Tournament.class)).findByPrimaryKey(Integer.parseInt(tournament_id));
        TournamentRound[] tRounds = tournament.getTournamentRounds();

        if (tRounds.length > 0) {
            Member member = ((MemberHome) IDOLookup.getHomeLegacy(Member.class)).findByPrimaryKey(Integer.parseInt(member_id));

            String[] tournament_group_ids = SimpleQuerier.executeStringQuery("SELECT TOURNAMENT_GROUP_ID FROM TOURNAMENT_MEMBER WHERE TOURNAMENT_ID = "+tournament_id+" AND MEMBER_ID = "+member_id+"");

            int grupNum = 1;

            if (startingtime_id.equals("0")) {
                String time = modinfo.getParameter("time");
                IWTimestamp stamp = new IWTimestamp(tRounds[0].getRoundDate());
                    stamp.setHour(Integer.parseInt(time.substring(0,2)));
                    stamp.setMinute(Integer.parseInt(time.substring(3,5)));
                    grupNum = getGrupNum(tRounds[0],stamp);
            }
            else {
                Startingtime sTime = ((StartingtimeHome) IDOLookup.getHomeLegacy(Startingtime.class)).findByPrimaryKey(Integer.parseInt(startingtime_id));
                grupNum = sTime.getGroupNum();
            }


            TournamentController.removeMemberFromTournament(modinfo, tournament,member);
            TournamentController.registerMember(member,tournament,tournament_group_ids[0]);

            TournamentController.setupStartingtime(modinfo, member,tournament,tRounds[0].getID(),grupNum);

        }
        checkTournament(tournament,iwrb,iwb);
    }
    catch (Exception e) {
        e.printStackTrace(System.err);
        add("Error");
    }
}

public void prepareFix(IWContext modinfo,IWResourceBundle iwrb, IWBundle iwb) throws Exception {
    String startingtime_id = modinfo.getParameter("startingtime_id");
        if (startingtime_id == null) {
            startingtime_id = "0";
        }

    if (startingtime_id.equalsIgnoreCase("0")) {
        String tournament_id = modinfo.getParameter("tournament_id");
        String member_id = modinfo.getParameter("member_id");

        Form form = new Form();
            form.maintainParameter("tournament_id");
            form.maintainParameter("member_id");

        Table table = new Table();
            table.setAlignment("center");
            table.setBorder(0);
            table.add(new HiddenInput("action","doFix"));
            form.add(table);

            table.add(iwrb.getLocalizedString("tournament.tee_time1","Tee time"));
            table.add(new TimeInput("time"));
            table.add(new SubmitButton(iwrb.getImage("/buttons/continue.gif")),1,2);
            table.setAlignment(1,1,"left");
            table.setAlignment(1,2,"right");

        add(form);
    }
    else {
        doFix(modinfo,iwrb,iwb);
    }
}


public void delete(IWContext modinfo,IWResourceBundle iwrb, IWBundle iwb) throws Exception {
    try {
        String tournament_id = modinfo.getParameter("tournament_id");
        String member_id = modinfo.getParameter("member_id");


        Tournament tournament = ((TournamentHome) IDOLookup.getHomeLegacy(Tournament.class)).findByPrimaryKey(Integer.parseInt(tournament_id));
        Member member = ((MemberHome) IDOLookup.getHomeLegacy(Member.class)).findByPrimaryKey(Integer.parseInt(member_id));

        TournamentController.removeMemberFromTournament(modinfo, tournament,member);

        checkTournament(tournament,iwrb,iwb);

    }
    catch (Exception e) {
        e.printStackTrace(System.err);
        add("Error");
    }

}


public int getGrupNum(TournamentRound tRound, IWTimestamp stamp) {
    int returner = 0;

    Tournament tournament = tRound.getTournament();
    IWTimestamp startStamp = new IWTimestamp(tRound.getRoundDate());
        stamp.addMinutes(1);

    int interval = tournament.getInterval();

    if (stamp.isLaterThan(startStamp)) {
      while (stamp.isLaterThan(startStamp)) {
          startStamp.addMinutes(interval);
          ++returner;
      }
    }
    else {
        returner = -10;
    }

    return returner;
}

public void deleteScorecard(int scorecard_id) throws SQLException {
	try {
    Scorecard scorecard = ((ScorecardHome) IDOLookup.getHomeLegacy(Scorecard.class)).findByPrimaryKey(scorecard_id);
    scorecard.delete();
	}
	catch (FinderException fe) {
		throw new SQLException(fe.getMessage());
	}
}

public void deleteStartingtime(int startingtime_id) throws SQLException {
	try {
    Startingtime startingtime = ((StartingtimeHome) IDOLookup.getHomeLegacy(Startingtime.class)).findByPrimaryKey(startingtime_id);
    startingtime.removeFrom((TournamentGroup) IDOLookup.instanciateEntity(TournamentGroup.class));
    startingtime.removeFrom((Tournament) IDOLookup.instanciateEntity(Tournament.class));

    startingtime.delete();
	}
	catch (FinderException fe) {
		throw new SQLException(fe.getMessage());
	}
}


}
