package com.idega.data;

import com.idega.core.business.ICTreeNodeLeafComparator;
import com.idega.core.data.ICTreeNode;

import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Locale;

/**
 * Title:        idegaclasses
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */

public abstract class TreeableEntityBMPBean extends com.idega.data.GenericEntity implements com.idega.data.TreeableEntity, IDOLegacyEntity, com.idega.core.data.ICTreeNode {

	protected boolean _sortLeafs = false;
	protected boolean _leafsFirst = false;

	public TreeableEntityBMPBean() {
		super();
	}

	public TreeableEntityBMPBean(int id) throws java.sql.SQLException {
		super(id);
	}

	protected void beforeInitializeAttributes() {
		addTreeRelationShip();
	}

	public int getNodeID() {
		return getID();
	}
	
	/**
	 * Default implementation just calls getName()
	 */
	public String getNodeName() {
		return getName();
	}
	
	/**
	 * Default implementation just calls getNodeName()
	 */
	public String getNodeName(Locale locale) {
		return getNodeName();
	}

	/**
	 * Returns the children of the reciever as an Iterator. Returns null if no children found
	 */
	public Iterator getChildren() {
		return getChildren(null);
	}

	public Iterator getChildren(String orderBy) {
		try {
			String thisTable = this.getTableName();
			String treeTable = EntityControl.getTreeRelationShipTableName(this);
			String idColumnName = this.getIDColumnName();
			String childIDColumnName = EntityControl.getTreeRelationShipChildColumnName(this);
			StringBuffer buffer = new StringBuffer();
			//buffer.append("select " + thisTable + ".* from " + thisTable + "," + treeTable + " where " + thisTable + "." + idColumnName + "=" + treeTable + "." + childIDColumnName + " and " + treeTable + "." + idColumnName + "='" + this.getPrimaryKey().toString() + "'");
			buffer.append("select " ).append( thisTable ).append(".* from ").append( thisTable).append(",").append(treeTable).append(" where ").append(thisTable).append(".").append(idColumnName).append(" = ").append(treeTable).append(".").append(childIDColumnName).append(" and ").append(treeTable).append(".").append(idColumnName).append( " = ").append(this.getPrimaryKey().toString()).append("");
			if (orderBy != null && !orderBy.equals("")) {
				buffer.append(" order by ").append(thisTable).append( ".").append(orderBy);
			}
			//System.out.println(buffer.toString());
			List list = EntityFinder.findAll(this, buffer.toString());
			if (list != null) {
				if (_sortLeafs) {
					ICTreeNodeLeafComparator c = new ICTreeNodeLeafComparator(_leafsFirst);
					Collections.sort(list, c);
				}
				return list.iterator();
			} else {
				return null;
			}
		} catch (Exception e) {
			System.err.println("There was an error in com.idega.data.TreeableEntityBMPBean.getChildren() " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
	}

	/**
	 *  Returns true if the receiver allows children.
	 */
	public boolean getAllowsChildren() {
		return true;
	}

	/**
	 *  Returns the child TreeNode at index childIndex.
	 */
	public ICTreeNode getChildAtIndex(int childIndex) {
		try {
			GenericEntity entity = (GenericEntity)this.getClass().newInstance();
			entity.findByPrimaryKey(childIndex);
			return (TreeableEntity)entity;
		} catch (Exception e) {
			System.err.println("There was an error in com.idega.data.TreeableEntityBMPBean.getChildAtIndex() " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
	}

	/**
	 *    Returns the number of children TreeNodes the receiver contains.
	 */
	public int getChildCount() {
		String treeTableName = EntityControl.getTreeRelationShipTableName(this);
		return EntityControl.returnSingleSQLQuery(this, "select count(*) from " + treeTableName + " where " + this.getIDColumnName() + "='" + this.getPrimaryKey().toString() + "'");
	}

	/**
	 * Returns the index of node in the receivers children.
	 */
	public int getIndex(ICTreeNode node) {
		return ((GenericEntity)node).getID();
	}

	/**
	 *  Returns the parent TreeNode of the receiver. Return null if none
	 */
	public ICTreeNode getParentNode() {
		try {
			int parent_id = EntityControl.returnSingleSQLQuery(this, "select " + this.getIDColumnName() + " from " + EntityControl.getTreeRelationShipTableName(this) + " where " + EntityControl.getTreeRelationShipChildColumnName(this) + "=" + this.getPrimaryKey().toString() + "");
			if (parent_id != -1) {
				GenericEntity entity = (GenericEntity)this.getClass().newInstance();
				entity.findByPrimaryKey(parent_id);
				return (TreeableEntity)entity;
			} else {
				return null;
			}
		} catch (Exception e) {
			System.err.println("There was an error in com.idega.data.TreeableEntityBMPBean.getParentNode() " + e.getMessage());
			e.printStackTrace(System.err);
			return null;
		}
	}

	public TreeableEntity getParentEntity() {
		return (TreeableEntity)getParentNode();
	}

	public boolean isLeaf() {
		int children = getChildCount();
		if (children > 0) {
			return false;
		} else {
			return true;
		}
	}

	public String getTreeRelationshipTableName(TreeableEntity entity) {
		return EntityControl.getTreeRelationShipTableName((TreeableEntityBMPBean)entity);
	}

	public String getTreeRelationshipChildColumnName(TreeableEntity entity) {
		return EntityControl.getTreeRelationShipChildColumnName((TreeableEntityBMPBean)entity);
	}

	public void addChild(TreeableEntity entity) throws java.sql.SQLException {
		addToTree((TreeableEntityBMPBean)entity, EntityControl.getTreeRelationShipChildColumnName((TreeableEntityBMPBean)entity), getTreeRelationshipTableName(entity));
	}

	public void removeChild(TreeableEntity entity) throws java.sql.SQLException {
		removeFrom((TreeableEntityBMPBean)entity, EntityControl.getTreeRelationShipChildColumnName((TreeableEntityBMPBean)entity));
	}

	public void moveChildrenFrom(TreeableEntity entityFrom) throws java.sql.SQLException {
		moveChildrenToCurrent((TreeableEntityBMPBean)entityFrom, EntityControl.getTreeRelationShipChildColumnName((TreeableEntityBMPBean)entityFrom));
	}

	/**
	 *
	 */
	public int getSiblingCount() {
		ICTreeNode parent = getParentNode();
		if (parent == null)
			return (0);

		return (parent.getChildCount() - 1);
	}

}
