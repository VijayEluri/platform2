//package com.wrox.connectionpool;
package com.idega.util.database;

import java.sql.*;
import java.io.*;
import java.util.*;
import com.idega.util.LogWriter;



/**
 *
*@author <a href="http://www.wrox.com">wrox</a> modified by <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
*@version 1.3
* Class to deliver database connections through a connectionpool
*/
public class PoolManager
{

   private static String thePropertiesFileLocation;
   private static String DEFAULT_DSN="default";
   static private PoolManager instance;
   static private int clients;

   private LogWriter logWriter;
   private PrintWriter pw;

   private Vector drivers = new Vector();
   private Hashtable pools = new Hashtable();

   private PoolManager()
   {
      init(System.getProperty("file.separator")+"db.properties");
   }

   private PoolManager(String propertiesFileLocation){
   	init(propertiesFileLocation);
   }


   static synchronized public PoolManager getInstance()
   {
      if (instance == null)
      {
         instance = new PoolManager();
      }
      clients++;
      return instance;
   }

   static synchronized public PoolManager getInstance(String propertiesFileLocation)
   {
      if (instance == null)
      {
         thePropertiesFileLocation=propertiesFileLocation;
         instance = new PoolManager(propertiesFileLocation);
      }
      clients++;
      return instance;
   }

   private void init(String propertiesFile)
   {
      // Log to System.err until we have read the logfile property
      pw = new PrintWriter(System.err, true);
      logWriter = new LogWriter("PoolManager", LogWriter.INFO, pw);
      //InputStream is = getClass().getResourceAsStream(propertiesFile);
      InputStream is;

      Properties dbProps = new Properties();
      try
      {
         is = (InputStream) new FileInputStream(propertiesFile);
         dbProps.load(is);
      }
      catch (Exception e)
      {

         logWriter.log("Can't read the properties file from the specified location",
            LogWriter.ERROR);
         return;
      }
      String logFile = dbProps.getProperty("logfile");
      if (logFile != null)
      {
         try
         {
            pw = new PrintWriter(new FileWriter(logFile, true), true);
            logWriter.setPrintWriter(pw);
         }
         catch (IOException e)
         {
            logWriter.log("Can't open the log file: " + logFile +
               ". Using System.err instead", LogWriter.ERROR);
         }
      }
      loadDrivers(dbProps);
      createPools(dbProps);
   }

   private void loadDrivers(Properties props)
   {
      String driverClasses = props.getProperty("drivers");
      StringTokenizer st = new StringTokenizer(driverClasses);
      while (st.hasMoreElements())
      {
         String driverClassName = st.nextToken().trim();
         try
         {
            Driver driver = (Driver)
               Class.forName(driverClassName).newInstance();
            DriverManager.registerDriver(driver);
            drivers.addElement(driver);
            logWriter.log("Registered JDBC driver " + driverClassName,
               LogWriter.INFO);
         }
         catch (Exception e)
         {
            logWriter.log(e, "Can't register JDBC driver: " +
               driverClassName, LogWriter.ERROR);
         }
      }
   }

