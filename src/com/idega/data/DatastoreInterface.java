/*
 * $Id: DatastoreInterface.java,v 1.95 2004/03/12 11:29:59 gimmi Exp $
 *
 * Copyright (C) 2001 Idega hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 *
 */
package com.idega.data;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.transaction.TransactionManager;

import com.idega.idegaweb.IWMainApplicationSettings;
import com.idega.transaction.IdegaTransactionManager;
import com.idega.util.Gender;
import com.idega.util.IWTimestamp;
import com.idega.util.database.ConnectionBroker;
import com.idega.util.logging.LoggingHelper;
/**
 * A class to serve as an abstraction of the underlying datastore
 *
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.3
  */
public abstract class DatastoreInterface {
	private static Hashtable interfacesHashtable;
	private static Map interfacesByDatasourcesMap;
	final static int STATEMENT_INSERT = 1;
	final static int STATEMENT_UPDATE = 2;
	protected boolean useTransactionsInEntityCreation = true;
	protected IDOTableCreator _TableCreator;
	protected DatabaseMetaData _databaseMetaData;
	public static DatastoreInterface getInstance(String datastoreType) {
		DatastoreInterface theReturn = null;
		String className;
		if (interfacesHashtable == null) {
			interfacesHashtable = new Hashtable();
		}
		if (datastoreType.equals("oracle")) {
			className = "com.idega.data.OracleDatastoreInterface";
		} else if (datastoreType.equals("interbase")) {
			className = "com.idega.data.InterbaseDatastoreInterface";
		} else if (datastoreType.equals("mysql")) {
			className = "com.idega.data.MySQLDatastoreInterface";
		} else if (datastoreType.equals("sapdb")) {
			className = "com.idega.data.SapDBDatastoreInterface";
		} else if (datastoreType.equals("mssqlserver")) {
			className = "com.idega.data.MSSQLServerDatastoreInterface";
		} else if (datastoreType.equals("db2")) {
			className = "com.idega.data.DB2DatastoreInterface";
		} else if (datastoreType.equals("informix")) {
			className = "com.idega.data.InformixDatastoreInterface";
		} else if (datastoreType.equals("hsql")) {
			className = "com.idega.data.HSQLDatastoreInterface";
		} else if (datastoreType.equals("mckoi")) {
			className = "com.idega.data.McKoiDatastoreInterface";
		} else {
			//className = "unimplemented DatastoreInterface";
			throw new IDONoDatastoreError();
		}
		theReturn = (DatastoreInterface)interfacesHashtable.get(className);
		if (theReturn == null) {
			try {
				theReturn = (DatastoreInterface)Class.forName(className).newInstance();
				interfacesHashtable.put(className, theReturn);
			} catch (Exception ex) {
				System.err.println("There was an error in com.idega.data.DatastoreInterface.getInstance(String className): " + ex.getMessage());
			}
		}
		return theReturn;
	}
	public static String getDatastoreType(String datasourceName) {
		Connection conn = null;
		String theReturn = "";
		try {
			conn = ConnectionBroker.getConnection(datasourceName);
			theReturn = getDataStoreType(conn);
		} finally {
			ConnectionBroker.freeConnection(datasourceName, conn);
		}
		return theReturn;
	}


	/**
	 * This method gets the correct instance of DatastoreInterface for the default datasource
	 * @return the instance of DatastoreInterface for the current application
	 */
	public static DatastoreInterface getInstance() {
		Connection conn=null;
		try{
			conn = ConnectionBroker.getConnection();
			return getInstance(conn);
		}
		finally{
			if(conn!=null){
				ConnectionBroker.freeConnection(conn);
			}
		}
	}

	/**
	 * This method gets the correct instance of DatastoreInterface for the Connection connection
	 * @param connection the connection to get the DatastoreInterface implementation for
	 * @return
	 */
	public static DatastoreInterface getInstance(Connection connection) {
		//String datastoreType = getDataStoreType(connection);
		//if(datastoreType.equals("idega"){
		if (connection instanceof com.idega.data.DatastoreConnection) {
			return ((DatastoreConnection)connection).getDatastoreInterface();
		}
		return getInstance(getDataStoreType(connection));
	}
	/**
	 * This method gets the correct instance of DatastoreInterface for the GenericEntity method
	 * @param entity the bean instance to get the DatastoreInterface implementation for
	 * @return
	 */
	public static DatastoreInterface getInstance(GenericEntity entity) {
		//String datastoreType=null;
		try {
			DatastoreInterface theReturn = getDatastoreInterfaceByDatasource(entity.getDatasource());
			if (theReturn == null) {
				Connection conn = entity.getConnection();
				theReturn = getInstance(conn);
				entity.freeConnection(conn);
			}
			if (theReturn != null) {
				setDatastoreInterfaceByDatasource(entity.getDatasource(), theReturn);
				return theReturn;
			} else {
				throw new IDONoDatastoreError("Datastore type can not be obtained or identified");
			}
		} catch (Exception ex) {
			//  System.err.println("Exception in DatastoreInterface.getInstance(IDOLegacyEntity entity): "+ex.getMessage());
			//}
			//catch(NullPointerException npe){
			//
			ex.printStackTrace();
			throw new IDONoDatastoreError();
		}
		//return getInstance(datastoreType);
	}
	/**
	
	 * Returns the type of the underlying datastore - returns: "mysql", "interbase", "oracle", "unimplemented"
	
	 */
	public static String getDataStoreType(Connection connection) {
		String dataStoreType;
		if (connection != null) {
			if (connection instanceof com.idega.data.DatastoreConnection) {
				return getDataStoreType(((DatastoreConnection)connection).getUnderLyingConnection());
			} else {
				String checkString = null;
				try {
					checkString = connection.getMetaData().getDatabaseProductName().toLowerCase();
				} catch (SQLException e) {
					//Old Check
					e.printStackTrace();
					checkString = connection.getClass().getName();
				}
				if (checkString.indexOf("oracle") != -1) {
					dataStoreType = "oracle";
				} else if (checkString.indexOf("interbase") != -1 || checkString.indexOf("firebird") != -1) {
					dataStoreType = "interbase";
				} else if (checkString.indexOf("hsql") != -1 || checkString.indexOf("hypersonicsql") != -1) {
					dataStoreType = "hsql";
				} else if (checkString.indexOf("mckoi") != -1 ) {
					dataStoreType = "mckoi";
				} else if (checkString.indexOf("mysql") != -1) {
					dataStoreType = "mysql";
				} else if (checkString.indexOf("sap") != -1) {
					dataStoreType = "sapdb";
				} else if (checkString.indexOf("db2") != -1) {
					dataStoreType = "db2";
				} else if (checkString.indexOf("microsoft sql") != -1 || checkString.indexOf("microsoftsql") != -1) {
					dataStoreType = "mssqlserver";
				} else if (checkString.indexOf("informix") != -1) {
					dataStoreType = "informix";
				} else if (checkString.indexOf("idega") != -1) {
					dataStoreType = "idega";
				} else {
					dataStoreType = "unimplemented";
				}
			}
		} else {
			dataStoreType = "";
		}
		return dataStoreType;
	}
	public String getSQLType(Class javaClass, int maxlength) {
		return getSQLType(javaClass.getName(), maxlength);
	}
	public abstract String getSQLType(String javaClassName, int maxlength);
	public String getIDColumnType() {
		return "INTEGER";
	}
	public IDOTableCreator getTableCreator() {
		if (_TableCreator == null) {
			_TableCreator = new IDOTableCreator(this);
		}
		return _TableCreator;
	}
	public void createEntityRecord(GenericEntity entity) throws Exception {
		getTableCreator().createEntityRecord(entity);
	}
	public void executeBeforeCreateEntityRecord(GenericEntity entity) throws Exception {
	}
	public void executeAfterCreateEntityRecord(GenericEntity entity) throws Exception {
	}
	public void deleteEntityRecord(GenericEntity entity) throws Exception {
		getTableCreator().deleteEntityRecord(entity);
	}
	public abstract void createTrigger(GenericEntity entity) throws Exception;
	//public abstract void createForeignKeys(IDOLegacyEntity entity)throws Exception;
	/**
	 * Executes a query to the entity's set datasource and returns the first result (ResultSet.getObject(1)).
	 * Returns null if there was no result.
	 * @param entity an entity instance for the datasource to query to.
	 * @param a well formatted SQL command string
	 */
	protected Object executeQuery(GenericEntity entity, String SQLCommand) throws Exception {
		return executeQuery(entity.getDatasource(),SQLCommand);
	}
	
