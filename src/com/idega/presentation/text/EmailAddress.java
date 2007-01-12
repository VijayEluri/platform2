//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/



package com.idega.presentation.text;



import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.ui.Window;





/**

*@author <a href="mailto:gimmi@idega.is">Grimur Jonsson</a>

*@version 1.2

*/

public class EmailAddress extends Link{



private PresentationObject obj;

private String ObjectType;

private String parameterString;



public EmailAddress(){

	super("");

}



public EmailAddress(String text){

	super( new Text(text) );

	setURL("mailto:"+text);

}



public EmailAddress(PresentationObject mo, Window myWindow){

}



public EmailAddress(Window myWindow){



}



public EmailAddress(PresentationObject mo){

	super();

	this.obj = mo;

	this.obj.setParentObject(this);

	this.ObjectType="PresentationObject";

}



public EmailAddress(Text text){

	super();

	text.setFontColor("");

	this.obj = text;

	this.obj.setParentObject(this);

	this.ObjectType="Text";



}



public EmailAddress(String text,String url){

	this(new Text(text),url);

}



public EmailAddress(PresentationObject mo,String url){

	super();

	this.obj = mo;

	setURL("mailto:"+url);

	this.obj.setParentObject(this);

	this.ObjectType="PresentationObject";

}



public EmailAddress(Text text,String url){

	super();

	text.setFontColor("");

	this.obj = text;

	setURL("mailto:"+url);

	this.obj.setParentObject(this);

	this.ObjectType="Text";

}



protected String getParameterString(IWContext iwc){



if ( this.parameterString==null) {
	this.parameterString="";
}

return this.parameterString;



}



public void setEmailAddress(String email){



	setURL("mailto"+email);



}





}



