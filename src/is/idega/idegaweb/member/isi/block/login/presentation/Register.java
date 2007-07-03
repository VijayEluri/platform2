/*
 * Created on Feb 3, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package is.idega.idegaweb.member.isi.block.login.presentation;

import is.idega.idegaweb.member.util.IWMemberConstants;
import java.rmi.RemoteException;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import javax.ejb.FinderException;
import com.idega.block.login.business.LoginBusiness;
import com.idega.business.IBOLookup;
import com.idega.core.accesscontrol.data.LoginTable;
import com.idega.core.accesscontrol.data.LoginTableHome;
import com.idega.core.contact.data.Email;
import com.idega.core.contact.data.EmailHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.PasswordInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.user.business.UserBusiness;
import com.idega.user.data.User;
import com.idega.util.SendMail;

/**
 * @author jonas
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Register extends Block {
	
	private IWContext _iwc = null;
	private IWResourceBundle _iwrb;
	private IWBundle _iwb;
	
	private String _kt = null;
	
	private boolean _mailError = false;
	
	//public final static int PERSONAL_NUMBER_NOT_FOUND = 1000;
	
	public final static String IW_BUNDLE_IDENTIFIER = "is.idega.idegaweb.member.isi";
	private boolean cannotRegisterUnlessAlreadyInAGroup = false;
	//private static final int[] KT_MULT = {3, 2, 7, 6, 5, 4, 3, 2}; 
	
	public void control() {
		System.out.println("Processing registration");
		PresentationObject po;
		String message;
		if(this._iwc.getParameter("reg_personal_number")!=null) {
			message = processStage1();
			if(message==null) {
				System.out.println("Stage 1 completed");
				po = getStage2Page(null);
			} else {
				System.out.println("Stage 1 redisplayed");
				po = getStage1Page(message);
			}
		} else if (this._iwc.getParameter("reg_password")!=null) {
			message = processStage2();
			if(message==null) {
				System.out.println("Stage 2 completed");
				po = getStage3Page(null);
			} else {
				if(this._mailError) {
					System.out.println("Stage 2 completed");
					po = getStage3Page(message);
				} else {
					System.out.println("Stage 2 redisplayed");
					po = getStage2Page(message);
				}
			}
		} else {
			System.out.println("Stage 1 - starting registration");
			po = getStage1Page(null);
		}
		
		add(po);
	}
	
	private PresentationObject getStage1Page(String message) {
		getParentPage().setTitle(this._iwrb.getLocalizedString("register.stage1_title", "Registration - SSN"));
		
		Table T = new Table();
		int row = 1;
		if(message!=null) {
			T.mergeCells(1,row,2,row);
			T.add(message, 1, row++);
		}
		String instructions = this._iwrb.getLocalizedString("register.stage1_instructions", "Please provide your SSN, this will be your username when you log in");
		T.mergeCells(1, row, 2, row);
		T.add(instructions, 1, row++);
		String labelPersonNumber = this._iwrb.getLocalizedString("register.person_number", "SSN");
		TextInput inputPersonalNumber = new TextInput("reg_personal_number");
		if (this._iwc.isParameterSet("reg_personal_number")) {
			inputPersonalNumber.setContent(this._iwc.getParameter("reg_personal_number"));
		}
		T.add(labelPersonNumber, 1, row);
		T.add(inputPersonalNumber, 2, row++);
		
		insertContinueCancelButtons(T, row++);

		Form myForm = new Form();
		myForm.add(T);
		return myForm;
	}
	
	private void insertContinueCancelButtons(Table table, int row) {
		//CloseButton cancel =
		//	new CloseButton(_iwrb.getLocalizedImageButton("cancel", "Cancel"));
		
		SubmitButton cont =
			new SubmitButton(this._iwrb.getLocalizedImageButton("send", "Send"), "send");
		
	//	table.add(cancel, 1, row);
		table.add(cont, 2, row);
	}
	
	private String processStage1() {
		String kt = this._iwc.getParameter("reg_personal_number");
		kt = getNumericalsOnly(kt);
		if(kt.length()!=10) {
			return this._iwrb.getLocalizedString("register.personal_number_invalid", "SSN invalid");
		}

			
			System.out.println("getting user with kt PN: " + kt);
			User user = null;
			try {
				user = getUserBusiness().getUser(kt);
				this._kt = kt;
			}
			catch (FinderException e) {
				return this._iwrb.getLocalizedString("register.pn_not_found", "No user found with given SSN");
			}
			catch (RemoteException e) {
				e.printStackTrace();
				return this._iwrb.getLocalizedString("register.error_looking_pn", "Error searching for user by SSN");
			}
			
			
			if(this.cannotRegisterUnlessAlreadyInAGroup){
				Collection parentGroups = user.getParentGroups();
				if(parentGroups!=null && !parentGroups.isEmpty()){
					this._kt = kt;
					return null;
				}
				else{
					return this._iwrb.getLocalizedString("register.user_not_in_any_group", "User must be a member of a group (club).");
				}
			
				
			}
			LoginTable lt = getLoginTable(user, false);
			if(lt!=null) {
				return this._iwrb.getLocalizedString("register.user_already_has_login", "You already have a login, can not create another");
			}
			
		
		return null;
	}
	
	private PresentationObject getStage2Page(String message) {
		getParentPage().setTitle(this._iwrb.getLocalizedString("register.stage2_title", "Registration - login"));
		
		Table T = new Table();
		int row = 1;
		if(message!=null) {
			T.mergeCells(1,row,2,row);
			T.add(message, 1, row++);
		}
		
		String labelPassword = this._iwrb.getLocalizedString("register.password", "Password");
		TextInput inputPassword = new PasswordInput("reg_password");
		String labelPasswordConfirmed = this._iwrb.getLocalizedString("register.password_confirmed", "Retyps Password");
		TextInput inputPasswordConfirmed = new PasswordInput("reg_password_confirmed");
		String labelEmail = this._iwrb.getLocalizedString("register.email_address", "Email");
		TextInput inputEmail = new TextInput("reg_email");
		String labelHintQuestion = this._iwrb.getLocalizedString("register.hint_question", "Hint question");
		TextInput inputHintQuestion = new TextInput("reg_hint_question");
		String labelHintAnswer = this._iwrb.getLocalizedString("register.hint_answer", "Answer");
		TextInput inputHintAnswer = new TextInput("reg_hint_answer");
		
		String textHint = this._iwrb.getLocalizedString("reg_hint_instructions", "Provide a question that only you know the answer of, and the answer. This will help you if you forget your password (optional)");
		
		T.add(labelPassword, 1, row);
		T.add(inputPassword, 2, row++);
		T.add(labelPasswordConfirmed, 1, row);
		T.add(inputPasswordConfirmed, 2, row++);
		T.add(labelEmail, 1, row);
		T.add(inputEmail, 2, row++);
		T.mergeCells(1, row, 2, row);
		T.add(textHint, 1, row++);
		T.add(labelHintQuestion, 1, row);
		T.add(inputHintQuestion, 2, row++);
		T.add(labelHintAnswer, 1, row);
		T.add(inputHintAnswer, 2, row++);
		
		
		insertContinueCancelButtons(T, row++);
		
		Form myForm = new Form();
		myForm.add(T);
		if(this._kt!=null) {
			HiddenInput hiKT = new HiddenInput("reg_kt", this._kt);
			myForm.add(hiKT);
		} else {
			System.out.println("No kt known to use for stage 2!! bummer.");
		}
		return myForm;
	}
	
	private String processStage2() {
		String password = this._iwc.getParameter("reg_password");
		String passwordConfirmed = this._iwc.getParameter("reg_password_confirmed");
		String email = this._iwc.getParameter("reg_email");
		String hintQ = this._iwc.getParameter("reg_hint_question");
		String hintA = this._iwc.getParameter("reg_hint_answer");
		String kt = this._iwc.getParameter("reg_kt");
		if(kt!=null) {
			this._kt = kt;
		}
		if(password==null || password.length()==0) {
			return this._iwrb.getLocalizedString("register.no_password", "No password entered");
		}
		if(!password.equals(passwordConfirmed)) {
			return this._iwrb.getLocalizedString("register.password_mismatch", "Retyped password different from first password");
		}
		hintQ = hintQ==null?"":hintQ;
		hintA = hintA==null?"":hintA;
		if( (hintQ.length()==0 && hintA.length()!=0) || (hintQ.length()!=0 && hintA.length()==0)) {
			return this._iwrb.getLocalizedString("register.hint_answer_invalid", "Provide both a question and an answer or neither.");
		}
		User user = null;
		boolean ok = true;
		try {
			System.out.println("getting user with kt PN: " + this._kt);
			user = getUserBusiness().getUser(this._kt);
		} catch (Exception e) {
			e.printStackTrace();
			ok = false;
		}
		if(user == null || !ok) {
			return this._iwrb.getLocalizedString("register.error_editing_user", "Error registering, Password not set");
		}
		String msg = null;
		try {
			LoginTable lt = getLoginTable(user, true);
			lt.setUserId(user.getID());
			lt.setUserLogin(kt);
			lt.store();
			if(email!=null && email.length()>0) {
				System.out.println("adding email " + email + " to user " + user.getName());
				Email emailIDO = ((EmailHome) IDOLookup.getHome(Email.class)).create();
				emailIDO.setEmailAddress(email);
				emailIDO.store();
				user.addEmail(emailIDO);
				user.store();
			}
			LoginBusiness.changeUserPassword(user, password);
			msg = sendMessage(user, kt, password, hintQ, hintA);
			if(msg!=null) {
				this._mailError = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return this._iwrb.getLocalizedString("register.error_changing_password", "Error changing password, password unchanged");
		}
		if(hintQ.length() != 0) {
			System.out.println("Setting hint question and answer");
			user.addMetaData("HINT_QUESTION", hintQ.trim());
			user.addMetaData("HINT_ANSWER", hintA.trim());
			user.store();
		}
		return msg;
	}
	
	private PresentationObject getStage3Page(String message) {
		getParentPage().setTitle(this._iwrb.getLocalizedString("register.stage3_title", "Registration - Done"));
		
		Table T = new Table();
		int row = 1;
		if(message!=null) {
			T.add(message, 1, row++);
		}
		
		String done = this._iwrb.getLocalizedString("register.done", "Registration finished.");
		T.add(done, 1, row++);
		
		//CloseButton close =
		//	new CloseButton(_iwrb.getLocalizedImageButton("close", "Close"));
		//T.add(close, 1, row++);
		
		return T;
	}
	
	public void main(IWContext iwc) throws RemoteException {
		this._iwc = iwc;
		this._iwb = getBundle(iwc);
		this._iwrb = getResourceBundle(iwc);
		control();
	}
	
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
	
	private String getNumericalsOnly(String userName) {
		userName = userName.trim();
		StringBuffer buf = new StringBuffer();
		
		int count = userName.length();
		char[] chars = userName.toCharArray();
		for(int i=0; i<count; i++) {
			char c = chars[i];
			if(Character.isDigit(c)) {
				buf.append(c);
			}
		}

		return buf.toString();
	}
	
	private UserBusiness getUserBusiness() throws RemoteException{
		return (UserBusiness) IBOLookup.getServiceInstance(this._iwc.getApplicationContext(),UserBusiness.class);
	}
	
	private String sendMessage(User user, String login, String password, String hintQ, String hintA) {
		String server = this._iwb.getProperty("register.email_server");
		
		if(server == null) {
			return this._iwrb.getLocalizedString("register.no_email_server_configured", "Couldn't send email notification of registration (no server defined)");
		}
		
		String letter;
		Object[] objs;
		if(hintQ!=null && hintQ.length()>0 && hintA!=null && hintA.length()>0 ) {
			// hintstuff in email
			letter = this._iwrb.getLocalizedString("register.email_body_with_hint", "You have been registered on Felix.\nUsername : {0} \nPassword: {1} \nYou supplied a hint question and answer\nQuestion: {2} \nAnswer:   {3} \n");
			objs = new String[] {login,password,hintQ,hintA};
		} else {
			// no hintstuff in email
			letter = this._iwrb.getLocalizedString("register.email_body", "You have been registered on Felix.\nUsername: {0} \nPassword: {1} \n");
			objs = new String[] {login,password};
		}
		
		
		
		if (letter != null) {
			try {
				Collection emailCol = user.getEmails();
				if(emailCol!=null && !emailCol.isEmpty()) {
					Iterator emailIter = user.getEmails().iterator();
					StringBuffer buf = new StringBuffer();
					while(emailIter.hasNext()) {
						String address = ((Email) emailIter.next()).getEmailAddress();
						String body = MessageFormat.format(letter,objs);
						
						System.out.println("Sending registration notification to " + address);
						
						SendMail.send((String)this._iwc.getApplicationAttribute(IWMemberConstants.APPLICATION_PARAMETER_ADMINISTRATOR_MAIN_EMAIL,"felix@felix.is"),
							address,
							"",
							"",
							server,
							this._iwrb.getLocalizedString("register.email_subject", "Felix Registration"),
							body);
						
						if(buf.length()>0) {
							buf.append(",");
						}
						buf.append(address);
					}
					
					return this._iwrb.getLocalizedString("register.email_sent_to", "Registration notification sent to: ") + buf.toString();
				} else {
					return this._iwrb.getLocalizedString("register.no_email_address", "Couldn't send email notification of registration, no address to send to");
				}
			} catch (Exception e) {
				System.out.println("Couldn't send email notification for registration for user " + login);
				e.printStackTrace();
				return this._iwrb.getLocalizedString("register.error_sending_email", "Error sending email notification of registration");
			}
		} else {
			System.out.println("No registration notification letter found, nothing sent to user " + login);
			return this._iwrb.getLocalizedString("register.no_email_letter_configured", "Couldn't send email notification of registration (no letter template defined)");
		}
	}

	private LoginTable getLoginTable(User user, boolean create) {
		LoginTable lt = null;
		try {
			LoginTableHome ltHome = (LoginTableHome) IDOLookup.getHome(LoginTable.class);
			Iterator iter = ltHome.findLoginsForUser(user).iterator();
			while(iter.hasNext()) {
				lt =  (LoginTable) iter.next();
				if(lt.getLoginType()==null || lt.getLoginType().length()==0) {
					System.out.println("found existing logintable");
					break;
				} else {
					lt = null;
				}
			}
			if(lt==null && create) {
				lt = ltHome.create();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lt;
	}
	
	/**
	 * @return Returns the cannotRegisterUnlessAlreadyInAGroup.
	 */
	public boolean cannotRegisterUnlessAlreadyInAGroup() {
		return this.cannotRegisterUnlessAlreadyInAGroup;
	}
	/**
	 * @param cannotRegisterUnlessAlreadyInAGroup The cannotRegisterUnlessAlreadyInAGroup to set.
	 */
	public void setCannotRegisterUnlessAlreadyInAGroup(boolean cannotRegisterUnlessAlreadyInAGroup) {
		this.cannotRegisterUnlessAlreadyInAGroup = cannotRegisterUnlessAlreadyInAGroup;
	}
}