	/**
	 * Executes a query to the  datasource and returns the first result (ResultSet.getObject(1)).
	 * Returns null if there was no result.
	 * @param dataSourceName
	 * @param SQLCommand
	 * @return
	 * @throws Exception
	 */
	protected Object executeQuery(String dataSourceName, String SQLCommand) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		ResultSet rs = null;
		Object theReturn = null;
		try {
			conn = ConnectionBroker.getConnection(dataSourceName);
			Stmt = conn.createStatement();
			//System.out.println(SQLCommand);
			rs = Stmt.executeQuery(SQLCommand);
			if (rs != null && rs.next()) {
				theReturn = rs.getObject(1);
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				ConnectionBroker.freeConnection(conn);
			}
		}
		return theReturn;
	}
	protected int executeUpdate(GenericEntity entity, String SQLCommand) throws Exception {
		Connection conn = null;
		Statement Stmt = null;
		int theReturn = 0;
		try {
			conn = entity.getConnection();
			//conn.commit();
			Stmt = conn.createStatement();
			log(SQLCommand);
			theReturn = Stmt.executeUpdate(SQLCommand);
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
		return theReturn;
	}
	/*public void populateBlob(BlobWrapper blob){
	
	        try{
	
		PreparedStatement myPreparedStatement = blob.getConnection().prepareStatement("insert into "+blob.getEntity().getTableName()+"("+blob.getTableColumnName()+") values(?) where "+blob.getEntity().getIDColumnName()+"='"+blob.getEntity().getID()+"'");
	
		// ByteArrayInputStream byteinstream = new ByteArrayInputStream(longbbuf);
	
		//InputStream byteinstream = new InputStream(longbbuf);
	
	
	
		//OutputStream out = blob.getOutputStream();
	
		InputStream byteinstream = blob.getInputStreamForBlobWrite();
	
	            //InputStream myInputStream = new InputStream();
	
	
	
	
	
					//byte	buffer[]= new byte[1024];
	
					//int		noRead	= 0;
	
	
	
					//noRead	= myInputStream.read( buffer, 0, 1023 );
	
	
	
					//Write out the file to the browser
	
					//while ( noRead != -1 ){
	
					//	output.write( buffer, 0, noRead );
	
					//	noRead	= myInputStream.read( buffer, 0, 1023 );
	
	      //
	
	
	
	
	
		myPreparedStatement.setBinaryStream(1, byteinstream, byteinstream.available() );
	
	
	
		myPreparedStatement.execute();
	
		myPreparedStatement.close();
	
	          }
	
	          catch(Exception ex){
	
	            System.err.println("Exception in DatastoreInterface.populateBlob: "+ex.getMessage());
	
	            ex.printStackTrace(System.err);
	
	          }
	
	
	
	}*/
	public boolean isConnectionOK(Connection conn) {
		Statement testStmt = null;
		try {
			if (!conn.isClosed()) {
				// Try to createStatement to see if it's really alive
				testStmt = conn.createStatement();
				testStmt.close();
			} else {
				return false;
			}
		} catch (Exception e) {
			if (testStmt != null) {
				try {
					testStmt.close();
				} catch (Exception se) {
				}
			}
			//logWriter.log(e, "Pooled Connection was not okay",LogWriter.ERROR);
			return false;
		}
		return true;
	}

	public void insert(GenericEntity entity) throws Exception {
		Connection conn = null;
		try {
			conn = entity.getConnection();
			insert(entity, conn);
		} finally {
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
	}

	/*
	public void insert(IDOLegacyEntity entity) throws Exception {
		this.executeBeforeInsert(entity);
		Connection conn = null;
		//Statement Stmt= null;
		PreparedStatement Stmt = null;
		ResultSet RS = null;
		try {
			conn = entity.getConnection();
			//Stmt = conn.createStatement();
			//int i = Stmt.executeUpdate("insert into "+entity.getTableName()+"("+entity.getCommaDelimitedColumnNames()+") values ("+entity.getCommaDelimitedColumnValues()+")");
			StringBuffer statement = new StringBuffer("");
			statement.append("insert into ");
			statement.append(entity.getTableName());
			statement.append("(");
			statement.append(getCommaDelimitedColumnNamesForInsert(entity));
			statement.append(") values (");
			statement.append(getQuestionmarksForColumns(entity));
			statement.append(")");
			if (isDebugActive())
				debug(statement.toString());
			Stmt = conn.prepareStatement(statement.toString());
			setForPreparedStatement(STATEMENT_INSERT, Stmt, entity);
			Stmt.execute();
			
			if(updateNumberGeneratedValueAfterInsert()){
				updateNumberGeneratedValue(entity,conn);
			}
			
		} finally {
			if (RS != null) {
				RS.close();
			}
			if (Stmt != null) {
				Stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
		this.executeAfterInsert(entity);
		entity.setEntityState(entity.STATE_IN_SYNCH_WITH_DATASTORE);
	}*/
	/**
	 * @param entity
	 * @param conn
	 */
	protected void updateNumberGeneratedValue(GenericEntity entity, Connection conn) {

	}
	/**
	 * @return boolean
	 */
	protected boolean updateNumberGeneratedValueAfterInsert() {
		return false;
	}
	/**
	
	**Creates a unique ID for the ID column
	
	**/
	public int createUniqueID(GenericEntity entity) throws Exception {
		int returnInt = -1;
		Connection conn = null;
		Statement stmt = null;
		ResultSet RS = null;
		try {
			conn = entity.getConnection();
			stmt = conn.createStatement();
			String sql = getCreateUniqueIDQuery(entity);
			logSQL(sql);
			RS = stmt.executeQuery(sql);
			RS.next();
			returnInt = RS.getInt(1);
		} finally {
			if (RS != null) {
				RS.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
		return returnInt;
	}
	protected String getCreateUniqueIDQuery(GenericEntity entity) throws Exception {
		return "";
	}
	protected void executeBeforeInsert(GenericEntity entity) throws Exception {
	}
	protected void executeAfterInsert(GenericEntity entity) throws Exception {
		if (entity.hasLobColumn() && (entity.getBlobColumnValue(entity.getLobColumnName()).getInputStreamForBlobWrite() != null)) {
			insertBlob(entity);
		}
		if (entity.hasMetaDataRelationship()) {
			crunchMetaData(entity);
		}
	}
	protected void executeBeforeUpdate(GenericEntity entity) throws Exception {
	}
	protected void executeAfterUpdate(GenericEntity entity) throws Exception {
		if (!supportsBlobInUpdate()) {
			if (entity.hasLobColumn() && (entity.getBlobColumnValue(entity.getLobColumnName()).getInputStreamForBlobWrite() != null)) {
				insertBlob(entity);
			}
		}
		if (entity.hasMetaDataRelationship()) {
			crunchMetaData(entity);
		}
	}
	protected void executeBeforeDelete(GenericEntity entity) throws Exception {
	}
	protected void executeAfterDelete(GenericEntity entity) throws Exception {
	}
	protected void crunchMetaData(GenericEntity entity) throws SQLException {
		if (entity.metaDataHasChanged()) { //else do nothing
			TransactionManager t = IdegaTransactionManager.getInstance();
			try {
				t.begin();
				int length;
				MetaData data;
				Map metadata = entity.getMetaDataAttributes();
				Hashtable ids = entity.getMetaDataIds();
				Map types = entity.getMetaDataTypes();
				Vector insert = entity.getMetaDataInsertVector();
				Vector delete = entity.getMetaDataDeleteVector();
				Vector update = entity.getMetaDataUpdateVector();
				if (insert != null) {
					length = insert.size();
					for (int i = 0; i < length; i++) {
						data = ((com.idega.data.MetaDataHome)com.idega.data.IDOLookup.getHomeLegacy(MetaData.class)).createLegacy();
						data.setMetaDataNameAndValue((String)insert.elementAt(i), (String)metadata.get((String)insert.elementAt(i)));
						if (types != null && types.containsKey((String)insert.elementAt(i)))
							data.setMetaDataType((String) types.get((String)insert.elementAt(i)));
						else
							data.setMetaDataType("java.lang.String");
						data.store();
						entity.idoAddTo(data);
					}
				}
				//else       System.out.println("insert is null");
				if (update != null) {
					length = update.size();
					//				System.out.println("update size: " + length);
					for (int i = 0; i < length; i++) {
						//System.out.println("updating: "+i);
						data = ((com.idega.data.MetaDataHome)com.idega.data.IDOLookup.getHomeLegacy(MetaData.class)).findByPrimaryKey((Integer)ids.get(update.elementAt(i)));
						//do not construct with id to avoid database access
						if (ids == null)
							System.out.println("ids is null");
						//System.out.println("ID: "+data.getID());
						data.setMetaDataNameAndValue((String)update.elementAt(i), (String)metadata.get((String)update.elementAt(i)));
						if (types != null && types.containsKey((String)update.elementAt(i)))
							data.setMetaDataType((String) types.get((String)update.elementAt(i)));
						else
							data.setMetaDataType("java.lang.String");
						data.store();
					}
				}
				//else       System.out.println("update is null");
				if (delete != null) {
					length = delete.size();
					for (int i = 0; i < length; i++) {
						data = ((com.idega.data.MetaDataHome)com.idega.data.IDOLookup.getHomeLegacy(MetaData.class)).createLegacy();
						data.setID((Integer)ids.get(delete.elementAt(i)));
						data.remove();
					}
				}
				//else       System.out.println("delete is null");
	
				entity.metaDataHasChanged(false); //so we don't do anything next time
	      t.commit();
			} catch (Exception e) {
  			try {
  				t.rollback();
  			} catch (Exception e1) {
  				throw new SQLException(e1.getMessage());
  			}
  			throw new SQLException(e.getMessage());
			}
		}
	}
	protected void insertBlob(GenericEntity entity) throws Exception {
		StringBuffer statement;
		Connection Conn = null;
		InputStream instream = null;
		PreparedStatement PS = null;
		try {
			statement = new StringBuffer("");
			statement.append("update ");
			statement.append(entity.getTableName());
			statement.append(" set ");
			statement.append(entity.getLobColumnName());
			statement.append("=? ");
			this.appendPrimaryKeyWhereClause(entity,statement);
			/*statement.append("where ");
			statement.append(entity.getIDColumnName());
			statement.append(" = '");
			statement.append(entity.getID());
			statement.append("'");*/
			//System.out.println(statement);
			//System.out.println("In insertBlob() in DatastoreInterface");
			BlobWrapper wrapper = entity.getBlobColumnValue(entity.getLobColumnName());
			if (wrapper != null) {
				//System.out.println("In insertBlob() in DatastoreInterface wrapper!=null");
				//Conn.setAutoCommit(false);
				instream = wrapper.getInputStreamForBlobWrite();
				if (instream != null) {
					//System.out.println("In insertBlob() in DatastoreInterface instream != null");
					Conn = entity.getConnection();
					//if(Conn== null){ System.out.println("In insertBlob() in DatastoreInterface conn==null"); return;}
					//BufferedInputStream bin = new BufferedInputStream(instream);
					String sql = statement.toString();
					PS = Conn.prepareStatement(sql);
					//System.out.println("bin.available(): "+bin.available());
					//PS.setBinaryStream(1, bin, 0 );
					//PS.setBinaryStream(1, instream, instream.available() );
					this.setBlobstreamForStatement(PS, instream, 1);
					PS.executeUpdate();
					PS.close();
					//System.out.println("bin.available(): "+bin.available());
					instream.close();
					// bin.close();
				}
				//Conn.commit();
				//Conn.setAutoCommit(true);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			System.err.println("error uploading blob to db for " + entity.getClass().getName());
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (PS != null) {
				try {
					PS.close();
				} catch (SQLException sqle) {
				}
			}
			if (Conn != null)
				entity.freeConnection(Conn);
			if (instream != null)
				instream.close();
		}
	}
	protected String setForPreparedStatement(int insertOrUpdate, PreparedStatement statement, GenericEntity entity) throws SQLException {
		String returnString = "";
		String[] names = entity.getColumnNames();
		int questionmarkCount = 1;
		if (insertOrUpdate == STATEMENT_UPDATE) {
			for (int i = 0; i < names.length; i++) {
				if (isValidColumnForUpdateList(entity, names[i])) {
					//if (returnString.equals("")){
					//	returnString = 	"'"+getStringColumnValue(names[i])+"'";
					//}
					//else{
					//	returnString = 	returnString + ",'" + getStringColumnValue(names[i])+"'";
					//}
					//System.out.println(names[i]);
					insertIntoPreparedStatement(names[i], statement, questionmarkCount, entity);
					questionmarkCount++;
				}
			}
		} else if (insertOrUpdate == STATEMENT_INSERT) {
			for (int i = 0; i < names.length; i++) {
				if (isValidColumnForInsertList(entity, names[i])) {
					//if (returnString.equals("")){
					//	returnString = 	"'"+getStringColumnValue(names[i])+"'";
					//}
					//else{
					//	returnString = 	returnString + ",'" + getStringColumnValue(names[i])+"'";
					//}
					//System.out.println(names[i]);
					insertIntoPreparedStatement(names[i], statement, questionmarkCount, entity);
					questionmarkCount++;
				}
			}
		}
		return returnString;
	}
	private void insertIntoPreparedStatement(String columnName, PreparedStatement statement, int index, GenericEntity entity) throws SQLException {
		try {

			String storageClassName = entity.getStorageClassName(columnName);
			if (storageClassName.equals("java.lang.Integer")) {
				statement.setInt(index, entity.getIntColumnValue(columnName));
			} else if (storageClassName.equals("java.lang.Boolean")) {
				boolean bool = entity.getBooleanColumnValue(columnName);
				if (bool) {
					statement.setString(index, "Y");
				} else {
					statement.setString(index, "N");
				}
			} else if (storageClassName.equals("java.lang.String")) {
				//statement.setString(index,entity.getStringColumnValue(columnName));
				setStringForPreparedStatement(columnName, statement, index, entity);
			} else if (storageClassName.equals("java.lang.Float")) {
				statement.setFloat(index, entity.getFloatColumnValue(columnName));
			} else if (storageClassName.equals("java.lang.Double")) {
				statement.setDouble(index, entity.getDoubleColumnValue(columnName));
			} else if (storageClassName.equals("java.sql.Timestamp")) {
				Timestamp stamp = (Timestamp)entity.getColumnValue(columnName);
				statement.setTimestamp(index, stamp);
			} else if (storageClassName.equals("java.sql.Time")) {
				statement.setTime(index, (Time)entity.getColumnValue(columnName));
			} else if (storageClassName.equals("java.sql.Date")) {
				statement.setDate(index, (java.sql.Date)entity.getColumnValue(columnName));
			} else if (storageClassName.equals("com.idega.util.Gender")) {
				statement.setString(index, entity.getColumnValue(columnName).toString());
			} else if (storageClassName.equals("com.idega.data.BlobWrapper")) {
				handleBlobUpdate(columnName, statement, index, entity);
				//statement.setDate(index,(java.sql.Date)getColumnValue(columnName));
			} else {
				statement.setObject(index, entity.getColumnValue(columnName));
			}
		} catch (Exception ex) {
			System.out.println("Original error message");
			ex.printStackTrace();
			throw new SQLException("Entity: " + entity.getEntityName() + "; Column:  " + columnName + " - " + ex.getMessage());
		}
	}
	public void handleBlobUpdate(String columnName, PreparedStatement statement, int index, GenericEntity entity) {
		BlobWrapper wrapper = entity.getBlobColumnValue(columnName);
		//System.out.println("DatastoreInterface, in handleBlobUpdate, columnName="+columnName+" index="+index);
		if (wrapper != null) {
			InputStream stream = wrapper.getInputStreamForBlobWrite();
			//System.out.println("DatastoreInterface, in handleBlobUpdate wrapper!=null");
			if (stream != null) {
				try {
					//System.out.println("in handleBlobUpdate, stream != null");
					//java.io.BufferedInputStream bin = new java.io.BufferedInputStream( stream );
					//statement.setBinaryStream(index, bin, bin.available() );
					//System.out.println("bin.available(): "+bin.available());
					//System.out.println("stream.available(): "+stream.available());
					//statement.setBinaryStream(index, stream, stream.available() );
					setBlobstreamForStatement(statement, stream, index);
				} catch (Exception e) {
					//System.err.println("Error updating BLOB field in "+entity.getClass().getName());
					e.printStackTrace(System.err);
				}
			}
		}
	}
	public void setBlobstreamForStatement(PreparedStatement statement, InputStream stream, int index) throws SQLException, IOException {
		statement.setBinaryStream(index, stream, stream.available());
	}
	
	public void update(GenericEntity entity) throws Exception {
		if (entity.columnsHaveChanged()) {
			Connection conn = null;
			try {
				conn = entity.getConnection();
				update(entity, conn);
			} finally {
				if (conn != null) {
					entity.freeConnection(conn);
				}
			}
		}
	}
	
	public void update(GenericEntity entity, Connection conn) throws Exception {
		executeBeforeUpdate(entity);
		PreparedStatement Stmt = null;
		try {
			StringBuffer statement = new StringBuffer("");
			statement.append("update ");
			statement.append(entity.getTableName());
			statement.append(" set ");
			statement.append(getAllColumnsAndQuestionMarks(entity));
			appendPrimaryKeyWhereClause(entity, statement);
			String sql = statement.toString();
			logSQL(sql);
			Stmt = conn.prepareStatement(sql);
			setForPreparedStatement(STATEMENT_UPDATE, Stmt, entity);
			Stmt.execute();
		} finally {
			if (Stmt != null) {
				try {
					Stmt.close();
				} catch (SQLException sqle) {
				}
			}
		}
		executeAfterUpdate(entity);
		entity.setEntityState(IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE);
	}

	public void insert(GenericEntity entity, Connection conn) throws Exception {
		executeBeforeInsert(entity);
		PreparedStatement Stmt = null;
		ResultSet RS = null;
		try {
			StringBuffer statement = new StringBuffer("");
			statement.append("insert into ");
			statement.append(entity.getTableName());
			statement.append("(");
			statement.append(getCommaDelimitedColumnNamesForInsert(entity));
			statement.append(") values (");
			statement.append(getQuestionmarksForColumns(entity));
			statement.append(")");
			String sql = statement.toString();
			logSQL(sql);
			Stmt = conn.prepareStatement(sql);
			setForPreparedStatement(STATEMENT_INSERT, Stmt, entity);
			Stmt.execute();
			Stmt.close();
			if (updateNumberGeneratedValueAfterInsert()) {
				updateNumberGeneratedValue(entity, conn);
			}
		} finally {
			if (RS != null) {
				RS.close();
			}
			if (Stmt != null) {
				try {
					Stmt.close();
				} catch (SQLException e) {
				}
			}
		}
		executeAfterInsert(entity);
		entity.setEntityState(IDOLegacyEntity.STATE_IN_SYNCH_WITH_DATASTORE);
	}


	public void delete(GenericEntity entity) throws Exception {
			Connection conn = null;
			try {
				conn = entity.getConnection();
				delete(entity, conn);
			} finally {
				if (conn != null) {
					entity.freeConnection(conn);
				}
			}
		
	}

	public void delete(GenericEntity entity, Connection conn) throws Exception {
		//executeBeforeInsert(entity);
		Statement Stmt = null;
		try {
			Stmt = conn.createStatement();
			StringBuffer statement = new StringBuffer("");
			statement.append("delete from  ");
			statement.append(entity.getTableName());
			appendPrimaryKeyWhereClause(entity, statement);
			String sql = statement.toString();
			logSQL(sql);
			Stmt.executeUpdate(sql);
			if (entity.hasMetaDataRelationship()) {
				deleteMetaData(entity, conn);
			}
		} finally {
			if (Stmt != null) {
				try {
					Stmt.close();
				} catch (SQLException sqle) {
				}
			}
		}
		//executeAfterInsert(entity);
		entity.setEntityState(IDOLegacyEntity.STATE_DELETED);
	}
	public void deleteMetaData(GenericEntity entity, Connection conn) throws Exception {
		Statement Stmt = null;
		Statement stmt2 = null;
		try {
			MetaData metadata = (MetaData)com.idega.data.GenericEntity.getStaticInstance(MetaData.class);
			Stmt = conn.createStatement();
			String middletable = entity.getNameOfMiddleTable(metadata, entity);
			String metadataIdColumn = metadata.getIDColumnName();
			String metadataname = metadata.getTableName();
			//get all the id's of the metadata
			StringBuffer statement = new StringBuffer("");
			statement.append("select ");
			statement.append(middletable);
			statement.append('.');
			statement.append(metadataIdColumn);
			statement.append(" from ");
			statement.append(middletable);
			statement.append(',');
			statement.append(metadataname);
			statement.append(" where ");
			statement.append(middletable);
			statement.append('.');
			statement.append(entity.getIDColumnName());
			statement.append('=');
			statement.append(entity.getID());
			statement.append(" and ");
			statement.append(middletable);
			statement.append('.');
			statement.append(metadataIdColumn);
			statement.append('=');
			statement.append(metadataname);
			statement.append('.');
			statement.append(metadataIdColumn);
			ResultSet RS = Stmt.executeQuery(statement.toString());
			stmt2 = conn.createStatement();
			StringBuffer statement2;
			//delete thos id's
			while (RS.next()) {
				statement2 = new StringBuffer("");
				statement2.append("delete from ");
				statement2.append(metadataname);
				statement2.append(" where ");
				statement2.append(metadataIdColumn);
				statement2.append('=');
				statement2.append(RS.getString(1));
				stmt2.executeUpdate(statement2.toString());
			}
			if (RS != null)
				RS.close();
			//delete from the middle table
			Stmt = conn.createStatement();
			statement = new StringBuffer("");
			statement.append("delete from ");
			statement.append(middletable);
			statement.append(" where ");
			statement.append(entity.getIDColumnName());
			statement.append('=');
			statement.append(entity.getID());
			String sql = statement.toString();
			logSQL(sql);
			Stmt.executeUpdate(sql);
		} finally {
			if (Stmt != null) {
				Stmt.close();
			}
			if (stmt2 != null) {
				stmt2.close();
			}
		}
	}
	public void deleteMetaData(GenericEntity entity) throws Exception {
		Connection conn = null;
		try {
			conn = entity.getConnection();
			deleteMetaData(entity, conn);
		} finally {
			if (conn != null) {
				entity.freeConnection(conn);
			}
		}
	}
	public boolean supportsBlobInUpdate() {
		return true;
	}
	/**
	
	*Used to generate the ?,? mark list for preparedstatement
	
	**/
	protected String getQuestionmarksForColumns(GenericEntity entity) {
		String returnString = "";
		String[] names = entity.getColumnNames();
		for (int i = 0; i < names.length; i++) {
			if (isValidColumnForInsertList(entity, names[i])) {
				//if (!isNull(names[i])){
				if (returnString.equals("")) {
					returnString = "?";
				} else {
					returnString = returnString + ",?";
				}
			}
		}
		return returnString;
	}
	boolean isValidColumnForUpdateList(GenericEntity entity, String columnName) {
		boolean isIDColumn = entity.isPrimaryKeyColumn(columnName);
		if (isIDColumn) {
			return false;
		} else {
			if (this.supportsBlobInUpdate()) {
				if (entity.isNull(columnName)) {
					return false;
				} else {
					if (entity.getStorageClassType(columnName) == EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER) {
						BlobWrapper wrapper = (BlobWrapper)entity.getColumnValue(columnName);
						if (wrapper == null) {
							return false;
						} else {
							return wrapper.isReadyForUpdate();
						}
					}
					return true;
				}
			} else {
				if (entity.isNull(columnName)) {
					return false;
				} else {
					if (entity.getStorageClassType(columnName) == EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER) {
						return false;
					}
					return true;
				}
			}
		}
	}
	protected static boolean isValidColumnForInsertList(GenericEntity entity, String columnName) {
		if (entity.isNull(columnName)) {
			return false;
		} else {
			if (entity.getStorageClassType(columnName) == EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER) {
				return false;
			}
			return true;
		}
	}
	protected static boolean isValidColumnForSelectList(GenericEntity entity, String columnName) {
		return !(entity.getStorageClassType(columnName) == EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER);
	}
	/**
	 * Meant to be overrided in subclasses
	 * @param entity
	 * @param columnName
	 * @return the columnName if there is nothing specific about the select
	 */
	protected String getColumnStringForSelectList(GenericEntity entity, String columnName) {
		return columnName;
	}
	/**
	 * Constructs the SQL for the select of an entity i.e. select name,id from employee
	 * @param entity
	 * @return the SQL query string
	 */
	protected String getCommaDelimitedColumnNamesForSelect(GenericEntity entity) {
		String newCachedColumnNameList = null;
		//String newCachedColumnNameList = entity.getCachedColumnNamesList();
		if (newCachedColumnNameList == null) {
			StringBuffer returnString = null;
			String[] names = entity.getColumnNames();
			for (int i = 0; i < names.length; i++) {
				if (isValidColumnForSelectList(entity, names[i])) {
					if (returnString == null) {
						returnString = new StringBuffer("");
						returnString.append(getColumnStringForSelectList(entity, names[i]));
					} else {
						returnString.append(",");
						returnString.append(getColumnStringForSelectList(entity, names[i]));
					}
				}
			}
			newCachedColumnNameList = returnString.toString();
		}
		return newCachedColumnNameList;
	}

	/**
	 * Returns a string with all the columns in the enti, which are valid for
	 * insert (e.g. not null) , comma separated
	 * @param entity
	 * @return String
	 */
	protected static String getCommaDelimitedColumnNamesForInsert(GenericEntity entity) {
		String newCachedColumnNameList = null;
		//String newCachedColumnNameList = entity.getCachedColumnNamesList();
		if (newCachedColumnNameList == null) {
			StringBuffer returnString = null;
			String[] names = entity.getColumnNames();
			for (int i = 0; i < names.length; i++) {
				if (isValidColumnForInsertList(entity, names[i])) {
					if (returnString == null) {
						returnString = new StringBuffer("");
						returnString.append(names[i]);
					} else {
						returnString.append(",");
						returnString.append(names[i]);
					}
				}
			}
      /*TODO thi: Bad implementation:
       * If there aren't any valid columns the returnString is null,
       * that is a null pointer exception is thrown (see: returnString.toString()).
       * That happens if the method doInsertInCreate() of an entity returns true and
       * if the primary key is not set by the executeBeforeInsert(GenericEntity) method.
       * (e.g. MySQLServer).
       * In this case the entity is inserted without any values when the method create() is invoked.
       * The problem is: what should be returned if the returnString is null?
       * An empty string or a null value causes a wrong SQLStatement.
       */
      newCachedColumnNameList = returnString.toString();
		}
		return newCachedColumnNameList;
	}
	protected static String getCommaDelimitedColumnValues(GenericEntity entity) {
		StringBuffer returnString = null;
		String[] names = entity.getColumnNames();
		for (int i = 0; i < names.length; i++) {
			if (isValidColumnForInsertList(entity, names[i])) {
				if (returnString == null) {
					returnString = new StringBuffer("");
					returnString.append("'");
					returnString.append(entity.getStringColumnValue(names[i]));
					returnString.append("'");
				} else {
					returnString.append(",'");
					returnString.append(entity.getStringColumnValue(names[i]));
					returnString.append("'");
				}
			}
		}
		return returnString.toString();
	}
	protected String getAllColumnsAndQuestionMarks(GenericEntity entity) {
		StringBuffer returnString = null;
		String[] names = entity.getColumnNames();
		String questionmark = "=?";
		for (int i = 0; i < names.length; i++) {
			//for (Enumeration e = columns.keys(); e.hasMoreElements();){
			//for (Enumeration e = columns.elements(); e.hasMoreElements();){
			//String ColumnName = (String)e.nextElement();
			String ColumnName = names[i];
			if (isValidColumnForUpdateList(entity, ColumnName)) {
				if (returnString == null) {
					returnString = new StringBuffer("");
					returnString.append(ColumnName);
					returnString.append(questionmark);
				} else {
					returnString.append(',');
					returnString.append(ColumnName);
					returnString.append(questionmark);
				}
			} else if (entity.hasBeenSetNull(ColumnName)) {
				if (returnString == null) {
					returnString = new StringBuffer("");
				} else {
					returnString.append(',');
				}
				returnString.append(ColumnName);
				returnString.append(" = null");
			}
		}
		return returnString.toString();
	}
	protected void createForeignKey(GenericEntity entity, String baseTableName, String columnName, String refrencingTableName, String referencingColumnName) throws Exception {
		String SQLCommand = "ALTER TABLE " + baseTableName + " ADD FOREIGN KEY (" + columnName + ") REFERENCES " + refrencingTableName + "(" + referencingColumnName + ")";
		executeUpdate(entity, SQLCommand);
	}
	protected String getCreatePrimaryKeyStatementBeginning(String tableName) {
		return "alter table " + tableName + " add primary key (";
	}
	public void setNumberGeneratorValue(GenericEntity entity, int value) {
		throw new RuntimeException("setNumberGeneratorValue() not implemented for " + this.getClass().getName());
	}

	public void setDatabaseMetaData(DatabaseMetaData meta) {
		_databaseMetaData = meta;
	}
	public DatabaseMetaData getDatabaseMetaData() {
		return _databaseMetaData;
	}
	protected static DatastoreInterface getDatastoreInterfaceByDatasource(String datasource) {
		return (DatastoreInterface)getInterfacesByDatasourcesMap().get(datasource);
	}
	protected static void setDatastoreInterfaceByDatasource(String datasource, DatastoreInterface dsi) {
		getInterfacesByDatasourcesMap().put(datasource, dsi);
	}
	private static Map getInterfacesByDatasourcesMap() {
		if (interfacesByDatasourcesMap == null) {
			interfacesByDatasourcesMap = new HashMap();
		}
		return interfacesByDatasourcesMap;
	}
	protected void setStringForPreparedStatement(String columnName, PreparedStatement statement, int index, GenericEntity entity) throws SQLException {
		statement.setString(index, entity.getStringColumnValue(columnName));
	}
	protected void fillStringColumn(GenericEntity entity, String columnName, ResultSet rs) throws SQLException {
		String value = rs.getString(columnName);
		if (value != null) {
			entity.setColumn(columnName, value);
		}
	}
	protected void fillColumn(GenericEntity entity, String columnName, ResultSet RS) throws SQLException {
		int classType = entity.getStorageClassType(columnName);
		if (classType == EntityAttribute.TYPE_JAVA_LANG_INTEGER) {
			//if (RS.getInt(columnName) != -1){
			int theInt = RS.getInt(columnName);
			boolean wasNull = RS.wasNull();
			if (!wasNull) {
				entity.setColumn(columnName, new Integer(theInt));
				//setColumn(columnName.toLowerCase(),new Integer(theInt));
			}
			//}
		} else if (classType == EntityAttribute.TYPE_JAVA_LANG_STRING) {
			/*if (RS.getString(columnName) != null){
				entity.setColumn(columnName,RS.getString(columnName));
			}*/
			fillStringColumn(entity, columnName, RS);
		} else if (classType == EntityAttribute.TYPE_JAVA_LANG_BOOLEAN) {
			String theString = RS.getString(columnName);
			if (theString != null) {
				if (theString.equals("Y")) {
					entity.setColumn(columnName, new Boolean(true));
				} else if (theString.equals("N")) {
					entity.setColumn(columnName, new Boolean(false));
				}
			}
		} else if (classType == EntityAttribute.TYPE_JAVA_LANG_FLOAT) {
			float theFloat = RS.getFloat(columnName);
			boolean wasNull = RS.wasNull();
			if (!wasNull) {
				entity.setColumn(columnName, new Float(theFloat));
				//setColumn(columnName.toLowerCase(),new Float(theFloat));
			}
		} else if (classType == EntityAttribute.TYPE_JAVA_LANG_DOUBLE) {
			double theDouble = RS.getDouble(columnName);
			boolean wasNull = RS.wasNull();
			if (!wasNull) {
				entity.setColumn(columnName, new Double(theDouble));
				//setColumn(columnName.toLowerCase(),new Double(theDouble));
			}
		} else if (classType == EntityAttribute.TYPE_JAVA_SQL_TIMESTAMP) {
			Timestamp ts = RS.getTimestamp(columnName);
			if (ts != null) {
				entity.setColumn(columnName, ts);
			}
		} else if (classType == EntityAttribute.TYPE_JAVA_SQL_DATE) {
			Date date = RS.getDate(columnName);
			if (date!= null) {
				entity.setColumn(columnName, date);
			}
		} else if (classType == EntityAttribute.TYPE_JAVA_SQL_TIME) {
			java.sql.Date date = RS.getDate(columnName);
			if (date != null) {
				entity.setColumn(columnName, date);
				//setColumn(columnName.toLowerCase(),date);
			}
		} else if (classType == EntityAttribute.TYPE_COM_IDEGA_DATA_BLOBWRAPPER) {
			/*if (RS.getDate(columnName) != null){
				setColumn(columnName.toLowerCase(),RS.getTime(columnName));
			}*/
			entity.setColumn(columnName, entity.getEmptyBlob(columnName));
			//setColumn(columnName.toLowerCase(),getEmptyBlob(columnName));
		} else if (classType == EntityAttribute.TYPE_COM_IDEGA_UTIL_GENDER) {
			String gender = RS.getString(columnName);
			if (gender != null) {
				entity.setColumn(columnName, new Gender(gender));
				//setColumn(columnName.toLowerCase(),new Gender(gender));
			}
		}
	}
	String getPrimaryKeyWhereClause(GenericEntity entity) {
		StringBuffer statement = new StringBuffer();
		appendPrimaryKeyWhereClause(entity, statement);
		return statement.toString();
	}
	void appendPrimaryKeyWhereClause(GenericEntity entity, StringBuffer bufferToAppendTo) {
		//try {
		IDOEntityField[] fields = entity.getGenericEntityDefinition().getPrimaryKeyDefinition().getFields();
		Object primaryKey = entity.getPrimaryKey();
		Object value;
		
		bufferToAppendTo.append(" where ");
		for (int i = 0; i < fields.length; i++) {
			if (primaryKey instanceof IDOPrimaryKey) {
				value = ((IDOPrimaryKey) primaryKey).getPrimaryKeyValue(fields[i].getSQLFieldName());
			}
			else {
				value = entity.getValue(fields[i].getSQLFieldName());
			}
			bufferToAppendTo.append(fields[i].getSQLFieldName());
			bufferToAppendTo.append("=");
			if(fields[0].getDataTypeClass() == Integer.class){
				bufferToAppendTo.append(value);
			}
			else {
				bufferToAppendTo.append("'");
				bufferToAppendTo.append(value);
				bufferToAppendTo.append("'");
			}
			if ((i + 1) < fields.length)
				bufferToAppendTo.append(" and ");
		}
		//System.out.println(bufferToAppendTo.toString());

		//} catch (java.rmi.RemoteException rme) {
		//	throw new RuntimeException(rme.getMessage());
		//}
	}

	/**
	 * Override in subclasses
	 **/
	public void onConnectionCreate(Connection newConn) {
		/*try{
			Statement stmt = newConn.createStatement();
			stmt.execute("")
		}
		catch(SQLException sqle){		
		}*/
	}


	/**
	 * Queries given datasource for table existance
	 * @param dataSourceName
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	public boolean doesTableExist(String dataSourceName, String tableName) throws Exception {
		
	
	// old impl
	/*
	 String checkQuery = "select count(*) from " + tableName;
	try {
		executeQuery(dataSourceName, checkQuery);	
		return true;
	} catch (Exception e) {
		//e.printStackTrace();
		return false;
	}
	*/

		//A connection friendler version and faster
		String[] tablesTypes = {"TABLE",  "VIEW"};
		Connection conn = null;
		boolean tableExists = false;
		try{
			
			conn = ConnectionBroker.getConnection(dataSourceName);
			DatabaseMetaData dbMetaData = conn.getMetaData();
			ResultSet rs = null;
			
			//Check for upper case
			rs = dbMetaData.getTables(null,null,tableName.toUpperCase(),tablesTypes);
			if(rs.next()){
				//table exists
				tableExists = true;
			}
			rs.close();
			
			//Check for lower case
			if(!tableExists){
				rs = dbMetaData.getTables(null,null,tableName.toLowerCase(),tablesTypes);
				if(rs.next()){
					//table exists
					tableExists = true;
				}
				rs.close();
			}
			
			//Check without any case manipulating, this can be removed if we always force uppercase		
			if(!tableExists){

				rs = dbMetaData.getTables(null,null,tableName,tablesTypes);
				if(rs.next()){
					//table exists
					tableExists = true;
				}
				rs.close();
			}
			

		
			
		}
		catch(SQLException e){
			  e.printStackTrace();
		}
		finally{
		  if(conn!=null){
			ConnectionBroker.freeConnection(conn);
		  }
		}
		
		return tableExists;
	}
	
	private String[] getColumnArrayFromMetaData(String dataSourceName,String tableName){
		Connection conn = null;
		ResultSet rs = null;
		Vector v = new Vector();
		try{
		  conn = ConnectionBroker.getConnection(dataSourceName);
		  //conn = entity.getConnection();
		  
		  //String tableName = entity.getTableName();
		  DatabaseMetaData metadata = conn.getMetaData();
		  
//		Check for upper case
		  rs = metadata.getColumns(null,null,tableName.toUpperCase(),"%");
		  //System.out.println("Table: "+tableName+" has the following columns:");
		  while (rs.next()) {
			String column = rs.getString("COLUMN_NAME");
			v.add(column);
			//System.out.println("\t\t"+column);
		  }
		  rs.close();
		  
//		Check for lower case
		  if(v.isEmpty()){
			rs = metadata.getColumns(null,null,tableName.toLowerCase(),"%");
			//System.out.println("Table: "+tableName+" has the following columns:");
			while (rs.next()) {
			  String column = rs.getString("COLUMN_NAME");
			  v.add(column);
			  //System.out.println("\t\t"+column);
			}
			rs.close();
		  }
		  
//		Check without any case manipulating, this can be removed if we always force uppercase		
		  if(v.isEmpty()){
			rs = metadata.getColumns(null,null,tableName,"%");
			//System.out.println("Table: "+tableName+" has the following columns:");
			while (rs.next()) {
			  String column = rs.getString("COLUMN_NAME");
			  v.add(column);
			  //System.out.println("\t\t"+column);
			}
			rs.close();
		  }
		  
		}
		catch(SQLException e){
		  e.printStackTrace();
		}
		finally{
		  if(conn!=null){
			ConnectionBroker.freeConnection(conn);
		  }
		}
		if(v!=null && !v.isEmpty())
			return (String[])v.toArray(new String[0]);
		return null;
	  }
	  /**
	   * Queries the given data source for table columns
	   * using database metadata by default
	   * @param dataSourceName
	   * @param tableName
	   * @return
	   */
	  public String[] getTableColumnNames(String dataSourceName,String tableName){
	  	return getColumnArrayFromMetaData(dataSourceName,tableName);
	  }
	  
	  
	  //STANDARD LOGGING METHODS:
	  
	  /**
	   * Logs out to the default log level (which is by default INFO)
	   * @param msg The message to log out
	   */
	  protected void log(String msg) {
	  	//System.out.println(string);
	  	getLogger().log(getDefaultLogLevel(),msg);
	  }

	  /**
	   * Logs out to the error log level (which is by default WARNING) to the default Logger
	   * @param e The Exception to log out
	   */
	  protected void log(Exception e) {
	  	LoggingHelper.logException(e,this,getLogger(),getErrorLogLevel());
	  }
	  
	  /**
	   * Logs out to the specified log level to the default Logger
	   * @param level The log level
	   * @param msg The message to log out
	   */
	  protected void log(Level level,String msg) {
	  	//System.out.println(msg);
	  	getLogger().log(level,msg);
	  }
	  
	  /**
	   * Logs out to the error log level (which is by default WARNING) to the default Logger
	   * @param msg The message to log out
	   */
	  protected void logError(String msg) {
	  	//System.err.println(msg);
	  	getLogger().log(getErrorLogLevel(),msg);
	  }

	  /**
	   * Logs out to the debug log level (which is by default FINER) to the default Logger
	   * @param msg The message to log out
	   */
	  protected void logDebug(String msg) {
	  	//System.err.println(msg);
	  	getLogger().log(getDebugLogLevel(),msg);
	  }
	  
	  /**
	   * Logs out to the SEVERE log level to the default Logger
	   * @param msg The message to log out
	   */
	  protected void logSevere(String msg) {
	  	//System.err.println(msg);
	  	getLogger().log(Level.SEVERE,msg);
	  }	
	  
	  
	  /**
	   * Logs out to the WARNING log level to the default Logger
	   * @param msg The message to log out
	   */
	  protected void logWarning(String msg) {
	  	//System.err.println(msg);
	  	getLogger().log(Level.WARNING,msg);
	  }
	  
	  /**
	   * Logs out to the CONFIG log level to the default Logger
	   * @param msg The message to log out
	   */
	  protected void logConfig(String msg) {
	  	//System.err.println(msg);
	  	getLogger().log(Level.CONFIG,msg);
	  }	
	  
	  /**
	   * Logs out to the debug log level to the default Logger
	   * @param msg The message to log out
	   */
	  protected void debug(String msg) {
	  	logDebug(msg);
	  }	
	  
	  /**
	   * Gets the default Logger. By default it uses the package and the class name to get the logger.<br>
	   * This behaviour can be overridden in subclasses.
	   * @return the default Logger
	   */
	  protected Logger getLogger(){
	  	return Logger.getLogger(this.getClass().getName());
	  }
	  
	  /**
	   * Gets the log level which messages are sent to when no log level is given.
	   * @return the Level
	   */
	  protected Level getDefaultLogLevel(){
	  	return Level.INFO;
	  }
	  /**
	   * Gets the log level which debug messages are sent to.
	   * @return the Level
	   */
	  protected Level getDebugLogLevel(){
	  	return Level.FINER;
	  }
	  /**
	   * Gets the log level which error messages are sent to.
	   * @return the Level
	   */
	  protected Level getErrorLogLevel(){
	  	return Level.WARNING;
	  }
	  
	  //ENTITY SPECIFIC LOG MEHTODS:
	  
	  ///**
	  // * This method outputs the outputString to System.out if the Application property
	  // * "debug" is set to "TRUE"
	  // */
	  //public void debug(String outputString) {
	  //	if (isDebugActive()) {
	  //		//System.out.println("[DEBUG] \"" + outputString + "\" : " + this.getEntityName());
	  //	}
	  //}
	  /**
	   * This method logs the sqlCommand if the Log Level is low enough 
	   */
	  public void logSQL(String sqlCommand) {
	  	log(Level.FINEST,sqlCommand);
	  	//if (isDebugActive()) {
	  	//System.out.println("[DEBUG] \"" + outputString + "\" : " + this.getEntityName());
	  	//}
	  }
	  
	  protected boolean isDebugActive() {
	  	return IWMainApplicationSettings.isDebugActive();
	  }
	  //END STANDARD LOGGING METHODS
	  
	  /**
	   * This method outputs the outputString to System.out if the Application property
	   * "debug" is set to "TRUE"
	   */
	  protected void debug(String outputString, GenericEntity entity) {
	  	/*if (IWMainApplicationSettings.isDebugActive()) {
	  		System.out.println("[DEBUG] \"" + outputString + "\" : " + entity.getEntityName());
	  	}*/
	  	String finalString = outputString + "\" : " + entity.getEntityName();
	  	debug(finalString);
	  }
	  /**
	   * This method outputs the outputString to System.out if the Application property
	   * "debug" is set to "TRUE"
	   */
	  /*protected static void debug(String outputString) {
	  	if (IWMainApplicationSettings.isDebugActive()) {
	  		System.out.println("[DEBUG] \"" + outputString + "\" : DatastoreInterface");
	  	}
	  }*/
	  
	  /**
	   * Formats the date to a string for use as is in a SQL query
	   * quotes and casting included
	    * @param date
	   * @return
	   */
	  public String format(java.sql.Date date){
	  	IWTimestamp stamp = new IWTimestamp(date);
	  	return " '"+(stamp.toSQLString())+"' ";
	  }
	  /**
	   * Formats the date to a string for use as is in a SQL query
	   * quotes and casting included
	   * @param timestamp
	   * @return
	   */
	  public String format(java.sql.Timestamp timestamp){
	  	IWTimestamp stamp = new IWTimestamp(timestamp);
	  	return " '"+(stamp.toSQLString())+"' ";
	  }
	  
}
