//idega 2000 - Tryggvi Larusson
/*
*Copyright 2000 idega.is All Rights Reserved.
*/

package com.idega.data;



import java.sql.*;
import javax.naming.*;
import javax.sql.*;
import java.util.*;
import com.idega.util.database.*;
import java.io.*;

/**
*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.0
*/
public class MySQLDatastoreInterface extends DatastoreInterface{


  MySQLDatastoreInterface(){
    useTransactionsInEntityCreation=false;
  }

 /*public String getSQLType(String javaClassName){
    String theReturn;
    if (javaClassName.equals("java.lang.Integer")){
      theReturn = "INTEGER";
    }
    else if (javaClassName.equals("java.lang.String")){
      theReturn = "VARCHAR";
    }
    else if (javaClassName.equals("java.lang.Boolean")){
      theReturn = "CHAR(1)";
    }
    else if (javaClassName.equals("java.lang.Float")){
      theReturn = "DOUBLE";
    }
    else if (javaClassName.equals("java.lang.Double")){
      theReturn = "DOUBLE";
    }
    else if (javaClassName.equals("java.sql.Timestamp")){
      theReturn = "TIMESTAMP";
    }
    else if (javaClassName.equals("java.sql.Date")){
      theReturn = "DATE";
    }
    else if (javaClassName.equals("java.sql.Blob")){
      theReturn = "BLOB";
    }
    else if (javaClassName.equals("java.sql.Time")){
      theReturn = "TIME";
    }
    else{
      theReturn = "";
    }
    return theReturn;
  }*/



  protected String getCreateUniqueIDQuery(GenericEntity entity){
    return "insert into "+getSequenceTableName(entity)+" values(null)";
  }


	/**
	**Creates a unique ID for the ID column
	**/
	public int createUniqueID(GenericEntity entity) throws Exception{
		int returnInt = -1;
		Connection conn = null;
		Statement stmt = null;
		ResultSet RS = null;
		try{

                      conn = entity.getConnection();
                      stmt = conn.createStatement();
                      stmt.executeUpdate(getCreateUniqueIDQuery(entity));
                      stmt.close();

                      stmt = conn.createStatement();
                      RS = stmt.executeQuery("select last_insert_id()");
                      RS.next();
                      returnInt = RS.getInt(1);
		}
		finally{
			if (RS != null){
				RS.close();
			}
			if (stmt != null){
				stmt.close();
			}
			if (conn != null){
				entity.freeConnection(conn);
			}
		}
		return returnInt;
	}


  protected void executeBeforeInsert(GenericEntity entity)throws Exception{
    if ( entity.isNull(entity.getIDColumnName()) ){
      entity.setID(createUniqueID(entity));
    }
  }

  public String getSequenceTableName(GenericEntity entity){
    return "seq_"+entity.getTableName();
  }


    public String getSQLType(String javaClassName,int maxlength){
      String theReturn;
      if (javaClassName.equals("java.lang.Integer")){
        theReturn = "INTEGER";
      }
      else if (javaClassName.equals("java.lang.String")){
        if (maxlength<0){
  	    theReturn = "VARCHAR(255)";
        }
        else if (maxlength<=255){
            theReturn = "VARCHAR("+maxlength+")";
        }

        else{
  	    theReturn = "TEXT";
        }

      }
      else if (javaClassName.equals("java.lang.Boolean")){
        theReturn = "CHAR(1)";
      }
      else if (javaClassName.equals("java.lang.Float")){
        theReturn = "DOUBLE";
      }
      else if (javaClassName.equals("java.lang.Double")){
        theReturn = "DOUBLE";
      }
      else if (javaClassName.equals("java.sql.Timestamp")){
        theReturn = "DATE";
      }
      else if (javaClassName.equals("java.sql.Date")){
        theReturn = "DATE";
      }
      else if (javaClassName.equals("java.sql.Blob")){
        theReturn = "BLOB";
      }
      else if (javaClassName.equals("java.sql.Time")){
        theReturn = "TIME";
      }
    else if (javaClassName.equals("com.idega.util.Gender")) {
      theReturn = "VARCHAR(1)";
    }
    else if (javaClassName.equals("com.idega.data.BlobWrapper")) {
      theReturn = "BLOB";
    }
      else{
        theReturn = "";
      }
      return theReturn;
    }


  public void createSequenceTable(GenericEntity entity)throws Exception{
    Connection conn = null;
    Statement Stmt = null;
    try {
      conn = entity.getConnection();
      Stmt = conn.createStatement();
      String s = "CREATE table " + getSequenceTableName(entity)+"("+entity.getIDColumnName()+" integer PRIMARY KEY auto_increment)";
      System.out.println(s);
      int i = Stmt.executeUpdate(s);

    }
    finally {
      if (Stmt != null) {
        Stmt.close();
      }
      if (conn != null) {
        entity.freeConnection(conn);
      }
    }
  }


  public void createTrigger(GenericEntity entity)throws Exception{
    createSequenceTable(entity);
  }

  public void createForeignKeys(GenericEntity entity)throws Exception{
  }


