//idega 2001 - Tryggvi Larusson
/*
*Copyright 2001 idega.is All Rights Reserved.
*/
package com.idega.builder.data;

//import java.util.*;
import java.sql.*;
import com.idega.data.*;
import com.idega.jmodule.object.*;


/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
*/
public class IBObjectInstance extends GenericEntity{

	public IBObjectInstance(){
		super();
	}

	public IBObjectInstance(int id)throws SQLException{
		super(id);
	}

	public void initializeAttributes(){

		//par1: column name, par2: visible column name, par3-par4: editable/showable, par5 ...

		addAttribute(getIDColumnName());
		addAttribute("ib_object_id","Vefeining",true,true,"java.lang.Integer","many-to-one","com.idega.builder.data.IBObject");

	}

	public String getEntityName(){
		return "ib_object_instance";
	}

	public void setDefaultValues(){
		//setColumn("image_id",1);
	}

	public String getName(){
		return getObject().getName()+" nr. "+this.getID();
	}

        public void setObjectID(int id){
          this.setColumn("ib_object_id",id);
        }

	public IBObject getObject(){
		return (IBObject) getColumnValue("ib_object_id");
	}

	public ModuleObject getNewInstance()throws Exception{
		return getObject().getNewInstance();
	}

	/*public IBObjectProperty[] getProperties(){
		IBObjectProperty[] array = new IBObjectProperty[0];
		try{
			array = (IBObjectProperty[])(new IBObjectProperty()).findAllByColumn(this.getIDColumnName(),this.getID());
		}
		catch(Exception ex){
			System.err.println("There was an error in IBObjectInstance: "+ex.getMessage());
		}
		return array;
	}*/



        /**
         * Unimplemented
         */
        /*public void addInstance(IBObjectInstance instanceToAdd)throws Exception{
          int id = instanceToAdd.getID();
          boolean checkIfOthers=false;
          IBObjectProperty[] properties = this.getProperties();
          for(int i=0;i<properties.length;i++){

            if(properties[i].getObjectInstanceID() == this.getID() && properties[i].getName().equals("idega_special_add")){
              checkIfOthers=true;
              IBObjectPropertyValue value = new IBObjectPropertyValue();
              value.setProperty(properties[i]);
              value.setPropertyValue(Integer.toString(id));
              value.insert();
              //theReturn = new IBObjectInstance[values.length];
              //for(int n=0;n<values.length;n++){
              //  theReturn[n] = new IBObjectInstance(Integer.parseInt(values[n].getPropertyValue()));
              //}


            }

            if(!checkIfOthers){
              IBObjectProperty property = new IBObjectProperty();
              property.setObjectInstance(this);
              property.setName("idega_special_add");
              property.insert();

              IBObjectPropertyValue value = new IBObjectPropertyValue();
              value.setProperty(property);
              value.setPropertyValue(Integer.toString(id));
              value.insert();

            }


          }


        }*/


        /**
         * Returns null if nothing found
         */
        public IBObjectInstance[] getContainingObjects()throws Exception{
          /*Connection conn = this.getConnection();
          Statement stmt = conn.createStatement();
          ResultSet RS = stmt.executeQuery("select ib_object_instance_id from ib_object_property where ib_object_property_name='idega_special_add' and ");
          while(RS.next()){

          }

          finally{
            if(conn!= null){
              freeConnection(conn);
            }
          }*/
          IBObjectInstance[] theReturn = null;

          /*IBObjectProperty[] properties = this.getProperties();
          for(int i=0;i<properties.length;i++){
            if(properties[i].getObjectInstanceID() == this.getID() && properties[i].getName().equals("idega_special_add")){
              IBObjectPropertyValue[] values = properties[i].getPropertyValues();
              theReturn = new IBObjectInstance[values.length];
              for(int n=0;n<values.length;n++){
                theReturn[n] = new IBObjectInstance(Integer.parseInt(values[n].getPropertyValue()));
              }
            }

          }*/
          return theReturn;
        }
}
