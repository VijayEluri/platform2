package com.idega.projects.golf.startingtime.presentation;

import com.idega.jmodule.object.ModuleObjectContainer;
import com.idega.projects.golf.service.StartService;
import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.interfaceobject.TextInput;
import com.idega.jmodule.object.interfaceobject.Form;
import com.idega.jmodule.object.interfaceobject.DropdownMenu;
import com.idega.jmodule.object.interfaceobject.SubmitButton;
import com.idega.jmodule.object.interfaceobject.HiddenInput;
import com.idega.jmodule.object.Image;
import com.idega.projects.golf.GolfField;
import com.idega.util.idegaTimestamp;
import com.idega.projects.golf.entity.TournamentDay;
import com.idega.projects.golf.entity.Tournament;
import com.idega.projects.golf.entity.TournamentRound;
import com.idega.data.EntityFinder;
import com.idega.jmodule.object.textObject.Text;
import com.idega.jmodule.object.textObject.Link;
import com.idega.projects.golf.entity.Union;
import com.idega.projects.golf.entity.Member;
import com.idega.jmodule.object.interfaceobject.CloseButton;
import com.idega.jmodule.object.interfaceobject.BackButton;
import com.idega.projects.golf.entity.StartingtimeFieldConfig;
import com.idega.projects.golf.business.GolfCacher;
import com.idega.projects.golf.templates.page.JmoduleWindowModuleWindow;

import java.sql.SQLException;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

