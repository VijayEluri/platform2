/*
 * $Id: GolfMainJSPModulePage.java,v 1.4 2003/11/21 19:01:27 tryggvil Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package is.idega.idegaweb.golf.templates.page;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import is.idega.idegaweb.golf.*;
import is.idega.idegaweb.golf.presentation.*;
import is.idega.idegaweb.golf.moduleobject.Login;
import com.idega.jmodule.*;
import com.idega.jmodule.banner.*;
import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;
import com.idega.jmodule.poll.moduleobject.*;
import com.idega.jmodule.news.data.*;
import com.idega.jmodule.forum.data.*;
import com.idega.jmodule.boxoffice.presentation.*;
import com.idega.util.*;
import java.sql.*;
import is.idega.idegaweb.golf.entity.*;
import java.io.*;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.core.localisation.business.LocaleSwitcher;


/**
 * @author Gudmundur idega iceland
 */
public class GolfMainJSPModulePage extends MainPage {
  protected Login login;
  protected Table centerTable;
  protected String align;

  protected final int SIDEWIDTH = 720;
  protected final int LEFTWIDTH = 163;
  protected final int RIGHTWIDTH = 148;
  private final static String IW_BUNDLE_IDENTIFIER="com.idega.idegaweb.golf";
  protected IWResourceBundle iwrb;
  protected IWBundle iwb;

  protected boolean isAdmin = false;

  public GolfMainJSPModulePage() {
    super();
    initCenter();
  }

  protected void User(IWContext iwc) throws SQLException, IOException {
    this.setTextDecoration("none");
    setTopMargin(5);
    add("top", golfHeader());
    add("top", Top(iwc));
    add("bottom", golfFooter());
    add(Left(iwc), Center(), Right(iwc));
    setWidth(1, Integer.toString(LEFTWIDTH) );
    setContentWidth( "100%");
    setWidth(3, Integer.toString(RIGHTWIDTH) );
  }

  protected Table Top(IWContext iwc) throws SQLException,IOException{
    Table topTable = new Table(2,1);
    topTable.setCellpadding(0);
    topTable.setCellspacing(0);
    topTable.setHeight("90");
    topTable.add(getLogin(),1,1);
    topTable.add(getHBanner(iwc),2,1);

    //topTable.add(getHoleView(),3,1);//debug landsmot
    //topTable.add(iwrb.getImage("/banners/small_ad.gif"),3,1);

    topTable.setAlignment(2,1,"center");
    //debug topTable.setAlignment(3,1,"center");
    topTable.setVerticalAlignment(1,1, "top");
    topTable.setVerticalAlignment(2,1, "middle");
    //topTable.setVerticalAlignment(3,1, "middle");

    topTable.setWidth(1, Integer.toString(LEFTWIDTH));
    topTable.setWidth("100%");
    //topTable.setWidth(3, Integer.toString(RIGHTWIDTH));

    return topTable;
  }


