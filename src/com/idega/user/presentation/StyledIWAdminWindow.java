package com.idega.user.presentation;


import com.idega.block.help.presentation.Help;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.Window;
import com.idega.user.business.UserBusiness;

public class StyledIWAdminWindow extends Window {

private final static String IW_BUNDLE_IDENTIFIER="com.idega.user"; 
public static final String MEMBER_HELP_BUNDLE_IDENTIFIER = "is.idega.idegaweb.member.isi";

private IWBundle iwb;
public IWBundle iwbCore;
public IWBundle iwbUser;
private IWResourceBundle iwrb;
private Form adminForm;
private Table adminTable;
private Table headerTable;
private Table mainTable;
private boolean merged = true;
private boolean displayEmpty = false;

private String rightWidth = "160";
private String method = "post";
private int _cellPadding = 0;

private Page parentPage;
private String styleScript = "UserApplicationStyle.css";
private String styleSrc = "";
private String inputTextStyle = "text";
private String backTableStyle = "back";
private String mainTableStyle = "main";
private String bannerTableStyle = "banner";

private UserBusiness userBusiness = null;
private boolean titleIsSet = false;
private Text adminTitle = null; 
private Image helpImage = null;


	public StyledIWAdminWindow(){
		super();
	}

	public StyledIWAdminWindow(String name){
		super(name);
	}

	public StyledIWAdminWindow(int width, int heigth) {
		super(width,heigth);
	}

	public StyledIWAdminWindow(String name,int width,int height){
		super(name,width,height);
	}

	public StyledIWAdminWindow(String name,String url){
		super(name,url);
	}

	public StyledIWAdminWindow(String name, int width, int height, String url){
		super(name,width,height,url);
	}

	public StyledIWAdminWindow(String name,String classToInstanciate,String template){
		super(name,classToInstanciate,template);
	}

	public StyledIWAdminWindow(String name,Class classToInstanciate,Class template){
		super(name,classToInstanciate,template);
	}

	public StyledIWAdminWindow(String name,Class classToInstanciate){
		super(name,classToInstanciate);
	}

	public Form getUnderlyingForm(){
		return adminForm;
	}

	private void makeTables()  {
    
		adminForm = new Form();
		adminForm.setMethod(method);

		headerTable = new Table();
		headerTable.setVerticalAlignment("top");
		headerTable.setCellpadding(0);
		headerTable.setCellspacing(0);
		headerTable.setStyleClass(bannerTableStyle);
		headerTable.setWidth("100%");
		headerTable.setAlignment(2,1,"right");
		headerTable.setVerticalAlignment(1,1,"top");
		if(titleIsSet) {
			headerTable.add(getAdminTitle(),2,1);
		}

		mainTable = new Table();
		mainTable.setStyleClass(backTableStyle);
		mainTable.setCellpadding(_cellPadding);
		mainTable.setAlignment("center");
		mainTable.setWidth("100%");
		mainTable.setHeight("100%");
		mainTable.setCellspacing(0);
		mainTable.setHeight(1,1,"6");
		mainTable.setWidth(1,2,"6");
		mainTable.setVerticalAlignment(2,2,"top");
		adminForm.add(mainTable);
	}
	/**
	 * Adds an image to the top banner of the page
	 * @param topImage - is added to the header.
	 */
	public void addTopImage(Image topImage) {
		headerTable.add(topImage);
	}

	public void add(PresentationObject obj, IWContext iwc) {
		userBusiness = getUserBusiness(iwc);
		Image topImage = userBusiness.getTopImage(iwc);
		if( !displayEmpty ){
			if(adminTable==null){
				makeTables();
				addTopImage(topImage);
				super.add(headerTable);
				super.add(mainTable);
			}
			mainTable.add(obj,2,2);
		}
		else super.add(obj);
	}
	
	public void addTitle(String title) {
		adminTitle = new Text(title+"&nbsp;&nbsp;");
//		adminTitle.setBold();
		adminTitle.setFontColor("#FFFFFF");
//		adminTitle.setFontSize("3");
//		adminTitle.setFontFace(Text.FONT_FACE_ARIAL);
		super.setTitle(title);
		titleIsSet = true;

		headerTable.add(adminTitle,2,1);
	}

	public void addTitle(String title,String style) {
		adminTitle = new Text(title+"&nbsp;&nbsp;");
		adminTitle.setFontStyle(style);
		super.setTitle(title);
		titleIsSet = true;
	}
	public Text getAdminTitle() {
		return adminTitle;
	}
	public Text formatText(String s, boolean bold){
		Text T= new Text();
		if ( s != null ) {
			T= new Text(s);
			if ( bold )
				T.setBold();
//			T.setFontColor("#000000");
//			T.setFontSize(Text.FONT_SIZE_7_HTML_1);
//			T.setFontFace(Text.FONT_FACE_VERDANA);
		}
		return T;
	}

	public void formatText(Text text, boolean bold){
		if ( bold )
			text.setBold();
//		text.setFontColor("#000000");
//		text.setFontSize(Text.FONT_SIZE_7_HTML_1);
//		text.setFontFace(Text.FONT_FACE_VERDANA);
	}

	public Text formatText(String s) {
		Text T = formatText(s,true);
		return T;
	}

	public Text formatHeadline(String s) {
		Text T= new Text();
		if ( s != null ) {
			T= new Text(s);
			T.setBold();
//			T.setFontColor("#000000");
//			T.setFontSize(Text.FONT_SIZE_10_HTML_2);
//			T.setFontFace(Text.FONT_FACE_VERDANA);
		}
		return T;
	}
	public void _main(IWContext iwc)throws Exception{
		iwb = getBundle(iwc);
		userBusiness = getUserBusiness(iwc);
		parentPage = this.getParentPage();
		styleSrc = userBusiness.getUserApplicationStyleSheet(parentPage, iwc);
		parentPage.addStyleSheetURL(styleSrc);
		
		super._main(iwc);
	}
	public void main(IWContext iwc)throws Exception{
	}
	public Help getHelp(String helpTextKey) {
	 	Help help = new Help();
	 	helpImage = new Image();
	 	helpImage.setSrc("/idegaweb/bundles/com.idega.user.bundle/resources/help.gif");
 	  help.setHelpTextBundle( MEMBER_HELP_BUNDLE_IDENTIFIER);
	  help.setHelpTextKey(helpTextKey);
	  help.setImage(helpImage);
	  return help;
	}


	
//	public UserBusiness getUserBusiness(IWApplicationContext iwac) throws RemoteException {
//		return (UserBusiness) com.idega.business.IBOLookup.getServiceInstance(iwac, UserBusiness.class);
//	}
	
	protected UserBusiness getUserBusiness(IWApplicationContext iwc) {
			if (userBusiness == null) {
				try {
					userBusiness = (UserBusiness) com.idega.business.IBOLookup.getServiceInstance(iwc, UserBusiness.class);
				}
				catch (java.rmi.RemoteException rme) {
					throw new RuntimeException(rme.getMessage());
				}
			}
			return userBusiness;
		}


	public String getBundleIdentifier(){
		return IW_BUNDLE_IDENTIFIER;
	}

}