/**
 * Title:        Golf
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class RegisterTime extends JmoduleWindowModuleWindow {

  private StartService business;
  private DropdownMenu unionDropdown;
  private Form myForm = null;
  private Table frameTable;
  private int maxCountInGroups = 4;
  private int maxPerOwnerPerDay = 8;

  private idegaTimestamp currentDay;
  private String currentField;
  private String currentUnion;
  private String currentMember;
  private StartingtimeFieldConfig fieldInfo;
  private Text templText;


  public RegisterTime() {
    super();
    myForm = new Form();
    frameTable = new Table();
    frameTable.setAlignment("center");
    frameTable.setWidth("100%");
    myForm.add(frameTable);
    this.add(myForm);
    business = new StartService();
    unionDropdown = (DropdownMenu)GolfCacher.getUnionAbbreviationDropdown("club").clone();
    templText = new Text("");
    templText.setFontSize(1);
  }



    public TextInput insertEditBox(String name, Form myForm)
    {
            TextInput myInput = new TextInput(name);
            myInput.setParentObject(myForm);
            myInput.setMaxlength(10);
            return myInput;
    }

    public TextInput insertEditBox(String name, String text)
    {
            TextInput myInput = new TextInput(name);
            myInput.setAsNotEmpty();
            myInput.setContent(text);
            return myInput;
    }

    public TextInput insertEditBox(String name, String text, int size)
    {
            TextInput myInput = new TextInput(name);
            myInput.setSize(size);
            myInput.setAsNotEmpty();
            myInput.setContent(text);
            return myInput;
    }


    public DropdownMenu insertUnionDropdown(String name, String text, int size) throws SQLException{
      DropdownMenu mydropdown = (DropdownMenu)unionDropdown.clone();

      mydropdown.setSelectedElement(text);
      mydropdown.keepStatusOnAction();
      return mydropdown;
    }


    public TextInput insertEditBox(String name, int size)
    {
            TextInput myInput = new TextInput(name);
            myInput.setSize(size);
            return myInput;
    }

    private SubmitButton insertButton(String btnName, String Action, String Method, Form theForm)
    {
            SubmitButton mySubmit = new SubmitButton(btnName);
            theForm.setMethod(Method);
            theForm.setAction(Action);
            return mySubmit;
    }

    private SubmitButton insertButton(Image image, String imageName, String Action, String Method, Form theForm)
    {
            SubmitButton mySubmit = new SubmitButton(image, imageName);
            theForm.setMethod(Method);
            theForm.setAction(Action);
            return mySubmit;
    }

    private SubmitButton insertButton(String btnName, String Action, String Method, String onSub, Form theForm)
    {
            SubmitButton mySubmit = new SubmitButton(btnName);
            mySubmit.setOnSubmit(onSub);
            theForm.setMethod(Method);
            theForm.setAction(Action);
            return mySubmit;
    }

    public void lineUpTable(int skraMarga, ModuleInfo modinfo)throws IOException
    {

            String btnSkraUrl = "/pics/formtakks/boka.gif";
            String btnCancelUrl = "/pics/formtakks/cancel.gif";

            int memberId = -1;
            boolean memberAvailable = false;
            //f� member id fyrir member til a� finna hann og setja inn � textinputi� fyrir hann
            if(modinfo.getSession().getAttribute("member_id") != null){
              memberId = Integer.parseInt((String)modinfo.getSession().getAttribute("member_id"));
              memberAvailable = true;
            }

            String lines[] = new String[skraMarga];
            int groupNums[] = new int[skraMarga];

            try
            {
              Member member = null;
              if(memberId != -1)
                member = new Member(memberId);
              String FieldID = currentField;
              String Date = modinfo.getSession().getAttribute("date").toString();
              String MemberId = modinfo.getSession().getAttribute("member_id").toString();
              GolfField myGolfField = getFieldInfo( Integer.parseInt(FieldID), Date);
              int Line = Integer.parseInt( modinfo.getParameter("line"));
              int max =business.countEntriesInGroup(Line,this.currentField,this.currentDay);

              for(int j = 0; j < skraMarga ; j++){
                if(max > 3){
                  while(max > 3){
                    Line++;
                    max = business.countEntriesInGroup(Line,this.currentField,this.currentDay);
                  }
                }
                max++;
                lines[j] = getTime(Line, myGolfField);
                groupNums[j] = Line;

              }

              Table myTable =  new Table(6, skraMarga+3);
              myTable.setCellpadding(0);
              myTable.setCellspacing(0);
              myTable.setWidth(2, "40");
              myTable.setHeight(1,"30");


              myTable.addText("<b>T�mi</b>", 2, 1);
              myTable.addText("<b>Kennitala</b>", 3, 1);
              myTable.addText("<b>S�rkort</b>", 5, 1);
              myTable.addText("<b>Kortan�mer</b>", 6, 1);

              myTable.setColumnAlignment(1,"center");
              myTable.setColumnAlignment(5,"center");
              myTable.setColumnAlignment(6,"center");



              boolean admin = false;
              boolean clubadmin = false;
              boolean clubworker = false;
              String unionAbbrevation = null;

              if(memberAvailable){
                admin = com.idega.jmodule.login.business.AccessControl.isAdmin(modinfo);
                clubadmin = com.idega.jmodule.login.business.AccessControl.isClubAdmin(modinfo);
                clubworker = com.idega.jmodule.login.business.AccessControl.isClubWorker(modinfo);
                unionAbbrevation = member.getMainUnion().getAbbrevation();
              }

              int i = 1;
              for ( ;i < skraMarga+1 ; i++)
              {
                  myTable.setWidth(1, "25");
                  myTable.addText("<b>"+lines[i-1]+"</b>", 2, i+1);
                  myTable.add(new HiddenInput("group_num",Integer.toString(groupNums[i-1])),2, i+1);
                  myTable.setAlignment(2, i+1, "left");

                  if(i == 1 && memberAvailable){
                    myTable.add(insertEditBox("secure_num", member.getSocialSecurityNumber()), 3, i+1);
                  }else{
                    myTable.add(insertEditBox("secure_num", myForm), 3, i+1);
                  }

                  myTable.add(insertEditBox("card", 4), 5, i+1);
                  myTable.add(insertEditBox("cardNo", 12), 6, i+1);
              }

              myTable.setColumnAlignment(5,"center");
              myTable.setColumnAlignment(6,"center");

              //setPlayers(modinfo);

              myTable.mergeCells(4, i+2, 6, i+2);
              myTable.add(insertButton(new Image(btnSkraUrl),"", modinfo.getRequestURI(), "post", myForm), 4, i+2);
              myTable.add(new CloseButton(new Image(btnCancelUrl)), 4, i+2);
              myTable.setAlignment(4, i+2, "right");
              frameTable.empty();
              frameTable.add(myTable);

            }
            catch (SQLException E) {
                    E.printStackTrace();
            }
            catch (IOException E) {
                    E.printStackTrace();
            }
    }

    public void handleFormInfo(ModuleInfo modinfo)throws SQLException, IOException {

      Vector illegal = new Vector(0);
      int k = 0;
      frameTable.empty();
      frameTable.setAlignment(1,1,"center");
        if( modinfo.getParameter("secure_num") != null){
          String sentSecureNums[] =  modinfo.getParameterValues("secure_num");
          String playerCard[] =  modinfo.getParameterValues("card");
          String playerCardNo[] =  modinfo.getParameterValues("cardNo");
          String lines[] = modinfo.getParameterValues("group_num");
          int numPlayers = sentSecureNums.length;
          boolean ones = false;
          boolean fullGroup = false;
          boolean fullQuota = false;

          if(sentSecureNums != null){
            for (int j = 0; j < sentSecureNums.length; j++) {
              try{
                if(sentSecureNums[j] != null && !"".equals(sentSecureNums[j]) ){
                  boolean ssn = false; // social security number
                  if(sentSecureNums[j].length() == 10){
                    try{
                      Integer.parseInt(sentSecureNums[j].substring(0,5));
                      Integer.parseInt(sentSecureNums[j].substring(6,9));
                      ssn = true;
                    }catch(NumberFormatException e){
                      ssn = false;
                    }
                  }

                  if(ssn){
                    Member tempMemb = (com.idega.projects.golf.entity.Member)Member.getMember(sentSecureNums[j]);
                    if(tempMemb != null){

                      if( business.countEntriesInGroup(Integer.parseInt(lines[j]),this.currentField,this.currentDay) >= maxCountInGroups){
                        illegal.add(k++,new Integer(j));
                        fullGroup = true;
                      }else if(business.countOwnersEntries(Integer.parseInt(this.currentMember),this.currentField,this.currentDay) >= maxPerOwnerPerDay ){
                        illegal.add(k++,new Integer(j));
                        fullQuota = true;
                      }else /*if(business.countMembersEntries){}else*/{
                        business.setStartingtime(Integer.parseInt(lines[j]), this.currentDay, this.currentField, Integer.toString(tempMemb.getID()),this.currentMember, tempMemb.getName(), Float.toString(tempMemb.getHandicap()), GolfCacher.getCachedUnion(tempMemb.getMainUnionID()).getAbbrevation(), playerCard[j], playerCardNo[j]);
                        ones = true;
                      }
                    }else{
                      illegal.add(k++,new Integer(j));
                    }
                  }else{
                    illegal.add(k++,new Integer(j));
                  }
                }
              }catch(SQLException e){
                illegal.add(k++,new Integer(j));
              }
            }

              if(illegal.size() > 0){

                Text myText = (Text)templText.clone();
                myText.setText("villa kom upp � eftirfarandi: ");

                Table tempTable = new Table(3,illegal.size());

                for (int i = 0; i < illegal.size(); i++) {
                  int index = ((Integer)illegal.get(i)).intValue();

                  Text tempIllegal1 = (Text)templText.clone();
                  tempIllegal1.setText(sentSecureNums[index]);
                  tempTable.add(tempIllegal1,1,i+1);

                  Text tempIllegal2 = (Text)templText.clone();
                  tempIllegal2.setText(playerCard[index]);
                  tempTable.add(tempIllegal2,2,i+1);

                  Text tempIllegal3 = (Text)templText.clone();
                  tempIllegal3.setText(playerCardNo[index]);
                  tempTable.add(tempIllegal3,3,i+1);

                }

                frameTable.add(Text.getBreak());
                frameTable.add(myText);
                frameTable.add(Text.getBreak());
                frameTable.add(tempTable);

                if(ones){
                  Text noError = (Text)templText.clone();
                  noError.setText("...a�rir voru skr��ir inn");
                  frameTable.add(Text.getBreak());
                  frameTable.add(noError);
                }

                if(fullGroup){
                  Text Error = (Text)templText.clone();
                  Error.setText("Holl sem reynt var a� skr� � er fullt ");
                  frameTable.add(Text.getBreak());
                  frameTable.add(Error);
                }

                if(fullQuota){
                  Text Quota = (Text)templText.clone();
                  Quota.setText("Hefur ekki r�ttindi til a� skr� fleiri � �essum velli � dag");
                  frameTable.add(Text.getBreak());
                  frameTable.add(Quota);

                  Text comment = (Text)templText.clone();
                  comment.setText("Haf�u samband vi� kl�bbinn ef �� vilt skr� fleiri");
                  frameTable.add(Text.getBreak());
                  frameTable.add(comment);
                } else {
                  Text comment = (Text)templText.clone();
                  comment.setText("Reyni� aftur e�a hafi� samband vi� kl�bbinn");
                  frameTable.add(Text.getBreak());
                  frameTable.add(comment);
                }

                  //this.add(new BackButton(new Image("/pics/rastimask/Takkar/Ttilbaka1.gif")));
                  frameTable.add(Text.getBreak());
                  frameTable.add(Text.getBreak());
                  frameTable.add(new CloseButton("Loka glugga"));

              }else{
                this.setParentToReload();
                this.close();
              }

          }else{
            Text comment = (Text)templText.clone();
            comment.setText("Enginn skr��ist");
            frameTable.add(Text.getBreak());
            frameTable.add(comment);

            //this.add(new BackButton(new Image("/pics/rastimask/Takkar/Ttilbaka1.gif")));
            frameTable.add(Text.getBreak());
            frameTable.add(Text.getBreak());
            frameTable.add(new CloseButton("Loka glugga"));
          }
        }else{
          Text comment = (Text)templText.clone();
          comment.setText("Enginn skr��ist");
          frameTable.add(Text.getBreak());
          frameTable.add(comment);

          //this.add(new BackButton(new Image("/pics/rastimask/Takkar/Ttilbaka1.gif")));
          frameTable.add(Text.getBreak());
          frameTable.add(Text.getBreak());
          frameTable.add(new CloseButton("Loka glugga"));
        }
    }

    public void setErroResponse(Form myForm, boolean inputErr)
    {
            String btnCloseUrl = "/pics/rastimask/Takkar/TLoka1.gif";
            String btnBackUrl = "/pics/rastimask/Takkar/Ttilbaka1.gif";

            Table myTable = new Table(2, 3);
            if(inputErr){
                    myTable.addText("Nau�synlegt er a� skr� eins marga og teknir voru fr�", 2, 1);
                    myTable.add(new BackButton("Til baka"), 2, 3);
            }
            else{
                    myTable.addText("�etta holl er �v� mi�ur fullt. Gj�r�u svo vel a� velja ��r n�jan t�ma", 2, 1);
                    myTable.add(new CloseButton("Loka glugga"), 2, 3);
            }

            myTable.setAlignment(2, 3, "center");
            myTable.setCellpadding(0);
            myTable.setCellspacing(0);
            frameTable.empty();
            frameTable.add(myTable);

    }

    public GolfField getFieldInfo( int field, String date) throws SQLException,IOException{
            StartingtimeFieldConfig FieldConfig = business.getFieldConfig( field , date );
            GolfField field_info = new GolfField ( new idegaTimestamp(FieldConfig.getOpenTime()).toSQLTimeString(), new idegaTimestamp(FieldConfig.getCloseTime()).toSQLTimeString(), FieldConfig.getMinutesBetweenStart(), field, date, FieldConfig.getDaysShown() );
            return field_info;
    }

     public String getTime( int end, GolfField myGolfField)
    {

        int interval = myGolfField.get_interval();
        int openMin = myGolfField.get_open_min();
            int openHour = myGolfField.get_open_hour();

            String Time = "";

            for(int i = 1; i <= end; i ++){

                    if (openMin >= 60){
                            openMin -= 60;
                            openHour++;
                    }

                    if (openMin < 10)
                            Time = openHour + ":0" + openMin;
                    else
                            Time = openHour + ":" + openMin;

                    openMin += interval;

            }
            return Time;
     }


 public void noPermission(){
    Text satyOut = new Text("�� hefur ekki r�ttindi fyrir �essa s��u");
    satyOut.setFontSize(4);
    Table AlignmentTable = new Table();
    AlignmentTable.setBorder(0);
    AlignmentTable.add(Text.getBreak());
    AlignmentTable.add(satyOut);
    AlignmentTable.setAlignment("center");
    AlignmentTable.add(Text.getBreak());
    AlignmentTable.add(Text.getBreak());
//    Link close = new Link("Loka glugga");
//    close.addParameter(closeParameterString, "true");
//    AlignmentTable.add(close);
    frameTable.empty();
    frameTable.add(AlignmentTable);
  }

  public void lineUpTournamentDay(ModuleInfo modinfo, List Tournaments){
    Text dayReserved = new Text("Dagur fr�tekinn fyrir m�t");
    dayReserved.setFontSize(4);
    Table AlignmentTable = new Table();
    AlignmentTable.setBorder(0);
    AlignmentTable.add(Text.getBreak());
    AlignmentTable.add(dayReserved);
    for (int i = 0; i < Tournaments.size(); i++) {
      AlignmentTable.add("<p>" + ((Tournament)Tournaments.get(i)).getName());
    }
    AlignmentTable.setAlignment("center");
    AlignmentTable.add(Text.getBreak());
    AlignmentTable.add(Text.getBreak());
    AlignmentTable.add(new CloseButton("Loka glugga"));
    frameTable.empty();
    frameTable.add(AlignmentTable);
  }



