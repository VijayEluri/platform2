package com.idega.jmodule.object;

import com.idega.jmodule.object.interfaceobject.Form;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import com.idega.util.GenericFormCollector;
import com.idega.util.datastructures.Collectable;
import com.idega.event.IWSubmitEvent;
import com.idega.event.IWSubmitListener;
import com.idega.jmodule.object.interfaceobject.SubmitButton;
import com.idega.event.IWModuleEvent;
import com.idega.event.IWEventException;
//import com.idega.jmodule.object.interfaceobject.ResetButton;

/**
 * Title:        UserModule
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega.is
 * @author 2000 - idega team - <a href="mailto:gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class TabbedPropertyPanel extends Form implements ChangeListener, IWSubmitListener {

  private Table frameTable;
  private Table buttonTable;
  private IWTabbedPane tpane;
  private static String TabbedPropertyPanelAttributeString = "-TabbedPropertyPanel";
  private GenericFormCollector collector;
  private boolean useCollector = false;
  private String attributeString;
  private boolean justConstructed = true;
  private boolean first = true;
  private boolean stateChanged = false;
  private boolean okClicked = false;
  private boolean cancelClicked = false;
  private boolean applyClicked = false;

  private SubmitButton ok;
  private SubmitButton cancel;
  private SubmitButton apply;

  private boolean useOkButton=true;
  private boolean useCancelButton=true;
  private boolean useApplyButton=true;

  private TabbedPropertyPanel(String key, ModuleInfo modinfo) {
    frameTable = new Table();
//    this.setMethod("get");
//    frameTable.setBorder(1);  // temp
    tpane = IWTabbedPane.getInstance(key,modinfo);
    tpane.addChangeListener(this);
    tpane.setTabsToFormSubmit(this);
    this.add(frameTable);
    initializeLayout();
    collector = new GenericFormCollector();
    initializeButtons();
    lineUpButtons();
    ok.addIWSubmitListener(this, this,modinfo);
    apply.addIWSubmitListener(this, this,modinfo);
    cancel.addIWSubmitListener(this, this,modinfo);
  }

  public void initializeButtons(){
    ok = new SubmitButton("     OK     ");
    cancel = new SubmitButton(" Cancel ");
    apply = new SubmitButton("  Apply  ");
  }


  public static TabbedPropertyPanel getInstance(String key, ModuleInfo modinfo){
    Object  obj = modinfo.getSessionAttribute(key+TabbedPropertyPanelAttributeString);
    if(obj != null && obj instanceof TabbedPropertyPanel){
      TabbedPropertyPanel TabPropPanelObj = (TabbedPropertyPanel)obj;
      TabPropPanelObj.justConstructed(false);
      return TabPropPanelObj;
    }else{
      TabbedPropertyPanel tempTab = new TabbedPropertyPanel(key,modinfo);
      modinfo.setSessionAttribute(key+TabbedPropertyPanelAttributeString, tempTab);
      tempTab.setAttributeString(key+TabbedPropertyPanelAttributeString);
      return tempTab;
    }
  }


  public boolean justConstructed(){
    return justConstructed;
  }

  public void justConstructed(boolean justConstructed){
    this.justConstructed = justConstructed;
    this.tpane.justConstructed(justConstructed);
  }

  public void setAttributeString(String attributeString){
    this.attributeString = attributeString;
  }

  public void dispose(ModuleInfo modinfo){
    modinfo.getSession().removeAttribute(attributeString);
    tpane.dispose(modinfo);
    ok.endEvent(modinfo);
    cancel.endEvent(modinfo);
    apply.endEvent(modinfo);
  }



  public IWTabbedPane getIWTabbedPane(){
    return tpane;
  }

  public void initializeLayout(){
    frameTable.resize(1,2);
    frameTable.add(tpane,1,1);
  }


  public void stateChanged(ChangeEvent e){
    if(useCollector && !first){
      stateChanged = true;
    }
    first = false;
  }

  public void actionPerformed(IWSubmitEvent e){
    if(e.getSource() == ok){
      this.okClicked = true;
      this.cancelClicked = false;
      this.applyClicked = false;
      collector.storeAll(e.getModuleInfo());
    }else if(e.getSource() == apply){
      this.okClicked = false;
      this.cancelClicked = false;
      this.applyClicked = true;
      collector.storeAll(e.getModuleInfo());
    }else if(e.getSource() == cancel){
      this.okClicked = false;
      this.cancelClicked = true;
      this.applyClicked = false;
    } else {
      this.okClicked = false;
      this.cancelClicked = false;
      this.applyClicked = false;
    }

  }

  public boolean clickedOk(){
    return this.okClicked;
  }

  public boolean clickedCancel(){
    return this.cancelClicked;
  }

  public boolean clickedApply(){
    return this.applyClicked;
  }



  public void addTab(ModuleObject collectable, int index, ModuleInfo modinfo){
    tpane.insertTab( collectable.getName(), collectable, index, modinfo);
    if(collectable instanceof Collectable){
      collector.addCollectable((Collectable)collectable, index);
      useCollector = true;
    }
  }

  public void disableOkButton(boolean value){
    useOkButton = !value;
  }

  public void disableCancelButton(boolean value){
    useCancelButton = !value;
  }

  public void disableApplyButton(boolean value){
    useApplyButton = !value;
  }


  public SubmitButton getOkButton(){
    return ok;
  }

  public SubmitButton getCancelButton(){
    return cancel;
  }

  public SubmitButton getApplyButton(){
    return apply;
  }


  public void lineUpButtons(){
    // assuming all buttons are enabled

    buttonTable = new Table(5,1);

    buttonTable.setCellpadding(0);
    buttonTable.setCellspacing(0);
    buttonTable.setHeight(27);

    buttonTable.setVerticalAlignment(1,1,"bottom");
    buttonTable.setVerticalAlignment(3,1,"bottom");
    buttonTable.setVerticalAlignment(5,1,"bottom");

    buttonTable.setWidth(2,1,"7");
    buttonTable.setWidth(4,1,"7");

    buttonTable.add(ok,1,1);
    buttonTable.add(cancel,3,1);
    buttonTable.add(apply,5,1);

    frameTable.add(buttonTable,1,2);
    frameTable.setAlignment(1,2,"right");

  }

  public void main(ModuleInfo modinfo) {

    if(stateChanged){
      collector.setSelectedIndex(tpane.getSelectedIndex(),modinfo);
      stateChanged = false;
    }

/*    if(this.justConstructed()){
      lineUpButtons();
      ok.addIWSubmitListener(this, this,modinfo);
      apply.addIWSubmitListener(this, this,modinfo);
    }
*/
  }


}