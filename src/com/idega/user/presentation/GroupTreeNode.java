package com.idega.user.presentation;

import java.util.*;
import com.idega.builder.data.IBDomainHome;
import java.rmi.RemoteException;
import javax.ejb.EJBException;
import com.idega.data.IDORelationshipException;
import javax.ejb.FinderException;
import com.idega.user.data.Group;
import com.idega.builder.data.IBDomain;
import com.idega.core.ICTreeNode;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: idega Software</p>
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class GroupTreeNode implements ICTreeNode {

  private ICTreeNode _parent = null;

  private IBDomain _domain = null;
  private Group _group = null;
  private int _nodeType;
  public static final int TYPE_DOMAIN = 0;
  public static final int TYPE_GROUP = 1;

  public GroupTreeNode(IBDomain domain) {
    _domain = domain;
    _nodeType = TYPE_DOMAIN;
  }

  public GroupTreeNode(Group group) {
    _group = group;
    _nodeType = TYPE_GROUP;
  }

  public int getNodeType(){
    return _nodeType;
  }

  public void setParent(ICTreeNode parent){
    _parent = parent;
  }


//    switch (_nodeType) {
//      case TYPE_DOMAIN:
//        break;
//      case TYPE_GROUP:
//        return _group
//      default:
//        throw new UnsupportedOperationException("Operation not supported for type:"+ getNodeType());
//    }

  public Iterator getChildren() {
    switch (_nodeType) {
      case TYPE_DOMAIN:
        try {
          List l = new Vector();
          Iterator iter = _domain.getTopLevelGroupsUnderDomain().iterator();
          GroupTreeNode node = null;
          while (iter.hasNext()) {
            Group item = (Group)iter.next();
            node = new GroupTreeNode(item);
            node.setParent(this);
            l.add(node);
          }

          return l.iterator();

        }
        catch(Exception e){
          throw new RuntimeException(e.getMessage());
        }
      case TYPE_GROUP:
//        return _group.getChildren();
          List l = new Vector();
          Iterator iter = _group.getChildren();
          GroupTreeNode node = null;
          while (iter.hasNext()) {
            Group item = (Group)iter.next();
            node = new GroupTreeNode(item);
            node.setParent(this);
            l.add(node);
          }

//          List l = new Vector();
//          List childrens = _group.getGroupsContained(_filter, _block);
//          if(childrens != null){
//            Iterator iter = _group.getChildren();
//            GroupTreeNode node = null;
//            while (iter.hasNext()) {
//              Group item = (Group)iter.next();
//              node = new GroupTreeNode(item);
//              node.setParent(this);
//              l.add(node);
//            }
//          }

          return l.iterator();
      default:
        throw new UnsupportedOperationException("Operation not supported for type:"+ getNodeType());
    }

  }
  public boolean getAllowsChildren() {
    switch (_nodeType) {
      case TYPE_DOMAIN:
        return true;
      case TYPE_GROUP:
        return _group.getAllowsChildren();
      default:
        throw new UnsupportedOperationException("Operation not supported for type:"+ getNodeType());
    }
  }
  public ICTreeNode getChildAtIndex(int childIndex) {
    switch (_nodeType) {
      case TYPE_DOMAIN:
        try{
          GroupTreeNode node = new GroupTreeNode(((IBDomainHome)_domain.getEJBHome()).findByPrimaryKey(new Integer(childIndex)));
          node.setParent(this);
          return node;
        }
        catch(Exception e){
          throw new RuntimeException(e.getMessage());
        }
      case TYPE_GROUP:
//        return _group.getChildAtIndex(childIndex);
        try{
          GroupTreeNode node = new GroupTreeNode((Group)_group.getChildAtIndex(childIndex));
          node.setParent(this);
          return node;
        }
        catch(Exception e){
          throw new RuntimeException(e.getMessage());
        }
      default:
        throw new UnsupportedOperationException("Operation not supported for type:"+ getNodeType());
    }
  }
  public int getChildCount() {
    switch (_nodeType) {
      case TYPE_DOMAIN:
        try {
          return _domain.getTopLevelGroupsUnderDomain().size();
        }
        catch(Exception e){
          throw new RuntimeException(e.getMessage());
        }
      case TYPE_GROUP:
        return _group.getChildCount();
      default:
        throw new UnsupportedOperationException("Operation not supported for type:"+ getNodeType());
    }
  }

  public int getIndex(ICTreeNode node) {
    switch (_nodeType) {
      case TYPE_DOMAIN:
        return node.getNodeID();
      case TYPE_GROUP:
        return _group.getIndex(node);
      default:
        throw new UnsupportedOperationException("Operation not supported for type:"+ getNodeType());
    }
  }
  public ICTreeNode getParentNode() {
      return _parent;
//    switch (_nodeType) {
//      case TYPE_DOMAIN:
//        return null;
//      case TYPE_GROUP:
//        return _group.getParentNode();
//      default:
//        throw new UnsupportedOperationException("Operation not supported for type:"+ getNodeType());
//    }
  }
  public boolean isLeaf() {
    switch (_nodeType) {
      case TYPE_DOMAIN:
        return false;
      case TYPE_GROUP:
        return _group.isLeaf();
      default:
        throw new UnsupportedOperationException("Operation not supported for type:"+ getNodeType());
    }
  }

  public String getNodeName() {
    switch (_nodeType) {
      case TYPE_DOMAIN:
        return _domain.getName();
      case TYPE_GROUP:
        return _group.getNodeName();
      default:
        throw new UnsupportedOperationException("Operation not supported for type:"+ getNodeType());
    }
  }

  public int getNodeID() {
    switch (_nodeType) {
      case TYPE_DOMAIN:
        try {
          return ((Integer)_domain.getPrimaryKey()).intValue();
        }
        catch (Exception ex) {
          throw new RuntimeException(ex.getMessage());
        }
      case TYPE_GROUP:
        return _group.getNodeID();
      default:
        throw new UnsupportedOperationException("Operation not supported for type:"+ getNodeType());
    }
  }

  public int getSiblingCount() {
    switch (_nodeType) {
      case TYPE_DOMAIN:
        if(_parent != null){
          return _parent.getChildCount();
        } else {
          return 0;
        }
      case TYPE_GROUP:
        return _group.getSiblingCount();
      default:
        throw new UnsupportedOperationException("Operation not supported for type:"+ getNodeType());
    }
  }
}