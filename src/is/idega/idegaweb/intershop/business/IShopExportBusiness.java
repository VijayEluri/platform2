/*
 *  $Id: IShopExportBusiness.java,v 1.8 2002/05/03 13:58:33 palli Exp $
 *
 *  Copyright (C) 2002 Idega hf. All Rights Reserved.
 *
 *  This software is the proprietary information of Idega hf.
 *  Use is subject to license terms.
 *
 */
package is.idega.idegaweb.intershop.business;

import com.idega.idegaweb.IWApplicationContext;
import com.strengur.idegaweb.intershop.business.IShopXMLDesc;
import is.idega.idegaweb.intershop.data.IShopTemplate;
import is.idega.idegaweb.intershop.data.IShopTemplateBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Properties;
import java.io.ByteArrayInputStream;

/**
 * @author <a href="mail:palli@idega.is">Pall Helgason</a>
 * @version 1.0
 */
public class IShopExportBusiness {
  private static IShopExportBusiness _instance = null;

  private IShopExportBusiness() {
  }

  public static IShopExportBusiness getInstance() {
    if (_instance == null)
      _instance = new IShopExportBusiness();

    return(_instance);
  }

  public boolean exportPage(IShopTemplate page, Properties dbProps, String html, IWApplicationContext iwac, String resetCacheURL) {
    try {
      IShopXMLDesc desc = new IShopXMLDesc(iwac);
      if (!desc.hasModifyPermissions(page.getIShopClass()))
        return(false);

      String driver = dbProps.getProperty("drivers");
      String url = dbProps.getProperty("default.url");
      String dbname = dbProps.getProperty("default.dbname");
      String user = dbProps.getProperty("default.user");
      String passwd = dbProps.getProperty("default.password");

      if (driver == null || url == null || dbname == null || user == null || passwd == null)
        return false;

      Class.forName(driver).newInstance();
      Connection conn = DriverManager.getConnection(url,user,passwd);

      StringBuffer sql = new StringBuffer("update ");
      sql.append(dbname);
      sql.append("..");
      sql.append(IShopTemplate.ISHOP_TABLE_TEMPLATES);
      sql.append(" set ");
      sql.append("content");
      sql.append(" = ");
      sql.append("?");
      sql.append(", ");
      sql.append("bytecode");
      sql.append(" = NULL where ");
      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopClassColumnName());
      sql.append(" = '");
      sql.append(page.getIShopClass());
      sql.append("' and ");
      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopIDColumnName());
      sql.append(" = '");
      sql.append(page.getIShopID());
      sql.append("' and ");
      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopLanguageNrColumnName());
      sql.append(" = ");
      sql.append(page.getIShopLanguageNr());

      PreparedStatement stmt = conn.prepareStatement(sql.toString());
      ByteArrayInputStream stream = new ByteArrayInputStream(html.getBytes());
      stmt.setAsciiStream(1,stream,stream.available());
      stmt.execute();

      stmt.close();
      conn.close();

      if (resetCacheURL != null)
        com.idega.util.FileUtil.getStringFromURL(resetCacheURL);
    }
    catch(Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  public boolean createPage(IShopTemplate page, Properties dbProps) {
    try {
      String driver = dbProps.getProperty("drivers");
      String url = dbProps.getProperty("default.url");
      String dbname = dbProps.getProperty("default.dbname");
      String user = dbProps.getProperty("default.user");
      String passwd = dbProps.getProperty("default.password");

      if (driver == null || url == null || dbname == null || user == null || passwd == null)
        return false;

      Class.forName(driver).newInstance();
      Connection conn = DriverManager.getConnection(url,user,passwd);

      StringBuffer sql = new StringBuffer("insert into ");
      sql.append(dbname);
      sql.append("..");
      sql.append(IShopTemplate.ISHOP_TABLE_TEMPLATES);
      sql.append(" (");
      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopClassColumnName());
      sql.append(", ");
      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopIDColumnName());
      sql.append(", ");
      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopLanguageNrColumnName());
      sql.append(") values('");
      sql.append(page.getIShopClass());
      sql.append("', '");
      sql.append(page.getIShopID());
      sql.append("', ");
      sql.append(page.getIShopLanguageNr());
      sql.append(")");

//      System.out.println("sql1 = " + sql.toString());
      Statement stmt = conn.createStatement();
      int res = stmt.executeUpdate(sql.toString());

      sql = new StringBuffer("insert into ");
      sql.append(dbname);
      sql.append("..");
      sql.append(IShopTemplate.ISHOP_TABLE_TEMPLATEDESC);
      sql.append(" (");
      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopClassColumnName());
      sql.append(", ");
      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopIDColumnName());
      sql.append(", ");
      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopNameColumnName());
      sql.append(", ");
      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopDescriptionColumnName());
      sql.append(") values('");
      sql.append(page.getIShopClass());
      sql.append("', '");
      sql.append(page.getIShopID());
      sql.append("', '");
      sql.append(page.getIShopName());
      sql.append("', '");
      sql.append(page.getIShopDescription());
      sql.append("')");

//      System.out.println("sql2 = " + sql.toString());
      stmt = conn.createStatement();
      int res2 = stmt.executeUpdate(sql.toString());

      stmt.close();
      conn.close();
    }
    catch(Exception e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }
}
