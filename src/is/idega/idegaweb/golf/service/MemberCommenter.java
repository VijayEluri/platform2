package is.idega.idegaweb.golf.service;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

import is.idega.idegaweb.golf.entity.Member;
import is.idega.idegaweb.golf.entity.MemberHome;
import is.idega.idegaweb.golf.entity.UnionMemberInfo;

import java.sql.SQLException;

import javax.ejb.FinderException;

import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Editor;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;


public class MemberCommenter extends Editor{

  Member eMember = null;
  UnionMemberInfo eUMI = null;
  String AddToImageUrl = "/buttons/add.gif";
  String ShiftImageUrl = "/buttons/replace.gif";
  String CloseImageUrl = "/buttons/close.gif";

	private final static String IW_BUNDLE_IDENTIFIER="com.idega.idegaweb.golf";
  protected IWResourceBundle iwrb;
  protected IWBundle iwb;

	public String getBundleIdentifier(){
	  return IW_BUNDLE_IDENTIFIER;
	}

  public MemberCommenter(){
  }

  protected void control(IWContext modinfo) {
		iwb = getBundle(modinfo);
		iwrb = getResourceBundle(modinfo);
    String member_id = modinfo.getParameter("member_id");
    String union_id = modinfo.getParameter("union_id");
    this.makeView();
    if(member_id != null && union_id != null){
      try{
      eMember = ((MemberHome) IDOLookup.getHomeLegacy(Member.class)).findByPrimaryKey(Integer.parseInt(member_id));
      eUMI = eMember.getUnionMemberInfo(Integer.parseInt(union_id));
      String comment = modinfo.getParameter("mc_comment")!=null?modinfo.getParameter("mc_comment"):"";
        if(modinfo.getParameter("adding")!=null || modinfo.getParameter("adding.x")!=null ){
          doAddToComment(eUMI,comment);
        }
        else if(modinfo.getParameter("shifting")!=null || modinfo.getParameter("shifting.x")!=null ){
          doShiftComment(eUMI,comment);
        }
        this.addLinks(formatText(iwrb.getLocalizedString("comments", "Athugasemdir")));
        this.addMain(doView(eMember,eUMI));
        this.setBorder(2);

      }
      catch(SQLException sql){add(iwrb.getLocalizedString("dberror","sql vandr��i"));}
      catch(FinderException fe){add(iwrb.getLocalizedString("dbError"));}
    }
    else
      add(iwrb.getLocalizedString("nochosen","enginn valinn"));
  }

  protected PresentationObject makeLinkTable(int menuNr){
    return new Text("");
  }

  private void doAddToComment(UnionMemberInfo eUmi,String comment){
    String temp = eUmi.getComment()!=null?eUmi.getComment():"";
    eUmi.setComment(temp + comment);
    try {
      eUmi.update();
    }
    catch (SQLException ex) {
      add(iwrb.getLocalizedString("notwritetodb","T�kst ekki a� skrifa � gagnagrunn"));
    }

  }
  private void doShiftComment(UnionMemberInfo eUmi,String comment){
    String temp = eUmi.getComment()!=null?eUmi.getComment():"";
    eUmi.setComment(comment);
    try {
      eUmi.update();
    }
    catch (SQLException ex) {
      add(iwrb.getLocalizedString("notwritetodb","T�kst ekki a� skrifa � gagnagrunn"));
    }
  }
  private PresentationObject doView(Member member, UnionMemberInfo umi){
    Table T = new Table();
    //T.add(formatText("Nafn :"),1,1);
    T.add(formatText(member.getName()),1,1);
    T.add(formatText(iwrb.getLocalizedString("ssn","Kt")+" :"),1,2);
    T.add(formatText(member.getSocialSecurityNumber()),1,2);
    //T.add(formatText("Athugasemd :"),1,3);
    String comment = umi.getComment();
    if(comment != null){
       T.add(formatText(comment ),1,4);
    }
    else{
      T.add(formatText(iwrb.getLocalizedString("nothing","Ekkert skr��")),1,4);
    }
    TextArea TA = new TextArea("mc_comment");
    TA.setHeight(3);
    TA.setWidth(40);
    T.add(TA,1,6);
    SubmitButton adder    = new SubmitButton(iwrb.getImage(AddToImageUrl),"adding");
    SubmitButton renewer  = new SubmitButton(iwrb.getImage(ShiftImageUrl),"shifting",iwrb.getLocalizedString("exchange","Skipta �t"));
    CloseButton closer    = new CloseButton(iwrb.getImage(CloseImageUrl));
    T.add(adder,1,7);
    T.add(renewer,1,7);
    T.add(closer,1,7);
    T.add(new HiddenInput("member_id",String.valueOf(member.getID())));
    T.add(new HiddenInput("union_id",String.valueOf(umi.getUnionID())));
    Form myForm = new Form();
    myForm.add(T);

    return myForm;
  }
}
