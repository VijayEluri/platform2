package com.idega.block.forum.data;

import com.idega.data.TreeableEntity;
import javax.ejb.*;

public interface ForumData extends TreeableEntity
{
 public void setThreadDate(java.sql.Timestamp p0);
 public boolean isValid();
 public int getParentThreadID();
 public java.lang.String getUserEMail();
 public int getUserID();
 public java.lang.String getThreadBody();
 public java.util.Iterator getChildren();
 public java.lang.String getThreadSubject();
 public void setUserID(int p0);
 public java.lang.String getUserName();
 public int getTopicID();
 public void setNumberOfResponses(int p0);
 public void setParentThreadID(int p0);
 public void setUserEMail(java.lang.String p0);
 public void setValid(boolean p0);
 public void setUserName(java.lang.String p0);
 public void setThreadBody(java.lang.String p0);
 public java.sql.Timestamp getThreadDate();
 public void setThreadSubject(java.lang.String p0);
 public int getNumberOfResponses();
 public void setTopicID(int p0);
}
