//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.core.data;

//import java.util.*;
import java.sql.*;
import com.idega.data.*;
import com.idega.jmodule.object.*;
import com.idega.block.news.presentation.NewsReader;
import com.idega.block.text.presentation.TextReader;
import com.idega.block.login.presentation.Login;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public class ICObject extends GenericEntity{

	public ICObject(){
		super();
	}

	public ICObject(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){

		//par1: column name, par2: visible column name, par3-par4: editable/showable, par5 ...

		addAttribute(getIDColumnName());
		addAttribute("object_name","Name",true,true,"java.lang.String");
		addAttribute("class_name","Class Name",true,true,"java.lang.String");
		//addAttribute("settings_url","Sl�� stillingas��u",true,true,"java.lang.String");
		//addAttribute("class_value","Klasi sj�lfur",true,true,"java.sql.Blob");
		//addAttribute("small_icon_image_id","Icon 16x16 (.gif)",false,false,"java.lang.Integer","many-to-one","com.idega.data.genericentity.Image");
		addAttribute("small_icon_image_id","Icon 16x16 (.gif)",false,false,"java.lang.Integer");
		//addAttribute("image_id","MyndN�mer",false,false,"java.lang.Integer","one-to-many","com.idega.projects.golf.entity.ImageEntity");
	}

        public void insertStartData()throws Exception{
          ICObject obj = new ICObject();
          obj.setName("Table");
          obj.setObjectClass(Table.class);
          obj.insert();

          obj = new ICObject();
          obj.setName("Image");
          obj.setObjectClass(com.idega.jmodule.object.Image.class);
          obj.insert();

          obj = new ICObject();
          obj.setName("NewsModule");
          obj.setObjectClass(NewsReader.class);
          obj.insert();

          obj = new ICObject();
          obj.setName("TextModule");
          obj.setObjectClass(TextReader.class);
          obj.insert();

          obj = new ICObject();
          obj.setName("LoginModule");
          obj.setObjectClass(Login.class);
          obj.insert();

        }

	public String getEntityName(){
		return "ic_object";
	}

	public void setDefaultValues(){
		//setColumn("image_id",1);
//                setColumn("small_icon_image_id",1);
	}

	public String getName(){
		return getStringColumnValue("object_name");
	}

        public void setName(String object_name) {
                setColumn("object_name",object_name);
        }

        public static String getClassNameColumnName(){
          return "class_name";
        }

	public String getClassName(){
		return getStringColumnValue("class_name");
	}

        public void setClassName(String className){
            setColumn("class_name",className);
        }

        public Class getObjectClass()throws ClassNotFoundException{
          return Class.forName(getClassName());
        }

        public void setObjectClass(Class c){
          setClassName(c.getName());
        }

	public ModuleObject getNewInstance()throws ClassNotFoundException,IllegalAccessException,InstantiationException{
		return (ModuleObject)getObjectClass().newInstance();
	}

}