    protected String getCreationStatement(GenericEntity entity){
		String returnString = "create table "+entity.getTableName()+"(";
		String[] names = entity.getColumnNames();
		for (int i = 0; i < names.length; i++){

                    /*if (entity.getMaxLength(names[i]) == -1){
                      if (entity.getStorageClassName(names[i]).equals("java.lang.String")){
                        returnString = 	returnString + names[i]+" "+getSQLType(entity.getStorageClassName(names[i]))+"(255)";
                      }
                      else{
                        returnString = 	returnString + names[i]+" "+getSQLType(entity.getStorageClassName(names[i]));
                      }

                    }
                    else{
                      returnString = 	returnString + names[i]+" "+getSQLType(entity.getStorageClassName(names[i]))+"("+entity.getMaxLength(names[i])+")";
                    }*/
                    returnString = 	returnString + names[i]+" "+getSQLType(entity.getStorageClassName(names[i]),entity.getMaxLength(names[i]));

                    if (entity.isPrimaryKey(names[i])){
                      returnString = 	returnString + " PRIMARY KEY auto_increment";
                    }
                    if (i!=names.length-1){
                      returnString = returnString+",";
                    }
		}
                returnString = returnString +")";
                System.out.println(returnString);
		return returnString;
}

  //protected void insertBlob(GenericEntity entity)throws Exception{
  //}

/*
  protected void executeAfterInsert(GenericEntity entity)throws Exception{

    Connection conn= null;
    Statement Stmt= null;
		ResultSet RS = null;
		try{
			conn = entity.getConnection();
                        if (entity.getID() == -1){
					Stmt = conn.createStatement();
					RS = Stmt.executeQuery("select last_insert_id()");
					//RS.next();
					entity.setID(RS.getInt(1));
			}

		}
		finally{
			if (RS != null){
				RS.close();
			}
			if(Stmt != null){
				Stmt.close();
			}
			if (conn != null){
				entity.freeConnection(conn);
			}
		}
                super.executeAfterInsert(entity);
  }
  */


   public void handleBlobUpdate(String columnName,PreparedStatement statement, int index,GenericEntity entity){
          BlobWrapper wrapper = entity.getBlobColumnValue(columnName);
          //System.out.println("in MySQLhandleBlobUpdate");
          if(wrapper!=null){
            InputStream stream = wrapper.getInputStreamForBlobWrite();
            //System.out.println("in MySQLhandleBlobUpdate wrapper!=null");
            if(stream!=null){
              try{
                //System.out.println("in MySQLhandleBlobUpdate, stream != null");
                //BufferedInputStream bin = new BufferedInputStream( stream );
                //statement.setBinaryStream(index, bin, bin.available() );
                  byte[] data = new byte[stream.available()];
                  //System.out.println("data.length="+data.length);
                  int noread = stream.read(data);
                  int i = 1;
                  while(noread!=-1){
                    noread = stream.read(data);
                    //System.out.println("in while "+i);
                    i++;
                  }
                  statement.setBytes(index, data );
                //statement.setBinaryStream(index, stream, stream.available() );
              }
              catch(Exception e){
                //System.err.println("Error updating BLOB field in "+entity.getClass().getName());
                e.printStackTrace();
              }
            }
          }

        }

  protected void insertBlob(GenericEntity entity)throws Exception{

    String statement ;
    Connection Conn = null;

    try{

      statement = "update " + entity.getEntityName() + " set " + entity.getLobColumnName() + "=? where " + entity.getIDColumnName() + " = '" + entity.getID()+"'";
      //System.out.println(statement);
      //System.out.println("In insertBlob() in DatastoreInterface");
      BlobWrapper wrapper = entity.getBlobColumnValue(entity.getLobColumnName());
      if(wrapper!=null){
        //System.out.println("In insertBlob() in DatastoreInterface wrapper!=null");
        //Conn.setAutoCommit(false);
        InputStream instream = wrapper.getInputStreamForBlobWrite();
        if(instream!=null){
          //System.out.println("In insertBlob() in DatastoreInterface instream != null");
          Conn = entity.getConnection();
          //if(Conn== null){ System.out.println("In insertBlob() in DatastoreInterface conn==null"); return;}
          //BufferedInputStream bin = new BufferedInputStream(instream);
          byte[] data = new byte[instream.available()];
          //System.out.println("data.length="+data.length);
          int noread = instream.read(data);
          int i = 1;
          while(noread!=-1){
            noread = instream.read(data);
            //System.out.println("in while "+i);
            i++;
          }
          PreparedStatement PS = Conn.prepareStatement(statement);
          //System.out.println("bin.available(): "+bin.available());
          //PS.setBinaryStream(1, bin, 0 );
          //PS.setBinaryStream(1, instream, instream.available() );
          PS.setBytes(1,data);
          PS.executeUpdate();
          PS.close();
          //System.out.println("bin.available(): "+bin.available());
          instream.close();
          //bin.close();
        }
        //Conn.commit();
        //Conn.setAutoCommit(true);
      }


    }
    catch(SQLException ex){ex.printStackTrace(); System.err.println( "error uploading blob to db for "+entity.getClass().getName());}
    catch(Exception ex){ex.printStackTrace();}
    finally{
      if(Conn != null) entity.freeConnection(Conn);
    }


  }


}
