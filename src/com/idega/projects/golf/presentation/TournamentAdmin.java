package com.idega.projects.golf.presentation;

import com.idega.jmodule.login.business.AccessControl;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.projects.golf.moduleobject.GolfDialog;
//    com.idega.projects.golf.entity.*,
//    com.idega.projects.golf.moduleobject.*,
//    com.idega.projects.golf.templates.*"

/**
*@author <a href="mailto:gimmi@idega.is">Gr�mur</a>
*@version 1.0
*/
 public class TournamentAdmin extends com.idega.jmodule.object.JModuleAdminWindow {

    public TournamentAdmin(){
      super();
      super.setMenubar(false);
    }

    public void _main(ModuleInfo modinfo)throws Exception{
       this.empty();
       super._main(modinfo);
    }

    /*
    public void main(ModuleInfo modinfo)throws Exception{
        if (AccessControl.isAdmin(modinfo)){
                GolfDialog dialog = new GolfDialog("M�tastj�ri");
                add(dialog);

            if (AccessControl.isClubAdmin(modinfo)){

                Form form1 = new Form("createtournament.jsp");
                SubmitButton Button1 = new SubmitButton(new Image("/pics/tournament/stofna.gif"),"b1");
                HiddenInput Hidden1 = new HiddenInput("tournament_control_mode","create");
                form1.add(Button1);
                dialog.add(form1);

                Form form5 = new Form("tournamentgroups.jsp");
                SubmitButton Button5 = new SubmitButton("b5","M�tsh�par");
                form5.add(Button5);
                dialog.add(form5);

                Form form2 = new Form("modifytournament.jsp");
                SubmitButton Button2 = new SubmitButton(new Image("/pics/tournament/breyta.gif"),"b2");
                HiddenInput Hidden2 = new HiddenInput("tournament_control_mode","edit");
                form2.add(Hidden2);
                form2.add(Button2);
                dialog.add(form2);

          }
          else{

                Form form1 = new Form("createtournament.jsp");
                SubmitButton Button1 = new SubmitButton(new Image("/pics/tournament/stofna.gif"),"b1");
                HiddenInput Hidden1 = new HiddenInput("tournament_control_mode","create");
                form1.add(Hidden1);
                form1.add(Button1);
                dialog.add(form1);

                Form form5 = new Form("tournamentgroups.jsp");
                SubmitButton Button5 = new SubmitButton("b5","M�tsh�par");
                form5.add(Button5);
                dialog.add(form5);

                Form form2 = new Form("modifytournament.jsp");
                SubmitButton Button2 = new SubmitButton(new Image("/pics/tournament/breyta.gif"),"b2");
                HiddenInput Hidden2 = new HiddenInput("tournament_control_mode","edit");
                form2.add(Hidden2);
                form2.add(Button2);
                dialog.add(form2);

                Form form3 = new Form("registermember.jsp");
                SubmitButton Button3 = new SubmitButton(new Image("/pics/tournament/skra.gif"),"b3");
                form3.add(Button3);
                dialog.add(form3);

                Form form4 = new Form("setupstartingtime.jsp");
                SubmitButton Button4 = new SubmitButton(new Image("/pics/tournament/stilla.gif"),"b4");
                form4.add(Button4);
                dialog.add(form4);


            }

        }
        else {
//            add("M�tastj�rinn er ni�ri sem stendur, kemur upp aftur br��lega");
            add("�� hefur ekki r�ttindi til �ess a� vera h�r");
        }
    }*/



}// class PaymentViewer


