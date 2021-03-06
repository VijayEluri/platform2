/**
 * 
 */
package se.idega.idegaweb.commune.adulteducation.data;

import java.util.Collection;
import javax.ejb.FinderException;
import com.idega.block.school.data.School;
import com.idega.block.school.data.SchoolSeason;
import com.idega.block.school.data.SchoolStudyPath;
import com.idega.block.school.data.SchoolStudyPathGroup;
import com.idega.block.school.data.SchoolType;
import com.idega.data.IDOFactory;


/**
 * <p>
 * TODO Dainis Describe Type AdultEducationCourseHomeImpl
 * </p>
 *  Last modified: $Date: 2006/04/05 12:14:59 $ by $Author: dainis $
 * 
 * @author <a href="mailto:Dainis@idega.com">Dainis</a>
 * @version $Revision: 1.3.2.3 $
 */
public class AdultEducationCourseHomeImpl extends IDOFactory implements AdultEducationCourseHome {

	protected Class getEntityInterfaceClass() {
		return AdultEducationCourse.class;
	}

	public AdultEducationCourse create() throws javax.ejb.CreateException {
		return (AdultEducationCourse) super.createIDO();
	}

	public AdultEducationCourse findByPrimaryKey(Object pk) throws javax.ejb.FinderException {
		return (AdultEducationCourse) super.findByPrimaryKeyIDO(pk);
	}

	public AdultEducationCourse findBySeasonAndCode(SchoolSeason season, String code) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AdultEducationCourseBMPBean) entity).ejbFindBySeasonAndCode(season, code);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public AdultEducationCourse findBySeasonAndCode(Object season, String code) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		Object pk = ((AdultEducationCourseBMPBean) entity).ejbFindBySeasonAndCode(season, code);
		this.idoCheckInPooledEntity(entity);
		return this.findByPrimaryKey(pk);
	}

	public Collection findAllBySeasonAndSchool(SchoolSeason season, School school) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((AdultEducationCourseBMPBean) entity).ejbFindAllBySeasonAndSchool(season, school);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllBySeasonAndSchool(Object season, Object school) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((AdultEducationCourseBMPBean) entity).ejbFindAllBySeasonAndSchool(season, school);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllBySeasonAndTypeAndSchoolAndStudyPathGroup(SchoolSeason season, SchoolType type,
			School school, SchoolStudyPathGroup group) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((AdultEducationCourseBMPBean) entity).ejbFindAllBySeasonAndTypeAndSchoolAndStudyPathGroup(
				season, type, school, group);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllBySeasonAndTypeAndSchoolAndStudyPathGroup(Object season, Object type, Object school,
			Object group) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((AdultEducationCourseBMPBean) entity).ejbFindAllBySeasonAndTypeAndSchoolAndStudyPathGroup(
				season, type, school, group);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllBySchoolAndSeasonAndStudyPathGroupConnectedToChoices(Object school, Object season,
			Object group, Object[] statuses) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((AdultEducationCourseBMPBean) entity).ejbFindAllBySchoolAndSeasonAndStudyPathGroupConnectedToChoices(
				school, season, group, statuses);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllBySchoolAndSeasonAndStudyPathGroupConnectedToStudents(Object school, Object season,
			Object group) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((AdultEducationCourseBMPBean) entity).ejbFindAllBySchoolAndSeasonAndStudyPathGroupConnectedToStudents(
				school, season, group);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllBySeasonAndSchoolAndStudyPath(SchoolSeason season, School school, SchoolStudyPath studyPath)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((AdultEducationCourseBMPBean) entity).ejbFindAllBySeasonAndSchoolAndStudyPath(
				season, school, studyPath);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllBySeasonAndSchoolAndStudyPath(Object season, Object school, Object studyPath)
			throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((AdultEducationCourseBMPBean) entity).ejbFindAllBySeasonAndSchoolAndStudyPath(
				season, school, studyPath);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}

	public Collection findAllAvailableCoursesByParameters(SchoolType schoolType, SchoolSeason schoolSeason,
			SchoolStudyPathGroup studyPathGroup, SchoolStudyPath studyPath, School school) throws FinderException {
		com.idega.data.IDOEntity entity = this.idoCheckOutPooledEntity();
		java.util.Collection ids = ((AdultEducationCourseBMPBean) entity).ejbFindAllAvailableCoursesByParameters(
				schoolType, schoolSeason, studyPathGroup, studyPath, school);
		this.idoCheckInPooledEntity(entity);
		return this.getEntityCollectionForPrimaryKeys(ids);
	}
}
