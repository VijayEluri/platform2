
package is.idega.idegaweb.golf.business;

import is.idega.idegaweb.golf.entity.Member;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.idega.util.IsCollator;


/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega multimedia
 * @author       <a href="mailto:aron@idega.is">aron@idega.is</a>
 * @version 1.0
 */

public class GenericMemberComparator implements Comparator {

  public static final int NAME      = 1;
  public static final int SOCIAL    = 2;
  public static final int FIRSTLASTMIDDLE = 3;
  public static final int LASTFIRSTMIDDLE = 4;
  public static final int FIRSTMIDDLELAST = 5;


  private int sortBy;

  public GenericMemberComparator() {
      sortBy = NAME;
  }

  public GenericMemberComparator(int toSortBy) {
      sortBy = toSortBy;
  }

  public void sortBy(int toSortBy) {
      sortBy = toSortBy;
  }

  public int compare(Object o1, Object o2) {
      Member p1 = (Member) o1;
      Member p2 = (Member) o2;
      int result = 0;

      switch (this.sortBy) {
        case NAME     : result = nameSort(o1, o2);
        break;

        case SOCIAL   : result = p1.getSocialSecurityNumber().compareTo(p2.getSocialSecurityNumber());
                        if(result == 0)
                          result = nameSort(o1, o2);
        break;
        case FIRSTLASTMIDDLE   : result = this.nameSort(o1,o2);
        break;
        case LASTFIRSTMIDDLE   : result = this.nameSortLastFirst(o1,o2);
        break;
        case FIRSTMIDDLELAST   : result = this.nameSortFirstMiddleLast(o1,o2);
        break;
      }

      return result;
  }

  private int nameSort(Object o1, Object o2) {
      Member p1 = (Member) o1;
      Member p2 = (Member) o2;

      // check on first name first...

      //int result = p1.getFirstName().compareTo(p2.getFirstName());
      String one = p1.getFirstName()!=null?p1.getFirstName():"";
      String two = p2.getFirstName()!=null?p2.getFirstName():"";
      int result = IsCollator.getIsCollator().compare(one,two);

      // if equal, check last name...
      if (result == 0){
          one = p1.getLastName()!=null?p1.getLastName():"";
          two = p2.getLastName()!=null?p2.getLastName():"";
          //result = p1.getLastName().compareTo(p2.getLastName());
          result = IsCollator.getIsCollator().compare(one,two);
      }
      // if equal, check middle name...
      if (result == 0){
          one = p1.getMiddleName()!=null?p1.getMiddleName():"";
          two = p2.getMiddleName()!=null?p2.getMiddleName():"";
          //result = p1.getMiddleName().compareTo(p2.getMiddleName());
          result = IsCollator.getIsCollator().compare(one,two);
      }
      return result;
  }

  private int nameSortLastFirst(Object o1, Object o2) {
      Member p1 = (Member) o1;
      Member p2 = (Member) o2;

      // check on first name first...


          //result = p1.getLastName().compareTo(p2.getLastName());
      String one = p1.getLastName()!=null?p1.getLastName():"";
      String two = p2.getLastName()!=null?p2.getLastName():"";
      int result = IsCollator.getIsCollator().compare(one,two);

      //int result = p1.getFirstName().compareTo(p2.getFirstName());
      if (result == 0){
        one = p1.getFirstName()!=null?p1.getFirstName():"";
        two = p2.getFirstName()!=null?p2.getFirstName():"";
        result = IsCollator.getIsCollator().compare(one,two);
      }
      // if equal, check middle name...
      if (result == 0){
        //result = p1.getMiddleName().compareTo(p2.getMiddleName());
        one = p1.getMiddleName()!=null?p1.getMiddleName():"";
        two = p2.getMiddleName()!=null?p2.getMiddleName():"";
        result = IsCollator.getIsCollator().compare(one,two);
      }

      return result;
  }

  private int nameSortFirstMiddleLast(Object o1, Object o2) {
      Member p1 = (Member) o1;
      Member p2 = (Member) o2;

      // check on first name first...

      String one = p1.getFirstName()!=null?p1.getFirstName():"";
      String two = p2.getFirstName()!=null?p2.getFirstName():"";
      int result = IsCollator.getIsCollator().compare(one,two);
      // if equal, check middle name...
      if (result == 0){
        one = p1.getMiddleName()!=null?p1.getMiddleName():"";
        two = p2.getMiddleName()!=null?p2.getMiddleName():"";
        result = IsCollator.getIsCollator().compare(one,two);
      }
      if (result == 0){
        one = p1.getLastName()!=null?p1.getLastName():"";
        two = p2.getLastName()!=null?p2.getLastName():"";
        result = IsCollator.getIsCollator().compare(one,two);
      }
      return result;
  }
  public boolean equals(Object obj) {
    /**@todo: Implement this java.util.Comparator method*/
    throw new java.lang.UnsupportedOperationException("Method equals() not yet implemented.");
  }

  public Iterator sort(Member[] members, int toSortBy) {
      sortBy = toSortBy;
      List list = new LinkedList();
      for(int i = 0; i < members.length; i++) {
          list.add(members[i]);
      }
      Collections.sort(list, this);
      return list.iterator();
  }

  public Iterator sort(Member[] members) {
      List list = new LinkedList();
      for(int i = 0; i < members.length; i++) {
          list.add(members[i]);
      }
      Collections.sort(list, this);
      return list.iterator();
  }

  public Member[] sortedArray(Member[] members, int toSortBy) {
      sortBy = toSortBy;
      List list = new LinkedList();
      for(int i = 0; i < members.length; i++) {
          list.add(members[i]);
      }
      Collections.sort(list, this);
      Object[] objArr = list.toArray();
      for(int i = 0; i < objArr.length; i++) {
          members[i] = (Member) objArr[i];
      }
      return (members);
  }

   public Vector sortedArray(Vector list) {
      Collections.sort(list, this);
      return list;
  }


  public Member[] sortedArray(Member[] members) {
      List list = new LinkedList();
      for(int i = 0; i < members.length; i++) {
          list.add(members[i]);
      }
      Collections.sort(list, this);
      Object[] objArr = list.toArray();
      for(int i = 0; i < objArr.length; i++) {
          members[i] = (Member) objArr[i];
      }
      return (members);
  }

  public Member[] reverseSortedArray(Member[] members, int toSortBy) {
      sortBy = toSortBy;
      List list = new LinkedList();
      for(int i = 0; i < members.length; i++) {
          list.add(members[i]);
      }
      Collections.sort(list, this);
      Collections.reverse(list);
      Object[] objArr = list.toArray();
      for(int i = 0; i < objArr.length; i++) {
          members[i] = (Member) objArr[i];
      }
      return (members);
  }

}