  protected Table Left(IWContext iwc) throws SQLException, IOException {
    Table leftTable = new Table(1,11);
    //leftTable.setBorder(1);
    leftTable.setVerticalAlignment("top");
    leftTable.setVerticalAlignment(1,1,"top");
    leftTable.setVerticalAlignment(1,2,"top");
    leftTable.setVerticalAlignment(1,3,"top");
    leftTable.setVerticalAlignment(1,4,"top");
    leftTable.setVerticalAlignment(1,5,"top");
    leftTable.setVerticalAlignment(1,6,"top");
    leftTable.setVerticalAlignment(1,7,"top");
    leftTable.setVerticalAlignment(1,8,"top");
    leftTable.setVerticalAlignment(1,9,"top");
    leftTable.setVerticalAlignment(1,10,"top");
    leftTable.setVerticalAlignment(1,11,"top");

    //leftTable.setHeight("100%");
    leftTable.setColumnAlignment(1, "left");
    leftTable.setWidth(LEFTWIDTH);
    leftTable.setCellpadding(0);
    leftTable.setCellspacing(0);

    leftTable.setAlignment(1,1,"center");

    // uncomment this and change for different tournaments
   // leftTable.add(getHoleView(),1,1);

    //leftTable.addBreak(1,1);
    leftTable.add(Languages(),1,1);

    HeaderTable sponsorBox = Sponsors();
    //sponsorBox.setCacheable("SponsorBox",86400000);//24 hour
    leftTable.add(Block.getCacheableObject(sponsorBox,"SponsorBox",86400000), 1,3);

    Block newsBox = new ClubNewsBox();
    //HeaderTable newsBox = clubNews();
    //newsBox.setCacheable("NewsBox",3600000);//60*60*1000 1 hour
    //leftTable.add(Block.getCacheableObject(newsBox,"NewsBox",3600000),1,5);
    leftTable.add(newsBox,1,5);

    TournamentBox tBox = new TournamentBox();
    tBox.setCacheable("TournamentBox",1800000);
    leftTable.add(tBox,1,7);

    Block chatBox = new ForumsBox();
    //HeaderTable chatBox = getChat();
    //chatBox.setCacheable("ChatBox",3600000);
    //leftTable.add(Block.getCacheableObject(chatBox,"ChatBox",3600000),1,9);
    leftTable.add(chatBox,1,9);

    leftTable.add(idega(),1,11);

    return leftTable;
  }

  protected Link getHoleView() {
      Window window = new Window("Hola fyrir holu",820,600,"/landsmot.jsp");
          window.setMenubar(true);
          window.setResizable(true);

      Link link = new Link(iwb.getImage("shared/landsmot.gif",111,76),window);
      return link;
  }

  protected Table Languages() throws SQLException {
    return Languages(null, null);
  }

  protected Table Languages(String attributeName, String attributeValue) throws SQLException {
    Table languages = new Table(4,1);
    languages.setAlignment("left");
    //Text IS = new Text(iwrb.getLocalizedString("languages.icelandic","icelandic"));
    Text IS = new Text(iwrb.getLocalizedString("languages.swedish","svenska"));
    Text EN = new Text(iwrb.getLocalizedString("languages.english","english"));
    IS.setFontSize(Text.FONT_SIZE_7_HTML_1);
    EN.setFontSize(Text.FONT_SIZE_7_HTML_1);

    // vantar link � textann og myndirnar
   // IS.setFontColor("#CCCCCC");
   // EN.setFontColor("#ABABAB");

    //Link isLink = new Link(iwb.getImage("shared/icelandic.gif"));
    Link isLink = new Link(iwb.getImage("shared/swedish.gif"));
    isLink.setEventListener(com.idega.core.localisation.business.LocaleSwitcher.class.getName());
    isLink.addParameter(LocaleSwitcher.languageParameterString,"sv_SE");
    //isLink.addParameter(LocaleSwitcher.languageParameterString,LocaleSwitcher.icelandicParameterString);

    Link enLink = new Link(iwb.getImage("shared/english.gif") );
    enLink.setEventListener(com.idega.core.localisation.business.LocaleSwitcher.class.getName());
    enLink.addParameter(LocaleSwitcher.languageParameterString,LocaleSwitcher.englishParameterString);


    if (attributeName != null && attributeValue != null) {
      isLink.addParameter(attributeName, attributeValue);
      enLink.addParameter(attributeName, attributeValue);
    }

    languages.add( isLink , 1,1);
    languages.add( IS , 2,1);
    languages.add( enLink , 3,1);
    languages.add( EN , 4,1);

    return languages;
  }



