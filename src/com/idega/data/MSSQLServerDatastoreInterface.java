/*
 * Created on 28.2.2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code Template
 */
package com.idega.data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
/**
 * Title:        MSSQLServerDatastoreInterface
 * Description:  A class to handle Microsoft SQL Server specific jdbc implementations.
 * Copyright:  (C) 2003 idega software All Rights Reserved.
 * Company:      idega software
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0  
 */
public class MSSQLServerDatastoreInterface extends DatastoreInterface
{
	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#getSQLType(java.lang.String, int)
	 */
	public String getSQLType(String javaClassName, int maxlength)
	{
		String theReturn;
		if (javaClassName.equals("java.lang.Integer"))
		{
			theReturn = "INTEGER";
		}
		else if (javaClassName.equals("java.lang.String"))
		{
			if (maxlength < 0)
			{
				theReturn = "VARCHAR(255)";
			}
			else if (maxlength <= 8000)
			{
				theReturn = "VARCHAR(" + maxlength + ")";
			}
			else
			{
				theReturn = "CLOB";
			}
		}
		else if (javaClassName.equals("java.lang.Boolean"))
		{
			theReturn = "CHAR(1)";
		}
		else if (javaClassName.equals("java.lang.Float"))
		{
			theReturn = "FLOAT";
		}
		else if (javaClassName.equals("java.lang.Double"))
		{
			theReturn = "FLOAT";
		}
		else if (javaClassName.equals("java.sql.Timestamp"))
		{
			theReturn = "DATETIME";
		}
		else if (javaClassName.equals("java.sql.Date") || javaClassName.equals("java.util.Date"))
		{
			theReturn = "DATETIME";
		}
		else if (javaClassName.equals("java.sql.Blob"))
		{
			theReturn = "IMAGE";
		}
		else if (javaClassName.equals("java.sql.Time"))
		{
			theReturn = "DATETIME";
		}
		else if (javaClassName.equals("com.idega.util.Gender"))
		{
			theReturn = "VARCHAR(1)";
		}
		else if (javaClassName.equals("com.idega.data.BlobWrapper"))
		{
			theReturn = "IMAGE";
		}
		else
		{
			theReturn = "";
		}
		return theReturn;
	}
	/* (non-Javadoc)
	 * @see com.idega.data.DatastoreInterface#createTrigger(com.idega.data.IDOLegacyEntity)
	 */
	public void createTrigger(IDOLegacyEntity entity) throws Exception
	{
		// TODO Auto-generated method stub
	}
	/**
	 * @param entity
	 * @param conn
	 */
	protected void updateNumberGeneratedValue(IDOLegacyEntity entity, Connection conn)
	{
		try
		{
			if (((IDOEntityBean) entity).getPrimaryKeyClass().equals(Integer.class))
			{
				boolean pkIsNull = entity.isNull(entity.getIDColumnName()) && entity.getPrimaryKey() == null;
				if (pkIsNull)
				{
					//Object value = this.executeQuery(entity, "select @@IDENTITY");
					Statement stmt = conn.createStatement();
					ResultSet rs = stmt.executeQuery("select @@IDENTITY");
					rs.next();
					int id = rs.getInt(1);
					entity.setID(id);
					rs.close();
					stmt.close();
					//String tableName = entity.getTableName();
					//Statement stmt2 = conn.createStatement();
					//stmt2.executeUpdate("set IDENTITY_INSERT " + tableName + " off");
					//stmt2.close();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * @return boolean
	 */
	protected boolean updateNumberGeneratedValueAfterInsert()
	{
		// TODO Auto-generated method stub
		return true;
	}
	public String getIDColumnType()
	{
		return "INTEGER IDENTITY";
	}
	/*protected void executeBeforeInsert(IDOLegacyEntity entity) throws Exception
	{
		try
		{
			if (((IDOEntityBean) entity).getPrimaryKeyClass().equals(Integer.class))
			{
				boolean pkIsNull = entity.isNull(entity.getIDColumnName()) && entity.getPrimaryKey() == null;
				if (!pkIsNull)
				{
					String tableName = entity.getTableName();
					executeUpdate(entity, "set IDENTITY_INSERT " + tableName + " on");
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		super.executeBeforeInsert(entity);
	}
	
	protected void executeAfterInsert(IDOLegacyEntity entity) throws Exception
	{
		try
		{
			if (((IDOEntityBean) entity).getPrimaryKeyClass().equals(Integer.class))
			{
				String tableName = entity.getTableName();
				executeUpdate(entity, "set IDENTITY_INSERT " + tableName + " off");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		super.executeAfterInsert(entity);
	}*/
	/**
	 * 
	 * Hacked version of the insert method.
	 * @todo: Implement in a better way.
	 */
	public void insert(IDOLegacyEntity entity, Connection conn) throws Exception
	{
		executeBeforeInsert(entity);
		PreparedStatement Stmt = null;
		ResultSet RS = null;
		try
		{
			boolean entityInsertModeIsOn = false;
			entityInsertModeIsOn = turnOnIdentityInsertFlag(entity, conn, entityInsertModeIsOn);
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
			Stmt.close();
			if (updateNumberGeneratedValueAfterInsert())
			{
				updateNumberGeneratedValue(entity, conn);
			}
			turnOffIdentityInsertFlag(entity, conn, entityInsertModeIsOn);
		}
		finally
		{
			if (RS != null)
			{
				RS.close();
			}
			if (Stmt != null)
			{
				Stmt.close();
			}
		}
		executeAfterInsert(entity);
		entity.setEntityState(entity.STATE_IN_SYNCH_WITH_DATASTORE);
	}
	private boolean turnOnIdentityInsertFlag(IDOLegacyEntity entity, Connection conn, boolean entityInsertModeIsOn)
	{
		try
		{
			if (((IDOEntityBean) entity).getPrimaryKeyClass().equals(Integer.class))
			{
				boolean pkIsNull = entity.isNull(entity.getIDColumnName()) && entity.getPrimaryKey() == null;
				if (!pkIsNull)
				{
					String tableName = entity.getTableName();
					Statement stmt2 = conn.createStatement();
					String sql = "set IDENTITY_INSERT " + tableName + " on";
					stmt2.executeUpdate(sql);
					debug(sql);
					stmt2.close();
					return true;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return false;
	}
	private boolean turnOffIdentityInsertFlag(IDOLegacyEntity entity, Connection conn, boolean entityInsertModeIsOn)
	{
		if (entityInsertModeIsOn)
		{
			try
			{
				if (((IDOEntityBean) entity).getPrimaryKeyClass().equals(Integer.class))
				{
					String tableName = entity.getTableName();
					Statement stmt2 = conn.createStatement();
					String sql = "set IDENTITY_INSERT " + tableName + " off";
					stmt2.executeUpdate(sql);
					debug(sql);
					return false;
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return true;
			}
		}
		return false;
	}
}
