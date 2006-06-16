package is.idega.idegaweb.golf.tournament.presentation;

import is.idega.idegaweb.golf.access.AccessControl;
import is.idega.idegaweb.golf.entity.Field;
import is.idega.idegaweb.golf.entity.Member;
import is.idega.idegaweb.golf.entity.MemberBMPBean;
import is.idega.idegaweb.golf.entity.MemberHome;
import is.idega.idegaweb.golf.entity.MemberInfo;
import is.idega.idegaweb.golf.entity.MemberInfoHome;
import is.idega.idegaweb.golf.entity.Scorecard;
import is.idega.idegaweb.golf.entity.StartingtimeView;
import is.idega.idegaweb.golf.entity.Tee;
import is.idega.idegaweb.golf.entity.TeeHome;
import is.idega.idegaweb.golf.entity.Tournament;
import is.idega.idegaweb.golf.entity.TournamentGroup;
import is.idega.idegaweb.golf.entity.TournamentGroupHome;
import is.idega.idegaweb.golf.entity.TournamentHome;
import is.idega.idegaweb.golf.entity.TournamentRound;
import is.idega.idegaweb.golf.entity.TournamentRoundHome;
import is.idega.idegaweb.golf.entity.Union;
import is.idega.idegaweb.golf.entity.UnionMemberInfo;
import is.idega.idegaweb.golf.handicap.business.Handicap;
import is.idega.idegaweb.golf.presentation.GolfBlock;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Break;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Paragraph;
import com.idega.presentation.text.Strong;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.Label;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.util.IWTimestamp;
import com.idega.util.text.TextSoap;

/**
 * @author gimmi
 */
public class RegistrationForMembers extends GolfBlock {

	public static final String PRM_TOURNAMENT_ID = "tournament_id";
	public static final String PRM_ACTION = "action";
	public static final String VAL_ACTION_OPEN = "open";
	private final static String PRM_TEETIME_GROUP_NUMBER = "teeGrNum";
	private final static String SUFFIX_TENTH_TEE = "_";

  CloseButton closeButton = new CloseButton();


  public void main(IWContext modinfo) throws Exception {
  	IWResourceBundle iwrb = getResourceBundle();
      String tournament_id = modinfo.getParameter(PRM_TOURNAMENT_ID);

      if (tournament_id != null)  {
          setTournament(modinfo, ((TournamentHome) IDOLookup.getHomeLegacy(Tournament.class)).findByPrimaryKey(Integer.parseInt(tournament_id)));
      }

      Tournament tournament = getTournament(modinfo);
      Member member = (is.idega.idegaweb.golf.entity.Member) AccessControl.getMember(modinfo);


      if (member != null) {
          if (tournament != null) {

              if (!getTournamentBusiness(modinfo).isMemberRegisteredInTournament(tournament,member) ) {

                  String action = modinfo.getParameter(PRM_ACTION);

                  if (action == null) {
                  	if(modinfo.isClientHandheld() || IWConstants.MARKUP_LANGUAGE_WML.endsWith(modinfo.getMarkupLanguage())){
                  		Paragraph p = new Paragraph();
                  		p.add(getLocalizedMessage("tournament.error_occurred","Error occurred"));
                  		add(p);
                  		
                  	}else{
                  	  add(getLocalizedMessage("tournament.error_occurred","Error occurred"));
                  	  add(Text.getBreak());
                       add(getButton(new CloseButton()));
                  	}
                  }else if (action.equalsIgnoreCase("open")) {
                      register(modinfo, iwrb);
                  }else if (action.equals("directRegistrationMembersChosen")) {
                      finalizeDirectRegistration(modinfo,iwrb);
                      if(modinfo.isClientHandheld() || IWConstants.MARKUP_LANGUAGE_WML.equals(modinfo.getMarkupLanguage())){
                      	directRegistrationConfirmMessageWML(modinfo);
                      } else {
                      	getDirectRegistrationTable(modinfo,iwrb);
                      }
                  }
              }
              else {
              	if(modinfo.isClientHandheld() || IWConstants.MARKUP_LANGUAGE_WML.endsWith(modinfo.getMarkupLanguage())){
              		Paragraph p = new Paragraph();
              		p.add(getMessageText(member.getName() +" "+ localize("tournament.is_registered_in_the_tournament_named","is registered in the tournament named") +" \""+tournament.getName()+"\" "));
              		add(p);
              	}else{
                  add("<center>");
                  add(getMessageText(member.getName() +" "+ localize("tournament.is_registered_in_the_tournament_named","is registered in the tournament named") +" \""+tournament.getName()+"\" "));
                  add("</center>");
              	}
              }
          }else {
          	if(modinfo.isClientHandheld() || IWConstants.MARKUP_LANGUAGE_WML.endsWith(modinfo.getMarkupLanguage())){
          		Paragraph p = new Paragraph();
          		p.add(getLocalizedMessage("tournament.no_tournament_selected","No tournament selected"));
	             add(p);
          		
          	}else{
              add(getLocalizedMessage("tournament.no_tournament_selected","No tournament selected"));
              add(Text.getBreak());
              add(closeButton);
              }
          }
      }
      else {
      	
      	if(modinfo.isClientHandheld() || IWConstants.MARKUP_LANGUAGE_WML.equals(modinfo.getMarkupLanguage())){
      		Paragraph p = new Paragraph();
      		p.add(getLocalizedMessage("tournament.you_have_to_register_to_the_system_using_login_and_password","You have to register to the system using login and password"));
             add(p);
      		
      	}else{
          add("<center>");
          add(Text.getBreak());
          add(getLocalizedMessage("tournament.you_have_to_register_to_the_system_using_login_and_password","You have to register to the system using login and password"));
          add(Text.getBreak());
          add(Text.getBreak());
          add(closeButton);
          add("</center>");
      	}
      }

  }


  public Tournament getTournament(IWContext modinfo) {
      return (Tournament) modinfo.getSessionAttribute("tournament_registrationForMembers");
  }

  public void setTournament(IWContext modinfo, Tournament tournament) {
      modinfo.setSessionAttribute("tournament_registrationForMembers",tournament);
  }

  public void register(IWContext modinfo, IWResourceBundle iwrb) throws RemoteException, SQLException {
      Tournament tournament = getTournament(modinfo);
      Member member = (is.idega.idegaweb.golf.entity.Member) AccessControl.getMember(modinfo);

      boolean isMemberValid = true;
      if (tournament.getIsClosed()) {
    	  	try {
    	  		isMemberValid = false;
			Union mUnion = member.getMainUnion();
			Union tUnion = tournament.getUnion();
			
			if (mUnion != null && mUnion.equals(tUnion)) {
				isMemberValid = true;
			}
			
		} catch (Exception e) {
		}
      }
      
      if (!isMemberValid) {
        	add(Text.getBreak());
            add("<center>");
            add(getLocalizedMessage("tournament.you_cannot_register_not_in_the_club","You can not register to this tournament, you are not a member of the club."));
            add(Text.getBreak());
            add(Text.getBreak());
            add(closeButton);
            add("</center>");
      } else if (getTournament(modinfo).isDirectRegistration()) {
          String subAction = modinfo.getParameter("sub_action");
          if (subAction == null) {
            try {
            		if(modinfo.isClientHandheld() || IWConstants.MARKUP_LANGUAGE_WML.equals(modinfo.getMarkupLanguage())){
            			if (modinfo.getParameter(PRM_TEETIME_GROUP_NUMBER) != null) {
            				getAvailableGroups(modinfo);
                     }else {
                     		getDirectRegistrationTableWML(modinfo,iwrb);
                     }
              	} else {
              		getDirectRegistrationTable(modinfo,iwrb);
              	}
            }
            catch (Exception e) {
              e.printStackTrace(System.err);
            }
          }else if (subAction.equals("saveDirectRegistration")) {
              if (!getTournamentBusiness(modinfo).isMemberRegisteredInTournament(tournament,member) ) {
                  saveDirectRegistration(modinfo,iwrb);
              }
          }
      }else {
          if (!getTournamentBusiness(modinfo).isMemberRegisteredInTournament(tournament,member) ) {
              String subAction = modinfo.getParameter("subAction");
              if (subAction == null) {
              	if(modinfo.isClientHandheld() || IWConstants.MARKUP_LANGUAGE_WML.equals(modinfo.getMarkupLanguage())){
              		notOnlineRegistrationWML(modinfo);
              	} else {
              		notOnlineRegistration(modinfo);
              	}
              }else if (subAction.equals("yes")) {
                  String tournament_group_id = modinfo.getParameter("tournament_group");
                  if (tournament_group_id == null) {
                      getAvailableGroups(modinfo);
                  }else {
                      performRegistrationNotOnline(modinfo,tournament_group_id);
                  }

              }
          }
          else {
          	if(modinfo.isClientHandheld() || IWConstants.MARKUP_LANGUAGE_WML.equals(modinfo.getMarkupLanguage())){
          		Paragraph p = new Paragraph();
          		p.add(getLocalizedMessage("tournament.you_are_already_registered_to_this_tournament","You are already registered to this tournament"));
	             add(p);
          		
          	}else{
	          	add(Text.getBreak());
	              add("<center>");
	              add(getLocalizedMessage("tournament.you_are_already_registered_to_this_tournament","You are already registered to this tournament"));
	              add(Text.getBreak());
	              add(Text.getBreak());
	              add(closeButton);
	              add("</center>");
          	}
          }

      }
  }


