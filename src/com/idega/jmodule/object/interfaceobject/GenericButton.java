//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.jmodule.object.interfaceobject;

import java.io.*;
import java.util.*;
import com.idega.jmodule.object.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.2
*/
public class GenericButton extends InterfaceObject{

//private Image defaultImage;
//private Image onMouseOverImage;
//private Image onClickImage;


public GenericButton(){
	super();
	setName("untitled");
	setValue("");
}

public GenericButton(String name,String value){
	super();
	setName(name);
	setValue(value);
}


public void setOnMouseOver(Image mouseOverImage){
	//this.onMouseOverImage=mousOverImage;
	setAttribute("onmouseover","this.src="+mouseOverImage.getURL());
}


public void setOnClick(Image onClickImage){
	setOnClick("this.src="+onClickImage.getURL());
}

public void print(ModuleInfo modinfo) throws IOException{
	initVariables(modinfo);
	//if ( doPrint(modinfo) ){
		if (getLanguage().equals("HTML")){

			if (getInterfaceStyle().equals("default")){
				println("<input type=\"button\" name=\""+getName()+"\" "+getAttributeString()+" >");
				println("</input>");
			}
		}
	//}
}

}