  protected HeaderTable getGolfLinks() {
    HeaderTable table = new HeaderTable();
    table.setBorderColor("#8ab490");
    table.setHeadlineSize(1);
    table.setHeadlineColor("#FFFFFF");
    table.setHeadlineLeft();
    table.setWidth(148);
    table.setHeaderText(iwrb.getLocalizedString("links","Links"));

    Table myTable = new Table(1,2);
    myTable.setAlignment(1,1,"center");
    myTable.setAlignment(1,2,"center");
    myTable.setCellpadding(2);
    myTable.setCellspacing(2);
    myTable.setHeight("95");
    myTable.setWidth("100%");

    Image europeant = iwrb.getImage("banners/europeantour.gif");
    europeant.setWidth(69);
    europeant.setHeight(47);


    Link europeantour = new Link(europeant,"http://www.europeantour.com");
    europeantour.setTarget("_new");

    Image pgaimg = iwrb.getImage("banners/pgatour.gif");
    pgaimg.setWidth(59);
    pgaimg.setHeight(80);

    Link pgatour = new Link(pgaimg,"http://www.pgatour.com");
    pgatour.setTarget("_new");

    myTable.add(europeantour,1,1);
    myTable.add(pgatour,1,2);

    table.add(myTable);
    return table;
  }


      protected Table idega(){
          Table idegaTable = new Table (1,1);
          idegaTable.setCellpadding(0);
          idegaTable.setCellspacing(0);
          idegaTable.setAlignment(1,1, "center");
          idegaTable.setWidth(148);

          Link idegaLink = new Link(iwrb.getImage("banners/idegalogo.gif"), "http://www.idega.is");

          idegaLink.setTarget("_blank");
          idegaTable.add(idegaLink, 1, 1);

          return idegaTable;
      }

        protected Login getLogin(){
          return new Login();
        }

        protected HeaderTable getGSIAssociates() {

           HeaderTable table = new HeaderTable();
            table.setBorderColor("#8ab490");
            table.setHeadlineSize(1);
            table.setHeadlineColor("#FFFFFF");
            table.setHeadlineLeft();
            table.setWidth(148);
            table.setHeaderText(iwrb.getLocalizedString("arePartOf","GSI is part of"));



              com.idega.presentation.Image logo1 = iwrb.getImage("banners/WAGC.gif");
              com.idega.presentation.Image logo2 = iwrb.getImage("banners/EGA.gif");
              com.idega.presentation.Image logo3 = iwrb.getImage("banners/RACrest2.gif");


              Link logo1Link = new Link(logo1,"http://www.wagc.org/");
                        logo1Link.setTarget("_new");
              Link logo2Link = new Link(logo2,"http://www.ega-golf.ch/");
                        logo2Link.setTarget("_new");
              Link logo3Link = new Link(logo3,"http://www.randa.org/");
                        logo3Link.setTarget("_new");


              table.add(logo1Link);
              table.add(Text.getBreak());
              table.add(logo2Link);
              table.add(Text.getBreak());
              table.add(logo3Link);


            return table;



        }

        protected HeaderTable getPollVoter(){
      BasicPollVoter poll = new BasicPollVoter("/poll/results.jsp",true);
        poll.setConnectionAttributes("union_id",3);
        poll.setHeaderColor("#8ab490");
        poll.setColor1("#FFFFFF");
        poll.setHeadlineColor("#FFFFFF");
	poll.setHeadlineSize(1);
        poll.setNumberOfShownPolls(3);
        poll.setHeadlineLeft();
        poll.setAdminButtonURL("/pollmanager.gif");


        HeaderTable pollTable = new HeaderTable();
            pollTable.setBorderColor("#8ab490");
            pollTable.setHeadlineSize(1);
            pollTable.setHeadlineColor("#FFFFFF");
            pollTable.setHeadlineLeft();
            pollTable.setWidth(148);
            pollTable.setHeaderText(iwrb.getLocalizedString("questionOfTheDay","Question of the day"));

        pollTable.add(poll);

          return pollTable;
       }


        protected Table Center(){
          return centerTable;
        }

