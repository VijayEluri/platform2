package com.idega.block.building.data;


public interface ComplexTypeViewHome extends com.idega.data.IDOHome
{
 public ComplexTypeView create() throws javax.ejb.CreateException;
 public ComplexTypeView findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public ComplexTypeView create(com.idega.block.building.data.ComplexTypeViewKey p0)throws javax.ejb.CreateException;
 public java.util.Collection findAll()throws javax.ejb.FinderException;
 public java.util.Collection findByCategory(java.lang.Integer p0)throws javax.ejb.FinderException;
 public ComplexTypeView findByPrimaryKey(com.idega.block.building.data.ComplexTypeViewKey p0)throws javax.ejb.FinderException;

}