package com.idega.block.text.business;

import java.sql.*;
import com.idega.presentation.IWContext;
import com.idega.block.text.data.*;
import com.idega.core.data.ICObjectInstance;
import com.idega.util.IWTimestamp;
import java.util.List;
import java.util.Iterator;
import com.idega.core.data.ICFile;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

public class ContentBusiness{

  public static Content getContent(int iContentId){
    return ContentFinder.getContent(iContentId );
  }

	public static boolean addFileToContent(int iContentId,int iICFileId){
		try {

			((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).findByPrimaryKeyLegacy(iICFileId).addTo(((com.idega.block.text.data.ContentHome)com.idega.data.IDOLookup.getHomeLegacy(Content.class)).findByPrimaryKeyLegacy(iContentId));
			return true;
		}
		catch (SQLException ex) {

		}
		return false;
	}

	public static boolean removeFileFromContent(int iContentId,int iICFileId){
	  try {
			((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).findByPrimaryKeyLegacy(iICFileId).removeFrom(((com.idega.block.text.data.ContentHome)com.idega.data.IDOLookup.getHomeLegacy(Content.class)).findByPrimaryKeyLegacy(iContentId));
		}
		catch (Exception ex) {

		}
		return false;
	}


  public static boolean deleteContent(int iContentId ) {
    javax.transaction.TransactionManager t = com.idega.transaction.IdegaTransactionManager.getInstance();
    try {
      t.begin();
    //  List O = TextFinder.listOfObjectInstanceTexts();
      Content eContent = ((com.idega.block.text.data.ContentHome)com.idega.data.IDOLookup.getHomeLegacy(Content.class)).findByPrimaryKeyLegacy(iContentId);
      List L = ContentFinder.listOfLocalizedText(eContent);
      if(L != null){
        LocalizedText lt;
        for (int i = 0; i < L.size(); i++) {
          lt = (LocalizedText) L.get(i);
          lt.removeFrom(eContent);
          lt.delete();
        }
      }
      L = ContentFinder.listOfContentFiles(eContent);
      if(L != null){
        ICFile file;
        for (int i = 0; i < L.size(); i++) {
          file = (ICFile) L.get(i);
          file.removeFrom(eContent);
          //file.delete();
        }
      }
      eContent.delete();

     t.commit();
     return true;
    }
    catch(Exception e) {
      try {
        t.rollback();
      }
      catch(javax.transaction.SystemException ex) {
        ex.printStackTrace();
      }
      e.printStackTrace();
    }
    return false;
  }

  public static Content saveContent( int iContentId,
                                  int iLocalizedTextId,
                                  int iLocaleId ,
                                  int iUserId ,
                                  Timestamp tsPublishFrom,
                                  Timestamp tsPublishTo,
                                  String sHeadline,
                                  String sBody,
                                  String sTitle,
                                  List listOfFiles){

    javax.transaction.TransactionManager t = com.idega.transaction.IdegaTransactionManager.getInstance();
    try {
      t.begin();
      boolean ctUpdate = false;
      boolean locUpdate = false;
      Content eContent = null;
      LocalizedText locText = null;
      if(iContentId > 0){
        ctUpdate = true;
        eContent = ((com.idega.block.text.data.ContentHome)com.idega.data.IDOLookup.getHomeLegacy(Content.class)).findByPrimaryKeyLegacy(iContentId);
        if(iLocalizedTextId > 0){
          locUpdate = true;
          locText = ((com.idega.block.text.data.LocalizedTextHome)com.idega.data.IDOLookup.getHomeLegacy(LocalizedText.class)).findByPrimaryKeyLegacy(iLocalizedTextId);
        }
        else{
          locUpdate = false;
          locText = ((com.idega.block.text.data.LocalizedTextHome)com.idega.data.IDOLookup.getHomeLegacy(LocalizedText.class)).createLegacy();
        }
      }
      else{
        ctUpdate = false;
        locUpdate = false;
        eContent = ((com.idega.block.text.data.ContentHome)com.idega.data.IDOLookup.getHomeLegacy(Content.class)).createLegacy();
        eContent.setCreated(IWTimestamp.getTimestampRightNow());
        locText = ((com.idega.block.text.data.LocalizedTextHome)com.idega.data.IDOLookup.getHomeLegacy(LocalizedText.class)).createLegacy();
      }

      locText.setHeadline(sHeadline);
      locText.setBody(sBody);
      locText.setLocaleId(iLocaleId);
      locText.setTitle( sTitle);
      locText.setUpdated(IWTimestamp.getTimestampRightNow());

      if(tsPublishFrom!= null)
        eContent.setPublishFrom(tsPublishFrom);
      if(tsPublishTo != null)
        eContent.setPublishTo(tsPublishTo);
      eContent.setLastUpdated(IWTimestamp.getTimestampRightNow());

      if(ctUpdate ){
        eContent.update();
        if(locUpdate){
          locText.update();
        }
        else if(!locUpdate){
          locText.setCreated(IWTimestamp.getTimestampRightNow());
          locText.insert();
          locText.addTo(eContent);
        }
      }
      else if(!ctUpdate){
        eContent.setCreated(IWTimestamp.getTimestampRightNow());
        if(iUserId > 0)
        eContent.setUserId(iUserId);
        eContent.insert();
        locText.setCreated(IWTimestamp.getTimestampRightNow());
        locText.insert();
        locText.addTo(eContent);
      }

      if(listOfFiles != null){
        Iterator I = listOfFiles.iterator();
        while(I.hasNext()){
          ICFile file = (ICFile) I.next();
          try {
            file.addTo(eContent);
          }
          catch (SQLException ex) {

          }
        }
      }
      t.commit();
      return eContent;
    }
    catch(Exception e) {
      try {
        t.rollback();
      }
      catch(javax.transaction.SystemException ex) {
        ex.printStackTrace();
      }
      e.printStackTrace();
    }

    return null;
  }
}

