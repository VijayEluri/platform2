//idega 2000 Grimur Jonsson - Tryggvi Larusson
/*
*Copyright 2000-2001 idega.is All Rights Reserved.
*/

package is.idega.idegaweb.golf.login.presentation;

import is.idega.idegaweb.golf.entity.Member;
import is.idega.idegaweb.golf.login.business.GolfLoginBusiness;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import is.idega.idegaweb.golf.block.login.business.AccessControl;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.Parameter;
import com.idega.presentation.ui.PasswordInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

/**
 * Title:        Login
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>,<a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.1
 */
public class GolfLogin extends Block{

String backgroundImageUrl = "";
String loginImageUrl = "";
String newUserImageUrl = "";
String logOutImageUrl = "";
String tryAgainImageUrl = "";
String loginWidth = "";
String loginHeight = "";
String color = "";
String loggedOnTextSize = "";
String loggedOnTextColor = "";
String userText = "";
String userTextColor = "";
String userTextSize = "";

String passwordText = "";
String passwordTextColor = "";
String passwordTextSize = "";
boolean vertical = false;
boolean noLoginImage=true;
String styleAttribute = "font-size: 10pt";
int inputLength = 10;
public static String controlParameter;
public boolean showNewUserImage = true;
private String forgotPasswordUrl;

private final static String IW_BUNDLE_IDENTIFIER="com.idega.projects.golf.login";
protected IWResourceBundle iwrb;
protected IWBundle iwb;

	public GolfLogin() {
		super();
		setDefaultValues();
	}

	private void setDefaultValues() {
		loginImageUrl="login.gif";
		newUserImageUrl="register.gif";
		loginWidth="148";
		loginHeight="89";
		userText = "Notandi";
		passwordText = "Lykilor�";
	}

	public void setVertical() {
		vertical = true;
	}

	public void setHorizontal() {
		vertical = false;
	}

	public void setStyle(String styleAttribute){
                this.styleAttribute=styleAttribute;
        }

        public void setInputLength(int inputLength) {
                this.inputLength=inputLength;
        }

        public void setLoggedOnTextSize(String size) {
		loggedOnTextSize = size;
	}
	public void setLoggedOnTextColor(String color) {
		loggedOnTextColor = color;
	}

	public void setUserText(String text) {
		userText = text;
	}
	public void setUserTextSize(String size) {
		userTextSize = size;
	}
	public void setUserTextColor(String color) {
		userTextColor = color;
	}
	public void setPasswordText(String text) {
		passwordText = text;
	}

	public void setPasswordTextSize(String size) {
		passwordTextSize = size;
	}
	public void setPasswordTextColor(String color) {
		passwordTextColor = color;
	}
	public void setColor(String color) {
		this.color = color;
	}

	public void setHeight(String height) {
		loginHeight = height;
	}

	public void setWidth(String width) {
		loginWidth = width;
	}

	public void setBackgroundImageUrl(String url) {
		backgroundImageUrl = url;
	}

	public void setLoginImageUrl(String url) {
                noLoginImage=false;
		loginImageUrl = url;
	}

	public void setNewUserImageUrl(String url) {
                noLoginImage=false;
		newUserImageUrl = url;
	}

	public void setTryAgainImageUrl(String url) {
		tryAgainImageUrl = url;
	}

	public void setLogOutImageUrl(String url) {
		logOutImageUrl = url;
	}
	public static boolean isAdmin(IWContext modinfo)throws Exception{
            return AccessControl.isAdmin(modinfo);
	}


	public static is.idega.idegaweb.golf.entity.Member getMember(IWContext modinfo){
		return (is.idega.idegaweb.golf.entity.Member)AccessControl.getMember(modinfo);
	}