        protected void initCenter(){

          centerTable = new Table(1,1);
          centerTable.setWidth("100%");
          centerTable.setHeight("100%");
          centerTable.setCellpadding(0);
          centerTable.setCellspacing(0);
          centerTable.setAlignment(1,1, "center");
          setVerticalAlignment( "top" );
        }

    protected HeaderTable getProGolfers() {
      HeaderTable table = new HeaderTable();
      table.setBorderColor("#8ab490");
      table.setHeadlineSize(1);
      table.setHeadlineColor("#FFFFFF");
      table.setHeadlineLeft();
      table.setWidth(148);
      table.setHeaderText(iwrb.getLocalizedString("golferpage.header_table_name","Pro golfers"));
      //this should be automated
      Table golfers = new Table(1,1);

      /**@todo If golfersPage should be generalized then new data could be inserted through
       * the GolfersFriendsDataBusiness class, "into" the GolferPageData class.  That class could
       * contain possibly only golfers who would have their own pages.  Then here we would use a
       * findAll-function on that Data class, to extract all the "pro"-golfers and when clicked on
       * they would submit their member_union_id.  Then the rest should work???  (depends on what
       * you want to get).  Bjarni.
       * */

      Text text = new Text("Bj�rgvin Sigurbergsson");
      text.setFontSize(1);

      Link golferLink = new Link(text,"/golfers/index.jsp");
      golferLink.addParameter("member_union_id", "3152");  //Bj�rgvins ID!!!!!!!

      golfers.add(golferLink,1,1);
      table.add(golfers);

      return table;
    }

        protected Table Right(IWContext iwc) throws SQLException,IOException{
          Table rightTable = new Table(1,11);
          rightTable.setWidth(RIGHTWIDTH);
          rightTable.setCellpadding(0);
          rightTable.setCellspacing(0);

          rightTable.setVerticalAlignment(1,1,"top");
          rightTable.setVerticalAlignment(1,2,"top");
          rightTable.setVerticalAlignment(1,3,"top");
          rightTable.setVerticalAlignment(1,4,"top");
          rightTable.setVerticalAlignment(1,5,"top");
          rightTable.setVerticalAlignment(1,6,"top");
          rightTable.setVerticalAlignment(1,7,"top");
          rightTable.setVerticalAlignment(1,8,"top");
          rightTable.setVerticalAlignment(1,9,"top");
          rightTable.setVerticalAlignment(1,10,"top");

          rightTable.setColumnAlignment(1, "center");
          //HeaderTable proGolfers = getProGolfers();
          //proGolfers.setCacheable("proGolfers",86400000);//24 hour

          //rightTable.add(Block.getCacheableObject(proGolfers,"proGolfers",86400000),1,1);
          //rightTable.add(new Flash("http://clarke.idega.is/golfnews.swt?text="+java.net.URLEncoder.encode(iwrb.getLocalizedString("template.international_golf_news","International golf news")),148,288),1,3);


          HeaderTable poll = getPollVoter();
          //poll.setCacheable("poll",3600000);//1 hour
          rightTable.add(Block.getCacheableObject(poll,"poll",3600000),1,1);//1,5

          HeaderTable asses = getGSIAssociates();
          //asses.setCacheable("asses",86400000);//24 hour
          rightTable.add(Block.getCacheableObject(asses,"asses",86400000),1,3);//1,7

          HeaderTable gLinks = getGolfLinks();
          //gLinks.setCacheable("gLinks",86400000);//24 hour
          rightTable.add(Block.getCacheableObject(gLinks,"gLinks",86400000),1,5);//1,9

          //BoxReader bLinks = getLinks(iwc);
          //bLinks.setCacheable("Miscbox",86400000);//1000*60*60*24 = 24 hours
          //rightTable.add(bLinks,1,7);

          Block bLinks = new MiscellenousBox();
          rightTable.add(bLinks,1,7);


         /* Block yellow = new Block();
          yellow.add(getYellowLine());
          yellow.setCacheable("yellow",86400000);//24 hour
          rightTable.add(getYellowLine(),1,9);//1,11*/


          return rightTable;
        }