public void main(ModuleInfo modinfo) throws Exception {
    super.main(modinfo);
    String date = modinfo.getSession().getAttribute("date").toString();
    //String field_id = modinfo.getSession().getAttribute("field_id").toString();
    currentField = modinfo.getSession().getAttribute("field_id").toString();
    currentUnion = modinfo.getSession().getAttribute("union_id").toString();


    boolean keepOn = true;

    try{
      currentMember = Integer.toString(com.idega.projects.golf.login.business.LoginBusiness.getMember(modinfo).getID());
      currentDay = new idegaTimestamp(date);
    }catch(Exception e){
      keepOn = false;
      this.noPermission();
    }



//    if(modinfo.getParameter(saveParameterString+".x") != null || modinfo.getParameter(saveParameterString) != null){
//      this.handleFormInfo(modinfo);
//    }

    if(keepOn){
      TournamentDay tempTD = new TournamentDay();
//      List Tournaments = EntityFinder.findAll(new Tournament(),"select tournament.* from tournament,tournament_day where tournament_day.tournament_id=tournament.tournament_id and tournament_day.day_date = '"+currentDay.toSQLDateString()+"' and tournament.field_id = " + currentField );
      List TournamentRounds = EntityFinder.findAll(new TournamentRound(),"select tournament_round.* from tournament,tournament_round where tournament_round.tournament_id=tournament.tournament_id and tournament_round.round_date >= '"+currentDay.toSQLDateString()+" 00:00' and tournament_round.round_date <= '"+currentDay.toSQLDateString()+" 23:59' and tournament.field_id = " + currentField );

      if(TournamentRounds != null ){
          List Tournaments = new Vector();
          for (int i = 0; i < TournamentRounds.size(); i++) {
            Tournaments.add(i,((TournamentRound)TournamentRounds.get(i)).getTournament());
          }

          fieldInfo = business.getFieldConfig( Integer.parseInt(currentField) , currentDay );
          lineUpTournamentDay(modinfo, Tournaments );
      }else{
        myForm.maintainParameter("secure_num");
        myForm.maintainParameter("line");
        int skraMargaInt = 0;
        String skraMarga = modinfo.getParameter("skraMarga");

        int line = Integer.parseInt( modinfo.getParameter("line"));
        int check = business.countEntriesInGroup(line, currentField, currentDay);

        if( check > 3){
          setErroResponse(myForm, false);
        }
        else{
          if( modinfo.getParameter("secure_num") != null){
            handleFormInfo(modinfo);
          }else{
            fieldInfo = business.getFieldConfig( Integer.parseInt(currentField) , currentDay );
            skraMargaInt = Integer.parseInt(skraMarga);
            lineUpTable(skraMargaInt, modinfo);
          }
        }
      }
    }else{
      this.noPermission();
    }
  } // method main() ends

} // Class ends