package is.idega.idegaweb.golf.presentation;

import com.idega.jmodule.login.business.AccessControl;
import com.idega.presentation.ui.*;
import is.idega.idegaweb.golf.moduleobject.GolfDialog;
import is.idega.idegaweb.golf.entity.*;
import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.util.idegaTimestamp;
import is.idega.idegaweb.golf.business.TournamentController;
//import com.idega.jmodule.ModuleEvent;
import java.sql.SQLException;
import java.io.IOException;
import com.idega.data.EntityFinder;

/**
*@author <a href="mailto:gimmi@idega.is">Gr�mur</a>
*@version 1.0
*/
 public class TournamentCreator extends TournamentAdmin {

      boolean bIsUpdate;
      String sTournamentIdToUpdate;

      SubmitButton Button1;
      SubmitButton Button2;
      SubmitButton modifyTournamentB1;
      Form modifyTournamentF1;
      SubmitButton modifyTournamentB2;
      DropdownMenu Dropdown1;
      DropdownMenu Dropdown2;
      SubmitButton Button3;
      SubmitButton Button4;
      SubmitButton Button5;
      SubmitButton Button6;
      SubmitButton daysButton;

      SubmitButton editTournament1;
      SubmitButton startingTimeB1;
      SubmitButton startingTimeB2;
      SubmitButton startingTimeB3;
      SubmitButton startingTimeB4;
      SubmitButton startingTimeB5;
      SubmitButton startingTimeB6;

      SubmitButton RegisterButton1;
      SubmitButton RegistermodifyTournamentB1;
      SubmitButton RegisterButton3;

      SubmitButton textFinished;

      Parameter entityPar;
      Parameter selectedTournament;

    public TournamentCreator(){
        super();
        System.out.println("TournamentCreator()");
    }


    public void main(IWContext iwc) throws Exception{
        System.out.println("TournamentCreator().main()");
        add("Kominn inn � TournamentCreator");

        checkIfUpdate(iwc);
	initializeButtons(iwc);
        checkAction(iwc);
    }

public void checkIfUpdate(IWContext iwc) {
        System.out.println("TournamentCreator().checkIfUpdate()");

        String sIsUpdate = iwc.getParameter("tournament_control_mode");

        bIsUpdate = false;
        boolean remove = false;

        if (sIsUpdate != null) {
            if (sIsUpdate.equals("edit")) {
              bIsUpdate = true;
              remove = false;
              String tournament_id = iwc.getParameter("tournament");

              if (tournament_id != null) {
                  sTournamentIdToUpdate = tournament_id;
                  iwc.setSessionAttribute("i_golf_tournament_update_id",tournament_id);
              }
            }
            else if (sIsUpdate.equals("create")) {
                remove = true;
            }
        }
        else {
            String temp_tournament_id = (String) iwc.getSessionAttribute("i_golf_tournament_update_id");
            if (temp_tournament_id != null) {
                sTournamentIdToUpdate = temp_tournament_id;
                bIsUpdate = true;
                remove = false;
            }
        }

        if (remove) {
                iwc.removeSessionAttribute("i_golf_tournament_update_id");
        }
}

public void initializeButtons(IWContext iwc){
        System.out.println("TournamentCreator().initializeButtons()");

        if (Button1 == null){
            System.out.println("TournamentCreator().initializeButtons().Button1==Null");

                /*
                Button1 = new SubmitButton(new Image("/pics/tournament/stofna.gif"),"b1");
                modifyTournamentF1 = new Form("modifytournament.jsp");
                modifyTournamentB1 = new SubmitButton(new Image("/pics/tournament/breyta.gif"),"mt1");
                modifyTournamentF1.add(modifyTournamentB1);
                RegisterButton1 = new SubmitButton(new Image("/pics/tournament/skra.gif"),"rb1");
                startingTimeB1 = new SubmitButton(new Image("/pics/tournament/stilla.gif"),"stb1");

                daysButton = new SubmitButton("dbu"
                ,"�fram");
                Button2 = new SubmitButton("bu2","�fram");
                Button3 = new SubmitButton("bu3","�fram");
                Button4 = new SubmitButton("bu4","�fram");
                Button5 = new SubmitButton("bu5","�fram");
                Button6 = new SubmitButton("bu6","Vista m�t");

                modifyTournamentB2= new SubmitButton("mt2","Breyta m�ti");

               RegistermodifyTournamentB1 = new SubmitButton("rt1","Finna");
               //RegisterButton3 = new SubmitButton("rb3","Skr� og uppf�ra grunnforgj�f");
               RegisterButton3 = new SubmitButton("rb3","Skr� kylfinga");

               startingTimeB2 = new SubmitButton("stb2","Vista r�st�ma");

               entityPar = new Parameter("idega_entity","");
               selectedTournament = new Parameter("action","selectedtournament");

               startingTimeB3 = new SubmitButton("stb3","");
               startingTimeB4 = new SubmitButton("stb4","");
               startingTimeB5 = new SubmitButton("stb5","");

               textFinished = new SubmitButton("stbtt7","�fram");
               */

                Button1 = new SubmitButton(new Image("/pics/tournament/stofna.gif"),"TournamentCreatorAction","b1");
                modifyTournamentF1 = new Form("modifytournament.jsp");
                modifyTournamentB1 = new SubmitButton(new Image("/pics/tournament/breyta.gif"),"TournamentCreatorAction","mt1");
                modifyTournamentF1.add(modifyTournamentB1);
                RegisterButton1 = new SubmitButton(new Image("/pics/tournament/skra.gif"),"TournamentCreatorAction","rb1");
                startingTimeB1 = new SubmitButton(new Image("/pics/tournament/stilla.gif"),"TournamentCreatorAction","stb1");

                daysButton = new SubmitButton("�fram","TournamentCreatorAction","dbu");
                Button2 = new SubmitButton("�fram","TournamentCreatorAction","bu2");
                Button3 = new SubmitButton("�fram","TournamentCreatorAction","bu3");
                Button4 = new SubmitButton("�fram","TournamentCreatorAction","bu4");
                Button5 = new SubmitButton("�fram","TournamentCreatorAction","bu5");
                Button6 = new SubmitButton("Vista m�t","TournamentCreatorAction","bu6");

                modifyTournamentB2= new SubmitButton("Breyta m�ti","TournamentCreatorAction","mt2");

               RegistermodifyTournamentB1 = new SubmitButton("Finna","TournamentCreatorAction","rt1");
               //RegisterButton3 = new SubmitButton("rb3","Skr� og uppf�ra grunnforgj�f");
               RegisterButton3 = new SubmitButton("Skr� kylfinga","TournamentCreatorAction","rb3");

               startingTimeB2 = new SubmitButton("Vista r�st�ma","TournamentCreatorAction","stb2");

               entityPar = new Parameter("idega_entity","");
               selectedTournament = new Parameter("action","selectedtournament");

               startingTimeB3 = new SubmitButton("","TournamentCreatorAction","stb3");
               startingTimeB4 = new SubmitButton("","TournamentCreatorAction","stb4");
               startingTimeB5 = new SubmitButton("","TournamentCreatorAction","stb5");

               textFinished = new SubmitButton("�fram","TournamentCreatorAction","stbtt7");

        }
}

public void checkAction(IWContext iwc) throws Exception{
    String action = iwc.getParameter("TournamentCreatorAction");
    add("<br>"+action);
    System.err.println("TournamentCreatorAction = "+action);


    String entityPar = iwc.getParameter("idega_entity");
    if (entityPar != null) {
        String thePar = (String) iwc.getSessionAttribute("group_tournament");
        if (thePar == null) { thePar = "";}
        if (thePar.equals("Y")){
                createTournament3(iwc);
        }
        else{
                createTournament5(iwc);
        }
    }
    else {
        if (action == null) {
            createTournament(iwc);
        }
        else if (action.equals("b1") ) {
            createTournament(iwc);
        }
        else if (action.equals("bu2")) {
            createTournament2(iwc);
        }
        else if (action.equals("bu3")) {
            createTournament4(iwc);
        }
        else if (action.equals("bu4")) {
            createTournament6(iwc);
        }
        else if (action.equals("bu5")) {
            createTournament6(iwc);
        }
        else if (action.equals("bu6")) {
            SaveTournament(iwc);
        }
        else if (action.equals("stbtt7")) {
            confirmSaveTournament(iwc);
        }
        else if (action.equals("dbu")) {
            typeInTournamentText(iwc);
        }
    }

}

/**
 * @todo: Check if the actionPerformed function is actually used
 */

/*
public void actionPerformed(ModuleEvent e)throws Exception{

        IWContext iwc = e.getIWContext();
	initializeButtons(iwc);
        checkIfUpdate(iwc);
//        add(""+bIsUpdate);

	//try{
		if(e.getSource().equals(Button1)){
			createTournament(iwc);
		}
		//else if(e.getSource().equals(modifyTournamentB1)){
		//	editTournament();
		//}
		else if(e.getSource().equals(Button2)){
			createTournament2(iwc);
		}
		//else if(e.getSource().equals(Dropdown2)){
		//	editTournament2();
		//}
		//else if(e.getSource().equals(editTournament1)){
		//	editTournament2();
		//}
		else if(e.getSource().equals(entityPar)){
			if (((String)iwc.getSessionAttribute("group_tournament")).equals("Y")){
				createTournament3(iwc);
			}
			else{
				createTournament5(iwc);
			}
		}
		else if(e.getSource().equals(Button3)){
			createTournament4(iwc);
		}
		else if(e.getSource().equals(Button4)){
			//if (((String)getSessionAttribute("group_tournament")).equals("Y")){
				createTournament6(iwc);
			//}
			//else{
			//	createTournament5(iwc);
			//}
		}
		else if(e.getSource().equals(Button5)){
                        createTournament6(iwc);

                        //setTournamentDays(iwc);

		}
		else if(e.getSource().equals(daysButton)){
                    typeInTournamentText(iwc);
		}
		else if(e.getSource().equals(textFinished)){
                    confirmSaveTournament(iwc);
		}
		else if(e.getSource().equals(Button6)){
			SaveTournament(iwc);
		}
		//else if (e.getSource().equals(RegisterButton1)){
		//	FindRegistrationMember();
		//}
		//else if (e.getSource().equals(RegistermodifyTournamentB1)){
		//	RegisterMember();
		//}
		//else if (e.getSource().equals(RegisterButton3)){
		//	SaveRegistration();
		//}
		//else if (e.getSource().equals(selectedTournament)){
		//	tournamentInfo();
		//}
		//else if (e.getSource().equals(startingTimeB1)){
		//	setupStartingtime();
		}
		//else{
                //        createTournament(iwc);
		//}
	//}
	//catch(SQLException ex){
	//	throw new IOException(ex.getMessage());
	//	//ex.printStackTrace();
	//	//throw (IOException) ex.fillInStackTrace();
	//}
}
*/

public void createTournament(IWContext iwc)throws SQLException{
	GolfDialog dialog1;

        String sSelectedTournamentType = "-1";
        String sSelectedTournamentForm = "-1";
        boolean bSelectedTournamentUseGroups = false;
        boolean bSelectedTournamentIsOpen = false;



        if (bIsUpdate) {
          dialog1 = new GolfDialog("Breyta m�ti");
          Tournament tour = new Tournament(Integer.parseInt(sTournamentIdToUpdate));
          dialog1.addMessage(tour.getName());

          sSelectedTournamentForm = Integer.toString(tour.getTournamentFormId());
          sSelectedTournamentType = Integer.toString(tour.getTournamentTypeId());
          bSelectedTournamentUseGroups = tour.getIfGroupTournament();
          bSelectedTournamentIsOpen = tour.getIfOpenTournament();
        }
        else {

          dialog1 = new GolfDialog("B�a til m�t");

        }
	add(dialog1);
        Form dialog = new Form();
           //addIfUpdate(iwc,dialog);
        dialog1.add(dialog);

	Table table = new Table(2,5);
	dialog.add(table);
	TournamentType type = new TournamentType();
	Dropdown1 = new DropdownMenu(type.getVisibleTournamentTypes());
            Dropdown1.setSelectedElement(sSelectedTournamentType);
	//Dropdown1.setToSubmit();

	TournamentForm form = new TournamentForm();
	DropdownMenu Dropdown2 = new DropdownMenu(form.findAll());
            Dropdown2.setSelectedElement(sSelectedTournamentForm);


        BooleanInput groupTournament = new BooleanInput("group_tournament");
            groupTournament.setSelected(bSelectedTournamentUseGroups);

        BooleanInput openTournament = new BooleanInput("open_tournament");
            openTournament.setSelected(bSelectedTournamentIsOpen);



	table.add(new Text("Keppnisform"),1,1);
	table.add(Dropdown1,2,1);

	table.add(new Text("Tegund"),1,2);
	table.add(Dropdown2,2,2);

	table.add(new Text("Flokkar"),1,3);
	table.add(groupTournament,2,3);

	table.add(new Text("Opi�"),1,4);
	table.add(openTournament,2,4);

	table.setAlignment(2,5,"right");
	table.add(Button2,2,5);
}

public void createTournament2(IWContext iwc)throws SQLException{
        Tournament tournament = null;
        if (this.bIsUpdate) {
            tournament = new Tournament(Integer.parseInt(sTournamentIdToUpdate));
            tournament.setTournamentTypeID(Integer.parseInt(iwc.getParameter("tournament_type")));
            tournament.setTournamentFormID(Integer.parseInt(iwc.getParameter("tournament_form")));
        }
        else {
            tournament = new Tournament();

            //TournamentType type = new TournamentType();
            //TournamentType type = new TournamentType(Integer.parseInt(getParameter("tournament_type")));
            //TournamentForm form = new TournamentForm(Integer.parseInt(getParameter("tournament_form")));

            //type.setID(Integer.parseInt(getIWContext().getRequest().getParameter("tournament_type")));
            //type.setID(1);

            //add(new Text(type.getName()));
            //tournament.setTournamentType(type);
            tournament.setTournamentTypeID(Integer.parseInt(iwc.getParameter("tournament_type")));
            tournament.setTournamentFormID(Integer.parseInt(iwc.getParameter("tournament_form")));

            //default settings
            tournament.setName("Nafn m�ts");
            tournament.setNumberOfRounds(2);
            tournament.setRegistrationFee(0);
            tournament.setNumberOfDays(1);
        }

            GolfDialog dialog1 = new GolfDialog("Skr��u inn uppl�singar fyrir m�ti�");
            add(dialog1);
            Form dialog = new Form();
              //addIfUpdate(iwc,dialog);
            dialog1.add(dialog);

            EntityInsert entityForm = new EntityInsert(tournament,iwc.getRequestURI()+"?idega_entity=true");
            entityForm.setFieldNotDisplayed("tournament_type_id");
            entityForm.setNotToInsert();
            entityForm.setButtonText("�fram");
            dialog.add(entityForm);

            if(AccessControl.isClubAdmin(iwc)){
              Member member = (Member)com.idega.jmodule.login.business.AccessControl.getMember(iwc);
              int union_id = member.getMainUnionID();
              Union union = new Union(union_id);
              tournament.setUnion(union);
              entityForm.setFieldNotDisplayed("union_id");
              entityForm.setColumnValueRange("field_id",union.getOwningFields());
            }

              iwc.setSessionAttribute("group_tournament",iwc.getParameter("group_tournament"));
            if(iwc.getParameter("group_tournament").equals("Y")){
                    //tournament.setVisible("registration_fee",false);
                    entityForm.setFieldNotDisplayed("registration_fee");
                    tournament.setGroupTournament(true);
            }
            else{
                    tournament.setGroupTournament(false);
            }

            if(iwc.getParameter("open_tournament").equals("Y")){
                    //tournament.setVisible("registration_fee",false);
                    entityForm.setFieldNotDisplayed("registration_fee");
                    tournament.setOpenTournament(true);
            }
            else{
                    tournament.setOpenTournament(false);
            }

            //tournament.setVisible("open_tournament",false);
            //tournament.setVisible("group_tournament",false);
            entityForm.setFieldNotDisplayed("tournament_form_id");
            entityForm.setFieldNotDisplayed("open_tournament");
            entityForm.setFieldNotDisplayed("group_tournament");

            /*tournament.setVisible("open_tournament",true);
            tournament.setVisible("group_tournament",true);
            tournament.setVisible("registration_fee",true);
            tournament.setVisible("registration_fee",true);*/
}





public void createTournament3(IWContext iwc)throws SQLException{
	TournamentGroup group = new TournamentGroup();

        GolfDialog dialog1 = new GolfDialog("Veldu flokka sem eiga a� ver�a me�");
	add(dialog1);
        Form dialog = new Form();
        dialog1.add(dialog);
        //addIfUpdate(iwc,dialog);
        SelectionBox  box = new SelectionBox("tournament_group");
        if(AccessControl.isClubAdmin(iwc)){
          Member member = (Member)com.idega.jmodule.login.business.AccessControl.getMember(iwc);
          int union_id = member.getMainUnionID();
          Union union = new Union(union_id);
          box.addMenuElements(union.getTournamentGroupsRecursive());

        }
        else if(AccessControl.isAdmin(iwc)){
          box.addMenuElements(EntityFinder.findAll(group));
        }

        if (bIsUpdate) {
            Tournament temp_tournament = new Tournament(Integer.parseInt(sTournamentIdToUpdate));
            TournamentGroup[] tempTourGroup = temp_tournament.getTournamentGroups();
            for (int i = 0; i < tempTourGroup.length; i++) {
                box.setSelectedElement(Integer.toString(tempTourGroup[i].getID()));
            }
        }

        box.setHeight(10);
	dialog.add(box);
	//Button3= new SubmitButton("bu3","�fram");
	dialog.add(Button3);
}


public void createTournament4(IWContext iwc) throws SQLException{

	String[] stringArr = iwc.getParameterValues("tournament_group");
	if (stringArr == null){
		add(new Text("�� verdur a� velja einhverja flokka"));
		createTournament3(iwc);
	}
	else{
		TournamentGroup[] groupArr = (TournamentGroup[]) (new TournamentGroup()).constructArray(stringArr);
		iwc.setSessionAttribute("tournament_group",groupArr);


        GolfDialog dialog1 = new GolfDialog("Skilgreindu keppnisgj�ld fyrir hvern flokk fyrir sig");
	add(dialog1);
        Form form = new Form();
        dialog1.add(form);


		for(int i = 0; i < groupArr.length;i++){
			form.add(new Text(groupArr[i].getName()));
                        IntegerInput input = null;
                        if (bIsUpdate) {
                            input = new IntegerInput("group_fee",groupArr[i].getRegistrationFee(Integer.parseInt(sTournamentIdToUpdate)));
                        }
                        else {
			    input =new IntegerInput("group_fee",0);
                        }
			input.setLength(5);
			form.add(input);
			form.addBreak();
		}



		//Button4= new SubmitButton("bu4","�fram");
		form.add(Button4);
	}
}

public void createTournament5(IWContext iwc) throws SQLException{

        TeeColor[] color = (TeeColor[]) new TeeColor().findAll();;
	SelectionBox  box = new SelectionBox(color);

        if (this.bIsUpdate) {
            Tournament tempTour = new Tournament(Integer.parseInt(this.sTournamentIdToUpdate));
            TeeColor[] selected_color =  tempTour.getTeeColors();
            for (int i = 0; i < selected_color.length; i++) {
                box.setSelectedElement(Integer.toString(selected_color[i].getID())) ;
            }

        }

        GolfDialog dialog1 = new GolfDialog("Veldu teiga sem eiga a� fylgja m�ti");
	add(dialog1);
        Form dialog = new Form();
        dialog1.add(dialog);


        box.setHeight(8);
	dialog.add(box);
	//Button5= new SubmitButton("bu5","�fram");
	dialog.add(Button5);

}

public void createTournament6(IWContext iwc) throws Exception{
	//Tournament tournament = (Tournament) getSession().getAttribute("idega_entity");
	//Enumeration enum = getRequest().getParameterNames();

	if (((String)iwc.getSessionAttribute("group_tournament")).equals("Y")){

			String[] stringArr = iwc.getParameterValues("group_fee");
			iwc.setSessionAttribute("group_fee",stringArr);

                        setTournamentDays(iwc);



	}
	else{
		String[] stringArr = iwc.getParameterValues("tee_color");
		if (stringArr == null){
			add(new Text("�� ver�ur ad velja einhverja teiga"));
			createTournament5(iwc);
		}
		else{

			TeeColor[] teecolorArr = (TeeColor[])(new TeeColor()).constructArray(stringArr);
			iwc.setSessionAttribute("tee_color",teecolorArr);

                        setTournamentDays(iwc);

		}
	}
}


public void setTournamentDays(IWContext iwc)throws Exception{
  Tournament tournament = (Tournament) iwc.getSessionAttribute("idega_entity");
  int numberOfDays=tournament.getNumberOfDays();
  if(numberOfDays>1){
      GolfDialog dialog1 = new GolfDialog("Skilgreindu m�tsdaga");
      add(dialog1);
      Form form = new Form();
      dialog1.add(form);
      idegaTimestamp stamp = new idegaTimestamp(tournament.getStartTime());
      for(int i=0;i<numberOfDays;i++){
          DateInput input = new DateInput("tournament_day");
          if(i!=0){
            stamp.addDays(1);
          }
          input.setYear(stamp.getYear());
          input.setMonth(stamp.getMonth());
          input.setDay(stamp.getDate());
         form.add("Dagur "+i+1+":");
         form.add(input);
         form.addBreak();
      }
      form.add(daysButton);
  }
  else{
      TournamentDay day = new TournamentDay();
      idegaTimestamp stamp = new idegaTimestamp(tournament.getStartTime());

        day.setDate(stamp.getSQLDate());
        //day.setTournament(tournament);
        //day.insert();
        iwc.setSessionAttribute("idega_tournament_day",day);
      typeInTournamentText(iwc);
  }
}

public void typeInTournamentText(IWContext iwc) throws Exception{
    Tournament tournament = (Tournament) iwc.getSessionAttribute("idega_entity");

    String[] tournamentDays = iwc.getParameterValues("tournament_day");
    if(tournamentDays!=null){
      for(int i=0;i<tournamentDays.length;i++){
        idegaTimestamp stamp = new idegaTimestamp(tournamentDays[i]);
        TournamentDay day = new TournamentDay();
        day.setDate(stamp.getSQLDate());
        //day.setTournament(tournament);
        //day.insert();
        iwc.setSessionAttribute("idega_tournament_day"+i,day);
      }
    }

    GolfDialog dialog1 = new GolfDialog("Skr��u inn texta sem n�nari umfj�llun um m�ti�");
    add(dialog1);
    Form form = new Form();
    TextArea area = new TextArea("extra_text");
    if (bIsUpdate) {
        area.setContent(tournament.getExtraText());
    }
    area.setWidth(45);
    area.setHeight(15);
    area.setWrap(true);
    form.add(area);
    dialog1.add(form);
    form.addBreak();
    form.add(textFinished);

}


public void confirmSaveTournament(IWContext iwc) throws Exception{
    Tournament tournament = (Tournament) iwc.getSessionAttribute("idega_entity");
    String extra_text = iwc.getParameter("extra_text");

    if(extra_text!=null){
      if (!extra_text.equalsIgnoreCase("")){
        tournament.setExtraText(extra_text);
      }
    }

    GolfDialog dialog1 = new GolfDialog("Lokaskref");
    add(dialog1);

    Form form = new Form();
    dialog1.add(form);
    form.add(Button6);
}


public void SaveTournament(IWContext iwc) throws SQLException,IOException{

	Tournament tournament = (Tournament) iwc.getSessionAttribute("idega_entity");
        if (bIsUpdate) {
            tournament.update();

            TournamentDay[] tempTournamentDays = tournament.getTournamentDays();
            for (int i = 0; i < tempTournamentDays.length; i++) {
                tempTournamentDays[i].delete();
            }

            TournamentGroup[] tempTournamentGroup = tournament.getTournamentGroups();
            for (int i = 0; i < tempTournamentGroup.length; i++) {
                tempTournamentGroup[i].removeFrom(tournament);
            }


            TeeColor[] tempTeeColor = tournament.getTeeColors();
            for (int i = 0; i < tempTeeColor.length; i++) {
                tempTeeColor[i].removeFrom(tournament);
            }

        }
        else {
    	    tournament.insert();
        }

        TournamentDay[] tournamentDays = tournament.getTournamentDays();

        TournamentDay day = (TournamentDay) iwc.getSessionAttribute("idega_tournament_day");
        iwc.removeSessionAttribute("idega_tournament_day");

        if(day==null){
          int i = 0;
          day=(TournamentDay) iwc.getSessionAttribute("idega_tournament_day"+i);
          while (day!=null){
            iwc.removeSessionAttribute("idega_tournament_day"+i);
            day.setTournament(tournament);
            day.insert();
            i++;
            day=(TournamentDay) iwc.getSessionAttribute("idega_tournament_day"+i);
          }
        }
        else{
            day.setTournament(tournament);
            day.insert();
        }





	if (((String)iwc.getSessionAttribute("group_tournament")).equals("Y")){
		TournamentGroup[] group = (TournamentGroup[]) iwc.getSessionAttribute("tournament_group");
		String[] group_fee = (String[]) iwc.getSessionAttribute("group_fee");
		for (int i = 0 ; i < group.length;i++){
			group[i].addTo(tournament,"registration_fee",group_fee[i]);
			try{
                          group[i].getTeeColor().addTo(tournament);
                        }
                        catch(Exception ex){
                          //NOCATCH
                        }
		}
	}
	else{
		TeeColor[] color = (TeeColor[]) iwc.getSessionAttribute("tee_color");
		for (int i = 0; i< color.length;i++){
			color[i].addTo(tournament);
		}
	}

        TournamentController.removeTournamentTableApplicationAttribute(iwc);
	GolfDialog dialog = new GolfDialog("Lokaskref");
	add(dialog);
	dialog.add(new Text("M�t vista�!"));

	//sends again to begin
	//getResponse().sendRedirect(getRequest().getRequestURI());
        getWindow().setParentToReload();
        getWindow().close();
}


public void selectTournament(String controlParameter)throws SQLException{

	GolfDialog dialog = new GolfDialog("Veldu m�t");
	add(dialog);
	dialog.add(new DropdownMenu((new Tournament()).findAll()));

	if (controlParameter.equals("startingtime")){
		dialog.add(startingTimeB1);
	}
}


/**
 * UNFINISHED
 */
public boolean isInEditMode(IWContext iwc){
/*
  String controlParameter="tournament_control_mode";
  String mode = iwc.getParameter(controlParameter);
  if(mode!=null){
    iwc.setSessionAttribute(controlParameter,mode);
    if(mode.equals("edit"))
      return true;
    return false;
  }
  else{
    mode = (String)iwc.getSessionAttribute(controlParameter);
    if(mode!=null){
      if(mode.equals("edit"))
        return true;
      return false;
    }
    return false;
  }
*/
    return bIsUpdate;
}

/**
 * UNFINISHED???
 */
public Tournament getTournament(IWContext iwc)throws Exception{
  String tournament_par = iwc.getParameter("tournament");
  Tournament tournament=null;
  if(tournament_par==null){
    tournament = (Tournament)iwc.getSessionAttribute("tournament_admin_tournament");
  }
  else{
    tournament = new Tournament(Integer.parseInt(tournament_par));
    iwc.setSessionAttribute("tournament_admin_tournament",tournament);
  }
  return tournament;
}



}// class TournamentCreator