	private void isNotSignedOn(String what) {

		Form myForm = new Form();
                myForm.setEventListener(GolfLoginBusiness.class);

		Table TveirTable = new Table(1,1);
			if (!(color.equals(""))) {
				TveirTable.setColor(color);
			}
			TveirTable.setBackgroundImage(1,1,iwrb.getImage(backgroundImageUrl));
			TveirTable.setAlignment(1,1,"center");
			TveirTable.setWidth(loginWidth);
			TveirTable.setHeight(loginHeight);
			TveirTable.setCellpadding(0);
			TveirTable.setCellspacing(0);

		Table TrirTable = new Table(1,2);
			TrirTable.setBorder(0);
			TrirTable.setWidth("100%");
			if (!(color.equals(""))) {
				TrirTable.setColor(color);
			}
			TrirTable.setCellpadding(0);
			TrirTable.setCellspacing(0);
			//debug eiki tok �etta �t
                        //TrirTable.setBackgroundImage(1,2,new Image(""));
			TrirTable.setColor(1,2,"#FFFFFF");
			TveirTable.add(TrirTable,1,1);

		Table uppi = new Table(1,1);
			if (!(color.equals(""))) {
				uppi.setColor(color);
			}
			uppi.setBorder(0);
			uppi.setCellpadding(0);
			uppi.setCellspacing(0);
			uppi.setAlignment(1,1,"center");
			uppi.setVerticalAlignment(1,1,"middle");
			uppi.setWidth("100%");
			TrirTable.add(uppi,1,1);

		Table nidri = new Table(1,1);
			if (!(color.equals(""))) {
				nidri.setColor(color);
			}
			nidri.setBorder(0);
			nidri.setAlignment(1,1,"center");
			nidri.setVerticalAlignment(1,1,"middle");
			nidri.setWidth("100%");
			TrirTable.add(nidri,1,2);



		Text textinn = new Text("");
			textinn.setFontSize(1);
			textinn.setBold();
		if (what.equals("empty")) {
			textinn.addToText(iwrb.getLocalizedString("id_needed","Identification needed"));
		}
		else if (what.equals("toBig")) {
			textinn.addToText(iwrb.getLocalizedString("wrong_format","Wrong format"));
		}
		uppi.add(textinn,1,1);
		nidri.add(new SubmitButton("Reyna aftur"),1,1);

		myForm.add(TveirTable);
		add(myForm);

	}