      private Form getYellowLine() {

        Form myForm = new Form("http://www.gulalinan.is/leit.asp","get");
          myForm.setMarkupAttribute("target","_blank");
          myForm.setName("Search");

        Table myTable = new Table(1,3);
          myTable.setWidth(120);
          myTable.setHeight(70);
          myTable.setCellpadding(0);
          myTable.setCellspacing(0);

        Image rammiUppi = new Image("/pics/gulalinan/rammi_uppi120.gif","",120,6);
          myTable.add(rammiUppi,1,1);
        Image rammiNidri = new Image("/pics/gulalinan/rammi_nidri120.gif","",120,8);
          myTable.add(rammiNidri,1,3);

        Table innerTable = new Table(1,3);
          innerTable.setWidth(120);
          innerTable.setHeight("100%");
          innerTable.setCellpadding(0);
          innerTable.setCellspacing(0);
          innerTable.setAlignment(1,1,"center");
          innerTable.setAlignment(1,2,"center");
          innerTable.setAlignment(1,3,"right");
          innerTable.setBackgroundImage(new Image("/pics/gulalinan/bakgrunnurx120.gif"));

        Image searchImage = new Image("/pics/gulalinan/gulalinanlogo.gif","",67,12);
        Link yellowLink = new Link(searchImage,"http://www.gulalinan.is");
          yellowLink.setTarget("_blank");

        TextInput textInput = new TextInput("kwd");
          textInput.setLength(12);

        HiddenInput hidden = new HiddenInput("ac","ks");

        Image submitImage = new Image("/pics/gulalinan/leita.gif","Leita",39,13);
        SubmitButton submit = new SubmitButton(submitImage,"image1");
          submit.setMarkupAttribute("hspace","5");

        innerTable.add(yellowLink,1,1);
        innerTable.add(textInput,1,2);
        innerTable.add(submit,1,3);

        myTable.add(innerTable,1,2);
        myForm.add(myTable);
        myForm.add(hidden);

        return myForm;

      }


      protected Table golfHeader(){
          Table golfHeader = new Table(1,2);

          Text zero = new Text("");
          zero.setFontSize("1");

          golfHeader.add(zero ,1,2);

          Table linkTable = new Table(8,1);


          golfHeader.setHeight( 1, "68");
          golfHeader.setHeight( 2, "16");
          setTopHeight("84");
          golfHeader.setWidth("720");
          golfHeader.setCellpadding(0);
          golfHeader.setCellspacing(0);

        Image banBg = iwrb.getImage("/mainpage/banner.gif");
         banBg.setWidth(720);
         banBg.setHeight(68);

         golfHeader.setBackgroundImage(1, 1, banBg);
          golfHeader.setVerticalAlignment(1,2, "top");

          linkTable.setHeight("14");
          linkTable.setCellpadding(0);
          linkTable.setCellspacing(0);
          linkTable.setWidth("720");
          linkTable.setVerticalAlignment("top");
          linkTable.setRowVerticalAlignment(1,"top");

          //Set inn linka
          Image clubImage = iwrb.getImage("/mainpage/clubs.gif");
         // clubImage.setWidth(101);
          //clubImage.setHeight(15);
         Link club = new Link(clubImage, "/clubs/");
          linkTable.add(club, 1, 1);

         Image startingtimesImage = iwrb.getImage("/mainpage/teetimes.gif");
         // startingtimesImage.setWidth(85);
          //startingtimesImage.setHeight(15);
         Link startingtimes = new Link(startingtimesImage, "/start/search.jsp");
         linkTable.add(startingtimes, 2, 1);

         Image handicapImage = iwrb.getImage("/mainpage/handicap.gif");
          //handicapImage.setWidth(85);
          //handicapImage.setHeight(15);
         Link handicap = new Link(handicapImage, "/handicap/");
           linkTable.add(handicap, 3, 1);

        Image modtaskraImage = iwrb.getImage("/mainpage/tournaments.gif");
          //modtaskraImage.setWidth(85);
          //modtaskraImage.setHeight(15);
        Link motaskra = new Link(modtaskraImage, "/tournament/");
          linkTable.add(motaskra, 4, 1);

          Image umGSIImage = iwrb.getImage("/mainpage/aboutgsi.gif");
          //umGSIImage.setWidth(73);
          //umGSIImage.setHeight(15);
          Link umGSI = new Link(umGSIImage, "/gsi/index.jsp");
          linkTable.add(umGSI, 5, 1);

          Image spjallidImage = iwrb.getImage("/mainpage/forums.gif");
          //spjallidImage.setWidth(85);
          //spjallidImage.setHeight(15);
          Link spjallid = new Link(spjallidImage, "/forum/index.jsp");
          linkTable.add(spjallid, 6, 1);

          Image indexImage = iwrb.getImage("/mainpage/home.gif");
          //indexImage.setWidth(85);
          //indexImage.setHeight(15);
          Link index = new Link(indexImage, "/index.jsp");
          linkTable.add(index, 7, 1);

          Image endImage = iwrb.getImage("/mainpage/linkend.gif");
          //endImage.setWidth(121);
          //endImage.setHeight(15);
          linkTable.add( endImage, 8, 1 );

          golfHeader.setAlignment(1,2, "top");
          golfHeader.add(linkTable, 1, 2 );

          return golfHeader;
      }