   private void createPools(Properties props)
   {
      Enumeration propNames = props.propertyNames();
      while (propNames.hasMoreElements())
      {
         String name = (String) propNames.nextElement();
         if (name.endsWith(".url"))
         {
            String poolName = name.substring(0, name.lastIndexOf("."));
            String url = props.getProperty(poolName + ".url");
            if (url == null)
            {
               logWriter.log("No URL specified for " + poolName,
                  LogWriter.ERROR);
               continue;
            }

            String user = props.getProperty(poolName + ".user");
            String password = props.getProperty(poolName + ".password");

            String maxConns = props.getProperty(poolName +
               ".maxconns", "0");
            int max;
            try
            {
               max = Integer.valueOf(maxConns).intValue();
            }
            catch (NumberFormatException e)
            {
               logWriter.log("Invalid maxconns value " + maxConns +
                               " for " + poolName, LogWriter.ERROR);
               max = 0;
            }

            String initConns = props.getProperty(poolName +
                              ".initconns", "0");
            int init;
            try
            {
               init = Integer.valueOf(initConns).intValue();
            }
            catch (NumberFormatException e)
            {
               logWriter.log("Invalid initconns value " + initConns +
                             " for " + poolName, LogWriter.ERROR);
               init = 0;
            }

            String loginTimeOut = props.getProperty(poolName +
               ".logintimeout", "5");
            int timeOut;
            try
            {
               timeOut = Integer.valueOf(loginTimeOut).intValue();
            }
            catch (NumberFormatException e)
            {
               logWriter.log("Invalid logintimeout value " + loginTimeOut +
                            " for " + poolName, LogWriter.ERROR);
               timeOut = 5;
            }

            String logLevelProp = props.getProperty(poolName +
                           ".loglevel", String.valueOf(LogWriter.ERROR));
            int logLevel = LogWriter.INFO;
            if (logLevelProp.equalsIgnoreCase("none"))
            {
               logLevel = LogWriter.NONE;
            }
            else if (logLevelProp.equalsIgnoreCase("error"))
            {
               logLevel = LogWriter.ERROR;
            }
            else if (logLevelProp.equalsIgnoreCase("debug"))
            {
               logLevel = LogWriter.DEBUG;
            }

            ConnectionPool pool =
                           new ConnectionPool(poolName, url, user, password,
                                            max, init, timeOut, pw, logLevel);
            pools.put(poolName, pool);
         }
      }
   }
   public Connection getConnection(){
		return getConnection(DEFAULT_DSN);
   }


   public Connection getConnection(String dataSourceName)
   {
        Connection conn = null;
        ConnectionPool pool = (ConnectionPool) pools.get(dataSourceName);
        if (pool != null)
        {
           try
           {
              conn = pool.getConnection();
           }
           catch (SQLException e)
           {
              logWriter.log(e, "Exception getting connection from " +
                 dataSourceName, LogWriter.ERROR);
           }
        }
        return conn;

   }

   public void freeConnection(String datasourceName, Connection con)
   {
      ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
      if (pool != null)
      {
         pool.freeConnection(con);
      }
   }
   public void freeConnection(Connection connection){
		freeConnection(DEFAULT_DSN,connection);
   }

   public synchronized void release()
   {
      // Wait until called by the last client
      //if (--clients != 0)
      //{
      //   return;
      //}

      Enumeration allPools = pools.elements();
      while (allPools.hasMoreElements())
      {
         ConnectionPool pool = (ConnectionPool) allPools.nextElement();
         pool.release();
      }

      Enumeration allDrivers = drivers.elements();
      while (allDrivers.hasMoreElements())
      {
         Driver driver = (Driver) allDrivers.nextElement();
         try
         {
            DriverManager.deregisterDriver(driver);
            logWriter.log("Deregistered JDBC driver " +
                          driver.getClass().getName(), LogWriter.INFO);
         }
         catch (SQLException e)
         {
            logWriter.log(e, "Couldn't deregister JDBC driver: " +
                             driver.getClass().getName(), LogWriter.ERROR);
         }
      }
   }

   //debug
   public String getStats(String name){

   		ConnectionPool tempPool = (ConnectionPool) pools.get(name);
   		return tempPool.getStats();

   }


   //Status of all pools:

   public String getStats(){
          String returnString = "";


   	  for (Enumeration e = pools.keys(); e.hasMoreElements();){

   	  	String name = (String) e.nextElement();
   	  	ConnectionPool tempPool = (ConnectionPool) pools.get(name);
   	  	returnString = returnString + "\nStatus of datasource "+name+ " is: "+tempPool.getStats()+" ";
   	  }


   	  return returnString;
    }



