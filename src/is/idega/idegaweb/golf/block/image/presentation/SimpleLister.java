package is.idega.idegaweb.golf.block.image.presentation;

import is.idega.idegaweb.golf.block.image.data.ImageEntity;

import java.sql.SQLException;
import java.util.List;

import com.idega.data.EntityFinder;
import com.idega.data.IDOLookup;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.util.IWTimestamp;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

public class SimpleLister extends PresentationObjectContainer {

    private String target = "viewer";
    public String prmImageView = "img_view_id";
    public String sessImageParameterName = "im_image_session_name";
    public String sessImageParameter = "image_id";
    private int listLimit = 50;

    public void  main(IWContext modinfo){
      getParentPage().setAllMargins(0);
      List L = listOfImages();

      checkParameterName(modinfo);

      if(L!= null){
        Table Frame = new Table();
          Frame.setWidth("100%");
        Frame.setCellpadding(0);
        Frame.setCellspacing(0);
        Table T = new Table();
          T.setWidth("100%");
        int len = L.size();
        int row = 1;
        T.add(formatText("Pictures"),1,row++);
        for (int i = 0; i < len; i++) {
          ImageEntity image = (ImageEntity) L.get(i);
          T.add(getImageLink(image,target,prmImageView),1,row);
          T.add(formatText(new IWTimestamp(image.getDateAdded()).getLocaleDate(modinfo.getCurrentLocale())),2,row);
          row++;
        }
        T.setCellpadding(2);
        T.setCellspacing(0);

        T.setHorizontalZebraColored("#CBCFD3","#ECEEF0");
        Frame.add(T,1,1);
        add(Frame);
      }
    }

  public void checkParameterName(IWContext modinfo){
     if(modinfo.getParameter(sessImageParameterName)!=null){
      sessImageParameter = modinfo.getParameter(sessImageParameterName);
      modinfo.setSessionAttribute(sessImageParameterName,sessImageParameter);
    }
    else if(modinfo.getSessionAttribute(sessImageParameterName)!=null)
      sessImageParameter = (String) modinfo.getSessionAttribute(sessImageParameterName);
  }

  public Link getImageLink(ImageEntity image,String target,String prm){
    Text T = new Text(image.getName());
    Link L = new Link(T,SimpleViewer.class);
    L.setFontSize(1);
    L.addParameter(sessImageParameter,image.getID());
    L.setTarget(target);
    return L;
  }

  public List listOfImages(){
    List L = null;
    try {
      L = EntityFinder.findAllDescendingOrdered(((ImageEntity)IDOLookup.instanciateEntity(ImageEntity.class)),"image_id");
    }
    catch (SQLException ex) {
      L = null;
    }
    return L;
  }

  public Text formatText(String s){
    Text T= new Text();
    if(s!=null){
      T= new Text(s);

      T.setFontColor("#000000");
      T.setFontSize(1);
    }
    return T;
  }
  public Text formatText(int i){
    return formatText(String.valueOf(i));
  }
}