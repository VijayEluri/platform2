package is.idega.idegaweb.golf.block.image.presentation;

import is.idega.idegaweb.golf.block.image.data.ImageEntity;
import is.idega.idegaweb.golf.block.image.data.ImageEntityHome;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ejb.EJBException;
import javax.ejb.RemoveException;

import com.idega.data.IDOLookup;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.Table;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

public class SimpleViewer extends PresentationObjectContainer{
    public String prmImageView = "img_view_id";
    public static final String prmAction = "img_view_action";
    public static final String actSave = "save",actDelete = "delete";
    public static final String sessionSaveParameter = "img_id";
    public static final String sessionParameter = "image_id";
    public String sessImageParameterName = "im_image_session_name";
    public String sessImageParameter = "image_id";

    public void  main(IWContext modinfo){

      String sImageId = getImageId(modinfo);
      String sAction = modinfo.getParameter(prmAction);

      if(sImageId != null){
        saveImageId(modinfo,sImageId);
        if(sAction != null){
          if(sAction.equals(actSave)){
            saveImageId(modinfo,sImageId);
          }
          else if(sAction.equals(actDelete)){
            deleteImage(sImageId);
            removeFromSession(modinfo);
          }
        }
        else{
          int id = Integer.parseInt(sImageId);
          try {
            ImageEntity ieImage = (ImageEntity)((ImageEntityHome)IDOLookup.getHomeLegacy(ImageEntity.class)).findByPrimaryKeyLegacy(id);
            Table T = new Table();
            T.add(ieImage.getName(),1,1);
            T.add(new Image(id),1,2);
            add(T);
          }
          catch (SQLException ex) {
            add("error");
          }
        }
      }
    }

    public boolean deleteImage(String sImageId){
      Connection Conn = null;
      try{
        Conn = com.idega.util.database.ConnectionBroker.getConnection();
        ResultSet RS;
        Statement Stmt = Conn.createStatement();
        int r = Stmt.executeUpdate("DELETE FROM IMAGE_IMAGE_CATAGORY WHERE IMAGE_ID = "+sImageId);
        Stmt.close();
      }
      catch(SQLException ex){
        ex.printStackTrace();
      }
      finally{
        if(Conn != null)
          com.idega.util.database.ConnectionBroker.freeConnection(Conn);
      }

      try {
        Integer iImageId = new Integer(sImageId);
        ((ImageEntityHome)IDOLookup.getHomeLegacy(ImageEntity.class)).remove(iImageId);
        return true;
      }
      catch (NumberFormatException ex){
        return false;
      } catch (EJBException e) {
		e.printStackTrace();
		return false;
	  } catch (RemoveException e) {
		e.printStackTrace();
		return false;
	  }
    }

    public String getImageId(IWContext modinfo){
      if(modinfo.getParameter(sessImageParameterName)!=null)
       sessImageParameter = modinfo.getParameter(sessImageParameterName);
      else if(modinfo.getSessionAttribute(sessImageParameterName)!=null){
        sessImageParameter = (String) modinfo.getSessionAttribute(sessImageParameterName);
      }
      //add(sessImageParameter);
      String s = null;
      if(modinfo.getParameter(sessImageParameter)!=null){
        s = modinfo.getParameter(sessImageParameter);
      }
      else if(modinfo.getSessionAttribute(sessImageParameter)!=null)
        s = (String) modinfo.getSessionAttribute(sessImageParameter);
      return s;
    }


    public void removeFromSession(IWContext modinfo){
      modinfo.removeSessionAttribute(sessImageParameter);
    }

    public void saveImageId(IWContext modinfo,String sImageId){
      modinfo.setSessionAttribute(sessImageParameter,sImageId);
      modinfo.setSessionAttribute(sessImageParameter+"2",sImageId);
    }

    public void saveImage(IWContext modinfo,String sImageId){
      modinfo.setSessionAttribute(sessionSaveParameter,sImageId);
    }

  }