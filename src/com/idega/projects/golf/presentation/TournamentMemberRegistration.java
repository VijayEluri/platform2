package com.idega.projects.golf.presentation;

import com.idega.jmodule.login.business.AccessControl;
import com.idega.presentation.ui.*;
import com.idega.projects.golf.moduleobject.GolfDialog;
import com.idega.projects.golf.entity.*;
import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.projects.golf.business.TournamentController;
import com.idega.projects.golf.entity.Member;
import java.sql.SQLException;
import java.util.Vector;
import java.util.StringTokenizer;

/**
*@author <a href="mailto:gimmi@idega.is">Gr�mur</a>
*@version 1.0
*/
 public class TournamentMemberRegistration extends TournamentAdmin {

Tournament tournament = null;


    public TournamentMemberRegistration(){
        super();
        //System.out.println("TournamentMemberRegistration()");
    }



public void main(IWContext iwc)throws Exception{
	//initializeButtons();
	String tournament_id;
        String action = iwc.getParameter("action");
        Table table = new Table(2,3);
        add(table);
        Member member = (Member) AccessControl.getMember(iwc);

        tournament_id=iwc.getParameter("tournament");
        if (tournament_id == null) {
            tournament_id = (String) iwc.getSessionAttribute("golf_register_member_tournament_id");
        }
        else {
            iwc.setSessionAttribute("golf_register_member_tournament_id",tournament_id);
        }

        if (action == null) {
            iwc.removeApplicationAttribute("golf_register_member_tournament_id");
//            action = "";
        }


	if (tournament_id != null){
	  tournament = new Tournament(Integer.parseInt(tournament_id));

          if(action.equals("register")){

                if (member== null){
                  table.add("�� ver�ur a� skr� �ig inn og vera skr��ur f�lagi � golf.is");
                }
                else{
                    table.add("Ertu viss um a� �� viljir skr� �ig m�ti� "+tournament.getName());
                    Form form = new Form();
                    table.add(form,1,2);
                    form.maintainParameter("tournament_id");
                    SubmitButton confirmButton  = new SubmitButton("J�","action","confirmregister");
                    form.add(confirmButton);
                }


          }
          else if(action.equals("confirmregister")){

                table.mergeCells(1,2,2,2);

                if (member== null){
                  table.add("�� ver�ur a� skr� �ig inn og vera skr��ur f�lagi � golf.is");
                }
                else{
                  if(isMemberAllowedToRegister(member,tournament)){
                    try{
                      member.addTo(tournament);
                      table.add(member.getName()+" hefur veri� skr��ur � m�ti� "+tournament.getName(),2,1);
                    }
                    catch(Exception ex){
                      //System.out.println(ex.getMessage());
                      //ex.printStackTrace(System.out);
                      table.add("Kylfingur �egar skr��ur e�a �nnur villa kom upp");
                    }
                  }
                  else{
                   table.add(member.getName()+" hefur ekki leyfi til a� skr� sig � "+tournament.getName(),2,1);
                  }


                }
              }
              else if(action.equals("unregister")){

                    try{
                      member.removeFrom(tournament);
                      table.add(member.getName()+" hefur veri� afskr��ur af m�tinu "+tournament.getName(),2,1);
                    }
                    catch(Exception ex){
                      System.out.println(ex.getMessage());
                      ex.printStackTrace(System.out);
                      table.add("Kylfingur ekki ��ur skr��ur a moti� e�a �nnur villa kom upp");
                    }
              }
              else if (action.equals("selectmember") ) {
                  selectMember(iwc);
              }
              else if (action.equals("searchBySocialSecurityNumber")) {
                  searchBySocialSecurityNumber(iwc);
              }
              else if (action.equals("getSearchBySocialSecurityNumberResults")) {
                  getSearchBySocialSecurityNumberResults(iwc);
              }
              else if (action.equals("registermarkedmembers")) {
                  registerMarkedMembers(iwc);
              }
              else if (action.equals("searchByName")) {
                  searchByName(iwc);
              }
              else if (action.equals("getSearchByNameResults")) {
                  getSearchByNameResults(iwc);
              }

	}
        else{
            Form form = new Form();
            Table table2 = new Table(1,2);
            table2.setAlignment("center");
            table2.add("Veldu m�t:",1,1);
//            DropdownMenu menu = new DropdownMenu();
            DropdownMenu menu = TournamentController.getDropdownOrderedByUnion(new DropdownMenu("tournament"),iwc);
                menu.setAttribute("size","10");


            /*if (isClubAdmin()) {
              Union union = member.getMainUnion();
              add("clubbari");
            }
            else if (isAdmin()){
              add("ekki clubbari");
            }*/
            //menu = new DropdownMenu((Tournament[])(new Tournament()).findAllOrdered("name"));


            table2.add(menu,1,2);
            SubmitButton button = new SubmitButton("Velja","action","selectmember");
            table2.add(button,1,2);
            add(form);
            form.add(table2);
        }

}

public void selectMember(IWContext iwc) throws SQLException{
    Form form = new Form();
    Table table2 = new Table(1,4);
    table2.setAlignment("center");
    table2.add("Veldu:",1,1);
    SubmitButton social = new SubmitButton("Leita eftir kennit�lu","action","searchBySocialSecurityNumber");
    SubmitButton name = new SubmitButton("Leita eftir nafni","action","searchByName");
//    SubmitButton club = new SubmitButton("Leita � kl�bbi","action","searchByClub");
    table2.add(social,1,2);
    table2.add(name,1,3);
//    table2.add(club,1,4);
    form.add(table2);
    add(form);
}


public void searchByName(IWContext iwc) {
    Form form = new Form();
    Table table2 = new Table(1,4);
    table2.setWidth(200);
    table2.setAlignment("center");
    table2.setAlignment(1,4,"center");
    table2.add("Sl��u inn nafn:",1,1);


    TextArea numberInput = new TextArea("name");
        numberInput.setWidth(30);
        numberInput.setHeight(5);
    SubmitButton socialResult = new SubmitButton("Leita","action","getSearchByNameResults");
    table2.add(numberInput,1,2);
    //table2.add("H�gt er a� sl� inn m�rg n�fn me� hverju sem er � milli.",1,3);
    table2.add(socialResult,1,4);


    form.add(table2);
    add(form);

}

public void getSearchByNameResults(IWContext iwc) throws SQLException {
    String names = iwc.getParameter("name");
    Member[] theMembers = this.findMembersByName(names);
    drawTableWithMembers(iwc, theMembers);
}
public void getSearchBySocialSecurityNumberResults(IWContext iwc) throws SQLException {
    String socialSecurityNumbers = iwc.getParameter("socialSecurityNumbers");
    Member[] theMembers = this.findMembersBySocialSecurityNumber(socialSecurityNumbers);
    drawTableWithMembers(iwc, theMembers);
}


public void searchBySocialSecurityNumber(IWContext iwc) {
    Form form = new Form();
    Table table2 = new Table(1,4);
    table2.setWidth(200);
    table2.setAlignment("center");
    table2.setAlignment(1,4,"center");
    table2.add("Sl��u inn kennit�lu:",1,1);

    TextArea numberInput = new TextArea("socialSecurityNumbers");
        numberInput.setWidth(30);
        numberInput.setHeight(5);
    SubmitButton socialResult = new SubmitButton("Leita","action","getSearchBySocialSecurityNumberResults");
    table2.add(numberInput,1,2);
    //table2.add("H�gt er a� sl� inn margar kennit�lur me� hverju sem er � milli.",1,3);
    table2.add(socialResult,1,4);
    form.add(table2);
    add(form);
}


public void drawTableWithMembers(IWContext iwc, Member[] theMembers) {
    int tableHeight = 4;

    if (theMembers != null) {
      tableHeight += theMembers.length;
    }

    Form form = new Form();
    Table table = new Table(8,tableHeight);
        table.setBorder(0);
        table.setCellpadding(2);
        table.setCellspacing(0);
        table.setWidth(2,"20");
        table.setWidth(4,"20");
        table.setWidth(6,"20");
   //    table.setWidth(200);
        table.setAlignment("center");
        table.setAlignment(1,tableHeight,"center");
        table.mergeCells(1,1,8,1);
        table.add("Ni�urst��ur leitar",1,1);
    form.add(table);




    Member member = null;
    Union union = null;
    Member[] members = null;
    CheckBox checker = null;
    boolean error = false;
    boolean notFound = false;
    boolean doSearch = true;
    String memberSocialNumber;
    String memberName;
    Link link = null;
    int memberId;
    int row = 1;

    ++row;
    table.add("<u>Kennitala</u>",1,row);
    table.add("<u>Nafn</u>",3,row);
    table.add("<u>Kl�bbur</u>",5,row);

    if (theMembers != null) {
        for (int i = 0; i < theMembers.length; i++) {
            ++row;
            memberSocialNumber = theMembers[i].getSocialSecurityNumber();
            memberName = theMembers[i].getName();
            memberId = theMembers[i].getID();
            checker = new CheckBox("checkedMemberId_"+memberId);
              checker.setChecked(true);

            link = new Link("Skr� � m�t",iwc.getRequestURI());
              link.addParameter("action","registerthesemembers");
              link.addParameter("member_id",memberId+"");
            table.add(memberSocialNumber,1,row);
            table.add(memberName,3,row);
            try {
                union = theMembers[i].getMainUnion();
                table.add(union.getAbbrevation(),5,row);
            }
            catch (Exception e) {
                e.printStackTrace(System.err);
                table.add("-",5,row);
            }
            table.add(link,7,row);
            table.add(checker,8,row);
            table.add(new HiddenInput("member_id",memberId+""),2,row);
            table.setAlignment(8,row,"right");
        }

        ++row;
        ++row;
        table.add(new BackButton("Til baka"),1,row);
        table.add(new SubmitButton("Skr� merkta","action","registermarkedmembers"),5,row);
        table.setAlignment(5,row,"right");
        table.mergeCells(5,row,8,row);
    }
    else {
        ++row;
        table.add("Enginn fannst",1,row);
    }
    table.resize(8,row);

    add(form);
}



public void registerMarkedMembers(IWContext iwc) throws SQLException{
    String[] member_id = (String[]) iwc.getParameterValues("member_id");
    String checker;
    Member member;

    for (int i = 0; i < member_id.length; i++) {
        checker = iwc.getParameter("checkedMemberId_"+member_id[i]);
        if (checker != null) {
          member = new Member(Integer.parseInt(member_id[i]));
          if (isMemberAllowedToRegister(member,this.tournament)) {
              registerMember(member, this.tournament);
          }
          else {
              add("<br>Me�limur : \""+member.getName()+"\" hefur ekki r�ttindi til a� skr� sig � m�ti�");
          }
        }
    }

}

public void registerMember(Member member, Tournament theTournament) throws SQLException {
        // temp lausn....
            try {
                member.addTo(theTournament);
                add("<br>Me�limur : \""+member.getName()+"\" hefur veri� skr��/ur � m�ti� \"");
                add(theTournament.getName()+"\"");
            }
            catch (SQLException s) {
                try {
                    Tournament[] tour = (Tournament[]) member.findRelated(theTournament);
                    if (tour.length > 0) {
                        add("<br>Me�limur : \""+member.getName()+"\" er �egar skr��/ur � m�ti�");
                    }
                }
                catch (SQLException sq) {
                    sq.printStackTrace(System.err);
                    add("<br>Me�limur : \""+member.getName()+"\" skr��ist ekki � m�ti�");
                }
            }
        //...temp lausn endar h�r

        /**
         * 2DO: setja member � m�t...
         *      checka fyrst hvort s� r�st�maskr�ning...
         *      og svo framvegis...
         */
}

public void unregisterMember(Member member, Tournament theTournament) throws SQLException{
        // temp lausn....
            try {
                member.removeFrom(theTournament);
                add("<br>Me�limur : \""+member.getName()+"\" hefur veri� skr��/ur �r m�tinu \"");
                add(theTournament.getName()+"\"");
            }
            catch (SQLException s) {
                try {
                    Tournament[] tour = (Tournament[]) member.findRelated(theTournament);
                    if (tour.length == 0) {
                        add("<br>Me�limur : \""+member.getName()+"\" er ekki skr��/ur � m�ti�");
                    }
                }
                catch (SQLException sq) {
                    sq.printStackTrace(System.err);
                    add("<br>Me�limur : \""+member.getName()+"\" skr��ist ekki �r m�tinu");
                }
            }
        //...temp lausn endar h�r
}


private boolean isMemberAllowedToRegister(Member member,Tournament tournament)throws SQLException{

	boolean theReturn = false;
        int memberAge = member.getAge();
        /**
         * TODO: Testa betur
         */


        if (member.getMainUnionID() != 1) {
            TournamentGroup[] groups = tournament.getTournamentGroups();
            //TournamentGroup[] groups = (TournamentGroup[])(new TournamentGroup()).findAll();


            if (tournament.getIfOpenTournament()){

                            if (tournament.getIfGroupTournament()){
                                    //Check if member is in the tournament group
                                    for (int i = 0 ; i < groups.length; i++){
                                            if (  (groups[i].getMinAge() >= memberAge) && (groups[i].getMaxAge() <= memberAge) && (groups[i].getMinHandicap() >= member.getHandicap() ) && (groups[i].getMaxHandicap() <= member.getHandicap() ) && ( groups[i].getGender() == member.getGender().charAt(0)  ) ){
                                                    theReturn = true;
                                            }
                                           //Check age:::
                                            //else if (){
                                            //
                                            //}
                                    }
                            }
                            else{
                                    theReturn=true;
                            }


            }
            else{
                    if (member.isMemberIn(tournament.getUnion())){
                            if (tournament.getIfGroupTournament()){
                                    //Check if member is in the tournament group
                                    for (int i = 0 ; i < groups.length; i++){
                                            if (  (groups[i].getMinAge() >= memberAge) && (groups[i].getMaxAge() <= memberAge) && (groups[i].getMinHandicap() >= member.getHandicap() ) && (groups[i].getMaxHandicap() <= member.getHandicap() ) && ( groups[i].getGender() == member.getGender().charAt(0)  ) ){
                                                    theReturn = true;
                                            }
                                            //Check age:::
                                            //else if (){
                                            //
                                            //}
                                    }
                            }
                            else{
                                    theReturn = true;
                            }
                    }
            }
        }


	return theReturn;
}


public Member[] findMembersBySocialSecurityNumber(String socialSecurityNumbers) throws SQLException{
    StringTokenizer token = new StringTokenizer(socialSecurityNumbers," \n\r\t\f,;:.+abcdefghijklmnopqrstuvwxyz");
    Vector vector = new Vector();
    while (token.hasMoreTokens()) {
        vector.addElement(token.nextToken());
    }
    return findMembersBySocialSecurityNumber(vector);
}

public Member[] findMembersByName(String socialSecurityNumbers) throws SQLException{
    StringTokenizer token = new StringTokenizer(socialSecurityNumbers,"\n\r\t\f,;:.+");
    Vector vector = new Vector();
    while (token.hasMoreTokens()) {
        vector.addElement(token.nextToken());
    }
    return findMembersByName(vector);
}



public Member[] findMembersByName(Vector name) throws SQLException {
    Member[] members = null;
    Member[] tempMembers = null;
    StringTokenizer  nameParts;
    String fullName = "";
    String firstName = "";
    String middleName = "";
    String lastName = "";
    int manyNames = 0;
    int numberInserted = 0;
    String SQLString = "Select * from member where ";
    String tempSQLString = "";


    for (int i = 0; i < name.size(); i++) {
        try {
            manyNames = 0;
            fullName = "";
            firstName = "";
            middleName = "";
            lastName = "";
            tempSQLString = "";


            fullName = (String) name.elementAt(i);
            nameParts = new StringTokenizer(fullName," ");
            manyNames = nameParts.countTokens();
            for (int j = 0; j < manyNames; j++) {
                if (j == 0) {
                    firstName = nameParts.nextToken();
                }
                else if (j == manyNames -1) {
                    lastName = nameParts.nextToken();
                }
                else {
                    middleName += nameParts.nextToken();
                    if (j != manyNames -2) {
                        middleName += " ";
                    }
                }
            }

            switch (manyNames) {
                case 0: break;
                case 1:
                        tempSQLString = "Select * from member where first_name='"+firstName+"'";
                        break;
                case 2:
                        tempSQLString = "Select * from member where first_name='"+firstName+"' and (last_name='"+lastName+"' OR middle_name='"+lastName+"')";
                        break;
                default:
                        tempSQLString = "Select * from member where first_name='"+firstName+"' and middle_name='"+middleName+"' and last_name ='"+lastName+"'";
                        break;
            }

            if (!tempSQLString.equalsIgnoreCase("")) {
                tempMembers = (Member[]) (new Member()).findAll(tempSQLString);
                for (int g = 0; g < tempMembers.length; g++) {
                    if (numberInserted != 0) {
                        SQLString += " OR ";
                    }
                    ++ numberInserted;
                    SQLString += " member_id = "+tempMembers[g].getID();
                }
            }
        }
        catch (Exception e) {

        }
    }
    if (numberInserted != 0) {
        SQLString += " order by first_name, last_name, middle_name";
        members = (Member[]) (new Member()).findAll(SQLString);
    }

    return members;

}

public Member[] findMembersBySocialSecurityNumber(Vector socialSecurityNumbers) throws SQLException {
    Member[] members = null;
    String securityNumber;
    String SQLString = "Select * from member where ";

    int numberInserted = 0;

    for (int i = 0; i < socialSecurityNumbers.size() ; i++) {
        try {
            securityNumber = (String) socialSecurityNumbers.elementAt(i);
            if (securityNumber.equals("")) {
                securityNumber = "idega_engin_kennitala";
            }

            members = (Member[]) (new Member()).findAll("Select * from member where social_security_number = '"+securityNumber+"' ");
            if (members.length > 0) {
              if (numberInserted != 0) {
                  SQLString += " OR ";
              }
              else {++numberInserted;}

              SQLString += "member_id = "+members[0].getID();
            }
        }
        catch (SQLException s){
            s.printStackTrace(System.err);
        }
    }

//    SQLString += "order by first_name,last_name,middle_name";
    SQLString += "order by social_security_number";
    try {
      members = (Member[]) (new Member()).findAll(SQLString);
    }
    catch (SQLException s){
    }
    return members;

}



}// class TournamentMemberRegistration


