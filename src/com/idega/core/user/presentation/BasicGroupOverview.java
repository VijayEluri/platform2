package com.idega.core.user.presentation;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.idega.core.data.GenericGroup;
import com.idega.core.user.business.UserGroupBusiness;
import com.idega.core.user.data.UserGroupRepresentative;
import com.idega.data.GenericEntity;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CloseButton;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.Window;

/**
 * Title:        User
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class BasicGroupOverview extends Page {

  private static final String PARAMETER_DELETE_GROUP =  "delete_ic_group";


  public BasicGroupOverview(){
    super();
  }


  public Table getGroups(IWContext iwc) throws Exception{
    String[] types = new String[1];
    types[0] = ((UserGroupRepresentative)GenericEntity.getStaticInstance(UserGroupRepresentative.class)).getGroupTypeValue();
    List groups = com.idega.core.data.GenericGroupBMPBean.getAllGroups(types,false);



    //groups.remove(com.idega.core.accesscontrol.business.AccessControl.getAdministratorGroup())

    Table groupTable = null;
    if(groups != null){
      List notDelet = (List)((Vector)iwc.getAccessController().getStandardGroups()).clone();
      notDelet.add(iwc.getAccessController().getPermissionGroupAdministrator());
      groupTable = new Table(3,(groups.size()>8)?groups.size():8);
      groupTable.setCellspacing(0);
      groupTable.setHorizontalZebraColored("D8D4CD","C3BEB5");
      groupTable.setWidth("100%");

      for (int i = 1; i <= groupTable.getRows() ; i++) {
        groupTable.setHeight(i,"20");
      }

      for (int i = 0; i < groups.size(); i++) {
        GenericGroup tempGroup = (GenericGroup)groups.get(i);
        if(tempGroup != null){

          Link aLink = new Link(new Text(tempGroup.getName()));
          aLink.setWindowToOpen(GroupPropertyWindow.class);
          aLink.addParameter(GroupPropertyWindow.PARAMETERSTRING_GROUP_ID, tempGroup.getID());
          groupTable.add(aLink,2,i+1);

          //if(!tempGroup.equals(AccessControl.getPermissionGroupAdministrator()) && !tempGroup.equals(AccessControl.getPermissionGroupEveryOne()) && !tempGroup.equals(AccessControl.getPermissionGroupUsers())){
          if(!notDelet.contains(tempGroup) && iwc.getAccessController().isAdmin(iwc)){
            Link delLink = new Link(new Text("Delete"));
            delLink.setWindowToOpen(ConfirmWindowBGO.class);
            delLink.addParameter(BasicGroupOverview.PARAMETER_DELETE_GROUP , tempGroup.getID());
            groupTable.add(delLink,3,i+1);
          }

        }
      }
    }

    return groupTable;
  }




  public void main(IWContext iwc) throws Exception {
    this.empty();
    this.add(this.getGroups(iwc));
    this.getParentPage().setAllMargins(0);
    this.getParentPage().setBackgroundColor("#d4d0c8");
  }





  public static class ConfirmWindowBGO extends Window{

    public Text question;
    public Form myForm;

    public SubmitButton confirm;
    public CloseButton close;
    public Table myTable = null;

    public static final String PARAMETER_CONFIRM = "confirm";

    public Vector parameters;

    public ConfirmWindowBGO(){
      super("ConfirmWindow",300,130);
      super.setBackgroundColor("#d4d0c8");
      super.setScrollbar(false);
      super.setAllMargins(0);

      this.question = Text.getBreak();
      this.myForm = new Form();
      this.parameters = new Vector();
      this.confirm = new SubmitButton(ConfirmWindowBGO.PARAMETER_CONFIRM,"   Yes   ");
      this.close = new CloseButton("   No    ");
      // close.setOnFocus();
      initialze();

    }


    public void lineUpElements(){
      this.myTable = new Table(2,2);
      this.myTable.setWidth("100%");
      this.myTable.setHeight("100%");
      this.myTable.setCellpadding(5);
      this.myTable.setCellspacing(5);
      //myTable.setBorder(1);


      this.myTable.mergeCells(1,1,2,1);

      this.myTable.add(this.question,1,1);

      this.myTable.add(this.confirm,1,2);

      this.myTable.add(this.close,2,2);

      this.myTable.setAlignment(1,1,"center");
//      myTable.setAlignment(2,1,"center");
      this.myTable.setAlignment(1,2,"right");
      this.myTable.setAlignment(2,2,"left");

      this.myTable.setVerticalAlignment(1,1,"middle");
      this.myTable.setVerticalAlignment(1,2,"middle");
      this.myTable.setVerticalAlignment(2,2,"middle");

      this.myTable.setHeight(2,"30%");

      this.myForm.add(this.myTable);

    }

    public void setQuestion(Text Question){
      this.question = Question;
    }


    /*abstract*/
    public void initialze(){
      this.setQuestion(new Text("Are you sure you want to delete this group?"));
      this.maintainParameter(BasicGroupOverview.PARAMETER_DELETE_GROUP);
    }


    public void maintainParameter(String parameter){
      this.parameters.add(parameter);
    }

    /*abstract*/
    public void actionPerformed(IWContext iwc)throws Exception{
      String groupDelId = iwc.getParameter(BasicGroupOverview.PARAMETER_DELETE_GROUP);

      if(groupDelId != null){
        UserGroupBusiness.deleteGroup(Integer.parseInt(groupDelId));
      }
    }


    public void _main(IWContext iwc) throws Exception {
      Iterator iter = this.parameters.iterator();
      while (iter.hasNext()) {
        String item = (String)iter.next();
        this.myForm.maintainParameter(item);
      }

      String confirmThis = iwc.getParameter(ConfirmWindowBGO.PARAMETER_CONFIRM);

      if(confirmThis != null){
        this.actionPerformed(iwc);
        this.setParentToReload();
        this.close();
      } else{
        this.empty();
        if(this.myTable == null){
          lineUpElements();
        }
        this.add(this.myForm);
      }
      super._main(iwc);
    }

  }
















} //Class end
