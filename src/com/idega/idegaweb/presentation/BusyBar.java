package com.idega.idegaweb.presentation;

import com.idega.presentation.Block;
import com.idega.presentation.Image;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.InterfaceObject;
import java.util.AbstractList;
import java.util.Iterator;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author <br><a href="mailto:aron@idega.is">Aron Birkir</a><br>
 * @version 1.0
 */

public class BusyBar extends Block {

  private String name = "busy";
  private String _url = "";
  private AbstractList busyObjects;
  private AbstractList disabledObjects;
  private boolean _busyOnChange = false;

  public BusyBar(String name) {
    this.name = name;
  }

  public void setName(String name){
    this.name = name;
  }

  private void addInterfaceObject(InterfaceObject obj){
    addBusyObject(obj);
    addDisabledObject(obj);
  }

  private void addInterfaceObject(Link obj){
    addBusyObject(obj);
    addDisabledObject(obj);
  }

  public void addBusyObject(InterfaceObject obj){
    if(busyObjects==null)
      busyObjects = new java.util.Vector();
    busyObjects.add(obj);
  }

  public void addDisabledObject(InterfaceObject obj){
    if(disabledObjects==null)
      disabledObjects = new java.util.Vector();
    disabledObjects.add(obj);
  }

  public void addBusyObject(Link obj){
    if(busyObjects==null)
      busyObjects = new java.util.Vector();
    busyObjects.add(obj);
  }

  public void addDisabledObject(Link obj){
    if(disabledObjects==null)
      disabledObjects = new java.util.Vector();
    disabledObjects.add(obj);
  }



  public void setInterfaceObject(InterfaceObject obj){
    addInterfaceObject(obj);
  }

   public void setLinkObject(Link obj){
    addInterfaceObject(obj);
  }

  public void setBusyBarUrl(String url){
    _url = url;
  }

  public void setBusyOnChange(){
    _busyOnChange = true;
  }

  public void main(IWContext iwc){
    if(_url == null || "".equals(_url) ){
      _url = iwc.getApplication().getCoreBundle().getImage("busy.gif").getURL();
    }
    getParentPage().setOnLoad(Image.getPreloadScript(_url));
    Image busy = iwc.getApplication().getCoreBundle().getImage("transparentcell.gif");
    busy.setName(name);
    setScripts();
    add(busy);
  }

  private void setScripts(){
    if(busyObjects!=null){
      java.util.Iterator iter = busyObjects.iterator();
      while(iter.hasNext()){
        Object o = (Object) iter.next();
        if(o instanceof InterfaceObject ){
          InterfaceObject obj = (InterfaceObject)o;
          if(disabledObjects != null){
            Iterator iter2 = disabledObjects.iterator();
            while (iter2.hasNext()) {
              PresentationObject item = (PresentationObject)iter2.next();
              if(_busyOnChange){
                obj.setOnChange(getDisabledScript(item));
              } else {
                obj.setOnClick(getDisabledScript(item));
              }
            }
          }
           if(_busyOnChange){
            obj.setOnChange("this.form.submit()");
            obj.setOnChange(getCallingScript());
            obj.setOnChange("return false;");
          } else {
            if(obj instanceof SubmitButton){
              obj.setOnClick("this.form.submit()");
            }
            obj.setOnClick(getCallingScript());
          }
        }
        else if(o instanceof Link){
          Link obj = (Link) o;
          obj.setOnClick(getCallingScript());
          if(disabledObjects != null){
            Iterator iter2 = disabledObjects.iterator();
            while (iter2.hasNext()) {
              PresentationObject item = (PresentationObject)iter2.next();
              obj.setOnClick(getDisabledScript(item));
            }
          }
        }
      }
    }
  }

  private String getDisabledScript(PresentationObject obj){
    if(obj instanceof InterfaceObject){
      return "document.forms['"+obj.getParentForm().getID()+"']."+obj.getID()+".disabled = true";
    } else {
      return "findObj("+obj.getID()+", null).disabled = true";
    }
    //return "document.getElementById("+obj.getID()+").disabled = true";
  }

  private String getCallingScript(){
    return "document.images['"+name+"'].src='"+_url+"'";
  }






}