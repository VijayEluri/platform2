//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.jmodule.object;

import com.idega.jmodule.*;
import java.io.*;
import java.util.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class Script extends ModuleObject{

private String scriptType;
private Hashtable scriptCode;

public Script(){
	this("javascript");
}

public Script(String scriptLanguage){
	super();
	setScriptLanguage(scriptLanguage);
	scriptCode = new Hashtable();
}

/*public void setScriptType(String scriptType){
	setAttribute("language",scriptType);
}*/

public void setScriptLanguage(String scriptLanguage){
	setAttribute("language",scriptLanguage);
}

public void setScriptSource(String sourceURL){
	setAttribute("src",sourceURL);
}

/*public void addToScriptCode(String code){
	this.scriptCode=this.scriptCode + "\n" + code;
}

public void setScriptCode(String code){
	this.scriptCode=code;
}

*/

public String getScriptCode(ModuleInfo modinfo){
	String returnString="";
	for (Enumeration e = scriptCode.keys(); e.hasMoreElements();){

		Object function=e.nextElement();
		String functionName = (String) function;
		String functionCode = (String) scriptCode.get(function);

		returnString = returnString + "\n\n" + functionCode;
	}

	return returnString;

}

public boolean doesFunctionExist(String function){
  if(scriptCode.get(function)==null){
      return false;
  }
  else{
      return true;
  }
}

public void removeFunction(String functionName){
	scriptCode.remove(functionName);
}

public void addToFunction(String functionName,String scriptString){

	if (scriptCode != null){
		String functionCode = (String) scriptCode.get(functionName);

		if ( functionCode != null){

			String beginString;
			String endString;
			String returnString;

			int lastbracket;
			lastbracket = functionCode.lastIndexOf("}");

			beginString = functionCode.substring(0,lastbracket);
			endString = "}";

			returnString = beginString + "\n" + scriptString + "\n" + endString;

			scriptCode.put(functionName,returnString);
		}
	}
}


public void addFunction(String functionName,String scriptString){
	scriptCode.put(functionName,scriptString);
}

public String getFunction(String functionName){
	return (String) scriptCode.get(functionName);
}

public void print(ModuleInfo modinfo)throws IOException{
	initVariables(modinfo);
	if (doPrint(modinfo)){
		if (getLanguage().equals("HTML")){

			//if (getInterfaceStyle().equals("something")){
			//}
			//else{
				println("<script "+getAttributeString()+" >");
				println("<!--//");
				if (! isAttributeSet("src")){
					println(getScriptCode(modinfo));
				}
				println("//-->");
				println("\n</script>");
				flush();
			//}
		}
		else if (getLanguage().equals("WML")){
			println("");
		}
	}
	else{
		super.print(modinfo);
	}
}


  public synchronized Object clone() {
    Script obj = null;
    try {
      obj = (Script)super.clone();
      obj.scriptType = this.scriptType;
      if(this.scriptCode != null){
        obj.scriptCode = (Hashtable)this.scriptCode.clone();
      }
    }
    catch(Exception ex) {
      ex.printStackTrace(System.err);
    }

    return obj;
  }

}//End class
