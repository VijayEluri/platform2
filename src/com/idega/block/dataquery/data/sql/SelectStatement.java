package com.idega.block.dataquery.data.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 4, 2003
 */
public class SelectStatement implements DynamicExpression {
  
	//TODO: thi fix that DISTINCT problem
  private final String SELECT = "SELECT";
  private final String DISTINCT = "DISTINCT";
  private final String FROM = "FROM";
  private final String WHERE = "WHERE";
  private final String AND = "AND";
  private final String ORDER_BY = "ORDER BY";
  private final char WHITE_SPACE = ' ';
  private final char COMMA = ',';
  
  private List innerClauses = new ArrayList();
  private List outerClauses = new ArrayList();
  private List selectClauses = new ArrayList();
  private List whereClauses = new ArrayList();
  private List orderByClauses = new ArrayList();
  
  private boolean selectDistinct = true;
  
  private Map identifierValueMap = new HashMap(0);
  private Map identifierInputDescriptionMap = new HashMap(0);
  
  public static SelectStatement getInstanceWithDistinctFunction() {
  	SelectStatement selectStatement = new SelectStatement();
  	selectStatement.setSelectDistinct(true);
  	return selectStatement;
  }
  
  public static SelectStatement getInstance() {
  	SelectStatement selectStatement = new SelectStatement();
  	selectStatement.setSelectDistinct(false);
  	return selectStatement;
  }
  	

  public void addInnerJoin(Expression join) {
    this.innerClauses.add(join);
  }
  
  public void addOuterJoin(Expression join)	{
  	this.outerClauses.add(join);
  }
  
  public void addSelectClause(Expression clause) {
    this.selectClauses.add(clause);
  }

  public void addWhereClause(DynamicExpression criterion) {
  	if (criterion.isDynamic())	{
  		Map identifierValueMap = criterion.getIdentifierValueMap();
  		Map identifierInputDescriptionMap = criterion.getIdentifierInputDescriptionMap();
  		this.identifierValueMap.putAll(identifierValueMap);
  		this.identifierInputDescriptionMap.putAll(identifierInputDescriptionMap);
  	}
    this.whereClauses.add(criterion);
  }
  
  public void addOrderByClause(Expression criterion)	{
  	this.orderByClauses.add(criterion);
  }
  
  public boolean isDynamic() {
  	return ! this.identifierValueMap.isEmpty();
  }
  
  public Map getIdentifierValueMap()	{
  	return this.identifierValueMap;
  }
  
  public Map getIdentifierInputDescriptionMap() {
  	return this.identifierInputDescriptionMap;
  }
  
  public void setIdentifierValueMap(Map identifierValueMap)	{
  	this.identifierValueMap = identifierValueMap;
  }
  
  public boolean isValid() {
  	return true;
  }
  
  public String toSQLString() {
    StringBuffer expression =  new StringBuffer();
    
    StringBuffer whiteSpaceCommaWhiteSpace =
      new StringBuffer(this.WHITE_SPACE).append(this.COMMA).append(this.WHITE_SPACE);
    
    StringBuffer spacing = new StringBuffer(this.SELECT).append(this.WHITE_SPACE);
    if (this.selectDistinct) {
    	spacing.append(this.DISTINCT).append(this.WHITE_SPACE);
    }
    Iterator select = this.selectClauses.iterator();
    while (select.hasNext())  {
      Expression clause = (Expression) select.next();
      expression.append(spacing).append(clause.toSQLString());
      spacing = whiteSpaceCommaWhiteSpace;
    }

    spacing = new StringBuffer().append(this.WHITE_SPACE).append(this.FROM).append(this.WHITE_SPACE);
    Iterator inner= this.innerClauses.iterator();
    while (inner.hasNext())  {
      Expression clause = (Expression) inner.next();
      expression.append(spacing).append(clause.toSQLString());
      spacing = whiteSpaceCommaWhiteSpace;
    }
    
    Iterator outer= this.outerClauses.iterator();
    while (outer.hasNext())  {
      Expression clause = (Expression) outer.next();
      expression.append(clause.toSQLString());
    }
    // where
    spacing = new StringBuffer().append(this.WHITE_SPACE).append(this.WHERE).append(this.WHITE_SPACE);
    StringBuffer and = new StringBuffer().append(this.WHITE_SPACE).append(this.AND).append(this.WHITE_SPACE);
    Iterator where = this.whereClauses.iterator();
    while (where.hasNext()) {
      DynamicExpression clause = (DynamicExpression) where.next();
      if (clause.isDynamic()) {
      	clause.setIdentifierValueMap(this.identifierValueMap);
      }
      expression.append(spacing).append(clause.toSQLString());
      spacing = and;
    }
    // order by
    spacing = new StringBuffer().append(this.WHITE_SPACE).append(this.ORDER_BY).append(this.WHITE_SPACE);
    StringBuffer comma = new StringBuffer().append(this.WHITE_SPACE).append(this.COMMA).append(this.WHITE_SPACE);
    Iterator orderBy = this.orderByClauses.iterator();
    while (orderBy.hasNext()) {
      Expression criterion = (Expression) orderBy.next();
      expression.append(spacing).append(criterion.toSQLString());
      spacing = comma;
    }
    
    return expression.toString();
  }
	/**
	 * @return Returns the selectDistinct.
	 */
	public boolean isSelectDistinct() {
		return this.selectDistinct;
	}

	/**
	 * @param selectDistinct The selectDistinct to set.
	 */
	public void setSelectDistinct(boolean selectDistinct) {
		this.selectDistinct = selectDistinct;
	}

}   

