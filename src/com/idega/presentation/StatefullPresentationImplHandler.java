package com.idega.presentation;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.rmi.RemoteException;
import com.idega.business.IBOLookup;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWUserContext;
import com.idega.event.IWStateMachine;
import com.idega.event.IWPresentationState;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class StatefullPresentationImplHandler {

  private Class _class = null;

  IWPresentationState _presentationState = null;


  public StatefullPresentationImplHandler() {
  }

  public static IWPresentationState getPresentationState(String compoundId, IWUserContext iwuc) {
    IWPresentationState presentationState = null;
    try {
      IWStateMachine stateMachine = (IWStateMachine)IBOLookup.getSessionInstance(iwuc,IWStateMachine.class);
      StringTokenizer tokenizer = new StringTokenizer(compoundId, "/");
      String lastElement = "";
      while (tokenizer.hasMoreTokens()) {
        lastElement = tokenizer.nextToken();
      }
      int childDelimiterPosition = lastElement.lastIndexOf("_");
      String classCode = lastElement.substring(0, childDelimiterPosition);
      String className = IWMainApplication.decryptClassName(classCode);
      Class presentationClass = Class.forName(className);
      presentationState = stateMachine.getStateFor(compoundId, presentationClass);
    }
    catch (ClassNotFoundException ce)  {
      throw new RuntimeException(ce.getMessage());
    }
    catch (RemoteException re) {
      throw new RuntimeException(re.getMessage());
    }
    return presentationState;
  }


  public Class getPresentationStateClass(){
    return _class;
  }

  public void setPresentationStateClass(Class stateClass){
    _class = stateClass;
  }

  public IWPresentationState getPresentationState(PresentationObject obj, IWUserContext iwuc){
    if(_presentationState == null){
      try {
        IWStateMachine stateMachine = (IWStateMachine)IBOLookup.getSessionInstance(iwuc,IWStateMachine.class);
        //if(obj.getICObjectInstanceID() == 0){
          _presentationState = stateMachine.getStateFor(obj.getCompoundId(), _class);
         // _presentationState = stateMachine.getStateFor(obj.getLocation(),_class);
        //} else {
        //  _presentationState = stateMachine.getStateFor(obj.getICObjectInstance());
        //}
      }
      catch (RemoteException re) {
        throw new RuntimeException(re.getMessage());
      }
      catch (SQLException sql) {
        throw new RuntimeException(sql.getMessage());
      }
    }
    return _presentationState;
  }


}