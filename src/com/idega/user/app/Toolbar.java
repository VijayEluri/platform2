package com.idega.user.app;

import java.util.List;
import java.util.Vector;

import com.idega.event.IWPresentationEvent;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.browser.presentation.IWBrowserView;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.user.block.search.presentation.SearchForm;
import com.idega.user.block.search.presentation.SearchWindow;
import com.idega.user.event.ChangeClassEvent;
import com.idega.user.presentation.CreateGroupWindow;
import com.idega.user.presentation.CreateUser;
import com.idega.user.presentation.MassMovingWindow;
import com.idega.util.IWColor;


/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class Toolbar extends Page implements IWBrowserView {

	protected String title;
	protected IWBundle iwb;
  protected IWResourceBundle iwrb;
  protected String _controlTarget = null;
  protected IWPresentationEvent _controlEvent = null;

  protected Vector _toolbarElements = new Vector();
  private SearchForm searchForm = new SearchForm();
  
  private String selectedGroupProviderStateId = null;
  private String userApplicationMainAreaStateId = null;


  public Toolbar(){
  }

  public void add(ToolbarElement element){
    _toolbarElements.add(element);
  }
  
  public void setSelectedGroupProviderStateId(String selectedGroupProviderStateId) {
    this.selectedGroupProviderStateId = selectedGroupProviderStateId;
  }
    

  public void add(Toolbar toolbar){
    _toolbarElements.add(toolbar);
//    addSeperator();
//    List l = getToolbarElements();
//    if (l != null) {
//      Iterator iter = l.iterator();
//      while (iter.hasNext()) {
//        Object item = iter.next();
//        this.add((ToolbarElement)item);
//      }
//    }
  }

  public void addSeperator(){

  }

  protected List getToolbarElements(){
    return _toolbarElements;
  }



  public String getBundleIdentifier(){
    return "com.idega.user";
  }

  public void setControlEventModel(IWPresentationEvent model){
    _controlEvent = model;
    searchForm.setControlEventModel(model);
  }

  public void setControlTarget(String controlTarget){
    _controlTarget = controlTarget;
    searchForm.setControlTarget(controlTarget);

  }


  public void main(IWContext iwc) throws Exception{
    this.empty();
    iwb = getBundle(iwc);
    iwrb = getResourceBundle(iwc);

    Table toolbarTable = new Table(2,3);
    toolbarTable.setCellpadding(0);
    toolbarTable.setCellspacing(0);
    toolbarTable.setWidth(Table.HUNDRED_PERCENT);
    toolbarTable.setHeight(2,Table.HUNDRED_PERCENT);
    toolbarTable.setHeight(1,1);
    toolbarTable.setHeight(3,1);

    IWColor color = new IWColor(207,208,210);//jonni color
    this.setBackgroundColor(color);
    this.getParentPage().setBackgroundColor(color);

    toolbarTable.setColor(1,2,color);
    toolbarTable.setColor(1,1,color.brighter());
    toolbarTable.setColor(2,1,color.brighter());
    toolbarTable.setColor(1,3,color.darker());
    toolbarTable.setColor(2,3,color.darker());


    toolbarTable.setAlignment(2,2,Table.HORIZONTAL_ALIGN_RIGHT);

    add(toolbarTable);

    Table toolbar1 = new Table(6,1);
    toolbar1.setColor(color);
    toolbar1.setCellpadding(0);
    toolbar1.setCellspacing(0);
    toolbar1.setAlignment(Table.HORIZONTAL_ALIGN_LEFT);
    //toolbar1.setWidth(Table.HUNDRED_PERCENT);
    //toolbar1.setHeight(Table.HUNDRED_PERCENT);
    //int iconDimentions = 20;


   //User
    Table button = new Table(2,1);
    button.setCellpadding(0);
    Image iconCrUser = iwb.getImage("new_user.gif");
    button.add(iconCrUser,1,1);
    Text text = new Text(iwrb.getLocalizedString("new.member","New member"));
    text.setFontFace(Text.FONT_FACE_VERDANA);
    text.setFontSize(Text.FONT_SIZE_7_HTML_1);
    Link tLink11 = new Link(text);
    tLink11.setWindowToOpen(CreateUser.class);
    button.add(tLink11,2,1);
    toolbar1.add(button,2,1);

    //Group
    Table button2 = new Table(2,1);
    button2.setCellpadding(0);
    Image iconCrGroup = iwb.getImage("new_group.gif");
    button2.add(iconCrGroup,1,1);
    Text text2 = new Text(iwrb.getLocalizedString("new.group","New group"));
    text2.setFontFace(Text.FONT_FACE_VERDANA);
    text2.setFontSize(Text.FONT_SIZE_7_HTML_1);
    Link tLink12 = new Link(text2);
    tLink12.setWindowToOpen(CreateGroupWindow.class);
    if (selectedGroupProviderStateId != null)
      tLink12.addParameter(CreateGroupWindow.SELECTED_GROUP_PROVIDER_PRESENTATION_STATE_ID_KEY, selectedGroupProviderStateId);
    button2.add(tLink12,2,1);
    toolbar1.add(button2,3,1);
    
		//Search temp
		Table button3 = new Table(2,1);
		button3.setCellpadding(0);
		Image iconSearch = iwb.getImage("new_group.gif");
		button3.add(iconSearch,1,1);
		Text text3 = new Text(iwrb.getLocalizedString("button.search","Search"));
		text3.setFontFace(Text.FONT_FACE_VERDANA);
		text3.setFontSize(Text.FONT_SIZE_7_HTML_1);
		Link tLink13 = new Link(text3);
		if (userApplicationMainAreaStateId != null)
			tLink13.addParameter(UserApplicationMainArea.USER_APPLICATION_MAIN_AREA_PS_KEY, userApplicationMainAreaStateId);
		tLink13.setWindowToOpen(SearchWindow.class);
		button3.add(tLink13,2,1);
		toolbar1.add(button3,4,1);

  
    //Search temp
    Table button4 = new Table(2,1);
    button4.setCellpadding(0);
    Image iconMassMoving = iwb.getImage("new_group.gif");
    button4.add(iconMassMoving,1,1);
    Text text4 = new Text(iwrb.getLocalizedString("button.massMoving","Move"));
    text4.setFontFace(Text.FONT_FACE_VERDANA);
    text4.setFontSize(Text.FONT_SIZE_7_HTML_1);
    Link tLink14 = new Link(text4);
    if (userApplicationMainAreaStateId != null) {
      tLink14.addParameter(UserApplicationMainArea.USER_APPLICATION_MAIN_AREA_PS_KEY, userApplicationMainAreaStateId);
    }
    if (selectedGroupProviderStateId  != null) {
      tLink14.addParameter(MassMovingWindow.SELECTED_GROUP_PROVIDER_PRESENTATION_STATE_ID_KEY, selectedGroupProviderStateId);
    }
    tLink14.setWindowToOpen(MassMovingWindow.class);
    button4.add(tLink14,2,1);
    toolbar1.add(button4,5,1);  
  
		//Member exchange window temp
		Table button5 = new Table(2,1);
		button5.setCellpadding(0);
		Image iconExchange = iwb.getImage("new_group.gif");
		button5.add(iconExchange,1,1);
		Text text5 = new Text(iwrb.getLocalizedString("button.club_member_exchange","Club exchange"));
		text5.setFontFace(Text.FONT_FACE_VERDANA);
		text5.setFontSize(Text.FONT_SIZE_7_HTML_1);
		Link tLink15 = new Link(text5);
		//TODO Eiki add somekind of plugin lookup for toolbar items
		tLink15.setWindowToOpen("is.idega.idegaweb.member.presentation.ClubMemberExchangeWindow");
		button5.add(tLink15,2,1);
		toolbar1.add(button5,6,1);
    
   //finance
   // toolbar1.add( this.getToolbarButtonWithChangeClassEvent(iwrb.getLocalizedString("finance","Finance"), iwb.getImage("finance.gif"), com.idega.block.finance.presentation.AccountViewer.class),4,1);

   //reports
   // toolbar1.add( this.getToolbarButtonWithChangeClassEvent(iwrb.getLocalizedString("reports","Reports"), iwb.getImage("reports.gif"), com.idega.block.reports.presentation.Reporter.class),5,1);

   //To do - stickies
//    toolbar1.add( this.getToolbarButtonWithChangeClassEvent("To do", iwb.getImage("todo.gif"), com.idega.block.news.presentation.News.class),7,1);

   //settings
  //  toolbar1.add( this.getToolbarButtonWithChangeClassEvent(iwrb.getLocalizedString("settings","Settings"), iwb.getImage("settings.gif"),com.idega.block.news.presentation.News.class ),4,1);

   //view
   //dropdownmenu
   // toolbar1.add( this.getToolbarButtonWithChangeClassEvent("Yfirlit", iwb.getImage("views.gif"), com.idega.block.news.presentation.News.class),7,1);

   //search
   IWLocation location = (IWLocation)this.getLocation().clone();
   location.setSubID(1);
   searchForm.setLocation(location,iwc);
   searchForm.setArtificialCompoundId(getCompoundId(),iwc);
   searchForm.setHorizontalAlignment("right");
   toolbarTable.add(searchForm,2,2);

/*
   Text text3 = new Text("&nbsp;Reset");
   text3.setFontFace(Text.FONT_FACE_VERDANA);
   text3.setFontSize(Text.FONT_SIZE_7_HTML_1);
   Link resetLink = new Link(text3);
   resetLink.addEventModel(new ResetPresentationEvent());
   if(_controlEvent != null){
     resetLink.addEventModel(_controlEvent);
   }
   if(_controlTarget != null){
     resetLink.setTarget(_controlTarget);
   }

   toolbar1.add(resetLink,8,1);*/

   //    toolbarTable.add(toolbar1,1,2);
//	this.add(toolbar1);
	toolbarTable.add(toolbar1,1,2);
  }


 protected Table getToolbarButtonWithChangeClassEvent(String textOnButton, Image icon, Class changeClass){
    Table button = new Table(2,1);
    button.setCellpadding(0);
    Text text = new Text(textOnButton);
    text.setFontFace(Text.FONT_FACE_VERDANA);
    text.setFontSize(Text.FONT_SIZE_7_HTML_1);
    Link eventLink = new Link(text);
    button.add(icon,1,1);
    button.add(eventLink,2,1);
    eventLink.addEventModel(new ChangeClassEvent(changeClass));
    if(_controlEvent != null){
      eventLink.addEventModel(_controlEvent);
    }
    if(_controlTarget != null){
      eventLink.setTarget(_controlTarget);
    }

    return button;
 }
 
 public void setTitle(String title){
 	this.title = title;
 }


	/**
	 * @param string
	 */
	public void setUserApplicationMainAreaStateId(String string) {
		userApplicationMainAreaStateId = string;
	}

}