      protected Table golfFooter(){
          Table golfFooter = new Table(6,1);
          golfFooter.setHeight("21");
          setBottomHeight( "21");
          golfFooter.setWidth("720");
          golfFooter.setCellpadding(0);
          golfFooter.setCellspacing(0);

          golfFooter.add(iwrb.getImage("/mainpage/bottom1.gif"),1,1);
          golfFooter.add(new Link (iwrb.getImage("/mainpage/bottom2.gif"), "/index.jsp"),2,1);
          golfFooter.add(iwrb.getImage("/mainpage/bottom3.gif"),3,1);
          Image back = iwrb.getImage("/mainpage/bottom4.gif");
          back.setMarkupAttribute("OnClick", "history.go(-1)");
          golfFooter.add(back,4,1);
          golfFooter.add(iwrb.getImage("/mainpage/bottom5.gif"),5,1);
          golfFooter.add(new Link (iwrb.getImage("/mainpage/bottom6.gif"), "mailto: golf@idega.is"),6,1);

          return golfFooter;
      }


      protected Table getHBanner(IWContext iwc) throws SQLException{
          Table bannerTable = new Table(1,1);
          bannerTable.setAlignment("center");
          bannerTable.setAlignment(1,1,"middle");
          bannerTable.setCellpadding(10);
          bannerTable.setCellspacing(0);


 		InsertBanner ib = new InsertBanner(3, isAdmin(iwc));
                  ib.setAdminButtonURL("/pics/jmodules/banner/bannerstjori.gif");
		bannerTable.add(ib,1,1);
          return bannerTable;
      }

