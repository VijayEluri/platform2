package com.idega.block.category.business;

import java.sql.*;
import com.idega.presentation.IWContext;
import com.idega.block.news.data.*;
import com.idega.block.text.data.LocalizedText;
import com.idega.block.text.business.*;
import com.idega.core.data.ICObjectInstance;
import com.idega.util.idegaTimestamp;
import com.idega.core.data.ICFile;
import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import com.idega.data.EntityFinder;
import com.idega.block.text.data.Content;
import com.idega.core.data.ICCategory;
import com.idega.core.business.ICObjectBusiness;

public class CategoryBusiness{

  private static CategoryBusiness categoryBusiness;

  public static CategoryBusiness getInstance(){
    if(categoryBusiness==null)
      categoryBusiness = new CategoryBusiness();
    return categoryBusiness;
  }

  public boolean disconnectBlock(int instanceid){
    List L = CategoryFinder.getInstance().listOfCategoryForObjectInstanceId(instanceid);
    if(L!= null){
      Iterator I = L.iterator();
      while(I.hasNext()){
        ICCategory Cat = (ICCategory) I.next();
        disconnectCategory(Cat,instanceid);
      }
      return true;
    }
    else
      return false;

  }

  public boolean disconnectCategory(ICCategory Cat,int iObjectInstanceId){
    try {
      if(iObjectInstanceId > 0  ){
        ICObjectInstance obj = new ICObjectInstance(iObjectInstanceId);
        Cat.removeFrom(obj);
      }
      return true;
    }
    catch (SQLException ex) {

    }
    return false;
  }

  /**
   *  removes all categories bound to specified instance
   */
  public boolean removeInstanceCategories(int iObjectInstanceId){
    try {
      ICObjectInstance obj =  ICObjectBusiness.getInstance().getICObjectInstance(iObjectInstanceId);
      obj.removeFrom(ICCategory.class);
    }
    catch (Exception ex) {

    }
    return false;

  }


  public boolean deleteBlock(int instanceid){
    List L = CategoryFinder.getInstance().listOfCategoryForObjectInstanceId(instanceid);
    if(L!= null){
      Iterator I = L.iterator();
      while(I.hasNext()){
        ICCategory N = (ICCategory) I.next();
        try{
          deleteCategory(N.getID(),instanceid );
        }
        catch(SQLException sql){

        }
      }
      return true;
    }
    else
      return false;
  }

  public void deleteCategory(int iCategoryId) throws SQLException{
    deleteCategory(iCategoryId ,CategoryFinder.getInstance().getObjectInstanceIdFromCategoryId(iCategoryId));
  }

  public void deleteCategory(int iCategoryId ,int iObjectInstanceId) throws SQLException {
    ICCategory nc = (ICCategory) CategoryFinder.getInstance().getCategory( iCategoryId );

    if(iObjectInstanceId > 0  ){
      ICObjectInstance obj = new ICObjectInstance(iObjectInstanceId);
      nc.removeFrom(obj);
    }
    nc.delete();

  }

  public void saveRelatedCategories(int iObjectInstanceId,int[] CategoryIds){
    try {
      ICObjectInstance.getEntityInstance(ICObjectInstance.class,iObjectInstanceId).removeFrom(ICCategory.class);
      for (int i = 0; i < CategoryIds.length; i++) {
        ICObjectInstance.getEntityInstance(ICObjectInstance.class,iObjectInstanceId).addTo(ICCategory.class,CategoryIds[i]);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public ICCategory saveCategory(int iCategoryId,String sName,String sDesc,int iObjectInstanceId,String type,boolean allowMultible){
    ICCategory Cat = new ICCategory();
    if(iCategoryId > 0)
      Cat = CategoryFinder.getInstance().getCategory(iCategoryId);
    Cat.setName(sName);
    Cat.setDescription(sDesc);
    Cat.setType(type);
    return saveCategory(Cat,iObjectInstanceId,allowMultible);
  }

  public boolean updateCategory(int id,String name,String info){
    try {
      ICCategory cat = new ICCategory(id);
      cat.setName(name);
      cat.setDescription(info);
      cat.update();
      return true;
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return false;
  }

  public ICCategory saveCategory(int iCategoryId,String sName,String sDesc,int iObjectInstanceId,String type){
    return saveCategory(iCategoryId,sName,sDesc,iObjectInstanceId,type,false);
  }

  public ICCategory saveCategory(ICCategory Cat,int iObjectInstanceId,boolean allowMultible){
    javax.transaction.TransactionManager t = com.idega.transaction.IdegaTransactionManager.getInstance();
    try{
     t.begin();
      if(Cat.getID()>0){
        Cat.update();
      }
      else{
        Cat.setCreated(idegaTimestamp.getTimestampRightNow());
        Cat.setValid(true);
        Cat.insert();
      }
      // Binding category to instanceId
      if(iObjectInstanceId > 0){
        ICObjectInstance objIns = new ICObjectInstance(iObjectInstanceId);
        // Allows only one category per instanceId
        if(!allowMultible)
          objIns.removeFrom((ICCategory)ICCategory.getEntityInstance(ICCategory.class));
        Cat.addTo(objIns);
      }
      t.commit();
      return Cat;
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


  public int createCategory(int iObjectInstanceId,String type){
    return saveCategory(-1,"Category - "+iObjectInstanceId,"Category - "+iObjectInstanceId,iObjectInstanceId ,type,false).getID();
  }

}