   public Hashtable getStatsHashtable(){

          Hashtable table = new Hashtable();

   	  for (Enumeration e = pools.keys(); e.hasMoreElements();){

   	  	String name = (String) e.nextElement();
   	  	ConnectionPool tempPool = (ConnectionPool) pools.get(name);
   	  	table.put(name,"Status of datasource "+name+ " is: "+tempPool.getStats()+" ");

   	  }

   	  return table;
    }


    public String[] getDatasources(){

        Vector sources = new Vector();

         for (Enumeration e = pools.keys(); e.hasMoreElements();){

   	  	String name = (String) e.nextElement();
                sources.add(name);
   	  }

        return (String[])sources.toArray(new String[0]);

    }


    public String getPasswordForPool(String datasourceName){
      String password = null;
      ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
      if (pool != null)
      {

            password = pool.getPassword();

      }
      return password;
    }

    public String getPasswordForPool(){
        return getPasswordForPool(DEFAULT_DSN);
    }

    public String getUserNameForPool(String datasourceName){
      String userName = null;
      ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
      if (pool != null)
      {

            userName = pool.getUserName();

      }
      return userName;
    }

    public String getUserNameForPool(){
        return getUserNameForPool(DEFAULT_DSN);
    }


    public String getURLForPool(String datasourceName){
      String URL = null;
      ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
      if (pool != null)
      {

            URL = pool.getURL();

      }
      return URL;
    }


    public String getURLForPool(){
      return getURLForPool(DEFAULT_DSN);
    }

    /**
     * This only works if there is one driver definition in the db.properties file
     * TODO: Fix this limitation

    public String getDriverClassForPool(String datasourceName){
      String driverClass = null;
      ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
      if (pool != null)
      {

            driverClass = drivers.elementAt(0);

      }
      return driverClass;
    }*/

    public String getDriverClassForPool(){
      String driverClass = null;
      //ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
      if (drivers != null)
      {

            driverClass = ((Driver)drivers.elementAt(0)).getClass().getName();

      }
      return driverClass;
      //return getDriverClassForPool(DEFAULT_DSN);
    }


    public Connection recycleConnection(Connection conn){
      return recycleConnection(conn,DEFAULT_DSN);
    }

     public Connection recycleConnection(Connection conn,String dataSourceName){
        ConnectionPool pool = (ConnectionPool) pools.get(dataSourceName);
        return pool.recycleConnection(conn);
     }

 /**
  * Trims the pool so that it has only size connections
  */
 public void trimTo(String datasourceName, int size){
           trimTo(datasourceName,size,size,size);
 }

 /**
  * Trims the pool so that it has only size,minSize,maxSize connections
  */
 public void trimTo(String datasourceName, int size,int minSize,int maxSize){
      ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
      if (pool != null)
      {
           pool.trimTo(size,minSize,maxSize);
      }
 }

 /**
  * Enlarges the pool so it has size number of connections
  */
 public void enlargeTo(String datasourceName, int size){
           this.enlargeTo(datasourceName,size,size,size);
 }

 /**
  * Enlarges the pool so it has size number of connections
  */
 public void enlargeTo(String datasourceName, int size,int minSize,int maxSize){
      ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
      if (pool != null)
      {
           pool.enlargeTo(size,minSize,maxSize);
      }
 }

  public int getCurrentConnectionCount(String datasourceName){
      ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
      if (pool != null)
      {
           return pool.getCurrentConnectionCount();
      }
      return 0;
  }

 public int getMaximumConnectionCount(String datasourceName){
      ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
      if (pool != null)
      {
           return pool.getMaximumConnectionCount();
      }
      return 0;
  }

 public int getMinimumConnectionCount(String datasourceName){
      ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
      if (pool != null)
      {
           return pool.getMinimumConnectionCount();
      }
      return 0;
  }

 public int getTimeOut(String datasourceName){
      ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
      if (pool != null)
      {
           return pool.getTimeOut();
      }
      return 0;
  }

 public void setTimeOut(String datasourceName,int timeout){
      ConnectionPool pool = (ConnectionPool) pools.get(datasourceName);
      if (pool != null)
      {
          pool.setTimeOut(timeout);
      }

  }


}