	private void startState(){

        String color = "";
        String loginWidth = "148";
        String loginHeight = "90";
        String backgroundImageUrl = "loginback.gif";
        String userText = iwrb.getLocalizedString("user","User");
        String userTextSize = "";
        String userTextColor = "";

        String passwordText = iwrb.getLocalizedString("password","Password");
        String passwordTextColor = "";
        String passwordTextSize = "";

        boolean vertical = false;

        String loginImageUrl = "login.gif";
        String newUserImageUrl = "register.gif";
        String tryAgainImageUrl = "tryagain.gif";

        String userImageUrl = null;
          userImageUrl = "username.gif";
        String passwordImageUrl = null;
          passwordImageUrl = "password.gif";

		Form myForm = new Form();
                myForm.setEventListener(GolfLoginBusiness.class);

			myForm.setMethod("post");
			myForm.maintainAllParameters();

		Table TveirTable = new Table(2,1);
			TveirTable.setAlignment(1,1,"center");
			if (!(color.equals(""))) {
			TveirTable.setColor(color);
			}
			TveirTable.setWidth(loginWidth);
			TveirTable.setHeight(loginHeight);
			TveirTable.setCellpadding(0);
			TveirTable.setCellspacing(0);

                        TveirTable.add(iwrb.getImage("arc.gif"),2,1);
                        TveirTable.setVerticalAlignment(2,1,"top");


		Table TrirTable = new Table(1,2);
			TrirTable.setWidth("100%");
			TrirTable.setBackgroundImage(1,1,iwrb.getImage(backgroundImageUrl));
			TrirTable.setHeight("100%");
			if (!(color.equals(""))) {
			TrirTable.setColor(color);
			}
			TrirTable.setCellpadding(0);
			TrirTable.setCellspacing(0);
                        //debug eiki �g t�k �etta �t
			//TrirTable.setBackgroundImage(1,2,iwrb.getImage(""));



			Text loginTexti = null;
                            if (userText != null) {
                                loginTexti = new Text(userText);
				if (!(userTextSize.equals(""))) {
					loginTexti.setFontSize(Integer.parseInt(userTextSize));
				}
				if (!(userTextColor.equals(""))) {
					loginTexti.setFontColor(userTextColor);
				}
                            }
			Text passwordTexti = null;
                            if (passwordText != null) {
                                passwordTexti = new Text(passwordText);
				if (!(passwordTextSize.equals(""))) {
					passwordTexti.setFontSize(Integer.parseInt(passwordTextSize));
				}
				if (!(passwordTextColor.equals(""))) {
					passwordTexti.setFontColor(passwordTextColor);
				}
                            }


                        com.idega.presentation.Image loginImage = null;
                          if (userImageUrl != null) {
                            loginImage = iwrb.getImage(userImageUrl);
                          }

                        com.idega.presentation.Image passwordImage = null;
                          if (passwordImageUrl != null) {
                            passwordImage = iwrb.getImage(passwordImageUrl);
                          }
		Table uppi;


		if (!(vertical)) {
			uppi = new Table(5,2);
			if (!(color.equals(""))) {
			uppi.setColor(color);
			}
			uppi.setCellpadding(0);
			uppi.setCellspacing(0);
			uppi.setWidth("100%");
			TrirTable.add(uppi,1,1);

                        if (loginImage != null) {
                            uppi.add(loginImage,2,1);
                        }
                        else if (loginTexti != null) {
			    uppi.add(loginTexti,2,1);
                        }

			TextInput login = new TextInput("login");
				login.setStyleAttribute("fontsize: 10pt");
				login.setSize(6);
			uppi.add(login,2,2);
			uppi.setAlignment(2,2,"left");

			if (passwordImage != null) {
                            uppi.add(passwordImage,4,1);
                        }
                        else if (passwordTexti != null) {
                            uppi.add(passwordTexti,4,1);
                        }
			PasswordInput passw = new PasswordInput("password");
				passw.setStyleAttribute("fontsize: 10pt");
				passw.setSize(6);
			uppi.add(passw,4,2);


		}
		else {
			uppi = new Table(3,3);
			uppi.setBorder(0);
			if (!(color.equals(""))) {
			uppi.setColor(color);
			}
			uppi.setCellpadding(0);
			uppi.setCellspacing(0);
			uppi.setAlignment("center");
			TrirTable.add(uppi,1,1);


			uppi.add(loginTexti,1,1);
			TextInput login = new TextInput("login");
				login.setStyleAttribute("fontsize: 10pt");
				login.setSize(10);
			uppi.add(login,3,1);

			uppi.add(passwordTexti,1,3);
			PasswordInput passw = new PasswordInput("password");
				passw.setStyleAttribute("fontsize: 10pt");
				passw.setSize(10);
			uppi.add(passw,3,3);
		}


		Table nidri = new Table(3,1);
			nidri.setBorder(0);
			if (!(color.equals(""))) {
			nidri.setColor(color);
			}
			nidri.setVerticalAlignment(1,1,"top");
			nidri.setVerticalAlignment(3,1,"top");
			nidri.setAlignment(1,1,"left");
			nidri.setAlignment(3,1,"right");
			nidri.setWidth("100%");
			nidri.setHeight("100%");
                        nidri.setCellpadding(0);
                        nidri.setCellspacing(0);
			TrirTable.add(nidri,1,2);




			nidri.add(new SubmitButton(iwrb.getImage(loginImageUrl),"tengja"),1,1);
                        if (showNewUserImage) {
  			  nidri.add(new SubmitButton(iwrb.getImage(newUserImageUrl),GolfLoginBusiness.newLoginStateParameter),3,1);
                        }else if (forgotPasswordUrl != null) {
  			  nidri.add(new Link(iwrb.getImage("forgotten.gif"),forgotPasswordUrl),3,1);
                        }



                        nidri.add(new Parameter(GolfLoginBusiness.LoginStateParameter,"login"));


		TveirTable.add(TrirTable);

	myForm.add(TveirTable);
	add(myForm);



	}

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }



        public void main(IWContext modinfo)throws Exception{
            iwrb = getResourceBundle(modinfo);
            iwb = getBundle(modinfo);

            String state = internalGetState(modinfo);
            if(state!=null){
                if(state.equals("loggedon")){
                  isLoggedOn(modinfo);
                }
                else if(state.equals("loggedoff")){
                  startState();
                }
                else if(state.equals("newlogin")){
                  String temp = modinfo.getParameter("login");
                  if(temp != null){
                    if (temp.length() == 11) {
                      loginFailed("toBig");
                    }else if( temp.equals("") || temp.equals(" ") ){
                      loginFailed("empty");
                    }else{
                      loginFailed("");
                    }
                  }else{
                    loginFailed("");
                  }
                }else if(state.equals("loginfailed")){
                  loginFailed("");
                }else{
                  startState();
                }

            }
            else{
                startState();
            }
        }



        public String internalGetState(IWContext modinfo){
            return GolfLoginBusiness.internalGetState(modinfo);
        }

