/*

 * $Id: ContractTagBMPBean.java,v 1.1 2002/04/06 19:07:29 tryggvil Exp $

 *

 * Copyright (C) 2001 Idega hf. All Rights Reserved.

 *

 * This software is the proprietary information of Idega hf.

 * Use is subject to license terms.

 *

 */

package com.idega.block.contract.data;



import java.sql.*;

import com.idega.data.IDOLegacyEntity;



/**

 *

 * @author <a href="mailto:aron@idega.is">aron@idega.is</a>

 * @version 1.0

 */

public class ContractTagBMPBean extends com.idega.data.GenericEntity implements com.idega.block.contract.data.ContractTag {



  public ContractTagBMPBean(){

          super();

  }



  public ContractTagBMPBean(int id)throws SQLException{

          super(id);

  }



  public void initializeAttributes(){

    addAttribute(getIDColumnName());

		addAttribute(getColumnNameCategoryId(),"Category",true,true,java.lang.Integer.class,"many-to-one",com.idega.block.contract.data.ContractCategory.class);

    addAttribute(getColumnNameName(), "Name", true, true, java.lang.String.class);

    addAttribute(getColumnNameInfo(), "Info", true, true, java.lang.String.class);

		addAttribute(getColumnNameInUse(), "In use", true, true, java.lang.Boolean.class);

		addAttribute(getColumnNameInList(), "In list", true, true, java.lang.Boolean.class);

  }



  public static String getEntityTableName(){ return "CON_TAG"; }

	public static String getColumnNameCategoryId(){return "CON_CATEGORY_ID";}

  public static String getColumnNameName(){return "NAME";}

  public static String getColumnNameInfo(){return "INFO";}

	public static String getColumnNameInUse(){return "IN_USE";}

	public static String getColumnNameInList(){return "IN_LIST";}



  public String getEntityName(){

    return getEntityTableName();

  }



  public String getName(){

    return getStringColumnValue(getColumnNameName());

  }

  public void setName(String name){

    setColumn(getColumnNameName(), name);

  }

  public String getInfo(){

    return getStringColumnValue(getColumnNameInfo());

  }

  public void setInfo(String info){

    setColumn(getColumnNameInfo(), info);

  }

	public void setInUse(boolean inUse){

	  setColumn(getColumnNameInUse(),inUse);

	}

	public boolean getInUse(){

		return  getBooleanColumnValue(getColumnNameInUse());

	}

	public void setInList(boolean inUse){

	  setColumn(getColumnNameInList(),inUse);

	}

	public boolean getInList(){

		return  getBooleanColumnValue(getColumnNameInList());

	}

	public void setCategoryId(int id){

	  setColumn(getColumnNameCategoryId(),id);

	}

	public void setCategoryId(Integer id){

	  setColumn(getColumnNameCategoryId(),id);

	}

	public Integer getCategoryId(){

	  return getIntegerColumnValue(getColumnNameCategoryId());

	}



}