  public void notOnlineRegistration(IWContext modinfo) {
      Member member = (is.idega.idegaweb.golf.entity.Member) AccessControl.getMember(modinfo);

      Table table = new Table();
          table.setBorder(0);
          table.setAlignment("center");

      Form yesForm  = new Form();
          yesForm.maintainParameter(PRM_ACTION);

          yesForm.add(new HiddenInput("subAction","yes"));
          SubmitButton yes = new SubmitButton(localize("tournament.yes","Yes"));
          yesForm.add(yes);

      Form noForm  = new Form();
          noForm.maintainParameter(PRM_ACTION);
          noForm.add(new HiddenInput("subAction","no"));
          CloseButton no = new CloseButton(localize("tournament.no","no"));
          noForm.add(no);


      table.mergeCells(1,1,2,1);
      table.setAlignment(1,1,"center");
      table.setAlignment(1,3,"center");
      table.setAlignment(2,3,"center");
      table.add(getText(localize("tournament.register","Register")+" \""+member.getName()+"\" "+localize("tournament.to_the_tournament_named","to the tournament named")+" \""+getTournament(modinfo).getName() +"\"?"));
      table.add(yesForm,1,3);
      table.add(noForm,2,3);

      add(Text.getBreak());
      add(table);
  }
  
  public void notOnlineRegistrationWML(IWContext modinfo) throws SQLException {
    Member member = AccessControl.getMember(modinfo);
    Tournament tournament = getTournament(modinfo);

    Text golfer = new Text(localize("tournament.golfer", "Golfer")+ ": " + member.getName());
	Text tournamentName = new Text(localize("tournament.Tournament", "Tournament")+ ": " + tournament.getName());
	Text tournamentClub = new Text(localize("tournament.Club", "Club")+ ": " + tournament.getUnion().getAbbrevation());
	Text tournamentField = new Text(localize("tournament.Field", "Field")+ ": " + tournament.getField().getName());
	Text trounamentStartDate = new Text(localize("tournament.Startdate", "Start date")+ ": " + new IWTimestamp(tournament.getStartTime()).getLocaleDate(modinfo.getCurrentLocale(),IWTimestamp.SHORT));
	Text confirmation = new Text(localize("tournament.registered_in","You have been registered in tournament"));
    
	Paragraph p = new Paragraph();
	
	p.add(golfer);
	p.add(new Break());
	p.add(tournamentName);
	p.add(new Break());
	p.add(tournamentClub);
	p.add(new Break());
	p.add(tournamentField);
	p.add(new Break());
	p.add(trounamentStartDate);
	add(p);

    Paragraph p1 = new Paragraph();
    Strong s1 = new Strong();
    s1.add(new Text(localize("tournament.register","Register")+" \""+member.getName()+"\" "+localize("tournament.to_the_tournament_named","to the tournament named")+" \""+getTournament(modinfo).getName() +"\"?"));
    p1.add(s1);
    add(p1);
    
    
    Link yes = new Link(localize("tournament.Forward","Forward >"));
    yes.maintainParameter(PRM_ACTION,modinfo);
    yes.addParameter("subAction","yes");
    


    Paragraph p2 = new Paragraph();
    Strong s = new Strong();
    s.add(yes);
    p2.add(s);

    add(p2);
}

  public void getAvailableGroups(IWContext modinfo) throws SQLException, RemoteException{
      Member member = (is.idega.idegaweb.golf.entity.Member) AccessControl.getMember(modinfo);

      Tournament tournament = getTournament(modinfo);

      TournamentGroup[] tGroups = tournament.getTournamentGroups();
      List groups = getTournamentBusiness(modinfo).getTournamentGroups(member,tournament);
      

      
      if (tGroups.length != 0) {
          if (groups.size() != 0)  {
          	if(modinfo.isClientHandheld() || IWConstants.MARKUP_LANGUAGE_WML.equals(modinfo.getMarkupLanguage())){
          		
          		Text golfer = new Text(localize("tournament.golfer", "Golfer")+ ": " + AccessControl.getMember(modinfo).getName());
          		Text tournamentName = new Text(localize("tournament.Tournament", "Tournament")+ ": " + tournament.getName());
          		Text tournamentClub = new Text(localize("tournament.Club", "Club")+ ": " + tournament.getUnion().getAbbrevation());
          		Text tournamentField = new Text(localize("tournament.Field", "Field")+ ": " + tournament.getField().getName());
          		Text trounamentStartDate = new Text(localize("tournament.Startdate", "Start date")+ ": " + new IWTimestamp(tournament.getStartTime()).getLocaleDate(modinfo.getCurrentLocale(),IWTimestamp.SHORT));
          		
          		
          		Paragraph p = new Paragraph();
          		
          		p.add(golfer);
          		p.add(new Break());
          		p.add(tournamentName);
          		p.add(new Break());
          		p.add(tournamentClub);
          		p.add(new Break());
          		p.add(tournamentField);
          		p.add(new Break());
          		p.add(trounamentStartDate);

          		
          		
          		
          		Form form = new Form();
          		    form.maintainParameter("subAction");
          		    String gr = modinfo.getParameter(PRM_TEETIME_GROUP_NUMBER);
          		    if(gr!=null){
	                		form.maintainParameter(PRM_TEETIME_GROUP_NUMBER);
	                		form.addParameter("action","directRegistrationMembersChosen");
	                		String tournament_round = modinfo.getParameter("tournament_round");

	                		form.addParameter("member_id", AccessControl.getMember(modinfo).getPrimaryKey().toString());
//	                		form.addParameter("tournament_group",);
	                		int index = gr.indexOf(SUFFIX_TENTH_TEE);
	                		if(index!=-1){
	                			form.addParameter("starting_time",gr.substring(0,index-1));
		                		form.addParameter("starting_tee",10);
		                		
		                		TournamentRound tRound = tournament.getTournamentRounds()[0];
	                  		Text tournamentTeetime = new Text(localize("tournament.Teetime", "Teetime")+ ": " + getRoundTimeFromGrupNum(Integer.parseInt(gr.substring(0,index-1)), tournament, tRound));
	                  		Text tournamentTee = new Text(localize("tournament.Tee", "Tee")+ ": " +"10");
	                  		
	                  		p.add(new Break());
	                  		p.add(tournamentTeetime);
	                  		p.add(new Break());
	                  		p.add(tournamentTee);
		                		
	                		}else{
	                			form.addParameter("starting_time",gr);
		                		form.addParameter("starting_tee",1);	
		                		
		                		TournamentRound tRound = tournament.getTournamentRounds()[0];
	                  		Text tournamentTeetime = new Text(localize("tournament.Teetime", "Teetime")+ ": " + getRoundTimeFromGrupNum(Integer.parseInt(gr), tournament, tRound));
	                  		Text tournamentTee = new Text(localize("tournament.Tee", "Tee")+ ": " +"1");
	                  		
	                  		p.add(new Break());
	                  		p.add(tournamentTeetime);
	                  		p.add(new Break());
	                  		p.add(tournamentTee);
	                		}
	                		
	                		form.maintainParameter("tournament_round");
	                		
	                }else{
	                	 	form.maintainParameter("action");
	                }
	
	            DropdownMenu groupsMenu = new DropdownMenu(groups);
	
	            Label l = new Label(localize("tournament.choose_group_to_play_in","Choose group to play in"),groupsMenu);
	            form.add(l);
	            form.add(groupsMenu);
	
	            SubmitButton afram = new SubmitButton(localize("trounament.register","Register"));
	            form.add(afram);
	            
	            add(p);
	            add(form);
          	} else {
          	    Form form = new Form();
	                form.maintainParameter("action");
	                form.maintainParameter("subAction");
	            Table table = new Table();
	                table.setBorder(0);
	                table.setAlignment("center");
	
	            DropdownMenu groupsMenu = new DropdownMenu(groups);
	
	            table.add(getLocalizedText("tournament.choose_group_to_play_in","Choose group to play in"));
	            table.mergeCells(1,1,2,1);
	            table.add(member.getName(),1,2);
	            table.add(groupsMenu,2,2);
	
	            SubmitButton afram = getTournamentBusiness(modinfo).getAheadButton(modinfo,"","");
	            table.setAlignment(2,3,"right");
	            table.add(afram,2,3);
	
	
	            add(Text.getBreak());
	            form.add(table);
	            add(form);

          	}
              

          }else {
          	if(modinfo.isClientHandheld() || IWConstants.MARKUP_LANGUAGE_WML.equals(modinfo.getMarkupLanguage())){
          		Paragraph p = new Paragraph();
          		p.add(getLocalizedMessage("tournament.you_do_not_have_permission_to_register","You do not have permission to register"));
	             p.add(new Break());
	             p.add(getLocalizedMessage("tournament.contact_the_club","Contact the club"));
	             add(p);
          		
          	}else{
	          	 add(Text.getBreak());
	              add("<center>");
	              add(getLocalizedMessage("tournament.you_do_not_have_permission_to_register","You do not have permission to register"));
	              add(Text.getBreak());
	              add(getLocalizedMessage("tournament.contact_the_club","Contact the club"));
	              add(Text.getBreak());
	              add(Text.getBreak());
	              add(closeButton);
	              add("</center>");
          	}
            }
      }
      else {
          incorrectSetup(modinfo);
      }

  }