      protected HeaderTable Sponsors() throws IOException{

          HeaderTable table = new HeaderTable();
            table.setBorderColor("#8ab490");
            table.setHeadlineSize(1);
            table.setHeadlineColor("#FFFFFF");
            table.setRightHeader(false);
            table.setHeadlineAlign("left");
            table.setWidth(148);
            table.setHeaderText(iwrb.getLocalizedString("associates","Associates"));

                Table innerTable = new Table(1,4);
                  innerTable.setWidth("100%");
                  innerTable.setColumnAlignment(1,"center");

                        Link one = new Link(iwrb.getImage("/banners/wmdata.gif"),"http://www.wmdata.se");
			Link two = new Link(iwrb.getImage("/banners/idega.gif"),"http://www.idega.is");
			Link three = new Link(iwrb.getImage("/banners/compaq.gif"),"http://www.compaq.se");
                	Link four = new Link(iwrb.getImage("/banners/telia.gif"),"http://www.telia.se");
			/*Link six = new Link(iwrb.getImage("/banners/samvinn.gif"),"http://www.samvinn.is");
			Link five = new Link(iwrb.getImage("/banners/ecco.gif"),"http://www.ecco.com");
			Link seven = new Link(iwrb.getImage("/banners/euro.gif"),"http://www.europay.is");
			Link eight = new Link(iwrb.getImage("/banners/syn.gif"),"http://www.syn.is");
			//Link nine = new Link(iwrb.getImage("/banners/golfcard.gif"),"http://www.europay.is/form/kort.htm");
*/
			one.setTarget("_blank");
			two.setTarget("_blank");
			three.setTarget("_blank");
			four.setTarget("_blank");
			/*five.setTarget("_blank");
			six.setTarget("_blank");
			seven.setTarget("_blank");
			eight.setTarget("_blank");*/
			//nine.setTarget("_blank");

			innerTable.add(one,1,1);
			innerTable.add(two,1,2);
			innerTable.add(three,1,3);
			innerTable.add(four,1,4);
			/*innerTable.add(five,1,5);
			innerTable.add(six,1,6);
			innerTable.add(seven,1,7);
			innerTable.add(eight,1,8);*/
			//innerTable.add(nine,1,9);

                table.add(innerTable);


         return table;
      }

  // ###########  Public - F�ll

  public void setVerticalAlignment(String alignment) {
    centerTable.setVerticalAlignment(alignment);
    centerTable.setVerticalAlignment(1,1,alignment);
  }


	public void add(PresentationObject objectToAdd) {
  	  Center().add(objectToAdd,1,1);
        }




  public void removeUnionIdSessionAttribute(IWContext iwc){
    iwc.removeSessionAttribute("golf_union_id");
  }

  public String getUnionID(IWContext iwc){
    return (String)iwc.getSessionAttribute("golf_union_id");
  }

  public void setUnionID(IWContext iwc, String union_id){
    iwc.setSessionAttribute("golf_union_id", union_id);
  }


  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }



  public Member getMember(IWContext iwc){
          return (Member)iwc.getSession().getAttribute("member_login");
  }

  public boolean isAdmin(IWContext iwc) {
    try {
      return com.idega.jmodule.login.business.AccessControl.isAdmin(iwc);
    }
    catch(SQLException E) {
      E.printStackTrace(System.err);
    }
    catch (Exception E) {
      E.printStackTrace(System.err);
    }
    finally {
	  }

    return false;
  }

  public boolean isDeveloper(IWContext iwc) {
    return com.idega.jmodule.login.business.AccessControl.isDeveloper(iwc);
  }

  public boolean isClubAdmin(IWContext iwc) {
    return com.idega.jmodule.login.business.AccessControl.isClubAdmin(iwc);
  }

  public boolean isClubWorker(IWContext iwc) {
    boolean ret;

    try {
      ret = com.idega.jmodule.login.business.AccessControl.isClubWorker(iwc);
    }
    catch(java.sql.SQLException e) {
      e.printStackTrace(System.err);
      ret = false;
    }

    return(ret);
  }

  public boolean isUser(IWContext iwc) {
    return com.idega.jmodule.login.business.AccessControl.isUser(iwc);
  }





  public void main(IWContext iwc) throws Exception {
    isAdmin = isAdmin(iwc);

    iwrb = getResourceBundle(iwc);
    iwb = getBundle(iwc);

    setLinkColor(iwb.getProperty("link_color","black"));
    setVlinkColor(iwb.getProperty("vlink_color","black"));
    setHoverColor(iwb.getProperty("hover_link_color","#8ab490"));

    try {
     User(iwc);
    }
    catch(SQLException E) {
      E.printStackTrace(System.err);
    }
    catch (IOException E) {
      E.printStackTrace(System.err);
    }
  }


}
