/*

 *  $Id: IShopBundleStarter.java,v 1.3 2002/04/06 19:11:21 tryggvil Exp $

 *

 *  Copyright (C) 2001 Idega hf. All Rights Reserved.

 *

 *  This software is the proprietary information of Idega hf.

 *  Use is subject to license terms.

 *

 */

package is.idega.idegaweb.intershop.business;



import com.idega.builder.app.IBApplication;

import com.idega.builder.data.IBPage;

import com.idega.builder.data.IBDomain;

import com.idega.builder.business.IBPageHelper;

import com.idega.data.EntityFinder;

import com.idega.data.IDOFinderException;

import com.idega.idegaweb.IWBundleStartable;

import com.idega.idegaweb.IWBundle;

import com.idega.presentation.IWContext;

import com.idega.util.FileUtil;

import is.idega.idegaweb.intershop.business.IShopTemplateHome;

import is.idega.idegaweb.intershop.presentation.IShopToolbarButton;

import java.io.FileInputStream;

import java.io.FileNotFoundException;

import java.io.IOException;

import java.util.List;

import java.util.Vector;

import java.util.Properties;

import java.util.Iterator;



import java.sql.Connection;

import java.sql.DriverManager;

import java.sql.ResultSet;

import java.sql.Statement;

import is.idega.idegaweb.intershop.data.IShopTemplate;

import is.idega.idegaweb.intershop.data.IShopTemplateBean;



/**

 * @author    <a href="mail:palli@idega.is">Pall Helgason</a>

 * @version   1.0

 */

public class IShopBundleStarter implements IWBundleStartable {

  /**

   * Constructor for the IShopBundleStarter object

   */

  public IShopBundleStarter() { }



  /**

   * Description of the Method

   *

   * @param starterBundle  Description of the Parameter

   */

  public void start(IWBundle starterBundle) {

    System.out.println("Starting Intershop initialization");

    IShopToolbarButton separator = new IShopToolbarButton(starterBundle, true);

    IShopToolbarButton button = new IShopToolbarButton(starterBundle, false);



    List l = (List)starterBundle.getApplication().getAttribute(IBApplication.TOOLBAR_ITEMS);

    if (l == null) {

      l = new Vector();

      starterBundle.getApplication().setAttribute(IBApplication.TOOLBAR_ITEMS, l);

    }



    l.add(separator);

    l.add(button);



    StringBuffer path = new StringBuffer(starterBundle.getPropertiesRealPath());

    if (!path.toString().endsWith(FileUtil.getFileSeparator()))

      path.append(FileUtil.getFileSeparator());



    path.append(starterBundle.getProperty("sybaseproperties","sybasedb.properties"));



    int count = IShopTemplateHome.getInstance().count();

    if (count == 0) {

      insertInitialData(path.toString());

    }

  }



  /**

   *

   */

  private void insertInitialData(String pathToPropertiesFile) {

    List l = null;

    try {

      l = EntityFinder.getInstance().findAll(IBDomain.class);

    }

    catch(IDOFinderException e) {

      e.printStackTrace();

      return;

    }



    IBDomain main = null;

    if (l.size() > 0)

      main = (IBDomain)l.get(0);

    else

      return;



    Properties props = new Properties();

    try {

      props.load(new FileInputStream(pathToPropertiesFile));

    }

    catch(FileNotFoundException e) {

      e.printStackTrace();

      return;

    }

    catch(IOException e) {

      e.printStackTrace();

      return;

    }



    String parent = Integer.toString(main.getStartPageID());

    int folderId = IBPageHelper.getInstance().createNewPage(parent,"Intershop folder",com.idega.builder.data.IBPageBMPBean.PAGE,"",null);





    String driver = props.getProperty("drivers");

    String url = props.getProperty("default.url");

    String dbname = props.getProperty("default.dbname");

    String user = props.getProperty("default.user");

    String passwd = props.getProperty("default.password");



    if (driver == null || url == null || dbname == null || user == null || passwd == null) {

      return;

    }



    Vector result = new Vector();



    try {

      Class.forName(driver).newInstance();

      Connection conn = DriverManager.getConnection(url,user,passwd);



      StringBuffer sql = new StringBuffer("select a.");

      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopClassColumnName());

      sql.append(", a.");

      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopIDColumnName());

      sql.append(", ");

      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopLanguageNrColumnName());

      sql.append(", ");

      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopDescriptionColumnName());

      sql.append(", ");

      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopNameColumnName());

      sql.append(" from ");

      sql.append(dbname);

      sql.append("..");

      sql.append(IShopTemplate.ISHOP_TABLE_TEMPLATES);

      sql.append(" a, ");

      sql.append(dbname);

      sql.append("..");

      sql.append(IShopTemplate.ISHOP_TABLE_TEMPLATEDESC);

      sql.append(" b ");

      sql.append(" where a.");

      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopClassColumnName());

      sql.append(" = b.");

      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopClassColumnName());

      sql.append(" and a.");

      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopIDColumnName());

      sql.append(" = b.");

      sql.append(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopIDColumnName());



      Statement stmt = conn.createStatement();

      ResultSet set = stmt.executeQuery(sql.toString());



      while (set.next()) {

        String className = set.getString(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopClassColumnName());

        String id = set.getString(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopIDColumnName());

        String langnr = set.getString(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopLanguageNrColumnName());

        String name = set.getString(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopNameColumnName());

        String desc = set.getString(is.idega.idegaweb.intershop.data.IShopTemplateBeanBMPBean.getIShopDescriptionColumnName());

        result.add(className);

        result.add(id);

        result.add(langnr);

        result.add(name);

        result.add(desc);

      }



      set.close();

      stmt.close();

      conn.close();

    }

    catch(Exception e) {

      e.printStackTrace();

      return;

    }



    try {

      Iterator it = result.iterator();

      while (it.hasNext()) {

        String className = (String)it.next();

        String id = (String)it.next();

        String langnr = (String)it.next();

        String name = (String)it.next();

        String desc = (String)it.next();



        int pageId = IBPageHelper.getInstance().createNewPage(Integer.toString(folderId),name,com.idega.builder.data.IBPageBMPBean.PAGE,"",null,null,IShopTemplate.SUBTYPE_NAME);

        IShopTemplate temp = IShopTemplateHome.getInstance().getNewElement();

        temp.setIShopClass(className);

        temp.setIShopDescription(desc);

        temp.setIShopID(id);

        temp.setIShopLanguageNr(Integer.parseInt(langnr));

        temp.setIShopName(name);

        temp.setPageID(pageId);

        IShopTemplateHome.getInstance().insert(temp);

      }

    }

    catch(Exception e) {

      e.printStackTrace();

    }

  }

}