  public void performRegistrationNotOnline(IWContext modinfo,String tournament_group_id) throws RemoteException, SQLException{
      Member member = (is.idega.idegaweb.golf.entity.Member) AccessControl.getMember(modinfo);
      Tournament tournament = getTournament(modinfo);
      getTournamentBusiness(modinfo).registerMember(member,tournament,tournament_group_id);
      
      if(modinfo.isClientHandheld() || IWConstants.MARKUP_LANGUAGE_WML.equals(modinfo.getMarkupLanguage())){
        
        Text golfer = new Text(localize("tournament.golfer", "Golfer")+ ": " + AccessControl.getMember(modinfo).getName());
	    	Text tournamentName = new Text(localize("tournament.Tournament", "Tournament")+ ": " + tournament.getName());
	    	Text tournamentClub = new Text(localize("tournament.Club", "Club")+ ": " + tournament.getUnion().getAbbrevation());
	    	Text tournamentField = new Text(localize("tournament.Field", "Field")+ ": " + tournament.getField().getName());
	    	Text trounamentStartDate = new Text(localize("tournament.Startdate", "Start date")+ ": " + new IWTimestamp(tournament.getStartTime()).getLocaleDate(modinfo.getCurrentLocale(),IWTimestamp.SHORT));
	    	Text confirmation = new Text(localize("tournament.registered_in","You have been registered in tournament"));
	    	
	    	
	    	Paragraph p = new Paragraph();
	    	
	    	p.add(golfer);
	    	p.add(new Break());
	    	p.add(tournamentName);
	    	p.add(new Break());
	    	p.add(tournamentClub);
	    	p.add(new Break());
	    	p.add(tournamentField);
	    	p.add(new Break());
	    	p.add(trounamentStartDate);
	        
	    	String tgroup = modinfo.getParameter("tournament_group");
	    	if(tgroup!=null){
	    		try {
	    			TournamentGroup tGroup = ((TournamentGroupHome) IDOLookup.getHomeLegacy(TournamentGroup.class)).findByPrimaryKey(Integer.parseInt(tgroup));
	    			Text trounamentGroup = new Text(localize("tournament.Tournament_group", "Tournament group")+ ": " + tGroup.getName());
	    			p.add(new Break());
	    			p.add(trounamentGroup);
	    		} catch (NumberFormatException e) {
	    			e.printStackTrace();
	    		} catch (FinderException e) {
	    			e.printStackTrace();
	    		}
	    	}
	    	
	    	Strong s = new Strong();
	    	s.add(confirmation);
	    	p.add(new Break());
	    	p.add(new Break());
	    	p.add(s);
	    	
	    	
	    	add(p);

  	  } else {
        add(getLocalizedText("tounament.registered_to_the_tournament","Registered to the tournament"));
        add(Text.getBreak());
        add(getButton(new CloseButton()));
  	  }

  }


  public void getDirectRegistrationTable(IWContext modinfo, IWResourceBundle iwrb) throws RemoteException, SQLException {
      Tournament tournament = getTournament(modinfo);
      String tournament_round_id = modinfo.getParameter("tournament_round");

      if (tournament_round_id == null) {
          TournamentRound[] tRounds = (TournamentRound[]) tournament.getTournamentRounds();
          if (tRounds.length > 0 ) {
              tournament_round_id = Integer.toString(tRounds[0].getID());
          }
      }

      if (tournament_round_id != null) {

      	  add(Text.getBreak());
          add("<center>");

          Table table = new Table();
            table.setWidth("90%");

            table.setAlignment(2,1,"left");
            table.setAlignment(2,2,"left");
            table.setAlignment(1,1,"right");
            table.setAlignment(1,2,"right");

            addHeading(localize("tournament.tournament_registration","Tournament registration"));
            table.add("1",1,1);
            
            table.add(getLocalizedText("tournament.choose_teetime_and_enter_ssn_in_the_textbox.__it_is_posible_to_register_more_than_one_at_a_time","Choose teetime and enter social security number.  It is posible to register more than one at a time."),2,1);

            table.add("2",1,2);
            table.add(getText(localize("tournament.press_the","Press the")+" \""+localize("tournament.save","Save")+"\" "+localize("tournament.button_located_at_the_bottom_of_the_page","button located at the bottom of the page.")),2,2);

          TournamentStartingtimeList form = getTournamentBusiness(modinfo).getStartingtimeTable(tournament,tournament_round_id,false,true,false,true);
          form.setSubmitButtonParameter("action", "open");

          add(table);
          add("<hr>");
          add(Text.getBreak());
          add(form);
          add("</center>");

      }
      else {
          incorrectSetup(modinfo);
      }


  }
  
