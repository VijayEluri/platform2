package is.idega.idegaweb.golf.course.data;


public interface CourseHome extends com.idega.data.IDOHome
{
 public Course create() throws javax.ejb.CreateException;
 public Course findByPrimaryKey(Object pk) throws javax.ejb.FinderException;
 public java.util.Collection findAllCourses()throws javax.ejb.FinderException;
 public Course findCourseByClubAndName(int p0,java.lang.String p1)throws javax.ejb.FinderException;
 public Course findCourseByName(java.lang.String p0)throws javax.ejb.FinderException;
 public java.util.Collection findCoursesByClub(int p0)throws javax.ejb.FinderException;
 public java.util.Collection findCoursesByType(java.lang.String p0)throws javax.ejb.FinderException;

}