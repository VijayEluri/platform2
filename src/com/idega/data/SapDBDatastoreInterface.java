//idega 2000 - Tryggvi Larusson

/*

*Copyright 2000 idega.is All Rights Reserved.

*/



package com.idega.data;



import java.sql.Connection;

import java.sql.Statement;



/**

*@author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>

*@version 1.0

*/

public class SapDBDatastoreInterface extends DatastoreInterface{





  SapDBDatastoreInterface(){

    super.useTransactionsInEntityCreation=false;

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

      	else if (maxlength<=8000){

			theReturn = "VARCHAR("+maxlength+")";



		}

		else{

			theReturn = "LONG VARCHAR";

		}



    }

    else if (javaClassName.equals("java.lang.Boolean")){

      theReturn = "CHAR(1)";

    }

    else if (javaClassName.equals("java.lang.Float")){

      theReturn = "FLOAT";

    }

    else if (javaClassName.equals("java.lang.Double")){

      theReturn = "FLOAT";

    }

    else if (javaClassName.equals("java.sql.Timestamp")){

      theReturn = "TIMESTAMP";

    }

    else if (javaClassName.equals("java.sql.Date") || javaClassName.equals("java.util.Date")) {

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

      theReturn = "LONG BYTE";

    }

    else{

      theReturn = "";

    }

    return theReturn;

  }







  public void createTrigger(IDOLegacyEntity entity)throws Exception{



                createSequence(entity);

/*

		Connection conn= null;

		Statement Stmt= null;

		try{

			conn = entity.getConnection();

			Stmt = conn.createStatement();

			int i = Stmt.executeUpdate("CREATE TRIGGER "+entity.getTableName()+"_trig FOR "+entity.getTableName()+" AFTER INSERT EXECUTE { IF NEW."+entity.getIDColumnName()+" is null THEN{ select "+this.getSequenceName(entity)+".NEXTVAL INTO TEMP FROM DUAL; :NEW."+entity.getIDColumnName()+":=TEMP;}}");

		}

		finally{

			if(Stmt != null){

				Stmt.close();

			}

			if (conn != null){

				entity.freeConnection(conn);

			}

		}

  */

  }





  public void createSequence(IDOLegacyEntity entity)throws Exception{

    createSequence(entity,1);

  }





  public void createSequence(IDOLegacyEntity entity,int startNumber)throws Exception{



		Connection conn= null;

		Statement Stmt= null;

		try{

			conn = entity.getConnection();

			Stmt = conn.createStatement();

                        String seqCreate = "create sequence "+entity.getTableName()+"_seq INCREMENT BY 1 START WITH "+startNumber+" MAXVALUE 1.0E28 MINVALUE 0 NOCYCLE CACHE 20 NOORDER";

                        int i = Stmt.executeUpdate(seqCreate);

		}

		finally{

			if(Stmt != null){

				Stmt.close();

			}

			if (conn != null){

				entity.freeConnection(conn);

			}

		}

  }





    public void deleteEntityRecord(IDOLegacyEntity entity)throws Exception{

      //deleteTrigger(entity);

      deleteSequence(entity);

      super.deleteEntityRecord(entity);



    }



      protected void deleteTrigger(IDOLegacyEntity entity)throws Exception{

		Connection conn= null;

		Statement Stmt= null;

		try{

			conn = entity.getConnection();

			Stmt = conn.createStatement();

			int i = Stmt.executeUpdate("drop trigger "+entity.getTableName()+"_trig");

		}

		finally{

			if(Stmt != null){

				Stmt.close();

			}

			if (conn != null){

				entity.freeConnection(conn);

			}

		}

    }



      protected void deleteSequence(IDOLegacyEntity entity)throws Exception{

		Connection conn= null;

		Statement Stmt= null;

		try{

			conn = entity.getConnection();

			Stmt = conn.createStatement();

			int i = Stmt.executeUpdate("drop sequence "+entity.getTableName()+"_seq");

		}

		finally{

			if(Stmt != null){

				Stmt.close();

			}

			if (conn != null){

				entity.freeConnection(conn);

			}

		}

    }





  /*  public void createForeignKeys(IDOLegacyEntity entity)throws Exception{

		Connection conn= null;

		Statement Stmt= null;

		try{

			conn = entity.getConnection();



   		        String[] names = entity.getColumnNames();

		        for (int i = 0; i < names.length; i++){



                          if (!entity.getRelationShipClassName(names[i]).equals("")){



                            Stmt = conn.createStatement();

                            String statement = "ALTER TABLE "+entity.getTableName()+" ADD FOREIGN KEY ("+names[i]+") REFERENCES "+((IDOLegacyEntity)Class.forName(entity.getRelationShipClassName(names[i])).newInstance()).getTableName()+" ";

                            System.out.println(statement);

			    int n = Stmt.executeUpdate(statement);



                    	    if(Stmt != null){

				Stmt.close();

			    }

                          }

                        }





		}

		finally{

			if(Stmt != null){

				Stmt.close();

			}

			if (conn != null){

				entity.freeConnection(conn);

			}

		}

  }*/



  protected void executeBeforeInsert(IDOLegacyEntity entity)throws Exception{

				if ( entity.isNull(entity.getIDColumnName()) ){

					entity.setID(createUniqueID(entity));

				}

  }