  public void getDirectRegistrationTableWML(IWContext modinfo, IWResourceBundle iwrb) throws RemoteException, SQLException {
    Tournament tournament = getTournament(modinfo);

    TournamentRound[] tRounds = (TournamentRound[]) tournament.getTournamentRounds();

    if (tRounds.length > 0) {

//        TournamentStartingtimeList form = getTournamentBusiness(modinfo).getStartingtimeTable(tournament,tournament_round_id,false,true,false,true);
//        form.setSubmitButtonParameter("action", "open");
    	
    		Text golfer = new Text(localize("tournament.golfer", "Golfer")+ ": " + AccessControl.getMember(modinfo).getName());
    		Text tournamentName = new Text(localize("tournament.Tournament", "Tournament")+ ": " + tournament.getName());
    		Text tournamentClub = new Text(localize("tournament.Club", "Club")+ ": " + tournament.getUnion().getAbbrevation());
    		Text tournamentField = new Text(localize("tournament.Field", "Field")+ ": " + tournament.getField().getName());
    		Text trounamentStartDate = new Text(localize("tournament.Startdate", "Start date")+ ": " + new IWTimestamp(tournament.getStartTime()).getLocaleDate(modinfo.getCurrentLocale(),IWTimestamp.SHORT));
    		
    		Paragraph p = new Paragraph();
    		
    		p.add(golfer);
    		p.add(new Break());
    		p.add(tournamentName);
    		p.add(new Break());
    		p.add(tournamentClub);
    		p.add(new Break());
    		p.add(tournamentField);
    		p.add(new Break());
    		p.add(trounamentStartDate);
    		
    		add(p);
    		
        Form form = new Form();
        form.addParameter("action", "open");
        form.addParameter("tournament_round",tRounds[0].getPrimaryKey().toString());
        
        DropdownMenu teetimes = getAvailableGrupNumsDropdownMenu(modinfo,PRM_TEETIME_GROUP_NUMBER,tournament,tRounds[0]);
        Label l = new Label(localize("tournament.choose_teetime","Choose teetime"),teetimes);
        SubmitButton button = new SubmitButton(localize("tournament.Forward","Forward >"));
        
        form.add(l);
        form.add(teetimes);
        form.add(button);
        add(form);
        
    }
    else {
        incorrectSetup(modinfo);
    }


}
  
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.idega.presentation.PresentationObject#main(com.idega.presentation.IWContext)
	 */
	public void getStartingtimeRegistrationForm(IWContext modinfo, Tournament tournament, String tournament_round_id) throws Exception {


		if (tournament != null ){
			Form form = new Form();
			
			
			Table topTable = new Table();
			Table table = new Table();
			Table borderTable = new Table();


			
			form.add(topTable);
			borderTable.add(table);
			form.add(borderTable);
			int row = 1;
			int numberOfMember = 0;


			TournamentRound[] tourRounds = tournament.getTournamentRounds();

			int tournamentRoundId = -1;
			if (tournament_round_id == null) {
				tournament_round_id = "-1";
				tournamentRoundId = tourRounds[0].getID();
			}
			else {
				tournamentRoundId = Integer.parseInt(tournament_round_id);
			}

			TournamentRound tournamentRound = null;
			try {
				tournamentRound = ((TournamentRoundHome) IDOLookup.getHomeLegacy(TournamentRound.class)).findByPrimaryKey(tournamentRoundId);
			}
			catch (FinderException fe) {
				throw new SQLException(fe.getMessage());
			}

			boolean display = false;
			if (tournamentRound.getVisibleStartingtimes()) {
				display = true;
			}
			int roundNumber = tournamentRound.getRoundNumber();

			IWTimestamp tourDay = null;

			DropdownMenu rounds = (DropdownMenu) getStyledInterface(new DropdownMenu("tournament_round"));

				tourDay = new IWTimestamp(tournamentRound.getRoundDate());
				rounds.addMenuElement(tournamentRound.getID(), getResourceBundle().getLocalizedString("tournament.round", "Round") + " " + tournamentRound.getRoundNumber() + " " + tourDay.getISLDate(".", true));

			Text timeText;
			Text dMemberSsn;
			Text dMemberName;
			Text dMemberHand;
			Text dMemberUnion;

			Text tim = new Text(getResourceBundle().getLocalizedString("tournament.time", "Time"));
			Text sc = new Text(getResourceBundle().getLocalizedString("tournament.social_security_number", "Social security number"));
			Text name = new Text(getResourceBundle().getLocalizedString("tournament.name", "Name"));
			Text club = new Text(getResourceBundle().getLocalizedString("tournament.club", "Club"));
			Text hc = new Text(getResourceBundle().getLocalizedString("tournament.handicap", "Handicap"));

			table.add(tim, 1, row);
			table.add(sc, 2, row);
			table.add(name, 3, row);
			table.add(club, 4, row);
			table.add(hc, 5, row);

	
			table.setRowStyleClass(row, getHeaderRowClass());

			java.text.DecimalFormat extraZero = new java.text.DecimalFormat("00");
			java.text.DecimalFormat handicapFormat = new java.text.DecimalFormat("###.0");
			Field field = tournament.getField();
			List members;
			CheckBox delete;

			Image removeImage = getBundle().getImage("/shared/tournament/de.gif", getResourceBundle().getLocalizedString("tournament.remove_from_tournament", "Remove from tournament"));
			removeImage.setToolTip(getResourceBundle().getLocalizedString("tournament.remove_from_tournament", "Remove from tournament"));

			Text tTime = new Text("");


			Link remove;
			Text tooMany = getSmallErrorText(getResourceBundle().getLocalizedString("tournament.no_room", "No room"));

			Union union;
			int union_id;
			String abbrevation = "'";

			boolean displayTee = false;
			if (tournamentRound.getStartingtees() > 1) {
				displayTee = true;
			}

			int groupCounterNum = 0;

			for (int y = 1; y <= tournamentRound.getStartingtees(); y++) {
				// HAR�K��UN DAU�ANS
				int tee_number = 1;
				if (y == 2) tee_number = 10;

				IWTimestamp startHour = new IWTimestamp(tournamentRound.getRoundDate());
				IWTimestamp endHour = new IWTimestamp(tournamentRound.getRoundEndDate());
				endHour.addMinutes(1);

				int minutesBetween = tournament.getInterval();
				int numberInGroup = tournament.getNumberInGroup();
				int groupCounter = 0;

				if (displayTee) {
					++row;
					Text startTee = new Text(getResourceBundle().getLocalizedString("tournament.starting_tee", "Starting tee") + " : " + tee_number);
					table.add(startTee, 1, row);
				}

				int startInGroup = 0;
				is.idega.idegaweb.golf.entity.Member tempMember;
				TextInput socialNumber;
				CheckBox paid;
				int zebraRow = 1;

				StartingtimeView[] sView;

				while (endHour.isLaterThan(startHour)) {
					++row;
					++groupCounter;
					++groupCounterNum;
					startInGroup = 0;

					timeText = (Text) tTime.clone();
					timeText.setText(Text.NON_BREAKING_SPACE + extraZero.format(startHour.getHour()) + ":" + extraZero.format(startHour.getMinute()) + Text.NON_BREAKING_SPACE);
					table.add(timeText, 1, row);

					sView = getTournamentBusiness(modinfo).getStartingtimeView(tournamentRound.getID(), "", "", "grup_num", groupCounter + "", tee_number, "");


					startInGroup = sView.length;

					String styleClass = null;
					for (int i = 0; i < sView.length; i++) {
						if (zebraRow % 2 != 0) {
							styleClass = getLightRowClass();
						}
						else {
							styleClass = getDarkRowClass();
						}
						zebraRow++;
						
						table.setHeight(row, 10);
						++numberOfMember;
						if (i != 0) table.add(tooMany, 1, row);

						if (display) {
							dMemberSsn = null;
							dMemberName = null;
							dMemberHand = null;
							dMemberUnion = null;
							if (sView[i].getMemberId() != 1) {
								dMemberSsn = new Text(sView[i].getSocialSecurityNumber());
								dMemberName = new Text(sView[i].getName());
								dMemberUnion = new Text(sView[i].getAbbrevation());
								dMemberHand = new Text(com.idega.util.text.TextSoap.singleDecimalFormat(sView[i].getHandicap()));
							}
							else {
								dMemberSsn = new Text("-");
								dMemberName = new Text(getResourceBundle().getLocalizedString("tournament.reserved", "Reserved"));
								dMemberUnion = new Text("-");
								dMemberHand = new Text("-");
							}

							table.add(dMemberSsn, 2, row);
							table.add(dMemberName, 3, row);
							table.add(dMemberUnion, 4, row);
							table.add(dMemberHand, 5, row);
						}
						else {
							table.mergeCells(2, row, 7, row);
							table.setStyleClass(2, row, styleClass);
						}
						row++;
					}

					for (int i = startInGroup; i < (numberInGroup); i++) {
						if (tee_number == 10) {
							socialNumber = (TextInput) getStyledInterface(new TextInput("social_security_number_for_group_" + groupCounter + "_"));
						}
						else {
							socialNumber = (TextInput) getStyledInterface(new TextInput("social_security_number_for_group_" + groupCounter));
						}
						socialNumber.setLength(15);
						socialNumber.setMaxlength(10);
						table.add(socialNumber, 2, row);

					}
					startHour.addMinutes(minutesBetween);
					--row;
				}
			}

			++row;

			Text many = getSmallHeader(getResourceBundle().getLocalizedString("tournament.number_of_participants", "Number of participants") + " : " + numberOfMember);
			table.add(many, 1, row);


			SubmitButton submitButton = (SubmitButton) getButton(new SubmitButton(getResourceBundle().getLocalizedString("tournament.save", "Save")));
//			if (submitButtonParameter != null) {
//				submitButton = (SubmitButton) getButton(new SubmitButton(getResourceBundle().getLocalizedString("tournament.save", "Save"), submitButtonParameter[0], submitButtonParameter[1]));
//			}
			table.add(new HiddenInput("sub_action", "saveDirectRegistration"), 4, row);
			table.add(submitButton, 4, row);
			table.add(new HiddenInput("number_of_groups", "" + groupCounterNum), 4, row);


			add(form);
		} else {
			logError("Tournament not found in session, or in parameter");
			
		}
	}



