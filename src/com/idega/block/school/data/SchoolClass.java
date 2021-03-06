package com.idega.block.school.data;


import com.idega.data.IDOAddRelationshipException;
import com.idega.data.IDORelationshipException;
import java.util.Collection;
import com.idega.user.data.User;
import com.idega.data.IDORemoveRelationshipException;
import java.sql.Timestamp;
import com.idega.data.IDOEntity;

public interface SchoolClass extends IDOEntity {
	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getName
	 */
	public String getName();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getCode
	 */
	public String getCode();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setCode
	 */
	public void setCode(String code);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getSchoolId
	 */
	public int getSchoolId();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getSchool
	 */
	public School getSchool();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setSchoolId
	 */
	public void setSchoolId(int id);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setSchool
	 */
	public void setSchool(School school);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getSchoolTypeId
	 */
	public int getSchoolTypeId();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getSchoolType
	 */
	public SchoolType getSchoolType();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setSchoolTypeId
	 */
	public void setSchoolTypeId(int id);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setSchoolType
	 */
	public void setSchoolType(SchoolType type);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setSchoolSeasonId
	 */
	public void setSchoolSeasonId(int id);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setSchoolSeason
	 */
	public void setSchoolSeason(SchoolSeason season);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getSchoolSeasonId
	 */
	public int getSchoolSeasonId();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getSchoolSeason
	 */
	public SchoolSeason getSchoolSeason();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setSchoolClassName
	 */
	public void setSchoolClassName(String name);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getSchoolClassName
	 */
	public String getSchoolClassName();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getValid
	 */
	public boolean getValid();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setValid
	 */
	public void setValid(boolean valid);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getReady
	 */
	public boolean getReady();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setReady
	 */
	public void setReady(boolean valid);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getLocked
	 */
	public boolean getLocked();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setLocked
	 */
	public void setLocked(boolean valid);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setReadyDate
	 */
	public void setReadyDate(Timestamp timestamp);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getReadyDate
	 */
	public Timestamp getReadyDate();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setLockedDate
	 */
	public void setLockedDate(Timestamp timestamp);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getLockedDate
	 */
	public Timestamp getLockedDate();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setIsSubGroup
	 */
	public void setIsSubGroup(boolean isSubGroup);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getIsSubGroup
	 */
	public boolean getIsSubGroup();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#setGroupStringId
	 */
	public void setGroupStringId(String groupStringId);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getGroupStringId
	 */
	public String getGroupStringId();

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#hasRelationToSchoolYear
	 */
	public boolean hasRelationToSchoolYear(SchoolYear schoolYear);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#hasRelationToTeacher
	 */
	public boolean hasRelationToTeacher(User teacher);

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#findRelatedUsers
	 */
	public Collection findRelatedUsers() throws IDORelationshipException;

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#findRelatedSchoolYears
	 */
	public Collection findRelatedSchoolYears() throws IDORelationshipException;

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#addSchoolYear
	 */
	public void addSchoolYear(SchoolYear year) throws IDOAddRelationshipException;

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#removeSchoolYear
	 */
	public void removeSchoolYear(SchoolYear year) throws IDORemoveRelationshipException;

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#addTeacher
	 */
	public void addTeacher(User teacher) throws IDOAddRelationshipException;

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#removeTeacher
	 */
	public void removeTeacher(User teacher) throws IDORemoveRelationshipException;

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#findRelatedStudyPaths
	 */
	public Collection findRelatedStudyPaths() throws IDORelationshipException;

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#addStudyPath
	 */
	public void addStudyPath(SchoolStudyPath studyPath) throws IDOAddRelationshipException;

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#removeStudyPath
	 */
	public void removeStudyPath(SchoolStudyPath studyPath) throws IDORemoveRelationshipException;

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#removeStudyPaths
	 */
	public void removeStudyPaths() throws IDORemoveRelationshipException;

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#removeFromSchoolYear
	 */
	public void removeFromSchoolYear() throws IDORemoveRelationshipException;

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#removeFromUser
	 */
	public void removeFromUser() throws IDORemoveRelationshipException;

	/**
	 * @see com.idega.block.school.data.SchoolClassBMPBean#getSubGroupPlacements
	 */
	public Collection getSubGroupPlacements() throws IDORelationshipException;
}