package com.idega.block.forum;

import com.idega.block.forum.presentation.*;
import com.idega.jmodule.object.*;
import com.idega.core.accesscontrol.business.AccessControl;

/**
 * Title:        JForums<p>
 * Description:  <p>
 * Copyright:    Copyright (c) idega margmi�lun hf.<p>
 * Company:      idega margmi�lun hf.<p>
 * @author idega 2000 - idega team - <a href="mailto:gummi@idega.is">gummi@idega.is</a>
 * @version 1.0
 */

public class idegaForum extends JModuleObject{

//  protected ModuleObject SomeThreads;
//  protected Variables myVariables;
//  protected Forum myForum;
//  protected String UserPresentation;
//  protected String DefaultPresentation;

//  public idegaForum( boolean isAdmin, String UserName, int UserID, String SomeThreadsPresentation ) {
    // setja � Application
//    SomeThreads = ((ModuleObject)Class.forName(SomeThreadsPresentation)).getSomeThreadsModule();

//  }
/*

  public ModuleObject getSomeThreads(){
    return SomeThreads;
  }



  public void main( ModuleInfo modinfo ){

    myVariables = (Variables)modinfo.getRequest().getSession().getAttribute("ForumVariables");
    if (myVariables == null){
      myVariables =  new myVariables();
      modinfo.getRequest().getSession().setAttribute("ForumVariables", myVariables );
    }

    myVariables.update(modinfo);

    myForum = new Forum(modinfo);

    this.add(myForum);
  }



  protected class Forum extends ModuleObjectContainer{

    public Table FrameTable;
    public FourmPresentation myPresentation;

    public Forum( ModuleInfo modinfo, String presentation ){
      FrameTable = new Table(1,1);

      myPresentation = (ForumPresentation)Class.forName(presentation);

      this.add(FrameTable);
    }




    public void doForums(){
      FrameTable.empty();
      FrameTable.add( getSideStatus() );
    }

    public void main(){
      doForums();
    }

  }  // Class Forum


  protected class Variables{

    //public Type ......;

    public Variables(){
      initialiceVariables();
    }

    public void initialiceVariables(){

    }

  }  // Class Variables
*/


  public void main(ModuleInfo modinfo) throws Exception {
    this.empty();
    if (modinfo.getSessionAttribute("idegaForums") == null){
      Forums theForums = new Forums();
      theForums.setUseForums(true);
      theForums.setUseUserRegistration(false);
      theForums.setUseLogin(false);
      theForums.setUseNameField(true);
      modinfo.setSessionAttribute("idegaForums" , theForums );
    }

    Forums myForums = (Forums)modinfo.getSessionAttribute("idegaForums");

    //myForums.setAllowedToDeleteThread(this.hasPermission(AccessControl.getDeletePermissionString(),this,modinfo));
    //myForums.setConnectionAttributes("union_id", 1);
    add(myForums);

  }

} // Class idegaForum





//    info.getResponse().sendRedirect(targetSide);