  public void incorrectSetup(IWContext modinfo) {
  	if(modinfo.isClientHandheld() || IWConstants.MARKUP_LANGUAGE_WML.equals(modinfo.getMarkupLanguage())){
  		Paragraph p = new Paragraph();
  		p.add(getLocalizedMessage("tournament.tournament_setup_is_not_right","Tournament setup is not right"));
  		p.add(Text.getBreak());
  		p.add(getLocalizedMessage("tournament.contact_the_club","Contact the club"));
        add(p);
  	} else {
  	  add(Text.getBreak());
  	  add("<center>");
      add(getLocalizedMessage("tournament.tournament_setup_is_not_right","Tournament setup is not right"));
      add(Text.getBreak());
      add(getLocalizedMessage("tournament.contact_the_club","Contact the club"));
      add(Text.getBreak());
      add(Text.getBreak());
      add(closeButton);
      add("</center>");
  	}
  }


  public void saveDirectRegistration(IWContext modinfo, IWResourceBundle iwrb) throws SQLException {
    Tournament tournament = getTournament(modinfo);

    boolean noOne = true;
    try {

        String sNumberOfGroups = modinfo.getParameter("number_of_groups");
        int iNumberOfGroups = Integer.parseInt(sNumberOfGroups);

        String sTournamentRoundId = modinfo.getParameter("tournament_round");
        int iTournamentRoundId = Integer.parseInt(sTournamentRoundId);

        String[] numbers;
        Member member = null;
        Table content = new Table();
            content.setWidth("85%");
            content.setAlignment("center");

        //TextInput correction;
        Form form = new Form();
            form.maintainParameter("tournament_round");
            form.add(new HiddenInput("action","directRegistrationMembersChosen"));
        Table table = new Table();
            table.setBorder(0);
            int tableRow = 1;
            table.setWidth(Table.HUNDRED_PERCENT);
            table.add(getLocalizedText("tournament.name","Name"),1,tableRow);
            table.add(getLocalizedText("tournament.group","Group"),3,tableRow);
            table.add(getLocalizedText("tournament.handicap","Handicap"),5,tableRow);

        Table other = new Table();
            other.setBorder(1);
            int otherRow = 1;
            other.setWidth(Table.HUNDRED_PERCENT);
            other.add(getLocalizedText("tournament.were_not_found","were not found"));

        Table done = new Table();
            done.setBorder(0);
            int doneRow = 1;
            done.add(getLocalizedText("tournament.are_already_registered","are already registered"));
        Table rejects = new Table();
            rejects.setBorder(0);
            int rejectsRow = 1;
            rejects.add(getLocalizedText("tournament.do_not_have_permission","do not have permission"));


        int[] errors = new int[4];
        TournamentGroup[] allGroupsInTournament = tournament.getTournamentGroups();
        DropdownMenu allGroups = new DropdownMenu(allGroupsInTournament,"extra_player_groups");
        TextInput derName;
        TextInput correction;
        boolean star = false;

        DropdownMenu memberGender = new DropdownMenu("extra_player_gender");
            memberGender.addMenuElement("M",iwrb.getLocalizedString("tournament.male","Male"));
            memberGender.addMenuElement("F",iwrb.getLocalizedString("tournament.female","Female"));

        for (int i = 1; i <= iNumberOfGroups ; i++) {

            numbers = (String[]) modinfo.getParameterValues("social_security_number_for_group_"+i);
            if (numbers != null) {

                for (int j = 0; j < numbers.length; j++) {
                    if (!numbers[j].equals("")) {
                        member =  (Member) MemberBMPBean.getMember(numbers[j]);

                        if (member == null) {
                            ++otherRow;
                            other.add(numbers[j],1,otherRow);
                        }
                        else {
                            errors = getTournamentBusiness(modinfo).isMemberAllowedToRegister(member,tournament);
                            if ( (errors[0] == 0) && (errors[1] == 0) && (errors[2] == 0) && (errors[3] == 0) ){
//                            if (canMemberRegister == 0) {
                                if (!getTournamentBusiness(modinfo).isMemberRegisteredInTournament(tournament, ((TournamentRoundHome) IDOLookup.getHomeLegacy(TournamentRound.class)).findByPrimaryKey(iTournamentRoundId),tournament.getNumberInGroup(),member) ) {
                                    List tGroups = getTournamentBusiness(modinfo).getTournamentGroups(member,tournament);
                                    if (tGroups != null) {
                                        ++tableRow;
                                        table.add(member.getName(),1,tableRow);
                                        table.add(new HiddenInput("member_id",""+member.getID()),1,tableRow);
                                        table.add(new HiddenInput("starting_time",""+i),1,tableRow);
                                        table.add(new HiddenInput("starting_tee","1"),1,tableRow);
                                        table.add(new DropdownMenu(tGroups),3,tableRow);
                                        if (member.getGender().equalsIgnoreCase("M")) {
                                            if (member.getHandicap() > tournament.getMaxHandicap() ) {
                                                table.add(tournament.getMaxHandicap()+" *("+member.getHandicap()+")",5,tableRow);
                                            }
                                            else {
                                                table.add(member.getHandicap()+"",5,tableRow);
                                            }
                                        }
                                        else {
                                            if (member.getHandicap() > tournament.getFemaleMaxHandicap() ) {
                                                table.add(tournament.getFemaleMaxHandicap()+" *("+member.getHandicap()+")",5,tableRow);
                                            }
                                            else {
                                                table.add(member.getHandicap()+"",5,tableRow);
                                            }
                                        }
                                        //correction = new TextInput("handicap_correction_"+member.getID());
                                        //    correction.setSize(3);
                                        //table.add(correction,7,tableRow);
                                    }
                                }
                                else {
                                        ++doneRow;
                                        done.add(member.getName(),1,doneRow);
                                }
                            }
                            else {
                                ++rejectsRow;
                                rejects.add(member.getName(),1,rejectsRow);
                            }
                        }
                    }
                }


            }
            numbers = (String[]) modinfo.getParameterValues("social_security_number_for_group_"+i+"_");
            if (numbers != null) {
                for (int j = 0; j < numbers.length; j++) {
                    if (!numbers[j].equals("")) {
                        member =  (Member) MemberBMPBean.getMember(numbers[j]);

                        if (member == null) {
                            ++otherRow;
                            other.add(numbers[j],1,otherRow);
                        }
                        else {
                            errors = getTournamentBusiness(modinfo).isMemberAllowedToRegister(member,tournament);

                            if ( (errors[0] == 0) && (errors[1] == 0) && (errors[2] == 0) && (errors[3] == 0) ){
                                if (!getTournamentBusiness(modinfo).isMemberRegisteredInTournament(tournament, ((TournamentRoundHome) IDOLookup.getHomeLegacy(TournamentRound.class)).findByPrimaryKey(iTournamentRoundId),tournament.getNumberInGroup(),member) ) {
                                    List tGroups = getTournamentBusiness(modinfo).getTournamentGroups(member,tournament);
                                    if (tGroups != null) {
                                        ++tableRow;
                                        table.add(member.getName(),1,tableRow);
                                        table.add(new HiddenInput("member_id",""+member.getID()),1,tableRow);
                                        table.add(new HiddenInput("starting_time",""+i),1,tableRow);
                                        table.add(new HiddenInput("starting_tee","10"),1,tableRow);
                                        table.add(new DropdownMenu(tGroups),3,tableRow);
                                        if (member.getGender().equalsIgnoreCase("M")) {
                                            if (member.getHandicap() > tournament.getMaxHandicap() ) {
                                                table.add(tournament.getMaxHandicap()+" *("+member.getHandicap()+")",5,tableRow);
//                                                star = true;
                                            }
                                            else {
                                                table.add(TextSoap.singleDecimalFormat(member.getHandicap())+"",5,tableRow);
                                            }
                                        }
                                        else {
                                            if (member.getHandicap() > tournament.getFemaleMaxHandicap() ) {
                                                table.add(tournament.getFemaleMaxHandicap()+" *("+member.getHandicap()+")",5,tableRow);
 //                                               star = true;
                                            }
                                            else {
                                                table.add(member.getHandicap()+"",5,tableRow);
                                            }
                                        }
//                                        correction = new TextInput("handicap_correction_"+member.getID());
//                                            correction.setSize(3);
//                                       table.add(correction,7,tableRow);
                                    }
                                }
                                else {
                                        ++doneRow;
                                        done.setAlignment(1,doneRow,"left");
                                        done.add(member.getName(),1,doneRow);
                                }
                            }
                            else {
                                ++rejectsRow;
                                rejects.setAlignment(1,rejectsRow,"left");
                                rejects.add(member.getName(),1,rejectsRow);
                            }
                        }
                    }
                }
            }
            
            /*
             * Group registration !!!  ( ONLY TEE 1 implemented ADD TEE 10 )!!!
             */
            boolean useGroups = tournament.getTournamentType().getUseGroups();
            if (useGroups && tournament.getNumberInTournamentGroup() > 1) {
            	String[] names = (String[]) modinfo.getParameterValues("groupname_for_group_"+i);
            	int membersPerTournamentGroup = tournament.getNumberInTournamentGroup();
            	int numInGrupNum = tournament.getNumberInGroup();
            	int numberOfTournamentGroups = numInGrupNum / membersPerTournamentGroup;
            	
            	for (int k = 0; k < numberOfTournamentGroups; k++) {
            		String groupName = names[k];
		            numbers = (String[]) modinfo.getParameterValues("social_security_number_for_group_"+i+"_"+k);
		            if (numbers != null) {
			            // Checking for valid entries
		            	boolean validNumbers = true;
		            	String defaultName = "";
		            	for (int j = 0; j < numbers.length; j++) {
		            		if (defaultName.equals("")) {
		            			defaultName = numbers[j].trim();
		            		}
		            		validNumbers &= !numbers[j].trim().equalsIgnoreCase("");
		            	}

		            	for (int j = 0; j < numbers.length && validNumbers; j++) {
		                    if (!numbers[j].equals("")) {
		                    	boolean isSSN = false;
		                    	try {
		                    		Integer.parseInt(numbers[j]);
		                    		isSSN = true;
		                    	} catch (Exception e) {
		                    		isSSN = false;
		                    	}
		                        member =  (Member) MemberBMPBean.getMember(numbers[j]);
		
		                        if (member == null && isSSN) {
		                            ++otherRow;
		                            other.add(numbers[j],1,otherRow);
		                            other.add(new HiddenInput("extra_player_social_security_number",numbers[j]),1,otherRow);
		                            other.add(new HiddenInput("extra_player_starting_tee","1"),1,otherRow);
		                            derName = new TextInput("extra_player_name");
		                            other.add(derName,3,otherRow);
		                            TextInput hand = new TextInput("extra_player_handicap");
		                              hand.setSize(3);
		                            try {
		                                Integer.parseInt(numbers[j].substring(0,6));
		                                Integer.parseInt(numbers[j].substring(9,10));
		                            }
		                            catch (NumberFormatException n) {
		                                derName.setContent(iwrb.getLocalizedString("tournament.ssn_is_wrong","Social security number is incorrect"));
		                            }
		                            if (numbers[j].length() < 10) {
		                                derName.setContent(iwrb.getLocalizedString("tournament.ssn_is_wrong","Social security number is incorrect"));
		                            }
		
		                            other.add(allGroups,5,otherRow);
		                            other.add(hand,7,otherRow);
		                            other.add(memberGender, 9,otherRow);
		                            other.add(new HiddenInput("extra_player_starting_time",""+i),1,otherRow);
		                            CheckBox box = new CheckBox("extra_player",""+ (otherRow-2));
		                            other.add(box,11,otherRow);
		
		                        }
		                        else {
		                        	boolean isRegistered = false;
		                        	if (member != null) {
		                        		isRegistered = getTournamentBusiness(modinfo).isMemberRegisteredInTournament(tournament, ((TournamentRoundHome) IDOLookup.getHomeLegacy(TournamentRound.class)).findByPrimaryKey(iTournamentRoundId),tournament.getNumberInGroup(),member);
		                        		errors = getTournamentBusiness(modinfo).isMemberAllowedToRegister(member,tournament);
		                        	} else {
		                        		errors = new int[]{0, 0, 0, 0};
		                        	}
		                            
		                            if ((groupName == null || groupName.trim().equals("")) && j==0) {
		                            	if (member != null) {
		                            		groupName = member.getName();
		                            	} else {
		                            		groupName = numbers[j];
		                            	}
		                            }
		                            if ( (errors[0] == 0) && (errors[1] == 0) && (errors[2] == 0) && (errors[3] == 0)){
		                                if (!isRegistered ) {
		                                    List tGroups = getTournamentBusiness(modinfo).getTournamentGroups(member,tournament);
		                                    if (tGroups != null) {
		                                        ++tableRow;
		                                        if (member != null) {
		                                        	table.add(groupName +" - "+member.getName(),1,tableRow);
		                                        } else {
			                                        table.add(groupName +" - "+numbers[j],1,tableRow);

		                                        }
		                                        table.add(new HiddenInput("group_name",groupName),1,tableRow);
		                                        if (member != null) {
		                                        	table.add(new HiddenInput("member_id",""+member.getID()),1,tableRow);
		                                        	table.add(new HiddenInput("member_name",""),1,tableRow);
		                                        } else {
		                                        	table.add(new HiddenInput("member_id","-1"),1,tableRow);
		                                        	table.add(new HiddenInput("member_name",numbers[j]),1,tableRow);
		                                        }
		                                        table.add(new HiddenInput("starting_time",""+i),1,tableRow);
		                                        table.add(new HiddenInput("starting_tee","1"),1,tableRow);
		                                        table.add(new DropdownMenu(tGroups),3,tableRow);
		                                        if (member != null) {
			                                        if (member.getGender().equalsIgnoreCase("M")) {
			                                            if (member.getHandicap() > tournament.getMaxHandicap() ) {
			                                                table.add(tournament.getMaxHandicap()+" *("+member.getHandicap()+")",5,tableRow);
			                                                star = true;
			                                            }
			                                            else {
			                                                table.add(TextSoap.singleDecimalFormat(member.getHandicap())+"",5,tableRow);
			                                            }
			                                        }
			                                        else {
			                                            if (member.getHandicap() > tournament.getFemaleMaxHandicap() ) {
			                                                table.add(tournament.getFemaleMaxHandicap()+" *("+member.getHandicap()+")",5,tableRow);
			                                                star = true;
			                                            }
			                                            else {
			                                                table.add(member.getHandicap()+"",5,tableRow);
			                                            }
			                                        }
		                                        }
		                                        correction = new TextInput("handicap_correction");
		                                            correction.setSize(3);
	                                            if (member == null) {
	                                            	correction.setContent("36");
	                                            }
		                                        table.add(correction,5,tableRow);
		                                    }
		                                }
		                                else {
		                                        ++doneRow;
		                                        done.setAlignment(1,doneRow,"left");
		                                        done.add(member.getName(),1,doneRow);
		                                }
		                            }
		                            else {
		                                ++rejectsRow;
		                                rejects.setAlignment(1,rejectsRow,"left");
		                                rejects.add(member.getName(),1,rejectsRow);
		                            }
		                        }
		                    }
		                }
		            	if (!validNumbers && !defaultName.equals("")) {
		            		++rejectsRow;
                            rejects.setAlignment(1,rejectsRow,"left");
                            if (groupName.equals("")) {
                            	rejects.add(defaultName,1,rejectsRow);
                            } else {
                            	rejects.add(groupName,1,rejectsRow);
                            }
                            rejects.add(getResourceBundle().getLocalizedString("too_few_golfers_in_group", "Too few golfers in the group"),2,rejectsRow);
		            	}
		            }
	            }
            }
            /*
             * Group Registration DONE
             */


        }

        if (tableRow > 1) {
                Table instructionTable = new Table();
                  instructionTable.setBorder(0);
                  instructionTable.setWidth("85%");
                  instructionTable.setAlignment("center");
                  instructionTable.mergeCells(1,1,3,1);
                  instructionTable.setAlignment(1,1,"left");
                  instructionTable.setAlignment(2,2,"left");
                  instructionTable.setAlignment(2,3,"left");
                  instructionTable.setAlignment(1,2,"right");
                  instructionTable.setAlignment(1,3,"right");

                  instructionTable.add(getSmallHeader(localize("tournament.tournament_registration","Tournament registration")));
                  instructionTable.add("3",1,2);
                  instructionTable.add(getLocalizedText("tournaemnt.if_you_fit_in_more_than_one_group_then_choose_group","If you fit in more than one group, then choose group."),3,2);

                  instructionTable.add("4",1,3);
                  instructionTable.add(getText(localize("tournament.press_the","Press the")+" \""+localize("trounament.continue","continue")+"\" "+localize("tournament.button_and_the_registration_is_finished","button and the registration is finished.")),3,3);

                  instructionTable.add("5",1,4);
                  instructionTable.setVerticalAlignment(1,4,"top");
                  instructionTable.add(getText(localize("tournament.if_player_has_higher_handicap_than_the_max_handicap_for_the_tournament_then_his_handicap_is_visible_within_parenthesis_after_his_gamehandicap","if_player_has_higher_handicap_than_the_max_handicap_for_the_tournament_then_his_handicap_is_visible_within_parenthesis_after_his_gamehandicap")),3,4);

                  instructionTable.add("6",1,5);
                  instructionTable.add(getMessageText(localize("tournament.check_registration_in_teetime_table","Check registration in teetime table.")),3,5);
                  add(Text.getBreak());
              add(instructionTable);
                add("<hr>");
        }

        if (tableRow > 1) {
            noOne = false;
            form.add(table);
            content.add(form);
            content.addBreak();
        }

        if (otherRow > 1) {
            if (noOne) {
                content.add(form);
            }
            noOne = false;
            form.add(other);
            content.addBreak();
        }

        Table buttonTable = new Table(1,1);
            buttonTable.setAlignment(1,1,"right");
            buttonTable.setWidth(Table.HUNDRED_PERCENT);
            buttonTable.add(getTournamentBusiness(modinfo).getAheadButton(modinfo,"",""));
        form.add(buttonTable);


        if (doneRow > 1) {
            noOne = false;
            content.add(done);
            content.addBreak();
        }
        if (rejectsRow > 1) {
            noOne = false;
            content.add(rejects);
        }
        if (!noOne) {
            add(content);
        }

        ++tableRow;

        table.setAlignment(3,tableRow,"right");




//        add(TournamentController.getBackLink());

    }
    catch (Exception e) {
        e.printStackTrace(System.err);
    }
    try {
        if (noOne) {
            this.getDirectRegistrationTable(modinfo,iwrb);
        }
    }
    catch (Exception ex) {
        ex.printStackTrace(System.err);
    }
}

public void finalizeDirectRegistration(IWContext modinfo, IWResourceBundle iwrb) throws RemoteException, SQLException {
    String tournament_round = modinfo.getParameter("tournament_round");

    String[] member_ids = modinfo.getParameterValues("member_id");
    String[] tournament_groups = modinfo.getParameterValues("tournament_group");
    String[] starting_time = modinfo.getParameterValues("starting_time");
    String[] starting_tee = modinfo.getParameterValues("starting_tee");
    String[] groupNames = modinfo.getParameterValues("group_name");
    String sTournamentRoundId = modinfo.getParameter("tournament_round");
    String handicapCorrection;

    Member member;
    TournamentGroup tGroup;
    Tournament tournament = getTournament(modinfo);
    
    javax.transaction.TransactionManager tm = com.idega.transaction.IdegaTransactionManager.getInstance();
    int fieldID = tournament.getFieldId();
    int fieldPar = tournament.getField().getFieldPar();
    Handicap handicap = new Handicap(-1);
    if (member_ids != null) {

    	if (tournament.getTournamentType().getUseGroups() && tournament.getNumberInTournamentGroup() > 1) {
    		int numInGroup = tournament.getNumberInTournamentGroup();
    		String[] corrections = modinfo.getParameterValues("handicap_correction");
    		String[] memberNames = modinfo.getParameterValues("member_name");
    		
    		MemberHome mHome = (MemberHome) IDOLookup.getHome(Member.class);

    		HashMap groups = new HashMap();
    		HashMap groupMembers = new HashMap();
    		int groupCounter = 0;
    		for (int i = 0; i < member_ids.length; i++) {
                tm = com.idega.transaction.IdegaTransactionManager.getInstance();
    			try {
    				tm.begin();
        			String groupName = groupNames[i];
    				Member group = (Member) groups.get(groupName);
    				if (group == null) {
    					// Create the group
						group  = mHome.create();
						group.setFirstName(groupName);
						group.setSocialSecurityNumber("0000000000");
						group.setGender("m");
						group.store();
						
		                UnionMemberInfo uMInfo = (UnionMemberInfo) IDOLookup.createLegacy(UnionMemberInfo.class);
	                    uMInfo.setUnionID(1);
	                    uMInfo.setMemberID(group.getID() );
	                    uMInfo.setMembershipType("main");
	                    uMInfo.setMemberStatus("A");
	                    uMInfo.insert();

	                    groups.put(groupName, group);
    				}
    				Member[] mems = (Member[]) groupMembers.get(group);
    				if (mems == null) {
    					mems = new Member[numInGroup];
    					groupCounter = 0;
    					groupMembers.put(group, mems);
    				}
    				
        			if (member_ids[i].equals("-1")) {
        				// Create the member ...
						member = mHome.create();
						member.setFirstName(memberNames[i]);
						member.setSocialSecurityNumber("0000000001");
						member.store();
        			} else {
	                    member = ((MemberHome) IDOLookup.getHomeLegacy(Member.class)).findByPrimaryKey(Integer.parseInt(member_ids[i]));
        			}
        			mems[groupCounter++] = member;
        			
					handicapCorrection = corrections[i];
                    if (handicapCorrection != null) {
                        if (!handicapCorrection.equalsIgnoreCase("")) {
                            correctHandicap(modinfo,member,handicapCorrection);
                        }
                    }
					
					if (groupCounter == numInGroup) {
						String ids = "";
						float totalHand = 0;

	                    tGroup = ((TournamentGroupHome) IDOLookup.getHomeLegacy(TournamentGroup.class)).findByPrimaryKey(Integer.parseInt(tournament_groups[i]));
	                    int teeColorId = getTournamentBusiness(modinfo).getTeeColorIdForTournamentGroup(tournament, tGroup);
	                    if (teeColorId < 0) {
	                    	teeColorId = tGroup.getTeeColorID();
	                    }
						Tee tee = ((TeeHome) IDOLookup.getHomeLegacy(Tee.class)).findByFieldAndTeeColorAndHoleNumber(fieldID, teeColorId, 1);
						
						
						for (int kk=0; kk<numInGroup; kk++) {
							if (kk != 0) {
								ids+= ",";
							}
							float hc = mems[kk].getHandicap();
							if (hc > tournament.getMaxHandicap()) {
								hc = tournament.getMaxHandicap();
							}
							int leikhandi = handicap.getLeikHandicap((double)tee.getSlope(), (double) tee.getCourseRating(), (double) fieldPar, hc);
							if (leikhandi > tournament.getMaxHandicap()) {
								leikhandi = (int) tournament.getMaxHandicap();
							}
							totalHand += leikhandi;
							ids += Integer.toString(mems[kk].getID());
							
						}
						
						switch (numInGroup) {
							case 2:
								totalHand = totalHand / (float) 5;
								break;
							case 3:
								totalHand = totalHand / (float) 7.5;
								break;
							case 4:
								totalHand = totalHand / (float) 10;
								break;
						}

						BigDecimal bd = new BigDecimal(totalHand);
						int leikhandi = bd.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();

						correctHandicap(modinfo, group, Integer.toString(leikhandi));
						
						group.addMetaData("group_members", ids);
						group.store();
						// Group is full, time to finalize	
	                    
	                    getTournamentBusiness(modinfo).registerMember(group,tournament,tournament_groups[i]);
                		getTournamentBusiness(modinfo).setupStartingtime(modinfo, group,tournament,Integer.parseInt(sTournamentRoundId),Integer.parseInt(starting_time[i]));
					}
					
					tm.commit();
                }
                catch (Exception ex) {
                    ex.printStackTrace(System.err);
                    try {
	                      tm.rollback();
	                    }catch (javax.transaction.SystemException se) {se.printStackTrace(System.err);}
                }

    		}
    		
    		
    	} else {
	    	for (int i = 0; i < member_ids.length; i++) {
	            try {
	                tm.begin();
	                member = ((MemberHome) IDOLookup.getHomeLegacy(Member.class)).findByPrimaryKey(Integer.parseInt(member_ids[i]));
	
	                tGroup = ((TournamentGroupHome) IDOLookup.getHomeLegacy(TournamentGroup.class)).findByPrimaryKey(Integer.parseInt(tournament_groups[i]));
	
	                getTournamentBusiness(modinfo).registerMember(member,tournament,tournament_groups[i]);
	                if (starting_tee[i].equals("10")) {
	                	getTournamentBusiness(modinfo).setupStartingtime(modinfo, member,tournament,Integer.parseInt(sTournamentRoundId),Integer.parseInt(starting_time[i]),10);
	                }else {
	                	getTournamentBusiness(modinfo).setupStartingtime(modinfo, member,tournament,Integer.parseInt(sTournamentRoundId),Integer.parseInt(starting_time[i]));
	                }
	                tm.commit();
	            }
	            catch (Exception ex) {
	                ex.printStackTrace(System.err);
	                try {
	                  tm.rollback();
	                }catch (javax.transaction.SystemException se) {
	                  se.printStackTrace(System.err);
	                }
	            }
	        }
	    }
    }

}


public void correctHandicap(IWContext modinfo,Member member ,String handicapString) {

    try {
          float handicap = 100;

          if ( handicapString != null && handicapString.length() > 0 ) {
            if ( handicapString.indexOf(",") != -1 ) {
              handicapString = handicapString.replace(',','.');
            }
            handicap = Float.parseFloat(handicapString);
          }

          MemberInfo[] infos = (MemberInfo[]) ((MemberInfo) IDOLookup.instanciateEntity(MemberInfo.class)).findAllByColumnEquals("member_id",member.getID()+"");
          MemberInfo memberInfo;
          if (infos.length > 0) {
              try {
              	memberInfo = ((MemberInfoHome) IDOLookup.getHomeLegacy(MemberInfo.class)).findByPrimaryKey(member.getID());
              }
              catch (FinderException fe) {
              	throw new SQLException(fe.getMessage());
              }
              memberInfo.setHandicap(handicap);
              memberInfo.update();
          }
          else {
              memberInfo = (MemberInfo) IDOLookup.createLegacy(MemberInfo.class);
              memberInfo.setMemberId(member.getID());
              memberInfo.setHandicap(handicap);
              memberInfo.setFirstHandicap(handicap);
              memberInfo.insert();
          }

          Tournament tournament = getTournament(modinfo);
          IWTimestamp stampur = new IWTimestamp(tournament.getStartTime());
            stampur.addDays(-1);


          Scorecard scoreCard = (Scorecard) IDOLookup.createLegacy(Scorecard.class);
            scoreCard.setMemberId(member.getID());
            scoreCard.setTournamentRoundId(1);
            scoreCard.setScorecardDate(stampur.getTimestamp());
            scoreCard.setTotalPoints(0);
            scoreCard.setHandicapBefore(memberInfo.getHandicap());
            scoreCard.setHandicapAfter(handicap);
            scoreCard.setSlope(0);
            scoreCard.setCourseRating(0);
            scoreCard.setTeeColorID(0);
            scoreCard.setFieldID(0);
            scoreCard.setHandicapCorrection(true);
            scoreCard.insert();


    }
    catch (SQLException sq ) {
        sq.printStackTrace(System.err);
    }
}

private String getRoundTimeFromGrupNum(int num, Tournament tournament, TournamentRound tRound) {
	int interval = tournament.getInterval();
	IWTimestamp start = new IWTimestamp(tRound.getRoundDate());
	start.addMinutes((num-1)*interval);
	java.text.DecimalFormat extraZero = new java.text.DecimalFormat("00");
	return extraZero.format(start.getHour()) + ":" + extraZero.format(start.getMinute());
}

public DropdownMenu getAvailableGrupNumsDropdownMenu(IWContext iwc, String dropdownName, Tournament tournament, TournamentRound tRound) throws SQLException, RemoteException {
	DropdownMenu menu = new DropdownMenu(dropdownName);

	int interval = tournament.getInterval();
	int grupNum = 0;
	IWTimestamp start = new IWTimestamp(tRound.getRoundDate());
	start.addMinutes(-interval);
	IWTimestamp end = new IWTimestamp(tRound.getRoundEndDate());
	java.text.DecimalFormat extraZero = new java.text.DecimalFormat("00");
//	menu.addMenuElement(0, "");

	boolean displayTee = false;
	if (tRound.getStartingtees() > 1) {
		displayTee = true;
	}
	
	int totalMinutes = IWTimestamp.getMinutesBetween(start,end);
	int numberOfGroups = totalMinutes/interval+1;
	
	boolean[] isFull_1 = getTournamentBusiness(iwc).getIfTeetimeGroupsAreFull(tournament,tRound,numberOfGroups,1);
	boolean[] isFull_10 = null;
	
	if(displayTee){
		isFull_10=getTournamentBusiness(iwc).getIfTeetimeGroupsAreFull(tournament,tRound,numberOfGroups,10);
	}

	while (end.isLaterThan(start)) {
		++grupNum;
		start.addMinutes(interval);
		
		if (displayTee) {
			if(!isFull_1[grupNum-1]){
				menu.addMenuElement(grupNum, extraZero.format(start.getHour()) + ":" + extraZero.format(start.getMinute()) + "&nbsp;&nbsp; ("+localize("tournament.tee","Tee")+" 1)");
			}
			if(!isFull_10[grupNum-1]){
				menu.addMenuElement(grupNum + SUFFIX_TENTH_TEE, extraZero.format(start.getHour()) + ":" + extraZero.format(start.getMinute()) + "&nbsp;&nbsp;  ("+localize("tournament.tee","Tee")+" 10)");
			}
		}
		else {
			if(!isFull_1[grupNum-1]){
				menu.addMenuElement(grupNum, extraZero.format(start.getHour()) + ":" + extraZero.format(start.getMinute()));
			}
		}
	}

	return menu;
}

public void directRegistrationConfirmMessageWML(IWContext iwc) throws SQLException{
    
    Tournament tournament = getTournament(iwc);
    
    Text golfer = new Text(localize("tournament.golfer", "Golfer")+ ": " + AccessControl.getMember(iwc).getName());
	Text tournamentName = new Text(localize("tournament.Tournament", "Tournament")+ ": " + tournament.getName());
	Text tournamentClub = new Text(localize("tournament.Club", "Club")+ ": " + tournament.getUnion().getAbbrevation());
	Text tournamentField = new Text(localize("tournament.Field", "Field")+ ": " + tournament.getField().getName());
	Text trounamentStartDate = new Text(localize("tournament.Startdate", "Start date")+ ": " + new IWTimestamp(tournament.getStartTime()).getLocaleDate(iwc.getCurrentLocale(),IWTimestamp.SHORT));
	Text confirmation = new Text(localize("tournament.registered_in","You have been registered in tournament"));
	
	
	Paragraph p = new Paragraph();
	
	p.add(golfer);
	p.add(new Break());
	p.add(tournamentName);
	p.add(new Break());
	p.add(tournamentClub);
	p.add(new Break());
	p.add(tournamentField);
	p.add(new Break());
	p.add(trounamentStartDate);
    
	String tgroup = iwc.getParameter("tournament_group");
	if(tgroup!=null){
		try {
			TournamentGroup tGroup = ((TournamentGroupHome) IDOLookup.getHomeLegacy(TournamentGroup.class)).findByPrimaryKey(Integer.parseInt(tgroup));
			Text trounamentGroup = new Text(localize("tournament.Tournament_group", "Tournament group")+ ": " + tGroup.getName());
			p.add(new Break());
			p.add(trounamentGroup);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}
	}
	
	
	String teetime = iwc.getParameter("starting_time");
	if(teetime!=null){
		TournamentRound tRound = tournament.getTournamentRounds()[0];
  		Text tournamentTeetime = new Text(localize("tournament.Teetime", "Teetime")+ ": " + getRoundTimeFromGrupNum(Integer.parseInt(teetime), tournament, tRound));
  		Text tournamentTee = new Text(localize("tournament.Tee", "Tee")+ ": " +iwc.getParameter("starting_tee"));
  		
  		p.add(new Break());
  		p.add(tournamentTeetime);
  		p.add(new Break());
  		p.add(tournamentTee);
  		
	}
	Strong s = new Strong();
	s.add(confirmation);
	p.add(new Break());
	p.add(new Break());
	p.add(s);
	
	
	add(p);
    

}

}