/*

  protected void insertBlob(IDOLegacyEntity entity)throws Exception{

    Connection Conn = null;

    oracle.sql.BLOB blob;



    try{

      Conn = entity.getConnection();

      if(Conn == null) return;



      //Conn.setAutoCommit(false);

      Statement stmt2 = Conn.createStatement();



      String cmd = "SELECT "+entity.getLobColumnName()+" FROM "+entity.getEntityName()+" WHERE "+entity.getIDColumnName()+" ='"+entity.getID()+"' FOR UPDATE ";

      ResultSet RS2 =  stmt2.executeQuery(cmd);



      RS2.next();

      blob = ((OracleResultSet)RS2).getBLOB(1);



        // write the array of binary data to a BLOB

      OutputStream outstream = blob.getBinaryOutputStream();



      int size = blob.getBufferSize();

      byte[] buffer = new byte[size];

      int length = -1;



      BlobWrapper wrapper = entity.getBlobColumnValue(entity.getLobColumnName());

      if(wrapper!=null){

        BufferedInputStream in = new BufferedInputStream( wrapper.getInputStreamForBlobWrite() );



        while ((length = in.read(buffer)) != -1)

            outstream.write(buffer, 0, length );



        in.close();

      }

      outstream.flush();

      outstream.close();



      stmt2.close();

      RS2.close();



      //Conn.commit();

      //Conn.setAutoCommit(true);



    }

    catch(SQLException ex){ex.printStackTrace(); System.err.println( "error saving to db");}

    catch(Exception ex){ex.printStackTrace();}

    finally{

      if(Conn != null) entity.freeConnection(Conn);

    }



  }

  */



  protected String getCreateUniqueIDQuery(IDOLegacyEntity entity){

    return "SELECT "+getSequenceName(entity)+".NEXTVAL FROM dual";

  }





	private static String getSequenceName(IDOLegacyEntity entity){

		String entityName = entity.getTableName();

		return entityName+"_seq";

                /*if (entityName.endsWith("_")){

			return entityName+"seq";

		}

		else{

			return entityName+"_seq";

		}*/

	}









  public void setNumberGeneratorValue(IDOLegacyEntity entity,int value){

    //throw new RuntimeException("setSequenceValue() not implemented for "+this.getClass().getName());

    //String statement = "update sequences set last_number="+value+" where sequence_name='"+this.getSequenceName(entity)+"'";

    String statement = "drop sequence "+this.getSequenceName(entity);

    try{

      this.executeUpdate(entity,statement);

      this.createSequence(entity,value+1);

    }

    catch(Exception e){

      e.printStackTrace();

    }

  }

















  public static boolean correctSequenceValue(Connection conn){

    boolean theReturn = true;

    String[] types = new String[2];

    types[0] = "TABLE";

    types[1] = "VIEW";



    try {



      java.sql.ResultSet RS = getInstance(conn).getDatabaseMetaData().getTables(null,null,"%",types);



      while (RS.next()) {

        try {

          String tableName = RS.getString("TABLE_NAME");

          System.err.println("tableName = "+tableName);

          boolean value = correctSequenceValue(conn, tableName);

          System.err.println("done = "+value);

        }

        catch (Exception ex) {

          ex.printStackTrace();

          theReturn = false;

        }

      }



    }

    catch (Exception e) {

      e.printStackTrace();

      theReturn = false;

    }



    return theReturn;

  }





  public static boolean correctSequenceValue( Connection conn, String tableName){



    boolean theReturn = false;

    String startNumberStatement = "select max("+tableName+"_id) from "+tableName;

    String statement = "drop sequence "+tableName+"_seq";

    try{

      int value = SimpleQuerier.executeIntQuery(startNumberStatement,conn);

      if(value != -1){

        executeUpdate(conn,statement);

        createSequence(conn, tableName, value+1);

        theReturn = true;

      } else {

        theReturn = false;

      }

    }

    catch(Exception e){

      e.printStackTrace();

      theReturn = false;

    }

    return theReturn;

  }



  private static void createSequence(Connection conn, String tableName, int startNumber)throws Exception{



    Statement Stmt= null;

    try{

            Stmt = conn.createStatement();

            String seqCreate = "create sequence "+tableName+"_seq INCREMENT BY 1 START WITH "+startNumber+" MAXVALUE 1.0E28 MINVALUE 0 NOCYCLE CACHE 20 NOORDER";

            int i = Stmt.executeUpdate(seqCreate);

    }

    finally{

            if(Stmt != null){

                    Stmt.close();

            }

//            if (conn != null){

//                    entity.freeConnection(conn);

//            }

    }

  }



   private static int executeUpdate(Connection conn,String SQLCommand)throws Exception{

      Statement Stmt = null;

      int theReturn = 0;

      try{



        Stmt = conn.createStatement();

        System.out.println(SQLCommand);

        theReturn= Stmt.executeUpdate(SQLCommand);

      }

      finally {

      if (Stmt != null) {

        Stmt.close();

      }

//      if (conn != null) {

//        entity.freeConnection(conn);

//      }

      }

      return theReturn;



  }





}
