package com.idega.projects.golf.presentation;

import com.idega.jmodule.login.business.AccessControl;
import com.idega.presentation.ui.*;
import com.idega.projects.golf.moduleobject.GolfDialog;
import com.idega.projects.golf.entity.*;
import com.idega.presentation.*;
import com.idega.projects.golf.business.TournamentController;

/**
*@author <a href="mailto:gimmi@idega.is">Gr�mur</a>
*@version 1.0
*/
 public class TournamentDeleter extends TournamentAdmin {

    public TournamentDeleter(){
        super();
        //System.out.println("TournamentDeleter()");
    }


public void main(IWContext iwc){
	//initializeButtons();
        //System.out.println("TournamentDeleter.main()");
        try{
          String tournament_id;
          String action = iwc.getParameter("action");
          Table table = new Table(2,3);
          add(table);
          Member member = (Member) AccessControl.getMember(iwc);
          //if (member == null){
          //  member = new Member();
          //}

          tournament_id=iwc.getParameter("tournament_id");
          String OK = iwc.getParameter("OK");

          if (tournament_id != null){

            Tournament tournament = new Tournament(Integer.parseInt(tournament_id));

            if(OK==null){
                boolean permission=false;
                if(AccessControl.isAdmin(iwc)){
                  permission=true;
                  if(AccessControl.isClubAdmin(iwc)){

                    //member = (Member)com.idega.jmodule.login.business.AccessControl.getMember(iwc);
                    int union_id = member.getMainUnionID();
                    int tourn_union_id=tournament.getUnionId();

                    if(union_id==tourn_union_id){
                      permission=true;
                    }
                    else{
                      permission=false;
                    }
                  }
                }
                if(permission){
                  table.add("Ertu viss um a� ey�a m�tinu "+tournament.getName()+"?");
                  table.addBreak();
                  table.add("Athuga�u a� ef m�tinu er eytt afskr�st allir sem hafa skr�� sig a m�ti�");
                  Form form = new Form();
                  table.add(form);
                  SubmitButton button = new SubmitButton("J�");
                  form.add(button);
                  form.add(new Parameter("tournament_id",tournament_id));
                  form.add(new Parameter("OK","OK"));
                }
                else{
                  table.add("�� hefur ekki r�ttindi til a� ey�a m�tinu "+tournament.getName()+"?",1,1);
                  table.add(new CloseButton("Loka"),1,2);
                }

            }
            else{
                tournament.delete();
                table.add("M�ti "+tournament.getName()+" eytt",1,1);
                table.add(new CloseButton("Loka"),1,2);
                TournamentController.removeTournamentTableApplicationAttribute(iwc);
                getWindow().setParentToReload();

            }
          }
          else {
             add("Ver�ur a� velja m�t");
          }
        }
        catch(Exception ex){
          add(new ExceptionWrapper(ex,this));
        }
}


}// class TournamentDeleter


