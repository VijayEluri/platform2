
/**
 * Title:        idegaTree<p>
 * Description:  <p>
 * Copyright:    Copyright (c) idega<p>
 * Company:      idega margmi�lun<p>
 * @author idega 2000 - idega team - <a href="mailto:gummi@idega.is">gummi@idega.is</a>
 * @version 1.0
 */
package com.idega.util;

import java.util.*;

public class IdegaTree {

  private Hashtable theTree;

  public IdegaTree() {
     theTree = new Hashtable();
  }


  public Object getNode(Object node_id){
    IdegaNode myNode = (IdegaNode)(theTree.get(node_id));
    return myNode.getNode();
  }

  public Vector getChildren_ids(Object node_id){
    IdegaNode myNode = (IdegaNode)(theTree.get(node_id));
    return myNode.getChildren_ids();
  }

  public Vector getChildrens(Object node_id){
    IdegaNode myNode = (IdegaNode)(theTree.get(node_id));
    Vector Child_ids = myNode.getChildren_ids();
    Vector Childs = new Vector();
    IdegaNode returnNode = new IdegaNode();

    for (int i = 0; i < Child_ids.size(); i++){
      returnNode = new IdegaNode(Child_ids.elementAt(i));
      Childs.add(returnNode.getNode());
    }
    return Childs;
  }

  public Object getParentID(Object node_id){
    IdegaNode myNode = (IdegaNode)(theTree.get(node_id));
    return myNode.getParentID();
  }


  /**
   * �etta fall byr til t�man hnut sem foreldri ef �a� er ekki til fyrir og
   * skrifar �ar af lei�andi yfir hnut komi annar me� sama ID.
   */

  public void addNode(Object theObject, Object node_id, Object parent_id){

    if (!theTree.contains(node_id)){
      IdegaNode myNode = new IdegaNode(parent_id);
      myNode.addNode(theObject);
      theTree.put(node_id,myNode);
      addChilde( node_id, parent_id);
    } else{
      IdegaNode myNode = (IdegaNode)(theTree.remove(node_id));
      myNode.addNode(theObject);
      myNode.setParent(parent_id);
      theTree.put(node_id,myNode);
      addChilde( node_id, parent_id);
    }
  }


  /**
   * �etta fall krefst �ess a� foreldri se til i trenu
   */

  public void addNodeInOrder(Object theObject, Object node_id, Object parent_id){
    IdegaNode myNode = new IdegaNode(parent_id);
    myNode.addNode(theObject);
    theTree.put(node_id,myNode);
    addChildeInOrder( node_id, parent_id);
  }

  /**
   * ei�ir hnut en f�rir b�rn sin a foreldri sitt
   */

  public void removeNode(Object node_id){
    IdegaNode thisNode = (IdegaNode)(theTree.remove(node_id));
    removeChilde( node_id, thisNode.getParentID());  //from Parent
    Vector childes = thisNode.getChildren_ids();
    for (int i = 0; i < childes.size(); i++ ){
     addChilde( childes.elementAt(i), thisNode.getParentID() );
    }
  }

  /**
   * ei�ir hnut og �llu �vi sem fyrir ne�an hann er (ekki utf�rt)
   */

   public void removeNodesTree(Object node_id){
    IdegaNode thisNode = (IdegaNode)(theTree.remove(node_id));
    removeChilde( node_id, thisNode.getParentID());  //from Parent
  }



  /**
   * Kallar � toString() falli� ur Hashtable hlutnum sem heldur utan um tr��.
   */

  public String toString(){
    return theTree.toString();
  }

  //  ### Private - F�ll  ###

  private void removeChilde(Object childe, Object parent){  // from parent
    IdegaNode myNode = (IdegaNode)(theTree.remove(parent));
    boolean temp = myNode.removeChildren(childe);
    theTree.put(parent, myNode);
  }

  private void addChilde( Object childe, Object parent){
    if (theTree.contains(parent)){
      IdegaNode myNode = (IdegaNode)(theTree.remove(parent));
      myNode.addChildren(childe);
      theTree.put( parent, myNode);
    } else{
      IdegaNode myNode = new IdegaNode();
      myNode.addChildren(childe);
      theTree.put( parent, myNode);
    }
  }

  private void addChildeInOrder(Object childe, Object parent){
    IdegaNode myNode = (IdegaNode)(theTree.remove(parent));
    myNode.addChildren(childe);
    theTree.put(parent, myNode);
  }


  private class IdegaNode{

//    private Object id;
    private Object myParentID;
    private Vector childeIDs;
    private Object theNode;

    public IdegaNode(Object parent_id){
      myParentID = parent_id;
      childeIDs = new Vector();
      childeIDs.trimToSize();
    }

    public IdegaNode(){
      childeIDs = new Vector();
      childeIDs.trimToSize();
    }
/*
    public IdegaNode(Object node_id){
      id = node_id;
    }

/*
    public Object getNodeID(){
      return id;
    }*/

    public void setParent(Object parent_id){
      myParentID = parent_id;
    }

    public void addChildren( Object children_id ){
      childeIDs.add(children_id);
      childeIDs.trimToSize();
    }

    public void addNode( Object Node ){
      theNode = Node;
    }

    public Object getNode(){
      return theNode;
    }

    public boolean hasChildren(){
      return (childeIDs.size() > 0);
    }

    public boolean removeChildren( Object children_id ){
      boolean temp = childeIDs.remove(children_id);
      childeIDs.trimToSize();
      return temp;
    }

    public Vector getChildren_ids(){
      return childeIDs;
    }

    public Object getParentID(){
      return myParentID;
    }

  } // class IdegaNode

} // class IdegaTree