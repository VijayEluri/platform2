package com.idega.user.data;


import com.idega.business.IBOService;
import com.idega.presentation.Image;
import com.idega.idegaweb.IWApplicationContext;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public interface GroupTypeHandler extends IBOService  {

  public GroupInfo getGroupInformationClass(Group group);
  public Class getGroupClassType();
  public Group getGroupInformationClassByPrimaryKey(Integer groupID);
  public Image getGroupTypeIcon();

}