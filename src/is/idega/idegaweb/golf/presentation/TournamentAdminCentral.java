package is.idega.idegaweb.golf.presentation;


import com.idega.presentation.JModuleAdminWindow;
import com.idega.presentation.text.Link;
import com.idega.jmodule.login.business.AccessControl;
import com.idega.presentation.ui.*;
import is.idega.idegaweb.golf.moduleobject.GolfDialog;
import com.idega.presentation.Image;
import com.idega.presentation.IWContext;
import com.idega.idegaweb.IWMainApplication;

public class TournamentAdminCentral extends TournamentAdmin{

    private static String basePage="/tournament/tournamentcentral.jsp";

    public TournamentAdminCentral() {
      super();
    }

    public void main(IWContext iwc){

        try {
            if (AccessControl.isAdmin(iwc)){
                    GolfDialog dialog = new GolfDialog("M�tastj�ri");
                    add(dialog);

                    Link link = this.getAdminButton("is.idega.idegaweb.golf.presentation.TournamentDeleter","Ey�a m�ti");
                    dialog.add(link);

                if (AccessControl.isClubAdmin(iwc)){




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

                    dialog.add("<br>");

                    Link lCreateTournament = this.getAdminButton("is.idega.idegaweb.golf.presentation.TournamentCreator","Sm��a m�t");
                    dialog.add(lCreateTournament);

                    dialog.add("<br>");

                    Link lModifyTournament = this.getAdminButton("is.idega.idegaweb.golf.presentation.TournamentModifier","Breyta m�t");
                    dialog.add(lModifyTournament);

                    dialog.add("<br>");

                    Link lRegisterMember = this.getAdminButton("is.idega.idegaweb.golf.presentation.TournamentMemberRegistration","Skr� kylfing");
                    dialog.add(lRegisterMember);

                    dialog.add("<br>");

                    Link lTournamentGroups = this.getAdminButton("is.idega.idegaweb.golf.presentation.TournamentGroups","M�tsh�par");
                    dialog.add(lTournamentGroups);

                    dialog.add("<br>");

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
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }


    public Link getAdminButton(Class adminClass){
        return getAdminButton(adminClass, adminClass.getName());
    }


    public Link getAdminButton(Class adminClass,String displayString){
        return getAdminButton(adminClass.getName(),displayString);
    }

    public Link getAdminButton(String adminClassName,String displayString){
        Link theReturn =  new Link(displayString,basePage);
        theReturn.addParameter(IWMainApplication.classToInstanciateParameter,IWMainApplication.getEncryptedClassName(adminClassName));
        return theReturn;
    }

}