private void isLoggedOn(IWContext modinfo){
        String color = "";
        String loginWidth = "150";
        String loginHeight = "90";
        String backgroundImageUrl = "loginback.gif";
        String userText = iwrb.getLocalizedString("user","User");
        String userTextColor = "";
        String userTextSize = "";
        String passwordText = iwrb.getLocalizedString("password","Password");
        String passwordTextColor = "";
        String passwordTextSize = "";
        boolean vertical = false;

        String loginImageUrl = "login.gif";
        String newUserImageUrl = "register.gif";
        String tryAgainImageUrl = "tryagain.gif";
        String logOutImageUrl = "logout.gif";







		Form myForm = new Form();
                  myForm.setEventListener(GolfLoginBusiness.class);

			myForm.setMethod("post");
			myForm.maintainAllParameters();


		Table TveirTable = new Table(2,1);
			TveirTable.setAlignment(1,1,"center");
			if (!(color.equals(""))) {
			TveirTable.setColor(color);
			}
			TveirTable.setWidth(loginWidth);
			TveirTable.setHeight(loginHeight);
			TveirTable.setCellpadding(0);
			TveirTable.setCellspacing(0);

                        TveirTable.add(iwrb.getImage("arc.gif"),2,1);
                        TveirTable.setVerticalAlignment(2,1,"top");


		Table TrirTable = new Table(1,2);
			TrirTable.setWidth("100%");
			TrirTable.setBackgroundImage(1,1,iwrb.getImage(backgroundImageUrl));
			TrirTable.setHeight("100%");
			if (!(color.equals(""))) {
			TrirTable.setColor(color);
			}
			TrirTable.setCellpadding(0);
			TrirTable.setCellspacing(0);
			                        //debug eiki �g t�k �etta �t
			//TrirTable.setBackgroundImage(1,2,iwrb.getImage(""));


			Text loginTexti = new Text(userText);
				if (!(userTextSize.equals(""))) {
					loginTexti.setFontSize(Integer.parseInt(userTextSize));
				}
				if (!(userTextColor.equals(""))) {
					loginTexti.setFontColor(userTextColor);
				}
			Text passwordTexti = new Text(passwordText);
				if (!(passwordTextSize.equals(""))) {
					passwordTexti.setFontSize(Integer.parseInt(passwordTextSize));
				}
				if (!(passwordTextColor.equals(""))) {
					passwordTexti.setFontColor(passwordTextColor);
				}

		Table uppi = new Table();

			uppi.setBorder(0);
      uppi.setWidth("100%");
      Text user = new Text();
			user.setBold();
			user.setFontSize(1);

      Member member = (Member) modinfo.getSession().getAttribute("member_login");
      user.addToText(member.getName());

      Link hlekkur = new Link(user,"/createlogin.jsp?kt="+member.getSocialSecurityNumber());

      uppi.add(hlekkur);
      uppi.setAlignment(1,1,"center");


      TrirTable.add(uppi,1,1);

		  Table nidri = new Table(1,1);
			nidri.setBorder(0);
			if (!(color.equals(""))) {
			nidri.setColor(color);
			}
			nidri.setVerticalAlignment(1,1,"top");
			nidri.setAlignment(1,1,"left");
			nidri.setWidth("100%");
			nidri.setHeight("100%");
                        nidri.setCellpadding(0);
                        nidri.setCellspacing(0);
			TrirTable.add(nidri,1,2);




			nidri.add(new SubmitButton(iwrb.getImage(logOutImageUrl),""),1,1);
                        nidri.add(new HiddenInput("action","�tskr�ning"));
                        nidri.add(new Parameter(GolfLoginBusiness.LoginStateParameter,"logoff"));
		TveirTable.add(TrirTable);

	myForm.add(TveirTable);
	add(myForm);


	}

	public void isNotLoggedOn() {
        String color = "";
        String loginWidth = "148";
        String loginHeight = "90";
        String backgroundImageUrl = "loginback.gif";
        String userText = "Notandi";
        String userTextColor = "";
        String userTextSize = "";
        String passwordText = "Lykilor�";
        String passwordTextColor = "";
        String passwordTextSize = "";
        boolean vertical = false;

        String loginImageUrl = "login.gif";
        String newUserImageUrl = "register.gif";
        String tryAgainImageUrl = "tryagain.gif";


		Form myForm = new Form();
 			myForm.setMethod("post");
			myForm.maintainAllParameters();


		Table TveirTable = new Table(2,1);
			TveirTable.setAlignment(1,1,"center");
			if (!(color.equals(""))) {
			TveirTable.setColor(color);
			}
			TveirTable.setWidth(loginWidth);
			TveirTable.setHeight(loginHeight);
			TveirTable.setCellpadding(0);
			TveirTable.setCellspacing(0);

                        TveirTable.add(iwrb.getImage("arc.gif"),2,1);
                        TveirTable.setVerticalAlignment(2,1,"top");


		Table TrirTable = new Table(1,2);
			TrirTable.setWidth("148");
			TrirTable.setBackgroundImage(1,1,iwrb.getImage(backgroundImageUrl));
			TrirTable.setHeight("100%");
			if (!(color.equals(""))) {
			TrirTable.setColor(color);
			}
			TrirTable.setCellpadding(0);
			TrirTable.setCellspacing(0);
			                        //debug eiki �g t�k �etta �t
			//TrirTable.setBackgroundImage(1,2,iwrb.getImage(""));


			Text loginTexti = new Text(userText);
				if (!(userTextSize.equals(""))) {
					loginTexti.setFontSize(Integer.parseInt(userTextSize));
				}
				if (!(userTextColor.equals(""))) {
					loginTexti.setFontColor(userTextColor);
				}
			Text passwordTexti = new Text(passwordText);
				if (!(passwordTextSize.equals(""))) {
					passwordTexti.setFontSize(Integer.parseInt(passwordTextSize));
				}
				if (!(passwordTextColor.equals(""))) {
					passwordTexti.setFontColor(passwordTextColor);
				}

		Table uppi = new Table();

			uppi.setBorder(0);
      uppi.setWidth("100%");
      Text failed = new Text(iwrb.getLocalizedString("loginfailed","Login failed"));
          failed.setFontSize(1);
      uppi.add(failed);
      uppi.setAlignment(1,1,"center");
		  TrirTable.add(uppi,1,1);

		Table nidri = new Table(1,1);
			nidri.setBorder(0);
			if (!(color.equals(""))) {
			nidri.setColor(color);
			}
			nidri.setVerticalAlignment(1,1,"top");
			nidri.setAlignment(1,1,"left");
			nidri.setWidth("100%");
			nidri.setHeight("100%");
                        nidri.setCellpadding(0);
                        nidri.setCellspacing(0);
			TrirTable.add(nidri,1,2);




			nidri.add(new SubmitButton(iwrb.getImage(tryAgainImageUrl),"Reyna aftur"),1,1);

		TveirTable.add(TrirTable);

	myForm.add(TveirTable);
	add(myForm);


	}

	private void loginFailed(String what) {
        String color = "";
        String loginWidth = "148";
        String loginHeight = "90";
        String backgroundImageUrl = "loginback.gif";
        String userText = iwrb.getLocalizedString("user","User");
        String userTextColor = "";
        String userTextSize = "";
        String passwordText = iwrb.getLocalizedString("password","Password");
        String passwordTextColor = "";
        String passwordTextSize = "";
        boolean vertical = false;

        String loginImageUrl = "login.gif";
        String newUserImageUrl = "register.gif";
        String tryAgainImageUrl = "tryagain.gif";







		Form myForm = new Form();
                 myForm.setEventListener(GolfLoginBusiness.class);

			myForm.setMethod("post");
			myForm.maintainAllParameters();


		Table TveirTable = new Table(2,1);
			TveirTable.setAlignment(1,1,"center");
			if (!(color.equals(""))) {
			TveirTable.setColor(color);
			}
			TveirTable.setWidth(loginWidth);
			TveirTable.setHeight(loginHeight);
			TveirTable.setCellpadding(0);
			TveirTable.setCellspacing(0);

                        TveirTable.add(iwrb.getImage("arc.gif"),2,1);
                        TveirTable.setVerticalAlignment(2,1,"top");

		Table TrirTable = new Table(1,2);
			TrirTable.setWidth("148");
			TrirTable.setBackgroundImage(1,1,iwrb.getImage(backgroundImageUrl));
			TrirTable.setHeight("100%");
			if (!(color.equals(""))) {
			TrirTable.setColor(color);
			}
			TrirTable.setCellpadding(0);
			TrirTable.setCellspacing(0);
                        //debug eiki �g t�k �etta �t
			//TrirTable.setBackgroundImage(1,2,iwrb.getImage(""));


			Text loginTexti = new Text(userText);
				if (!(userTextSize.equals(""))) {
					loginTexti.setFontSize(Integer.parseInt(userTextSize));
				}
				if (!(userTextColor.equals(""))) {
					loginTexti.setFontColor(userTextColor);
				}
			Text passwordTexti = new Text(passwordText);
				if (!(passwordTextSize.equals(""))) {
					passwordTexti.setFontSize(Integer.parseInt(passwordTextSize));
				}
				if (!(passwordTextColor.equals(""))) {
					passwordTexti.setFontColor(passwordTextColor);
				}

		Table uppi = new Table();

	  uppi.setBorder(0);
    uppi.setWidth("100%");
    Text failed =  new Text(iwrb.getLocalizedString("loginfailed","Login failed"));
    failed.setFontSize(1);
    if (what.equals("empty")) {
            failed.setText(iwrb.getLocalizedString("id_needed","Identification needed"));
    }
    else if (what.equals("toBig")) {
            failed.setText(iwrb.getLocalizedString("wrong_format","Wrong format"));
    }
    uppi.add(failed);
    uppi.setAlignment(1,1,"center");


			TrirTable.add(uppi,1,1);

		  Table nidri = new Table(1,1);
			nidri.setBorder(0);
			if (!(color.equals(""))) {
			nidri.setColor(color);
			}
			nidri.setVerticalAlignment(1,1,"top");
			nidri.setAlignment(1,1,"left");
			nidri.setWidth("100%");
			nidri.setHeight("100%");
                        nidri.setCellpadding(0);
                        nidri.setCellspacing(0);
			TrirTable.add(nidri,1,2);




			nidri.add(new SubmitButton( iwrb.getImage(tryAgainImageUrl),"Reyna aftur"),1,1);
      nidri.add(new Parameter(GolfLoginBusiness.LoginStateParameter,"tryagain"));

		TveirTable.add(TrirTable);

	myForm.add(TveirTable);
	add(myForm);



	}


 ///// additional mothods  /////////


	private void logOut(IWContext modinfo) throws Exception{
    	  GolfLoginBusiness.logOut2(modinfo);
        }


    public void setForgotPasswordUrl(String url) {
      this.forgotPasswordUrl = url;
    }

}
